/*     */ package com.google.zxing.client.result;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.Result;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class VINResultParser
/*     */   extends ResultParser
/*     */ {
/*  31 */   private static final Pattern IOQ = Pattern.compile("[IOQ]");
/*  32 */   private static final Pattern AZ09 = Pattern.compile("[A-Z0-9]{17}");
/*     */ 
/*     */   
/*     */   public VINParsedResult parse(Result result) {
/*  36 */     if (result.getBarcodeFormat() != BarcodeFormat.CODE_39) {
/*  37 */       return null;
/*     */     }
/*  39 */     String rawText = result.getText();
/*  40 */     rawText = IOQ.matcher(rawText).replaceAll("").trim();
/*  41 */     if (!AZ09.matcher(rawText).matches()) {
/*  42 */       return null;
/*     */     }
/*     */     try {
/*  45 */       if (!checkChecksum(rawText)) {
/*  46 */         return null;
/*     */       }
/*  48 */       String wmi = rawText.substring(0, 3);
/*  49 */       return new VINParsedResult(rawText, wmi, rawText
/*     */           
/*  51 */           .substring(3, 9), rawText
/*  52 */           .substring(9, 17), 
/*  53 */           countryCode(wmi), rawText
/*  54 */           .substring(3, 8), 
/*  55 */           modelYear(rawText.charAt(9)), rawText
/*  56 */           .charAt(10), rawText
/*  57 */           .substring(11));
/*  58 */     } catch (IllegalArgumentException illegalArgumentException) {
/*  59 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean checkChecksum(CharSequence vin) {
/*  64 */     int sum = 0;
/*  65 */     for (int i = 0; i < vin.length(); i++) {
/*  66 */       sum += vinPositionWeight(i + 1) * vinCharValue(vin.charAt(i));
/*     */     }
/*  68 */     char checkChar = vin.charAt(8);
/*  69 */     char expectedCheckChar = checkChar(sum % 11);
/*  70 */     return (checkChar == expectedCheckChar);
/*     */   }
/*     */   
/*     */   private static int vinCharValue(char c) {
/*  74 */     if (c >= 'A' && c <= 'I') {
/*  75 */       return c - 65 + 1;
/*     */     }
/*  77 */     if (c >= 'J' && c <= 'R') {
/*  78 */       return c - 74 + 1;
/*     */     }
/*  80 */     if (c >= 'S' && c <= 'Z') {
/*  81 */       return c - 83 + 2;
/*     */     }
/*  83 */     if (c >= '0' && c <= '9') {
/*  84 */       return c - 48;
/*     */     }
/*  86 */     throw new IllegalArgumentException();
/*     */   }
/*     */   
/*     */   private static int vinPositionWeight(int position) {
/*  90 */     if (position > 0 && position <= 7) {
/*  91 */       return 9 - position;
/*     */     }
/*  93 */     if (position == 8) {
/*  94 */       return 10;
/*     */     }
/*  96 */     if (position == 9) {
/*  97 */       return 0;
/*     */     }
/*  99 */     if (position >= 10 && position <= 17) {
/* 100 */       return 19 - position;
/*     */     }
/* 102 */     throw new IllegalArgumentException();
/*     */   }
/*     */   
/*     */   private static char checkChar(int remainder) {
/* 106 */     if (remainder < 10) {
/* 107 */       return (char)(remainder + 48);
/*     */     }
/* 109 */     if (remainder == 10) {
/* 110 */       return 'X';
/*     */     }
/* 112 */     throw new IllegalArgumentException();
/*     */   }
/*     */   
/*     */   private static int modelYear(char c) {
/* 116 */     if (c >= 'E' && c <= 'H') {
/* 117 */       return c - 69 + 1984;
/*     */     }
/* 119 */     if (c >= 'J' && c <= 'N') {
/* 120 */       return c - 74 + 1988;
/*     */     }
/* 122 */     if (c == 'P') {
/* 123 */       return 1993;
/*     */     }
/* 125 */     if (c >= 'R' && c <= 'T') {
/* 126 */       return c - 82 + 1994;
/*     */     }
/* 128 */     if (c >= 'V' && c <= 'Y') {
/* 129 */       return c - 86 + 1997;
/*     */     }
/* 131 */     if (c >= '1' && c <= '9') {
/* 132 */       return c - 49 + 2001;
/*     */     }
/* 134 */     if (c >= 'A' && c <= 'D') {
/* 135 */       return c - 65 + 2010;
/*     */     }
/* 137 */     throw new IllegalArgumentException();
/*     */   }
/*     */   
/*     */   private static String countryCode(CharSequence wmi) {
/* 141 */     char c1 = wmi.charAt(0);
/* 142 */     char c2 = wmi.charAt(1);
/* 143 */     switch (c1) {
/*     */       case '1':
/*     */       case '4':
/*     */       case '5':
/* 147 */         return "US";
/*     */       case '2':
/* 149 */         return "CA";
/*     */       case '3':
/* 151 */         if (c2 >= 'A' && c2 <= 'W') {
/* 152 */           return "MX";
/*     */         }
/*     */         break;
/*     */       case '9':
/* 156 */         if ((c2 >= 'A' && c2 <= 'E') || (c2 >= '3' && c2 <= '9')) {
/* 157 */           return "BR";
/*     */         }
/*     */         break;
/*     */       case 'J':
/* 161 */         if (c2 >= 'A' && c2 <= 'T') {
/* 162 */           return "JP";
/*     */         }
/*     */         break;
/*     */       case 'K':
/* 166 */         if (c2 >= 'L' && c2 <= 'R') {
/* 167 */           return "KO";
/*     */         }
/*     */         break;
/*     */       case 'L':
/* 171 */         return "CN";
/*     */       case 'M':
/* 173 */         if (c2 >= 'A' && c2 <= 'E') {
/* 174 */           return "IN";
/*     */         }
/*     */         break;
/*     */       case 'S':
/* 178 */         if (c2 >= 'A' && c2 <= 'M') {
/* 179 */           return "UK";
/*     */         }
/* 181 */         if (c2 >= 'N' && c2 <= 'T') {
/* 182 */           return "DE";
/*     */         }
/*     */         break;
/*     */       case 'V':
/* 186 */         if (c2 >= 'F' && c2 <= 'R') {
/* 187 */           return "FR";
/*     */         }
/* 189 */         if (c2 >= 'S' && c2 <= 'W') {
/* 190 */           return "ES";
/*     */         }
/*     */         break;
/*     */       case 'W':
/* 194 */         return "DE";
/*     */       case 'X':
/* 196 */         if (c2 == '0' || (c2 >= '3' && c2 <= '9')) {
/* 197 */           return "RU";
/*     */         }
/*     */         break;
/*     */       case 'Z':
/* 201 */         if (c2 >= 'A' && c2 <= 'R') {
/* 202 */           return "IT";
/*     */         }
/*     */         break;
/*     */     } 
/* 206 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\VINResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */