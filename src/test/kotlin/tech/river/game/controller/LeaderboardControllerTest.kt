package tech.river.game.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import tech.river.game.config.GameProperties
import tech.river.game.dto.PlayerWinnings
import tech.river.game.repository.BetRepository

@WebMvcTest(LeaderboardController::class)
@MockkBean(BetRepository::class, GameProperties::class)
class LeaderboardControllerTest(
    @Autowired val betRepository: BetRepository,
    @Autowired val gameProperties: GameProperties,
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) {

    @Test
    fun `getTopPlayers returns top players list when limit is provided within allowed range`() {
        every { gameProperties.leaderboard.maxLimit } returns 100

        val expectedPlayers = listOf(
            PlayerWinnings(playerId = 1, username = "user1", totalWinnings = 1000),
            PlayerWinnings(playerId = 2, username = "user2", totalWinnings = 800)
        )
        val pageable = PageRequest.of(0, 2)
        every { betRepository.findTopPlayers(pageable) } returns expectedPlayers

        val result = mockMvc.get("/leaderboard?limit=2") {
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isOk() } }
            .andReturn()

        val responseContent = result.response.contentAsString
        val players = objectMapper.readValue(responseContent, Array<PlayerWinnings>::class.java)
        assertThat(players.size).isEqualTo(2)

        assertThat(players[0].playerId).isEqualTo(1)
        assertThat(players[0].username).isEqualTo("user1")
        assertThat(players[0].totalWinnings).isEqualTo(1000)

        assertThat(players[1].playerId).isEqualTo(2)
        assertThat(players[1].username).isEqualTo("user2")
        assertThat(players[1].totalWinnings).isEqualTo(800)

        verify { betRepository.findTopPlayers(pageable) }
    }

    @Test
    fun `getTopPlayers returns default top 10 when limit is not provided`() {
        every { gameProperties.leaderboard.maxLimit } returns 100

        val expectedPlayers = (1..10).map { i ->
            PlayerWinnings(playerId = i.toLong(), username = "user$i", totalWinnings = (1000 - i * 10).toLong())
        }
        val pageable = PageRequest.of(0, 10)
        every { betRepository.findTopPlayers(pageable) } returns expectedPlayers

        val result = mockMvc.get("/leaderboard") {
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isOk() } }
            .andReturn()

        val responseContent = result.response.contentAsString
        val players = objectMapper.readValue(responseContent, Array<PlayerWinnings>::class.java)
        assertThat(players.size).isEqualTo(10)

        verify { betRepository.findTopPlayers(pageable) }
    }

    @Test
    fun `getTopPlayers returns error when limit is greater than configured max limit`() {
        every { gameProperties.leaderboard.maxLimit } returns 100

        val result = mockMvc.get("/leaderboard?limit=101") {
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isBadRequest() } }
            .andReturn()

        assertThat(result.response.errorMessage).contains("Maximum allowed limit is 100")
    }
}
