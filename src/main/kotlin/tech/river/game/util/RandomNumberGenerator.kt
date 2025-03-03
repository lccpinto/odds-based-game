package tech.river.game.util

import org.springframework.stereotype.Component

interface RandomNumberGenerator {
    fun generate(min: Int, max: Int): Int
}

@Component
class DefaultRandomNumberGenerator : RandomNumberGenerator {
    override fun generate(min: Int, max: Int): Int = (min..max).random()
}