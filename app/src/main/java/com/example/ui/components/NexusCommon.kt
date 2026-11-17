package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.NexusSeedData
import com.example.ui.theme.AvatarPalette
import com.example.ui.theme.NexusBrandColors
import com.example.ui.theme.NexusBrandGradient
import com.example.ui.theme.NexusOnlineGreen
import com.example.ui.theme.SpaceGroteskFontFamily

val NEXUS_REACTION_EMOJIS = listOf("❤️", "🔥", "😂", "😮", "😢", "👏")

@Composable
fun NexusAvatar(
    name: String,
    seed: Int,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    hasStoryRing: Boolean = false,
    storyViewed: Boolean = false,
    isOnline: Boolean = false,
    avatarUri: String? = null
) {
    val palette = AvatarPalette[seed.mod(AvatarPalette.size)]
    val initials = name.trim().split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "N" }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        val ringModifier = when {
            hasStoryRing && !storyViewed -> Modifier
                .fillMaxSize()
                .border(width = 2.5.dp, brush = NexusBrandGradient, shape = CircleShape)
                .padding(3.5.dp)
            hasStoryRing && storyViewed -> Modifier
                .fillMaxSize()
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
                .padding(3.5.dp)
            else -> Modifier.fillMaxSize()
        }

        Box(
            modifier = ringModifier
                .clip(CircleShape)
                .background(Brush.linearGradient(palette)),
            contentAlignment = Alignment.Center
        ) {
            if (!avatarUri.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUri,
                    contentDescription = "$name avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = initials,
                    color = Color.White,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.36f).sp
                )
            }
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(NexusOnlineGreen)
            )
        }
    }
}

@Composable
fun NexusBrandWordmark(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(NexusBrandGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "N",
                color = Color.White,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
        Text(
            text = "Nexus",
            style = TextStyle(
                brush = Brush.horizontalGradient(NexusBrandColors),
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 23.sp,
                letterSpacing = (-0.5).sp
            )
        )
    }
}

@Composable
fun NexusMediaContent(
    mediaUri: String?,
    mediaDrawableKey: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val drawableRes = NexusSeedData.resolveDrawable(mediaDrawableKey)
    when {
        !mediaUri.isNullOrBlank() -> {
            AsyncImage(
                model = mediaUri,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        drawableRes != null -> {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    }
}

@Composable
fun ReactionPickerPill(
    visible: Boolean,
    selectedEmoji: String?,
    onSelectEmoji: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(140)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        ),
        exit = fadeOut(tween(120)) + scaleOut(targetScale = 0.85f),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 8.dp,
            shadowElevation = 10.dp,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(999.dp)
                )
                .testTag("reaction_picker_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NEXUS_REACTION_EMOJIS.forEach { emoji ->
                    val isSelected = emoji == selectedEmoji
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelectEmoji(emoji)
                            }
                            .testTag("reaction_emoji_$emoji"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 21.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PostSkeletonCard(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton_shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )

    val base = MaterialTheme.colorScheme.surfaceContainer
    val highlight = MaterialTheme.colorScheme.surfaceContainerHigh
    val brush = Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(shimmerOffset - 300f, 0f),
        end = Offset(shimmerOffset, 260f)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .width(84.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(brush)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(brush)
            )
        }
    }
}

fun formatCompactCount(count: Int): String = when {
    count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000f)
    count >= 1_000 -> String.format("%.1fK", count / 1_000f)
    else -> count.toString()
}

fun formatRelativeTime(timestamp: Long): String {
    val diffMinutes = ((System.currentTimeMillis() - timestamp) / 60_000L).coerceAtLeast(0L)
    return when {
        diffMinutes < 1L -> "Just now"
        diffMinutes < 60L -> "${diffMinutes}m"
        diffMinutes < 24L * 60L -> "${diffMinutes / 60L}h"
        else -> "${diffMinutes / (24L * 60L)}d"
    }
}
