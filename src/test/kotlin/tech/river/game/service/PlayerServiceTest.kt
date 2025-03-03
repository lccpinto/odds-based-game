package tech.river.game.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import tech.river.game.config.GameProperties
import tech.river.game.model.Player
import tech.river.game.repository.PlayerRepository

class PlayerServiceTest {

    private val playerRepository: PlayerRepository = mockk(relaxed = true)
    private val gameProperties = GameProperties(
        player = GameProperties.PlayerProperties(initialBalance = 1000)
    )
    private val playerService = PlayerService(playerRepository, gameProperties)

    @Test
    fun `registerPlayer should succeed when username is unique`() {
        every { playerRepository.findByUsername("johnDoe") } returns null
        every { playerRepository.save(any()) } answers { arg<Player>(0).copy(id = 1) }

        val registeredPlayer = playerService.registerPlayer("John", "Doe", "johnDoe")

        assertThat(registeredPlayer.walletBalance).isEqualTo(1000)
        assertThat(registeredPlayer.username).isEqualTo("johnDoe")
        verify { playerRepository.findByUsername("johnDoe") }
        verify { playerRepository.save(any()) }
    }

    @Test
    fun `registerPlayer should throw exception for duplicate username`() {
        every { playerRepository.findByUsername("johnDoe") } returns
            Player(id = 1, name = "John", surname = "Doe", username = "johnDoe", walletBalance = 1000)

        assertThatThrownBy {
            playerService.registerPlayer("John", "Doe", "johnDoe")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Username already exists")
    }
}
