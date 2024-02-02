/*    */ package com.google.zxing.client.result;
/*    */ 
/*    */ import com.google.zxing.Result;
/*    */ import java.util.Map;
/*    */ import java.util.regex.Pattern;
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
/*    */ public final class EmailAddressResultParser
/*    */   extends ResultParser
/*    */ {
/* 32 */   private static final Pattern COMMA = Pattern.compile(",");
/*    */ 
/*    */   
/*    */   public EmailAddressParsedResult parse(Result result) {
/*    */     String rawText;
/* 37 */     if ((rawText = getMassagedText(result)).startsWith("mailto:") || rawText.startsWith("MAILTO:")) {
/*    */       String hostEmail;
/*    */       
/*    */       int queryStart;
/* 41 */       if ((queryStart = (hostEmail = rawText.substring(7)).indexOf('?')) >= 0) {
/* 42 */         hostEmail = hostEmail.substring(0, queryStart);
/*    */       }
/*    */       try {
/* 45 */         hostEmail = urlDecode(hostEmail);
/* 46 */       } catch (IllegalArgumentException illegalArgumentException) {
/* 47 */         return null;
/*    */       } 
/* 49 */       String[] arrayOfString1 = null;
/* 50 */       if (!hostEmail.isEmpty()) {
/* 51 */         arrayOfString1 = COMMA.split(hostEmail);
/*    */       }
/* 53 */       Map<String, String> nameValues = parseNameValuePairs(rawText);
/* 54 */       String[] ccs = null;
/* 55 */       String[] bccs = null;
/* 56 */       String subject = null;
/* 57 */       String body = null;
/* 58 */       if (nameValues != null) {
/* 59 */         String tosString; if (arrayOfString1 == null && (
/*    */           
/* 61 */           tosString = nameValues.get("to")) != null) {
/* 62 */           arrayOfString1 = COMMA.split(tosString);
/*    */         }
/*    */         
/*    */         String ccString;
/* 66 */         if ((ccString = nameValues.get("cc")) != null) {
/* 67 */           ccs = COMMA.split(ccString);
/*    */         }
/*    */         String bccString;
/* 70 */         if ((bccString = nameValues.get("bcc")) != null) {
/* 71 */           bccs = COMMA.split(bccString);
/*    */         }
/* 73 */         subject = nameValues.get("subject");
/* 74 */         body = nameValues.get("body");
/*    */       } 
/* 76 */       return new EmailAddressParsedResult(arrayOfString1, ccs, bccs, subject, body);
/*    */     } 
/* 78 */     if (!EmailDoCoMoResultParser.isBasicallyValidEmailAddress(rawText)) {
/* 79 */       return null;
/*    */     }
/* 81 */     return new EmailAddressParsedResult(rawText);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\EmailAddressResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */