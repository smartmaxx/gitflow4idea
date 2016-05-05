package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.text.StringUtil;
import git4idea.commands.GitCommandResult;
import gitflow.GitflowConfigUtil;
import gitflow.ui.GitflowBranchChooseDialog;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;


public class TrackBugfixAction extends GitflowAction {

	TrackBugfixAction( ) {
		super( "Track Bugfix" );
	}

	@Override public void actionPerformed( AnActionEvent e ) {
		super.actionPerformed( e );

		ArrayList<String> remoteBugfixBranches = branchUtil.getRemoteBranchesWithPrefix( bugfixPrefix );

		if ( remoteBugfixBranches.size( ) > 0 ) {
			GitflowBranchChooseDialog branchChoose = new GitflowBranchChooseDialog( myProject, remoteBugfixBranches );

			branchChoose.show( );
			if ( branchChoose.isOK( ) ) {
				String branchName = branchChoose.getSelectedBranchName( );
				if ( StringUtil.isEmpty( branchName ) ) {
					NotifyUtil.notifyError( myProject, "Error", "No remote branch selected" );
					return;
				}
				final String bugfixName = GitflowConfigUtil.getBugfixNameFromBranch( myProject, branchName );
				final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener( myProject );

				new Task.Backgroundable( myProject, "Tracking Bugfix " + bugfixName, false ) {
					@Override public void run( @NotNull ProgressIndicator indicator ) {
						GitCommandResult result = myGitflow.trackBugfix( repo, bugfixName, errorLineHandler );

						if ( result.success( ) ) {
							String trackedBugfixMessage = String
									.format( "A new branch '%s%s' was created", bugfixPrefix, bugfixName );
							NotifyUtil.notifySuccess( myProject, bugfixName, trackedBugfixMessage );
						} else {
							NotifyUtil.notifyError( myProject, "Error",
									"Please have a look at the Version Control console for more details" );
						}

						repo.update( );

					}
				}.queue( );
			}
		} else {
			NotifyUtil.notifyError( myProject, "Error", "No remote Bugfix branches" );
		}

	}
}