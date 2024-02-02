/*    */ package io.undertow.websockets.spi;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.websockets.core.WebSocketChannel;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.Set;
/*    */ import org.xnio.FinishedIoFuture;
/*    */ import org.xnio.FutureResult;
/*    */ import org.xnio.IoFuture;
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
/*    */ public class BlockingWebSocketHttpServerExchange
/*    */   extends AsyncWebSocketHttpServerExchange
/*    */ {
/*    */   private final OutputStream out;
/*    */   private final InputStream in;
/*    */   
/*    */   public BlockingWebSocketHttpServerExchange(HttpServerExchange exchange, Set<WebSocketChannel> peerConnections) {
/* 43 */     super(exchange, peerConnections);
/* 44 */     this.out = exchange.getOutputStream();
/* 45 */     this.in = exchange.getInputStream();
/*    */   }
/*    */ 
/*    */   
/*    */   public IoFuture<Void> sendData(ByteBuffer data) {
/*    */     try {
/* 51 */       while (data.hasRemaining()) {
/* 52 */         this.out.write(data.get());
/*    */       }
/* 54 */       return (IoFuture<Void>)new FinishedIoFuture(null);
/* 55 */     } catch (IOException e) {
/* 56 */       FutureResult<Void> ioFuture = new FutureResult();
/* 57 */       ioFuture.setException(e);
/* 58 */       return ioFuture.getIoFuture();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public IoFuture<byte[]> readRequestData() {
/* 64 */     ByteArrayOutputStream data = new ByteArrayOutputStream();
/*    */     try {
/* 66 */       byte[] buf = new byte[1024];
/*    */       int r;
/* 68 */       while ((r = this.in.read(buf)) != -1) {
/* 69 */         data.write(buf, 0, r);
/*    */       }
/* 71 */       return (IoFuture<byte[]>)new FinishedIoFuture(data.toByteArray());
/* 72 */     } catch (IOException e) {
/* 73 */       FutureResult<byte[]> ioFuture = new FutureResult();
/* 74 */       ioFuture.setException(e);
/* 75 */       return ioFuture.getIoFuture();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\spi\BlockingWebSocketHttpServerExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */