/*     */ package com.wf.captcha.utils;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.FileOutputStream;
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
/*     */ public class GifEncoder
/*     */ {
/*     */   protected int width;
/*     */   protected int height;
/*  33 */   protected Color transparent = null;
/*     */   protected int transIndex;
/*  35 */   protected int repeat = -1;
/*  36 */   protected int delay = 0;
/*     */   protected boolean started = false;
/*     */   protected OutputStream out;
/*     */   protected BufferedImage image;
/*     */   protected byte[] pixels;
/*     */   protected byte[] indexedPixels;
/*     */   protected int colorDepth;
/*     */   protected byte[] colorTab;
/*  44 */   protected boolean[] usedEntry = new boolean[256];
/*  45 */   protected int palSize = 7;
/*  46 */   protected int dispose = -1;
/*     */   protected boolean closeStream = false;
/*     */   protected boolean firstFrame = true;
/*     */   protected boolean sizeSet = false;
/*  50 */   protected int sample = 10;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDelay(int ms) {
/*  59 */     this.delay = Math.round(ms / 10.0F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDispose(int code) {
/*  70 */     if (code >= 0) {
/*  71 */       this.dispose = code;
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
/*     */   public void setRepeat(int iter) {
/*  84 */     if (iter >= 0) {
/*  85 */       this.repeat = iter;
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
/*     */   public void setTransparent(Color c) {
/* 101 */     this.transparent = c;
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
/*     */   public boolean addFrame(BufferedImage im) {
/* 115 */     if (im == null || !this.started) {
/* 116 */       return false;
/*     */     }
/* 118 */     boolean ok = true;
/*     */     try {
/* 120 */       if (!this.sizeSet)
/*     */       {
/* 122 */         setSize(im.getWidth(), im.getHeight());
/*     */       }
/* 124 */       this.image = im;
/* 125 */       getImagePixels();
/* 126 */       analyzePixels();
/* 127 */       if (this.firstFrame) {
/* 128 */         writeLSD();
/* 129 */         writePalette();
/* 130 */         if (this.repeat >= 0)
/*     */         {
/* 132 */           writeNetscapeExt();
/*     */         }
/*     */       } 
/* 135 */       writeGraphicCtrlExt();
/* 136 */       writeImageDesc();
/* 137 */       if (!this.firstFrame) {
/* 138 */         writePalette();
/*     */       }
/* 140 */       writePixels();
/* 141 */       this.firstFrame = false;
/* 142 */     } catch (IOException e) {
/* 143 */       ok = false;
/*     */     } 
/*     */     
/* 146 */     return ok;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean outFlush() {
/* 151 */     boolean ok = true;
/*     */     try {
/* 153 */       this.out.flush();
/* 154 */       return ok;
/* 155 */     } catch (IOException e) {
/* 156 */       ok = false;
/*     */ 
/*     */       
/* 159 */       return ok;
/*     */     } 
/*     */   }
/*     */   public byte[] getFrameByteArray() {
/* 163 */     return ((ByteArrayOutputStream)this.out).toByteArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean finish() {
/* 174 */     if (!this.started) return false; 
/* 175 */     boolean ok = true;
/* 176 */     this.started = false;
/*     */     try {
/* 178 */       this.out.write(59);
/* 179 */       this.out.flush();
/* 180 */       if (this.closeStream) {
/* 181 */         this.out.close();
/*     */       }
/* 183 */     } catch (IOException e) {
/* 184 */       ok = false;
/*     */     } 
/*     */     
/* 187 */     return ok;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 192 */     this.transIndex = 0;
/* 193 */     this.out = null;
/* 194 */     this.image = null;
/* 195 */     this.pixels = null;
/* 196 */     this.indexedPixels = null;
/* 197 */     this.colorTab = null;
/* 198 */     this.closeStream = false;
/* 199 */     this.firstFrame = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFrameRate(float fps) {
/* 209 */     if (fps != 0.0F) {
/* 210 */       this.delay = Math.round(100.0F / fps);
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
/*     */   public void setQuality(int quality) {
/* 225 */     if (quality < 1) quality = 1; 
/* 226 */     this.sample = quality;
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
/*     */   public void setSize(int w, int h) {
/* 238 */     if (this.started && !this.firstFrame)
/* 239 */       return;  this.width = w;
/* 240 */     this.height = h;
/* 241 */     if (this.width < 1) this.width = 320; 
/* 242 */     if (this.height < 1) this.height = 240; 
/* 243 */     this.sizeSet = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean start(OutputStream os) {
/* 254 */     if (os == null) return false; 
/* 255 */     boolean ok = true;
/* 256 */     this.closeStream = false;
/* 257 */     this.out = os;
/*     */     try {
/* 259 */       writeString("GIF89a");
/* 260 */     } catch (IOException e) {
/* 261 */       ok = false;
/*     */     } 
/* 263 */     return this.started = ok;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean start(String file) {
/* 273 */     boolean ok = true;
/*     */     try {
/* 275 */       this.out = new BufferedOutputStream(new FileOutputStream(file));
/* 276 */       ok = start(this.out);
/* 277 */       this.closeStream = true;
/* 278 */     } catch (IOException e) {
/* 279 */       ok = false;
/*     */     } 
/* 281 */     return this.started = ok;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void analyzePixels() {
/* 288 */     int len = this.pixels.length;
/* 289 */     int nPix = len / 3;
/* 290 */     this.indexedPixels = new byte[nPix];
/* 291 */     Quant nq = new Quant(this.pixels, len, this.sample);
/*     */     
/* 293 */     this.colorTab = nq.process();
/*     */     
/* 295 */     for (int i = 0; i < this.colorTab.length; i += 3) {
/* 296 */       byte temp = this.colorTab[i];
/* 297 */       this.colorTab[i] = this.colorTab[i + 2];
/* 298 */       this.colorTab[i + 2] = temp;
/* 299 */       this.usedEntry[i / 3] = false;
/*     */     } 
/*     */     
/* 302 */     int k = 0;
/* 303 */     for (int j = 0; j < nPix; j++) {
/*     */       
/* 305 */       int index = nq.map(this.pixels[k++] & 0xFF, this.pixels[k++] & 0xFF, this.pixels[k++] & 0xFF);
/*     */ 
/*     */       
/* 308 */       this.usedEntry[index] = true;
/* 309 */       this.indexedPixels[j] = (byte)index;
/*     */     } 
/* 311 */     this.pixels = null;
/* 312 */     this.colorDepth = 8;
/* 313 */     this.palSize = 7;
/*     */     
/* 315 */     if (this.transparent != null) {
/* 316 */       this.transIndex = findClosest(this.transparent);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int findClosest(Color c) {
/* 327 */     if (this.colorTab == null) return -1; 
/* 328 */     int r = c.getRed();
/* 329 */     int g = c.getGreen();
/* 330 */     int b = c.getBlue();
/* 331 */     int minpos = 0;
/* 332 */     int dmin = 16777216;
/* 333 */     int len = this.colorTab.length;
/* 334 */     for (int i = 0; i < len; ) {
/* 335 */       int dr = r - (this.colorTab[i++] & 0xFF);
/* 336 */       int dg = g - (this.colorTab[i++] & 0xFF);
/* 337 */       int db = b - (this.colorTab[i] & 0xFF);
/* 338 */       int d = dr * dr + dg * dg + db * db;
/* 339 */       int index = i / 3;
/* 340 */       if (this.usedEntry[index] && d < dmin) {
/* 341 */         dmin = d;
/* 342 */         minpos = index;
/*     */       } 
/* 344 */       i++;
/*     */     } 
/* 346 */     return minpos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void getImagePixels() {
/* 353 */     int w = this.image.getWidth();
/* 354 */     int h = this.image.getHeight();
/* 355 */     int type = this.image.getType();
/* 356 */     if (w != this.width || h != this.height || type != 5) {
/*     */ 
/*     */ 
/*     */       
/* 360 */       BufferedImage temp = new BufferedImage(this.width, this.height, 5);
/*     */       
/* 362 */       Graphics2D g = temp.createGraphics();
/* 363 */       g.drawImage(this.image, 0, 0, null);
/* 364 */       this.image = temp;
/*     */     } 
/* 366 */     this.pixels = ((DataBufferByte)this.image.getRaster().getDataBuffer()).getData();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeGraphicCtrlExt() throws IOException {
/*     */     int transp, i;
/* 375 */     this.out.write(33);
/* 376 */     this.out.write(249);
/* 377 */     this.out.write(4);
/*     */     
/* 379 */     if (this.transparent == null) {
/* 380 */       transp = 0;
/* 381 */       i = 0;
/*     */     } else {
/* 383 */       transp = 1;
/* 384 */       i = 2;
/*     */     } 
/* 386 */     if (this.dispose >= 0) {
/* 387 */       i = this.dispose & 0x7;
/*     */     }
/* 389 */     i <<= 2;
/*     */ 
/*     */     
/* 392 */     this.out.write(0x0 | i | 0x0 | transp);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 397 */     writeShort(this.delay);
/* 398 */     this.out.write(this.transIndex);
/* 399 */     this.out.write(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeImageDesc() throws IOException {
/* 408 */     this.out.write(44);
/* 409 */     writeShort(0);
/* 410 */     writeShort(0);
/* 411 */     writeShort(this.width);
/* 412 */     writeShort(this.height);
/*     */     
/* 414 */     if (this.firstFrame) {
/*     */       
/* 416 */       this.out.write(0);
/*     */     } else {
/*     */       
/* 419 */       this.out.write(0x80 | this.palSize);
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
/*     */   protected void writeLSD() throws IOException {
/* 434 */     writeShort(this.width);
/* 435 */     writeShort(this.height);
/*     */     
/* 437 */     this.out.write(0xF0 | this.palSize);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 442 */     this.out.write(0);
/* 443 */     this.out.write(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeNetscapeExt() throws IOException {
/* 453 */     this.out.write(33);
/* 454 */     this.out.write(255);
/* 455 */     this.out.write(11);
/* 456 */     writeString("NETSCAPE2.0");
/* 457 */     this.out.write(3);
/* 458 */     this.out.write(1);
/* 459 */     writeShort(this.repeat);
/* 460 */     this.out.write(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writePalette() throws IOException {
/* 469 */     this.out.write(this.colorTab, 0, this.colorTab.length);
/* 470 */     int n = 768 - this.colorTab.length;
/* 471 */     for (int i = 0; i < n; i++) {
/* 472 */       this.out.write(0);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writePixels() throws IOException {
/* 482 */     Encoder encoder = new Encoder(this.width, this.height, this.indexedPixels, this.colorDepth);
/* 483 */     encoder.encode(this.out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeShort(int value) throws IOException {
/* 493 */     this.out.write(value & 0xFF);
/* 494 */     this.out.write(value >> 8 & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeString(String s) throws IOException {
/* 504 */     for (int i = 0; i < s.length(); i++)
/* 505 */       this.out.write((byte)s.charAt(i)); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captch\\utils\GifEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */