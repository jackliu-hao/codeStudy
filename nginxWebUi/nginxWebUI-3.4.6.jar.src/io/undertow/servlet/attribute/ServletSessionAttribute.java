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
/*    */ public class ServletSessionAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   private final String attributeName;
/*    */   
/*    */   public ServletSessionAttribute(String attributeName) {
/* 41 */     this.attributeName = attributeName;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 46 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 47 */     if (context != null) {
/* 48 */       ServletRequest req = context.getServletRequest();
/* 49 */       if (req instanceof HttpServletRequest) {
/* 50 */         HttpSession session = ((HttpServletRequest)req).getSession(false);
/* 51 */         if (session != null) {
/* 52 */           Object result = session.getAttribute(this.attributeName);
/* 53 */           if (result != null) {
/* 54 */             return result.toString();
/*    */           }
/*    */         } 
/*    */       } 
/*    */     } 
/* 59 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 64 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 65 */     if (context != null) {
/* 66 */       ServletRequest req = context.getServletRequest();
/* 67 */       if (req instanceof HttpServletRequest) {
/* 68 */         HttpSession session = ((HttpServletRequest)req).getSession(false);
/* 69 */         if (session != null) {
/* 70 */           session.setAttribute(this.attributeName, newValue);
/*    */         }
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 78 */     return "%{s," + this.attributeName + "}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 85 */       return "Servlet session attribute";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 90 */       if (token.startsWith("%{s,") && token.endsWith("}")) {
/* 91 */         String attributeName = token.substring(4, token.length() - 1);
/* 92 */         return new ServletSessionAttribute(attributeName);
/*    */       } 
/* 94 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 99 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletSessionAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */