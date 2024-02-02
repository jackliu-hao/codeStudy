/*     */ package com.sun.mail.auth;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MD4
/*     */ {
/*     */   private final int[] state;
/*     */   private final int[] x;
/*     */   private static final int blockSize = 64;
/*  70 */   private final byte[] buffer = new byte[64];
/*     */   
/*     */   private int bufOfs;
/*     */   
/*     */   private long bytesProcessed;
/*     */   
/*     */   private static final int S11 = 3;
/*     */   
/*     */   private static final int S12 = 7;
/*     */   
/*     */   private static final int S13 = 11;
/*     */   
/*     */   private static final int S14 = 19;
/*     */   
/*     */   private static final int S21 = 3;
/*     */   
/*     */   private static final int S22 = 5;
/*     */   
/*     */   private static final int S23 = 9;
/*     */   
/*     */   private static final int S24 = 13;
/*     */   
/*     */   private static final int S31 = 3;
/*     */   
/*     */   private static final int S32 = 9;
/*     */   private static final int S33 = 11;
/*     */   private static final int S34 = 15;
/*  97 */   private static final byte[] padding = new byte[136]; static {
/*  98 */     padding[0] = Byte.MIN_VALUE;
/*     */   }
/*     */ 
/*     */   
/*     */   public MD4() {
/* 103 */     this.state = new int[4];
/* 104 */     this.x = new int[16];
/* 105 */     implReset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] digest(byte[] in) {
/* 112 */     implReset();
/* 113 */     engineUpdate(in, 0, in.length);
/* 114 */     byte[] out = new byte[16];
/* 115 */     implDigest(out, 0);
/* 116 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void implReset() {
/* 124 */     this.state[0] = 1732584193;
/* 125 */     this.state[1] = -271733879;
/* 126 */     this.state[2] = -1732584194;
/* 127 */     this.state[3] = 271733878;
/* 128 */     this.bufOfs = 0;
/* 129 */     this.bytesProcessed = 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void implDigest(byte[] out, int ofs) {
/* 138 */     long bitsProcessed = this.bytesProcessed << 3L;
/*     */     
/* 140 */     int index = (int)this.bytesProcessed & 0x3F;
/* 141 */     int padLen = (index < 56) ? (56 - index) : (120 - index);
/* 142 */     engineUpdate(padding, 0, padLen);
/*     */ 
/*     */ 
/*     */     
/* 146 */     this.buffer[56] = (byte)(int)bitsProcessed;
/* 147 */     this.buffer[57] = (byte)(int)(bitsProcessed >> 8L);
/* 148 */     this.buffer[58] = (byte)(int)(bitsProcessed >> 16L);
/* 149 */     this.buffer[59] = (byte)(int)(bitsProcessed >> 24L);
/* 150 */     this.buffer[60] = (byte)(int)(bitsProcessed >> 32L);
/* 151 */     this.buffer[61] = (byte)(int)(bitsProcessed >> 40L);
/* 152 */     this.buffer[62] = (byte)(int)(bitsProcessed >> 48L);
/* 153 */     this.buffer[63] = (byte)(int)(bitsProcessed >> 56L);
/* 154 */     implCompress(this.buffer, 0);
/*     */ 
/*     */     
/* 157 */     for (int i = 0; i < this.state.length; i++) {
/* 158 */       int x = this.state[i];
/* 159 */       out[ofs++] = (byte)x;
/* 160 */       out[ofs++] = (byte)(x >> 8);
/* 161 */       out[ofs++] = (byte)(x >> 16);
/* 162 */       out[ofs++] = (byte)(x >> 24);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void engineUpdate(byte[] b, int ofs, int len) {
/* 167 */     if (len == 0) {
/*     */       return;
/*     */     }
/* 170 */     if (ofs < 0 || len < 0 || ofs > b.length - len) {
/* 171 */       throw new ArrayIndexOutOfBoundsException();
/*     */     }
/* 173 */     if (this.bytesProcessed < 0L) {
/* 174 */       implReset();
/*     */     }
/* 176 */     this.bytesProcessed += len;
/*     */     
/* 178 */     if (this.bufOfs != 0) {
/* 179 */       int n = Math.min(len, 64 - this.bufOfs);
/* 180 */       System.arraycopy(b, ofs, this.buffer, this.bufOfs, n);
/* 181 */       this.bufOfs += n;
/* 182 */       ofs += n;
/* 183 */       len -= n;
/* 184 */       if (this.bufOfs >= 64) {
/*     */         
/* 186 */         implCompress(this.buffer, 0);
/* 187 */         this.bufOfs = 0;
/*     */       } 
/*     */     } 
/*     */     
/* 191 */     while (len >= 64) {
/* 192 */       implCompress(b, ofs);
/* 193 */       len -= 64;
/* 194 */       ofs += 64;
/*     */     } 
/*     */     
/* 197 */     if (len > 0) {
/* 198 */       System.arraycopy(b, ofs, this.buffer, 0, len);
/* 199 */       this.bufOfs = len;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int FF(int a, int b, int c, int d, int x, int s) {
/* 204 */     a += (b & c | (b ^ 0xFFFFFFFF) & d) + x;
/* 205 */     return a << s | a >>> 32 - s;
/*     */   }
/*     */   
/*     */   private static int GG(int a, int b, int c, int d, int x, int s) {
/* 209 */     a += (b & c | b & d | c & d) + x + 1518500249;
/* 210 */     return a << s | a >>> 32 - s;
/*     */   }
/*     */   
/*     */   private static int HH(int a, int b, int c, int d, int x, int s) {
/* 214 */     a += (b ^ c ^ d) + x + 1859775393;
/* 215 */     return a << s | a >>> 32 - s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void implCompress(byte[] buf, int ofs) {
/* 225 */     for (int xfs = 0; xfs < this.x.length; xfs++) {
/* 226 */       this.x[xfs] = buf[ofs] & 0xFF | (buf[ofs + 1] & 0xFF) << 8 | (buf[ofs + 2] & 0xFF) << 16 | (buf[ofs + 3] & 0xFF) << 24;
/*     */       
/* 228 */       ofs += 4;
/*     */     } 
/*     */     
/* 231 */     int a = this.state[0];
/* 232 */     int b = this.state[1];
/* 233 */     int c = this.state[2];
/* 234 */     int d = this.state[3];
/*     */ 
/*     */     
/* 237 */     a = FF(a, b, c, d, this.x[0], 3);
/* 238 */     d = FF(d, a, b, c, this.x[1], 7);
/* 239 */     c = FF(c, d, a, b, this.x[2], 11);
/* 240 */     b = FF(b, c, d, a, this.x[3], 19);
/* 241 */     a = FF(a, b, c, d, this.x[4], 3);
/* 242 */     d = FF(d, a, b, c, this.x[5], 7);
/* 243 */     c = FF(c, d, a, b, this.x[6], 11);
/* 244 */     b = FF(b, c, d, a, this.x[7], 19);
/* 245 */     a = FF(a, b, c, d, this.x[8], 3);
/* 246 */     d = FF(d, a, b, c, this.x[9], 7);
/* 247 */     c = FF(c, d, a, b, this.x[10], 11);
/* 248 */     b = FF(b, c, d, a, this.x[11], 19);
/* 249 */     a = FF(a, b, c, d, this.x[12], 3);
/* 250 */     d = FF(d, a, b, c, this.x[13], 7);
/* 251 */     c = FF(c, d, a, b, this.x[14], 11);
/* 252 */     b = FF(b, c, d, a, this.x[15], 19);
/*     */ 
/*     */     
/* 255 */     a = GG(a, b, c, d, this.x[0], 3);
/* 256 */     d = GG(d, a, b, c, this.x[4], 5);
/* 257 */     c = GG(c, d, a, b, this.x[8], 9);
/* 258 */     b = GG(b, c, d, a, this.x[12], 13);
/* 259 */     a = GG(a, b, c, d, this.x[1], 3);
/* 260 */     d = GG(d, a, b, c, this.x[5], 5);
/* 261 */     c = GG(c, d, a, b, this.x[9], 9);
/* 262 */     b = GG(b, c, d, a, this.x[13], 13);
/* 263 */     a = GG(a, b, c, d, this.x[2], 3);
/* 264 */     d = GG(d, a, b, c, this.x[6], 5);
/* 265 */     c = GG(c, d, a, b, this.x[10], 9);
/* 266 */     b = GG(b, c, d, a, this.x[14], 13);
/* 267 */     a = GG(a, b, c, d, this.x[3], 3);
/* 268 */     d = GG(d, a, b, c, this.x[7], 5);
/* 269 */     c = GG(c, d, a, b, this.x[11], 9);
/* 270 */     b = GG(b, c, d, a, this.x[15], 13);
/*     */ 
/*     */     
/* 273 */     a = HH(a, b, c, d, this.x[0], 3);
/* 274 */     d = HH(d, a, b, c, this.x[8], 9);
/* 275 */     c = HH(c, d, a, b, this.x[4], 11);
/* 276 */     b = HH(b, c, d, a, this.x[12], 15);
/* 277 */     a = HH(a, b, c, d, this.x[2], 3);
/* 278 */     d = HH(d, a, b, c, this.x[10], 9);
/* 279 */     c = HH(c, d, a, b, this.x[6], 11);
/* 280 */     b = HH(b, c, d, a, this.x[14], 15);
/* 281 */     a = HH(a, b, c, d, this.x[1], 3);
/* 282 */     d = HH(d, a, b, c, this.x[9], 9);
/* 283 */     c = HH(c, d, a, b, this.x[5], 11);
/* 284 */     b = HH(b, c, d, a, this.x[13], 15);
/* 285 */     a = HH(a, b, c, d, this.x[3], 3);
/* 286 */     d = HH(d, a, b, c, this.x[11], 9);
/* 287 */     c = HH(c, d, a, b, this.x[7], 11);
/* 288 */     b = HH(b, c, d, a, this.x[15], 15);
/*     */     
/* 290 */     this.state[0] = this.state[0] + a;
/* 291 */     this.state[1] = this.state[1] + b;
/* 292 */     this.state[2] = this.state[2] + c;
/* 293 */     this.state[3] = this.state[3] + d;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\auth\MD4.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */