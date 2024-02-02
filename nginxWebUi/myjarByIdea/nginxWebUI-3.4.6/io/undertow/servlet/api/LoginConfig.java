package io.undertow.servlet.api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class LoginConfig implements Cloneable {
   private final LinkedList<AuthMethodConfig> authMethods;
   private final String realmName;
   private final String loginPage;
   private final String errorPage;

   public LoginConfig(String realmName, String loginPage, String errorPage) {
      this.authMethods = new LinkedList();
      this.realmName = realmName;
      this.loginPage = loginPage;
      this.errorPage = errorPage;
   }

   public LoginConfig(String realmName) {
      this(realmName, (String)null, (String)null);
   }

   public LoginConfig(String mechanismName, String realmName, String loginPage, String errorPage) {
      this.authMethods = new LinkedList();
      this.realmName = realmName;
      this.loginPage = loginPage;
      this.errorPage = errorPage;
      this.addFirstAuthMethod(mechanismName);
   }

   public LoginConfig(String mechanismName, String realmName) {
      this(mechanismName, realmName, (String)null, (String)null);
   }

   public String getRealmName() {
      return this.realmName;
   }

   public String getLoginPage() {
      return this.loginPage;
   }

   public String getErrorPage() {
      return this.errorPage;
   }

   public LoginConfig addFirstAuthMethod(AuthMethodConfig authMethodConfig) {
      this.authMethods.addFirst(authMethodConfig);
      return this;
   }

   public LoginConfig addLastAuthMethod(AuthMethodConfig authMethodConfig) {
      this.authMethods.addLast(authMethodConfig);
      return this;
   }

   public LoginConfig addFirstAuthMethod(String authMethodConfig) {
      this.authMethods.addFirst(new AuthMethodConfig(authMethodConfig));
      return this;
   }

   public LoginConfig addLastAuthMethod(String authMethodConfig) {
      this.authMethods.addLast(new AuthMethodConfig(authMethodConfig));
      return this;
   }

   public List<AuthMethodConfig> getAuthMethods() {
      return this.authMethods;
   }

   public LoginConfig clone() {
      LoginConfig lc = new LoginConfig(this.realmName, this.loginPage, this.errorPage);
      Iterator var2 = this.authMethods.iterator();

      while(var2.hasNext()) {
         AuthMethodConfig method = (AuthMethodConfig)var2.next();
         lc.authMethods.add(method.clone());
      }

      return lc;
   }
}
