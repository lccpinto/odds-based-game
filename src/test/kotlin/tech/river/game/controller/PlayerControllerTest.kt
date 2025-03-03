package tech.river.game.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import tech.river.game.model.Player
import tech.river.game.service.PlayerService

@WebMvcTest(PlayerController::class)
@MockkBean(PlayerService::class)
class PlayerControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val playerService: PlayerService
) {

    @Test
    fun `register endpoint should return created player`() {
        val registrationRequest = mapOf(
            "name" to "John",
            "surname" to "Doe",
            "username" to "johnDoe"
        )
        val player = Player(id = 1, name = "John", surname = "Doe", username = "johnDoe", walletBalance = 1000)
        every { playerService.registerPlayer("John", "Doe", "johnDoe") } returns player

        val result = mockMvc.post("/players/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registrationRequest)
        }
            .andExpect {
                status { isCreated() } }
            .andReturn()

        val responseContent = result.response.contentAsString
        val responsePlayer = objectMapper.readValue(responseContent, Player::class.java)
        assertThat(responsePlayer.id).isEqualTo(1)
        assertThat(responsePlayer.name).isEqualTo("John")
        assertThat(responsePlayer.username).isEqualTo("johnDoe")

        verify { playerService.registerPlayer("John", "Doe", "johnDoe") }
    }

    @Test
    fun `register endpoint should return error when registration fails`() {
        val registrationRequest = mapOf(
            "name" to "John",
            "surname" to "Doe",
            "username" to "johnDoe"
        )
        every { playerService.registerPlayer("John", "Doe", "johnDoe") } throws IllegalArgumentException("Username already exists")

        mockMvc.post("/players/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registrationRequest)
        }
            .andExpect {
                status { isBadRequest() }
                content { string(containsString("Username already exists")) }
            }
    }
}
