package com.example.studentactivityapp.presentation.admin.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.FirebaseModule
import com.example.studentactivityapp.data.model.Reward
import com.example.studentactivityapp.data.repository.RewardRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RewardFormState(
    val rewardId: String? = null,
    val title: String = "",
    val description: String = "",
    val pointsText: String = "",
    val iconName: String = "gift",
    val isSaving: Boolean = false,
    val error: String? = null
) {
    val isEdit get() = rewardId != null
    val isValid get() = title.isNotBlank() && (pointsText.toIntOrNull() ?: 0) > 0
}

data class AdminRewardsUiState(
    val isLoading: Boolean = false,
    val rewards: List<Reward> = emptyList(),
    val showForm: Boolean = false,
    val form: RewardFormState = RewardFormState(),
    val error: String? = null
)

class AdminRewardsViewModel : ViewModel() {

    private val repository = RewardRepository()
    private val firestore = FirebaseModule.firestore
    private var allRewards: List<Reward> = emptyList()
    private var rewardsListener: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(AdminRewardsUiState())
    val uiState: StateFlow<AdminRewardsUiState> = _uiState.asStateFlow()

    init {
        startListening()
    }

    private fun startListening() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        rewardsListener = firestore.collection("rewards")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                    return@addSnapshotListener
                }
                allRewards = snapshot?.documents?.map { doc ->
                    Reward(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        points = (doc.getLong("points") ?: 0).toInt(),
                        iconName = doc.getString("iconName") ?: "gift"
                    )
                } ?: emptyList()
                _uiState.value = _uiState.value.copy(isLoading = false, rewards = allRewards)
            }
    }

    fun loadRewards() { /* snapshot listener handles updates automatically */ }

    fun openAddForm() {
        _uiState.value = _uiState.value.copy(showForm = true, form = RewardFormState())
    }

    fun openEditForm(reward: Reward) {
        _uiState.value = _uiState.value.copy(
            showForm = true,
            form = RewardFormState(
                rewardId = reward.id,
                title = reward.title,
                description = reward.description,
                pointsText = reward.points.toString(),
                iconName = reward.iconName
            )
        )
    }

    fun closeForm() {
        _uiState.value = _uiState.value.copy(showForm = false, form = RewardFormState())
    }

    fun updateTitle(value: String) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(title = value, error = null))
    }

    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(description = value, error = null))
    }

    fun updatePoints(value: String) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(pointsText = value, error = null))
    }

    fun updateIcon(iconName: String) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(iconName = iconName))
    }

    fun saveReward() {
        val form = _uiState.value.form
        val points = form.pointsText.toIntOrNull() ?: return
        _uiState.value = _uiState.value.copy(form = form.copy(isSaving = true, error = null))

        viewModelScope.launch {
            val result = if (form.isEdit) {
                repository.updateReward(form.rewardId!!, form.title.trim(), form.description.trim(), points, form.iconName)
            } else {
                repository.createReward(form.title.trim(), form.description.trim(), points, form.iconName)
            }

            result.onSuccess {
                closeForm()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    form = _uiState.value.form.copy(isSaving = false, error = error.message ?: "Ошибка сохранения")
                )
            }
        }
    }

    fun deleteReward(rewardId: String) {
        val snapshot = allRewards.toList()
        allRewards = allRewards.filter { it.id != rewardId }
        _uiState.value = _uiState.value.copy(rewards = allRewards)

        viewModelScope.launch {
            repository.deleteReward(rewardId).onFailure {
                allRewards = snapshot
                _uiState.value = _uiState.value.copy(rewards = allRewards, error = it.message ?: "Ошибка удаления")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        rewardsListener?.remove()
    }
}
