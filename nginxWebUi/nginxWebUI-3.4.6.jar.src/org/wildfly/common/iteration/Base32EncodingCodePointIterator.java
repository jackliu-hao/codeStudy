/*     */ package org.wildfly.common.iteration;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import org.wildfly.common.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class Base32EncodingCodePointIterator
/*     */   extends CodePointIterator
/*     */ {
/*     */   private final ByteIterator iter;
/*     */   private final boolean addPadding;
/*     */   private int c0;
/*     */   private int c1;
/*     */   private int c2;
/*     */   private int c3;
/*     */   private int c4;
/*     */   private int c5;
/*     */   private int c6;
/*     */   private int c7;
/*     */   private int state;
/*     */   private int offset;
/*     */   
/*     */   Base32EncodingCodePointIterator(ByteIterator iter, boolean addPadding) {
/*  36 */     this.iter = iter;
/*  37 */     this.addPadding = addPadding;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/*  89 */     return ((this.state == 0 && this.iter.hasNext()) || (this.state > 0 && this.state < 41));
/*     */   }
/*     */   
/*     */   public boolean hasPrevious() {
/*  93 */     return (this.offset > 0);
/*     */   }
/*     */   
/*     */   abstract int calc0(int paramInt);
/*     */   
/*     */   abstract int calc1(int paramInt1, int paramInt2);
/*     */   
/*     */   abstract int calc2(int paramInt);
/*     */   
/*     */   abstract int calc3(int paramInt1, int paramInt2);
/*     */   
/*     */   abstract int calc4(int paramInt1, int paramInt2);
/*     */   
/*     */   abstract int calc5(int paramInt);
/*     */   
/*     */   abstract int calc6(int paramInt1, int paramInt2);
/*     */   
/*     */   abstract int calc7(int paramInt);
/*     */   
/*     */   public int next() throws NoSuchElementException { int b0, b1, b2, b3, b4;
/* 113 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 114 */     this.offset++;
/* 115 */     switch (this.state) {
/*     */       case 0:
/* 117 */         assert this.iter.hasNext();
/* 118 */         b0 = this.iter.next();
/* 119 */         this.c0 = calc0(b0);
/* 120 */         if (!this.iter.hasNext()) {
/* 121 */           this.c1 = calc1(b0, 0);
/* 122 */           this.state = 10;
/* 123 */           return this.c0;
/*     */         } 
/* 125 */         b1 = this.iter.next();
/* 126 */         this.c1 = calc1(b0, b1);
/* 127 */         this.c2 = calc2(b1);
/* 128 */         if (!this.iter.hasNext()) {
/* 129 */           this.c3 = calc3(b1, 0);
/* 130 */           this.state = 18;
/* 131 */           return this.c0;
/*     */         } 
/* 133 */         b2 = this.iter.next();
/* 134 */         this.c3 = calc3(b1, b2);
/* 135 */         if (!this.iter.hasNext()) {
/* 136 */           this.c4 = calc4(b2, 0);
/* 137 */           this.state = 26;
/* 138 */           return this.c0;
/*     */         } 
/* 140 */         b3 = this.iter.next();
/* 141 */         this.c4 = calc4(b2, b3);
/* 142 */         this.c5 = calc5(b3);
/* 143 */         if (!this.iter.hasNext()) {
/* 144 */           this.c6 = calc6(b3, 0);
/* 145 */           this.state = 34;
/* 146 */           return this.c0;
/*     */         } 
/* 148 */         b4 = this.iter.next();
/* 149 */         this.c6 = calc6(b3, b4);
/* 150 */         this.c7 = calc7(b4);
/* 151 */         this.state = 2;
/* 152 */         return this.c0;
/*     */       
/*     */       case 1:
/*     */       case 9:
/*     */       case 17:
/*     */       case 25:
/*     */       case 33:
/* 159 */         this.state++;
/* 160 */         return this.c0;
/*     */       
/*     */       case 2:
/*     */       case 18:
/*     */       case 26:
/*     */       case 34:
/* 166 */         this.state++;
/* 167 */         return this.c1;
/*     */       
/*     */       case 3:
/*     */       case 19:
/*     */       case 27:
/*     */       case 35:
/* 173 */         this.state++;
/* 174 */         return this.c2;
/*     */       
/*     */       case 4:
/*     */       case 28:
/*     */       case 36:
/* 179 */         this.state++;
/* 180 */         return this.c3;
/*     */       
/*     */       case 5:
/*     */       case 37:
/* 184 */         this.state++;
/* 185 */         return this.c4;
/*     */       
/*     */       case 6:
/*     */       case 38:
/* 189 */         this.state++;
/* 190 */         return this.c5;
/*     */       
/*     */       case 7:
/* 193 */         this.state = 8;
/* 194 */         return this.c6;
/*     */       
/*     */       case 8:
/* 197 */         this.state = 0;
/* 198 */         return this.c7;
/*     */       
/*     */       case 10:
/* 201 */         this.state = this.addPadding ? 11 : 41;
/* 202 */         return this.c1;
/*     */       
/*     */       case 20:
/* 205 */         this.state = this.addPadding ? 21 : 42;
/* 206 */         return this.c3;
/*     */       
/*     */       case 29:
/* 209 */         this.state = this.addPadding ? 30 : 43;
/* 210 */         return this.c4;
/*     */       
/*     */       case 39:
/* 213 */         this.state = this.addPadding ? 40 : 44;
/* 214 */         return this.c6;
/*     */       
/*     */       case 11:
/*     */       case 12:
/*     */       case 13:
/*     */       case 14:
/*     */       case 15:
/*     */       case 21:
/*     */       case 22:
/*     */       case 23:
/*     */       case 30:
/*     */       case 31:
/* 226 */         this.state++;
/* 227 */         return 61;
/*     */       
/*     */       case 16:
/* 230 */         this.state = 41;
/* 231 */         return 61;
/*     */       
/*     */       case 24:
/* 234 */         this.state = 42;
/* 235 */         return 61;
/*     */       
/*     */       case 32:
/* 238 */         this.state = 43;
/* 239 */         return 61;
/*     */       
/*     */       case 40:
/* 242 */         this.state = 44;
/* 243 */         return 61;
/*     */     } 
/*     */     
/* 246 */     throw Assert.impossibleSwitchCase(this.state); } public int peekNext() throws NoSuchElementException {
/*     */     int b0;
/*     */     int b1;
/*     */     int b2;
/*     */     int b3;
/*     */     int b4;
/* 252 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 253 */     switch (this.state) {
/*     */       case 0:
/* 255 */         assert this.iter.hasNext();
/* 256 */         b0 = this.iter.next();
/* 257 */         this.c0 = calc0(b0);
/* 258 */         if (!this.iter.hasNext()) {
/* 259 */           this.c1 = calc1(b0, 0);
/* 260 */           this.state = 9;
/* 261 */           return this.c0;
/*     */         } 
/* 263 */         b1 = this.iter.next();
/* 264 */         this.c1 = calc1(b0, b1);
/* 265 */         this.c2 = calc2(b1);
/* 266 */         if (!this.iter.hasNext()) {
/* 267 */           this.c3 = calc3(b1, 0);
/* 268 */           this.state = 17;
/* 269 */           return this.c0;
/*     */         } 
/* 271 */         b2 = this.iter.next();
/* 272 */         this.c3 = calc3(b1, b2);
/* 273 */         if (!this.iter.hasNext()) {
/* 274 */           this.c4 = calc4(b2, 0);
/* 275 */           this.state = 25;
/* 276 */           return this.c0;
/*     */         } 
/* 278 */         b3 = this.iter.next();
/* 279 */         this.c4 = calc4(b2, b3);
/* 280 */         this.c5 = calc5(b3);
/* 281 */         if (!this.iter.hasNext()) {
/* 282 */           this.c6 = calc6(b3, 0);
/* 283 */           this.state = 33;
/* 284 */           return this.c0;
/*     */         } 
/* 286 */         b4 = this.iter.next();
/* 287 */         this.c6 = calc6(b3, b4);
/* 288 */         this.c7 = calc7(b4);
/* 289 */         this.state = 1;
/* 290 */         return this.c0;
/*     */       
/*     */       case 1:
/*     */       case 9:
/*     */       case 17:
/*     */       case 25:
/*     */       case 33:
/* 297 */         return this.c0;
/*     */       
/*     */       case 2:
/*     */       case 10:
/*     */       case 18:
/*     */       case 26:
/*     */       case 34:
/* 304 */         return this.c1;
/*     */       
/*     */       case 3:
/*     */       case 19:
/*     */       case 27:
/*     */       case 35:
/* 310 */         return this.c2;
/*     */       
/*     */       case 4:
/*     */       case 20:
/*     */       case 28:
/*     */       case 36:
/* 316 */         return this.c3;
/*     */       
/*     */       case 5:
/*     */       case 29:
/*     */       case 37:
/* 321 */         return this.c4;
/*     */       
/*     */       case 6:
/*     */       case 38:
/* 325 */         return this.c5;
/*     */       
/*     */       case 7:
/*     */       case 39:
/* 329 */         return this.c6;
/*     */       
/*     */       case 8:
/* 332 */         return this.c7;
/*     */       
/*     */       case 11:
/*     */       case 12:
/*     */       case 13:
/*     */       case 14:
/*     */       case 15:
/*     */       case 16:
/*     */       case 21:
/*     */       case 22:
/*     */       case 23:
/*     */       case 24:
/*     */       case 30:
/*     */       case 31:
/*     */       case 32:
/*     */       case 40:
/* 348 */         return 61;
/*     */     } 
/*     */     
/* 351 */     throw Assert.impossibleSwitchCase(this.state);
/*     */   }
/*     */ 
/*     */   
/*     */   public int previous() throws NoSuchElementException {
/*     */     int b4, b3, b2, b1, b0;
/* 357 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 358 */     this.offset--;
/* 359 */     switch (this.state) {
/*     */       case 33:
/* 361 */         this.iter.previous();
/*     */       
/*     */       case 25:
/* 364 */         this.iter.previous();
/*     */       
/*     */       case 17:
/* 367 */         this.iter.previous();
/*     */       
/*     */       case 9:
/* 370 */         this.iter.previous();
/*     */       
/*     */       case 0:
/*     */       case 1:
/*     */       case 45:
/* 375 */         b4 = this.iter.previous();
/* 376 */         b3 = this.iter.previous();
/* 377 */         b2 = this.iter.previous();
/* 378 */         b1 = this.iter.previous();
/* 379 */         b0 = this.iter.previous();
/* 380 */         this.c0 = calc0(b0);
/* 381 */         this.c1 = calc1(b0, b1);
/* 382 */         this.c2 = calc2(b1);
/* 383 */         this.c3 = calc3(b1, b2);
/* 384 */         this.c4 = calc4(b2, b3);
/* 385 */         this.c5 = calc5(b3);
/* 386 */         this.c6 = calc6(b3, b4);
/* 387 */         this.c7 = calc7(b4);
/* 388 */         this.state = 8;
/* 389 */         return this.c7;
/*     */       
/*     */       case 2:
/*     */       case 10:
/*     */       case 18:
/*     */       case 26:
/*     */       case 34:
/* 396 */         this.state--;
/* 397 */         return this.c0;
/*     */       
/*     */       case 3:
/*     */       case 11:
/*     */       case 19:
/*     */       case 27:
/*     */       case 35:
/* 404 */         this.state--;
/* 405 */         return this.c1;
/*     */       
/*     */       case 4:
/*     */       case 20:
/*     */       case 28:
/*     */       case 36:
/* 411 */         this.state--;
/* 412 */         return this.c2;
/*     */       
/*     */       case 5:
/*     */       case 21:
/*     */       case 29:
/*     */       case 37:
/* 418 */         this.state--;
/* 419 */         return this.c3;
/*     */       
/*     */       case 6:
/*     */       case 30:
/*     */       case 38:
/* 424 */         this.state--;
/* 425 */         return this.c4;
/*     */       
/*     */       case 7:
/*     */       case 39:
/* 429 */         this.state--;
/* 430 */         return this.c5;
/*     */       
/*     */       case 8:
/*     */       case 40:
/* 434 */         this.state--;
/* 435 */         return this.c6;
/*     */       
/*     */       case 12:
/*     */       case 13:
/*     */       case 14:
/*     */       case 15:
/*     */       case 16:
/*     */       case 22:
/*     */       case 23:
/*     */       case 24:
/*     */       case 31:
/*     */       case 32:
/* 447 */         this.state--;
/* 448 */         return 61;
/*     */       
/*     */       case 41:
/* 451 */         if (this.addPadding) {
/* 452 */           this.state = 16;
/* 453 */           return 61;
/*     */         } 
/* 455 */         this.state = 10;
/* 456 */         return this.c1;
/*     */ 
/*     */       
/*     */       case 42:
/* 460 */         if (this.addPadding) {
/* 461 */           this.state = 24;
/* 462 */           return 61;
/*     */         } 
/* 464 */         this.state = 20;
/* 465 */         return this.c3;
/*     */ 
/*     */       
/*     */       case 43:
/* 469 */         if (this.addPadding) {
/* 470 */           this.state = 32;
/* 471 */           return 61;
/*     */         } 
/* 473 */         this.state = 29;
/* 474 */         return this.c4;
/*     */ 
/*     */       
/*     */       case 44:
/* 478 */         if (this.addPadding) {
/* 479 */           this.state = 40;
/* 480 */           return 61;
/*     */         } 
/* 482 */         this.state = 39;
/* 483 */         return this.c6;
/*     */     } 
/*     */ 
/*     */     
/* 487 */     throw Assert.impossibleSwitchCase(this.state);
/*     */   }
/*     */ 
/*     */   
/*     */   public int peekPrevious() throws NoSuchElementException {
/*     */     int result;
/* 493 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 494 */     switch (this.state) {
/*     */       case 33:
/* 496 */         this.iter.previous();
/*     */       case 25:
/* 498 */         this.iter.previous();
/*     */       case 17:
/* 500 */         this.iter.previous();
/*     */       case 9:
/* 502 */         this.iter.previous();
/*     */       case 0:
/*     */       case 1:
/*     */       case 45:
/* 506 */         result = calc7(this.iter.peekPrevious());
/* 507 */         if (this.state == 9) {
/* 508 */           this.iter.next();
/* 509 */         } else if (this.state == 17) {
/* 510 */           this.iter.next();
/* 511 */           this.iter.next();
/* 512 */         } else if (this.state == 25) {
/* 513 */           this.iter.next();
/* 514 */           this.iter.next();
/* 515 */           this.iter.next();
/* 516 */         } else if (this.state == 33) {
/* 517 */           this.iter.next();
/* 518 */           this.iter.next();
/* 519 */           this.iter.next();
/* 520 */           this.iter.next();
/*     */         } 
/* 522 */         return result;
/*     */       
/*     */       case 2:
/*     */       case 10:
/*     */       case 18:
/*     */       case 26:
/*     */       case 34:
/* 529 */         return this.c0;
/*     */       
/*     */       case 3:
/*     */       case 11:
/*     */       case 19:
/*     */       case 27:
/*     */       case 35:
/* 536 */         return this.c1;
/*     */       
/*     */       case 4:
/*     */       case 20:
/*     */       case 28:
/*     */       case 36:
/* 542 */         return this.c2;
/*     */       
/*     */       case 5:
/*     */       case 21:
/*     */       case 29:
/*     */       case 37:
/* 548 */         return this.c3;
/*     */       
/*     */       case 6:
/*     */       case 30:
/*     */       case 38:
/* 553 */         return this.c4;
/*     */       
/*     */       case 7:
/*     */       case 39:
/* 557 */         return this.c5;
/*     */       
/*     */       case 8:
/*     */       case 40:
/* 561 */         return this.c6;
/*     */       
/*     */       case 12:
/*     */       case 13:
/*     */       case 14:
/*     */       case 15:
/*     */       case 16:
/*     */       case 22:
/*     */       case 23:
/*     */       case 24:
/*     */       case 31:
/*     */       case 32:
/* 573 */         return 61;
/*     */       
/*     */       case 41:
/* 576 */         return this.addPadding ? 61 : this.c1;
/*     */       
/*     */       case 42:
/* 579 */         return this.addPadding ? 61 : this.c3;
/*     */       
/*     */       case 43:
/* 582 */         return this.addPadding ? 61 : this.c4;
/*     */       
/*     */       case 44:
/* 585 */         return this.addPadding ? 61 : this.c6;
/*     */     } 
/*     */     
/* 588 */     throw Assert.impossibleSwitchCase(this.state);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getIndex() {
/* 594 */     return this.offset;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\Base32EncodingCodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */