package io.undertow.security.api;

public interface SessionNonceManager extends NonceManager {
  void associateHash(String paramString, byte[] paramArrayOfbyte);
  
  byte[] lookupHash(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\api\SessionNonceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */