package gitflow;

import com.intellij.openapi.project.Project;
import git4idea.GitLocalBranch;
import git4idea.GitRemoteBranch;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import gitflow.ui.NotifyUtil;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowBranchUtil {

	Project myProject;
	GitRepository repo;

	String currentBranchName;
	String branchnameMaster;
	String prefixFeature;
	String prefixRelease;
	String prefixHotfix;
	String prefixBugfix;

	public GitflowBranchUtil( Project project ) {
		myProject = project;
		repo = GitBranchUtil.getCurrentRepository( project );

		currentBranchName = GitBranchUtil.getBranchNameOrRev( repo );

		branchnameMaster = GitflowConfigUtil.getMasterBranch( project );
		prefixFeature = GitflowConfigUtil.getFeaturePrefix( project );
		prefixRelease = GitflowConfigUtil.getReleasePrefix( project );
		prefixHotfix = GitflowConfigUtil.getHotfixPrefix( project );
		prefixBugfix = GitflowConfigUtil.getBugfixPrefix( project );
	}

	public static void open( String stashUrl, String sourceBranch, String destinationBranch, Project myProject ) {
		if ( Desktop.isDesktopSupported( ) ) {
			try {
				String pullRequestUrl = buildPullRequestUrl( stashUrl, sourceBranch, destinationBranch );
				Desktop.getDesktop( ).browse( new URI( pullRequestUrl ) );
				NotifyUtil.notifyInfo( myProject, "Pull Request action",
						"URL: `" + pullRequestUrl + "` is opened in your " + "default web browser" );
			} catch ( Exception e ) {
				NotifyUtil.notifyError( myProject, "Can't open Pull RQ url", e );
			}
		} else {
			NotifyUtil.notifyError( myProject, "Can't open Pull RQ url", "Desktop is not supported" );
		}
	}

	private static String buildPullRequestUrl( String stashUrl, String sourceBranch, String destinationBranch ) {
		return stashUrl + "/pull-requests?create&targetBranch=" + destinationBranch + "&sourceBranch=" + sourceBranch;
	}

	public boolean hasGitflow( ) {
		boolean hasGitflow = false;
		hasGitflow = GitflowConfigUtil.getMasterBranch( myProject ) != null
				&& GitflowConfigUtil.getDevelopBranch( myProject ) != null
				&& GitflowConfigUtil.getFeaturePrefix( myProject ) != null
				&& GitflowConfigUtil.getReleasePrefix( myProject ) != null
				&& GitflowConfigUtil.getHotfixPrefix( myProject ) != null
				&& GitflowConfigUtil.getBugfixPrefix( myProject ) != null;

		return hasGitflow;
	}

	public boolean isCurrentBranchMaster( ) {
		return currentBranchName.startsWith( branchnameMaster );
	}

	public boolean isCurrentBranchFeature( ) {
		return currentBranchName.startsWith( prefixFeature );
	}

	public boolean isCurrentBranchRelease( ) {
		return currentBranchName.startsWith( prefixRelease );
	}

	public boolean isCurrentBranchHotfix( ) {
		return currentBranchName.startsWith( prefixHotfix );
	}

	//checks whether the current branch also exists on the remote
	public boolean isCurrentBranchPublished( ) {
		return getRemoteBranchesWithPrefix( currentBranchName ).isEmpty( ) == false;
	}

	//if no prefix specified, returns all remote branches
	public ArrayList<String> getRemoteBranchesWithPrefix( String prefix ) {
		ArrayList<String> remoteBranches = getRemoteBranchNames( );
		ArrayList<String> selectedBranches = new ArrayList<String>( );

		for ( Iterator<String> i = remoteBranches.iterator( ); i.hasNext( ); ) {
			String branch = i.next( );
			if ( branch.contains( prefix ) ) {
				selectedBranches.add( branch );
			}
		}

		return selectedBranches;
	}

	public ArrayList<String> filterBranchListByPrefix( Collection<String> inputBranches, String prefix ) {
		ArrayList<String> outputBranches = new ArrayList<String>( );

		for ( Iterator<String> i = inputBranches.iterator( ); i.hasNext( ); ) {
			String branch = i.next( );
			if ( branch.contains( prefix ) ) {
				outputBranches.add( branch );
			}
		}

		return outputBranches;
	}

	public ArrayList<String> getRemoteBranchNames( ) {
		ArrayList<GitRemoteBranch> remoteBranches = new ArrayList<GitRemoteBranch>(
				repo.getBranches( ).getRemoteBranches( ) );
		ArrayList<String> branchNameList = new ArrayList<String>( );

		for ( Iterator<GitRemoteBranch> i = remoteBranches.iterator( ); i.hasNext( ); ) {
			GitRemoteBranch branch = i.next( );
			branchNameList.add( branch.getName( ) );
		}

		return branchNameList;
	}

	public ArrayList<String> getLocalBranchNames( ) {
		ArrayList<GitLocalBranch> localBranches = new ArrayList<GitLocalBranch>(
				repo.getBranches( ).getLocalBranches( ) );
		ArrayList<String> branchNameList = new ArrayList<String>( );

		for ( Iterator<GitLocalBranch> i = localBranches.iterator( ); i.hasNext( ); ) {
			GitLocalBranch branch = i.next( );
			branchNameList.add( branch.getName( ) );
		}

		return branchNameList;
	}

	public GitRemote getRemoteByBranch( String branchName ) {
		GitRemote remote = null;

		ArrayList<GitRemoteBranch> remoteBranches = new ArrayList<GitRemoteBranch>(
				repo.getBranches( ).getRemoteBranches( ) );

		for ( Iterator<GitRemoteBranch> i = remoteBranches.iterator( ); i.hasNext( ); ) {
			GitRemoteBranch branch = i.next( );
			if ( branch.getName( ) == branchName ) {
				remote = branch.getRemote( );
				break;
			}
		}

		return remote;
	}

	public boolean areAllBranchesTracked( String prefix ) {

		ArrayList<String> localBranches = filterBranchListByPrefix( getLocalBranchNames( ), prefix );

		//to avoid a vacuous truth value. That is, when no branches at all exist, they shouldn't be
		//considered as "all pulled"
		if ( localBranches.isEmpty( ) ) {
			return false;
		}

		ArrayList<String> remoteBranches = getRemoteBranchNames( );

		//check that every local branch has a matching remote branch
		for ( Iterator<String> i = localBranches.iterator( ); i.hasNext( ); ) {
			String localBranch = i.next( );
			boolean hasMatchingRemoteBranch = false;

			for ( Iterator<String> j = remoteBranches.iterator( ); j.hasNext( ); ) {
				String remoteBranch = j.next( );

				if ( remoteBranch.contains( localBranch ) ) {
					hasMatchingRemoteBranch = true;
					break;
				}
			}

			//at least one matching branch wasn't found
			if ( hasMatchingRemoteBranch == false ) {
				return false;
			}
		}

		return true;
	}

	public List<String> getLocalReleaseBranches( ) {
		return filterBranchListByPrefix( getLocalBranchNames( ), prefixRelease );
	}

	public List<String> getRemoteReleaseBranches( ) {
		return filterBranchListByPrefix( getRemoteBranchNames( ), prefixRelease );
	}

	public boolean isCurrentBranchBugfix( ) {
		return currentBranchName.startsWith( prefixBugfix );
	}
}
