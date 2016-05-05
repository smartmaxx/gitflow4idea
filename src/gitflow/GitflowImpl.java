package gitflow;

import git4idea.commands.*;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */

public class GitflowImpl extends GitImpl implements Gitflow {

	//we must use reflection to add this command, since the git4idea implementation doesn't expose it
	private GitCommand GitflowCommand( ) {
		Method m = null;
		try {
			m = GitCommand.class.getDeclaredMethod( "write", String.class );
		} catch ( NoSuchMethodException e ) {
			e.printStackTrace( );
		}
		//m.invoke(d);//exception java.lang.IllegalAccessException
		m.setAccessible( true );//Abracadabra

		GitCommand command = null;

		try {
			command = ( GitCommand ) m.invoke( null, "flow" );//now its ok
		} catch ( IllegalAccessException e ) {
			e.printStackTrace( );
		} catch ( InvocationTargetException e ) {
			e.printStackTrace( );
		}

		return command;
	}

	//we must use reflection to add this command, since the git4idea implementation doesn't expose it
	private static GitCommandResult run( @org.jetbrains.annotations.NotNull git4idea.commands.GitLineHandler handler ) {
		Method m = null;
		try {
			m = GitImpl.class.getDeclaredMethod( "run", GitLineHandler.class );
		} catch ( NoSuchMethodException e ) {
			e.printStackTrace( );
		}

		m.setAccessible( true );//Abracadabra

		GitCommandResult result = null;

		try {
			result = ( GitCommandResult ) m.invoke( null, handler );//now its ok
		} catch ( IllegalAccessException e ) {
			e.printStackTrace( );
		} catch ( InvocationTargetException e ) {
			e.printStackTrace( );
		}

		return result;
	}

	public GitCommandResult initRepo( @NotNull GitRepository repository, GitflowInitOptions initOptions,
			@Nullable GitLineHandlerListener... listeners ) {

		GitCommandResult result;

		if ( initOptions.isUseDefaults( ) ) {
			final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
					GitflowCommand( ) );
			h.setSilent( false );
			h.setStdoutSuppressed( false );
			h.setStderrSuppressed( false );

			h.addParameters( "init" );
			h.addParameters( "-d" );

			result = run( h );
		} else {

			final GitInitLineHandler h = new GitInitLineHandler( initOptions, repository.getProject( ),
					repository.getRoot( ), GitflowCommand( ) );

			h.setSilent( false );
			h.setStdoutSuppressed( false );
			h.setStderrSuppressed( false );

			h.addParameters( "init" );

			for ( GitLineHandlerListener listener : listeners ) {
				h.addLineListener( listener );
			}
			result = run( h );
		}

