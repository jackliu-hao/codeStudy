package com.google.zxing.client.result;

import java.util.regex.Pattern;

public final class URIParsedResult extends ParsedResult {
   private static final Pattern USER_IN_HOST = Pattern.compile(":/*([^/@]+)@[^/]+");
   private final String uri;
   private final String title;

   public URIParsedResult(String uri, String title) {
      super(ParsedResultType.URI);
      this.uri = massageURI(uri);
      this.title = title;
   }

   public String getURI() {
      return this.uri;
   }

   public String getTitle() {
      return this.title;
   }

   public boolean isPossiblyMaliciousURI() {
      return USER_IN_HOST.matcher(this.uri).find();
   }

   public String getDisplayResult() {
      StringBuilder result = new StringBuilder(30);
      maybeAppend(this.title, result);
      maybeAppend(this.uri, result);
      return result.toString();
   }

   private static String massageURI(String uri) {
      int protocolEnd;
      if ((protocolEnd = (uri = uri.trim()).indexOf(58)) < 0 || isColonFollowedByPortNumber(uri, protocolEnd)) {
         uri = "http://" + uri;
      }

      return uri;
   }

   private static boolean isColonFollowedByPortNumber(String uri, int protocolEnd) {
      int start = protocolEnd + 1;
      int nextSlash;
      if ((nextSlash = uri.indexOf(47, start)) < 0) {
         nextSlash = uri.length();
      }

      return ResultParser.isSubstringOfDigits(uri, start, nextSlash - start);
   }
}
