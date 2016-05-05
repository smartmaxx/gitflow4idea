package gitflow.ui;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.List;


public class GitflowStartBugfixDialog extends AbstractBranchStartDialog {

	public GitflowStartBugfixDialog( Project project ) {
		super( project );
	}

	@Override protected String getLabel( ) {
		return "bugfix";
	}

	@Override protected String getDefaultBranch( ) {
		List<String> releases = getGitflowBranchUtil( ).getLocalReleaseBranches( );
		if ( !releases.isEmpty( ) ) {
			return releases.get( 0 );
		}
		return "";
	}

	@Override protected JComponent createCenterPanel( ) {
		return super.createCenterPanel( );
	}
}
