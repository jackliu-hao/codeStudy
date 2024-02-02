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
/*    */ public class ThreadNameAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String THREAD_NAME_SHORT = "%I";
/*    */   public static final String THREAD_NAME = "%{THREAD_NAME}";
/* 33 */   public static final ExchangeAttribute INSTANCE = new ThreadNameAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 41 */     return Thread.currentThread().getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 46 */     throw new ReadOnlyAttributeException("Thread name", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 51 */     return "%{THREAD_NAME}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 58 */       return "Thread name";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 63 */       if (token.equals("%{THREAD_NAME}") || token.equals("%I")) {
/* 64 */         return ThreadNameAttribute.INSTANCE;
/*    */       }
/* 66 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 71 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\ThreadNameAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */