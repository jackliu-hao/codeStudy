/*     */ package cn.hutool.core.img.gif;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class LZWEncoder
/*     */ {
/*     */   private static final int EOF = -1;
/*     */   private final int imgW;
/*     */   private final int imgH;
/*     */   private final byte[] pixAry;
/*     */   private final int initCodeSize;
/*     */   private int remaining;
/*     */   private int curPixel;
/*     */   static final int BITS = 12;
/*     */   static final int HSIZE = 5003;
/*     */   int n_bits;
/*  45 */   int maxbits = 12;
/*     */   int maxcode;
/*  47 */   int maxmaxcode = 4096;
/*     */   
/*  49 */   int[] htab = new int[5003];
/*  50 */   int[] codetab = new int[5003];
/*     */   
/*  52 */   int hsize = 5003;
/*     */   
/*  54 */   int free_ent = 0;
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
/*  92 */   int cur_accum = 0;
/*  93 */   int cur_bits = 0;
/*     */   
/*  95 */   final int[] masks = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535 };
/*     */ 
/*     */ 
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
/* 119 */   byte[] accum = new byte[256];
/*     */ 
/*     */   
/*     */   LZWEncoder(int width, int height, byte[] pixels, int color_depth) {
/* 123 */     this.imgW = width;
/* 124 */     this.imgH = height;
/* 125 */     this.pixAry = pixels;
/* 126 */     this.initCodeSize = Math.max(2, color_depth);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void char_out(byte c, OutputStream outs) throws IOException {
/* 132 */     this.accum[this.a_count++] = c;
/* 133 */     if (this.a_count >= 254) {
/* 134 */       flush_char(outs);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void cl_block(OutputStream outs) throws IOException {
/* 141 */     cl_hash(this.hsize);
/* 142 */     this.free_ent = this.ClearCode + 2;
/* 143 */     this.clear_flg = true;
/*     */     
/* 145 */     output(this.ClearCode, outs);
/*     */   }
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
/*     */   void compress(int init_bits, OutputStream outs) throws IOException {
/* 164 */     this.g_init_bits = init_bits;
/*     */ 
/*     */     
/* 167 */     this.clear_flg = false;
/* 168 */     this.n_bits = this.g_init_bits;
/* 169 */     this.maxcode = MAXCODE(this.n_bits);
/*     */     
/* 171 */     this.ClearCode = 1 << init_bits - 1;
/* 172 */     this.EOFCode = this.ClearCode + 1;
/* 173 */     this.free_ent = this.ClearCode + 2;
/*     */     
/* 175 */     this.a_count = 0;
/*     */     
/* 177 */     int ent = nextPixel();
/*     */     
/* 179 */     int hshift = 0; int fcode;
/* 180 */     for (fcode = this.hsize; fcode < 65536; fcode *= 2)
/* 181 */       hshift++; 
/* 182 */     hshift = 8 - hshift;
/*     */     
/* 184 */     int hsize_reg = this.hsize;
/* 185 */     cl_hash(hsize_reg);
/*     */     
/* 187 */     output(this.ClearCode, outs);
/*     */     int c;
/* 189 */     label35: while ((c = nextPixel()) != -1) {
/* 190 */       fcode = (c << this.maxbits) + ent;
/* 191 */       int i = c << hshift ^ ent;
/*     */       
/* 193 */       if (this.htab[i] == fcode) {
/* 194 */         ent = this.codetab[i]; continue;
/*     */       } 
/* 196 */       if (this.htab[i] >= 0) {
/*     */         
/* 198 */         int disp = hsize_reg - i;
/* 199 */         if (i == 0)
/* 200 */           disp = 1; 
/*     */         do {
/* 202 */           if ((i -= disp) < 0) {
/* 203 */             i += hsize_reg;
/*     */           }
/* 205 */           if (this.htab[i] == fcode) {
/* 206 */             ent = this.codetab[i];
/*     */             continue label35;
/*     */           } 
/* 209 */         } while (this.htab[i] >= 0);
/*     */       } 
/* 211 */       output(ent, outs);
/* 212 */       ent = c;
/* 213 */       if (this.free_ent < this.maxmaxcode) {
/* 214 */         this.codetab[i] = this.free_ent++;
/* 215 */         this.htab[i] = fcode; continue;
/*     */       } 
/* 217 */       cl_block(outs);
/*     */     } 
/*     */     
/* 220 */     output(ent, outs);
/* 221 */     output(this.EOFCode, outs);
/*     */   }
/*     */ 
/*     */   
/*     */   void encode(OutputStream os) throws IOException {
/* 226 */     os.write(this.initCodeSize);
/*     */     
/* 228 */     this.remaining = this.imgW * this.imgH;
/* 229 */     this.curPixel = 0;
/*     */     
/* 231 */     compress(this.initCodeSize + 1, os);
/*     */     
/* 233 */     os.write(0);
/*     */   }
/*     */ 
/*     */   
/*     */   void flush_char(OutputStream outs) throws IOException {
/* 238 */     if (this.a_count > 0) {
/* 239 */       outs.write(this.a_count);
/* 240 */       outs.write(this.accum, 0, this.a_count);
/* 241 */       this.a_count = 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   final int MAXCODE(int n_bits) {
/* 246 */     return (1 << n_bits) - 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int nextPixel() {
/* 253 */     if (this.remaining == 0) {
/* 254 */       return -1;
/*     */     }
/* 256 */     this.remaining--;
/*     */     
/* 258 */     byte pix = this.pixAry[this.curPixel++];
/*     */     
/* 260 */     return pix & 0xFF;
/*     */   }
/*     */   
/*     */   void output(int code, OutputStream outs) throws IOException {
/* 264 */     this.cur_accum &= this.masks[this.cur_bits];
/*     */     
/* 266 */     if (this.cur_bits > 0) {
/* 267 */       this.cur_accum |= code << this.cur_bits;
/*     */     } else {
/* 269 */       this.cur_accum = code;
/*     */     } 
/* 271 */     this.cur_bits += this.n_bits;
/*     */     
/* 273 */     while (this.cur_bits >= 8) {
/* 274 */       char_out((byte)(this.cur_accum & 0xFF), outs);
/* 275 */       this.cur_accum >>= 8;
/* 276 */       this.cur_bits -= 8;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 281 */     if (this.free_ent > this.maxcode || this.clear_flg) {
/* 282 */       if (this.clear_flg) {
/* 283 */         this.maxcode = MAXCODE(this.n_bits = this.g_init_bits);
/* 284 */         this.clear_flg = false;
/*     */       } else {
/* 286 */         this.n_bits++;
/* 287 */         if (this.n_bits == this.maxbits) {
/* 288 */           this.maxcode = this.maxmaxcode;
/*     */         } else {
/* 290 */           this.maxcode = MAXCODE(this.n_bits);
/*     */         } 
/*     */       } 
/*     */     }
/* 294 */     if (code == this.EOFCode) {
/*     */       
/* 296 */       while (this.cur_bits > 0) {
/* 297 */         char_out((byte)(this.cur_accum & 0xFF), outs);
/* 298 */         this.cur_accum >>= 8;
/* 299 */         this.cur_bits -= 8;
/*     */       } 
/*     */       
/* 302 */       flush_char(outs);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\img\gif\LZWEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */