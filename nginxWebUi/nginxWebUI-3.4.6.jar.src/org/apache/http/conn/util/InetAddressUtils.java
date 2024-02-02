/*     */ package org.apache.http.conn.util;
/*     */ 
/*     */ import java.util.regex.Pattern;
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
/*     */ public class InetAddressUtils
/*     */ {
/*     */   private static final String IPV4_BASIC_PATTERN_STRING = "(([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){1}(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){2}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
/*  47 */   private static final Pattern IPV4_PATTERN = Pattern.compile("^(([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){1}(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){2}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
/*     */ 
/*     */   
/*  50 */   private static final Pattern IPV4_MAPPED_IPV6_PATTERN = Pattern.compile("^::[fF]{4}:(([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){1}(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){2}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
/*     */ 
/*     */   
/*  53 */   private static final Pattern IPV6_STD_PATTERN = Pattern.compile("^[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7}$");
/*     */ 
/*     */ 
/*     */   
/*  57 */   private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern.compile("^(([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,5})?)::(([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,5})?)$");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final char COLON_CHAR = ':';
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MAX_COLON_COUNT = 7;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isIPv4Address(String input) {
/*  78 */     return IPV4_PATTERN.matcher(input).matches();
/*     */   }
/*     */   
/*     */   public static boolean isIPv4MappedIPv64Address(String input) {
/*  82 */     return IPV4_MAPPED_IPV6_PATTERN.matcher(input).matches();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isIPv6StdAddress(String input) {
/*  92 */     return IPV6_STD_PATTERN.matcher(input).matches();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isIPv6HexCompressedAddress(String input) {
/* 102 */     int colonCount = 0;
/* 103 */     for (int i = 0; i < input.length(); i++) {
/* 104 */       if (input.charAt(i) == ':') {
/* 105 */         colonCount++;
/*     */       }
/*     */     } 
/* 108 */     return (colonCount <= 7 && IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isIPv6Address(String input) {
/* 118 */     return (isIPv6StdAddress(input) || isIPv6HexCompressedAddress(input));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\con\\util\InetAddressUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */