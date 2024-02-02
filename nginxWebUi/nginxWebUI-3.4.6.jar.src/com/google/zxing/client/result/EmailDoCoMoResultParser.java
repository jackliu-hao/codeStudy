/*    */ package com.google.zxing.client.result;
/*    */ 
/*    */ import com.google.zxing.Result;
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
/*    */ 
/*    */ public final class EmailDoCoMoResultParser
/*    */   extends AbstractDoCoMoResultParser
/*    */ {
/* 32 */   private static final Pattern ATEXT_ALPHANUMERIC = Pattern.compile("[a-zA-Z0-9@.!#$%&'*+\\-/=?^_`{|}~]+");
/*    */ 
/*    */   
/*    */   public EmailAddressParsedResult parse(Result result) {
/*    */     String rawText;
/* 37 */     if (!(rawText = getMassagedText(result)).startsWith("MATMSG:")) {
/* 38 */       return null;
/*    */     }
/*    */     String[] tos;
/* 41 */     if ((tos = matchDoCoMoPrefixedField("TO:", rawText, true)) == null)
/* 42 */       return null;  String[] arrayOfString1; int i;
/*    */     byte b;
/* 44 */     for (i = (arrayOfString1 = tos).length, b = 0; b < i; b++) {
/* 45 */       if (!isBasicallyValidEmailAddress(arrayOfString1[b])) {
/* 46 */         return null;
/*    */       }
/*    */     } 
/* 49 */     String subject = matchSingleDoCoMoPrefixedField("SUB:", rawText, false);
/* 50 */     String body = matchSingleDoCoMoPrefixedField("BODY:", rawText, false);
/* 51 */     return new EmailAddressParsedResult(tos, null, null, subject, body);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static boolean isBasicallyValidEmailAddress(String email) {
/* 61 */     return (email != null && ATEXT_ALPHANUMERIC.matcher(email).matches() && email.indexOf('@') >= 0);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\EmailDoCoMoResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */