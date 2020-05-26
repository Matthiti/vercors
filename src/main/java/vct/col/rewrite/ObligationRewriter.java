package vct.col.rewrite;

import hre.ast.MessageOrigin;
import vct.col.ast.expr.MethodInvokation;
import vct.col.ast.stmt.decl.*;

import java.util.ArrayList;

public class ObligationRewriter extends AbstractRewriter {

  private static final String OBLIGATION_VARIABLE = "obsVars";

  private ASTClass obligationClass;

  public ObligationRewriter(ProgramUnit source) {
    super(source);
  }

  @Override
  public void visit(ASTSpecial s) {
    if (s.kind != ASTSpecial.Kind.Wait) {
      super.visit(s);
      return;
    }
  }

  @Override
  public void visit(Method m) {
    ArrayList<DeclarationStatement> args = new ArrayList<>();
    args.add(create.field_decl(OBLIGATION_VARIABLE, create.class_type(obligationClass.name())));

    for (DeclarationStatement arg : m.getArgs()) {
      args.add(rewrite(arg));
    }

    result = create.method_kind(m.getKind(), rewrite(m.getReturnType()), rewrite(m.getContract()), m.getName(), args, m.usesVarArgs(), rewrite(m.getBody()));
  }

  @Override
  public void visit(MethodInvokation s) {
    MethodInvokation res = create.invokation(
        rewrite(s.object),
        s.dispatch,
        s.method,
        rewrite(create.local_name(OBLIGATION_VARIABLE), s.getArgs())
    );

    res.set_before(rewrite(s.get_before()));
    res.set_after(rewrite(s.get_after()));
    result = res;
  }

  @Override
  public ProgramUnit rewriteAll() {
    obligationClass = create.ast_class("ObligationVariables", ASTClass.ClassKind.Plain, null, null, null);
    obligationClass.setOrigin(new MessageOrigin("Generated code: Obligation variables"));
//    obligationClass.add(create.field_decl("Wt", create.primitive_type(PrimitiveSort.Integer), create.constant(42)));
    ProgramUnit res = super.rewriteOrdered();
    res.add(obligationClass);
    return res;
  }
}
