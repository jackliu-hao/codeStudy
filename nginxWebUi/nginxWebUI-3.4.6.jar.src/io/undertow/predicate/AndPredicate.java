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
/*    */ class AndPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private final Predicate[] predicates;
/*    */   
/*    */   AndPredicate(Predicate... predicates) {
/* 31 */     this.predicates = predicates;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 36 */     for (Predicate predicate : this.predicates) {
/* 37 */       if (!predicate.resolve(value)) {
/* 38 */         return false;
/*    */       }
/*    */     } 
/* 41 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 46 */     StringBuilder result = new StringBuilder();
/* 47 */     for (Predicate predicate : this.predicates) {
/* 48 */       if (result.length() > 0) {
/* 49 */         result.append(" and ");
/*    */       }
/* 51 */       result.append(predicate.toString());
/*    */     } 
/* 53 */     return result.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\AndPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */