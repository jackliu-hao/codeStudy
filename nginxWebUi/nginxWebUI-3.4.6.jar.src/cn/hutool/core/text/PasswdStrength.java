/*     */ package cn.hutool.core.text;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
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
/*     */ public class PasswdStrength
/*     */ {
/*     */   public enum PASSWD_LEVEL
/*     */   {
/*  18 */     EASY, MIDIUM, STRONG, VERY_STRONG, EXTREMELY_STRONG;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum CHAR_TYPE
/*     */   {
/*  25 */     NUM, SMALL_LETTER, CAPITAL_LETTER, OTHER_CHAR;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   private static final String[] DICTIONARY = new String[] { "password", "abc123", "iloveyou", "adobe123", "123123", "sunshine", "1314520", "a1b2c3", "123qwe", "aaa111", "qweasd", "admin", "passwd" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  37 */   private static final int[] SIZE_TABLE = new int[] { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int check(String passwd) {
/*  47 */     if (null == passwd) {
/*  48 */       throw new IllegalArgumentException("password is empty");
/*     */     }
/*  50 */     int len = passwd.length();
/*  51 */     int level = 0;
/*     */ 
/*     */     
/*  54 */     if (countLetter(passwd, CHAR_TYPE.NUM) > 0) {
/*  55 */       level++;
/*     */     }
/*  57 */     if (countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0) {
/*  58 */       level++;
/*     */     }
/*  60 */     if (len > 4 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0) {
/*  61 */       level++;
/*     */     }
/*  63 */     if (len > 6 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) {
/*  64 */       level++;
/*     */     }
/*     */     
/*  67 */     if ((len > 4 && countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0) || (
/*  68 */       countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0) || (
/*  69 */       countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) || (
/*  70 */       countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0) || (
/*  71 */       countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) || (
/*  72 */       countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0)) {
/*  73 */       level++;
/*     */     }
/*     */     
/*  76 */     if ((len > 6 && countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && 
/*  77 */       countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0) || (countLetter(passwd, CHAR_TYPE.NUM) > 0 && 
/*  78 */       countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) || (
/*  79 */       countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0 && 
/*  80 */       countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) || (countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && 
/*  81 */       countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0)) {
/*  82 */       level++;
/*     */     }
/*     */     
/*  85 */     if (len > 8 && countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && 
/*  86 */       countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) {
/*  87 */       level++;
/*     */     }
/*     */     
/*  90 */     if ((len > 6 && countLetter(passwd, CHAR_TYPE.NUM) >= 3 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 3) || (
/*  91 */       countLetter(passwd, CHAR_TYPE.NUM) >= 3 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 3) || (
/*  92 */       countLetter(passwd, CHAR_TYPE.NUM) >= 3 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2) || (
/*  93 */       countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 3 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 3) || (
/*  94 */       countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 3 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2) || (
/*  95 */       countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 3 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2)) {
/*  96 */       level++;
/*     */     }
/*     */     
/*  99 */     if ((len > 8 && countLetter(passwd, CHAR_TYPE.NUM) >= 2 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 2 && 
/* 100 */       countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 2) || (countLetter(passwd, CHAR_TYPE.NUM) >= 2 && 
/* 101 */       countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 2 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2) || (
/* 102 */       countLetter(passwd, CHAR_TYPE.NUM) >= 2 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 2 && 
/* 103 */       countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2) || (countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 2 && 
/* 104 */       countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 2 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2)) {
/* 105 */       level++;
/*     */     }
/*     */     
/* 108 */     if (len > 10 && countLetter(passwd, CHAR_TYPE.NUM) >= 2 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 2 && 
/* 109 */       countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 2 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2) {
/* 110 */       level++;
/*     */     }
/*     */     
/* 113 */     if (countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 3) {
/* 114 */       level++;
/*     */     }
/* 116 */     if (countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 6) {
/* 117 */       level++;
/*     */     }
/*     */     
/* 120 */     if (len > 12) {
/* 121 */       level++;
/* 122 */       if (len >= 16) {
/* 123 */         level++;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 128 */     if ("abcdefghijklmnopqrstuvwxyz".indexOf(passwd) > 0 || "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(passwd) > 0) {
/* 129 */       level--;
/*     */     }
/* 131 */     if ("qwertyuiop".indexOf(passwd) > 0 || "asdfghjkl".indexOf(passwd) > 0 || "zxcvbnm".indexOf(passwd) > 0) {
/* 132 */       level--;
/*     */     }
/* 134 */     if (StrUtil.isNumeric(passwd) && ("01234567890".indexOf(passwd) > 0 || "09876543210".indexOf(passwd) > 0)) {
/* 135 */       level--;
/*     */     }
/*     */     
/* 138 */     if (countLetter(passwd, CHAR_TYPE.NUM) == len || countLetter(passwd, CHAR_TYPE.SMALL_LETTER) == len || 
/* 139 */       countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) == len) {
/* 140 */       level--;
/*     */     }
/*     */     
/* 143 */     if (len % 2 == 0) {
/* 144 */       String part1 = passwd.substring(0, len / 2);
/* 145 */       String part2 = passwd.substring(len / 2);
/* 146 */       if (part1.equals(part2)) {
/* 147 */         level--;
/*     */       }
/* 149 */       if (StrUtil.isCharEquals(part1) && StrUtil.isCharEquals(part2)) {
/* 150 */         level--;
/*     */       }
/*     */     } 
/* 153 */     if (len % 3 == 0) {
/* 154 */       String part1 = passwd.substring(0, len / 3);
/* 155 */       String part2 = passwd.substring(len / 3, len / 3 * 2);
/* 156 */       String part3 = passwd.substring(len / 3 * 2);
/* 157 */       if (part1.equals(part2) && part2.equals(part3)) {
/* 158 */         level--;
/*     */       }
/*     */     } 
/*     */     
/* 162 */     if (StrUtil.isNumeric(passwd) && len >= 6 && len <= 8) {
/* 163 */       int year = 0;
/* 164 */       if (len == 8 || len == 6) {
/* 165 */         year = Integer.parseInt(passwd.substring(0, len - 4));
/*     */       }
/* 167 */       int size = sizeOfInt(year);
/* 168 */       int month = Integer.parseInt(passwd.substring(size, size + 2));
/* 169 */       int day = Integer.parseInt(passwd.substring(size + 2, len));
/* 170 */       if (year >= 1950 && year < 2050 && month >= 1 && month <= 12 && day >= 1 && day <= 31) {
/* 171 */         level--;
/*     */       }
/*     */     } 
/*     */     
/* 175 */     for (String s : DICTIONARY) {
/* 176 */       if (passwd.equals(s) || s.contains(passwd)) {
/* 177 */         level--;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 182 */     if (len <= 6) {
/* 183 */       level--;
/* 184 */       if (len <= 4) {
/* 185 */         level--;
/* 186 */         if (len <= 3) {
/* 187 */           level = 0;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 192 */     if (StrUtil.isCharEquals(passwd)) {
/* 193 */       level = 0;
/*     */     }
/*     */     
/* 196 */     if (level < 0) {
/* 197 */       level = 0;
/*     */     }
/*     */     
/* 200 */     return level;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PASSWD_LEVEL getLevel(String passwd) {
/* 210 */     int level = check(passwd);
/* 211 */     switch (level) {
/*     */       case 0:
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/* 216 */         return PASSWD_LEVEL.EASY;
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/* 220 */         return PASSWD_LEVEL.MIDIUM;
/*     */       case 7:
/*     */       case 8:
/*     */       case 9:
/* 224 */         return PASSWD_LEVEL.STRONG;
/*     */       case 10:
/*     */       case 11:
/*     */       case 12:
/* 228 */         return PASSWD_LEVEL.VERY_STRONG;
/*     */     } 
/* 230 */     return PASSWD_LEVEL.EXTREMELY_STRONG;
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
/*     */   private static CHAR_TYPE checkCharacterType(char c) {
/* 242 */     if (c >= '0' && c <= '9') {
/* 243 */       return CHAR_TYPE.NUM;
/*     */     }
/* 245 */     if (c >= 'A' && c <= 'Z') {
/* 246 */       return CHAR_TYPE.CAPITAL_LETTER;
/*     */     }
/* 248 */     if (c >= 'a' && c <= 'z') {
/* 249 */       return CHAR_TYPE.SMALL_LETTER;
/*     */     }
/* 251 */     return CHAR_TYPE.OTHER_CHAR;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int countLetter(String passwd, CHAR_TYPE type) {
/* 262 */     int count = 0;
/* 263 */     if (null != passwd) {
/* 264 */       int length = passwd.length();
/* 265 */       if (length > 0) {
/* 266 */         for (int i = 0; i < length; i++) {
/* 267 */           if (checkCharacterType(passwd.charAt(i)) == type) {
/* 268 */             count++;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 273 */     return count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int sizeOfInt(int x) {
/* 283 */     for (int i = 0;; i++) {
/* 284 */       if (x <= SIZE_TABLE[i])
/* 285 */         return i + 1; 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\PasswdStrength.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */