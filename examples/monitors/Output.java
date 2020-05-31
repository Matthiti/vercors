public class java_DOT_lang_DOT_Object{
  /*constructor*/ constructor_java_DOT_lang_DOT_Object_java_DOT_lang_DOT_Object(EncodedGlobalVariables<> globals);

  /*constructor*/ internal_java_DOT_lang_DOT_Object_java_DOT_lang_DOT_Object(EncodedGlobalVariables<> globals);

}
public class Barrier  extends java_DOT_lang_DOT_Object
{
  private int field_Barrier_n;
  /*@
    requires (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ] , write ));
    requires (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ].waitLevel , write ));
    requires (\forall int i;i >= 0 && i < \length( obs );obs [ i ].waitLevel >= 0);
    requires n > 0;
    ensures Perm( Wt , write );
    ensures Perm( Ot , write );
    ensures Wt == 0;
    ensures Ot == 0;
    ensures (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ] , write ));
    ensures (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ].waitLevel , write ));
    ensures (\forall int i;i >= 0 && i < \length( obs );obs [ i ].waitLevel >= 0);
    ensures Perm( this.field_Barrier_n , write );
  @*/
  /*constructor*/ constructor_Barrier_Barrier__Integer(cell<Obligation<>>[] obs,EncodedGlobalVariables<> globals,int n);

  /*@
    requires (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ] , write ));
    requires (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ].waitLevel , write ));
    requires (\forall int i;i >= 0 && i < \length( obs );obs [ i ].waitLevel >= 0);
    requires n > 0;
    ensures Perm( Wt , write );
    ensures Perm( Ot , write );
    ensures Wt == 0;
    ensures Ot == 0;
    ensures (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ] , write ));
    ensures (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ].waitLevel , write ));
    ensures (\forall int i;i >= 0 && i < \length( obs );obs [ i ].waitLevel >= 0);
    ensures Perm( this.field_Barrier_n , write );
  @*/
  /*constructor*/ internal_Barrier_Barrier__Integer(cell<Obligation<>>[] obs,EncodedGlobalVariables<> globals,int n){
    this.field_Barrier_n=n;
    Wt=0;
    Ot=0;
  }

  /*@
    requires Perm( Wt , write );
    requires Perm( Ot , write );
    requires Wt >= 0;
    requires Ot >= 0;
    requires Wt <= 0 || Ot > 0;
    requires (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ] , write ));
    requires (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ].waitLevel , write ));
    requires (\forall int i;i >= 0 && i < \length( obs );obs [ i ].waitLevel >= 0);
    requires Perm( this.field_Barrier_n , write );
    requires this.field_Barrier_n == 2;
    requires Wt == 0;
    requires Ot == 1;
    ensures Perm( Wt , write );
    ensures Perm( Ot , write );
    ensures Wt >= 0;
    ensures Ot >= 0;
    ensures Wt <= 0 || Ot > 0;
    ensures (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ] , write ));
    ensures (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ].waitLevel , write ));
    ensures (\forall int i;i >= 0 && i < \length( obs );obs [ i ].waitLevel >= 0);
    ensures Perm( this.field_Barrier_n , write );
  @*/
  void method_Barrier_waitForBarrier(cell<Obligation<>>[] obs,EncodedGlobalVariables<> globals);

  /*@
    requires Perm( Wt , write );
    requires Perm( Ot , write );
    requires Wt >= 0;
    requires Ot >= 0;
    requires Wt <= 0 || Ot > 0;
    requires (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ] , write ));
    requires (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ].waitLevel , write ));
    requires (\forall int i;i >= 0 && i < \length( obs );obs [ i ].waitLevel >= 0);
    requires Perm( this.field_Barrier_n , write );
    requires this.field_Barrier_n == 2;
    requires Wt == 0;
    requires Ot == 1;
    ensures Perm( Wt , write );
    ensures Perm( Ot , write );
    ensures Wt >= 0;
    ensures Ot >= 0;
    ensures Wt <= 0 || Ot > 0;
    ensures (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ] , write ));
    ensures (\forall* int i;i >= 0 && i < \length( obs );Perm( obs [ i ].waitLevel , write ));
    ensures (\forall int i;i >= 0 && i < \length( obs );obs [ i ].waitLevel >= 0);
    ensures Perm( this.field_Barrier_n , write );
  @*/
  void internal_Barrier_waitForBarrier(cell<Obligation<>>[] obs,EncodedGlobalVariables<> globals){
    this.field_Barrier_n=this.field_Barrier_n - 1;
    if (this.field_Barrier_n == 0) {
      this.Wt=0;
      {
        assert Wt - 1 <= 0 || Ot > 0;
        this.Ot=this.Ot - 1;
      }
    } else {
      {
        assert Wt - 1 <= 0 || Ot > 0;
        this.Ot=this.Ot - 1;
      }
      {
        assert this.Wt + 1 <= 0 || this.Ot > 0;
        this.Wt=this.Wt + 1;
      }
    }
  }

  int Wt;
  int Ot;
}
public class EncodedGlobalVariables{
}
public class Obligation{
  java_DOT_lang_DOT_Object<> object;
  boolean isLock;
  int waitLevel;
  /*constructor*/ Obligation(java_DOT_lang_DOT_Object<> object,boolean isLock,int waitLevel){
    this.object=object;
    this.isLock=isLock;
    this.waitLevel=waitLevel;
  }

}