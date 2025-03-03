package tech.river.game.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import tech.river.game.dto.PlayerWinnings
import tech.river.game.model.Bet

interface BetRepository : JpaRepository<Bet, Long> {
    fun findByPlayerUsername(username: String, pageable: Pageable): Page<Bet>

    @Query("SELECT new tech.river.game.dto.PlayerWinnings(b.player.id, b.player.username, SUM(b.winnings)) " +
        "FROM Bet b GROUP BY b.player.id, b.player.username ORDER BY SUM(b.winnings) DESC")
    fun findTopPlayers(pageable: Pageable): List<PlayerWinnings>
}