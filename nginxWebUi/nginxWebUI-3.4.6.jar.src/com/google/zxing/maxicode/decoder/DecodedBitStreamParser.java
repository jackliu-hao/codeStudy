/*     */ package com.google.zxing.maxicode.decoder;
/*     */ 
/*     */ import com.google.zxing.common.DecoderResult;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.NumberFormat;
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
/*     */ final class DecodedBitStreamParser
/*     */ {
/*     */   private static final char SHIFTA = '￰';
/*     */   private static final char SHIFTB = '￱';
/*     */   private static final char SHIFTC = '￲';
/*     */   private static final char SHIFTD = '￳';
/*     */   private static final char SHIFTE = '￴';
/*     */   private static final char TWOSHIFTA = '￵';
/*     */   private static final char THREESHIFTA = '￶';
/*     */   private static final char LATCHA = '￷';
/*     */   private static final char LATCHB = '￸';
/*     */   private static final char LOCK = '￹';
/*     */   private static final char ECI = '￺';
/*     */   private static final char NS = '￻';
/*     */   private static final char PAD = '￼';
/*     */   private static final char FS = '\034';
/*     */   private static final char GS = '\035';
/*     */   private static final char RS = '\036';
/*  49 */   private static final String[] SETS = new String[] { "\nABCDEFGHIJKLMNOPQRSTUVWXYZ￺\034\035\036￻ ￼\"#$%&'()*+,-./0123456789:￱￲￳￴￸", "`abcdefghijklmnopqrstuvwxyz￺\034\035\036￻{￼}~;<=>?[\\]^_ ,./:@!|￼￵￶￼￰￲￳￴￷", "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚ￺\034\035\036ÛÜÝÞßª¬±²³µ¹º¼½¾￷ ￹￳￴￸", "àáâãäåæçèéêëìíîïðñòóôõö÷øùú￺\034\035\036￻ûüýþÿ¡¨«¯°´·¸»¿￷ ￲￹￴￸", "\000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032￺￼￼\033￻\034\035\036\037 ¢£¤¥¦§©­®¶￷ ￲￳￹￸", "\000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?" };
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
/*     */   static DecoderResult decode(byte[] bytes, int mode) {
/*     */     String postcode;
/*     */     NumberFormat threeDigits;
/*     */     String country, service;
/*  74 */     StringBuilder result = new StringBuilder(144);
/*  75 */     switch (mode) {
/*     */       
/*     */       case 2:
/*     */       case 3:
/*  79 */         if (mode == 2) {
/*  80 */           int pc = getPostCode2(bytes);
/*     */           
/*  82 */           postcode = (new DecimalFormat("0000000000".substring(0, getPostCode2Length(bytes)))).format(pc);
/*     */         } else {
/*  84 */           postcode = getPostCode3(bytes);
/*     */         } 
/*     */         
/*  87 */         country = (threeDigits = new DecimalFormat("000")).format(getCountry(bytes));
/*  88 */         service = threeDigits.format(getServiceClass(bytes));
/*  89 */         result.append(getMessage(bytes, 10, 84));
/*  90 */         if (result.toString().startsWith("[)>\03601\035")) {
/*  91 */           result.insert(9, postcode + '\035' + country + '\035' + service + '\035'); break;
/*     */         } 
/*  93 */         result.insert(0, postcode + '\035' + country + '\035' + service + '\035');
/*     */         break;
/*     */       
/*     */       case 4:
/*  97 */         result.append(getMessage(bytes, 1, 93));
/*     */         break;
/*     */       case 5:
/* 100 */         result.append(getMessage(bytes, 1, 77));
/*     */         break;
/*     */     } 
/* 103 */     return new DecoderResult(bytes, result.toString(), null, String.valueOf(mode));
/*     */   }
/*     */   
/*     */   private static int getBit(int bit, byte[] bytes) {
/* 107 */     bit--;
/* 108 */     return ((bytes[bit / 6] & 1 << 5 - bit % 6) == 0) ? 0 : 1;
/*     */   }
/*     */   
/*     */   private static int getInt(byte[] bytes, byte[] x) {
/* 112 */     if (x.length == 0) {
/* 113 */       throw new IllegalArgumentException();
/*     */     }
/* 115 */     int val = 0;
/* 116 */     for (int i = 0; i < x.length; i++) {
/* 117 */       val += getBit(x[i], bytes) << x.length - i - 1;
/*     */     }
/* 119 */     return val;
/*     */   }
/*     */   
/*     */   private static int getCountry(byte[] bytes) {
/* 123 */     return getInt(bytes, new byte[] { 53, 54, 43, 44, 45, 46, 47, 48, 37, 38 });
/*     */   }
/*     */   
/*     */   private static int getServiceClass(byte[] bytes) {
/* 127 */     return getInt(bytes, new byte[] { 55, 56, 57, 58, 59, 60, 49, 50, 51, 52 });
/*     */   }
/*     */   
/*     */   private static int getPostCode2Length(byte[] bytes) {
/* 131 */     return getInt(bytes, new byte[] { 39, 40, 41, 42, 31, 32 });
/*     */   }
/*     */   
/*     */   private static int getPostCode2(byte[] bytes) {
/* 135 */     return getInt(bytes, new byte[] { 33, 34, 35, 36, 25, 26, 27, 28, 29, 30, 19, 20, 21, 22, 23, 24, 13, 14, 15, 16, 17, 18, 7, 8, 9, 10, 11, 12, 1, 2 });
/*     */   }
/*     */ 
/*     */   
/*     */   private static String getPostCode3(byte[] bytes) {
/* 140 */     return String.valueOf(new char[] { SETS[0]
/*     */           
/* 142 */           .charAt(getInt(bytes, new byte[] { 39, 40, 41, 42, 31, 32 })), SETS[0]
/* 143 */           .charAt(getInt(bytes, new byte[] { 33, 34, 35, 36, 25, 26 })), SETS[0]
/* 144 */           .charAt(getInt(bytes, new byte[] { 27, 28, 29, 30, 19, 20 })), SETS[0]
/* 145 */           .charAt(getInt(bytes, new byte[] { 21, 22, 23, 24, 13, 14 })), SETS[0]
/* 146 */           .charAt(getInt(bytes, new byte[] { 15, 16, 17, 18, 7, 8 })), SETS[0]
/* 147 */           .charAt(getInt(bytes, new byte[] { 9, 10, 11, 12, 1, 2 })) });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getMessage(byte[] bytes, int start, int len) {
/* 153 */     StringBuilder sb = new StringBuilder();
/* 154 */     int shift = -1;
/* 155 */     int set = 0;
/* 156 */     int lastset = 0;
/* 157 */     for (int i = start; i < start + len; i++) {
/*     */       int nsval; char c;
/* 159 */       switch (c = SETS[set].charAt(bytes[i])) {
/*     */         case '￷':
/* 161 */           set = 0;
/* 162 */           shift = -1;
/*     */           break;
/*     */         case '￸':
/* 165 */           set = 1;
/* 166 */           shift = -1;
/*     */           break;
/*     */         case '￰':
/*     */         case '￱':
/*     */         case '￲':
/*     */         case '￳':
/*     */         case '￴':
/* 173 */           lastset = set;
/* 174 */           set = c - 65520;
/* 175 */           shift = 1;
/*     */           break;
/*     */         case '￵':
/* 178 */           lastset = set;
/* 179 */           set = 0;
/* 180 */           shift = 2;
/*     */           break;
/*     */         case '￶':
/* 183 */           lastset = set;
/* 184 */           set = 0;
/* 185 */           shift = 3;
/*     */           break;
/*     */         case '￻':
/* 188 */           nsval = (bytes[++i] << 24) + (bytes[++i] << 18) + (bytes[++i] << 12) + (bytes[++i] << 6) + bytes[++i];
/* 189 */           sb.append((new DecimalFormat("000000000")).format(nsval));
/*     */           break;
/*     */         case '￹':
/* 192 */           shift = -1;
/*     */           break;
/*     */         default:
/* 195 */           sb.append(c); break;
/*     */       } 
/* 197 */       if (shift-- == 0) {
/* 198 */         set = lastset;
/*     */       }
/*     */     } 
/* 201 */     while (sb.length() > 0 && sb.charAt(sb.length() - 1) == '￼') {
/* 202 */       sb.setLength(sb.length() - 1);
/*     */     }
/* 204 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\maxicode\decoder\DecodedBitStreamParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */