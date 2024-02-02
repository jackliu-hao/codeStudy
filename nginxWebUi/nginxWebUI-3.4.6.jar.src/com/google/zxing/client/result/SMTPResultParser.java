/*    */ package com.google.zxing.client.result;
/*    */ 
/*    */ import com.google.zxing.Result;
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
/*    */ 
/*    */ public final class SMTPResultParser
/*    */   extends ResultParser
/*    */ {
/*    */   public EmailAddressParsedResult parse(Result result) {
/*    */     String rawText;
/* 32 */     if (!(rawText = getMassagedText(result)).startsWith("smtp:") && !rawText.startsWith("SMTP:")) {
/* 33 */       return null;
/*    */     }
/* 35 */     String emailAddress = rawText.substring(5);
/* 36 */     String subject = null;
/* 37 */     String body = null;
/*    */ 
/*    */     
/* 40 */     subject = emailAddress.substring(colon + 1);
/* 41 */     emailAddress = emailAddress.substring(0, colon);
/*    */     int colon;
/* 43 */     if ((colon = emailAddress.indexOf(':')) >= 0 && (colon = subject.indexOf(':')) >= 0) {
/* 44 */       body = subject.substring(colon + 1);
/* 45 */       subject = subject.substring(0, colon);
/*    */     } 
/*    */     
/* 48 */     return new EmailAddressParsedResult(new String[] { emailAddress }, null, null, subject, body);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\SMTPResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */