/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.server.session.SecureRandomSessionIdGenerator;
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.server.session.SessionManager;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.jboss.logging.Logger;
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
/*     */ public class InMemorySingleSignOnManager
/*     */   implements SingleSignOnManager
/*     */ {
/*  39 */   private static final Logger log = Logger.getLogger(InMemorySingleSignOnManager.class);
/*     */   
/*  41 */   private static final SecureRandomSessionIdGenerator SECURE_RANDOM_SESSION_ID_GENERATOR = new SecureRandomSessionIdGenerator();
/*     */   
/*  43 */   private final Map<String, SingleSignOn> ssoEntries = new ConcurrentHashMap<>();
/*     */ 
/*     */   
/*     */   public SingleSignOn findSingleSignOn(String ssoId) {
/*  47 */     return this.ssoEntries.get(ssoId);
/*     */   }
/*     */ 
/*     */   
/*     */   public SingleSignOn createSingleSignOn(Account account, String mechanism) {
/*  52 */     String id = SECURE_RANDOM_SESSION_ID_GENERATOR.createSessionId();
/*  53 */     SingleSignOn entry = new SimpleSingleSignOnEntry(id, account, mechanism);
/*  54 */     this.ssoEntries.put(id, entry);
/*  55 */     if (log.isTraceEnabled()) {
/*  56 */       log.tracef("Creating SSO ID %s for Principal %s and Roles %s.", id, account.getPrincipal().getName(), account.getRoles().toString());
/*     */     }
/*  58 */     return entry;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeSingleSignOn(SingleSignOn sso) {
/*  63 */     if (log.isTraceEnabled()) {
/*  64 */       log.tracef("Removing SSO ID %s.", sso.getId());
/*     */     }
/*  66 */     this.ssoEntries.remove(sso.getId());
/*     */   }
/*     */   
/*     */   private static class SimpleSingleSignOnEntry implements SingleSignOn {
/*     */     private final String id;
/*     */     private final Account account;
/*     */     private final String mechanismName;
/*  73 */     private final Map<SessionManager, Session> sessions = (Map<SessionManager, Session>)new CopyOnWriteMap();
/*     */     
/*     */     SimpleSingleSignOnEntry(String id, Account account, String mechanismName) {
/*  76 */       this.id = id;
/*  77 */       this.account = account;
/*  78 */       this.mechanismName = mechanismName;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getId() {
/*  83 */       return this.id;
/*     */     }
/*     */ 
/*     */     
/*     */     public Account getAccount() {
/*  88 */       return this.account;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getMechanismName() {
/*  93 */       return this.mechanismName;
/*     */     }
/*     */ 
/*     */     
/*     */     public Iterator<Session> iterator() {
/*  98 */       return Collections.<Session>unmodifiableCollection(this.sessions.values()).iterator();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean contains(Session session) {
/* 103 */       return this.sessions.containsKey(session.getSessionManager());
/*     */     }
/*     */ 
/*     */     
/*     */     public Session getSession(SessionManager manager) {
/* 108 */       return this.sessions.get(manager);
/*     */     }
/*     */ 
/*     */     
/*     */     public void add(Session session) {
/* 113 */       this.sessions.put(session.getSessionManager(), session);
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove(Session session) {
/* 118 */       this.sessions.remove(session.getSessionManager());
/*     */     }
/*     */     
/*     */     public void close() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\InMemorySingleSignOnManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */