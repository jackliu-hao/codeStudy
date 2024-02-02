/*     */ package org.apache.http.client.utils;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class Rfc3492Idn
/*     */   implements Idn
/*     */ {
/*     */   private static final int base = 36;
/*     */   private static final int tmin = 1;
/*     */   private static final int tmax = 26;
/*     */   private static final int skew = 38;
/*     */   private static final int damp = 700;
/*     */   private static final int initial_bias = 72;
/*     */   private static final int initial_n = 128;
/*     */   private static final char delimiter = '-';
/*     */   private static final String ACE_PREFIX = "xn--";
/*     */   
/*     */   private int adapt(int delta, int numpoints, boolean firsttime) {
/*  55 */     int d = delta;
/*  56 */     if (firsttime) {
/*  57 */       d /= 700;
/*     */     } else {
/*  59 */       d /= 2;
/*     */     } 
/*  61 */     d += d / numpoints;
/*  62 */     int k = 0;
/*  63 */     while (d > 455) {
/*  64 */       d /= 35;
/*  65 */       k += 36;
/*     */     } 
/*  67 */     return k + 36 * d / (d + 38);
/*     */   }
/*     */   
/*     */   private int digit(char c) {
/*  71 */     if (c >= 'A' && c <= 'Z') {
/*  72 */       return c - 65;
/*     */     }
/*  74 */     if (c >= 'a' && c <= 'z') {
/*  75 */       return c - 97;
/*     */     }
/*  77 */     if (c >= '0' && c <= '9') {
/*  78 */       return c - 48 + 26;
/*     */     }
/*  80 */     throw new IllegalArgumentException("illegal digit: " + c);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toUnicode(String punycode) {
/*  85 */     StringBuilder unicode = new StringBuilder(punycode.length());
/*  86 */     StringTokenizer tok = new StringTokenizer(punycode, ".");
/*  87 */     while (tok.hasMoreTokens()) {
/*  88 */       String t = tok.nextToken();
/*  89 */       if (unicode.length() > 0) {
/*  90 */         unicode.append('.');
/*     */       }
/*  92 */       if (t.startsWith("xn--")) {
/*  93 */         t = decode(t.substring(4));
/*     */       }
/*  95 */       unicode.append(t);
/*     */     } 
/*  97 */     return unicode.toString();
/*     */   }
/*     */   
/*     */   protected String decode(String s) {
/* 101 */     String input = s;
/* 102 */     int n = 128;
/* 103 */     int i = 0;
/* 104 */     int bias = 72;
/* 105 */     StringBuilder output = new StringBuilder(input.length());
/* 106 */     int lastdelim = input.lastIndexOf('-');
/* 107 */     if (lastdelim != -1) {
/* 108 */       output.append(input.subSequence(0, lastdelim));
/* 109 */       input = input.substring(lastdelim + 1);
/*     */     } 
/*     */     
/* 112 */     while (!input.isEmpty()) {
/* 113 */       int oldi = i;
/* 114 */       int w = 1;
/* 115 */       int k = 36;
/* 116 */       for (; !input.isEmpty(); k += 36) {
/*     */         int t;
/*     */         
/* 119 */         char c = input.charAt(0);
/* 120 */         input = input.substring(1);
/* 121 */         int digit = digit(c);
/* 122 */         i += digit * w;
/*     */         
/* 124 */         if (k <= bias + 1) {
/* 125 */           t = 1;
/* 126 */         } else if (k >= bias + 26) {
/* 127 */           t = 26;
/*     */         } else {
/* 129 */           t = k - bias;
/*     */         } 
/* 131 */         if (digit < t) {
/*     */           break;
/*     */         }
/* 134 */         w *= 36 - t;
/*     */       } 
/* 136 */       bias = adapt(i - oldi, output.length() + 1, (oldi == 0));
/* 137 */       n += i / (output.length() + 1);
/* 138 */       i %= output.length() + 1;
/*     */       
/* 140 */       output.insert(i, (char)n);
/* 141 */       i++;
/*     */     } 
/* 143 */     return output.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\clien\\utils\Rfc3492Idn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */