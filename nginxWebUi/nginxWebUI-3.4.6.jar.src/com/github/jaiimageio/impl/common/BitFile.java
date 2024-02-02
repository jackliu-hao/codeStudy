/*     */ package com.github.jaiimageio.impl.common;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ public class BitFile
/*     */ {
/*     */   ImageOutputStream output_;
/*     */   byte[] buffer_;
/*     */   int index_;
/*     */   int bitsLeft_;
/*     */   boolean blocks_ = false;
/*     */   
/*     */   public BitFile(ImageOutputStream output, boolean blocks) {
/*  73 */     this.output_ = output;
/*  74 */     this.blocks_ = blocks;
/*  75 */     this.buffer_ = new byte[256];
/*  76 */     this.index_ = 0;
/*  77 */     this.bitsLeft_ = 8;
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/*  82 */     int numBytes = this.index_ + ((this.bitsLeft_ == 8) ? 0 : 1);
/*  83 */     if (numBytes > 0) {
/*     */       
/*  85 */       if (this.blocks_)
/*  86 */         this.output_.write(numBytes); 
/*  87 */       this.output_.write(this.buffer_, 0, numBytes);
/*  88 */       this.buffer_[0] = 0;
/*  89 */       this.index_ = 0;
/*  90 */       this.bitsLeft_ = 8;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBits(int bits, int numbits) throws IOException {
/*  96 */     int bitsWritten = 0;
/*  97 */     int numBytes = 255;
/*     */ 
/*     */     
/*     */     do {
/* 101 */       if ((this.index_ == 254 && this.bitsLeft_ == 0) || this.index_ > 254) {
/*     */         
/* 103 */         if (this.blocks_) {
/* 104 */           this.output_.write(numBytes);
/*     */         }
/* 106 */         this.output_.write(this.buffer_, 0, numBytes);
/*     */         
/* 108 */         this.buffer_[0] = 0;
/* 109 */         this.index_ = 0;
/* 110 */         this.bitsLeft_ = 8;
/*     */       } 
/*     */       
/* 113 */       if (numbits <= this.bitsLeft_)
/*     */       {
/* 115 */         if (this.blocks_)
/*     */         {
/* 117 */           this.buffer_[this.index_] = (byte)(this.buffer_[this.index_] | (bits & (1 << numbits) - 1) << 8 - this.bitsLeft_);
/* 118 */           bitsWritten += numbits;
/* 119 */           this.bitsLeft_ -= numbits;
/* 120 */           numbits = 0;
/*     */         }
/*     */         else
/*     */         {
/* 124 */           this.buffer_[this.index_] = (byte)(this.buffer_[this.index_] | (bits & (1 << numbits) - 1) << this.bitsLeft_ - numbits);
/* 125 */           bitsWritten += numbits;
/* 126 */           this.bitsLeft_ -= numbits;
/* 127 */           numbits = 0;
/*     */         
/*     */         }
/*     */ 
/*     */       
/*     */       }
/* 133 */       else if (this.blocks_)
/*     */       {
/*     */ 
/*     */         
/* 137 */         this.buffer_[this.index_] = (byte)(this.buffer_[this.index_] | (bits & (1 << this.bitsLeft_) - 1) << 8 - this.bitsLeft_);
/* 138 */         bitsWritten += this.bitsLeft_;
/* 139 */         bits >>= this.bitsLeft_;
/* 140 */         numbits -= this.bitsLeft_;
/* 141 */         this.buffer_[++this.index_] = 0;
/* 142 */         this.bitsLeft_ = 8;
/*     */ 
/*     */       
/*     */       }
/*     */       else
/*     */       {
/*     */         
/* 149 */         int topbits = bits >>> numbits - this.bitsLeft_ & (1 << this.bitsLeft_) - 1;
/* 150 */         this.buffer_[this.index_] = (byte)(this.buffer_[this.index_] | topbits);
/* 151 */         numbits -= this.bitsLeft_;
/* 152 */         bitsWritten += this.bitsLeft_;
/* 153 */         this.buffer_[++this.index_] = 0;
/* 154 */         this.bitsLeft_ = 8;
/*     */       }
/*     */     
/*     */     }
/* 158 */     while (numbits != 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\BitFile.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */