package com.example.studentactivityapp.presentation.admin.studentprofile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentactivityapp.ui.components.InitialsAvatar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val presets = listOf(10, 25, 50, 100)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStudentProfileScreen(
    studentId: String,
    innerPadding: PaddingValues,
    onBackClick: () -> Unit,
    viewModel: AdminStudentProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.loadStudent(studentId)
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessage()
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF7F3FF), Color.White)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(innerPadding)
    ) {
        TopAppBar(
            title = {
                Text("Профиль студента", fontWeight = FontWeight.Bold, color = Color(0xFF2D1B69))
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color(0xFF7B61FF))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        val isLoading = uiState.isLoading
        val student = uiState.student

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7B61FF))
                }
            }
            student == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.error ?: "Студент не найден", color = Color.Red)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            InitialsAvatar(
                                name = student.name,
                                size = 80.dp,
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = student.name.ifBlank { "Без имени" },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D1B69)
                            )
                            Text(text = student.email, color = Color(0xFF8A84A0))
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Color(0xFFF4EEFF), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF7B61FF))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${student.points} баллов",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7B61FF),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Изменить баллы",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val form = uiState.form

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = form.isAdding,
                            onClick = { viewModel.setMode(true) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = Color(0xFF43A047),
                                activeContentColor = Color.White
                            )
                        ) { Text("Начислить") }
                        SegmentedButton(
                            selected = !form.isAdding,
                            onClick = { viewModel.setMode(false) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = Color(0xFFE53935),
                                activeContentColor = Color.White
                            )
                        ) { Text("Списать") }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.forEach { amount ->
                            FilterChip(
                                selected = form.amount == amount.toString(),
                                onClick = { viewModel.setPreset(amount) },
                                label = { Text("+$amount") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF7B61FF),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = form.amount,
                        onValueChange = { viewModel.updateAmount(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Количество баллов") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7B61FF),
                            unfocusedBorderColor = Color(0xFFD8D0F0)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = form.reason,
                        onValueChange = { viewModel.updateReason(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Причина (необязательно)") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7B61FF),
                            unfocusedBorderColor = Color(0xFFD8D0F0)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val actionColor = if (form.isAdding) Color(0xFF43A047) else Color(0xFFE53935)
                    val actionLabel = if (form.isAdding) "Начислить баллы" else "Списать баллы"

                    Button(
                        onClick = { viewModel.applyAdjustment(studentId) },
                        enabled = form.isValid && !form.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = actionColor,
                            disabledContainerColor = Color(0xFFCCCCCC)
                        )
                    ) {
                        if (form.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(actionLabel, fontWeight = FontWeight.Bold)
                        }
                    }

                    uiState.successMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = Color(0xFF43A047), fontWeight = FontWeight.SemiBold)
                    }
                    uiState.error?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = Color.Red)
                    }

                    val adjustments = uiState.adjustments
                    if (adjustments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = Color(0xFF7B61FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "История изменений",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D1B69)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        adjustments.forEach { record ->
                            AdjustmentRow(record = record)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun AdjustmentRow(record: PointAdjustmentRecord) {
    val isPositive = record.delta >= 0
    val color = if (isPositive) Color(0xFF43A047) else Color(0xFFE53935)
    val sign = if (isPositive) "+" else ""
    val dateStr = SimpleDateFormat("d MMM, HH:mm", Locale("ru")).format(Date(record.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        if (isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$sign${record.delta}",
                    fontWeight = FontWeight.Bold,
                    color = color,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (record.reason.isBlank()) "Без причины" else record.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2D1B69)
                )
                Text(text = dateStr, style = MaterialTheme.typography.bodySmall, color = Color(0xFF8A84A0))
            }
        }
    }
}
