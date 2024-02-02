package org.h2.security.auth.impl;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import org.h2.api.CredentialsValidator;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.ConfigProperties;

public class JaasCredentialsValidator implements CredentialsValidator {
   public static final String DEFAULT_APPNAME = "h2";
   private String appName;

   public JaasCredentialsValidator() {
      this("h2");
   }

   public JaasCredentialsValidator(String var1) {
      this.appName = var1;
   }

   public void configure(ConfigProperties var1) {
      this.appName = var1.getStringValue("appName", this.appName);
   }

   public boolean validateCredentials(AuthenticationInfo var1) throws Exception {
      LoginContext var2 = new LoginContext(this.appName, new AuthenticationInfoCallbackHandler(var1));
      var2.login();
      var1.setNestedIdentity(var2.getSubject());
      return true;
   }

   static class AuthenticationInfoCallbackHandler implements CallbackHandler {
      AuthenticationInfo authenticationInfo;

      AuthenticationInfoCallbackHandler(AuthenticationInfo var1) {
         this.authenticationInfo = var1;
      }

      public void handle(Callback[] var1) throws IOException, UnsupportedCallbackException {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] instanceof NameCallback) {
               ((NameCallback)var1[var2]).setName(this.authenticationInfo.getUserName());
            } else if (var1[var2] instanceof PasswordCallback) {
               ((PasswordCallback)var1[var2]).setPassword(this.authenticationInfo.getPassword().toCharArray());
            }
         }

      }
   }
}
