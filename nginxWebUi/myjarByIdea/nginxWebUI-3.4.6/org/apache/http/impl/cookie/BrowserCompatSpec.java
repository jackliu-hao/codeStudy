package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

/** @deprecated */
@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BrowserCompatSpec extends CookieSpecBase {
   private static final String[] DEFAULT_DATE_PATTERNS = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z"};

   public BrowserCompatSpec(String[] datepatterns, BrowserCompatSpecFactory.SecurityLevel securityLevel) {
      super(new BrowserCompatVersionAttributeHandler(), new BasicDomainHandler(), securityLevel == BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_IE_MEDIUM ? new BasicPathHandler() {
         public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
         }
      } : new BasicPathHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler(datepatterns != null ? (String[])datepatterns.clone() : DEFAULT_DATE_PATTERNS));
   }

   public BrowserCompatSpec(String[] datepatterns) {
      this(datepatterns, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
   }

   public BrowserCompatSpec() {
      this((String[])null, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
   }

   public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
      Args.notNull(header, "Header");
      Args.notNull(origin, "Cookie origin");
      String headername = header.getName();
      if (!headername.equalsIgnoreCase("Set-Cookie")) {
         throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
      } else {
         HeaderElement[] helems = header.getElements();
         boolean versioned = false;
         boolean netscape = false;
         HeaderElement[] arr$ = helems;
         int len$ = helems.length;

         HeaderElement elem;
         for(int i$ = 0; i$ < len$; ++i$) {
            elem = arr$[i$];
            if (elem.getParameterByName("version") != null) {
               versioned = true;
            }

            if (elem.getParameterByName("expires") != null) {
               netscape = true;
            }
         }

         if (!netscape && versioned) {
            return this.parse(helems, origin);
         } else {
            NetscapeDraftHeaderParser parser = NetscapeDraftHeaderParser.DEFAULT;
            CharArrayBuffer buffer;
            ParserCursor cursor;
            if (header instanceof FormattedHeader) {
               buffer = ((FormattedHeader)header).getBuffer();
               cursor = new ParserCursor(((FormattedHeader)header).getValuePos(), buffer.length());
            } else {
               String s = header.getValue();
               if (s == null) {
                  throw new MalformedCookieException("Header value is null");
               }

               buffer = new CharArrayBuffer(s.length());
               buffer.append(s);
               cursor = new ParserCursor(0, buffer.length());
            }

            elem = parser.parseHeader(buffer, cursor);
            String name = elem.getName();
            String value = elem.getValue();
            if (name != null && !name.isEmpty()) {
               BasicClientCookie cookie = new BasicClientCookie(name, value);
               cookie.setPath(getDefaultPath(origin));
               cookie.setDomain(getDefaultDomain(origin));
               NameValuePair[] attribs = elem.getParameters();

               for(int j = attribs.length - 1; j >= 0; --j) {
                  NameValuePair attrib = attribs[j];
                  String s = attrib.getName().toLowerCase(Locale.ROOT);
                  cookie.setAttribute(s, attrib.getValue());
                  CookieAttributeHandler handler = this.findAttribHandler(s);
                  if (handler != null) {
                     handler.parse(cookie, attrib.getValue());
                  }
               }

               if (netscape) {
                  cookie.setVersion(0);
               }

               return Collections.singletonList(cookie);
            } else {
               throw new MalformedCookieException("Cookie name may not be empty");
            }
         }
      }
   }

   private static boolean isQuoteEnclosed(String s) {
      return s != null && s.startsWith("\"") && s.endsWith("\"");
   }

   public List<Header> formatCookies(List<Cookie> cookies) {
      Args.notEmpty((Collection)cookies, "List of cookies");
      CharArrayBuffer buffer = new CharArrayBuffer(20 * cookies.size());
      buffer.append("Cookie");
      buffer.append(": ");

      for(int i = 0; i < cookies.size(); ++i) {
         Cookie cookie = (Cookie)cookies.get(i);
         if (i > 0) {
            buffer.append("; ");
         }

         String cookieName = cookie.getName();
         String cookieValue = cookie.getValue();
         if (cookie.getVersion() > 0 && !isQuoteEnclosed(cookieValue)) {
            BasicHeaderValueFormatter.INSTANCE.formatHeaderElement(buffer, new BasicHeaderElement(cookieName, cookieValue), false);
         } else {
            buffer.append(cookieName);
            buffer.append("=");
            if (cookieValue != null) {
               buffer.append(cookieValue);
            }
         }
      }

      List<Header> headers = new ArrayList(1);
      headers.add(new BufferedHeader(buffer));
      return headers;
   }

   public int getVersion() {
      return 0;
   }

   public Header getVersionHeader() {
      return null;
   }

   public String toString() {
      return "compatibility";
   }
}
