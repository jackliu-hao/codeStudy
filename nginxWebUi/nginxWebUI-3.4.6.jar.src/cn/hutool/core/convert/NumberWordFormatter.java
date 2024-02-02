/*     */ package cn.hutool.core.convert;
/*     */ 
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
/*     */ public class NumberWordFormatter
/*     */ {
/*  15 */   private static final String[] NUMBER = new String[] { "", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE" };
/*     */   
/*  17 */   private static final String[] NUMBER_TEEN = new String[] { "TEN", "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN", "NINETEEN" };
/*     */   
/*  19 */   private static final String[] NUMBER_TEN = new String[] { "TEN", "TWENTY", "THIRTY", "FORTY", "FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY" };
/*     */   
/*  21 */   private static final String[] NUMBER_MORE = new String[] { "", "THOUSAND", "MILLION", "BILLION" };
/*     */   
/*  23 */   private static final String[] NUMBER_SUFFIX = new String[] { "k", "w", "", "m", "", "", "b", "", "", "t", "", "", "p", "", "", "e" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(Object x) {
/*  32 */     if (x != null) {
/*  33 */       return format(x.toString());
/*     */     }
/*  35 */     return "";
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
/*     */   public static String formatSimple(long value) {
/*  48 */     return formatSimple(value, true);
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
/*     */   public static String formatSimple(long value, boolean isTwo) {
/*  60 */     if (value < 1000L) {
/*  61 */       return String.valueOf(value);
/*     */     }
/*  63 */     int index = -1;
/*  64 */     double res = value;
/*  65 */     while (res > 10.0D && (false == isTwo || index < 1)) {
/*  66 */       if (res >= 1000.0D) {
/*  67 */         res /= 1000.0D;
/*  68 */         index++;
/*     */       } 
/*  70 */       if (res > 10.0D) {
/*  71 */         res /= 10.0D;
/*  72 */         index++;
/*     */       } 
/*     */     } 
/*  75 */     return String.format("%s%s", new Object[] { NumberUtil.decimalFormat("#.##", res), NUMBER_SUFFIX[index] });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String format(String x) {
/*     */     String lstr;
/*  85 */     int z = x.indexOf(".");
/*  86 */     String rstr = "";
/*  87 */     if (z > -1) {
/*  88 */       lstr = x.substring(0, z);
/*  89 */       rstr = x.substring(z + 1);
/*     */     } else {
/*     */       
/*  92 */       lstr = x;
/*     */     } 
/*     */     
/*  95 */     String lstrrev = StrUtil.reverse(lstr);
/*  96 */     String[] a = new String[5];
/*     */     
/*  98 */     switch (lstrrev.length() % 3) {
/*     */       case 1:
/* 100 */         lstrrev = lstrrev + "00";
/*     */         break;
/*     */       case 2:
/* 103 */         lstrrev = lstrrev + "0";
/*     */         break;
/*     */     } 
/* 106 */     StringBuilder lm = new StringBuilder();
/* 107 */     for (int i = 0; i < lstrrev.length() / 3; i++) {
/* 108 */       a[i] = StrUtil.reverse(lstrrev.substring(3 * i, 3 * i + 3));
/* 109 */       if (false == "000".equals(a[i])) {
/*     */         
/* 111 */         if (i != 0) {
/* 112 */           lm.insert(0, transThree(a[i]) + " " + parseMore(i) + " ");
/*     */         }
/*     */         else {
/*     */           
/* 116 */           lm = new StringBuilder(transThree(a[i]));
/*     */         } 
/*     */       } else {
/* 119 */         lm.append(transThree(a[i]));
/*     */       } 
/*     */     } 
/*     */     
/* 123 */     String xs = "";
/* 124 */     if (z > -1) {
/* 125 */       xs = "AND CENTS " + transTwo(rstr) + " ";
/*     */     }
/*     */     
/* 128 */     return lm.toString().trim() + " " + xs + "ONLY";
/*     */   }
/*     */   
/*     */   private static String parseFirst(String s) {
/* 132 */     return NUMBER[Integer.parseInt(s.substring(s.length() - 1))];
/*     */   }
/*     */   
/*     */   private static String parseTeen(String s) {
/* 136 */     return NUMBER_TEEN[Integer.parseInt(s) - 10];
/*     */   }
/*     */   
/*     */   private static String parseTen(String s) {
/* 140 */     return NUMBER_TEN[Integer.parseInt(s.substring(0, 1)) - 1];
/*     */   }
/*     */   
/*     */   private static String parseMore(int i) {
/* 144 */     return NUMBER_MORE[i];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String transTwo(String s) {
/*     */     String value;
/* 151 */     if (s.length() > 2) {
/* 152 */       s = s.substring(0, 2);
/* 153 */     } else if (s.length() < 2) {
/* 154 */       s = "0" + s;
/*     */     } 
/*     */     
/* 157 */     if (s.startsWith("0")) {
/* 158 */       value = parseFirst(s);
/* 159 */     } else if (s.startsWith("1")) {
/* 160 */       value = parseTeen(s);
/* 161 */     } else if (s.endsWith("0")) {
/* 162 */       value = parseTen(s);
/*     */     } else {
/* 164 */       value = parseTen(s) + " " + parseFirst(s);
/*     */     } 
/* 166 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String transThree(String s) {
/*     */     String value;
/* 173 */     if (s.startsWith("0")) {
/* 174 */       value = transTwo(s.substring(1));
/* 175 */     } else if ("00".equals(s.substring(1))) {
/* 176 */       value = parseFirst(s.substring(0, 1)) + " HUNDRED";
/*     */     } else {
/* 178 */       value = parseFirst(s.substring(0, 1)) + " HUNDRED AND " + transTwo(s.substring(1));
/*     */     } 
/* 180 */     return value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\NumberWordFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */