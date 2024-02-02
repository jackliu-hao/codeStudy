/*    */ package io.undertow.server;
/*    */ 
/*    */ import io.undertow.connector.ByteBufferPool;
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import java.nio.ByteBuffer;
/*    */ import org.xnio.Pool;
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
/*    */ 
/*    */ 
/*    */ public class XnioBufferPoolAdaptor
/*    */   implements Pool<ByteBuffer>
/*    */ {
/*    */   private final ByteBufferPool byteBufferPool;
/*    */   
/*    */   public XnioBufferPoolAdaptor(ByteBufferPool byteBufferPool) {
/* 38 */     this.byteBufferPool = byteBufferPool;
/*    */   }
/*    */ 
/*    */   
/*    */   public Pooled<ByteBuffer> allocate() {
/* 43 */     final PooledByteBuffer buf = this.byteBufferPool.allocate();
/* 44 */     return new Pooled<ByteBuffer>()
/*    */       {
/*    */         public void discard() {
/* 47 */           buf.close();
/*    */         }
/*    */ 
/*    */         
/*    */         public void free() {
/* 52 */           buf.close();
/*    */         }
/*    */ 
/*    */         
/*    */         public ByteBuffer getResource() throws IllegalStateException {
/* 57 */           return buf.getBuffer();
/*    */         }
/*    */ 
/*    */         
/*    */         public void close() {
/* 62 */           buf.close();
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\XnioBufferPoolAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */