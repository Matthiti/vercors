/**
 * This program should be verified successfully.
 *
 * Last checked: 15-06-2020
 */
public class Test {

  /*@
    requires Perm(\Wt(this), read);
    requires Perm(\Ot(this), write);
    requires \Wt(this) == 0;
    requires \Ot(this) >= 0;
   */
  public void main() {
    //@ charge_ob this;
    discharge();
  }

  /*@
    requires Perm(\Wt(this), read);
    requires Perm(\Ot(this), write);
    requires \Wt(this) == 0;
    requires \Ot(this) >= 1;
   */
  public void discharge() {
    //@ discharge_ob this;
  }
}