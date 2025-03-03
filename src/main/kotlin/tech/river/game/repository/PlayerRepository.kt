package tech.river.game.repository

import org.springframework.data.jpa.repository.JpaRepository
import tech.river.game.model.Player

interface PlayerRepository : JpaRepository<Player, Long> {
    fun findByUsername(username: String): Player?
}