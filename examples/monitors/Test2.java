public class Test
{

  public Test() {
    testVars = new TestVars();
  }

  /*@
    requires Perm( testVars, read );
    requires Perm( testVars.Ot , 1 );
    requires Perm( testVars.Wt , 1 );
    ensures Perm( testVars, read );
    ensures Perm( testVars.Ot , 1 );
    ensures Perm( testVars.Wt , 1 );
  @*/
  public void main(){
    {
      this.testVars.enoughObs(this.testVars.Wt - 1,this.testVars.Ot);
      this.testVars.Ot=this.testVars.Ot - 1;
    }
  }

  TestVars testVars;
}
public class TestVars{
  int Wt=0;
  int Ot=0;

  public void enoughObs(int Wt, int Ot) {

  }

}