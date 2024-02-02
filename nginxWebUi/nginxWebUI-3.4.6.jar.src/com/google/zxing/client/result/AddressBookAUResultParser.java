/*    */ package com.google.zxing.client.result;
/*    */ 
/*    */ import com.google.zxing.Result;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public final class AddressBookAUResultParser
/*    */   extends ResultParser
/*    */ {
/*    */   public AddressBookParsedResult parse(Result result) {
/*    */     String rawText;
/* 38 */     if (!(rawText = getMassagedText(result)).contains("MEMORY") || !rawText.contains("\r\n")) {
/* 39 */       return null;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 44 */     String name = matchSinglePrefixedField("NAME1:", rawText, '\r', true);
/* 45 */     String pronunciation = matchSinglePrefixedField("NAME2:", rawText, '\r', true);
/*    */     
/* 47 */     String[] phoneNumbers = matchMultipleValuePrefix("TEL", 3, rawText, true);
/* 48 */     String[] emails = matchMultipleValuePrefix("MAIL", 3, rawText, true);
/* 49 */     String note = matchSinglePrefixedField("MEMORY:", rawText, '\r', false);
/*    */     
/* 51 */     (new String[1])[0] = address; String address, addresses[] = ((address = matchSinglePrefixedField("ADD:", rawText, '\r', true)) == null) ? null : new String[1];
/* 52 */     return new AddressBookParsedResult(maybeWrap(name), null, pronunciation, phoneNumbers, null, emails, null, null, note, addresses, null, null, null, null, null, null);
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
/*    */ 
/*    */ 
/*    */   
/*    */   private static String[] matchMultipleValuePrefix(String prefix, int max, String rawText, boolean trim) {
/* 74 */     List<String> values = null; String value;
/* 75 */     for (int i = 1; i <= max && (
/*    */       
/* 77 */       value = matchSinglePrefixedField(prefix + i + ':', rawText, '\r', trim)) != null; i++) {
/*    */ 
/*    */       
/* 80 */       if (values == null) {
/* 81 */         values = new ArrayList<>(max);
/*    */       }
/* 83 */       values.add(value);
/*    */     } 
/* 85 */     if (values == null) {
/* 86 */       return null;
/*    */     }
/* 88 */     return values.<String>toArray(new String[values.size()]);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\AddressBookAUResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */