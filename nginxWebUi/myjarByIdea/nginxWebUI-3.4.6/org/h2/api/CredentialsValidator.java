package org.h2.api;

import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.Configurable;

public interface CredentialsValidator extends Configurable {
   boolean validateCredentials(AuthenticationInfo var1) throws Exception;
}
