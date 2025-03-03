package tech.river.game.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class WalletTransaction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne val player: Player,
    val amount: Int,
    val transactionType: TransactionType,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    enum class TransactionType {
        BET_DEDUCTION, WINNING_CREDIT
    }
}
