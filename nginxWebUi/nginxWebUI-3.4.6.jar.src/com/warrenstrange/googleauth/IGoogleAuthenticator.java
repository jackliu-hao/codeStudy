package com.warrenstrange.googleauth;

public interface IGoogleAuthenticator {
  GoogleAuthenticatorKey createCredentials();
  
  GoogleAuthenticatorKey createCredentials(String paramString);
  
  int getTotpPassword(String paramString);
  
  int getTotpPassword(String paramString, long paramLong);
  
  int getTotpPasswordOfUser(String paramString);
  
  int getTotpPasswordOfUser(String paramString, long paramLong);
  
  boolean authorize(String paramString, int paramInt);
  
  boolean authorize(String paramString, int paramInt, long paramLong);
  
  boolean authorizeUser(String paramString, int paramInt);
  
  boolean authorizeUser(String paramString, int paramInt, long paramLong);
  
  ICredentialRepository getCredentialRepository();
  
  void setCredentialRepository(ICredentialRepository paramICredentialRepository);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\warrenstrange\googleauth\IGoogleAuthenticator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */