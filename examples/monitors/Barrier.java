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
    requires \Ot > 0;
    requires n <= \Ot;
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
    int n = 3;
    Barrier barrier = new Barrier(n);
    //@ charge_ob barrier;
    //@ charge_ob barrier;
    //@ charge_ob barrier;
    BarrierThread[] threads = new BarrierThread[n];
    for (int i = 0; i < n; i++) {
      threads[i] = new BarrierThread(barrier);
      threads[i].start();
    }

    for (int j = 0; j < n; j++) {
      threads[j].join();
    }
  }
}

class BarrierThread {

  private final Barrier barrier;

  public BarrierThread(Barrier barrier) {
    this.barrier = barrier;
  }

  public void run() {
    for (int i = 0; i < 10; i++) {
      // Do stuff
    }
    barrier.waitForBarrier();
    for (int j = 10; j< 20; j++) {
      // Do other stuff
    }
  }

  public void start() {
    run();
  }

  public void join() {

  }
}