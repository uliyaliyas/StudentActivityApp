package com.example.studentactivityapp.presentation.student.snake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentactivityapp.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Direction { UP, DOWN, LEFT, RIGHT }

enum class GameState { IDLE, PLAYING, GAME_OVER }

data class SnakeUiState(
    val snake: List<Pair<Int, Int>> = listOf(Pair(10, 10)),
    val food: Pair<Int, Int> = Pair(5, 5),
    val direction: Direction = Direction.RIGHT,
    val score: Int = 0,
    val gameState: GameState = GameState.IDLE,
    val dailyPointsEarned: Int = 0,
    val dailyLimit: Int = UserRepository.SNAKE_DAILY_LIMIT,
    val message: String? = null,
    val isGuest: Boolean = false
)

class SnakeViewModel : ViewModel() {

    companion object {
        const val GRID_SIZE = 20
        const val TICK_MS = 220L
    }

    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(SnakeUiState())
    val uiState: StateFlow<SnakeUiState> = _uiState.asStateFlow()

    private var gameJob: Job? = null
    private var pendingDirection: Direction = Direction.RIGHT

    init {
        viewModelScope.launch {
            val daily = userRepository.getSnakeDailyPoints().getOrNull() ?: 0
            _uiState.value = _uiState.value.copy(dailyPointsEarned = daily)
        }
    }

    fun changeDirection(newDir: Direction) {
        val cur = _uiState.value.direction
        val forbidden = when (cur) {
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            Direction.LEFT -> Direction.RIGHT
            Direction.RIGHT -> Direction.LEFT
        }
        if (newDir != forbidden) {
            pendingDirection = newDir
        }
    }

    fun startGame() {
        gameJob?.cancel()
        val startSnake = listOf(Pair(10, 10), Pair(9, 10), Pair(8, 10))
        val food = generateFood(startSnake)
        pendingDirection = Direction.RIGHT
        _uiState.value = _uiState.value.copy(
            snake = startSnake,
            food = food,
            direction = Direction.RIGHT,
            score = 0,
            gameState = GameState.PLAYING,
            message = null
        )
        gameJob = viewModelScope.launch {
            while (true) {
                delay(TICK_MS)
                tick()
                if (_uiState.value.gameState != GameState.PLAYING) break
            }
        }
    }

    private fun tick() {
        val state = _uiState.value
        if (state.gameState != GameState.PLAYING) return

        val dir = pendingDirection
        _uiState.value = state.copy(direction = dir)

        val head = state.snake.first()
        val newHead = when (dir) {
            Direction.UP -> Pair(head.first, (head.second - 1 + GRID_SIZE) % GRID_SIZE)
            Direction.DOWN -> Pair(head.first, (head.second + 1) % GRID_SIZE)
            Direction.LEFT -> Pair((head.first - 1 + GRID_SIZE) % GRID_SIZE, head.second)
            Direction.RIGHT -> Pair((head.first + 1) % GRID_SIZE, head.second)
        }

        if (state.snake.contains(newHead)) {
            endGame(state.score)
            return
        }

        val ateFood = newHead == state.food
        val newSnake = if (ateFood) {
            listOf(newHead) + state.snake
        } else {
            listOf(newHead) + state.snake.dropLast(1)
        }
        val newFood = if (ateFood) generateFood(newSnake) else state.food
        val newScore = if (ateFood) state.score + 1 else state.score

        _uiState.value = _uiState.value.copy(
            snake = newSnake,
            food = newFood,
            score = newScore
        )
    }

    private fun endGame(score: Int) {
        gameJob?.cancel()
        _uiState.value = _uiState.value.copy(gameState = GameState.GAME_OVER)

        if (score == 0) {
            _uiState.value = _uiState.value.copy(message = "Игра окончена. Очков: 0")
            return
        }

        viewModelScope.launch {
            val earned = score
            val result = userRepository.addSnakePoints(earned)
            val added = result.getOrNull() ?: 0
            val newDaily = _uiState.value.dailyPointsEarned + added
            val msg = if (added > 0) {
                "Игра окончена! +$added баллов (счёт: $score)"
            } else {
                "Игра окончена! Дневной лимит ${UserRepository.SNAKE_DAILY_LIMIT} баллов исчерпан."
            }
            _uiState.value = _uiState.value.copy(
                dailyPointsEarned = newDaily,
                message = msg
            )
        }
    }

    private fun generateFood(snake: List<Pair<Int, Int>>): Pair<Int, Int> {
        val allCells = (0 until GRID_SIZE).flatMap { x ->
            (0 until GRID_SIZE).map { y -> Pair(x, y) }
        }
        val free = allCells - snake.toSet()
        return if (free.isNotEmpty()) free[Random.nextInt(free.size)] else Pair(0, 0)
    }
}
