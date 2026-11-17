package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatMessageEntity
import com.example.data.ChatThreadEntity
import com.example.data.CommentEntity
import com.example.data.NexusDatabase
import com.example.data.NexusRepository
import com.example.data.NexusSeedData
import com.example.data.NotificationEntity
import com.example.data.PostEntity
import com.example.data.StoryEntity
import com.example.data.UserProfileEntity
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    EXPLORE,
    REELS,
    PROFILE
}

sealed interface SubRoute {
    data object None : SubRoute
    data object ChatList : SubRoute
    data class ChatThread(val threadId: String) : SubRoute
}

sealed interface ActiveSheet {
    data object None : ActiveSheet
    data object CreatePost : ActiveSheet
    data class Comments(val postId: String) : ActiveSheet
    data class PostOptions(val post: PostEntity) : ActiveSheet
    data object Notifications : ActiveSheet
    data object EditProfile : ActiveSheet
    data object ProfileSettings : ActiveSheet
    data class ExploreDetail(val post: PostEntity) : ActiveSheet
}

@OptIn(ExperimentalCoroutinesApi::class)
class NexusViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NexusRepository

    init {
        val dao = NexusDatabase.getInstance(application).nexusDao()
        repository = NexusRepository(dao)
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _subRoute = MutableStateFlow<SubRoute>(SubRoute.None)
    val subRoute: StateFlow<SubRoute> = _subRoute.asStateFlow()

    private val _activeSheet = MutableStateFlow<ActiveSheet>(ActiveSheet.None)
    val activeSheet: StateFlow<ActiveSheet> = _activeSheet.asStateFlow()

    private val _activeStoryIndex = MutableStateFlow<Int?>(null)
    val activeStoryIndex: StateFlow<Int?> = _activeStoryIndex.asStateFlow()

    private val _reactionPickerPostId = MutableStateFlow<String?>(null)
    val reactionPickerPostId: StateFlow<String?> = _reactionPickerPostId.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _isRefreshingFeed = MutableStateFlow(false)
    val isRefreshingFeed: StateFlow<Boolean> = _isRefreshingFeed.asStateFlow()

    private val _isRefreshingExplore = MutableStateFlow(false)
    val isRefreshingExplore: StateFlow<Boolean> = _isRefreshingExplore.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _exploreQuery = MutableStateFlow("")
    val exploreQuery: StateFlow<String> = _exploreQuery.asStateFlow()

    private val _exploreCategory = MutableStateFlow("Trending")
    val exploreCategory: StateFlow<String> = _exploreCategory.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var refreshCounter = 0
    private var infinitePageCounter = 0

    val posts: StateFlow<List<PostEntity>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stories: StateFlow<List<StoryEntity>> = repository.stories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatThreads: StateFlow<List<ChatThreadEntity>> = repository.chatThreads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfile
        .combine(flowOf(NexusSeedData.initialProfile())) { db, fallback -> db ?: fallback }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NexusSeedData.initialProfile())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = _subRoute
        .flatMapLatest { route ->
            if (route is SubRoute.ChatThread) {
                repository.observeMessages(route.threadId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun observeComments(postId: String): Flow<List<CommentEntity>> =
        repository.observeComments(postId)

    fun selectTab(tab: MainTab) {
        _subRoute.value = SubRoute.None
        _reactionPickerPostId.value = null
        _currentTab.value = tab
    }

    fun openChatList() {
        _reactionPickerPostId.value = null
        _subRoute.value = SubRoute.ChatList
    }

    fun openChatThread(threadId: String) {
        _reactionPickerPostId.value = null
        _subRoute.value = SubRoute.ChatThread(threadId)
        viewModelScope.launch {
            repository.markThreadRead(threadId)
        }
    }

    fun openSheet(sheet: ActiveSheet) {
        _reactionPickerPostId.value = null
        _activeSheet.value = sheet
        if (sheet is ActiveSheet.Notifications) {
            viewModelScope.launch {
                repository.markNotificationsRead()
            }
        }
    }

    fun closeSheet() {
        _activeSheet.value = ActiveSheet.None
    }

    fun openStory(index: Int) {
        _reactionPickerPostId.value = null
        _activeStoryIndex.value = index
        val story = stories.value.getOrNull(index) ?: return
        viewModelScope.launch {
            repository.markStoryViewed(story.id)
        }
    }

    fun markStoryViewedById(storyId: String) {
        viewModelScope.launch {
            repository.markStoryViewed(storyId)
        }
    }

    fun closeStoryViewer() {
        _activeStoryIndex.value = null
    }

    fun showReactionPicker(postId: String?) {
        _reactionPickerPostId.value = postId
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun updateExploreQuery(query: String) {
        _exploreQuery.value = query
    }

    fun selectExploreCategory(category: String) {
        _exploreCategory.value = category
    }

    fun consumeSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    /**
     * Handles Android hardware back navigation strictly:
     * 1. Reaction picker -> 2. Story viewer -> 3. Bottom sheets -> 4. Chat thread -> 5. Chat list -> 6. Home tab
     * Returns true if back was consumed, false if system should exit app.
     */
    fun handleHardwareBack(): Boolean {
        return when {
            _reactionPickerPostId.value != null -> {
                _reactionPickerPostId.value = null
                true
            }
            _activeStoryIndex.value != null -> {
                _activeStoryIndex.value = null
                true
            }
            _activeSheet.value != ActiveSheet.None -> {
                _activeSheet.value = ActiveSheet.None
                true
            }
            _subRoute.value is SubRoute.ChatThread -> {
                _subRoute.value = SubRoute.ChatList
                true
            }
            _subRoute.value is SubRoute.ChatList -> {
                _subRoute.value = SubRoute.None
                true
            }
            _currentTab.value != MainTab.HOME -> {
                _currentTab.value = MainTab.HOME
                true
            }
            else -> false
        }
    }

    fun refreshFeed() {
        if (_isRefreshingFeed.value) return
        viewModelScope.launch {
            _isRefreshingFeed.value = true
            delay(850L)
            refreshCounter += 1
            repository.insertRefreshDrop(refreshCounter)
            _isRefreshingFeed.value = false
            _snackbarMessage.value = "Feed updated"
        }
    }

    fun refreshExplore() {
        if (_isRefreshingExplore.value) return
        viewModelScope.launch {
            _isRefreshingExplore.value = true
            delay(700L)
            _isRefreshingExplore.value = false
        }
    }

    fun loadMorePosts() {
        if (_isLoadingMore.value || infinitePageCounter >= 5) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            delay(800L)
            infinitePageCounter += 1
            repository.loadMoreInfinitePosts(infinitePageCounter)
            _isLoadingMore.value = false
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            repository.toggleLike(postId)
        }
    }

    fun setReaction(postId: String, emoji: String) {
        _reactionPickerPostId.value = null
        viewModelScope.launch {
            repository.setReaction(postId, emoji)
        }
    }

    fun toggleBookmark(postId: String) {
        viewModelScope.launch {
            repository.toggleBookmark(postId)
        }
    }

    fun repost(postId: String) {
        viewModelScope.launch {
            repository.incrementRepost(postId)
            _snackbarMessage.value = "Shared to your Nexus circle"
        }
    }

    fun toggleFollowAuthor(postId: String) {
        viewModelScope.launch {
            repository.toggleFollowAuthor(postId)
        }
    }

    fun addComment(postId: String, text: String) {
        viewModelScope.launch {
            repository.addComment(postId, text)
        }
    }

    fun toggleCommentLike(comment: CommentEntity) {
        viewModelScope.launch {
            repository.toggleCommentLike(comment)
        }
    }

    fun createPost(
        caption: String,
        category: String,
        mediaUri: String?,
        gradientIndex: Int,
        locationTag: String?,
        isReel: Boolean
    ) {
        viewModelScope.launch {
            repository.createPost(caption, category, mediaUri, gradientIndex, locationTag, isReel)
            _activeSheet.value = ActiveSheet.None
            if (isReel) {
                _currentTab.value = MainTab.REELS
            } else {
                _currentTab.value = MainTab.HOME
            }
            _subRoute.value = SubRoute.None
            _snackbarMessage.value = if (isReel) "Reel published!" else "Post shared to Nexus"
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
            _activeSheet.value = ActiveSheet.None
            _snackbarMessage.value = "Post removed"
        }
    }

    fun sendChatMessage(
        threadId: String,
        text: String,
        mediaUri: String?,
        replyTo: ChatMessageEntity?,
        peerName: String
    ) {
        viewModelScope.launch {
            repository.sendMessage(threadId, text, mediaUri, replyTo, peerName)
        }
    }

    fun reactToChatMessage(message: ChatMessageEntity, emoji: String) {
        viewModelScope.launch {
            repository.reactToMessage(message, emoji)
        }
    }

    fun updateProfile(displayName: String, handle: String, bio: String, location: String) {
        viewModelScope.launch {
            repository.updateProfile(displayName, handle, bio, location)
            _activeSheet.value = ActiveSheet.None
            _snackbarMessage.value = "Profile updated"
        }
    }
}
