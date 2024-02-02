/*    */ package org.xnio;
/*    */ 
/*    */ import java.nio.Buffer;
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
/*    */ public interface BufferAllocator<B extends Buffer>
/*    */ {
/* 46 */   public static final BufferAllocator<ByteBuffer> BYTE_BUFFER_ALLOCATOR = new BufferAllocator<ByteBuffer>() {
/*    */       public ByteBuffer allocate(int size) {
/* 48 */         return ByteBuffer.allocate(size);
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 55 */   public static final BufferAllocator<ByteBuffer> DIRECT_BYTE_BUFFER_ALLOCATOR = new BufferAllocator<ByteBuffer>() {
/*    */       public ByteBuffer allocate(int size) {
/* 57 */         return ByteBuffer.allocateDirect(size);
/*    */       }
/*    */     };
/*    */   
/*    */   B allocate(int paramInt) throws IllegalArgumentException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\BufferAllocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */