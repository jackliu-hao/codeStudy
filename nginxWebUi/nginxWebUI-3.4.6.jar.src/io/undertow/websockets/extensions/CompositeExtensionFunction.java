/*    */ package io.undertow.websockets.extensions;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import io.undertow.websockets.core.StreamSinkFrameChannel;
/*    */ import io.undertow.websockets.core.StreamSourceFrameChannel;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ 
/*    */ public class CompositeExtensionFunction
/*    */   implements ExtensionFunction
/*    */ {
/*    */   private final ExtensionFunction[] delegates;
/*    */   
/*    */   private CompositeExtensionFunction(ExtensionFunction... delegates) {
/* 15 */     this.delegates = delegates;
/*    */   }
/*    */   
/*    */   public static ExtensionFunction compose(List<ExtensionFunction> functions) {
/* 19 */     if (null == functions) {
/* 20 */       return NoopExtensionFunction.INSTANCE;
/*    */     }
/* 22 */     return compose(functions.<ExtensionFunction>toArray(new ExtensionFunction[functions.size()]));
/*    */   }
/*    */   
/*    */   public static ExtensionFunction compose(ExtensionFunction... functions) {
/* 26 */     if (functions == null || functions.length == 0)
/* 27 */       return NoopExtensionFunction.INSTANCE; 
/* 28 */     if (functions.length == 1) {
/* 29 */       return functions[0];
/*    */     }
/*    */     
/* 32 */     return new CompositeExtensionFunction(functions);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasExtensionOpCode() {
/* 37 */     for (ExtensionFunction delegate : this.delegates) {
/* 38 */       if (delegate.hasExtensionOpCode()) {
/* 39 */         return true;
/*    */       }
/*    */     } 
/* 42 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int writeRsv(int rsv) {
/* 47 */     for (ExtensionFunction ext : this.delegates) {
/* 48 */       rsv = ext.writeRsv(rsv);
/*    */     }
/*    */     
/* 51 */     return rsv;
/*    */   }
/*    */ 
/*    */   
/*    */   public PooledByteBuffer transformForWrite(PooledByteBuffer pooledBuffer, StreamSinkFrameChannel channel, boolean lastFrame) throws IOException {
/* 56 */     PooledByteBuffer result = pooledBuffer;
/* 57 */     for (ExtensionFunction delegate : this.delegates) {
/* 58 */       result = delegate.transformForWrite(result, channel, lastFrame);
/*    */     }
/* 60 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public PooledByteBuffer transformForRead(PooledByteBuffer pooledBuffer, StreamSourceFrameChannel channel, boolean lastFragementOfMessage) throws IOException {
/* 65 */     PooledByteBuffer result = pooledBuffer;
/*    */     
/* 67 */     for (ExtensionFunction delegate : this.delegates) {
/* 68 */       result = delegate.transformForRead(result, channel, lastFragementOfMessage);
/*    */     }
/* 70 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public void dispose() {
/* 75 */     for (ExtensionFunction delegate : this.delegates)
/* 76 */       delegate.dispose(); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\extensions\CompositeExtensionFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */