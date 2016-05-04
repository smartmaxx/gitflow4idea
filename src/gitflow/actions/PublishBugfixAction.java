package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import gitflow.GitflowConfigUtil;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;


public class PublishBugfixAction extends GitflowAction {
    PublishBugfixAction( ) {
        super("Publish Bugfix");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        final String bugfixName = GitflowConfigUtil.getBugfixNameFromBranch(myProject, currentBranchName);
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

        new Task.Backgroundable(myProject, "Publishing bugfix " + bugfixName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitCommandResult result = myGitflow.publishBugfix(repo, bugfixName, errorLineHandler);

                if (result.success()) {
                    String publishedBugfixMessage = String.format("A new remote branch '%s%s' was created", bugfixPrefix, bugfixName);
                    NotifyUtil.notifySuccess(myProject, bugfixName, publishedBugfixMessage);
                } else {
                    NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                }

                repo.update();
            }
        }.queue();

    }
}