/*    */ package io.undertow.servlet.attribute;
/*    */ 
/*    */ import io.undertow.attribute.ExchangeAttribute;
/*    */ import io.undertow.attribute.ExchangeAttributeBuilder;
/*    */ import io.undertow.attribute.ReadOnlyAttributeException;
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
/*    */ public class ServletNameAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String SERVLET_NAME = "%{SERVLET_NAME}";
/* 36 */   public static final ExchangeAttribute INSTANCE = new ServletNameAttribute();
/*    */ 
/*    */ 
/*    */   
/*    */   public static final String NAME = "Servlet Name";
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 45 */     ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 46 */     return src.getCurrentServlet().getManagedServlet().getServletInfo().getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 51 */     throw new ReadOnlyAttributeException("Servlet Name", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 56 */     return "%{SERVLET_NAME}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 63 */       return "Servlet Name";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 68 */       return token.equals("%{SERVLET_NAME}") ? ServletNameAttribute.INSTANCE : null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 73 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletNameAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */