package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import gitflow.GitflowConfigUtil;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;


public class PushAction extends GitflowAction {
	private final String branchType;

	PushAction( String branchType ) {
		super( "Push " +branchType);
		this.branchType = branchType;
	}

	@Override public void actionPerformed( AnActionEvent anActionEvent ) {
		super.actionPerformed( anActionEvent );

		final String gitflowBranch = GitflowConfigUtil.getCurrentBranchShortName( myProject, currentBranchName );

		new Task.Backgroundable( myProject, "Pushing "+ branchType +" " + gitflowBranch, false ) {
			@Override public void run( @NotNull ProgressIndicator indicator ) {
				GitCommandResult result = myGitflow
						.pushBranch( repo, currentBranchName, false, new GitflowErrorsListener( myProject ) );

				if ( result.success( ) ) {
					String pushedBranshMessage = String
							.format( "A remote branchType '%s%s' was created/update", featurePrefix,
									gitflowBranch );
					NotifyUtil.notifySuccess( myProject, gitflowBranch, pushedBranshMessage );
				} else {
					NotifyUtil.notifyError( myProject, "Error",
							"Please have a look at the Version Control console for more details" );
				}

				repo.update( );

			}
		}.queue( );

	}
}