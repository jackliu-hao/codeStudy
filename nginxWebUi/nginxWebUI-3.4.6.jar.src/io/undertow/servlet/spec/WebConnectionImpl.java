/*    */ package io.undertow.servlet.spec;
/*    */ 
/*    */ import io.undertow.connector.ByteBufferPool;
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.Channel;
/*    */ import java.util.concurrent.Executor;
/*    */ import javax.servlet.ServletInputStream;
/*    */ import javax.servlet.ServletOutputStream;
/*    */ import javax.servlet.http.WebConnection;
/*    */ import org.xnio.ChannelListener;
/*    */ import org.xnio.IoUtils;
/*    */ import org.xnio.StreamConnection;
/*    */ import org.xnio.channels.StreamSinkChannel;
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
/*    */ public class WebConnectionImpl
/*    */   implements WebConnection
/*    */ {
/*    */   private final StreamConnection channel;
/*    */   private final UpgradeServletOutputStream outputStream;
/*    */   private final UpgradeServletInputStream inputStream;
/*    */   private final Executor ioExecutor;
/*    */   
/*    */   public WebConnectionImpl(StreamConnection channel, ByteBufferPool bufferPool, Executor ioExecutor) {
/* 44 */     this.channel = channel;
/* 45 */     this.ioExecutor = ioExecutor;
/* 46 */     this.outputStream = new UpgradeServletOutputStream((StreamSinkChannel)channel.getSinkChannel(), ioExecutor);
/* 47 */     this.inputStream = new UpgradeServletInputStream((StreamSourceChannel)channel.getSourceChannel(), bufferPool, ioExecutor);
/* 48 */     channel.getCloseSetter().set(new ChannelListener<StreamConnection>()
/*    */         {
/*    */           public void handleEvent(StreamConnection channel) {
/*    */             try {
/* 52 */               WebConnectionImpl.this.close();
/* 53 */             } catch (Exception e) {
/* 54 */               throw new RuntimeException(e);
/*    */             } 
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */   
/*    */   public ServletInputStream getInputStream() throws IOException {
/* 62 */     return this.inputStream;
/*    */   }
/*    */ 
/*    */   
/*    */   public ServletOutputStream getOutputStream() throws IOException {
/* 67 */     return this.outputStream;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws Exception {
/*    */     try {
/* 73 */       this.outputStream.closeBlocking();
/*    */     } finally {
/* 75 */       IoUtils.safeClose(new Closeable[] { (Closeable)this.inputStream, (Closeable)this.channel });
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\WebConnectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */