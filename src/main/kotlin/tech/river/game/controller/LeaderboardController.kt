package tech.river.game.controller

import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import tech.river.game.config.GameProperties
import tech.river.game.dto.PlayerWinnings
import tech.river.game.repository.BetRepository

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val betRepository: BetRepository,
    private val gameProperties: GameProperties
) {

    @GetMapping
    fun getTopPlayers(
        @RequestParam(value = "limit", defaultValue = "10") limit: Int
    ): ResponseEntity<List<PlayerWinnings>> {
        if (limit > gameProperties.leaderboard.maxLimit) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Maximum allowed limit is ${gameProperties.leaderboard.maxLimit}"
            )
        }

        val pageable = PageRequest.of(0, limit)
        val leaderboard = betRepository.findTopPlayers(pageable)

        return ResponseEntity.ok(leaderboard)
    }
}