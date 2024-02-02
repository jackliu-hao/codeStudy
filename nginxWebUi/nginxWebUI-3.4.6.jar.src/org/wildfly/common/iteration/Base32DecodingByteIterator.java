/*     */ package org.wildfly.common.iteration;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import org.wildfly.common._private.CommonMessages;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class Base32DecodingByteIterator
/*     */   extends ByteIterator
/*     */ {
/*     */   private final CodePointIterator iter;
/*     */   private final boolean requirePadding;
/*  67 */   private int state = 0; private int o0;
/*     */   private int o1;
/*     */   private int o2;
/*     */   
/*     */   Base32DecodingByteIterator(CodePointIterator iter, boolean requirePadding) {
/*  72 */     this.iter = iter;
/*  73 */     this.requirePadding = requirePadding;
/*     */   }
/*     */   private int o3; private int o4; private int offset;
/*     */   public boolean hasNext() {
/*  77 */     if (this.state == 0) {
/*  78 */       if (!this.iter.hasNext()) {
/*  79 */         return false;
/*     */       }
/*  81 */       int b0 = this.iter.next();
/*  82 */       if (b0 == 61) {
/*  83 */         throw CommonMessages.msg.unexpectedPadding();
/*     */       }
/*  85 */       if (!this.iter.hasNext()) {
/*  86 */         if (this.requirePadding) {
/*  87 */           throw CommonMessages.msg.expectedPadding();
/*     */         }
/*  89 */         throw CommonMessages.msg.incompleteDecode();
/*     */       } 
/*     */       
/*  92 */       int b1 = this.iter.next();
/*  93 */       if (b1 == 61) {
/*  94 */         throw CommonMessages.msg.unexpectedPadding();
/*     */       }
/*  96 */       this.o0 = calc0(b0, b1);
/*  97 */       if (!this.iter.hasNext()) {
/*  98 */         if (this.requirePadding) {
/*  99 */           throw CommonMessages.msg.expectedPadding();
/*     */         }
/* 101 */         this.state = 25;
/* 102 */         return true;
/*     */       } 
/* 104 */       int b2 = this.iter.next();
/* 105 */       if (b2 == 61) {
/* 106 */         for (int i = 0; i < 5; i++) {
/* 107 */           if (!this.iter.hasNext()) {
/* 108 */             throw CommonMessages.msg.expectedPaddingCharacters(6);
/*     */           }
/* 110 */           if (this.iter.next() != 61) {
/* 111 */             throw CommonMessages.msg.expectedPaddingCharacters(6);
/*     */           }
/*     */         } 
/* 114 */         this.state = 15;
/* 115 */         return true;
/*     */       } 
/* 117 */       if (!this.iter.hasNext()) {
/* 118 */         if (this.requirePadding) {
/* 119 */           throw CommonMessages.msg.expectedPadding();
/*     */         }
/* 121 */         throw CommonMessages.msg.incompleteDecode();
/*     */       } 
/*     */       
/* 124 */       int b3 = this.iter.next();
/* 125 */       if (b3 == 61) {
/* 126 */         throw CommonMessages.msg.unexpectedPadding();
/*     */       }
/* 128 */       this.o1 = calc1(b1, b2, b3);
/* 129 */       if (!this.iter.hasNext()) {
/* 130 */         if (this.requirePadding) {
/* 131 */           throw CommonMessages.msg.expectedPadding();
/*     */         }
/* 133 */         this.state = 23;
/* 134 */         return true;
/*     */       } 
/* 136 */       int b4 = this.iter.next();
/* 137 */       if (b4 == 61) {
/* 138 */         for (int i = 0; i < 3; i++) {
/* 139 */           if (!this.iter.hasNext()) {
/* 140 */             throw CommonMessages.msg.expectedPaddingCharacters(4);
/*     */           }
/* 142 */           if (this.iter.next() != 61) {
/* 143 */             throw CommonMessages.msg.expectedPaddingCharacters(4);
/*     */           }
/*     */         } 
/* 146 */         this.state = 13;
/* 147 */         return true;
/*     */       } 
/* 149 */       this.o2 = calc2(b3, b4);
/* 150 */       if (!this.iter.hasNext()) {
/* 151 */         if (this.requirePadding) {
/* 152 */           throw CommonMessages.msg.expectedPadding();
/*     */         }
/* 154 */         this.state = 20;
/* 155 */         return true;
/*     */       } 
/* 157 */       int b5 = this.iter.next();
/* 158 */       if (b5 == 61) {
/* 159 */         for (int i = 0; i < 2; i++) {
/* 160 */           if (!this.iter.hasNext()) {
/* 161 */             throw CommonMessages.msg.expectedPaddingCharacters(3);
/*     */           }
/* 163 */           if (this.iter.next() != 61) {
/* 164 */             throw CommonMessages.msg.expectedPaddingCharacters(3);
/*     */           }
/*     */         } 
/* 167 */         this.state = 10;
/* 168 */         return true;
/*     */       } 
/* 170 */       if (!this.iter.hasNext()) {
/* 171 */         if (this.requirePadding) {
/* 172 */           throw CommonMessages.msg.expectedPadding();
/*     */         }
/* 174 */         throw CommonMessages.msg.incompleteDecode();
/*     */       } 
/*     */       
/* 177 */       int b6 = this.iter.next();
/* 178 */       if (b6 == 61) {
/* 179 */         throw CommonMessages.msg.unexpectedPadding();
/*     */       }
/* 181 */       this.o3 = calc3(b4, b5, b6);
/* 182 */       if (!this.iter.hasNext()) {
/* 183 */         if (this.requirePadding) {
/* 184 */           throw CommonMessages.msg.expectedPadding();
/*     */         }
/* 186 */         this.state = 16;
/* 187 */         return true;
/*     */       } 
/* 189 */       int b7 = this.iter.next();
/* 190 */       if (b7 == 61) {
/* 191 */         this.state = 6;
/* 192 */         return true;
/*     */       } 
/* 194 */       this.o4 = calc4(b6, b7);
/* 195 */       this.state = 1;
/* 196 */       return true;
/*     */     } 
/* 198 */     return (this.state < 26);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasPrevious() {
/* 203 */     return (this.offset > 0);
/*     */   }
/*     */   
/*     */   abstract int calc0(int paramInt1, int paramInt2);
/*     */   
/*     */   abstract int calc1(int paramInt1, int paramInt2, int paramInt3);
/*     */   
/*     */   abstract int calc2(int paramInt1, int paramInt2);
/*     */   
/*     */   abstract int calc3(int paramInt1, int paramInt2, int paramInt3);
/*     */   
/*     */   abstract int calc4(int paramInt1, int paramInt2);
/*     */   
/*     */   public int next() {
/* 217 */     if (!hasNext()) {
/* 218 */       throw new NoSuchElementException();
/*     */     }
/* 220 */     switch (this.state) {
/*     */       case 1:
/*     */       case 6:
/*     */       case 10:
/*     */       case 13:
/*     */       case 16:
/*     */       case 20:
/*     */       case 23:
/* 228 */         this.state++;
/* 229 */         this.offset++;
/* 230 */         return this.o0;
/*     */       
/*     */       case 2:
/*     */       case 7:
/*     */       case 11:
/*     */       case 17:
/*     */       case 21:
/* 237 */         this.state++;
/* 238 */         this.offset++;
/* 239 */         return this.o1;
/*     */       
/*     */       case 3:
/*     */       case 8:
/*     */       case 18:
/* 244 */         this.state++;
/* 245 */         this.offset++;
/* 246 */         return this.o2;
/*     */       
/*     */       case 4:
/* 249 */         this.state = 5;
/* 250 */         this.offset++;
/* 251 */         return this.o3;
/*     */       
/*     */       case 5:
/* 254 */         this.state = 0;
/* 255 */         this.offset++;
/* 256 */         return this.o4;
/*     */       
/*     */       case 9:
/* 259 */         this.state = 26;
/* 260 */         this.offset++;
/* 261 */         return this.o3;
/*     */       
/*     */       case 12:
/* 264 */         this.state = 27;
/* 265 */         this.offset++;
/* 266 */         return this.o2;
/*     */       
/*     */       case 14:
/* 269 */         this.state = 28;
/* 270 */         this.offset++;
/* 271 */         return this.o1;
/*     */       
/*     */       case 15:
/* 274 */         this.state = 29;
/* 275 */         this.offset++;
/* 276 */         return this.o0;
/*     */       
/*     */       case 19:
/* 279 */         this.state = 30;
/* 280 */         this.offset++;
/* 281 */         return this.o3;
/*     */       
/*     */       case 22:
/* 284 */         this.state = 31;
/* 285 */         this.offset++;
/* 286 */         return this.o2;
/*     */       
/*     */       case 24:
/* 289 */         this.state = 32;
/* 290 */         this.offset++;
/* 291 */         return this.o1;
/*     */       
/*     */       case 25:
/* 294 */         this.state = 33;
/* 295 */         this.offset++;
/* 296 */         return this.o0;
/*     */     } 
/*     */ 
/*     */     
/* 300 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int peekNext() throws NoSuchElementException {
/* 306 */     if (!hasNext()) {
/* 307 */       throw new NoSuchElementException();
/*     */     }
/* 309 */     switch (this.state) {
/*     */       case 1:
/*     */       case 6:
/*     */       case 10:
/*     */       case 13:
/*     */       case 15:
/*     */       case 16:
/*     */       case 20:
/*     */       case 23:
/*     */       case 25:
/* 319 */         return this.o0;
/*     */       
/*     */       case 2:
/*     */       case 7:
/*     */       case 11:
/*     */       case 14:
/*     */       case 17:
/*     */       case 21:
/*     */       case 24:
/* 328 */         return this.o1;
/*     */       
/*     */       case 3:
/*     */       case 8:
/*     */       case 12:
/*     */       case 18:
/*     */       case 22:
/* 335 */         return this.o2;
/*     */       
/*     */       case 4:
/*     */       case 9:
/*     */       case 19:
/* 340 */         return this.o3;
/*     */       
/*     */       case 5:
/* 343 */         return this.o4;
/*     */     } 
/*     */ 
/*     */     
/* 347 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int previous() {
/*     */     int i, b7, b6, b5, b4, b3, b2, b1, b0;
/* 353 */     if (!hasPrevious()) {
/* 354 */       throw new NoSuchElementException();
/*     */     }
/* 356 */     int skipChars = 0;
/* 357 */     switch (this.state) {
/*     */       case 0:
/*     */       case 1:
/*     */       case 6:
/*     */       case 10:
/*     */       case 13:
/*     */       case 15:
/*     */       case 16:
/*     */       case 20:
/*     */       case 23:
/*     */       case 25:
/* 368 */         if (this.state == 6 || this.state == 10 || this.state == 13 || this.state == 15) {
/* 369 */           skipChars = 8;
/* 370 */         } else if (this.state == 16) {
/* 371 */           skipChars = 7;
/* 372 */         } else if (this.state == 20) {
/* 373 */           skipChars = 5;
/* 374 */         } else if (this.state == 23) {
/* 375 */           skipChars = 4;
/* 376 */         } else if (this.state == 25) {
/* 377 */           skipChars = 2;
/*     */         } 
/* 379 */         for (i = 0; i < skipChars; i++) {
/* 380 */           this.iter.previous();
/*     */         }
/* 382 */         b7 = this.iter.previous();
/* 383 */         b6 = this.iter.previous();
/* 384 */         b5 = this.iter.previous();
/* 385 */         b4 = this.iter.previous();
/* 386 */         b3 = this.iter.previous();
/* 387 */         b2 = this.iter.previous();
/* 388 */         b1 = this.iter.previous();
/* 389 */         b0 = this.iter.previous();
/* 390 */         this.o0 = calc0(b0, b1);
/* 391 */         this.o1 = calc1(b1, b2, b3);
/* 392 */         this.o2 = calc2(b3, b4);
/* 393 */         this.o3 = calc3(b4, b5, b6);
/* 394 */         this.o4 = calc4(b6, b7);
/* 395 */         this.state = 5;
/* 396 */         this.offset--;
/* 397 */         return this.o4;
/*     */       
/*     */       case 2:
/*     */       case 7:
/*     */       case 11:
/*     */       case 14:
/*     */       case 17:
/*     */       case 21:
/*     */       case 24:
/* 406 */         this.state--;
/* 407 */         this.offset--;
/* 408 */         return this.o0;
/*     */       
/*     */       case 3:
/*     */       case 8:
/*     */       case 12:
/*     */       case 18:
/*     */       case 22:
/* 415 */         this.state--;
/* 416 */         this.offset--;
/* 417 */         return this.o1;
/*     */       
/*     */       case 4:
/*     */       case 9:
/*     */       case 19:
/* 422 */         this.state--;
/* 423 */         this.offset--;
/* 424 */         return this.o2;
/*     */       
/*     */       case 5:
/* 427 */         this.state = 4;
/* 428 */         this.offset--;
/* 429 */         return this.o3;
/*     */       
/*     */       case 26:
/* 432 */         this.state = 9;
/* 433 */         this.offset--;
/* 434 */         return this.o3;
/*     */       
/*     */       case 27:
/* 437 */         this.state = 12;
/* 438 */         this.offset--;
/* 439 */         return this.o2;
/*     */       
/*     */       case 28:
/* 442 */         this.state = 14;
/* 443 */         this.offset--;
/* 444 */         return this.o1;
/*     */       
/*     */       case 29:
/* 447 */         this.state = 15;
/* 448 */         this.offset--;
/* 449 */         return this.o0;
/*     */       
/*     */       case 30:
/* 452 */         this.state = 19;
/* 453 */         this.offset--;
/* 454 */         return this.o3;
/*     */       
/*     */       case 31:
/* 457 */         this.state = 22;
/* 458 */         this.offset--;
/* 459 */         return this.o2;
/*     */       
/*     */       case 32:
/* 462 */         this.state = 24;
/* 463 */         this.offset--;
/* 464 */         return this.o1;
/*     */       
/*     */       case 33:
/* 467 */         this.state = 25;
/* 468 */         this.offset--;
/* 469 */         return this.o0;
/*     */     } 
/*     */     
/* 472 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int peekPrevious() throws NoSuchElementException {
/*     */     int i, b7, b6, j;
/* 478 */     if (!hasPrevious()) {
/* 479 */       throw new NoSuchElementException();
/*     */     }
/* 481 */     int skipChars = 0;
/* 482 */     switch (this.state) {
/*     */       case 0:
/*     */       case 1:
/*     */       case 6:
/*     */       case 10:
/*     */       case 13:
/*     */       case 15:
/*     */       case 16:
/*     */       case 20:
/*     */       case 23:
/*     */       case 25:
/* 493 */         if (this.state == 6 || this.state == 10 || this.state == 13 || this.state == 15) {
/* 494 */           skipChars = 8;
/* 495 */         } else if (this.state == 16) {
/* 496 */           skipChars = 7;
/* 497 */         } else if (this.state == 20) {
/* 498 */           skipChars = 5;
/* 499 */         } else if (this.state == 23) {
/* 500 */           skipChars = 4;
/* 501 */         } else if (this.state == 25) {
/* 502 */           skipChars = 2;
/*     */         } 
/* 504 */         for (i = 0; i < skipChars; i++) {
/* 505 */           this.iter.previous();
/*     */         }
/* 507 */         b7 = this.iter.previous();
/* 508 */         b6 = this.iter.peekPrevious();
/* 509 */         this.iter.next();
/* 510 */         for (j = 0; j < skipChars; j++) {
/* 511 */           this.iter.next();
/*     */         }
/* 513 */         return calc4(b6, b7);
/*     */       
/*     */       case 2:
/*     */       case 7:
/*     */       case 11:
/*     */       case 14:
/*     */       case 17:
/*     */       case 21:
/*     */       case 24:
/*     */       case 29:
/*     */       case 33:
/* 524 */         return this.o0;
/*     */       
/*     */       case 3:
/*     */       case 8:
/*     */       case 12:
/*     */       case 18:
/*     */       case 22:
/*     */       case 28:
/*     */       case 32:
/* 533 */         return this.o1;
/*     */       
/*     */       case 4:
/*     */       case 9:
/*     */       case 19:
/*     */       case 27:
/*     */       case 31:
/* 540 */         return this.o2;
/*     */       
/*     */       case 5:
/*     */       case 26:
/*     */       case 30:
/* 545 */         return this.o3;
/*     */     } 
/*     */     
/* 548 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getIndex() {
/* 554 */     return this.offset;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\Base32DecodingByteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */