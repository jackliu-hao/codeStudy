/*     */ package org.apache.commons.compress.harmony.unpack200;
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
/*     */ public final class SegmentUtils
/*     */ {
/*     */   public static int countArgs(String descriptor) {
/*  25 */     return countArgs(descriptor, 1);
/*     */   }
/*     */   
/*     */   public static int countInvokeInterfaceArgs(String descriptor) {
/*  29 */     return countArgs(descriptor, 2);
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
/*     */   protected static int countArgs(String descriptor, int widthOfLongsAndDoubles) {
/*  42 */     int bra = descriptor.indexOf('(');
/*  43 */     int ket = descriptor.indexOf(')');
/*  44 */     if (bra == -1 || ket == -1 || ket < bra) {
/*  45 */       throw new IllegalArgumentException("No arguments");
/*     */     }
/*     */     
/*  48 */     boolean inType = false;
/*  49 */     boolean consumingNextType = false;
/*  50 */     int count = 0;
/*  51 */     for (int i = bra + 1; i < ket; i++) {
/*  52 */       char charAt = descriptor.charAt(i);
/*  53 */       if (inType && charAt == ';') {
/*  54 */         inType = false;
/*  55 */         consumingNextType = false;
/*  56 */       } else if (!inType && charAt == 'L') {
/*  57 */         inType = true;
/*  58 */         count++;
/*  59 */       } else if (charAt == '[') {
/*  60 */         consumingNextType = true;
/*  61 */       } else if (!inType) {
/*     */         
/*  63 */         if (consumingNextType) {
/*  64 */           count++;
/*  65 */           consumingNextType = false;
/*  66 */         } else if (charAt == 'D' || charAt == 'J') {
/*  67 */           count += widthOfLongsAndDoubles;
/*     */         } else {
/*  69 */           count++;
/*     */         } 
/*     */       } 
/*  72 */     }  return count;
/*     */   }
/*     */   
/*     */   public static int countMatches(long[] flags, IMatcher matcher) {
/*  76 */     int count = 0;
/*  77 */     for (int i = 0; i < flags.length; i++) {
/*  78 */       if (matcher.matches(flags[i])) {
/*  79 */         count++;
/*     */       }
/*     */     } 
/*  82 */     return count;
/*     */   }
/*     */   
/*     */   public static int countBit16(int[] flags) {
/*  86 */     int count = 0;
/*  87 */     for (int i = 0; i < flags.length; i++) {
/*  88 */       if ((flags[i] & 0x10000) != 0) {
/*  89 */         count++;
/*     */       }
/*     */     } 
/*  92 */     return count;
/*     */   }
/*     */   
/*     */   public static int countBit16(long[] flags) {
/*  96 */     int count = 0;
/*  97 */     for (int i = 0; i < flags.length; i++) {
/*  98 */       if ((flags[i] & 0x10000L) != 0L) {
/*  99 */         count++;
/*     */       }
/*     */     } 
/* 102 */     return count;
/*     */   }
/*     */   
/*     */   public static int countBit16(long[][] flags) {
/* 106 */     int count = 0;
/* 107 */     for (int i = 0; i < flags.length; i++) {
/* 108 */       for (int j = 0; j < (flags[i]).length; j++) {
/* 109 */         if ((flags[i][j] & 0x10000L) != 0L) {
/* 110 */           count++;
/*     */         }
/*     */       } 
/*     */     } 
/* 114 */     return count;
/*     */   }
/*     */   
/*     */   public static int countMatches(long[][] flags, IMatcher matcher) {
/* 118 */     int count = 0;
/* 119 */     for (int i = 0; i < flags.length; i++) {
/* 120 */       count += countMatches(flags[i], matcher);
/*     */     }
/* 122 */     return count;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\SegmentUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */