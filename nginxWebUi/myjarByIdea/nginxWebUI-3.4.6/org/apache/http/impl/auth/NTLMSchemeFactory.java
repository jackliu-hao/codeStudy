package org.apache.http.impl.auth;

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
public class NTLMSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider {
   public AuthScheme newInstance(HttpParams params) {
      return new NTLMScheme();
   }

   public AuthScheme create(HttpContext context) {
      return new NTLMScheme();
   }
}
