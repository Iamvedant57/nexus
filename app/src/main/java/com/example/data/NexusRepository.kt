package com.example.data

import kotlinx.coroutines.flow.Flow

class NexusRepository(private val dao: NexusDao) {
    val allPosts: Flow<List<PostEntity>> = dao.observeAllPosts()
    val stories: Flow<List<StoryEntity>> = dao.observeStories()
    val chatThreads: Flow<List<ChatThreadEntity>> = dao.observeChatThreads()
    val userProfile: Flow<UserProfileEntity?> = dao.observeUserProfile()
    val notifications: Flow<List<NotificationEntity>> = dao.observeNotifications()

    fun observeComments(postId: String): Flow<List<CommentEntity>> = dao.observeComments(postId)

    fun observeMessages(threadId: String): Flow<List<ChatMessageEntity>> =
        dao.observeMessagesForThread(threadId)

    suspend fun ensureSeeded() {
        if (dao.getPostCount() == 0) {
            val now = System.currentTimeMillis()
            dao.upsertUserProfile(NexusSeedData.initialProfile())
            dao.insertStories(NexusSeedData.initialStories(now))
            dao.insertPosts(NexusSeedData.initialPosts(now))
            dao.insertComments(NexusSeedData.initialComments(now))
            dao.insertChatThreads(NexusSeedData.initialChatThreads(now))
            dao.insertChatMessages(NexusSeedData.initialChatMessages(now))
            dao.insertNotifications(NexusSeedData.initialNotifications(now))
        }
    }

    suspend fun toggleLike(postId: String) {
        val post = dao.getPostById(postId) ?: return
        val newLiked = !post.isLiked
        val delta = if (newLiked) 1 else -1
        val newReaction = if (newLiked) (post.myReactionEmoji ?: "❤️") else null
        dao.insertPost(
            post.copy(
                isLiked = newLiked,
                likesCount = (post.likesCount + delta).coerceAtLeast(0),
                myReactionEmoji = newReaction
            )
        )
    }

    suspend fun setReaction(postId: String, emoji: String) {
        val post = dao.getPostById(postId) ?: return
        val wasLiked = post.isLiked
        val toggledOff = post.myReactionEmoji == emoji && wasLiked
        if (toggledOff) {
            dao.insertPost(
                post.copy(
                    isLiked = false,
                    likesCount = (post.likesCount - 1).coerceAtLeast(0),
                    myReactionEmoji = null
                )
            )
        } else {
            dao.insertPost(
                post.copy(
                    isLiked = true,
                    likesCount = if (wasLiked) post.likesCount else post.likesCount + 1,
                    myReactionEmoji = emoji
                )
            )
        }
    }

    suspend fun toggleBookmark(postId: String) {
        val post = dao.getPostById(postId) ?: return
        dao.insertPost(post.copy(isBookmarked = !post.isBookmarked))
    }

    suspend fun incrementRepost(postId: String) {
        val post = dao.getPostById(postId) ?: return
        dao.insertPost(post.copy(repostsCount = post.repostsCount + 1))
    }

    suspend fun toggleFollowAuthor(postId: String) {
        val post = dao.getPostById(postId) ?: return
        dao.insertPost(post.copy(isFollowingAuthor = !post.isFollowingAuthor))
    }

    suspend fun addComment(postId: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val profile = dao.getUserProfile() ?: NexusSeedData.initialProfile()
        dao.insertComment(
            CommentEntity(
                postId = postId,
                authorName = profile.displayName,
                authorHandle = profile.handle,
                authorAvatarSeed = 0,
                text = trimmed,
                likesCount = 0,
                isLiked = false,
                createdAt = System.currentTimeMillis()
            )
        )
        val post = dao.getPostById(postId)
        if (post != null) {
            dao.insertPost(post.copy(commentsCount = post.commentsCount + 1))
        }
    }

    suspend fun toggleCommentLike(comment: CommentEntity) {
        val nextLiked = !comment.isLiked
        val nextLikes = (comment.likesCount + if (nextLiked) 1 else -1).coerceAtLeast(0)
        dao.updateCommentLike(comment.id, nextLiked, nextLikes)
    }

