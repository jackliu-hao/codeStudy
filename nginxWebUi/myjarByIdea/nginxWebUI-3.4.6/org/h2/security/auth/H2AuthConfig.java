package org.h2.security.auth;

import java.util.ArrayList;
import java.util.List;

public class H2AuthConfig {
   private boolean allowUserRegistration = true;
   private boolean createMissingRoles = true;
   private List<RealmConfig> realms;
   private List<UserToRolesMapperConfig> userToRolesMappers;

   public boolean isAllowUserRegistration() {
      return this.allowUserRegistration;
   }

   public void setAllowUserRegistration(boolean var1) {
      this.allowUserRegistration = var1;
   }

   public boolean isCreateMissingRoles() {
      return this.createMissingRoles;
   }

   public void setCreateMissingRoles(boolean var1) {
      this.createMissingRoles = var1;
   }

   public List<RealmConfig> getRealms() {
      if (this.realms == null) {
         this.realms = new ArrayList();
      }

      return this.realms;
   }

   public void setRealms(List<RealmConfig> var1) {
      this.realms = var1;
   }

   public List<UserToRolesMapperConfig> getUserToRolesMappers() {
      if (this.userToRolesMappers == null) {
         this.userToRolesMappers = new ArrayList();
      }

      return this.userToRolesMappers;
   }

   public void setUserToRolesMappers(List<UserToRolesMapperConfig> var1) {
      this.userToRolesMappers = var1;
   }
}
