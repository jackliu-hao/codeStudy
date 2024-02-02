/*     */ package com.google.zxing.datamatrix.encoder;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultPlacement
/*     */ {
/*     */   private final CharSequence codewords;
/*     */   private final int numrows;
/*     */   private final int numcols;
/*     */   private final byte[] bits;
/*     */   
/*     */   public DefaultPlacement(CharSequence codewords, int numcols, int numrows) {
/*  39 */     this.codewords = codewords;
/*  40 */     this.numcols = numcols;
/*  41 */     this.numrows = numrows;
/*  42 */     this.bits = new byte[numcols * numrows];
/*  43 */     Arrays.fill(this.bits, (byte)-1);
/*     */   }
/*     */   
/*     */   final int getNumrows() {
/*  47 */     return this.numrows;
/*     */   }
/*     */   
/*     */   final int getNumcols() {
/*  51 */     return this.numcols;
/*     */   }
/*     */   
/*     */   final byte[] getBits() {
/*  55 */     return this.bits;
/*     */   }
/*     */   
/*     */   public final boolean getBit(int col, int row) {
/*  59 */     return (this.bits[row * this.numcols + col] == 1);
/*     */   }
/*     */   
/*     */   private void setBit(int col, int row, boolean bit) {
/*  63 */     this.bits[row * this.numcols + col] = (byte)(bit ? 1 : 0);
/*     */   }
/*     */   
/*     */   private boolean hasBit(int col, int row) {
/*  67 */     return (this.bits[row * this.numcols + col] >= 0);
/*     */   }
/*     */   
/*     */   public final void place() {
/*  71 */     int pos = 0;
/*  72 */     int row = 4;
/*  73 */     int col = 0;
/*     */ 
/*     */     
/*     */     do {
/*  77 */       if (row == this.numrows && col == 0) {
/*  78 */         corner1(pos++);
/*     */       }
/*  80 */       if (row == this.numrows - 2 && col == 0 && this.numcols % 4 != 0) {
/*  81 */         corner2(pos++);
/*     */       }
/*  83 */       if (row == this.numrows - 2 && col == 0 && this.numcols % 8 == 4) {
/*  84 */         corner3(pos++);
/*     */       }
/*  86 */       if (row == this.numrows + 4 && col == 2 && this.numcols % 8 == 0) {
/*  87 */         corner4(pos++);
/*     */       }
/*     */       
/*     */       do {
/*  91 */         if (row < this.numrows && col >= 0 && !hasBit(col, row)) {
/*  92 */           utah(row, col, pos++);
/*     */         }
/*  94 */         row -= 2;
/*  95 */         col += 2;
/*  96 */       } while (row >= 0 && col < this.numcols);
/*  97 */       row++;
/*  98 */       col += 3;
/*     */ 
/*     */       
/*     */       do {
/* 102 */         if (row >= 0 && col < this.numcols && !hasBit(col, row)) {
/* 103 */           utah(row, col, pos++);
/*     */         }
/* 105 */         row += 2;
/* 106 */         col -= 2;
/* 107 */       } while (row < this.numrows && col >= 0);
/* 108 */       row += 3;
/* 109 */       col++;
/*     */     
/*     */     }
/* 112 */     while (row < this.numrows || col < this.numcols);
/*     */ 
/*     */     
/* 115 */     if (!hasBit(this.numcols - 1, this.numrows - 1)) {
/* 116 */       setBit(this.numcols - 1, this.numrows - 1, true);
/* 117 */       setBit(this.numcols - 2, this.numrows - 2, true);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void module(int row, int col, int pos, int bit) {
/* 122 */     if (row < 0) {
/* 123 */       row += this.numrows;
/* 124 */       col += 4 - (this.numrows + 4) % 8;
/*     */     } 
/* 126 */     if (col < 0) {
/* 127 */       col += this.numcols;
/* 128 */       row += 4 - (this.numcols + 4) % 8;
/*     */     } 
/*     */ 
/*     */     
/* 132 */     int v = this.codewords.charAt(pos) & 1 << 8 - bit;
/* 133 */     setBit(col, row, (v != 0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void utah(int row, int col, int pos) {
/* 144 */     module(row - 2, col - 2, pos, 1);
/* 145 */     module(row - 2, col - 1, pos, 2);
/* 146 */     module(row - 1, col - 2, pos, 3);
/* 147 */     module(row - 1, col - 1, pos, 4);
/* 148 */     module(row - 1, col, pos, 5);
/* 149 */     module(row, col - 2, pos, 6);
/* 150 */     module(row, col - 1, pos, 7);
/* 151 */     module(row, col, pos, 8);
/*     */   }
/*     */   
/*     */   private void corner1(int pos) {
/* 155 */     module(this.numrows - 1, 0, pos, 1);
/* 156 */     module(this.numrows - 1, 1, pos, 2);
/* 157 */     module(this.numrows - 1, 2, pos, 3);
/* 158 */     module(0, this.numcols - 2, pos, 4);
/* 159 */     module(0, this.numcols - 1, pos, 5);
/* 160 */     module(1, this.numcols - 1, pos, 6);
/* 161 */     module(2, this.numcols - 1, pos, 7);
/* 162 */     module(3, this.numcols - 1, pos, 8);
/*     */   }
/*     */   
/*     */   private void corner2(int pos) {
/* 166 */     module(this.numrows - 3, 0, pos, 1);
/* 167 */     module(this.numrows - 2, 0, pos, 2);
/* 168 */     module(this.numrows - 1, 0, pos, 3);
/* 169 */     module(0, this.numcols - 4, pos, 4);
/* 170 */     module(0, this.numcols - 3, pos, 5);
/* 171 */     module(0, this.numcols - 2, pos, 6);
/* 172 */     module(0, this.numcols - 1, pos, 7);
/* 173 */     module(1, this.numcols - 1, pos, 8);
/*     */   }
/*     */   
/*     */   private void corner3(int pos) {
/* 177 */     module(this.numrows - 3, 0, pos, 1);
/* 178 */     module(this.numrows - 2, 0, pos, 2);
/* 179 */     module(this.numrows - 1, 0, pos, 3);
/* 180 */     module(0, this.numcols - 2, pos, 4);
/* 181 */     module(0, this.numcols - 1, pos, 5);
/* 182 */     module(1, this.numcols - 1, pos, 6);
/* 183 */     module(2, this.numcols - 1, pos, 7);
/* 184 */     module(3, this.numcols - 1, pos, 8);
/*     */   }
/*     */   
/*     */   private void corner4(int pos) {
/* 188 */     module(this.numrows - 1, 0, pos, 1);
/* 189 */     module(this.numrows - 1, this.numcols - 1, pos, 2);
/* 190 */     module(0, this.numcols - 3, pos, 3);
/* 191 */     module(0, this.numcols - 2, pos, 4);
/* 192 */     module(0, this.numcols - 1, pos, 5);
/* 193 */     module(1, this.numcols - 3, pos, 6);
/* 194 */     module(1, this.numcols - 2, pos, 7);
/* 195 */     module(1, this.numcols - 1, pos, 8);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\DefaultPlacement.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */