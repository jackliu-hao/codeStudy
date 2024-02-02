package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.util.Map;
import java.util.regex.Pattern;

public final class EmailAddressResultParser extends ResultParser {
   private static final Pattern COMMA = Pattern.compile(",");

   public EmailAddressParsedResult parse(Result result) {
      String rawText;
      if (!(rawText = getMassagedText(result)).startsWith("mailto:") && !rawText.startsWith("MAILTO:")) {
         return !EmailDoCoMoResultParser.isBasicallyValidEmailAddress(rawText) ? null : new EmailAddressParsedResult(rawText);
      } else {
         String hostEmail;
         int queryStart;
         if ((queryStart = (hostEmail = rawText.substring(7)).indexOf(63)) >= 0) {
            hostEmail = hostEmail.substring(0, queryStart);
         }

         try {
            hostEmail = urlDecode(hostEmail);
         } catch (IllegalArgumentException var13) {
            return null;
         }

         String[] tos = null;
         if (!hostEmail.isEmpty()) {
            tos = COMMA.split(hostEmail);
         }

         Map<String, String> nameValues = parseNameValuePairs(rawText);
         String[] ccs = null;
         String[] bccs = null;
         String subject = null;
         String body = null;
         if (nameValues != null) {
            String ccString;
            if (tos == null && (ccString = (String)nameValues.get("to")) != null) {
               tos = COMMA.split(ccString);
            }

            if ((ccString = (String)nameValues.get("cc")) != null) {
               ccs = COMMA.split(ccString);
            }

            String bccString;
            if ((bccString = (String)nameValues.get("bcc")) != null) {
               bccs = COMMA.split(bccString);
            }

            subject = (String)nameValues.get("subject");
            body = (String)nameValues.get("body");
         }

         return new EmailAddressParsedResult(tos, ccs, bccs, subject, body);
      }
   }
}
