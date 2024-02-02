package org.apache.http.conn.ssl;

import java.net.Socket;
import java.util.Map;

/** @deprecated */
@Deprecated
public interface PrivateKeyStrategy {
   String chooseAlias(Map<String, PrivateKeyDetails> var1, Socket var2);
}
