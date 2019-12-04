trait Criteria extends (String => Boolean) { self =>
    def |+| (other: Criteria): Criteria = new Criteria {
        override def apply(s: String): Boolean = self(s) && other(s)
    }
}
case object Increasing extends Criteria {
    override def apply(s: String): Boolean = s.foldLeft(Option('0')) { 
        (previous, next) => if (previous exists {_ <= next}) Some(next) else None
    }.isDefined
}
case object HasMatchingAdjacents extends Criteria {
    def digitsWithMatchingAdjacents(of: String, groupSize: Int): Set[Char] = of.toCharArray.sliding(groupSize, 1).collect {
        case Array(x, xs @ _*) if xs forall { _ == x } => x
    }.toSet
    override def apply(s: String): Boolean = digitsWithMatchingAdjacents(s, 2).nonEmpty
}
case object HasPair extends Criteria {
    override def apply(s: String): Boolean = {
        val digitsWithPairs = HasMatchingAdjacents.digitsWithMatchingAdjacents(s, 2)
        val digitsWithTriples = HasMatchingAdjacents.digitsWithMatchingAdjacents(s, 3)

        digitsWithPairs.diff(digitsWithTriples).nonEmpty
    }
}


object Day4 extends App {
    val lowerBound = 158126
    val upperBound = 624574

    private def getMatchingPasswordsCount(usingCriteria: Criteria): Int = (for {
        possibleNumber <- lowerBound to upperBound
        textRepr = possibleNumber.toString if usingCriteria(textRepr)
    } yield 1).sum


    def part1: Int = getMatchingPasswordsCount(Increasing |+| HasMatchingAdjacents)
    def part2: Int = getMatchingPasswordsCount(Increasing |+| HasPair)


    println(part1, part2)
}