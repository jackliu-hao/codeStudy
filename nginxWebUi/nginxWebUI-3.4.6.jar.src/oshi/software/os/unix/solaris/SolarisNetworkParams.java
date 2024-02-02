/*    */ package oshi.software.os.unix.solaris;
/*    */ 
/*    */ import com.sun.jna.Native;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.jna.platform.unix.solaris.SolarisLibc;
/*    */ import oshi.software.common.AbstractNetworkParams;
/*    */ import oshi.util.ExecutingCommand;
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
/*    */ @ThreadSafe
/*    */ final class SolarisNetworkParams
/*    */   extends AbstractNetworkParams
/*    */ {
/* 41 */   private static final SolarisLibc LIBC = SolarisLibc.INSTANCE;
/*    */ 
/*    */   
/*    */   public String getHostName() {
/* 45 */     byte[] hostnameBuffer = new byte[256];
/* 46 */     if (0 != LIBC.gethostname(hostnameBuffer, hostnameBuffer.length)) {
/* 47 */       return super.getHostName();
/*    */     }
/* 49 */     return Native.toString(hostnameBuffer);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getIpv4DefaultGateway() {
/* 54 */     return searchGateway(ExecutingCommand.runNative("route get -inet default"));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getIpv6DefaultGateway() {
/* 59 */     return searchGateway(ExecutingCommand.runNative("route get -inet6 default"));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\solaris\SolarisNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */