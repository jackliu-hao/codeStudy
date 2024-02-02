/*    */ package io.undertow.servlet.attribute;
/*    */ 
/*    */ import io.undertow.attribute.ExchangeAttribute;
/*    */ import io.undertow.attribute.ExchangeAttributeBuilder;
/*    */ import io.undertow.attribute.ReadOnlyAttributeException;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
/*    */ import javax.servlet.ServletRequest;
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
/*    */ public class ServletRequestCharacterEncodingAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REQUEST_CHARACTER_ENCODING = "%{REQUEST_CHARACTER_ENCODING}";
/* 38 */   public static final ServletRequestCharacterEncodingAttribute INSTANCE = new ServletRequestCharacterEncodingAttribute();
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 42 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 43 */     if (context != null) {
/* 44 */       ServletRequest req = context.getServletRequest();
/* 45 */       return req.getCharacterEncoding();
/*    */     } 
/* 47 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 52 */     throw new ReadOnlyAttributeException("Request Character Encoding", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 57 */     return "%{REQUEST_CHARACTER_ENCODING}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 64 */       return "Request Character Encoding";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 69 */       if (token.equals("%{REQUEST_CHARACTER_ENCODING}")) {
/* 70 */         return ServletRequestCharacterEncodingAttribute.INSTANCE;
/*    */       }
/* 72 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 77 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletRequestCharacterEncodingAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */