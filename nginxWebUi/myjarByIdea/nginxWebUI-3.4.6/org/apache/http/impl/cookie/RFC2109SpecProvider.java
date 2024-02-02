package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.protocol.HttpContext;

@Obsolete
@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class RFC2109SpecProvider implements CookieSpecProvider {
   private final PublicSuffixMatcher publicSuffixMatcher;
   private final boolean oneHeader;
   private volatile CookieSpec cookieSpec;

   public RFC2109SpecProvider(PublicSuffixMatcher publicSuffixMatcher, boolean oneHeader) {
      this.oneHeader = oneHeader;
      this.publicSuffixMatcher = publicSuffixMatcher;
   }

   public RFC2109SpecProvider(PublicSuffixMatcher publicSuffixMatcher) {
      this(publicSuffixMatcher, false);
   }

   public RFC2109SpecProvider() {
      this((PublicSuffixMatcher)null, false);
   }

   public CookieSpec create(HttpContext context) {
      if (this.cookieSpec == null) {
         synchronized(this) {
            if (this.cookieSpec == null) {
               this.cookieSpec = new RFC2109Spec(this.oneHeader, new CommonCookieAttributeHandler[]{new RFC2109VersionHandler(), new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new RFC2109DomainHandler(), this.publicSuffixMatcher), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler()});
            }
         }
      }

      return this.cookieSpec;
   }
}
