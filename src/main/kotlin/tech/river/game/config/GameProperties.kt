package tech.river.game.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
@ConfigurationProperties(prefix = "game")
data class GameProperties(
    val player: PlayerProperties = PlayerProperties(),
    val bet: BetProperties = BetProperties(),
    val winningRules: WinningRulesProperties = WinningRulesProperties(),
    val leaderboard: LeaderboardProperties = LeaderboardProperties()
) {
    data class PlayerProperties(
        var initialBalance: Int = 1000
    )

    data class BetProperties(
        var minNumber: Int = 1,
        var maxNumber: Int = 10
    )

    data class WinningRulesProperties(
        var exactMatch: BigDecimal = BigDecimal.TEN,
        var oneOff: BigDecimal = BigDecimal("5"),
        var twoOff: BigDecimal = BigDecimal("0.5")
    )

    data class LeaderboardProperties(
        var maxLimit: Int = 100
    )
}