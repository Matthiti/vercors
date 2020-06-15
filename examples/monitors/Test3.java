/**
 * Internal test, no idea if it works.
 */
public class Test
{

  int Wt = 0;
  int Ot = 0;

  public void enoughObs(int Wt, int Ot) {

  }

  /*@
    requires Perm( Ot , 1 );
    requires Perm( Wt , 1 );
    ensures Perm( Ot , 1 );
    ensures Perm( Wt , 1 );
  @*/
  public void main(){
    {
      this.enoughObs(this.Wt - 1,this.Ot);
      this.Ot=this.Ot - 1;
    }
  }
}
