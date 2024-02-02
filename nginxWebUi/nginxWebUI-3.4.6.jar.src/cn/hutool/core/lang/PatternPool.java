/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.map.WeakConcurrentMap;
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
/*     */ public class PatternPool
/*     */ {
/*  18 */   public static final Pattern GENERAL = Pattern.compile("^\\w+$");
/*     */ 
/*     */ 
/*     */   
/*  22 */   public static final Pattern NUMBERS = Pattern.compile("\\d+");
/*     */ 
/*     */ 
/*     */   
/*  26 */   public static final Pattern WORD = Pattern.compile("[a-zA-Z]+");
/*     */ 
/*     */ 
/*     */   
/*  30 */   public static final Pattern CHINESE = Pattern.compile("[⺀-⻿⼀-⿟㇀-㇯㐀-䶿一-鿿豈-﫿𠀀-𪛟𪜀-𫜿𫝀-𫠟𫠠-𬺯丽-𯨟]");
/*     */ 
/*     */ 
/*     */   
/*  34 */   public static final Pattern CHINESES = Pattern.compile("[⺀-⻿⼀-⿟㇀-㇯㐀-䶿一-鿿豈-﫿𠀀-𪛟𪜀-𫜿𫝀-𫠟𫠠-𬺯丽-𯨟]+");
/*     */ 
/*     */ 
/*     */   
/*  38 */   public static final Pattern GROUP_VAR = Pattern.compile("\\$(\\d+)");
/*     */ 
/*     */ 
/*     */   
/*  42 */   public static final Pattern IPV4 = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)$");
/*     */ 
/*     */ 
/*     */   
/*  46 */   public static final Pattern IPV6 = Pattern.compile("(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))");
/*     */ 
/*     */ 
/*     */   
/*  50 */   public static final Pattern MONEY = Pattern.compile("^(\\d+(?:\\.\\d+)?)$");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   public static final Pattern EMAIL = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])", 2);
/*     */ 
/*     */ 
/*     */   
/*  60 */   public static final Pattern MOBILE = Pattern.compile("(?:0|86|\\+86)?1[3-9]\\d{9}");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   public static final Pattern MOBILE_HK = Pattern.compile("(?:0|852|\\+852)?\\d{8}");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   public static final Pattern MOBILE_TW = Pattern.compile("(?:0|886|\\+886)?(?:|-)09\\d{8}");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   public static final Pattern MOBILE_MO = Pattern.compile("(?:0|853|\\+853)?(?:|-)6\\d{7}");
/*     */ 
/*     */ 
/*     */   
/*  86 */   public static final Pattern TEL = Pattern.compile("(010|02\\d|0[3-9]\\d{2})-?(\\d{6,8})");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   public static final Pattern TEL_400_800 = Pattern.compile("0\\d{2,3}[\\- ]?[1-9]\\d{6,7}|[48]00[\\- ]?[1-9]\\d{6}");
/*     */ 
/*     */ 
/*     */   
/*  96 */   public static final Pattern CITIZEN_ID = Pattern.compile("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([012]\\d)|3[0-1])\\d{3}(\\d|X|x)");
/*     */ 
/*     */ 
/*     */   
/* 100 */   public static final Pattern ZIP_CODE = Pattern.compile("^(0[1-7]|1[0-356]|2[0-7]|3[0-6]|4[0-7]|5[0-7]|6[0-7]|7[0-5]|8[0-9]|9[0-8])\\d{4}|99907[78]$");
/*     */ 
/*     */ 
/*     */   
/* 104 */   public static final Pattern BIRTHDAY = Pattern.compile("^(\\d{2,4})([/\\-.年]?)(\\d{1,2})([/\\-.月]?)(\\d{1,2})日?$");
/*     */ 
/*     */ 
/*     */   
/* 108 */   public static final Pattern URL = Pattern.compile("[a-zA-Z]+://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]");
/*     */ 
/*     */ 
/*     */   
/* 112 */   public static final Pattern URL_HTTP = Pattern.compile("(https?|ftp|file)://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]", 2);
/*     */ 
/*     */ 
/*     */   
/* 116 */   public static final Pattern GENERAL_WITH_CHINESE = Pattern.compile("^[一-鿿\\w]+$");
/*     */ 
/*     */ 
/*     */   
/* 120 */   public static final Pattern UUID = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 2);
/*     */ 
/*     */ 
/*     */   
/* 124 */   public static final Pattern UUID_SIMPLE = Pattern.compile("^[0-9a-fA-F]{32}$");
/*     */ 
/*     */ 
/*     */   
/* 128 */   public static final Pattern MAC_ADDRESS = Pattern.compile("((?:[a-fA-F0-9]{1,2}[:-]){5}[a-fA-F0-9]{1,2})|0x(\\d{12}).+ETHER", 2);
/*     */ 
/*     */ 
/*     */   
/* 132 */   public static final Pattern HEX = Pattern.compile("^[a-fA-F0-9]+$");
/*     */ 
/*     */ 
/*     */   
/* 136 */   public static final Pattern TIME = Pattern.compile("\\d{1,2}:\\d{1,2}(:\\d{1,2})?");
/*     */ 
/*     */ 
/*     */   
/* 140 */   public static final Pattern PLATE_NUMBER = Pattern.compile("^(([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[ABCDEFGHJK])|([ABCDEFGHJK]([A-HJ-NP-Z0-9])[0-9]{4})))|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]\\d{3}\\d{1,3}[领])|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳使领]))$");
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
/* 152 */   public static final Pattern CREDIT_CODE = Pattern.compile("^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 161 */   public static final Pattern CAR_VIN = Pattern.compile("^[A-HJ-NPR-Z0-9]{8}[0-9X][A-HJ-NPR-Z0-9]{2}\\d{6}$");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 168 */   public static final Pattern CAR_DRIVING_LICENCE = Pattern.compile("^[0-9]{12}$");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 173 */   public static final Pattern CHINESE_NAME = Pattern.compile("^[⺀-鿿·]{2,60}$");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 179 */   private static final WeakConcurrentMap<RegexWithFlag, Pattern> POOL = new WeakConcurrentMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Pattern get(String regex) {
/* 188 */     return get(regex, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Pattern get(String regex, int flags) {
/* 199 */     RegexWithFlag regexWithFlag = new RegexWithFlag(regex, flags);
/* 200 */     return (Pattern)POOL.computeIfAbsent(regexWithFlag, key -> Pattern.compile(regex, flags));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Pattern remove(String regex, int flags) {
/* 211 */     return (Pattern)POOL.remove(new RegexWithFlag(regex, flags));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void clear() {
/* 218 */     POOL.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class RegexWithFlag
/*     */   {
/*     */     private final String regex;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final int flag;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public RegexWithFlag(String regex, int flag) {
/* 239 */       this.regex = regex;
/* 240 */       this.flag = flag;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 245 */       int prime = 31;
/* 246 */       int result = 1;
/* 247 */       result = 31 * result + this.flag;
/* 248 */       result = 31 * result + ((this.regex == null) ? 0 : this.regex.hashCode());
/* 249 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 254 */       if (this == obj) {
/* 255 */         return true;
/*     */       }
/* 257 */       if (obj == null) {
/* 258 */         return false;
/*     */       }
/* 260 */       if (getClass() != obj.getClass()) {
/* 261 */         return false;
/*     */       }
/* 263 */       RegexWithFlag other = (RegexWithFlag)obj;
/* 264 */       if (this.flag != other.flag) {
/* 265 */         return false;
/*     */       }
/* 267 */       if (this.regex == null) {
/* 268 */         return (other.regex == null);
/*     */       }
/* 270 */       return this.regex.equals(other.regex);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\PatternPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */