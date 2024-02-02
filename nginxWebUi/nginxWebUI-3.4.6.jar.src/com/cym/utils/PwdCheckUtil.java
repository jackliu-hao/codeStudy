/*     */ package com.cym.utils;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ 
/*     */ public class PwdCheckUtil
/*     */ {
/*   7 */   public static String[] KEYBOARD_SLOPE_ARR = new String[] { "!qaz", "1qaz", "@wsx", "2wsx", "#edc", "3edc", "$rfv", "4rfv", "%tgb", "5tgb", "^yhn", "6yhn", "&ujm", "7ujm", "*ik,", "8ik,", "(ol.", "9ol.", ")p;/", "0p;/", "+[;.", "=[;.", "_pl,", "-pl,", ")okm", "0okm", "(ijn", "9ijn", "*uhb", "8uhb", "&ygv", "7ygv", "^tfc", "6tfc", "%rdx", "5rdx", "$esz", "4esz" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  13 */   public static String[] KEYBOARD_HORIZONTAL_ARR = new String[] { "01234567890-=", "!@#$%^&*()_+", "qwertyuiop[]", "QWERTYUIOP{}", "asdfghjkl;'", "ASDFGHJKL:", "zxcvbnm,./", "ZXCVBNM<>?" };
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
/*  24 */   public static String DEFAULT_SPECIAL_CHAR = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
/*  25 */   public static String SPECIAL_CHAR = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean checkPasswordLength(String password, String minNum, String maxNum) {
/*  40 */     boolean flag = false;
/*  41 */     if (StrUtil.isBlank(maxNum)) {
/*  42 */       minNum = (StrUtil.isBlank(minNum) == true) ? "0" : minNum;
/*  43 */       if (password.length() >= Integer.parseInt(minNum)) {
/*  44 */         flag = true;
/*     */       }
/*     */     } else {
/*  47 */       minNum = (StrUtil.isBlank(minNum) == true) ? "0" : minNum;
/*  48 */       if (password.length() >= Integer.parseInt(minNum) && password
/*  49 */         .length() <= Integer.parseInt(maxNum)) {
/*  50 */         flag = true;
/*     */       }
/*     */     } 
/*  53 */     return flag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean checkContainDigit(String password) {
/*  62 */     char[] chPass = password.toCharArray();
/*  63 */     boolean flag = false;
/*  64 */     int num_count = 0;
/*     */     
/*  66 */     for (int i = 0; i < chPass.length; i++) {
/*  67 */       if (Character.isDigit(chPass[i])) {
/*  68 */         num_count++;
/*     */       }
/*     */     } 
/*  71 */     if (num_count >= 1) {
/*  72 */       flag = true;
/*     */     }
/*  74 */     return flag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean checkContainCase(String password) {
/*  83 */     char[] chPass = password.toCharArray();
/*  84 */     boolean flag = false;
/*  85 */     int char_count = 0;
/*     */     
/*  87 */     for (int i = 0; i < chPass.length; i++) {
/*  88 */       if (Character.isLetter(chPass[i])) {
/*  89 */         char_count++;
/*     */       }
/*     */     } 
/*  92 */     if (char_count >= 1) {
/*  93 */       flag = true;
/*     */     }
/*  95 */     return flag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean checkContainLowerCase(String password) {
/* 105 */     char[] chPass = password.toCharArray();
/* 106 */     boolean flag = false;
/* 107 */     int char_count = 0;
/*     */     
/* 109 */     for (int i = 0; i < chPass.length; i++) {
/* 110 */       if (Character.isLowerCase(chPass[i])) {
/* 111 */         char_count++;
/*     */       }
/*     */     } 
/* 114 */     if (char_count >= 1) {
/* 115 */       flag = true;
/*     */     }
/* 117 */     return flag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean checkContainUpperCase(String password) {
/* 127 */     char[] chPass = password.toCharArray();
/* 128 */     boolean flag = false;
/* 129 */     int char_count = 0;
/*     */     
/* 131 */     for (int i = 0; i < chPass.length; i++) {
/* 132 */       if (Character.isUpperCase(chPass[i])) {
/* 133 */         char_count++;
/*     */       }
/*     */     } 
/* 136 */     if (char_count >= 1) {
/* 137 */       flag = true;
/*     */     }
/* 139 */     return flag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean checkContainSpecialChar(String password) {
/* 149 */     char[] chPass = password.toCharArray();
/* 150 */     boolean flag = false;
/* 151 */     int special_count = 0;
/*     */     
/* 153 */     for (int i = 0; i < chPass.length; i++) {
/* 154 */       if (SPECIAL_CHAR.indexOf(chPass[i]) != -1) {
/* 155 */         special_count++;
/*     */       }
/*     */     } 
/*     */     
/* 159 */     if (special_count >= 1) {
/* 160 */       flag = true;
/*     */     }
/* 162 */     return flag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean checkLateralKeyboardSite(String password, int repetitions, boolean isLower) {
/* 172 */     String t_password = new String(password);
/*     */     
/* 174 */     t_password = t_password.toLowerCase();
/* 175 */     int n = t_password.length();
/*     */ 
/*     */ 
/*     */     
/* 179 */     boolean flag = false;
/* 180 */     int arrLen = KEYBOARD_HORIZONTAL_ARR.length;
/* 181 */     int limit_num = repetitions;
/*     */     
/* 183 */     for (int i = 0; i + limit_num <= n; i++) {
/* 184 */       String str = t_password.substring(i, i + limit_num);
/* 185 */       String distinguishStr = password.substring(i, i + limit_num);
/*     */       
/* 187 */       for (int j = 0; j < arrLen; j++) {
/* 188 */         String configStr = KEYBOARD_HORIZONTAL_ARR[j];
/* 189 */         String revOrderStr = (new StringBuffer(KEYBOARD_HORIZONTAL_ARR[j])).reverse().toString();
/*     */ 
/*     */         
/* 192 */         if (isLower) {
/*     */           
/* 194 */           String UpperStr = KEYBOARD_HORIZONTAL_ARR[j].toUpperCase();
/* 195 */           if (configStr.indexOf(distinguishStr) != -1 || UpperStr.indexOf(distinguishStr) != -1) {
/* 196 */             flag = true;
/* 197 */             return flag;
/*     */           } 
/*     */           
/* 200 */           String revUpperStr = (new StringBuffer(UpperStr)).reverse().toString();
/* 201 */           if (revOrderStr.indexOf(distinguishStr) != -1 || revUpperStr.indexOf(distinguishStr) != -1) {
/* 202 */             flag = true;
/* 203 */             return flag;
/*     */           } 
/*     */         } else {
/* 206 */           if (configStr.indexOf(str) != -1) {
/* 207 */             flag = true;
/* 208 */             return flag;
/*     */           } 
/*     */           
/* 211 */           if (revOrderStr.indexOf(str) != -1) {
/* 212 */             flag = true;
/* 213 */             return flag;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 218 */     return flag;
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
/*     */   public static boolean checkKeyboardSlantSite(String password, int repetitions, boolean isLower) {
/* 235 */     String t_password = new String(password);
/* 236 */     t_password = t_password.toLowerCase();
/* 237 */     int n = t_password.length();
/*     */ 
/*     */ 
/*     */     
/* 241 */     boolean flag = false;
/* 242 */     int arrLen = KEYBOARD_SLOPE_ARR.length;
/* 243 */     int limit_num = repetitions;
/*     */     
/* 245 */     for (int i = 0; i + limit_num <= n; i++) {
/* 246 */       String str = t_password.substring(i, i + limit_num);
/* 247 */       String distinguishStr = password.substring(i, i + limit_num);
/* 248 */       for (int j = 0; j < arrLen; j++) {
/* 249 */         String configStr = KEYBOARD_SLOPE_ARR[j];
/* 250 */         String revOrderStr = (new StringBuffer(KEYBOARD_SLOPE_ARR[j])).reverse().toString();
/*     */         
/* 252 */         if (isLower) {
/*     */           
/* 254 */           String UpperStr = KEYBOARD_SLOPE_ARR[j].toUpperCase();
/* 255 */           if (configStr.indexOf(distinguishStr) != -1 || UpperStr.indexOf(distinguishStr) != -1) {
/* 256 */             flag = true;
/* 257 */             return flag;
/*     */           } 
/*     */           
/* 260 */           String revUpperStr = (new StringBuffer(UpperStr)).reverse().toString();
/* 261 */           if (revOrderStr.indexOf(distinguishStr) != -1 || revUpperStr.indexOf(distinguishStr) != -1) {
/* 262 */             flag = true;
/* 263 */             return flag;
/*     */           } 
/*     */         } else {
/* 266 */           if (configStr.indexOf(str) != -1) {
/* 267 */             flag = true;
/* 268 */             return flag;
/*     */           } 
/*     */           
/* 271 */           if (revOrderStr.indexOf(str) != -1) {
/* 272 */             flag = true;
/* 273 */             return flag;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 278 */     return flag;
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
/*     */   public static boolean checkSequentialChars(String password, int repetitions, boolean isLower) {
/* 291 */     String t_password = new String(password);
/* 292 */     boolean flag = false;
/* 293 */     int limit_num = repetitions;
/* 294 */     int normal_count = 0;
/* 295 */     int reversed_count = 0;
/*     */     
/* 297 */     if (!isLower) {
/* 298 */       t_password = t_password.toLowerCase();
/*     */     }
/* 300 */     int n = t_password.length();
/* 301 */     char[] pwdCharArr = t_password.toCharArray();
/*     */     
/* 303 */     for (int i = 0; i + limit_num <= n; i++) {
/* 304 */       normal_count = 0;
/* 305 */       reversed_count = 0;
/* 306 */       for (int j = 0; j < limit_num - 1; j++) {
/*     */         
/* 308 */         normal_count++;
/* 309 */         if (pwdCharArr[i + j + 1] - pwdCharArr[i + j] == 1 && normal_count == limit_num - 1) {
/* 310 */           return true;
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 315 */         reversed_count++;
/* 316 */         if (pwdCharArr[i + j] - pwdCharArr[i + j + 1] == 1 && reversed_count == limit_num - 1) {
/* 317 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 322 */     return flag;
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
/*     */   public static boolean checkSequentialSameChars(String password, int repetitions) {
/* 336 */     String t_password = new String(password);
/* 337 */     int n = t_password.length();
/* 338 */     char[] pwdCharArr = t_password.toCharArray();
/* 339 */     boolean flag = false;
/* 340 */     int limit_num = repetitions;
/* 341 */     int count = 0;
/* 342 */     for (int i = 0; i + limit_num <= n; i++) {
/* 343 */       count = 0;
/* 344 */       for (int j = 0; j < limit_num - 1; j++) {
/*     */         
/* 346 */         count++;
/* 347 */         if (pwdCharArr[i + j] == pwdCharArr[i + j + 1] && count == limit_num - 1) {
/* 348 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 353 */     return flag;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\PwdCheckUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */