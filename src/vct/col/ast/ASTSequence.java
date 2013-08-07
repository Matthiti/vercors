package vct.col.ast;

public interface ASTSequence<T extends ASTSequence<T>> extends Iterable<ASTNode> {

  public T add(ASTNode item);
  
  public int size();
  
  public ASTNode get(int i);
  
}
