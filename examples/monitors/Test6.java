/**
 * This program should be verified successfully.
 *
 * Last checked: 15-06-2020
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