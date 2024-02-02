package org.apache.http;

import org.apache.http.protocol.HttpContext;

public interface HttpResponseFactory {
  HttpResponse newHttpResponse(ProtocolVersion paramProtocolVersion, int paramInt, HttpContext paramHttpContext);
  
  HttpResponse newHttpResponse(StatusLine paramStatusLine, HttpContext paramHttpContext);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\HttpResponseFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */