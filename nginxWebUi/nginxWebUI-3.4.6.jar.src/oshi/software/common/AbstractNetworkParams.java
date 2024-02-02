/*     */ package oshi.software.common;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.software.os.NetworkParams;
/*     */ import oshi.util.FileUtil;
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
/*     */ @ThreadSafe
/*     */ public abstract class AbstractNetworkParams
/*     */   implements NetworkParams
/*     */ {
/*  46 */   private static final Logger LOG = LoggerFactory.getLogger(AbstractNetworkParams.class);
/*     */   
/*     */   private static final String NAMESERVER = "nameserver";
/*     */   
/*     */   public String getDomainName() {
/*     */     try {
/*  52 */       return InetAddress.getLocalHost().getCanonicalHostName();
/*  53 */     } catch (UnknownHostException e) {
/*  54 */       LOG.error("Unknown host exception when getting address of local host: {}", e.getMessage());
/*  55 */       return "";
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getHostName() {
/*     */     try {
/*  62 */       String hn = InetAddress.getLocalHost().getHostName();
/*  63 */       int dot = hn.indexOf('.');
/*  64 */       if (dot == -1) {
/*  65 */         return hn;
/*     */       }
/*  67 */       return hn.substring(0, dot);
/*     */     }
/*  69 */     catch (UnknownHostException e) {
/*  70 */       LOG.error("Unknown host exception when getting address of local host: {}", e.getMessage());
/*  71 */       return "";
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getDnsServers() {
/*  77 */     List<String> resolv = FileUtil.readFile("/etc/resolv.conf");
/*  78 */     String key = "nameserver";
/*  79 */     int maxNameServer = 3;
/*  80 */     List<String> servers = new ArrayList<>();
/*  81 */     for (int i = 0; i < resolv.size() && servers.size() < maxNameServer; i++) {
/*  82 */       String line = resolv.get(i);
/*  83 */       if (line.startsWith(key)) {
/*  84 */         String value = line.substring(key.length()).replaceFirst("^[ \t]+", "");
/*  85 */         if (value.length() != 0 && value.charAt(0) != '#' && value.charAt(0) != ';') {
/*  86 */           String val = value.split("[ \t#;]", 2)[0];
/*  87 */           servers.add(val);
/*     */         } 
/*     */       } 
/*     */     } 
/*  91 */     return servers.<String>toArray(new String[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String searchGateway(List<String> lines) {
/* 103 */     for (String line : lines) {
/* 104 */       String leftTrimmed = line.replaceFirst("^\\s+", "");
/* 105 */       if (leftTrimmed.startsWith("gateway:")) {
/* 106 */         String[] split = ParseUtil.whitespaces.split(leftTrimmed);
/* 107 */         if (split.length < 2) {
/* 108 */           return "";
/*     */         }
/* 110 */         return split[1].split("%")[0];
/*     */       } 
/*     */     } 
/* 113 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 118 */     return String.format("Host name: %s, Domain name: %s, DNS servers: %s, IPv4 Gateway: %s, IPv6 Gateway: %s", new Object[] {
/* 119 */           getHostName(), getDomainName(), Arrays.toString((Object[])getDnsServers()), 
/* 120 */           getIpv4DefaultGateway(), getIpv6DefaultGateway()
/*     */         });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\common\AbstractNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */