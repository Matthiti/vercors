package vct.col.rewrite;

import hre.ast.MessageOrigin;
import vct.col.ast.expr.*;
import vct.col.ast.generic.ASTNode;
import vct.col.ast.stmt.decl.*;
import vct.col.ast.type.ASTReserved;
import vct.col.ast.type.PrimitiveSort;
import vct.col.ast.util.ContractBuilder;

public class ObligationRewriter extends AbstractRewriter {

  private static final String OBLIGATION_VARIABLE = "obsVars";
  private static final String OBLIGATIONS_PER_THREAD = "obs";

  private ASTClass obligationVariablesClass;
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
            create.special(
                ASTSpecial.Kind.Assert,
                enoughObs(
                    create.expression(
                        StandardOperator.Minus,
                        Wt(),
                        create.constant(1)
                    ),
                    Ot()
                )
            ),
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
        object != null ? create.dereference(
            object,
            OBLIGATION_VARIABLE
        ) : create.field_name(OBLIGATION_VARIABLE),
        "Wt"
    );
  }

  private Dereference Wt() {
    return Wt(null);
  }

  private Dereference Ot(ASTNode object) {
    return create.dereference(
        object != null ? create.dereference(
            object,
            OBLIGATION_VARIABLE
        ) : create.field_name(OBLIGATION_VARIABLE),
        "Ot"
    );
  }

  private Dereference Ot() {
    return Ot(null);
  }

  private OperatorExpression enoughObs(ASTNode Wt, ASTNode Ot) {
    return create.expression(
        StandardOperator.Or,
        create.expression(
            StandardOperator.LTE,
            Wt,
            constant(0)
        ),
        create.expression(
            StandardOperator.GT,
            Ot,
            constant(0)
        )
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
  public void visit(OperatorExpression e) {
    switch (e.operator()) {
      case Obligations:
        break;
      case CondVarOf:
        break;
      case LockOf:
        break;
      default:
        super.visit(e);
    }
  }

  @Override
  public void visit(NameExpression e) {
    if (e.isReserved(ASTReserved.Wt)) {
      result = Wt();
    } else if (e.isReserved(ASTReserved.Ot)) {
      result = Ot();
    } else {
      super.visit(e);
    }
  }

  @Override
  public void visit(Method m) {
    // Don't add args, pre- and postconditions to fake methods and static methods
    if (m.getOrigin() instanceof MessageOrigin || m.isStatic()) {
      super.visit(m);
      return;
    }

    DeclarationStatement[] args = new DeclarationStatement[m.getArity() + 1];
    args[0] = create.field_decl(
        OBLIGATIONS_PER_THREAD,
        create.primitive_type(
            PrimitiveSort.Array,
            create.primitive_type(PrimitiveSort.Cell, create.class_type(obligationClass.getName()))
        )
    );

    for (int i = 1; i <= m.getArity(); i++) {
      args[i] = rewrite(m.getArgs()[i - 1]);
    }

    // Don't add pre- and postconditions to constructors
    // TODO: only check if Constructor, or only allow Method.Kind.Plain?
    if (m.getKind().equals(Method.Kind.Constructor)) {
      result = create.method_kind(m.getKind(), m.getReturnType(), rewrite(m.getContract()), m.getName(), args, rewrite(m.getBody()));
      return;
    }

    ContractBuilder cb = new ContractBuilder();
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
            Ot(),
            create.reserved_name(ASTReserved.FullPerm)
        )
    );

    cb.requires(
        create.expression(
            StandardOperator.Perm,
            Wt(),
            create.reserved_name(ASTReserved.FullPerm)
        )
    );

    cb.requires(
        create.expression(
            StandardOperator.GTE,
            Ot(),
            create.constant(0)
        )
    );

    cb.requires(
        create.expression(
            StandardOperator.GTE,
            Wt(),
            create.constant(0)
        )
    );

    cb.requires(
        enoughObs(Wt(), Ot())
    );

    ASTNode obsPerm = create.starall(
        create.expression(
            StandardOperator.And,
            create.expression(
                StandardOperator.GTE,
                create.local_name("i"),
                create.constant(0)
            ),
            create.expression(
                StandardOperator.LT,
                create.local_name("i"),
                create.expression(
                    StandardOperator.Length,
                    create.local_name(OBLIGATIONS_PER_THREAD)
                )
            )
        ),
        create.expression(
            StandardOperator.Perm,
            create.expression(
                StandardOperator.Subscript,
                create.local_name(OBLIGATIONS_PER_THREAD),
                create.local_name("i")
            ),
            create.reserved_name(ASTReserved.FullPerm)
        ),
        create.field_decl("i", create.primitive_type(PrimitiveSort.Integer))
    );

    ASTNode waitLevelPerm = create.starall(
        create.expression(
            StandardOperator.And,
            create.expression(
                StandardOperator.GTE,
                create.local_name("i"),
                create.constant(0)
            ),
            create.expression(
                StandardOperator.LT,
                create.local_name("i"),
                create.expression(
                    StandardOperator.Length,
                    create.local_name(OBLIGATIONS_PER_THREAD)
                )
            )
        ),
        create.expression(
            StandardOperator.Perm,
            create.dereference(
                create.expression(
                    StandardOperator.Subscript,
                    create.local_name(OBLIGATIONS_PER_THREAD),
                    create.local_name("i")
                ),
                "waitLevel"
            ),
            create.reserved_name(ASTReserved.FullPerm)
        ),
        create.field_decl("i", create.primitive_type(PrimitiveSort.Integer))
    );

    ASTNode waitLevelsCheck = create.forall(
        create.expression(
            StandardOperator.And,
            create.expression(
                StandardOperator.GTE,
                create.local_name("i"),
                create.constant(0)
            ),
            create.expression(
                StandardOperator.LT,
                create.local_name("i"),
                create.expression(
                    StandardOperator.Length,
                    create.local_name(OBLIGATIONS_PER_THREAD)
                )
            )
        ),
        create.expression(
            StandardOperator.GTE,
            create.dereference(
                create.expression(
                    StandardOperator.Subscript,
                    create.local_name(OBLIGATIONS_PER_THREAD),
                    create.local_name("i")
                ),
                "waitLevel"
            ),
            create.constant(0)
        ),
        create.field_decl("i", create.primitive_type(PrimitiveSort.Integer))
    );

//    cb.requires(
//        create.expression(
//            StandardOperator.NEQ,
//            create.local_name("obs"),
//            create.reserved_name(ASTReserved.Null)
//        )
//    );
    cb.requires(obsPerm);
    cb.requires(waitLevelPerm);
    cb.requires(waitLevelsCheck);

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
            Ot(),
            create.reserved_name(ASTReserved.FullPerm)
        )
    );

    cb.ensures(
        create.expression(
            StandardOperator.Perm,
            Wt(),
            create.reserved_name(ASTReserved.FullPerm)
        )
    );

    cb.ensures(
        create.expression(
            StandardOperator.GTE,
            Ot(),
            create.constant(0)
        )
    );

    cb.ensures(
        create.expression(
            StandardOperator.GTE,
            Wt(),
            create.constant(0)
        )
    );

    cb.ensures(
        enoughObs(Wt(), Ot())
    );

    cb.ensures(obsPerm);
    cb.ensures(waitLevelPerm);
    cb.ensures(waitLevelsCheck);

    rewrite(m.getContract(), cb);
    result = create.method_kind(m.getKind(), m.getReturnType(), cb.getContract(), m.getName(), args, rewrite(m.getBody()));
  }

  @Override
  public void visit(MethodInvokation s) {
    Method m = s.getDefinition();
    if (m == null) {
      super.visit(s);
      return;
    }

    MethodInvokation res = create.invokation(
        m.getKind() == Method.Kind.Constructor ? rewrite(s.dispatch) : rewrite(s.object),
        rewrite(s.dispatch),
        s.method,
        rewrite(create.local_name(OBLIGATIONS_PER_THREAD), s.getArgs())
    );

    res.set_before(rewrite(s.get_before()));
    res.set_after(rewrite(s.get_after()));
    result = res;
  }

  @Override
  public void visit(ASTClass c) {
    // Check if this is a generated class. If so, don't inject the obligation variables.
    if (c.getOrigin() instanceof MessageOrigin) {
      super.visit(c);
      return;
    }

    ASTNode newObsVars = create.expression(StandardOperator.New, create.class_type(obligationVariablesClass.name()));
    c.add(create.field_decl(
        OBLIGATION_VARIABLE,
        create.class_type(obligationVariablesClass.name()),
        newObsVars
    ));
    super.visit(c);
  }

  @Override
  public ProgramUnit rewriteAll() {
    create.setOrigin(new MessageOrigin("Generated code: Obligation variables"));
    obligationVariablesClass = create.ast_class("ObligationVariables", ASTClass.ClassKind.Plain, null, null, null);
    createObligationClass();
    ProgramUnit res = super.rewriteOrdered();

    for (DeclarationStatement var : variables()) {
      obligationVariablesClass.add(var);
    }

    res.add(obligationVariablesClass);
    res.add(obligationClass);
    return res;
  }

  private DeclarationStatement[] variables() {
    return new DeclarationStatement[] {
        create.field_decl("Wt", create.primitive_type(PrimitiveSort.Integer), create.constant(0)),
        create.field_decl("Ot", create.primitive_type(PrimitiveSort.Integer), create.constant(0))
    };
  }

  private void createObligationClass() {
    obligationClass = create.ast_class("Obligation", ASTClass.ClassKind.Plain, null, null, null);
    obligationClass.add(
        create.field_decl("object", create.class_type("java_DOT_lang_DOT_Object"))
    );
    obligationClass.add(
        create.field_decl("isLock", create.primitive_type(PrimitiveSort.Boolean))
    );
    obligationClass.add(
        create.field_decl("waitLevel", create.primitive_type(PrimitiveSort.Integer))
    );

    obligationClass.add(
        create.method_kind(
            Method.Kind.Constructor,
            create.primitive_type(PrimitiveSort.Void),
            null,
            obligationClass.getName(),
            new DeclarationStatement[] {
                create.field_decl("object", create.class_type("java_DOT_lang_DOT_Object")),
                create.field_decl("isLock", create.primitive_type(PrimitiveSort.Boolean)),
                create.field_decl("waitLevel", create.primitive_type(PrimitiveSort.Integer))
            },
            create.block(
                create.assignment(
                    create.dereference(
                        create.reserved_name(ASTReserved.This),
                        "object"
                    ),
                    create.local_name("object")
                ),
                create.assignment(
                    create.dereference(
                        create.reserved_name(ASTReserved.This),
                        "isLock"
                    ),
                    create.local_name("isLock")
                ),
                create.assignment(
                    create.dereference(
                        create.reserved_name(ASTReserved.This),
                        "waitLevel"
                    ),
                    create.local_name("waitLevel")
                )
            )
        )
    );
  }
}
