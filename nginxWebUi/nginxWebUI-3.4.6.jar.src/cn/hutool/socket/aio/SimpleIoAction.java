/*    */ package cn.hutool.socket.aio;
/*    */ 
/*    */ import cn.hutool.log.StaticLog;
/*    */ import java.nio.ByteBuffer;
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
/*    */ public abstract class SimpleIoAction
/*    */   implements IoAction<ByteBuffer>
/*    */ {
/*    */   public void accept(AioSession session) {}
/*    */   
/*    */   public void failed(Throwable exc, AioSession session) {
/* 22 */     StaticLog.error(exc);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\aio\SimpleIoAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */