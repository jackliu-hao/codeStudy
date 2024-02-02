/*    */ package com.google.protobuf;
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
/*    */ abstract class BufferAllocator
/*    */ {
/* 41 */   private static final BufferAllocator UNPOOLED = new BufferAllocator()
/*    */     {
/*    */       public AllocatedBuffer allocateHeapBuffer(int capacity)
/*    */       {
/* 45 */         return AllocatedBuffer.wrap(new byte[capacity]);
/*    */       }
/*    */ 
/*    */       
/*    */       public AllocatedBuffer allocateDirectBuffer(int capacity) {
/* 50 */         return AllocatedBuffer.wrap(ByteBuffer.allocateDirect(capacity));
/*    */       }
/*    */     };
/*    */ 
/*    */   
/*    */   public static BufferAllocator unpooled() {
/* 56 */     return UNPOOLED;
/*    */   }
/*    */   
/*    */   public abstract AllocatedBuffer allocateHeapBuffer(int paramInt);
/*    */   
/*    */   public abstract AllocatedBuffer allocateDirectBuffer(int paramInt);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\BufferAllocator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */