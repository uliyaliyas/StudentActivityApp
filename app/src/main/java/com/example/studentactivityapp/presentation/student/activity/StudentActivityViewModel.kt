package com.example.studentactivityapp.presentation.student.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.model.CompletedTask
import com.example.studentactivityapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class FilterPeriod(val label: String) {
    ALL("Всё время"),
    TODAY("Сегодня"),
    WEEK("Неделя"),
    MONTH("Месяц")
}

data class ChartBar(
    val label: String,
    val points: Int,
    val isToday: Boolean = false
)

data class StudentActivityUiState(
    val isLoading: Boolean = false,
    val completedTasks: List<CompletedTask> = emptyList(),
    val selectedFilter: FilterPeriod = FilterPeriod.ALL,
    val totalPoints: Int = 0,
    val chartBars: List<ChartBar> = emptyList(),
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
            totalPoints = filtered.sumOf { it.points },
            chartBars = buildChartBars(period)
        )
    }

    private fun buildChartBars(period: FilterPeriod): List<ChartBar> {
        return when (period) {
            FilterPeriod.TODAY, FilterPeriod.WEEK, FilterPeriod.ALL -> buildDailyBars(7)
            FilterPeriod.MONTH -> buildWeeklyBars(4)
        }
    }

    private fun buildDailyBars(days: Int): List<ChartBar> {
        val todayStart = dayStart(0)
        return (days - 1 downTo 0).map { daysAgo ->
            val start = dayStart(daysAgo)
            val end = start + 86_400_000L
            val pts = allTasks.filter { it.completedAt in start until end }.sumOf { it.points }
            val label = SimpleDateFormat("EE", Locale("ru")).format(Date(start))
                .take(2).replaceFirstChar { it.uppercase() }
            ChartBar(label = label, points = pts, isToday = start == todayStart)
        }
    }

    private fun buildWeeklyBars(weeks: Int): List<ChartBar> {
        return (weeks - 1 downTo 0).map { weeksAgo ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                add(Calendar.WEEK_OF_YEAR, -weeksAgo)
            }
            val start = cal.timeInMillis
            val end = start + 7 * 86_400_000L
            val pts = allTasks.filter { it.completedAt in start until end }.sumOf { it.points }
            val label = "Н${weeks - weeksAgo}"
            ChartBar(label = label, points = pts, isToday = weeksAgo == 0)
        }
    }

    private fun dayStart(daysAgo: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, -daysAgo)
        }.timeInMillis
    }

    private fun periodStartMs(period: FilterPeriod): Long {
        if (period == FilterPeriod.ALL) return 0L
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
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
