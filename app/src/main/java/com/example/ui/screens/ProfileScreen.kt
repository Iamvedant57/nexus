package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.NexusAvatar
import com.example.ui.components.NexusPostCard
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.NexusHorizontalGradient
import com.example.ui.theme.NexusVerifiedBlue

@Composable
fun ProfileScreen(
    profile: UserProfileEntity,
    allPosts: List<PostEntity>,
    reactionPickerPostId: String?,
    onEditProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleLike: (String) -> Unit,
    onLongPressLike: (String) -> Unit,
    onSelectReaction: (String, String) -> Unit,
    onOpenComments: (String) -> Unit,
    onRepost: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenOptions: (PostEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Posts", "Reels", "Saved")

    val myPosts = remember(allPosts, profile.handle) {
        allPosts.filter { it.authorHandle == profile.handle && !it.isReel }
    }
    val myReels = remember(allPosts, profile.handle) {
        allPosts.filter { it.authorHandle == profile.handle && it.isReel }
    }
    val savedPosts = remember(allPosts) {
        allPosts.filter { it.isBookmarked }
    }

    val displayedPosts = when (selectedTab) {
        0 -> myPosts
        1 -> myReels
        else -> savedPosts
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(key = "profile_header") {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Banner Cover Art
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(148.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_profile_banner),
                        contentDescription = "Profile cover banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.45f)
                                    )
                                )
                            )
                    )
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.38f))
                            .testTag("profile_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.cd_settings),
                            tint = Color.White
                        )
                    }
                }

                // Overlapping Avatar + Edit Profile action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NexusAvatar(
                        name = profile.displayName,
                        seed = 0,
                        size = 84.dp,
                        hasStoryRing = true,
                        avatarUri = profile.avatarUri,
                        modifier = Modifier
                            .offset(y = (-34).dp)
                            .border(3.dp, MaterialTheme.colorScheme.background, CircleShape)
                    )

                    OutlinedButton(
                        onClick = onEditProfile,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .padding(bottom = 8.dp)
                            .testTag("edit_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit profile")
                    }
                }

                // Name, handle, bio, location
                Column(
                    modifier = Modifier
                        .offset(y = (-20).dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = profile.displayName,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Verified",
                            tint = NexusVerifiedBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = profile.handle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (profile.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = profile.bio,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (profile.location.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = profile.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Strip
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStat(
                                value = (myPosts.size + myReels.size).toString(),
                                label = "Drops"
                            )
                            ProfileStat(
                                value = formatCompactCount(profile.followersCount),
                                label = "Followers"
                            )
                            ProfileStat(
                                value = formatCompactCount(profile.followingCount),
                                label = "Following"
                            )
                        }
                    }
                }
            }
        }

        item(key = "profile_tabs") {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .padding(horizontal = 24.dp)
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
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("profile_tab_$label"),
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (displayedPosts.isEmpty()) {
            item(key = "profile_empty") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BookmarkBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (selectedTab == 2) {
                                stringResource(R.string.empty_saved_title)
                            } else {
                                "Nothing here yet"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedTab == 2) {
                                stringResource(R.string.empty_saved_body)
                            } else {
                                "Tap the + Create button below to share your first drop."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(displayedPosts, key = { it.id }) { post ->
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
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
