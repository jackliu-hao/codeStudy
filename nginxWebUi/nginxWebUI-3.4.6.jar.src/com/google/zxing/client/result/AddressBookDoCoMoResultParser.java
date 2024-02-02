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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class AddressBookDoCoMoResultParser
/*    */   extends AbstractDoCoMoResultParser
/*    */ {
/*    */   public AddressBookParsedResult parse(Result result) {
/*    */     String rawText;
/* 41 */     if (!(rawText = getMassagedText(result)).startsWith("MECARD:")) {
/* 42 */       return null;
/*    */     }
/*    */     String[] rawName;
/* 45 */     if ((rawName = matchDoCoMoPrefixedField("N:", rawText, true)) == null) {
/* 46 */       return null;
/*    */     }
/* 48 */     String name = parseName(rawName[0]);
/* 49 */     String pronunciation = matchSingleDoCoMoPrefixedField("SOUND:", rawText, true);
/* 50 */     String[] phoneNumbers = matchDoCoMoPrefixedField("TEL:", rawText, true);
/* 51 */     String[] emails = matchDoCoMoPrefixedField("EMAIL:", rawText, true);
/* 52 */     String note = matchSingleDoCoMoPrefixedField("NOTE:", rawText, false);
/* 53 */     String[] addresses = matchDoCoMoPrefixedField("ADR:", rawText, true);
/*    */     String birthday;
/* 55 */     if (!isStringOfDigits(birthday = matchSingleDoCoMoPrefixedField("BDAY:", rawText, true), 8))
/*    */     {
/* 57 */       birthday = null;
/*    */     }
/* 59 */     String[] urls = matchDoCoMoPrefixedField("URL:", rawText, true);
/*    */ 
/*    */ 
/*    */     
/* 63 */     String org = matchSingleDoCoMoPrefixedField("ORG:", rawText, true);
/*    */     
/* 65 */     return new AddressBookParsedResult(maybeWrap(name), null, pronunciation, phoneNumbers, null, emails, null, null, note, addresses, null, org, birthday, null, urls, null);
/*    */   }
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
/*    */   private static String parseName(String name) {
/*    */     int comma;
/* 85 */     if ((comma = name.indexOf(',')) >= 0)
/*    */     {
/* 87 */       return name.substring(comma + 1) + ' ' + name.substring(0, comma);
/*    */     }
/* 89 */     return name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\AddressBookDoCoMoResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */