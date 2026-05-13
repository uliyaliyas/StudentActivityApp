package com.example.studentactivityapp.presentation.student.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.CompletedTask
import com.example.studentactivityapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

enum class FilterPeriod(val label: String) {
    ALL("Всё время"),
    TODAY("Сегодня"),
    WEEK("Неделя"),
    MONTH("Месяц")
}

data class StudentActivityUiState(
    val isLoading: Boolean = false,
    val completedTasks: List<CompletedTask> = emptyList(),
    val selectedFilter: FilterPeriod = FilterPeriod.ALL,
    val totalPoints: Int = 0,
    val error: String? = null
)

class StudentActivityViewModel : ViewModel() {

    private val repository = TaskRepository()
    private var allTasks: List<CompletedTask> = emptyList()

    private val _uiState = MutableStateFlow(StudentActivityUiState())
    val uiState: StateFlow<StudentActivityUiState> = _uiState.asStateFlow()

    fun loadCompletedTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getCompletedTasks()
                .onSuccess { tasks ->
                    allTasks = tasks
                    applyFilter(_uiState.value.selectedFilter)
                }
                .onFailure { error ->
                    _uiState.value = StudentActivityUiState(error = error.message)
                }
        }
    }

    fun setFilter(period: FilterPeriod) {
        applyFilter(period)
    }

    private fun applyFilter(period: FilterPeriod) {
        val cutoff = periodStartMs(period)
        val filtered = if (cutoff == 0L) allTasks else allTasks.filter { it.completedAt >= cutoff }
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            completedTasks = filtered,
            selectedFilter = period,
            totalPoints = filtered.sumOf { it.points }
        )
    }

    private fun periodStartMs(period: FilterPeriod): Long {
        if (period == FilterPeriod.ALL) return 0L
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        when (period) {
            FilterPeriod.TODAY -> Unit
            FilterPeriod.WEEK -> cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            FilterPeriod.MONTH -> cal.set(Calendar.DAY_OF_MONTH, 1)
            FilterPeriod.ALL -> Unit
        }
        return cal.timeInMillis
    }
}
