/*     */ package cn.hutool.core.img.gif;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NeuQuant
/*     */ {
/*     */   protected static final int NETSIZE = 256;
/*     */   protected static final int PRIME1 = 499;
/*     */   protected static final int PRIME2 = 491;
/*     */   protected static final int PRIME3 = 487;
/*     */   protected static final int PRIME4 = 503;
/*     */   protected static final int MINPICTUREBYTES = 1509;
/*     */   protected static final int MAXNETPOS = 255;
/*     */   protected static final int NETBIASSHIFT = 4;
/*     */   protected static final int NCYCLES = 100;
/*     */   protected static final int INTBIASSHIFT = 16;
/*     */   protected static final int INTBIAS = 65536;
/*     */   protected static final int GAMMASHIFT = 10;
/*     */   protected static final int GAMMA = 1024;
/*     */   protected static final int BETASHIFT = 10;
/*     */   protected static final int BETA = 64;
/*     */   protected static final int BETAGAMMA = 65536;
/*     */   protected static final int INITRAD = 32;
/*     */   protected static final int RADIUSBIASSHIFT = 6;
/*     */   protected static final int RADIUSBIAS = 64;
/*     */   protected static final int INITRADIUS = 2048;
/*     */   protected static final int RADIUSDEC = 30;
/*     */   protected static final int ALPHABIASSHIFT = 10;
/*     */   protected static final int INITALPHA = 1024;
/*     */   protected int alphadec;
/*     */   protected static final int RADBIASSHIFT = 8;
/*     */   protected static final int RADBIAS = 256;
/*     */   protected static final int ALPHARADBSHIFT = 18;
/*     */   protected static final int ALPHARADBIAS = 262144;
/*     */   protected byte[] thepicture;
/*     */   protected int lengthcount;
/*     */   protected int samplefac;
/*     */   protected int[][] network;
/* 103 */   protected int[] netindex = new int[256];
/*     */ 
/*     */   
/* 106 */   protected int[] bias = new int[256];
/*     */   
/* 108 */   protected int[] freq = new int[256];
/* 109 */   protected int[] radpower = new int[32];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NeuQuant(byte[] thepic, int len, int sample) {
/* 119 */     this.thepicture = thepic;
/* 120 */     this.lengthcount = len;
/* 121 */     this.samplefac = sample;
/*     */     
/* 123 */     this.network = new int[256][];
/* 124 */     for (int i = 0; i < 256; i++) {
/* 125 */       this.network[i] = new int[4];
/* 126 */       int[] p = this.network[i];
/* 127 */       p[2] = (i << 12) / 256; p[1] = (i << 12) / 256; p[0] = (i << 12) / 256;
/* 128 */       this.freq[i] = 256;
/* 129 */       this.bias[i] = 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public byte[] colorMap() {
/* 134 */     byte[] map = new byte[768];
/* 135 */     int[] index = new int[256];
/* 136 */     for (int i = 0; i < 256; i++)
/* 137 */       index[this.network[i][3]] = i; 
/* 138 */     int k = 0;
/* 139 */     for (int j = 0; j < 256; j++) {
/* 140 */       int m = index[j];
/* 141 */       map[k++] = (byte)this.network[m][0];
/* 142 */       map[k++] = (byte)this.network[m][1];
/* 143 */       map[k++] = (byte)this.network[m][2];
/*     */     } 
/* 145 */     return map;
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
/* 157 */     int previouscol = 0;
/* 158 */     int startpos = 0;
/* 159 */     for (int i = 0; i < 256; i++) {
/* 160 */       int[] p = this.network[i];
/* 161 */       int smallpos = i;
/* 162 */       int smallval = p[1];
/*     */       int k;
/* 164 */       for (k = i + 1; k < 256; k++) {
/* 165 */         int[] arrayOfInt = this.network[k];
/* 166 */         if (arrayOfInt[1] < smallval) {
/* 167 */           smallpos = k;
/* 168 */           smallval = arrayOfInt[1];
/*     */         } 
/*     */       } 
/* 171 */       int[] q = this.network[smallpos];
/*     */       
/* 173 */       if (i != smallpos) {
/* 174 */         k = q[0];
/* 175 */         q[0] = p[0];
/* 176 */         p[0] = k;
/* 177 */         k = q[1];
/* 178 */         q[1] = p[1];
/* 179 */         p[1] = k;
/* 180 */         k = q[2];
/* 181 */         q[2] = p[2];
/* 182 */         p[2] = k;
/* 183 */         k = q[3];
/* 184 */         q[3] = p[3];
/* 185 */         p[3] = k;
/*     */       } 
/*     */       
/* 188 */       if (smallval != previouscol) {
/* 189 */         this.netindex[previouscol] = startpos + i >> 1;
/* 190 */         for (k = previouscol + 1; k < smallval; k++)
/* 191 */           this.netindex[k] = i; 
/* 192 */         previouscol = smallval;
/* 193 */         startpos = i;
/*     */       } 
/*     */     } 
/* 196 */     this.netindex[previouscol] = startpos + 255 >> 1;
/* 197 */     for (int j = previouscol + 1; j < 256; j++) {
/* 198 */       this.netindex[j] = 255;
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
/* 210 */     if (this.lengthcount < 1509)
/* 211 */       this.samplefac = 1; 
/* 212 */     this.alphadec = 30 + (this.samplefac - 1) / 3;
/* 213 */     byte[] p = this.thepicture;
/* 214 */     int pix = 0;
/* 215 */     int lim = this.lengthcount;
/* 216 */     int samplepixels = this.lengthcount / 3 * this.samplefac;
/* 217 */     int delta = samplepixels / 100;
/* 218 */     int alpha = 1024;
/* 219 */     int radius = 2048;
/*     */     
/* 221 */     int rad = radius >> 6; int i;
/* 222 */     for (i = 0; i < rad; i++) {
/* 223 */       this.radpower[i] = alpha * (rad * rad - i * i) * 256 / rad * rad;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 228 */     if (this.lengthcount < 1509) {
/* 229 */       step = 3;
/* 230 */     } else if (this.lengthcount % 499 != 0) {
/* 231 */       step = 1497;
/*     */     }
/* 233 */     else if (this.lengthcount % 491 != 0) {
/* 234 */       step = 1473;
/*     */     }
/* 236 */     else if (this.lengthcount % 487 != 0) {
/* 237 */       step = 1461;
/*     */     } else {
/* 239 */       step = 1509;
/*     */     } 
/*     */ 
/*     */     
/* 243 */     i = 0;
/* 244 */     while (i < samplepixels) {
/* 245 */       int b = (p[pix] & 0xFF) << 4;
/* 246 */       int g = (p[pix + 1] & 0xFF) << 4;
/* 247 */       int r = (p[pix + 2] & 0xFF) << 4;
/* 248 */       int j = contest(b, g, r);
/*     */       
/* 250 */       altersingle(alpha, j, b, g, r);
/* 251 */       if (rad != 0) {
/* 252 */         alterneigh(rad, j, b, g, r);
/*     */       }
/* 254 */       pix += step;
/* 255 */       if (pix >= lim) {
/* 256 */         pix -= this.lengthcount;
/*     */       }
/* 258 */       i++;
/* 259 */       if (delta == 0)
/* 260 */         delta = 1; 
/* 261 */       if (i % delta == 0) {
/* 262 */         alpha -= alpha / this.alphadec;
/* 263 */         radius -= radius / 30;
/* 264 */         rad = radius >> 6;
/* 265 */         if (rad <= 1)
/* 266 */           rad = 0; 
/* 267 */         for (j = 0; j < rad; j++) {
/* 268 */           this.radpower[j] = alpha * (rad * rad - j * j) * 256 / rad * rad;
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
/* 283 */     int bestd = 1000;
/* 284 */     int best = -1;
/* 285 */     int i = this.netindex[g];
/* 286 */     int j = i - 1;
/*     */     
/* 288 */     while (i < 256 || j >= 0) {
/* 289 */       if (i < 256) {
/* 290 */         int[] p = this.network[i];
/* 291 */         int dist = p[1] - g;
/* 292 */         if (dist >= bestd) {
/* 293 */           i = 256;
/*     */         } else {
/* 295 */           i++;
/* 296 */           if (dist < 0)
/* 297 */             dist = -dist; 
/* 298 */           int a = p[0] - b;
/* 299 */           if (a < 0)
/* 300 */             a = -a; 
/* 301 */           dist += a;
/* 302 */           if (dist < bestd) {
/* 303 */             a = p[2] - r;
/* 304 */             if (a < 0)
/* 305 */               a = -a; 
/* 306 */             dist += a;
/* 307 */             if (dist < bestd) {
/* 308 */               bestd = dist;
/* 309 */               best = p[3];
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 314 */       if (j >= 0) {
/* 315 */         int[] p = this.network[j];
/* 316 */         int dist = g - p[1];
/* 317 */         if (dist >= bestd) {
/* 318 */           j = -1; continue;
/*     */         } 
/* 320 */         j--;
/* 321 */         if (dist < 0)
/* 322 */           dist = -dist; 
/* 323 */         int a = p[0] - b;
/* 324 */         if (a < 0)
/* 325 */           a = -a; 
/* 326 */         dist += a;
/* 327 */         if (dist < bestd) {
/* 328 */           a = p[2] - r;
/* 329 */           if (a < 0)
/* 330 */             a = -a; 
/* 331 */           dist += a;
/* 332 */           if (dist < bestd) {
/* 333 */             bestd = dist;
/* 334 */             best = p[3];
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 340 */     return best;
/*     */   }
/*     */   
/*     */   public byte[] process() {
/* 344 */     learn();
/* 345 */     unbiasnet();
/* 346 */     inxbuild();
/* 347 */     return colorMap();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void unbiasnet() {
/* 353 */     for (int i = 0; i < 256; i++) {
/* 354 */       this.network[i][0] = this.network[i][0] >> 4;
/* 355 */       this.network[i][1] = this.network[i][1] >> 4;
/* 356 */       this.network[i][2] = this.network[i][2] >> 4;
/* 357 */       this.network[i][3] = i;
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
/* 368 */     int lo = i - rad;
/* 369 */     if (lo < -1)
/* 370 */       lo = -1; 
/* 371 */     int hi = i + rad;
/* 372 */     if (hi > 256) {
/* 373 */       hi = 256;
/*     */     }
/* 375 */     int j = i + 1;
/* 376 */     int k = i - 1;
/* 377 */     int m = 1;
/* 378 */     while (j < hi || k > lo) {
/* 379 */       int a = this.radpower[m++];
/* 380 */       if (j < hi) {
/* 381 */         int[] p = this.network[j++];
/*     */         try {
/* 383 */           p[0] = p[0] - a * (p[0] - b) / 262144;
/* 384 */           p[1] = p[1] - a * (p[1] - g) / 262144;
/* 385 */           p[2] = p[2] - a * (p[2] - r) / 262144;
/* 386 */         } catch (Exception exception) {}
/*     */       } 
/*     */       
/* 389 */       if (k > lo) {
/* 390 */         int[] p = this.network[k--];
/*     */         try {
/* 392 */           p[0] = p[0] - a * (p[0] - b) / 262144;
/* 393 */           p[1] = p[1] - a * (p[1] - g) / 262144;
/* 394 */           p[2] = p[2] - a * (p[2] - r) / 262144;
/* 395 */         } catch (Exception exception) {}
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
/* 406 */     int[] n = this.network[i];
/* 407 */     n[0] = n[0] - alpha * (n[0] - b) / 1024;
/* 408 */     n[1] = n[1] - alpha * (n[1] - g) / 1024;
/* 409 */     n[2] = n[2] - alpha * (n[2] - r) / 1024;
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
/* 425 */     int bestd = Integer.MAX_VALUE;
/* 426 */     int bestbiasd = bestd;
/* 427 */     int bestpos = -1;
/* 428 */     int bestbiaspos = bestpos;
/*     */     
/* 430 */     for (int i = 0; i < 256; i++) {
/* 431 */       int[] n = this.network[i];
/* 432 */       int dist = n[0] - b;
/* 433 */       if (dist < 0)
/* 434 */         dist = -dist; 
/* 435 */       int a = n[1] - g;
/* 436 */       if (a < 0)
/* 437 */         a = -a; 
/* 438 */       dist += a;
/* 439 */       a = n[2] - r;
/* 440 */       if (a < 0)
/* 441 */         a = -a; 
/* 442 */       dist += a;
/* 443 */       if (dist < bestd) {
/* 444 */         bestd = dist;
/* 445 */         bestpos = i;
/*     */       } 
/* 447 */       int biasdist = dist - (this.bias[i] >> 12);
/* 448 */       if (biasdist < bestbiasd) {
/* 449 */         bestbiasd = biasdist;
/* 450 */         bestbiaspos = i;
/*     */       } 
/* 452 */       int betafreq = this.freq[i] >> 10;
/* 453 */       this.freq[i] = this.freq[i] - betafreq;
/* 454 */       this.bias[i] = this.bias[i] + (betafreq << 10);
/*     */     } 
/* 456 */     this.freq[bestpos] = this.freq[bestpos] + 64;
/* 457 */     this.bias[bestpos] = this.bias[bestpos] - 65536;
/* 458 */     return bestbiaspos;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\img\gif\NeuQuant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */