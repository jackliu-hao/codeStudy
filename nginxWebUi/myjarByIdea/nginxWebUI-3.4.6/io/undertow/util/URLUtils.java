package io.undertow.util;

import io.undertow.UndertowMessages;
import io.undertow.server.HttpServerExchange;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class URLUtils {
   private static final char PATH_SEPARATOR = '/';
   private static final QueryStringParser QUERY_STRING_PARSER = new QueryStringParser('&', false) {
      void handle(HttpServerExchange exchange, String key, String value) {
         exchange.addQueryParam(key, value);
      }
   };
   private static final QueryStringParser PATH_PARAM_PARSER = new QueryStringParser(';', true) {
      void handle(HttpServerExchange exchange, String key, String value) {
         exchange.addPathParam(key, value);
      }
   };
   private static final Pattern SCHEME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+-.]*:.*");

   private URLUtils() {
   }

   public static void parseQueryString(String string, HttpServerExchange exchange, String charset, boolean doDecode, int maxParameters) throws ParameterLimitException {
      QUERY_STRING_PARSER.parse(string, exchange, charset, doDecode, maxParameters);
   }

   /** @deprecated */
   @Deprecated
   public static void parsePathParms(String string, HttpServerExchange exchange, String charset, boolean doDecode, int maxParameters) throws ParameterLimitException {
      parsePathParams(string, exchange, charset, doDecode, maxParameters);
   }

   public static int parsePathParams(String string, HttpServerExchange exchange, String charset, boolean doDecode, int maxParameters) throws ParameterLimitException {
      return PATH_PARAM_PARSER.parse(string, exchange, charset, doDecode, maxParameters);
   }

   public static String decode(String s, String enc, boolean decodeSlash, StringBuilder buffer) {
      return decode(s, enc, decodeSlash, true, buffer);
   }

   public static String decode(String s, String enc, boolean decodeSlash, boolean formEncoding, StringBuilder buffer) {
      buffer.setLength(0);
      boolean needToChange = false;
      int numChars = s.length();
      int i = 0;

      while(i < numChars) {
         char c = s.charAt(i);
         if (c == '+') {
            if (formEncoding) {
               buffer.append(' ');
               ++i;
               needToChange = true;
            } else {
               ++i;
               buffer.append(c);
            }
         } else {
            if (c == '%' || c > 127) {
               try {
                  byte[] bytes = new byte[numChars - i + 1];
                  int pos = 0;

                  while(true) {
                     if (i >= numChars) {
                        String decoded = new String(bytes, 0, pos, enc);
                        buffer.append(decoded);
                        break;
                     }

                     if (c == '%') {
                        if (i + 2 >= s.length()) {
                           throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, (Exception)null);
                        }

                        char p1 = Character.toLowerCase(s.charAt(i + 1));
                        char p2 = Character.toLowerCase(s.charAt(i + 2));
                        if (!decodeSlash && (p1 == '2' && p2 == 'f' || p1 == '5' && p2 == 'c')) {
                           if (pos + 2 >= bytes.length) {
                              bytes = expandBytes(bytes);
                           }

                           bytes[pos++] = (byte)c;
                           bytes[pos++] = (byte)s.charAt(i + 1);
                           bytes[pos++] = (byte)s.charAt(i + 2);
                           i += 3;
                           if (i < numChars) {
                              c = s.charAt(i);
                           }
                        } else {
                           int v = false;
                           int v;
                           if (p1 >= '0' && p1 <= '9') {
                              v = p1 - 48 << 4;
                           } else {
                              if (p1 < 'a' || p1 > 'f') {
                                 throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, (Exception)null);
                              }

                              v = p1 - 97 + 10 << 4;
                           }

                           if (p2 >= '0' && p2 <= '9') {
                              v += p2 - 48;
                           } else {
                              if (p2 < 'a' || p2 > 'f') {
                                 throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, (Exception)null);
                              }

                              v += p2 - 97 + 10;
                           }

                           if (v < 0) {
                              throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, (Exception)null);
                           }

                           if (pos == bytes.length) {
                              bytes = expandBytes(bytes);
                           }

                           bytes[pos++] = (byte)v;
                           i += 3;
                           if (i < numChars) {
                              c = s.charAt(i);
                           }
                        }
                     } else if (c == '+' && formEncoding) {
                        if (pos == bytes.length) {
                           bytes = expandBytes(bytes);
                        }

                        bytes[pos++] = 32;
                        ++i;
                        if (i < numChars) {
                           c = s.charAt(i);
                        }
                     } else {
                        if (pos == bytes.length) {
                           bytes = expandBytes(bytes);
                        }

                        ++i;
                        if (c >> 8 != 0) {
                           bytes[pos++] = (byte)(c >> 8);
                           if (pos == bytes.length) {
                              bytes = expandBytes(bytes);
                           }

                           bytes[pos++] = (byte)c;
                        } else {
                           bytes[pos++] = (byte)c;
                        }

                        if (i < numChars) {
                           c = s.charAt(i);
                        }
                     }
                  }
               } catch (NumberFormatException var14) {
                  throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, var14);
               } catch (UnsupportedEncodingException var15) {
                  throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, var15);
               }

               needToChange = true;
               break;
            }

            buffer.append(c);
            ++i;
         }
      }

      return needToChange ? buffer.toString() : s;
   }

   private static byte[] expandBytes(byte[] bytes) {
      byte[] newBytes = new byte[bytes.length + 10];
      System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
      return newBytes;
   }

   public static String normalizeSlashes(String path) {
      StringBuilder builder = new StringBuilder(path);

      boolean modified;
      for(modified = false; builder.length() > 0 && builder.length() != 1 && '/' == builder.charAt(builder.length() - 1); modified = true) {
         builder.deleteCharAt(builder.length() - 1);
      }

      if (builder.length() == 0 || '/' != builder.charAt(0)) {
         builder.insert(0, '/');
         modified = true;
      }

      return modified ? builder.toString() : path;
   }

   public static boolean isAbsoluteUrl(String location) {
      return location != null && location.length() > 0 && location.contains(":") ? SCHEME_PATTERN.matcher(location).matches() : false;
   }

   private abstract static class QueryStringParser {
      private final char separator;
      private final boolean parseUntilSeparator;

      QueryStringParser(char separator, boolean parseUntilSeparator) {
         this.separator = separator;
         this.parseUntilSeparator = parseUntilSeparator;
      }

      int parse(String string, HttpServerExchange exchange, String charset, boolean doDecode, int max) throws ParameterLimitException {
         int count = 0;
         int i = false;

         try {
            int stringStart = 0;
            String attrName = null;

            int i;
            for(i = 0; i < string.length(); ++i) {
               char c = string.charAt(i);
               if (c == '=' && attrName == null) {
                  attrName = string.substring(stringStart, i);
                  stringStart = i + 1;
               } else if (c == this.separator) {
                  if (attrName != null) {
                     this.handle(exchange, this.decode(charset, attrName, doDecode), this.decode(charset, string.substring(stringStart, i), doDecode));
                     ++count;
                     if (count > max) {
                        throw UndertowMessages.MESSAGES.tooManyParameters(max);
                     }
                  } else if (stringStart != i) {
                     this.handle(exchange, this.decode(charset, string.substring(stringStart, i), doDecode), "");
                     ++count;
                     if (count > max) {
                        throw UndertowMessages.MESSAGES.tooManyParameters(max);
                     }
                  }

                  stringStart = i + 1;
                  attrName = null;
               } else if (this.parseUntilSeparator && (c == '?' || c == '/')) {
                  break;
               }
            }

            if (attrName != null) {
               this.handle(exchange, this.decode(charset, attrName, doDecode), this.decode(charset, string.substring(stringStart, i), doDecode));
               ++count;
               if (count > max) {
                  throw UndertowMessages.MESSAGES.tooManyParameters(max);
               }
            } else if (string.length() != stringStart) {
               this.handle(exchange, this.decode(charset, string.substring(stringStart, i), doDecode), "");
               ++count;
               if (count > max) {
                  throw UndertowMessages.MESSAGES.tooManyParameters(max);
               }
            }

            return i;
         } catch (UnsupportedEncodingException var11) {
            throw new RuntimeException(var11);
         }
      }

      private String decode(String charset, String attrName, boolean doDecode) throws UnsupportedEncodingException {
         return doDecode ? URLUtils.decode(attrName, charset, true, true, new StringBuilder()) : attrName;
      }

      abstract void handle(HttpServerExchange var1, String var2, String var3);
   }
}
