package gitflow;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import gitflow.ui.GitflowOptionsForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


/**
 * @author Andreas Vogler (Andreas.Vogler@geneon.de)
 * @author Opher Vishnia (opherv@gmail.com)
 */

public class GitflowConfigurable implements Configurable {
	public static final String GITFLOW_FEATURE_FETCH_ORIGIN = "Gitflow.featureFetchOrigin";
	public static final String GITFLOW_FEATURE_FINISH_BY_PULL_RQ = "Gitflow.featureFinishWithPullRq";
	public static final String GITFLOW_FEATURE_KEEP_REMOTE = "Gitflow.featureKeepRemote";
	public static final String GITFLOW_FEATURE_PUBLISH_ON_START = "Gitflow.featurePublishOnStart";

	public static final String GITFLOW_RELEASE_FETCH_ORIGIN = "Gitflow.releaseFetchOrigin";
	public static final String GITFLOW_PUSH_ON_FINISH_RELEASE = "Gitflow.pushOnFinishRelease";
	public static final String GITFLOW_RELEASE_PUBLISH_ON_START = "Gitflow.releasePublishOnStart";
	public static final String GITFLOW_RELEASE_FINISH_BY_PULL_RQ = "Gitflow.releaseFinishWithPullRq";
	public static final String GITFLOW_PUSH_ON_FINISH_HOTFIX = "Gitflow.pushOnFinishHotfix";
	public static final String GITFLOW_HOTFIX_FINISH_BY_PULL_RQ = "Gitflow.hotfixFinishWithPullRq";
	public static final String GITFLOW_HOTFIX_PUBLISH_ON_START = "Gitflow.hotfixPublishOnStart";
	public static final String GITFLOW_DONT_TAG_RELEASE = "Gitflow.dontTagRelease";
	public static final String GITFLOW_USE_CUSTOM_TAG_COMMIT_MESSAGE = "Gitflow.useCustomTagCommitMessage";
	public static final String GITFLOW_CUSTOM_TAG_COMMIT_MESSAGE = "Gitflow.customTagCommitMessage";

	public static final String GITFLOW_PUSH_ON_FINISH_BUGFIX = "Gitflow.pushOnFinishBugfix";
	public static final String GITFLOW_BUGFIX_FINISH_BY_PULL_RQ = "Gitflow.bugfixFinishWithPullRq";
	public static final String GITFLOW_BUGFIX_PUBLISH_ON_START = "Gitflow.bugfixPublishOnStart";

	public static final String GITFLOW_HOTFIX_FETCH_ORIGIN = "Gitflow.hotfixFetchOrigin";
	public static final String GITFLOW_DONT_TAG_HOTFIX = "Gitflow.dontTagHotfix";
	public static final String GITFLOW_USE_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE = "Gitflow.useCustomHotfixTagCommitMessage";
	public static final String GITFLOW_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE = "Gitflow.customHotfixTagCommitMessage";

	public static final String GITFLOW_BUGFIX_FETCH_ORIGIN = "Gitflow.bugfixFetchOrigin";
	public static final String GITFLOW_DONT_TAG_BUGFIX = "Gitflow.dontTagBugfix";
	public static final String GITFLOW_USE_CUSTOM_BUGFIX_TAG_COMMIT_MESSAGE = "Gitflow.useCustomBugfixTagCommitMessage";
	public static final String GITFLOW_CUSTOM_BUGFIX_TAG_COMMIT_MESSAGE = "Gitflow.customBugfixTagCommitMessage";

	public static final String GITFLOW_STASH_URL = "Gitflow.stashUrl";

	public static final String DEFAULT_TAG_COMMIT_MESSAGE = "Tagging version %name%";
	public static final String DEFAULT_TAG_HOTFIX_COMMIT_MESSAGE = "Tagging version %name%";
	private static final String DEFAULT_STASH_URL = "";
	Project project;

	GitflowOptionsForm gitflowOptionsForm;

	public GitflowConfigurable( Project project ) {
		this.project = project;
	}

    /* feature */

