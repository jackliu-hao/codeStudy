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
public class RFC2965SpecProvider implements CookieSpecProvider {
   private final PublicSuffixMatcher publicSuffixMatcher;
   private final boolean oneHeader;
   private volatile CookieSpec cookieSpec;

   public RFC2965SpecProvider(PublicSuffixMatcher publicSuffixMatcher, boolean oneHeader) {
      this.oneHeader = oneHeader;
      this.publicSuffixMatcher = publicSuffixMatcher;
   }

   public RFC2965SpecProvider(PublicSuffixMatcher publicSuffixMatcher) {
      this(publicSuffixMatcher, false);
   }

   public RFC2965SpecProvider() {
      this((PublicSuffixMatcher)null, false);
   }

   public CookieSpec create(HttpContext context) {
      if (this.cookieSpec == null) {
         synchronized(this) {
            if (this.cookieSpec == null) {
               this.cookieSpec = new RFC2965Spec(this.oneHeader, new CommonCookieAttributeHandler[]{new RFC2965VersionAttributeHandler(), new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new RFC2965DomainAttributeHandler(), this.publicSuffixMatcher), new RFC2965PortAttributeHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new RFC2965CommentUrlAttributeHandler(), new RFC2965DiscardAttributeHandler()});
            }
         }
      }

      return this.cookieSpec;
   }
}
