/*    */ package io.undertow.servlet.attribute;
/*    */ 
/*    */ import io.undertow.attribute.ExchangeAttribute;
/*    */ import io.undertow.attribute.ExchangeAttributeBuilder;
/*    */ import io.undertow.attribute.ReadOnlyAttributeException;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpSession;
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
/*    */ public class ServletSessionIdAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String SESSION_ID_SHORT = "%S";
/*    */   public static final String SESSION_ID = "%{SESSION_ID}";
/* 41 */   public static final ServletSessionIdAttribute INSTANCE = new ServletSessionIdAttribute();
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 45 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 46 */     if (context != null) {
/* 47 */       ServletRequest req = context.getServletRequest();
/* 48 */       if (req instanceof HttpServletRequest) {
/* 49 */         HttpSession session = ((HttpServletRequest)req).getSession(false);
/* 50 */         if (session != null) {
/* 51 */           return session.getId();
/*    */         }
/*    */       } 
/*    */     } 
/* 55 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 60 */     throw new ReadOnlyAttributeException("Session ID", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 65 */     return "%{SESSION_ID}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 72 */       return "Session ID attribute";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 77 */       if (token.equals("%{SESSION_ID}") || token.equals("%S")) {
/* 78 */         return ServletSessionIdAttribute.INSTANCE;
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletSessionIdAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */