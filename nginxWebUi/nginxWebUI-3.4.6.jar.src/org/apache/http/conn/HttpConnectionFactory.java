package org.apache.http.conn;

import org.apache.http.config.ConnectionConfig;

public interface HttpConnectionFactory<T, C extends org.apache.http.HttpConnection> {
  C create(T paramT, ConnectionConfig paramConnectionConfig);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\HttpConnectionFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */