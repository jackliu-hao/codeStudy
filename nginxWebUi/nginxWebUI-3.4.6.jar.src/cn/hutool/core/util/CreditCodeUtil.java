/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.lang.PatternPool;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class CreditCodeUtil
/*     */ {
/*  26 */   public static final Pattern CREDIT_CODE_PATTERN = PatternPool.CREDIT_CODE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   private static final int[] WEIGHT = new int[] { 1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28 };
/*     */ 
/*     */ 
/*     */   
/*  35 */   private static final char[] BASE_CODE_ARRAY = "0123456789ABCDEFGHJKLMNPQRTUWXY".toCharArray();
/*     */ 
/*     */ 
/*     */   
/*  39 */   private static final Map<Character, Integer> CODE_INDEX_MAP = new ConcurrentHashMap<>(); static {
/*  40 */     for (int i = 0; i < BASE_CODE_ARRAY.length; i++) {
/*  41 */       CODE_INDEX_MAP.put(Character.valueOf(BASE_CODE_ARRAY[i]), Integer.valueOf(i));
/*     */     }
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
/*     */   public static boolean isCreditCodeSimple(CharSequence creditCode) {
/*  60 */     if (StrUtil.isBlank(creditCode)) {
/*  61 */       return false;
/*     */     }
/*  63 */     return ReUtil.isMatch(CREDIT_CODE_PATTERN, creditCode);
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
/*     */   public static boolean isCreditCode(CharSequence creditCode) {
/*  80 */     if (false == isCreditCodeSimple(creditCode)) {
/*  81 */       return false;
/*     */     }
/*     */     
/*  84 */     int parityBit = getParityBit(creditCode);
/*  85 */     if (parityBit < 0) {
/*  86 */       return false;
/*     */     }
/*     */     
/*  89 */     return (creditCode.charAt(17) == BASE_CODE_ARRAY[parityBit]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String randomCreditCode() {
/*  98 */     StringBuilder buf = new StringBuilder(18);
/*     */     
/*     */     int i;
/*     */     
/* 102 */     for (i = 0; i < 2; i++) {
/* 103 */       int num = RandomUtil.randomInt(BASE_CODE_ARRAY.length - 1);
/* 104 */       buf.append(Character.toUpperCase(BASE_CODE_ARRAY[num]));
/*     */     } 
/* 106 */     for (i = 2; i < 8; i++) {
/* 107 */       int num = RandomUtil.randomInt(10);
/* 108 */       buf.append(BASE_CODE_ARRAY[num]);
/*     */     } 
/* 110 */     for (i = 8; i < 17; i++) {
/* 111 */       int num = RandomUtil.randomInt(BASE_CODE_ARRAY.length - 1);
/* 112 */       buf.append(BASE_CODE_ARRAY[num]);
/*     */     } 
/*     */     
/* 115 */     String code = buf.toString();
/* 116 */     return code + BASE_CODE_ARRAY[getParityBit(code)];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getParityBit(CharSequence creditCode) {
/* 126 */     int sum = 0;
/*     */     
/* 128 */     for (int i = 0; i < 17; i++) {
/* 129 */       Integer codeIndex = CODE_INDEX_MAP.get(Character.valueOf(creditCode.charAt(i)));
/* 130 */       if (null == codeIndex) {
/* 131 */         return -1;
/*     */       }
/* 133 */       sum += codeIndex.intValue() * WEIGHT[i];
/*     */     } 
/* 135 */     int result = 31 - sum % 31;
/* 136 */     return (result == 31) ? 0 : result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\CreditCodeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */