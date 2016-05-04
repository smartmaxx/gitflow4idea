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
import gitflow.ui.GitflowBranchChooseDialog;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class FinishBugfixAction extends GitflowAction {

	FinishBugfixAction( ) {
		super( "Finish Bugfix" );
	}

	@Override public void actionPerformed( AnActionEvent e ) {
		super.actionPerformed( e );

		List<String> remoteReleaseBranches = branchUtil.getRemoteReleaseBranches( );

		if ( remoteReleaseBranches.size( ) > 0 ) {
			String originatingRelease = "";
			List<String> localReleaseBranches = branchUtil.getLocalReleaseBranches( );
			if ( !localReleaseBranches.isEmpty( ) && localReleaseBranches.size( ) == 1 ) {
				originatingRelease = localReleaseBranches.get( 0 );
			}
			if ( StringUtil.isEmpty( originatingRelease ) ) {
				NotifyUtil.notifyError( myProject, "Error", "No local release found" );
				return;
			}
			List<String> filteredRemoteReleases = branchUtil
					.filterBranchListByPrefix( remoteReleaseBranches, originatingRelease );
			if ( filteredRemoteReleases.isEmpty( ) ) {
				NotifyUtil.notifyError( myProject, "Error",
						"Local release branch `" + originatingRelease + "` not found in remote list. Please publish "
								+ "it or fetch changes from origin" );
				return;
			}
			String originatingRemoteRelease = filteredRemoteReleases.get( 0 );
			GitflowBranchChooseDialog branchChoose = new GitflowBranchChooseDialog( myProject, remoteReleaseBranches,
					originatingRemoteRelease );

			branchChoose.show( );
			if ( branchChoose.isOK( ) ) {
				String branchName = branchChoose.getSelectedBranchName( );
				if ( StringUtil.isEmpty( branchName ) ) {
					NotifyUtil.notifyError( myProject, "Error", "No release branch selected" );
					return;
				}
				String currentBranchName = GitBranchUtil.getBranchNameOrRev( repo );

				if ( currentBranchName.isEmpty( ) == false ) {

					final String bugfixName = GitflowConfigUtil.getBugfixNameFromBranch( myProject, currentBranchName );

					final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener( myProject );

					final String originatingLocalRelease = originatingRelease;
					new Task.Backgroundable( myProject, "Finishing bugfix " + bugfixName, false ) {
						@Override public void run( @NotNull ProgressIndicator indicator ) {
							GitCommandResult result = myGitflow.finishBugfix( repo, bugfixName, errorLineHandler );

							if ( result.success( ) ) {
								String finishedBugfixMessage = String
										.format( "The bugfix branch '%s%s' was merged into '%s' and '%s'", bugfixPrefix,
												bugfixName, releasePrefix, masterBranch );
								if ( GitflowConfigurable.bugfixFinishByPullRq( myProject ) ) {
									finishedBugfixMessage = String.format(
											"The bugfix branch '%s%s' was pushed to origin and deleted locally. "
													+ "Working copy switched to '%s'", featurePrefix, bugfixName,
											originatingLocalRelease );
									String stashUrl = GitflowConfigurable.getStashUrl( myProject );
									if ( !StringUtil.isEmpty( stashUrl ) ) {
										GitflowBranchUtil
												.open( stashUrl, bugfixPrefix + bugfixName, originatingLocalRelease,
														myProject );
									}
								}
								NotifyUtil.notifySuccess( myProject, bugfixName, finishedBugfixMessage );
							} else {
								NotifyUtil.notifyError( myProject, "Error",
										"Please have a look at the Version Control console for more details" );
							}

							repo.update( );
						}
					}.queue( );
				}
			}

		}
	}
}