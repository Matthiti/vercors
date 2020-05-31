/**
 * This program should be verified successfully.
 */
public class Barrier {

  int n;

  //@ ensures Perm(this.n, write);
  public Barrier(int n) {
    this.n = n;
  }
}

class Main {

  public void main() {
    Barrier barrier = new Barrier(3);
    barrier.n = 4;
    //@ charge_ob barrier;
  }
}