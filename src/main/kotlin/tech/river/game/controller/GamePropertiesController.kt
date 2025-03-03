package tech.river.game.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.river.game.config.GameProperties

@RestController
@RequestMapping("/game-properties")
class GamePropertiesController(
    private val gameProperties: GameProperties
) {
    @GetMapping
    fun getGameProperties(): GameProperties = gameProperties
}