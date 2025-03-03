package tech.river.game.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Bet(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne val player: Player,
    val betNumber: Int,
    val betAmount: Int,
    val generatedNumber: Int,
    val winnings: Int,
    val result: BetResult,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    enum class BetResult {
        WIN, LOSE
    }
}
