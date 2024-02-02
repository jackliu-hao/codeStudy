/*    */ package cn.hutool.socket.nio;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.log.StaticLog;
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.CompletionHandler;
/*    */ import java.nio.channels.ServerSocketChannel;
/*    */ import java.nio.channels.SocketChannel;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AcceptHandler
/*    */   implements CompletionHandler<ServerSocketChannel, NioServer>
/*    */ {
/*    */   public void completed(ServerSocketChannel serverSocketChannel, NioServer nioServer) {
/*    */     SocketChannel socketChannel;
/*    */     try {
/* 23 */       socketChannel = serverSocketChannel.accept();
/* 24 */       StaticLog.debug("Client [{}] accepted.", new Object[] { socketChannel.getRemoteAddress() });
/* 25 */     } catch (IOException e) {
/* 26 */       throw new IORuntimeException(e);
/*    */     } 
/*    */ 
/*    */     
/* 30 */     NioUtil.registerChannel(nioServer.getSelector(), socketChannel, Operation.READ);
/*    */   }
/*    */ 
/*    */   
/*    */   public void failed(Throwable exc, NioServer nioServer) {
/* 35 */     StaticLog.error(exc);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\nio\AcceptHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */