/*     */ package com.github.jaiimageio.impl.common;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LZWStringTable
/*     */ {
/*  88 */   byte[] strChr_ = new byte[4096];
/*  89 */   short[] strNxt_ = new short[4096];
/*  90 */   int[] strLen_ = new int[4096];
/*  91 */   short[] strHsh_ = new short[9973];
/*     */   
/*     */   private static final int RES_CODES = 2;
/*     */   
/*     */   private static final short HASH_FREE = -1;
/*     */   
/*     */   private static final short NEXT_FIRST = -1;
/*     */   private static final int MAXBITS = 12;
/*     */   private static final int MAXSTR = 4096;
/*     */   private static final short HASHSIZE = 9973;
/*     */   private static final short HASHSTEP = 2039;
/*     */   short numStrings_;
/*     */   
/*     */   public int AddCharString(short index, byte b) {
/* 105 */     if (this.numStrings_ >= 4096)
/*     */     {
/* 107 */       return 65535;
/*     */     }
/*     */     
/* 110 */     int hshidx = Hash(index, b);
/* 111 */     while (this.strHsh_[hshidx] != -1) {
/* 112 */       hshidx = (hshidx + 2039) % 9973;
/*     */     }
/* 114 */     this.strHsh_[hshidx] = this.numStrings_;
/* 115 */     this.strChr_[this.numStrings_] = b;
/* 116 */     if (index == -1) {
/*     */       
/* 118 */       this.strNxt_[this.numStrings_] = -1;
/* 119 */       this.strLen_[this.numStrings_] = 1;
/*     */     }
/*     */     else {
/*     */       
/* 123 */       this.strNxt_[this.numStrings_] = index;
/* 124 */       this.strLen_[this.numStrings_] = this.strLen_[index] + 1;
/*     */     } 
/*     */     
/* 127 */     this.numStrings_ = (short)(this.numStrings_ + 1); return this.numStrings_;
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
/*     */   public short FindCharString(short index, byte b) {
/* 140 */     if (index == -1) {
/* 141 */       return (short)(b & 0xFF);
/*     */     }
/* 143 */     int hshidx = Hash(index, b); int nxtidx;
/* 144 */     while ((nxtidx = this.strHsh_[hshidx]) != -1) {
/*     */       
/* 146 */       if (this.strNxt_[nxtidx] == index && this.strChr_[nxtidx] == b)
/* 147 */         return (short)nxtidx; 
/* 148 */       hshidx = (hshidx + 2039) % 9973;
/*     */     } 
/*     */     
/* 151 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ClearTable(int codesize) {
/* 160 */     this.numStrings_ = 0;
/*     */     
/* 162 */     for (int q = 0; q < 9973; q++) {
/* 163 */       this.strHsh_[q] = -1;
/*     */     }
/* 165 */     int w = (1 << codesize) + 2;
/* 166 */     for (int i = 0; i < w; i++) {
/* 167 */       AddCharString((short)-1, (byte)i);
/*     */     }
/*     */   }
/*     */   
/*     */   public static int Hash(short index, byte lastbyte) {
/* 172 */     return (((short)(lastbyte << 8) ^ index) & 0xFFFF) % 9973;
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
/*     */   public int expandCode(byte[] buf, int offset, short code, int skipHead) {
/*     */     int expandLen;
/* 197 */     if (offset == -2)
/*     */     {
/* 199 */       if (skipHead == 1) skipHead = 0; 
/*     */     }
/* 201 */     if (code == -1 || skipHead == this.strLen_[code])
/*     */     {
/* 203 */       return 0;
/*     */     }
/*     */     
/* 206 */     int codeLen = this.strLen_[code] - skipHead;
/* 207 */     int bufSpace = buf.length - offset;
/* 208 */     if (bufSpace > codeLen) {
/* 209 */       expandLen = codeLen;
/*     */     } else {
/* 211 */       expandLen = bufSpace;
/*     */     } 
/* 213 */     int skipTail = codeLen - expandLen;
/*     */     
/* 215 */     int idx = offset + expandLen;
/*     */ 
/*     */ 
/*     */     
/* 219 */     while (idx > offset && code != -1) {
/*     */       
/* 221 */       if (--skipTail < 0)
/*     */       {
/* 223 */         buf[--idx] = this.strChr_[code];
/*     */       }
/* 225 */       code = this.strNxt_[code];
/*     */     } 
/*     */     
/* 228 */     if (codeLen > expandLen) {
/* 229 */       return -expandLen;
/*     */     }
/* 231 */     return expandLen;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void dump(PrintStream out) {
/* 237 */     for (int i = 258; i < this.numStrings_; i++)
/* 238 */       out.println(" strNxt_[" + i + "] = " + this.strNxt_[i] + " strChr_ " + 
/* 239 */           Integer.toHexString(this.strChr_[i] & 0xFF) + " strLen_ " + 
/* 240 */           Integer.toHexString(this.strLen_[i])); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\LZWStringTable.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */