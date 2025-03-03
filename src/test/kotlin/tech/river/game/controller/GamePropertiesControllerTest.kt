package tech.river.game.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import tech.river.game.config.GameProperties

@WebMvcTest(GamePropertiesController::class)
class GamePropertiesControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) {

    @Autowired
    lateinit var gameProperties: GameProperties

    @Test
    fun `getGameProperties returns current game properties`() {
        val result = mockMvc.get("/game-properties") {
        }
            .andExpect { status { isOk() } }
            .andReturn()

        val responseContent = result.response.contentAsString
        val returnedProps = objectMapper.readValue(responseContent, GameProperties::class.java)

        assertThat(returnedProps.player.initialBalance).isEqualTo(gameProperties.player.initialBalance)
        assertThat(returnedProps.bet.minNumber).isEqualTo(gameProperties.bet.minNumber)
        assertThat(returnedProps.bet.maxNumber).isEqualTo(gameProperties.bet.maxNumber)
        assertThat(returnedProps.winningRules.exactMatch).isEqualTo(gameProperties.winningRules.exactMatch)
        assertThat(returnedProps.leaderboard.maxLimit).isEqualTo(gameProperties.leaderboard.maxLimit)
    }
}
