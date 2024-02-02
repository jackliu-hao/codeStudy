package org.apache.http.auth;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

public interface ContextAwareAuthScheme extends AuthScheme {
  Header authenticate(Credentials paramCredentials, HttpRequest paramHttpRequest, HttpContext paramHttpContext) throws AuthenticationException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\ContextAwareAuthScheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */