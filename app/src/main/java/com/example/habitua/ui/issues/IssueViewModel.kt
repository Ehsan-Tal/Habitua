package com.example.habitua.ui.issues

import androidx.lifecycle.ViewModel
import com.example.habitua.data.AppRepository
import com.example.habitua.ui.backgroundDrawables
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

private val TAG = "IssueViewModel"

class IssueViewModel(
    private val appRepository: AppRepository
): ViewModel() {

    var backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)

    private val _issueUiState = MutableStateFlow(IssueUiState())
    val issueUiState: StateFlow<IssueUiState> = _issueUiState.asStateFlow()

    private var issueJob: Job? = null

}

data class IssueUiState(
    val name: String = ""
)