	public static boolean featureFetchOrigin( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_FEATURE_FETCH_ORIGIN, false );
	}

	public static boolean featureKeepRemote( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_FEATURE_KEEP_REMOTE, false );
	}

	public static boolean featureFinishByPullRq( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_FEATURE_FINISH_BY_PULL_RQ, true );
	}

	public static boolean featurePublishOnStart( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_FEATURE_PUBLISH_ON_START, false );
	}

    /* release */

	public static boolean releaseFetchOrigin( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_RELEASE_FETCH_ORIGIN, false );
	}

	public static boolean pushOnReleaseFinish( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_PUSH_ON_FINISH_RELEASE, false );
	}

	public static boolean releaseFinishByPullRq( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_RELEASE_FINISH_BY_PULL_RQ, true );
	}

	public static boolean releasePublishOnStart( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_RELEASE_PUBLISH_ON_START, true );
	}

	public static boolean dontTagRelease( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_DONT_TAG_RELEASE, false );
	}

    /* finish release custom commit message */

	public static boolean useCustomTagCommitMessage( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_USE_CUSTOM_TAG_COMMIT_MESSAGE, false );
	}

	public static String getCustomTagCommitMessage( Project project ) {
		if ( useCustomTagCommitMessage( project ) ) {
			return PropertiesComponent.getInstance( project )
					.getValue( GitflowConfigurable.GITFLOW_CUSTOM_TAG_COMMIT_MESSAGE );
		} else {
			return GitflowConfigurable.DEFAULT_TAG_COMMIT_MESSAGE;
		}
	}

    /*hotfix*/

	public static boolean hotfixFetchOrigin( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_HOTFIX_FETCH_ORIGIN, false );
	}

	public static boolean pushOnHotfixFinish( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_PUSH_ON_FINISH_HOTFIX, false );
	}

	public static boolean hotfixFinishByPullRq( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_HOTFIX_FINISH_BY_PULL_RQ, true );
	}

	public static boolean hotfixPublishOnStart( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_HOTFIX_PUBLISH_ON_START, false );
	}

	public static boolean dontTagHotfix( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_DONT_TAG_HOTFIX, false );
	}

	public static boolean dontTagBugfix( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_DONT_TAG_BUGFIX, true );
	}

	/* finish hotfix custom commit message */
	public static boolean useCustomHotfixTagCommitMessage( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_USE_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, false );
	}

	public static boolean useCustomBugfixTagCommitMessage( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_USE_CUSTOM_BUGFIX_TAG_COMMIT_MESSAGE, false );
	}

	public static String getCustomHotfixTagCommitMessage( Project project ) {
		if ( useCustomHotfixTagCommitMessage( project ) ) {
			return PropertiesComponent.getInstance( project )
					.getValue( GitflowConfigurable.GITFLOW_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE );
		} else {
			return GitflowConfigurable.DEFAULT_TAG_HOTFIX_COMMIT_MESSAGE;
		}
	}

	/* bugfix */

	public static boolean bugfixFetchOrigin( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_BUGFIX_FETCH_ORIGIN, false );
	}

	public static boolean pushOnBugfixFinish( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_PUSH_ON_FINISH_BUGFIX, false );
	}

	public static boolean bugfixFinishByPullRq( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_BUGFIX_FINISH_BY_PULL_RQ, true );
	}

	public static boolean bugfixPublishOnStart( Project project ) {
		return PropertiesComponent.getInstance( project )
				.getBoolean( GitflowConfigurable.GITFLOW_BUGFIX_PUBLISH_ON_START, false );
	}

	public static String getStashUrl( Project project ) {
		return PropertiesComponent.getInstance( project ).getValue( GitflowConfigurable.GITFLOW_STASH_URL, "" );
	}

	@Override public String getDisplayName( ) {
		return "GitflowEx";
	}

	@Nullable @Override public String getHelpTopic( ) {
		return null;
	}

	@Nullable @Override public JComponent createComponent( ) {
		gitflowOptionsForm = new GitflowOptionsForm( );
		return gitflowOptionsForm.getContentPane( );
	}

	@Override public boolean isModified( ) {
		return PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_FEATURE_FETCH_ORIGIN, false )
				!= gitflowOptionsForm.isFeatureFetchOrigin( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_FEATURE_KEEP_REMOTE, false )
						!= gitflowOptionsForm.isFeatureKeepRemote( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_FEATURE_FINISH_BY_PULL_RQ, true )
						!= gitflowOptionsForm.isFinishFeatureByPullRq( ) ||

				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_FEATURE_PUBLISH_ON_START, false )
						!= gitflowOptionsForm.isFeaturePublishOnStart( ) ||

				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_RELEASE_FETCH_ORIGIN, false )
						!= gitflowOptionsForm.isReleaseFetchOrigin( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_PUSH_ON_FINISH_RELEASE, false )
						!= gitflowOptionsForm.isPushOnFinishRelease( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_DONT_TAG_RELEASE, false )
						!= gitflowOptionsForm.isDontTagRelease( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_RELEASE_FINISH_BY_PULL_RQ, false )
						!= gitflowOptionsForm.isFinishReleaseByPullRq( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_RELEASE_PUBLISH_ON_START, true )
						!= gitflowOptionsForm.isReleasePublishOnStart( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_USE_CUSTOM_TAG_COMMIT_MESSAGE, false )
						!= gitflowOptionsForm.isUseCustomTagCommitMessage( ) ||
				PropertiesComponent.getInstance( project )
						.getValue( GITFLOW_CUSTOM_TAG_COMMIT_MESSAGE, DEFAULT_TAG_COMMIT_MESSAGE )
						.equals( gitflowOptionsForm.getCustomTagCommitMessage( ) ) == false ||

				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_HOTFIX_FETCH_ORIGIN, false )
						!= gitflowOptionsForm.isHotfixFetchOrigin( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_PUSH_ON_FINISH_HOTFIX, false )
						!= gitflowOptionsForm.isPushOnFinishHotfix( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_HOTFIX_FINISH_BY_PULL_RQ, false )
						!= gitflowOptionsForm.isFinishHotfixByPullRq( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_HOTFIX_PUBLISH_ON_START, false )
						!= gitflowOptionsForm.isHotfixPublishOnStart( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_DONT_TAG_HOTFIX, false )
						!= gitflowOptionsForm.isDontTagHotfix( ) ||
				PropertiesComponent.getInstance( project )
						.getBoolean( GITFLOW_USE_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, false ) != gitflowOptionsForm
						.isUseCustomHotfixComitMessage( ) ||
				PropertiesComponent.getInstance( project )
						.getValue( GITFLOW_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, DEFAULT_TAG_HOTFIX_COMMIT_MESSAGE )
						.equals( gitflowOptionsForm.getCustomHotfixCommitMessage( ) ) == false ||

				PropertiesComponent.getInstance( project ).getValue( GITFLOW_STASH_URL, DEFAULT_STASH_URL )
						.equals( gitflowOptionsForm.getStashUrl( ) ) == false ||

				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_BUGFIX_FETCH_ORIGIN, false )
						!= gitflowOptionsForm.isBugfixFetchOrigin( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_PUSH_ON_FINISH_BUGFIX, false )
						!= gitflowOptionsForm.isPushOnFinishBugfix( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_BUGFIX_PUBLISH_ON_START, false )
						!= gitflowOptionsForm.isBugfixPublishOnStart( ) ||
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_BUGFIX_FINISH_BY_PULL_RQ, false )
						!= gitflowOptionsForm.isFinishBugfixByPullRq( );
	}

	@Override public void apply( ) throws ConfigurationException {
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_FEATURE_FETCH_ORIGIN,
				Boolean.toString( gitflowOptionsForm.isFeatureFetchOrigin( ) ) );
		PropertiesComponent.getInstance( project )
				.setValue( GITFLOW_FEATURE_KEEP_REMOTE, Boolean.toString( gitflowOptionsForm.isFeatureKeepRemote( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_FEATURE_FINISH_BY_PULL_RQ,
				Boolean.toString( gitflowOptionsForm.isFinishFeatureByPullRq( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_FEATURE_PUBLISH_ON_START,
				Boolean.toString( gitflowOptionsForm.isFeaturePublishOnStart( ) ) );

		PropertiesComponent.getInstance( project ).setValue( GITFLOW_RELEASE_FETCH_ORIGIN,
				Boolean.toString( gitflowOptionsForm.isReleaseFetchOrigin( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_PUSH_ON_FINISH_RELEASE,
				Boolean.toString( gitflowOptionsForm.isPushOnFinishRelease( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_RELEASE_FINISH_BY_PULL_RQ,
				Boolean.toString( gitflowOptionsForm.isFinishReleaseByPullRq( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_RELEASE_PUBLISH_ON_START,
				Boolean.toString( gitflowOptionsForm.isReleasePublishOnStart( ) ) );
		PropertiesComponent.getInstance( project )
				.setValue( GITFLOW_DONT_TAG_RELEASE, Boolean.toString( gitflowOptionsForm.isDontTagRelease( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_USE_CUSTOM_TAG_COMMIT_MESSAGE,
				Boolean.toString( gitflowOptionsForm.isUseCustomTagCommitMessage( ) ) );
		PropertiesComponent.getInstance( project )
				.setValue( GITFLOW_CUSTOM_TAG_COMMIT_MESSAGE, gitflowOptionsForm.getCustomTagCommitMessage( ) );

		PropertiesComponent.getInstance( project )
				.setValue( GITFLOW_HOTFIX_FETCH_ORIGIN, Boolean.toString( gitflowOptionsForm.isHotfixFetchOrigin( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_PUSH_ON_FINISH_HOTFIX,
				Boolean.toString( gitflowOptionsForm.isPushOnFinishHotfix( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_HOTFIX_FINISH_BY_PULL_RQ,
				Boolean.toString( gitflowOptionsForm.isFinishHotfixByPullRq( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_HOTFIX_PUBLISH_ON_START,
				Boolean.toString( gitflowOptionsForm.isHotfixPublishOnStart( ) ) );
		PropertiesComponent.getInstance( project )
				.setValue( GITFLOW_DONT_TAG_HOTFIX, Boolean.toString( gitflowOptionsForm.isDontTagHotfix( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_USE_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE,
				Boolean.toString( gitflowOptionsForm.isUseCustomHotfixComitMessage( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE,
				gitflowOptionsForm.getCustomHotfixCommitMessage( ) );

		PropertiesComponent.getInstance( project )
				.setValue( GITFLOW_BUGFIX_FETCH_ORIGIN, Boolean.toString( gitflowOptionsForm.isBugfixFetchOrigin( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_PUSH_ON_FINISH_BUGFIX,
				Boolean.toString( gitflowOptionsForm.isPushOnFinishBugfix( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_BUGFIX_FINISH_BY_PULL_RQ,
				Boolean.toString( gitflowOptionsForm.isFinishBugfixByPullRq( ) ) );
		PropertiesComponent.getInstance( project ).setValue( GITFLOW_BUGFIX_PUBLISH_ON_START,
				Boolean.toString( gitflowOptionsForm.isBugfixPublishOnStart( ) ) );

		PropertiesComponent.getInstance( project ).setValue( GITFLOW_STASH_URL, gitflowOptionsForm.getStashUrl( ) );
	}

	@Override public void reset( ) {
		gitflowOptionsForm.setFeatureFetchOrigin(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_FEATURE_FETCH_ORIGIN, false ) );
		gitflowOptionsForm.setFeatureKeepRemote(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_FEATURE_KEEP_REMOTE, false ) );
		gitflowOptionsForm.setFinishFeatureByPullRq(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_FEATURE_FINISH_BY_PULL_RQ, true ) );
		gitflowOptionsForm.setFeaturePublishOnStart(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_FEATURE_PUBLISH_ON_START, false ) );

		gitflowOptionsForm.setReleaseFetchOrigin(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_RELEASE_FETCH_ORIGIN, false ) );
		gitflowOptionsForm.setPushOnFinishRelease(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_PUSH_ON_FINISH_RELEASE, false ) );
		gitflowOptionsForm.setFinishReleaseByPullRq(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_RELEASE_FINISH_BY_PULL_RQ, true ) );
		gitflowOptionsForm.setReleasePublishOnStart(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_RELEASE_PUBLISH_ON_START, true ) );
		gitflowOptionsForm.setDontTagRelease(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_DONT_TAG_RELEASE, false ) );
		gitflowOptionsForm.setUseCustomTagCommitMessage(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_USE_CUSTOM_TAG_COMMIT_MESSAGE, false ) );
		gitflowOptionsForm.setCustomTagCommitMessage( PropertiesComponent.getInstance( project )
				.getValue( GITFLOW_CUSTOM_TAG_COMMIT_MESSAGE, DEFAULT_TAG_COMMIT_MESSAGE ) );

		gitflowOptionsForm.setHotfixFetchOrigin(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_HOTFIX_FETCH_ORIGIN, false ) );
		gitflowOptionsForm.setPushOnFinishHotfix(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_PUSH_ON_FINISH_HOTFIX, false ) );
		gitflowOptionsForm.setFinishHotfixByPullRq(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_HOTFIX_FINISH_BY_PULL_RQ, true ) );
		gitflowOptionsForm.setHotfixPublishOnStart(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_HOTFIX_PUBLISH_ON_START, false ) );
		gitflowOptionsForm.setDontTagHotfix(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_DONT_TAG_HOTFIX, false ) );
		gitflowOptionsForm.setUseCustomHotfixCommitMessage( PropertiesComponent.getInstance( project )
				.getBoolean( GITFLOW_USE_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, false ) );
		gitflowOptionsForm.setCustomHotfixCommitMessage( PropertiesComponent.getInstance( project )
				.getValue( GITFLOW_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, DEFAULT_TAG_HOTFIX_COMMIT_MESSAGE ) );

		gitflowOptionsForm.setCustomHotfixCommitMessage( PropertiesComponent.getInstance( project )
				.getValue( GITFLOW_CUSTOM_HOTFIX_TAG_COMMIT_MESSAGE, DEFAULT_TAG_HOTFIX_COMMIT_MESSAGE ) );

		gitflowOptionsForm.setBugfixFetchOrigin(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_BUGFIX_FETCH_ORIGIN, false ) );
		gitflowOptionsForm.setPushOnFinishBugfix(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_PUSH_ON_FINISH_BUGFIX, false ) );
		gitflowOptionsForm.setFinishBugfixByPullRq(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_BUGFIX_FINISH_BY_PULL_RQ, true ) );
		gitflowOptionsForm.setBugfixPublishOnStart(
				PropertiesComponent.getInstance( project ).getBoolean( GITFLOW_BUGFIX_PUBLISH_ON_START, true ) );

		gitflowOptionsForm.setStashUrl(
				PropertiesComponent.getInstance( project ).getValue( GITFLOW_STASH_URL, DEFAULT_STASH_URL ) );

	}

	@Override public void disposeUIResources( ) {
		gitflowOptionsForm = null;
	}
}
