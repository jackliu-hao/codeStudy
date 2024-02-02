/*    */ package io.undertow.util;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import java.util.BitSet;
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
/*    */ public final class Rfc6265CookieSupport
/*    */ {
/* 28 */   private static final BitSet domainValid = new BitSet(128);
/*    */   static {
/*    */     char c;
/* 31 */     for (c = '0'; c <= '9'; c = (char)(c + 1)) {
/* 32 */       domainValid.set(c);
/*    */     }
/* 34 */     for (c = 'a'; c <= 'z'; c = (char)(c + 1)) {
/* 35 */       domainValid.set(c);
/*    */     }
/* 37 */     for (c = 'A'; c <= 'Z'; c = (char)(c + 1)) {
/* 38 */       domainValid.set(c);
/*    */     }
/* 40 */     domainValid.set(46);
/* 41 */     domainValid.set(45);
/*    */   }
/*    */   
/*    */   public static void validateCookieValue(String value) {
/* 45 */     int start = 0;
/* 46 */     int end = value.length();
/*    */     
/* 48 */     if (end > 1 && value.charAt(0) == '"' && value.charAt(end - 1) == '"') {
/* 49 */       start = 1;
/* 50 */       end--;
/*    */     } 
/*    */     
/* 53 */     char[] chars = value.toCharArray();
/* 54 */     for (int i = start; i < end; i++) {
/* 55 */       char c = chars[i];
/* 56 */       if (c < '!' || c == '"' || c == ',' || c == ';' || c == '\\' || c == '') {
/* 57 */         throw UndertowMessages.MESSAGES.invalidCookieValue(Integer.toString(c));
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void validateDomain(String domain) {
/* 63 */     int i = 0;
/* 64 */     int prev = -1;
/* 65 */     int cur = -1;
/* 66 */     char[] chars = domain.toCharArray();
/* 67 */     while (i < chars.length) {
/* 68 */       prev = cur;
/* 69 */       cur = chars[i];
/* 70 */       if (!domainValid.get(cur)) {
/* 71 */         throw UndertowMessages.MESSAGES.invalidCookieDomain(domain);
/*    */       }
/*    */       
/* 74 */       if ((prev == 46 || prev == -1) && (cur == 46 || cur == 45)) {
/* 75 */         throw UndertowMessages.MESSAGES.invalidCookieDomain(domain);
/*    */       }
/*    */       
/* 78 */       if (prev == 45 && cur == 46) {
/* 79 */         throw UndertowMessages.MESSAGES.invalidCookieDomain(domain);
/*    */       }
/* 81 */       i++;
/*    */     } 
/*    */     
/* 84 */     if (cur == 46 || cur == 45) {
/* 85 */       throw UndertowMessages.MESSAGES.invalidCookieDomain(domain);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void validatePath(String path) {
/* 90 */     char[] chars = path.toCharArray();
/*    */     
/* 92 */     for (int i = 0; i < chars.length; i++) {
/* 93 */       char ch = chars[i];
/* 94 */       if (ch < ' ' || ch > '~' || ch == ';')
/* 95 */         throw UndertowMessages.MESSAGES.invalidCookiePath(path); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\Rfc6265CookieSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */