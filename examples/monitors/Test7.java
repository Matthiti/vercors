public class Barrier {

  private int n;

  /*@
    ensures Perm(this.n, write);
   */
  public Barrier(int n) {
    this.n = n;
  }

  /*@
    context Perm(n, write);
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

class Main {

  public void main() {
    int n = 1;
    Barrier barrier = new Barrier(n);
    //@ charge_ob barrier;
    BarrierThread t = new BarrierThread(barrier);
  }
}

class BarrierThread {

  private final Barrier barrier;

  //@ ensures Perm(this.barrier, write);
  public BarrierThread(Barrier barrier) {
    this.barrier = barrier;
  }

  //@ requires Perm(barrier, write);
  //@ requires \Ot(barrier) > 0;
  //@ ensures Perm(barrier, write);
  public void run() {
    for (int i = 0; i < 10; i++) {
      // Do stuff
    }
    barrier.waitForBarrier();
    for (int j = 10; j< 20; j++) {
      // Do other stuff
    }
  }

  //@ requires Perm(barrier, write);
  //@ ensures Perm(barrier, write);
  public void start() {
    run();
  }

  public void join() {

  }
}