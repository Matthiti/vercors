package vct.col.rewrite;

import hre.ast.MessageOrigin;
import vct.col.ast.expr.*;
import vct.col.ast.generic.ASTNode;
import vct.col.ast.stmt.composite.BlockStatement;
import vct.col.ast.stmt.decl.*;
import vct.col.ast.type.ASTReserved;
import vct.col.ast.type.PrimitiveSort;
import vct.col.ast.util.ContractBuilder;

import java.util.HashMap;

public class ObligationRewriter extends AbstractRewriter {

  private static final String OBLIGATIONS_PER_THREAD = "obs";
  private static final String OBLIGATION_CLASS_CONSTRUCTOR = "constructor_Obligation_Obligation__java_DOT_lang_DOT_Object__Boolean";
  private static final String WT = "Wt";
  private static final String OT = "Ot";
  private static final String WAIT_LEVEL_LOCK = "wait_level_lock";
  private static final String WAIT_LEVEL_COND = "wait_level_cond";

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
                    create.expression(
                        StandardOperator.Plus,
                        Wt(s.getArg(0)),
                        create.constant(1)
                    ),
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
        String ob = randomIdentifier();
        result = create.block(
            incrementOt(s.getArg(0)),
            create.field_decl(
                ob,
                create.class_type(obligationClass.name()),
                create.invokation(
                    create.class_type(obligationClass.name()),
                    create.class_type(obligationClass.name()),
                    OBLIGATION_CLASS_CONSTRUCTOR,
                    s.getArg(0),
                    create.constant(false)
                )
            ),
            create.assignment(
                create.local_name(OBLIGATIONS_PER_THREAD),
                create.expression(
                    StandardOperator.Plus,
                    create.local_name(OBLIGATIONS_PER_THREAD),
                    create.struct_value(
                        create.primitive_type(PrimitiveSort.Bag, create.class_type(obligationClass.getName())),
                        new HashMap<>(),
                        create.local_name(ob)
                    )
                )
            )
        );
        break;
      case ChargeObs:
        ob = randomIdentifier();
        result = create.block(
            addToOt(s.getArg(0), s.getArg(1)),
            create.field_decl(
                ob,
                create.class_type(obligationClass.name()),
                create.invokation(
                    create.class_type(obligationClass.name()),
                    create.class_type(obligationClass.name()),
                    OBLIGATION_CLASS_CONSTRUCTOR,
                    s.getArg(0),
                    create.constant(false)
                )
            ),
            create.for_loop(
                create.field_decl("_i", create.primitive_type(PrimitiveSort.Integer), create.constant(0)),
                create.expression(
                    StandardOperator.LT,
                    create.local_name("_i"),
                    s.getArg(1)
                ),
                create.expression(
                    StandardOperator.AddAssign,
                    create.local_name("_i"),
                    create.constant(1)
                ),
                create.assignment(
                    create.local_name(OBLIGATIONS_PER_THREAD),
                    create.expression(
                        StandardOperator.Plus,
                        create.local_name(OBLIGATIONS_PER_THREAD),
                        create.struct_value(
                            create.primitive_type(PrimitiveSort.Bag, create.class_type(obligationClass.getName())),
                            new HashMap<>(),
                            create.local_name(ob)
                        )
                    )
                )
            )
        );
        break;
      case DischargeOb:
        ob = randomIdentifier();
        result = create.block(
            // TODO: add assertion that it is in obs
            create.field_decl(
                ob,
                create.class_type(obligationClass.name()),
                create.invokation(
                    create.class_type(obligationClass.name()),
                    create.class_type(obligationClass.name()),
                    OBLIGATION_CLASS_CONSTRUCTOR,
                    s.getArg(0),
                    create.constant(false)
                )
            ),
            create.special(
                ASTSpecial.Kind.Assert,
                create.expression(
                    StandardOperator.GT,
                    create.expression(
                        StandardOperator.Member,
                        create.local_name(ob),
                        create.local_name(OBLIGATIONS_PER_THREAD)
                    ),
                    create.constant(0)
                )
            ),
            create.special(
                ASTSpecial.Kind.Assert,
                enoughObs(
                    Wt(s.getArg(0)),
                    create.expression(
                        StandardOperator.Minus,
                        Ot(s.getArg(0)),
                        create.constant(1)
                    )
                )
            ),
            decrementOt(s.getArg(0))
            // TODO: remove also from obs
        );
        break;
      case DischargeObs:
        result = create.block(
            // TODO: add assertion that is is in obs
            create.special(
                ASTSpecial.Kind.Assert,
                enoughObs(
                    Wt(s.getArg(0)),
                    create.expression(
                        StandardOperator.Minus,
                        Ot(s.getArg(0)),
                        s.getArg(1)
                    )
                )
            ),
            removeFromOt(s.getArg(0), s.getArg(1))
            // TODO: remove also from obs
        );
        break;
      case TransferOb:
        // TODO: implement
        break;
      case TransferObs:
        // TODO: implement
        break;
      case SetWaitLevel:
        result = create.assignment(
            waitLevel((OperatorExpression) s.getArg(0)),
            s.getArg(1)
        );
        break;
      default:
        super.visit(s);
    }
  }

  private ASTNode Wt(ASTNode object) {
    return object != null
        ? create.dereference(
            object,
            WT
        )
        : create.field_name(WT);
  }

  private ASTNode Wt() {
    return Wt(null);
  }

  private ASTNode Ot(ASTNode object) {
    return object != null
        ? create.dereference(
            object,
            OT
        )
        : create.field_name(OT);
  }

  private ASTNode Ot() {
    return Ot(null);
  }

  private OperatorExpression enoughObs(ASTNode Wt, ASTNode Ot) {
    return create.expression(
        StandardOperator.Or,
        create.expression(
            StandardOperator.EQ,
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
    return addToWt(v, create.constant(1));
  }

  private ASTNode addToWt(ASTNode v, ASTNode n) {
    return create.assignment(
        Wt(v),
        create.expression(
            StandardOperator.Plus,
            Wt(v),
            n
        )
    );
  }

  private ASTNode decrementWt(ASTNode v) {
    return removeFromWt(v, create.constant(1));
  }

  private ASTNode removeFromWt(ASTNode v, ASTNode n) {
    return create.assignment(
        Wt(v),
        create.expression(
            StandardOperator.Minus,
            Wt(v),
            n
        )
    );
  }

  private ASTNode incrementOt(ASTNode v) {
    return addToOt(v, create.constant(1));
  }

  private ASTNode addToOt(ASTNode v, ASTNode n) {
    return create.assignment(
        Ot(v),
        create.expression(
            StandardOperator.Plus,
            Ot(v),
            n
        )
    );
  }

  private ASTNode decrementOt(ASTNode v) {
    return removeFromOt(v, create.constant(1));
  }

  private ASTNode removeFromOt(ASTNode v, ASTNode n) {
    return create.assignment(
        Ot(v),
        create.expression(
            StandardOperator.Minus,
            Ot(v),
            n
        )
    );
  }

  private ASTNode waitLevel(OperatorExpression expr) {
    switch (expr.operator()) {
      case LockOf:
        return waitLevelLock(expr.first());
      case CondVarOf:
        return waitLevelCond(expr.first());
      default:
        return null;
        // TODO: throw error. This state should never be reached.
    }
  }

  private ASTNode waitLevelLock() {
    return waitLevelLock(null);
  }

  private ASTNode waitLevelLock(ASTNode object) {
    return object != null
        ? create.dereference(
            object,
            WAIT_LEVEL_LOCK
        )
        : create.field_name(WAIT_LEVEL_LOCK);
  }

  private ASTNode waitLevelCond() {
    return waitLevelCond(null);
  }

  private ASTNode waitLevelCond(ASTNode object) {
    return object != null
        ? create.dereference(
          object,
          WAIT_LEVEL_COND
        )
        : create.field_name(WAIT_LEVEL_COND);
  }

  private ASTNode obligation(OperatorExpression expr) {
    switch (expr.operator()) {
      case LockOf:
        // TODO;
      case CondVarOf:
        // TODO;
      default:
        return null;
        // TODO: throw error. This state should never be reached.
    }
  }

  private ASTNode obligationLock() {
    return obligationLock(null);
  }

  private ASTNode obligationLock(ASTNode object) {
    return object != null
        ? create.dereference(
            object,
            OBLIGATION_LOCK
        )
        : create.field_name(OBLIGATION_LOCK);
  }

  private ASTNode obligationCond() {
    return obligationCond(null);
  }

  private ASTNode obligationCond(ASTNode object) {
    return object != null
        ? create.dereference(
            object,
            OBLIGATION_COND
        )
        : create.field_name(OBLIGATION_COND);
  }

  // TODO: find better way?
  private String randomIdentifier() {
    String allowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder builder = new StringBuilder("ob_");

    for (int i = 0; i < 10; i++) {
      int index = (int) (allowed.length() * Math.random());
      builder.append(allowed.charAt(index));
    }

    return builder.toString();
  }

  @Override
  public void visit(OperatorExpression e) {
    switch (e.operator()) {
      case CondVarOf:
        break;
      case LockOf:
        break;
      case Wt:
        result = Wt(e.first());
        break;
      case Ot:
        result = Ot(e.first());
        break;
      case WaitLevel:
        result = waitLevel((OperatorExpression) e.first());
        break;
      case HasOb:
        // TODO: implement
        result = create.constant(true);
        break;
      case HasObs:
        // TODO: implement
        result = create.constant(true);
        break;
      default:
        super.visit(e);
    }
  }

  @Override
  public void visit(NameExpression e) {
    if (e.isReserved(ASTReserved.NoObs)) {
      // TODO: implement
      result = create.constant(true);
    } else {
      super.visit(e);
    }
  }

  @Override
  public void visit(Method m) {
    DeclarationStatement[] args = new DeclarationStatement[m.getArity() + 1];
    args[0] = create.field_decl(
        OBLIGATIONS_PER_THREAD,
        create.primitive_type(
            PrimitiveSort.Bag,
            create.class_type(obligationClass.getName())
        )
    );

    for (int i = 1; i <= m.getArity(); i++) {
      args[i] = rewrite(m.getArgs()[i - 1]);
    }

    ContractBuilder cb = new ContractBuilder();
    if (m.getKind().equals(Method.Kind.Constructor)) {
      ASTNode body = m.getBody();
      if (body != null) {
        BlockStatement block = getBodyBlock(body);

        block.addStatement(
            create.assignment(
                Wt(),
                create.constant(0)
            )
        );
        block.addStatement(
            create.assignment(
                Ot(),
                create.constant(0)
            )
        );
      }

      cb.ensures(
          create.expression(
              StandardOperator.Perm,
              Wt(),
              create.reserved_name(ASTReserved.FullPerm)
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
              waitLevelLock(),
              create.reserved_name(ASTReserved.FullPerm)
          )
      );

      cb.ensures(
          create.expression(
              StandardOperator.Perm,
              waitLevelCond(),
              create.reserved_name(ASTReserved.FullPerm)
          )
      );

      cb.ensures(
          create.expression(
              StandardOperator.EQ,
              Wt(),
              create.constant(0)
          )
      );

      cb.ensures(
          create.expression(
              StandardOperator.EQ,
              Ot(),
              create.constant(0)
          )
      );
    }

    if (!m.getKind().equals(Method.Kind.Predicate)) {
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
                      StandardOperator.Size,
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

//      cb.requires(obsPerm);
//      cb.ensures(obsPerm);
    }

    ASTNode body = rewrite(m.getBody());
    if (m.isSynchronized() && body != null) {
      BlockStatement block = getBodyBlock(body);
      block.prepend(
          create.special(
              ASTSpecial.Kind.Inhale,
              create.expression(
                  StandardOperator.Perm,
                  Ot(),
                  create.reserved_name(ASTReserved.FullPerm)
              )
          )
      );
      block.prepend(
          create.special(
              ASTSpecial.Kind.Inhale,
              create.expression(
                  StandardOperator.Perm,
                  Wt(),
                  create.reserved_name(ASTReserved.FullPerm)
              )
          )
      );

      block.append(
          create.special(
              ASTSpecial.Kind.Exhale,
              create.expression(
                  StandardOperator.Perm,
                  Ot(),
                  create.reserved_name(ASTReserved.FullPerm)
              )
          )
      );

      block.append(
          create.special(
              ASTSpecial.Kind.Exhale,
              create.expression(
                  StandardOperator.Perm,
                  Wt(),
                  create.reserved_name(ASTReserved.FullPerm)
              )
          )
      );

      body = block;
    }

    rewrite(m.getContract(), cb);
    result = create.method_kind(m.getKind(), m.getReturnType(), cb.getContract(), m.getName(), args, body);
  }

  private BlockStatement getBodyBlock(ASTNode body) {
    if (body instanceof BlockStatement) {
      return (BlockStatement) body;
    }
    return create.block(body);
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
    // Ignore all non-standard generated classes
    // TODO: more specific prefix?
    if (c.getOrigin() instanceof MessageOrigin && !c.getName().startsWith("java")) {
      super.visit(c);
      return;
    }
    // Add Wt and Ot to each class
    c.add(create.field_decl(WT, create.primitive_type(PrimitiveSort.Integer)));
    c.add(create.field_decl(OT, create.primitive_type(PrimitiveSort.Integer)));

    // Add the wait levels of the lock and the condition variable to each class
    c.add(create.field_decl(WAIT_LEVEL_LOCK, create.primitive_type(PrimitiveSort.Integer)));
    c.add(create.field_decl(WAIT_LEVEL_COND, create.primitive_type(PrimitiveSort.Integer)));

    super.visit(c);
  }

  @Override
  public ProgramUnit rewriteAll() {
    create.setOrigin(new MessageOrigin("Generated code: Obligation variables"));
    createObligationClass();
    ProgramUnit res = super.rewriteOrdered();

    res.add(obligationClass);
    return res;
  }

  private void createObligationClass() {
    obligationClass = create.ast_class("Obligation", ASTClass.ClassKind.Plain, null, null, null);
    obligationClass.add(
        create.field_decl("object", create.class_type("java_DOT_lang_DOT_Object"))
    );
    obligationClass.add(
        create.field_decl("isLock", create.primitive_type(PrimitiveSort.Boolean))
    );

    // Constructor
    obligationClass.add(
        create.method_kind(
            Method.Kind.Constructor,
            create.primitive_type(PrimitiveSort.Void),
            null,
            OBLIGATION_CLASS_CONSTRUCTOR,
            new DeclarationStatement[] {
                create.field_decl("object", create.class_type("java_DOT_lang_DOT_Object")),
                create.field_decl("isLock", create.primitive_type(PrimitiveSort.Boolean)),
            },
            null
        )
    );

    // Internal constructor
    obligationClass.add(
        create.method_kind(
            Method.Kind.Constructor,
            create.primitive_type(PrimitiveSort.Void),
            null,
            "internal_Obligation_Obligation__java_DOT_lang_DOT_Object__Boolean",
            new DeclarationStatement[] {
                create.field_decl("object", create.class_type("java_DOT_lang_DOT_Object")),
                create.field_decl("isLock", create.primitive_type(PrimitiveSort.Boolean)),
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
                )
            )
        )
    );
  }
}
