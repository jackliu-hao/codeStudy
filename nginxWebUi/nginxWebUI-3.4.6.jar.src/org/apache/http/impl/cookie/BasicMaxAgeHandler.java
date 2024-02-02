/*    */ package org.apache.http.impl.cookie;
/*    */ 
/*    */ import java.util.Date;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*    */ import org.apache.http.cookie.MalformedCookieException;
/*    */ import org.apache.http.cookie.SetCookie;
/*    */ import org.apache.http.util.Args;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class BasicMaxAgeHandler
/*    */   extends AbstractCookieAttributeHandler
/*    */   implements CommonCookieAttributeHandler
/*    */ {
/*    */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/*    */     int age;
/* 53 */     Args.notNull(cookie, "Cookie");
/* 54 */     if (value == null) {
/* 55 */       throw new MalformedCookieException("Missing value for 'max-age' attribute");
/*    */     }
/*    */     
/*    */     try {
/* 59 */       age = Integer.parseInt(value);
/* 60 */     } catch (NumberFormatException e) {
/* 61 */       throw new MalformedCookieException("Invalid 'max-age' attribute: " + value);
/*    */     } 
/*    */     
/* 64 */     if (age < 0) {
/* 65 */       throw new MalformedCookieException("Negative 'max-age' attribute: " + value);
/*    */     }
/*    */     
/* 68 */     cookie.setExpiryDate(new Date(System.currentTimeMillis() + age * 1000L));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAttributeName() {
/* 73 */     return "max-age";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\BasicMaxAgeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */