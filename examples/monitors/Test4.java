/**
 * Internal test, no idea if it works.
 */
class Test {

  //@ requires obs != null;
  //@ requires (\forall* int i;i >= 0 && i < obs.length;Perm( obs [ i ] , read ));
  //@ requires (\forall* int i; i >= 0 && i < obs.length; Perm( obs[i].a, write));
  //@ requires (\forall int i; i >= 0 && i < obs.length; obs[i].a >= 0);
  public void main(Obligation2[] obs) {

  }
}

class Obligation2 {
  int a;
}