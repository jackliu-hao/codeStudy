package io.undertow.security.impl;

import io.undertow.security.idm.Account;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionManager;

public interface SingleSignOn extends Iterable<Session>, AutoCloseable {
   String getId();

   Account getAccount();

   String getMechanismName();

   boolean contains(Session var1);

   void add(Session var1);

   void remove(Session var1);

   Session getSession(SessionManager var1);

   void close();
}
