/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.StatusCodes;
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
/*    */ public class ResponseReasonPhraseAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String RESPONSE_REASON_PHRASE = "%{RESPONSE_REASON_PHRASE}";
/* 34 */   public static final ExchangeAttribute INSTANCE = new ResponseReasonPhraseAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 42 */     return StatusCodes.getReason(exchange.getStatusCode());
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 47 */     exchange.setReasonPhrase(newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 52 */     return "%{RESPONSE_REASON_PHRASE}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 59 */       return "Response reason phrase";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 64 */       if (token.equals("%{RESPONSE_REASON_PHRASE}")) {
/* 65 */         return ResponseReasonPhraseAttribute.INSTANCE;
/*    */       }
/* 67 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 72 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\ResponseReasonPhraseAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */