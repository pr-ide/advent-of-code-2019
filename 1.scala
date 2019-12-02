import scala.io.Source


object Day1 extends App {

    private def toFuel(mass: Long): Long = Math.floor(
        mass / 3.0
    ).toLong - 2L

    def part1: Long = 
      Source
        .fromFile("day1.txt")
        .getLines
        .map(_.toLong)
        .map(toFuel)
        .sum

    def part2: Long =
      Source
        .fromFile("day1.txt")
        .getLines
        .map(_.toLong)
        .flatMap { massLike =>
          lazy val fuels: Stream[Long] = massLike #:: fuels map toFuel
          fuels.takeWhile(_ > 0).toList
        }.sum

    println(part1, part2)
    
}
