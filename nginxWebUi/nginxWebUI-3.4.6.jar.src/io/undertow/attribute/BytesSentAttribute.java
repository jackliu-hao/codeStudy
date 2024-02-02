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
/*    */ public class BytesSentAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String BYTES_SENT_SHORT_UPPER = "%B";
/*    */   public static final String BYTES_SENT_SHORT_LOWER = "%b";
/*    */   public static final String BYTES_SENT = "%{BYTES_SENT}";
/*    */   private final boolean dashIfZero;
/*    */   
/*    */   public BytesSentAttribute(boolean dashIfZero) {
/* 37 */     this.dashIfZero = dashIfZero;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 43 */     if (this.dashIfZero) {
/* 44 */       long bytesSent = exchange.getResponseBytesSent();
/* 45 */       return (bytesSent == 0L) ? "-" : Long.toString(bytesSent);
/*    */     } 
/* 47 */     return Long.toString(exchange.getResponseBytesSent());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 53 */     throw new ReadOnlyAttributeException("Bytes sent", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 58 */     return "%{BYTES_SENT}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 65 */       return "Bytes Sent";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 70 */       if (token.equals("%b")) {
/* 71 */         return new BytesSentAttribute(true);
/*    */       }
/* 73 */       if (token.equals("%{BYTES_SENT}") || token.equals("%B")) {
/* 74 */         return new BytesSentAttribute(false);
/*    */       }
/* 76 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 81 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\BytesSentAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */