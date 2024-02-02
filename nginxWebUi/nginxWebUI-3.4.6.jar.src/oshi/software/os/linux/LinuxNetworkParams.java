/*     */ package oshi.software.os.linux;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.platform.linux.LibC;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.jna.platform.linux.LinuxLibc;
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
/*     */ final class LinuxNetworkParams
/*     */   extends AbstractNetworkParams
/*     */ {
/*  53 */   private static final Logger LOG = LoggerFactory.getLogger(LinuxNetworkParams.class);
/*     */   
/*  55 */   private static final LinuxLibc LIBC = LinuxLibc.INSTANCE;
/*     */   
/*     */   private static final String IPV4_DEFAULT_DEST = "0.0.0.0";
/*     */   
/*     */   private static final String IPV6_DEFAULT_DEST = "::/0";
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
/*  72 */     int res = LIBC.getaddrinfo(hostname, null, hint, ptr);
/*  73 */     if (res > 0) {
/*  74 */       if (LOG.isErrorEnabled()) {
/*  75 */         LOG.error("Failed getaddrinfo(): {}", LIBC.gai_strerror(res));
/*     */       }
/*  77 */       return "";
/*     */     } 
/*  79 */     CLibrary.Addrinfo info = new CLibrary.Addrinfo(ptr.getValue());
/*  80 */     String canonname = info.ai_canonname.trim();
/*  81 */     LIBC.freeaddrinfo(ptr.getValue());
/*  82 */     return canonname;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getHostName() {
/*  87 */     byte[] hostnameBuffer = new byte[256];
/*  88 */     if (0 != LibC.INSTANCE.gethostname(hostnameBuffer, hostnameBuffer.length)) {
/*  89 */       return super.getHostName();
/*     */     }
/*  91 */     return Native.toString(hostnameBuffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getIpv4DefaultGateway() {
/*  96 */     List<String> routes = ExecutingCommand.runNative("route -A inet -n");
/*  97 */     if (routes.size() <= 2) {
/*  98 */       return "";
/*     */     }
/*     */     
/* 101 */     String gateway = "";
/* 102 */     int minMetric = Integer.MAX_VALUE;
/*     */     
/* 104 */     for (int i = 2; i < routes.size(); i++) {
/* 105 */       String[] fields = ParseUtil.whitespaces.split(routes.get(i));
/* 106 */       if (fields.length > 4 && fields[0].equals("0.0.0.0")) {
/* 107 */         boolean isGateway = (fields[3].indexOf('G') != -1);
/* 108 */         int metric = ParseUtil.parseIntOrDefault(fields[4], 2147483647);
/* 109 */         if (isGateway && metric < minMetric) {
/* 110 */           minMetric = metric;
/* 111 */           gateway = fields[1];
/*     */         } 
/*     */       } 
/*     */     } 
/* 115 */     return gateway;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getIpv6DefaultGateway() {
/* 120 */     List<String> routes = ExecutingCommand.runNative("route -A inet6 -n");
/* 121 */     if (routes.size() <= 2) {
/* 122 */       return "";
/*     */     }
/*     */     
/* 125 */     String gateway = "";
/* 126 */     int minMetric = Integer.MAX_VALUE;
/*     */     
/* 128 */     for (int i = 2; i < routes.size(); i++) {
/* 129 */       String[] fields = ParseUtil.whitespaces.split(routes.get(i));
/* 130 */       if (fields.length > 3 && fields[0].equals("::/0")) {
/* 131 */         boolean isGateway = (fields[2].indexOf('G') != -1);
/* 132 */         int metric = ParseUtil.parseIntOrDefault(fields[3], 2147483647);
/* 133 */         if (isGateway && metric < minMetric) {
/* 134 */           minMetric = metric;
/* 135 */           gateway = fields[1];
/*     */         } 
/*     */       } 
/*     */     } 
/* 139 */     return gateway;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\linux\LinuxNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */