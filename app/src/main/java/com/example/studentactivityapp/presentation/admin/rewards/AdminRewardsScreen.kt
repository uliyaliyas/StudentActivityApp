package com.example.studentactivityapp.presentation.admin.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentactivityapp.data.model.Reward
import com.example.studentactivityapp.ui.components.appGradient

private val iconOptions = listOf(
    "gift" to Icons.Default.CardGiftcard,
    "cafe" to Icons.Default.LocalCafe,
    "food" to Icons.Default.LunchDining,
    "offer" to Icons.Default.LocalOffer,
    "trophy" to Icons.Default.EmojiEvents
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRewardsScreen(
    innerPadding: PaddingValues,
    onBackClick: () -> Unit,
    viewModel: AdminRewardsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var rewardToDelete by remember { mutableStateOf<Reward?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.loadRewards()
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
                Icon(Icons.Default.Add, contentDescription = "Добавить награду")
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color(0xFF7B61FF)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Награды",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B69)
                    )
                    Text(
                        text = "Всего: ${uiState.rewards.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7A6F9B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            uiState.error?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val isLoading = uiState.isLoading
            val rewards = uiState.rewards

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF7B61FF))
                    }
                }
                rewards.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Наград пока нет. Нажмите + чтобы добавить.", color = Color(0xFF7A6F9B))
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(rewards, key = { it.id }) { reward ->
                            AdminRewardCard(
                                reward = reward,
                                onEditClick = { viewModel.openEditForm(reward) },
                                onDeleteClick = { rewardToDelete = reward }
                            )
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
            RewardFormContent(
                form = uiState.form,
                onTitleChange = { viewModel.updateTitle(it) },
                onDescriptionChange = { viewModel.updateDescription(it) },
                onPointsChange = { viewModel.updatePoints(it) },
                onIconChange = { viewModel.updateIcon(it) },
                onSave = { viewModel.saveReward() },
                onCancel = { viewModel.closeForm() }
            )
        }
    }

    val pending = rewardToDelete
    if (pending != null) {
        AlertDialog(
            onDismissRequest = { rewardToDelete = null },
            title = { Text("Удалить награду?", fontWeight = FontWeight.Bold, color = Color(0xFF2D1B69)) },
            text = { Text("Награда «${pending.title}» будет удалена навсегда.", color = Color(0xFF2D1B69)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteReward(pending.id)
                    rewardToDelete = null
                }) {
                    Text("Удалить", color = Color(0xFFE53935))
                }
            },
            dismissButton = {
                TextButton(onClick = { rewardToDelete = null }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun RewardFormContent(
    form: RewardFormState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPointsChange: (String) -> Unit,
    onIconChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
            .imePadding()
    ) {
        Text(
            text = if (form.isEdit) "Редактировать награду" else "Новая награда",
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
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Описание") },
            singleLine = true,
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
            label = { Text("Стоимость (баллы)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7B61FF),
                unfocusedBorderColor = Color(0xFFD8D0F0)
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Иконка",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D1B69)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            iconOptions.forEach { (name, icon) ->
                val selected = form.iconName == name
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (selected) Color(0xFFEDE5FF) else Color(0xFFF5F5F5),
                            RoundedCornerShape(14.dp)
                        )
                        .then(
                            if (selected) Modifier.border(2.dp, Color(0xFF7B61FF), RoundedCornerShape(14.dp))
                            else Modifier
                        )
                        .clickable { onIconChange(name) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = if (selected) Color(0xFF7B61FF) else Color(0xFFAAAAAA),
                        modifier = Modifier.size(24.dp)
                    )
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
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (form.isEdit) "Сохранить изменения" else "Создать награду",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text("Отмена", color = Color(0xFF7A6F9B))
        }
    }
}

@Composable
private fun AdminRewardCard(
    reward: Reward,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val icon = iconOptions.find { it.first == reward.iconName }?.second ?: Icons.Default.CardGiftcard

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFFEDE5FF), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF7B61FF))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = reward.title, fontWeight = FontWeight.Bold, color = Color(0xFF2D1B69))
                Text(
                    text = reward.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A6F9B)
                )
                Text(
                    text = "${reward.points} баллов",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7B61FF),
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Редактировать", tint = Color(0xFF7B61FF))
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color(0xFFE53935))
            }
        }
    }
}
