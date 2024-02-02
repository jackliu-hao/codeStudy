package org.apache.http.impl.bootstrap;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;

public interface SSLServerSetupHandler {
  void initialize(SSLServerSocket paramSSLServerSocket) throws SSLException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\bootstrap\SSLServerSetupHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */