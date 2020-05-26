public class MonitorBarrier {

  private int n;

  public MonitorBarrier(int n) {
    this.n = n;
  }

  public synchronized void waitForBarrier() {
    n--;
    if (n == 0) {
      notifyAll(); // Deadlock if this was notify()
    } else {
      while (n > 0) {
        wait();
      }
    }
  }

  public static void main(String[] args) {
    MonitorBarrier barrier = new MonitorBarrier(1);
    barrier.waitForBarrier();
  }
}