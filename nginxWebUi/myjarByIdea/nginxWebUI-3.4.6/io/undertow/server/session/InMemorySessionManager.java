package io.undertow.server.session;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import io.undertow.util.ConcurrentDirectDeque;
import io.undertow.util.WorkerUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;

public class InMemorySessionManager implements SessionManager, SessionManagerStatistics {
   private final AttachmentKey<SessionImpl> NEW_SESSION;
   private final SessionIdGenerator sessionIdGenerator;
   private final ConcurrentMap<String, SessionImpl> sessions;
   private final SessionListeners sessionListeners;
   private volatile int defaultSessionTimeout;
   private final int maxSize;
   private final ConcurrentDirectDeque<String> evictionQueue;
   private final String deploymentName;
   private final AtomicLong createdSessionCount;
   private final AtomicLong rejectedSessionCount;
   private volatile long longestSessionLifetime;
   private volatile long expiredSessionCount;
   private volatile BigInteger totalSessionLifetime;
   private final AtomicInteger highestSessionCount;
   private final boolean statisticsEnabled;
   private volatile long startTime;
   private final boolean expireOldestUnusedSessionOnMax;

   public InMemorySessionManager(String deploymentName, int maxSessions, boolean expireOldestUnusedSessionOnMax) {
      this(new SecureRandomSessionIdGenerator(), deploymentName, maxSessions, expireOldestUnusedSessionOnMax);
   }

   public InMemorySessionManager(SessionIdGenerator sessionIdGenerator, String deploymentName, int maxSessions, boolean expireOldestUnusedSessionOnMax) {
      this(sessionIdGenerator, deploymentName, maxSessions, expireOldestUnusedSessionOnMax, true);
   }

   public InMemorySessionManager(SessionIdGenerator sessionIdGenerator, String deploymentName, int maxSessions, boolean expireOldestUnusedSessionOnMax, boolean statisticsEnabled) {
      this.NEW_SESSION = AttachmentKey.create(SessionImpl.class);
      this.sessionListeners = new SessionListeners();
      this.defaultSessionTimeout = 1800;
      this.createdSessionCount = new AtomicLong();
      this.rejectedSessionCount = new AtomicLong();
      this.longestSessionLifetime = 0L;
      this.expiredSessionCount = 0L;
      this.totalSessionLifetime = BigInteger.ZERO;
      this.highestSessionCount = new AtomicInteger();
      this.sessionIdGenerator = sessionIdGenerator;
      this.deploymentName = deploymentName;
      this.statisticsEnabled = statisticsEnabled;
      this.expireOldestUnusedSessionOnMax = expireOldestUnusedSessionOnMax;
      this.sessions = new ConcurrentHashMap();
      this.maxSize = maxSessions;
      ConcurrentDirectDeque<String> evictionQueue = null;
      if (maxSessions > 0 && expireOldestUnusedSessionOnMax) {
         evictionQueue = ConcurrentDirectDeque.newInstance();
      }

      this.evictionQueue = evictionQueue;
   }

   public InMemorySessionManager(String deploymentName, int maxSessions) {
      this(deploymentName, maxSessions, false);
   }

   public InMemorySessionManager(String id) {
      this(id, -1);
   }

   public String getDeploymentName() {
      return this.deploymentName;
   }

   public void start() {
      this.createdSessionCount.set(0L);
      this.expiredSessionCount = 0L;
      this.rejectedSessionCount.set(0L);
      this.totalSessionLifetime = BigInteger.ZERO;
      this.startTime = System.currentTimeMillis();
   }

   public void stop() {
      Map.Entry session;
      for(Iterator var1 = this.sessions.entrySet().iterator(); var1.hasNext(); this.sessionListeners.sessionDestroyed((Session)session.getValue(), (HttpServerExchange)null, SessionListener.SessionDestroyedReason.UNDEPLOY)) {
         session = (Map.Entry)var1.next();
         SessionImpl sessionValue = (SessionImpl)session.getValue();
         sessionValue.destroy();
         if (sessionValue.getId() == null) {
            sessionValue.setId((String)session.getKey());
         }
      }

      this.sessions.clear();
   }

