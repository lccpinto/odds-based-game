package tech.river.game.dto

data class PlayerWinnings(
    val playerId: Long,
    val username: String,
    val totalWinnings: Long
)