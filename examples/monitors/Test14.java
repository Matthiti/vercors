/**
 * This program should be verified successfully.
 *
 * Last checked: 15-06-2020
 */
public class Test14 {

  //@ requires Perm(\wait_level(\lock(o)), read);
  //@ requires \wait_level(\lock(o)) == 0;
  public void main(O o) {

  }

  public void test() {
    O o = new O();
    //@ set_wait_level \lock(o), 0;
    main(o);
  }
}

class O {
}