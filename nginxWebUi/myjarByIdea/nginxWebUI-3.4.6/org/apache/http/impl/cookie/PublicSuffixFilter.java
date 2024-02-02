package org.apache.http.impl.cookie;

import java.util.Collection;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;

/** @deprecated */
@Deprecated
public class PublicSuffixFilter implements CookieAttributeHandler {
   private final CookieAttributeHandler wrapped;
   private Collection<String> exceptions;
   private Collection<String> suffixes;
   private PublicSuffixMatcher matcher;

   public PublicSuffixFilter(CookieAttributeHandler wrapped) {
      this.wrapped = wrapped;
   }

   public void setPublicSuffixes(Collection<String> suffixes) {
      this.suffixes = suffixes;
      this.matcher = null;
   }

   public void setExceptions(Collection<String> exceptions) {
      this.exceptions = exceptions;
      this.matcher = null;
   }

   public boolean match(Cookie cookie, CookieOrigin origin) {
      return this.isForPublicSuffix(cookie) ? false : this.wrapped.match(cookie, origin);
   }

   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
      this.wrapped.parse(cookie, value);
   }

   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
      this.wrapped.validate(cookie, origin);
   }

   private boolean isForPublicSuffix(Cookie cookie) {
      if (this.matcher == null) {
         this.matcher = new PublicSuffixMatcher(this.suffixes, this.exceptions);
      }

      return this.matcher.matches(cookie.getDomain());
   }
}
