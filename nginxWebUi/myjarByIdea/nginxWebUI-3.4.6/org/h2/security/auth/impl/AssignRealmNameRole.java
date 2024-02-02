package org.h2.security.auth.impl;

import java.util.Arrays;
import java.util.Collection;
import org.h2.api.UserToRolesMapper;
import org.h2.security.auth.AuthenticationException;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.ConfigProperties;

public class AssignRealmNameRole implements UserToRolesMapper {
   private String roleNameFormat;

   public AssignRealmNameRole() {
      this("@%s");
   }

   public AssignRealmNameRole(String var1) {
      this.roleNameFormat = var1;
   }

   public void configure(ConfigProperties var1) {
      this.roleNameFormat = var1.getStringValue("roleNameFormat", this.roleNameFormat);
   }

   public Collection<String> mapUserToRoles(AuthenticationInfo var1) throws AuthenticationException {
      return Arrays.asList(String.format(this.roleNameFormat, var1.getRealm()));
   }
}
