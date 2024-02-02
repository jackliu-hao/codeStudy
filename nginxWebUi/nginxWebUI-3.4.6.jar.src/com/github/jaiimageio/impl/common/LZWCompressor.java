/*     */ package com.github.jaiimageio.impl.common;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import javax.imageio.stream.ImageOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LZWCompressor
/*     */ {
/*     */   int codeSize_;
/*     */   int clearCode_;
/*     */   int endOfInfo_;
/*     */   int numBits_;
/*     */   int limit_;
/*     */   short prefix_;
/*     */   BitFile bf_;
/*     */   LZWStringTable lzss_;
/*     */   boolean tiffFudge_;
/*     */   
/*     */   public LZWCompressor(ImageOutputStream out, int codeSize, boolean TIFF) throws IOException {
/*  94 */     this.bf_ = new BitFile(out, !TIFF);
/*  95 */     this.codeSize_ = codeSize;
/*  96 */     this.tiffFudge_ = TIFF;
/*  97 */     this.clearCode_ = 1 << this.codeSize_;
/*  98 */     this.endOfInfo_ = this.clearCode_ + 1;
/*  99 */     this.numBits_ = this.codeSize_ + 1;
/*     */     
/* 101 */     this.limit_ = (1 << this.numBits_) - 1;
/* 102 */     if (this.tiffFudge_) {
/* 103 */       this.limit_--;
/*     */     }
/* 105 */     this.prefix_ = -1;
/* 106 */     this.lzss_ = new LZWStringTable();
/* 107 */     this.lzss_.ClearTable(this.codeSize_);
/* 108 */     this.bf_.writeBits(this.clearCode_, this.numBits_);
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
/*     */   public void compress(byte[] buf, int offset, int length) throws IOException {
/* 122 */     int maxOffset = offset + length;
/* 123 */     for (int idx = offset; idx < maxOffset; idx++) {
/*     */       
/* 125 */       byte c = buf[idx]; short index;
/* 126 */       if ((index = this.lzss_.FindCharString(this.prefix_, c)) != -1) {
/* 127 */         this.prefix_ = index;
/*     */       } else {
/*     */         
/* 130 */         this.bf_.writeBits(this.prefix_, this.numBits_);
/* 131 */         if (this.lzss_.AddCharString(this.prefix_, c) > this.limit_) {
/*     */           
/* 133 */           if (this.numBits_ == 12) {
/*     */             
/* 135 */             this.bf_.writeBits(this.clearCode_, this.numBits_);
/* 136 */             this.lzss_.ClearTable(this.codeSize_);
/* 137 */             this.numBits_ = this.codeSize_ + 1;
/*     */           } else {
/*     */             
/* 140 */             this.numBits_++;
/*     */           } 
/* 142 */           this.limit_ = (1 << this.numBits_) - 1;
/* 143 */           if (this.tiffFudge_)
/* 144 */             this.limit_--; 
/*     */         } 
/* 146 */         this.prefix_ = (short)((short)c & 0xFF);
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
/*     */   
/*     */   public void flush() throws IOException {
/* 159 */     if (this.prefix_ != -1) {
/* 160 */       this.bf_.writeBits(this.prefix_, this.numBits_);
/*     */     }
/* 162 */     this.bf_.writeBits(this.endOfInfo_, this.numBits_);
/* 163 */     this.bf_.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void dump(PrintStream out) {
/* 168 */     this.lzss_.dump(out);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\LZWCompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */