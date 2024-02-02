/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.handlers.Cookie;
/*    */ import io.undertow.server.handlers.CookieImpl;
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
/*    */ public class RequestCookieAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   private static final String TOKEN_PREFIX = "%{req-cookie,";
/*    */   private final String cookieName;
/*    */   
/*    */   public RequestCookieAttribute(String cookieName) {
/* 36 */     this.cookieName = cookieName;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 41 */     for (Cookie cookie : exchange.requestCookies()) {
/* 42 */       if (this.cookieName.equals(cookie.getName())) {
/* 43 */         return cookie.getValue();
/*    */       }
/*    */     } 
/* 46 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 51 */     exchange.setRequestCookie((Cookie)new CookieImpl(this.cookieName, newValue));
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 56 */     return "%{req-cookie," + this.cookieName + "}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 63 */       return "Request cookie";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 68 */       if (token.startsWith("%{req-cookie,") && token.endsWith("}")) {
/* 69 */         String cookieName = token.substring("%{req-cookie,".length(), token.length() - 1);
/* 70 */         return new RequestCookieAttribute(cookieName);
/*    */       } 
/* 72 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 77 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RequestCookieAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */