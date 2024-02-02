package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class IgnoreSpecFactory implements CookieSpecFactory, CookieSpecProvider {
   public CookieSpec newInstance(HttpParams params) {
      return new IgnoreSpec();
   }

   public CookieSpec create(HttpContext context) {
      return new IgnoreSpec();
   }
}
