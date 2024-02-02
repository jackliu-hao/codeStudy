package io.undertow.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Headers {
   public static final String ACCEPT_STRING = "Accept";
   public static final String ACCEPT_CHARSET_STRING = "Accept-Charset";
   public static final String ACCEPT_ENCODING_STRING = "Accept-Encoding";
   public static final String ACCEPT_LANGUAGE_STRING = "Accept-Language";
   public static final String ACCEPT_RANGES_STRING = "Accept-Ranges";
   public static final String AGE_STRING = "Age";
   public static final String ALLOW_STRING = "Allow";
   public static final String AUTHENTICATION_INFO_STRING = "Authentication-Info";
   public static final String AUTHORIZATION_STRING = "Authorization";
   public static final String CACHE_CONTROL_STRING = "Cache-Control";
   public static final String COOKIE_STRING = "Cookie";
   public static final String COOKIE2_STRING = "Cookie2";
   public static final String CONNECTION_STRING = "Connection";
   public static final String CONTENT_DISPOSITION_STRING = "Content-Disposition";
   public static final String CONTENT_ENCODING_STRING = "Content-Encoding";
   public static final String CONTENT_LANGUAGE_STRING = "Content-Language";
   public static final String CONTENT_LENGTH_STRING = "Content-Length";
   public static final String CONTENT_LOCATION_STRING = "Content-Location";
   public static final String CONTENT_MD5_STRING = "Content-MD5";
   public static final String CONTENT_RANGE_STRING = "Content-Range";
   public static final String CONTENT_SECURITY_POLICY_STRING = "Content-Security-Policy";
   public static final String CONTENT_TYPE_STRING = "Content-Type";
   public static final String DATE_STRING = "Date";
   public static final String ETAG_STRING = "ETag";
   public static final String EXPECT_STRING = "Expect";
   public static final String EXPIRES_STRING = "Expires";
   public static final String FORWARDED_STRING = "Forwarded";
   public static final String FROM_STRING = "From";
   public static final String HOST_STRING = "Host";
   public static final String IF_MATCH_STRING = "If-Match";
   public static final String IF_MODIFIED_SINCE_STRING = "If-Modified-Since";
   public static final String IF_NONE_MATCH_STRING = "If-None-Match";
   public static final String IF_RANGE_STRING = "If-Range";
   public static final String IF_UNMODIFIED_SINCE_STRING = "If-Unmodified-Since";
   public static final String LAST_MODIFIED_STRING = "Last-Modified";
   public static final String LOCATION_STRING = "Location";
   public static final String MAX_FORWARDS_STRING = "Max-Forwards";
   public static final String ORIGIN_STRING = "Origin";
   public static final String PRAGMA_STRING = "Pragma";
   public static final String PROXY_AUTHENTICATE_STRING = "Proxy-Authenticate";
   public static final String PROXY_AUTHORIZATION_STRING = "Proxy-Authorization";
   public static final String RANGE_STRING = "Range";
   public static final String REFERER_STRING = "Referer";
   public static final String REFERRER_POLICY_STRING = "Referrer-Policy";
   public static final String REFRESH_STRING = "Refresh";
   public static final String RETRY_AFTER_STRING = "Retry-After";
   public static final String SEC_WEB_SOCKET_ACCEPT_STRING = "Sec-WebSocket-Accept";
   public static final String SEC_WEB_SOCKET_EXTENSIONS_STRING = "Sec-WebSocket-Extensions";
   public static final String SEC_WEB_SOCKET_KEY_STRING = "Sec-WebSocket-Key";
   public static final String SEC_WEB_SOCKET_KEY1_STRING = "Sec-WebSocket-Key1";
   public static final String SEC_WEB_SOCKET_KEY2_STRING = "Sec-WebSocket-Key2";
   public static final String SEC_WEB_SOCKET_LOCATION_STRING = "Sec-WebSocket-Location";
   public static final String SEC_WEB_SOCKET_ORIGIN_STRING = "Sec-WebSocket-Origin";
   public static final String SEC_WEB_SOCKET_PROTOCOL_STRING = "Sec-WebSocket-Protocol";
   public static final String SEC_WEB_SOCKET_VERSION_STRING = "Sec-WebSocket-Version";
   public static final String SERVER_STRING = "Server";
   public static final String SERVLET_ENGINE_STRING = "Servlet-Engine";
   public static final String SET_COOKIE_STRING = "Set-Cookie";
   public static final String SET_COOKIE2_STRING = "Set-Cookie2";
   public static final String SSL_CLIENT_CERT_STRING = "SSL_CLIENT_CERT";
   public static final String SSL_CIPHER_STRING = "SSL_CIPHER";
   public static final String SSL_SESSION_ID_STRING = "SSL_SESSION_ID";
   public static final String SSL_CIPHER_USEKEYSIZE_STRING = "SSL_CIPHER_USEKEYSIZE";
   public static final String STATUS_STRING = "Status";
   public static final String STRICT_TRANSPORT_SECURITY_STRING = "Strict-Transport-Security";
   public static final String TE_STRING = "TE";
   public static final String TRAILER_STRING = "Trailer";
   public static final String TRANSFER_ENCODING_STRING = "Transfer-Encoding";
   public static final String UPGRADE_STRING = "Upgrade";
   public static final String USER_AGENT_STRING = "User-Agent";
   public static final String VARY_STRING = "Vary";
   public static final String VIA_STRING = "Via";
   public static final String WARNING_STRING = "Warning";
   public static final String WWW_AUTHENTICATE_STRING = "WWW-Authenticate";
   public static final String X_CONTENT_TYPE_OPTIONS_STRING = "X-Content-Type-Options";
   public static final String X_DISABLE_PUSH_STRING = "X-Disable-Push";
   public static final String X_FORWARDED_FOR_STRING = "X-Forwarded-For";
   public static final String X_FORWARDED_PROTO_STRING = "X-Forwarded-Proto";
   public static final String X_FORWARDED_HOST_STRING = "X-Forwarded-Host";
   public static final String X_FORWARDED_PORT_STRING = "X-Forwarded-Port";
   public static final String X_FORWARDED_SERVER_STRING = "X-Forwarded-Server";
   public static final String X_FRAME_OPTIONS_STRING = "X-Frame-Options";
   public static final String X_XSS_PROTECTION_STRING = "X-Xss-Protection";
   public static final HttpString ACCEPT = new HttpString("Accept", 1);
   public static final HttpString ACCEPT_CHARSET = new HttpString("Accept-Charset", 2);
   public static final HttpString ACCEPT_ENCODING = new HttpString("Accept-Encoding", 3);
   public static final HttpString ACCEPT_LANGUAGE = new HttpString("Accept-Language", 4);
   public static final HttpString ACCEPT_RANGES = new HttpString("Accept-Ranges", 5);
   public static final HttpString AGE = new HttpString("Age", 6);
   public static final HttpString ALLOW = new HttpString("Allow", 7);
   public static final HttpString AUTHENTICATION_INFO = new HttpString("Authentication-Info", 8);
   public static final HttpString AUTHORIZATION = new HttpString("Authorization", 9);
   public static final HttpString CACHE_CONTROL = new HttpString("Cache-Control", 10);
   public static final HttpString CONNECTION = new HttpString("Connection", 11);
   public static final HttpString CONTENT_DISPOSITION = new HttpString("Content-Disposition", 12);
   public static final HttpString CONTENT_ENCODING = new HttpString("Content-Encoding", 13);
   public static final HttpString CONTENT_LANGUAGE = new HttpString("Content-Language", 14);
   public static final HttpString CONTENT_LENGTH = new HttpString("Content-Length", 15);
   public static final HttpString CONTENT_LOCATION = new HttpString("Content-Location", 16);
   public static final HttpString CONTENT_MD5 = new HttpString("Content-MD5", 17);
   public static final HttpString CONTENT_RANGE = new HttpString("Content-Range", 18);
   public static final HttpString CONTENT_SECURITY_POLICY = new HttpString("Content-Security-Policy", 19);
   public static final HttpString CONTENT_TYPE = new HttpString("Content-Type", 20);
   public static final HttpString COOKIE = new HttpString("Cookie", 21);
   public static final HttpString COOKIE2 = new HttpString("Cookie2", 22);
   public static final HttpString DATE = new HttpString("Date", 23);
   public static final HttpString ETAG = new HttpString("ETag", 24);
   public static final HttpString EXPECT = new HttpString("Expect", 25);
   public static final HttpString EXPIRES = new HttpString("Expires", 26);
   public static final HttpString FORWARDED = new HttpString("Forwarded", 27);
   public static final HttpString FROM = new HttpString("From", 28);
   public static final HttpString HOST = new HttpString("Host", 29);
   public static final HttpString IF_MATCH = new HttpString("If-Match", 30);
   public static final HttpString IF_MODIFIED_SINCE = new HttpString("If-Modified-Since", 31);
   public static final HttpString IF_NONE_MATCH = new HttpString("If-None-Match", 32);
   public static final HttpString IF_RANGE = new HttpString("If-Range", 33);
   public static final HttpString IF_UNMODIFIED_SINCE = new HttpString("If-Unmodified-Since", 34);
   public static final HttpString LAST_MODIFIED = new HttpString("Last-Modified", 35);
   public static final HttpString LOCATION = new HttpString("Location", 36);
   public static final HttpString MAX_FORWARDS = new HttpString("Max-Forwards", 37);
   public static final HttpString ORIGIN = new HttpString("Origin", 38);
   public static final HttpString PRAGMA = new HttpString("Pragma", 39);
   public static final HttpString PROXY_AUTHENTICATE = new HttpString("Proxy-Authenticate", 40);
   public static final HttpString PROXY_AUTHORIZATION = new HttpString("Proxy-Authorization", 41);
   public static final HttpString RANGE = new HttpString("Range", 42);
   public static final HttpString REFERER = new HttpString("Referer", 43);
   public static final HttpString REFERRER_POLICY = new HttpString("Referrer-Policy", 44);
   public static final HttpString REFRESH = new HttpString("Refresh", 45);
   public static final HttpString RETRY_AFTER = new HttpString("Retry-After", 46);
   public static final HttpString SEC_WEB_SOCKET_ACCEPT = new HttpString("Sec-WebSocket-Accept", 47);
   public static final HttpString SEC_WEB_SOCKET_EXTENSIONS = new HttpString("Sec-WebSocket-Extensions", 48);
   public static final HttpString SEC_WEB_SOCKET_KEY = new HttpString("Sec-WebSocket-Key", 49);
   public static final HttpString SEC_WEB_SOCKET_KEY1 = new HttpString("Sec-WebSocket-Key1", 50);
   public static final HttpString SEC_WEB_SOCKET_KEY2 = new HttpString("Sec-WebSocket-Key2", 51);
   public static final HttpString SEC_WEB_SOCKET_LOCATION = new HttpString("Sec-WebSocket-Location", 52);
   public static final HttpString SEC_WEB_SOCKET_ORIGIN = new HttpString("Sec-WebSocket-Origin", 53);
   public static final HttpString SEC_WEB_SOCKET_PROTOCOL = new HttpString("Sec-WebSocket-Protocol", 54);
   public static final HttpString SEC_WEB_SOCKET_VERSION = new HttpString("Sec-WebSocket-Version", 55);
   public static final HttpString SERVER = new HttpString("Server", 56);
   public static final HttpString SERVLET_ENGINE = new HttpString("Servlet-Engine", 57);
   public static final HttpString SET_COOKIE = new HttpString("Set-Cookie", 58);
   public static final HttpString SET_COOKIE2 = new HttpString("Set-Cookie2", 59);
   public static final HttpString SSL_CIPHER = new HttpString("SSL_CIPHER", 60);
   public static final HttpString SSL_CIPHER_USEKEYSIZE = new HttpString("SSL_CIPHER_USEKEYSIZE", 61);
   public static final HttpString SSL_CLIENT_CERT = new HttpString("SSL_CLIENT_CERT", 62);
   public static final HttpString SSL_SESSION_ID = new HttpString("SSL_SESSION_ID", 63);
   public static final HttpString STATUS = new HttpString("Status", 64);
   public static final HttpString STRICT_TRANSPORT_SECURITY = new HttpString("Strict-Transport-Security", 65);
   public static final HttpString TE = new HttpString("TE", 66);
   public static final HttpString TRAILER = new HttpString("Trailer", 67);
   public static final HttpString TRANSFER_ENCODING = new HttpString("Transfer-Encoding", 68);
   public static final HttpString UPGRADE = new HttpString("Upgrade", 69);
   public static final HttpString USER_AGENT = new HttpString("User-Agent", 70);
   public static final HttpString VARY = new HttpString("Vary", 71);
   public static final HttpString VIA = new HttpString("Via", 72);
   public static final HttpString WARNING = new HttpString("Warning", 73);
   public static final HttpString WWW_AUTHENTICATE = new HttpString("WWW-Authenticate", 74);
   public static final HttpString X_CONTENT_TYPE_OPTIONS = new HttpString("X-Content-Type-Options", 75);
   public static final HttpString X_DISABLE_PUSH = new HttpString("X-Disable-Push", 76);
   public static final HttpString X_FORWARDED_FOR = new HttpString("X-Forwarded-For", 77);
   public static final HttpString X_FORWARDED_HOST = new HttpString("X-Forwarded-Host", 78);
   public static final HttpString X_FORWARDED_PORT = new HttpString("X-Forwarded-Port", 79);
   public static final HttpString X_FORWARDED_PROTO = new HttpString("X-Forwarded-Proto", 80);
   public static final HttpString X_FORWARDED_SERVER = new HttpString("X-Forwarded-Server", 81);
   public static final HttpString X_FRAME_OPTIONS = new HttpString("X-Frame-Options", 82);
   public static final HttpString X_XSS_PROTECTION = new HttpString("X-Xss-Protection", 83);
   public static final HttpString COMPRESS = new HttpString("compress");
   public static final HttpString X_COMPRESS = new HttpString("x-compress");
   public static final HttpString DEFLATE = new HttpString("deflate");
   public static final HttpString IDENTITY = new HttpString("identity");
   public static final HttpString GZIP = new HttpString("gzip");
   public static final HttpString X_GZIP = new HttpString("x-gzip");
   public static final HttpString CHUNKED = new HttpString("chunked");
   public static final HttpString KEEP_ALIVE = new HttpString("keep-alive");
   public static final HttpString CLOSE = new HttpString("close");
   public static final String CONTENT_TRANSFER_ENCODING_STRING = "Content-Transfer-Encoding";
   public static final HttpString CONTENT_TRANSFER_ENCODING = new HttpString("Content-Transfer-Encoding");
   public static final HttpString BASIC = new HttpString("Basic");
   public static final HttpString DIGEST = new HttpString("Digest");
   public static final HttpString NEGOTIATE = new HttpString("Negotiate");
   public static final HttpString ALGORITHM = new HttpString("algorithm");
   public static final HttpString AUTH_PARAM = new HttpString("auth-param");
   public static final HttpString CNONCE = new HttpString("cnonce");
   public static final HttpString DOMAIN = new HttpString("domain");
   public static final HttpString NEXT_NONCE = new HttpString("nextnonce");
   public static final HttpString NONCE = new HttpString("nonce");
   public static final HttpString NONCE_COUNT = new HttpString("nc");
   public static final HttpString OPAQUE = new HttpString("opaque");
   public static final HttpString QOP = new HttpString("qop");
   public static final HttpString REALM = new HttpString("realm");
   public static final HttpString RESPONSE = new HttpString("response");
   public static final HttpString RESPONSE_AUTH = new HttpString("rspauth");
   public static final HttpString STALE = new HttpString("stale");
   public static final HttpString URI = new HttpString("uri");
   public static final HttpString USERNAME = new HttpString("username");
   private static final Map<String, HttpString> HTTP_STRING_MAP;

   private Headers() {
   }

   public static HttpString fromCache(String string) {
      return (HttpString)HTTP_STRING_MAP.get(string);
   }

   /** @deprecated */
   @Deprecated
   public static String extractTokenFromHeader(String header, String key) {
      int pos = header.indexOf(' ' + key + '=');
      if (pos == -1) {
         if (!header.startsWith(key + '=')) {
            return null;
         }

         pos = 0;
      } else {
         ++pos;
      }

      int start = pos + key.length() + 1;

      int end;
      for(end = start; end < header.length(); ++end) {
         char c = header.charAt(end);
         if (c == ' ' || c == '\t' || c == ';') {
            break;
         }
      }

      return header.substring(start, end);
   }

   public static String extractQuotedValueFromHeader(String header, String key) {
      int keypos = 0;
      int pos = -1;
      boolean whiteSpace = true;
      boolean inQuotes = false;

      int i;
      int start;
      for(i = 0; i < header.length() - 1; ++i) {
         start = header.charAt(i);
         if (inQuotes) {
            if (start == 34) {
               inQuotes = false;
            }
         } else {
            if (key.charAt(keypos) == start && (whiteSpace || keypos > 0)) {
               ++keypos;
               whiteSpace = false;
            } else if (start == 34) {
               keypos = 0;
               inQuotes = true;
               whiteSpace = false;
            } else {
               keypos = 0;
               whiteSpace = start == 32 || start == 59 || start == 9;
            }

            if (keypos == key.length()) {
               if (header.charAt(i + 1) == '=') {
                  pos = i + 2;
                  break;
               }

               keypos = 0;
            }
         }
      }

      if (pos == -1) {
         return null;
      } else {
         char c;
         if (header.charAt(pos) == '"') {
            start = pos + 1;

            for(i = start; i < header.length(); ++i) {
               c = header.charAt(i);
               if (c == '"') {
                  break;
               }
            }

            return header.substring(start, i);
         } else {
            for(i = pos; i < header.length(); ++i) {
               c = header.charAt(i);
               if (c == ' ' || c == '\t' || c == ';') {
                  break;
               }
            }

            return header.substring(pos, i);
         }
      }
   }

   public static String extractQuotedValueFromHeaderWithEncoding(String header, String key) {
      String value = extractQuotedValueFromHeader(header, key);
      if (value != null) {
         return value;
      } else {
         value = extractQuotedValueFromHeader(header, key + "*");
         if (value != null) {
            int characterSetDelimiter = value.indexOf(39);
            int languageDelimiter = value.lastIndexOf(39, characterSetDelimiter + 1);
            String characterSet = value.substring(0, characterSetDelimiter);

            try {
               String fileNameURLEncoded = value.substring(languageDelimiter + 1);
               return URLDecoder.decode(fileNameURLEncoded, characterSet);
            } catch (UnsupportedEncodingException var7) {
               throw new RuntimeException(var7);
            }
         } else {
            return null;
         }
      }
   }

   static {
      Map<String, HttpString> map = (Map)AccessController.doPrivileged(new PrivilegedAction<Map<String, HttpString>>() {
         public Map<String, HttpString> run() {
            Map<String, HttpString> map = new HashMap();
            Field[] fields = Headers.class.getDeclaredFields();
            Field[] var3 = fields;
            int var4 = fields.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Field field = var3[var5];
               if (Modifier.isStatic(field.getModifiers()) && field.getType() == HttpString.class) {
                  field.setAccessible(true);

                  try {
                     HttpString result = (HttpString)field.get((Object)null);
                     map.put(result.toString(), result);
                  } catch (IllegalAccessException var8) {
                     throw new RuntimeException(var8);
                  }
               }
            }

            return map;
         }
      });
      HTTP_STRING_MAP = Collections.unmodifiableMap(map);
   }
}
