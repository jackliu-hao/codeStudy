/*    */ package io.undertow.attribute;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class QueryStringAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String QUERY_STRING_SHORT = "%q";
/*    */   public static final String QUERY_STRING = "%{QUERY_STRING}";
/*    */   public static final String BARE_QUERY_STRING = "%{BARE_QUERY_STRING}";
/* 34 */   public static final ExchangeAttribute INSTANCE = new QueryStringAttribute(true);
/* 35 */   public static final ExchangeAttribute BARE_INSTANCE = new QueryStringAttribute(false);
/*    */   
/*    */   private final boolean includeQuestionMark;
/*    */   
/*    */   private QueryStringAttribute(boolean includeQuestionMark) {
/* 40 */     this.includeQuestionMark = includeQuestionMark;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 45 */     String qs = exchange.getQueryString();
/* 46 */     if (qs.isEmpty() || !this.includeQuestionMark) {
/* 47 */       return qs;
/*    */     }
/* 49 */     return '?' + qs;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 54 */     exchange.setQueryString(newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 59 */     if (this.includeQuestionMark) {
/* 60 */       return "%{QUERY_STRING}";
/*    */     }
/* 62 */     return "%{BARE_QUERY_STRING}";
/*    */   }
/*    */ 
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 70 */       return "Query String";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 75 */       if (token.equals("%{QUERY_STRING}") || token.equals("%q"))
/* 76 */         return QueryStringAttribute.INSTANCE; 
/* 77 */       if (token.equals("%{BARE_QUERY_STRING}")) {
/* 78 */         return QueryStringAttribute.BARE_INSTANCE;
/*    */       }
/* 80 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 85 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\QueryStringAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */