/*     */ package oshi.software.os.mac;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.jna.platform.mac.SystemB;
/*     */ import oshi.jna.platform.unix.CLibrary;
/*     */ import oshi.software.common.AbstractNetworkParams;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ final class MacNetworkParams
/*     */   extends AbstractNetworkParams
/*     */ {
/*  52 */   private static final Logger LOG = LoggerFactory.getLogger(MacNetworkParams.class);
/*     */   
/*  54 */   private static final SystemB SYS = SystemB.INSTANCE;
/*     */   
/*     */   private static final String IPV6_ROUTE_HEADER = "Internet6:";
/*     */   
/*     */   private static final String DEFAULT_GATEWAY = "default";
/*     */ 
/*     */   
/*     */   public String getDomainName() {
/*  62 */     CLibrary.Addrinfo hint = new CLibrary.Addrinfo();
/*  63 */     hint.ai_flags = 2;
/*  64 */     String hostname = "";
/*     */     try {
/*  66 */       hostname = InetAddress.getLocalHost().getHostName();
/*  67 */     } catch (UnknownHostException e) {
/*  68 */       LOG.error("Unknown host exception when getting address of local host: {}", e.getMessage());
/*  69 */       return "";
/*     */     } 
/*  71 */     PointerByReference ptr = new PointerByReference();
/*  72 */     int res = SYS.getaddrinfo(hostname, null, hint, ptr);
/*  73 */     if (res > 0) {
/*  74 */       if (LOG.isErrorEnabled()) {
/*  75 */         LOG.error("Failed getaddrinfo(): {}", SYS.gai_strerror(res));
/*     */       }
/*  77 */       return "";
/*     */     } 
/*  79 */     CLibrary.Addrinfo info = new CLibrary.Addrinfo(ptr.getValue());
/*  80 */     String canonname = info.ai_canonname.trim();
/*  81 */     SYS.freeaddrinfo(ptr.getValue());
/*  82 */     return canonname;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getHostName() {
/*  87 */     byte[] hostnameBuffer = new byte[256];
/*  88 */     if (0 != SYS.gethostname(hostnameBuffer, hostnameBuffer.length)) {
/*  89 */       return super.getHostName();
/*     */     }
/*  91 */     return Native.toString(hostnameBuffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getIpv4DefaultGateway() {
/*  96 */     return searchGateway(ExecutingCommand.runNative("route -n get default"));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getIpv6DefaultGateway() {
/* 101 */     List<String> lines = ExecutingCommand.runNative("netstat -nr");
/* 102 */     boolean v6Table = false;
/* 103 */     for (String line : lines) {
/* 104 */       if (v6Table && line.startsWith("default")) {
/* 105 */         String[] fields = ParseUtil.whitespaces.split(line);
/* 106 */         if (fields.length > 2 && fields[2].contains("G"))
/* 107 */           return fields[1].split("%")[0];  continue;
/*     */       } 
/* 109 */       if (line.startsWith("Internet6:")) {
/* 110 */         v6Table = true;
/*     */       }
/*     */     } 
/* 113 */     return "";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\mac\MacNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */