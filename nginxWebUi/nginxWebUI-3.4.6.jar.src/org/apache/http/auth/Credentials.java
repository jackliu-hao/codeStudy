package org.apache.http.auth;

import java.security.Principal;

public interface Credentials {
  Principal getUserPrincipal();
  
  String getPassword();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\Credentials.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */