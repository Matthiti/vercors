package vct.col.rewrite;

import vct.col.ast.expr.MethodInvokation;
import vct.col.ast.expr.NameExpression;
import vct.col.ast.generic.ASTNode;
import vct.col.ast.stmt.decl.ASTSpecial;
import vct.col.ast.stmt.decl.ProgramUnit;
import vct.col.ast.type.ASTReserved;

public class JavaMonitorEncoder extends AbstractRewriter {

  public JavaMonitorEncoder(ProgramUnit source) {
    super(source);
  }

  @Override
  public void visit(MethodInvokation m) {
    // If the method has arguments, then it cannot be a monitor method.
    if (m.getArgs().length > 0) {
      super.visit(m);
      return;
    }
    switch (m.method) {
      case "wait":
        result = new ASTSpecial(ASTSpecial.Kind.Wait, getObject(m));
        break;
      case "notify":
        result = new ASTSpecial(ASTSpecial.Kind.Notify, getObject(m));
        break;
      case "notifyAll":
        result = new ASTSpecial(ASTSpecial.Kind.NotifyAll, getObject(m));
        break;
      default:
        super.visit(m);
    }
  }

  private ASTNode getObject(MethodInvokation m) {
    return m.object != null ? m.object : new NameExpression(ASTReserved.This);
  }
}
