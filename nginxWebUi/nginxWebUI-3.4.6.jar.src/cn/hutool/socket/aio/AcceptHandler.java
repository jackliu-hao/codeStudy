/*    */ package cn.hutool.socket.aio;
/*    */ 
/*    */ import cn.hutool.log.StaticLog;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.AsynchronousSocketChannel;
/*    */ import java.nio.channels.CompletionHandler;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AcceptHandler
/*    */   implements CompletionHandler<AsynchronousSocketChannel, AioServer>
/*    */ {
/*    */   public void completed(AsynchronousSocketChannel socketChannel, AioServer aioServer) {
/* 20 */     aioServer.accept();
/*    */     
/* 22 */     IoAction<ByteBuffer> ioAction = aioServer.ioAction;
/*    */     
/* 24 */     AioSession session = new AioSession(socketChannel, ioAction, aioServer.config);
/*    */     
/* 26 */     ioAction.accept(session);
/*    */ 
/*    */     
/* 29 */     session.read();
/*    */   }
/*    */ 
/*    */   
/*    */   public void failed(Throwable exc, AioServer aioServer) {
/* 34 */     StaticLog.error(exc);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\aio\AcceptHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */