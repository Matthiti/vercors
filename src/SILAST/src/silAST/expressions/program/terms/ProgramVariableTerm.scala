package silAST.expressions.program.terms
import silAST.AtomicNode
import silAST.symbols.ProgramVariable
import silAST.source.SourceLocation
import silAST.ASTNode
import silAST.expressions.terms.GTerm
import silAST.expressions.terms.GAtomicTerm

class ProgramVariableTerm(
		sl : SourceLocation, 
		val variable : ProgramVariable 
	) 
	extends GTerm[ProgramVariableTerm](sl) 
	with GProgramTerm[ProgramVariableTerm]
	with GAtomicTerm[ProgramVariableTerm]
{
	assert(variable!=null);
	
	override def toString : String = variable.name
	override def subNodes : Seq[ASTNode] = variable :: Nil
}