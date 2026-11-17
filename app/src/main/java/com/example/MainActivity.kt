package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ActiveSheet
import com.example.ui.MainTab
import com.example.ui.NexusViewModel
import com.example.ui.SubRoute
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.CreatePostBottomSheet
import com.example.ui.components.EditProfileBottomSheet
import com.example.ui.components.ExploreDetailBottomSheet
import com.example.ui.components.NexusAdaptiveNavigationRail
import com.example.ui.components.NexusBottomNavigationBar
import com.example.ui.components.NexusSplashOverlay
import com.example.ui.components.NexusTopAppBar
import com.example.ui.components.NotificationsBottomSheet
import com.example.ui.components.PostOptionsBottomSheet
import com.example.ui.components.ProfileSettingsBottomSheet
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.ChatThreadScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeFeedScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReelsScreen
import com.example.ui.screens.StoryViewerOverlay
import com.example.ui.theme.NexusTheme
import com.example.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NexusViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
            val subRoute by viewModel.subRoute.collectAsStateWithLifecycle()
            val activeStoryIndex by viewModel.activeStoryIndex.collectAsStateWithLifecycle()

            val systemDark = isSystemInDarkTheme()
            val isDarkContent = when {
                activeStoryIndex != null -> true
                currentTab == MainTab.REELS && subRoute == SubRoute.None -> true
                themeMode == ThemeMode.DARK -> true
                themeMode == ThemeMode.LIGHT -> false
                else -> systemDark
            }

            DisposableEffect(isDarkContent) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkContent) {
                        SystemBarStyle.dark(AndroidColor.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
                    },
                    navigationBarStyle = if (isDarkContent) {
                        SystemBarStyle.dark(AndroidColor.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
                    }
                )
                onDispose {}
            }

            NexusTheme(themeMode = themeMode) {
                NexusAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NexusAppRoot(viewModel: NexusViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val subRoute by viewModel.subRoute.collectAsStateWithLifecycle()
    val activeSheet by viewModel.activeSheet.collectAsStateWithLifecycle()
    val activeStoryIndex by viewModel.activeStoryIndex.collectAsStateWithLifecycle()
    val reactionPickerPostId by viewModel.reactionPickerPostId.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val chatThreads by viewModel.chatThreads.collectAsStateWithLifecycle()
    val chatMessages by viewModel.activeChatMessages.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    val isRefreshingFeed by viewModel.isRefreshingFeed.collectAsStateWithLifecycle()
    val isRefreshingExplore by viewModel.isRefreshingExplore.collectAsStateWithLifecycle()
    val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()
    val exploreQuery by viewModel.exploreQuery.collectAsStateWithLifecycle()
    val exploreCategory by viewModel.exploreCategory.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    var showSplash by rememberSaveable { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.consumeSnackbar()
        }
    }

    // Hardware back navigation: sheets/overlays -> sub-routes -> Home tab -> system exit
    val hasBackState = reactionPickerPostId != null ||
        activeStoryIndex != null ||
        activeSheet != ActiveSheet.None ||
        subRoute != SubRoute.None ||
        currentTab != MainTab.HOME

    BackHandler(enabled = hasBackState) {
        viewModel.handleHardwareBack()
    }

    val unreadNotifications = remember(notifications) {
        notifications.count { it.isUnread }
    }
    val unreadChats = remember(chatThreads) {
        chatThreads.sumOf { it.unreadCount }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = subRoute,
            transitionSpec = {
                if (targetState != SubRoute.None && initialState == SubRoute.None ||
                    targetState is SubRoute.ChatThread && initialState is SubRoute.ChatList
                ) {
                    (slideInHorizontally(tween(260)) { it / 3 } + fadeIn(tween(220)))
                        .togetherWith(fadeOut(tween(180)))
                } else {
                    fadeIn(tween(220))
                        .togetherWith(slideOutHorizontally(tween(220)) { it / 3 } + fadeOut(tween(180)))
                }
            },
            label = "subroute_transition"
        ) { route ->
            when (route) {
                SubRoute.None -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val isCompactViewport = maxWidth < 600.dp

                        Row(modifier = Modifier.fillMaxSize()) {
                            if (!isCompactViewport) {
                                NexusAdaptiveNavigationRail(
                                    currentTab = currentTab,
                                    onSelectTab = viewModel::selectTab,
                                    onCreateClick = { viewModel.openSheet(ActiveSheet.CreatePost) }
                                )
                            }

                            Scaffold(
                                modifier = Modifier.weight(1f),
                                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                                containerColor = MaterialTheme.colorScheme.background,
                                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                                topBar = {
                                    if (currentTab != MainTab.REELS) {
                                        NexusTopAppBar(
                                            currentTab = currentTab,
                                            unreadNotificationsCount = unreadNotifications,
                                            unreadChatsCount = unreadChats,
                                            onOpenNotifications = {
                                                viewModel.openSheet(ActiveSheet.Notifications)
                                            },
                                            onOpenChats = viewModel::openChatList
                                        )
                                    }
                                },
                                bottomBar = {
                                    if (isCompactViewport) {
                                        NexusBottomNavigationBar(
                                            currentTab = currentTab,
                                            isDarkOverlay = currentTab == MainTab.REELS,
                                            onSelectTab = viewModel::selectTab,
                                            onCreateClick = {
                                                viewModel.openSheet(ActiveSheet.CreatePost)
                                            }
                                        )
                                    }
                                }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    val contentModifier = if (currentTab == MainTab.REELS) {
                                        Modifier.fillMaxSize()
                                    } else {
                                        Modifier
                                            .fillMaxHeight()
                                            .widthIn(max = 600.dp)
                                    }

                                    AnimatedContent(
                                        targetState = currentTab,
                                        transitionSpec = {
                                            fadeIn(tween(190)).togetherWith(fadeOut(tween(150)))
                                        },
                                        modifier = contentModifier,
                                        label = "main_tab_transition"
                                    ) { tab ->
                                        when (tab) {
                                            MainTab.HOME -> HomeFeedScreen(
                                                posts = posts,
                                                stories = stories,
                                                isRefreshing = isRefreshingFeed,
                                                isLoadingMore = isLoadingMore,
                                                reactionPickerPostId = reactionPickerPostId,
                                                onRefresh = viewModel::refreshFeed,
                                                onLoadMore = viewModel::loadMorePosts,
                                                onOpenStory = viewModel::openStory,
                                                onToggleLike = viewModel::toggleLike,
                                                onLongPressLike = { postId ->
                                                    viewModel.showReactionPicker(
                                                        if (reactionPickerPostId == postId) null else postId
                                                    )
                                                },
                                                onSelectReaction = viewModel::setReaction,
                                                onOpenComments = { postId ->
                                                    viewModel.openSheet(ActiveSheet.Comments(postId))
                                                },
                                                onRepost = viewModel::repost,
                                                onToggleBookmark = viewModel::toggleBookmark,
                                                onToggleFollow = viewModel::toggleFollowAuthor,
                                                onOpenOptions = { post ->
                                                    viewModel.openSheet(ActiveSheet.PostOptions(post))
                                                },
                                                onExploreClick = { viewModel.selectTab(MainTab.EXPLORE) }
                                            )

                                            MainTab.EXPLORE -> ExploreScreen(
                                                posts = posts,
                                                query = exploreQuery,
                                                selectedCategory = exploreCategory,
                                                isRefreshing = isRefreshingExplore,
                                                onQueryChange = viewModel::updateExploreQuery,
                                                onSelectCategory = viewModel::selectExploreCategory,
                                                onRefresh = viewModel::refreshExplore,
                                                onPostClick = { post ->
                                                    viewModel.openSheet(ActiveSheet.ExploreDetail(post))
                                                },
                                                onToggleFollow = viewModel::toggleFollowAuthor
                                            )

                                            MainTab.REELS -> {
                                                val reels = remember(posts) {
                                                    posts.filter { it.isReel || !it.mediaDrawableKey.isNullOrBlank() || !it.mediaUri.isNullOrBlank() }
                                                }
                                                ReelsScreen(
                                                    reels = reels,
                                                    reactionPickerPostId = reactionPickerPostId,
                                                    onToggleLike = viewModel::toggleLike,
                                                    onLongPressLike = { postId ->
                                                        viewModel.showReactionPicker(
                                                            if (reactionPickerPostId == postId) null else postId
                                                        )
                                                    },
                                                    onSelectReaction = viewModel::setReaction,
                                                    onOpenComments = { postId ->
                                                        viewModel.openSheet(ActiveSheet.Comments(postId))
                                                    },
                                                    onRepost = viewModel::repost,
                                                    onToggleBookmark = viewModel::toggleBookmark,
                                                    onToggleFollow = viewModel::toggleFollowAuthor
                                                )
                                            }

                                            MainTab.PROFILE -> ProfileScreen(
                                                profile = userProfile,
                                                allPosts = posts,
                                                reactionPickerPostId = reactionPickerPostId,
                                                onEditProfile = {
                                                    viewModel.openSheet(ActiveSheet.EditProfile)
                                                },
                                                onOpenSettings = {
                                                    viewModel.openSheet(ActiveSheet.ProfileSettings)
                                                },
                                                onToggleLike = viewModel::toggleLike,
                                                onLongPressLike = { postId ->
                                                    viewModel.showReactionPicker(
                                                        if (reactionPickerPostId == postId) null else postId
                                                    )
                                                },
                                                onSelectReaction = viewModel::setReaction,
                                                onOpenComments = { postId ->
                                                    viewModel.openSheet(ActiveSheet.Comments(postId))
                                                },
                                                onRepost = viewModel::repost,
                                                onToggleBookmark = viewModel::toggleBookmark,
                                                onToggleFollow = viewModel::toggleFollowAuthor,
                                                onOpenOptions = { post ->
                                                    viewModel.openSheet(ActiveSheet.PostOptions(post))
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                SubRoute.ChatList -> ChatListScreen(
                    threads = chatThreads,
                    onBack = { viewModel.handleHardwareBack() },
                    onOpenThread = viewModel::openChatThread
                )

                is SubRoute.ChatThread -> {
                    val thread = chatThreads.find { it.id == route.threadId }
                    if (thread != null) {
                        ChatThreadScreen(
                            thread = thread,
                            messages = chatMessages,
                            onBack = { viewModel.handleHardwareBack() },
                            onSendMessage = { text, mediaUri, replyTo ->
                                viewModel.sendChatMessage(
                                    threadId = thread.id,
                                    text = text,
                                    mediaUri = mediaUri,
                                    replyTo = replyTo,
                                    peerName = thread.peerName
                                )
                            },
                            onReactMessage = viewModel::reactToChatMessage
                        )
                    }
                }
            }
        }

        // Full-screen Swipeable Story Viewer
        activeStoryIndex?.let { storyIdx ->
            StoryViewerOverlay(
                stories = stories,
                initialIndex = storyIdx,
                onMarkViewed = viewModel::markStoryViewedById,
                onSendReaction = { feedback ->
                    viewModel.showMessage(feedback)
                },
                onClose = viewModel::closeStoryViewer
            )
        }

        // Bottom Sheets (instead of centered modals)
        when (val sheet = activeSheet) {
            ActiveSheet.None -> Unit

            ActiveSheet.CreatePost -> CreatePostBottomSheet(
                onDismiss = viewModel::closeSheet,
                onPublish = viewModel::createPost
            )

            is ActiveSheet.Comments -> {
                val post = posts.find { it.id == sheet.postId }
                val comments by viewModel.observeComments(sheet.postId)
                    .collectAsStateWithLifecycle(initialValue = emptyList())
                CommentsBottomSheet(
                    post = post,
                    comments = comments,
                    onDismiss = viewModel::closeSheet,
                    onAddComment = { text -> viewModel.addComment(sheet.postId, text) },
                    onToggleCommentLike = viewModel::toggleCommentLike
                )
            }

            is ActiveSheet.PostOptions -> {
                val livePost = posts.find { it.id == sheet.post.id } ?: sheet.post
                PostOptionsBottomSheet(
                    post = livePost,
                    currentUserHandle = userProfile.handle,
                    onDismiss = viewModel::closeSheet,
                    onToggleFollow = { viewModel.toggleFollowAuthor(livePost.id) },
                    onToggleBookmark = { viewModel.toggleBookmark(livePost.id) },
                    onRepost = { viewModel.repost(livePost.id) },
                    onCopyLink = {
                        clipboardManager.setText(AnnotatedString("https://nexus.app/drop/${livePost.id}"))
                        viewModel.showMessage("Link copied to clipboard")
                    },
                    onDelete = { viewModel.deletePost(livePost.id) }
                )
            }

            ActiveSheet.Notifications -> NotificationsBottomSheet(
                notifications = notifications,
                onDismiss = viewModel::closeSheet
            )

            ActiveSheet.EditProfile -> EditProfileBottomSheet(
                profile = userProfile,
                onDismiss = viewModel::closeSheet,
                onSave = viewModel::updateProfile
            )

            ActiveSheet.ProfileSettings -> ProfileSettingsBottomSheet(
                currentThemeMode = themeMode,
                onSelectThemeMode = viewModel::setThemeMode,
                onDismiss = viewModel::closeSheet
            )

            is ActiveSheet.ExploreDetail -> {
                val livePost = posts.find { it.id == sheet.post.id } ?: sheet.post
                ExploreDetailBottomSheet(
                    post = livePost,
                    onDismiss = viewModel::closeSheet,
                    onToggleLike = { viewModel.toggleLike(livePost.id) },
                    onOpenComments = { viewModel.openSheet(ActiveSheet.Comments(livePost.id)) },
                    onToggleBookmark = { viewModel.toggleBookmark(livePost.id) }
                )
            }
        }

        // Animated Brand Splash Screen
        NexusSplashOverlay(
            visible = showSplash,
            onFinished = { showSplash = false }
        )
    }
}
