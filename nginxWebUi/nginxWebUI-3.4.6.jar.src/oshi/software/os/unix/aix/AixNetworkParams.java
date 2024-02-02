/*    */ package oshi.software.os.unix.aix;
/*    */ 
/*    */ import com.sun.jna.Native;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.jna.platform.unix.aix.AixLibc;
/*    */ import oshi.software.common.AbstractNetworkParams;
/*    */ import oshi.util.ExecutingCommand;
/*    */ import oshi.util.ParseUtil;
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
/*    */ @ThreadSafe
/*    */ final class AixNetworkParams
/*    */   extends AbstractNetworkParams
/*    */ {
/* 43 */   private static final AixLibc LIBC = AixLibc.INSTANCE;
/*    */ 
/*    */   
/*    */   public String getHostName() {
/* 47 */     byte[] hostnameBuffer = new byte[256];
/* 48 */     if (0 != LIBC.gethostname(hostnameBuffer, hostnameBuffer.length)) {
/* 49 */       return super.getHostName();
/*    */     }
/* 51 */     return Native.toString(hostnameBuffer);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getIpv4DefaultGateway() {
/* 56 */     return getDefaultGateway("netstat -rnf inet");
/*    */   }
/*    */ 
/*    */   
/*    */   public String getIpv6DefaultGateway() {
/* 61 */     return getDefaultGateway("netstat -rnf inet6");
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
/*    */ 
/*    */ 
/*    */   
/*    */   private static String getDefaultGateway(String netstat) {
/* 78 */     for (String line : ExecutingCommand.runNative(netstat)) {
/* 79 */       String[] split = ParseUtil.whitespaces.split(line);
/* 80 */       if (split.length > 7 && "default".equals(split[0])) {
/* 81 */         return split[1];
/*    */       }
/*    */     } 
/* 84 */     return "unknown";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\aix\AixNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */