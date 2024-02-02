/*     */ package cn.hutool.core.img.gif;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.io.BufferedOutputStream;
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
/*     */ 
/*     */ 
/*     */ public class AnimatedGifEncoder
/*     */ {
/*     */   protected int width;
/*     */   protected int height;
/*  35 */   protected Color transparent = null;
/*     */   
/*     */   protected boolean transparentExactMatch = false;
/*  38 */   protected Color background = null;
/*     */   protected int transIndex;
/*  40 */   protected int repeat = -1;
/*  41 */   protected int delay = 0;
/*     */   protected boolean started = false;
/*     */   protected OutputStream out;
/*     */   protected BufferedImage image;
/*     */   protected byte[] pixels;
/*     */   protected byte[] indexedPixels;
/*     */   protected int colorDepth;
/*     */   protected byte[] colorTab;
/*  49 */   protected boolean[] usedEntry = new boolean[256];
/*  50 */   protected int palSize = 7;
/*  51 */   protected int dispose = -1;
/*     */   protected boolean closeStream = false;
/*     */   protected boolean firstFrame = true;
/*     */   protected boolean sizeSet = false;
/*  55 */   protected int sample = 10;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDelay(int ms) {
/*  65 */     this.delay = Math.round(ms / 10.0F);
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
/*  76 */     if (code >= 0) {
/*  77 */       this.dispose = code;
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
/*  90 */     if (iter >= 0) {
/*  91 */       this.repeat = iter;
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
/* 107 */     setTransparent(c, false);
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
/*     */   public void setTransparent(Color c, boolean exactMatch) {
/* 126 */     this.transparent = c;
/* 127 */     this.transparentExactMatch = exactMatch;
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
/*     */   public void setBackground(Color c) {
/* 144 */     this.background = c;
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
/* 158 */     if (im == null || !this.started) {
/* 159 */       return false;
/*     */     }
/* 161 */     boolean ok = true;
/*     */     try {
/* 163 */       if (!this.sizeSet)
/*     */       {
/* 165 */         setSize(im.getWidth(), im.getHeight());
/*     */       }
/* 167 */       this.image = im;
/* 168 */       getImagePixels();
/* 169 */       analyzePixels();
/* 170 */       if (this.firstFrame) {
/* 171 */         writeLSD();
/* 172 */         writePalette();
/* 173 */         if (this.repeat >= 0)
/*     */         {
/* 175 */           writeNetscapeExt();
/*     */         }
/*     */       } 
/* 178 */       writeGraphicCtrlExt();
/* 179 */       writeImageDesc();
/* 180 */       if (!this.firstFrame) {
/* 181 */         writePalette();
/*     */       }
/* 183 */       writePixels();
/* 184 */       this.firstFrame = false;
/* 185 */     } catch (IOException e) {
/* 186 */       ok = false;
/*     */     } 
/*     */     
/* 189 */     return ok;
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
/* 200 */     if (!this.started) return false; 
/* 201 */     boolean ok = true;
/* 202 */     this.started = false;
/*     */     try {
/* 204 */       this.out.write(59);
/* 205 */       this.out.flush();
/* 206 */       if (this.closeStream) {
/* 207 */         this.out.close();
/*     */       }
/* 209 */     } catch (IOException e) {
/* 210 */       ok = false;
/*     */     } 
/*     */ 
/*     */     
/* 214 */     this.transIndex = 0;
/* 215 */     this.out = null;
/* 216 */     this.image = null;
/* 217 */     this.pixels = null;
/* 218 */     this.indexedPixels = null;
/* 219 */     this.colorTab = null;
/* 220 */     this.closeStream = false;
/* 221 */     this.firstFrame = true;
/*     */     
/* 223 */     return ok;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFrameRate(float fps) {
/* 233 */     if (fps != 0.0F) {
/* 234 */       this.delay = Math.round(100.0F / fps);
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
/* 249 */     if (quality < 1) quality = 1; 
/* 250 */     this.sample = quality;
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
/* 262 */     if (this.started && !this.firstFrame)
/* 263 */       return;  this.width = w;
/* 264 */     this.height = h;
/* 265 */     if (this.width < 1) this.width = 320; 
/* 266 */     if (this.height < 1) this.height = 240; 
/* 267 */     this.sizeSet = true;
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
/* 278 */     if (os == null) return false; 
/* 279 */     boolean ok = true;
/* 280 */     this.closeStream = false;
/* 281 */     this.out = os;
/*     */     try {
/* 283 */       writeString("GIF89a");
/* 284 */     } catch (IOException e) {
/* 285 */       ok = false;
/*     */     } 
/* 287 */     return this.started = ok;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean start(String file) {
/*     */     boolean ok;
/*     */     try {
/* 299 */       this.out = new BufferedOutputStream(new FileOutputStream(file));
/* 300 */       ok = start(this.out);
/* 301 */       this.closeStream = true;
/* 302 */     } catch (IOException e) {
/* 303 */       ok = false;
/*     */     } 
/* 305 */     return this.started = ok;
/*     */   }
/*     */   
/*     */   public boolean isStarted() {
/* 309 */     return this.started;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void analyzePixels() {
/* 316 */     int len = this.pixels.length;
/* 317 */     int nPix = len / 3;
/* 318 */     this.indexedPixels = new byte[nPix];
/* 319 */     NeuQuant nq = new NeuQuant(this.pixels, len, this.sample);
/*     */     
/* 321 */     this.colorTab = nq.process();
/*     */     
/* 323 */     for (int i = 0; i < this.colorTab.length; i += 3) {
/* 324 */       byte temp = this.colorTab[i];
/* 325 */       this.colorTab[i] = this.colorTab[i + 2];
/* 326 */       this.colorTab[i + 2] = temp;
/* 327 */       this.usedEntry[i / 3] = false;
/*     */     } 
/*     */     
/* 330 */     int k = 0;
/* 331 */     for (int j = 0; j < nPix; j++) {
/*     */       
/* 333 */       int index = nq.map(this.pixels[k++] & 0xFF, this.pixels[k++] & 0xFF, this.pixels[k++] & 0xFF);
/*     */ 
/*     */       
/* 336 */       this.usedEntry[index] = true;
/* 337 */       this.indexedPixels[j] = (byte)index;
/*     */     } 
/* 339 */     this.pixels = null;
/* 340 */     this.colorDepth = 8;
/* 341 */     this.palSize = 7;
/*     */     
/* 343 */     if (this.transparent != null) {
/* 344 */       this.transIndex = this.transparentExactMatch ? findExact(this.transparent) : findClosest(this.transparent);
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
/* 355 */     if (this.colorTab == null) return -1; 
/* 356 */     int r = c.getRed();
/* 357 */     int g = c.getGreen();
/* 358 */     int b = c.getBlue();
/* 359 */     int minpos = 0;
/* 360 */     int dmin = 16777216;
/* 361 */     int len = this.colorTab.length;
/* 362 */     for (int i = 0; i < len; ) {
/* 363 */       int dr = r - (this.colorTab[i++] & 0xFF);
/* 364 */       int dg = g - (this.colorTab[i++] & 0xFF);
/* 365 */       int db = b - (this.colorTab[i] & 0xFF);
/* 366 */       int d = dr * dr + dg * dg + db * db;
/* 367 */       int index = i / 3;
/* 368 */       if (this.usedEntry[index] && d < dmin) {
/* 369 */         dmin = d;
/* 370 */         minpos = index;
/*     */       } 
/* 372 */       i++;
/*     */     } 
/* 374 */     return minpos;
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
/*     */   boolean isColorUsed(Color c) {
/* 386 */     return (findExact(c) != -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int findExact(Color c) {
/* 396 */     if (this.colorTab == null) {
/* 397 */       return -1;
/*     */     }
/*     */     
/* 400 */     int r = c.getRed();
/* 401 */     int g = c.getGreen();
/* 402 */     int b = c.getBlue();
/* 403 */     int len = this.colorTab.length / 3;
/* 404 */     for (int index = 0; index < len; index++) {
/* 405 */       int i = index * 3;
/*     */       
/* 407 */       if (this.usedEntry[index] && r == (this.colorTab[i] & 0xFF) && g == (this.colorTab[i + 1] & 0xFF) && b == (this.colorTab[i + 2] & 0xFF)) {
/* 408 */         return index;
/*     */       }
/*     */     } 
/* 411 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void getImagePixels() {
/* 418 */     int w = this.image.getWidth();
/* 419 */     int h = this.image.getHeight();
/* 420 */     int type = this.image.getType();
/* 421 */     if (w != this.width || h != this.height || type != 5) {
/*     */ 
/*     */ 
/*     */       
/* 425 */       BufferedImage temp = new BufferedImage(this.width, this.height, 5);
/*     */       
/* 427 */       Graphics2D g = temp.createGraphics();
/* 428 */       g.setColor(this.background);
/* 429 */       g.fillRect(0, 0, this.width, this.height);
/* 430 */       g.drawImage(this.image, 0, 0, (ImageObserver)null);
/* 431 */       this.image = temp;
/*     */     } 
/* 433 */     this.pixels = ((DataBufferByte)this.image.getRaster().getDataBuffer()).getData();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeGraphicCtrlExt() throws IOException {
/*     */     int transp, i;
/* 442 */     this.out.write(33);
/* 443 */     this.out.write(249);
/* 444 */     this.out.write(4);
/*     */     
/* 446 */     if (this.transparent == null) {
/* 447 */       transp = 0;
/* 448 */       i = 0;
/*     */     } else {
/* 450 */       transp = 1;
/* 451 */       i = 2;
/*     */     } 
/* 453 */     if (this.dispose >= 0) {
/* 454 */       i = this.dispose & 0x7;
/*     */     }
/* 456 */     i <<= 2;
/*     */ 
/*     */ 
/*     */     
/* 460 */     this.out.write(0x0 | i | 0x0 | transp);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 465 */     writeShort(this.delay);
/* 466 */     this.out.write(this.transIndex);
/* 467 */     this.out.write(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeImageDesc() throws IOException {
/* 476 */     this.out.write(44);
/* 477 */     writeShort(0);
/* 478 */     writeShort(0);
/* 479 */     writeShort(this.width);
/* 480 */     writeShort(this.height);
/*     */     
/* 482 */     if (this.firstFrame) {
/*     */       
/* 484 */       this.out.write(0);
/*     */     }
/*     */     else {
/*     */       
/* 488 */       this.out.write(0x80 | this.palSize);
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
/* 503 */     writeShort(this.width);
/* 504 */     writeShort(this.height);
/*     */ 
/*     */     
/* 507 */     this.out.write(0xF0 | this.palSize);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 512 */     this.out.write(0);
/* 513 */     this.out.write(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeNetscapeExt() throws IOException {
/* 523 */     this.out.write(33);
/* 524 */     this.out.write(255);
/* 525 */     this.out.write(11);
/* 526 */     writeString("NETSCAPE2.0");
/* 527 */     this.out.write(3);
/* 528 */     this.out.write(1);
/* 529 */     writeShort(this.repeat);
/* 530 */     this.out.write(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writePalette() throws IOException {
/* 539 */     this.out.write(this.colorTab, 0, this.colorTab.length);
/* 540 */     int n = 768 - this.colorTab.length;
/* 541 */     for (int i = 0; i < n; i++) {
/* 542 */       this.out.write(0);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writePixels() throws IOException {
/* 552 */     LZWEncoder encoder = new LZWEncoder(this.width, this.height, this.indexedPixels, this.colorDepth);
/* 553 */     encoder.encode(this.out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeShort(int value) throws IOException {
/* 563 */     this.out.write(value & 0xFF);
/* 564 */     this.out.write(value >> 8 & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeString(String s) throws IOException {
/* 574 */     for (int i = 0; i < s.length(); i++)
/* 575 */       this.out.write((byte)s.charAt(i)); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\img\gif\AnimatedGifEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */