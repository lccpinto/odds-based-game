package tech.river.game

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import tech.river.game.config.GameProperties

@SpringBootApplication
@EnableConfigurationProperties(GameProperties::class)
class OddsBasedGameApplication

fun main(args: Array<String>) {
	runApplication<OddsBasedGameApplication>(*args)
}
