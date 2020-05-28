public class MonitorBarrier {

  private int n;

  /*@
    ensures Perm(this.n, write);
   */
  public MonitorBarrier(int n) {
    this.n = n;
  }

  /*@
    context Perm(n, write);
   */
  public synchronized void waitForBarrier() {
    n--;
    if (n == 0) {
//      notifyAll();
    } else {
      //@ loop_invariant Perm(n, write);
      while (n > 0) {
//        wait();
      }
    }
  }

  /*@
    context Perm(n, write);
   */
  public void main() {
    //@ discharge_ob this;
    this.waitForBarrier();
  }
}