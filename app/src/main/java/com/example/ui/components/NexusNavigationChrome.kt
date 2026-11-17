package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainTab
import com.example.ui.theme.NexusBrandGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexusTopAppBar(
    currentTab: MainTab,
    unreadNotificationsCount: Int,
    unreadChatsCount: Int,
    onOpenNotifications: () -> Unit,
    onOpenChats: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets.statusBars,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            when (currentTab) {
                MainTab.HOME -> NexusBrandWordmark()
                MainTab.EXPLORE -> Text(
                    text = stringResource(R.string.nav_explore),
                    style = MaterialTheme.typography.headlineSmall
                )
                MainTab.REELS -> Text(
                    text = stringResource(R.string.nav_reels),
                    style = MaterialTheme.typography.headlineSmall
                )
                MainTab.PROFILE -> Text(
                    text = stringResource(R.string.nav_profile),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        actions = {
            IconButton(
                onClick = onOpenNotifications,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("top_notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotificationsCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = Color.White
                            ) {
                                Text(unreadNotificationsCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (unreadNotificationsCount > 0) {
                            Icons.Filled.Notifications
                        } else {
                            Icons.Outlined.Notifications
                        },
                        contentDescription = stringResource(R.string.cd_notifications)
                    )
                }
            }

            IconButton(
                onClick = onOpenChats,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("top_chats_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadChatsCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ) {
                                Text(unreadChatsCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (unreadChatsCount > 0) {
                            Icons.AutoMirrored.Filled.Chat
                        } else {
                            Icons.AutoMirrored.Outlined.Chat
                        },
                        contentDescription = stringResource(R.string.cd_messages)
                    )
                }
            }
        }
    )
}

@Composable
fun NexusBottomNavigationBar(
    currentTab: MainTab,
    isDarkOverlay: Boolean = false,
    onSelectTab: (MainTab) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val containerColor = if (isDarkOverlay) {
        Color(0xFF0E0A17).copy(alpha = 0.94f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
    }
    val contentColor = if (isDarkOverlay) Color.White else MaterialTheme.colorScheme.onSurface
    val mutedColor = if (isDarkOverlay) {
        Color.White.copy(alpha = 0.65f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
        tonalElevation = if (isDarkOverlay) 0.dp else 4.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = if (isDarkOverlay) {
                    Color.White.copy(alpha = 0.12f)
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BottomNavItem(
                    label = stringResource(R.string.nav_home),
                    selected = currentTab == MainTab.HOME,
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    activeColor = if (isDarkOverlay) Color.White else MaterialTheme.colorScheme.primary,
                    inactiveColor = mutedColor,
                    tag = "nav_home",
                    onClick = { onSelectTab(MainTab.HOME) },
                    modifier = Modifier.weight(1f)
                )

                BottomNavItem(
                    label = stringResource(R.string.nav_explore),
                    selected = currentTab == MainTab.EXPLORE,
                    selectedIcon = Icons.Filled.Explore,
                    unselectedIcon = Icons.Outlined.Explore,
                    activeColor = if (isDarkOverlay) Color.White else MaterialTheme.colorScheme.primary,
                    inactiveColor = mutedColor,
                    tag = "nav_explore",
                    onClick = { onSelectTab(MainTab.EXPLORE) },
                    modifier = Modifier.weight(1f)
                )

                // Floating center Create button following Material 3 guidelines
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = (-8).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(54.dp)
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(18.dp),
                                ambientColor = MaterialTheme.colorScheme.secondary,
                                spotColor = MaterialTheme.colorScheme.primary
                            )
                            .clip(RoundedCornerShape(18.dp))
                            .background(NexusBrandGradient)
                            .border(
                                width = 1.5.dp,
                                color = contentColor.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, color = Color.White),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onCreateClick()
                                }
                            )
                            .testTag("nav_create"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.cd_create),
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                BottomNavItem(
                    label = stringResource(R.string.nav_reels),
                    selected = currentTab == MainTab.REELS,
                    selectedIcon = Icons.Filled.PlayCircle,
                    unselectedIcon = Icons.Outlined.PlayCircle,
                    activeColor = if (isDarkOverlay) Color.White else MaterialTheme.colorScheme.primary,
                    inactiveColor = mutedColor,
                    tag = "nav_reels",
                    onClick = { onSelectTab(MainTab.REELS) },
                    modifier = Modifier.weight(1f)
                )

                BottomNavItem(
                    label = stringResource(R.string.nav_profile),
                    selected = currentTab == MainTab.PROFILE,
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    activeColor = if (isDarkOverlay) Color.White else MaterialTheme.colorScheme.primary,
                    inactiveColor = mutedColor,
                    tag = "nav_profile",
                    onClick = { onSelectTab(MainTab.PROFILE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    activeColor: Color,
    inactiveColor: Color,
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        animationSpec = spring(),
        label = "nav_icon_scale"
    )

    Column(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .padding(vertical = 6.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (selected) activeColor.copy(alpha = 0.14f) else Color.Transparent
                )
                .padding(horizontal = 14.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = if (selected) activeColor else inactiveColor,
                modifier = Modifier
                    .size(23.dp)
                    .scale(scale)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (selected) activeColor else inactiveColor,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun NexusAdaptiveNavigationRail(
    currentTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(NexusBrandGradient)
                    .clickable(onClick = onCreateClick)
                    .testTag("rail_create"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_create),
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    ) {
        val items = listOf(
            Triple(MainTab.HOME, stringResource(R.string.nav_home), Pair(Icons.Filled.Home, Icons.Outlined.Home)),
            Triple(MainTab.EXPLORE, stringResource(R.string.nav_explore), Pair(Icons.Filled.Explore, Icons.Outlined.Explore)),
            Triple(MainTab.REELS, stringResource(R.string.nav_reels), Pair(Icons.Filled.PlayCircle, Icons.Outlined.PlayCircle)),
            Triple(MainTab.PROFILE, stringResource(R.string.nav_profile), Pair(Icons.Filled.Person, Icons.Outlined.Person))
        )
        items.forEach { (tab, title, icons) ->
            val selected = currentTab == tab
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) icons.first else icons.second,
                        contentDescription = title
                    )
                },
                label = { Text(title) }
            )
        }
    }
}
