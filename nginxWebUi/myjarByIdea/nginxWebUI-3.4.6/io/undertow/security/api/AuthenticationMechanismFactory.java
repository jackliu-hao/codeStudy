package io.undertow.security.api;

import io.undertow.security.idm.IdentityManager;
import io.undertow.server.handlers.form.FormParserFactory;
import java.util.Map;

public interface AuthenticationMechanismFactory {
   String REALM = "realm";
   String LOGIN_PAGE = "login_page";
   String ERROR_PAGE = "error_page";
   String CONTEXT_PATH = "context_path";
   String DEFAULT_PAGE = "default_page";
   String OVERRIDE_INITIAL = "override_initial";

   /** @deprecated */
   @Deprecated
   default AuthenticationMechanism create(String mechanismName, FormParserFactory formParserFactory, Map<String, String> properties) {
      return null;
   }

   default AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
      return this.create(mechanismName, formParserFactory, properties);
   }
}
