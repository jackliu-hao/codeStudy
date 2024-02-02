/*     */ package com.wf.captcha.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Encoder
/*     */ {
/*     */   private static final int EOF = -1;
/*     */   private int imgW;
/*     */   private int imgH;
/*     */   private byte[] pixAry;
/*     */   private int initCodeSize;
/*     */   private int remaining;
/*     */   private int curPixel;
/*     */   static final int BITS = 12;
/*     */   static final int HSIZE = 5003;
/*     */   int n_bits;
/*  23 */   int maxbits = 12;
/*     */   int maxcode;
/*  25 */   int maxmaxcode = 4096;
/*     */   
/*  27 */   int[] htab = new int[5003];
/*  28 */   int[] codetab = new int[5003];
/*     */   
/*  30 */   int hsize = 5003;
/*     */   
/*  32 */   int free_ent = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean clear_flg = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int g_init_bits;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int ClearCode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int EOFCode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   int cur_accum = 0;
/*  71 */   int cur_bits = 0;
/*     */   
/*  73 */   int[] masks = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int a_count;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  97 */   byte[] accum = new byte[256];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Encoder(int width, int height, byte[] pixels, int color_depth) {
/* 108 */     this.imgW = width;
/* 109 */     this.imgH = height;
/* 110 */     this.pixAry = pixels;
/* 111 */     this.initCodeSize = Math.max(2, color_depth);
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
/*     */   void char_out(byte c, OutputStream outs) throws IOException {
/* 123 */     this.accum[this.a_count++] = c;
/* 124 */     if (this.a_count >= 254) {
/* 125 */       flush_char(outs);
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
/*     */   void cl_block(OutputStream outs) throws IOException {
/* 137 */     cl_hash(this.hsize);
/* 138 */     this.free_ent = this.ClearCode + 2;
/* 139 */     this.clear_flg = true;
/*     */     
/* 141 */     output(this.ClearCode, outs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void cl_hash(int hsize) {
/* 150 */     for (int i = 0; i < hsize; i++) {
/* 151 */       this.htab[i] = -1;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void compress(int init_bits, OutputStream outs) throws IOException {
/* 169 */     this.g_init_bits = init_bits;
/*     */ 
/*     */     
/* 172 */     this.clear_flg = false;
/* 173 */     this.n_bits = this.g_init_bits;
/* 174 */     this.maxcode = MAXCODE(this.n_bits);
/*     */     
/* 176 */     this.ClearCode = 1 << init_bits - 1;
/* 177 */     this.EOFCode = this.ClearCode + 1;
/* 178 */     this.free_ent = this.ClearCode + 2;
/*     */     
/* 180 */     this.a_count = 0;
/*     */     
/* 182 */     int ent = nextPixel();
/*     */     
/* 184 */     int hshift = 0; int fcode;
/* 185 */     for (fcode = this.hsize; fcode < 65536; fcode *= 2)
/* 186 */       hshift++; 
/* 187 */     hshift = 8 - hshift;
/*     */     
/* 189 */     int hsize_reg = this.hsize;
/* 190 */     cl_hash(hsize_reg);
/*     */     
/* 192 */     output(this.ClearCode, outs);
/*     */     
/*     */     int c;
/* 195 */     label35: while ((c = nextPixel()) != -1) {
/* 196 */       fcode = (c << this.maxbits) + ent;
/* 197 */       int i = c << hshift ^ ent;
/*     */       
/* 199 */       if (this.htab[i] == fcode) {
/* 200 */         ent = this.codetab[i]; continue;
/*     */       } 
/* 202 */       if (this.htab[i] >= 0) {
/*     */         
/* 204 */         int disp = hsize_reg - i;
/* 205 */         if (i == 0)
/* 206 */           disp = 1; 
/*     */         do {
/* 208 */           if ((i -= disp) < 0) {
/* 209 */             i += hsize_reg;
/*     */           }
/* 211 */           if (this.htab[i] == fcode) {
/* 212 */             ent = this.codetab[i];
/*     */             continue label35;
/*     */           } 
/* 215 */         } while (this.htab[i] >= 0);
/*     */       } 
/* 217 */       output(ent, outs);
/* 218 */       ent = c;
/* 219 */       if (this.free_ent < this.maxmaxcode) {
/* 220 */         this.codetab[i] = this.free_ent++;
/* 221 */         this.htab[i] = fcode; continue;
/*     */       } 
/* 223 */       cl_block(outs);
/*     */     } 
/*     */     
/* 226 */     output(ent, outs);
/* 227 */     output(this.EOFCode, outs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void encode(OutputStream os) throws IOException {
/* 237 */     os.write(this.initCodeSize);
/*     */     
/* 239 */     this.remaining = this.imgW * this.imgH;
/* 240 */     this.curPixel = 0;
/*     */     
/* 242 */     compress(this.initCodeSize + 1, os);
/*     */     
/* 244 */     os.write(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void flush_char(OutputStream outs) throws IOException {
/* 254 */     if (this.a_count > 0) {
/* 255 */       outs.write(this.a_count);
/* 256 */       outs.write(this.accum, 0, this.a_count);
/* 257 */       this.a_count = 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final int MAXCODE(int n_bits) {
/* 266 */     return (1 << n_bits) - 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int nextPixel() {
/* 277 */     if (this.remaining == 0) {
/* 278 */       return -1;
/*     */     }
/* 280 */     this.remaining--;
/*     */     
/* 282 */     byte pix = this.pixAry[this.curPixel++];
/*     */     
/* 284 */     return pix & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void output(int code, OutputStream outs) throws IOException {
/* 293 */     this.cur_accum &= this.masks[this.cur_bits];
/*     */     
/* 295 */     if (this.cur_bits > 0) {
/* 296 */       this.cur_accum |= code << this.cur_bits;
/*     */     } else {
/* 298 */       this.cur_accum = code;
/*     */     } 
/* 300 */     this.cur_bits += this.n_bits;
/*     */     
/* 302 */     while (this.cur_bits >= 8) {
/* 303 */       char_out((byte)(this.cur_accum & 0xFF), outs);
/* 304 */       this.cur_accum >>= 8;
/* 305 */       this.cur_bits -= 8;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 310 */     if (this.free_ent > this.maxcode || this.clear_flg) {
/* 311 */       if (this.clear_flg) {
/* 312 */         this.maxcode = MAXCODE(this.n_bits = this.g_init_bits);
/* 313 */         this.clear_flg = false;
/*     */       } else {
/* 315 */         this.n_bits++;
/* 316 */         if (this.n_bits == this.maxbits) {
/* 317 */           this.maxcode = this.maxmaxcode;
/*     */         } else {
/* 319 */           this.maxcode = MAXCODE(this.n_bits);
/*     */         } 
/*     */       } 
/*     */     }
/* 323 */     if (code == this.EOFCode) {
/*     */       
/* 325 */       while (this.cur_bits > 0) {
/* 326 */         char_out((byte)(this.cur_accum & 0xFF), outs);
/* 327 */         this.cur_accum >>= 8;
/* 328 */         this.cur_bits -= 8;
/*     */       } 
/*     */       
/* 331 */       flush_char(outs);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captch\\utils\Encoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */