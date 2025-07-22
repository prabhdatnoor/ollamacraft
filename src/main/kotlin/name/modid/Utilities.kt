package name.modid

import kotlin.random.Random

// % chance to return true
fun boolByPercentage(percent: Int): Boolean {
    return Random.nextFloat() < (percent / 100)
}