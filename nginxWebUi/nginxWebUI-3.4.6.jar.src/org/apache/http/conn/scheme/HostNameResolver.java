package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetAddress;

@Deprecated
public interface HostNameResolver {
  InetAddress resolve(String paramString) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\scheme\HostNameResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */