package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookiePathComparator;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Obsolete
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class RFC2109Spec extends CookieSpecBase {
   static final String[] DATE_PATTERNS = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy"};
   private final boolean oneHeader;

   public RFC2109Spec(String[] datepatterns, boolean oneHeader) {
      super(new RFC2109VersionHandler(), new BasicPathHandler() {
         public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
            if (!this.match(cookie, origin)) {
               throw new CookieRestrictionViolationException("Illegal 'path' attribute \"" + cookie.getPath() + "\". Path of origin: \"" + origin.getPath() + "\"");
            }
         }
      }, new RFC2109DomainHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler(datepatterns != null ? (String[])datepatterns.clone() : DATE_PATTERNS));
      this.oneHeader = oneHeader;
   }

   public RFC2109Spec() {
      this((String[])null, false);
   }

   protected RFC2109Spec(boolean oneHeader, CommonCookieAttributeHandler... handlers) {
      super(handlers);
      this.oneHeader = oneHeader;
   }

   public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
      Args.notNull(header, "Header");
      Args.notNull(origin, "Cookie origin");
      if (!header.getName().equalsIgnoreCase("Set-Cookie")) {
         throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
      } else {
         HeaderElement[] elems = header.getElements();
         return this.parse(elems, origin);
      }
   }

   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      String name = cookie.getName();
      if (name.indexOf(32) != -1) {
         throw new CookieRestrictionViolationException("Cookie name may not contain blanks");
      } else if (name.startsWith("$")) {
         throw new CookieRestrictionViolationException("Cookie name may not start with $");
      } else {
         super.validate(cookie, origin);
      }
   }

   public List<Header> formatCookies(List<Cookie> cookies) {
      Args.notEmpty((Collection)cookies, "List of cookies");
      Object cookieList;
      if (cookies.size() > 1) {
         cookieList = new ArrayList(cookies);
         Collections.sort((List)cookieList, CookiePathComparator.INSTANCE);
      } else {
         cookieList = cookies;
      }

      return this.oneHeader ? this.doFormatOneHeader((List)cookieList) : this.doFormatManyHeaders((List)cookieList);
   }

   private List<Header> doFormatOneHeader(List<Cookie> cookies) {
      int version = Integer.MAX_VALUE;
      Iterator i$ = cookies.iterator();

      while(i$.hasNext()) {
         Cookie cookie = (Cookie)i$.next();
         if (cookie.getVersion() < version) {
            version = cookie.getVersion();
         }
      }

      CharArrayBuffer buffer = new CharArrayBuffer(40 * cookies.size());
      buffer.append("Cookie");
      buffer.append(": ");
      buffer.append("$Version=");
      buffer.append(Integer.toString(version));
      Iterator i$ = cookies.iterator();

      while(i$.hasNext()) {
         Cookie cooky = (Cookie)i$.next();
         buffer.append("; ");
         this.formatCookieAsVer(buffer, cooky, version);
      }

      List<Header> headers = new ArrayList(1);
      headers.add(new BufferedHeader(buffer));
      return headers;
   }

   private List<Header> doFormatManyHeaders(List<Cookie> cookies) {
      List<Header> headers = new ArrayList(cookies.size());
      Iterator i$ = cookies.iterator();

      while(i$.hasNext()) {
         Cookie cookie = (Cookie)i$.next();
         int version = cookie.getVersion();
         CharArrayBuffer buffer = new CharArrayBuffer(40);
         buffer.append("Cookie: ");
         buffer.append("$Version=");
         buffer.append(Integer.toString(version));
         buffer.append("; ");
         this.formatCookieAsVer(buffer, cookie, version);
         headers.add(new BufferedHeader(buffer));
      }

      return headers;
   }

   protected void formatParamAsVer(CharArrayBuffer buffer, String name, String value, int version) {
      buffer.append(name);
      buffer.append("=");
      if (value != null) {
         if (version > 0) {
            buffer.append('"');
            buffer.append(value);
            buffer.append('"');
         } else {
            buffer.append(value);
         }
      }

   }

   protected void formatCookieAsVer(CharArrayBuffer buffer, Cookie cookie, int version) {
      this.formatParamAsVer(buffer, cookie.getName(), cookie.getValue(), version);
      if (cookie.getPath() != null && cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("path")) {
         buffer.append("; ");
         this.formatParamAsVer(buffer, "$Path", cookie.getPath(), version);
      }

      if (cookie.getDomain() != null && cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("domain")) {
         buffer.append("; ");
         this.formatParamAsVer(buffer, "$Domain", cookie.getDomain(), version);
      }

   }

   public int getVersion() {
      return 1;
   }

   public Header getVersionHeader() {
      return null;
   }

   public String toString() {
      return "rfc2109";
   }
}
