/*    */ package org.apache.http.impl.cookie;
/*    */ 
/*    */ import java.util.Date;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.client.utils.DateUtils;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class BasicExpiresHandler
/*    */   extends AbstractCookieAttributeHandler
/*    */   implements CommonCookieAttributeHandler
/*    */ {
/*    */   private final String[] datepatterns;
/*    */   
/*    */   public BasicExpiresHandler(String[] datepatterns) {
/* 51 */     Args.notNull(datepatterns, "Array of date patterns");
/* 52 */     this.datepatterns = datepatterns;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/* 58 */     Args.notNull(cookie, "Cookie");
/* 59 */     if (value == null) {
/* 60 */       throw new MalformedCookieException("Missing value for 'expires' attribute");
/*    */     }
/* 62 */     Date expiry = DateUtils.parseDate(value, this.datepatterns);
/* 63 */     if (expiry == null) {
/* 64 */       throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
/*    */     }
/*    */     
/* 67 */     cookie.setExpiryDate(expiry);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAttributeName() {
/* 72 */     return "expires";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\BasicExpiresHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */