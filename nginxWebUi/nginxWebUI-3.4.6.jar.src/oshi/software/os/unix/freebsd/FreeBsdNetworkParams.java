/*    */ package oshi.software.os.unix.freebsd;
/*    */ 
/*    */ import com.sun.jna.Native;
/*    */ import com.sun.jna.ptr.PointerByReference;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.jna.platform.unix.CLibrary;
/*    */ import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
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
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ final class FreeBsdNetworkParams
/*    */   extends AbstractNetworkParams
/*    */ {
/* 47 */   private static final Logger LOG = LoggerFactory.getLogger(FreeBsdNetworkParams.class);
/*    */   
/* 49 */   private static final FreeBsdLibc LIBC = FreeBsdLibc.INSTANCE;
/*    */ 
/*    */   
/*    */   public String getDomainName() {
/* 53 */     CLibrary.Addrinfo hint = new CLibrary.Addrinfo();
/* 54 */     hint.ai_flags = 2;
/* 55 */     String hostname = getHostName();
/*    */     
/* 57 */     PointerByReference ptr = new PointerByReference();
/* 58 */     int res = LIBC.getaddrinfo(hostname, null, hint, ptr);
/* 59 */     if (res > 0) {
/* 60 */       if (LOG.isErrorEnabled()) {
/* 61 */         LOG.warn("Failed getaddrinfo(): {}", LIBC.gai_strerror(res));
/*    */       }
/* 63 */       return "";
/*    */     } 
/* 65 */     CLibrary.Addrinfo info = new CLibrary.Addrinfo(ptr.getValue());
/* 66 */     String canonname = info.ai_canonname.trim();
/* 67 */     LIBC.freeaddrinfo(ptr.getValue());
/* 68 */     return canonname;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getHostName() {
/* 73 */     byte[] hostnameBuffer = new byte[256];
/* 74 */     if (0 != LIBC.gethostname(hostnameBuffer, hostnameBuffer.length)) {
/* 75 */       return super.getHostName();
/*    */     }
/* 77 */     return Native.toString(hostnameBuffer);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getIpv4DefaultGateway() {
/* 82 */     return searchGateway(ExecutingCommand.runNative("route -4 get default"));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getIpv6DefaultGateway() {
/* 87 */     return searchGateway(ExecutingCommand.runNative("route -6 get default"));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\freebsd\FreeBsdNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */