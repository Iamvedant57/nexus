package com.example.data

import com.example.R

object NexusSeedData {
    fun resolveDrawable(key: String?): Int? = when (key) {
        "neon_city" -> R.drawable.img_feed_neon_city
        "studio_setup" -> R.drawable.img_feed_studio_setup
        "abstract_prism" -> R.drawable.img_feed_abstract_prism
        "profile_banner" -> R.drawable.img_profile_banner
        else -> null
    }

    fun initialProfile(): UserProfileEntity = UserProfileEntity(
        id = 1,
        displayName = "Alex Rivera",
        handle = "@alex.nexus",
        bio = "Visual designer & creative technologist. Building kinetic mobile interfaces & neon soundscapes.",
        location = "San Francisco, CA",
        followersCount = 2480,
        followingCount = 312,
        avatarUri = null
    )

    fun initialStories(now: Long): List<StoryEntity> = listOf(
        StoryEntity(
            id = "story_1",
            authorName = "Maya Lin",
            authorHandle = "@mayalin.art",
            avatarSeed = 0,
            mediaDrawableKey = "neon_city",
            caption = "Midnight rain reflections in Shibuya ✨",
            accentIndex = 0,
            isViewed = false,
            createdAt = now - 10 * 60_000L
        ),
        StoryEntity(
            id = "story_2",
            authorName = "Kaito Mori",
            authorHandle = "@kaitosound",
            avatarSeed = 1,
            mediaDrawableKey = "studio_setup",
            caption = "Golden hour analog synth session 🎹",
            accentIndex = 1,
            isViewed = false,
            createdAt = now - 25 * 60_000L
        ),
        StoryEntity(
            id = "story_3",
            authorName = "Sora Vance",
            authorHandle = "@sora.3d",
            avatarSeed = 2,
            mediaDrawableKey = "abstract_prism",
            caption = "Testing iridescent shader passes in Blender",
            accentIndex = 2,
            isViewed = false,
            createdAt = now - 45 * 60_000L
        ),
        StoryEntity(
            id = "story_4",
            authorName = "Nova Studio",
            authorHandle = "@novastudio",
            avatarSeed = 3,
            mediaDrawableKey = "profile_banner",
            caption = "New silk mesh palette dropping Friday",
            accentIndex = 3,
            isViewed = false,
            createdAt = now - 70 * 60_000L
        ),
        StoryEntity(
            id = "story_5",
            authorName = "Devon Chase",
            authorHandle = "@devonui",
            avatarSeed = 4,
            mediaDrawableKey = "studio_setup",
            caption = "Clean desk, clear mind, shipping v2",
            accentIndex = 0,
            isViewed = true,
            createdAt = now - 120 * 60_000L
        ),
        StoryEntity(
            id = "story_6",
            authorName = "Elena Frost",
            authorHandle = "@elenafrost",
            avatarSeed = 5,
            mediaDrawableKey = "neon_city",
            caption = "After-hours color grading walkthrough",
            accentIndex = 1,
            isViewed = true,
            createdAt = now - 180 * 60_000L
        )
    )

    fun initialPosts(now: Long): List<PostEntity> = listOf(
        PostEntity(
            id = "post_1",
            authorName = "Maya Lin",
            authorHandle = "@mayalin.art",
            authorAvatarSeed = 0,
            authorVerified = true,
            caption = "Caught the violet-pink neon bounce off wet asphalt right after the downpour. Zero filters—just 35mm f/1.4 and patience.",
            category = "Photography",
            feedBucket = "BOTH",
            mediaDrawableKey = "neon_city",
            mediaUri = null,
            gradientThemeIndex = 0,
            locationTag = "Shibuya Crossing, Tokyo",
            likesCount = 1428,
            commentsCount = 3,
            repostsCount = 94,
            isLiked = false,
            isBookmarked = false,
            isFollowingAuthor = true,
            myReactionEmoji = null,
            createdAt = now - 14 * 60_000L,
            isReel = false,
            audioTrack = null
        ),
        PostEntity(
            id = "post_2",
            authorName = "Kaito Mori",
            authorHandle = "@kaitosound",
            authorAvatarSeed = 1,
            authorVerified = true,
            caption = "4 PM golden hour hits the analog desk just right. Sketching warm arpeggios for our upcoming Nexus Live set.",
            category = "Music",
            feedBucket = "BOTH",
            mediaDrawableKey = "studio_setup",
            mediaUri = null,
            gradientThemeIndex = 1,
            locationTag = "Echo Park Studio",
            likesCount = 892,
            commentsCount = 2,
            repostsCount = 41,
            isLiked = true,
            isBookmarked = true,
            isFollowingAuthor = true,
            myReactionEmoji = "🔥",
            createdAt = now - 42 * 60_000L,
            isReel = false,
            audioTrack = null
        ),
        PostEntity(
            id = "post_3",
            authorName = "Sora Vance",
            authorHandle = "@sora.3d",
            authorAvatarSeed = 2,
            authorVerified = true,
            caption = "Motion study #18: liquid glass ribbons refracting violet and tangerine rim light at 120fps. Swipe up for the shader breakdown.",
            category = "Design",
            feedBucket = "FOR_YOU",
            mediaDrawableKey = "abstract_prism",
            mediaUri = null,
            gradientThemeIndex = 2,
            locationTag = "Seoul Design Lab",
            likesCount = 3210,
            commentsCount = 2,
            repostsCount = 285,
            isLiked = false,
            isBookmarked = false,
            isFollowingAuthor = false,
            myReactionEmoji = null,
            createdAt = now - 95 * 60_000L,
            isReel = true,
            audioTrack = "Prism Echoes • Sora & Kaito"
        ),
        PostEntity(
            id = "post_4",
            authorName = "Devon Chase",
            authorHandle = "@devonui",
            authorAvatarSeed = 4,
            authorVerified = false,
            caption = "Hot take: Great mobile navigation disappears into thumb muscle memory. Bottom-anchored actions, spring-driven sheets, and crisp haptics beat cluttered sidebars every single time.",
            category = "Tech",
            feedBucket = "BOTH",
            mediaDrawableKey = null,
            mediaUri = null,
            gradientThemeIndex = 0,
            locationTag = "Brooklyn, NY",
            likesCount = 654,
            commentsCount = 2,
            repostsCount = 112,
            isLiked = false,
            isBookmarked = false,
            isFollowingAuthor = true,
            myReactionEmoji = null,
            createdAt = now - 160 * 60_000L,
            isReel = false,
            audioTrack = null
        ),
        PostEntity(
            id = "post_5",
            authorName = "Nova Studio",
            authorHandle = "@novastudio",
            authorAvatarSeed = 3,
            authorVerified = true,
            caption = "Gradient systems feel alive when they mirror natural light transitions—twilight violet warming into sunset coral.",
            category = "Design",
            feedBucket = "FOR_YOU",
            mediaDrawableKey = "profile_banner",
            mediaUri = null,
            gradientThemeIndex = 3,
            locationTag = "Reykjavik",
            likesCount = 1975,
            commentsCount = 1,
            repostsCount = 148,
            isLiked = false,
            isBookmarked = true,
            isFollowingAuthor = false,
            myReactionEmoji = null,
            createdAt = now - 230 * 60_000L,
            isReel = true,
            audioTrack = "Aurora Drift • Ambient Mix"
        ),
        PostEntity(
            id = "post_6",
            authorName = "Alex Rivera",
            authorHandle = "@alex.nexus",
            authorAvatarSeed = 0,
            authorVerified = true,
            caption = "Shipping the new Nexus mobile-first experience today! Swipeable tabs, long-press reactions, and zero-clutter bottom sheets.",
            category = "Tech",
            feedBucket = "BOTH",
            mediaDrawableKey = "neon_city",
            mediaUri = null,
            gradientThemeIndex = 1,
            locationTag = "San Francisco, CA",
            likesCount = 540,
            commentsCount = 1,
            repostsCount = 38,
            isLiked = false,
            isBookmarked = false,
            isFollowingAuthor = true,
            myReactionEmoji = null,
            createdAt = now - 310 * 60_000L,
            isReel = true,
            audioTrack = "Midnight Kinetic • Original Audio"
        ),
        PostEntity(
            id = "post_7",
            authorName = "Elena Frost",
            authorHandle = "@elenafrost",
            authorAvatarSeed = 5,
            authorVerified = true,
            caption = "Minimalist daylight corner for sketching new kinetic typography reels. Nothing beats physical paper before jumping into code.",
            category = "Photography",
            feedBucket = "FOR_YOU",
            mediaDrawableKey = "studio_setup",
            mediaUri = null,
            gradientThemeIndex = 2,
            locationTag = "Melbourne",
            likesCount = 1120,
            commentsCount = 1,
            repostsCount = 67,
            isLiked = false,
            isBookmarked = false,
            isFollowingAuthor = false,
            myReactionEmoji = null,
            createdAt = now - 420 * 60_000L,
            isReel = true,
            audioTrack = "Golden Hour Lo-Fi • Studio"
        )
    )

    fun initialComments(now: Long): List<CommentEntity> = listOf(
        CommentEntity(
            postId = "post_1",
            authorName = "Sora Vance",
            authorHandle = "@sora.3d",
            authorAvatarSeed = 2,
            text = "The specular glow on the wet pavement looks unreal!",
            likesCount = 24,
            isLiked = false,
            createdAt = now - 11 * 60_000L
        ),
        CommentEntity(
            postId = "post_1",
            authorName = "Kaito Mori",
            authorHandle = "@kaitosound",
            authorAvatarSeed = 1,
            text = "Need this as cover art for the next EP 🔥",
            likesCount = 15,
            isLiked = true,
            createdAt = now - 8 * 60_000L
        ),
        CommentEntity(
            postId = "post_1",
            authorName = "Devon Chase",
            authorHandle = "@devonui",
            authorAvatarSeed = 4,
            text = "35mm never misses in the rain.",
            likesCount = 7,
            isLiked = false,
            createdAt = now - 4 * 60_000L
        ),
        CommentEntity(
            postId = "post_2",
            authorName = "Maya Lin",
            authorHandle = "@mayalin.art",
            authorAvatarSeed = 0,
            text = "Can already hear the warm analog bass through this photo.",
            likesCount = 19,
            isLiked = false,
            createdAt = now - 30 * 60_000L
        ),
        CommentEntity(
            postId = "post_2",
            authorName = "Elena Frost",
            authorHandle = "@elenafrost",
            authorAvatarSeed = 5,
            text = "That lighting + synth combo is pure comfort.",
            likesCount = 11,
            isLiked = false,
            createdAt = now - 18 * 60_000L
        ),
        CommentEntity(
            postId = "post_3",
            authorName = "Nova Studio",
            authorHandle = "@novastudio",
            authorAvatarSeed = 3,
            text = "Index of refraction dialed to perfection ✨",
            likesCount = 42,
            isLiked = false,
            createdAt = now - 60 * 60_000L
        ),
        CommentEntity(
            postId = "post_3",
            authorName = "Alex Rivera",
            authorHandle = "@alex.nexus",
            authorAvatarSeed = 0,
            text = "120fps glass caustics on mobile look silky smooth.",
            likesCount = 16,
            isLiked = true,
            createdAt = now - 45 * 60_000L
        ),
        CommentEntity(
            postId = "post_4",
            authorName = "Maya Lin",
            authorHandle = "@mayalin.art",
            authorAvatarSeed = 0,
            text = "100% agree. Thumb zone ergonomics change everything.",
            likesCount = 31,
            isLiked = false,
            createdAt = now - 130 * 60_000L
        ),
        CommentEntity(
            postId = "post_4",
            authorName = "Sora Vance",
            authorHandle = "@sora.3d",
            authorAvatarSeed = 2,
            text = "Bottom sheets > centered modals every day.",
            likesCount = 18,
            isLiked = false,
            createdAt = now - 110 * 60_000L
        ),
        CommentEntity(
            postId = "post_5",
            authorName = "Devon Chase",
            authorHandle = "@devonui",
            authorAvatarSeed = 4,
            text = "That silk grain texture ties the whole gradient together.",
            likesCount = 22,
            isLiked = false,
            createdAt = now - 200 * 60_000L
        ),
        CommentEntity(
            postId = "post_6",
            authorName = "Kaito Mori",
            authorHandle = "@kaitosound",
            authorAvatarSeed = 1,
            text = "Swipe-to-reply in chats feels super snappy!",
            likesCount = 14,
            isLiked = false,
            createdAt = now - 280 * 60_000L
        ),
        CommentEntity(
            postId = "post_7",
            authorName = "Maya Lin",
            authorHandle = "@mayalin.art",
            authorAvatarSeed = 0,
            text = "Paper sketches always unlock the best compositions.",
            likesCount = 9,
            isLiked = false,
            createdAt = now - 390 * 60_000L
        )
    )

    fun initialChatThreads(now: Long): List<ChatThreadEntity> = listOf(
        ChatThreadEntity(
            id = "thread_maya",
            peerName = "Maya Lin",
            peerHandle = "@mayalin.art",
            peerAvatarSeed = 0,
            isOnline = true,
            unreadCount = 2,
            lastMessagePreview = "Sending over the raw Shibuya stills now!",
            lastMessageAt = now - 3 * 60_000L
        ),
        ChatThreadEntity(
            id = "thread_kaito",
            peerName = "Kaito Mori",
            peerHandle = "@kaitosound",
            peerAvatarSeed = 1,
            isOnline = true,
            unreadCount = 1,
            lastMessagePreview = "Did you hear the new synth stem at 0:42?",
            lastMessageAt = now - 28 * 60_000L
        ),
        ChatThreadEntity(
            id = "thread_sora",
            peerName = "Sora Vance",
            peerHandle = "@sora.3d",
            peerAvatarSeed = 2,
            isOnline = false,
            unreadCount = 0,
            lastMessagePreview = "Let's collab on the Nexus splash shader next week.",
            lastMessageAt = now - 150 * 60_000L
        ),
        ChatThreadEntity(
            id = "thread_devon",
            peerName = "Devon Chase",
            peerHandle = "@devonui",
            peerAvatarSeed = 4,
            isOnline = true,
            unreadCount = 0,
            lastMessagePreview = "Material 3 bottom sheets feel effortless on 390dp.",
            lastMessageAt = now - 380 * 60_000L
        )
    )

    fun initialChatMessages(now: Long): List<ChatMessageEntity> = listOf(
        ChatMessageEntity(
            threadId = "thread_maya",
            senderIsMe = false,
            text = "Just wrapped the twilight shoot near Scramble Crossing 🌧️",
            mediaUri = null,
            replyToMessageId = null,
            replyToSnippet = null,
            replyToAuthor = null,
            reactionEmoji = "🔥",
            createdAt = now - 12 * 60_000L
        ),
        ChatMessageEntity(
            threadId = "thread_maya",
            senderIsMe = true,
            text = "Saw your story—the violet bounce on the pavement is wild!",
            mediaUri = null,
            replyToMessageId = null,
            replyToSnippet = null,
            replyToAuthor = null,
            reactionEmoji = "❤️",
            createdAt = now - 8 * 60_000L
        ),
        ChatMessageEntity(
            threadId = "thread_maya",
            senderIsMe = false,
            text = "Swipe right on this message to test swipe-to-reply, or long-press for emoji reactions!",
            mediaUri = null,
            replyToMessageId = null,
            replyToSnippet = null,
            replyToAuthor = null,
            reactionEmoji = null,
            createdAt = now - 5 * 60_000L
        ),
        ChatMessageEntity(
            threadId = "thread_maya",
            senderIsMe = false,
            text = "Sending over the raw Shibuya stills now!",
            mediaUri = null,
            replyToMessageId = null,
            replyToSnippet = null,
            replyToAuthor = null,
            reactionEmoji = null,
            createdAt = now - 3 * 60_000L
        ),
        ChatMessageEntity(
            threadId = "thread_kaito",
            senderIsMe = true,
            text = "Yo Kaito, that golden hour studio shot looks cozy.",
            mediaUri = null,
            replyToMessageId = null,
            replyToSnippet = null,
            replyToAuthor = null,
            reactionEmoji = null,
            createdAt = now - 40 * 60_000L
        ),
        ChatMessageEntity(
            threadId = "thread_kaito",
            senderIsMe = false,
            text = "Did you hear the new synth stem at 0:42?",
            mediaUri = null,
            replyToMessageId = null,
            replyToSnippet = null,
            replyToAuthor = null,
            reactionEmoji = null,
            createdAt = now - 28 * 60_000L
        ),
        ChatMessageEntity(
            threadId = "thread_sora",
            senderIsMe = false,
            text = "Let's collab on the Nexus splash shader next week.",
            mediaUri = null,
            replyToMessageId = null,
            replyToSnippet = null,
            replyToAuthor = null,
            reactionEmoji = "👏",
            createdAt = now - 150 * 60_000L
        ),
        ChatMessageEntity(
            threadId = "thread_devon",
            senderIsMe = false,
            text = "Material 3 bottom sheets feel effortless on 390dp.",
            mediaUri = null,
            replyToMessageId = null,
            replyToSnippet = null,
            replyToAuthor = null,
            reactionEmoji = "🔥",
            createdAt = now - 380 * 60_000L
        )
    )

    fun initialNotifications(now: Long): List<NotificationEntity> = listOf(
        NotificationEntity(
            actorName = "Maya Lin",
            actorHandle = "@mayalin.art",
            actorAvatarSeed = 0,
            actionText = "reacted 🔥 to your Nexus drop",
            type = "LIKE",
            isUnread = true,
            createdAt = now - 6 * 60_000L
        ),
        NotificationEntity(
            actorName = "Kaito Mori",
            actorHandle = "@kaitosound",
            actorAvatarSeed = 1,
            actionText = "commented: \"Swipe-to-reply in chats feels super snappy!\"",
            type = "COMMENT",
            isUnread = true,
            createdAt = now - 22 * 60_000L
        ),
        NotificationEntity(
            actorName = "Sora Vance",
            actorHandle = "@sora.3d",
            actorAvatarSeed = 2,
            actionText = "reposted your mobile navigation thread",
            type = "REPOST",
            isUnread = false,
            createdAt = now - 90 * 60_000L
        ),
        NotificationEntity(
            actorName = "Nova Studio",
            actorHandle = "@novastudio",
            actorAvatarSeed = 3,
            actionText = "started following you",
            type = "FOLLOW",
            isUnread = false,
            createdAt = now - 210 * 60_000L
        )
    )

    fun generateInfiniteBatch(page: Int, now: Long): List<PostEntity> {
        val templates = listOf(
            PostEntity(
                id = "post_inf_${page}_1",
                authorName = "Ariana Solis",
                authorHandle = "@arianasolis",
                authorAvatarSeed = 3,
                authorVerified = true,
                caption = "Late-night color study #${page * 3 + 1}: pushing violet shadows against warm tangerine highlights for OLED screens.",
                category = "Design",
                feedBucket = "BOTH",
                mediaDrawableKey = "abstract_prism",
                mediaUri = null,
                gradientThemeIndex = 2,
                locationTag = "Berlin, DE",
                likesCount = 410 + page * 37,
                commentsCount = 12,
                repostsCount = 29,
                isLiked = false,
                isBookmarked = false,
                isFollowingAuthor = true,
                myReactionEmoji = null,
                createdAt = now - (500L + page * 60L) * 60_000L,
                isReel = false,
                audioTrack = null
            ),
            PostEntity(
                id = "post_inf_${page}_2",
                authorName = "Liam Kurosawa",
                authorHandle = "@liamkuro",
                authorAvatarSeed = 4,
                authorVerified = false,
                caption = "Night walk through backstreet arcades. Every puddle turns into a prism after 11 PM.",
                category = "Photography",
                feedBucket = "FOR_YOU",
                mediaDrawableKey = "neon_city",
                mediaUri = null,
                gradientThemeIndex = 0,
                locationTag = "Osaka, Japan",
                likesCount = 785 + page * 23,
                commentsCount = 18,
                repostsCount = 54,
                isLiked = false,
                isBookmarked = false,
                isFollowingAuthor = false,
                myReactionEmoji = null,
                createdAt = now - (540L + page * 60L) * 60_000L,
                isReel = false,
                audioTrack = null
            ),
            PostEntity(
                id = "post_inf_${page}_3",
                authorName = "Zara Patel",
                authorHandle = "@zarabuilds",
                authorAvatarSeed = 1,
                authorVerified = true,
                caption = "Micro-interaction tip: pair 48dp minimum touch targets with subtle spring physics so every tap feels grounded in the real world.",
                category = "Tech",
                feedBucket = "BOTH",
                mediaDrawableKey = null,
                mediaUri = null,
                gradientThemeIndex = 3,
                locationTag = "Toronto, ON",
                likesCount = 528 + page * 19,
                commentsCount = 9,
                repostsCount = 63,
                isLiked = false,
                isBookmarked = false,
                isFollowingAuthor = true,
                myReactionEmoji = null,
                createdAt = now - (580L + page * 60L) * 60_000L,
                isReel = false,
                audioTrack = null
            )
        )
        return templates
    }
}
