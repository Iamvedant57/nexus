package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NexusDao {
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun observeAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: String): PostEntity?

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun observeComments(postId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("UPDATE comments SET isLiked = :liked, likesCount = :likes WHERE id = :commentId")
    suspend fun updateCommentLike(commentId: Long, liked: Boolean, likes: Int)

    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun observeStories(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("UPDATE stories SET isViewed = 1 WHERE id = :storyId")
    suspend fun markStoryViewed(storyId: String)

    @Query("SELECT * FROM chat_threads ORDER BY lastMessageAt DESC")
    fun observeChatThreads(): Flow<List<ChatThreadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatThreads(threads: List<ChatThreadEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatThread(thread: ChatThreadEntity)

    @Query("UPDATE chat_threads SET unreadCount = 0 WHERE id = :threadId")
    suspend fun markThreadRead(threadId: String)

    @Query("SELECT * FROM chat_messages WHERE threadId = :threadId ORDER BY createdAt ASC, id ASC")
    fun observeMessagesForThread(threadId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<ChatMessageEntity>)

    @Query("UPDATE chat_messages SET reactionEmoji = :emoji WHERE id = :messageId")
    suspend fun updateMessageReaction(messageId: Long, emoji: String?)

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun observeUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun observeNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isUnread = 0")
    suspend fun markAllNotificationsRead()
}