   public Session createSession(HttpServerExchange serverExchange, SessionConfig config) {
      String sessionID;
      SessionImpl session;
      if (this.maxSize > 0) {
         if (this.expireOldestUnusedSessionOnMax) {
            while(this.sessions.size() >= this.maxSize && !this.evictionQueue.isEmpty()) {
               sessionID = (String)this.evictionQueue.poll();
               if (sessionID == null) {
                  break;
               }

               UndertowLogger.REQUEST_LOGGER.debugf("Removing session %s as max size has been hit", sessionID);
               session = (SessionImpl)this.sessions.get(sessionID);
               if (session != null) {
                  session.invalidate((HttpServerExchange)null, SessionListener.SessionDestroyedReason.TIMEOUT);
               }
            }
         } else if (this.sessions.size() >= this.maxSize) {
            if (this.statisticsEnabled) {
               this.rejectedSessionCount.incrementAndGet();
            }

            throw UndertowMessages.MESSAGES.tooManySessions(this.maxSize);
         }
      }

      if (config == null) {
         throw UndertowMessages.MESSAGES.couldNotFindSessionCookieConfig();
      } else {
         sessionID = config.findSessionId(serverExchange);
         session = new SessionImpl(this, config, serverExchange.getIoThread(), serverExchange.getConnection().getWorker(), this.defaultSessionTimeout);
         if (sessionID != null) {
            if (!this.saveSessionID(sessionID, session)) {
               throw UndertowMessages.MESSAGES.sessionWithIdAlreadyExists(sessionID);
            }
         } else {
            sessionID = this.createAndSaveNewID(session);
         }

         session.setId(sessionID);
         if (this.evictionQueue != null) {
            session.setEvictionToken(this.evictionQueue.offerLastAndReturnToken(sessionID));
         }

         UndertowLogger.SESSION_LOGGER.debugf("Created session with id %s for exchange %s", sessionID, serverExchange);
         config.setSessionId(serverExchange, session.getId());
         session.bumpTimeout();
         this.sessionListeners.sessionCreated(session, serverExchange);
         serverExchange.putAttachment(this.NEW_SESSION, session);
         if (this.statisticsEnabled) {
            this.createdSessionCount.incrementAndGet();

            int highest;
            int sessionSize;
            do {
               highest = this.highestSessionCount.get();
               sessionSize = this.sessions.size();
            } while(sessionSize > highest && !this.highestSessionCount.compareAndSet(highest, sessionSize));
         }

         return session;
      }
   }

   private boolean saveSessionID(String sessionID, SessionImpl session) {
      return this.sessions.putIfAbsent(sessionID, session) == null;
   }

   private String createAndSaveNewID(SessionImpl session) {
      for(int i = 0; i < 100; ++i) {
         String sessionID = this.sessionIdGenerator.createSessionId();
         if (this.saveSessionID(sessionID, session)) {
            return sessionID;
         }
      }

      throw UndertowMessages.MESSAGES.couldNotGenerateUniqueSessionId();
   }

   public Session getSession(HttpServerExchange serverExchange, SessionConfig config) {
      if (serverExchange != null) {
         SessionImpl newSession = (SessionImpl)serverExchange.getAttachment(this.NEW_SESSION);
         if (newSession != null) {
            return newSession;
         }
      }

      String sessionId = config.findSessionId(serverExchange);
      SessionImpl session = (SessionImpl)this.getSession(sessionId);
      if (session != null && serverExchange != null) {
         session.requestStarted(serverExchange);
      }

      return session;
   }

   public Session getSession(String sessionId) {
      if (sessionId == null) {
         return null;
      } else {
         SessionImpl sess = (SessionImpl)this.sessions.get(sessionId);
         if (sess == null) {
            return null;
         } else {
            if (sess.getId() == null) {
               sess.setId(sessionId);
            }

            return sess;
         }
      }
   }

