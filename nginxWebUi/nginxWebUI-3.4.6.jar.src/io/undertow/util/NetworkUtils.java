/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
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
/*     */ public class NetworkUtils
/*     */ {
/*     */   public static final String IP4_EXACT = "(?:\\d{1,3}\\.){3}\\d{1,3}";
/*     */   public static final String IP6_EXACT = "^(?:([0-9a-fA-F]{1,4}:){7,7}(?:[0-9a-fA-F]){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,7}(?:(:))|(?:([0-9a-fA-F]{1,4}:)){1,6}(?:(:[0-9a-fA-F]){1,4})|(?:([0-9a-fA-F]{1,4}:)){1,5}(?:(:[0-9a-fA-F]{1,4})){1,2}|(?:([0-9a-fA-F]{1,4}:)){1,4}(?:(:[0-9a-fA-F]{1,4})){1,3}|(?:([0-9a-fA-F]{1,4}:)){1,3}(?:(:[0-9a-fA-F]{1,4})){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,2}(?:(:[0-9a-fA-F]{1,4})){1,5}|(?:([0-9a-fA-F]{1,4}:))(?:(:[0-9a-fA-F]{1,4})){1,6}|(?:(:))(?:((:[0-9a-fA-F]{1,4}){1,7}|(?:(:)))))$";
/*     */   
/*     */   public static String formatPossibleIpv6Address(String address) {
/*  58 */     if (address == null) {
/*  59 */       return null;
/*     */     }
/*  61 */     if (!address.contains(":")) {
/*  62 */       return address;
/*     */     }
/*  64 */     if (address.startsWith("[") && address.endsWith("]")) {
/*  65 */       return address;
/*     */     }
/*  67 */     return "[" + address + "]";
/*     */   }
/*     */ 
/*     */   
/*     */   public static InetAddress parseIpv4Address(String addressString) throws IOException {
/*  72 */     String[] parts = addressString.split("\\.");
/*  73 */     if (parts.length != 4) {
/*  74 */       throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
/*     */     }
/*  76 */     byte[] data = new byte[4];
/*  77 */     for (int i = 0; i < 4; i++) {
/*  78 */       String part = parts[i];
/*  79 */       if (part.length() == 0 || (part.charAt(0) == '0' && part.length() > 1))
/*     */       {
/*  81 */         throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
/*     */       }
/*  83 */       data[i] = (byte)Integer.parseInt(part);
/*     */     } 
/*  85 */     return InetAddress.getByAddress(data);
/*     */   }
/*     */ 
/*     */   
/*     */   public static InetAddress parseIpv6Address(String addressString) throws IOException {
/*  90 */     return InetAddress.getByAddress(parseIpv6AddressToBytes(addressString));
/*     */   }
/*     */   
/*     */   public static byte[] parseIpv6AddressToBytes(String addressString) throws IOException {
/*  94 */     boolean startsWithColon = addressString.startsWith(":");
/*  95 */     if (startsWithColon && !addressString.startsWith("::")) {
/*  96 */       throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
/*     */     }
/*  98 */     String[] parts = (startsWithColon ? addressString.substring(1) : addressString).split(":");
/*  99 */     byte[] data = new byte[16];
/* 100 */     int partOffset = 0;
/* 101 */     boolean seenEmpty = false;
/* 102 */     if (parts.length > 8) {
/* 103 */       throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
/*     */     }
/* 105 */     for (int i = 0; i < parts.length; i++) {
/* 106 */       String part = parts[i];
/* 107 */       if (part.length() > 4)
/* 108 */         throw UndertowMessages.MESSAGES.invalidIpAddress(addressString); 
/* 109 */       if (part.isEmpty()) {
/* 110 */         if (seenEmpty) {
/* 111 */           throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
/*     */         }
/* 113 */         seenEmpty = true;
/* 114 */         int off = 8 - parts.length;
/* 115 */         if (off < 0) {
/* 116 */           throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
/*     */         }
/* 118 */         partOffset = off * 2;
/*     */       } else {
/* 120 */         int num = Integer.parseInt(part, 16);
/* 121 */         data[i * 2 + partOffset] = (byte)(num >> 8);
/* 122 */         data[i * 2 + partOffset + 1] = (byte)num;
/*     */       } 
/*     */     } 
/* 125 */     if (parts.length < 8 && !addressString.endsWith("::") && !seenEmpty)
/*     */     {
/* 127 */       throw UndertowMessages.MESSAGES.invalidIpAddress(addressString);
/*     */     }
/* 129 */     return data;
/*     */   }
/*     */   
/*     */   public static String toObfuscatedString(InetAddress address) {
/* 133 */     if (address == null) {
/* 134 */       return null;
/*     */     }
/* 136 */     String s = address.getHostAddress();
/* 137 */     if (address instanceof java.net.Inet4Address)
/*     */     {
/* 139 */       return s.substring(0, s.lastIndexOf(".") + 1);
/*     */     }
/*     */     
/* 142 */     return s.substring(0, s.indexOf(":", s.indexOf(":") + 1) + 1);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\NetworkUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */