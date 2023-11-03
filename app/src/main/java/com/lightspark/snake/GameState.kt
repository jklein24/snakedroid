package com.lightspark.snake

data class GameState(
    val snake: Snake,
    val food: Food,
    val score: Int,
    val gameOver: Boolean,
    val paused: Boolean,
    val direction: Direction,
    val speed: Int,
    val highScore: Int,
    val boardWidth: Int,
    val boardHeight: Int,
) {
    fun update(): GameState {
        // TODO: Implement this.
        return this
    }

    companion object {
        fun initial(boardWidth: Int, boardHeight: Int, snakeLength: Int = 3, highScore: Int = 0): GameState {
            val randomPosition = Position.random(boardWidth - snakeLength, boardHeight)
            val awayFromEdge = if (randomPosition.x > boardWidth / 2) Direction.LEFT else Direction.RIGHT
            return GameState(
                snake = Snake(
                    body = if (awayFromEdge == Direction.LEFT) {
                        (0 until snakeLength).map { Position(randomPosition.x + it, randomPosition.y) }
                    } else {
                        (snakeLength - 1 downTo 0).map { Position(randomPosition.x + it, randomPosition.y) }
                    },
                    dead = false,
                ),
                food = Food(
                    position = Position.random(boardWidth, boardHeight),
                    eaten = false,
                ),
                score = 0,
                gameOver = false,
                paused = false,
                direction = awayFromEdge,
                speed = 1,
                highScore = highScore,
                boardWidth = boardWidth,
                boardHeight = boardHeight,
            )
        }
    }
}


data class Snake(
    val body: List<Position>,
    val dead: Boolean
)

data class Food(
    val position: Position,
    val eaten: Boolean
)

data class Position(
    val x: Int,
    val y: Int
) {
    companion object {
        fun random(width: Int, height: Int): Position {
            return Position(
                x = (0 until width).random(),
                y = (0 until height).random(),
            )
        }
    }
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}
