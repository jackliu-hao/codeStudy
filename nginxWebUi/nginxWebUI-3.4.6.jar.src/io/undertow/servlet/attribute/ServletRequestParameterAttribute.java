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
/*    */ public class ServletRequestParameterAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   private final String attributeName;
/*    */   
/*    */   public ServletRequestParameterAttribute(String attributeName) {
/* 37 */     this.attributeName = attributeName;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 42 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 43 */     if (context != null) {
/* 44 */       Object result = context.getServletRequest().getParameter(this.attributeName);
/* 45 */       if (result != null) {
/* 46 */         return result.toString();
/*    */       }
/*    */     } 
/* 49 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 54 */     throw new ReadOnlyAttributeException();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 59 */     return "%{rp," + this.attributeName + "}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 66 */       return "Servlet request parameter";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 71 */       if (token.startsWith("%{rp,") && token.endsWith("}")) {
/* 72 */         String attributeName = token.substring(5, token.length() - 1);
/* 73 */         return new ServletRequestParameterAttribute(attributeName);
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletRequestParameterAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */