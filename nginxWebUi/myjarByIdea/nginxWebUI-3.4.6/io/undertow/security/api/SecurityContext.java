package io.undertow.security.api;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import java.util.List;

public interface SecurityContext {
   boolean authenticate();

   boolean login(String var1, String var2);

   void logout();

   void setAuthenticationRequired();

   boolean isAuthenticationRequired();

   /** @deprecated */
   @Deprecated
   void addAuthenticationMechanism(AuthenticationMechanism var1);

   /** @deprecated */
   @Deprecated
   List<AuthenticationMechanism> getAuthenticationMechanisms();

   boolean isAuthenticated();

   Account getAuthenticatedAccount();

   String getMechanismName();

   /** @deprecated */
   @Deprecated
   IdentityManager getIdentityManager();

   void authenticationComplete(Account var1, String var2, boolean var3);

   void authenticationFailed(String var1, String var2);

   void registerNotificationReceiver(NotificationReceiver var1);

   void removeNotificationReceiver(NotificationReceiver var1);
}
