/**
 * This program should be verified successfully.
 */
public class Test {

  //@ requires \Wt == 0;
  public void main() {
    //@ charge_ob this;
    discharge();
  }

  //@ requires \Ot >= 1;
  //@ requires \Wt == 0;
  public void discharge() {
    //@ discharge_ob this;
  }
}