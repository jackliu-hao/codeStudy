/*    */ package io.undertow.servlet.attribute;
/*    */ 
/*    */ import io.undertow.attribute.ExchangeAttribute;
/*    */ import io.undertow.attribute.ExchangeAttributeBuilder;
/*    */ import io.undertow.attribute.ReadOnlyAttributeException;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.http.HttpServletRequest;
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
/*    */ public class ServletRequestedSessionIdFromCookieAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REQUESTED_SESSION_ID_FROM_COOKIE = "%{REQUESTED_SESSION_ID_FROM_COOKIE}";
/* 39 */   public static final ServletRequestedSessionIdFromCookieAttribute INSTANCE = new ServletRequestedSessionIdFromCookieAttribute();
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 43 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 44 */     if (context != null) {
/* 45 */       ServletRequest req = context.getServletRequest();
/* 46 */       if (req instanceof HttpServletRequest) {
/* 47 */         return Boolean.toString(((HttpServletRequest)req).isRequestedSessionIdFromCookie());
/*    */       }
/*    */     } 
/* 50 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 55 */     throw new ReadOnlyAttributeException("Requested session ID from cookie", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 60 */     return "%{REQUESTED_SESSION_ID_FROM_COOKIE}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 67 */       return "Requested Session ID from cookie attribute";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 72 */       if (token.equals("%{REQUESTED_SESSION_ID_FROM_COOKIE}")) {
/* 73 */         return ServletRequestedSessionIdFromCookieAttribute.INSTANCE;
/*    */       }
/* 75 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 80 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletRequestedSessionIdFromCookieAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */