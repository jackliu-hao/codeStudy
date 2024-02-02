package org.h2.api;

import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.Configurable;

public interface CredentialsValidator extends Configurable {
  boolean validateCredentials(AuthenticationInfo paramAuthenticationInfo) throws Exception;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\api\CredentialsValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */