/*    */ package cn.hutool.system;
/*    */ 
/*    */ import cn.hutool.core.net.NetUtil;
/*    */ import java.io.Serializable;
/*    */ import java.net.InetAddress;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HostInfo
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final String HOST_NAME;
/*    */   private final String HOST_ADDRESS;
/*    */   
/*    */   public HostInfo() {
/* 18 */     InetAddress localhost = NetUtil.getLocalhost();
/* 19 */     if (null != localhost) {
/* 20 */       this.HOST_NAME = localhost.getHostName();
/* 21 */       this.HOST_ADDRESS = localhost.getHostAddress();
/*    */     } else {
/* 23 */       this.HOST_NAME = null;
/* 24 */       this.HOST_ADDRESS = null;
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
/*    */ 
/*    */   
/*    */   public final String getName() {
/* 38 */     return this.HOST_NAME;
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
/*    */   public final String getAddress() {
/* 51 */     return this.HOST_ADDRESS;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String toString() {
/* 61 */     StringBuilder builder = new StringBuilder();
/*    */     
/* 63 */     SystemUtil.append(builder, "Host Name:    ", getName());
/* 64 */     SystemUtil.append(builder, "Host Address: ", getAddress());
/*    */     
/* 66 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\HostInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */