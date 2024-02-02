/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.lang.PatternPool;
/*     */ import cn.hutool.core.lang.Validator;
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
/*     */ public class PhoneUtil
/*     */ {
/*     */   public static boolean isMobile(CharSequence value) {
/*  28 */     return Validator.isMatchRegex(PatternPool.MOBILE, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isMobileHk(CharSequence value) {
/*  39 */     return Validator.isMatchRegex(PatternPool.MOBILE_HK, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isMobileTw(CharSequence value) {
/*  50 */     return Validator.isMatchRegex(PatternPool.MOBILE_TW, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isMobileMo(CharSequence value) {
/*  61 */     return Validator.isMatchRegex(PatternPool.MOBILE_MO, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isTel(CharSequence value) {
/*  72 */     return Validator.isMatchRegex(PatternPool.TEL, value);
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
/*     */   public static boolean isTel400800(CharSequence value) {
/*  84 */     return Validator.isMatchRegex(PatternPool.TEL_400_800, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isPhone(CharSequence value) {
/*  95 */     return (isMobile(value) || isTel400800(value) || isMobileHk(value) || isMobileTw(value) || isMobileMo(value));
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
/*     */   public static CharSequence hideBefore(CharSequence phone) {
/* 107 */     return StrUtil.hide(phone, 0, 7);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CharSequence hideBetween(CharSequence phone) {
/* 118 */     return StrUtil.hide(phone, 3, 7);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CharSequence hideAfter(CharSequence phone) {
/* 129 */     return StrUtil.hide(phone, 7, 11);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CharSequence subBefore(CharSequence phone) {
/* 140 */     return StrUtil.sub(phone, 0, 3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CharSequence subBetween(CharSequence phone) {
/* 151 */     return StrUtil.sub(phone, 3, 7);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CharSequence subAfter(CharSequence phone) {
/* 162 */     return StrUtil.sub(phone, 7, 11);
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
/*     */   public static CharSequence subTelBefore(CharSequence value) {
/* 174 */     return ReUtil.getGroup1(PatternPool.TEL, value);
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
/*     */   public static CharSequence subTelAfter(CharSequence value) {
/* 186 */     return ReUtil.get(PatternPool.TEL, value, 2);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\PhoneUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */