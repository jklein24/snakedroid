package com.lightspark.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lightspark.snake.ui.theme.SnakeInterviewTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val BOARD_SIZE = 30

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var gameState by remember {
                mutableStateOf(
                    GameState.initial(
                        boardWidth = BOARD_SIZE,
                        boardHeight = BOARD_SIZE,
                        snakeLength = 3,
                    )
                )
            }
            val coroutineScope = rememberCoroutineScope()

            suspend fun loop() {
                while (!gameState.gameOver) {
                    gameState = gameState.update()
                    delay(100)
                }
            }

            // Update the game state every 100ms based on the current direction:
            LaunchedEffect(key1 = "gameupdate") {
                loop()
            }

            SnakeInterviewTheme {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.DarkGray)
                ) {
                    GameBoard(gameState, boardSizePixels = BOARD_SIZE)
                    Spacer(modifier = Modifier.height(32.dp))
                    ScoreBoard(gameState.score, gameState.highScore, modifier = Modifier
                        .widthIn(max = 200.dp)
                        .padding(24.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Controls(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 24.dp),
                        onUpPress = { gameState = gameState.copy(direction = Direction.UP) },
                        onDownPress = { gameState = gameState.copy(direction = Direction.DOWN) },
                        onLeftPress = { gameState = gameState.copy(direction = Direction.LEFT) },
                        onRightPress = { gameState = gameState.copy(direction = Direction.RIGHT) },
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
                if (gameState.gameOver) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        GameOverDialog(modifier = Modifier.widthIn(min = 200.dp)) {
                            gameState = GameState.initial(
                                boardWidth = BOARD_SIZE,
                                boardHeight = BOARD_SIZE,
                                snakeLength = 3,
                                highScore = gameState.highScore,
                            )
                            coroutineScope.launch {
                                loop()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameBoard(
    gameState: GameState,
    modifier: Modifier = Modifier,
    boardSizePixels: Int = 30,
) {
    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxWidth()
    ) {
        val pixelSize = Size(size.width / boardSizePixels, size.height / boardSizePixels)
        drawRect(color = Color.White, size = size)
        drawRect(color = Color.Black, topLeft = Offset(4f, 4f), size = size.copy(width = size.width - 8, height = size.height - 8))
        gameState.snake.body.forEach {
            drawRect(
                color = Color.White,
                topLeft = Offset(it.x * pixelSize.width, it.y * pixelSize.height),
                size = pixelSize,
            )
        }
        drawCircle(
            color = Color.Red,
            center = Offset(gameState.food.position.x * pixelSize.width + pixelSize.width / 2, gameState.food.position.y * pixelSize.height + pixelSize.height / 2),
            radius = pixelSize.width / 2,
        )
    }
}

@Composable
fun ScoreBoard(
    score: Int,
    highScore: Int,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Score", style = MaterialTheme.typography.labelLarge.copy(color = Color.White))
            Text(text = score.toString(), style = MaterialTheme.typography.displaySmall.copy(color = Color.White))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "High Score", style = MaterialTheme.typography.labelLarge.copy(color = Color.White))
            Text(text = highScore.toString(), style = MaterialTheme.typography.displaySmall.copy(color = Color.White))
        }
    }
}

@Composable
fun Controls(modifier: Modifier = Modifier, onUpPress: () -> Unit = {}, onDownPress: () -> Unit = {}, onLeftPress: () -> Unit = {}, onRightPress: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.onKeyEvent {
        when (it.nativeKeyEvent.keyCode) {
            19 -> onUpPress()
            20 -> onDownPress()
            21 -> onLeftPress()
            22 -> onRightPress()
        }
        true
    }) {
        Button(onClick = onUpPress, modifier = Modifier.size(72.dp)) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Up",
            )
        }
        Row {
            Button(onClick = onLeftPress, modifier = Modifier.size(72.dp)) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Left",
                )
            }
            Spacer(modifier = Modifier.width(48.dp))
            Button(onClick = onRightPress, modifier = Modifier.size(72.dp)) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Right",
                )
            }
        }
        Button(onClick = onDownPress, modifier = Modifier.size(72.dp)) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Down",
            )
        }
    }
}

@Composable
fun GameOverDialog(modifier: Modifier = Modifier, onRestart: () -> Unit = {}) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Game Over", style = MaterialTheme.typography.displayMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRestart) {
                Text(text = "Restart")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SnakePreview() {
    SnakeInterviewTheme {
        Column {
            GameBoard(GameState.initial(30, 30, 3))
            Controls()
        }
    }
}