package name.modid

import kotlin.random.Random

// % chance to return true. provide as int 0-100
fun boolByPercentage(percent: Int): Boolean {
    return Random.nextFloat() < (percent / 100)
}