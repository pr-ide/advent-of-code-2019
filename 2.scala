import scala.io.Source

sealed trait Operation
object Operation {
    def apply(code: Int): Operation = code match {
        case 1 => Addition
        case 2 => Multiplication
        case 99 => Halt
    }
}
trait IntFolder {
    val op: (Int, Int) => Int
}

case object Addition extends Operation with IntFolder {
    override val op: (Int, Int) => Int = _ + _
}

case object Multiplication extends Operation with IntFolder {
    override val op: (Int, Int) => Int = _ * _
}

case object Halt extends Operation



final case class Chunk(op: Int, leftPos: Int, rightPos: Int, targetPos: Int) {
  def interpret(rawData: IndexedSeq[Int]): Either[Halt.type, IndexedSeq[Int]] = Operation(op) match {
      case folder: IntFolder => 
        val operationResult = folder.op(rawData(leftPos), rawData(rightPos))
        Right(rawData.updated(targetPos, operationResult))
      case _ => Left(Halt) 
  }
}
final case class Program(ofData: IndexedSeq[Int], onStep: Int = 0) {
    def run: IndexedSeq[Int] = if (onStep >= ofData.length - 4) ofData else {
        val head = ofData.slice(onStep, onStep + 4) match {
            case Seq(op, left, right, target) => Chunk(op, left, right, target)
        }

        head
          .interpret(ofData)
          .map(newProgram => Program(newProgram, onStep + 4).run)
          .getOrElse(ofData)
    } 
}


object Day2 extends App {

  private def loadWith(noun: Int, verb: Int): IndexedSeq[Int] = {
      val rawData = 
        Source
          .fromFile("day2.txt")
          .getLines
          .flatMap { _.split(",").map(_.toInt) }
          .toIndexedSeq

      rawData.updated(1, noun).updated(2, verb)
  }  

  def part1: Int = {
      val inAlarmState = loadWith(12, 2)
      Program(inAlarmState).run.head
  }

  def part2: Int = (for {
      noun <- 0 until 100
      verb <- 0 until 100
      output = Program(loadWith(noun, verb)).run.head if output == 19690720 
  } yield noun * 100 + verb).head

  println(part1, part2)

}