    suspend fun createPost(
        caption: String,
        category: String,
        mediaUri: String?,
        gradientIndex: Int,
        locationTag: String?,
        isReel: Boolean
    ) {
        val trimmed = caption.trim()
        if (trimmed.isEmpty() && mediaUri == null) return
        val profile = dao.getUserProfile() ?: NexusSeedData.initialProfile()
        val now = System.currentTimeMillis()
        dao.insertPost(
            PostEntity(
                id = "user_post_$now",
                authorName = profile.displayName,
                authorHandle = profile.handle,
                authorAvatarSeed = 0,
                authorVerified = true,
                caption = trimmed.ifEmpty { "Captured a fresh moment on Nexus ✨" },
                category = category,
                feedBucket = "BOTH",
                mediaDrawableKey = if (mediaUri == null && isReel) "abstract_prism" else null,
                mediaUri = mediaUri,
                gradientThemeIndex = gradientIndex,
                locationTag = locationTag?.trim()?.takeIf { it.isNotEmpty() },
                likesCount = 1,
                commentsCount = 0,
                repostsCount = 0,
                isLiked = true,
                isBookmarked = false,
                isFollowingAuthor = true,
                myReactionEmoji = "✨",
                createdAt = now,
                isReel = isReel,
                audioTrack = if (isReel) "Original Sound • ${profile.displayName}" else null
            )
        )
    }

    suspend fun deletePost(postId: String) {
        dao.deletePost(postId)
    }

    suspend fun insertRefreshDrop(refreshCount: Int) {
        val now = System.currentTimeMillis()
        val freshPost = PostEntity(
            id = "refresh_post_${refreshCount}_$now",
            authorName = if (refreshCount % 2 == 0) "Maya Lin" else "Kaito Mori",
            authorHandle = if (refreshCount % 2 == 0) "@mayalin.art" else "@kaitosound",
            authorAvatarSeed = refreshCount % 5,
            authorVerified = true,
            caption = "Fresh drop #$refreshCount just landed on Nexus! Pull-to-refresh brought in new community captures and twilight textures.",
            category = if (refreshCount % 2 == 0) "Photography" else "Music",
            feedBucket = "BOTH",
            mediaDrawableKey = if (refreshCount % 2 == 0) "profile_banner" else "studio_setup",
            mediaUri = null,
            gradientThemeIndex = refreshCount % 4,
            locationTag = "Live Feed",
            likesCount = 18 + refreshCount * 4,
            commentsCount = 0,
            repostsCount = 3,
            isLiked = false,
            isBookmarked = false,
            isFollowingAuthor = true,
            myReactionEmoji = null,
            createdAt = now,
            isReel = false,
            audioTrack = null
        )
        dao.insertPost(freshPost)
    }

    suspend fun loadMoreInfinitePosts(page: Int) {
        val now = System.currentTimeMillis()
        dao.insertPosts(NexusSeedData.generateInfiniteBatch(page, now))
    }

    suspend fun markStoryViewed(storyId: String) {
        dao.markStoryViewed(storyId)
    }

    suspend fun markThreadRead(threadId: String) {
        dao.markThreadRead(threadId)
    }

    suspend fun sendMessage(
        threadId: String,
        text: String,
        mediaUri: String?,
        replyTo: ChatMessageEntity?,
        peerName: String
    ) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() && mediaUri == null) return
        val now = System.currentTimeMillis()
        val msg = ChatMessageEntity(
            threadId = threadId,
            senderIsMe = true,
            text = trimmed.ifEmpty { "Sent a photo" },
            mediaUri = mediaUri,
            replyToMessageId = replyTo?.id,
            replyToSnippet = replyTo?.text?.take(60),
            replyToAuthor = if (replyTo == null) null else if (replyTo.senderIsMe) "You" else peerName,
            reactionEmoji = null,
            createdAt = now
        )
        dao.insertChatMessage(msg)

        val currentThreads = dao.observeChatThreads()
        // Update thread preview by inserting updated thread record
        val existingPreview = trimmed.ifEmpty { "📷 Photo" }
        dao.insertChatThread(
            ChatThreadEntity(
                id = threadId,
                peerName = peerName,
                peerHandle = "@${peerName.lowercase().replace(" ", "")}",
                peerAvatarSeed = when (threadId) {
                    "thread_maya" -> 0
                    "thread_kaito" -> 1
                    "thread_sora" -> 2
                    else -> 4
                },
                isOnline = true,
                unreadCount = 0,
                lastMessagePreview = existingPreview,
                lastMessageAt = now
            )
        )
    }

    suspend fun reactToMessage(message: ChatMessageEntity, emoji: String) {
        val nextEmoji = if (message.reactionEmoji == emoji) null else emoji
        dao.updateMessageReaction(message.id, nextEmoji)
    }

    suspend fun updateProfile(displayName: String, handle: String, bio: String, location: String) {
        val current = dao.getUserProfile() ?: NexusSeedData.initialProfile()
        dao.upsertUserProfile(
            current.copy(
                displayName = displayName.trim().ifEmpty { current.displayName },
                handle = handle.trim().ifEmpty { current.handle },
                bio = bio.trim(),
                location = location.trim()
            )
        )
    }

    suspend fun markNotificationsRead() {
        dao.markAllNotificationsRead()
    }
}
