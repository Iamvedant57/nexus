package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.CommentEntity
import com.example.data.NotificationEntity
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.ui.theme.NexusBrandGradient
import com.example.ui.theme.NexusCardGradients
import com.example.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostBottomSheet(
    onDismiss: () -> Unit,
    onPublish: (
        caption: String,
        category: String,
        mediaUri: String?,
        gradientIndex: Int,
        locationTag: String?,
        isReel: Boolean
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var caption by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Design") }
    var locationTag by remember { mutableStateOf("") }
    var selectedMediaUri by remember { mutableStateOf<String?>(null) }
    var gradientIndex by remember { mutableIntStateOf(0) }
    var isReel by remember { mutableStateOf(false) }

    val categories = listOf("Design", "Photography", "Tech", "Music")

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedMediaUri = uri.toString()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 18.dp, vertical = 6.dp)
                .testTag("create_post_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isReel) "Create Reel" else "New Nexus Drop",
                    style = MaterialTheme.typography.headlineSmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.PlayCircle,
                        contentDescription = null,
                        tint = if (isReel) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reel", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isReel,
                        onCheckedChange = { isReel = it },
                        modifier = Modifier.testTag("create_reel_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text(stringResource(R.string.hint_write_caption)) },
                minLines = 3,
                maxLines = 6,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_caption_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Photo Picker + Selected Preview
            if (selectedMediaUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f)
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    AsyncImage(
                        model = selectedMediaUri,
                        contentDescription = "Selected post photo",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { selectedMediaUri = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remove photo",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                // Mood Gradient Selector for text cards
                Text(
                    text = "Card Gradient (or attach photo)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NexusCardGradients.forEachIndexed { index, colors ->
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(colors))
                                .border(
                                    width = if (gradientIndex == index) 3.dp else 0.dp,
                                    color = if (gradientIndex == index) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { gradientIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            if (gradientIndex == index) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected gradient",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clickable {
                                photoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Photo",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Category chips
            Text(
                text = "Topic",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { item ->
                    FilterChip(
                        selected = category == item,
                        onClick = { category = item },
                        label = { Text(item) },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = locationTag,
                onValueChange = { locationTag = it },
                label = { Text("Location (optional)") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            val canPublish = caption.isNotBlank() || selectedMediaUri != null
            Button(
                onClick = {
                    onPublish(caption, category, selectedMediaUri, gradientIndex, locationTag, isReel)
                },
                enabled = canPublish,
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("publish_post_button")
            ) {
                Text(
                    text = if (isReel) "Publish Reel" else "Share to Nexus",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    post: PostEntity?,
    comments: List<CommentEntity>,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit,
    onToggleCommentLike: (CommentEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var commentDraft by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 320.dp, max = 560.dp)
                .navigationBarsPadding()
                .imePadding()
                .testTag("comments_sheet")
        ) {
            Text(
                text = "Comments (${comments.size})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (comments.isEmpty()) {
                    item {
                        Text(
                            text = "No comments yet. Start the conversation!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                } else {
                    items(comments, key = { it.id }) { comment ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            NexusAvatar(
                                name = comment.authorName,
                                seed = comment.authorAvatarSeed,
                                size = 36.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = comment.authorName,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Text(
                                        text = formatRelativeTime(comment.createdAt),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = comment.text,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onToggleCommentLike(comment) }
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (comment.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Like comment",
                                    tint = if (comment.isLiked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = comment.likesCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = commentDraft,
                    onValueChange = { commentDraft = it },
                    placeholder = { Text(stringResource(R.string.hint_write_comment)) },
                    singleLine = true,
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("comment_input")
                )
                val canSend = commentDraft.isNotBlank() && post != null
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
                            onAddComment(commentDraft)
                            commentDraft = ""
                        }
                        .testTag("comment_send_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Post comment",
                        tint = if (canSend) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostOptionsBottomSheet(
    post: PostEntity,
    currentUserHandle: String,
    onDismiss: () -> Unit,
    onToggleFollow: () -> Unit,
    onToggleBookmark: () -> Unit,
    onRepost: () -> Unit,
    onCopyLink: () -> Unit,
    onDelete: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("post_options_sheet")
        ) {
            SheetActionRow(
                icon = Icons.Filled.PersonAdd,
                label = if (post.isFollowingAuthor) "Unfollow ${post.authorName}" else "Follow ${post.authorName}",
                onClick = {
                    onToggleFollow()
                    onDismiss()
                }
            )
            SheetActionRow(
                icon = if (post.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                label = if (post.isBookmarked) "Remove from Saved" else "Save drop to Profile",
                onClick = {
                    onToggleBookmark()
                    onDismiss()
                }
            )
            SheetActionRow(
                icon = Icons.Filled.Repeat,
                label = "Repost to your circle",
                onClick = {
                    onRepost()
                    onDismiss()
                }
            )
            SheetActionRow(
                icon = Icons.Filled.Link,
                label = "Copy link",
                onClick = {
                    onCopyLink()
                    onDismiss()
                }
            )
            if (post.authorHandle == currentUserHandle) {
                SheetActionRow(
                    icon = Icons.Filled.DeleteOutline,
                    label = "Delete drop",
                    tint = MaterialTheme.colorScheme.error,
                    onClick = onDelete
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsBottomSheet(
    notifications: List<NotificationEntity>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 480.dp)
                .navigationBarsPadding()
                .testTag("notifications_sheet")
        ) {
            Text(
                text = "Activity",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NexusAvatar(
                            name = item.actorName,
                            seed = item.actorAvatarSeed,
                            size = 42.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${item.actorName} ${item.actionText}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = formatRelativeTime(item.createdAt),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBottomSheet(
    profile: UserProfileEntity,
    onDismiss: () -> Unit,
    onSave: (displayName: String, handle: String, bio: String, location: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember(profile) { mutableStateOf(profile.displayName) }
    var handle by remember(profile) { mutableStateOf(profile.handle) }
    var bio by remember(profile) { mutableStateOf(profile.bio) }
    var location by remember(profile) { mutableStateOf(profile.location) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Edit Profile", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = handle,
                onValueChange = { handle = it },
                label = { Text("Handle") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onSave(name, handle, bio, location) },
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Changes")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsBottomSheet(
    currentThemeMode: ThemeMode,
    onSelectThemeMode: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 8.dp)
                .testTag("settings_sheet")
        ) {
            Text(text = "Appearance & App", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Dark mode automatically follows your Android system theme by default.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            val options = listOf(
                ThemeMode.SYSTEM to "Follow System Theme (Default)",
                ThemeMode.LIGHT to "Light Mode",
                ThemeMode.DARK to "Dark Mode"
            )
            options.forEach { (mode, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onSelectThemeMode(mode) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentThemeMode == mode,
                        onClick = { onSelectThemeMode(mode) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = label, style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreDetailBottomSheet(
    post: PostEntity,
    onDismiss: () -> Unit,
    onToggleLike: () -> Unit,
    onOpenComments: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            NexusPostCard(
                post = post,
                isReactionPickerVisible = false,
                onToggleLike = onToggleLike,
                onLongPressLike = {},
                onSelectReaction = {},
                onOpenComments = {
                    onDismiss()
                    onOpenComments()
                },
                onRepost = {},
                onToggleBookmark = onToggleBookmark,
                onToggleFollow = {},
                onOpenOptions = {}
            )
        }
    }
}

@Composable
private fun SheetActionRow(
    icon: ImageVector,
    label: String,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = tint,
            fontWeight = FontWeight.Medium
        )
    }
}
