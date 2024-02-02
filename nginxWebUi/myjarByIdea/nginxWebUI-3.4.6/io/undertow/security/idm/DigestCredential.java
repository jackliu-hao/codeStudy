package io.undertow.security.idm;

public interface DigestCredential extends Credential {
   DigestAlgorithm getAlgorithm();

   boolean verifyHA1(byte[] var1);

   String getRealm();

   byte[] getSessionData();
}
