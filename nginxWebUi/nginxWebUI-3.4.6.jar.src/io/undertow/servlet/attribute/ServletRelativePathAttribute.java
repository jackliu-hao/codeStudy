/*    */ package io.undertow.servlet.attribute;
/*    */ 
/*    */ import io.undertow.attribute.ExchangeAttribute;
/*    */ import io.undertow.attribute.ExchangeAttributeBuilder;
/*    */ import io.undertow.attribute.ReadOnlyAttributeException;
/*    */ import io.undertow.attribute.RelativePathAttribute;
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
/*    */ public class ServletRelativePathAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String RELATIVE_PATH_SHORT = "%R";
/*    */   public static final String RELATIVE_PATH = "%{RELATIVE_PATH}";
/* 41 */   public static final ExchangeAttribute INSTANCE = new ServletRelativePathAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 49 */     ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 50 */     if (src == null) {
/* 51 */       return RequestURLAttribute.INSTANCE.readAttribute(exchange);
/*    */     }
/* 53 */     String path = (String)src.getServletRequest().getAttribute("javax.servlet.forward.path_info");
/* 54 */     String sp = (String)src.getServletRequest().getAttribute("javax.servlet.forward.servlet_path");
/* 55 */     if (path == null && sp == null) {
/* 56 */       return RequestURLAttribute.INSTANCE.readAttribute(exchange);
/*    */     }
/* 58 */     if (sp == null)
/* 59 */       return path; 
/* 60 */     if (path == null) {
/* 61 */       return sp;
/*    */     }
/* 63 */     return sp + path;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 69 */     RelativePathAttribute.INSTANCE.writeAttribute(exchange, newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 74 */     return "%{RELATIVE_PATH}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 81 */       return "Relative Path";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 86 */       return (token.equals("%{RELATIVE_PATH}") || token.equals("%R")) ? ServletRelativePathAttribute.INSTANCE : null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 91 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\attribute\ServletRelativePathAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */