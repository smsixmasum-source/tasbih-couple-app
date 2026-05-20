package com.tasbih.couple.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasbih.couple.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState(isLoading = true)
            val result = authRepo.login(email, password)
            _state.value = if (result.isSuccess) AuthState(isSuccess = true)
            else AuthState(error = result.exceptionOrNull()?.message ?: "লগইন ব্যর্থ")
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _state.value = AuthState(isLoading = true)
            val result = authRepo.register(email, password, name)
            _state.value = if (result.isSuccess) AuthState(isSuccess = true)
            else AuthState(error = result.exceptionOrNull()?.message ?: "রেজিস্ট্রেশন ব্যর্থ")
        }
    }
}
