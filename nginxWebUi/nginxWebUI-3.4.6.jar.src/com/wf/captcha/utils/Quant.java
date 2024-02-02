/*     */ package com.wf.captcha.utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Quant
/*     */ {
/*     */   protected static final int netsize = 256;
/*     */   protected static final int prime1 = 499;
/*     */   protected static final int prime2 = 491;
/*     */   protected static final int prime3 = 487;
/*     */   protected static final int prime4 = 503;
/*     */   protected static final int minpicturebytes = 1509;
/*     */   protected static final int maxnetpos = 255;
/*     */   protected static final int netbiasshift = 4;
/*     */   protected static final int ncycles = 100;
/*     */   protected static final int intbiasshift = 16;
/*     */   protected static final int intbias = 65536;
/*     */   protected static final int gammashift = 10;
/*     */   protected static final int gamma = 1024;
/*     */   protected static final int betashift = 10;
/*     */   protected static final int beta = 64;
/*     */   protected static final int betagamma = 65536;
/*     */   protected static final int initrad = 32;
/*     */   protected static final int radiusbiasshift = 6;
/*     */   protected static final int radiusbias = 64;
/*     */   protected static final int initradius = 2048;
/*     */   protected static final int radiusdec = 30;
/*     */   protected static final int alphabiasshift = 10;
/*     */   protected static final int initalpha = 1024;
/*     */   protected int alphadec;
/*     */   protected static final int radbiasshift = 8;
/*     */   protected static final int radbias = 256;
/*     */   protected static final int alpharadbshift = 18;
/*     */   protected static final int alpharadbias = 262144;
/*     */   protected byte[] thepicture;
/*     */   protected int lengthcount;
/*     */   protected int samplefac;
/*     */   protected int[][] network;
/*  78 */   protected int[] netindex = new int[256];
/*     */ 
/*     */   
/*  81 */   protected int[] bias = new int[256];
/*     */   
/*  83 */   protected int[] freq = new int[256];
/*  84 */   protected int[] radpower = new int[32];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Quant(byte[] thepic, int len, int sample) {
/*  94 */     this.thepicture = thepic;
/*  95 */     this.lengthcount = len;
/*  96 */     this.samplefac = sample;
/*     */     
/*  98 */     this.network = new int[256][];
/*  99 */     for (int i = 0; i < 256; i++) {
/* 100 */       this.network[i] = new int[4];
/* 101 */       int[] p = this.network[i];
/* 102 */       p[2] = (i << 12) / 256; p[1] = (i << 12) / 256; p[0] = (i << 12) / 256;
/* 103 */       this.freq[i] = 256;
/* 104 */       this.bias[i] = 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public byte[] colorMap() {
/* 109 */     byte[] map = new byte[768];
/* 110 */     int[] index = new int[256];
/* 111 */     for (int i = 0; i < 256; i++)
/* 112 */       index[this.network[i][3]] = i; 
/* 113 */     int k = 0;
/* 114 */     for (int j = 0; j < 256; j++) {
/* 115 */       int m = index[j];
/* 116 */       map[k++] = (byte)this.network[m][0];
/* 117 */       map[k++] = (byte)this.network[m][1];
/* 118 */       map[k++] = (byte)this.network[m][2];
/*     */     } 
/* 120 */     return map;
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
/*     */   public void inxbuild() {
/* 132 */     int previouscol = 0;
/* 133 */     int startpos = 0;
/* 134 */     for (int i = 0; i < 256; i++) {
/* 135 */       int[] p = this.network[i];
/* 136 */       int smallpos = i;
/* 137 */       int smallval = p[1];
/*     */       int k;
/* 139 */       for (k = i + 1; k < 256; k++) {
/* 140 */         int[] arrayOfInt = this.network[k];
/* 141 */         if (arrayOfInt[1] < smallval) {
/* 142 */           smallpos = k;
/* 143 */           smallval = arrayOfInt[1];
/*     */         } 
/*     */       } 
/* 146 */       int[] q = this.network[smallpos];
/*     */       
/* 148 */       if (i != smallpos) {
/* 149 */         k = q[0];
/* 150 */         q[0] = p[0];
/* 151 */         p[0] = k;
/* 152 */         k = q[1];
/* 153 */         q[1] = p[1];
/* 154 */         p[1] = k;
/* 155 */         k = q[2];
/* 156 */         q[2] = p[2];
/* 157 */         p[2] = k;
/* 158 */         k = q[3];
/* 159 */         q[3] = p[3];
/* 160 */         p[3] = k;
/*     */       } 
/*     */       
/* 163 */       if (smallval != previouscol) {
/* 164 */         this.netindex[previouscol] = startpos + i >> 1;
/* 165 */         for (k = previouscol + 1; k < smallval; k++)
/* 166 */           this.netindex[k] = i; 
/* 167 */         previouscol = smallval;
/* 168 */         startpos = i;
/*     */       } 
/*     */     } 
/* 171 */     this.netindex[previouscol] = startpos + 255 >> 1;
/* 172 */     for (int j = previouscol + 1; j < 256; j++) {
/* 173 */       this.netindex[j] = 255;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void learn() {
/*     */     int step;
/* 185 */     if (this.lengthcount < 1509)
/* 186 */       this.samplefac = 1; 
/* 187 */     this.alphadec = 30 + (this.samplefac - 1) / 3;
/* 188 */     byte[] p = this.thepicture;
/* 189 */     int pix = 0;
/* 190 */     int lim = this.lengthcount;
/* 191 */     int samplepixels = this.lengthcount / 3 * this.samplefac;
/* 192 */     int delta = samplepixels / 100;
/* 193 */     int alpha = 1024;
/* 194 */     int radius = 2048;
/*     */     
/* 196 */     int rad = radius >> 6;
/* 197 */     if (rad <= 1)
/* 198 */       rad = 0;  int i;
/* 199 */     for (i = 0; i < rad; i++) {
/* 200 */       this.radpower[i] = alpha * (rad * rad - i * i) * 256 / rad * rad;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 205 */     if (this.lengthcount < 1509) {
/* 206 */       step = 3;
/* 207 */     } else if (this.lengthcount % 499 != 0) {
/* 208 */       step = 1497;
/*     */     }
/* 210 */     else if (this.lengthcount % 491 != 0) {
/* 211 */       step = 1473;
/*     */     }
/* 213 */     else if (this.lengthcount % 487 != 0) {
/* 214 */       step = 1461;
/*     */     } else {
/* 216 */       step = 1509;
/*     */     } 
/*     */ 
/*     */     
/* 220 */     i = 0;
/* 221 */     while (i < samplepixels) {
/* 222 */       int b = (p[pix + 0] & 0xFF) << 4;
/* 223 */       int g = (p[pix + 1] & 0xFF) << 4;
/* 224 */       int r = (p[pix + 2] & 0xFF) << 4;
/* 225 */       int j = contest(b, g, r);
/*     */       
/* 227 */       altersingle(alpha, j, b, g, r);
/* 228 */       if (rad != 0) {
/* 229 */         alterneigh(rad, j, b, g, r);
/*     */       }
/* 231 */       pix += step;
/* 232 */       if (pix >= lim) {
/* 233 */         pix -= this.lengthcount;
/*     */       }
/* 235 */       i++;
/* 236 */       if (delta == 0)
/* 237 */         delta = 1; 
/* 238 */       if (i % delta == 0) {
/* 239 */         alpha -= alpha / this.alphadec;
/* 240 */         radius -= radius / 30;
/* 241 */         rad = radius >> 6;
/* 242 */         if (rad <= 1)
/* 243 */           rad = 0; 
/* 244 */         for (j = 0; j < rad; j++) {
/* 245 */           this.radpower[j] = alpha * (rad * rad - j * j) * 256 / rad * rad;
/*     */         }
/*     */       } 
/*     */     } 
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
/*     */   public int map(int b, int g, int r) {
/* 260 */     int bestd = 1000;
/* 261 */     int best = -1;
/* 262 */     int i = this.netindex[g];
/* 263 */     int j = i - 1;
/*     */     
/* 265 */     while (i < 256 || j >= 0) {
/* 266 */       if (i < 256) {
/* 267 */         int[] p = this.network[i];
/* 268 */         int dist = p[1] - g;
/* 269 */         if (dist >= bestd) {
/* 270 */           i = 256;
/*     */         } else {
/* 272 */           i++;
/* 273 */           if (dist < 0)
/* 274 */             dist = -dist; 
/* 275 */           int a = p[0] - b;
/* 276 */           if (a < 0)
/* 277 */             a = -a; 
/* 278 */           dist += a;
/* 279 */           if (dist < bestd) {
/* 280 */             a = p[2] - r;
/* 281 */             if (a < 0)
/* 282 */               a = -a; 
/* 283 */             dist += a;
/* 284 */             if (dist < bestd) {
/* 285 */               bestd = dist;
/* 286 */               best = p[3];
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 291 */       if (j >= 0) {
/* 292 */         int[] p = this.network[j];
/* 293 */         int dist = g - p[1];
/* 294 */         if (dist >= bestd) {
/* 295 */           j = -1; continue;
/*     */         } 
/* 297 */         j--;
/* 298 */         if (dist < 0)
/* 299 */           dist = -dist; 
/* 300 */         int a = p[0] - b;
/* 301 */         if (a < 0)
/* 302 */           a = -a; 
/* 303 */         dist += a;
/* 304 */         if (dist < bestd) {
/* 305 */           a = p[2] - r;
/* 306 */           if (a < 0)
/* 307 */             a = -a; 
/* 308 */           dist += a;
/* 309 */           if (dist < bestd) {
/* 310 */             bestd = dist;
/* 311 */             best = p[3];
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 317 */     return best;
/*     */   }
/*     */   
/*     */   public byte[] process() {
/* 321 */     learn();
/* 322 */     unbiasnet();
/* 323 */     inxbuild();
/* 324 */     return colorMap();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unbiasnet() {
/* 333 */     for (int i = 0; i < 256; i++) {
/* 334 */       this.network[i][0] = this.network[i][0] >> 4;
/* 335 */       this.network[i][1] = this.network[i][1] >> 4;
/* 336 */       this.network[i][2] = this.network[i][2] >> 4;
/* 337 */       this.network[i][3] = i;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void alterneigh(int rad, int i, int b, int g, int r) {
/* 348 */     int lo = i - rad;
/* 349 */     if (lo < -1)
/* 350 */       lo = -1; 
/* 351 */     int hi = i + rad;
/* 352 */     if (hi > 256) {
/* 353 */       hi = 256;
/*     */     }
/* 355 */     int j = i + 1;
/* 356 */     int k = i - 1;
/* 357 */     int m = 1;
/* 358 */     while (j < hi || k > lo) {
/* 359 */       int a = this.radpower[m++];
/* 360 */       if (j < hi) {
/* 361 */         int[] p = this.network[j++];
/*     */         try {
/* 363 */           p[0] = p[0] - a * (p[0] - b) / 262144;
/* 364 */           p[1] = p[1] - a * (p[1] - g) / 262144;
/* 365 */           p[2] = p[2] - a * (p[2] - r) / 262144;
/* 366 */         } catch (Exception exception) {}
/*     */       } 
/*     */       
/* 369 */       if (k > lo) {
/* 370 */         int[] p = this.network[k--];
/*     */         try {
/* 372 */           p[0] = p[0] - a * (p[0] - b) / 262144;
/* 373 */           p[1] = p[1] - a * (p[1] - g) / 262144;
/* 374 */           p[2] = p[2] - a * (p[2] - r) / 262144;
/* 375 */         } catch (Exception exception) {}
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void altersingle(int alpha, int i, int b, int g, int r) {
/* 386 */     int[] n = this.network[i];
/* 387 */     n[0] = n[0] - alpha * (n[0] - b) / 1024;
/* 388 */     n[1] = n[1] - alpha * (n[1] - g) / 1024;
/* 389 */     n[2] = n[2] - alpha * (n[2] - r) / 1024;
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
/*     */   protected int contest(int b, int g, int r) {
/* 405 */     int bestd = Integer.MAX_VALUE;
/* 406 */     int bestbiasd = bestd;
/* 407 */     int bestpos = -1;
/* 408 */     int bestbiaspos = bestpos;
/*     */     
/* 410 */     for (int i = 0; i < 256; i++) {
/* 411 */       int[] n = this.network[i];
/* 412 */       int dist = n[0] - b;
/* 413 */       if (dist < 0)
/* 414 */         dist = -dist; 
/* 415 */       int a = n[1] - g;
/* 416 */       if (a < 0)
/* 417 */         a = -a; 
/* 418 */       dist += a;
/* 419 */       a = n[2] - r;
/* 420 */       if (a < 0)
/* 421 */         a = -a; 
/* 422 */       dist += a;
/* 423 */       if (dist < bestd) {
/* 424 */         bestd = dist;
/* 425 */         bestpos = i;
/*     */       } 
/* 427 */       int biasdist = dist - (this.bias[i] >> 12);
/* 428 */       if (biasdist < bestbiasd) {
/* 429 */         bestbiasd = biasdist;
/* 430 */         bestbiaspos = i;
/*     */       } 
/* 432 */       int betafreq = this.freq[i] >> 10;
/* 433 */       this.freq[i] = this.freq[i] - betafreq;
/* 434 */       this.bias[i] = this.bias[i] + (betafreq << 10);
/*     */     } 
/* 436 */     this.freq[bestpos] = this.freq[bestpos] + 64;
/* 437 */     this.bias[bestpos] = this.bias[bestpos] - 65536;
/* 438 */     return bestbiaspos;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captch\\utils\Quant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */