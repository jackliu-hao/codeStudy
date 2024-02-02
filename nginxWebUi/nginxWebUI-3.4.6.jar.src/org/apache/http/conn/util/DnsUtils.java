/*    */ package org.apache.http.conn.util;
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
/*    */ public class DnsUtils
/*    */ {
/*    */   private static boolean isUpper(char c) {
/* 41 */     return (c >= 'A' && c <= 'Z');
/*    */   }
/*    */   
/*    */   public static String normalize(String s) {
/* 45 */     if (s == null) {
/* 46 */       return null;
/*    */     }
/* 48 */     int pos = 0;
/* 49 */     int remaining = s.length();
/* 50 */     while (remaining > 0 && 
/* 51 */       !isUpper(s.charAt(pos))) {
/*    */ 
/*    */       
/* 54 */       pos++;
/* 55 */       remaining--;
/*    */     } 
/* 57 */     if (remaining > 0) {
/* 58 */       StringBuilder buf = new StringBuilder(s.length());
/* 59 */       buf.append(s, 0, pos);
/* 60 */       while (remaining > 0) {
/* 61 */         char c = s.charAt(pos);
/* 62 */         if (isUpper(c)) {
/* 63 */           buf.append((char)(c + 32));
/*    */         } else {
/* 65 */           buf.append(c);
/*    */         } 
/* 67 */         pos++;
/* 68 */         remaining--;
/*    */       } 
/* 70 */       return buf.toString();
/*    */     } 
/* 72 */     return s;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\con\\util\DnsUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */