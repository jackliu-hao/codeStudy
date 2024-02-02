package io.undertow.security.impl;

import io.undertow.security.idm.Account;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionManager;

public interface SingleSignOn extends Iterable<Session>, AutoCloseable {
  String getId();
  
  Account getAccount();
  
  String getMechanismName();
  
  boolean contains(Session paramSession);
  
  void add(Session paramSession);
  
  void remove(Session paramSession);
  
  Session getSession(SessionManager paramSessionManager);
  
  void close();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\SingleSignOn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */