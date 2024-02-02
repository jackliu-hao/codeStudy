package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class WifiResultParser extends ResultParser {
   public WifiParsedResult parse(Result result) {
      String rawText;
      if (!(rawText = getMassagedText(result)).startsWith("WIFI:")) {
         return null;
      } else {
         String ssid;
         if ((ssid = matchSinglePrefixedField("S:", rawText, ';', false)) != null && !ssid.isEmpty()) {
            String pass = matchSinglePrefixedField("P:", rawText, ';', false);
            String type;
            if ((type = matchSinglePrefixedField("T:", rawText, ';', false)) == null) {
               type = "nopass";
            }

            boolean hidden = Boolean.parseBoolean(matchSinglePrefixedField("H:", rawText, ';', false));
            return new WifiParsedResult(type, ssid, pass, hidden);
         } else {
            return null;
         }
      }
   }
}
