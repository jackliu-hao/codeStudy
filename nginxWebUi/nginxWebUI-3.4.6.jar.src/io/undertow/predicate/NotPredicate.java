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
/*    */ public class NotPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private final Predicate predicate;
/*    */   
/*    */   NotPredicate(Predicate predicate) {
/* 31 */     this.predicate = predicate;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 36 */     return !this.predicate.resolve(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 41 */     return " not " + this.predicate.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\NotPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */