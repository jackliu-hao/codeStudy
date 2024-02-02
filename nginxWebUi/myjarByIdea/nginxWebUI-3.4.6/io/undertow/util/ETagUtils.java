package io.undertow.util;

import io.undertow.server.HttpServerExchange;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ETagUtils {
   private static final char COMMA = ',';
   private static final char QUOTE = '"';
   private static final char W = 'W';
   private static final char SLASH = '/';

   public static boolean handleIfMatch(HttpServerExchange exchange, ETag etag, boolean allowWeak) {
      return handleIfMatch(exchange, Collections.singletonList(etag), allowWeak);
   }

   public static boolean handleIfMatch(HttpServerExchange exchange, List<ETag> etags, boolean allowWeak) {
      return handleIfMatch(exchange.getRequestHeaders().getFirst(Headers.IF_MATCH), etags, allowWeak);
   }

   public static boolean handleIfMatch(String ifMatch, ETag etag, boolean allowWeak) {
      return handleIfMatch(ifMatch, Collections.singletonList(etag), allowWeak);
   }

   public static boolean handleIfMatch(String ifMatch, List<ETag> etags, boolean allowWeak) {
      if (ifMatch == null) {
         return true;
      } else if (ifMatch.equals("*")) {
         return true;
      } else {
         List<ETag> parts = parseETagList(ifMatch);
         Iterator var4 = parts.iterator();

         label52:
         while(true) {
            ETag part;
            do {
               if (!var4.hasNext()) {
                  return false;
               }

               part = (ETag)var4.next();
            } while(part.isWeak() && !allowWeak);

            Iterator var6 = etags.iterator();

            ETag tag;
            do {
               do {
                  do {
                     if (!var6.hasNext()) {
                        continue label52;
                     }

                     tag = (ETag)var6.next();
                  } while(tag == null);
               } while(tag.isWeak() && !allowWeak);
            } while(!tag.getTag().equals(part.getTag()));

            return true;
         }
      }
   }

   public static boolean handleIfNoneMatch(HttpServerExchange exchange, ETag etag, boolean allowWeak) {
      return handleIfNoneMatch(exchange, Collections.singletonList(etag), allowWeak);
   }

   public static boolean handleIfNoneMatch(HttpServerExchange exchange, List<ETag> etags, boolean allowWeak) {
      return handleIfNoneMatch(exchange.getRequestHeaders().getFirst(Headers.IF_NONE_MATCH), etags, allowWeak);
   }

   public static boolean handleIfNoneMatch(String ifNoneMatch, ETag etag, boolean allowWeak) {
      return handleIfNoneMatch(ifNoneMatch, Collections.singletonList(etag), allowWeak);
   }

   public static boolean handleIfNoneMatch(String ifNoneMatch, List<ETag> etags, boolean allowWeak) {
      if (ifNoneMatch == null) {
         return true;
      } else {
         List<ETag> parts = parseETagList(ifNoneMatch);
         Iterator var4 = parts.iterator();

         label52:
         while(true) {
            ETag part;
            do {
               if (!var4.hasNext()) {
                  return true;
               }

               part = (ETag)var4.next();
               if (part.getTag().equals("*")) {
                  return false;
               }
            } while(part.isWeak() && !allowWeak);

            Iterator var6 = etags.iterator();

            ETag tag;
            do {
               do {
                  do {
                     if (!var6.hasNext()) {
                        continue label52;
                     }

                     tag = (ETag)var6.next();
                  } while(tag == null);
               } while(tag.isWeak() && !allowWeak);
            } while(!tag.getTag().equals(part.getTag()));

            return false;
         }
      }
   }

   public static List<ETag> parseETagList(String header) {
      char[] headerChars = header.toCharArray();
      List<ETag> response = new ArrayList();
      SearchingFor searchingFor = ETagUtils.SearchingFor.START_OF_VALUE;
      int valueStart = 0;
      boolean weak = false;
      boolean malformed = false;

      for(int i = 0; i < headerChars.length; ++i) {
         String value;
         switch (searchingFor) {
            case START_OF_VALUE:
               if (headerChars[i] != ',' && !Character.isWhitespace(headerChars[i])) {
                  if (headerChars[i] == '"') {
                     valueStart = i + 1;
                     searchingFor = ETagUtils.SearchingFor.LAST_QUOTE;
                     weak = false;
                     malformed = false;
                  } else if (headerChars[i] == 'W') {
                     searchingFor = ETagUtils.SearchingFor.WEAK_SLASH;
                  }
               }
               break;
            case WEAK_SLASH:
               if (headerChars[i] == '"') {
                  valueStart = i + 1;
                  searchingFor = ETagUtils.SearchingFor.LAST_QUOTE;
                  weak = true;
                  malformed = false;
               } else if (headerChars[i] != '/') {
                  malformed = true;
                  searchingFor = ETagUtils.SearchingFor.END_OF_VALUE;
               }
               break;
            case LAST_QUOTE:
               if (headerChars[i] == '"') {
                  value = String.valueOf(headerChars, valueStart, i - valueStart);
                  response.add(new ETag(weak, value.trim()));
                  searchingFor = ETagUtils.SearchingFor.START_OF_VALUE;
               }
               break;
            case END_OF_VALUE:
               if ((headerChars[i] == ',' || Character.isWhitespace(headerChars[i])) && !malformed) {
                  value = String.valueOf(headerChars, valueStart, i - valueStart);
                  response.add(new ETag(weak, value.trim()));
                  searchingFor = ETagUtils.SearchingFor.START_OF_VALUE;
               }
         }
      }

      if ((searchingFor == ETagUtils.SearchingFor.END_OF_VALUE || searchingFor == ETagUtils.SearchingFor.LAST_QUOTE) && !malformed) {
         String value = String.valueOf(headerChars, valueStart, headerChars.length - valueStart);
         response.add(new ETag(weak, value.trim()));
      }

      return response;
   }

   public static ETag getETag(HttpServerExchange exchange) {
      String tag = exchange.getResponseHeaders().getFirst(Headers.ETAG);
      if (tag == null) {
         return null;
      } else {
         char[] headerChars = tag.toCharArray();
         SearchingFor searchingFor = ETagUtils.SearchingFor.START_OF_VALUE;
         int valueStart = 0;
         boolean weak = false;
         boolean malformed = false;

         for(int i = 0; i < headerChars.length; ++i) {
            String value;
            switch (searchingFor) {
               case START_OF_VALUE:
                  if (headerChars[i] != ',' && !Character.isWhitespace(headerChars[i])) {
                     if (headerChars[i] == '"') {
                        valueStart = i + 1;
                        searchingFor = ETagUtils.SearchingFor.LAST_QUOTE;
                        weak = false;
                        malformed = false;
                     } else if (headerChars[i] == 'W') {
                        searchingFor = ETagUtils.SearchingFor.WEAK_SLASH;
                     }
                  }
                  break;
               case WEAK_SLASH:
                  if (headerChars[i] == '"') {
                     valueStart = i + 1;
                     searchingFor = ETagUtils.SearchingFor.LAST_QUOTE;
                     weak = true;
                     malformed = false;
                  } else if (headerChars[i] != '/') {
                     return null;
                  }
                  break;
               case LAST_QUOTE:
                  if (headerChars[i] == '"') {
                     value = String.valueOf(headerChars, valueStart, i - valueStart);
                     return new ETag(weak, value.trim());
                  }
                  break;
               case END_OF_VALUE:
                  if ((headerChars[i] == ',' || Character.isWhitespace(headerChars[i])) && !malformed) {
                     value = String.valueOf(headerChars, valueStart, i - valueStart);
                     return new ETag(weak, value.trim());
                  }
            }
         }

         if ((searchingFor == ETagUtils.SearchingFor.END_OF_VALUE || searchingFor == ETagUtils.SearchingFor.LAST_QUOTE) && !malformed) {
            String value = String.valueOf(headerChars, valueStart, headerChars.length - valueStart);
            return new ETag(weak, value.trim());
         } else {
            return null;
         }
      }
   }

   static enum SearchingFor {
      START_OF_VALUE,
      LAST_QUOTE,
      END_OF_VALUE,
      WEAK_SLASH;
   }
}
