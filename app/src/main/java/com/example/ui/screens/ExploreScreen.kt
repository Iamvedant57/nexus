package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PostEntity
import com.example.ui.components.NexusAvatar
import com.example.ui.components.NexusMediaContent
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.NexusCardGradients
import com.example.ui.theme.SpaceGroteskFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    posts: List<PostEntity>,
    query: String,
    selectedCategory: String,
    isRefreshing: Boolean,
    onQueryChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onRefresh: () -> Unit,
    onPostClick: (PostEntity) -> Unit,
    onToggleFollow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("Trending", "Photography", "Design", "Tech", "Music")

    val filteredPosts = remember(posts, query, selectedCategory) {
        posts.filter { post ->
            val matchesCategory = selectedCategory == "Trending" ||
                post.category.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                post.caption.contains(query, ignoreCase = true) ||
                post.authorName.contains(query, ignoreCase = true) ||
                post.authorHandle.contains(query, ignoreCase = true) ||
                post.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val spotlightCreators = remember(posts) {
        posts.distinctBy { it.authorHandle }.take(5)
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_pull_refresh")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item(key = "search_bar") {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.hint_search_explore),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = { onQueryChange("") },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(999.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("explore_search_input")
                )
            }

            item(key = "category_chips") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    items(categories) { category ->
                        val selected = category == selectedCategory
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectCategory(category) },
                            label = {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("explore_chip_$category")
                        )
                    }
                }
            }

            if (query.isBlank() && selectedCategory == "Trending") {
                item(key = "creators_spotlight") {
                    Column(modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)) {
                        Text(
                            text = "Rising Creators",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(spotlightCreators, key = { it.id }) { creatorPost ->
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 2.dp,
                                    modifier = Modifier
                                        .width(146.dp)
                                        .clickable { onPostClick(creatorPost) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        NexusAvatar(
                                            name = creatorPost.authorName,
                                            seed = creatorPost.authorAvatarSeed,
                                            size = 48.dp,
                                            hasStoryRing = true
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = creatorPost.authorName,
                                            style = MaterialTheme.typography.labelLarge,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = creatorPost.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(999.dp),
                                            color = if (creatorPost.isFollowingAuthor) {
                                                MaterialTheme.colorScheme.surfaceContainerHigh
                                            } else {
                                                MaterialTheme.colorScheme.primary
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .minimumInteractiveComponentSize()
                                                .clickable { onToggleFollow(creatorPost.id) }
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = if (creatorPost.isFollowingAuthor) "Following" else "Follow",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (creatorPost.isFollowingAuthor) {
                                                        MaterialTheme.colorScheme.onSurface
                                                    } else {
                                                        MaterialTheme.colorScheme.onPrimary
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item(key = "grid_header") {
                Text(
                    text = if (query.isBlank()) "Discover $selectedCategory" else "Results for \"$query\"",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (filteredPosts.isEmpty()) {
                item(key = "empty_explore") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No matching drops found",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try another keyword or clear your category filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val rows = filteredPosts.chunked(2)
                items(rows, key = { row -> row.joinToString("_") { it.id } }) { rowPosts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowPosts.forEach { post ->
                            ExploreGridTile(
                                post = post,
                                onClick = { onPostClick(post) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowPosts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreGridTile(
    post: PostEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasVisualMedia = !post.mediaUri.isNullOrBlank() || !post.mediaDrawableKey.isNullOrBlank()
    Box(
        modifier = modifier
            .aspectRatio(0.85f)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .testTag("explore_tile_${post.id}")
    ) {
        if (hasVisualMedia) {
            NexusMediaContent(
                mediaUri = post.mediaUri,
                mediaDrawableKey = post.mediaDrawableKey,
                contentDescription = post.caption,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val grad = NexusCardGradients[post.gradientThemeIndex.mod(NexusCardGradients.size)]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(grad))
                    .padding(14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = post.caption,
                    color = Color.White,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f))
                    )
                )
        )

        if (post.isReel) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Reel",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(22.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = post.authorName,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = formatCompactCount(post.likesCount),
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
