package vct.col.ast.`type`

import vct.col.ast.stmt.decl.ProgramUnit
import vct.col.ast.util.{ASTVisitor, TypeMapping}
import vct.col.util.{ASTMapping, ASTMapping1, VisitorHelper}

case class ObligationType(sort: ObligationSort) extends Type with VisitorHelper {
  override def hashCode() = sort.hashCode()
  override def supertypeof(unit: ProgramUnit, t: Type): Boolean = false

  override def accept_simple[R, A](m: ASTMapping1[R, A], arg: A): R = m.map(this, arg)
  override def accept_simple[T](v: ASTVisitor[T]): Unit = handle_standard(() => v.visit(this))
  override def accept_simple[T](m: ASTMapping[T]): T = handle_standard(() => m.map(this))
  override def accept_simple[T](m: TypeMapping[T]): T = handle_standard(() => m.map(this))


  override def equals(o: Any) = o match {
    case ot:ObligationType => ot.sort.equals(this.sort)
    case _ => false
  }

  override def debugTreeChildrenFields: Iterable[String] = Seq("args")
  override def debugTreePropertyFields: Iterable[String] = Seq()
}
