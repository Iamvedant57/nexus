package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarSeed: Int,
    val authorVerified: Boolean,
    val caption: String,
    val category: String,
    val feedBucket: String, // "FOR_YOU" or "FOLLOWING" or "BOTH"
    val mediaDrawableKey: String?,
    val mediaUri: String?,
    val gradientThemeIndex: Int,
    val locationTag: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val repostsCount: Int,
    val isLiked: Boolean,
    val isBookmarked: Boolean,
    val isFollowingAuthor: Boolean,
    val myReactionEmoji: String?,
    val createdAt: Long,
    val isReel: Boolean,
    val audioTrack: String?
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarSeed: Int,
    val text: String,
    val likesCount: Int,
    val isLiked: Boolean,
    val createdAt: Long
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorHandle: String,
    val avatarSeed: Int,
    val mediaDrawableKey: String,
    val caption: String,
    val accentIndex: Int,
    val isViewed: Boolean,
    val createdAt: Long
)

@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey val id: String,
    val peerName: String,
    val peerHandle: String,
    val peerAvatarSeed: Int,
    val isOnline: Boolean,
    val unreadCount: Int,
    val lastMessagePreview: String,
    val lastMessageAt: Long
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val threadId: String,
    val senderIsMe: Boolean,
    val text: String,
    val mediaUri: String?,
    val replyToMessageId: Long?,
    val replyToSnippet: String?,
    val replyToAuthor: String?,
    val reactionEmoji: String?,
    val createdAt: Long
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val displayName: String,
    val handle: String,
    val bio: String,
    val location: String,
    val followersCount: Int,
    val followingCount: Int,
    val avatarUri: String?
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actorName: String,
    val actorHandle: String,
    val actorAvatarSeed: Int,
    val actionText: String,
    val type: String, // "LIKE", "COMMENT", "FOLLOW", "REPOST"
    val isUnread: Boolean,
    val createdAt: Long
)