   public synchronized void registerSessionListener(SessionListener listener) {
      UndertowLogger.SESSION_LOGGER.debugf("Registered session listener %s", listener);
      this.sessionListeners.addSessionListener(listener);
   }

   public synchronized void removeSessionListener(SessionListener listener) {
      UndertowLogger.SESSION_LOGGER.debugf("Removed session listener %s", listener);
      this.sessionListeners.removeSessionListener(listener);
   }

   public void setDefaultSessionTimeout(int timeout) {
      UndertowLogger.SESSION_LOGGER.debugf("Setting default session timeout to %s", timeout);
      this.defaultSessionTimeout = timeout;
   }

   public Set<String> getTransientSessions() {
      return this.getAllSessions();
   }

   public Set<String> getActiveSessions() {
      return this.getAllSessions();
   }

   public Set<String> getAllSessions() {
      return new HashSet(this.sessions.keySet());
   }

   public boolean equals(Object object) {
      if (!(object instanceof SessionManager)) {
         return false;
      } else {
         SessionManager manager = (SessionManager)object;
         return this.deploymentName.equals(manager.getDeploymentName());
      }
   }

   public int hashCode() {
      return this.deploymentName.hashCode();
   }

   public String toString() {
      return this.deploymentName;
   }

   public SessionManagerStatistics getStatistics() {
      return this;
   }

   public long getCreatedSessionCount() {
      return this.createdSessionCount.get();
   }

   public long getMaxActiveSessions() {
      return (long)this.maxSize;
   }

   public long getHighestSessionCount() {
      return (long)this.highestSessionCount.get();
   }

   public long getActiveSessionCount() {
      return (long)this.sessions.size();
   }

   public long getExpiredSessionCount() {
      return this.expiredSessionCount;
   }

   public long getRejectedSessions() {
      return this.rejectedSessionCount.get();
   }

   public long getMaxSessionAliveTime() {
      return this.longestSessionLifetime;
   }

   public synchronized long getAverageSessionAliveTime() {
      return this.expiredSessionCount == 0L ? 0L : (new BigDecimal(this.totalSessionLifetime)).divide(BigDecimal.valueOf(this.expiredSessionCount), MathContext.DECIMAL128).longValue();
   }

   public long getStartTime() {
      return this.startTime;
   }

   private static class SessionImpl implements Session {
      final AttachmentKey<Long> FIRST_REQUEST_ACCESS;
      final InMemorySessionManager sessionManager;
      final ConcurrentMap<String, Object> attributes;
      volatile long lastAccessed;
      final long creationTime;
      volatile int maxInactiveInterval;
      static volatile AtomicReferenceFieldUpdater<SessionImpl, Object> evictionTokenUpdater = (AtomicReferenceFieldUpdater)AccessController.doPrivileged(new PrivilegedAction<AtomicReferenceFieldUpdater<SessionImpl, Object>>() {
         public AtomicReferenceFieldUpdater<SessionImpl, Object> run() {
            return InMemorySessionManager.SessionImpl.createTokenUpdater();
         }
      });
      private volatile String sessionId;
      private volatile Object evictionToken;
      private final SessionConfig sessionCookieConfig;
      private volatile long expireTime;
      private volatile boolean invalid;
      private volatile boolean invalidationStarted;
      final XnioIoThread executor;
      final XnioWorker worker;
      XnioExecutor.Key timerCancelKey;
      Runnable cancelTask;

      private static AtomicReferenceFieldUpdater<SessionImpl, Object> createTokenUpdater() {
         return AtomicReferenceFieldUpdater.newUpdater(SessionImpl.class, Object.class, "evictionToken");
      }

      private SessionImpl(InMemorySessionManager sessionManager, SessionConfig sessionCookieConfig, XnioIoThread executor, XnioWorker worker, int maxInactiveInterval) {
         this.FIRST_REQUEST_ACCESS = AttachmentKey.create(Long.class);
         this.attributes = new ConcurrentHashMap();
         this.expireTime = -1L;
         this.invalid = false;
         this.invalidationStarted = false;
         this.cancelTask = new Runnable() {
            public void run() {
               SessionImpl.this.worker.execute(new Runnable() {
                  public void run() {
                     long currentTime = System.currentTimeMillis();
                     if (currentTime >= SessionImpl.this.expireTime) {
                        SessionImpl.this.invalidate((HttpServerExchange)null, SessionListener.SessionDestroyedReason.TIMEOUT);
                     } else {
                        SessionImpl.this.timerCancelKey = WorkerUtils.executeAfter(SessionImpl.this.executor, SessionImpl.this.cancelTask, SessionImpl.this.expireTime - currentTime, TimeUnit.MILLISECONDS);
                     }

                  }
               });
            }
         };
         this.sessionManager = sessionManager;
         this.sessionCookieConfig = sessionCookieConfig;
         this.executor = executor;
         this.worker = worker;
         this.creationTime = this.lastAccessed = System.currentTimeMillis();
         this.setMaxInactiveInterval(maxInactiveInterval);
      }

      synchronized void bumpTimeout() {
         if (!this.invalidationStarted) {
            long maxInactiveInterval = this.getMaxInactiveIntervalMilis();
            if (maxInactiveInterval > 0L) {
               long newExpireTime = System.currentTimeMillis() + maxInactiveInterval;
               if (this.timerCancelKey != null && newExpireTime < this.expireTime) {
                  if (!this.timerCancelKey.remove()) {
                     return;
                  }

                  this.timerCancelKey = null;
               }

               this.expireTime = newExpireTime;
               UndertowLogger.SESSION_LOGGER.tracef("Bumping timeout for session %s to %s", this.sessionId, this.expireTime);
               if (this.timerCancelKey == null) {
                  this.timerCancelKey = this.executor.executeAfter(this.cancelTask, maxInactiveInterval + 1L, TimeUnit.MILLISECONDS);
               }
            } else {
               this.expireTime = -1L;
               if (this.timerCancelKey != null) {
                  this.timerCancelKey.remove();
                  this.timerCancelKey = null;
               }
            }

         }
      }

      private void setEvictionToken(Object evictionToken) {
         this.evictionToken = evictionToken;
         if (evictionToken != null && evictionTokenUpdater.compareAndSet(this, evictionToken, (Object)null)) {
            this.sessionManager.evictionQueue.removeToken(evictionToken);
            this.evictionToken = this.sessionManager.evictionQueue.offerLastAndReturnToken(this.sessionId);
         }

      }

      private void setId(String sessionId) {
         this.sessionId = sessionId;
      }

      public String getId() {
         return this.sessionId;
      }

      void requestStarted(HttpServerExchange serverExchange) {
         Long existing = (Long)serverExchange.getAttachment(this.FIRST_REQUEST_ACCESS);
         if (existing == null && !this.invalid) {
            serverExchange.putAttachment(this.FIRST_REQUEST_ACCESS, System.currentTimeMillis());
         }

         this.bumpTimeout();
      }

      public void requestDone(HttpServerExchange serverExchange) {
         Long existing = (Long)serverExchange.getAttachment(this.FIRST_REQUEST_ACCESS);
         if (existing != null) {
            this.lastAccessed = existing;
         }

         this.bumpTimeout();
      }

      public long getCreationTime() {
         if (this.invalid) {
            throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
         } else {
            return this.creationTime;
         }
      }

      public long getLastAccessedTime() {
         if (this.invalid) {
            throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
         } else {
            return this.lastAccessed;
         }
      }

      public void setMaxInactiveInterval(int interval) {
         if (this.invalid) {
            throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
         } else {
            UndertowLogger.SESSION_LOGGER.debugf("Setting max inactive interval for %s to %s", this.sessionId, interval);
            this.maxInactiveInterval = interval;
            this.bumpTimeout();
         }
      }

      public int getMaxInactiveInterval() {
         if (this.invalid) {
            throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
         } else {
            return this.maxInactiveInterval;
         }
      }

      private long getMaxInactiveIntervalMilis() {
         if (this.invalid) {
            throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
         } else {
            return (long)this.maxInactiveInterval * 1000L;
         }
      }

      public Object getAttribute(String name) {
         if (this.invalid) {
            throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
         } else {
            return this.attributes.get(name);
         }
      }

      public Set<String> getAttributeNames() {
         if (this.invalid) {
            throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
         } else {
            return this.attributes.keySet();
         }
      }

      public Object setAttribute(String name, Object value) {
         if (value == null) {
            return this.removeAttribute(name);
         } else if (this.invalid) {
            throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
         } else {
            Object existing = this.attributes.put(name, value);
            if (existing == null) {
               this.sessionManager.sessionListeners.attributeAdded(this, name, value);
            } else {
               this.sessionManager.sessionListeners.attributeUpdated(this, name, value, existing);
            }

            UndertowLogger.SESSION_LOGGER.tracef("Setting session attribute %s to %s for session %s", name, value, this.sessionId);
            return existing;
         }
      }

      public Object removeAttribute(String name) {
         if (this.invalid) {
            throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
         } else {
            Object existing = this.attributes.remove(name);
            this.sessionManager.sessionListeners.attributeRemoved(this, name, existing);
            UndertowLogger.SESSION_LOGGER.tracef("Removing session attribute %s for session %s", name, this.sessionId);
            return existing;
         }
      }

      public void invalidate(HttpServerExchange exchange) {
         this.invalidate(exchange, SessionListener.SessionDestroyedReason.INVALIDATED);
         if (exchange != null) {
            exchange.removeAttachment(this.sessionManager.NEW_SESSION);
         }

         Object evictionToken = this.evictionToken;
         if (evictionToken != null) {
            this.sessionManager.evictionQueue.removeToken(evictionToken);
         }

      }

      void invalidate(HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {
         synchronized(this) {
            if (this.timerCancelKey != null) {
               this.timerCancelKey.remove();
            }

            SessionImpl sess = (SessionImpl)this.sessionManager.sessions.remove(this.sessionId);
            if (sess == null) {
               if (reason == SessionListener.SessionDestroyedReason.INVALIDATED) {
                  throw UndertowMessages.MESSAGES.sessionAlreadyInvalidated();
               }

               return;
            }

            this.invalidationStarted = true;
         }

         UndertowLogger.SESSION_LOGGER.debugf("Invalidating session %s for exchange %s", this.sessionId, exchange);
         this.sessionManager.sessionListeners.sessionDestroyed(this, exchange, reason);
         this.invalid = true;
         if (this.sessionManager.statisticsEnabled) {
            long life = System.currentTimeMillis() - this.creationTime;
            synchronized(this.sessionManager) {
               this.sessionManager.expiredSessionCount++;
               this.sessionManager.totalSessionLifetime = this.sessionManager.totalSessionLifetime.add(BigInteger.valueOf(life));
               if (this.sessionManager.longestSessionLifetime < life) {
                  this.sessionManager.longestSessionLifetime = life;
               }
            }
         }

         if (exchange != null) {
            this.sessionCookieConfig.clearSession(exchange, this.getId());
         }

      }

      public SessionManager getSessionManager() {
         return this.sessionManager;
      }

      public String changeSessionId(HttpServerExchange exchange, SessionConfig config) {
         String oldId = this.sessionId;
         String newId = this.sessionManager.createAndSaveNewID(this);
         this.sessionId = newId;
         if (!this.invalid) {
            config.setSessionId(exchange, this.getId());
         }

         this.sessionManager.sessions.remove(oldId);
         this.sessionManager.sessionListeners.sessionIdChanged(this, oldId);
         UndertowLogger.SESSION_LOGGER.debugf("Changing session id %s to %s", oldId, newId);
         return newId;
      }

      private synchronized void destroy() {
         if (this.timerCancelKey != null) {
            this.timerCancelKey.remove();
         }

         this.cancelTask = null;
      }

      // $FF: synthetic method
      SessionImpl(InMemorySessionManager x0, SessionConfig x1, XnioIoThread x2, XnioWorker x3, int x4, Object x5) {
         this(x0, x1, x2, x3, x4);
      }
   }
}
