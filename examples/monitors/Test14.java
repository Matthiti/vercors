/**
 * This program should be verified successfully.
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