package com.warrenstrange.googleauth;

public interface IGoogleAuthenticator {
   GoogleAuthenticatorKey createCredentials();

   GoogleAuthenticatorKey createCredentials(String var1);

   int getTotpPassword(String var1);

   int getTotpPassword(String var1, long var2);

   int getTotpPasswordOfUser(String var1);

   int getTotpPasswordOfUser(String var1, long var2);

   boolean authorize(String var1, int var2);

   boolean authorize(String var1, int var2, long var3);

   boolean authorizeUser(String var1, int var2);

   boolean authorizeUser(String var1, int var2, long var3);

   ICredentialRepository getCredentialRepository();

   void setCredentialRepository(ICredentialRepository var1);
}
