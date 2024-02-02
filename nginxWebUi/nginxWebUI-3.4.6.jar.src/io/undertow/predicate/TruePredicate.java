/*    */ package io.undertow.predicate;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
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
/*    */ public class TruePredicate
/*    */   implements Predicate
/*    */ {
/* 28 */   public static final TruePredicate INSTANCE = new TruePredicate();
/*    */   
/*    */   public static TruePredicate instance() {
/* 31 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 36 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 41 */     return "true";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\TruePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */