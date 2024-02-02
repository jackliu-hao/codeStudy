package io.undertow.security.impl;

import io.undertow.security.idm.Account;

public interface SingleSignOnManager {
  SingleSignOn createSingleSignOn(Account paramAccount, String paramString);
  
  SingleSignOn findSingleSignOn(String paramString);
  
  void removeSingleSignOn(SingleSignOn paramSingleSignOn);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\SingleSignOnManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */