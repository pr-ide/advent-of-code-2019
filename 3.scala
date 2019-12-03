import scala.io.Source


object Orientation extends Enumeration {
  val Vertical, Horizontal = Value
}

final case class Point(x: Int, y: Int) {
  def distance(to: Point = Point(0, 0)): Int = (x - to.x).abs + (y - to.y).abs
}

final case class OrientedLine(begin: Point, end: Point) { // Vector is taken
  import Orientation._
  import OrientedLine.IntBetween

  def orientation = if (begin.x == end.x) Vertical else Horizontal
  def intersects(that: OrientedLine): Option[Point] = (orientation, that.orientation) match {
    case (Vertical, Horizontal) =>
      for {
        xIntersect <- begin.x.between(that.begin.x, that.end.x)
        yIntersect <- that.begin.y.between(begin.y, end.y)
      } yield Point(xIntersect, yIntersect)
    case (Horizontal, Vertical) => that intersects this
    case (_, _) => None
  }
  def contains(p: Point): Boolean = if (orientation == Vertical && p.x == begin.x) {
    p.y.between(begin.y, end.y).isDefined
  } else if (orientation == Horizontal && p.y == begin.y) {
    p.x.between(begin.x, end.x).isDefined
  } else false

  def length: Int = begin.distance(to = end)
}

object OrientedLine {
  implicit class IntBetween(val i: Int) extends AnyVal {
    def between(left: Int, right: Int): Option[Int] = if (left > right) between(right, left) else {
      if ((left < i) && (i < right)) Some(i) else None
    }
  }
}

final case class Trace(vectors: List[OrientedLine])
object Trace {

  private def nextPoint(origin: Point, direction: Char, delta: Int): Point = direction match {
    case 'U' => origin.copy(y = origin.y - delta)
    case 'D' => origin.copy(y = origin.y + delta)
    case 'L' => origin.copy(x = origin.x - delta)
    case 'R' => origin.copy(x = origin.x + delta)
  }

  def parse(trace: String): Trace = {
    import scala.annotation.tailrec

    @tailrec def parseImpl(origin: Point,
                           directionsWithDeltas: List[(Char, Int)],
                           accumulatedLines: List[OrientedLine]): List[OrientedLine] = directionsWithDeltas match {
      case (direction, len) :: xs =>
        val next = nextPoint(origin, direction, len)
        parseImpl(next, xs, OrientedLine(origin, next) :: accumulatedLines)
      case Nil => accumulatedLines
    }

    val moves = trace.split(",").map { cmd => (cmd.head, cmd.tail.toInt) }.toList
    Trace(parseImpl(Point(0, 0), moves, Nil).init)
  }
}

object Day3 extends App {

  val traces =
    Source
      .fromFile("day3.txt")
      .getLines
      .toList
      .map(Trace.parse)


  val trace1 :: trace2 :: Nil = traces
    .map(_.vectors.partition(_.orientation == Orientation.Vertical))

  val intersections = (for {
    hLine <- trace1._2
    vLine <- trace2._1
    intersectionPoint <- hLine intersects vLine
  } yield intersectionPoint) union (for {
    hLine <- trace2._2
    vLine <- trace1._1
    intersectionPoint <- hLine intersects vLine
  } yield intersectionPoint)


  def part1: Int = intersections.map(_.distance()).min

  def part2: Int = (for {
      point <- intersections
      totalTracesWeight = (for {
        trace <- traces
        traceToIntersection = trace.vectors.reverse.takeWhile(line => !line.contains(point))
        fromOrigin = trace.vectors.last.begin.distance()
        toPoint = traceToIntersection.last.end.distance(to = point)
        traceWeight = traceToIntersection.foldLeft(fromOrigin + toPoint) {_ + _.length}
      } yield traceWeight).sum
    } yield totalTracesWeight).min

  println(part1, part2)

}
