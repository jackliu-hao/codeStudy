package io.undertow.util;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.handlers.form.FormParserFactory;
import java.util.Map;

public class ImmediateAuthenticationMechanismFactory implements AuthenticationMechanismFactory {
   private final AuthenticationMechanism authenticationMechanism;

   public ImmediateAuthenticationMechanismFactory(AuthenticationMechanism authenticationMechanism) {
      this.authenticationMechanism = authenticationMechanism;
   }

   public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
      return this.authenticationMechanism;
   }
}
