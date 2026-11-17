package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PostEntity
import com.example.ui.theme.NexusCardGradients
import com.example.ui.theme.NexusVerifiedBlue
import com.example.ui.theme.SpaceGroteskFontFamily
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NexusPostCard(
    post: PostEntity,
    isReactionPickerVisible: Boolean,
    onToggleLike: () -> Unit,
    onLongPressLike: () -> Unit,
    onSelectReaction: (String) -> Unit,
    onOpenComments: () -> Unit,
    onRepost: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleFollow: () -> Unit,
    onOpenOptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var showDoubleTapHeart by remember { mutableStateOf(false) }

    LaunchedEffect(showDoubleTapHeart) {
        if (showDoubleTapHeart) {
            delay(650L)
            showDoubleTapHeart = false
        }
    }

    val likeScale by animateFloatAsState(
        targetValue = if (post.isLiked) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "like_bounce"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 6.dp, top = 12.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NexusAvatar(
                        name = post.authorName,
                        seed = post.authorAvatarSeed,
                        size = 42.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (post.authorVerified) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = "Verified creator",
                                    tint = NexusVerifiedBlue,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = "· ${formatRelativeTime(post.createdAt)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = post.authorHandle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (!post.locationTag.isNullOrBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = post.locationTag,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    if (!post.isFollowingAuthor) {
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(999.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable(onClick = onToggleFollow)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("follow_button_${post.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Follow",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenOptions,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("post_options_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreHoriz,
                            contentDescription = stringResource(R.string.cd_more_options),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val hasVisualMedia = !post.mediaUri.isNullOrBlank() || !post.mediaDrawableKey.isNullOrBlank()

                if (hasVisualMedia) {
                    Text(
                        text = post.caption,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .aspectRatio(4f / 3f)
                            .pointerInput(post.id) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showDoubleTapHeart = true
                                        if (!post.isLiked) onToggleLike()
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        NexusMediaContent(
                            mediaUri = post.mediaUri,
                            mediaDrawableKey = post.mediaDrawableKey,
                            contentDescription = "Post image by ${post.authorName}",
                            modifier = Modifier.matchParentSize()
                        )
                        if (post.isReel) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.62f),
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "REEL",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                        androidx.compose.animation.AnimatedVisibility(
                            visible = showDoubleTapHeart,
                            enter = fadeIn() + scaleIn(
                                initialScale = 0.4f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                            ),
                            exit = fadeOut() + scaleOut(targetScale = 1.4f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(84.dp)
                            )
                        }
                    }
                } else {
                    // Expressive gradient quote/thought card
                    val colors = NexusCardGradients[post.gradientThemeIndex.mod(NexusCardGradients.size)]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Brush.linearGradient(colors))
                            .pointerInput(post.id) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showDoubleTapHeart = true
                                        if (!post.isLiked) onToggleLike()
                                    }
                                )
                            }
                            .padding(horizontal = 20.dp, vertical = 26.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = post.caption,
                            color = Color.White,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            lineHeight = 26.sp
                        )
                    }
                }

                // Category badge + Action bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Like / Long-press Reaction Trigger (48dp+ target)
                        Row(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(999.dp))
                                .combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true),
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onToggleLike()
                                    },
                                    onLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onLongPressLike()
                                    }
                                )
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("like_button_${post.id}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if (post.isLiked && !post.myReactionEmoji.isNullOrBlank() && post.myReactionEmoji != "❤️") {
                                Text(
                                    text = post.myReactionEmoji,
                                    fontSize = 19.sp,
                                    modifier = Modifier.scale(likeScale)
                                )
                            } else {
                                Icon(
                                    imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = stringResource(R.string.cd_like),
                                    tint = if (post.isLiked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .size(21.dp)
                                        .scale(likeScale)
                                )
                            }
                            Text(
                                text = formatCompactCount(post.likesCount),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (post.isLiked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Comments
                        Row(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(999.dp))
                                .clickable(onClick = onOpenComments)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("comment_button_${post.id}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = stringResource(R.string.cd_comment),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = formatCompactCount(post.commentsCount),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Repost / Share
                        Row(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(999.dp))
                                .clickable(onClick = onRepost)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("repost_button_${post.id}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Repeat,
                                contentDescription = stringResource(R.string.cd_repost),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = formatCompactCount(post.repostsCount),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Bookmark
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("bookmark_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = stringResource(R.string.cd_bookmark),
                            tint = if (post.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Floating Long-press Reaction Picker
            ReactionPickerPill(
                visible = isReactionPickerVisible,
                selectedEmoji = post.myReactionEmoji,
                onSelectEmoji = onSelectReaction,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 14.dp, bottom = 56.dp)
            )
        }
    }
}
