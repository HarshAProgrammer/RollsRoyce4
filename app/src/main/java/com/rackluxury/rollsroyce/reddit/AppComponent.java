package com.rackluxury.rollsroyce.reddit;

import javax.inject.Singleton;

import dagger.Component;
import com.rackluxury.rollsroyce.reddit.activities.RedditAccountPostsActivity;
import com.rackluxury.rollsroyce.reddit.activities.AccountSavedThingActivity;
import com.rackluxury.rollsroyce.reddit.activities.AnonymousSubscriptionsActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditCommentActivity;
import com.rackluxury.rollsroyce.reddit.activities.CreateMultiRedditActivity;
import com.rackluxury.rollsroyce.reddit.activities.CustomThemeListingActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditCustomThemePreviewActivity;
import com.rackluxury.rollsroyce.reddit.activities.CustomizePostFilterActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditCustomizeThemeActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditEditCommentActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditEditMultiRedditActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditEditPostActivity;
import com.rackluxury.rollsroyce.reddit.activities.FetchRandomSubredditOrPostActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditFilteredPostsActivity;
import com.rackluxury.rollsroyce.reddit.activities.FullMarkdownActivity;
import com.rackluxury.rollsroyce.reddit.activities.GiveAwardActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditInboxActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditLinkResolverActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditLockScreenActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditLoginActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditMainActivity;
import com.rackluxury.rollsroyce.reddit.activities.MultiredditSelectionActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditPostFilterPreferenceActivity;
import com.rackluxury.rollsroyce.reddit.activities.PostFilterUsageListingActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditPostGalleryActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditPostImageActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditPostLinkActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditPostTextActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditPostVideoActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditRPANActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditRulesActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditSearchActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditSearchUsersResultActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditSettingsActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditSubredditMultiselectionActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditTrendingActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewPostDetailActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewPrivateMessagesActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewRedditGalleryActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewVideoActivity;
import com.rackluxury.rollsroyce.reddit.activities.ReportActivity;
import com.rackluxury.rollsroyce.reddit.activities.SearchResultActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditSearchSubredditsResultActivity;
import com.rackluxury.rollsroyce.reddit.activities.SelectUserFlairActivity;
import com.rackluxury.rollsroyce.reddit.activities.SelectedSubredditsAndUsersActivity;
import com.rackluxury.rollsroyce.reddit.activities.SendPrivateMessageActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditSubmitCrosspostActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditSubredditSelectionActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditSubscribedThingListingActivity;
import com.rackluxury.rollsroyce.reddit.activities.SuicidePreventionActivity;
import com.rackluxury.rollsroyce.reddit.activities.ViewImageOrGifActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewImgurMediaActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewMultiRedditDetailActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewSubredditDetailActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewUserDetailActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditWebViewActivity;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.FlairBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.fragments.CommentsListingFragment;
import com.rackluxury.rollsroyce.reddit.fragments.FollowedUsersListingFragment;
import com.rackluxury.rollsroyce.reddit.fragments.InboxFragment;
import com.rackluxury.rollsroyce.reddit.fragments.MultiRedditListingFragment;
import com.rackluxury.rollsroyce.reddit.fragments.PostFragment;
import com.rackluxury.rollsroyce.reddit.fragments.SidebarFragment;
import com.rackluxury.rollsroyce.reddit.fragments.SubredditListingFragment;
import com.rackluxury.rollsroyce.reddit.fragments.SubscribedSubredditsListingFragment;
import com.rackluxury.rollsroyce.reddit.fragments.UserListingFragment;
import com.rackluxury.rollsroyce.reddit.fragments.ViewImgurImageFragment;
import com.rackluxury.rollsroyce.reddit.fragments.ViewImgurVideoFragment;
import com.rackluxury.rollsroyce.reddit.fragments.ViewPostDetailFragment;
import com.rackluxury.rollsroyce.reddit.fragments.ViewRPANBroadcastFragment;
import com.rackluxury.rollsroyce.reddit.fragments.ViewRedditGalleryImageOrGifFragment;
import com.rackluxury.rollsroyce.reddit.fragments.ViewRedditGalleryVideoFragment;
import com.rackluxury.rollsroyce.reddit.services.RedditDownloadMediaService;
import com.rackluxury.rollsroyce.reddit.services.RedditDownloadRedditVideoService;
import com.rackluxury.rollsroyce.reddit.services.MaterialYouService;
import com.rackluxury.rollsroyce.reddit.services.SubmitPostService;
import com.rackluxury.rollsroyce.reddit.settings.AdvancedPreferenceFragment;
import com.rackluxury.rollsroyce.reddit.settings.CrashReportsFragment;
import com.rackluxury.rollsroyce.reddit.settings.CustomizeBottomAppBarFragment;
import com.rackluxury.rollsroyce.reddit.settings.CustomizeMainPageTabsFragment;
import com.rackluxury.rollsroyce.reddit.settings.DownloadLocationPreferenceFragment;
import com.rackluxury.rollsroyce.reddit.settings.GesturesAndButtonsPreferenceFragment;
import com.rackluxury.rollsroyce.reddit.settings.MainPreferenceFragment;
import com.rackluxury.rollsroyce.reddit.settings.MiscellaneousPreferenceFragment;
import com.rackluxury.rollsroyce.reddit.settings.NotificationPreferenceFragment;
import com.rackluxury.rollsroyce.reddit.settings.NsfwAndSpoilerFragment;
import com.rackluxury.rollsroyce.reddit.settings.PostHistoryFragment;
import com.rackluxury.rollsroyce.reddit.settings.SecurityPreferenceFragment;
import com.rackluxury.rollsroyce.reddit.settings.ThemePreferenceFragment;
import com.rackluxury.rollsroyce.reddit.settings.TranslationFragment;
import com.rackluxury.rollsroyce.reddit.settings.VideoPreferenceFragment;

@Singleton
@Component(modules = com.rackluxury.rollsroyce.reddit.AppModule.class)
public interface AppComponent {
    void inject(RedditMainActivity redditMainActivity);

    void inject(RedditLoginActivity redditLoginActivity);

    void inject(PostFragment postFragment);

    void inject(SubredditListingFragment subredditListingFragment);

    void inject(UserListingFragment userListingFragment);

    void inject(RedditViewPostDetailActivity redditViewPostDetailActivity);

    void inject(RedditViewSubredditDetailActivity redditViewSubredditDetailActivity);

    void inject(RedditViewUserDetailActivity redditViewUserDetailActivity);

    void inject(RedditCommentActivity redditCommentActivity);

    void inject(RedditSubscribedThingListingActivity redditSubscribedThingListingActivity);

    void inject(RedditPostTextActivity redditPostTextActivity);

    void inject(SubscribedSubredditsListingFragment subscribedSubredditsListingFragment);

    void inject(RedditPostLinkActivity redditPostLinkActivity);

    void inject(RedditPostImageActivity redditPostImageActivity);

    void inject(RedditPostVideoActivity redditPostVideoActivity);

    void inject(FlairBottomSheetFragment flairBottomSheetFragment);

    void inject(RedditRulesActivity redditRulesActivity);

    void inject(CommentsListingFragment commentsListingFragment);

    void inject(SubmitPostService submitPostService);

    void inject(RedditFilteredPostsActivity redditFilteredPostsActivity);

    void inject(SearchResultActivity searchResultActivity);

    void inject(RedditSearchSubredditsResultActivity redditSearchSubredditsResultActivity);

    void inject(FollowedUsersListingFragment followedUsersListingFragment);

    void inject(RedditSubredditSelectionActivity redditSubredditSelectionActivity);

    void inject(RedditEditPostActivity redditEditPostActivity);

    void inject(RedditEditCommentActivity redditEditCommentActivity);

    void inject(RedditAccountPostsActivity redditAccountPostsActivity);

    void inject(com.rackluxury.rollsroyce.reddit.PullNotificationWorker pullNotificationWorker);

    void inject(RedditInboxActivity redditInboxActivity);

    void inject(NotificationPreferenceFragment notificationPreferenceFragment);

    void inject(RedditLinkResolverActivity redditLinkResolverActivity);

    void inject(RedditSearchActivity redditSearchActivity);

    void inject(RedditSettingsActivity redditSettingsActivity);

    void inject(MainPreferenceFragment mainPreferenceFragment);

    void inject(AccountSavedThingActivity accountSavedThingActivity);

    void inject(ViewImageOrGifActivity viewGIFActivity);

    void inject(RedditViewMultiRedditDetailActivity redditViewMultiRedditDetailActivity);

    void inject(RedditViewVideoActivity redditViewVideoActivity);

    void inject(GesturesAndButtonsPreferenceFragment gesturesAndButtonsPreferenceFragment);

    void inject(CreateMultiRedditActivity createMultiRedditActivity);

    void inject(RedditSubredditMultiselectionActivity redditSubredditMultiselectionActivity);

    void inject(ThemePreferenceFragment themePreferenceFragment);

    void inject(RedditCustomizeThemeActivity redditCustomizeThemeActivity);

    void inject(CustomThemeListingActivity customThemeListingActivity);

    void inject(SidebarFragment sidebarFragment);

    void inject(AdvancedPreferenceFragment advancedPreferenceFragment);

    void inject(RedditCustomThemePreviewActivity redditCustomThemePreviewActivity);

    void inject(RedditEditMultiRedditActivity redditEditMultiRedditActivity);

    void inject(SelectedSubredditsAndUsersActivity selectedSubredditsAndUsersActivity);

    void inject(ReportActivity reportActivity);

    void inject(RedditViewImgurMediaActivity redditViewImgurMediaActivity);

    void inject(ViewImgurVideoFragment viewImgurVideoFragment);

    void inject(RedditDownloadRedditVideoService redditDownloadRedditVideoService);

    void inject(MultiRedditListingFragment multiRedditListingFragment);

    void inject(InboxFragment inboxFragment);

    void inject(RedditViewPrivateMessagesActivity redditViewPrivateMessagesActivity);

    void inject(SendPrivateMessageActivity sendPrivateMessageActivity);

    void inject(VideoPreferenceFragment videoPreferenceFragment);

    void inject(RedditViewRedditGalleryActivity redditViewRedditGalleryActivity);

    void inject(ViewRedditGalleryVideoFragment viewRedditGalleryVideoFragment);

    void inject(CustomizeMainPageTabsFragment customizeMainPageTabsFragment);

    void inject(RedditDownloadMediaService redditDownloadMediaService);

    void inject(DownloadLocationPreferenceFragment downloadLocationPreferenceFragment);

    void inject(RedditSubmitCrosspostActivity redditSubmitCrosspostActivity);

    void inject(FullMarkdownActivity fullMarkdownActivity);

    void inject(SelectUserFlairActivity selectUserFlairActivity);

    void inject(SecurityPreferenceFragment securityPreferenceFragment);

    void inject(NsfwAndSpoilerFragment nsfwAndSpoilerFragment);

    void inject(CustomizeBottomAppBarFragment customizeBottomAppBarFragment);

    void inject(GiveAwardActivity giveAwardActivity);

    void inject(TranslationFragment translationFragment);

    void inject(FetchRandomSubredditOrPostActivity fetchRandomSubredditOrPostActivity);

    void inject(MiscellaneousPreferenceFragment miscellaneousPreferenceFragment);

    void inject(CustomizePostFilterActivity customizePostFilterActivity);

    void inject(PostHistoryFragment postHistoryFragment);

    void inject(RedditPostFilterPreferenceActivity redditPostFilterPreferenceActivity);

    void inject(PostFilterUsageListingActivity postFilterUsageListingActivity);

    void inject(RedditSearchUsersResultActivity redditSearchUsersResultActivity);

    void inject(MultiredditSelectionActivity multiredditSelectionActivity);

    void inject(ViewImgurImageFragment viewImgurImageFragment);

    void inject(ViewRedditGalleryImageOrGifFragment viewRedditGalleryImageOrGifFragment);

    void inject(ViewPostDetailFragment viewPostDetailFragment);

    void inject(SuicidePreventionActivity suicidePreventionActivity);

    void inject(RedditWebViewActivity redditWebViewActivity);

    void inject(CrashReportsFragment crashReportsFragment);

    void inject(AnonymousSubscriptionsActivity anonymousSubscriptionsActivity);

    void inject(RedditLockScreenActivity redditLockScreenActivity);

    void inject(MaterialYouService materialYouService);

    void inject(RedditRPANActivity redditRpanActivity);

    void inject(ViewRPANBroadcastFragment viewRPANBroadcastFragment);

    void inject(RedditPostGalleryActivity redditPostGalleryActivity);

    void inject(RedditTrendingActivity redditTrendingActivity);
}
