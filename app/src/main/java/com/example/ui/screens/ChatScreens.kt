package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.ChatMessageEntity
import com.example.data.ChatThreadEntity
import com.example.ui.components.NexusAvatar
import com.example.ui.components.ReactionPickerPill
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.NexusBrandGradient
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    threads: List<ChatThreadEntity>,
    onBack: () -> Unit,
    onOpenThread: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("chat_list_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Messages",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item(key = "active_now_strip") {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Active now",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(threads.filter { it.isOnline }, key = { "online_${it.id}" }) { thread ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onOpenThread(thread.id) }
                                    .padding(4.dp)
                            ) {
                                NexusAvatar(
                                    name = thread.peerName,
                                    seed = thread.peerAvatarSeed,
                                    size = 54.dp,
                                    isOnline = true
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = thread.peerName.substringBefore(" "),
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                }
            }

            items(threads, key = { it.id }) { thread ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .clickable { onOpenThread(thread.id) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("chat_thread_${thread.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NexusAvatar(
                        name = thread.peerName,
                        seed = thread.peerAvatarSeed,
                        size = 52.dp,
                        isOnline = thread.isOnline
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = thread.peerName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (thread.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold
                            )
                            Text(
                                text = formatRelativeTime(thread.lastMessageAt),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (thread.unreadCount > 0) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = thread.lastMessagePreview,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (thread.unreadCount > 0) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontWeight = if (thread.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (thread.unreadCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(NexusBrandGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = thread.unreadCount.toString(),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatThreadScreen(
    thread: ChatThreadEntity,
    messages: List<ChatMessageEntity>,
    onBack: () -> Unit,
    onSendMessage: (text: String, mediaUri: String?, replyTo: ChatMessageEntity?) -> Unit,
    onReactMessage: (ChatMessageEntity, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageInput by remember { mutableStateOf("") }
    var pendingMediaUri by remember { mutableStateOf<String?>(null) }
    var replyingTo by remember { mutableStateOf<ChatMessageEntity?>(null) }
    var reactionTargetMessageId by remember { mutableStateOf<Long?>(null) }
    val listState = rememberLazyListState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pendingMediaUri = uri.toString()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("chat_thread_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NexusAvatar(
                            name = thread.peerName,
                            seed = thread.peerAvatarSeed,
                            size = 38.dp,
                            isOnline = thread.isOnline
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = thread.peerName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (thread.isOnline) "Active now" else thread.peerHandle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Subtle hint banner for swipe-to-reply
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tip: Swipe right on any message to reply · Long-press to react",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) { reactionTargetMessageId = null },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    SwipeableChatBubble(
                        message = message,
                        peerName = thread.peerName,
                        isReactionPickerOpen = reactionTargetMessageId == message.id,
                        onSwipeToReply = { replyingTo = message },
                        onLongPress = {
                            reactionTargetMessageId =
                                if (reactionTargetMessageId == message.id) null else message.id
                        },
                        onSelectReaction = { emoji ->
                            onReactMessage(message, emoji)
                            reactionTargetMessageId = null
                        }
                    )
                }
            }

            // Reply-to preview banner
            AnimatedVisibility(visible = replyingTo != null) {
                replyingTo?.let { target ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(NexusBrandGradient)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Replying to ${if (target.senderIsMe) "yourself" else thread.peerName}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = target.text,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = { replyingTo = null },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Cancel reply"
                                )
                            }
                        }
                    }
                }
            }

            // Pending photo attachment preview
            AnimatedVisibility(visible = pendingMediaUri != null) {
                pendingMediaUri?.let { uri ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected photo attachment",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Photo ready to send",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { pendingMediaUri = null },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Remove photo")
                        }
                    }
                }
            }

            // Composer Bar
            Surface(
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = stringResource(R.string.cd_add_photo),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.hint_message),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_message_input")
                    )

                    val canSend = messageInput.isNotBlank() || pendingMediaUri != null
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (canSend) NexusBrandGradient
                                else androidx.compose.ui.graphics.SolidColor(
                                    MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            )
                            .clickable(enabled = canSend) {
                                onSendMessage(messageInput, pendingMediaUri, replyingTo)
                                messageInput = ""
                                pendingMediaUri = null
                                replyingTo = null
                            }
                            .testTag("chat_send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(R.string.cd_send_message),
                            tint = if (canSend) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SwipeableChatBubble(
    message: ChatMessageEntity,
    peerName: String,
    isReactionPickerOpen: Boolean,
    onSwipeToReply: () -> Unit,
    onLongPress: () -> Unit,
    onSelectReaction: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val dragOffsetX = remember(message.id) { Animatable(0f) }
    var triggeredReplyHaptic by remember(message.id) { mutableStateOf(false) }
    val replyThresholdPx = 110f

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.senderIsMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        // Revealed reply icon behind bubble during swipe
        val revealProgress = (dragOffsetX.value / replyThresholdPx).coerceIn(0f, 1f)
        if (revealProgress > 0.05f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
                    .size(34.dp)
                    .alpha(revealProgress)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = "Swipe to reply",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
            horizontalAlignment = if (message.senderIsMe) Alignment.End else Alignment.Start,
            modifier = Modifier
                .offset { IntOffset(dragOffsetX.value.roundToInt(), 0) }
                .pointerInput(message.id) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (dragOffsetX.value >= replyThresholdPx) {
                                    onSwipeToReply()
                                }
                                triggeredReplyHaptic = false
                                dragOffsetX.animateTo(0f, animationSpec = spring())
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                triggeredReplyHaptic = false
                                dragOffsetX.animateTo(0f, animationSpec = spring())
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val next = (dragOffsetX.value + dragAmount).coerceIn(0f, 160f)
                                dragOffsetX.snapTo(next)
                                if (next >= replyThresholdPx && !triggeredReplyHaptic) {
                                    triggeredReplyHaptic = true
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 286.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (message.senderIsMe) 18.dp else 4.dp,
                            bottomEnd = if (message.senderIsMe) 4.dp else 18.dp
                        )
                    )
                    .background(
                        if (message.senderIsMe) NexusBrandGradient
                        else androidx.compose.ui.graphics.SolidColor(
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongPress()
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("chat_bubble_${message.id}")
            ) {
                Column {
                    if (!message.replyToSnippet.isNullOrBlank()) {
                        Surface(
                            color = if (message.senderIsMe) {
                                Color.White.copy(alpha = 0.18f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = message.replyToAuthor ?: peerName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (message.senderIsMe) Color.White else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = message.replyToSnippet,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (message.senderIsMe) {
                                        Color.White.copy(alpha = 0.85f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }

                    if (!message.mediaUri.isNullOrBlank()) {
                        AsyncImage(
                            model = message.mediaUri,
                            contentDescription = "Shared photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (message.senderIsMe) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                if (!message.reactionEmoji.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        tonalElevation = 2.dp
                    ) {
                        Text(
                            text = message.reactionEmoji,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = formatRelativeTime(message.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ReactionPickerPill(
                visible = isReactionPickerOpen,
                selectedEmoji = message.reactionEmoji,
                onSelectEmoji = onSelectReaction,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
