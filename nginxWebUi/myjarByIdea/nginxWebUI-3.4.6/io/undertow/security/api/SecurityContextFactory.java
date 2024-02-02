package io.undertow.security.api;

import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;

/** @deprecated */
@Deprecated
public interface SecurityContextFactory {
   SecurityContext createSecurityContext(HttpServerExchange var1, AuthenticationMode var2, IdentityManager var3, String var4);
}
