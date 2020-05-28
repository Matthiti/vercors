package vct.col.rewrite;

import hre.ast.MessageOrigin;
import vct.col.ast.expr.Dereference;
import vct.col.ast.expr.MethodInvokation;
import vct.col.ast.expr.OperatorExpression;
import vct.col.ast.expr.StandardOperator;
import vct.col.ast.generic.ASTNode;
import vct.col.ast.stmt.decl.*;
import vct.col.ast.type.ASTReserved;
import vct.col.ast.type.PrimitiveSort;
import vct.col.ast.util.ContractBuilder;

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
        result = create.block(
            create.special(
                ASTSpecial.Kind.Assert,
                enoughObs(
                    s.getArg(0),
                    create.expression(StandardOperator.Plus, Wt(s.getArg(0)), create.constant(1)),
                    Ot(s.getArg(0)))
            ),
            incrementWt(s.getArg(0))
        );
        break;
      case Notify:
        result = create.ifthenelse(
            create.expression(StandardOperator.GT, Wt(s.getArg(0)), constant(0)),
            decrementWt(s.getArg(0))
        );
        break;
      case NotifyAll:
        result = create.assignment(
            Wt(s.getArg(0)),
            create.constant(0)
        );
        break;
      case ChargeOb:
        result = create.block(
            incrementOt(s.getArg(0))
            // TODO: add also to obs
        );
        break;
      case DischargeOb:
        result = create.block(
            // TODO: add assertion that it is in obs
//            create.special(
//                ASTSpecial.Kind.Assert,
//                enoughObs(
//                    s.getArg(0),
//                    create.expression(StandardOperator.Minus, Wt(s.getArg(0)), create.constant(1)),
//                    Ot(s.getArg(0)))
//            ),
            decrementOt(s.getArg(0))
            // TODO: remove also from obs
        );
        break;
      default:
        super.visit(s);
    }
  }

  private Dereference Wt(ASTNode object) {
    return create.dereference(
        create.dereference(
            object,
            OBLIGATION_VARIABLE
        ),
        "Wt"
    );
  }

  private Dereference Ot(ASTNode object) {
    return create.dereference(
        create.dereference(
            object,
            OBLIGATION_VARIABLE
        ),
        "Ot"
    );
  }

  private MethodInvokation enoughObs(ASTNode object, ASTNode Wt, ASTNode Ot) {
    return create.invokation(
        create.dereference(
            object,
            OBLIGATION_VARIABLE
        ),
        null,
        ENOUGH_OBS_METHOD,
        Wt, Ot
    );
  }

  private ASTNode incrementWt(ASTNode v) {
    return create.assignment(
        Wt(v),
        create.expression(
            StandardOperator.Plus,
            Wt(v),
            create.constant(1)
        )
    );
  }

  private ASTNode decrementWt(ASTNode v) {
    return create.assignment(
        Wt(v),
        create.expression(
            StandardOperator.Minus,
            Wt(v),
            create.constant(1)
        )
    );
  }

  private ASTNode incrementOt(ASTNode v) {
    return create.assignment(
        Ot(v),
        create.expression(
            StandardOperator.Plus,
            Ot(v),
            create.constant(1)
        )
    );
  }

  private ASTNode decrementOt(ASTNode v) {
    return create.assignment(
        Ot(v),
        create.expression(
            StandardOperator.Minus,
            Ot(v),
            create.constant(1)
        )
    );
  }

  @Override
  public void visit(Method m) {
    // Don't add pre- and postconditions to fake and static methods
    // TODO: only check if Constructor, or only allow Method.Kind.Plain?
    if (m.getOrigin() instanceof MessageOrigin || m.isStatic() || m.getKind().equals(Method.Kind.Constructor)) {
      super.visit(m);
      return;
    }

    ContractBuilder cb = new ContractBuilder();
    cb.ensures(
        create.expression(
            StandardOperator.Perm,
            create.field_name("obsVars"),
            create.reserved_name(ASTReserved.ReadPerm)
        )
    );

    cb.ensures(
        create.expression(
            StandardOperator.Perm,
            create.dereference(
                create.field_name("obsVars"),
                "Ot"
            ),
            create.reserved_name(ASTReserved.FullPerm)
        )
    );

    cb.ensures(
        create.expression(
            StandardOperator.Perm,
            create.dereference(
                create.field_name("obsVars"),
                "Wt"
            ),
            create.reserved_name(ASTReserved.FullPerm)
        )
    );

    if (m.getKind().equals(Method.Kind.Plain)) {
      cb.requires(
          create.expression(
              StandardOperator.Perm,
              create.field_name("obsVars"),
              create.reserved_name(ASTReserved.ReadPerm)
          )
      );

      cb.requires(
          create.expression(
              StandardOperator.Perm,
              create.dereference(
                  create.field_name("obsVars"),
                  "Ot"
              ),
              create.reserved_name(ASTReserved.FullPerm)
          )
      );

      cb.requires(
          create.expression(
              StandardOperator.Perm,
              create.dereference(
                  create.field_name("obsVars"),
                  "Wt"
              ),
              create.reserved_name(ASTReserved.FullPerm)
          )
      );
    }

    rewrite(m.getContract(), cb);
    result = create.method_kind(m.getKind(), m.getReturnType(), cb.getContract(), m.getName(), rewrite(m.getArgs()), rewrite(m.getBody()));
  }

  @Override
  public void visit(ASTClass c) {
    // Check if this is a generated class. If so, don't inject the obligation variables.
    if (c.getOrigin() instanceof MessageOrigin) {
      super.visit(c);
      return;
    }

    ASTNode newObsVars = create.expression(StandardOperator.New, create.class_type(obligationClass.name()));
    c.add(create.field_decl(
        OBLIGATION_VARIABLE,
        create.class_type(obligationClass.name()),
        newObsVars
    ));
    super.visit(c);
  }

  @Override
  public ProgramUnit rewriteAll() {
    create.setOrigin(new MessageOrigin("Generated code: Obligation variables"));
    obligationClass = create.ast_class("ObligationVariables", ASTClass.ClassKind.Plain, null, null, null);
    ProgramUnit res = super.rewriteOrdered();

    for (DeclarationStatement var : variables()) {
      obligationClass.add(var);
    }
    obligationClass.add_dynamic(enoughObsPredicate());

    res.add(obligationClass);
    return res;
  }

  private Method enoughObsPredicate() {
    DeclarationStatement[] args = new DeclarationStatement[] {
        create.field_decl("Wt", create.primitive_type(PrimitiveSort.Integer)),
        create.field_decl("Ot", create.primitive_type(PrimitiveSort.Integer))
    };

    OperatorExpression expr = create.expression(
        StandardOperator.Or,
        create.expression(StandardOperator.EQ, create.local_name("Wt"), create.constant(0)),
        create.expression(StandardOperator.GT, create.local_name("Ot"), create.constant(0))
    );

    return create.method_kind(
        Method.Kind.Predicate,
        create.primitive_type(PrimitiveSort.Resource),
        null,
        ENOUGH_OBS_METHOD,
        args,
        expr
    );
  }

  private DeclarationStatement[] variables() {
    return new DeclarationStatement[] {
        create.field_decl("Wt", create.primitive_type(PrimitiveSort.Integer), create.constant(0)),
        create.field_decl("Ot", create.primitive_type(PrimitiveSort.Integer), create.constant(0))
    };
  }
}
