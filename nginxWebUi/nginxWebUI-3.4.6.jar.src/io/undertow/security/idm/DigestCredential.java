package io.undertow.security.idm;

public interface DigestCredential extends Credential {
  DigestAlgorithm getAlgorithm();
  
  boolean verifyHA1(byte[] paramArrayOfbyte);
  
  String getRealm();
  
  byte[] getSessionData();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\idm\DigestCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */