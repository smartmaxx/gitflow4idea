package gitflow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;


/**
 * Dialog for choosing branches
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */

public class GitflowBranchChooseDialog extends DialogWrapper {
	private JPanel contentPane;
	private JList branchList;

	public GitflowBranchChooseDialog( Project project, List<String> branchNames, String selectedValue ) {
		this( project, branchNames );
		if ( !StringUtil.isEmpty( selectedValue ) ) {
			branchList.setSelectedValue( selectedValue, true );
		}
	}

	public GitflowBranchChooseDialog( Project project, List<String> branchNames ) {
		super( project, true );

		setModal( true );

		setTitle( "Choose Branch" );
		branchList.setListData( branchNames.toArray( ) );

		init( );
	}

	@Nullable @Override protected JComponent createCenterPanel( ) {
		return contentPane;
	}

	public String getSelectedBranchName( ) {
		try {
			return branchList.getSelectedValue( ).toString( );
		} catch ( Exception e ) {
			return null;
		}
	}
}
