/*    */ package io.undertow.websockets.core.function;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.ReadableByteChannel;
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
/*    */ public class ChannelFunctionReadableByteChannel
/*    */   implements ReadableByteChannel
/*    */ {
/*    */   private final ChannelFunction[] functions;
/*    */   private final ReadableByteChannel channel;
/*    */   
/*    */   public ChannelFunctionReadableByteChannel(ReadableByteChannel channel, ChannelFunction... functions) {
/* 33 */     this.channel = channel;
/* 34 */     this.functions = functions;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(ByteBuffer dst) throws IOException {
/* 39 */     int pos = dst.position();
/* 40 */     int r = 0;
/*    */     try {
/* 42 */       r = this.channel.read(dst);
/* 43 */       return r;
/*    */     } finally {
/* 45 */       if (r > 0) {
/* 46 */         for (ChannelFunction func : this.functions) {
/* 47 */           func.afterRead(dst, pos, r);
/*    */         }
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isOpen() {
/* 55 */     return this.channel.isOpen();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 60 */     this.channel.close();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\function\ChannelFunctionReadableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */