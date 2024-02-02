package io.undertow.security.idm;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;

public interface Account extends Serializable {
  Principal getPrincipal();
  
  Set<String> getRoles();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\idm\Account.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */