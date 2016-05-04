package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import gitflow.GitflowConfigUtil;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;


public class UpdateFromDevelopAction extends GitflowAction {

	String customFeatureName = null;

	UpdateFromDevelopAction( ) {
		super( "Update from develop" );
	}

	UpdateFromDevelopAction( String name ) {
		super( "Update from develop" );
		customFeatureName = name;
	}

	@Override public void actionPerformed( AnActionEvent e ) {
		super.actionPerformed( e );

		String currentBranchName = GitBranchUtil.getBranchNameOrRev( repo );
		if ( currentBranchName.isEmpty( ) == false ) {

			final AnActionEvent event = e;
			final String featureName;
			// Check if a feature name was specified, otherwise take name from current branch
			if ( customFeatureName != null ) {
				featureName = customFeatureName;
			} else {
				featureName = GitflowConfigUtil.getFeatureNameFromBranch( myProject, currentBranchName );
			}
			final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener( myProject );

			new Task.Backgroundable( myProject, "Updating from develop to feature " + featureName, false ) {
				@Override public void run( @NotNull ProgressIndicator indicator ) {
					GitCommandResult result = myGitflow
							.updateFromDevelop( repo, featurePrefix + featureName, errorLineHandler );

					if ( result.success( ) ) {
						String finishedFeatureMessage = String
								.format( "The develop branch '%s' was merged into '%s%s'", developBranch, featurePrefix,
										featureName );
						NotifyUtil.notifySuccess( myProject, featureName, finishedFeatureMessage );
					} else if ( errorLineHandler.hasMergeError ) {
						// (merge errors are handled in the onSuccess handler)
					} else {
						NotifyUtil.notifyError( myProject, "Error",
								"Please have a look at the Version Control console for more details" );
					}

					repo.update( );

				}

				@Override public void onSuccess( ) {
					super.onSuccess( );

					//merge conflicts if necessary
					if ( errorLineHandler.hasMergeError ) {
						if ( handleMerge( ) ) {
							UpdateFromDevelopAction completeFinishFeatureAction = new UpdateFromDevelopAction(
									featureName );
							completeFinishFeatureAction.actionPerformed( event );
						}

					}

				}
			}.queue( );
		}

	}

}