package org.apache.http.impl.client;

import java.util.Collection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class TargetAuthenticationStrategy extends AuthenticationStrategyImpl {
   public static final TargetAuthenticationStrategy INSTANCE = new TargetAuthenticationStrategy();

   public TargetAuthenticationStrategy() {
      super(401, "WWW-Authenticate");
   }

   Collection<String> getPreferredAuthSchemes(RequestConfig config) {
      return config.getTargetPreferredAuthSchemes();
   }
}
