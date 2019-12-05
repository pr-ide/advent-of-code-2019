import scala.io.Source
import scala.util.{Left, Right}
import Interpreter.Code


final case class Interpreter(
                              stdIn: List[Int],
                              stdOut: List[Int],
                              pos: Int,
                              code: Code
                            ) {
  def withCode(code: Code) = copy(code = code)
  def nextState: Either[Interpreter, Interpreter] =
    Instruction(code(pos)).apply(pos) match {
      case Halt(_) => Left(this)
      case instruction => Right(instruction runOn this)
    }
  def fullRun: Interpreter = nextState match {
    case Left(evaluationFinished) => evaluationFinished
    case Right(evaluationInProgress) => evaluationInProgress.fullRun
  }
}

object Interpreter {
  type Code = IndexedSeq[Int]
  def apply(ofCode: Code, stdIn: List[Int]): Interpreter = Interpreter(
    stdIn,
    stdOut = Nil,
    pos = 0,
    code = ofCode
  )
}

sealed trait Instruction {
  protected val argsMapper: Seq[Int => Parameter]
  private def extractArgs(fromInterpreter: Interpreter): Seq[Int] = {
    fromInterpreter.code
      .slice(pos + 1, pos + argsMapper.length + 1)
      .zip(argsMapper)
      .map { case (arg, toParam) => toParam(arg) }
      .map(_ getValue fromInterpreter)
  }
  protected def execute(args: Seq[Int])(ctx: Interpreter): Interpreter

  val pos: Int
  def nextPos(args: Seq[Int]): Int = pos + 1 + argsMapper.length
  def runOn(interpreter: Interpreter): Interpreter = {
    val args = extractArgs(interpreter)
    execute(args)(interpreter).copy(pos = nextPos(args))
  }
}

sealed trait Parameter {
  val self: Int
  def getValue(fromInterpreter: Interpreter): Int
}

final case class Value(self: Int) extends Parameter {
  override def getValue(i: Interpreter): Int = self
}
final case class Pointer(self: Int) extends Parameter {
  override def getValue(fromInterpreter: Interpreter): Int =
    fromInterpreter.code(self)
}
object Parameter {
  def apply(mode: Int): Int => Parameter = mode match {
    case 0 => Pointer.apply _
    case 1 => Value.apply _
  }
}


sealed trait BinaryOpWithEffect extends Instruction {
  protected val combiner: (Int, Int) => Int
  protected val leftOperandMode: Int
  protected val rightOperandMode: Int

  override protected val argsMapper: Seq[Int => Parameter] = Seq(
    leftOperandMode, rightOperandMode, 1
  ).map(Parameter(_))

  override protected def execute(args: Seq[Int])
                                (onInterpreter: Interpreter): Interpreter = args match {
    case Seq(left, right, target) => onInterpreter.withCode(
      onInterpreter.code.updated(target, combiner(left, right))
    )
  }

}

final case class Addition(
                           pos: Int,
                           protected val leftOperandMode: Int,
                           protected val rightOperandMode: Int
                         ) extends BinaryOpWithEffect {
  override protected val combiner: (Int, Int) => Int = _ + _
}

final case class Multiplication(
                                 pos: Int,
                                 protected val leftOperandMode: Int,
                                 protected val rightOperandMode: Int
                               ) extends BinaryOpWithEffect {
  override protected val combiner: (Int, Int) => Int = _ * _
}

final case class LessThan(pos: Int,
                          protected val leftOperandMode: Int,
                          protected val rightOperandMode: Int
                         ) extends BinaryOpWithEffect {
  override protected val combiner = (left: Int, right: Int) => if (left < right) 1 else 0
}

final case class Equals(pos: Int,
                        protected val leftOperandMode: Int,
                        protected val rightOperandMode: Int
                       ) extends BinaryOpWithEffect {
  override protected val combiner = (left: Int, right: Int) => if (left == right) 1 else 0
}

sealed trait JumpOp extends Instruction {
  protected def testArgument(arg: Int): Boolean

  protected val operandMode: Int
  protected val jumperMode: Int

  override protected val argsMapper: Seq[Int => Parameter] = Seq(
    operandMode, jumperMode
  ).map(Parameter(_))

  override def nextPos(args: Seq[Int]): Int = args match {
    case Seq(parameter, jumpTo) => if (testArgument(parameter)) jumpTo else super.nextPos(args)
  }

  override protected def execute(args: Seq[Int])
                                (onInterpreter: Interpreter): Interpreter = onInterpreter
}

final case class JumpIfTrue(pos: Int,
                            operandMode: Int,
                            jumperMode: Int) extends JumpOp {
  override protected def testArgument(arg: Int): Boolean = arg != 0
}

final case class JumpIfFalse(pos: Int,
                             operandMode: Int,
                             jumperMode: Int) extends JumpOp {
  override protected def testArgument(arg: Int): Boolean = arg == 0
}



final case class Input(pos: Int) extends Instruction {
  override protected val argsMapper: Seq[Int => Parameter] = Seq(
    Value.apply _
  )

  override protected def execute(args: Seq[Int])
                                (onInterpreter: Interpreter): Interpreter = args match {
    case Seq(writeTo) =>
      val input :: rest = onInterpreter.stdIn
      onInterpreter.copy(stdIn = rest).withCode(
        onInterpreter.code.updated(writeTo, input)
      )
  }
}

final case class Output(pos: Int, private val readMode: Int) extends Instruction {
  override protected val argsMapper: Seq[Int => Parameter] = Seq(
    Parameter(readMode)
  )
  override protected def execute(args: Seq[Int])
                                (onInterpreter: Interpreter): Interpreter = args match {
    case Seq(parameter) =>
      onInterpreter.copy(stdOut = parameter :: onInterpreter.stdOut)
  }
}
final case class Halt(pos: Int) extends Instruction {
  override protected val argsMapper: Seq[Int => Parameter] = Seq.empty
  override protected def execute(s: Seq[Int])
                                (onInterpreter: Interpreter): Interpreter = onInterpreter
}

object Instruction {
  import scala.util.matching.Regex

  private val InstructionPattern = """(\d\d)([01]*)""".r
  private def toInstructionPattern(regWord: Int): String = {
    val instruction = regWord.toString.reverse
    if (instruction.length < 2) instruction concat "0" else instruction
  }

  private def parsedArgModes(ofString: String): Array[Int] =
    ofString.concat("00").toCharArray.map(_.asDigit)


  def apply(regWord: Int): Int => Instruction = index => toInstructionPattern(regWord) match {
    case InstructionPattern(opCode, argModes) => opCode.reverse match {
      case "01" =>
        val Array(leftMode, rightMode, _*) = parsedArgModes(argModes)
        Addition(index, leftMode, rightMode)
      case "02" =>
        val Array(leftMode, rightMode, _*) = parsedArgModes(argModes)
        Multiplication(index, leftMode, rightMode)
      case "03" => Input(index)
      case "04" => Output(index, argModes.headOption.map(_.asDigit).getOrElse(0))
      case "05" =>
        val Array(argumentMode, jumperMode, _*) = parsedArgModes(argModes)
        JumpIfTrue(index, argumentMode, jumperMode)
      case "06" =>
        val Array(argumentMode, jumperMode, _*) = parsedArgModes(argModes)
        JumpIfFalse(index, argumentMode, jumperMode)
      case "07" =>
        val Array(leftMode, rightMode, _*) = parsedArgModes(argModes)
        LessThan(index, leftMode, rightMode)
      case "08" =>
        val Array(leftMode, rightMode, _*) = parsedArgModes(argModes)
        Equals(index, leftMode, rightMode)

      case "99" => Halt(index)
    }
  }
}



object Day5 extends App {

  val rawData =
    Source
      .fromFile("day5.txt")
      .getLines
      .flatMap { _.split(",").map(_.toInt) }
      .toIndexedSeq
  

  def part1: Int =
    Interpreter(rawData, stdIn = 1 :: Nil)
      .fullRun
      .stdOut
      .head

  def part2: Int =
    Interpreter(rawData, stdIn = 5 :: Nil)
      .fullRun
      .stdOut
      .head

  println(part1, part2)
}
