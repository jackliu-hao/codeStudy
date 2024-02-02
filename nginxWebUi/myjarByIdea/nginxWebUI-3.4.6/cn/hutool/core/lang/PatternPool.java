package cn.hutool.core.lang;

import cn.hutool.core.map.WeakConcurrentMap;
import java.util.regex.Pattern;

public class PatternPool {
   public static final Pattern GENERAL = Pattern.compile("^\\w+$");
   public static final Pattern NUMBERS = Pattern.compile("\\d+");
   public static final Pattern WORD = Pattern.compile("[a-zA-Z]+");
   public static final Pattern CHINESE = Pattern.compile("[⺀-\u2eff⼀-\u2fdf㇀-\u31ef㐀-䶿一-\u9fff豈-\ufaff\ud840\udc00-\ud869\udedf\ud869\udf00-\ud86d\udf3f\ud86d\udf40-\ud86e\udc1f\ud86e\udc20-\ud873\udeaf\ud87e\udc00-\ud87e\ude1f]");
   public static final Pattern CHINESES = Pattern.compile("[⺀-\u2eff⼀-\u2fdf㇀-\u31ef㐀-䶿一-\u9fff豈-\ufaff\ud840\udc00-\ud869\udedf\ud869\udf00-\ud86d\udf3f\ud86d\udf40-\ud86e\udc1f\ud86e\udc20-\ud873\udeaf\ud87e\udc00-\ud87e\ude1f]+");
   public static final Pattern GROUP_VAR = Pattern.compile("\\$(\\d+)");
   public static final Pattern IPV4 = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)$");
   public static final Pattern IPV6 = Pattern.compile("(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))");
   public static final Pattern MONEY = Pattern.compile("^(\\d+(?:\\.\\d+)?)$");
   public static final Pattern EMAIL = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])", 2);
   public static final Pattern MOBILE = Pattern.compile("(?:0|86|\\+86)?1[3-9]\\d{9}");
   public static final Pattern MOBILE_HK = Pattern.compile("(?:0|852|\\+852)?\\d{8}");
   public static final Pattern MOBILE_TW = Pattern.compile("(?:0|886|\\+886)?(?:|-)09\\d{8}");
   public static final Pattern MOBILE_MO = Pattern.compile("(?:0|853|\\+853)?(?:|-)6\\d{7}");
   public static final Pattern TEL = Pattern.compile("(010|02\\d|0[3-9]\\d{2})-?(\\d{6,8})");
   public static final Pattern TEL_400_800 = Pattern.compile("0\\d{2,3}[\\- ]?[1-9]\\d{6,7}|[48]00[\\- ]?[1-9]\\d{6}");
   public static final Pattern CITIZEN_ID = Pattern.compile("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([012]\\d)|3[0-1])\\d{3}(\\d|X|x)");
   public static final Pattern ZIP_CODE = Pattern.compile("^(0[1-7]|1[0-356]|2[0-7]|3[0-6]|4[0-7]|5[0-7]|6[0-7]|7[0-5]|8[0-9]|9[0-8])\\d{4}|99907[78]$");
   public static final Pattern BIRTHDAY = Pattern.compile("^(\\d{2,4})([/\\-.年]?)(\\d{1,2})([/\\-.月]?)(\\d{1,2})日?$");
   public static final Pattern URL = Pattern.compile("[a-zA-Z]+://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]");
   public static final Pattern URL_HTTP = Pattern.compile("(https?|ftp|file)://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]", 2);
   public static final Pattern GENERAL_WITH_CHINESE = Pattern.compile("^[一-\u9fff\\w]+$");
   public static final Pattern UUID = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 2);
   public static final Pattern UUID_SIMPLE = Pattern.compile("^[0-9a-fA-F]{32}$");
   public static final Pattern MAC_ADDRESS = Pattern.compile("((?:[a-fA-F0-9]{1,2}[:-]){5}[a-fA-F0-9]{1,2})|0x(\\d{12}).+ETHER", 2);
   public static final Pattern HEX = Pattern.compile("^[a-fA-F0-9]+$");
   public static final Pattern TIME = Pattern.compile("\\d{1,2}:\\d{1,2}(:\\d{1,2})?");
   public static final Pattern PLATE_NUMBER = Pattern.compile("^(([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[ABCDEFGHJK])|([ABCDEFGHJK]([A-HJ-NP-Z0-9])[0-9]{4})))|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]\\d{3}\\d{1,3}[领])|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳使领]))$");
   public static final Pattern CREDIT_CODE = Pattern.compile("^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$");
   public static final Pattern CAR_VIN = Pattern.compile("^[A-HJ-NPR-Z0-9]{8}[0-9X][A-HJ-NPR-Z0-9]{2}\\d{6}$");
   public static final Pattern CAR_DRIVING_LICENCE = Pattern.compile("^[0-9]{12}$");
   public static final Pattern CHINESE_NAME = Pattern.compile("^[⺀-\u9fff·]{2,60}$");
   private static final WeakConcurrentMap<RegexWithFlag, Pattern> POOL = new WeakConcurrentMap();

   public static Pattern get(String regex) {
      return get(regex, 0);
   }

   public static Pattern get(String regex, int flags) {
      RegexWithFlag regexWithFlag = new RegexWithFlag(regex, flags);
      return (Pattern)POOL.computeIfAbsent(regexWithFlag, (key) -> {
         return Pattern.compile(regex, flags);
      });
   }

   public static Pattern remove(String regex, int flags) {
      return (Pattern)POOL.remove(new RegexWithFlag(regex, flags));
   }

   public static void clear() {
      POOL.clear();
   }

   private static class RegexWithFlag {
      private final String regex;
      private final int flag;

      public RegexWithFlag(String regex, int flag) {
         this.regex = regex;
         this.flag = flag;
      }

      public int hashCode() {
         int prime = true;
         int result = 1;
         result = 31 * result + this.flag;
         result = 31 * result + (this.regex == null ? 0 : this.regex.hashCode());
         return result;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            RegexWithFlag other = (RegexWithFlag)obj;
            if (this.flag != other.flag) {
               return false;
            } else if (this.regex == null) {
               return other.regex == null;
            } else {
               return this.regex.equals(other.regex);
            }
         }
      }
   }
}
