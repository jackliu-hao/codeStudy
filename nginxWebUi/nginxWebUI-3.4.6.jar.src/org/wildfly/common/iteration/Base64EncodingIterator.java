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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class Base64EncodingIterator
/*     */   extends CodePointIterator
/*     */ {
/*     */   private final ByteIterator iter;
/*     */   private final boolean addPadding;
/*     */   private int c0;
/*     */   private int c1;
/*     */   private int c2;
/*     */   private int c3;
/*     */   private int state;
/*     */   private int offset;
/*     */   
/*     */   Base64EncodingIterator(ByteIterator iter, boolean addPadding) {
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
/*     */   public boolean hasNext() {
/*  59 */     return ((this.state == 0 && this.iter.hasNext()) || (this.state > 0 && this.state < 13));
/*     */   } abstract int calc0(int paramInt);
/*     */   abstract int calc1(int paramInt1, int paramInt2);
/*     */   public boolean hasPrevious() {
/*  63 */     return (this.offset > 0);
/*     */   }
/*     */ 
/*     */   
/*     */   abstract int calc2(int paramInt1, int paramInt2);
/*     */ 
/*     */   
/*     */   abstract int calc3(int paramInt);
/*     */ 
/*     */   
/*     */   public int next() throws NoSuchElementException {
/*     */     int b0, b1, b2;
/*  75 */     if (!hasNext()) throw new NoSuchElementException(); 
/*  76 */     this.offset++;
/*  77 */     switch (this.state) {
/*     */       case 0:
/*  79 */         assert this.iter.hasNext();
/*  80 */         b0 = this.iter.next();
/*  81 */         this.c0 = calc0(b0);
/*  82 */         if (!this.iter.hasNext()) {
/*  83 */           this.c1 = calc1(b0, 0);
/*  84 */           this.state = 6;
/*  85 */           return this.c0;
/*     */         } 
/*  87 */         b1 = this.iter.next();
/*  88 */         this.c1 = calc1(b0, b1);
/*  89 */         if (!this.iter.hasNext()) {
/*  90 */           this.c2 = calc2(b1, 0);
/*  91 */           this.state = 10;
/*  92 */           return this.c0;
/*     */         } 
/*  94 */         b2 = this.iter.next();
/*  95 */         this.c2 = calc2(b1, b2);
/*  96 */         this.c3 = calc3(b2);
/*  97 */         this.state = 2;
/*  98 */         return this.c0;
/*     */       
/*     */       case 1:
/* 101 */         this.state = 2;
/* 102 */         return this.c0;
/*     */       
/*     */       case 2:
/* 105 */         this.state = 3;
/* 106 */         return this.c1;
/*     */       
/*     */       case 3:
/* 109 */         this.state = 4;
/* 110 */         return this.c2;
/*     */       
/*     */       case 4:
/* 113 */         this.state = 0;
/* 114 */         return this.c3;
/*     */       
/*     */       case 5:
/* 117 */         this.state = 6;
/* 118 */         return this.c0;
/*     */       
/*     */       case 6:
/* 121 */         this.state = this.addPadding ? 7 : 13;
/* 122 */         return this.c1;
/*     */       
/*     */       case 7:
/* 125 */         this.state = 8;
/* 126 */         return 61;
/*     */       
/*     */       case 8:
/* 129 */         this.state = 13;
/* 130 */         return 61;
/*     */       
/*     */       case 9:
/* 133 */         this.state = 10;
/* 134 */         return this.c0;
/*     */       
/*     */       case 10:
/* 137 */         this.state = 11;
/* 138 */         return this.c1;
/*     */       
/*     */       case 11:
/* 141 */         this.state = this.addPadding ? 12 : 14;
/* 142 */         return this.c2;
/*     */       
/*     */       case 12:
/* 145 */         this.state = 14;
/* 146 */         return 61;
/*     */     } 
/*     */     
/* 149 */     throw Assert.impossibleSwitchCase(this.state);
/*     */   }
/*     */   public int peekNext() throws NoSuchElementException {
/*     */     int b0;
/*     */     int b1;
/*     */     int b2;
/* 155 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 156 */     switch (this.state) {
/*     */       case 0:
/* 158 */         assert this.iter.hasNext();
/* 159 */         b0 = this.iter.next();
/* 160 */         this.c0 = calc0(b0);
/* 161 */         if (!this.iter.hasNext()) {
/* 162 */           this.c1 = calc1(b0, 0);
/* 163 */           this.state = 5;
/* 164 */           return this.c0;
/*     */         } 
/* 166 */         b1 = this.iter.next();
/* 167 */         this.c1 = calc1(b0, b1);
/* 168 */         if (!this.iter.hasNext()) {
/* 169 */           this.c2 = calc2(b1, 0);
/* 170 */           this.state = 9;
/* 171 */           return this.c0;
/*     */         } 
/* 173 */         b2 = this.iter.next();
/* 174 */         this.c2 = calc2(b1, b2);
/* 175 */         this.c3 = calc3(b2);
/* 176 */         this.state = 1;
/* 177 */         return this.c0;
/*     */       
/*     */       case 1:
/* 180 */         return this.c0;
/*     */       
/*     */       case 2:
/* 183 */         return this.c1;
/*     */       
/*     */       case 3:
/* 186 */         return this.c2;
/*     */       
/*     */       case 4:
/* 189 */         return this.c3;
/*     */       
/*     */       case 5:
/* 192 */         return this.c0;
/*     */       
/*     */       case 6:
/* 195 */         return this.c1;
/*     */       
/*     */       case 7:
/* 198 */         return 61;
/*     */       
/*     */       case 8:
/* 201 */         return 61;
/*     */       
/*     */       case 9:
/* 204 */         return this.c0;
/*     */       
/*     */       case 10:
/* 207 */         return this.c1;
/*     */       
/*     */       case 11:
/* 210 */         return this.c2;
/*     */       
/*     */       case 12:
/* 213 */         return 61;
/*     */     } 
/*     */     
/* 216 */     throw Assert.impossibleSwitchCase(this.state);
/*     */   }
/*     */ 
/*     */   
/*     */   public int previous() throws NoSuchElementException {
/*     */     int b2, b1, b0;
/* 222 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 223 */     this.offset--;
/* 224 */     switch (this.state) {
/*     */       case 0:
/*     */       case 1:
/*     */       case 5:
/*     */       case 9:
/*     */       case 15:
/* 230 */         b2 = this.iter.previous();
/* 231 */         b1 = this.iter.previous();
/* 232 */         b0 = this.iter.previous();
/* 233 */         this.c0 = calc0(b0);
/* 234 */         this.c1 = calc1(b0, b1);
/* 235 */         this.c2 = calc2(b1, b2);
/* 236 */         this.c3 = calc3(b2);
/* 237 */         this.state = 4;
/* 238 */         return this.c3;
/*     */       
/*     */       case 2:
/* 241 */         this.state = 1;
/* 242 */         return this.c0;
/*     */       
/*     */       case 3:
/* 245 */         this.state = 2;
/* 246 */         return this.c1;
/*     */       
/*     */       case 4:
/* 249 */         this.state = 3;
/* 250 */         return this.c2;
/*     */       
/*     */       case 6:
/* 253 */         this.state = 5;
/* 254 */         return this.c0;
/*     */       
/*     */       case 7:
/* 257 */         this.state = 6;
/* 258 */         return this.c1;
/*     */       
/*     */       case 8:
/* 261 */         this.state = 7;
/* 262 */         return 61;
/*     */       
/*     */       case 10:
/* 265 */         this.state = 9;
/* 266 */         return this.c0;
/*     */       
/*     */       case 11:
/* 269 */         this.state = 10;
/* 270 */         return this.c1;
/*     */       
/*     */       case 12:
/* 273 */         this.state = 11;
/* 274 */         return this.c2;
/*     */       
/*     */       case 13:
/* 277 */         this.state = 8;
/* 278 */         return 61;
/*     */       
/*     */       case 14:
/* 281 */         this.state = 12;
/* 282 */         return 61;
/*     */     } 
/*     */     
/* 285 */     throw Assert.impossibleSwitchCase(this.state);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int peekPrevious() throws NoSuchElementException {
/* 291 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 292 */     switch (this.state) {
/*     */       case 0:
/*     */       case 1:
/*     */       case 5:
/*     */       case 9:
/*     */       case 15:
/* 298 */         return calc3(this.iter.peekPrevious());
/*     */       
/*     */       case 2:
/* 301 */         return this.c0;
/*     */       
/*     */       case 3:
/* 304 */         return this.c1;
/*     */       
/*     */       case 4:
/* 307 */         return this.c2;
/*     */       
/*     */       case 6:
/* 310 */         return this.c0;
/*     */       
/*     */       case 7:
/* 313 */         return this.c1;
/*     */       
/*     */       case 8:
/* 316 */         return 61;
/*     */       
/*     */       case 10:
/* 319 */         return this.c0;
/*     */       
/*     */       case 11:
/* 322 */         return this.c1;
/*     */       
/*     */       case 12:
/* 325 */         return this.c2;
/*     */       
/*     */       case 13:
/* 328 */         return 61;
/*     */       
/*     */       case 14:
/* 331 */         return 61;
/*     */     } 
/*     */     
/* 334 */     throw Assert.impossibleSwitchCase(this.state);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getIndex() {
/* 340 */     return this.offset;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\Base64EncodingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */