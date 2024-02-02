/*      */ package cn.hutool.core.lang;
/*      */ 
/*      */ import cn.hutool.core.date.DateUtil;
/*      */ import cn.hutool.core.exceptions.ValidateException;
/*      */ import cn.hutool.core.util.CreditCodeUtil;
/*      */ import cn.hutool.core.util.IdcardUtil;
/*      */ import cn.hutool.core.util.NumberUtil;
/*      */ import cn.hutool.core.util.ObjectUtil;
/*      */ import cn.hutool.core.util.ReUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Validator
/*      */ {
/*   33 */   public static final Pattern GENERAL = PatternPool.GENERAL;
/*      */ 
/*      */ 
/*      */   
/*   37 */   public static final Pattern NUMBERS = PatternPool.NUMBERS;
/*      */ 
/*      */ 
/*      */   
/*   41 */   public static final Pattern GROUP_VAR = PatternPool.GROUP_VAR;
/*      */ 
/*      */ 
/*      */   
/*   45 */   public static final Pattern IPV4 = PatternPool.IPV4;
/*      */ 
/*      */ 
/*      */   
/*   49 */   public static final Pattern IPV6 = PatternPool.IPV6;
/*      */ 
/*      */ 
/*      */   
/*   53 */   public static final Pattern MONEY = PatternPool.MONEY;
/*      */ 
/*      */ 
/*      */   
/*   57 */   public static final Pattern EMAIL = PatternPool.EMAIL;
/*      */ 
/*      */ 
/*      */   
/*   61 */   public static final Pattern MOBILE = PatternPool.MOBILE;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   66 */   public static final Pattern CITIZEN_ID = PatternPool.CITIZEN_ID;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   71 */   public static final Pattern ZIP_CODE = PatternPool.ZIP_CODE;
/*      */ 
/*      */ 
/*      */   
/*   75 */   public static final Pattern BIRTHDAY = PatternPool.BIRTHDAY;
/*      */ 
/*      */ 
/*      */   
/*   79 */   public static final Pattern URL = PatternPool.URL;
/*      */ 
/*      */ 
/*      */   
/*   83 */   public static final Pattern URL_HTTP = PatternPool.URL_HTTP;
/*      */ 
/*      */ 
/*      */   
/*   87 */   public static final Pattern GENERAL_WITH_CHINESE = PatternPool.GENERAL_WITH_CHINESE;
/*      */ 
/*      */ 
/*      */   
/*   91 */   public static final Pattern UUID = PatternPool.UUID;
/*      */ 
/*      */ 
/*      */   
/*   95 */   public static final Pattern UUID_SIMPLE = PatternPool.UUID_SIMPLE;
/*      */ 
/*      */ 
/*      */   
/*   99 */   public static final Pattern PLATE_NUMBER = PatternPool.PLATE_NUMBER;
/*      */ 
/*      */ 
/*      */   
/*  103 */   public static final Pattern CAR_VIN = PatternPool.CAR_VIN;
/*      */ 
/*      */ 
/*      */   
/*  107 */   public static final Pattern CAR_DRIVING_LICENCE = PatternPool.CAR_DRIVING_LICENCE;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isTrue(boolean value) {
/*  117 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isFalse(boolean value) {
/*  128 */     return (false == value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean validateTrue(boolean value, String errorMsgTemplate, Object... params) throws ValidateException {
/*  142 */     if (isFalse(value)) {
/*  143 */       throw new ValidateException(errorMsgTemplate, params);
/*      */     }
/*  145 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean validateFalse(boolean value, String errorMsgTemplate, Object... params) throws ValidateException {
/*  159 */     if (isTrue(value)) {
/*  160 */       throw new ValidateException(errorMsgTemplate, params);
/*      */     }
/*  162 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNull(Object value) {
/*  172 */     return (null == value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotNull(Object value) {
/*  182 */     return (null != value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T validateNull(T value, String errorMsgTemplate, Object... params) throws ValidateException {
/*  197 */     if (isNotNull(value)) {
/*  198 */       throw new ValidateException(errorMsgTemplate, params);
/*      */     }
/*  200 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T validateNotNull(T value, String errorMsgTemplate, Object... params) throws ValidateException {
/*  214 */     if (isNull(value)) {
/*  215 */       throw new ValidateException(errorMsgTemplate, params);
/*      */     }
/*  217 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(Object value) {
/*  228 */     return (null == value || (value instanceof String && StrUtil.isEmpty((String)value)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(Object value) {
/*  239 */     return (false == isEmpty(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T validateEmpty(T value, String errorMsg) throws ValidateException {
/*  253 */     if (isNotEmpty(value)) {
/*  254 */       throw new ValidateException(errorMsg);
/*      */     }
/*  256 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T validateNotEmpty(T value, String errorMsg) throws ValidateException {
/*  270 */     if (isEmpty(value)) {
/*  271 */       throw new ValidateException(errorMsg);
/*      */     }
/*  273 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equal(Object t1, Object t2) {
/*  285 */     return ObjectUtil.equal(t1, t2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object validateEqual(Object t1, Object t2, String errorMsg) throws ValidateException {
/*  298 */     if (false == equal(t1, t2)) {
/*  299 */       throw new ValidateException(errorMsg);
/*      */     }
/*  301 */     return t1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void validateNotEqual(Object t1, Object t2, String errorMsg) throws ValidateException {
/*  313 */     if (equal(t1, t2)) {
/*  314 */       throw new ValidateException(errorMsg);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void validateNotEmptyAndEqual(Object t1, Object t2, String errorMsg) throws ValidateException {
/*  329 */     validateNotEmpty(t1, errorMsg);
/*  330 */     validateEqual(t1, t2, errorMsg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void validateNotEmptyAndNotEqual(Object t1, Object t2, String errorMsg) throws ValidateException {
/*  344 */     validateNotEmpty(t1, errorMsg);
/*  345 */     validateNotEqual(t1, t2, errorMsg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateMatchRegex(String regex, T value, String errorMsg) throws ValidateException {
/*  360 */     if (false == isMatchRegex(regex, (CharSequence)value)) {
/*  361 */       throw new ValidateException(errorMsg);
/*      */     }
/*  363 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isMatchRegex(Pattern pattern, CharSequence value) {
/*  374 */     return ReUtil.isMatch(pattern, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isMatchRegex(String regex, CharSequence value) {
/*  385 */     return ReUtil.isMatch(regex, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isGeneral(CharSequence value) {
/*  395 */     return isMatchRegex(GENERAL, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateGeneral(T value, String errorMsg) throws ValidateException {
/*  408 */     if (false == isGeneral((CharSequence)value)) {
/*  409 */       throw new ValidateException(errorMsg);
/*      */     }
/*  411 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isGeneral(CharSequence value, int min, int max) {
/*  423 */     if (min < 0) {
/*  424 */       min = 0;
/*      */     }
/*  426 */     String reg = "^\\w{" + min + "," + max + "}$";
/*  427 */     if (max <= 0) {
/*  428 */       reg = "^\\w{" + min + ",}$";
/*      */     }
/*  430 */     return isMatchRegex(reg, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateGeneral(T value, int min, int max, String errorMsg) throws ValidateException {
/*  445 */     if (false == isGeneral((CharSequence)value, min, max)) {
/*  446 */       throw new ValidateException(errorMsg);
/*      */     }
/*  448 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isGeneral(CharSequence value, int min) {
/*  459 */     return isGeneral(value, min, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateGeneral(T value, int min, String errorMsg) throws ValidateException {
/*  473 */     return validateGeneral(value, min, 0, errorMsg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isLetter(CharSequence value) {
/*  484 */     return StrUtil.isAllCharMatch(value, Character::isLetter);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateLetter(T value, String errorMsg) throws ValidateException {
/*  498 */     if (false == isLetter((CharSequence)value)) {
/*  499 */       throw new ValidateException(errorMsg);
/*      */     }
/*  501 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isUpperCase(CharSequence value) {
/*  512 */     return StrUtil.isAllCharMatch(value, Character::isUpperCase);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateUpperCase(T value, String errorMsg) throws ValidateException {
/*  526 */     if (false == isUpperCase((CharSequence)value)) {
/*  527 */       throw new ValidateException(errorMsg);
/*      */     }
/*  529 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isLowerCase(CharSequence value) {
/*  540 */     return StrUtil.isAllCharMatch(value, Character::isLowerCase);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateLowerCase(T value, String errorMsg) throws ValidateException {
/*  554 */     if (false == isLowerCase((CharSequence)value)) {
/*  555 */       throw new ValidateException(errorMsg);
/*      */     }
/*  557 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNumber(CharSequence value) {
/*  567 */     return NumberUtil.isNumber(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasNumber(CharSequence value) {
/*  578 */     return ReUtil.contains(PatternPool.NUMBERS, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String validateNumber(String value, String errorMsg) throws ValidateException {
/*  590 */     if (false == isNumber(value)) {
/*  591 */       throw new ValidateException(errorMsg);
/*      */     }
/*  593 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isWord(CharSequence value) {
/*  604 */     return isMatchRegex(PatternPool.WORD, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateWord(T value, String errorMsg) throws ValidateException {
/*  618 */     if (false == isWord((CharSequence)value)) {
/*  619 */       throw new ValidateException(errorMsg);
/*      */     }
/*  621 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isMoney(CharSequence value) {
/*  631 */     return isMatchRegex(MONEY, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateMoney(T value, String errorMsg) throws ValidateException {
/*  644 */     if (false == isMoney((CharSequence)value)) {
/*  645 */       throw new ValidateException(errorMsg);
/*      */     }
/*  647 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isZipCode(CharSequence value) {
/*  658 */     return isMatchRegex(ZIP_CODE, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateZipCode(T value, String errorMsg) throws ValidateException {
/*  671 */     if (false == isZipCode((CharSequence)value)) {
/*  672 */       throw new ValidateException(errorMsg);
/*      */     }
/*  674 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmail(CharSequence value) {
/*  684 */     return isMatchRegex(EMAIL, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateEmail(T value, String errorMsg) throws ValidateException {
/*  697 */     if (false == isEmail((CharSequence)value)) {
/*  698 */       throw new ValidateException(errorMsg);
/*      */     }
/*  700 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isMobile(CharSequence value) {
/*  710 */     return isMatchRegex(MOBILE, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateMobile(T value, String errorMsg) throws ValidateException {
/*  723 */     if (false == isMobile((CharSequence)value)) {
/*  724 */       throw new ValidateException(errorMsg);
/*      */     }
/*  726 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isCitizenId(CharSequence value) {
/*  736 */     return IdcardUtil.isValidCard(String.valueOf(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateCitizenIdNumber(T value, String errorMsg) throws ValidateException {
/*  749 */     if (false == isCitizenId((CharSequence)value)) {
/*  750 */       throw new ValidateException(errorMsg);
/*      */     }
/*  752 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isBirthday(int year, int month, int day) {
/*  765 */     int thisYear = DateUtil.thisYear();
/*  766 */     if (year < 1900 || year > thisYear) {
/*  767 */       return false;
/*      */     }
/*      */ 
/*      */     
/*  771 */     if (month < 1 || month > 12) {
/*  772 */       return false;
/*      */     }
/*      */ 
/*      */     
/*  776 */     if (day < 1 || day > 31) {
/*  777 */       return false;
/*      */     }
/*      */     
/*  780 */     if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
/*  781 */       return false;
/*      */     }
/*  783 */     if (month == 2)
/*      */     {
/*  785 */       return (day < 29 || (day == 29 && DateUtil.isLeapYear(year)));
/*      */     }
/*  787 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isBirthday(CharSequence value) {
/*  805 */     Matcher matcher = BIRTHDAY.matcher(value);
/*  806 */     if (matcher.find()) {
/*  807 */       int year = Integer.parseInt(matcher.group(1));
/*  808 */       int month = Integer.parseInt(matcher.group(3));
/*  809 */       int day = Integer.parseInt(matcher.group(5));
/*  810 */       return isBirthday(year, month, day);
/*      */     } 
/*  812 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateBirthday(T value, String errorMsg) throws ValidateException {
/*  825 */     if (false == isBirthday((CharSequence)value)) {
/*  826 */       throw new ValidateException(errorMsg);
/*      */     }
/*  828 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isIpv4(CharSequence value) {
/*  838 */     return isMatchRegex(IPV4, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateIpv4(T value, String errorMsg) throws ValidateException {
/*  851 */     if (false == isIpv4((CharSequence)value)) {
/*  852 */       throw new ValidateException(errorMsg);
/*      */     }
/*  854 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isIpv6(CharSequence value) {
/*  864 */     return isMatchRegex(IPV6, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateIpv6(T value, String errorMsg) throws ValidateException {
/*  877 */     if (false == isIpv6((CharSequence)value)) {
/*  878 */       throw new ValidateException(errorMsg);
/*      */     }
/*  880 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isMac(CharSequence value) {
/*  891 */     return isMatchRegex(PatternPool.MAC_ADDRESS, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateMac(T value, String errorMsg) throws ValidateException {
/*  905 */     if (false == isMac((CharSequence)value)) {
/*  906 */       throw new ValidateException(errorMsg);
/*      */     }
/*  908 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isPlateNumber(CharSequence value) {
/*  919 */     return isMatchRegex(PLATE_NUMBER, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validatePlateNumber(T value, String errorMsg) throws ValidateException {
/*  933 */     if (false == isPlateNumber((CharSequence)value)) {
/*  934 */       throw new ValidateException(errorMsg);
/*      */     }
/*  936 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isUrl(CharSequence value) {
/*  946 */     if (StrUtil.isBlank(value)) {
/*  947 */       return false;
/*      */     }
/*      */     try {
/*  950 */       new URL(StrUtil.str(value));
/*  951 */     } catch (MalformedURLException e) {
/*  952 */       return false;
/*      */     } 
/*  954 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateUrl(T value, String errorMsg) throws ValidateException {
/*  967 */     if (false == isUrl((CharSequence)value)) {
/*  968 */       throw new ValidateException(errorMsg);
/*      */     }
/*  970 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isChinese(CharSequence value) {
/*  980 */     return isMatchRegex(PatternPool.CHINESES, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasChinese(CharSequence value) {
/*  991 */     return ReUtil.contains("[⺀-⻿⼀-⿟㇀-㇯㐀-䶿一-鿿豈-﫿𠀀-𪛟𪜀-𫜿𫝀-𫠟𫠠-𬺯丽-𯨟]+", value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateChinese(T value, String errorMsg) throws ValidateException {
/* 1004 */     if (false == isChinese((CharSequence)value)) {
/* 1005 */       throw new ValidateException(errorMsg);
/*      */     }
/* 1007 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isGeneralWithChinese(CharSequence value) {
/* 1017 */     return isMatchRegex(GENERAL_WITH_CHINESE, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateGeneralWithChinese(T value, String errorMsg) throws ValidateException {
/* 1030 */     if (false == isGeneralWithChinese((CharSequence)value)) {
/* 1031 */       throw new ValidateException(errorMsg);
/*      */     }
/* 1033 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isUUID(CharSequence value) {
/* 1044 */     return (isMatchRegex(UUID, value) || isMatchRegex(UUID_SIMPLE, value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateUUID(T value, String errorMsg) throws ValidateException {
/* 1058 */     if (false == isUUID((CharSequence)value)) {
/* 1059 */       throw new ValidateException(errorMsg);
/*      */     }
/* 1061 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isHex(CharSequence value) {
/* 1072 */     return isMatchRegex(PatternPool.HEX, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateHex(T value, String errorMsg) throws ValidateException {
/* 1086 */     if (false == isHex((CharSequence)value)) {
/* 1087 */       throw new ValidateException(errorMsg);
/*      */     }
/* 1089 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isBetween(Number value, Number min, Number max) {
/* 1102 */     Assert.notNull(value);
/* 1103 */     Assert.notNull(min);
/* 1104 */     Assert.notNull(max);
/* 1105 */     double doubleValue = value.doubleValue();
/* 1106 */     return (doubleValue >= min.doubleValue() && doubleValue <= max.doubleValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void validateBetween(Number value, Number min, Number max, String errorMsg) throws ValidateException {
/* 1120 */     if (false == isBetween(value, min, max)) {
/* 1121 */       throw new ValidateException(errorMsg);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isCreditCode(CharSequence creditCode) {
/* 1140 */     return CreditCodeUtil.isCreditCode(creditCode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isCarVin(CharSequence value) {
/* 1152 */     return isMatchRegex(CAR_VIN, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateCarVin(T value, String errorMsg) throws ValidateException {
/* 1167 */     if (false == isCarVin((CharSequence)value)) {
/* 1168 */       throw new ValidateException(errorMsg);
/*      */     }
/* 1170 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isCarDrivingLicence(CharSequence value) {
/* 1183 */     return isMatchRegex(CAR_DRIVING_LICENCE, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isChineseName(CharSequence value) {
/* 1218 */     return isMatchRegex(PatternPool.CHINESE_NAME, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T validateCarDrivingLicence(T value, String errorMsg) throws ValidateException {
/* 1234 */     if (false == isCarDrivingLicence((CharSequence)value)) {
/* 1235 */       throw new ValidateException(errorMsg);
/*      */     }
/* 1237 */     return value;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Validator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */