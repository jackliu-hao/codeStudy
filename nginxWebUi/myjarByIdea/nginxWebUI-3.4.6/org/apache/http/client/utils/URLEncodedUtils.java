package org.apache.http.client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.message.TokenParser;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class URLEncodedUtils {
   public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
   private static final char QP_SEP_A = '&';
   private static final char QP_SEP_S = ';';
   private static final String NAME_VALUE_SEPARATOR = "=";
   private static final char PATH_SEPARATOR = '/';
   private static final BitSet PATH_SEPARATORS = new BitSet(256);
   private static final BitSet UNRESERVED;
   private static final BitSet PUNCT;
   private static final BitSet USERINFO;
   private static final BitSet PATHSAFE;
   private static final BitSet URIC;
   private static final BitSet RESERVED;
   private static final BitSet URLENCODER;
   private static final BitSet PATH_SPECIAL;
   private static final int RADIX = 16;

   /** @deprecated */
   @Deprecated
   public static List<NameValuePair> parse(URI uri, String charsetName) {
      return parse(uri, charsetName != null ? Charset.forName(charsetName) : null);
   }

   public static List<NameValuePair> parse(URI uri, Charset charset) {
      Args.notNull(uri, "URI");
      String query = uri.getRawQuery();
      return query != null && !query.isEmpty() ? parse(query, charset) : createEmptyList();
   }

   public static List<NameValuePair> parse(HttpEntity entity) throws IOException {
      Args.notNull(entity, "HTTP entity");
      ContentType contentType = ContentType.get(entity);
      if (contentType != null && contentType.getMimeType().equalsIgnoreCase("application/x-www-form-urlencoded")) {
         long len = entity.getContentLength();
         Args.check(len <= 2147483647L, "HTTP entity is too large");
         Charset charset = contentType.getCharset() != null ? contentType.getCharset() : HTTP.DEF_CONTENT_CHARSET;
         InputStream inStream = entity.getContent();
         if (inStream == null) {
            return createEmptyList();
         } else {
            CharArrayBuffer buf;
            try {
               buf = new CharArrayBuffer(len > 0L ? (int)len : 1024);
               Reader reader = new InputStreamReader(inStream, charset);
               char[] tmp = new char[1024];

               int l;
               while((l = reader.read(tmp)) != -1) {
                  buf.append((char[])tmp, 0, l);
               }
            } finally {
               inStream.close();
            }

            return buf.isEmpty() ? createEmptyList() : parse(buf, charset, '&');
         }
      } else {
         return createEmptyList();
      }
   }

   public static boolean isEncoded(HttpEntity entity) {
      Args.notNull(entity, "HTTP entity");
      Header h = entity.getContentType();
      if (h != null) {
         HeaderElement[] elems = h.getElements();
         if (elems.length > 0) {
            String contentType = elems[0].getName();
            return contentType.equalsIgnoreCase("application/x-www-form-urlencoded");
         }
      }

      return false;
   }

   /** @deprecated */
   @Deprecated
   public static void parse(List<NameValuePair> parameters, Scanner scanner, String charset) {
      parse(parameters, scanner, "[&;]", charset);
   }

   /** @deprecated */
   @Deprecated
   public static void parse(List<NameValuePair> parameters, Scanner scanner, String parameterSepartorPattern, String charset) {
      scanner.useDelimiter(parameterSepartorPattern);

      String name;
      String value;
      for(; scanner.hasNext(); parameters.add(new BasicNameValuePair(name, value))) {
         String token = scanner.next();
         int i = token.indexOf("=");
         if (i != -1) {
            name = decodeFormFields(token.substring(0, i).trim(), charset);
            value = decodeFormFields(token.substring(i + 1).trim(), charset);
         } else {
            name = decodeFormFields(token.trim(), charset);
            value = null;
         }
      }

   }

   public static List<NameValuePair> parse(String s, Charset charset) {
      if (s == null) {
         return createEmptyList();
      } else {
         CharArrayBuffer buffer = new CharArrayBuffer(s.length());
         buffer.append(s);
         return parse(buffer, charset, '&', ';');
      }
   }

   public static List<NameValuePair> parse(String s, Charset charset, char... separators) {
      if (s == null) {
         return createEmptyList();
      } else {
         CharArrayBuffer buffer = new CharArrayBuffer(s.length());
         buffer.append(s);
         return parse(buffer, charset, separators);
      }
   }

   public static List<NameValuePair> parse(CharArrayBuffer buf, Charset charset, char... separators) {
      Args.notNull(buf, "Char array buffer");
      TokenParser tokenParser = TokenParser.INSTANCE;
      BitSet delimSet = new BitSet();
      char[] arr$ = separators;
      int len$ = separators.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         char separator = arr$[i$];
         delimSet.set(separator);
      }

      ParserCursor cursor = new ParserCursor(0, buf.length());
      List<NameValuePair> list = new ArrayList();

      while(!cursor.atEnd()) {
         delimSet.set(61);
         String name = tokenParser.parseToken(buf, cursor, delimSet);
         String value = null;
         if (!cursor.atEnd()) {
            int delim = buf.charAt(cursor.getPos());
            cursor.updatePos(cursor.getPos() + 1);
            if (delim == '=') {
               delimSet.clear(61);
               value = tokenParser.parseToken(buf, cursor, delimSet);
               if (!cursor.atEnd()) {
                  cursor.updatePos(cursor.getPos() + 1);
               }
            }
         }

         if (!name.isEmpty()) {
            list.add(new BasicNameValuePair(decodeFormFields(name, charset), decodeFormFields(value, charset)));
         }
      }

      return list;
   }

   static List<String> splitSegments(CharSequence s, BitSet separators) {
      ParserCursor cursor = new ParserCursor(0, s.length());
      if (cursor.atEnd()) {
         return Collections.emptyList();
      } else {
         if (separators.get(s.charAt(cursor.getPos()))) {
            cursor.updatePos(cursor.getPos() + 1);
         }

         List<String> list = new ArrayList();

         StringBuilder buf;
         for(buf = new StringBuilder(); !cursor.atEnd(); cursor.updatePos(cursor.getPos() + 1)) {
            char current = s.charAt(cursor.getPos());
            if (separators.get(current)) {
               list.add(buf.toString());
               buf.setLength(0);
            } else {
               buf.append(current);
            }
         }

         list.add(buf.toString());
         return list;
      }
   }

   static List<String> splitPathSegments(CharSequence s) {
      return splitSegments(s, PATH_SEPARATORS);
   }

   public static List<String> parsePathSegments(CharSequence s, Charset charset) {
      Args.notNull(s, "Char sequence");
      List<String> list = splitPathSegments(s);

      for(int i = 0; i < list.size(); ++i) {
         list.set(i, urlDecode((String)list.get(i), charset != null ? charset : Consts.UTF_8, false));
      }

      return list;
   }

   public static List<String> parsePathSegments(CharSequence s) {
      return parsePathSegments(s, Consts.UTF_8);
   }

   public static String formatSegments(Iterable<String> segments, Charset charset) {
      Args.notNull(segments, "Segments");
      StringBuilder result = new StringBuilder();
      Iterator i$ = segments.iterator();

      while(i$.hasNext()) {
         String segment = (String)i$.next();
         result.append('/').append(urlEncode(segment, charset, PATHSAFE, false));
      }

      return result.toString();
   }

   public static String formatSegments(String... segments) {
      return formatSegments(Arrays.asList(segments), Consts.UTF_8);
   }

   public static String format(List<? extends NameValuePair> parameters, String charset) {
      return format(parameters, '&', charset);
   }

   public static String format(List<? extends NameValuePair> parameters, char parameterSeparator, String charset) {
      StringBuilder result = new StringBuilder();
      Iterator i$ = parameters.iterator();

      while(i$.hasNext()) {
         NameValuePair parameter = (NameValuePair)i$.next();
         String encodedName = encodeFormFields(parameter.getName(), charset);
         String encodedValue = encodeFormFields(parameter.getValue(), charset);
         if (result.length() > 0) {
            result.append(parameterSeparator);
         }

         result.append(encodedName);
         if (encodedValue != null) {
            result.append("=");
            result.append(encodedValue);
         }
      }

      return result.toString();
   }

   public static String format(Iterable<? extends NameValuePair> parameters, Charset charset) {
      return format(parameters, '&', charset);
   }

   public static String format(Iterable<? extends NameValuePair> parameters, char parameterSeparator, Charset charset) {
      Args.notNull(parameters, "Parameters");
      StringBuilder result = new StringBuilder();
      Iterator i$ = parameters.iterator();

      while(i$.hasNext()) {
         NameValuePair parameter = (NameValuePair)i$.next();
         String encodedName = encodeFormFields(parameter.getName(), charset);
         String encodedValue = encodeFormFields(parameter.getValue(), charset);
         if (result.length() > 0) {
            result.append(parameterSeparator);
         }

         result.append(encodedName);
         if (encodedValue != null) {
            result.append("=");
            result.append(encodedValue);
         }
      }

      return result.toString();
   }

   private static List<NameValuePair> createEmptyList() {
      return new ArrayList(0);
   }

   private static String urlEncode(String content, Charset charset, BitSet safechars, boolean blankAsPlus) {
      if (content == null) {
         return null;
      } else {
         StringBuilder buf = new StringBuilder();
         ByteBuffer bb = charset.encode(content);

         while(true) {
            while(bb.hasRemaining()) {
               int b = bb.get() & 255;
               if (safechars.get(b)) {
                  buf.append((char)b);
               } else if (blankAsPlus && b == 32) {
                  buf.append('+');
               } else {
                  buf.append("%");
                  char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 15, 16));
                  char hex2 = Character.toUpperCase(Character.forDigit(b & 15, 16));
                  buf.append(hex1);
                  buf.append(hex2);
               }
            }

            return buf.toString();
         }
      }
   }

   private static String urlDecode(String content, Charset charset, boolean plusAsBlank) {
      if (content == null) {
         return null;
      } else {
         ByteBuffer bb = ByteBuffer.allocate(content.length());
         CharBuffer cb = CharBuffer.wrap(content);

         while(true) {
            while(true) {
               while(cb.hasRemaining()) {
                  char c = cb.get();
                  if (c == '%' && cb.remaining() >= 2) {
                     char uc = cb.get();
                     char lc = cb.get();
                     int u = Character.digit(uc, 16);
                     int l = Character.digit(lc, 16);
                     if (u != -1 && l != -1) {
                        bb.put((byte)((u << 4) + l));
                     } else {
                        bb.put((byte)37);
                        bb.put((byte)uc);
                        bb.put((byte)lc);
                     }
                  } else if (plusAsBlank && c == '+') {
                     bb.put((byte)32);
                  } else {
                     bb.put((byte)c);
                  }
               }

               bb.flip();
               return charset.decode(bb).toString();
            }
         }
      }
   }

   private static String decodeFormFields(String content, String charset) {
      return content == null ? null : urlDecode(content, charset != null ? Charset.forName(charset) : Consts.UTF_8, true);
   }

   private static String decodeFormFields(String content, Charset charset) {
      return content == null ? null : urlDecode(content, charset != null ? charset : Consts.UTF_8, true);
   }

   private static String encodeFormFields(String content, String charset) {
      return content == null ? null : urlEncode(content, charset != null ? Charset.forName(charset) : Consts.UTF_8, URLENCODER, true);
   }

   private static String encodeFormFields(String content, Charset charset) {
      return content == null ? null : urlEncode(content, charset != null ? charset : Consts.UTF_8, URLENCODER, true);
   }

   static String encUserInfo(String content, Charset charset) {
      return urlEncode(content, charset, USERINFO, false);
   }

   static String encUric(String content, Charset charset) {
      return urlEncode(content, charset, URIC, false);
   }

   static String encPath(String content, Charset charset) {
      return urlEncode(content, charset, PATH_SPECIAL, false);
   }

   static {
      PATH_SEPARATORS.set(47);
      UNRESERVED = new BitSet(256);
      PUNCT = new BitSet(256);
      USERINFO = new BitSet(256);
      PATHSAFE = new BitSet(256);
      URIC = new BitSet(256);
      RESERVED = new BitSet(256);
      URLENCODER = new BitSet(256);
      PATH_SPECIAL = new BitSet(256);

      int i;
      for(i = 97; i <= 122; ++i) {
         UNRESERVED.set(i);
      }

      for(i = 65; i <= 90; ++i) {
         UNRESERVED.set(i);
      }

      for(i = 48; i <= 57; ++i) {
         UNRESERVED.set(i);
      }

      UNRESERVED.set(95);
      UNRESERVED.set(45);
      UNRESERVED.set(46);
      UNRESERVED.set(42);
      URLENCODER.or(UNRESERVED);
      UNRESERVED.set(33);
      UNRESERVED.set(126);
      UNRESERVED.set(39);
      UNRESERVED.set(40);
      UNRESERVED.set(41);
      PUNCT.set(44);
      PUNCT.set(59);
      PUNCT.set(58);
      PUNCT.set(36);
      PUNCT.set(38);
      PUNCT.set(43);
      PUNCT.set(61);
      USERINFO.or(UNRESERVED);
      USERINFO.or(PUNCT);
      PATHSAFE.or(UNRESERVED);
      PATHSAFE.set(59);
      PATHSAFE.set(58);
      PATHSAFE.set(64);
      PATHSAFE.set(38);
      PATHSAFE.set(61);
      PATHSAFE.set(43);
      PATHSAFE.set(36);
      PATHSAFE.set(44);
      PATH_SPECIAL.or(PATHSAFE);
      PATH_SPECIAL.set(47);
      RESERVED.set(59);
      RESERVED.set(47);
      RESERVED.set(63);
      RESERVED.set(58);
      RESERVED.set(64);
      RESERVED.set(38);
      RESERVED.set(61);
      RESERVED.set(43);
      RESERVED.set(36);
      RESERVED.set(44);
      RESERVED.set(91);
      RESERVED.set(93);
      URIC.or(RESERVED);
      URIC.or(UNRESERVED);
   }
}
