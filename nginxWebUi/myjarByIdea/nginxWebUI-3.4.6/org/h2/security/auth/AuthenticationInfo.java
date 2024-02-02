package org.h2.security.auth;

import org.h2.engine.ConnectionInfo;
import org.h2.util.StringUtils;

public class AuthenticationInfo {
   private ConnectionInfo connectionInfo;
   private String password;
   private String realm;
   Object nestedIdentity;

   public AuthenticationInfo(ConnectionInfo var1) {
      this.connectionInfo = var1;
      this.realm = var1.getProperty("AUTHREALM", (String)null);
      if (this.realm != null) {
         this.realm = StringUtils.toUpperEnglish(this.realm);
      }

      this.password = var1.getProperty("AUTHZPWD", (String)null);
   }

   public String getUserName() {
      return this.connectionInfo.getUserName();
   }

   public String getRealm() {
      return this.realm;
   }

   public String getPassword() {
      return this.password;
   }

   public ConnectionInfo getConnectionInfo() {
      return this.connectionInfo;
   }

   public String getFullyQualifiedName() {
      return this.realm == null ? this.connectionInfo.getUserName() : this.connectionInfo.getUserName() + "@" + this.realm;
   }

   public Object getNestedIdentity() {
      return this.nestedIdentity;
   }

   public void setNestedIdentity(Object var1) {
      this.nestedIdentity = var1;
   }

   public void clean() {
      this.password = null;
      this.nestedIdentity = null;
      this.connectionInfo.cleanAuthenticationInfo();
   }
}
