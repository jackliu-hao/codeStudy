/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.lang.Assert;
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
/*     */ 
/*     */ 
/*     */ public class PunyCode
/*     */ {
/*     */   private static final int TMIN = 1;
/*     */   private static final int TMAX = 26;
/*     */   private static final int BASE = 36;
/*     */   private static final int INITIAL_N = 128;
/*     */   private static final int INITIAL_BIAS = 72;
/*     */   private static final int DAMP = 700;
/*     */   private static final int SKEW = 38;
/*     */   private static final char DELIMITER = '-';
/*     */   public static final String PUNY_CODE_PREFIX = "xn--";
/*     */   
/*     */   public static String encode(CharSequence input) throws UtilException {
/*  35 */     return encode(input, false);
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
/*     */   public static String encode(CharSequence input, boolean withPrefix) throws UtilException {
/*  47 */     int n = 128;
/*  48 */     int delta = 0;
/*  49 */     int bias = 72;
/*  50 */     StringBuilder output = new StringBuilder();
/*     */     
/*  52 */     int length = input.length();
/*  53 */     int b = 0;
/*  54 */     for (int i = 0; i < length; i++) {
/*  55 */       char c = input.charAt(i);
/*  56 */       if (isBasic(c)) {
/*  57 */         output.append(c);
/*  58 */         b++;
/*     */       } 
/*     */     } 
/*     */     
/*  62 */     if (b > 0) {
/*  63 */       output.append('-');
/*     */     }
/*  65 */     int h = b;
/*  66 */     while (h < length) {
/*  67 */       int m = Integer.MAX_VALUE;
/*     */       
/*  69 */       for (int k = 0; k < length; k++) {
/*  70 */         char c = input.charAt(k);
/*  71 */         if (c >= n && c < m) {
/*  72 */           m = c;
/*     */         }
/*     */       } 
/*  75 */       if (m - n > (Integer.MAX_VALUE - delta) / (h + 1)) {
/*  76 */         throw new UtilException("OVERFLOW");
/*     */       }
/*  78 */       delta += (m - n) * (h + 1);
/*  79 */       n = m;
/*  80 */       for (int j = 0; j < length; j++) {
/*  81 */         int c = input.charAt(j);
/*  82 */         if (c < n) {
/*  83 */           delta++;
/*  84 */           if (0 == delta) {
/*  85 */             throw new UtilException("OVERFLOW");
/*     */           }
/*     */         } 
/*  88 */         if (c == n) {
/*  89 */           int q = delta;
/*  90 */           for (int i1 = 36;; i1 += 36) {
/*     */             int t;
/*  92 */             if (i1 <= bias) {
/*  93 */               t = 1;
/*  94 */             } else if (i1 >= bias + 26) {
/*  95 */               t = 26;
/*     */             } else {
/*  97 */               t = i1 - bias;
/*     */             } 
/*  99 */             if (q < t) {
/*     */               break;
/*     */             }
/* 102 */             output.append((char)digit2codepoint(t + (q - t) % (36 - t)));
/* 103 */             q = (q - t) / (36 - t);
/*     */           } 
/* 105 */           output.append((char)digit2codepoint(q));
/* 106 */           bias = adapt(delta, h + 1, (h == b));
/* 107 */           delta = 0;
/* 108 */           h++;
/*     */         } 
/*     */       } 
/* 111 */       delta++;
/* 112 */       n++;
/*     */     } 
/*     */     
/* 115 */     if (withPrefix) {
/* 116 */       output.insert(0, "xn--");
/*     */     }
/* 118 */     return output.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decode(String input) throws UtilException {
/* 129 */     input = StrUtil.removePrefixIgnoreCase(input, "xn--");
/*     */     
/* 131 */     int n = 128;
/* 132 */     int i = 0;
/* 133 */     int bias = 72;
/* 134 */     StringBuilder output = new StringBuilder();
/* 135 */     int d = input.lastIndexOf('-');
/* 136 */     if (d > 0) {
/* 137 */       for (int j = 0; j < d; j++) {
/* 138 */         char c = input.charAt(j);
/* 139 */         if (isBasic(c)) {
/* 140 */           output.append(c);
/*     */         }
/*     */       } 
/* 143 */       d++;
/*     */     } else {
/* 145 */       d = 0;
/*     */     } 
/* 147 */     int length = input.length();
/* 148 */     while (d < length) {
/* 149 */       int oldi = i;
/* 150 */       int w = 1;
/* 151 */       for (int k = 36;; k += 36) {
/* 152 */         int t; if (d == length) {
/* 153 */           throw new UtilException("BAD_INPUT");
/*     */         }
/* 155 */         int c = input.charAt(d++);
/* 156 */         int digit = codepoint2digit(c);
/* 157 */         if (digit > (Integer.MAX_VALUE - i) / w) {
/* 158 */           throw new UtilException("OVERFLOW");
/*     */         }
/* 160 */         i += digit * w;
/*     */         
/* 162 */         if (k <= bias) {
/* 163 */           t = 1;
/* 164 */         } else if (k >= bias + 26) {
/* 165 */           t = 26;
/*     */         } else {
/* 167 */           t = k - bias;
/*     */         } 
/* 169 */         if (digit < t) {
/*     */           break;
/*     */         }
/* 172 */         w *= 36 - t;
/*     */       } 
/* 174 */       bias = adapt(i - oldi, output.length() + 1, (oldi == 0));
/* 175 */       if (i / (output.length() + 1) > Integer.MAX_VALUE - n) {
/* 176 */         throw new UtilException("OVERFLOW");
/*     */       }
/* 178 */       n += i / (output.length() + 1);
/* 179 */       i %= output.length() + 1;
/* 180 */       output.insert(i, (char)n);
/* 181 */       i++;
/*     */     } 
/*     */     
/* 184 */     return output.toString();
/*     */   }
/*     */   
/*     */   private static int adapt(int delta, int numpoints, boolean first) {
/* 188 */     if (first) {
/* 189 */       delta /= 700;
/*     */     } else {
/* 191 */       delta /= 2;
/*     */     } 
/* 193 */     delta += delta / numpoints;
/* 194 */     int k = 0;
/* 195 */     while (delta > 455) {
/* 196 */       delta /= 35;
/* 197 */       k += 36;
/*     */     } 
/* 199 */     return k + 36 * delta / (delta + 38);
/*     */   }
/*     */   
/*     */   private static boolean isBasic(char c) {
/* 203 */     return (c < '');
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
/*     */   private static int digit2codepoint(int d) throws UtilException {
/* 223 */     Assert.checkBetween(d, 0, 35);
/* 224 */     if (d < 26)
/*     */     {
/* 226 */       return d + 97; } 
/* 227 */     if (d < 36)
/*     */     {
/* 229 */       return d - 26 + 48;
/*     */     }
/* 231 */     throw new UtilException("BAD_INPUT");
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
/*     */   private static int codepoint2digit(int c) throws UtilException {
/* 252 */     if (c - 48 < 10)
/*     */     {
/* 254 */       return c - 48 + 26; } 
/* 255 */     if (c - 97 < 26)
/*     */     {
/* 257 */       return c - 97;
/*     */     }
/* 259 */     throw new UtilException("BAD_INPUT");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\PunyCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */