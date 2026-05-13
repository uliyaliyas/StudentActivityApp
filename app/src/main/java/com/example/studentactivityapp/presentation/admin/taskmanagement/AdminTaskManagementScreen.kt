package com.example.studentactivityapp.presentation.admin.taskmanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import com.example.studentactivityapp.data.model.Task
import com.example.studentactivityapp.ui.components.appGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTaskManagementScreen(
    innerPadding: PaddingValues,
    onAddTaskClick: () -> Unit = {},
    viewModel: AdminTaskManagementViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    val gradient = appGradient

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddForm() },
                containerColor = Color(0xFF7B61FF),
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задание")
            }
        },
        containerColor = Color.Transparent
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(innerPadding)
                .padding(scaffoldPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val taskCount = uiState.tasks.size

            Text(
                text = "Задания",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B69)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (uiState.searchQuery.isBlank()) "Всего: $taskCount"
                       else "Найдено: $taskCount",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7A6F9B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.search(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск по названию", color = Color(0xFFAAAAAA)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF7B61FF))
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.search("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистить", tint = Color(0xFF7A6F9B))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7B61FF),
                    unfocusedBorderColor = Color(0xFFD8D0F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            uiState.error?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val isLoading = uiState.isLoading
            val tasks = uiState.tasks

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF7B61FF))
                    }
                }
                tasks.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (uiState.searchQuery.isBlank()) "Нет заданий" else "Ничего не найдено",
                            color = Color(0xFF7A6F9B)
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(tasks, key = { it.id }) { task ->
                            AdminTaskItem(
                                task = task,
                                onEditClick = { viewModel.openEditForm(task) },
                                onDeleteClick = { taskToDelete = task }
                            )
                        }
                    }
                }
            }
            }
        }
    }

    if (uiState.showForm) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeForm() },
            sheetState = sheetState
        ) {
            TaskFormContent(
                form = uiState.form,
                onTitleChange = { viewModel.updateTitle(it) },
                onDescriptionChange = { viewModel.updateDescription(it) },
                onPointsChange = { viewModel.updatePoints(it) },
                onDeadlineChange = { viewModel.updateDeadline(it) },
                onDeadlineClear = { viewModel.clearDeadline() },
                onSave = { viewModel.saveTask() },
                onCancel = { viewModel.closeForm() }
            )
        }
    }

    val pendingTask = taskToDelete
    if (pendingTask != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = {
                Text("Удалить задание?", fontWeight = FontWeight.Bold, color = Color(0xFF2D1B69))
            },
            text = {
                Text("Задание «${pendingTask.title}» будет удалено навсегда.", color = Color(0xFF2D1B69))
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(pendingTask.id)
                    taskToDelete = null
                }) {
                    Text("Удалить", color = Color(0xFFE53935))
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) { Text("Отмена") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskFormContent(
    form: TaskFormState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPointsChange: (String) -> Unit,
    onDeadlineChange: (Long) -> Unit,
    onDeadlineClear: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (form.deadline > 0L) form.deadline else null
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDeadlineChange(it) }
                    showDatePicker = false
                }) { Text("ОК") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
            .imePadding()
    ) {
        Text(
            text = if (form.isEdit) "Редактировать задание" else "Новое задание",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = form.title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Название") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7B61FF),
                unfocusedBorderColor = Color(0xFFD8D0F0)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = form.description,
            onValueChange = onDescriptionChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            label = { Text("Описание") },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7B61FF),
                unfocusedBorderColor = Color(0xFFD8D0F0)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = form.pointsText,
            onValueChange = onPointsChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Баллы") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7B61FF),
                unfocusedBorderColor = Color(0xFFD8D0F0)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = if (form.deadline > 0L)
                    SimpleDateFormat("d MMMM yyyy", Locale("ru")).format(Date(form.deadline))
                else "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.weight(1f),
                label = { Text("Дедлайн (необязательно)") },
                placeholder = { Text("Не задан") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату", tint = Color(0xFF7B61FF))
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7B61FF),
                    unfocusedBorderColor = Color(0xFFD8D0F0)
                )
            )
            if (form.deadline > 0L) {
                Spacer(modifier = Modifier.size(6.dp))
                IconButton(onClick = onDeadlineClear) {
                    Icon(Icons.Default.Clear, contentDescription = "Убрать дедлайн", tint = Color(0xFF7A6F9B))
                }
            }
        }

        form.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onSave,
            enabled = form.isValid && !form.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7B61FF),
                disabledContainerColor = Color(0xFFD0C8F0)
            )
        ) {
            if (form.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (form.isEdit) "Сохранить изменения" else "Создать задание",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отмена", color = Color(0xFF7A6F9B))
        }
    }
}

@Composable
private fun AdminTaskItem(
    task: Task,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFFEDE5FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = Color(0xFF7B61FF)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7A6F9B)
                    )
                }

                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать", tint = Color(0xFF7B61FF))
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color(0xFFE53935))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF1EBFF)) {
                    Text(
                        text = "+${task.points} баллов",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color(0xFF7B61FF),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (task.deadline > 0L) {
                    val now = System.currentTimeMillis()
                    val days = ((task.deadline - now) / 86_400_000).toInt()
                    val (chipColor, chipText) = when {
                        days < 0 -> Color(0xFFFFEBEE) to "просрочено"
                        days == 0 -> Color(0xFFFFEBEE) to "сегодня"
                        days <= 3 -> Color(0xFFFFF3E0) to "осталось $days дн."
                        else -> Color(0xFFE8F5E9) to SimpleDateFormat("d MMM", Locale("ru")).format(Date(task.deadline))
                    }
                    val textColor = when {
                        days <= 0 -> Color(0xFFE53935)
                        days <= 3 -> Color(0xFFF57C00)
                        else -> Color(0xFF388E3C)
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = chipColor) {
                        Text(
                            text = chipText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
