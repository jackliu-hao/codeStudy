/*     */ package com.mysql.cj.util;
/*     */ 
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import java.text.Normalizer;
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
/*     */ public class SaslPrep
/*     */ {
/*     */   public enum StringType
/*     */   {
/*  52 */     STORED,
/*     */ 
/*     */ 
/*     */     
/*  56 */     QUERY;
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
/*     */   public static String prepare(String str, StringType sType) {
/*  73 */     if (str.length() == 0) {
/*  74 */       return str;
/*     */     }
/*     */     
/*  77 */     StringBuilder sb = new StringBuilder(str.length());
/*     */ 
/*     */     
/*  80 */     for (char chr : str.toCharArray()) {
/*  81 */       if (isNonAsciiSpaceChar(chr)) {
/*  82 */         sb.append(' ');
/*  83 */       } else if (!isMappeableToNothing(chr)) {
/*  84 */         sb.append(chr);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  89 */     String preparedStr = normalizeKc(sb);
/*     */ 
/*     */     
/*  92 */     boolean startsWithRAndAlCat = isBidiRAndAlCat(preparedStr.codePointAt(0));
/*  93 */     boolean endsWithRAndAlCat = isBidiRAndAlCat(preparedStr
/*  94 */         .codePointAt(preparedStr.length() - (Character.isLowSurrogate(preparedStr.charAt(preparedStr.length() - 1)) ? 2 : 1)));
/*  95 */     boolean containsRAndAlCat = (startsWithRAndAlCat || endsWithRAndAlCat);
/*  96 */     boolean containsLCat = false; int i;
/*  97 */     for (i = 0; i < preparedStr.length(); i = ni) {
/*  98 */       char chr = preparedStr.charAt(i);
/*  99 */       int cp = preparedStr.codePointAt(i);
/* 100 */       int ni = i + Character.charCount(cp);
/*     */ 
/*     */       
/* 103 */       if (isProhibited(chr, cp)) {
/* 104 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Prohibited character at position " + i + ".");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 110 */       if (!containsRAndAlCat) {
/* 111 */         containsRAndAlCat = isBidiRAndAlCat(cp);
/*     */       }
/* 113 */       if (!containsLCat) {
/* 114 */         containsLCat = isBidiLCat(cp);
/*     */       }
/* 116 */       if (containsRAndAlCat && containsLCat) {
/* 117 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Cannot contain both RandALCat characters and LCat characters.");
/*     */       }
/* 119 */       if (ni >= preparedStr.length() && containsRAndAlCat && (!startsWithRAndAlCat || !endsWithRAndAlCat)) {
/* 120 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Cannot contain RandALCat characters and not start and end with RandALCat characters.");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 125 */       if (sType == StringType.STORED && isUnassigned(cp)) {
/* 126 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unassigned character at position " + i + ".");
/*     */       }
/*     */     } 
/*     */     
/* 130 */     return preparedStr;
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
/*     */   private static boolean isNonAsciiSpaceChar(char chr) {
/* 143 */     return (chr == ' ' || chr == ' ' || (chr >= ' ' && chr <= '​') || chr == ' ' || chr == ' ' || chr == '　');
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
/*     */   private static boolean isMappeableToNothing(char chr) {
/* 156 */     return (chr == '­' || chr == '͏' || chr == '᠆' || (chr >= '᠋' && chr <= '᠍') || (chr >= '​' && chr <= '‍') || chr == '⁠' || (chr >= '︀' && chr <= '️') || chr == '﻿');
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
/*     */   private static String normalizeKc(CharSequence str) {
/* 169 */     return Normalizer.normalize(str, Normalizer.Form.NFKC);
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
/*     */   private static boolean isProhibited(char chr, int cp) {
/* 184 */     return (isAsciiControlCharacter(chr) || isNonAsciiControlCharacter(cp) || isPrivateUseCharacter(cp) || 
/* 185 */       isNonCharacterCodePoint(cp) || isSurrogateCode(chr) || isInappropriateForPlainTextCharacter(chr) || 
/* 186 */       isInappropriateForCanonicalRepresentationCharacter(chr) || isChangeDisplayPropertiesOrDeprecatedCharacter(chr) || isTaggingCharacter(cp));
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
/*     */   private static boolean isAsciiControlCharacter(char chr) {
/* 198 */     return (chr <= '\037' || chr == '');
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
/*     */   private static boolean isNonAsciiControlCharacter(int cp) {
/* 210 */     return ((cp >= 128 && cp <= 159) || cp == 1757 || cp == 1807 || cp == 6158 || cp == 8204 || cp == 8205 || cp == 8232 || cp == 8233 || (cp >= 8288 && cp <= 8291) || (cp >= 8298 && cp <= 8303) || cp == 65279 || (cp >= 65529 && cp <= 65532) || (cp >= 119155 && cp <= 119162));
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
/*     */   private static boolean isPrivateUseCharacter(int cp) {
/* 224 */     return ((cp >= 57344 && cp <= 63743) || (cp >= 983040 && cp <= 1048573) || (cp >= 1048576 && cp <= 1114109));
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
/*     */   private static boolean isNonCharacterCodePoint(int cp) {
/* 236 */     return ((cp >= 64976 && cp <= 65007) || (cp >= 65534 && cp <= 65535) || (cp >= 131070 && cp <= 131071) || (cp >= 196606 && cp <= 196607) || (cp >= 262142 && cp <= 262143) || (cp >= 327678 && cp <= 327679) || (cp >= 393214 && cp <= 393215) || (cp >= 458750 && cp <= 458751) || (cp >= 524286 && cp <= 524287) || (cp >= 589822 && cp <= 589823) || (cp >= 655358 && cp <= 655359) || (cp >= 720894 && cp <= 720895) || (cp >= 786430 && cp <= 786431) || (cp >= 851966 && cp <= 851967) || (cp >= 917502 && cp <= 917503) || (cp >= 983038 && cp <= 983039) || (cp >= 1048574 && cp <= 1048575) || (cp >= 1114110 && cp <= 1114111));
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
/*     */   private static boolean isSurrogateCode(char chr) {
/* 252 */     return (chr >= '?' && chr <= '?');
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
/*     */   private static boolean isInappropriateForPlainTextCharacter(char chr) {
/* 264 */     return (chr == '￹' || (chr >= '￺' && chr <= '�'));
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
/*     */   private static boolean isInappropriateForCanonicalRepresentationCharacter(char chr) {
/* 277 */     return (chr >= '⿰' && chr <= '⿻');
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
/*     */   private static boolean isChangeDisplayPropertiesOrDeprecatedCharacter(char chr) {
/* 289 */     return (chr == '̀' || chr == '́' || chr == '‎' || chr == '‏' || (chr >= '‪' && chr <= '‮') || (chr >= '⁪' && chr <= '⁯'));
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
/*     */   private static boolean isTaggingCharacter(int cp) {
/* 302 */     return (cp == 917505 || (cp >= 917536 && cp <= 917631));
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
/*     */   private static boolean isBidiRAndAlCat(int cp) {
/* 315 */     byte dir = Character.getDirectionality(cp);
/* 316 */     return (dir == 1 || dir == 2);
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
/*     */   private static boolean isBidiLCat(int cp) {
/* 329 */     byte dir = Character.getDirectionality(cp);
/* 330 */     return (dir == 0);
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
/*     */   private static boolean isUnassigned(int cp) {
/* 347 */     return !Character.isDefined(cp);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\SaslPrep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */