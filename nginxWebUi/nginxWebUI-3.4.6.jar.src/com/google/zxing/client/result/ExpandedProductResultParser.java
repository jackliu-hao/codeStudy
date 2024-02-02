/*     */ package com.google.zxing.client.result;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.Result;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ExpandedProductResultParser
/*     */   extends ResultParser
/*     */ {
/*     */   public ExpandedProductParsedResult parse(Result result) {
/*  45 */     if (result.getBarcodeFormat() != 
/*  46 */       BarcodeFormat.RSS_EXPANDED)
/*     */     {
/*  48 */       return null;
/*     */     }
/*  50 */     String rawText = getMassagedText(result);
/*     */     
/*  52 */     String productID = null;
/*  53 */     String sscc = null;
/*  54 */     String lotNumber = null;
/*  55 */     String productionDate = null;
/*  56 */     String packagingDate = null;
/*  57 */     String bestBeforeDate = null;
/*  58 */     String expirationDate = null;
/*  59 */     String weight = null;
/*  60 */     String weightType = null;
/*  61 */     String weightIncrement = null;
/*  62 */     String price = null;
/*  63 */     String priceIncrement = null;
/*  64 */     String priceCurrency = null;
/*  65 */     Map<String, String> uncommonAIs = new HashMap<>();
/*     */     
/*  67 */     int i = 0;
/*     */     
/*  69 */     while (i < rawText.length()) {
/*     */       String ai;
/*  71 */       if ((ai = findAIvalue(i, rawText)) == null)
/*     */       {
/*     */         
/*  74 */         return null;
/*     */       }
/*     */       
/*  77 */       String value = findValue(i = i + ai.length() + 2, rawText);
/*  78 */       i += value.length();
/*     */       
/*  80 */       switch (ai) {
/*     */         case "00":
/*  82 */           sscc = value;
/*     */           continue;
/*     */         case "01":
/*  85 */           productID = value;
/*     */           continue;
/*     */         case "10":
/*  88 */           lotNumber = value;
/*     */           continue;
/*     */         case "11":
/*  91 */           productionDate = value;
/*     */           continue;
/*     */         case "13":
/*  94 */           packagingDate = value;
/*     */           continue;
/*     */         case "15":
/*  97 */           bestBeforeDate = value;
/*     */           continue;
/*     */         case "17":
/* 100 */           expirationDate = value;
/*     */           continue;
/*     */         case "3100":
/*     */         case "3101":
/*     */         case "3102":
/*     */         case "3103":
/*     */         case "3104":
/*     */         case "3105":
/*     */         case "3106":
/*     */         case "3107":
/*     */         case "3108":
/*     */         case "3109":
/* 112 */           weight = value;
/* 113 */           weightType = "KG";
/* 114 */           weightIncrement = ai.substring(3);
/*     */           continue;
/*     */         case "3200":
/*     */         case "3201":
/*     */         case "3202":
/*     */         case "3203":
/*     */         case "3204":
/*     */         case "3205":
/*     */         case "3206":
/*     */         case "3207":
/*     */         case "3208":
/*     */         case "3209":
/* 126 */           weight = value;
/* 127 */           weightType = "LB";
/* 128 */           weightIncrement = ai.substring(3);
/*     */           continue;
/*     */         case "3920":
/*     */         case "3921":
/*     */         case "3922":
/*     */         case "3923":
/* 134 */           price = value;
/* 135 */           priceIncrement = ai.substring(3);
/*     */           continue;
/*     */         case "3930":
/*     */         case "3931":
/*     */         case "3932":
/*     */         case "3933":
/* 141 */           if (value.length() < 4)
/*     */           {
/*     */ 
/*     */             
/* 145 */             return null;
/*     */           }
/* 147 */           price = value.substring(3);
/* 148 */           priceCurrency = value.substring(0, 3);
/* 149 */           priceIncrement = ai.substring(3);
/*     */           continue;
/*     */       } 
/*     */       
/* 153 */       uncommonAIs.put(ai, value);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 158 */     return new ExpandedProductParsedResult(rawText, productID, sscc, lotNumber, productionDate, packagingDate, bestBeforeDate, expirationDate, weight, weightType, weightIncrement, price, priceIncrement, priceCurrency, uncommonAIs);
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
/*     */   private static String findAIvalue(int i, String rawText) {
/* 176 */     if (rawText.charAt(i) != 
/*     */       
/* 178 */       '(') {
/* 179 */       return null;
/*     */     }
/*     */     
/* 182 */     CharSequence rawTextAux = rawText.substring(i + 1);
/*     */     
/* 184 */     StringBuilder buf = new StringBuilder();
/* 185 */     for (int index = 0; index < rawTextAux.length(); index++) {
/*     */       char currentChar;
/* 187 */       if ((currentChar = rawTextAux.charAt(index)) == ')')
/* 188 */         return buf.toString(); 
/* 189 */       if (currentChar >= '0' && currentChar <= '9') {
/* 190 */         buf.append(currentChar);
/*     */       } else {
/* 192 */         return null;
/*     */       } 
/*     */     } 
/* 195 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private static String findValue(int i, String rawText) {
/* 199 */     StringBuilder buf = new StringBuilder();
/* 200 */     String rawTextAux = rawText.substring(i);
/*     */     
/* 202 */     for (int index = 0; index < rawTextAux.length(); index++) {
/*     */       char c;
/* 204 */       if ((c = rawTextAux.charAt(index)) == '(') {
/*     */ 
/*     */         
/* 207 */         if (findAIvalue(index, rawTextAux) == null) {
/* 208 */           buf.append('(');
/*     */         } else {
/*     */           break;
/*     */         } 
/*     */       } else {
/* 213 */         buf.append(c);
/*     */       } 
/*     */     } 
/* 216 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\ExpandedProductResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */