package org.apache.http;

import org.apache.http.protocol.HttpContext;

public interface ConnectionReuseStrategy {
  boolean keepAlive(HttpResponse paramHttpResponse, HttpContext paramHttpContext);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\ConnectionReuseStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */