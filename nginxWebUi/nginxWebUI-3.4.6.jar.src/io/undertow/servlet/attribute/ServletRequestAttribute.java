/*    */ package io.undertow.servlet.attribute;
/*    */ 
/*    */ import io.undertow.attribute.ExchangeAttribute;
/*    */ import io.undertow.attribute.ExchangeAttributeBuilder;
/*    */ import io.undertow.attribute.ReadOnlyAttributeException;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ public class ServletRequestAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   private final String attributeName;
/*    */   
/*    */   public ServletRequestAttribute(String attributeName) {
/* 40 */     this.attributeName = attributeName;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 45 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 46 */     if (context != null) {
/* 47 */       Object result = context.getServletRequest().getAttribute(this.attributeName);
/* 48 */       if (result != null) {
/* 49 */         return result.toString();
/*    */       }
/*    */     } else {
/* 52 */       Map<String, String> attrs = (Map<String, String>)exchange.getAttachment(HttpServerExchange.REQUEST_ATTRIBUTES);
/* 53 */       if (attrs != null) {
/* 54 */         return attrs.get(this.attributeName);
/*    */       }
/*    */     } 
/* 57 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 62 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 63 */     if (context != null) {
/* 64 */       context.getServletRequest().setAttribute(this.attributeName, newValue);
/*    */     } else {
/* 66 */       Map<String, String> attrs = (Map<String, String>)exchange.getAttachment(HttpServerExchange.REQUEST_ATTRIBUTES);
/* 67 */       if (attrs == null) {
/* 68 */         exchange.putAttachment(HttpServerExchange.REQUEST_ATTRIBUTES, attrs = new HashMap<>());
/*    */       }
/* 70 */       attrs.put(this.attributeName, newValue);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return "%{r," + this.attributeName + "}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 83 */       return "Servlet request attribute";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 88 */       if (token.startsWith("%{r,") && token.endsWith("}")) {
/* 89 */         String attributeName = token.substring(4, token.length() - 1);
/* 90 */         return new ServletRequestAttribute(attributeName);
/*    */       } 
/* 92 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 97 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletRequestAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */