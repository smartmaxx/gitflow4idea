package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.text.StringUtil;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowConfigUtil;
import gitflow.GitflowConfigurable;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;


public class FinishFeatureAction extends GitflowAction {

	String customFeatureName = null;

	FinishFeatureAction( ) {
		super( "Finish Feature" );
	}

	FinishFeatureAction( String name ) {
		super( "Finish Feature" );
		customFeatureName = name;
	}

	@Override public void actionPerformed( final AnActionEvent e ) {
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

			new Task.Backgroundable( myProject, "Finishing feature " + featureName, false ) {
				@Override public void run( @NotNull ProgressIndicator indicator ) {
					GitCommandResult result = myGitflow.finishFeature( repo, featureName, errorLineHandler );

					if ( result.success( ) ) {
						String finishedFeatureMessage = String
								.format( "The feature branch '%s%s' was merged into '%s'", featurePrefix, featureName,
										developBranch );
						if ( GitflowConfigurable.featureFinishByPullRq( myProject ) ) {
							finishedFeatureMessage = String.format(
									"The feature branch '%s%s' was pushed to origin and deleted locally. Working copy switched to '%s'",
									featurePrefix, featureName, developBranch );
							String stashUrl = GitflowConfigurable.getStashUrl( myProject );
							if ( !StringUtil.isEmpty( stashUrl ) ) {
								GitflowBranchUtil
										.open( stashUrl, featurePrefix + featureName, developBranch, myProject );
							}
						}
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
							FinishFeatureAction completeFinishFeatureAction = new FinishFeatureAction( featureName );
							completeFinishFeatureAction.actionPerformed( event );
						}

					}

				}
			}.queue( );
		}

	}

}