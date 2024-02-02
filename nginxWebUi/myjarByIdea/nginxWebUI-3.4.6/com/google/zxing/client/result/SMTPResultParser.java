package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class SMTPResultParser extends ResultParser {
   public EmailAddressParsedResult parse(Result result) {
      String rawText;
      if (!(rawText = getMassagedText(result)).startsWith("smtp:") && !rawText.startsWith("SMTP:")) {
         return null;
      } else {
         String emailAddress = rawText.substring(5);
         String subject = null;
         String body = null;
         int colon;
         if ((colon = emailAddress.indexOf(58)) >= 0) {
            subject = emailAddress.substring(colon + 1);
            emailAddress = emailAddress.substring(0, colon);
            if ((colon = subject.indexOf(58)) >= 0) {
               body = subject.substring(colon + 1);
               subject = subject.substring(0, colon);
            }
         }

         return new EmailAddressParsedResult(new String[]{emailAddress}, (String[])null, (String[])null, subject, body);
      }
   }
}
