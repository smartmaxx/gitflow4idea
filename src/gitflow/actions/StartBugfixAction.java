package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.DialogWrapper;
import git4idea.commands.GitCommandResult;
import gitflow.ui.GitflowStartBugfixDialog;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;


public class StartBugfixAction extends GitflowAction {

	StartBugfixAction( ) {
		super( "Start Bugfix" );
	}

	@Override public void actionPerformed( AnActionEvent e ) {
		super.actionPerformed( e );

		GitflowStartBugfixDialog dialog = new GitflowStartBugfixDialog( myProject );
		dialog.show( );

		if ( dialog.getExitCode( ) != DialogWrapper.OK_EXIT_CODE ) {
			return;
		}

		final String bugfixName = dialog.getNewBranchName( );
		final String baseBranchName = dialog.getBaseBranchName( );

		new Task.Backgroundable( myProject, "Starting bugfix " + bugfixName, false ) {
			@Override public void run( @NotNull ProgressIndicator indicator ) {
				createHotfixBranch( baseBranchName, bugfixName );
			}
		}.queue( );
	}

	private void createHotfixBranch( String baseBranchName, String hotfixBranchName ) {
		GitflowErrorsListener errorListener = new GitflowErrorsListener( myProject );
		GitCommandResult result = myGitflow.startHotfix( repo, hotfixBranchName, baseBranchName, errorListener );

		if ( result.success( ) ) {
			String startedHotfixMessage = String
					.format( "A new bugfix '%s%s' was created, based on '%s'", bugfixPrefix, hotfixBranchName,
							baseBranchName );
			NotifyUtil.notifySuccess( myProject, hotfixBranchName, startedHotfixMessage );
		} else {
			NotifyUtil.notifyError( myProject, "Error",
					"Please have a look at the Version Control console for more details" );
		}

		repo.update( );
	}
}