package io.undertow.security.api;

public interface SessionNonceManager extends NonceManager {
   void associateHash(String var1, byte[] var2);

   byte[] lookupHash(String var1);
}
