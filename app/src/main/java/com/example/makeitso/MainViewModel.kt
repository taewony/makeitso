package com.example.makeitso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.makeitso.data.model.ErrorMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class MainViewModel : ViewModel() {
    fun launchCatching(
        showErrorSnackbar: (ErrorMessage) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ) =
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                println("MainViewModel: Exception caught in launchCatching: ${throwable.message}")
                throwable.printStackTrace()
                
                // Phase 1: Firebase crashlytics 호출 제거
                // Phase 3에서 Firebase 연동 시 다시 추가 예정
                
                val error = if (throwable.message.isNullOrBlank()) {
                    ErrorMessage.IdError(R.string.generic_error)
                } else {
                    ErrorMessage.StringError(throwable.message!!)
                }
                showErrorSnackbar(error)
            },
            block = block
        )
}
