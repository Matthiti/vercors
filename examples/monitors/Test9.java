/**
 * This program should be verified successfully.
 *
 * Last checked: 15-06-2020
 */
public class Test9 {

  private O o;

  public Test9(O o) {
    this.o = o;
  }

  //@ context Perm(o, read);
  //@ context Perm(\Ot(o), write);
  public void main() {
    //@ charge_ob o;
  }
}

class O {
}