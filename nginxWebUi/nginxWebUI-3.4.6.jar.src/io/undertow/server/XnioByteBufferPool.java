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
/*    */ public class XnioByteBufferPool
/*    */   implements ByteBufferPool
/*    */ {
/*    */   private final Pool<ByteBuffer> pool;
/*    */   private final ByteBufferPool arrayBackedPool;
/*    */   private final int bufferSize;
/*    */   private final boolean direct;
/*    */   
/*    */   public XnioByteBufferPool(Pool<ByteBuffer> pool) {
/* 39 */     this.pool = pool;
/* 40 */     Pooled<ByteBuffer> buf = pool.allocate();
/* 41 */     this.bufferSize = ((ByteBuffer)buf.getResource()).remaining();
/* 42 */     this.direct = !((ByteBuffer)buf.getResource()).hasArray();
/* 43 */     buf.free();
/* 44 */     if (this.direct) {
/* 45 */       this.arrayBackedPool = new DefaultByteBufferPool(false, this.bufferSize);
/*    */     } else {
/* 47 */       this.arrayBackedPool = this;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public PooledByteBuffer allocate() {
/* 53 */     final Pooled<ByteBuffer> buf = this.pool.allocate();
/* 54 */     return new PooledByteBuffer()
/*    */       {
/*    */         private boolean open = true;
/*    */ 
/*    */         
/*    */         public ByteBuffer getBuffer() {
/* 60 */           return (ByteBuffer)buf.getResource();
/*    */         }
/*    */ 
/*    */         
/*    */         public void close() {
/* 65 */           this.open = false;
/* 66 */           buf.free();
/*    */         }
/*    */ 
/*    */         
/*    */         public boolean isOpen() {
/* 71 */           return this.open;
/*    */         }
/*    */       };
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteBufferPool getArrayBackedPool() {
/* 78 */     return this.arrayBackedPool;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public int getBufferSize() {
/* 88 */     return this.bufferSize;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isDirect() {
/* 93 */     return this.direct;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\XnioByteBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */