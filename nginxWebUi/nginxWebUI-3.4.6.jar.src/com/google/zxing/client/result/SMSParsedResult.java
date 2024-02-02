/*     */ package com.google.zxing.client.result;
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
/*     */ public final class SMSParsedResult
/*     */   extends ParsedResult
/*     */ {
/*     */   private final String[] numbers;
/*     */   private final String[] vias;
/*     */   private final String subject;
/*     */   private final String body;
/*     */   
/*     */   public SMSParsedResult(String number, String via, String subject, String body) {
/*  36 */     super(ParsedResultType.SMS);
/*  37 */     this.numbers = new String[] { number };
/*  38 */     this.vias = new String[] { via };
/*  39 */     this.subject = subject;
/*  40 */     this.body = body;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SMSParsedResult(String[] numbers, String[] vias, String subject, String body) {
/*  47 */     super(ParsedResultType.SMS);
/*  48 */     this.numbers = numbers;
/*  49 */     this.vias = vias;
/*  50 */     this.subject = subject;
/*  51 */     this.body = body;
/*     */   }
/*     */   
/*     */   public String getSMSURI() {
/*     */     StringBuilder result;
/*  56 */     (result = new StringBuilder()).append("sms:");
/*  57 */     boolean first = true;
/*  58 */     for (int i = 0; i < this.numbers.length; i++) {
/*  59 */       if (first) {
/*  60 */         first = false;
/*     */       } else {
/*  62 */         result.append(',');
/*     */       } 
/*  64 */       result.append(this.numbers[i]);
/*  65 */       if (this.vias != null && this.vias[i] != null) {
/*  66 */         result.append(";via=");
/*  67 */         result.append(this.vias[i]);
/*     */       } 
/*     */     } 
/*  70 */     boolean hasBody = (this.body != null);
/*  71 */     boolean hasSubject = (this.subject != null);
/*  72 */     if (hasBody || hasSubject) {
/*  73 */       result.append('?');
/*  74 */       if (hasBody) {
/*  75 */         result.append("body=");
/*  76 */         result.append(this.body);
/*     */       } 
/*  78 */       if (hasSubject) {
/*  79 */         if (hasBody) {
/*  80 */           result.append('&');
/*     */         }
/*  82 */         result.append("subject=");
/*  83 */         result.append(this.subject);
/*     */       } 
/*     */     } 
/*  86 */     return result.toString();
/*     */   }
/*     */   
/*     */   public String[] getNumbers() {
/*  90 */     return this.numbers;
/*     */   }
/*     */   
/*     */   public String[] getVias() {
/*  94 */     return this.vias;
/*     */   }
/*     */   
/*     */   public String getSubject() {
/*  98 */     return this.subject;
/*     */   }
/*     */   
/*     */   public String getBody() {
/* 102 */     return this.body;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayResult() {
/* 107 */     StringBuilder result = new StringBuilder(100);
/* 108 */     maybeAppend(this.numbers, result);
/* 109 */     maybeAppend(this.subject, result);
/* 110 */     maybeAppend(this.body, result);
/* 111 */     return result.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\SMSParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */