public class List {

    private Object[] values;

    public List() {
        this.values = new Object[0];
    }

    /*@
        context Perm(values, read);
        context values != null;
        context (\forall* int i; i >= 0 && i < values.length; Perm(values[i], write));
        ensures values.length == \old(values.length) + 1;
        ensures (\forall int i; i >= 0 && i < \old(values.length); values[i] == \old(values[i]));
        ensures values[values.length - 1] == o;
     */
    public void add(Object o) {
        Object[] newValues = new Object[values.length + 1];
        /*@
            loop_invariant Perm(values, read);
            loop_invariant values != null;
            loop_invariant (\forall* int i; i >= 0 && i < values.length; Perm(values[i], read));
            loop_invariant (\forall* int i; i >= 0 && i < newValues.length; Perm(newValues[i], write));
            loop_invariant (\forall int j; j >= 0 && j < i; newValues[j] == values[j]);
         */
        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i];
        }

        newValues[values.length] = o;
        values = newValues;
    }
}