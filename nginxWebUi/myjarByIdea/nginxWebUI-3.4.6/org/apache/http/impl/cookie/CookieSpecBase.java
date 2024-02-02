package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public abstract class CookieSpecBase extends AbstractCookieSpec {
   public CookieSpecBase() {
   }

   protected CookieSpecBase(HashMap<String, CookieAttributeHandler> map) {
      super(map);
   }

   protected CookieSpecBase(CommonCookieAttributeHandler... handlers) {
      super(handlers);
   }

   protected static String getDefaultPath(CookieOrigin origin) {
      String defaultPath = origin.getPath();
      int lastSlashIndex = defaultPath.lastIndexOf(47);
      if (lastSlashIndex >= 0) {
         if (lastSlashIndex == 0) {
            lastSlashIndex = 1;
         }

         defaultPath = defaultPath.substring(0, lastSlashIndex);
      }

      return defaultPath;
   }

   protected static String getDefaultDomain(CookieOrigin origin) {
      return origin.getHost();
   }

   protected List<Cookie> parse(HeaderElement[] elems, CookieOrigin origin) throws MalformedCookieException {
      List<Cookie> cookies = new ArrayList(elems.length);
      HeaderElement[] arr$ = elems;
      int len$ = elems.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         HeaderElement headerelement = arr$[i$];
         String name = headerelement.getName();
         String value = headerelement.getValue();
         if (name != null && !name.isEmpty()) {
            BasicClientCookie cookie = new BasicClientCookie(name, value);
            cookie.setPath(getDefaultPath(origin));
            cookie.setDomain(getDefaultDomain(origin));
            NameValuePair[] attribs = headerelement.getParameters();

            for(int j = attribs.length - 1; j >= 0; --j) {
               NameValuePair attrib = attribs[j];
               String s = attrib.getName().toLowerCase(Locale.ROOT);
               cookie.setAttribute(s, attrib.getValue());
               CookieAttributeHandler handler = this.findAttribHandler(s);
               if (handler != null) {
                  handler.parse(cookie, attrib.getValue());
               }
            }

            cookies.add(cookie);
         }
      }

      return cookies;
   }

   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      Args.notNull(origin, "Cookie origin");
      Iterator i$ = this.getAttribHandlers().iterator();

      while(i$.hasNext()) {
         CookieAttributeHandler handler = (CookieAttributeHandler)i$.next();
         handler.validate(cookie, origin);
      }

   }

   public boolean match(Cookie cookie, CookieOrigin origin) {
      Args.notNull(cookie, "Cookie");
      Args.notNull(origin, "Cookie origin");
      Iterator i$ = this.getAttribHandlers().iterator();

      CookieAttributeHandler handler;
      do {
         if (!i$.hasNext()) {
            return true;
         }

         handler = (CookieAttributeHandler)i$.next();
      } while(handler.match(cookie, origin));

      return false;
   }
}
