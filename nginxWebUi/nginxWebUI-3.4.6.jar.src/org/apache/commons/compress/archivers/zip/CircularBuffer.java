/*    */ package org.apache.commons.compress.archivers.zip;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class CircularBuffer
/*    */ {
/*    */   private final int size;
/*    */   private final byte[] buffer;
/*    */   private int readIndex;
/*    */   private int writeIndex;
/*    */   
/*    */   CircularBuffer(int size) {
/* 43 */     this.size = size;
/* 44 */     this.buffer = new byte[size];
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean available() {
/* 51 */     return (this.readIndex != this.writeIndex);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void put(int value) {
/* 58 */     this.buffer[this.writeIndex] = (byte)value;
/* 59 */     this.writeIndex = (this.writeIndex + 1) % this.size;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int get() {
/* 66 */     if (available()) {
/* 67 */       int value = this.buffer[this.readIndex];
/* 68 */       this.readIndex = (this.readIndex + 1) % this.size;
/* 69 */       return value & 0xFF;
/*    */     } 
/* 71 */     return -1;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void copy(int distance, int length) {
/* 81 */     int pos1 = this.writeIndex - distance;
/* 82 */     int pos2 = pos1 + length;
/* 83 */     for (int i = pos1; i < pos2; i++) {
/* 84 */       this.buffer[this.writeIndex] = this.buffer[(i + this.size) % this.size];
/* 85 */       this.writeIndex = (this.writeIndex + 1) % this.size;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\CircularBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */