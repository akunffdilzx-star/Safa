package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.NotificationHelper
import com.example.service.SafaFloatingOverlayService
import com.example.ui.components.FloatingHudOverlay
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.CbtBrowserScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.ChatRoomScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeveloperPanelScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MemberProfileScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.SafaTheme
import com.example.ui.viewmodel.SafaViewModel
import com.example.ui.viewmodel.SafaViewModelFactory
import com.example.ui.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {

    private val viewModel: SafaViewModel by viewModels {
        val app = application as SafaApplication
        SafaViewModelFactory(app.authRepository, app.questionRepository, app.chatRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
            val questions by viewModel.allQuestions.collectAsStateWithLifecycle()
            val logs by viewModel.recentLogs.collectAsStateWithLifecycle()
            val members by viewModel.allMembers.collectAsStateWithLifecycle()
            val statusText by viewModel.currentStatusText.collectAsStateWithLifecycle()
            val isExecuting by viewModel.isExecuting.collectAsStateWithLifecycle()
            val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()
            val isFloatingEnabled by viewModel.isFloatingEnabled.collectAsStateWithLifecycle()
            val isNotificationEnabled by viewModel.isNotificationEnabled.collectAsStateWithLifecycle()

            // Chat states
            val conversations by viewModel.conversations.collectAsStateWithLifecycle()
            val currentMessages by viewModel.currentMessages.collectAsStateWithLifecycle()
            val activeConversation by viewModel.activeConversation.collectAsStateWithLifecycle()

            // Privacy & Settings states
            val isOnlineStatusEnabled by viewModel.isOnlineStatusEnabled.collectAsStateWithLifecycle()
            val isReadReceiptEnabled by viewModel.isReadReceiptEnabled.collectAsStateWithLifecycle()
            val groupAddPermission by viewModel.groupAddPermission.collectAsStateWithLifecycle()

            // Notification permission launcher for Android 13+
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted && isNotificationEnabled) {
                    NotificationHelper.showQuickNotification(this@MainActivity, statusText)
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPerm = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!hasPerm) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            // Sync quick action notification with status and settings
            LaunchedEffect(isNotificationEnabled, statusText) {
                if (isNotificationEnabled) {
                    NotificationHelper.showQuickNotification(this@MainActivity, statusText)
                } else {
                    NotificationHelper.cancelQuickNotification(this@MainActivity)
                }
            }

            // Sync system floating overlay service across other apps
            LaunchedEffect(isFloatingEnabled) {
                if (isFloatingEnabled && Settings.canDrawOverlays(this@MainActivity)) {
                    SafaFloatingOverlayService.start(this@MainActivity)
                } else {
                    SafaFloatingOverlayService.stop(this@MainActivity)
                }
            }

            SafaTheme(themeMode = currentTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            ScreenDestination.ChatList -> {
                                ChatListScreen(
                                    conversations = conversations,
                                    onOpenChat = { conv -> viewModel.openChat(conv) },
                                    onOpenProfile = { viewModel.navigateTo(ScreenDestination.Profile) },
                                    onStartNewChat = { name -> viewModel.startNewChat(name) },
                                    onCreateGroup = { name -> viewModel.createGroup(name) }
                                )
                            }
                            ScreenDestination.ChatRoom -> {
                                ChatRoomScreen(
                                    conversation = activeConversation,
                                    messages = currentMessages,
                                    onSendMessage = { text -> viewModel.sendChatMessage(text) },
                                    onNavigateBack = { viewModel.navigateBack() }
                                )
                            }
                            ScreenDestination.Profile -> {
                                ProfileScreen(
                                    onOpenSettings = { viewModel.navigateTo(ScreenDestination.Settings) },
                                    onNavigateBack = { viewModel.navigateBack() }
                                )
                            }
                            ScreenDestination.Settings -> {
                                SettingsScreen(
                                    currentTheme = currentTheme,
                                    isOnlineStatusEnabled = isOnlineStatusEnabled,
                                    isReadReceiptEnabled = isReadReceiptEnabled,
                                    groupAddPermission = groupAddPermission,
                                    isNotificationEnabled = isNotificationEnabled,
                                    onSelectTheme = { viewModel.setTheme(it) },
                                    onToggleOnlineStatus = { viewModel.toggleOnlineStatus(it) },
                                    onToggleReadReceipt = { viewModel.toggleReadReceipt(it) },
                                    onSetGroupPermission = { viewModel.setGroupPermission(it) },
                                    onToggleNotification = { viewModel.toggleNotification(it) },
                                    onOpenSolverWorkspace = { viewModel.navigateTo(ScreenDestination.Dashboard) },
                                    onNavigateBack = { viewModel.navigateBack() }
                                )
                            }
                            ScreenDestination.Dashboard -> {
                                DashboardScreen(
                                    currentUser = currentUser,
                                    questions = questions,
                                    logs = logs,
                                    statusText = statusText,
                                    isExecuting = isExecuting,
                                    selectedModel = selectedModel,
                                    currentTheme = currentTheme,
                                    isFloatingEnabled = isFloatingEnabled,
                                    isNotificationEnabled = isNotificationEnabled,
                                    onToggleFloating = { viewModel.toggleFloating(it) },
                                    onToggleNotification = { viewModel.toggleNotification(it) },
                                    onCycleTheme = { viewModel.cycleTheme() },
                                    onCekAll = { viewModel.executeCekAll() },
                                    onCekOneByOne = { viewModel.executeCekOneByOne() },
                                    onAutoFill = { viewModel.executeAutoFill() },
                                    onResetAnswers = { viewModel.resetAnswers() },
                                    onClearLogs = { viewModel.clearLogs() },
                                    onAddQuestion = { num, text, type, opts ->
                                        viewModel.addQuestion(num, text, type, opts)
                                    },
                                    onDeleteQuestion = { viewModel.deleteQuestion(it) },
                                    onOpenDeveloperPanel = {
                                        viewModel.navigateTo(ScreenDestination.DeveloperPanel)
                                    },
                                    onOpenProfile = {
                                        viewModel.navigateTo(ScreenDestination.MemberProfile)
                                    },
                                    onOpenAbout = {
                                        viewModel.navigateTo(ScreenDestination.About)
                                    },
                                    onOpenCbtBrowser = {
                                        viewModel.navigateTo(ScreenDestination.CbtBrowser)
                                    }
                                )
                            }
                            ScreenDestination.DeveloperPanel -> {
                                DeveloperPanelScreen(
                                    members = members,
                                    onNavigateBack = { viewModel.navigateBack() },
                                    onCreateMember = { u, p, n -> viewModel.createMember(u, p, n) },
                                    onToggleMemberStatus = { viewModel.toggleMemberStatus(it) },
                                    onResetPassword = { id, newP -> viewModel.resetMemberPassword(id, newP) },
                                    onDeleteMember = { viewModel.deleteMember(it) }
                                )
                            }
                            ScreenDestination.MemberProfile -> {
                                MemberProfileScreen(
                                    currentUser = currentUser,
                                    selectedModel = selectedModel,
                                    onModelSelected = { viewModel.selectModel(it) },
                                    onSaveApiKey = { viewModel.saveApiKey(it) },
                                    onLogout = { viewModel.logout() },
                                    onNavigateBack = { viewModel.navigateBack() }
                                )
                            }
                            ScreenDestination.About -> {
                                AboutScreen(
                                    onNavigateBack = { viewModel.navigateBack() }
                                )
                            }
                            ScreenDestination.CbtBrowser -> {
                                val app = application as SafaApplication
                                CbtBrowserScreen(
                                    currentUser = currentUser,
                                    questionRepository = app.questionRepository,
                                    onNavigateBack = { viewModel.navigateBack() }
                                )
                            }
                            ScreenDestination.Login -> {
                                LoginScreen(
                                    onLoginSuccess = {
                                        viewModel.navigateTo(ScreenDestination.ChatList)
                                    },
                                    onOpenAbout = {
                                        viewModel.navigateTo(ScreenDestination.About)
                                    },
                                    onPerformLogin = { u, p -> viewModel.login(u, p) }
                                )
                            }
                        }

                        // Floating HUD Overlay (available in Dashboard)
                        if (currentScreen == ScreenDestination.Dashboard) {
                            FloatingHudOverlay(
                                isFloatingEnabled = isFloatingEnabled,
                                statusText = statusText,
                                isExecuting = isExecuting,
                                onCekAll = { viewModel.executeCekAll() },
                                onCekOneByOne = { viewModel.executeCekOneByOne() },
                                onAutoFill = { viewModel.executeAutoFill() }
                            )
                        }
                    }
                }
            }
        }
    }
}
