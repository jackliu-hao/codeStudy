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
/*    */ public class FalsePredicate
/*    */   implements Predicate
/*    */ {
/* 28 */   public static final FalsePredicate INSTANCE = new FalsePredicate();
/*    */   
/*    */   public static FalsePredicate instance() {
/* 31 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 36 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 41 */     return "false";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\FalsePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */