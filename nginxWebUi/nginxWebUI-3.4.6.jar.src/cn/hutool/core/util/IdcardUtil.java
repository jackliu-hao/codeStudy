/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.date.DatePattern;
/*     */ import cn.hutool.core.date.DateTime;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.date.format.DateParser;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.PatternPool;
/*     */ import cn.hutool.core.lang.Validator;
/*     */ import java.io.Serializable;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class IdcardUtil
/*     */ {
/*     */   private static final int CHINA_ID_MIN_LENGTH = 15;
/*     */   private static final int CHINA_ID_MAX_LENGTH = 18;
/*  41 */   private static final int[] POWER = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
/*     */ 
/*     */ 
/*     */   
/*  45 */   private static final Map<String, String> CITY_CODES = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*  49 */   private static final Map<Character, Integer> TW_FIRST_CODE = new HashMap<>();
/*     */   
/*     */   static {
/*  52 */     CITY_CODES.put("11", "北京");
/*  53 */     CITY_CODES.put("12", "天津");
/*  54 */     CITY_CODES.put("13", "河北");
/*  55 */     CITY_CODES.put("14", "山西");
/*  56 */     CITY_CODES.put("15", "内蒙古");
/*  57 */     CITY_CODES.put("21", "辽宁");
/*  58 */     CITY_CODES.put("22", "吉林");
/*  59 */     CITY_CODES.put("23", "黑龙江");
/*  60 */     CITY_CODES.put("31", "上海");
/*  61 */     CITY_CODES.put("32", "江苏");
/*  62 */     CITY_CODES.put("33", "浙江");
/*  63 */     CITY_CODES.put("34", "安徽");
/*  64 */     CITY_CODES.put("35", "福建");
/*  65 */     CITY_CODES.put("36", "江西");
/*  66 */     CITY_CODES.put("37", "山东");
/*  67 */     CITY_CODES.put("41", "河南");
/*  68 */     CITY_CODES.put("42", "湖北");
/*  69 */     CITY_CODES.put("43", "湖南");
/*  70 */     CITY_CODES.put("44", "广东");
/*  71 */     CITY_CODES.put("45", "广西");
/*  72 */     CITY_CODES.put("46", "海南");
/*  73 */     CITY_CODES.put("50", "重庆");
/*  74 */     CITY_CODES.put("51", "四川");
/*  75 */     CITY_CODES.put("52", "贵州");
/*  76 */     CITY_CODES.put("53", "云南");
/*  77 */     CITY_CODES.put("54", "西藏");
/*  78 */     CITY_CODES.put("61", "陕西");
/*  79 */     CITY_CODES.put("62", "甘肃");
/*  80 */     CITY_CODES.put("63", "青海");
/*  81 */     CITY_CODES.put("64", "宁夏");
/*  82 */     CITY_CODES.put("65", "新疆");
/*  83 */     CITY_CODES.put("71", "台湾");
/*  84 */     CITY_CODES.put("81", "香港");
/*  85 */     CITY_CODES.put("82", "澳门");
/*     */     
/*  87 */     CITY_CODES.put("83", "台湾");
/*  88 */     CITY_CODES.put("91", "国外");
/*     */     
/*  90 */     TW_FIRST_CODE.put(Character.valueOf('A'), Integer.valueOf(10));
/*  91 */     TW_FIRST_CODE.put(Character.valueOf('B'), Integer.valueOf(11));
/*  92 */     TW_FIRST_CODE.put(Character.valueOf('C'), Integer.valueOf(12));
/*  93 */     TW_FIRST_CODE.put(Character.valueOf('D'), Integer.valueOf(13));
/*  94 */     TW_FIRST_CODE.put(Character.valueOf('E'), Integer.valueOf(14));
/*  95 */     TW_FIRST_CODE.put(Character.valueOf('F'), Integer.valueOf(15));
/*  96 */     TW_FIRST_CODE.put(Character.valueOf('G'), Integer.valueOf(16));
/*  97 */     TW_FIRST_CODE.put(Character.valueOf('H'), Integer.valueOf(17));
/*  98 */     TW_FIRST_CODE.put(Character.valueOf('J'), Integer.valueOf(18));
/*  99 */     TW_FIRST_CODE.put(Character.valueOf('K'), Integer.valueOf(19));
/* 100 */     TW_FIRST_CODE.put(Character.valueOf('L'), Integer.valueOf(20));
/* 101 */     TW_FIRST_CODE.put(Character.valueOf('M'), Integer.valueOf(21));
/* 102 */     TW_FIRST_CODE.put(Character.valueOf('N'), Integer.valueOf(22));
/* 103 */     TW_FIRST_CODE.put(Character.valueOf('P'), Integer.valueOf(23));
/* 104 */     TW_FIRST_CODE.put(Character.valueOf('Q'), Integer.valueOf(24));
/* 105 */     TW_FIRST_CODE.put(Character.valueOf('R'), Integer.valueOf(25));
/* 106 */     TW_FIRST_CODE.put(Character.valueOf('S'), Integer.valueOf(26));
/* 107 */     TW_FIRST_CODE.put(Character.valueOf('T'), Integer.valueOf(27));
/* 108 */     TW_FIRST_CODE.put(Character.valueOf('U'), Integer.valueOf(28));
/* 109 */     TW_FIRST_CODE.put(Character.valueOf('V'), Integer.valueOf(29));
/* 110 */     TW_FIRST_CODE.put(Character.valueOf('X'), Integer.valueOf(30));
/* 111 */     TW_FIRST_CODE.put(Character.valueOf('Y'), Integer.valueOf(31));
/* 112 */     TW_FIRST_CODE.put(Character.valueOf('W'), Integer.valueOf(32));
/* 113 */     TW_FIRST_CODE.put(Character.valueOf('Z'), Integer.valueOf(33));
/* 114 */     TW_FIRST_CODE.put(Character.valueOf('I'), Integer.valueOf(34));
/* 115 */     TW_FIRST_CODE.put(Character.valueOf('O'), Integer.valueOf(35));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String convert15To18(String idCard) {
/*     */     StringBuilder idCard18;
/* 126 */     if (idCard.length() != 15) {
/* 127 */       return null;
/*     */     }
/* 129 */     if (ReUtil.isMatch(PatternPool.NUMBERS, idCard)) {
/*     */       
/* 131 */       String birthday = idCard.substring(6, 12);
/* 132 */       DateTime dateTime = DateUtil.parse(birthday, "yyMMdd");
/*     */       
/* 134 */       int sYear = DateUtil.year((Date)dateTime);
/* 135 */       if (sYear > 2000)
/*     */       {
/* 137 */         sYear -= 100;
/*     */       }
/* 139 */       idCard18 = StrUtil.builder().append(idCard, 0, 6).append(sYear).append(idCard.substring(8));
/*     */       
/* 141 */       char sVal = getCheckCode18(idCard18.toString());
/* 142 */       idCard18.append(sVal);
/*     */     } else {
/* 144 */       return null;
/*     */     } 
/* 146 */     return idCard18.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isValidCard(String idCard) {
/*     */     String[] cardVal;
/* 157 */     if (StrUtil.isBlank(idCard)) {
/* 158 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 162 */     int length = idCard.length();
/* 163 */     switch (length) {
/*     */       case 18:
/* 165 */         return isValidCard18(idCard);
/*     */       case 15:
/* 167 */         return isValidCard15(idCard);
/*     */       case 10:
/* 169 */         cardVal = isValidCard10(idCard);
/* 170 */         return (null != cardVal && "true".equals(cardVal[2]));
/*     */     } 
/*     */     
/* 173 */     return false;
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
/*     */   public static boolean isValidCard18(String idcard) {
/* 224 */     return isValidCard18(idcard, true);
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
/*     */   public static boolean isValidCard18(String idcard, boolean ignoreCase) {
/* 261 */     if (18 != idcard.length()) {
/* 262 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 266 */     String proCode = idcard.substring(0, 2);
/* 267 */     if (null == CITY_CODES.get(proCode)) {
/* 268 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 272 */     if (false == Validator.isBirthday(idcard.substring(6, 14))) {
/* 273 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 277 */     String code17 = idcard.substring(0, 17);
/* 278 */     if (ReUtil.isMatch(PatternPool.NUMBERS, code17)) {
/*     */       
/* 280 */       char val = getCheckCode18(code17);
/*     */       
/* 282 */       return CharUtil.equals(val, idcard.charAt(17), ignoreCase);
/*     */     } 
/* 284 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isValidCard15(String idcard) {
/* 294 */     if (15 != idcard.length()) {
/* 295 */       return false;
/*     */     }
/* 297 */     if (ReUtil.isMatch(PatternPool.NUMBERS, idcard)) {
/*     */       
/* 299 */       String proCode = idcard.substring(0, 2);
/* 300 */       if (null == CITY_CODES.get(proCode)) {
/* 301 */         return false;
/*     */       }
/*     */ 
/*     */       
/* 305 */       return (false != Validator.isBirthday("19" + idcard.substring(6, 12)));
/*     */     } 
/* 307 */     return false;
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
/*     */   public static String[] isValidCard10(String idcard) {
/* 321 */     if (StrUtil.isBlank(idcard)) {
/* 322 */       return null;
/*     */     }
/* 324 */     String[] info = new String[3];
/* 325 */     String card = idcard.replaceAll("[()]", "");
/* 326 */     if (card.length() != 8 && card.length() != 9 && idcard.length() != 10) {
/* 327 */       return null;
/*     */     }
/* 329 */     if (idcard.matches("^[a-zA-Z][0-9]{9}$")) {
/* 330 */       info[0] = "台湾";
/* 331 */       char char2 = idcard.charAt(1);
/* 332 */       if ('1' == char2) {
/* 333 */         info[1] = "M";
/* 334 */       } else if ('2' == char2) {
/* 335 */         info[1] = "F";
/*     */       } else {
/* 337 */         info[1] = "N";
/* 338 */         info[2] = "false";
/* 339 */         return info;
/*     */       } 
/* 341 */       info[2] = isValidTWCard(idcard) ? "true" : "false";
/* 342 */     } else if (idcard.matches("^[157][0-9]{6}\\(?[0-9A-Z]\\)?$")) {
/* 343 */       info[0] = "澳门";
/* 344 */       info[1] = "N";
/* 345 */       info[2] = "true";
/* 346 */     } else if (idcard.matches("^[A-Z]{1,2}[0-9]{6}\\(?[0-9A]\\)?$")) {
/* 347 */       info[0] = "香港";
/* 348 */       info[1] = "N";
/* 349 */       info[2] = isValidHKCard(idcard) ? "true" : "false";
/*     */     } else {
/* 351 */       return null;
/*     */     } 
/* 353 */     return info;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isValidTWCard(String idcard) {
/* 363 */     if (null == idcard || idcard.length() != 10) {
/* 364 */       return false;
/*     */     }
/* 366 */     Integer iStart = TW_FIRST_CODE.get(Character.valueOf(idcard.charAt(0)));
/* 367 */     if (null == iStart) {
/* 368 */       return false;
/*     */     }
/* 370 */     int sum = iStart.intValue() / 10 + iStart.intValue() % 10 * 9;
/*     */     
/* 372 */     String mid = idcard.substring(1, 9);
/* 373 */     char[] chars = mid.toCharArray();
/* 374 */     int iflag = 8;
/* 375 */     for (char c : chars) {
/* 376 */       sum += Integer.parseInt(String.valueOf(c)) * iflag;
/* 377 */       iflag--;
/*     */     } 
/*     */     
/* 380 */     String end = idcard.substring(9, 10);
/* 381 */     return (((sum % 10 == 0) ? 0 : (10 - sum % 10)) == Integer.parseInt(end));
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
/*     */   public static boolean isValidHKCard(String idcard) {
/*     */     int sum;
/* 397 */     String card = idcard.replaceAll("[()]", "");
/*     */     
/* 399 */     if (card.length() == 9) {
/* 400 */       sum = (Character.toUpperCase(card.charAt(0)) - 55) * 9 + (Character.toUpperCase(card.charAt(1)) - 55) * 8;
/* 401 */       card = card.substring(1, 9);
/*     */     } else {
/* 403 */       sum = 522 + (Character.toUpperCase(card.charAt(0)) - 55) * 8;
/*     */     } 
/*     */ 
/*     */     
/* 407 */     String mid = card.substring(1, 7);
/* 408 */     String end = card.substring(7, 8);
/* 409 */     char[] chars = mid.toCharArray();
/* 410 */     int iflag = 7;
/* 411 */     for (char c : chars) {
/* 412 */       sum += Integer.parseInt(String.valueOf(c)) * iflag;
/* 413 */       iflag--;
/*     */     } 
/* 415 */     if ("A".equalsIgnoreCase(end)) {
/* 416 */       sum += 10;
/*     */     } else {
/* 418 */       sum += Integer.parseInt(end);
/*     */     } 
/* 420 */     return (sum % 11 == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getBirthByIdCard(String idcard) {
/* 431 */     return getBirth(idcard);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getBirth(String idCard) {
/* 441 */     Assert.notBlank(idCard, "id card must be not blank!", new Object[0]);
/* 442 */     int len = idCard.length();
/* 443 */     if (len < 15)
/* 444 */       return null; 
/* 445 */     if (len == 15) {
/* 446 */       idCard = convert15To18(idCard);
/*     */     }
/*     */     
/* 449 */     return ((String)Objects.<String>requireNonNull(idCard)).substring(6, 14);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DateTime getBirthDate(String idCard) {
/* 459 */     String birthByIdCard = getBirthByIdCard(idCard);
/* 460 */     return (null == birthByIdCard) ? null : DateUtil.parse(birthByIdCard, (DateParser)DatePattern.PURE_DATE_FORMAT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getAgeByIdCard(String idcard) {
/* 470 */     return getAgeByIdCard(idcard, (Date)DateUtil.date());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getAgeByIdCard(String idcard, Date dateToCompare) {
/* 481 */     String birth = getBirthByIdCard(idcard);
/* 482 */     return DateUtil.age((Date)DateUtil.parse(birth, "yyyyMMdd"), dateToCompare);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Short getYearByIdCard(String idcard) {
/* 492 */     int len = idcard.length();
/* 493 */     if (len < 15)
/* 494 */       return null; 
/* 495 */     if (len == 15) {
/* 496 */       idcard = convert15To18(idcard);
/*     */     }
/* 498 */     return Short.valueOf(((String)Objects.<String>requireNonNull(idcard)).substring(6, 10));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Short getMonthByIdCard(String idcard) {
/* 508 */     int len = idcard.length();
/* 509 */     if (len < 15)
/* 510 */       return null; 
/* 511 */     if (len == 15) {
/* 512 */       idcard = convert15To18(idcard);
/*     */     }
/* 514 */     return Short.valueOf(((String)Objects.<String>requireNonNull(idcard)).substring(10, 12));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Short getDayByIdCard(String idcard) {
/* 524 */     int len = idcard.length();
/* 525 */     if (len < 15)
/* 526 */       return null; 
/* 527 */     if (len == 15) {
/* 528 */       idcard = convert15To18(idcard);
/*     */     }
/* 530 */     return Short.valueOf(((String)Objects.<String>requireNonNull(idcard)).substring(12, 14));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getGenderByIdCard(String idcard) {
/* 540 */     Assert.notBlank(idcard);
/* 541 */     int len = idcard.length();
/* 542 */     if (len < 15) {
/* 543 */       throw new IllegalArgumentException("ID Card length must be 15 or 18");
/*     */     }
/*     */     
/* 546 */     if (len == 15) {
/* 547 */       idcard = convert15To18(idcard);
/*     */     }
/* 549 */     char sCardChar = ((String)Objects.<String>requireNonNull(idcard)).charAt(16);
/* 550 */     return (sCardChar % 2 != 0) ? 1 : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getProvinceCodeByIdCard(String idcard) {
/* 561 */     int len = idcard.length();
/* 562 */     if (len == 15 || len == 18) {
/* 563 */       return idcard.substring(0, 2);
/*     */     }
/* 565 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getProvinceByIdCard(String idcard) {
/* 575 */     String code = getProvinceCodeByIdCard(idcard);
/* 576 */     if (StrUtil.isNotBlank(code)) {
/* 577 */       return CITY_CODES.get(code);
/*     */     }
/* 579 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getCityCodeByIdCard(String idcard) {
/* 590 */     int len = idcard.length();
/* 591 */     if (len == 15 || len == 18) {
/* 592 */       return idcard.substring(0, 4);
/*     */     }
/* 594 */     return null;
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
/*     */   public static String getDistrictCodeByIdCard(String idcard) {
/* 606 */     int len = idcard.length();
/* 607 */     if (len == 15 || len == 18) {
/* 608 */       return idcard.substring(0, 6);
/*     */     }
/* 610 */     return null;
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
/*     */   public static String hide(String idcard, int startInclude, int endExclude) {
/* 624 */     return StrUtil.hide(idcard, startInclude, endExclude);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Idcard getIdcardInfo(String idcard) {
/* 635 */     return new Idcard(idcard);
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
/*     */   private static char getCheckCode18(String code17) {
/* 647 */     int sum = getPowerSum(code17.toCharArray());
/* 648 */     return getCheckCode18(sum);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static char getCheckCode18(int iSum) {
/* 658 */     switch (iSum % 11) {
/*     */       case 10:
/* 660 */         return '2';
/*     */       case 9:
/* 662 */         return '3';
/*     */       case 8:
/* 664 */         return '4';
/*     */       case 7:
/* 666 */         return '5';
/*     */       case 6:
/* 668 */         return '6';
/*     */       case 5:
/* 670 */         return '7';
/*     */       case 4:
/* 672 */         return '8';
/*     */       case 3:
/* 674 */         return '9';
/*     */       case 2:
/* 676 */         return 'X';
/*     */       case 1:
/* 678 */         return '0';
/*     */       case 0:
/* 680 */         return '1';
/*     */     } 
/* 682 */     return ' ';
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getPowerSum(char[] iArr) {
/* 693 */     int iSum = 0;
/* 694 */     if (POWER.length == iArr.length) {
/* 695 */       for (int i = 0; i < iArr.length; i++) {
/* 696 */         iSum += Integer.parseInt(String.valueOf(iArr[i])) * POWER[i];
/*     */       }
/*     */     }
/* 699 */     return iSum;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Idcard
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     
/*     */     private final String provinceCode;
/*     */ 
/*     */     
/*     */     private final String cityCode;
/*     */ 
/*     */     
/*     */     private final DateTime birthDate;
/*     */     
/*     */     private final Integer gender;
/*     */     
/*     */     private final int age;
/*     */ 
/*     */     
/*     */     public Idcard(String idcard) {
/* 724 */       this.provinceCode = IdcardUtil.getProvinceCodeByIdCard(idcard);
/* 725 */       this.cityCode = IdcardUtil.getCityCodeByIdCard(idcard);
/* 726 */       this.birthDate = IdcardUtil.getBirthDate(idcard);
/* 727 */       this.gender = Integer.valueOf(IdcardUtil.getGenderByIdCard(idcard));
/* 728 */       this.age = IdcardUtil.getAgeByIdCard(idcard);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getProvinceCode() {
/* 737 */       return this.provinceCode;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getProvince() {
/* 746 */       return (String)IdcardUtil.CITY_CODES.get(this.provinceCode);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getCityCode() {
/* 755 */       return this.cityCode;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public DateTime getBirthDate() {
/* 764 */       return this.birthDate;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Integer getGender() {
/* 773 */       return this.gender;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getAge() {
/* 782 */       return this.age;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 787 */       return "Idcard{provinceCode='" + this.provinceCode + '\'' + ", cityCode='" + this.cityCode + '\'' + ", birthDate=" + this.birthDate + ", gender=" + this.gender + ", age=" + this.age + '}';
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\IdcardUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */