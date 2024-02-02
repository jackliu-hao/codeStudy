/*    */ package io.undertow.websockets.core;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.WritableByteChannel;
/*    */ import org.xnio.channels.Channels;
/*    */ import org.xnio.channels.SuspendableWriteChannel;
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
/*    */ public final class BinaryOutputStream
/*    */   extends OutputStream
/*    */ {
/*    */   private final StreamSinkFrameChannel sender;
/*    */   private boolean closed;
/*    */   
/*    */   public BinaryOutputStream(StreamSinkFrameChannel sender) {
/* 38 */     this.sender = sender;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 43 */     checkClosed();
/* 44 */     if (Thread.currentThread() == this.sender.getIoThread()) {
/* 45 */       throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
/*    */     }
/* 47 */     Channels.writeBlocking((WritableByteChannel)this.sender, ByteBuffer.wrap(b, off, len));
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/* 52 */     checkClosed();
/* 53 */     if (Thread.currentThread() == this.sender.getIoThread()) {
/* 54 */       throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
/*    */     }
/* 56 */     Channels.writeBlocking((WritableByteChannel)this.sender, ByteBuffer.wrap(new byte[] { (byte)b }));
/*    */   }
/*    */ 
/*    */   
/*    */   public void flush() throws IOException {
/* 61 */     checkClosed();
/* 62 */     if (Thread.currentThread() == this.sender.getIoThread()) {
/* 63 */       throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
/*    */     }
/* 65 */     this.sender.flush();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 70 */     if (!this.closed) {
/* 71 */       this.closed = true;
/* 72 */       this.sender.shutdownWrites();
/* 73 */       Channels.flushBlocking((SuspendableWriteChannel)this.sender);
/*    */     } 
/*    */   }
/*    */   
/*    */   private void checkClosed() throws IOException {
/* 78 */     if (this.closed)
/* 79 */       throw UndertowMessages.MESSAGES.streamIsClosed(); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\BinaryOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */