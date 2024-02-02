package io.undertow.security.api;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import java.util.List;

public interface SecurityContext {
  boolean authenticate();
  
  boolean login(String paramString1, String paramString2);
  
  void logout();
  
  void setAuthenticationRequired();
  
  boolean isAuthenticationRequired();
  
  @Deprecated
  void addAuthenticationMechanism(AuthenticationMechanism paramAuthenticationMechanism);
  
  @Deprecated
  List<AuthenticationMechanism> getAuthenticationMechanisms();
  
  boolean isAuthenticated();
  
  Account getAuthenticatedAccount();
  
  String getMechanismName();
  
  @Deprecated
  IdentityManager getIdentityManager();
  
  void authenticationComplete(Account paramAccount, String paramString, boolean paramBoolean);
  
  void authenticationFailed(String paramString1, String paramString2);
  
  void registerNotificationReceiver(NotificationReceiver paramNotificationReceiver);
  
  void removeNotificationReceiver(NotificationReceiver paramNotificationReceiver);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\api\SecurityContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */