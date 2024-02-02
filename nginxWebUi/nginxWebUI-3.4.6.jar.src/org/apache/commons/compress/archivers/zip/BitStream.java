/*    */ package org.apache.commons.compress.archivers.zip;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.nio.ByteOrder;
/*    */ import org.apache.commons.compress.utils.BitInputStream;
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
/*    */ class BitStream
/*    */   extends BitInputStream
/*    */ {
/*    */   BitStream(InputStream in) {
/* 36 */     super(in, ByteOrder.LITTLE_ENDIAN);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   int nextBit() throws IOException {
/* 45 */     return (int)readBits(1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   long nextBits(int n) throws IOException {
/* 55 */     if (n < 0 || n > 8) {
/* 56 */       throw new IOException("Trying to read " + n + " bits, at most 8 are allowed");
/*    */     }
/* 58 */     return readBits(n);
/*    */   }
/*    */   
/*    */   int nextByte() throws IOException {
/* 62 */     return (int)readBits(8);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\BitStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */