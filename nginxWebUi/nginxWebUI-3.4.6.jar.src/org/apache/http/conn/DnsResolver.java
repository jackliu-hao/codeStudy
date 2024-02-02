package org.apache.http.conn;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface DnsResolver {
  InetAddress[] resolve(String paramString) throws UnknownHostException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\DnsResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */