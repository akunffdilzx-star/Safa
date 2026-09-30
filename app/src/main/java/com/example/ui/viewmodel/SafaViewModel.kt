package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ExecutionLogEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.UserEntity
import com.example.data.model.ChatConversation
import com.example.data.model.ChatMessage
import com.example.data.model.QuestionType
import com.example.data.model.ThemeMode
import com.example.data.model.UserRole
import com.example.data.repository.AuthRepository
import com.example.data.repository.ChatRepository
import com.example.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object ChatList : ScreenDestination()
    object ChatRoom : ScreenDestination()
    object Profile : ScreenDestination()
    object Settings : ScreenDestination()
    object Dashboard : ScreenDestination()
    object DeveloperPanel : ScreenDestination()
    object MemberProfile : ScreenDestination()
    object About : ScreenDestination()
    object CbtBrowser : ScreenDestination()
    object Login : ScreenDestination()
}

class SafaViewModel(
    private val authRepository: AuthRepository,
    private val questionRepository: QuestionRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    val currentUser: StateFlow<UserEntity?> = authRepository.currentUser
    val allMembers: StateFlow<List<UserEntity>> = authRepository.getAllMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuestions: StateFlow<List<QuestionEntity>> = questionRepository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLogs: StateFlow<List<ExecutionLogEntity>> = questionRepository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isExecuting: StateFlow<Boolean> = questionRepository.isExecuting
    val currentStatusText: StateFlow<String> = questionRepository.currentStatusText
    val selectedModel: StateFlow<String> = questionRepository.selectedModel

    // Chat flows
    val conversations: StateFlow<List<ChatConversation>> = chatRepository.conversations
    val currentMessages: StateFlow<List<ChatMessage>> = chatRepository.currentMessages
    val activeConversation: StateFlow<ChatConversation?> = chatRepository.activeConversation

    // Start directly on Dashboard for SAFA AI tasks/homework solver
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Dashboard)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    // Default theme is Liquid Glass (SAFA AI Cyberpunk)
    private val _currentTheme = MutableStateFlow(ThemeMode.LIQUID_GLASS)
    val currentTheme: StateFlow<ThemeMode> = _currentTheme.asStateFlow()

    private val _isFloatingEnabled = MutableStateFlow(true)
    val isFloatingEnabled: StateFlow<Boolean> = _isFloatingEnabled.asStateFlow()

    private val _isNotificationEnabled = MutableStateFlow(true)
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    // Privacy & Preferences from the video
    private val _isOnlineStatusEnabled = MutableStateFlow(true)
    val isOnlineStatusEnabled: StateFlow<Boolean> = _isOnlineStatusEnabled.asStateFlow()

    private val _isReadReceiptEnabled = MutableStateFlow(true)
    val isReadReceiptEnabled: StateFlow<Boolean> = _isReadReceiptEnabled.asStateFlow()

    private val _groupAddPermission = MutableStateFlow("Perlu Izin")
    val groupAddPermission: StateFlow<String> = _groupAddPermission.asStateFlow()

    fun navigateTo(screen: ScreenDestination) {
        _currentScreen.value = screen
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            ScreenDestination.DeveloperPanel,
            ScreenDestination.MemberProfile,
            ScreenDestination.About,
            ScreenDestination.CbtBrowser,
            ScreenDestination.Profile,
            ScreenDestination.Settings,
            ScreenDestination.ChatRoom,
            ScreenDestination.ChatList -> {
                _currentScreen.value = ScreenDestination.Dashboard
            }
            ScreenDestination.Login,
            ScreenDestination.Dashboard -> {
                // At root
            }
        }
    }

    // Chat Actions
    fun openChat(conv: ChatConversation) {
        chatRepository.openConversation(conv)
        _currentScreen.value = ScreenDestination.ChatRoom
    }

    fun sendChatMessage(text: String) {
        val apiKey = currentUser.value?.geminiApiKey
        chatRepository.sendMessage(text, apiKey)
    }

    fun startNewChat(name: String) {
        chatRepository.startNewChatWith(name)
        _currentScreen.value = ScreenDestination.ChatRoom
    }

    fun createGroup(name: String) {
        chatRepository.createNewGroup(name)
        _currentScreen.value = ScreenDestination.ChatRoom
    }

    // Settings actions
    fun setTheme(theme: ThemeMode) {
        _currentTheme.value = theme
    }

    fun toggleOnlineStatus(enabled: Boolean) {
        _isOnlineStatusEnabled.value = enabled
    }

    fun toggleReadReceipt(enabled: Boolean) {
        _isReadReceiptEnabled.value = enabled
    }

    fun setGroupPermission(permission: String) {
        _groupAddPermission.value = permission
    }

    fun toggleFloating(enabled: Boolean) {
        _isFloatingEnabled.value = enabled
    }

    fun toggleNotification(enabled: Boolean) {
        _isNotificationEnabled.value = enabled
    }

    fun cycleTheme() {
        val allThemes = ThemeMode.values()
        val nextIndex = (allThemes.indexOf(_currentTheme.value) + 1) % allThemes.size
        _currentTheme.value = allThemes[nextIndex]
    }

    fun selectModel(model: String) {
        questionRepository.setSelectedModel(model)
    }

    suspend fun saveApiKey(apiKey: String): Result<Unit> {
        return authRepository.updateApiKey(apiKey)
    }

    suspend fun login(username: String, password: String): Result<Unit> {
        val res = authRepository.login(username, password)
        if (res.isSuccess) {
            _currentScreen.value = ScreenDestination.Dashboard
        }
        return res.map { Unit }
    }

    fun logout() {
        authRepository.logout()
        _currentScreen.value = ScreenDestination.Login
    }

    suspend fun createMember(username: String, password: String, notes: String): Result<Long> {
        return authRepository.createMember(username, password, notes)
    }

    fun toggleMemberStatus(user: UserEntity) {
        viewModelScope.launch {
            authRepository.toggleMemberStatus(user)
        }
    }

    fun resetMemberPassword(userId: Long, newPass: String) {
        viewModelScope.launch {
            authRepository.updateMemberPassword(userId, newPass)
        }
    }

    fun deleteMember(userId: Long) {
        viewModelScope.launch {
            authRepository.deleteMember(userId)
        }
    }

    // Solver Actions
    fun executeCekAll() {
        viewModelScope.launch {
            questionRepository.executeCekAll()
        }
    }

    fun executeCekOneByOne() {
        viewModelScope.launch {
            questionRepository.executeCekOneByOne()
        }
    }

    fun executeAutoFill() {
        viewModelScope.launch {
            questionRepository.executeAutoFill()
        }
    }

    fun resetAnswers() {
        viewModelScope.launch {
            questionRepository.resetAllAnswers()
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            questionRepository.clearLogs()
        }
    }

    fun addQuestion(number: Int, text: String, type: QuestionType, options: List<String>) {
        viewModelScope.launch {
            questionRepository.addQuestion(number, text, type, options)
        }
    }

    fun deleteQuestion(id: Long) {
        viewModelScope.launch {
            questionRepository.deleteQuestion(id)
        }
    }
}

class SafaViewModelFactory(
    private val authRepository: AuthRepository,
    private val questionRepository: QuestionRepository,
    private val chatRepository: ChatRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SafaViewModel(authRepository, questionRepository, chatRepository) as T
    }
}
