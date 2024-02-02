package io.undertow.security.api;

import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;

@Deprecated
public interface SecurityContextFactory {
  SecurityContext createSecurityContext(HttpServerExchange paramHttpServerExchange, AuthenticationMode paramAuthenticationMode, IdentityManager paramIdentityManager, String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\api\SecurityContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */