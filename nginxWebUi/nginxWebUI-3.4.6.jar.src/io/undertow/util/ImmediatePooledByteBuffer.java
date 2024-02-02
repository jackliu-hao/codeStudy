/*    */ package io.undertow.util;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import java.nio.ByteBuffer;
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
/*    */ public class ImmediatePooledByteBuffer
/*    */   implements PooledByteBuffer
/*    */ {
/*    */   private ByteBuffer buffer;
/*    */   
/*    */   public ImmediatePooledByteBuffer(ByteBuffer buffer) {
/* 33 */     this.buffer = buffer;
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteBuffer getBuffer() {
/* 38 */     return this.buffer;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() {}
/*    */ 
/*    */   
/*    */   public boolean isOpen() {
/* 47 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ImmediatePooledByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */