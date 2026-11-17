package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PostEntity
import com.example.ui.components.NexusAvatar
import com.example.ui.components.NexusMediaContent
import com.example.ui.components.ReactionPickerPill
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.NexusBrandGradient
import com.example.ui.theme.NexusCardGradients
import com.example.ui.theme.SpaceGroteskFontFamily
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelsScreen(
    reels: List<PostEntity>,
    reactionPickerPostId: String?,
    onToggleLike: (String) -> Unit,
    onLongPressLike: (String) -> Unit,
    onSelectReaction: (String, String) -> Unit,
    onOpenComments: (String) -> Unit,
    onRepost: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (reels.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF090612)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No reels yet. Tap + to publish one!",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
        return
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { reels.size })

    VerticalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090612))
            .testTag("reels_vertical_pager")
    ) { page ->
        val reel = reels[page]
        val haptic = LocalHapticFeedback.current
        var isPlaying by remember(reel.id) { mutableStateOf(true) }
        var showHeartBurst by remember(reel.id) { mutableStateOf(false) }

        LaunchedEffect(showHeartBurst) {
            if (showHeartBurst) {
                delay(650L)
                showHeartBurst = false
            }
        }

        val infiniteTransition = rememberInfiniteTransition(label = "reel_motion")
        val discAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "disc_rotation"
        )
        val ambientPulse by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (isPlaying) 1.06f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3600),
                repeatMode = RepeatMode.Reverse
            ),
            label = "reel_ambient_zoom"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(reel.id) {
                    detectTapGestures(
                        onTap = { isPlaying = !isPlaying },
                        onDoubleTap = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showHeartBurst = true
                            if (!reel.isLiked) onToggleLike(reel.id)
                        }
                    )
                }
        ) {
            val hasMedia = !reel.mediaUri.isNullOrBlank() || !reel.mediaDrawableKey.isNullOrBlank()
            if (hasMedia) {
                NexusMediaContent(
                    mediaUri = reel.mediaUri,
                    mediaDrawableKey = reel.mediaDrawableKey,
                    contentDescription = "Reel by ${reel.authorName}",
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(ambientPulse),
                    contentScale = ContentScale.Crop
                )
            } else {
                val colors = NexusCardGradients[reel.gradientThemeIndex.mod(NexusCardGradients.size)]
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(colors)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reel.caption,
                        color = Color.White,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }

            // Bottom gradient scrim
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.86f))
                        )
                    )
            )

            // Top Reels Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reels",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color.White.copy(alpha = 0.16f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = if (isPlaying) "Playing" else "Paused",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isPlaying) "Playing" else "Paused",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // Center Play/Pause indicator when paused
            AnimatedVisibility(
                visible = !isPlaying,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Resume reel",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            // Double-tap Heart Burst
            AnimatedVisibility(
                visible = showHeartBurst,
                enter = fadeIn() + scaleIn(
                    initialScale = 0.4f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ),
                exit = fadeOut() + scaleOut(targetScale = 1.4f),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(96.dp)
                )
            }

            // Right-side thumb-friendly Action Rail
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp, bottom = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Like / Reaction button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(16.dp))
                        .combinedClickable(
                            onClick = { onToggleLike(reel.id) },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongPressLike(reel.id)
                            }
                        )
                        .padding(6.dp)
                        .testTag("reel_like_${reel.id}")
                ) {
                    if (reel.isLiked && !reel.myReactionEmoji.isNullOrBlank() && reel.myReactionEmoji != "❤️") {
                        Text(text = reel.myReactionEmoji, fontSize = 26.sp)
                    } else {
                        Icon(
                            imageVector = if (reel.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like reel",
                            tint = if (reel.isLiked) Color(0xFFEC4899) else Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCompactCount(reel.likesCount),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Comment button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenComments(reel.id) }
                        .padding(6.dp)
                        .testTag("reel_comment_${reel.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Reel comments",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCompactCount(reel.commentsCount),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Repost / Share button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onRepost(reel.id) }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Repeat,
                        contentDescription = "Share reel",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCompactCount(reel.repostsCount),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Bookmark button
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(CircleShape)
                        .clickable { onToggleBookmark(reel.id) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (reel.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save reel",
                        tint = if (reel.isBookmarked) Color(0xFFF97316) else Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Spinning Audio Disc
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .rotate(if (isPlaying) discAngle else 0f)
                        .clip(CircleShape)
                        .background(NexusBrandGradient)
                        .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Bottom-left Creator Info & Audio Ticker
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 84.dp, bottom = 22.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NexusAvatar(
                        name = reel.authorName,
                        seed = reel.authorAvatarSeed,
                        size = 38.dp,
                        hasStoryRing = true
                    )
                    Text(
                        text = reel.authorName,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (reel.authorVerified) {
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (reel.isFollowingAuthor) {
                            Color.White.copy(alpha = 0.18f)
                        } else {
                            Color(0xFF7C3AED)
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clickable { onToggleFollow(reel.id) }
                    ) {
                        Text(
                            text = if (reel.isFollowingAuthor) "Following" else "Follow",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = reel.caption,
                    color = Color.White.copy(alpha = 0.94f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = reel.audioTrack ?: "Original Sound • ${reel.authorName}",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Long-press reaction picker overlay
            ReactionPickerPill(
                visible = reactionPickerPostId == reel.id,
                selectedEmoji = reel.myReactionEmoji,
                onSelectEmoji = { emoji -> onSelectReaction(reel.id, emoji) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 68.dp, bottom = 220.dp)
            )
        }
    }
}
