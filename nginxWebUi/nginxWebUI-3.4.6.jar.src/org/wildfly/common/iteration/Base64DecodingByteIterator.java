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
/*     */ abstract class Base64DecodingByteIterator
/*     */   extends ByteIterator
/*     */ {
/*     */   private final CodePointIterator iter;
/*     */   private final boolean requirePadding;
/*  45 */   private int state = 0;
/*     */   private int o0;
/*     */   private int o1;
/*     */   
/*     */   Base64DecodingByteIterator(CodePointIterator iter, boolean requirePadding) {
/*  50 */     this.iter = iter;
/*  51 */     this.requirePadding = requirePadding;
/*     */   }
/*     */   private int o2; private int offset;
/*     */   public boolean hasNext() {
/*  55 */     if (this.state == 0) {
/*  56 */       if (!this.iter.hasNext()) {
/*  57 */         return false;
/*     */       }
/*  59 */       int b0 = this.iter.next();
/*  60 */       if (b0 == 61) {
/*  61 */         throw CommonMessages.msg.unexpectedPadding();
/*     */       }
/*  63 */       if (!this.iter.hasNext()) {
/*  64 */         if (this.requirePadding) {
/*  65 */           throw CommonMessages.msg.expectedPadding();
/*     */         }
/*  67 */         throw CommonMessages.msg.incompleteDecode();
/*     */       } 
/*     */       
/*  70 */       int b1 = this.iter.next();
/*  71 */       if (b1 == 61) {
/*  72 */         throw CommonMessages.msg.unexpectedPadding();
/*     */       }
/*  74 */       this.o0 = calc0(b0, b1);
/*  75 */       if (!this.iter.hasNext()) {
/*  76 */         if (this.requirePadding) {
/*  77 */           throw CommonMessages.msg.expectedPadding();
/*     */         }
/*  79 */         this.state = 9;
/*  80 */         return true;
/*     */       } 
/*  82 */       int b2 = this.iter.next();
/*  83 */       if (b2 == 61) {
/*  84 */         if (!this.iter.hasNext()) {
/*  85 */           throw CommonMessages.msg.expectedTwoPaddingCharacters();
/*     */         }
/*  87 */         if (this.iter.next() != 61) {
/*  88 */           throw CommonMessages.msg.expectedTwoPaddingCharacters();
/*     */         }
/*  90 */         this.state = 6;
/*  91 */         return true;
/*     */       } 
/*  93 */       this.o1 = calc1(b1, b2);
/*  94 */       if (!this.iter.hasNext()) {
/*  95 */         if (this.requirePadding) {
/*  96 */           throw CommonMessages.msg.expectedPadding();
/*     */         }
/*  98 */         this.state = 7;
/*  99 */         return true;
/*     */       } 
/* 101 */       int b3 = this.iter.next();
/* 102 */       if (b3 == 61) {
/* 103 */         this.state = 4;
/* 104 */         return true;
/*     */       } 
/* 106 */       this.o2 = calc2(b2, b3);
/* 107 */       this.state = 1;
/* 108 */       return true;
/*     */     } 
/* 110 */     return (this.state < 10);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasPrevious() {
/* 115 */     return (this.state != 0 || this.offset > 0);
/*     */   }
/*     */   
/*     */   abstract int calc0(int paramInt1, int paramInt2);
/*     */   
/*     */   abstract int calc1(int paramInt1, int paramInt2);
/*     */   
/*     */   abstract int calc2(int paramInt1, int paramInt2);
/*     */   
/*     */   public int next() {
/* 125 */     if (!hasNext()) {
/* 126 */       throw new NoSuchElementException();
/*     */     }
/* 128 */     switch (this.state) {
/*     */       case 1:
/* 130 */         this.state = 2;
/* 131 */         this.offset++;
/* 132 */         return this.o0;
/*     */       
/*     */       case 2:
/* 135 */         this.state = 3;
/* 136 */         this.offset++;
/* 137 */         return this.o1;
/*     */       
/*     */       case 3:
/* 140 */         this.state = 0;
/* 141 */         this.offset++;
/* 142 */         return this.o2;
/*     */       
/*     */       case 4:
/* 145 */         this.state = 5;
/* 146 */         this.offset++;
/* 147 */         return this.o0;
/*     */       
/*     */       case 5:
/* 150 */         this.state = 11;
/* 151 */         this.offset++;
/* 152 */         return this.o1;
/*     */       
/*     */       case 6:
/* 155 */         this.state = 10;
/* 156 */         this.offset++;
/* 157 */         return this.o0;
/*     */       
/*     */       case 7:
/* 160 */         this.state = 8;
/* 161 */         this.offset++;
/* 162 */         return this.o0;
/*     */       
/*     */       case 8:
/* 165 */         this.state = 13;
/* 166 */         this.offset++;
/* 167 */         return this.o1;
/*     */       
/*     */       case 9:
/* 170 */         this.state = 12;
/* 171 */         this.offset++;
/* 172 */         return this.o0;
/*     */     } 
/*     */ 
/*     */     
/* 176 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int peekNext() throws NoSuchElementException {
/* 182 */     if (!hasNext()) {
/* 183 */       throw new NoSuchElementException();
/*     */     }
/* 185 */     switch (this.state) {
/*     */       case 1:
/*     */       case 4:
/*     */       case 6:
/*     */       case 7:
/*     */       case 9:
/* 191 */         return this.o0;
/*     */       
/*     */       case 2:
/*     */       case 5:
/*     */       case 8:
/* 196 */         return this.o1;
/*     */       
/*     */       case 3:
/* 199 */         return this.o2;
/*     */     } 
/*     */ 
/*     */     
/* 203 */     throw new NoSuchElementException();
/*     */   } public int previous() {
/*     */     int b3;
/*     */     int b2;
/*     */     int b1;
/*     */     int b0;
/* 209 */     if (!hasPrevious()) {
/* 210 */       throw new NoSuchElementException();
/*     */     }
/* 212 */     switch (this.state) {
/*     */       case 6:
/* 214 */         this.iter.previous();
/*     */ 
/*     */       
/*     */       case 4:
/* 218 */         this.iter.previous();
/*     */ 
/*     */       
/*     */       case 0:
/*     */       case 1:
/*     */       case 7:
/*     */       case 9:
/* 225 */         b3 = this.iter.previous();
/* 226 */         b2 = this.iter.previous();
/* 227 */         b1 = this.iter.previous();
/* 228 */         b0 = this.iter.previous();
/* 229 */         this.o0 = calc0(b0, b1);
/* 230 */         this.o1 = calc1(b1, b2);
/* 231 */         this.state = 3;
/* 232 */         this.offset--;
/* 233 */         return this.o2 = calc2(b2, b3);
/*     */       
/*     */       case 2:
/* 236 */         this.state = 1;
/* 237 */         this.offset--;
/* 238 */         return this.o0;
/*     */       
/*     */       case 3:
/* 241 */         this.state = 2;
/* 242 */         this.offset--;
/* 243 */         return this.o1;
/*     */       
/*     */       case 5:
/* 246 */         this.state = 4;
/* 247 */         this.offset--;
/* 248 */         return this.o0;
/*     */       
/*     */       case 8:
/* 251 */         this.state = 7;
/* 252 */         this.offset--;
/* 253 */         return this.o0;
/*     */       
/*     */       case 10:
/* 256 */         this.state = 6;
/* 257 */         this.offset--;
/* 258 */         return this.o0;
/*     */       
/*     */       case 11:
/* 261 */         this.state = 5;
/* 262 */         this.offset--;
/* 263 */         return this.o1;
/*     */       
/*     */       case 12:
/* 266 */         this.state = 9;
/* 267 */         this.offset--;
/* 268 */         return this.o0;
/*     */       
/*     */       case 13:
/* 271 */         this.state = 8;
/* 272 */         this.offset--;
/* 273 */         return this.o1;
/*     */     } 
/*     */ 
/*     */     
/* 277 */     throw new NoSuchElementException();
/*     */   }
/*     */   
/*     */   public int peekPrevious() throws NoSuchElementException {
/*     */     int b3;
/*     */     int b2;
/* 283 */     if (!hasPrevious()) {
/* 284 */       throw new NoSuchElementException();
/*     */     }
/* 286 */     switch (this.state) {
/*     */       case 6:
/* 288 */         this.iter.previous();
/*     */ 
/*     */       
/*     */       case 4:
/* 292 */         this.iter.previous();
/*     */ 
/*     */       
/*     */       case 0:
/*     */       case 1:
/*     */       case 7:
/*     */       case 9:
/* 299 */         b3 = this.iter.previous();
/* 300 */         b2 = this.iter.peekPrevious();
/* 301 */         this.iter.next();
/* 302 */         if (this.state == 4) {
/* 303 */           this.iter.next();
/* 304 */         } else if (this.state == 6) {
/* 305 */           this.iter.next();
/* 306 */           this.iter.next();
/*     */         } 
/* 308 */         return calc2(b2, b3);
/*     */       
/*     */       case 2:
/* 311 */         return this.o0;
/*     */       
/*     */       case 3:
/* 314 */         return this.o1;
/*     */       
/*     */       case 5:
/* 317 */         return this.o0;
/*     */       
/*     */       case 8:
/* 320 */         return this.o0;
/*     */       
/*     */       case 10:
/* 323 */         return this.o0;
/*     */       
/*     */       case 11:
/* 326 */         return this.o1;
/*     */       
/*     */       case 12:
/* 329 */         return this.o0;
/*     */       
/*     */       case 13:
/* 332 */         return this.o1;
/*     */     } 
/*     */ 
/*     */     
/* 336 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getIndex() {
/* 342 */     return this.offset;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\Base64DecodingByteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */