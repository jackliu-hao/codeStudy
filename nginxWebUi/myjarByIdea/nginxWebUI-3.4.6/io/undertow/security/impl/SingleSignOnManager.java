package io.undertow.security.impl;

import io.undertow.security.idm.Account;

public interface SingleSignOnManager {
   SingleSignOn createSingleSignOn(Account var1, String var2);

   SingleSignOn findSingleSignOn(String var1);

   void removeSingleSignOn(SingleSignOn var1);
}
