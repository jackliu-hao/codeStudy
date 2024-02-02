package org.apache.http.impl.auth;

import java.nio.charset.Charset;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class DigestSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider {
   private final Charset charset;

   public DigestSchemeFactory(Charset charset) {
      this.charset = charset;
   }

   public DigestSchemeFactory() {
      this((Charset)null);
   }

   public AuthScheme newInstance(HttpParams params) {
      return new DigestScheme();
   }

   public AuthScheme create(HttpContext context) {
      return new DigestScheme(this.charset);
   }
}
