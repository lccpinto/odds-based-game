package tech.river.game.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import tech.river.game.model.WalletTransaction

interface WalletTransactionRepository : JpaRepository<WalletTransaction, Long> {
    fun findByPlayerUsername(username: String, pageable: Pageable): Page<WalletTransaction>
}
