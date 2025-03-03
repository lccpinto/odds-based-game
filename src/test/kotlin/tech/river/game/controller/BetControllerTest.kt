package tech.river.game.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import tech.river.game.model.Bet
import tech.river.game.model.Bet.BetResult
import tech.river.game.model.Player
import tech.river.game.repository.BetRepository
import tech.river.game.service.GameService
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(BetController::class)
@MockkBean(BetRepository::class, GameService::class)
class BetControllerTest(
    @Autowired val betRepository: BetRepository,
    @Autowired val gameService: GameService,
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) {

    @Test
    fun `placeBet endpoint returns bet result`() {
        val betRequest = mapOf(
            "username" to "johnDoe",
            "betNumber" to 5,
            "betAmount" to 100
        )
        val player = Player(id = 1, name = "John", surname = "Doe", username = "johnDoe", walletBalance = 1900)
        val bet = Bet(
            id = 1,
            player = player,
            betNumber = 5,
            betAmount = 100,
            generatedNumber = 5,
            winnings = 1000,
            result = BetResult.WIN,
            timestamp = LocalDateTime.now()
        )
        every { gameService.placeBet("johnDoe", 5, 100) } returns bet

        val result = mockMvc.post("/bets") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(betRequest)
        }
            .andExpect { status { isOk() } }
            .andReturn()

        val responseContent = result.response.contentAsString
        val responseBet = objectMapper.readValue(responseContent, Bet::class.java)
        assertThat(responseBet.id).isEqualTo(1)
        assertThat(responseBet.result).isEqualTo(BetResult.WIN)
        verify { gameService.placeBet("johnDoe", 5, 100) }
    }

    @Test
    fun `getBet endpoint returns bet when found`() {
        val player = Player(id = 1, name = "John", surname = "Doe", username = "johnDoe", walletBalance = 1900)
        val bet = Bet(
            id = 1,
            player = player,
            betNumber = 5,
            betAmount = 100,
            generatedNumber = 7,
            winnings = 0,
            result = BetResult.LOSE,
            timestamp = LocalDateTime.now()
        )
        every { betRepository.findById(1) } returns Optional.of(bet)

        val result = mockMvc.get("/bets/1") {
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isOk() } }
            .andReturn()

        val responseContent = result.response.contentAsString
        val responseBet = objectMapper.readValue(responseContent, Bet::class.java)
        assertThat(responseBet.id).isEqualTo(1)
        assertThat(responseBet.result).isEqualTo(BetResult.LOSE)
        verify { betRepository.findById(1) }
    }

    @Test
    fun `getBetsByPlayer endpoint returns paginated bets for a player`() {
        val player = Player(id = 1, name = "John", surname = "Doe", username = "johnDoe", walletBalance = 1900)
        val bets = listOf(
            Bet(
                id = 1,
                player = player,
                betNumber = 5,
                betAmount = 100,
                generatedNumber = 5,
                winnings = 1000,
                result = BetResult.WIN,
                timestamp = LocalDateTime.now()
            ),
            Bet(
                id = 2,
                player = player,
                betNumber = 3,
                betAmount = 50,
                generatedNumber = 7,
                winnings = 0,
                result = BetResult.LOSE,
                timestamp = LocalDateTime.now()
            )
        )
        val pageable = PageRequest.of(0, 2)
        val page: Page<Bet> = PageImpl(bets, pageable, bets.size.toLong())
        every { betRepository.findByPlayerUsername("johnDoe", pageable) } returns page

        val result = mockMvc.get("/bets/player/johnDoe?page=0&size=2") {
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isOk() } }
            .andReturn()

        val responseContent = result.response.contentAsString
        val jsonNode = objectMapper.readTree(responseContent)
        val contentNode = jsonNode.get("content")
        val responseBets = objectMapper.treeToValue(contentNode, Array<Bet>::class.java)
        assertThat(responseBets.size).isEqualTo(2)
        verify { betRepository.findByPlayerUsername("johnDoe", pageable) }
    }
}
