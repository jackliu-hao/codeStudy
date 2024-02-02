/*     */ package cn.hutool.core.img.gif;
/*     */ 
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GifDecoder
/*     */ {
/*     */   public static final int STATUS_OK = 0;
/*     */   public static final int STATUS_FORMAT_ERROR = 1;
/*     */   public static final int STATUS_OPEN_ERROR = 2;
/*     */   protected BufferedInputStream in;
/*     */   protected int status;
/*     */   protected int width;
/*     */   protected int height;
/*     */   protected boolean gctFlag;
/*     */   protected int gctSize;
/*  66 */   protected int loopCount = 1;
/*     */   
/*     */   protected int[] gct;
/*     */   
/*     */   protected int[] lct;
/*     */   protected int[] act;
/*     */   protected int bgIndex;
/*     */   protected int bgColor;
/*     */   protected int lastBgColor;
/*     */   protected int pixelAspect;
/*     */   protected boolean lctFlag;
/*     */   protected boolean interlace;
/*     */   protected int lctSize;
/*     */   protected int ix;
/*     */   protected int iy;
/*     */   protected int iw;
/*     */   protected int ih;
/*     */   protected Rectangle lastRect;
/*     */   protected BufferedImage image;
/*     */   protected BufferedImage lastImage;
/*  86 */   protected byte[] block = new byte[256];
/*  87 */   protected int blockSize = 0;
/*     */ 
/*     */   
/*  90 */   protected int dispose = 0;
/*     */   
/*  92 */   protected int lastDispose = 0;
/*     */   protected boolean transparency = false;
/*  94 */   protected int delay = 0;
/*     */   
/*     */   protected int transIndex;
/*     */   protected static final int MAX_STACK_SIZE = 4096;
/*     */   protected short[] prefix;
/*     */   protected byte[] suffix;
/*     */   protected byte[] pixelStack;
/*     */   protected byte[] pixels;
/*     */   protected ArrayList<GifFrame> frames;
/*     */   protected int frameCount;
/*     */   
/*     */   static class GifFrame
/*     */   {
/*     */     public BufferedImage image;
/*     */     public int delay;
/*     */     
/*     */     public GifFrame(BufferedImage im, int del) {
/* 111 */       this.image = im;
/* 112 */       this.delay = del;
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
/*     */   public int getDelay(int n) {
/* 127 */     this.delay = -1;
/* 128 */     if (n >= 0 && n < this.frameCount) {
/* 129 */       this.delay = ((GifFrame)this.frames.get(n)).delay;
/*     */     }
/* 131 */     return this.delay;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFrameCount() {
/* 140 */     return this.frameCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage getImage() {
/* 149 */     return getFrame(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLoopCount() {
/* 159 */     return this.loopCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setPixels() {
/* 169 */     int[] dest = ((DataBufferInt)this.image.getRaster().getDataBuffer()).getData();
/*     */ 
/*     */     
/* 172 */     if (this.lastDispose > 0) {
/* 173 */       if (this.lastDispose == 3) {
/*     */         
/* 175 */         int n = this.frameCount - 2;
/* 176 */         if (n > 0) {
/* 177 */           this.lastImage = getFrame(n - 1);
/*     */         } else {
/* 179 */           this.lastImage = null;
/*     */         } 
/*     */       } 
/*     */       
/* 183 */       if (this.lastImage != null) {
/*     */         
/* 185 */         int[] prev = ((DataBufferInt)this.lastImage.getRaster().getDataBuffer()).getData();
/* 186 */         System.arraycopy(prev, 0, dest, 0, this.width * this.height);
/*     */ 
/*     */         
/* 189 */         if (this.lastDispose == 2) {
/*     */           Color c;
/* 191 */           Graphics2D g = this.image.createGraphics();
/*     */           
/* 193 */           if (this.transparency) {
/* 194 */             c = new Color(0, 0, 0, 0);
/*     */           } else {
/* 196 */             c = new Color(this.lastBgColor);
/*     */           } 
/* 198 */           g.setColor(c);
/* 199 */           g.setComposite(AlphaComposite.Src);
/* 200 */           g.fill(this.lastRect);
/* 201 */           g.dispose();
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 207 */     int pass = 1;
/* 208 */     int inc = 8;
/* 209 */     int iline = 0;
/* 210 */     for (int i = 0; i < this.ih; i++) {
/* 211 */       int line = i;
/* 212 */       if (this.interlace) {
/* 213 */         if (iline >= this.ih) {
/* 214 */           pass++;
/* 215 */           switch (pass) {
/*     */             case 2:
/* 217 */               iline = 4;
/*     */               break;
/*     */             case 3:
/* 220 */               iline = 2;
/* 221 */               inc = 4;
/*     */               break;
/*     */             case 4:
/* 224 */               iline = 1;
/* 225 */               inc = 2; break;
/*     */           } 
/*     */         } 
/* 228 */         line = iline;
/* 229 */         iline += inc;
/*     */       } 
/* 231 */       line += this.iy;
/* 232 */       if (line < this.height) {
/* 233 */         int k = line * this.width;
/* 234 */         int dx = k + this.ix;
/* 235 */         int dlim = dx + this.iw;
/* 236 */         if (k + this.width < dlim) {
/* 237 */           dlim = k + this.width;
/*     */         }
/* 239 */         int sx = i * this.iw;
/* 240 */         while (dx < dlim) {
/*     */           
/* 242 */           int index = this.pixels[sx++] & 0xFF;
/* 243 */           int c = this.act[index];
/* 244 */           if (c != 0) {
/* 245 */             dest[dx] = c;
/*     */           }
/* 247 */           dx++;
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
/*     */   public BufferedImage getFrame(int n) {
/* 260 */     BufferedImage im = null;
/* 261 */     if (n >= 0 && n < this.frameCount) {
/* 262 */       im = ((GifFrame)this.frames.get(n)).image;
/*     */     }
/* 264 */     return im;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dimension getFrameSize() {
/* 273 */     return new Dimension(this.width, this.height);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(BufferedInputStream is) {
/* 283 */     init();
/* 284 */     if (is != null) {
/* 285 */       this.in = is;
/* 286 */       readHeader();
/* 287 */       if (false == err()) {
/* 288 */         readContents();
/* 289 */         if (this.frameCount < 0) {
/* 290 */           this.status = 1;
/*     */         }
/*     */       } 
/*     */     } else {
/* 294 */       this.status = 2;
/*     */     } 
/* 296 */     IoUtil.close(is);
/* 297 */     return this.status;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(InputStream is) {
/* 307 */     init();
/* 308 */     if (is != null) {
/* 309 */       if (!(is instanceof BufferedInputStream))
/* 310 */         is = new BufferedInputStream(is); 
/* 311 */       this.in = (BufferedInputStream)is;
/* 312 */       readHeader();
/* 313 */       if (!err()) {
/* 314 */         readContents();
/* 315 */         if (this.frameCount < 0) {
/* 316 */           this.status = 1;
/*     */         }
/*     */       } 
/*     */     } else {
/* 320 */       this.status = 2;
/*     */     } 
/* 322 */     IoUtil.close(is);
/* 323 */     return this.status;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(String name) {
/* 334 */     this.status = 0;
/*     */     try {
/* 336 */       name = name.trim().toLowerCase();
/* 337 */       if (name.contains("file:") || name
/* 338 */         .indexOf(":/") > 0) {
/* 339 */         URL url = new URL(name);
/* 340 */         this.in = new BufferedInputStream(url.openStream());
/*     */       } else {
/* 342 */         this.in = new BufferedInputStream(new FileInputStream(name));
/*     */       } 
/* 344 */       this.status = read(this.in);
/* 345 */     } catch (IOException e) {
/* 346 */       this.status = 2;
/*     */     } 
/*     */     
/* 349 */     return this.status;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void decodeImageData() {
/* 357 */     int NullCode = -1;
/* 358 */     int npix = this.iw * this.ih;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 377 */     if (this.pixels == null || this.pixels.length < npix) {
/* 378 */       this.pixels = new byte[npix];
/*     */     }
/* 380 */     if (this.prefix == null) this.prefix = new short[4096]; 
/* 381 */     if (this.suffix == null) this.suffix = new byte[4096]; 
/* 382 */     if (this.pixelStack == null) this.pixelStack = new byte[4097];
/*     */ 
/*     */ 
/*     */     
/* 386 */     int data_size = read();
/* 387 */     int clear = 1 << data_size;
/* 388 */     int end_of_information = clear + 1;
/* 389 */     int available = clear + 2;
/* 390 */     int old_code = NullCode;
/* 391 */     int code_size = data_size + 1;
/* 392 */     int code_mask = (1 << code_size) - 1; int code;
/* 393 */     for (code = 0; code < clear; code++) {
/* 394 */       this.prefix[code] = 0;
/* 395 */       this.suffix[code] = (byte)code;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 400 */     int bi = 0, pi = bi, top = pi, first = top, count = first, bits = count, datum = bits;
/*     */     int i;
/* 402 */     for (i = 0; i < npix; ) {
/* 403 */       if (top == 0) {
/* 404 */         if (bits < code_size) {
/*     */           
/* 406 */           if (count == 0) {
/*     */             
/* 408 */             count = readBlock();
/* 409 */             if (count <= 0)
/*     */               break; 
/* 411 */             bi = 0;
/*     */           } 
/* 413 */           datum += (this.block[bi] & 0xFF) << bits;
/* 414 */           bits += 8;
/* 415 */           bi++;
/* 416 */           count--;
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 422 */         code = datum & code_mask;
/* 423 */         datum >>= code_size;
/* 424 */         bits -= code_size;
/*     */ 
/*     */ 
/*     */         
/* 428 */         if (code > available || code == end_of_information)
/*     */           break; 
/* 430 */         if (code == clear) {
/*     */           
/* 432 */           code_size = data_size + 1;
/* 433 */           code_mask = (1 << code_size) - 1;
/* 434 */           available = clear + 2;
/* 435 */           old_code = NullCode;
/*     */           continue;
/*     */         } 
/* 438 */         if (old_code == NullCode) {
/* 439 */           this.pixelStack[top++] = this.suffix[code];
/* 440 */           old_code = code;
/* 441 */           first = code;
/*     */           continue;
/*     */         } 
/* 444 */         int in_code = code;
/* 445 */         if (code == available) {
/* 446 */           this.pixelStack[top++] = (byte)first;
/* 447 */           code = old_code;
/*     */         } 
/* 449 */         while (code > clear) {
/* 450 */           this.pixelStack[top++] = this.suffix[code];
/* 451 */           code = this.prefix[code];
/*     */         } 
/* 453 */         first = this.suffix[code] & 0xFF;
/*     */ 
/*     */ 
/*     */         
/* 457 */         if (available >= 4096) {
/* 458 */           this.pixelStack[top++] = (byte)first;
/*     */           continue;
/*     */         } 
/* 461 */         this.pixelStack[top++] = (byte)first;
/* 462 */         this.prefix[available] = (short)old_code;
/* 463 */         this.suffix[available] = (byte)first;
/* 464 */         available++;
/* 465 */         if ((available & code_mask) == 0 && available < 4096) {
/*     */           
/* 467 */           code_size++;
/* 468 */           code_mask += available;
/*     */         } 
/* 470 */         old_code = in_code;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 475 */       top--;
/* 476 */       this.pixels[pi++] = this.pixelStack[top];
/* 477 */       i++;
/*     */     } 
/*     */     
/* 480 */     for (i = pi; i < npix; i++) {
/* 481 */       this.pixels[i] = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean err() {
/* 492 */     return (this.status != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void init() {
/* 499 */     this.status = 0;
/* 500 */     this.frameCount = 0;
/* 501 */     this.frames = new ArrayList<>();
/* 502 */     this.gct = null;
/* 503 */     this.lct = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int read() {
/* 512 */     int curByte = 0;
/*     */     try {
/* 514 */       curByte = this.in.read();
/* 515 */     } catch (IOException e) {
/* 516 */       this.status = 1;
/*     */     } 
/* 518 */     return curByte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int readBlock() {
/* 527 */     this.blockSize = read();
/* 528 */     int n = 0;
/* 529 */     if (this.blockSize > 0) {
/*     */       
/*     */       try {
/* 532 */         while (n < this.blockSize) {
/* 533 */           int count = this.in.read(this.block, n, this.blockSize - n);
/* 534 */           if (count == -1)
/*     */             break; 
/* 536 */           n += count;
/*     */         } 
/* 538 */       } catch (IOException iOException) {}
/*     */ 
/*     */ 
/*     */       
/* 542 */       if (n < this.blockSize) {
/* 543 */         this.status = 1;
/*     */       }
/*     */     } 
/* 546 */     return n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int[] readColorTable(int ncolors) {
/* 556 */     int nbytes = 3 * ncolors;
/* 557 */     int[] tab = null;
/* 558 */     byte[] c = new byte[nbytes];
/* 559 */     int n = 0;
/*     */     try {
/* 561 */       n = this.in.read(c);
/* 562 */     } catch (IOException iOException) {}
/*     */ 
/*     */     
/* 565 */     if (n < nbytes) {
/* 566 */       this.status = 1;
/*     */     } else {
/* 568 */       tab = new int[256];
/* 569 */       int i = 0;
/* 570 */       int j = 0;
/* 571 */       while (i < ncolors) {
/* 572 */         int r = c[j++] & 0xFF;
/* 573 */         int g = c[j++] & 0xFF;
/* 574 */         int b = c[j++] & 0xFF;
/* 575 */         tab[i++] = 0xFF000000 | r << 16 | g << 8 | b;
/*     */       } 
/*     */     } 
/* 578 */     return tab;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void readContents() {
/* 586 */     boolean done = false;
/* 587 */     while (!done && !err()) {
/* 588 */       StringBuilder app; int i, code = read();
/* 589 */       switch (code) {
/*     */         
/*     */         case 44:
/* 592 */           readImage();
/*     */           continue;
/*     */         
/*     */         case 33:
/* 596 */           code = read();
/* 597 */           switch (code) {
/*     */             case 249:
/* 599 */               readGraphicControlExt();
/*     */               continue;
/*     */             
/*     */             case 255:
/* 603 */               readBlock();
/* 604 */               app = new StringBuilder();
/* 605 */               for (i = 0; i < 11; i++) {
/* 606 */                 app.append((char)this.block[i]);
/*     */               }
/* 608 */               if ("NETSCAPE2.0".equals(app.toString())) {
/* 609 */                 readNetscapeExt(); continue;
/*     */               } 
/* 611 */               skip();
/*     */               continue;
/*     */           } 
/*     */ 
/*     */           
/* 616 */           skip();
/*     */           continue;
/*     */ 
/*     */         
/*     */         case 59:
/* 621 */           done = true;
/*     */           continue;
/*     */         
/*     */         case 0:
/*     */           continue;
/*     */       } 
/*     */       
/* 628 */       this.status = 1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void readGraphicControlExt() {
/* 637 */     read();
/* 638 */     int packed = read();
/* 639 */     this.dispose = (packed & 0x1C) >> 2;
/* 640 */     if (this.dispose == 0) {
/* 641 */       this.dispose = 1;
/*     */     }
/* 643 */     this.transparency = ((packed & 0x1) != 0);
/* 644 */     this.delay = readShort() * 10;
/* 645 */     this.transIndex = read();
/* 646 */     read();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void readHeader() {
/* 653 */     StringBuilder id = new StringBuilder();
/* 654 */     for (int i = 0; i < 6; i++) {
/* 655 */       id.append((char)read());
/*     */     }
/* 657 */     if (false == id.toString().startsWith("GIF")) {
/* 658 */       this.status = 1;
/*     */       
/*     */       return;
/*     */     } 
/* 662 */     readLSD();
/* 663 */     if (this.gctFlag && !err()) {
/* 664 */       this.gct = readColorTable(this.gctSize);
/* 665 */       this.bgColor = this.gct[this.bgIndex];
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void readImage() {
/* 673 */     this.ix = readShort();
/* 674 */     this.iy = readShort();
/* 675 */     this.iw = readShort();
/* 676 */     this.ih = readShort();
/*     */     
/* 678 */     int packed = read();
/* 679 */     this.lctFlag = ((packed & 0x80) != 0);
/* 680 */     this.interlace = ((packed & 0x40) != 0);
/*     */ 
/*     */     
/* 683 */     this.lctSize = 2 << (packed & 0x7);
/*     */     
/* 685 */     if (this.lctFlag) {
/* 686 */       this.lct = readColorTable(this.lctSize);
/* 687 */       this.act = this.lct;
/*     */     } else {
/* 689 */       this.act = this.gct;
/* 690 */       if (this.bgIndex == this.transIndex)
/* 691 */         this.bgColor = 0; 
/*     */     } 
/* 693 */     int save = 0;
/* 694 */     if (this.transparency) {
/* 695 */       save = this.act[this.transIndex];
/* 696 */       this.act[this.transIndex] = 0;
/*     */     } 
/*     */     
/* 699 */     if (this.act == null) {
/* 700 */       this.status = 1;
/*     */     }
/*     */     
/* 703 */     if (err())
/*     */       return; 
/* 705 */     decodeImageData();
/* 706 */     skip();
/*     */     
/* 708 */     if (err())
/*     */       return; 
/* 710 */     this.frameCount++;
/*     */ 
/*     */     
/* 713 */     this.image = new BufferedImage(this.width, this.height, 3);
/*     */ 
/*     */     
/* 716 */     setPixels();
/*     */     
/* 718 */     this.frames.add(new GifFrame(this.image, this.delay));
/*     */     
/* 720 */     if (this.transparency) {
/* 721 */       this.act[this.transIndex] = save;
/*     */     }
/* 723 */     resetFrame();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void readLSD() {
/* 733 */     this.width = readShort();
/* 734 */     this.height = readShort();
/*     */ 
/*     */     
/* 737 */     int packed = read();
/* 738 */     this.gctFlag = ((packed & 0x80) != 0);
/*     */ 
/*     */     
/* 741 */     this.gctSize = 2 << (packed & 0x7);
/*     */     
/* 743 */     this.bgIndex = read();
/* 744 */     this.pixelAspect = read();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void readNetscapeExt() {
/*     */     do {
/* 752 */       readBlock();
/* 753 */       if (this.block[0] != 1)
/*     */         continue; 
/* 755 */       int b1 = this.block[1] & 0xFF;
/* 756 */       int b2 = this.block[2] & 0xFF;
/* 757 */       this.loopCount = b2 << 8 | b1;
/*     */     }
/* 759 */     while (this.blockSize > 0 && !err());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int readShort() {
/* 769 */     return read() | read() << 8;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void resetFrame() {
/* 776 */     this.lastDispose = this.dispose;
/* 777 */     this.lastRect = new Rectangle(this.ix, this.iy, this.iw, this.ih);
/* 778 */     this.lastImage = this.image;
/* 779 */     this.lastBgColor = this.bgColor;
/* 780 */     this.lct = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void skip() {
/*     */     do {
/* 789 */       readBlock();
/* 790 */     } while (this.blockSize > 0 && !err());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\img\gif\GifDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */