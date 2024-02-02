package org.apache.http.cookie;

import java.util.List;
import org.apache.http.Header;
import org.apache.http.annotation.Obsolete;

public interface CookieSpec {
   @Obsolete
   int getVersion();

   List<Cookie> parse(Header var1, CookieOrigin var2) throws MalformedCookieException;

   void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException;

   boolean match(Cookie var1, CookieOrigin var2);

   List<Header> formatCookies(List<Cookie> var1);

   @Obsolete
   Header getVersionHeader();
}
