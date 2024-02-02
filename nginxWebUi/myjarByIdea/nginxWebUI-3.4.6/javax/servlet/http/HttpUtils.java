package javax.servlet.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.servlet.ServletInputStream;

/** @deprecated */
@Deprecated
public class HttpUtils {
   private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
   private static ResourceBundle lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");

   public static Hashtable<String, String[]> parseQueryString(String s) {
      String[] valArray = null;
      if (s == null) {
         throw new IllegalArgumentException();
      } else {
         Hashtable<String, String[]> ht = new Hashtable();
         StringBuilder sb = new StringBuilder();

         String key;
         for(StringTokenizer st = new StringTokenizer(s, "&"); st.hasMoreTokens(); ht.put(key, valArray)) {
            String pair = st.nextToken();
            int pos = pair.indexOf(61);
            if (pos == -1) {
               throw new IllegalArgumentException();
            }

            key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos + 1, pair.length()), sb);
            if (!ht.containsKey(key)) {
               valArray = new String[]{val};
            } else {
               String[] oldVals = (String[])ht.get(key);
               valArray = new String[oldVals.length + 1];

               for(int i = 0; i < oldVals.length; ++i) {
                  valArray[i] = oldVals[i];
               }

               valArray[oldVals.length] = val;
            }
         }

         return ht;
      }
   }

   public static Hashtable<String, String[]> parsePostData(int len, ServletInputStream in) {
      if (len <= 0) {
         return new Hashtable();
      } else if (in == null) {
         throw new IllegalArgumentException();
      } else {
         byte[] postedBytes = new byte[len];

         try {
            int offset = 0;

            do {
               int inputLen = in.read(postedBytes, offset, len - offset);
               if (inputLen <= 0) {
                  String msg = lStrings.getString("err.io.short_read");
                  throw new IllegalArgumentException(msg);
               }

               offset += inputLen;
            } while(len - offset > 0);
         } catch (IOException var7) {
            throw new IllegalArgumentException(var7.getMessage());
         }

         try {
            String postedBody = new String(postedBytes, 0, len, "8859_1");
            return parseQueryString(postedBody);
         } catch (UnsupportedEncodingException var6) {
            throw new IllegalArgumentException(var6.getMessage());
         }
      }
   }

   private static String parseName(String s, StringBuilder sb) {
      sb.setLength(0);

      for(int i = 0; i < s.length(); ++i) {
         char c = s.charAt(i);
         switch (c) {
            case '%':
               try {
                  sb.append((char)Integer.parseInt(s.substring(i + 1, i + 3), 16));
                  i += 2;
               } catch (NumberFormatException var6) {
                  throw new IllegalArgumentException();
               } catch (StringIndexOutOfBoundsException var7) {
                  String rest = s.substring(i);
                  sb.append(rest);
                  if (rest.length() == 2) {
                     ++i;
                  }
               }
               break;
            case '+':
               sb.append(' ');
               break;
            default:
               sb.append(c);
         }
      }

      return sb.toString();
   }

   public static StringBuffer getRequestURL(HttpServletRequest req) {
      StringBuffer url = new StringBuffer();
      String scheme = req.getScheme();
      int port = req.getServerPort();
      String urlPath = req.getRequestURI();
      url.append(scheme);
      url.append("://");
      url.append(req.getServerName());
      if (scheme.equals("http") && port != 80 || scheme.equals("https") && port != 443) {
         url.append(':');
         url.append(req.getServerPort());
      }

      url.append(urlPath);
      return url;
   }
}
