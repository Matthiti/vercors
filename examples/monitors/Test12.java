/**
 * This program should be verified successfully.
 *
 * Last checked: 15-06-2020
 */
public class Test12 {

  //@ context Perm(\Ot(this), read);
  //@ context \Ot(this) >= 0;
  public synchronized void main() {
    //@ charge_obs this, 3;
    //@ discharge_ob this;
    //@ discharge_ob this;
    //@ discharge_ob this;
  }
}