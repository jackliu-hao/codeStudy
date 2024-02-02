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
/*    */ 
/*    */ 
/*    */ public final class BizcardResultParser
/*    */   extends AbstractDoCoMoResultParser
/*    */ {
/*    */   public AddressBookParsedResult parse(Result result) {
/*    */     String rawText;
/* 40 */     if (!(rawText = getMassagedText(result)).startsWith("BIZCARD:")) {
/* 41 */       return null;
/*    */     }
/* 43 */     String firstName = matchSingleDoCoMoPrefixedField("N:", rawText, true);
/* 44 */     String lastName = matchSingleDoCoMoPrefixedField("X:", rawText, true);
/* 45 */     String fullName = buildName(firstName, lastName);
/* 46 */     String title = matchSingleDoCoMoPrefixedField("T:", rawText, true);
/* 47 */     String org = matchSingleDoCoMoPrefixedField("C:", rawText, true);
/* 48 */     String[] addresses = matchDoCoMoPrefixedField("A:", rawText, true);
/* 49 */     String phoneNumber1 = matchSingleDoCoMoPrefixedField("B:", rawText, true);
/* 50 */     String phoneNumber2 = matchSingleDoCoMoPrefixedField("M:", rawText, true);
/* 51 */     String phoneNumber3 = matchSingleDoCoMoPrefixedField("F:", rawText, true);
/* 52 */     String email = matchSingleDoCoMoPrefixedField("E:", rawText, true);
/*    */     
/* 54 */     return new AddressBookParsedResult(maybeWrap(fullName), null, null, 
/*    */ 
/*    */         
/* 57 */         buildPhoneNumbers(phoneNumber1, phoneNumber2, phoneNumber3), null, 
/*    */         
/* 59 */         maybeWrap(email), null, null, null, addresses, null, org, null, title, null, null);
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
/*    */   private static String[] buildPhoneNumbers(String number1, String number2, String number3) {
/* 75 */     List<String> numbers = new ArrayList<>(3);
/* 76 */     if (number1 != null) {
/* 77 */       numbers.add(number1);
/*    */     }
/* 79 */     if (number2 != null) {
/* 80 */       numbers.add(number2);
/*    */     }
/* 82 */     if (number3 != null) {
/* 83 */       numbers.add(number3);
/*    */     }
/*    */     int size;
/* 86 */     if ((size = numbers.size()) == 0) {
/* 87 */       return null;
/*    */     }
/* 89 */     return numbers.<String>toArray(new String[size]);
/*    */   }
/*    */   
/*    */   private static String buildName(String firstName, String lastName) {
/* 93 */     if (firstName == null) {
/* 94 */       return lastName;
/*    */     }
/* 96 */     return (lastName == null) ? firstName : (firstName + ' ' + lastName);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\BizcardResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */