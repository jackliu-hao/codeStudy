/*    */ package oshi.util.tuples;
/*    */ 
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public class Pair<A, B>
/*    */ {
/*    */   private final A a;
/*    */   private final B b;
/*    */   
/*    */   public Pair(A a, B b) {
/* 51 */     this.a = a;
/* 52 */     this.b = b;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final A getA() {
/* 61 */     return this.a;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final B getB() {
/* 70 */     return this.b;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\tuples\Pair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */