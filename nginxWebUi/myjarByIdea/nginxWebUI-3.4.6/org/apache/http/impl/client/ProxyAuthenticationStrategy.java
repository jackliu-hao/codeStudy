package org.apache.http.impl.client;

import java.util.Collection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class ProxyAuthenticationStrategy extends AuthenticationStrategyImpl {
   public static final ProxyAuthenticationStrategy INSTANCE = new ProxyAuthenticationStrategy();

   public ProxyAuthenticationStrategy() {
      super(407, "Proxy-Authenticate");
   }

   Collection<String> getPreferredAuthSchemes(RequestConfig config) {
      return config.getProxyPreferredAuthSchemes();
   }
}
