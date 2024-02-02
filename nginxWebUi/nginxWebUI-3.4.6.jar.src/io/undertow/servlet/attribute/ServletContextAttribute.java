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
/*    */ public class ServletContextAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   private final String attributeName;
/*    */   
/*    */   public ServletContextAttribute(String attributeName) {
/* 37 */     this.attributeName = attributeName;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 42 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 43 */     if (context != null) {
/* 44 */       Object result = context.getCurrentServletContext().getAttribute(this.attributeName);
/* 45 */       if (result != null) {
/* 46 */         return result.toString();
/*    */       }
/*    */     } 
/* 49 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 54 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 55 */     if (context != null) {
/* 56 */       context.getCurrentServletContext().setAttribute(this.attributeName, newValue);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 62 */     return "%{sc," + this.attributeName + "}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 69 */       return "Servlet context attribute";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 74 */       if (token.startsWith("%{sc,") && token.endsWith("}")) {
/* 75 */         String attributeName = token.substring(5, token.length() - 1);
/* 76 */         return new ServletContextAttribute(attributeName);
/*    */       } 
/* 78 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 83 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletContextAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */