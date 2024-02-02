/*    */ package io.undertow.servlet.attribute;
/*    */ 
/*    */ import io.undertow.attribute.ExchangeAttribute;
/*    */ import io.undertow.attribute.ExchangeAttributeBuilder;
/*    */ import io.undertow.attribute.ReadOnlyAttributeException;
/*    */ import io.undertow.attribute.RequestLineAttribute;
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
/*    */ public class ServletRequestLineAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REQUEST_LINE_SHORT = "%r";
/*    */   public static final String REQUEST_LINE = "%{REQUEST_LINE}";
/* 40 */   public static final ExchangeAttribute INSTANCE = new ServletRequestLineAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 48 */     ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 49 */     if (src == null) {
/* 50 */       return RequestLineAttribute.INSTANCE.readAttribute(exchange);
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 55 */     StringBuilder sb = (new StringBuilder()).append(exchange.getRequestMethod().toString()).append(' ').append(ServletRequestURLAttribute.INSTANCE.readAttribute(exchange));
/* 56 */     String query = (String)src.getServletRequest().getAttribute("javax.servlet.forward.query_string");
/* 57 */     if (query != null && !query.isEmpty()) {
/* 58 */       sb.append('?');
/* 59 */       sb.append(query);
/* 60 */     } else if (!exchange.getQueryString().isEmpty()) {
/* 61 */       sb.append('?');
/* 62 */       sb.append(exchange.getQueryString());
/*    */     } 
/* 64 */     sb.append(' ')
/* 65 */       .append(exchange.getProtocol().toString()).toString();
/* 66 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 71 */     throw new ReadOnlyAttributeException("Request line", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return "%{REQUEST_LINE}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 83 */       return "Request line";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 88 */       if (token.equals("%{REQUEST_LINE}") || token.equals("%r")) {
/* 89 */         return ServletRequestLineAttribute.INSTANCE;
/*    */       }
/* 91 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 96 */       return 1;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletRequestLineAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */