package org.h2.security.auth.impl;

import java.util.regex.Pattern;
import org.h2.api.CredentialsValidator;
import org.h2.security.SHA256;
import org.h2.security.auth.AuthenticationException;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.ConfigProperties;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public class StaticUserCredentialsValidator implements CredentialsValidator {
   private Pattern userNamePattern;
   private String password;
   private byte[] salt;
   private byte[] hashWithSalt;

   public StaticUserCredentialsValidator() {
   }

   public StaticUserCredentialsValidator(String var1, String var2) {
      if (var1 != null) {
         this.userNamePattern = Pattern.compile(var1.toUpperCase());
      }

      this.salt = MathUtils.secureRandomBytes(256);
      this.hashWithSalt = SHA256.getHashWithSalt(var2.getBytes(), this.salt);
   }

   public boolean validateCredentials(AuthenticationInfo var1) throws AuthenticationException {
      if (this.userNamePattern != null && !this.userNamePattern.matcher(var1.getUserName()).matches()) {
         return false;
      } else {
         return this.password != null ? this.password.equals(var1.getPassword()) : Utils.compareSecure(this.hashWithSalt, SHA256.getHashWithSalt(var1.getPassword().getBytes(), this.salt));
      }
   }

   public void configure(ConfigProperties var1) {
      String var2 = var1.getStringValue("userNamePattern", (String)null);
      if (var2 != null) {
         this.userNamePattern = Pattern.compile(var2);
      }

      this.password = var1.getStringValue("password", this.password);
      String var3 = var1.getStringValue("salt", (String)null);
      if (var3 != null) {
         this.salt = StringUtils.convertHexToBytes(var3);
      }

      String var4 = var1.getStringValue("hash", (String)null);
      if (var4 != null) {
         this.hashWithSalt = SHA256.getHashWithSalt(StringUtils.convertHexToBytes(var4), this.salt);
      }

   }
}
