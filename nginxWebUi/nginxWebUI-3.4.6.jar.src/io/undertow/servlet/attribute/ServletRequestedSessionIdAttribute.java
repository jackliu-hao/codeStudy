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
/*    */ public class ServletRequestedSessionIdAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REQUESTED_SESSION_ID = "%{REQUESTED_SESSION_ID}";
/* 39 */   public static final ServletRequestedSessionIdAttribute INSTANCE = new ServletRequestedSessionIdAttribute();
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 43 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 44 */     if (context != null) {
/* 45 */       ServletRequest req = context.getServletRequest();
/* 46 */       if (req instanceof HttpServletRequest) {
/* 47 */         return ((HttpServletRequest)req).getRequestedSessionId();
/*    */       }
/*    */     } 
/* 50 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 55 */     throw new ReadOnlyAttributeException("Session ID", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 60 */     return "%{REQUESTED_SESSION_ID}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 67 */       return "Requested Session ID attribute";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 72 */       if (token.equals("%{REQUESTED_SESSION_ID}")) {
/* 73 */         return ServletRequestedSessionIdAttribute.INSTANCE;
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletRequestedSessionIdAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */