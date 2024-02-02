package cn.hutool.core.lang;

public interface RegexPool {
  public static final String GENERAL = "^\\w+$";
  
  public static final String NUMBERS = "\\d+";
  
  public static final String WORD = "[a-zA-Z]+";
  
  public static final String CHINESE = "[⺀-⻿⼀-⿟㇀-㇯㐀-䶿一-鿿豈-﫿𠀀-𪛟𪜀-𫜿𫝀-𫠟𫠠-𬺯丽-𯨟]";
  
  public static final String CHINESES = "[⺀-⻿⼀-⿟㇀-㇯㐀-䶿一-鿿豈-﫿𠀀-𪛟𪜀-𫜿𫝀-𫠟𫠠-𬺯丽-𯨟]+";
  
  public static final String GROUP_VAR = "\\$(\\d+)";
  
  public static final String IPV4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)$";
  
  public static final String IPV6 = "(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))";
  
  public static final String MONEY = "^(\\d+(?:\\.\\d+)?)$";
  
  public static final String EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
  
  public static final String MOBILE = "(?:0|86|\\+86)?1[3-9]\\d{9}";
  
  public static final String MOBILE_HK = "(?:0|852|\\+852)?\\d{8}";
  
  public static final String MOBILE_TW = "(?:0|886|\\+886)?(?:|-)09\\d{8}";
  
  public static final String MOBILE_MO = "(?:0|853|\\+853)?(?:|-)6\\d{7}";
  
  public static final String TEL = "(010|02\\d|0[3-9]\\d{2})-?(\\d{6,8})";
  
  public static final String TEL_400_800 = "0\\d{2,3}[\\- ]?[1-9]\\d{6,7}|[48]00[\\- ]?[1-9]\\d{6}";
  
  public static final String CITIZEN_ID = "[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([012]\\d)|3[0-1])\\d{3}(\\d|X|x)";
  
  public static final String ZIP_CODE = "^(0[1-7]|1[0-356]|2[0-7]|3[0-6]|4[0-7]|5[0-7]|6[0-7]|7[0-5]|8[0-9]|9[0-8])\\d{4}|99907[78]$";
  
  public static final String BIRTHDAY = "^(\\d{2,4})([/\\-.年]?)(\\d{1,2})([/\\-.月]?)(\\d{1,2})日?$";
  
  public static final String URI = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";
  
  public static final String URL = "[a-zA-Z]+://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]";
  
  public static final String URL_HTTP = "(https?|ftp|file)://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]";
  
  public static final String GENERAL_WITH_CHINESE = "^[一-鿿\\w]+$";
  
  public static final String UUID = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
  
  public static final String UUID_SIMPLE = "^[0-9a-fA-F]{32}$";
  
  public static final String MAC_ADDRESS = "((?:[a-fA-F0-9]{1,2}[:-]){5}[a-fA-F0-9]{1,2})|0x(\\d{12}).+ETHER";
  
  public static final String HEX = "^[a-fA-F0-9]+$";
  
  public static final String TIME = "\\d{1,2}:\\d{1,2}(:\\d{1,2})?";
  
  public static final String PLATE_NUMBER = "^(([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[ABCDEFGHJK])|([ABCDEFGHJK]([A-HJ-NP-Z0-9])[0-9]{4})))|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]\\d{3}\\d{1,3}[领])|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳使领]))$";
  
  public static final String CREDIT_CODE = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$";
  
  public static final String CAR_VIN = "^[A-HJ-NPR-Z0-9]{8}[0-9X][A-HJ-NPR-Z0-9]{2}\\d{6}$";
  
  public static final String CAR_DRIVING_LICENCE = "^[0-9]{12}$";
  
  public static final String CHINESE_NAME = "^[⺀-鿿·]{2,60}$";
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\RegexPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */