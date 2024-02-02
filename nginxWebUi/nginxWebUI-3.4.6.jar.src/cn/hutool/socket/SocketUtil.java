/*    */ package cn.hutool.socket;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import java.io.IOException;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.Socket;
/*    */ import java.net.SocketAddress;
/*    */ import java.nio.channels.AsynchronousSocketChannel;
/*    */ import java.nio.channels.ClosedChannelException;
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
/*    */ public class SocketUtil
/*    */ {
/*    */   public static SocketAddress getRemoteAddress(AsynchronousSocketChannel channel) {
/*    */     try {
/* 29 */       return (null == channel) ? null : channel.getRemoteAddress();
/* 30 */     } catch (ClosedChannelException e) {
/*    */       
/* 32 */       return null;
/* 33 */     } catch (IOException e) {
/* 34 */       throw new IORuntimeException(e);
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
/*    */   public static boolean isConnected(AsynchronousSocketChannel channel) {
/* 46 */     return (null != getRemoteAddress(channel));
/*    */   }
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
/*    */   public static Socket connect(String hostname, int port) throws IORuntimeException {
/* 59 */     return connect(hostname, port, -1);
/*    */   }
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
/*    */   public static Socket connect(String hostname, int port, int connectionTimeout) throws IORuntimeException {
/* 73 */     return connect(new InetSocketAddress(hostname, port), connectionTimeout);
/*    */   }
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
/*    */   public static Socket connect(InetSocketAddress address, int connectionTimeout) throws IORuntimeException {
/* 86 */     Socket socket = new Socket();
/*    */     try {
/* 88 */       if (connectionTimeout <= 0) {
/* 89 */         socket.connect(address);
/*    */       } else {
/* 91 */         socket.connect(address, connectionTimeout);
/*    */       } 
/* 93 */     } catch (IOException e) {
/* 94 */       throw new IORuntimeException(e);
/*    */     } 
/* 96 */     return socket;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\SocketUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */