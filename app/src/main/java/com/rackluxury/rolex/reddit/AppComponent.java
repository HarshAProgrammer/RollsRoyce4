package com.rackluxury.rolex.reddit;

import javax.inject.Singleton;

import dagger.Component;
import com.rackluxury.rolex.reddit.activities.RedditAccountPostsActivity;
import com.rackluxury.rolex.reddit.activities.AccountSavedThingActivity;
import com.rackluxury.rolex.reddit.activities.AnonymousSubscriptionsActivity;
import com.rackluxury.rolex.reddit.activities.RedditCommentActivity;
import com.rackluxury.rolex.reddit.activities.CreateMultiRedditActivity;
import com.rackluxury.rolex.reddit.activities.CustomThemeListingActivity;
import com.rackluxury.rolex.reddit.activities.RedditCustomThemePreviewActivity;
import com.rackluxury.rolex.reddit.activities.CustomizePostFilterActivity;
import com.rackluxury.rolex.reddit.activities.RedditCustomizeThemeActivity;
import com.rackluxury.rolex.reddit.activities.RedditEditCommentActivity;
import com.rackluxury.rolex.reddit.activities.RedditEditMultiRedditActivity;
import com.rackluxury.rolex.reddit.activities.RedditEditPostActivity;
import com.rackluxury.rolex.reddit.activities.FetchRandomSubredditOrPostActivity;
import com.rackluxury.rolex.reddit.activities.RedditFilteredPostsActivity;
import com.rackluxury.rolex.reddit.activities.FullMarkdownActivity;
import com.rackluxury.rolex.reddit.activities.GiveAwardActivity;
import com.rackluxury.rolex.reddit.activities.RedditInboxActivity;
import com.rackluxury.rolex.reddit.activities.RedditLinkResolverActivity;
import com.rackluxury.rolex.reddit.activities.RedditLockScreenActivity;
import com.rackluxury.rolex.reddit.activities.RedditLoginActivity;
import com.rackluxury.rolex.reddit.activities.RedditMainActivity;
import com.rackluxury.rolex.reddit.activities.MultiredditSelectionActivity;
import com.rackluxury.rolex.reddit.activities.RedditPostFilterPreferenceActivity;
import com.rackluxury.rolex.reddit.activities.PostFilterUsageListingActivity;
import com.rackluxury.rolex.reddit.activities.RedditPostGalleryActivity;
import com.rackluxury.rolex.reddit.activities.RedditPostImageActivity;
import com.rackluxury.rolex.reddit.activities.RedditPostLinkActivity;
import com.rackluxury.rolex.reddit.activities.RedditPostTextActivity;
import com.rackluxury.rolex.reddit.activities.RedditPostVideoActivity;
import com.rackluxury.rolex.reddit.activities.RedditRPANActivity;
import com.rackluxury.rolex.reddit.activities.RedditRulesActivity;
import com.rackluxury.rolex.reddit.activities.RedditSearchActivity;
import com.rackluxury.rolex.reddit.activities.RedditSearchUsersResultActivity;
import com.rackluxury.rolex.reddit.activities.RedditSettingsActivity;
import com.rackluxury.rolex.reddit.activities.RedditSubredditMultiselectionActivity;
import com.rackluxury.rolex.reddit.activities.RedditTrendingActivity;
import com.rackluxury.rolex.reddit.activities.RedditViewPostDetailActivity;
import com.rackluxury.rolex.reddit.activities.RedditViewPrivateMessagesActivity;
import com.rackluxury.rolex.reddit.activities.RedditViewRedditGalleryActivity;
import com.rackluxury.rolex.reddit.activities.RedditViewVideoActivity;
import com.rackluxury.rolex.reddit.activities.ReportActivity;
import com.rackluxury.rolex.reddit.activities.SearchResultActivity;
import com.rackluxury.rolex.reddit.activities.RedditSearchSubredditsResultActivity;
import com.rackluxury.rolex.reddit.activities.SelectUserFlairActivity;
import com.rackluxury.rolex.reddit.activities.SelectedSubredditsAndUsersActivity;
import com.rackluxury.rolex.reddit.activities.SendPrivateMessageActivity;
import com.rackluxury.rolex.reddit.activities.RedditSubmitCrosspostActivity;
import com.rackluxury.rolex.reddit.activities.RedditSubredditSelectionActivity;
import com.rackluxury.rolex.reddit.activities.RedditSubscribedThingListingActivity;
import com.rackluxury.rolex.reddit.activities.SuicidePreventionActivity;
import com.rackluxury.rolex.reddit.activities.ViewImageOrGifActivity;
import com.rackluxury.rolex.reddit.activities.RedditViewImgurMediaActivity;
import com.rackluxury.rolex.reddit.activities.RedditViewMultiRedditDetailActivity;
import com.rackluxury.rolex.reddit.activities.RedditViewSubredditDetailActivity;
import com.rackluxury.rolex.reddit.activities.RedditViewUserDetailActivity;
import com.rackluxury.rolex.reddit.activities.RedditWebViewActivity;
import com.rackluxury.rolex.reddit.bottomsheetfragments.FlairBottomSheetFragment;
import com.rackluxury.rolex.reddit.fragments.CommentsListingFragment;
import com.rackluxury.rolex.reddit.fragments.FollowedUsersListingFragment;
import com.rackluxury.rolex.reddit.fragments.InboxFragment;
import com.rackluxury.rolex.reddit.fragments.MultiRedditListingFragment;
import com.rackluxury.rolex.reddit.fragments.PostFragment;
import com.rackluxury.rolex.reddit.fragments.SidebarFragment;
import com.rackluxury.rolex.reddit.fragments.SubredditListingFragment;
import com.rackluxury.rolex.reddit.fragments.SubscribedSubredditsListingFragment;
import com.rackluxury.rolex.reddit.fragments.UserListingFragment;
import com.rackluxury.rolex.reddit.fragments.ViewImgurImageFragment;
import com.rackluxury.rolex.reddit.fragments.ViewImgurVideoFragment;
import com.rackluxury.rolex.reddit.fragments.ViewPostDetailFragment;
import com.rackluxury.rolex.reddit.fragments.ViewRPANBroadcastFragment;
import com.rackluxury.rolex.reddit.fragments.ViewRedditGalleryImageOrGifFragment;
import com.rackluxury.rolex.reddit.fragments.ViewRedditGalleryVideoFragment;
import com.rackluxury.rolex.reddit.services.RedditDownloadMediaService;
import com.rackluxury.rolex.reddit.services.RedditDownloadRedditVideoService;
import com.rackluxury.rolex.reddit.services.MaterialYouService;
import com.rackluxury.rolex.reddit.services.SubmitPostService;
import com.rackluxury.rolex.reddit.settings.AdvancedPreferenceFragment;
import com.rackluxury.rolex.reddit.settings.CrashReportsFragment;
import com.rackluxury.rolex.reddit.settings.CustomizeBottomAppBarFragment;
import com.rackluxury.rolex.reddit.settings.CustomizeMainPageTabsFragment;
import com.rackluxury.rolex.reddit.settings.DownloadLocationPreferenceFragment;
import com.rackluxury.rolex.reddit.settings.GesturesAndButtonsPreferenceFragment;
import com.rackluxury.rolex.reddit.settings.MainPreferenceFragment;
import com.rackluxury.rolex.reddit.settings.MiscellaneousPreferenceFragment;
import com.rackluxury.rolex.reddit.settings.NotificationPreferenceFragment;
import com.rackluxury.rolex.reddit.settings.NsfwAndSpoilerFragment;
import com.rackluxury.rolex.reddit.settings.PostHistoryFragment;
import com.rackluxury.rolex.reddit.settings.SecurityPreferenceFragment;
import com.rackluxury.rolex.reddit.settings.ThemePreferenceFragment;
import com.rackluxury.rolex.reddit.settings.TranslationFragment;
import com.rackluxury.rolex.reddit.settings.VideoPreferenceFragment;

@Singleton
@Component(modules = com.rackluxury.rolex.reddit.AppModule.class)
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

    void inject(com.rackluxury.rolex.reddit.PullNotificationWorker pullNotificationWorker);

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
