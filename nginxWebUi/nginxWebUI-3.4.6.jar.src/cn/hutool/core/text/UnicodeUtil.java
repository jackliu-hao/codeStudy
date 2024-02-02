/*     */ package cn.hutool.core.text;
/*     */ 
/*     */ import cn.hutool.core.util.CharUtil;
/*     */ import cn.hutool.core.util.HexUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UnicodeUtil
/*     */ {
/*     */   public static String toString(String unicode) {
/*  23 */     if (StrUtil.isBlank(unicode)) {
/*  24 */       return unicode;
/*     */     }
/*     */     
/*  27 */     int len = unicode.length();
/*  28 */     StringBuilder sb = new StringBuilder(len);
/*     */     
/*  30 */     int pos = 0; int i;
/*  31 */     while ((i = StrUtil.indexOfIgnoreCase(unicode, "\\u", pos)) != -1) {
/*  32 */       sb.append(unicode, pos, i);
/*  33 */       pos = i;
/*  34 */       if (i + 5 < len) {
/*     */         
/*     */         try {
/*  37 */           char c = (char)Integer.parseInt(unicode.substring(i + 2, i + 6), 16);
/*  38 */           sb.append(c);
/*  39 */           pos = i + 6;
/*  40 */         } catch (NumberFormatException e) {
/*     */           
/*  42 */           sb.append(unicode, pos, i + 2);
/*  43 */           pos = i + 2;
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  51 */     if (pos < len) {
/*  52 */       sb.append(unicode, pos, len);
/*     */     }
/*  54 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toUnicode(char c) {
/*  66 */     return HexUtil.toUnicodeHex(c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toUnicode(int c) {
/*  78 */     return HexUtil.toUnicodeHex(c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toUnicode(String str) {
/*  88 */     return toUnicode(str, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toUnicode(String str, boolean isSkipAscii) {
/*  99 */     if (StrUtil.isEmpty(str)) {
/* 100 */       return str;
/*     */     }
/*     */     
/* 103 */     int len = str.length();
/* 104 */     StringBuilder unicode = new StringBuilder(str.length() * 6);
/*     */     
/* 106 */     for (int i = 0; i < len; i++) {
/* 107 */       char c = str.charAt(i);
/* 108 */       if (isSkipAscii && CharUtil.isAsciiPrintable(c)) {
/* 109 */         unicode.append(c);
/*     */       } else {
/* 111 */         unicode.append(HexUtil.toUnicodeHex(c));
/*     */       } 
/*     */     } 
/* 114 */     return unicode.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\UnicodeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */