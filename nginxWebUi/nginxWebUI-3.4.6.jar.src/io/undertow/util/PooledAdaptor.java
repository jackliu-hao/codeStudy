/*    */ package io.undertow.util;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import java.nio.ByteBuffer;
/*    */ import org.xnio.Pooled;
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
/*    */ public class PooledAdaptor
/*    */   implements Pooled<ByteBuffer>
/*    */ {
/*    */   private final PooledByteBuffer buffer;
/*    */   
/*    */   public PooledAdaptor(PooledByteBuffer buffer) {
/* 34 */     this.buffer = buffer;
/*    */   }
/*    */ 
/*    */   
/*    */   public void discard() {
/* 39 */     this.buffer.close();
/*    */   }
/*    */ 
/*    */   
/*    */   public void free() {
/* 44 */     this.buffer.close();
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteBuffer getResource() throws IllegalStateException {
/* 49 */     return this.buffer.getBuffer();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/* 54 */     this.buffer.close();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 59 */     return "PooledAdaptor(" + this.buffer + ")";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\PooledAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */