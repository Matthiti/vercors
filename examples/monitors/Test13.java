/**
 * This program should be verified successfully.
 *
 * Last checked: 15-06-2020
 */
public class Test13 {

  //@ requires Perm(\Ot(o), write);
  //@ requires Perm(\Wt(o), read);
  //@ requires \Ot(o) >= 0;
  //@ requires \Wt(o) == 0;
  public void main(O o) {
    //@ charge_ob o;
    //@ discharge_ob o;
  }

  public void test() {
    O o = new O();
    main(o);
  }
}

class O {
}