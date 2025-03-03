package tech.river.game.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import tech.river.game.controller.request.BetRequest
import tech.river.game.model.Bet
import tech.river.game.repository.BetRepository
import tech.river.game.repository.WalletTransactionRepository
import tech.river.game.service.GameService

@RestController
@RequestMapping("/bets")
class BetController(
    private val gameService: GameService,
    private val betRepository: BetRepository
) {

    @PostMapping
    fun placeBet(@RequestBody request: BetRequest): ResponseEntity<Bet> {
        val bet = gameService.placeBet(request.username, request.betNumber, request.betAmount)
        return ResponseEntity.ok(bet)
    }

    @GetMapping("/{betId}")
    fun getBet(@PathVariable betId: Long): ResponseEntity<Bet> {
        val bet = betRepository.findById(betId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Bet not found") }
        return ResponseEntity.ok(bet)
    }

    @GetMapping("/player/{username}")
    fun getBetsByPlayer(
        @PathVariable username: String,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): ResponseEntity<Page<Bet>> {
        val pageable = PageRequest.of(page, size)
        val bets = betRepository.findByPlayerUsername(username, pageable)
        return ResponseEntity.ok(bets)
    }
}
