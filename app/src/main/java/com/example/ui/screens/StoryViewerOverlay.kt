package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StoryEntity
import com.example.ui.components.NexusAvatar
import com.example.ui.components.NexusMediaContent
import com.example.ui.components.formatRelativeTime
import kotlinx.coroutines.launch

@Composable
fun StoryViewerOverlay(
    stories: List<StoryEntity>,
    initialIndex: Int,
    onMarkViewed: (String) -> Unit,
    onSendReaction: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (stories.isEmpty()) return
    val safeInitial = initialIndex.coerceIn(0, stories.lastIndex)
    val pagerState = rememberPagerState(initialPage = safeInitial, pageCount = { stories.size })
    val scope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }
    var replyText by remember { mutableStateOf("") }

    LaunchedEffect(pagerState.currentPage) {
        val currentStory = stories[pagerState.currentPage]
        onMarkViewed(currentStory.id)
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5500, easing = LinearEasing)
        )
        if (pagerState.currentPage < stories.lastIndex) {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        } else {
            onClose()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090612))
            .testTag("story_viewer_overlay")
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val story = stories[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(page) {
                        detectTapGestures { offset ->
                            val isLeftTap = offset.x < size.width * 0.32f
                            scope.launch {
                                if (isLeftTap) {
                                    if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                } else {
                                    if (pagerState.currentPage < stories.lastIndex) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    } else {
                                        onClose()
                                    }
                                }
                            }
                        }
                    }
            ) {
                NexusMediaContent(
                    mediaUri = null,
                    mediaDrawableKey = story.mediaDrawableKey,
                    contentDescription = "Story by ${story.authorName}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top & bottom scrims for legibility
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.72f), Color.Transparent)
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.86f))
                            )
                        )
                )

                // Caption above reply bar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .navigationBarsPadding()
                        .padding(start = 18.dp, end = 18.dp, bottom = 92.dp)
                ) {
                    Text(
                        text = story.caption,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Top progress bars + creator header
        val activeStory = stories[pagerState.currentPage]
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stories.forEachIndexed { idx, _ ->
                    val barProgress = when {
                        idx < pagerState.currentPage -> 1f
                        idx == pagerState.currentPage -> progress.value
                        else -> 0f
                    }
                    LinearProgressIndicator(
                        progress = { barProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.32f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NexusAvatar(
                    name = activeStory.authorName,
                    seed = activeStory.avatarSeed,
                    size = 38.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activeStory.authorName,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${activeStory.authorHandle} · ${formatRelativeTime(activeStory.createdAt)}",
                        color = Color.White.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("story_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close story",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom reply + quick emoji reactions
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = replyText,
                onValueChange = { replyText = it },
                placeholder = {
                    Text(
                        text = "Reply to ${activeStory.authorName.substringBefore(" ")}...",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(999.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White.copy(alpha = 0.8f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.45f),
                    cursorColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            )

            if (replyText.isNotBlank()) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .size(48.dp)
                        .clickable {
                            onSendReaction("Replied to ${activeStory.authorName}: $replyText")
                            replyText = ""
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send story reply",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                listOf("❤️", "🔥", "👏").forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                            .clickable {
                                onSendReaction("Sent $emoji to ${activeStory.authorName}'s story")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}
