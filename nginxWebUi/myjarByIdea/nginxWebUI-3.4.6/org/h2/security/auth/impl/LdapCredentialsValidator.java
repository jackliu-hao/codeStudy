package org.h2.security.auth.impl;

import java.util.Hashtable;
import javax.naming.directory.InitialDirContext;
import org.h2.api.CredentialsValidator;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.ConfigProperties;

public class LdapCredentialsValidator implements CredentialsValidator {
   private String bindDnPattern;
   private String host;
   private int port;
   private boolean secure;
   private String url;

   public void configure(ConfigProperties var1) {
      this.bindDnPattern = var1.getStringValue("bindDnPattern");
      this.host = var1.getStringValue("host");
      this.secure = var1.getBooleanValue("secure", true);
      this.port = var1.getIntValue("port", this.secure ? 636 : 389);
      this.url = "ldap" + (this.secure ? "s" : "") + "://" + this.host + ":" + this.port;
   }

   public boolean validateCredentials(AuthenticationInfo var1) throws Exception {
      InitialDirContext var2 = null;

      boolean var5;
      try {
         String var3 = this.bindDnPattern.replace("%u", var1.getUserName());
         Hashtable var4 = new Hashtable();
         var4.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
         var4.put("java.naming.provider.url", this.url);
         var4.put("java.naming.security.authentication", "simple");
         var4.put("java.naming.security.principal", var3);
         var4.put("java.naming.security.credentials", var1.getPassword());
         if (this.secure) {
            var4.put("java.naming.security.protocol", "ssl");
         }

         var2 = new InitialDirContext(var4);
         var1.setNestedIdentity(var3);
         var5 = true;
      } finally {
         if (var2 != null) {
            var2.close();
         }

      }

      return var5;
   }
}
