package org.apache.http.ssl;

import java.net.Socket;
import java.util.Map;

public interface PrivateKeyStrategy {
   String chooseAlias(Map<String, PrivateKeyDetails> var1, Socket var2);
}
