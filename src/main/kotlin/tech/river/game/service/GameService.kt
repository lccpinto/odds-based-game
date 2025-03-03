package tech.river.game.service

import org.springframework.stereotype.Service
import tech.river.game.config.GameProperties
import tech.river.game.model.Bet
import tech.river.game.model.WalletTransaction
import tech.river.game.model.WalletTransaction.TransactionType.BET_DEDUCTION
import tech.river.game.model.WalletTransaction.TransactionType.WINNING_CREDIT
import tech.river.game.repository.BetRepository
import tech.river.game.repository.PlayerRepository
import tech.river.game.repository.WalletTransactionRepository
import tech.river.game.util.RandomNumberGenerator
import kotlin.math.abs

@Service
class GameService(
    private val playerRepository: PlayerRepository,
    private val betRepository: BetRepository,
    private val walletTransactionRepository: WalletTransactionRepository,
    private val gameProperties: GameProperties,
    private val randomNumberGenerator: RandomNumberGenerator
) {
    fun placeBet(username: String, betNumber: Int, betAmount: Int): Bet {
        val min = gameProperties.bet.minNumber
        val max = gameProperties.bet.maxNumber

        if (betNumber !in min..max) {
            throw IllegalArgumentException("Bet number must be between $min and $max")
        }

        val player = playerRepository.findByUsername(username) ?: throw IllegalArgumentException("Player not found")

        if (player.walletBalance < betAmount) {
            throw IllegalArgumentException("Insufficient funds")
        }

        player.walletBalance -= betAmount
        playerRepository.save(player)
        walletTransactionRepository.save(
            WalletTransaction(
                player = player,
                amount = -betAmount,
                transactionType = BET_DEDUCTION
            )
        )

        val generatedNumber = randomNumberGenerator.generate(min, max)

        val winnings = calculateWinnings(betNumber, generatedNumber, betAmount)
        val result = if (winnings > 0) Bet.BetResult.WIN else Bet.BetResult.LOSE

        if (winnings > 0) {
            player.walletBalance += winnings
            playerRepository.save(player)
            walletTransactionRepository.save(
                WalletTransaction(
                    player = player,
                    amount = winnings,
                    transactionType = WINNING_CREDIT
                )
            )
        }

        return betRepository.save(
            Bet(
                player = player,
                betNumber = betNumber,
                betAmount = betAmount,
                generatedNumber = generatedNumber,
                winnings = winnings,
                result = result
            )
        )
    }

    fun calculateWinnings(betNumber: Int, generatedNumber: Int, betAmount: Int): Int {
        val difference = abs(betNumber - generatedNumber)
        return when (difference) {
            0 -> (betAmount.toBigDecimal() * gameProperties.winningRules.exactMatch).toInt()
            1 -> (betAmount.toBigDecimal() * gameProperties.winningRules.oneOff).toInt()
            2 -> (betAmount.toBigDecimal() * gameProperties.winningRules.twoOff).toInt()
            else -> 0
        }
    }
}
