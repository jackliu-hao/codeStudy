package org.h2.security.auth;

import org.h2.engine.Database;
import org.h2.engine.User;

public interface Authenticator {
  User authenticate(AuthenticationInfo paramAuthenticationInfo, Database paramDatabase) throws AuthenticationException;
  
  void init(Database paramDatabase) throws AuthConfigException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\Authenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */