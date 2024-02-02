package org.h2.security.auth;

import org.h2.engine.Database;
import org.h2.engine.User;

public interface Authenticator {
   User authenticate(AuthenticationInfo var1, Database var2) throws AuthenticationException;

   void init(Database var1) throws AuthConfigException;
}
