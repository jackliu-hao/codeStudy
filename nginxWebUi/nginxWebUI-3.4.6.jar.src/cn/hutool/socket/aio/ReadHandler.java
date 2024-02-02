/*    */ package cn.hutool.socket.aio;
/*    */ 
/*    */ import cn.hutool.socket.SocketRuntimeException;
/*    */ import java.nio.channels.CompletionHandler;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ReadHandler
/*    */   implements CompletionHandler<Integer, AioSession>
/*    */ {
/*    */   public void completed(Integer result, AioSession session) {
/* 17 */     session.callbackRead();
/*    */   }
/*    */ 
/*    */   
/*    */   public void failed(Throwable exc, AioSession session) {
/* 22 */     throw new SocketRuntimeException(exc);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\aio\ReadHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */