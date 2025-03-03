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
import tech.river.game.model.Player
import tech.river.game.model.WalletTransaction
import tech.river.game.model.WalletTransaction.TransactionType
import tech.river.game.repository.WalletTransactionRepository
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(TransactionController::class)
@MockkBean(WalletTransactionRepository::class)
class TransactionControllerTest(
    @Autowired val walletTransactionRepository: WalletTransactionRepository,
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) {

    @Test
    fun `getWalletTransactions endpoint returns paginated transactions for player`() {
        val player = Player(id = 1, name = "John", surname = "Doe", username = "johnDoe", walletBalance = 1900)
        val transactions = listOf(
            WalletTransaction(
                id = 1,
                player = player,
                amount = -100,
                transactionType = TransactionType.BET_DEDUCTION,
                timestamp = LocalDateTime.now()
            ),
            WalletTransaction(
                id = 2,
                player = player,
                amount = 1000,
                transactionType = TransactionType.WINNING_CREDIT,
                timestamp = LocalDateTime.now()
            )
        )
        val pageable = PageRequest.of(0, 2)
        val page: Page<WalletTransaction> = PageImpl(transactions, pageable, transactions.size.toLong())
        every { walletTransactionRepository.findByPlayerUsername("johnDoe", pageable) } returns page

        val result = mockMvc.get("/transactions/player/johnDoe?size=2&page=0") {
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isOk() } }
            .andReturn()

        val responseContent = result.response.contentAsString
        val jsonNode = objectMapper.readTree(responseContent)
        val contentNode = jsonNode.get("content")
        val responseTransactions = objectMapper.treeToValue(contentNode, Array<WalletTransaction>::class.java)
        assertThat(responseTransactions.size).isEqualTo(2)
        verify { walletTransactionRepository.findByPlayerUsername("johnDoe", pageable) }
    }
}
