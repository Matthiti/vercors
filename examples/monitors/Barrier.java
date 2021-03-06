public class Barrier {

  private int n;

  /*@
    requires n > 0;
    ensures Perm(this.n, read);
    ensures this.n == n;
   */
  public Barrier(int n) {
    this.n = n;
  }

  /*@
    context Perm(n, read);
    context Perm(\Ot(this), read);
    context Perm(\wait_level(\lock(this)), read);
    context Perm(\wait_level(\cond(this)), read);
    context \wait_level(\lock(this)) == 0;
    context \wait_level(\cond(this)) == 1;
    requires n > 0;
    requires \Ot(this) > 0;
    requires n <= \Ot(this);
    requires \has_ob(\cond(this));
    ensures !\has_ob(\cond(this));
    ensures n == \old(n) - 1;
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

  //@ context \no_obs;
  public void main() {
    Barrier barrier = new Barrier(3);
    //@ set_wait_level \lock(barrier), 0;
    //@ set_wait_level \cond(barrier), 1;
    //@ charge_obs barrier, 3;

    BarrierThread t1 = new BarrierThread(barrier);
    //@ transfer_ob barrier, t1.getId();
    BarrierThread t2 = new BarrierThread(barrier);
    //@ transfer_ob barrier, t2.getId();
    BarrierThread t3 = new BarrierThread(barrier);
    //@ transfer_ob barrier, t3.getId();
    t1.start();
    t2.start();
    t3.start();

    t1.join();
    t2.join();
    t3.join();
  }
}

class BarrierThread {

  private final Barrier barrier;

  /*@
    ensures Perm(this.barrier, read);
    ensures this.barrier == barrier;
   */
  public BarrierThread(Barrier barrier) {
    this.barrier = barrier;
  }

  /*@
    context Perm(barrier, read);
    context Perm(\Ot(barrier), read);
    context Perm(\wait_level(\lock(barrier)), read);
    context Perm(\wait_level(\cond(barrier)), read);
    context Perm(barrier.n, read);
    context \wait_level(\lock(barrier)) == 0;
    context \wait_level(\cond(barrier)) == 1;
    requires barrier.n > 0;
    requires \Ot(barrier) > 0;
    requires barrier.n <= \Ot(barrier);
    requires \has_ob(\cond(barrier));
    ensures  \no_obs;
    ensures barrier.n == \old(barrier.n) - 1;
   */
  public void start() {
    for (int i = 0; i < 10; i++) { }
    barrier.waitForBarrier();
    for (int j = 10; j < 20; j++) { }
  }

  public void join();

  //@ ensures \result == \current_thread;
  public int getId();
}
