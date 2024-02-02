/*    */ package cn.hutool.socket.nio;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.SelectableChannel;
/*    */ import java.nio.channels.Selector;
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
/*    */ public class NioUtil
/*    */ {
/*    */   public static void registerChannel(Selector selector, SelectableChannel channel, Operation ops) {
/* 24 */     if (channel == null) {
/*    */       return;
/*    */     }
/*    */     
/*    */     try {
/* 29 */       channel.configureBlocking(false);
/*    */ 
/*    */       
/* 32 */       channel.register(selector, ops.getValue());
/* 33 */     } catch (IOException e) {
/* 34 */       throw new IORuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\nio\NioUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */