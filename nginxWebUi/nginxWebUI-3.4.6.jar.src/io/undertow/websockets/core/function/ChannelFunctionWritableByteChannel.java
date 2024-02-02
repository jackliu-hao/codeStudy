/*    */ package io.undertow.websockets.core.function;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.WritableByteChannel;
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
/*    */ public class ChannelFunctionWritableByteChannel
/*    */   implements WritableByteChannel
/*    */ {
/*    */   private final ChannelFunction[] functions;
/*    */   private final WritableByteChannel channel;
/*    */   
/*    */   public ChannelFunctionWritableByteChannel(WritableByteChannel channel, ChannelFunction... functions) {
/* 32 */     this.channel = channel;
/* 33 */     this.functions = functions;
/*    */   }
/*    */ 
/*    */   
/*    */   public int write(ByteBuffer src) throws IOException {
/* 38 */     for (ChannelFunction func : this.functions) {
/* 39 */       int pos = src.position();
/* 40 */       func.beforeWrite(src, pos, src.limit() - pos);
/*    */     } 
/* 42 */     return this.channel.write(src);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isOpen() {
/* 47 */     return this.channel.isOpen();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 52 */     this.channel.close();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\function\ChannelFunctionWritableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */