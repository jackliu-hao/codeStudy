/*     */ package com.google.zxing.oned.rss;
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
/*     */ public final class RSSUtils
/*     */ {
/*     */   public static int getRSSvalue(int[] widths, int maxWidth, boolean noNarrow) {
/*  67 */     int n = 0; int arrayOfInt[], i; byte b;
/*  68 */     for (i = (arrayOfInt = widths).length, b = 0; b < i; ) { int width = arrayOfInt[b];
/*  69 */       n += width; b++; }
/*     */     
/*  71 */     int val = 0;
/*  72 */     int narrowMask = 0;
/*  73 */     int elements = widths.length;
/*  74 */     for (int bar = 0; bar < elements - 1; bar++) {
/*     */       
/*  76 */       int elmWidth = 1; narrowMask |= 1 << bar;
/*  77 */       for (; elmWidth < widths[bar]; 
/*  78 */         elmWidth++, narrowMask &= 1 << bar ^ 0xFFFFFFFF) {
/*  79 */         int subVal = combins(n - elmWidth - 1, elements - bar - 2);
/*  80 */         if (noNarrow && narrowMask == 0 && n - elmWidth - elements - bar - 1 >= elements - bar - 1)
/*     */         {
/*  82 */           subVal -= combins(n - elmWidth - elements - bar, elements - bar - 2);
/*     */         }
/*     */         
/*  85 */         if (elements - bar - 1 > 1) {
/*  86 */           int lessVal = 0;
/*  87 */           int mxwElement = n - elmWidth - elements - bar - 2;
/*  88 */           for (; mxwElement > maxWidth; mxwElement--) {
/*  89 */             lessVal += combins(n - elmWidth - mxwElement - 1, elements - bar - 3);
/*     */           }
/*     */           
/*  92 */           subVal -= lessVal * (elements - 1 - bar);
/*  93 */         } else if (n - elmWidth > maxWidth) {
/*  94 */           subVal--;
/*     */         } 
/*  96 */         val += subVal;
/*     */       } 
/*  98 */       n -= elmWidth;
/*     */     } 
/* 100 */     return val;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int combins(int n, int r) {
/*     */     int maxDenom, minDenom;
/* 106 */     if (n - r > r) {
/* 107 */       minDenom = r;
/* 108 */       maxDenom = n - r;
/*     */     } else {
/* 110 */       minDenom = n - r;
/* 111 */       maxDenom = r;
/*     */     } 
/* 113 */     int val = 1;
/* 114 */     int j = 1;
/* 115 */     for (int i = n; i > maxDenom; i--) {
/* 116 */       val *= i;
/* 117 */       if (j <= minDenom) {
/* 118 */         val /= j;
/* 119 */         j++;
/*     */       } 
/*     */     } 
/* 122 */     while (j <= minDenom) {
/* 123 */       val /= j;
/* 124 */       j++;
/*     */     } 
/* 126 */     return val;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\RSSUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */