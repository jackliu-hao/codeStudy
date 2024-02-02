/*    */ package org.apache.http.util;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.SocketAddress;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class NetUtils
/*    */ {
/*    */   public static void formatAddress(StringBuilder buffer, SocketAddress socketAddress) {
/* 42 */     Args.notNull(buffer, "Buffer");
/* 43 */     Args.notNull(socketAddress, "Socket address");
/* 44 */     if (socketAddress instanceof InetSocketAddress) {
/* 45 */       InetSocketAddress socketaddr = (InetSocketAddress)socketAddress;
/* 46 */       InetAddress inetaddr = socketaddr.getAddress();
/* 47 */       buffer.append((inetaddr != null) ? inetaddr.getHostAddress() : inetaddr).append(':').append(socketaddr.getPort());
/*    */     } else {
/*    */       
/* 50 */       buffer.append(socketAddress);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\NetUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */