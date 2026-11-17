package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.PostEntity
import com.example.data.StoryEntity
import com.example.ui.components.NexusAvatar
import com.example.ui.components.NexusPostCard
import com.example.ui.components.PostSkeletonCard
import com.example.ui.theme.NexusHorizontalGradient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedScreen(
    posts: List<PostEntity>,
    stories: List<StoryEntity>,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    reactionPickerPostId: String?,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onOpenStory: (Int) -> Unit,
    onToggleLike: (String) -> Unit,
    onLongPressLike: (String) -> Unit,
    onSelectReaction: (String, String) -> Unit,
    onOpenComments: (String) -> Unit,
    onRepost: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenOptions: (PostEntity) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(stringResource(R.string.tab_for_you), stringResource(R.string.tab_following))
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    val forYouPosts = remember(posts) { posts }
    val followingPosts = remember(posts) {
        posts.filter { it.isFollowingAuthor || it.feedBucket == "FOLLOWING" }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Material 3 Swipeable Feed Tabs
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = { tabPositions ->
                if (pagerState.currentPage < tabPositions.size) {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .padding(horizontal = 32.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(NexusHorizontalGradient)
                    )
                }
            },
            divider = {
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("feed_tab_$index"),
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                )
            }
        }

        // Swipeable Feed Pager ("For You" <-> "Following")
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            val pagePosts = if (page == 0) forYouPosts else followingPosts
            FeedPageContent(
                posts = pagePosts,
                stories = if (page == 0) stories else emptyList(),
                isFollowingTab = page == 1,
                isRefreshing = isRefreshing,
                isLoadingMore = isLoadingMore && page == 0,
                reactionPickerPostId = reactionPickerPostId,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
                onOpenStory = onOpenStory,
                onToggleLike = onToggleLike,
                onLongPressLike = onLongPressLike,
                onSelectReaction = onSelectReaction,
                onOpenComments = onOpenComments,
                onRepost = onRepost,
                onToggleBookmark = onToggleBookmark,
                onToggleFollow = onToggleFollow,
                onOpenOptions = onOpenOptions,
                onExploreClick = onExploreClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedPageContent(
    posts: List<PostEntity>,
    stories: List<StoryEntity>,
    isFollowingTab: Boolean,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    reactionPickerPostId: String?,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onOpenStory: (Int) -> Unit,
    onToggleLike: (String) -> Unit,
    onLongPressLike: (String) -> Unit,
    onSelectReaction: (String, String) -> Unit,
    onOpenComments: (String) -> Unit,
    onRepost: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenOptions: (PostEntity) -> Unit,
    onExploreClick: () -> Unit
) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember(posts.size) {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 2 && lastVisible >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMore, isFollowingTab) {
        if (shouldLoadMore && !isFollowingTab) {
            onLoadMore()
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier
            .fillMaxSize()
            .testTag(if (isFollowingTab) "following_pull_refresh" else "foryou_pull_refresh")
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 6.dp, bottom = 24.dp)
        ) {
            if (stories.isNotEmpty()) {
                item(key = "stories_strip") {
                    StoriesStrip(
                        stories = stories,
                        onOpenStory = onOpenStory
                    )
                }
            }

            if (posts.isEmpty() && !isRefreshing) {
                item(key = "empty_state") {
                    if (isFollowingTab) {
                        EmptyFollowingState(onExploreClick = onExploreClick)
                    } else {
                        Column {
                            PostSkeletonCard()
                            PostSkeletonCard()
                        }
                    }
                }
            } else {
                items(
                    items = posts,
                    key = { it.id }
                ) { post ->
                    NexusPostCard(
                        post = post,
                        isReactionPickerVisible = reactionPickerPostId == post.id,
                        onToggleLike = { onToggleLike(post.id) },
                        onLongPressLike = { onLongPressLike(post.id) },
                        onSelectReaction = { emoji -> onSelectReaction(post.id, emoji) },
                        onOpenComments = { onOpenComments(post.id) },
                        onRepost = { onRepost(post.id) },
                        onToggleBookmark = { onToggleBookmark(post.id) },
                        onToggleFollow = { onToggleFollow(post.id) },
                        onOpenOptions = { onOpenOptions(post) }
                    )
                }

                if (isLoadingMore) {
                    item(key = "skeleton_more_1") { PostSkeletonCard() }
                    item(key = "skeleton_more_2") { PostSkeletonCard() }
                }
            }
        }
    }
}

@Composable
private fun StoriesStrip(
    stories: List<StoryEntity>,
    onOpenStory: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = stories,
            key = { _, story -> story.id }
        ) { index, story ->
            Column(
                modifier = Modifier
                    .width(72.dp)
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onOpenStory(index) }
                    .padding(vertical = 4.dp)
                    .testTag("story_item_${story.id}"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NexusAvatar(
                    name = story.authorName,
                    seed = story.avatarSeed,
                    size = 62.dp,
                    hasStoryRing = true,
                    storyViewed = story.isViewed
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = story.authorName.substringBefore(" "),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (story.isViewed) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyFollowingState(onExploreClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.GroupAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.empty_following_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.empty_following_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable(onClick = onExploreClick)
            ) {
                Text(
                    text = "Browse Explore",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
        }
    }
}
