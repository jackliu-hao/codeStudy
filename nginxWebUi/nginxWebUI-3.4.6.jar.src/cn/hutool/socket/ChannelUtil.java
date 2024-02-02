/*    */ package cn.hutool.socket;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.core.thread.ThreadFactoryBuilder;
/*    */ import java.io.IOException;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.nio.channels.AsynchronousChannelGroup;
/*    */ import java.nio.channels.AsynchronousSocketChannel;
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
/*    */ public class ChannelUtil
/*    */ {
/*    */   public static AsynchronousChannelGroup createFixedGroup(int poolSize) {
/*    */     try {
/* 30 */       return AsynchronousChannelGroup.withFixedThreadPool(poolSize, 
/*    */           
/* 32 */           ThreadFactoryBuilder.create().setNamePrefix("Huool-socket-").build());
/*    */     }
/* 34 */     catch (IOException e) {
/* 35 */       throw new IORuntimeException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static AsynchronousSocketChannel connect(AsynchronousChannelGroup group, InetSocketAddress address) {
/*    */     AsynchronousSocketChannel channel;
/*    */     try {
/* 49 */       channel = AsynchronousSocketChannel.open(group);
/* 50 */     } catch (IOException e) {
/* 51 */       throw new IORuntimeException(e);
/*    */     } 
/*    */     
/*    */     try {
/* 55 */       channel.connect(address).get();
/* 56 */     } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
/* 57 */       IoUtil.close(channel);
/* 58 */       throw new SocketRuntimeException(e);
/*    */     } 
/* 60 */     return channel;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\ChannelUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */