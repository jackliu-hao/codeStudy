/*    */ package org.apache.http.impl.cookie;
/*    */ 
/*    */ import java.util.Date;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*    */ import org.apache.http.cookie.MalformedCookieException;
/*    */ import org.apache.http.cookie.SetCookie;
/*    */ import org.apache.http.util.Args;
/*    */ import org.apache.http.util.TextUtils;
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
/*    */ public class LaxMaxAgeHandler
/*    */   extends AbstractCookieAttributeHandler
/*    */   implements CommonCookieAttributeHandler
/*    */ {
/* 49 */   private static final Pattern MAX_AGE_PATTERN = Pattern.compile("^\\-?[0-9]+$");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/* 57 */     Args.notNull(cookie, "Cookie");
/* 58 */     if (TextUtils.isBlank(value)) {
/*    */       return;
/*    */     }
/* 61 */     Matcher matcher = MAX_AGE_PATTERN.matcher(value);
/* 62 */     if (matcher.matches()) {
/*    */       int age;
/*    */       try {
/* 65 */         age = Integer.parseInt(value);
/* 66 */       } catch (NumberFormatException e) {
/*    */         return;
/*    */       } 
/* 69 */       Date expiryDate = (age >= 0) ? new Date(System.currentTimeMillis() + age * 1000L) : new Date(Long.MIN_VALUE);
/*    */       
/* 71 */       cookie.setExpiryDate(expiryDate);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAttributeName() {
/* 77 */     return "max-age";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\LaxMaxAgeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */