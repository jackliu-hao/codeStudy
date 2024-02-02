package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class URIResultParser extends ResultParser {
   private static final Pattern URL_WITH_PROTOCOL_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9+-.]+:");
   private static final Pattern URL_WITHOUT_PROTOCOL_PATTERN = Pattern.compile("([a-zA-Z0-9\\-]+\\.){1,6}[a-zA-Z]{2,}(:\\d{1,5})?(/|\\?|$)");

   public URIParsedResult parse(Result result) {
      String rawText;
      if (!(rawText = getMassagedText(result)).startsWith("URL:") && !rawText.startsWith("URI:")) {
         return isBasicallyValidURI(rawText = rawText.trim()) ? new URIParsedResult(rawText, (String)null) : null;
      } else {
         return new URIParsedResult(rawText.substring(4).trim(), (String)null);
      }
   }

   static boolean isBasicallyValidURI(String uri) {
      if (uri.contains(" ")) {
         return false;
      } else {
         Matcher m;
         if ((m = URL_WITH_PROTOCOL_PATTERN.matcher(uri)).find() && m.start() == 0) {
            return true;
         } else {
            return (m = URL_WITHOUT_PROTOCOL_PATTERN.matcher(uri)).find() && m.start() == 0;
         }
      }
   }
}
