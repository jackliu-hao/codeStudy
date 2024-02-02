/*    */ package io.undertow.websockets.extensions;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import io.undertow.websockets.core.StreamSinkFrameChannel;
/*    */ import io.undertow.websockets.core.StreamSourceFrameChannel;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class NoopExtensionFunction
/*    */   implements ExtensionFunction {
/* 10 */   public static final ExtensionFunction INSTANCE = new NoopExtensionFunction();
/*    */ 
/*    */   
/*    */   public boolean hasExtensionOpCode() {
/* 14 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int writeRsv(int rsv) {
/* 19 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public PooledByteBuffer transformForWrite(PooledByteBuffer pooledBuffer, StreamSinkFrameChannel channel, boolean lastFrame) throws IOException {
/* 24 */     return pooledBuffer;
/*    */   }
/*    */ 
/*    */   
/*    */   public PooledByteBuffer transformForRead(PooledByteBuffer pooledBuffer, StreamSourceFrameChannel channel, boolean lastFragmentOfFrame) throws IOException {
/* 29 */     return pooledBuffer;
/*    */   }
/*    */   
/*    */   public void dispose() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\extensions\NoopExtensionFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */