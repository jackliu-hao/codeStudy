package org.apache.http.ssl;

import java.net.Socket;
import java.util.Map;

public interface PrivateKeyStrategy {
  String chooseAlias(Map<String, PrivateKeyDetails> paramMap, Socket paramSocket);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\ssl\PrivateKeyStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */