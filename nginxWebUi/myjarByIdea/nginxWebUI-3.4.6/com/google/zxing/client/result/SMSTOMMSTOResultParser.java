package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class SMSTOMMSTOResultParser extends ResultParser {
   public SMSParsedResult parse(Result result) {
      String rawText;
      if (!(rawText = getMassagedText(result)).startsWith("smsto:") && !rawText.startsWith("SMSTO:") && !rawText.startsWith("mmsto:") && !rawText.startsWith("MMSTO:")) {
         return null;
      } else {
         String number = rawText.substring(6);
         String body = null;
         int bodyStart;
         if ((bodyStart = number.indexOf(58)) >= 0) {
            body = number.substring(bodyStart + 1);
            number = number.substring(0, bodyStart);
         }

         return new SMSParsedResult(number, (String)null, (String)null, body);
      }
   }
}
