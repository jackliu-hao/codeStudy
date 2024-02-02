/*     */ package io.undertow.server.session;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.ConcurrentDirectDeque;
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.math.MathContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class InMemorySessionManager
/*     */   implements SessionManager, SessionManagerStatistics
/*     */ {
/*  55 */   private final AttachmentKey<SessionImpl> NEW_SESSION = AttachmentKey.create(SessionImpl.class);
/*     */   
/*     */   private final SessionIdGenerator sessionIdGenerator;
/*     */   
/*     */   private final ConcurrentMap<String, SessionImpl> sessions;
/*     */   
/*  61 */   private final SessionListeners sessionListeners = new SessionListeners();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   private volatile int defaultSessionTimeout = 1800;
/*     */   
/*     */   private final int maxSize;
/*     */   
/*     */   private final ConcurrentDirectDeque<String> evictionQueue;
/*     */   
/*     */   private final String deploymentName;
/*     */   
/*  74 */   private final AtomicLong createdSessionCount = new AtomicLong();
/*  75 */   private final AtomicLong rejectedSessionCount = new AtomicLong();
/*  76 */   private volatile long longestSessionLifetime = 0L;
/*  77 */   private volatile long expiredSessionCount = 0L;
/*  78 */   private volatile BigInteger totalSessionLifetime = BigInteger.ZERO;
/*  79 */   private final AtomicInteger highestSessionCount = new AtomicInteger();
/*     */   
/*     */   private final boolean statisticsEnabled;
/*     */   
/*     */   private volatile long startTime;
/*     */   
/*     */   private final boolean expireOldestUnusedSessionOnMax;
/*     */ 
/*     */   
/*     */   public InMemorySessionManager(String deploymentName, int maxSessions, boolean expireOldestUnusedSessionOnMax) {
/*  89 */     this(new SecureRandomSessionIdGenerator(), deploymentName, maxSessions, expireOldestUnusedSessionOnMax);
/*     */   }
/*     */   
/*     */   public InMemorySessionManager(SessionIdGenerator sessionIdGenerator, String deploymentName, int maxSessions, boolean expireOldestUnusedSessionOnMax) {
/*  93 */     this(sessionIdGenerator, deploymentName, maxSessions, expireOldestUnusedSessionOnMax, true);
/*     */   }
/*     */   
/*     */   public InMemorySessionManager(SessionIdGenerator sessionIdGenerator, String deploymentName, int maxSessions, boolean expireOldestUnusedSessionOnMax, boolean statisticsEnabled) {
/*  97 */     this.sessionIdGenerator = sessionIdGenerator;
/*  98 */     this.deploymentName = deploymentName;
/*  99 */     this.statisticsEnabled = statisticsEnabled;
/* 100 */     this.expireOldestUnusedSessionOnMax = expireOldestUnusedSessionOnMax;
/* 101 */     this.sessions = new ConcurrentHashMap<>();
/* 102 */     this.maxSize = maxSessions;
/* 103 */     ConcurrentDirectDeque<String> evictionQueue = null;
/* 104 */     if (maxSessions > 0 && expireOldestUnusedSessionOnMax) {
/* 105 */       evictionQueue = ConcurrentDirectDeque.newInstance();
/*     */     }
/* 107 */     this.evictionQueue = evictionQueue;
/*     */   }
/*     */   
/*     */   public InMemorySessionManager(String deploymentName, int maxSessions) {
/* 111 */     this(deploymentName, maxSessions, false);
/*     */   }
/*     */   
/*     */   public InMemorySessionManager(String id) {
/* 115 */     this(id, -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDeploymentName() {
/* 120 */     return this.deploymentName;
/*     */   }
/*     */ 
/*     */   
/*     */   public void start() {
/* 125 */     this.createdSessionCount.set(0L);
/* 126 */     this.expiredSessionCount = 0L;
/* 127 */     this.rejectedSessionCount.set(0L);
/* 128 */     this.totalSessionLifetime = BigInteger.ZERO;
/* 129 */     this.startTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 134 */     for (Map.Entry<String, SessionImpl> session : this.sessions.entrySet()) {
/* 135 */       SessionImpl sessionValue = session.getValue();
/* 136 */       sessionValue.destroy();
/* 137 */       if (sessionValue.getId() == null)
/*     */       {
/*     */ 
/*     */         
/* 141 */         sessionValue.setId(session.getKey());
/*     */       }
/* 143 */       this.sessionListeners.sessionDestroyed(session.getValue(), null, SessionListener.SessionDestroyedReason.UNDEPLOY);
/*     */     } 
/* 145 */     this.sessions.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public Session createSession(HttpServerExchange serverExchange, SessionConfig config) {
/* 150 */     if (this.maxSize > 0) {
/* 151 */       if (this.expireOldestUnusedSessionOnMax) {
/* 152 */         while (this.sessions.size() >= this.maxSize && !this.evictionQueue.isEmpty()) {
/*     */           
/* 154 */           String key = (String)this.evictionQueue.poll();
/* 155 */           if (key == null) {
/*     */             break;
/*     */           }
/* 158 */           UndertowLogger.REQUEST_LOGGER.debugf("Removing session %s as max size has been hit", key);
/* 159 */           SessionImpl toRemove = this.sessions.get(key);
/* 160 */           if (toRemove != null) {
/* 161 */             toRemove.invalidate(null, SessionListener.SessionDestroyedReason.TIMEOUT);
/*     */           }
/*     */         } 
/* 164 */       } else if (this.sessions.size() >= this.maxSize) {
/* 165 */         if (this.statisticsEnabled) {
/* 166 */           this.rejectedSessionCount.incrementAndGet();
/*     */         }
/* 168 */         throw UndertowMessages.MESSAGES.tooManySessions(this.maxSize);
/*     */       } 
/*     */     }
/* 171 */     if (config == null) {
/* 172 */       throw UndertowMessages.MESSAGES.couldNotFindSessionCookieConfig();
/*     */     }
/*     */     
/* 175 */     String sessionID = config.findSessionId(serverExchange);
/* 176 */     SessionImpl session = new SessionImpl(this, config, serverExchange.getIoThread(), serverExchange.getConnection().getWorker(), this.defaultSessionTimeout);
/* 177 */     if (sessionID != null) {
/* 178 */       if (!saveSessionID(sessionID, session)) {
/* 179 */         throw UndertowMessages.MESSAGES.sessionWithIdAlreadyExists(sessionID);
/*     */       }
/*     */     } else {
/* 182 */       sessionID = createAndSaveNewID(session);
/*     */     } 
/* 184 */     session.setId(sessionID);
/* 185 */     if (this.evictionQueue != null) {
/* 186 */       session.setEvictionToken(this.evictionQueue.offerLastAndReturnToken(sessionID));
/*     */     }
/* 188 */     UndertowLogger.SESSION_LOGGER.debugf("Created session with id %s for exchange %s", sessionID, serverExchange);
/* 189 */     config.setSessionId(serverExchange, session.getId());
/* 190 */     session.bumpTimeout();
/* 191 */     this.sessionListeners.sessionCreated(session, serverExchange);
/* 192 */     serverExchange.putAttachment(this.NEW_SESSION, session);
/*     */     
/* 194 */     if (this.statisticsEnabled) {
/* 195 */       int highest, sessionSize; this.createdSessionCount.incrementAndGet();
/*     */ 
/*     */       
/*     */       do {
/* 199 */         highest = this.highestSessionCount.get();
/* 200 */         sessionSize = this.sessions.size();
/* 201 */         if (sessionSize <= highest) {
/*     */           break;
/*     */         }
/* 204 */       } while (!this.highestSessionCount.compareAndSet(highest, sessionSize));
/*     */     } 
/*     */     
/* 207 */     return session;
/*     */   }
/*     */   
/*     */   private boolean saveSessionID(String sessionID, SessionImpl session) {
/* 211 */     return (this.sessions.putIfAbsent(sessionID, session) == null);
/*     */   }
/*     */   
/*     */   private String createAndSaveNewID(SessionImpl session) {
/* 215 */     for (int i = 0; i < 100; i++) {
/* 216 */       String sessionID = this.sessionIdGenerator.createSessionId();
/* 217 */       if (saveSessionID(sessionID, session)) {
/* 218 */         return sessionID;
/*     */       }
/*     */     } 
/*     */     
/* 222 */     throw UndertowMessages.MESSAGES.couldNotGenerateUniqueSessionId();
/*     */   }
/*     */ 
/*     */   
/*     */   public Session getSession(HttpServerExchange serverExchange, SessionConfig config) {
/* 227 */     if (serverExchange != null) {
/* 228 */       SessionImpl newSession = (SessionImpl)serverExchange.getAttachment(this.NEW_SESSION);
/* 229 */       if (newSession != null) {
/* 230 */         return newSession;
/*     */       }
/*     */     } 
/* 233 */     String sessionId = config.findSessionId(serverExchange);
/* 234 */     SessionImpl session = (SessionImpl)getSession(sessionId);
/* 235 */     if (session != null && serverExchange != null) {
/* 236 */       session.requestStarted(serverExchange);
/*     */     }
/* 238 */     return session;
/*     */   }
/*     */ 
/*     */   
/*     */   public Session getSession(String sessionId) {
/* 243 */     if (sessionId == null) {
/* 244 */       return null;
/*     */     }
/* 246 */     SessionImpl sess = this.sessions.get(sessionId);
/* 247 */     if (sess == null) {
/* 248 */       return null;
/*     */     }
/* 250 */     if (sess.getId() == null)
/*     */     {
/*     */ 
/*     */       
/* 254 */       sess.setId(sessionId);
/*     */     }
/* 256 */     return sess;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void registerSessionListener(SessionListener listener) {
/* 261 */     UndertowLogger.SESSION_LOGGER.debugf("Registered session listener %s", listener);
/* 262 */     this.sessionListeners.addSessionListener(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void removeSessionListener(SessionListener listener) {
/* 267 */     UndertowLogger.SESSION_LOGGER.debugf("Removed session listener %s", listener);
/* 268 */     this.sessionListeners.removeSessionListener(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDefaultSessionTimeout(int timeout) {
/* 273 */     UndertowLogger.SESSION_LOGGER.debugf("Setting default session timeout to %s", timeout);
/* 274 */     this.defaultSessionTimeout = timeout;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getTransientSessions() {
/* 279 */     return getAllSessions();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getActiveSessions() {
/* 284 */     return getAllSessions();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getAllSessions() {
/* 289 */     return new HashSet<>(this.sessions.keySet());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 294 */     if (!(object instanceof SessionManager)) return false; 
/* 295 */     SessionManager manager = (SessionManager)object;
/* 296 */     return this.deploymentName.equals(manager.getDeploymentName());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 301 */     return this.deploymentName.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 306 */     return this.deploymentName;
/*     */   }
/*     */ 
/*     */   
/*     */   public SessionManagerStatistics getStatistics() {
/* 311 */     return this;
/*     */   }
/*     */   
/*     */   public long getCreatedSessionCount() {
/* 315 */     return this.createdSessionCount.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxActiveSessions() {
/* 320 */     return this.maxSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getHighestSessionCount() {
/* 325 */     return this.highestSessionCount.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getActiveSessionCount() {
/* 330 */     return this.sessions.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getExpiredSessionCount() {
/* 335 */     return this.expiredSessionCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRejectedSessions() {
/* 340 */     return this.rejectedSessionCount.get();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxSessionAliveTime() {
/* 346 */     return this.longestSessionLifetime;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized long getAverageSessionAliveTime() {
/* 352 */     if (this.expiredSessionCount == 0L) {
/* 353 */       return 0L;
/*     */     }
/* 355 */     return (new BigDecimal(this.totalSessionLifetime)).divide(BigDecimal.valueOf(this.expiredSessionCount), MathContext.DECIMAL128).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/* 360 */     return this.startTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class SessionImpl
/*     */     implements Session
/*     */   {
/* 370 */     final AttachmentKey<Long> FIRST_REQUEST_ACCESS = AttachmentKey.create(Long.class);
/*     */     final InMemorySessionManager sessionManager;
/* 372 */     final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();
/*     */ 
/*     */     
/*     */     volatile long lastAccessed;
/*     */     
/*     */     final long creationTime;
/*     */     
/*     */     volatile int maxInactiveInterval;
/*     */ 
/*     */     
/* 382 */     static volatile AtomicReferenceFieldUpdater<SessionImpl, Object> evictionTokenUpdater = AccessController.<AtomicReferenceFieldUpdater<SessionImpl, Object>>doPrivileged(new PrivilegedAction<AtomicReferenceFieldUpdater<SessionImpl, Object>>()
/*     */         {
/*     */           public AtomicReferenceFieldUpdater<InMemorySessionManager.SessionImpl, Object> run() {
/* 385 */             return InMemorySessionManager.SessionImpl.createTokenUpdater();
/*     */           }
/*     */         });
/*     */     private volatile String sessionId;
/*     */     
/*     */     private static AtomicReferenceFieldUpdater<SessionImpl, Object> createTokenUpdater() {
/* 391 */       return AtomicReferenceFieldUpdater.newUpdater(SessionImpl.class, Object.class, "evictionToken");
/*     */     }
/*     */ 
/*     */     
/*     */     private volatile Object evictionToken;
/*     */     
/*     */     private final SessionConfig sessionCookieConfig;
/* 398 */     private volatile long expireTime = -1L;
/*     */     
/*     */     private volatile boolean invalid = false;
/*     */     
/*     */     private volatile boolean invalidationStarted = false;
/*     */     final XnioIoThread executor;
/*     */     final XnioWorker worker;
/*     */     XnioExecutor.Key timerCancelKey;
/*     */     
/* 407 */     Runnable cancelTask = new Runnable()
/*     */       {
/*     */         public void run() {
/* 410 */           InMemorySessionManager.SessionImpl.this.worker.execute(new Runnable()
/*     */               {
/*     */                 public void run() {
/* 413 */                   long currentTime = System.currentTimeMillis();
/* 414 */                   if (currentTime >= InMemorySessionManager.SessionImpl.this.expireTime) {
/* 415 */                     InMemorySessionManager.SessionImpl.this.invalidate(null, SessionListener.SessionDestroyedReason.TIMEOUT);
/*     */                   } else {
/* 417 */                     InMemorySessionManager.SessionImpl.this.timerCancelKey = WorkerUtils.executeAfter(InMemorySessionManager.SessionImpl.this.executor, InMemorySessionManager.SessionImpl.this.cancelTask, InMemorySessionManager.SessionImpl.this.expireTime - currentTime, TimeUnit.MILLISECONDS);
/*     */                   } 
/*     */                 }
/*     */               });
/*     */         }
/*     */       };
/*     */     
/*     */     private SessionImpl(InMemorySessionManager sessionManager, SessionConfig sessionCookieConfig, XnioIoThread executor, XnioWorker worker, int maxInactiveInterval) {
/* 425 */       this.sessionManager = sessionManager;
/* 426 */       this.sessionCookieConfig = sessionCookieConfig;
/* 427 */       this.executor = executor;
/* 428 */       this.worker = worker;
/* 429 */       this.creationTime = this.lastAccessed = System.currentTimeMillis();
/* 430 */       setMaxInactiveInterval(maxInactiveInterval);
/*     */     }
/*     */     
/*     */     synchronized void bumpTimeout() {
/* 434 */       if (this.invalidationStarted) {
/*     */         return;
/*     */       }
/*     */       
/* 438 */       long maxInactiveInterval = getMaxInactiveIntervalMilis();
/* 439 */       if (maxInactiveInterval > 0L) {
/* 440 */         long newExpireTime = System.currentTimeMillis() + maxInactiveInterval;
/* 441 */         if (this.timerCancelKey != null && newExpireTime < this.expireTime) {
/*     */           
/* 443 */           if (!this.timerCancelKey.remove()) {
/*     */             return;
/*     */           }
/* 446 */           this.timerCancelKey = null;
/*     */         } 
/* 448 */         this.expireTime = newExpireTime;
/* 449 */         UndertowLogger.SESSION_LOGGER.tracef("Bumping timeout for session %s to %s", this.sessionId, Long.valueOf(this.expireTime));
/* 450 */         if (this.timerCancelKey == null)
/*     */         {
/*     */ 
/*     */           
/* 454 */           this.timerCancelKey = this.executor.executeAfter(this.cancelTask, maxInactiveInterval + 1L, TimeUnit.MILLISECONDS);
/*     */         }
/*     */       } else {
/* 457 */         this.expireTime = -1L;
/* 458 */         if (this.timerCancelKey != null) {
/* 459 */           this.timerCancelKey.remove();
/* 460 */           this.timerCancelKey = null;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     private void setEvictionToken(Object evictionToken) {
/* 466 */       this.evictionToken = evictionToken;
/* 467 */       if (evictionToken != null) {
/* 468 */         Object token = evictionToken;
/* 469 */         if (evictionTokenUpdater.compareAndSet(this, token, null)) {
/* 470 */           this.sessionManager.evictionQueue.removeToken(token);
/* 471 */           this.evictionToken = this.sessionManager.evictionQueue.offerLastAndReturnToken(this.sessionId);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     private void setId(String sessionId) {
/* 477 */       this.sessionId = sessionId;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getId() {
/* 482 */       return this.sessionId;
/*     */     }
/*     */     
/*     */     void requestStarted(HttpServerExchange serverExchange) {
/* 486 */       Long existing = (Long)serverExchange.getAttachment(this.FIRST_REQUEST_ACCESS);
/* 487 */       if (existing == null && 
/* 488 */         !this.invalid) {
/* 489 */         serverExchange.putAttachment(this.FIRST_REQUEST_ACCESS, Long.valueOf(System.currentTimeMillis()));
/*     */       }
/*     */       
/* 492 */       bumpTimeout();
/*     */     }
/*     */ 
/*     */     
/*     */     public void requestDone(HttpServerExchange serverExchange) {
/* 497 */       Long existing = (Long)serverExchange.getAttachment(this.FIRST_REQUEST_ACCESS);
/* 498 */       if (existing != null) {
/* 499 */         this.lastAccessed = existing.longValue();
/*     */       }
/* 501 */       bumpTimeout();
/*     */     }
/*     */ 
/*     */     
/*     */     public long getCreationTime() {
/* 506 */       if (this.invalid) {
/* 507 */         throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
/*     */       }
/* 509 */       return this.creationTime;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getLastAccessedTime() {
/* 514 */       if (this.invalid) {
/* 515 */         throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
/*     */       }
/* 517 */       return this.lastAccessed;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setMaxInactiveInterval(int interval) {
/* 522 */       if (this.invalid) {
/* 523 */         throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
/*     */       }
/* 525 */       UndertowLogger.SESSION_LOGGER.debugf("Setting max inactive interval for %s to %s", this.sessionId, Integer.valueOf(interval));
/* 526 */       this.maxInactiveInterval = interval;
/* 527 */       bumpTimeout();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMaxInactiveInterval() {
/* 532 */       if (this.invalid) {
/* 533 */         throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
/*     */       }
/* 535 */       return this.maxInactiveInterval;
/*     */     }
/*     */     
/*     */     private long getMaxInactiveIntervalMilis() {
/* 539 */       if (this.invalid) {
/* 540 */         throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
/*     */       }
/* 542 */       return this.maxInactiveInterval * 1000L;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getAttribute(String name) {
/* 547 */       if (this.invalid) {
/* 548 */         throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
/*     */       }
/* 550 */       return this.attributes.get(name);
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> getAttributeNames() {
/* 555 */       if (this.invalid) {
/* 556 */         throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
/*     */       }
/* 558 */       return this.attributes.keySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public Object setAttribute(String name, Object value) {
/* 563 */       if (value == null) {
/* 564 */         return removeAttribute(name);
/*     */       }
/* 566 */       if (this.invalid) {
/* 567 */         throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
/*     */       }
/* 569 */       Object existing = this.attributes.put(name, value);
/* 570 */       if (existing == null) {
/* 571 */         this.sessionManager.sessionListeners.attributeAdded(this, name, value);
/*     */       } else {
/* 573 */         this.sessionManager.sessionListeners.attributeUpdated(this, name, value, existing);
/*     */       } 
/* 575 */       UndertowLogger.SESSION_LOGGER.tracef("Setting session attribute %s to %s for session %s", name, value, this.sessionId);
/* 576 */       return existing;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object removeAttribute(String name) {
/* 581 */       if (this.invalid) {
/* 582 */         throw UndertowMessages.MESSAGES.sessionIsInvalid(this.sessionId);
/*     */       }
/* 584 */       Object existing = this.attributes.remove(name);
/* 585 */       this.sessionManager.sessionListeners.attributeRemoved(this, name, existing);
/* 586 */       UndertowLogger.SESSION_LOGGER.tracef("Removing session attribute %s for session %s", name, this.sessionId);
/* 587 */       return existing;
/*     */     }
/*     */ 
/*     */     
/*     */     public void invalidate(HttpServerExchange exchange) {
/* 592 */       invalidate(exchange, SessionListener.SessionDestroyedReason.INVALIDATED);
/* 593 */       if (exchange != null) {
/* 594 */         exchange.removeAttachment(this.sessionManager.NEW_SESSION);
/*     */       }
/* 596 */       Object evictionToken = this.evictionToken;
/* 597 */       if (evictionToken != null) {
/* 598 */         this.sessionManager.evictionQueue.removeToken(evictionToken);
/*     */       }
/*     */     }
/*     */     
/*     */     void invalidate(HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {
/* 603 */       synchronized (this) {
/* 604 */         if (this.timerCancelKey != null) {
/* 605 */           this.timerCancelKey.remove();
/*     */         }
/* 607 */         SessionImpl sess = (SessionImpl)this.sessionManager.sessions.remove(this.sessionId);
/* 608 */         if (sess == null) {
/* 609 */           if (reason == SessionListener.SessionDestroyedReason.INVALIDATED) {
/* 610 */             throw UndertowMessages.MESSAGES.sessionAlreadyInvalidated();
/*     */           }
/*     */           return;
/*     */         } 
/* 614 */         this.invalidationStarted = true;
/*     */       } 
/* 616 */       UndertowLogger.SESSION_LOGGER.debugf("Invalidating session %s for exchange %s", this.sessionId, exchange);
/*     */       
/* 618 */       this.sessionManager.sessionListeners.sessionDestroyed(this, exchange, reason);
/* 619 */       this.invalid = true;
/*     */       
/* 621 */       if (this.sessionManager.statisticsEnabled) {
/* 622 */         long life = System.currentTimeMillis() - this.creationTime;
/* 623 */         synchronized (this.sessionManager) {
/* 624 */           this.sessionManager.expiredSessionCount++;
/* 625 */           this.sessionManager.totalSessionLifetime = this.sessionManager.totalSessionLifetime.add(BigInteger.valueOf(life));
/* 626 */           if (this.sessionManager.longestSessionLifetime < life) {
/* 627 */             this.sessionManager.longestSessionLifetime = life;
/*     */           }
/*     */         } 
/*     */       } 
/* 631 */       if (exchange != null) {
/* 632 */         this.sessionCookieConfig.clearSession(exchange, getId());
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public SessionManager getSessionManager() {
/* 638 */       return this.sessionManager;
/*     */     }
/*     */ 
/*     */     
/*     */     public String changeSessionId(HttpServerExchange exchange, SessionConfig config) {
/* 643 */       String oldId = this.sessionId;
/* 644 */       String newId = this.sessionManager.createAndSaveNewID(this);
/* 645 */       this.sessionId = newId;
/* 646 */       if (!this.invalid) {
/* 647 */         config.setSessionId(exchange, getId());
/*     */       }
/* 649 */       this.sessionManager.sessions.remove(oldId);
/* 650 */       this.sessionManager.sessionListeners.sessionIdChanged(this, oldId);
/* 651 */       UndertowLogger.SESSION_LOGGER.debugf("Changing session id %s to %s", oldId, newId);
/*     */       
/* 653 */       return newId;
/*     */     }
/*     */     
/*     */     private synchronized void destroy() {
/* 657 */       if (this.timerCancelKey != null) {
/* 658 */         this.timerCancelKey.remove();
/*     */       }
/* 660 */       this.cancelTask = null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\InMemorySessionManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */