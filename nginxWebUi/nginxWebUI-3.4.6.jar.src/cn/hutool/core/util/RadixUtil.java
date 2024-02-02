/*     */ package cn.hutool.core.util;
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
/*     */ public class RadixUtil
/*     */ {
/*     */   public static final String RADIXS_34 = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
/*     */   public static final String RADIXS_SHUFFLE_34 = "H3UM16TDFPSBZJ90CW28QYRE45AXKNGV7L";
/*     */   public static final String RADIXS_59 = "0123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
/*     */   public static final String RADIXS_SHUFFLE_59 = "vh9wGkfK8YmqbsoENP3764SeCX0dVzrgy1HRtpnTaLjJW2xQiZAcBMUFDu5";
/*     */   
/*     */   public static String encode(String radixs, int num) {
/*  56 */     long tmpNum = (num >= 0) ? num : (4294967296L - ((num ^ 0xFFFFFFFF) + 1));
/*  57 */     return encode(radixs, tmpNum, 32);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(String radixs, long num) {
/*  68 */     if (num < 0L) {
/*  69 */       throw new RuntimeException("暂不支持负数！");
/*     */     }
/*     */     
/*  72 */     return encode(radixs, num, 64);
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
/*     */   public static int decodeToInt(String radixs, String encodeStr) {
/*  84 */     return (int)decode(radixs, encodeStr);
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
/*     */   public static long decode(String radixs, String encodeStr) {
/*  96 */     int rl = radixs.length();
/*  97 */     long res = 0L;
/*     */     
/*  99 */     for (char c : encodeStr.toCharArray()) {
/* 100 */       res = res * rl + radixs.indexOf(c);
/*     */     }
/* 102 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String encode(String radixs, long num, int maxLength) {
/* 107 */     if (radixs.length() < 2) {
/* 108 */       throw new RuntimeException("自定义进制最少两个字符哦！");
/*     */     }
/*     */     
/* 111 */     int rl = radixs.length();
/*     */     
/* 113 */     long tmpNum = num;
/*     */ 
/*     */     
/* 116 */     char[] aa = new char[maxLength];
/*     */     
/* 118 */     int i = aa.length;
/*     */     do {
/* 120 */       aa[--i] = radixs.charAt((int)(tmpNum % rl));
/* 121 */       tmpNum /= rl;
/* 122 */     } while (tmpNum > 0L);
/*     */     
/* 124 */     return new String(aa, i, aa.length - i);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\RadixUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */