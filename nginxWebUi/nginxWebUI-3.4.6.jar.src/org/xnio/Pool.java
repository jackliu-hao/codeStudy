/*    */ package org.xnio;
/*    */ 
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
/*    */ public interface Pool<T>
/*    */ {
/* 27 */   public static final Pool<ByteBuffer> HEAP = new Pool<ByteBuffer>() {
/*    */       public Pooled<ByteBuffer> allocate() {
/* 29 */         return Buffers.globalPooledWrapper(ByteBufferPool.MEDIUM_HEAP.allocate());
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 36 */   public static final Pool<ByteBuffer> DIRECT = new Pool<ByteBuffer>() {
/*    */       public Pooled<ByteBuffer> allocate() {
/* 38 */         return Buffers.globalPooledWrapper(ByteBufferPool.MEDIUM_DIRECT.allocate());
/*    */       }
/*    */     };
/*    */   
/*    */   Pooled<T> allocate();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Pool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */