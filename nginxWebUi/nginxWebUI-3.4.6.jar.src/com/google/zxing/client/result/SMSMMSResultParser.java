/*     */ package com.google.zxing.client.result;
/*     */ 
/*     */ import com.google.zxing.Result;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
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
/*     */ public final class SMSMMSResultParser
/*     */   extends ResultParser
/*     */ {
/*     */   public SMSParsedResult parse(Result result) {
/*     */     String smsURIWithoutQuery, rawText;
/*  46 */     if (!(rawText = getMassagedText(result)).startsWith("sms:") && !rawText.startsWith("SMS:") && 
/*  47 */       !rawText.startsWith("mms:") && !rawText.startsWith("MMS:")) {
/*  48 */       return null;
/*     */     }
/*     */ 
/*     */     
/*  52 */     Map<String, String> nameValuePairs = parseNameValuePairs(rawText);
/*  53 */     String subject = null;
/*  54 */     String body = null;
/*  55 */     boolean querySyntax = false;
/*  56 */     if (nameValuePairs != null && !nameValuePairs.isEmpty()) {
/*  57 */       subject = nameValuePairs.get("subject");
/*  58 */       body = nameValuePairs.get("body");
/*  59 */       querySyntax = true;
/*     */     } 
/*     */ 
/*     */     
/*     */     int queryStart;
/*     */ 
/*     */     
/*  66 */     if ((queryStart = rawText.indexOf('?', 4)) < 0 || !querySyntax) {
/*  67 */       smsURIWithoutQuery = rawText.substring(4);
/*     */     } else {
/*  69 */       smsURIWithoutQuery = rawText.substring(4, queryStart);
/*     */     } 
/*     */     
/*  72 */     int lastComma = -1;
/*     */     
/*  74 */     List<String> numbers = new ArrayList<>(1);
/*  75 */     List<String> vias = new ArrayList<>(1); int comma;
/*  76 */     while ((comma = smsURIWithoutQuery.indexOf(',', lastComma + 1)) > lastComma) {
/*  77 */       String numberPart = smsURIWithoutQuery.substring(lastComma + 1, comma);
/*  78 */       addNumberVia(numbers, vias, numberPart);
/*  79 */       lastComma = comma;
/*     */     } 
/*  81 */     addNumberVia(numbers, vias, smsURIWithoutQuery.substring(lastComma + 1));
/*     */     
/*  83 */     return new SMSParsedResult(numbers.<String>toArray(new String[numbers.size()]), vias
/*  84 */         .<String>toArray(new String[vias.size()]), subject, body);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void addNumberVia(Collection<String> numbers, Collection<String> vias, String numberPart) {
/*     */     String via;
/*     */     int numberEnd;
/*  93 */     if ((numberEnd = numberPart.indexOf(';')) < 0) {
/*  94 */       numbers.add(numberPart);
/*  95 */       vias.add(null); return;
/*     */     } 
/*  97 */     numbers.add(numberPart.substring(0, numberEnd));
/*     */     
/*     */     String maybeVia;
/* 100 */     if ((maybeVia = numberPart.substring(numberEnd + 1)).startsWith("via=")) {
/* 101 */       via = maybeVia.substring(4);
/*     */     } else {
/* 103 */       via = null;
/*     */     } 
/* 105 */     vias.add(via);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\SMSMMSResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */