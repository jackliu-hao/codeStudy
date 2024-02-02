/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class IterableByteBufferInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private Iterator<ByteBuffer> iterator;
/*     */   private ByteBuffer currentByteBuffer;
/*     */   private int dataSize;
/*     */   private int currentIndex;
/*     */   private int currentByteBufferPos;
/*     */   private boolean hasArray;
/*     */   private byte[] currentArray;
/*     */   private int currentArrayOffset;
/*     */   private long currentAddress;
/*     */   
/*     */   IterableByteBufferInputStream(Iterable<ByteBuffer> data) {
/*  71 */     this.iterator = data.iterator();
/*  72 */     this.dataSize = 0;
/*  73 */     for (ByteBuffer unused : data) {
/*  74 */       this.dataSize++;
/*     */     }
/*  76 */     this.currentIndex = -1;
/*     */     
/*  78 */     if (!getNextByteBuffer()) {
/*  79 */       this.currentByteBuffer = Internal.EMPTY_BYTE_BUFFER;
/*  80 */       this.currentIndex = 0;
/*  81 */       this.currentByteBufferPos = 0;
/*  82 */       this.currentAddress = 0L;
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean getNextByteBuffer() {
/*  87 */     this.currentIndex++;
/*  88 */     if (!this.iterator.hasNext()) {
/*  89 */       return false;
/*     */     }
/*  91 */     this.currentByteBuffer = this.iterator.next();
/*  92 */     this.currentByteBufferPos = this.currentByteBuffer.position();
/*  93 */     if (this.currentByteBuffer.hasArray()) {
/*  94 */       this.hasArray = true;
/*  95 */       this.currentArray = this.currentByteBuffer.array();
/*  96 */       this.currentArrayOffset = this.currentByteBuffer.arrayOffset();
/*     */     } else {
/*  98 */       this.hasArray = false;
/*  99 */       this.currentAddress = UnsafeUtil.addressOffset(this.currentByteBuffer);
/* 100 */       this.currentArray = null;
/*     */     } 
/* 102 */     return true;
/*     */   }
/*     */   
/*     */   private void updateCurrentByteBufferPos(int numberOfBytesRead) {
/* 106 */     this.currentByteBufferPos += numberOfBytesRead;
/* 107 */     if (this.currentByteBufferPos == this.currentByteBuffer.limit()) {
/* 108 */       getNextByteBuffer();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 114 */     if (this.currentIndex == this.dataSize) {
/* 115 */       return -1;
/*     */     }
/* 117 */     if (this.hasArray) {
/* 118 */       int i = this.currentArray[this.currentByteBufferPos + this.currentArrayOffset] & 0xFF;
/* 119 */       updateCurrentByteBufferPos(1);
/* 120 */       return i;
/*     */     } 
/* 122 */     int result = UnsafeUtil.getByte(this.currentByteBufferPos + this.currentAddress) & 0xFF;
/* 123 */     updateCurrentByteBufferPos(1);
/* 124 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] output, int offset, int length) throws IOException {
/* 130 */     if (this.currentIndex == this.dataSize) {
/* 131 */       return -1;
/*     */     }
/* 133 */     int remaining = this.currentByteBuffer.limit() - this.currentByteBufferPos;
/* 134 */     if (length > remaining) {
/* 135 */       length = remaining;
/*     */     }
/* 137 */     if (this.hasArray) {
/* 138 */       System.arraycopy(this.currentArray, this.currentByteBufferPos + this.currentArrayOffset, output, offset, length);
/*     */       
/* 140 */       updateCurrentByteBufferPos(length);
/*     */     } else {
/* 142 */       int prevPos = this.currentByteBuffer.position();
/* 143 */       this.currentByteBuffer.position(this.currentByteBufferPos);
/* 144 */       this.currentByteBuffer.get(output, offset, length);
/* 145 */       this.currentByteBuffer.position(prevPos);
/* 146 */       updateCurrentByteBufferPos(length);
/*     */     } 
/* 148 */     return length;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\IterableByteBufferInputStream.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */