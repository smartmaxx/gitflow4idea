package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowConfigUtil;
import gitflow.GitflowConfigurable;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;


public class FinishHotfixAction extends GitflowAction {

	FinishHotfixAction( ) {
		super( "Finish Hotfix" );
	}

	@Override public void actionPerformed( AnActionEvent e ) {
		super.actionPerformed( e );

		final String currentBranchName = GitBranchUtil.getBranchNameOrRev( repo );

		if ( currentBranchName.isEmpty( ) == false ) {

			//TODO HOTFIX NAME
			final String hotfixName = GitflowConfigUtil.getHotfixNameFromBranch( myProject, currentBranchName );

			final String tagMessage;

			String defaultTagMessage = GitflowConfigurable.getCustomHotfixTagCommitMessage( myProject );
			defaultTagMessage = defaultTagMessage.replace( "%name%", hotfixName );

			if ( GitflowConfigurable.dontTagHotfix( myProject ) ) {
				tagMessage = "";
			} else {
				tagMessage = Messages.showInputDialog( myProject, "Enter the tag message:", "Finish Hotfix",
						Messages.getQuestionIcon( ), defaultTagMessage, null );
			}

			final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener( myProject );

			if ( tagMessage != null ) {
				new Task.Backgroundable( myProject, "Finishing hotfix " + hotfixName, false ) {
					@Override public void run( @NotNull ProgressIndicator indicator ) {
						GitCommandResult result = myGitflow
								.finishHotfix( repo, hotfixName, tagMessage, errorLineHandler );

						if ( result.success( ) ) {
							String finishedHotfixMessage = String
									.format( "The hotfix branch '%s%s' was merged into '%s' and '%s'", hotfixPrefix,
											hotfixName, developBranch, masterBranch );
							if ( GitflowConfigurable.featureFinishByPullRq( myProject ) ) {
								finishedHotfixMessage = String.format(
										"The hotfix branch '%s%s' was pushed to origin and deleted locally. Working "
												+ "copy switched to '%s'", hotfixPrefix, hotfixName, masterBranch );
								String stashUrl = GitflowConfigurable.getStashUrl( myProject );
								if ( !StringUtil.isEmpty( stashUrl ) ) {
									GitflowBranchUtil
											.open( stashUrl, hotfixPrefix + hotfixName, masterBranch, myProject );
								}
							}
							NotifyUtil.notifySuccess( myProject, hotfixName, finishedHotfixMessage );
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