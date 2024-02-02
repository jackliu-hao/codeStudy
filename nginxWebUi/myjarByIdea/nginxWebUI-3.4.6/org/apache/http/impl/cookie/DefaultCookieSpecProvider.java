package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.protocol.HttpContext;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class DefaultCookieSpecProvider implements CookieSpecProvider {
   private final CompatibilityLevel compatibilityLevel;
   private final PublicSuffixMatcher publicSuffixMatcher;
   private final String[] datepatterns;
   private final boolean oneHeader;
   private volatile CookieSpec cookieSpec;

   public DefaultCookieSpecProvider(CompatibilityLevel compatibilityLevel, PublicSuffixMatcher publicSuffixMatcher, String[] datepatterns, boolean oneHeader) {
      this.compatibilityLevel = compatibilityLevel != null ? compatibilityLevel : DefaultCookieSpecProvider.CompatibilityLevel.DEFAULT;
      this.publicSuffixMatcher = publicSuffixMatcher;
      this.datepatterns = datepatterns;
      this.oneHeader = oneHeader;
   }

   public DefaultCookieSpecProvider(CompatibilityLevel compatibilityLevel, PublicSuffixMatcher publicSuffixMatcher) {
      this(compatibilityLevel, publicSuffixMatcher, (String[])null, false);
   }

   public DefaultCookieSpecProvider(PublicSuffixMatcher publicSuffixMatcher) {
      this(DefaultCookieSpecProvider.CompatibilityLevel.DEFAULT, publicSuffixMatcher, (String[])null, false);
   }

   public DefaultCookieSpecProvider() {
      this(DefaultCookieSpecProvider.CompatibilityLevel.DEFAULT, (PublicSuffixMatcher)null, (String[])null, false);
   }

   public CookieSpec create(HttpContext context) {
      if (this.cookieSpec == null) {
         synchronized(this) {
            if (this.cookieSpec == null) {
               RFC2965Spec strict = new RFC2965Spec(this.oneHeader, new CommonCookieAttributeHandler[]{new RFC2965VersionAttributeHandler(), new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new RFC2965DomainAttributeHandler(), this.publicSuffixMatcher), new RFC2965PortAttributeHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new RFC2965CommentUrlAttributeHandler(), new RFC2965DiscardAttributeHandler()});
               RFC2109Spec obsoleteStrict = new RFC2109Spec(this.oneHeader, new CommonCookieAttributeHandler[]{new RFC2109VersionHandler(), new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new RFC2109DomainHandler(), this.publicSuffixMatcher), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler()});
               NetscapeDraftSpec netscapeDraft = new NetscapeDraftSpec(new CommonCookieAttributeHandler[]{PublicSuffixDomainFilter.decorate(new BasicDomainHandler(), this.publicSuffixMatcher), this.compatibilityLevel == DefaultCookieSpecProvider.CompatibilityLevel.IE_MEDIUM_SECURITY ? new BasicPathHandler() {
                  public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
                  }
               } : new BasicPathHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler(this.datepatterns != null ? (String[])this.datepatterns.clone() : new String[]{"EEE, dd-MMM-yy HH:mm:ss z"})});
               this.cookieSpec = new DefaultCookieSpec(strict, obsoleteStrict, netscapeDraft);
            }
         }
      }

      return this.cookieSpec;
   }

   public static enum CompatibilityLevel {
      DEFAULT,
      IE_MEDIUM_SECURITY;
   }
}
