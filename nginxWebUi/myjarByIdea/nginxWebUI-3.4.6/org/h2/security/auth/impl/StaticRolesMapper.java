package org.h2.security.auth.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.h2.api.UserToRolesMapper;
import org.h2.security.auth.AuthenticationException;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.ConfigProperties;

public class StaticRolesMapper implements UserToRolesMapper {
   private Collection<String> roles;

   public StaticRolesMapper() {
   }

   public StaticRolesMapper(String... var1) {
      this.roles = Arrays.asList(var1);
   }

   public void configure(ConfigProperties var1) {
      String var2 = var1.getStringValue("roles", "");
      if (var2 != null) {
         this.roles = new HashSet(Arrays.asList(var2.split(",")));
      }

   }

   public Collection<String> mapUserToRoles(AuthenticationInfo var1) throws AuthenticationException {
      return this.roles;
   }
}
