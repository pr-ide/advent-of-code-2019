import scala.io.Source


sealed trait TreeNode[T] {
    def bfsImpl(searched: T)(visitedNodes: List[TreeNode[T]]): Option[List[TreeNode[T]]]

     def directRelations: Int
     def indirectRelations: Int
     val value: T

     def totalRelations = directRelations + indirectRelations

     def bfs(searched: T): Option[List[TreeNode[T]]] = bfsImpl(searched)(Nil).map(_.reverse)
}

final case class NonEmptyNode[T](value: T, descendants: Iterable[TreeNode[T]]) extends TreeNode[T] {
    override def directRelations: Int = descendants.foldLeft(descendants.size) { _ + _.directRelations }
    override def indirectRelations: Int = descendants.foldLeft(0) { (accu, node) =>
        accu + node.indirectRelations + node.directRelations 
    }

    override def bfsImpl(searched: T)(visited: List[TreeNode[T]]): Option[List[TreeNode[T]]] = 
        if (searched == value) Some(this :: visited) else descendants.toStream.map {
            _.bfsImpl(searched)(this :: visited)
        }.dropWhile(_.isEmpty).headOption.flatten
        
}

final case class Leaf[T](value: T) extends TreeNode[T] {
    override def directRelations: Int = 0
    override def indirectRelations: Int = 0

    override def bfsImpl(searched: T)(visited: List[TreeNode[T]]): Option[List[TreeNode[T]]] = 
        if (searched == value) Some(
            this :: visited
        ) else None
}

object TreeNode {

    def apply[T](fromAdjacencyPairs: Map[T, Iterable[T]], usingRoot: T): TreeNode[T] = {
        fromAdjacencyPairs
            .get(usingRoot)
            .collectFirst { case children if children.nonEmpty =>
                NonEmptyNode(usingRoot, children map { TreeNode(fromAdjacencyPairs, _) })
            }.getOrElse(Leaf(usingRoot))
    }
}



object Day6 extends App {


    val relations: Map[String, Seq[String]] = Source.fromFile("day6.txt")
        .getLines
        .map { line =>
            val Array(from, to) = line.split("\\)")
            from -> to
        }.toSeq
         .groupBy(_._1)
         .mapValues(_ map {_._2})
         .toMap

    val orbitTree: TreeNode[String] = TreeNode(relations, "COM")

    
    def part1: Int = orbitTree.totalRelations
    def part2: Int = (for {
        me <- orbitTree.bfs("YOU")
        santa <- orbitTree.bfs("SAN")
        
        intersectionNode = {
            me.zip(santa).takeWhile { case (l, r) => l == r }.last._1
        }

        myTraceToIntersection = me.dropWhile(_ != intersectionNode).tail
        santasTraceToIntersection = santa.dropWhile(_ != intersectionNode).tail
    } yield myTraceToIntersection.length + santasTraceToIntersection.length - 2).get


    println(part1, part2)
}