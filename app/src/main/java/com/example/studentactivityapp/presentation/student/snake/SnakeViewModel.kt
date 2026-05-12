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

enum class Direction { UP, DOWN, LEFT, RIGHT }
enum class GameState { IDLE, PLAYING, GAME_OVER }

data class SnakeUiState(
    val snake: List<Pair<Int, Int>> = listOf(Pair(10, 10), Pair(9, 10), Pair(8, 10)),
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
        private const val TICK_MS = 220L
    }

    private val userRepository = UserRepository()
    private val _uiState = MutableStateFlow(SnakeUiState())
    val uiState: StateFlow<SnakeUiState> = _uiState.asStateFlow()

    private var nextDirection = Direction.RIGHT
    private var gameJob: Job? = null

    init {
        loadDailyPoints()
    }

    private fun loadDailyPoints() {
        viewModelScope.launch {
            userRepository.getSnakeDailyPoints().fold(
                onSuccess = { points ->
                    _uiState.value = _uiState.value.copy(dailyPointsEarned = points)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isGuest = true)
                }
            )
        }
    }

    fun changeDirection(dir: Direction) {
        val current = _uiState.value.direction
        if (dir == Direction.UP && current == Direction.DOWN) return
        if (dir == Direction.DOWN && current == Direction.UP) return
        if (dir == Direction.LEFT && current == Direction.RIGHT) return
        if (dir == Direction.RIGHT && current == Direction.LEFT) return
        nextDirection = dir
    }

    fun startGame() {
        gameJob?.cancel()
        nextDirection = Direction.RIGHT
        val initialSnake = listOf(Pair(10, 10), Pair(9, 10), Pair(8, 10))
        _uiState.value = _uiState.value.copy(
            snake = initialSnake,
            food = generateFood(initialSnake),
            direction = Direction.RIGHT,
            score = 0,
            gameState = GameState.PLAYING,
            message = null
        )
        gameJob = viewModelScope.launch {
            while (_uiState.value.gameState == GameState.PLAYING) {
                delay(TICK_MS)
                tick()
            }
        }
    }

    private fun tick() {
        val state = _uiState.value
        if (state.gameState != GameState.PLAYING) return

        val dir = nextDirection
        val head = state.snake.first()
        val newHead = when (dir) {
            Direction.UP -> Pair(head.first, head.second - 1)
            Direction.DOWN -> Pair(head.first, head.second + 1)
            Direction.LEFT -> Pair(head.first - 1, head.second)
            Direction.RIGHT -> Pair(head.first + 1, head.second)
        }

        if (newHead.first < 0 || newHead.first >= GRID_SIZE ||
            newHead.second < 0 || newHead.second >= GRID_SIZE ||
            state.snake.contains(newHead)
        ) {
            endGame(state.score)
            return
        }

        val ateFood = newHead == state.food
        val newSnake = if (ateFood) listOf(newHead) + state.snake
                       else listOf(newHead) + state.snake.dropLast(1)
        val newFood = if (ateFood) generateFood(newSnake) else state.food
        val newScore = if (ateFood) state.score + 1 else state.score

        _uiState.value = state.copy(
            snake = newSnake,
            food = newFood,
            direction = dir,
            score = newScore
        )
    }

    private fun endGame(score: Int) {
        gameJob?.cancel()
        _uiState.value = _uiState.value.copy(gameState = GameState.GAME_OVER)
        if (!_uiState.value.isGuest && score > 0) {
            saveScore(score)
        }
    }

    private fun saveScore(score: Int) {
        viewModelScope.launch {
            userRepository.addSnakePoints(score).fold(
                onSuccess = { added ->
                    _uiState.value = _uiState.value.copy(
                        dailyPointsEarned = _uiState.value.dailyPointsEarned + added,
                        message = if (added > 0) "+$added баллов за игру!"
                                  else "Дневной лимит исчерпан"
                    )
                },
                onFailure = { }
            )
        }
    }

    private fun generateFood(snake: List<Pair<Int, Int>>): Pair<Int, Int> {
        val snakeSet = snake.toSet()
        val free = mutableListOf<Pair<Int, Int>>()
        for (x in 0 until GRID_SIZE) {
            for (y in 0 until GRID_SIZE) {
                val cell = Pair(x, y)
                if (cell !in snakeSet) free.add(cell)
            }
        }
        return if (free.isNotEmpty()) free.random() else Pair(0, 0)
    }

    override fun onCleared() {
        gameJob?.cancel()
        super.onCleared()
    }
}
