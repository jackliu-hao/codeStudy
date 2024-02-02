/*     */ package org.apache.commons.compress.utils;
/*     */ 
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
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
/*     */ public class ArchiveUtils
/*     */ {
/*     */   private static final int MAX_SANITIZED_NAME_LENGTH = 255;
/*     */   
/*     */   public static String toString(ArchiveEntry entry) {
/*  50 */     StringBuilder sb = new StringBuilder();
/*  51 */     sb.append(entry.isDirectory() ? 100 : 45);
/*  52 */     String size = Long.toString(entry.getSize());
/*  53 */     sb.append(' ');
/*     */     
/*  55 */     for (int i = 7; i > size.length(); i--) {
/*  56 */       sb.append(' ');
/*     */     }
/*  58 */     sb.append(size);
/*  59 */     sb.append(' ').append(entry.getName());
/*  60 */     return sb.toString();
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
/*     */   public static boolean matchAsciiBuffer(String expected, byte[] buffer, int offset, int length) {
/*  75 */     byte[] buffer1 = expected.getBytes(StandardCharsets.US_ASCII);
/*  76 */     return isEqual(buffer1, 0, buffer1.length, buffer, offset, length, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean matchAsciiBuffer(String expected, byte[] buffer) {
/*  87 */     return matchAsciiBuffer(expected, buffer, 0, buffer.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] toAsciiBytes(String inputString) {
/*  98 */     return inputString.getBytes(StandardCharsets.US_ASCII);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toAsciiString(byte[] inputBytes) {
/* 108 */     return new String(inputBytes, StandardCharsets.US_ASCII);
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
/*     */   public static String toAsciiString(byte[] inputBytes, int offset, int length) {
/* 120 */     return new String(inputBytes, offset, length, StandardCharsets.US_ASCII);
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
/*     */   public static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2, boolean ignoreTrailingNulls) {
/* 139 */     int minLen = (length1 < length2) ? length1 : length2; int i;
/* 140 */     for (i = 0; i < minLen; i++) {
/* 141 */       if (buffer1[offset1 + i] != buffer2[offset2 + i]) {
/* 142 */         return false;
/*     */       }
/*     */     } 
/* 145 */     if (length1 == length2) {
/* 146 */       return true;
/*     */     }
/* 148 */     if (ignoreTrailingNulls) {
/* 149 */       if (length1 > length2) {
/* 150 */         for (i = length2; i < length1; i++) {
/* 151 */           if (buffer1[offset1 + i] != 0) {
/* 152 */             return false;
/*     */           }
/*     */         } 
/*     */       } else {
/* 156 */         for (i = length1; i < length2; i++) {
/* 157 */           if (buffer2[offset2 + i] != 0) {
/* 158 */             return false;
/*     */           }
/*     */         } 
/*     */       } 
/* 162 */       return true;
/*     */     } 
/* 164 */     return false;
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
/*     */   public static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2) {
/* 181 */     return isEqual(buffer1, offset1, length1, buffer2, offset2, length2, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isEqual(byte[] buffer1, byte[] buffer2) {
/* 192 */     return isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, false);
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
/*     */   public static boolean isEqual(byte[] buffer1, byte[] buffer2, boolean ignoreTrailingNulls) {
/* 204 */     return isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, ignoreTrailingNulls);
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
/*     */   public static boolean isEqualWithNull(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2) {
/* 221 */     return isEqual(buffer1, offset1, length1, buffer2, offset2, length2, true);
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
/*     */   public static boolean isArrayZero(byte[] a, int size) {
/* 234 */     for (int i = 0; i < size; i++) {
/* 235 */       if (a[i] != 0) {
/* 236 */         return false;
/*     */       }
/*     */     } 
/* 239 */     return true;
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
/*     */   public static String sanitize(String s) {
/* 258 */     char[] cs = s.toCharArray();
/* 259 */     char[] chars = (cs.length <= 255) ? cs : Arrays.copyOf(cs, 255);
/* 260 */     if (cs.length > 255) {
/* 261 */       for (int i = 252; i < 255; i++) {
/* 262 */         chars[i] = '.';
/*     */       }
/*     */     }
/* 265 */     StringBuilder sb = new StringBuilder();
/* 266 */     for (char c : chars) {
/* 267 */       if (!Character.isISOControl(c)) {
/* 268 */         Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
/* 269 */         if (block != null && block != Character.UnicodeBlock.SPECIALS) {
/* 270 */           sb.append(c);
/*     */           continue;
/*     */         } 
/*     */       } 
/* 274 */       sb.append('?'); continue;
/*     */     } 
/* 276 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\ArchiveUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */