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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public class Triplet<A, B, C>
/*    */ {
/*    */   private final A a;
/*    */   private final B b;
/*    */   private final C c;
/*    */   
/*    */   public Triplet(A a, B b, C c) {
/* 56 */     this.a = a;
/* 57 */     this.b = b;
/* 58 */     this.c = c;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final A getA() {
/* 67 */     return this.a;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final B getB() {
/* 76 */     return this.b;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final C getC() {
/* 85 */     return this.c;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\tuples\Triplet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */