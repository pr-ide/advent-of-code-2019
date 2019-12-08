import scala.io.Source

object Day8 extends App {

    val layerSize: Int = 25 * 6

    val layers: Seq[String] = Source.fromFile("day8.txt")
        .getLines
        .flatMap { _ grouped layerSize }
        .toSeq


    def part1: Int = {
        val countedByChar: Seq[Map[Char, Int]] = layers.map {
            _.groupBy(identity).mapValues(_.length).toMap
        }

        val leastZeros: Map[Char, Int] = 
            countedByChar
                .minBy { _.getOrElse('0', Int.MaxValue) }
                .withDefaultValue(0)

        leastZeros('1') * leastZeros('2')
    }


    /** 
    * The output of this function is a byte string.
    * Use https://www.dcode.fr/binary-image converter with WIDTH=25 to extract text from it.
    **/
    def part2: String = {

        def combinePixels(top: Char, bottom: Char): Char = 
            if (top == '2') bottom else top

        layers.reduce { (topLayer, bottomLayer) =>
            topLayer.zip(bottomLayer).map({combinePixels _}.tupled).mkString
        }
    }


    println(part1, part2)
    
}