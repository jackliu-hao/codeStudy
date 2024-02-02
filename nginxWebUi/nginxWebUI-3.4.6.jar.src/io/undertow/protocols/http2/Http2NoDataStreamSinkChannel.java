/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.FileChannel;
/*    */ import org.xnio.channels.StreamSourceChannel;
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
/*    */ abstract class Http2NoDataStreamSinkChannel
/*    */   extends AbstractHttp2StreamSinkChannel
/*    */ {
/*    */   protected Http2NoDataStreamSinkChannel(Http2Channel channel) {
/* 41 */     super(channel);
/*    */   }
/*    */ 
/*    */   
/*    */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 46 */     throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
/*    */   }
/*    */ 
/*    */   
/*    */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 51 */     throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
/*    */   }
/*    */ 
/*    */   
/*    */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 56 */     throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
/*    */   }
/*    */ 
/*    */   
/*    */   public long write(ByteBuffer[] srcs) throws IOException {
/* 61 */     throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
/*    */   }
/*    */ 
/*    */   
/*    */   public int write(ByteBuffer src) throws IOException {
/* 66 */     throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
/*    */   }
/*    */ 
/*    */   
/*    */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 71 */     throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
/*    */   }
/*    */ 
/*    */   
/*    */   public long writeFinal(ByteBuffer[] srcs) throws IOException {
/* 76 */     throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
/*    */   }
/*    */ 
/*    */   
/*    */   public int writeFinal(ByteBuffer src) throws IOException {
/* 81 */     throw UndertowMessages.MESSAGES.controlFrameCannotHaveBodyContent();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2NoDataStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */