package com.example.makeitso.ui.home

import com.example.makeitso.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : MainViewModel() {
    private val _isLoadingUser = MutableStateFlow(false)
    val isLoadingUser: StateFlow<Boolean>
        get() = _isLoadingUser.asStateFlow()

    private val _showAiNudgeDialog = MutableStateFlow(false)
    val showAiNudgeDialog: StateFlow<Boolean>
        get() = _showAiNudgeDialog.asStateFlow()

    fun onNudgeButtonClick() {
        _showAiNudgeDialog.value = true
    }

    fun onDialogDismiss() {
        _showAiNudgeDialog.value = false
    }
}

