/*    */ package io.undertow.servlet.attribute;
/*    */ 
/*    */ import io.undertow.attribute.ExchangeAttribute;
/*    */ import io.undertow.attribute.ExchangeAttributeBuilder;
/*    */ import io.undertow.attribute.ReadOnlyAttributeException;
/*    */ import io.undertow.attribute.RequestURLAttribute;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
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
/*    */ public class ServletRequestURLAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REQUEST_URL_SHORT = "%U";
/*    */   public static final String REQUEST_URL = "%{REQUEST_URL}";
/* 40 */   public static final ExchangeAttribute INSTANCE = new ServletRequestURLAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 48 */     ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 49 */     if (src == null) {
/* 50 */       return RequestURLAttribute.INSTANCE.readAttribute(exchange);
/*    */     }
/* 52 */     String uri = (String)src.getServletRequest().getAttribute("javax.servlet.forward.request_uri");
/* 53 */     if (uri != null) {
/* 54 */       return uri;
/*    */     }
/* 56 */     uri = (String)src.getServletRequest().getAttribute("javax.servlet.error.request_uri");
/* 57 */     if (uri != null) {
/* 58 */       return uri;
/*    */     }
/* 60 */     return RequestURLAttribute.INSTANCE.readAttribute(exchange);
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 65 */     RequestURLAttribute.INSTANCE.writeAttribute(exchange, newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 70 */     return "%{REQUEST_URL}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 77 */       return "Request URL";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 82 */       if (token.equals("%{REQUEST_URL}") || token.equals("%U")) {
/* 83 */         return ServletRequestURLAttribute.INSTANCE;
/*    */       }
/* 85 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 90 */       return 1;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletRequestURLAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */