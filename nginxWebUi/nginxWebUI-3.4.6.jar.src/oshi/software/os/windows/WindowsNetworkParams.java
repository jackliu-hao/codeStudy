/*     */ package oshi.software.os.windows;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.win32.IPHlpAPI;
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.Kernel32Util;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ @ThreadSafe
/*     */ final class WindowsNetworkParams
/*     */   extends AbstractNetworkParams
/*     */ {
/*  53 */   private static final Logger LOG = LoggerFactory.getLogger(WindowsNetworkParams.class);
/*     */   
/*     */   private static final int COMPUTER_NAME_DNS_DOMAIN_FULLY_QUALIFIED = 3;
/*     */ 
/*     */   
/*     */   public String getDomainName() {
/*  59 */     char[] buffer = new char[256];
/*  60 */     IntByReference bufferSize = new IntByReference(buffer.length);
/*  61 */     if (!Kernel32.INSTANCE.GetComputerNameEx(3, buffer, bufferSize)) {
/*  62 */       LOG.error("Failed to get dns domain name. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
/*  63 */       return "";
/*     */     } 
/*  65 */     return (new String(buffer)).trim();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getDnsServers() {
/*  70 */     IntByReference bufferSize = new IntByReference();
/*  71 */     int ret = IPHlpAPI.INSTANCE.GetNetworkParams(null, bufferSize);
/*  72 */     if (ret != 111) {
/*  73 */       LOG.error("Failed to get network parameters buffer size. Error code: {}", Integer.valueOf(ret));
/*  74 */       return new String[0];
/*     */     } 
/*     */     
/*  77 */     Memory buffer = new Memory(bufferSize.getValue());
/*  78 */     ret = IPHlpAPI.INSTANCE.GetNetworkParams((Pointer)buffer, bufferSize);
/*  79 */     if (ret != 0) {
/*  80 */       LOG.error("Failed to get network parameters. Error code: {}", Integer.valueOf(ret));
/*  81 */       return new String[0];
/*     */     } 
/*  83 */     IPHlpAPI.FIXED_INFO fixedInfo = new IPHlpAPI.FIXED_INFO((Pointer)buffer);
/*     */     
/*  85 */     List<String> list = new ArrayList<>();
/*  86 */     IPHlpAPI.IP_ADDR_STRING dns = fixedInfo.DnsServerList;
/*  87 */     while (dns != null) {
/*     */ 
/*     */       
/*  90 */       String addr = new String(dns.IpAddress.String, StandardCharsets.US_ASCII);
/*  91 */       int nullPos = addr.indexOf(false);
/*  92 */       if (nullPos != -1) {
/*  93 */         addr = addr.substring(0, nullPos);
/*     */       }
/*  95 */       list.add(addr);
/*  96 */       IPHlpAPI.IP_ADDR_STRING.ByReference byReference = dns.Next;
/*     */     } 
/*  98 */     return list.<String>toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getHostName() {
/* 103 */     return Kernel32Util.getComputerName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getIpv4DefaultGateway() {
/* 108 */     return parseIpv4Route();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getIpv6DefaultGateway() {
/* 113 */     return parseIpv6Route();
/*     */   }
/*     */   
/*     */   private static String parseIpv4Route() {
/* 117 */     List<String> lines = ExecutingCommand.runNative("route print -4 0.0.0.0");
/* 118 */     for (String line : lines) {
/* 119 */       String[] fields = ParseUtil.whitespaces.split(line.trim());
/* 120 */       if (fields.length > 2 && "0.0.0.0".equals(fields[0])) {
/* 121 */         return fields[2];
/*     */       }
/*     */     } 
/* 124 */     return "";
/*     */   }
/*     */   
/*     */   private static String parseIpv6Route() {
/* 128 */     List<String> lines = ExecutingCommand.runNative("route print -6 ::/0");
/* 129 */     for (String line : lines) {
/* 130 */       String[] fields = ParseUtil.whitespaces.split(line.trim());
/* 131 */       if (fields.length > 3 && "::/0".equals(fields[2])) {
/* 132 */         return fields[3];
/*     */       }
/*     */     } 
/* 135 */     return "";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\windows\WindowsNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */