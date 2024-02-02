/*     */ package com.google.zxing.common;
/*     */ 
/*     */ import com.google.zxing.FormatException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public enum CharacterSetECI
/*     */ {
/*  33 */   Cp437(new int[] { 0, 2 }, new String[0]),
/*  34 */   ISO8859_1(new int[] { 1, 3 }, new String[] { "ISO-8859-1" }),
/*  35 */   ISO8859_2(4, new String[] { "ISO-8859-2" }),
/*  36 */   ISO8859_3(5, new String[] { "ISO-8859-3" }),
/*  37 */   ISO8859_4(6, new String[] { "ISO-8859-4" }),
/*  38 */   ISO8859_5(7, new String[] { "ISO-8859-5" }),
/*  39 */   ISO8859_6(8, new String[] { "ISO-8859-6" }),
/*  40 */   ISO8859_7(9, new String[] { "ISO-8859-7" }),
/*  41 */   ISO8859_8(10, new String[] { "ISO-8859-8" }),
/*  42 */   ISO8859_9(11, new String[] { "ISO-8859-9" }),
/*  43 */   ISO8859_10(12, new String[] { "ISO-8859-10" }),
/*  44 */   ISO8859_11(13, new String[] { "ISO-8859-11" }),
/*  45 */   ISO8859_13(15, new String[] { "ISO-8859-13" }),
/*  46 */   ISO8859_14(16, new String[] { "ISO-8859-14" }),
/*  47 */   ISO8859_15(17, new String[] { "ISO-8859-15" }),
/*  48 */   ISO8859_16(18, new String[] { "ISO-8859-16" }),
/*  49 */   SJIS(20, new String[] { "Shift_JIS" }),
/*  50 */   Cp1250(21, new String[] { "windows-1250" }),
/*  51 */   Cp1251(22, new String[] { "windows-1251" }),
/*  52 */   Cp1252(23, new String[] { "windows-1252" }),
/*  53 */   Cp1256(24, new String[] { "windows-1256" }),
/*  54 */   UnicodeBigUnmarked(25, new String[] { "UTF-16BE", "UnicodeBig" }),
/*  55 */   UTF8(26, new String[] { "UTF-8" }),
/*  56 */   ASCII(new int[] { 27, 170 }, new String[] { "US-ASCII" }),
/*  57 */   Big5(28),
/*  58 */   GB18030(29, new String[] { "GB2312", "EUC_CN", "GBK" }),
/*  59 */   EUC_KR(30, new String[] { "EUC-KR" }); private static final Map<Integer, CharacterSetECI> VALUE_TO_ECI;
/*     */   static {
/*  61 */     VALUE_TO_ECI = new HashMap<>();
/*  62 */     NAME_TO_ECI = new HashMap<>(); CharacterSetECI[] arrayOfCharacterSetECI; int i;
/*     */     byte b;
/*  64 */     for (i = (arrayOfCharacterSetECI = values()).length, b = 0; b < i; b++) {
/*  65 */       CharacterSetECI eci; int arrayOfInt[], j; byte b1; for (j = (arrayOfInt = (eci = arrayOfCharacterSetECI[b]).values).length, b1 = 0; b1 < j; ) { int value = arrayOfInt[b1];
/*  66 */         VALUE_TO_ECI.put(Integer.valueOf(value), eci); b1++; }
/*     */       
/*  68 */       NAME_TO_ECI.put(eci.name(), eci); String[] arrayOfString;
/*  69 */       for (j = (arrayOfString = eci.otherEncodingNames).length, b1 = 0; b1 < j; ) { String name = arrayOfString[b1];
/*  70 */         NAME_TO_ECI.put(name, eci);
/*     */         b1++; }
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static final Map<String, CharacterSetECI> NAME_TO_ECI;
/*     */   
/*     */   private final int[] values;
/*     */   private final String[] otherEncodingNames;
/*     */   
/*     */   CharacterSetECI(int value, String... otherEncodingNames) {
/*  83 */     this.values = new int[] { value };
/*  84 */     this.otherEncodingNames = otherEncodingNames;
/*     */   }
/*     */   
/*     */   CharacterSetECI(int[] values, String... otherEncodingNames) {
/*  88 */     this.values = values;
/*  89 */     this.otherEncodingNames = otherEncodingNames;
/*     */   }
/*     */   
/*     */   public int getValue() {
/*  93 */     return this.values[0];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CharacterSetECI getCharacterSetECIByValue(int value) throws FormatException {
/* 103 */     if (value < 0 || value >= 900) {
/* 104 */       throw FormatException.getFormatInstance();
/*     */     }
/* 106 */     return VALUE_TO_ECI.get(Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CharacterSetECI getCharacterSetECIByName(String name) {
/* 115 */     return NAME_TO_ECI.get(name);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\CharacterSetECI.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */