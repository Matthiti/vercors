package vct.col.rewrite;

import hre.ast.MessageOrigin;
import vct.col.ast.expr.MethodInvokation;
import vct.col.ast.expr.StandardOperator;
import vct.col.ast.stmt.decl.*;
import vct.col.ast.stmt.terminal.ReturnStatement;
import vct.col.ast.type.PrimitiveSort;
import vct.col.ast.util.ContractBuilder;

import java.util.ArrayList;

public class ObligationRewriter extends AbstractRewriter {

  private static final String OBLIGATION_VARIABLE = "obsVars";
  private static final String ENOUGH_OBS_METHOD = "enoughObs";

  private ASTClass obligationClass;

  public ObligationRewriter(ProgramUnit source) {
    super(source);
  }

  @Override
  public void visit(ASTSpecial s) {
    switch (s.kind) {
      case Wait:
        int n = 0;
        break;
      case Notify:
        break;
      case NotifyAll:
        break;
      default:
        super.visit(s);
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
    create.setOrigin(new MessageOrigin("Generated code: Obligation variables"));
    obligationClass = create.ast_class("ObligationVariables", ASTClass.ClassKind.Plain, null, null, null);
    ProgramUnit res = super.rewriteOrdered();

    for (DeclarationStatement var : variables()) {
      obligationClass.add(var);
    }
    obligationClass.add_dynamic(enoughObsMethod());

    res.add(obligationClass);
    return res;
  }

  private Method enoughObsMethod() {
    ContractBuilder cb = new ContractBuilder();
    cb.requires(create.expression(StandardOperator.GTE, create.local_name("Wt_v"), create.constant(0)));
    cb.requires(create.expression(StandardOperator.GTE, create.local_name("Ot_v"), create.constant(0)));

    DeclarationStatement[] args = new DeclarationStatement[] {
        create.field_decl("Wt_v", create.primitive_type(PrimitiveSort.Integer)),
        create.field_decl("Ot_v", create.primitive_type(PrimitiveSort.Integer))
    };

    ReturnStatement stat = create.return_statement(
      create.expression(StandardOperator.Or,
          create.expression(StandardOperator.EQ, create.local_name("Wt_v"), create.constant(0)),
          create.expression(StandardOperator.GT, create.local_name("Ot_v"), create.constant(0))
      )
    );

    return create.method_decl(
        create.primitive_type(PrimitiveSort.Boolean),
        cb.getContract(),
        ENOUGH_OBS_METHOD,
        args,
        create.block(stat)
    );
  }

  private DeclarationStatement[] variables() {
    return new DeclarationStatement[] {
        create.field_decl("Wt", create.primitive_type(PrimitiveSort.Integer), create.constant(42))
    };
  }
}
