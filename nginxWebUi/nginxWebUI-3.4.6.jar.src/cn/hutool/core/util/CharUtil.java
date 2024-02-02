/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.text.ASCIIStrCache;
/*     */ import cn.hutool.core.text.CharPool;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CharUtil
/*     */   implements CharPool
/*     */ {
/*     */   public static boolean isAscii(char ch) {
/*  31 */     return (ch < '');
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAsciiPrintable(char ch) {
/*  50 */     return (ch >= ' ' && ch < '');
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAsciiControl(char ch) {
/*  69 */     return (ch < ' ' || ch == '');
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isLetter(char ch) {
/*  89 */     return (isLetterUpper(ch) || isLetterLower(ch));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isLetterUpper(char ch) {
/* 110 */     return (ch >= 'A' && ch <= 'Z');
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isLetterLower(char ch) {
/* 131 */     return (ch >= 'a' && ch <= 'z');
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isNumber(char ch) {
/* 152 */     return (ch >= '0' && ch <= '9');
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isHexChar(char c) {
/* 168 */     return (isNumber(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isLetterOrNumber(char ch) {
/* 187 */     return (isLetter(ch) || isNumber(ch));
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
/*     */   public static String toString(char c) {
/* 199 */     return ASCIIStrCache.toString(c);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isCharClass(Class<?> clazz) {
/* 214 */     return (clazz == Character.class || clazz == char.class);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isChar(Object value) {
/* 230 */     return (value instanceof Character || value.getClass() == char.class);
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
/*     */ 
/*     */   
/*     */   public static boolean isBlankChar(char c) {
/* 244 */     return isBlankChar(c);
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
/*     */ 
/*     */   
/*     */   public static boolean isBlankChar(int c) {
/* 258 */     return (Character.isWhitespace(c) || 
/* 259 */       Character.isSpaceChar(c) || c == 65279 || c == 8234 || c == 0);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isEmoji(char c) {
/* 274 */     return (false == ((c == '\000' || c == '\t' || c == '\n' || c == '\r' || (c >= ' ' && c <= '퟿') || (c >= '' && c <= '�') || (c >= 1048576 && c <= 1114111)) ? true : false));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isFileSeparator(char c) {
/* 292 */     return ('/' == c || '\\' == c);
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
/*     */   
/*     */   public static boolean equals(char c1, char c2, boolean caseInsensitive) {
/* 305 */     if (caseInsensitive) {
/* 306 */       return (Character.toLowerCase(c1) == Character.toLowerCase(c2));
/*     */     }
/* 308 */     return (c1 == c2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getType(int c) {
/* 319 */     return Character.getType(c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int digit16(int b) {
/* 330 */     return Character.digit(b, 16);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char toCloseChar(char c) {
/* 350 */     int result = c;
/* 351 */     if (c >= '1' && c <= '9') {
/* 352 */       result = 9312 + c - 49;
/* 353 */     } else if (c >= 'A' && c <= 'Z') {
/* 354 */       result = 9398 + c - 65;
/* 355 */     } else if (c >= 'a' && c <= 'z') {
/* 356 */       result = 9424 + c - 97;
/*     */     } 
/* 358 */     return (char)result;
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
/*     */   public static char toCloseByNumber(int number) {
/* 380 */     if (number > 20) {
/* 381 */       throw new IllegalArgumentException("Number must be [1-20]");
/*     */     }
/* 383 */     return (char)(9312 + number - 1);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\CharUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */