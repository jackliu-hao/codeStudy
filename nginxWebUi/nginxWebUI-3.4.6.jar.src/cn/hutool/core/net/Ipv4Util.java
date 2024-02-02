/*     */ package cn.hutool.core.net;
/*     */ 
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.PatternPool;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.regex.Matcher;
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
/*     */ public class Ipv4Util
/*     */ {
/*     */   public static final String LOCAL_IP = "127.0.0.1";
/*     */   public static final String IP_SPLIT_MARK = "-";
/*     */   public static final String IP_MASK_SPLIT_MARK = "/";
/*     */   public static final int IP_MASK_MAX = 32;
/*     */   
/*     */   public static String formatIpBlock(String ip, String mask) {
/*  50 */     return ip + "/" + getMaskBitByMask(mask);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> list(String ipRange, boolean isAll) {
/*  61 */     if (ipRange.contains("-")) {
/*     */       
/*  63 */       String[] range = StrUtil.splitToArray(ipRange, "-");
/*  64 */       return list(range[0], range[1]);
/*  65 */     }  if (ipRange.contains("/")) {
/*     */       
/*  67 */       String[] param = StrUtil.splitToArray(ipRange, "/");
/*  68 */       return list(param[0], Integer.parseInt(param[1]), isAll);
/*     */     } 
/*  70 */     return ListUtil.toList((Object[])new String[] { ipRange });
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
/*     */   
/*     */   public static List<String> list(String ip, int maskBit, boolean isAll) {
/*  83 */     if (maskBit == 32) {
/*  84 */       List<String> list = new ArrayList<>();
/*  85 */       if (isAll) {
/*  86 */         list.add(ip);
/*     */       }
/*  88 */       return list;
/*     */     } 
/*     */     
/*  91 */     String startIp = getBeginIpStr(ip, maskBit);
/*  92 */     String endIp = getEndIpStr(ip, maskBit);
/*  93 */     if (isAll) {
/*  94 */       return list(startIp, endIp);
/*     */     }
/*     */     
/*  97 */     int lastDotIndex = startIp.lastIndexOf('.') + 1;
/*     */     
/*  99 */     startIp = StrUtil.subPre(startIp, lastDotIndex) + (Integer.parseInt(Objects.<String>requireNonNull(StrUtil.subSuf(startIp, lastDotIndex))) + 1);
/* 100 */     lastDotIndex = endIp.lastIndexOf('.') + 1;
/*     */     
/* 102 */     endIp = StrUtil.subPre(endIp, lastDotIndex) + (Integer.parseInt(Objects.<String>requireNonNull(StrUtil.subSuf(endIp, lastDotIndex))) - 1);
/* 103 */     return list(startIp, endIp);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> list(String ipFrom, String ipTo) {
/* 114 */     int[] ipf = (int[])Convert.convert(int[].class, StrUtil.splitToArray(ipFrom, '.'));
/* 115 */     int[] ipt = (int[])Convert.convert(int[].class, StrUtil.splitToArray(ipTo, '.'));
/*     */     
/* 117 */     List<String> ips = new ArrayList<>();
/* 118 */     for (int a = ipf[0]; a <= ipt[0]; ) {
/* 119 */       int b = (a == ipf[0]) ? ipf[1] : 0; for (;; a++) { if (b <= ((a == ipt[0]) ? ipt[1] : 255)) {
/*     */           
/* 121 */           int c = (b == ipf[1]) ? ipf[2] : 0; for (;; b++) { if (c <= ((b == ipt[1]) ? ipt[2] : 255)) {
/*     */               
/* 123 */               int d = (c == ipf[2]) ? ipf[3] : 0; for (;; c++) { if (d <= ((c == ipt[2]) ? ipt[3] : 255))
/*     */                 
/* 125 */                 { ips.add(a + "." + b + "." + c + "." + d); d++; continue; }  }  break;
/*     */             }  }
/*     */            break;
/*     */         }  }
/*     */     
/* 130 */     }  return ips;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String longToIpv4(long longIP) {
/* 140 */     StringBuilder sb = StrUtil.builder();
/*     */     
/* 142 */     sb.append(longIP >> 24L & 0xFFL);
/* 143 */     sb.append('.');
/*     */     
/* 145 */     sb.append(longIP >> 16L & 0xFFL);
/* 146 */     sb.append('.');
/* 147 */     sb.append(longIP >> 8L & 0xFFL);
/* 148 */     sb.append('.');
/* 149 */     sb.append(longIP & 0xFFL);
/* 150 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long ipv4ToLong(String strIP) {
/* 161 */     Matcher matcher = PatternPool.IPV4.matcher(strIP);
/* 162 */     if (matcher.matches()) {
/* 163 */       return matchAddress(matcher);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 168 */     throw new IllegalArgumentException("Invalid IPv4 address!");
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
/*     */   public static String getBeginIpStr(String ip, int maskBit) {
/* 180 */     return longToIpv4(getBeginIpLong(ip, maskBit).longValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Long getBeginIpLong(String ip, int maskBit) {
/* 191 */     return Long.valueOf(ipv4ToLong(ip) & ipv4ToLong(getMaskByMaskBit(maskBit)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getEndIpStr(String ip, int maskBit) {
/* 202 */     return longToIpv4(getEndIpLong(ip, maskBit).longValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getMaskBitByMask(String mask) {
/* 213 */     Integer maskBit = MaskBit.getMaskBit(mask);
/* 214 */     if (maskBit == null) {
/* 215 */       throw new IllegalArgumentException("Invalid netmask " + mask);
/*     */     }
/* 217 */     return maskBit.intValue();
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
/*     */   public static int countByMaskBit(int maskBit, boolean isAll) {
/* 229 */     if (false == isAll && (maskBit <= 0 || maskBit >= 32)) {
/* 230 */       return 0;
/*     */     }
/*     */     
/* 233 */     int count = (int)Math.pow(2.0D, (32 - maskBit));
/* 234 */     return isAll ? count : (count - 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMaskByMaskBit(int maskBit) {
/* 244 */     return MaskBit.get(maskBit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMaskByIpRange(String fromIp, String toIp) {
/* 255 */     long toIpLong = ipv4ToLong(toIp);
/* 256 */     long fromIpLong = ipv4ToLong(fromIp);
/* 257 */     Assert.isTrue((fromIpLong < toIpLong), "to IP must be greater than from IP!", new Object[0]);
/*     */     
/* 259 */     String[] fromIpSplit = StrUtil.splitToArray(fromIp, '.');
/* 260 */     String[] toIpSplit = StrUtil.splitToArray(toIp, '.');
/* 261 */     StringBuilder mask = new StringBuilder();
/* 262 */     for (int i = 0; i < toIpSplit.length; i++) {
/* 263 */       mask.append(255 - Integer.parseInt(toIpSplit[i]) + Integer.parseInt(fromIpSplit[i])).append('.');
/*     */     }
/* 265 */     return mask.substring(0, mask.length() - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int countByIpRange(String fromIp, String toIp) {
/* 276 */     long toIpLong = ipv4ToLong(toIp);
/* 277 */     long fromIpLong = ipv4ToLong(fromIp);
/* 278 */     if (fromIpLong > toIpLong) {
/* 279 */       throw new IllegalArgumentException("to IP must be greater than from IP!");
/*     */     }
/* 281 */     int count = 1;
/* 282 */     int[] fromIpSplit = StrUtil.split(fromIp, '.').stream().mapToInt(Integer::parseInt).toArray();
/* 283 */     int[] toIpSplit = StrUtil.split(toIp, '.').stream().mapToInt(Integer::parseInt).toArray();
/* 284 */     for (int i = fromIpSplit.length - 1; i >= 0; i--) {
/* 285 */       count = (int)(count + (toIpSplit[i] - fromIpSplit[i]) * Math.pow(256.0D, (fromIpSplit.length - i - 1)));
/*     */     }
/* 287 */     return count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isMaskValid(String mask) {
/* 297 */     return (MaskBit.getMaskBit(mask) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isMaskBitValid(int maskBit) {
/* 307 */     return (MaskBit.get(maskBit) != null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isInnerIP(String ipAddress) {
/* 326 */     long ipNum = ipv4ToLong(ipAddress);
/*     */     
/* 328 */     long aBegin = ipv4ToLong("10.0.0.0");
/* 329 */     long aEnd = ipv4ToLong("10.255.255.255");
/*     */     
/* 331 */     long bBegin = ipv4ToLong("172.16.0.0");
/* 332 */     long bEnd = ipv4ToLong("172.31.255.255");
/*     */     
/* 334 */     long cBegin = ipv4ToLong("192.168.0.0");
/* 335 */     long cEnd = ipv4ToLong("192.168.255.255");
/*     */     
/* 337 */     boolean isInnerIp = (isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || "127.0.0.1".equals(ipAddress));
/* 338 */     return isInnerIp;
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
/*     */ 
/*     */   
/*     */   public static Long getEndIpLong(String ip, int maskBit) {
/* 352 */     return Long.valueOf(getBeginIpLong(ip, maskBit).longValue() + (
/* 353 */         ipv4ToLong(getMaskByMaskBit(maskBit)) ^ 0xFFFFFFFFFFFFFFFFL));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static long matchAddress(Matcher matcher) {
/* 363 */     long addr = 0L;
/* 364 */     for (int i = 1; i <= 4; i++) {
/* 365 */       addr |= Long.parseLong(matcher.group(i)) << 8 * (4 - i);
/*     */     }
/* 367 */     return addr;
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
/*     */   private static boolean isInner(long userIp, long begin, long end) {
/* 379 */     return (userIp >= begin && userIp <= end);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\Ipv4Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */