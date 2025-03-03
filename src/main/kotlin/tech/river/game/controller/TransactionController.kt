package tech.river.game.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.river.game.model.WalletTransaction
import tech.river.game.repository.WalletTransactionRepository

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val walletTransactionRepository: WalletTransactionRepository
) {

    @GetMapping("/player/{username}")
    fun getWalletTransactions(
        @PathVariable username: String,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): ResponseEntity<Page<WalletTransaction>> {
        val pageable = PageRequest.of(page, size)
        val transactions = walletTransactionRepository.findByPlayerUsername(username, pageable)
        return ResponseEntity.ok(transactions)
    }

}
