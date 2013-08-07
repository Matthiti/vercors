// -*- tab-width:2 ; indent-tabs-mode:nil -*-
package vct.boogie;

import vct.util.Syntax;
import static vct.col.ast.StandardOperator.*;
import static vct.col.ast.ASTReserved.*;

/**
 * Create Syntax objects for Boogie and Chalice.
 * 
 * @author Stefan Blom
 */
public class BoogieSyntax {
  private static Syntax boogie;
  private static Syntax chalice;
  
  private static void setCommon(Syntax syntax){
    syntax.addPrefix(Not,"!",130);
    syntax.addPrefix(UMinus,"-",130);
    syntax.addPrefix(UPlus,"+",130);
    syntax.addLeftFix(Mult,"*",120);
    syntax.addLeftFix(Div,"/",120);
    syntax.addLeftFix(Mod,"%",120);
    syntax.addLeftFix(Plus,"+",110);
    syntax.addLeftFix(Minus,"-",110);
    syntax.addInfix(LT,"<",90);
    syntax.addInfix(LTE,"<=",90);
    syntax.addInfix(GT,">",90);
    syntax.addInfix(GTE,">=",90);
    syntax.addInfix(EQ,"==",80);
    syntax.addInfix(NEQ,"!=",80);
    syntax.addLeftFix(Star,"&&",40); // 40??
    syntax.addLeftFix(And,"&&",40); // 40??
    syntax.addLeftFix(Or,"||",40); // 30??
    syntax.addLeftFix(Implies, "==>", 30); 
    syntax.addLeftFix(IFF, "<==>", 30);
    syntax.addRightFix(Assign,"=",10);
    syntax.addFunction(Old,"old");
    syntax.addOperator(ITE,20,"","?",":","");
    syntax.addOperator(Size,0,"|","|");
    
    syntax.addReserved(Result,"result");
  }

  public static synchronized Syntax getBoogie(){
    if(boogie==null){
      boogie=new Syntax();
      setCommon(boogie);
    }
    return boogie;
  }
  
  public static synchronized Syntax getChalice(){
    if(chalice==null){
      chalice=new Syntax();
      setCommon(chalice);
      chalice.addFunction(Perm,"acc");
      chalice.addOperator(Nil,0,"nil<",">");
      chalice.addOperator(Cons,0,"([","]++(","))");
      chalice.addOperator(Subscript,0,"(",")[","]");
      chalice.addLeftFix(Append,"++",100);
      
      chalice.addReserved(This,"this");
      chalice.addReserved(Null,"null");
      chalice.addReserved(Any,"*");
    }
    return chalice;
  }

}


/*
Java Operators 	Precedence
14 postfix 	expr++ expr--
13 unary 	++expr --expr +expr -expr ~ !
12 multiplicative 	* / %
11 additive 	+ -
10 shift 	<< >> >>>
 9 relational 	< > <= >= instanceof
 8 equality 	== !=
 7 bitwise AND 	&
 6 bitwise exclusive OR 	^
 5 bitwise inclusive OR 	|
 4 logical AND 	&&
 3 logical OR 	||
 2 ternary 	? :
 1 assignment 	= += -= *= /= %= &= ^= |= <<= >>= >>>=
*/

