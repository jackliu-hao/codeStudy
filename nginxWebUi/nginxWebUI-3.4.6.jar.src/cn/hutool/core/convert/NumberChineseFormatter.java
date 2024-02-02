/*     */ package cn.hutool.core.convert;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.NumberUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NumberChineseFormatter
/*     */ {
/*  25 */   private static final char[] DIGITS = new char[] { '零', '一', '壹', '二', '贰', '三', '叁', '四', '肆', '五', '伍', '六', '陆', '七', '柒', '八', '捌', '九', '玖' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   private static final ChineseUnit[] CHINESE_NAME_VALUE = new ChineseUnit[] { new ChineseUnit(' ', 1, false), new ChineseUnit('十', 10, false), new ChineseUnit('拾', 10, false), new ChineseUnit('百', 100, false), new ChineseUnit('佰', 100, false), new ChineseUnit('千', 1000, false), new ChineseUnit('仟', 1000, false), new ChineseUnit('万', 10000, true), new ChineseUnit('亿', 100000000, true) };
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
/*     */   public static String format(double amount, boolean isUseTraditional) {
/*  51 */     return format(amount, isUseTraditional, false);
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
/*     */   public static String format(double amount, boolean isUseTraditional, boolean isMoneyMode, String negativeName, String unitName) {
/*  74 */     if (0.0D == amount) {
/*  75 */       return "零";
/*     */     }
/*  77 */     Assert.checkBetween(amount, -9.999999999999998E13D, 9.999999999999998E13D, "Number support only: (-99999999999999.99 ~ 99999999999999.99)！", new Object[0]);
/*     */ 
/*     */     
/*  80 */     StringBuilder chineseStr = new StringBuilder();
/*     */ 
/*     */     
/*  83 */     if (amount < 0.0D) {
/*  84 */       chineseStr.append(StrUtil.isNullOrUndefined(negativeName) ? "负" : negativeName);
/*  85 */       amount = -amount;
/*     */     } 
/*     */     
/*  88 */     long yuan = Math.round(amount * 100.0D);
/*  89 */     int fen = (int)(yuan % 10L);
/*  90 */     yuan /= 10L;
/*  91 */     int jiao = (int)(yuan % 10L);
/*  92 */     yuan /= 10L;
/*     */ 
/*     */     
/*  95 */     if (false == isMoneyMode || 0L != yuan) {
/*     */       
/*  97 */       chineseStr.append(longToChinese(yuan, isUseTraditional));
/*  98 */       if (isMoneyMode) {
/*  99 */         chineseStr.append(StrUtil.isNullOrUndefined(unitName) ? "元" : unitName);
/*     */       }
/*     */     } 
/*     */     
/* 103 */     if (0 == jiao && 0 == fen) {
/*     */       
/* 105 */       if (isMoneyMode) {
/* 106 */         chineseStr.append("整");
/*     */       }
/* 108 */       return chineseStr.toString();
/*     */     } 
/*     */ 
/*     */     
/* 112 */     if (false == isMoneyMode) {
/* 113 */       chineseStr.append("点");
/*     */     }
/*     */ 
/*     */     
/* 117 */     if (0L == yuan && 0 == jiao) {
/*     */       
/* 119 */       if (false == isMoneyMode) {
/* 120 */         chineseStr.append("零");
/*     */       }
/*     */     } else {
/* 123 */       chineseStr.append(numberToChinese(jiao, isUseTraditional));
/* 124 */       if (isMoneyMode && 0 != jiao) {
/* 125 */         chineseStr.append("角");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 130 */     if (0 != fen) {
/* 131 */       chineseStr.append(numberToChinese(fen, isUseTraditional));
/* 132 */       if (isMoneyMode) {
/* 133 */         chineseStr.append("分");
/*     */       }
/*     */     } 
/*     */     
/* 137 */     return chineseStr.toString();
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
/*     */   public static String format(double amount, boolean isUseTraditional, boolean isMoneyMode) {
/* 149 */     return format(amount, isUseTraditional, isMoneyMode, "负", "元");
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
/*     */   public static String format(long amount, boolean isUseTraditional) {
/* 161 */     if (0L == amount) {
/* 162 */       return "零";
/*     */     }
/* 164 */     Assert.checkBetween(amount, -9.999999999999998E13D, 9.999999999999998E13D, "Number support only: (-99999999999999.99 ~ 99999999999999.99)！", new Object[0]);
/*     */ 
/*     */     
/* 167 */     StringBuilder chineseStr = new StringBuilder();
/*     */ 
/*     */     
/* 170 */     if (amount < 0L) {
/* 171 */       chineseStr.append("负");
/* 172 */       amount = -amount;
/*     */     } 
/*     */     
/* 175 */     chineseStr.append(longToChinese(amount, isUseTraditional));
/* 176 */     return chineseStr.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String formatSimple(long amount) {
/*     */     String res;
/* 186 */     if (amount < 10000L && amount > -10000L) {
/* 187 */       return String.valueOf(amount);
/*     */     }
/*     */     
/* 190 */     if (amount < 100000000L && amount > -100000000L) {
/* 191 */       res = NumberUtil.div((float)amount, 10000.0F, 2) + "万";
/* 192 */     } else if (amount < 1000000000000L && amount > -1000000000000L) {
/* 193 */       res = NumberUtil.div((float)amount, 1.0E8F, 2) + "亿";
/*     */     } else {
/* 195 */       res = NumberUtil.div((float)amount, 1.0E12F, 2) + "万亿";
/*     */     } 
/* 197 */     return res;
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
/*     */   public static String formatThousand(int amount, boolean isUseTraditional) {
/* 210 */     Assert.checkBetween(amount, -999, 999, "Number support only: (-999 ~ 999)！", new Object[0]);
/*     */     
/* 212 */     String chinese = thousandToChinese(amount, isUseTraditional);
/* 213 */     if (amount < 20 && amount >= 10)
/*     */     {
/* 215 */       return chinese.substring(1);
/*     */     }
/* 217 */     return chinese;
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
/*     */   public static String numberCharToChinese(char c, boolean isUseTraditional) {
/* 229 */     if (c < '0' || c > '9') {
/* 230 */       return String.valueOf(c);
/*     */     }
/* 232 */     return String.valueOf(numberToChinese(c - 48, isUseTraditional));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String longToChinese(long amount, boolean isUseTraditional) {
/* 243 */     if (0L == amount) {
/* 244 */       return "零";
/*     */     }
/*     */ 
/*     */     
/* 248 */     int[] parts = new int[4];
/* 249 */     for (int i = 0; amount != 0L; i++) {
/* 250 */       parts[i] = (int)(amount % 10000L);
/* 251 */       amount /= 10000L;
/*     */     } 
/*     */     
/* 254 */     StringBuilder chineseStr = new StringBuilder();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 259 */     int partValue = parts[0];
/* 260 */     if (partValue > 0) {
/* 261 */       String partChinese = thousandToChinese(partValue, isUseTraditional);
/* 262 */       chineseStr.insert(0, partChinese);
/*     */       
/* 264 */       if (partValue < 1000)
/*     */       {
/* 266 */         addPreZero(chineseStr);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 271 */     partValue = parts[1];
/* 272 */     if (partValue > 0) {
/* 273 */       if (partValue % 10 == 0 && parts[0] > 0)
/*     */       {
/* 275 */         addPreZero(chineseStr);
/*     */       }
/* 277 */       String partChinese = thousandToChinese(partValue, isUseTraditional);
/* 278 */       chineseStr.insert(0, partChinese + "万");
/*     */       
/* 280 */       if (partValue < 1000)
/*     */       {
/* 282 */         addPreZero(chineseStr);
/*     */       }
/*     */     } else {
/* 285 */       addPreZero(chineseStr);
/*     */     } 
/*     */ 
/*     */     
/* 289 */     partValue = parts[2];
/* 290 */     if (partValue > 0) {
/* 291 */       if (partValue % 10 == 0 && parts[1] > 0)
/*     */       {
/* 293 */         addPreZero(chineseStr);
/*     */       }
/*     */       
/* 296 */       String partChinese = thousandToChinese(partValue, isUseTraditional);
/* 297 */       chineseStr.insert(0, partChinese + "亿");
/*     */       
/* 299 */       if (partValue < 1000)
/*     */       {
/* 301 */         addPreZero(chineseStr);
/*     */       }
/*     */     } else {
/* 304 */       addPreZero(chineseStr);
/*     */     } 
/*     */ 
/*     */     
/* 308 */     partValue = parts[3];
/* 309 */     if (partValue > 0) {
/* 310 */       if (parts[2] == 0) {
/* 311 */         chineseStr.insert(0, "亿");
/*     */       }
/* 313 */       String partChinese = thousandToChinese(partValue, isUseTraditional);
/* 314 */       chineseStr.insert(0, partChinese + "万");
/*     */     } 
/*     */     
/* 317 */     if (StrUtil.isNotEmpty(chineseStr) && '零' == chineseStr.charAt(0)) {
/* 318 */       return chineseStr.substring(1);
/*     */     }
/*     */     
/* 321 */     return chineseStr.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String thousandToChinese(int amountPart, boolean isUseTraditional) {
/* 332 */     if (amountPart == 0)
/*     */     {
/* 334 */       return String.valueOf(DIGITS[0]);
/*     */     }
/*     */     
/* 337 */     int temp = amountPart;
/*     */     
/* 339 */     StringBuilder chineseStr = new StringBuilder();
/* 340 */     boolean lastIsZero = true;
/* 341 */     for (int i = 0; temp > 0; i++) {
/* 342 */       int digit = temp % 10;
/* 343 */       if (digit == 0) {
/* 344 */         if (false == lastIsZero)
/*     */         {
/* 346 */           chineseStr.insert(0, "零");
/*     */         }
/* 348 */         lastIsZero = true;
/*     */       } else {
/* 350 */         chineseStr.insert(0, numberToChinese(digit, isUseTraditional) + getUnitName(i, isUseTraditional));
/* 351 */         lastIsZero = false;
/*     */       } 
/* 353 */       temp /= 10;
/*     */     } 
/* 355 */     return chineseStr.toString();
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
/*     */   public static int chineseToNumber(String chinese) {
/* 371 */     int length = chinese.length();
/* 372 */     int result = 0;
/*     */ 
/*     */     
/* 375 */     int section = 0;
/* 376 */     int number = 0;
/* 377 */     ChineseUnit unit = null;
/*     */     
/* 379 */     for (int i = 0; i < length; i++) {
/* 380 */       char c = chinese.charAt(i);
/* 381 */       int num = chineseToNumber(c);
/* 382 */       if (num >= 0) {
/* 383 */         if (num == 0) {
/*     */           
/* 385 */           if (number > 0 && null != unit) {
/* 386 */             section += number * unit.value / 10;
/*     */           }
/* 388 */           unit = null;
/* 389 */         } else if (number > 0) {
/*     */           
/* 391 */           throw new IllegalArgumentException(StrUtil.format("Bad number '{}{}' at: {}", new Object[] { Character.valueOf(chinese.charAt(i - 1)), Character.valueOf(c), Integer.valueOf(i) }));
/*     */         } 
/*     */         
/* 394 */         number = num;
/*     */       } else {
/* 396 */         unit = chineseToUnit(c);
/* 397 */         if (null == unit)
/*     */         {
/* 399 */           throw new IllegalArgumentException(StrUtil.format("Unknown unit '{}' at: {}", new Object[] { Character.valueOf(c), Integer.valueOf(i) }));
/*     */         }
/*     */ 
/*     */         
/* 403 */         if (unit.secUnit) {
/*     */           
/* 405 */           section = (section + number) * unit.value;
/* 406 */           result += section;
/* 407 */           section = 0;
/*     */         } else {
/*     */           
/* 410 */           int unitNumber = number;
/* 411 */           if (0 == number && 0 == i)
/*     */           {
/*     */ 
/*     */             
/* 415 */             unitNumber = 1;
/*     */           }
/* 417 */           section += unitNumber * unit.value;
/*     */         } 
/* 419 */         number = 0;
/*     */       } 
/*     */     } 
/*     */     
/* 423 */     if (number > 0 && null != unit) {
/* 424 */       number *= unit.value / 10;
/*     */     }
/*     */     
/* 427 */     return result + section + number;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ChineseUnit chineseToUnit(char chinese) {
/* 437 */     for (ChineseUnit chineseNameValue : CHINESE_NAME_VALUE) {
/* 438 */       if (chineseNameValue.name == chinese) {
/* 439 */         return chineseNameValue;
/*     */       }
/*     */     } 
/* 442 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int chineseToNumber(char chinese) {
/* 453 */     if ('两' == chinese)
/*     */     {
/* 455 */       chinese = '二';
/*     */     }
/* 457 */     int i = ArrayUtil.indexOf(DIGITS, chinese);
/* 458 */     if (i > 0) {
/* 459 */       return (i + 1) / 2;
/*     */     }
/* 461 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static char numberToChinese(int number, boolean isUseTraditional) {
/* 472 */     if (0 == number) {
/* 473 */       return DIGITS[0];
/*     */     }
/* 475 */     return DIGITS[number * 2 - (isUseTraditional ? 0 : 1)];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getUnitName(int index, boolean isUseTraditional) {
/* 486 */     if (0 == index) {
/* 487 */       return "";
/*     */     }
/* 489 */     return String.valueOf((CHINESE_NAME_VALUE[index * 2 - (isUseTraditional ? 0 : 1)]).name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ChineseUnit
/*     */   {
/*     */     private final char name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final int value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final boolean secUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ChineseUnit(char name, int value, boolean secUnit) {
/* 521 */       this.name = name;
/* 522 */       this.value = value;
/* 523 */       this.secUnit = secUnit;
/*     */     }
/*     */   }
/*     */   
/*     */   private static void addPreZero(StringBuilder chineseStr) {
/* 528 */     if (StrUtil.isEmpty(chineseStr)) {
/*     */       return;
/*     */     }
/* 531 */     char c = chineseStr.charAt(0);
/* 532 */     if ('零' != c)
/* 533 */       chineseStr.insert(0, '零'); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\NumberChineseFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */