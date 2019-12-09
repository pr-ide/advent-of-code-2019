import scala.io.Source
import scala.util.{Left, Right}


final case class SparseMemory(data: Map[BigInt, BigInt]) {
    def at(pos: BigInt): Option[BigInt] = data get pos
    def updated(atPos: BigInt, withValue: BigInt) = copy(data = data.updated(atPos, withValue))
    def apply(pos: BigInt): BigInt = at(pos).getOrElse(0)

    def slice(startIdx: BigInt, endIdx: BigInt): Seq[BigInt] = startIdx until endIdx map apply 
}



final case class Interpreter(
                              stdIn: List[BigInt],
                              stdOut: List[BigInt],
                              pos: BigInt,
                              relativePtrPos: BigInt,
                              code: SparseMemory
                            ) {
  def withCode(code: SparseMemory) = copy(code = code)
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
  def apply(ofCode: Seq[BigInt], stdIn: List[BigInt]): Interpreter = Interpreter(
    stdIn,
    stdOut = Nil,
    pos = 0L,
    relativePtrPos = 0L,
    code = SparseMemory(ofCode.zipWithIndex.map { case (v, i) => BigInt(i) -> v }.toMap)
  )
}

sealed trait Instruction {
  protected val argsMapper: Seq[BigInt => Parameter]
  private def extractArgs(fromInterpreter: Interpreter): Seq[BigInt] = {
    fromInterpreter.code
      .slice(pos + 1, pos + argsMapper.length + 1)
      .zip(argsMapper)
      .map { case (arg, toParam) => toParam(arg) }
      .map(_ getValue fromInterpreter)
  }
  protected def execute(args: Seq[BigInt])(ctx: Interpreter): Interpreter

  val pos: BigInt
  def nextPos(args: Seq[BigInt]): BigInt = pos + 1 + argsMapper.length
  def runOn(interpreter: Interpreter): Interpreter = {
    val args = extractArgs(interpreter)
    execute(args)(interpreter).copy(pos = nextPos(args))
  }
}

sealed trait Parameter {
  val self: BigInt
  def valued: Parameter
  def getValue(fromInterpreter: Interpreter): BigInt
}

final case class Value(self: BigInt) extends Parameter {
  override def getValue(i: Interpreter): BigInt = self
  override def valued: Parameter = this
}
final case class Pointer(self: BigInt) extends Parameter {
  override def valued: Parameter = Value(self)  
  override def getValue(fromInterpreter: Interpreter): BigInt =
    fromInterpreter.code(self)
}
final case class RelativePointer(self: BigInt) extends Parameter {
  override def valued: Parameter = RelativeValue(self)  
  override def getValue(fromInterpreter: Interpreter): BigInt =
    fromInterpreter.code(fromInterpreter.relativePtrPos + self)  
}
final case class RelativeValue(self: BigInt) extends Parameter {
  override def valued: Parameter = this  
  override def getValue(fromInterpreter: Interpreter): BigInt = 
    fromInterpreter.relativePtrPos + self
}
object Parameter {
  def apply(mode: Int): BigInt => Parameter = mode match {
    case 0 => Pointer.apply _
    case 1 => Value.apply _
    case 2 => RelativePointer.apply _
  }
}


sealed trait BinaryOpWithEffect extends Instruction {
  protected val combiner: (BigInt, BigInt) => BigInt
  protected val leftOperandMode: Int
  protected val rightOperandMode: Int
  protected val targetMode: Int

  override protected val argsMapper: Seq[BigInt => Parameter] = Seq(
    Parameter(leftOperandMode), Parameter(rightOperandMode), Parameter(targetMode).andThen(_.valued)
  )

  override protected def execute(args: Seq[BigInt])
                                (onInterpreter: Interpreter): Interpreter = args match {
    case Seq(left, right, target) => onInterpreter.withCode(
      onInterpreter.code.updated(target, combiner(left, right))
    )
  }

}

final case class Addition(
                           pos: BigInt,
                           protected val leftOperandMode: Int,
                           protected val rightOperandMode: Int,
                           protected val targetMode: Int
                         ) extends BinaryOpWithEffect {
  override protected val combiner: (BigInt, BigInt) => BigInt = _ + _
}

final case class Multiplication(
                                 pos: BigInt,
                                 protected val leftOperandMode: Int,
                                 protected val rightOperandMode: Int,
                                 protected val targetMode: Int
                               ) extends BinaryOpWithEffect {
  override protected val combiner: (BigInt, BigInt) => BigInt = _ * _
}

final case class LessThan(pos: BigInt,
                          protected val leftOperandMode: Int,
                          protected val rightOperandMode: Int,
                          protected val targetMode: Int
                         ) extends BinaryOpWithEffect {
  override protected val combiner = (left: BigInt, right: BigInt) => if (left < right) 1 else 0
}

final case class Equals(pos: BigInt,
                        protected val leftOperandMode: Int,
                        protected val rightOperandMode: Int,
                        protected val targetMode: Int
                       ) extends BinaryOpWithEffect {
  override protected val combiner = (left: BigInt, right: BigInt) => if (left == right) 1 else 0
}

sealed trait JumpOp extends Instruction {
  protected def testArgument(arg: BigInt): Boolean

  protected val operandMode: Int
  protected val jumperMode: Int

  override protected val argsMapper: Seq[BigInt => Parameter] = Seq(
    operandMode, jumperMode
  ).map(Parameter(_))

  override def nextPos(args: Seq[BigInt]): BigInt = args match {
    case Seq(parameter, jumpTo) => if (testArgument(parameter)) jumpTo else super.nextPos(args)
  }

  override protected def execute(args: Seq[BigInt])
                                (onInterpreter: Interpreter): Interpreter = onInterpreter
}

final case class JumpIfTrue(pos: BigInt,
                            operandMode: Int,
                            jumperMode: Int) extends JumpOp {
  override protected def testArgument(arg: BigInt): Boolean = arg != 0
}

final case class JumpIfFalse(pos: BigInt,
                             operandMode: Int,
                             jumperMode: Int) extends JumpOp {
  override protected def testArgument(arg: BigInt): Boolean = arg == 0
}


final case class RelativePointerAdjust(pos: BigInt, 
                                       deltaMode: Int) extends Instruction {
  override protected val argsMapper: Seq[BigInt => Parameter] = Seq(
    Parameter(deltaMode)
  )
  override protected def execute(args: Seq[BigInt])
                                (onInterpreter: Interpreter): Interpreter = args match {
    case Seq(delta) => onInterpreter.copy(relativePtrPos = onInterpreter.relativePtrPos + delta)
  }
}


final case class Input(pos: BigInt, writeMode: Int) extends Instruction {
  override protected val argsMapper: Seq[BigInt => Parameter] = Seq(Parameter(writeMode).andThen(_.valued))

  override protected def execute(args: Seq[BigInt])
                                (onInterpreter: Interpreter): Interpreter = args match {
    case Seq(writeTo) =>
      val input :: rest = onInterpreter.stdIn
      onInterpreter.copy(stdIn = rest).withCode(
        onInterpreter.code.updated(writeTo, input)
      )
  }
}

final case class Output(pos: BigInt, private val readMode: Int) extends Instruction {
  override protected val argsMapper: Seq[BigInt => Parameter] = Seq(
    Parameter(readMode)
  )
  override protected def execute(args: Seq[BigInt])
                                (onInterpreter: Interpreter): Interpreter = args match {
    case Seq(parameter) =>
      onInterpreter.copy(stdOut = parameter :: onInterpreter.stdOut)
  }
}
final case class Halt(pos: BigInt) extends Instruction {
  override protected val argsMapper: Seq[BigInt => Parameter] = Seq.empty
  override protected def execute(s: Seq[BigInt])
                                (onInterpreter: Interpreter): Interpreter = onInterpreter
}

object Instruction {
  import scala.util.matching.Regex

  private val InstructionPattern = """(\d\d)([012]*)""".r
  private def toInstructionPattern(regWord: BigInt): String = {
    val instruction = regWord.toString.reverse
    if (instruction.length < 2) instruction concat "0" else instruction
  }

  private def parsedArgModes(ofString: String): Array[Int] =
    ofString.concat("000").toCharArray.map(_.asDigit)


  def apply(regWord: BigInt): BigInt => Instruction = index => toInstructionPattern(regWord) match {
    case InstructionPattern(opCode, argModes) => opCode.reverse match {
      case "01" =>
        val Array(leftMode, rightMode, targetMode, _*) = parsedArgModes(argModes)
        Addition(index, leftMode, rightMode, targetMode)
      case "02" =>
        val Array(leftMode, rightMode, targetMode, _*) = parsedArgModes(argModes)
        Multiplication(index, leftMode, rightMode, targetMode)
      case "03" => 
        val Array(argMode, _*) = parsedArgModes(argModes)
        Input(index, argMode)
      case "04" => Output(index, argModes.headOption.map(_.asDigit).getOrElse(0))
      case "05" =>
        val Array(argumentMode, jumperMode, _*) = parsedArgModes(argModes)
        JumpIfTrue(index, argumentMode, jumperMode)
      case "06" =>
        val Array(argumentMode, jumperMode, _*) = parsedArgModes(argModes)
        JumpIfFalse(index, argumentMode, jumperMode)
      case "07" =>
        val Array(leftMode, rightMode, targetMode, _*) = parsedArgModes(argModes)
        LessThan(index, leftMode, rightMode, targetMode)
      case "08" =>
        val Array(leftMode, rightMode, targetMode, _*) = parsedArgModes(argModes)
        Equals(index, leftMode, rightMode, targetMode)
      case "09" =>
        val Array(deltaMode, _*) = parsedArgModes(argModes)
        RelativePointerAdjust(index, deltaMode)

      case "99" => Halt(index)
    }
  }
}



object Day9 extends App {

  val rawData =
    Source
      .fromFile("day9.txt")
      .getLines
      .flatMap { _.split(",").map(BigInt(_)) }
      .toIndexedSeq
  
  def run(withInput: BigInt): BigInt =
    Interpreter(rawData, stdIn = withInput :: Nil)
      .fullRun
      .stdOut match {
        case boostCode :: Nil => boostCode
        case withErrors @ x :: xs => throw new RuntimeException(
          s"""Tests failed: ${withErrors mkString ", "}"""
        )
      }

  def part1: BigInt = run(BigInt(1))
  def part2: BigInt = run(BigInt(2))


  println(part1, part2)
}
