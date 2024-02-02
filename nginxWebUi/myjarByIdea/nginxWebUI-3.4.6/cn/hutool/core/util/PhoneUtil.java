package cn.hutool.core.util;

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.lang.Validator;
import java.util.regex.Pattern;

public class PhoneUtil {
   public static boolean isMobile(CharSequence value) {
      return Validator.isMatchRegex(PatternPool.MOBILE, value);
   }

   public static boolean isMobileHk(CharSequence value) {
      return Validator.isMatchRegex(PatternPool.MOBILE_HK, value);
   }

   public static boolean isMobileTw(CharSequence value) {
      return Validator.isMatchRegex(PatternPool.MOBILE_TW, value);
   }

   public static boolean isMobileMo(CharSequence value) {
      return Validator.isMatchRegex(PatternPool.MOBILE_MO, value);
   }

   public static boolean isTel(CharSequence value) {
      return Validator.isMatchRegex(PatternPool.TEL, value);
   }

   public static boolean isTel400800(CharSequence value) {
      return Validator.isMatchRegex(PatternPool.TEL_400_800, value);
   }

   public static boolean isPhone(CharSequence value) {
      return isMobile(value) || isTel400800(value) || isMobileHk(value) || isMobileTw(value) || isMobileMo(value);
   }

   public static CharSequence hideBefore(CharSequence phone) {
      return StrUtil.hide(phone, 0, 7);
   }

   public static CharSequence hideBetween(CharSequence phone) {
      return StrUtil.hide(phone, 3, 7);
   }

   public static CharSequence hideAfter(CharSequence phone) {
      return StrUtil.hide(phone, 7, 11);
   }

   public static CharSequence subBefore(CharSequence phone) {
      return StrUtil.sub(phone, 0, 3);
   }

   public static CharSequence subBetween(CharSequence phone) {
      return StrUtil.sub(phone, 3, 7);
   }

   public static CharSequence subAfter(CharSequence phone) {
      return StrUtil.sub(phone, 7, 11);
   }

   public static CharSequence subTelBefore(CharSequence value) {
      return ReUtil.getGroup1(PatternPool.TEL, value);
   }

   public static CharSequence subTelAfter(CharSequence value) {
      return ReUtil.get((Pattern)PatternPool.TEL, value, 2);
   }
}
