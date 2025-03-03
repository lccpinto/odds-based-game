package tech.river.game.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.river.game.model.Player
import tech.river.game.service.PlayerService

@RestController
@RequestMapping("/players")
class PlayerController(private val playerService: PlayerService) {

    data class RegistrationRequest(val name: String, val surname: String, val username: String)

    @PostMapping("/register")
    fun register(@RequestBody request: RegistrationRequest): ResponseEntity<Player> {
        val player = playerService.registerPlayer(request.name, request.surname, request.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(player)
    }
}