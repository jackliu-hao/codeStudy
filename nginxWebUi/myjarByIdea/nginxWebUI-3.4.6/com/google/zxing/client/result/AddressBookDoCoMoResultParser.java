package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class AddressBookDoCoMoResultParser extends AbstractDoCoMoResultParser {
   public AddressBookParsedResult parse(Result result) {
      String rawText;
      if (!(rawText = getMassagedText(result)).startsWith("MECARD:")) {
         return null;
      } else {
         String[] rawName;
         if ((rawName = matchDoCoMoPrefixedField("N:", rawText, true)) == null) {
            return null;
         } else {
            String name = parseName(rawName[0]);
            String pronunciation = matchSingleDoCoMoPrefixedField("SOUND:", rawText, true);
            String[] phoneNumbers = matchDoCoMoPrefixedField("TEL:", rawText, true);
            String[] emails = matchDoCoMoPrefixedField("EMAIL:", rawText, true);
            String note = matchSingleDoCoMoPrefixedField("NOTE:", rawText, false);
            String[] addresses = matchDoCoMoPrefixedField("ADR:", rawText, true);
            String birthday;
            if (!isStringOfDigits(birthday = matchSingleDoCoMoPrefixedField("BDAY:", rawText, true), 8)) {
               birthday = null;
            }

            String[] urls = matchDoCoMoPrefixedField("URL:", rawText, true);
            String org = matchSingleDoCoMoPrefixedField("ORG:", rawText, true);
            return new AddressBookParsedResult(maybeWrap(name), (String[])null, pronunciation, phoneNumbers, (String[])null, emails, (String[])null, (String)null, note, addresses, (String[])null, org, birthday, (String)null, urls, (String[])null);
         }
      }
   }

   private static String parseName(String name) {
      int comma;
      return (comma = name.indexOf(44)) >= 0 ? name.substring(comma + 1) + ' ' + name.substring(0, comma) : name;
   }
}
