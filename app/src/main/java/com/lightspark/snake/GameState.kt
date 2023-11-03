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
        if (gameOver || paused) {
            return this
        }

        val newSnake = snake.update(direction, speed, boardWidth, boardHeight)
        val newFood = if (newSnake.head() == food.position) {
            food.copy(eaten = true)
        } else {
            food
        }

        return GameState(
            snake = if (newFood.eaten) {
                newSnake.copy(
                    body = listOf(newSnake.head()) + newSnake.body,
                )
            } else {
                newSnake
            },
            food = if (newFood.eaten) {
                val newPosition = Position.random(BOARD_SIZE, BOARD_SIZE)
                if (newSnake.body.contains(newPosition)) {
                    // If the new food position is inside the snake, try again.
                    newFood.copy(position = newPosition)
                } else {
                    newFood.copy(position = newPosition, eaten = false)
                }
            } else {
                newFood
            },
            score = if (newFood.eaten) score + 1 else score,
            gameOver = newSnake.dead,
            paused = false,
            direction = direction,
            speed = if (newFood.eaten) speed + 1 else speed,
            highScore = if (newSnake.dead && score > highScore) score else highScore,
            boardWidth = boardWidth,
            boardHeight = boardHeight,
        )
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
) {
    fun update(direction: Direction, speed: Int, boardWidth: Int, boardHeight: Int): Snake {
        if (dead) {
            return this
        }

        val newHead = head().move(direction)
        val newBody = listOf(newHead) + body.dropLast(1)
        val hitWall = newHead.x !in 0 until boardWidth || newHead.y !in 0 until boardHeight
        val newDead = newHead in body.drop(1) || hitWall

        return Snake(
            body = newBody,
            dead = newDead,
        )
    }

    fun head(): Position {
        return body.first()
    }
}

data class Food(
    val position: Position,
    val eaten: Boolean
)

data class Position(
    val x: Int,
    val y: Int
) {
    fun move(direction: Direction): Position {
        return when (direction) {
            Direction.UP -> Position(x, y - 1)
            Direction.DOWN -> Position(x, y + 1)
            Direction.LEFT -> Position(x - 1, y)
            Direction.RIGHT -> Position(x + 1, y)
        }
    }

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
