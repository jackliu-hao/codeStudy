/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.compress.utils.CloseShieldFilterInputStream;
/*     */ import org.apache.commons.compress.utils.CountingInputStream;
/*     */ import org.apache.commons.compress.utils.InputStreamStatistics;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ExplodingInputStream
/*     */   extends InputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*     */   private final InputStream in;
/*     */   private BitStream bits;
/*     */   private final int dictionarySize;
/*     */   private final int numberOfTrees;
/*     */   private final int minimumMatchLength;
/*     */   private BinaryTree literalTree;
/*     */   private BinaryTree lengthTree;
/*     */   private BinaryTree distanceTree;
/*  67 */   private final CircularBuffer buffer = new CircularBuffer(32768);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long uncompressedCount;
/*     */ 
/*     */ 
/*     */   
/*     */   private long treeSizes;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExplodingInputStream(int dictionarySize, int numberOfTrees, InputStream in) {
/*  82 */     if (dictionarySize != 4096 && dictionarySize != 8192) {
/*  83 */       throw new IllegalArgumentException("The dictionary size must be 4096 or 8192");
/*     */     }
/*  85 */     if (numberOfTrees != 2 && numberOfTrees != 3) {
/*  86 */       throw new IllegalArgumentException("The number of trees must be 2 or 3");
/*     */     }
/*  88 */     this.dictionarySize = dictionarySize;
/*  89 */     this.numberOfTrees = numberOfTrees;
/*  90 */     this.minimumMatchLength = numberOfTrees;
/*  91 */     this.in = in;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init() throws IOException {
/* 100 */     if (this.bits == null) {
/*     */       
/* 102 */       try (CountingInputStream i = new CountingInputStream((InputStream)new CloseShieldFilterInputStream(this.in))) {
/* 103 */         if (this.numberOfTrees == 3) {
/* 104 */           this.literalTree = BinaryTree.decode((InputStream)i, 256);
/*     */         }
/*     */         
/* 107 */         this.lengthTree = BinaryTree.decode((InputStream)i, 64);
/* 108 */         this.distanceTree = BinaryTree.decode((InputStream)i, 64);
/* 109 */         this.treeSizes += i.getBytesRead();
/*     */       } 
/*     */       
/* 112 */       this.bits = new BitStream(this.in);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 118 */     if (!this.buffer.available()) {
/*     */       try {
/* 120 */         fillBuffer();
/* 121 */       } catch (IllegalArgumentException ex) {
/* 122 */         throw new IOException("bad IMPLODE stream", ex);
/*     */       } 
/*     */     }
/*     */     
/* 126 */     int ret = this.buffer.get();
/* 127 */     if (ret > -1) {
/* 128 */       this.uncompressedCount++;
/*     */     }
/* 130 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 138 */     return this.bits.getBytesRead() + this.treeSizes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getUncompressedCount() {
/* 146 */     return this.uncompressedCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 154 */     this.in.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fillBuffer() throws IOException {
/* 162 */     init();
/*     */     
/* 164 */     int bit = this.bits.nextBit();
/* 165 */     if (bit == -1) {
/*     */       return;
/*     */     }
/*     */     
/* 169 */     if (bit == 1) {
/*     */       int literal;
/*     */       
/* 172 */       if (this.literalTree != null) {
/* 173 */         literal = this.literalTree.read(this.bits);
/*     */       } else {
/* 175 */         literal = this.bits.nextByte();
/*     */       } 
/*     */       
/* 178 */       if (literal == -1) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 183 */       this.buffer.put(literal);
/*     */     }
/*     */     else {
/*     */       
/* 187 */       int distanceLowSize = (this.dictionarySize == 4096) ? 6 : 7;
/* 188 */       int distanceLow = (int)this.bits.nextBits(distanceLowSize);
/* 189 */       int distanceHigh = this.distanceTree.read(this.bits);
/* 190 */       if (distanceHigh == -1 && distanceLow <= 0) {
/*     */         return;
/*     */       }
/*     */       
/* 194 */       int distance = distanceHigh << distanceLowSize | distanceLow;
/*     */       
/* 196 */       int length = this.lengthTree.read(this.bits);
/* 197 */       if (length == 63) {
/* 198 */         long nextByte = this.bits.nextBits(8);
/* 199 */         if (nextByte == -1L) {
/*     */           return;
/*     */         }
/*     */         
/* 203 */         length = (int)(length + nextByte);
/*     */       } 
/* 205 */       length += this.minimumMatchLength;
/*     */       
/* 207 */       this.buffer.copy(distance + 1, length);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ExplodingInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */