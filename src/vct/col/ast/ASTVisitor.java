// -*- tab-width:2 ; indent-tabs-mode:nil -*-
package vct.col.ast;

public interface ASTVisitor<T> {
  
  public T getResult();
  
  public void pre_visit(ASTNode n);
  
  public void post_visit(ASTNode n);
  
  public void visit(StandardProcedure p);
  
  public void visit(ConstantExpression e);
  
  public void visit(OperatorExpression e);
  
  public void visit(NameExpression e);
  
  public void visit(ClassType t);
  
  public void visit(FunctionType t);
  
  public void visit(PrimitiveType t);
  
  public void visit(RecordType t);
  
  public void visit(MethodInvokation e);

  public void visit(BlockStatement s);
  
  public void visit(IfStatement s);
  
  public void visit(ReturnStatement s);
  
  public void visit(AssignmentStatement s);

  public void visit(DeclarationStatement s);
  
  public void visit(LoopStatement s);
  
  public void visit(Method m);

  public void visit(ASTClass c);

  public void visit(ASTWith astWith);

  public void visit(BindingExpression e);

  public void visit(Dereference e);

  public void visit(Lemma lemma);

  public void visit(ParallelBarrier parallelBarrier);

  public void visit(ParallelBlock parallelBlock);

  public void visit(Contract contract);

  public void visit(ASTSpecial special);

  public void visit(VariableDeclaration variableDeclaration);

}


