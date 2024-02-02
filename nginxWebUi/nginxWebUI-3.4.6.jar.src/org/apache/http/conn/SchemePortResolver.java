package org.apache.http.conn;

import org.apache.http.HttpHost;

public interface SchemePortResolver {
  int resolve(HttpHost paramHttpHost) throws UnsupportedSchemeException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\SchemePortResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */