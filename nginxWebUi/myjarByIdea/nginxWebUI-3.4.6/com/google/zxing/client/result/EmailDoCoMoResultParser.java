package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.util.regex.Pattern;

public final class EmailDoCoMoResultParser extends AbstractDoCoMoResultParser {
   private static final Pattern ATEXT_ALPHANUMERIC = Pattern.compile("[a-zA-Z0-9@.!#$%&'*+\\-/=?^_`{|}~]+");

   public EmailAddressParsedResult parse(Result result) {
      String rawText;
      if (!(rawText = getMassagedText(result)).startsWith("MATMSG:")) {
         return null;
      } else {
         String[] tos;
         if ((tos = matchDoCoMoPrefixedField("TO:", rawText, true)) == null) {
            return null;
         } else {
            String[] var4 = tos;
            int var5 = tos.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               if (!isBasicallyValidEmailAddress(var4[var6])) {
                  return null;
               }
            }

            String subject = matchSingleDoCoMoPrefixedField("SUB:", rawText, false);
            String body = matchSingleDoCoMoPrefixedField("BODY:", rawText, false);
            return new EmailAddressParsedResult(tos, (String[])null, (String[])null, subject, body);
         }
      }
   }

   static boolean isBasicallyValidEmailAddress(String email) {
      return email != null && ATEXT_ALPHANUMERIC.matcher(email).matches() && email.indexOf(64) >= 0;
   }
}
