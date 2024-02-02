package io.undertow.security.impl;

import io.undertow.security.idm.Account;
import io.undertow.server.session.SecureRandomSessionIdGenerator;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionManager;
import io.undertow.util.CopyOnWriteMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jboss.logging.Logger;

public class InMemorySingleSignOnManager implements SingleSignOnManager {
   private static final Logger log = Logger.getLogger(InMemorySingleSignOnManager.class);
   private static final SecureRandomSessionIdGenerator SECURE_RANDOM_SESSION_ID_GENERATOR = new SecureRandomSessionIdGenerator();
   private final Map<String, SingleSignOn> ssoEntries = new ConcurrentHashMap();

   public SingleSignOn findSingleSignOn(String ssoId) {
      return (SingleSignOn)this.ssoEntries.get(ssoId);
   }

   public SingleSignOn createSingleSignOn(Account account, String mechanism) {
      String id = SECURE_RANDOM_SESSION_ID_GENERATOR.createSessionId();
      SingleSignOn entry = new SimpleSingleSignOnEntry(id, account, mechanism);
      this.ssoEntries.put(id, entry);
      if (log.isTraceEnabled()) {
         log.tracef((String)"Creating SSO ID %s for Principal %s and Roles %s.", (Object)id, account.getPrincipal().getName(), account.getRoles().toString());
      }

      return entry;
   }

   public void removeSingleSignOn(SingleSignOn sso) {
      if (log.isTraceEnabled()) {
         log.tracef("Removing SSO ID %s.", (Object)sso.getId());
      }

      this.ssoEntries.remove(sso.getId());
   }

   private static class SimpleSingleSignOnEntry implements SingleSignOn {
      private final String id;
      private final Account account;
      private final String mechanismName;
      private final Map<SessionManager, Session> sessions = new CopyOnWriteMap();

      SimpleSingleSignOnEntry(String id, Account account, String mechanismName) {
         this.id = id;
         this.account = account;
         this.mechanismName = mechanismName;
      }

      public String getId() {
         return this.id;
      }

      public Account getAccount() {
         return this.account;
      }

      public String getMechanismName() {
         return this.mechanismName;
      }

      public Iterator<Session> iterator() {
         return Collections.unmodifiableCollection(this.sessions.values()).iterator();
      }

      public boolean contains(Session session) {
         return this.sessions.containsKey(session.getSessionManager());
      }

      public Session getSession(SessionManager manager) {
         return (Session)this.sessions.get(manager);
      }

      public void add(Session session) {
         this.sessions.put(session.getSessionManager(), session);
      }

      public void remove(Session session) {
         this.sessions.remove(session.getSessionManager());
      }

      public void close() {
      }
   }
}
