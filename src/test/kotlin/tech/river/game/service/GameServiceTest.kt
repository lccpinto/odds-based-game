package tech.river.game.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import tech.river.game.config.GameProperties
import tech.river.game.model.Bet
import tech.river.game.model.Bet.BetResult
import tech.river.game.model.Player
import tech.river.game.repository.BetRepository
import tech.river.game.repository.PlayerRepository
import tech.river.game.repository.WalletTransactionRepository
import tech.river.game.util.RandomNumberGenerator
import java.math.BigDecimal

class GameServiceTest {

    private val playerRepository: PlayerRepository = mockk(relaxed = true)
    private val betRepository: BetRepository = mockk(relaxed = true)
    private val walletTransactionRepository: WalletTransactionRepository = mockk(relaxed = true)
    private val randomNumberGenerator: RandomNumberGenerator = mockk()
    private val gameProperties = GameProperties(
        player = GameProperties.PlayerProperties(initialBalance = 1000),
        bet = GameProperties.BetProperties(minNumber = 1, maxNumber = 10),
        winningRules = GameProperties.WinningRulesProperties(
            exactMatch = BigDecimal.TEN,
            oneOff = BigDecimal("5"),
            twoOff = BigDecimal("0.5")
        )
    )

    private val gameService = GameService(
        playerRepository,
        betRepository,
        walletTransactionRepository,
        gameProperties,
        randomNumberGenerator
    )

    private fun createPlayer(balance: Int = 1000) =
        Player(id = 1,
            name = "Test",
            surname = "User",
            username = "testUser",
            walletBalance = balance
        )

    @Test
    fun `placeBet should throw exception when bet number is out of range`() {
        val player = createPlayer()
        every { playerRepository.findByUsername(player.username) } returns player

        assertThatThrownBy {
            gameService.placeBet(player.username, 0, 100)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Bet number must be between")
    }

    @Test
    fun `placeBet should throw exception when insufficient funds`() {
        val player = createPlayer(balance = 50)
        every { playerRepository.findByUsername(player.username) } returns player

        assertThatThrownBy {
            gameService.placeBet(player.username, 5, 100)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Insufficient funds")
    }

    @Test
    fun `placeBet exact match wins 10x bet amount`() {
        val player = createPlayer()
        every { playerRepository.findByUsername(player.username) } returns player
        every { playerRepository.save(any()) } answers { arg<Player>(0) }
        every { walletTransactionRepository.save(any()) } answers { arg(0) }
        every { betRepository.save(any()) } answers { arg<Bet>(0) }
        every { randomNumberGenerator.generate(1, 10) } returns 5

        val betAmount = 100
        val bet = gameService.placeBet(player.username, 5, betAmount)

        // Expected winnings = 100 * 10 = 1000
        assertThat(bet.winnings).isEqualTo(1000)
        assertThat(bet.result).isEqualTo(BetResult.WIN)
        // Expected wallet: 1000 - 100 + 1000 = 1900
        assertThat(player.walletBalance).isEqualTo(1900)

        verify { randomNumberGenerator.generate(1, 10) }
    }

    @Test
    fun `placeBet one off wins 5x bet amount`() {
        val player = createPlayer()
        every { playerRepository.findByUsername(player.username) } returns player
        every { playerRepository.save(any()) } answers { arg<Player>(0) }
        every { walletTransactionRepository.save(any()) } answers { arg(0) }
        every { betRepository.save(any()) } answers { arg<Bet>(0) }
        every { randomNumberGenerator.generate(1, 10) } returns 6

        val betAmount = 100
        val bet = gameService.placeBet(player.username, 5, betAmount)
        // Expected winnings = 100 * 5 = 500
        assertThat(bet.winnings).isEqualTo(500)
        assertThat(bet.result).isEqualTo(BetResult.WIN)
        // Wallet: 1000 - 100 + 500 = 1400
        assertThat(player.walletBalance).isEqualTo(1400)

        verify { randomNumberGenerator.generate(1, 10) }
    }

    @Test
    fun `placeBet two off wins half the bet amount`() {
        val player = createPlayer()
        every { playerRepository.findByUsername(player.username) } returns player
        every { playerRepository.save(any()) } answers { arg<Player>(0) }
        every { walletTransactionRepository.save(any()) } answers { arg(0) }
        every { betRepository.save(any()) } answers { arg<Bet>(0) }
        every { randomNumberGenerator.generate(1, 10) } returns 7

        val betAmount = 100
        val bet = gameService.placeBet(player.username, 5, betAmount)
        // Expected winnings = 100 * 0.5 = 50
        assertThat(bet.winnings).isEqualTo(50)
        assertThat(bet.result).isEqualTo(BetResult.WIN)
        // Wallet: 1000 - 100 + 50 = 950
        assertThat(player.walletBalance).isEqualTo(950)

        verify { randomNumberGenerator.generate(1, 10) }
    }

    @Test
    fun `placeBet lose when difference is 3 or more`() {
        val player = createPlayer()
        every { playerRepository.findByUsername(player.username) } returns player
        every { playerRepository.save(any()) } answers { arg<Player>(0) }
        every { walletTransactionRepository.save(any()) } answers { arg(0) }
        every { betRepository.save(any()) } answers { arg<Bet>(0) }
        every { randomNumberGenerator.generate(1, 10) } returns 9

        val betAmount = 100
        val bet = gameService.placeBet(player.username, 5, betAmount)
        // Expected winnings = 0 and result = lose
        assertThat(bet.winnings).isEqualTo(0)
        assertThat(bet.result).isEqualTo(BetResult.LOSE)
        // Wallet: 1000 - 100 = 900
        assertThat(player.walletBalance).isEqualTo(900)

        verify { randomNumberGenerator.generate(1, 10) }
    }
}
