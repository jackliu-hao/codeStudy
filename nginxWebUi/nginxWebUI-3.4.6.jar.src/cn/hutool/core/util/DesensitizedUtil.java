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
/*     */ public class DesensitizedUtil
/*     */ {
/*     */   public enum DesensitizedType
/*     */   {
/*  31 */     USER_ID,
/*     */     
/*  33 */     CHINESE_NAME,
/*     */     
/*  35 */     ID_CARD,
/*     */     
/*  37 */     FIXED_PHONE,
/*     */     
/*  39 */     MOBILE_PHONE,
/*     */     
/*  41 */     ADDRESS,
/*     */     
/*  43 */     EMAIL,
/*     */     
/*  45 */     PASSWORD,
/*     */     
/*  47 */     CAR_LICENSE,
/*     */     
/*  49 */     BANK_CARD;
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
/*     */   public static String desensitized(CharSequence str, DesensitizedType desensitizedType) {
/*  74 */     if (StrUtil.isBlank(str)) {
/*  75 */       return "";
/*     */     }
/*  77 */     String newStr = String.valueOf(str);
/*  78 */     switch (desensitizedType) {
/*     */       case USER_ID:
/*  80 */         newStr = String.valueOf(userId());
/*     */         break;
/*     */       case CHINESE_NAME:
/*  83 */         newStr = chineseName(String.valueOf(str));
/*     */         break;
/*     */       case ID_CARD:
/*  86 */         newStr = idCardNum(String.valueOf(str), 1, 2);
/*     */         break;
/*     */       case FIXED_PHONE:
/*  89 */         newStr = fixedPhone(String.valueOf(str));
/*     */         break;
/*     */       case MOBILE_PHONE:
/*  92 */         newStr = mobilePhone(String.valueOf(str));
/*     */         break;
/*     */       case ADDRESS:
/*  95 */         newStr = address(String.valueOf(str), 8);
/*     */         break;
/*     */       case EMAIL:
/*  98 */         newStr = email(String.valueOf(str));
/*     */         break;
/*     */       case PASSWORD:
/* 101 */         newStr = password(String.valueOf(str));
/*     */         break;
/*     */       case CAR_LICENSE:
/* 104 */         newStr = carLicense(String.valueOf(str));
/*     */         break;
/*     */       case BANK_CARD:
/* 107 */         newStr = bankCard(String.valueOf(str));
/*     */         break;
/*     */     } 
/*     */     
/* 111 */     return newStr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Long userId() {
/* 120 */     return Long.valueOf(0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String chineseName(String fullName) {
/* 130 */     if (StrUtil.isBlank(fullName)) {
/* 131 */       return "";
/*     */     }
/* 133 */     return StrUtil.hide(fullName, 1, fullName.length());
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
/*     */   public static String idCardNum(String idCardNum, int front, int end) {
/* 146 */     if (StrUtil.isBlank(idCardNum)) {
/* 147 */       return "";
/*     */     }
/*     */     
/* 150 */     if (front + end > idCardNum.length()) {
/* 151 */       return "";
/*     */     }
/*     */     
/* 154 */     if (front < 0 || end < 0) {
/* 155 */       return "";
/*     */     }
/* 157 */     return StrUtil.hide(idCardNum, front, idCardNum.length() - end);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String fixedPhone(String num) {
/* 167 */     if (StrUtil.isBlank(num)) {
/* 168 */       return "";
/*     */     }
/* 170 */     return StrUtil.hide(num, 4, num.length() - 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String mobilePhone(String num) {
/* 180 */     if (StrUtil.isBlank(num)) {
/* 181 */       return "";
/*     */     }
/* 183 */     return StrUtil.hide(num, 3, num.length() - 4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String address(String address, int sensitiveSize) {
/* 194 */     if (StrUtil.isBlank(address)) {
/* 195 */       return "";
/*     */     }
/* 197 */     int length = address.length();
/* 198 */     return StrUtil.hide(address, length - sensitiveSize, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String email(String email) {
/* 208 */     if (StrUtil.isBlank(email)) {
/* 209 */       return "";
/*     */     }
/* 211 */     int index = StrUtil.indexOf(email, '@');
/* 212 */     if (index <= 1) {
/* 213 */       return email;
/*     */     }
/* 215 */     return StrUtil.hide(email, 1, index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String password(String password) {
/* 225 */     if (StrUtil.isBlank(password)) {
/* 226 */       return "";
/*     */     }
/* 228 */     return StrUtil.repeat('*', password.length());
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
/*     */   public static String carLicense(String carLicense) {
/* 243 */     if (StrUtil.isBlank(carLicense)) {
/* 244 */       return "";
/*     */     }
/*     */     
/* 247 */     if (carLicense.length() == 7) {
/* 248 */       carLicense = StrUtil.hide(carLicense, 3, 6);
/* 249 */     } else if (carLicense.length() == 8) {
/*     */       
/* 251 */       carLicense = StrUtil.hide(carLicense, 3, 7);
/*     */     } 
/* 253 */     return carLicense;
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
/*     */   public static String bankCard(String bankCardNo) {
/* 265 */     if (StrUtil.isBlank(bankCardNo)) {
/* 266 */       return bankCardNo;
/*     */     }
/* 268 */     bankCardNo = StrUtil.trim(bankCardNo);
/* 269 */     if (bankCardNo.length() < 9) {
/* 270 */       return bankCardNo;
/*     */     }
/*     */     
/* 273 */     int length = bankCardNo.length();
/* 274 */     int midLength = length - 8;
/* 275 */     StringBuilder buf = new StringBuilder();
/*     */     
/* 277 */     buf.append(bankCardNo, 0, 4);
/* 278 */     for (int i = 0; i < midLength; i++) {
/* 279 */       if (i % 4 == 0) {
/* 280 */         buf.append(' ');
/*     */       }
/* 282 */       buf.append('*');
/*     */     } 
/* 284 */     buf.append(' ').append(bankCardNo, length - 4, length);
/* 285 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\DesensitizedUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */