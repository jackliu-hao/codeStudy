/*    */ package com.google.zxing.client.result;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class EmailAddressParsedResult
/*    */   extends ParsedResult
/*    */ {
/*    */   private final String[] tos;
/*    */   private final String[] ccs;
/*    */   private final String[] bccs;
/*    */   private final String subject;
/*    */   private final String body;
/*    */   
/*    */   EmailAddressParsedResult(String to) {
/* 34 */     this(new String[] { to }, (String[])null, (String[])null, (String)null, (String)null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   EmailAddressParsedResult(String[] tos, String[] ccs, String[] bccs, String subject, String body) {
/* 42 */     super(ParsedResultType.EMAIL_ADDRESS);
/* 43 */     this.tos = tos;
/* 44 */     this.ccs = ccs;
/* 45 */     this.bccs = bccs;
/* 46 */     this.subject = subject;
/* 47 */     this.body = body;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public String getEmailAddress() {
/* 56 */     return (this.tos == null || this.tos.length == 0) ? null : this.tos[0];
/*    */   }
/*    */   
/*    */   public String[] getTos() {
/* 60 */     return this.tos;
/*    */   }
/*    */   
/*    */   public String[] getCCs() {
/* 64 */     return this.ccs;
/*    */   }
/*    */   
/*    */   public String[] getBCCs() {
/* 68 */     return this.bccs;
/*    */   }
/*    */   
/*    */   public String getSubject() {
/* 72 */     return this.subject;
/*    */   }
/*    */   
/*    */   public String getBody() {
/* 76 */     return this.body;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public String getMailtoURI() {
/* 85 */     return "mailto:";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDisplayResult() {
/* 90 */     StringBuilder result = new StringBuilder(30);
/* 91 */     maybeAppend(this.tos, result);
/* 92 */     maybeAppend(this.ccs, result);
/* 93 */     maybeAppend(this.bccs, result);
/* 94 */     maybeAppend(this.subject, result);
/* 95 */     maybeAppend(this.body, result);
/* 96 */     return result.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\EmailAddressParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */