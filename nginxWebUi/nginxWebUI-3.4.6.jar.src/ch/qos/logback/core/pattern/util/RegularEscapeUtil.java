/*    */ package ch.qos.logback.core.pattern.util;
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
/*    */ public class RegularEscapeUtil
/*    */   implements IEscapeUtil
/*    */ {
/*    */   public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
/* 24 */     if (escapeChars.indexOf(next) >= 0) {
/* 25 */       buf.append(next);
/*    */     }
/* 27 */     switch (next) {
/*    */       case '_':
/*    */         return;
/*    */       
/*    */       case '\\':
/* 32 */         buf.append(next);
/*    */       
/*    */       case 't':
/* 35 */         buf.append('\t');
/*    */       
/*    */       case 'r':
/* 38 */         buf.append('\r');
/*    */       
/*    */       case 'n':
/* 41 */         buf.append('\n');
/*    */     } 
/*    */     
/* 44 */     String commaSeperatedEscapeChars = formatEscapeCharsForListing(escapeChars);
/* 45 */     throw new IllegalArgumentException("Illegal char '" + next + " at column " + pointer + ". Only \\\\, \\_" + commaSeperatedEscapeChars + ", \\t, \\n, \\r combinations are allowed as escape characters.");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   String formatEscapeCharsForListing(String escapeChars) {
/* 51 */     StringBuilder commaSeperatedEscapeChars = new StringBuilder();
/* 52 */     for (int i = 0; i < escapeChars.length(); i++) {
/* 53 */       commaSeperatedEscapeChars.append(", \\").append(escapeChars.charAt(i));
/*    */     }
/* 55 */     return commaSeperatedEscapeChars.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public static String basicEscape(String s) {
/* 60 */     int len = s.length();
/* 61 */     StringBuilder sbuf = new StringBuilder(len);
/*    */     
/* 63 */     int i = 0;
/* 64 */     while (i < len) {
/* 65 */       char c = s.charAt(i++);
/* 66 */       if (c == '\\') {
/* 67 */         c = s.charAt(i++);
/* 68 */         if (c == 'n') {
/* 69 */           c = '\n';
/* 70 */         } else if (c == 'r') {
/* 71 */           c = '\r';
/* 72 */         } else if (c == 't') {
/* 73 */           c = '\t';
/* 74 */         } else if (c == 'f') {
/* 75 */           c = '\f';
/* 76 */         } else if (c == '\b') {
/* 77 */           c = '\b';
/* 78 */         } else if (c == '"') {
/* 79 */           c = '"';
/* 80 */         } else if (c == '\'') {
/* 81 */           c = '\'';
/* 82 */         } else if (c == '\\') {
/* 83 */           c = '\\';
/*    */         } 
/*    */       } 
/* 86 */       sbuf.append(c);
/*    */     } 
/* 88 */     return sbuf.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\patter\\util\RegularEscapeUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */