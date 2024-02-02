package cn.hutool.core.net;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

public class Ipv4Util {
   public static final String LOCAL_IP = "127.0.0.1";
   public static final String IP_SPLIT_MARK = "-";
   public static final String IP_MASK_SPLIT_MARK = "/";
   public static final int IP_MASK_MAX = 32;

   public static String formatIpBlock(String ip, String mask) {
      return ip + "/" + getMaskBitByMask(mask);
   }

   public static List<String> list(String ipRange, boolean isAll) {
      String[] param;
      if (ipRange.contains("-")) {
         param = StrUtil.splitToArray(ipRange, "-");
         return list(param[0], param[1]);
      } else if (ipRange.contains("/")) {
         param = StrUtil.splitToArray(ipRange, "/");
         return list(param[0], Integer.parseInt(param[1]), isAll);
      } else {
         return ListUtil.toList((Object[])(ipRange));
      }
   }

   public static List<String> list(String ip, int maskBit, boolean isAll) {
      if (maskBit == 32) {
         List<String> list = new ArrayList();
         if (isAll) {
            list.add(ip);
         }

         return list;
      } else {
         String startIp = getBeginIpStr(ip, maskBit);
         String endIp = getEndIpStr(ip, maskBit);
         if (isAll) {
            return list(startIp, endIp);
         } else {
            int lastDotIndex = startIp.lastIndexOf(46) + 1;
            startIp = StrUtil.subPre(startIp, lastDotIndex) + (Integer.parseInt((String)Objects.requireNonNull(StrUtil.subSuf(startIp, lastDotIndex))) + 1);
            lastDotIndex = endIp.lastIndexOf(46) + 1;
            endIp = StrUtil.subPre(endIp, lastDotIndex) + (Integer.parseInt((String)Objects.requireNonNull(StrUtil.subSuf(endIp, lastDotIndex))) - 1);
            return list(startIp, endIp);
         }
      }
   }

   public static List<String> list(String ipFrom, String ipTo) {
      int[] ipf = (int[])Convert.convert((Class)int[].class, StrUtil.splitToArray(ipFrom, '.'));
      int[] ipt = (int[])Convert.convert((Class)int[].class, StrUtil.splitToArray(ipTo, '.'));
      List<String> ips = new ArrayList();

      for(int a = ipf[0]; a <= ipt[0]; ++a) {
         for(int b = a == ipf[0] ? ipf[1] : 0; b <= (a == ipt[0] ? ipt[1] : 255); ++b) {
            for(int c = b == ipf[1] ? ipf[2] : 0; c <= (b == ipt[1] ? ipt[2] : 255); ++c) {
               for(int d = c == ipf[2] ? ipf[3] : 0; d <= (c == ipt[2] ? ipt[3] : 255); ++d) {
                  ips.add(a + "." + b + "." + c + "." + d);
               }
            }
         }
      }

      return ips;
   }

   public static String longToIpv4(long longIP) {
      StringBuilder sb = StrUtil.builder();
      sb.append(longIP >> 24 & 255L);
      sb.append('.');
      sb.append(longIP >> 16 & 255L);
      sb.append('.');
      sb.append(longIP >> 8 & 255L);
      sb.append('.');
      sb.append(longIP & 255L);
      return sb.toString();
   }

   public static long ipv4ToLong(String strIP) {
      Matcher matcher = PatternPool.IPV4.matcher(strIP);
      if (matcher.matches()) {
         return matchAddress(matcher);
      } else {
         throw new IllegalArgumentException("Invalid IPv4 address!");
      }
   }

   public static String getBeginIpStr(String ip, int maskBit) {
      return longToIpv4(getBeginIpLong(ip, maskBit));
   }

   public static Long getBeginIpLong(String ip, int maskBit) {
      return ipv4ToLong(ip) & ipv4ToLong(getMaskByMaskBit(maskBit));
   }

   public static String getEndIpStr(String ip, int maskBit) {
      return longToIpv4(getEndIpLong(ip, maskBit));
   }

   public static int getMaskBitByMask(String mask) {
      Integer maskBit = MaskBit.getMaskBit(mask);
      if (maskBit == null) {
         throw new IllegalArgumentException("Invalid netmask " + mask);
      } else {
         return maskBit;
      }
   }

   public static int countByMaskBit(int maskBit, boolean isAll) {
      if (isAll || maskBit > 0 && maskBit < 32) {
         int count = (int)Math.pow(2.0, (double)(32 - maskBit));
         return isAll ? count : count - 2;
      } else {
         return 0;
      }
   }

   public static String getMaskByMaskBit(int maskBit) {
      return MaskBit.get(maskBit);
   }

   public static String getMaskByIpRange(String fromIp, String toIp) {
      long toIpLong = ipv4ToLong(toIp);
      long fromIpLong = ipv4ToLong(fromIp);
      Assert.isTrue(fromIpLong < toIpLong, "to IP must be greater than from IP!");
      String[] fromIpSplit = StrUtil.splitToArray(fromIp, '.');
      String[] toIpSplit = StrUtil.splitToArray(toIp, '.');
      StringBuilder mask = new StringBuilder();

      for(int i = 0; i < toIpSplit.length; ++i) {
         mask.append(255 - Integer.parseInt(toIpSplit[i]) + Integer.parseInt(fromIpSplit[i])).append('.');
      }

      return mask.substring(0, mask.length() - 1);
   }

   public static int countByIpRange(String fromIp, String toIp) {
      long toIpLong = ipv4ToLong(toIp);
      long fromIpLong = ipv4ToLong(fromIp);
      if (fromIpLong > toIpLong) {
         throw new IllegalArgumentException("to IP must be greater than from IP!");
      } else {
         int count = 1;
         int[] fromIpSplit = StrUtil.split(fromIp, '.').stream().mapToInt(Integer::parseInt).toArray();
         int[] toIpSplit = StrUtil.split(toIp, '.').stream().mapToInt(Integer::parseInt).toArray();

         for(int i = fromIpSplit.length - 1; i >= 0; --i) {
            count = (int)((double)count + (double)(toIpSplit[i] - fromIpSplit[i]) * Math.pow(256.0, (double)(fromIpSplit.length - i - 1)));
         }

         return count;
      }
   }

   public static boolean isMaskValid(String mask) {
      return MaskBit.getMaskBit(mask) != null;
   }

   public static boolean isMaskBitValid(int maskBit) {
      return MaskBit.get(maskBit) != null;
   }

   public static boolean isInnerIP(String ipAddress) {
      long ipNum = ipv4ToLong(ipAddress);
      long aBegin = ipv4ToLong("10.0.0.0");
      long aEnd = ipv4ToLong("10.255.255.255");
      long bBegin = ipv4ToLong("172.16.0.0");
      long bEnd = ipv4ToLong("172.31.255.255");
      long cBegin = ipv4ToLong("192.168.0.0");
      long cEnd = ipv4ToLong("192.168.255.255");
      boolean isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || "127.0.0.1".equals(ipAddress);
      return isInnerIp;
   }

   public static Long getEndIpLong(String ip, int maskBit) {
      return getBeginIpLong(ip, maskBit) + ~ipv4ToLong(getMaskByMaskBit(maskBit));
   }

   private static long matchAddress(Matcher matcher) {
      long addr = 0L;

      for(int i = 1; i <= 4; ++i) {
         addr |= Long.parseLong(matcher.group(i)) << 8 * (4 - i);
      }

      return addr;
   }

   private static boolean isInner(long userIp, long begin, long end) {
      return userIp >= begin && userIp <= end;
   }
}
