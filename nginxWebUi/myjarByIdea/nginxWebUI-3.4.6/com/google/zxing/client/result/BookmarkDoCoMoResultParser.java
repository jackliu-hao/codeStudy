package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class BookmarkDoCoMoResultParser extends AbstractDoCoMoResultParser {
   public URIParsedResult parse(Result result) {
      String rawText;
      if (!(rawText = result.getText()).startsWith("MEBKM:")) {
         return null;
      } else {
         String title = matchSingleDoCoMoPrefixedField("TITLE:", rawText, true);
         String[] rawUri;
         if ((rawUri = matchDoCoMoPrefixedField("URL:", rawText, true)) == null) {
            return null;
         } else {
            String uri;
            return URIResultParser.isBasicallyValidURI(uri = rawUri[0]) ? new URIParsedResult(uri, title) : null;
         }
      }
   }
}
