package silAST.expressions.program.terms

import scala.collection.Seq
import silAST.source.SourceLocation
import silAST.symbols.DataType
import silAST.ASTNode
import silAST.expressions.terms.GTerm

class CastTerm[+T <: GTerm[T]](
    sl:SourceLocation, 
    val expression: T, 
    val newType : DataType)
    extends GTerm[T](sl) with GProgramTerm[T] 
{
  assert(expression!=null);
  assert(newType   !=null);

  override def toString : String = "(" + expression + ") : " + newType.toString

  override def subNodes : Seq[ASTNode] = expression :: newType :: Nil

  override def subTerms  : Seq[T] = expression :: Nil

}