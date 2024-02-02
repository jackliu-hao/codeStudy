package org.apache.http;

import java.io.IOException;
import java.net.Socket;

public interface HttpConnectionFactory<T extends HttpConnection> {
  T createConnection(Socket paramSocket) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\HttpConnectionFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */