package me.syrym

import java.io.File
import kotlin.math.floor

var result = 0.0
fun main() {
    File("/home/syrym/Projects/adventofcode2019/src/main/resources/input/day1.txt").useLines { sequence ->
        sequence.forEach { calculateTotalFuel(it.toDouble()) }
    }
    println(result)
}

fun calculateFuel(mass: Double): Double = floor(mass / 3) - 2

fun calculateTotalFuel(mass: Double) {
    val res = calculateFuel(mass)
    if (res > 0) {
        result += res
        calculateTotalFuel(res)
    }
}