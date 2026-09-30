package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ExecutionLogEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.UserEntity
import com.example.data.model.QuestionType
import com.example.data.model.ThemeMode
import com.example.data.repository.AuthRepository
import com.example.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Login : ScreenDestination()
    object Dashboard : ScreenDestination()
    object DeveloperPanel : ScreenDestination()
    object MemberProfile : ScreenDestination()
    object About : ScreenDestination()
    object CbtBrowser : ScreenDestination()
}

class SafaViewModel(
    private val authRepository: AuthRepository,
    private val questionRepository: QuestionRepository
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

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Login)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _currentTheme = MutableStateFlow(ThemeMode.LIQUID_GLASS)
    val currentTheme: StateFlow<ThemeMode> = _currentTheme.asStateFlow()

    private val _isFloatingEnabled = MutableStateFlow(true)
    val isFloatingEnabled: StateFlow<Boolean> = _isFloatingEnabled.asStateFlow()

    private val _isNotificationEnabled = MutableStateFlow(true)
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    fun navigateTo(screen: ScreenDestination) {
        _currentScreen.value = screen
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            ScreenDestination.DeveloperPanel,
            ScreenDestination.MemberProfile,
            ScreenDestination.About,
            ScreenDestination.CbtBrowser -> {
                _currentScreen.value = if (currentUser.value != null) {
                    ScreenDestination.Dashboard
                } else {
                    ScreenDestination.Login
                }
            }
            ScreenDestination.Dashboard -> {
                // If on dashboard, back does nothing or exits
            }
            ScreenDestination.Login -> {
                // At root
            }
        }
    }

    suspend fun login(user: String, pass: String): Result<Unit> {
        val res = authRepository.login(user, pass)
        return if (res.isSuccess) {
            _currentScreen.value = ScreenDestination.Dashboard
            Result.success(Unit)
        } else {
            Result.failure(res.exceptionOrNull() ?: Exception("Login gagal"))
        }
    }

    fun logout() {
        authRepository.logout()
        _currentScreen.value = ScreenDestination.Login
    }

    fun cycleTheme() {
        _currentTheme.value = when (_currentTheme.value) {
            ThemeMode.LIQUID_GLASS -> ThemeMode.NEOBRUTALISM_DARK
            ThemeMode.NEOBRUTALISM_DARK -> ThemeMode.MINIMALIST_OBSIDIAN
            ThemeMode.MINIMALIST_OBSIDIAN -> ThemeMode.LIQUID_GLASS
        }
    }

    fun toggleFloating(enabled: Boolean) {
        _isFloatingEnabled.value = enabled
    }

    fun toggleNotification(enabled: Boolean) {
        _isNotificationEnabled.value = enabled
    }

    fun selectModel(model: String) {
        questionRepository.setSelectedModel(model)
    }

    suspend fun saveApiKey(key: String): Result<Unit> {
        return authRepository.updateApiKey(key)
    }

    suspend fun createMember(username: String, pass: String, notes: String): Result<Long> {
        return authRepository.createMember(username, pass, notes)
    }

    fun toggleMemberStatus(member: UserEntity) {
        viewModelScope.launch {
            authRepository.toggleMemberStatus(member)
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

    // AI Execution Calls
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
    private val questionRepository: QuestionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SafaViewModel(authRepository, questionRepository) as T
    }
}
