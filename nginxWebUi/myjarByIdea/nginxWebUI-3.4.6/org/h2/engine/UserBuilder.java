package org.h2.engine;

import org.h2.security.auth.AuthenticationInfo;
import org.h2.util.MathUtils;

public class UserBuilder {
   public static User buildUser(AuthenticationInfo var0, Database var1, boolean var2) {
      User var3 = new User(var1, var2 ? var1.allocateObjectId() : -1, var0.getFullyQualifiedName(), false);
      var3.setUserPasswordHash(var0.getRealm() == null ? var0.getConnectionInfo().getUserPasswordHash() : MathUtils.secureRandomBytes(64));
      var3.setTemporary(!var2);
      return var3;
   }
}
