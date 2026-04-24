package com.example.studentactivityapp.presentation.admin.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AdminAddTaskScreen() {
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val points = remember { mutableStateOf("") }
    val type = remember { mutableStateOf("Посещение") }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF7F3FF), Color(0xFFFFFFFF))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Добавить задание",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Создай новое задание для студентов",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF7A6F9B)
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = title.value,
            onValueChange = { title.value = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            label = { Text("Название") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description.value,
            onValueChange = { description.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            label = { Text("Описание") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = points.value,
            onValueChange = { points.value = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            label = { Text("Баллы") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Тип задания",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D1B69)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TaskTypeChip("Посещение", type.value == "Посещение") { type.value = "Посещение" }
            TaskTypeChip("Комментарий", type.value == "Комментарий") { type.value = "Комментарий" }
            TaskTypeChip("Кастом", type.value == "Кастом") { type.value = "Кастом" }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF))
        ) {
            Text("Сохранить")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("Отмена", color = Color.Black)
        }
    }
}

@Composable
private fun TaskTypeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFF7B61FF) else Color.White,
        tonalElevation = if (selected) 2.dp else 0.dp,
        shadowElevation = if (selected) 2.dp else 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            color = if (selected) Color.White else Color(0xFF6E638B),
            fontWeight = FontWeight.Medium
        )
    }
}