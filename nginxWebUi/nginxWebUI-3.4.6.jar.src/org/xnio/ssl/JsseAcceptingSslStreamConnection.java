/*    */ package org.xnio.ssl;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import javax.net.ssl.SSLContext;
/*    */ import javax.net.ssl.SSLEngine;
/*    */ import org.xnio.IoUtils;
/*    */ import org.xnio.OptionMap;
/*    */ import org.xnio.Pool;
/*    */ import org.xnio.StreamConnection;
/*    */ import org.xnio.channels.AcceptingChannel;
/*    */ import org.xnio.channels.ConnectedChannel;
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
/*    */ final class JsseAcceptingSslStreamConnection
/*    */   extends AbstractAcceptingSslChannel<SslConnection, StreamConnection>
/*    */ {
/*    */   JsseAcceptingSslStreamConnection(SSLContext sslContext, AcceptingChannel<? extends StreamConnection> tcpServer, OptionMap optionMap, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool, boolean startTls) {
/* 42 */     super(sslContext, tcpServer, optionMap, socketBufferPool, applicationBufferPool, startTls);
/*    */   }
/*    */ 
/*    */   
/*    */   public SslConnection accept(StreamConnection tcpConnection, SSLEngine engine) throws IOException {
/* 47 */     if (!JsseXnioSsl.NEW_IMPL) {
/* 48 */       return new JsseSslStreamConnection(tcpConnection, engine, this.socketBufferPool, this.applicationBufferPool, this.startTls);
/*    */     }
/* 50 */     JsseSslConnection connection = new JsseSslConnection(tcpConnection, engine, this.socketBufferPool, this.applicationBufferPool);
/* 51 */     if (!this.startTls) {
/*    */       try {
/* 53 */         connection.startHandshake();
/* 54 */       } catch (IOException e) {
/* 55 */         IoUtils.safeClose((Closeable)connection);
/* 56 */         throw e;
/*    */       } 
/*    */     }
/* 59 */     return connection;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\JsseAcceptingSslStreamConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */