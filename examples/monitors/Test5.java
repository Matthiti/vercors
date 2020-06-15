/**
 * This program should be verified successfully.
 *
 * Last checked: 15-06-2020
 */
public class Barrier {

  private int n;

  /*@
    requires n > 0;
    ensures Perm(this.n, write);
   */
  public Barrier(int n) {
    this.n = n;
  }

  /*@
    context Perm(n, write);
    context Perm(\Ot(this), read);
    requires n > 0;
    requires \Ot(this) > 0;
    requires n <= \Ot(this);
   */
  public synchronized void waitForBarrier() {
    n--;
    if (n == 0) {
      notifyAll();
      //@ discharge_ob this;
    } else {
      //@ discharge_ob this;
      wait();
    }
  }
}