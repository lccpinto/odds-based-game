package tech.river.game.service

import org.springframework.stereotype.Service
import tech.river.game.config.GameProperties
import tech.river.game.model.Player
import tech.river.game.repository.PlayerRepository

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
    private val gameProperties: GameProperties
) {
    fun registerPlayer(name: String, surname: String, username: String): Player {
        if (playerRepository.findByUsername(username) != null) {
            throw IllegalArgumentException("Username already exists")
        }

        val player = Player(name = name,
            surname = surname,
            username = username,
            walletBalance = gameProperties.player.initialBalance
        )

        return playerRepository.save(player)
    }
}