		return result;
	}

	//feature

	public GitCommandResult startFeature( @NotNull GitRepository repository, @NotNull String featureName,
			@Nullable String baseBranch, @Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		h.setSilent( false );

		h.addParameters( "feature" );
		h.addParameters( "start" );
		if ( GitflowConfigurable.featureFetchOrigin( repository.getProject( ) ) ) {
			h.addParameters( "-F" );
		}
		h.addParameters( featureName );

		if ( baseBranch != null ) {
			h.addParameters( baseBranch );
		}

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		GitCommandResult result = run( h );
		if ( result.success( ) && GitflowConfigurable.featurePublishOnStart( repository.getProject( ) ) ) {
			result = publishFeature( repository, featureName, listeners );
		}
		return result;
	}

	public GitCommandResult finishFeature( @NotNull GitRepository repository, @NotNull String featureName,
			@Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );

		setUrl( h, repository );
		h.setSilent( false );

		if ( GitflowConfigurable.featureFinishByPullRq( repository.getProject( ) ) ) {
			GitCommandResult result = pushBranch( repository,
					GitflowConfigUtil.getFeaturePrefix( repository.getProject( ) ) + featureName, false, listeners );
			if ( result.success( ) ) {
				GitLineHandler handler = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
						GitflowCommand( ) );
				handler.addParameters( "feature" );
				handler.addParameters( "delete" );
				handler.addParameters( featureName );
				handler.addParameters( "-f" );
				return run( handler );
			}
			return result;
		} else {

			h.addParameters( "finish" );

			if ( GitflowConfigurable.featureKeepRemote( repository.getProject( ) ) ) {
				h.addParameters( "--keepremote" );
			}

			if ( GitflowConfigurable.featureFetchOrigin( repository.getProject( ) ) ) {
				h.addParameters( "-F" );
			}

			h.addParameters( featureName );

			for ( GitLineHandlerListener listener : listeners ) {
				h.addLineListener( listener );
			}
			return run( h );
		}
	}

	public GitCommandResult publishFeature( @NotNull GitRepository repository, @NotNull String featureName,
			@Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		setUrl( h, repository );
		h.setSilent( false );

		h.addParameters( "feature" );
		h.addParameters( "publish" );
		h.addParameters( featureName );

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		return run( h );
	}

	public GitCommandResult pushBranch( @NotNull GitRepository repository, @NotNull String branchName, boolean withTags,
			@Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ), GitCommand.PUSH );
		setUrl( h, repository );
		h.setSilent( false );

		h.addParameters( "origin" );
		//        h.addParameters("publish");
		h.addParameters( branchName );
		if ( withTags ) {
			h.addParameters( "--tags" );
		}

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		return run( h );
	}

	public GitCommandResult addTag( @NotNull GitRepository repository, @NotNull String tagName,
			@NotNull String tagMessage, @Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ), GitCommand.TAG );
		setUrl( h, repository );
		h.setSilent( false );

		h.addParameters( "-a" );
		h.addParameters( tagName );
		h.addParameters( "-m" );
		h.addParameters( tagMessage );

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		return run( h );
	}

	// feature pull seems to be kind of useless. see
	// http://stackoverflow.com/questions/18412750/why-doesnt-git-flow-feature-pull-track
	public GitCommandResult pullFeature( @NotNull GitRepository repository, @NotNull String featureName,
			@NotNull GitRemote remote, @Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		setUrl( h, repository );
		h.setSilent( false );
		h.addParameters( "feature" );
		h.addParameters( "pull" );
		h.addParameters( remote.getName( ) );
		h.addParameters( featureName );

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		return run( h );
	}

	public GitCommandResult trackFeature( @NotNull GitRepository repository, @NotNull String featureName,
			@NotNull GitRemote remote, @Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		setUrl( h, repository );
		h.setSilent( false );
		h.addParameters( "feature" );
		h.addParameters( "track" );
		h.addParameters( featureName );

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		return run( h );
	}

	//release

	public GitCommandResult startRelease( @NotNull GitRepository repository, @NotNull String releaseName,
			@Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		h.setSilent( false );

		h.addParameters( "release" );
		h.addParameters( "start" );

		if ( GitflowConfigurable.releaseFetchOrigin( repository.getProject( ) ) ) {
			h.addParameters( "-F" );
		}

		h.addParameters( releaseName );

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		GitCommandResult result = run( h );
		if ( result.success( ) && GitflowConfigurable.releasePublishOnStart( repository.getProject( ) ) ) {
			result = publishRelease( repository, releaseName, listeners);
		}
		return result;
	}

	public GitCommandResult finishRelease( @NotNull GitRepository repository, @NotNull String releaseName,
			@NotNull String tagMessage, @Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		setUrl( h, repository );
		h.setSilent( false );
		if ( GitflowConfigurable.releaseFinishByPullRq( repository.getProject( ) ) ) {
			boolean useTagging = !GitflowConfigurable.dontTagRelease( repository.getProject( ) );
			if ( useTagging ) {
				GitCommandResult result = addTag( repository, releaseName, tagMessage, listeners );
				if ( !result.success( ) ) {
					return result;
				}
			}
			GitCommandResult result = pushBranch( repository,
					GitflowConfigUtil.getReleasePrefix( repository.getProject( ) ) + releaseName, useTagging,
					listeners );
			if ( result.success( ) ) {

				GitLineHandler handler = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
						GitflowCommand( ) );
				handler.addParameters( "release" );
				handler.addParameters( "delete" );
				handler.addParameters( releaseName );
				handler.addParameters( "-f" );
				return run( handler );
			}
			return result;
		} else {

			h.addParameters( "release" );
			h.addParameters( "finish" );
			if ( GitflowConfigurable.releaseFetchOrigin( repository.getProject( ) ) ) {
				h.addParameters( "-F" );
			}
			if ( GitflowConfigurable.pushOnReleaseFinish( repository.getProject( ) ) ) {
				h.addParameters( "-p" );
			}

			if ( GitflowConfigurable.dontTagRelease( repository.getProject( ) ) ) {
				h.addParameters( "-n" );
			} else {
				h.addParameters( "-m" );
				h.addParameters( tagMessage );
			}

			h.addParameters( releaseName );

			for ( GitLineHandlerListener listener : listeners ) {
				h.addLineListener( listener );
			}
			return run( h );
		}
	}

	public GitCommandResult publishRelease( @NotNull GitRepository repository, @NotNull String releaseName,
			@Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		setUrl( h, repository );

		h.setSilent( false );

		h.addParameters( "release" );
		h.addParameters( "publish" );
		h.addParameters( releaseName );

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		return run( h );
	}

	public GitCommandResult trackRelease( @NotNull GitRepository repository, @NotNull String releaseName,
			@Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		setUrl( h, repository );
		h.setSilent( false );

		h.addParameters( "release" );
		h.addParameters( "track" );
		h.addParameters( releaseName );

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		return run( h );
	}

	//hotfix

	public GitCommandResult startHotfix( @NotNull GitRepository repository, @NotNull String hotfixName,
			@Nullable String baseBranch, @Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		h.setSilent( false );

		h.addParameters( "hotfix" );
		h.addParameters( "start" );
		if ( GitflowConfigurable.hotfixFetchOrigin( repository.getProject( ) ) ) {
			h.addParameters( "-F" );
		}
		h.addParameters( hotfixName );

		if ( baseBranch != null ) {
			h.addParameters( baseBranch );
		}

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		GitCommandResult result = run( h );
		if ( result.success( ) && GitflowConfigurable.hotfixPublishOnStart( repository.getProject( ) ) ) {
			result = publishHotfix( repository, hotfixName, listeners );
		}
		return result;
	}

	public GitCommandResult startBugfix( @NotNull GitRepository repository, @NotNull String bugfixName,
			@Nullable String baseBranch, @Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		h.setSilent( false );

		h.addParameters( "bugfix" );
		h.addParameters( "start" );
		if ( GitflowConfigurable.bugfixFetchOrigin( repository.getProject( ) ) ) {
			h.addParameters( "-F" );
		}
		h.addParameters( bugfixName );

		if ( baseBranch != null ) {
			h.addParameters( baseBranch );
		}

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		GitCommandResult result = run( h );
		if ( result.success( ) && GitflowConfigurable.bugfixPublishOnStart( repository.getProject( ) ) ) {
			result = publishBugfix( repository, bugfixName, listeners );
		}
		return result;
	}

	public GitCommandResult finishHotfix( @NotNull GitRepository repository, @NotNull String hotfixName,
			@NotNull String tagMessage, @Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		setUrl( h, repository );
		h.setSilent( false );
		if ( GitflowConfigurable.hotfixFinishByPullRq( repository.getProject( ) ) ) {
			boolean useTagging = !GitflowConfigurable.dontTagHotfix( repository.getProject( ) );
			if ( useTagging ) {
				GitCommandResult result = addTag( repository, hotfixName, tagMessage, listeners );
				if ( !result.success( ) ) {
					return result;
				}
			}
			GitCommandResult result = pushBranch( repository,
					GitflowConfigUtil.getHotfixPrefix( repository.getProject( ) ) + hotfixName, useTagging, listeners );
			if ( result.success( ) ) {

				GitLineHandler handler = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
						GitflowCommand( ) );
				handler.addParameters( "hotfix" );
				handler.addParameters( "delete" );
				handler.addParameters( hotfixName );
				handler.addParameters( "-f" );
				return run( handler );
			}
			return result;
		} else {

			h.addParameters( "hotfix" );
			h.addParameters( "finish" );
			if ( GitflowConfigurable.hotfixFetchOrigin( repository.getProject( ) ) ) {
				h.addParameters( "-F" );
			}
			if ( GitflowConfigurable.pushOnHotfixFinish( repository.getProject( ) ) ) {
				h.addParameters( "-p" );
			}

			if ( GitflowConfigurable.dontTagHotfix( repository.getProject( ) ) ) {
				h.addParameters( "-n" );
			} else {
				h.addParameters( "-m" );
				h.addParameters( tagMessage );
			}

			h.addParameters( hotfixName );

			for ( GitLineHandlerListener listener : listeners ) {
				h.addLineListener( listener );
			}
			return run( h );
		}
	}

	public GitCommandResult finishBugfix( @NotNull GitRepository repository, @NotNull String bugfixName,
			@Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		setUrl( h, repository );
		h.setSilent( false );
		if ( GitflowConfigurable.bugfixFinishByPullRq( repository.getProject( ) ) ) {
			GitCommandResult result = pushBranch( repository,
					GitflowConfigUtil.getBugfixPrefix( repository.getProject( ) ) + bugfixName, false, listeners );
			if ( result.success( ) ) {
				GitLineHandler handler = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
						GitflowCommand( ) );
				handler.addParameters( "bugfix" );
				handler.addParameters( "delete" );
				handler.addParameters( bugfixName );
				handler.addParameters( "-f" );
				return run( handler );
			}
			return result;
		} else {

			h.addParameters( "bugfix" );
			h.addParameters( "finish" );
			if ( GitflowConfigurable.bugfixFetchOrigin( repository.getProject( ) ) ) {
				h.addParameters( "-F" );
			}
			if ( GitflowConfigurable.pushOnBugfixFinish( repository.getProject( ) ) ) {
				h.addParameters( "-p" );
			}

			h.addParameters( bugfixName );

			for ( GitLineHandlerListener listener : listeners ) {
				h.addLineListener( listener );
			}
			return run( h );
		}
	}

	public GitCommandResult publishHotfix( @NotNull GitRepository repository, @NotNull String hotfixName,
			@Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		setUrl( h, repository );

		h.setSilent( false );

		h.addParameters( "hotfix" );
		h.addParameters( "publish" );
		h.addParameters( hotfixName );

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		return run( h );
	}

	public GitCommandResult publishBugfix( @NotNull GitRepository repository, @NotNull String bugfixName,
			@Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		setUrl( h, repository );

		h.setSilent( false );

		h.addParameters( "bugfix" );
		h.addParameters( "publish" );
		h.addParameters( bugfixName );

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		return run( h );
	}

	@Override public GitCommandResult updateFromDevelop( @NotNull GitRepository repository, @NotNull String featureName,
			@Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitCommand.CHECKOUT );
		setUrl( h, repository );
		//1. switch to develop
		h.setSilent( false );

		h.addParameters( "develop" );

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		GitCommandResult result = run( h );
		if ( result.success( ) ) {
			//2. pull from origin to develop
			GitLineHandler handler = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
					GitCommand.PULL );
			handler.addParameters( "origin" );
			handler.addParameters( "develop" );
			for ( GitLineHandlerListener listener : listeners ) {
				handler.addLineListener( listener );
			}
			result = run( handler );
			if ( result.success( ) ) {
				//3. go back to feature
				handler = new GitLineHandler( repository.getProject( ), repository.getRoot( ), GitCommand.CHECKOUT );
				handler.addParameters( featureName );
				for ( GitLineHandlerListener listener : listeners ) {
					handler.addLineListener( listener );
				}
				result = run( handler );
				if ( result.success( ) ) {
					//4. merge from develop to feature
					handler = new GitLineHandler( repository.getProject( ), repository.getRoot( ), GitCommand.MERGE );
					handler.addParameters( "origin/develop" );
					for ( GitLineHandlerListener listener : listeners ) {
						handler.addLineListener( listener );
					}
					result = run( handler );
					return result;
				}
			}
		}
		return result;
	}

	private void setUrl( GitLineHandler h, GitRepository repository ) {
		ArrayList<GitRemote> remotes = new ArrayList<GitRemote>( repository.getRemotes( ) );

		//make sure a remote repository is available
		if ( !remotes.isEmpty( ) ) {
			h.setUrl( remotes.iterator( ).next( ).getFirstUrl( ) );
		}
	}

	public GitCommandResult trackBugfix( @NotNull GitRepository repository, @NotNull String bugfixName,
			@Nullable GitLineHandlerListener... listeners ) {
		final GitLineHandler h = new GitLineHandler( repository.getProject( ), repository.getRoot( ),
				GitflowCommand( ) );
		setUrl( h, repository );
		h.setSilent( false );

		h.addParameters( "bugfix" );
		h.addParameters( "track" );
		h.addParameters( bugfixName );

		for ( GitLineHandlerListener listener : listeners ) {
			h.addLineListener( listener );
		}
		return run( h );
	}

}
