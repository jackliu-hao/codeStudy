/*      */ package com.sun.mail.imap;
/*      */ 
/*      */ import com.sun.mail.iap.BadCommandException;
/*      */ import com.sun.mail.iap.CommandFailedException;
/*      */ import com.sun.mail.iap.ConnectionException;
/*      */ import com.sun.mail.iap.ProtocolException;
/*      */ import com.sun.mail.iap.Response;
/*      */ import com.sun.mail.iap.ResponseHandler;
/*      */ import com.sun.mail.imap.protocol.IMAPProtocol;
/*      */ import com.sun.mail.imap.protocol.ListInfo;
/*      */ import com.sun.mail.imap.protocol.Namespaces;
/*      */ import com.sun.mail.util.MailLogger;
/*      */ import com.sun.mail.util.PropUtil;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.net.InetAddress;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import java.util.logging.Level;
/*      */ import javax.mail.AuthenticationFailedException;
/*      */ import javax.mail.Folder;
/*      */ import javax.mail.MessagingException;
/*      */ import javax.mail.PasswordAuthentication;
/*      */ import javax.mail.Quota;
/*      */ import javax.mail.QuotaAwareStore;
/*      */ import javax.mail.Session;
/*      */ import javax.mail.Store;
/*      */ import javax.mail.StoreClosedException;
/*      */ import javax.mail.URLName;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class IMAPStore
/*      */   extends Store
/*      */   implements QuotaAwareStore, ResponseHandler
/*      */ {
/*      */   public static final int RESPONSE = 1000;
/*      */   protected final String name;
/*      */   protected final int defaultPort;
/*      */   protected final boolean isSSL;
/*      */   private final int blksize;
/*      */   private boolean ignoreSize;
/*      */   private final int statusCacheTimeout;
/*      */   private final int appendBufferSize;
/*      */   private final int minIdleTime;
/*  177 */   private volatile int port = -1;
/*      */   
/*      */   protected String host;
/*      */   
/*      */   protected String user;
/*      */   
/*      */   protected String password;
/*      */   
/*      */   protected String proxyAuthUser;
/*      */   
/*      */   protected String authorizationID;
/*      */   
/*      */   protected String saslRealm;
/*      */   
/*      */   private Namespaces namespaces;
/*      */   
/*      */   private boolean disableAuthLogin = false;
/*      */   
/*      */   private boolean disableAuthPlain = false;
/*      */   
/*      */   private boolean disableAuthNtlm = false;
/*      */   
/*      */   private boolean enableStartTLS = false;
/*      */   
/*      */   private boolean requireStartTLS = false;
/*      */   
/*      */   private boolean usingSSL = false;
/*      */   
/*      */   private boolean enableSASL = false;
/*      */   
/*      */   private String[] saslMechanisms;
/*      */   private boolean forcePasswordRefresh = false;
/*      */   private boolean enableImapEvents = false;
/*      */   private String guid;
/*      */   private volatile boolean connectionFailed = false;
/*      */   private volatile boolean forceClose = false;
/*  213 */   private final Object connectionFailedLock = new Object();
/*      */   
/*      */   private boolean debugusername;
/*      */   
/*      */   private boolean debugpassword;
/*      */   
/*      */   protected MailLogger logger;
/*      */   
/*      */   private boolean messageCacheDebug;
/*  222 */   private volatile Constructor folderConstructor = null;
/*  223 */   private volatile Constructor folderConstructorLI = null;
/*      */   
/*      */   private final ConnectionPool pool;
/*      */ 
/*      */   
/*      */   static class ConnectionPool
/*      */   {
/*  230 */     private Vector authenticatedConnections = new Vector();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Vector folders;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean storeConnectionInUse = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private long lastTimePruned;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final boolean separateStoreConnection;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final long clientTimeoutInterval;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final long serverTimeoutInterval;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final int poolSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final long pruningInterval;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final MailLogger logger;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static final int RUNNING = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static final int IDLE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static final int ABORTING = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  305 */     private int idleState = 0;
/*      */     private IMAPProtocol idleProtocol;
/*      */     
/*      */     ConnectionPool(String name, MailLogger plogger, Session session) {
/*  309 */       this.lastTimePruned = System.currentTimeMillis();
/*      */       
/*  311 */       boolean debug = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".connectionpool.debug", false);
/*      */       
/*  313 */       this.logger = plogger.getSubLogger("connectionpool", "DEBUG IMAP CP", debug);
/*      */ 
/*      */ 
/*      */       
/*  317 */       int size = PropUtil.getIntSessionProperty(session, "mail." + name + ".connectionpoolsize", -1);
/*      */       
/*  319 */       if (size > 0) {
/*  320 */         this.poolSize = size;
/*  321 */         if (this.logger.isLoggable(Level.CONFIG))
/*  322 */           this.logger.config("mail.imap.connectionpoolsize: " + this.poolSize); 
/*      */       } else {
/*  324 */         this.poolSize = 1;
/*      */       } 
/*      */       
/*  327 */       int connectionPoolTimeout = PropUtil.getIntSessionProperty(session, "mail." + name + ".connectionpooltimeout", -1);
/*      */       
/*  329 */       if (connectionPoolTimeout > 0) {
/*  330 */         this.clientTimeoutInterval = connectionPoolTimeout;
/*  331 */         if (this.logger.isLoggable(Level.CONFIG)) {
/*  332 */           this.logger.config("mail.imap.connectionpooltimeout: " + this.clientTimeoutInterval);
/*      */         }
/*      */       } else {
/*  335 */         this.clientTimeoutInterval = 45000L;
/*      */       } 
/*      */       
/*  338 */       int serverTimeout = PropUtil.getIntSessionProperty(session, "mail." + name + ".servertimeout", -1);
/*      */       
/*  340 */       if (serverTimeout > 0) {
/*  341 */         this.serverTimeoutInterval = serverTimeout;
/*  342 */         if (this.logger.isLoggable(Level.CONFIG)) {
/*  343 */           this.logger.config("mail.imap.servertimeout: " + this.serverTimeoutInterval);
/*      */         }
/*      */       } else {
/*  346 */         this.serverTimeoutInterval = 1800000L;
/*      */       } 
/*      */       
/*  349 */       int pruning = PropUtil.getIntSessionProperty(session, "mail." + name + ".pruninginterval", -1);
/*      */       
/*  351 */       if (pruning > 0) {
/*  352 */         this.pruningInterval = pruning;
/*  353 */         if (this.logger.isLoggable(Level.CONFIG)) {
/*  354 */           this.logger.config("mail.imap.pruninginterval: " + this.pruningInterval);
/*      */         }
/*      */       } else {
/*  357 */         this.pruningInterval = 60000L;
/*      */       } 
/*      */ 
/*      */       
/*  361 */       this.separateStoreConnection = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".separatestoreconnection", false);
/*      */ 
/*      */       
/*  364 */       if (this.separateStoreConnection) {
/*  365 */         this.logger.config("dedicate a store connection");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  379 */   private ResponseHandler nonStoreResponseHandler = new ResponseHandler()
/*      */     {
/*      */       public void handleResponse(Response r) {
/*  382 */         if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE())
/*  383 */           IMAPStore.this.handleResponseCode(r); 
/*  384 */         if (r.isBYE())
/*  385 */           IMAPStore.this.logger.fine("IMAPStore non-store connection dead"); 
/*      */       }
/*      */       
/*      */       private final IMAPStore this$0;
/*      */     };
/*      */   
/*      */   static final boolean $assertionsDisabled;
/*      */   
/*      */   public IMAPStore(Session session, URLName url) {
/*  394 */     this(session, url, "imap", false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPStore(Session session, URLName url, String name, boolean isSSL) {
/*  402 */     super(session, url);
/*  403 */     if (url != null)
/*  404 */       name = url.getProtocol(); 
/*  405 */     this.name = name;
/*  406 */     if (!isSSL) {
/*  407 */       isSSL = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".ssl.enable", false);
/*      */     }
/*  409 */     if (isSSL) {
/*  410 */       this.defaultPort = 993;
/*      */     } else {
/*  412 */       this.defaultPort = 143;
/*  413 */     }  this.isSSL = isSSL;
/*      */     
/*  415 */     this.debug = session.getDebug();
/*  416 */     this.debugusername = PropUtil.getBooleanSessionProperty(session, "mail.debug.auth.username", true);
/*      */     
/*  418 */     this.debugpassword = PropUtil.getBooleanSessionProperty(session, "mail.debug.auth.password", false);
/*      */     
/*  420 */     this.logger = new MailLogger(getClass(), "DEBUG " + name.toUpperCase(), session);
/*      */ 
/*      */     
/*  423 */     boolean partialFetch = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".partialfetch", true);
/*      */     
/*  425 */     if (!partialFetch) {
/*  426 */       this.blksize = -1;
/*  427 */       this.logger.config("mail.imap.partialfetch: false");
/*      */     } else {
/*  429 */       this.blksize = PropUtil.getIntSessionProperty(session, "mail." + name + ".fetchsize", 16384);
/*      */       
/*  431 */       if (this.logger.isLoggable(Level.CONFIG)) {
/*  432 */         this.logger.config("mail.imap.fetchsize: " + this.blksize);
/*      */       }
/*      */     } 
/*  435 */     this.ignoreSize = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".ignorebodystructuresize", false);
/*      */     
/*  437 */     if (this.logger.isLoggable(Level.CONFIG)) {
/*  438 */       this.logger.config("mail.imap.ignorebodystructuresize: " + this.ignoreSize);
/*      */     }
/*  440 */     this.statusCacheTimeout = PropUtil.getIntSessionProperty(session, "mail." + name + ".statuscachetimeout", 1000);
/*      */     
/*  442 */     if (this.logger.isLoggable(Level.CONFIG)) {
/*  443 */       this.logger.config("mail.imap.statuscachetimeout: " + this.statusCacheTimeout);
/*      */     }
/*      */     
/*  446 */     this.appendBufferSize = PropUtil.getIntSessionProperty(session, "mail." + name + ".appendbuffersize", -1);
/*      */     
/*  448 */     if (this.logger.isLoggable(Level.CONFIG)) {
/*  449 */       this.logger.config("mail.imap.appendbuffersize: " + this.appendBufferSize);
/*      */     }
/*  451 */     this.minIdleTime = PropUtil.getIntSessionProperty(session, "mail." + name + ".minidletime", 10);
/*      */     
/*  453 */     if (this.logger.isLoggable(Level.CONFIG)) {
/*  454 */       this.logger.config("mail.imap.minidletime: " + this.minIdleTime);
/*      */     }
/*      */     
/*  457 */     String s = session.getProperty("mail." + name + ".proxyauth.user");
/*  458 */     if (s != null) {
/*  459 */       this.proxyAuthUser = s;
/*  460 */       if (this.logger.isLoggable(Level.CONFIG)) {
/*  461 */         this.logger.config("mail.imap.proxyauth.user: " + this.proxyAuthUser);
/*      */       }
/*      */     } 
/*      */     
/*  465 */     this.disableAuthLogin = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".auth.login.disable", false);
/*      */     
/*  467 */     if (this.disableAuthLogin) {
/*  468 */       this.logger.config("disable AUTH=LOGIN");
/*      */     }
/*      */     
/*  471 */     this.disableAuthPlain = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".auth.plain.disable", false);
/*      */     
/*  473 */     if (this.disableAuthPlain) {
/*  474 */       this.logger.config("disable AUTH=PLAIN");
/*      */     }
/*      */     
/*  477 */     this.disableAuthNtlm = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".auth.ntlm.disable", false);
/*      */     
/*  479 */     if (this.disableAuthNtlm) {
/*  480 */       this.logger.config("disable AUTH=NTLM");
/*      */     }
/*      */     
/*  483 */     this.enableStartTLS = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".starttls.enable", false);
/*      */     
/*  485 */     if (this.enableStartTLS) {
/*  486 */       this.logger.config("enable STARTTLS");
/*      */     }
/*      */     
/*  489 */     this.requireStartTLS = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".starttls.required", false);
/*      */     
/*  491 */     if (this.requireStartTLS) {
/*  492 */       this.logger.config("require STARTTLS");
/*      */     }
/*      */     
/*  495 */     this.enableSASL = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".sasl.enable", false);
/*      */     
/*  497 */     if (this.enableSASL) {
/*  498 */       this.logger.config("enable SASL");
/*      */     }
/*      */     
/*  501 */     if (this.enableSASL) {
/*  502 */       s = session.getProperty("mail." + name + ".sasl.mechanisms");
/*  503 */       if (s != null && s.length() > 0) {
/*  504 */         if (this.logger.isLoggable(Level.CONFIG))
/*  505 */           this.logger.config("SASL mechanisms allowed: " + s); 
/*  506 */         Vector v = new Vector(5);
/*  507 */         StringTokenizer st = new StringTokenizer(s, " ,");
/*  508 */         while (st.hasMoreTokens()) {
/*  509 */           String m = st.nextToken();
/*  510 */           if (m.length() > 0)
/*  511 */             v.addElement(m); 
/*      */         } 
/*  513 */         this.saslMechanisms = new String[v.size()];
/*  514 */         v.copyInto((Object[])this.saslMechanisms);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  519 */     s = session.getProperty("mail." + name + ".sasl.authorizationid");
/*  520 */     if (s != null) {
/*  521 */       this.authorizationID = s;
/*  522 */       this.logger.log(Level.CONFIG, "mail.imap.sasl.authorizationid: {0}", this.authorizationID);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  527 */     s = session.getProperty("mail." + name + ".sasl.realm");
/*  528 */     if (s != null) {
/*  529 */       this.saslRealm = s;
/*  530 */       this.logger.log(Level.CONFIG, "mail.imap.sasl.realm: {0}", this.saslRealm);
/*      */     } 
/*      */ 
/*      */     
/*  534 */     this.forcePasswordRefresh = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".forcepasswordrefresh", false);
/*      */     
/*  536 */     if (this.forcePasswordRefresh) {
/*  537 */       this.logger.config("enable forcePasswordRefresh");
/*      */     }
/*      */     
/*  540 */     this.enableImapEvents = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".enableimapevents", false);
/*      */     
/*  542 */     if (this.enableImapEvents) {
/*  543 */       this.logger.config("enable IMAP events");
/*      */     }
/*      */     
/*  546 */     this.messageCacheDebug = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".messagecache.debug", false);
/*      */ 
/*      */     
/*  549 */     this.guid = session.getProperty("mail." + name + ".yahoo.guid");
/*  550 */     if (this.guid != null) {
/*  551 */       this.logger.log(Level.CONFIG, "mail.imap.yahoo.guid: {0}", this.guid);
/*      */     }
/*  553 */     s = session.getProperty("mail." + name + ".folder.class");
/*  554 */     if (s != null) {
/*  555 */       this.logger.log(Level.CONFIG, "IMAP: folder class: {0}", s);
/*      */       try {
/*  557 */         ClassLoader cl = getClass().getClassLoader();
/*      */ 
/*      */         
/*  560 */         Class folderClass = null;
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/*  565 */           folderClass = Class.forName(s, false, cl);
/*  566 */         } catch (ClassNotFoundException ex1) {
/*      */ 
/*      */ 
/*      */           
/*  570 */           folderClass = Class.forName(s);
/*      */         } 
/*      */         
/*  573 */         Class[] c = { String.class, char.class, IMAPStore.class, Boolean.class };
/*      */         
/*  575 */         this.folderConstructor = folderClass.getConstructor(c);
/*  576 */         Class[] c2 = { ListInfo.class, IMAPStore.class };
/*  577 */         this.folderConstructorLI = folderClass.getConstructor(c2);
/*  578 */       } catch (Exception ex) {
/*  579 */         this.logger.log(Level.CONFIG, "IMAP: failed to load folder class", ex);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  584 */     this.pool = new ConnectionPool(name, this.logger, session);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized boolean protocolConnect(String host, int pport, String user, String password) throws MessagingException {
/*  601 */     IMAPProtocol protocol = null;
/*      */ 
/*      */     
/*  604 */     if (host == null || password == null || user == null) {
/*  605 */       if (this.logger.isLoggable(Level.FINE)) {
/*  606 */         this.logger.fine("protocolConnect returning false, host=" + host + ", user=" + traceUser(user) + ", password=" + tracePassword(password));
/*      */       }
/*      */ 
/*      */       
/*  610 */       return false;
/*      */     } 
/*      */ 
/*      */     
/*  614 */     if (pport != -1) {
/*  615 */       this.port = pport;
/*      */     } else {
/*  617 */       this.port = PropUtil.getIntSessionProperty(this.session, "mail." + this.name + ".port", this.port);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  622 */     if (this.port == -1) {
/*  623 */       this.port = this.defaultPort;
/*      */     }
/*      */     
/*      */     try {
/*      */       boolean poolEmpty;
/*  628 */       synchronized (this.pool) {
/*  629 */         poolEmpty = this.pool.authenticatedConnections.isEmpty();
/*      */       } 
/*      */       
/*  632 */       if (poolEmpty) {
/*  633 */         if (this.logger.isLoggable(Level.FINE)) {
/*  634 */           this.logger.fine("trying to connect to host \"" + host + "\", port " + this.port + ", isSSL " + this.isSSL);
/*      */         }
/*  636 */         protocol = newIMAPProtocol(host, this.port);
/*  637 */         if (this.logger.isLoggable(Level.FINE)) {
/*  638 */           this.logger.fine("protocolConnect login, host=" + host + ", user=" + traceUser(user) + ", password=" + tracePassword(password));
/*      */         }
/*      */ 
/*      */         
/*  642 */         login(protocol, user, password);
/*      */         
/*  644 */         protocol.addResponseHandler(this);
/*      */         
/*  646 */         this.usingSSL = protocol.isSSL();
/*      */         
/*  648 */         this.host = host;
/*  649 */         this.user = user;
/*  650 */         this.password = password;
/*      */         
/*  652 */         synchronized (this.pool) {
/*  653 */           this.pool.authenticatedConnections.addElement(protocol);
/*      */         } 
/*      */       } 
/*  656 */     } catch (CommandFailedException cex) {
/*      */       
/*  658 */       if (protocol != null)
/*  659 */         protocol.disconnect(); 
/*  660 */       protocol = null;
/*  661 */       throw new AuthenticationFailedException(cex.getResponse().getRest());
/*      */     }
/*  663 */     catch (ProtocolException pex) {
/*      */       
/*  665 */       if (protocol != null)
/*  666 */         protocol.disconnect(); 
/*  667 */       protocol = null;
/*  668 */       throw new MessagingException(pex.getMessage(), pex);
/*  669 */     } catch (IOException ioex) {
/*  670 */       throw new MessagingException(ioex.getMessage(), ioex);
/*      */     } 
/*      */     
/*  673 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPProtocol newIMAPProtocol(String host, int port) throws IOException, ProtocolException {
/*  685 */     return new IMAPProtocol(this.name, host, port, this.session.getProperties(), this.isSSL, this.logger);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void login(IMAPProtocol p, String u, String pw) throws ProtocolException {
/*      */     String authzid;
/*  695 */     if (this.enableStartTLS || this.requireStartTLS) {
/*  696 */       if (p.hasCapability("STARTTLS")) {
/*  697 */         p.startTLS();
/*      */         
/*  699 */         p.capability();
/*  700 */       } else if (this.requireStartTLS) {
/*  701 */         this.logger.fine("STARTTLS required but not supported by server");
/*  702 */         throw new ProtocolException("STARTTLS required but not supported by server");
/*      */       } 
/*      */     }
/*      */     
/*  706 */     if (p.isAuthenticated()) {
/*      */       return;
/*      */     }
/*      */     
/*  710 */     preLogin(p);
/*      */ 
/*      */     
/*  713 */     if (this.guid != null) {
/*  714 */       p.id(this.guid);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  721 */     p.getCapabilities().put("__PRELOGIN__", "");
/*      */     
/*  723 */     if (this.authorizationID != null) {
/*  724 */       authzid = this.authorizationID;
/*  725 */     } else if (this.proxyAuthUser != null) {
/*  726 */       authzid = this.proxyAuthUser;
/*      */     } else {
/*  728 */       authzid = null;
/*      */     } 
/*  730 */     if (this.enableSASL) {
/*  731 */       p.sasllogin(this.saslMechanisms, this.saslRealm, authzid, u, pw);
/*      */     }
/*  733 */     if (!p.isAuthenticated())
/*      */     {
/*  735 */       if (p.hasCapability("AUTH=PLAIN") && !this.disableAuthPlain) {
/*  736 */         p.authplain(authzid, u, pw);
/*  737 */       } else if ((p.hasCapability("AUTH-LOGIN") || p.hasCapability("AUTH=LOGIN")) && !this.disableAuthLogin) {
/*      */         
/*  739 */         p.authlogin(u, pw);
/*  740 */       } else if (p.hasCapability("AUTH=NTLM") && !this.disableAuthNtlm) {
/*  741 */         p.authntlm(authzid, u, pw);
/*  742 */       } else if (!p.hasCapability("LOGINDISABLED")) {
/*  743 */         p.login(u, pw);
/*      */       } else {
/*  745 */         throw new ProtocolException("No login methods supported!");
/*      */       }  } 
/*  747 */     if (this.proxyAuthUser != null) {
/*  748 */       p.proxyauth(this.proxyAuthUser);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  754 */     if (p.hasCapability("__PRELOGIN__")) {
/*      */       try {
/*  756 */         p.capability();
/*  757 */       } catch (ConnectionException cex) {
/*  758 */         throw cex;
/*      */       }
/*  760 */       catch (ProtocolException pex) {}
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void preLogin(IMAPProtocol p) throws ProtocolException {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSSL() {
/*  788 */     return this.usingSSL;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setUsername(String user) {
/*  807 */     this.user = user;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPassword(String password) {
/*  821 */     this.password = password;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   IMAPProtocol getProtocol(IMAPFolder folder) throws MessagingException {
/*  831 */     IMAPProtocol p = null;
/*      */ 
/*      */     
/*  834 */     while (p == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  842 */       synchronized (this.pool) {
/*      */ 
/*      */ 
/*      */         
/*  846 */         if (this.pool.authenticatedConnections.isEmpty() || (this.pool.authenticatedConnections.size() == 1 && (this.pool.separateStoreConnection || this.pool.storeConnectionInUse))) {
/*      */ 
/*      */ 
/*      */           
/*  850 */           this.logger.fine("no connections in the pool, creating a new one");
/*      */           try {
/*  852 */             if (this.forcePasswordRefresh) {
/*  853 */               refreshPassword();
/*      */             }
/*  855 */             p = newIMAPProtocol(this.host, this.port);
/*      */             
/*  857 */             login(p, this.user, this.password);
/*  858 */           } catch (Exception ex1) {
/*  859 */             if (p != null)
/*      */               try {
/*  861 */                 p.disconnect();
/*  862 */               } catch (Exception ex2) {} 
/*  863 */             p = null;
/*      */           } 
/*      */           
/*  866 */           if (p == null)
/*  867 */             throw new MessagingException("connection failure"); 
/*      */         } else {
/*  869 */           if (this.logger.isLoggable(Level.FINE)) {
/*  870 */             this.logger.fine("connection available -- size: " + this.pool.authenticatedConnections.size());
/*      */           }
/*      */ 
/*      */           
/*  874 */           p = this.pool.authenticatedConnections.lastElement();
/*  875 */           this.pool.authenticatedConnections.removeElement(p);
/*      */ 
/*      */           
/*  878 */           long lastUsed = System.currentTimeMillis() - p.getTimestamp();
/*  879 */           if (lastUsed > this.pool.serverTimeoutInterval) {
/*      */             
/*      */             try {
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  886 */               p.removeResponseHandler(this);
/*  887 */               p.addResponseHandler(this.nonStoreResponseHandler);
/*  888 */               p.noop();
/*  889 */               p.removeResponseHandler(this.nonStoreResponseHandler);
/*  890 */               p.addResponseHandler(this);
/*  891 */             } catch (ProtocolException pex) {
/*      */               try {
/*  893 */                 p.removeResponseHandler(this.nonStoreResponseHandler);
/*  894 */                 p.disconnect();
/*      */               } finally {
/*      */                 
/*  897 */                 p = null;
/*      */                 
/*      */                 continue;
/*      */               } 
/*      */             } 
/*      */           }
/*      */           
/*  904 */           p.removeResponseHandler(this);
/*      */         } 
/*      */ 
/*      */         
/*  908 */         timeoutConnections();
/*      */ 
/*      */         
/*  911 */         if (folder != null) {
/*  912 */           if (this.pool.folders == null)
/*  913 */             this.pool.folders = new Vector(); 
/*  914 */           this.pool.folders.addElement(folder);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  920 */     return p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IMAPProtocol getStoreProtocol() throws ProtocolException {
/*  942 */     IMAPProtocol p = null;
/*      */     
/*  944 */     while (p == null) {
/*  945 */       synchronized (this.pool) {
/*  946 */         waitIfIdle();
/*      */ 
/*      */ 
/*      */         
/*  950 */         if (this.pool.authenticatedConnections.isEmpty()) {
/*  951 */           this.pool.logger.fine("getStoreProtocol() - no connections in the pool, creating a new one");
/*      */           
/*      */           try {
/*  954 */             if (this.forcePasswordRefresh) {
/*  955 */               refreshPassword();
/*      */             }
/*  957 */             p = newIMAPProtocol(this.host, this.port);
/*      */             
/*  959 */             login(p, this.user, this.password);
/*  960 */           } catch (Exception ex1) {
/*  961 */             if (p != null)
/*      */               try {
/*  963 */                 p.logout();
/*  964 */               } catch (Exception ex2) {} 
/*  965 */             p = null;
/*      */           } 
/*      */           
/*  968 */           if (p == null) {
/*  969 */             throw new ConnectionException("failed to create new store connection");
/*      */           }
/*      */           
/*  972 */           p.addResponseHandler(this);
/*  973 */           this.pool.authenticatedConnections.addElement(p);
/*      */         }
/*      */         else {
/*      */           
/*  977 */           if (this.pool.logger.isLoggable(Level.FINE)) {
/*  978 */             this.pool.logger.fine("getStoreProtocol() - connection available -- size: " + this.pool.authenticatedConnections.size());
/*      */           }
/*      */           
/*  981 */           p = this.pool.authenticatedConnections.firstElement();
/*      */         } 
/*      */         
/*  984 */         if (this.pool.storeConnectionInUse) {
/*      */ 
/*      */           
/*      */           try {
/*  988 */             p = null;
/*  989 */             this.pool.wait();
/*  990 */           } catch (InterruptedException ex) {}
/*      */         } else {
/*  992 */           this.pool.storeConnectionInUse = true;
/*      */           
/*  994 */           this.pool.logger.fine("getStoreProtocol() -- storeConnectionInUse");
/*      */         } 
/*      */         
/*  997 */         timeoutConnections();
/*      */       } 
/*      */     } 
/* 1000 */     return p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   IMAPProtocol getFolderStoreProtocol() throws ProtocolException {
/* 1007 */     IMAPProtocol p = getStoreProtocol();
/* 1008 */     p.removeResponseHandler(this);
/* 1009 */     p.addResponseHandler(this.nonStoreResponseHandler);
/* 1010 */     return p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void refreshPassword() {
/*      */     InetAddress inetAddress;
/* 1022 */     if (this.logger.isLoggable(Level.FINE)) {
/* 1023 */       this.logger.fine("refresh password, user: " + traceUser(this.user));
/*      */     }
/*      */     try {
/* 1026 */       inetAddress = InetAddress.getByName(this.host);
/* 1027 */     } catch (UnknownHostException e) {
/* 1028 */       inetAddress = null;
/*      */     } 
/* 1030 */     PasswordAuthentication pa = this.session.requestPasswordAuthentication(inetAddress, this.port, this.name, null, this.user);
/*      */ 
/*      */     
/* 1033 */     if (pa != null) {
/* 1034 */       this.user = pa.getUserName();
/* 1035 */       this.password = pa.getPassword();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean allowReadOnlySelect() {
/* 1045 */     return PropUtil.getBooleanSessionProperty(this.session, "mail." + this.name + ".allowreadonlyselect", false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean hasSeparateStoreConnection() {
/* 1053 */     return this.pool.separateStoreConnection;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   MailLogger getConnectionPoolLogger() {
/* 1060 */     return this.pool.logger;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean getMessageCacheDebug() {
/* 1067 */     return this.messageCacheDebug;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean isConnectionPoolFull() {
/* 1075 */     synchronized (this.pool) {
/* 1076 */       if (this.pool.logger.isLoggable(Level.FINE)) {
/* 1077 */         this.pool.logger.fine("connection pool current size: " + this.pool.authenticatedConnections.size() + "   pool size: " + this.pool.poolSize);
/*      */       }
/*      */ 
/*      */       
/* 1081 */       return (this.pool.authenticatedConnections.size() >= this.pool.poolSize);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void releaseProtocol(IMAPFolder folder, IMAPProtocol protocol) {
/* 1091 */     synchronized (this.pool) {
/* 1092 */       if (protocol != null)
/*      */       {
/*      */         
/* 1095 */         if (!isConnectionPoolFull()) {
/* 1096 */           protocol.addResponseHandler(this);
/* 1097 */           this.pool.authenticatedConnections.addElement(protocol);
/*      */           
/* 1099 */           if (this.logger.isLoggable(Level.FINE)) {
/* 1100 */             this.logger.fine("added an Authenticated connection -- size: " + this.pool.authenticatedConnections.size());
/*      */           }
/*      */         } else {
/*      */           
/* 1104 */           this.logger.fine("pool is full, not adding an Authenticated connection");
/*      */           
/*      */           try {
/* 1107 */             protocol.logout();
/* 1108 */           } catch (ProtocolException pex) {}
/*      */         } 
/*      */       }
/*      */       
/* 1112 */       if (this.pool.folders != null) {
/* 1113 */         this.pool.folders.removeElement(folder);
/*      */       }
/* 1115 */       timeoutConnections();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void releaseStoreProtocol(IMAPProtocol protocol) {
/*      */     boolean failed;
/* 1127 */     if (protocol == null) {
/* 1128 */       cleanup();
/*      */ 
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1137 */     synchronized (this.connectionFailedLock) {
/* 1138 */       failed = this.connectionFailed;
/* 1139 */       this.connectionFailed = false;
/*      */     } 
/*      */ 
/*      */     
/* 1143 */     synchronized (this.pool) {
/* 1144 */       this.pool.storeConnectionInUse = false;
/* 1145 */       this.pool.notifyAll();
/*      */       
/* 1147 */       this.pool.logger.fine("releaseStoreProtocol()");
/*      */       
/* 1149 */       timeoutConnections();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1157 */     assert !Thread.holdsLock(this.pool);
/* 1158 */     if (failed) {
/* 1159 */       cleanup();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   void releaseFolderStoreProtocol(IMAPProtocol protocol) {
/* 1166 */     if (protocol == null)
/*      */       return; 
/* 1168 */     protocol.removeResponseHandler(this.nonStoreResponseHandler);
/* 1169 */     protocol.addResponseHandler(this);
/* 1170 */     synchronized (this.pool) {
/* 1171 */       this.pool.storeConnectionInUse = false;
/* 1172 */       this.pool.notifyAll();
/*      */       
/* 1174 */       this.pool.logger.fine("releaseFolderStoreProtocol()");
/*      */       
/* 1176 */       timeoutConnections();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void emptyConnectionPool(boolean force) {
/* 1185 */     synchronized (this.pool) {
/* 1186 */       int index = this.pool.authenticatedConnections.size() - 1;
/* 1187 */       for (; index >= 0; index--) {
/*      */         try {
/* 1189 */           IMAPProtocol p = this.pool.authenticatedConnections.elementAt(index);
/*      */           
/* 1191 */           p.removeResponseHandler(this);
/* 1192 */           if (force)
/* 1193 */           { p.disconnect(); }
/*      */           else
/* 1195 */           { p.logout(); } 
/* 1196 */         } catch (ProtocolException pex) {}
/*      */       } 
/*      */       
/* 1199 */       this.pool.authenticatedConnections.removeAllElements();
/*      */     } 
/*      */     
/* 1202 */     this.pool.logger.fine("removed all authenticated connections from pool");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void timeoutConnections() {
/* 1210 */     synchronized (this.pool) {
/*      */ 
/*      */ 
/*      */       
/* 1214 */       if (System.currentTimeMillis() - this.pool.lastTimePruned > this.pool.pruningInterval && this.pool.authenticatedConnections.size() > 1) {
/*      */ 
/*      */ 
/*      */         
/* 1218 */         if (this.pool.logger.isLoggable(Level.FINE)) {
/* 1219 */           this.pool.logger.fine("checking for connections to prune: " + (System.currentTimeMillis() - this.pool.lastTimePruned));
/*      */           
/* 1221 */           this.pool.logger.fine("clientTimeoutInterval: " + this.pool.clientTimeoutInterval);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1230 */         int index = this.pool.authenticatedConnections.size() - 1;
/* 1231 */         for (; index > 0; index--) {
/* 1232 */           IMAPProtocol p = this.pool.authenticatedConnections.elementAt(index);
/*      */           
/* 1234 */           if (this.pool.logger.isLoggable(Level.FINE)) {
/* 1235 */             this.pool.logger.fine("protocol last used: " + (System.currentTimeMillis() - p.getTimestamp()));
/*      */           }
/* 1237 */           if (System.currentTimeMillis() - p.getTimestamp() > this.pool.clientTimeoutInterval) {
/*      */ 
/*      */             
/* 1240 */             this.pool.logger.fine("authenticated connection timed out, logging out the connection");
/*      */ 
/*      */ 
/*      */             
/* 1244 */             p.removeResponseHandler(this);
/* 1245 */             this.pool.authenticatedConnections.removeElementAt(index);
/*      */             
/*      */             try {
/* 1248 */               p.logout();
/* 1249 */             } catch (ProtocolException pex) {}
/*      */           } 
/*      */         } 
/* 1252 */         this.pool.lastTimePruned = System.currentTimeMillis();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getFetchBlockSize() {
/* 1261 */     return this.blksize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean ignoreBodyStructureSize() {
/* 1268 */     return this.ignoreSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Session getSession() {
/* 1275 */     return this.session;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getStatusCacheTimeout() {
/* 1282 */     return this.statusCacheTimeout;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getAppendBufferSize() {
/* 1289 */     return this.appendBufferSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getMinIdleTime() {
/* 1296 */     return this.minIdleTime;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean hasCapability(String capability) throws MessagingException {
/* 1307 */     IMAPProtocol p = null;
/*      */     try {
/* 1309 */       p = getStoreProtocol();
/* 1310 */       return p.hasCapability(capability);
/* 1311 */     } catch (ProtocolException pex) {
/* 1312 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } finally {
/* 1314 */       releaseStoreProtocol(p);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isConnected() {
/* 1323 */     if (!super.isConnected())
/*      */     {
/*      */       
/* 1326 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1345 */     IMAPProtocol p = null;
/*      */     try {
/* 1347 */       p = getStoreProtocol();
/* 1348 */       p.noop();
/* 1349 */     } catch (ProtocolException pex) {
/*      */     
/*      */     } finally {
/* 1352 */       releaseStoreProtocol(p);
/*      */     } 
/*      */ 
/*      */     
/* 1356 */     return super.isConnected();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void close() throws MessagingException {
/* 1363 */     if (!super.isConnected()) {
/*      */       return;
/*      */     }
/* 1366 */     IMAPProtocol protocol = null;
/*      */     try {
/*      */       boolean isEmpty;
/* 1369 */       synchronized (this.pool) {
/*      */ 
/*      */         
/* 1372 */         isEmpty = this.pool.authenticatedConnections.isEmpty();
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1383 */       if (isEmpty) {
/* 1384 */         this.pool.logger.fine("close() - no connections ");
/* 1385 */         cleanup();
/*      */         
/*      */         return;
/*      */       } 
/* 1389 */       protocol = getStoreProtocol();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1396 */       synchronized (this.pool) {
/* 1397 */         this.pool.authenticatedConnections.removeElement(protocol);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1415 */       protocol.logout();
/* 1416 */     } catch (ProtocolException pex) {
/*      */       
/* 1418 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } finally {
/* 1420 */       releaseStoreProtocol(protocol);
/*      */     } 
/*      */   }
/*      */   
/*      */   protected void finalize() throws Throwable {
/* 1425 */     super.finalize();
/* 1426 */     close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized void cleanup() {
/*      */     boolean force;
/* 1434 */     if (!super.isConnected()) {
/* 1435 */       this.logger.fine("IMAPStore cleanup, not connected");
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1446 */     synchronized (this.connectionFailedLock) {
/* 1447 */       force = this.forceClose;
/* 1448 */       this.forceClose = false;
/* 1449 */       this.connectionFailed = false;
/*      */     } 
/* 1451 */     if (this.logger.isLoggable(Level.FINE)) {
/* 1452 */       this.logger.fine("IMAPStore cleanup, force " + force);
/*      */     }
/* 1454 */     Vector foldersCopy = null;
/* 1455 */     boolean done = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1467 */       synchronized (this.pool) {
/* 1468 */         if (this.pool.folders != null) {
/* 1469 */           done = false;
/* 1470 */           foldersCopy = this.pool.folders;
/* 1471 */           this.pool.folders = null;
/*      */         } else {
/* 1473 */           done = true;
/*      */         } 
/*      */       } 
/* 1476 */       if (done) {
/*      */         break;
/*      */       }
/*      */       
/* 1480 */       for (int i = 0, fsize = foldersCopy.size(); i < fsize; i++) {
/* 1481 */         IMAPFolder f = foldersCopy.elementAt(i);
/*      */         
/*      */         try {
/* 1484 */           if (force) {
/* 1485 */             this.logger.fine("force folder to close");
/*      */ 
/*      */ 
/*      */             
/* 1489 */             f.forceClose();
/*      */           } else {
/* 1491 */             this.logger.fine("close folder");
/* 1492 */             f.close(false);
/*      */           } 
/* 1494 */         } catch (MessagingException mex) {
/*      */         
/* 1496 */         } catch (IllegalStateException ex) {}
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1503 */     synchronized (this.pool) {
/* 1504 */       emptyConnectionPool(force);
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/* 1509 */       super.close();
/* 1510 */     } catch (MessagingException mex) {}
/* 1511 */     this.logger.fine("IMAPStore cleanup done");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Folder getDefaultFolder() throws MessagingException {
/* 1519 */     checkConnected();
/* 1520 */     return new DefaultFolder(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Folder getFolder(String name) throws MessagingException {
/* 1528 */     checkConnected();
/* 1529 */     return newIMAPFolder(name, '￿');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Folder getFolder(URLName url) throws MessagingException {
/* 1537 */     checkConnected();
/* 1538 */     return newIMAPFolder(url.getFile(), '￿');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPFolder newIMAPFolder(String fullName, char separator, Boolean isNamespace) {
/* 1547 */     IMAPFolder f = null;
/* 1548 */     if (this.folderConstructor != null) {
/*      */       try {
/* 1550 */         Object[] o = { fullName, new Character(separator), this, isNamespace };
/*      */         
/* 1552 */         f = this.folderConstructor.newInstance(o);
/* 1553 */       } catch (Exception ex) {
/* 1554 */         this.logger.log(Level.FINE, "exception creating IMAPFolder class", ex);
/*      */       } 
/*      */     }
/*      */     
/* 1558 */     if (f == null)
/* 1559 */       f = new IMAPFolder(fullName, separator, this, isNamespace); 
/* 1560 */     return f;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPFolder newIMAPFolder(String fullName, char separator) {
/* 1568 */     return newIMAPFolder(fullName, separator, (Boolean)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPFolder newIMAPFolder(ListInfo li) {
/* 1576 */     IMAPFolder f = null;
/* 1577 */     if (this.folderConstructorLI != null) {
/*      */       try {
/* 1579 */         Object[] o = { li, this };
/* 1580 */         f = this.folderConstructorLI.newInstance(o);
/* 1581 */       } catch (Exception ex) {
/* 1582 */         this.logger.log(Level.FINE, "exception creating IMAPFolder class LI", ex);
/*      */       } 
/*      */     }
/*      */     
/* 1586 */     if (f == null)
/* 1587 */       f = new IMAPFolder(li, this); 
/* 1588 */     return f;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Folder[] getPersonalNamespaces() throws MessagingException {
/* 1596 */     Namespaces ns = getNamespaces();
/* 1597 */     if (ns == null || ns.personal == null)
/* 1598 */       return super.getPersonalNamespaces(); 
/* 1599 */     return namespaceToFolders(ns.personal, (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Folder[] getUserNamespaces(String user) throws MessagingException {
/* 1608 */     Namespaces ns = getNamespaces();
/* 1609 */     if (ns == null || ns.otherUsers == null)
/* 1610 */       return super.getUserNamespaces(user); 
/* 1611 */     return namespaceToFolders(ns.otherUsers, user);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Folder[] getSharedNamespaces() throws MessagingException {
/* 1619 */     Namespaces ns = getNamespaces();
/* 1620 */     if (ns == null || ns.shared == null)
/* 1621 */       return super.getSharedNamespaces(); 
/* 1622 */     return namespaceToFolders(ns.shared, (String)null);
/*      */   }
/*      */   
/*      */   private synchronized Namespaces getNamespaces() throws MessagingException {
/* 1626 */     checkConnected();
/*      */     
/* 1628 */     IMAPProtocol p = null;
/*      */     
/* 1630 */     if (this.namespaces == null) {
/*      */       try {
/* 1632 */         p = getStoreProtocol();
/* 1633 */         this.namespaces = p.namespace();
/* 1634 */       } catch (BadCommandException bex) {
/*      */       
/* 1636 */       } catch (ConnectionException cex) {
/* 1637 */         throw new StoreClosedException(this, cex.getMessage());
/* 1638 */       } catch (ProtocolException pex) {
/* 1639 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } finally {
/* 1641 */         releaseStoreProtocol(p);
/*      */       } 
/*      */     }
/* 1644 */     return this.namespaces;
/*      */   }
/*      */ 
/*      */   
/*      */   private Folder[] namespaceToFolders(Namespaces.Namespace[] ns, String user) {
/* 1649 */     Folder[] fa = new Folder[ns.length];
/* 1650 */     for (int i = 0; i < fa.length; i++) {
/* 1651 */       String name = (ns[i]).prefix;
/* 1652 */       if (user == null) {
/*      */         
/* 1654 */         int len = name.length();
/* 1655 */         if (len > 0 && name.charAt(len - 1) == (ns[i]).delimiter) {
/* 1656 */           name = name.substring(0, len - 1);
/*      */         }
/*      */       } else {
/* 1659 */         name = name + user;
/*      */       } 
/* 1661 */       fa[i] = newIMAPFolder(name, (ns[i]).delimiter, Boolean.valueOf((user == null)));
/*      */     } 
/*      */     
/* 1664 */     return fa;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Quota[] getQuota(String root) throws MessagingException {
/* 1685 */     checkConnected();
/* 1686 */     Quota[] qa = null;
/*      */     
/* 1688 */     IMAPProtocol p = null;
/*      */     try {
/* 1690 */       p = getStoreProtocol();
/* 1691 */       qa = p.getQuotaRoot(root);
/* 1692 */     } catch (BadCommandException bex) {
/* 1693 */       throw new MessagingException("QUOTA not supported", bex);
/* 1694 */     } catch (ConnectionException cex) {
/* 1695 */       throw new StoreClosedException(this, cex.getMessage());
/* 1696 */     } catch (ProtocolException pex) {
/* 1697 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } finally {
/* 1699 */       releaseStoreProtocol(p);
/*      */     } 
/* 1701 */     return qa;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setQuota(Quota quota) throws MessagingException {
/* 1714 */     checkConnected();
/* 1715 */     IMAPProtocol p = null;
/*      */     try {
/* 1717 */       p = getStoreProtocol();
/* 1718 */       p.setQuota(quota);
/* 1719 */     } catch (BadCommandException bex) {
/* 1720 */       throw new MessagingException("QUOTA not supported", bex);
/* 1721 */     } catch (ConnectionException cex) {
/* 1722 */       throw new StoreClosedException(this, cex.getMessage());
/* 1723 */     } catch (ProtocolException pex) {
/* 1724 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } finally {
/* 1726 */       releaseStoreProtocol(p);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void checkConnected() {
/* 1731 */     assert Thread.holdsLock(this);
/* 1732 */     if (!super.isConnected()) {
/* 1733 */       throw new IllegalStateException("Not connected");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handleResponse(Response r) {
/* 1741 */     if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE())
/* 1742 */       handleResponseCode(r); 
/* 1743 */     if (r.isBYE()) {
/* 1744 */       this.logger.fine("IMAPStore connection dead");
/*      */ 
/*      */       
/* 1747 */       synchronized (this.connectionFailedLock) {
/* 1748 */         this.connectionFailed = true;
/* 1749 */         if (r.isSynthetic()) {
/* 1750 */           this.forceClose = true;
/*      */         }
/*      */       } 
/*      */       return;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void idle() throws MessagingException {
/* 1793 */     IMAPProtocol p = null;
/*      */ 
/*      */     
/* 1796 */     assert !Thread.holdsLock(this.pool);
/* 1797 */     synchronized (this) {
/* 1798 */       checkConnected();
/*      */     } 
/*      */     try {
/* 1801 */       synchronized (this.pool) {
/* 1802 */         p = getStoreProtocol();
/* 1803 */         if (this.pool.idleState == 0) {
/* 1804 */           p.idleStart();
/* 1805 */           this.pool.idleState = 1;
/*      */         } else {
/*      */ 
/*      */           
/*      */           try {
/*      */ 
/*      */             
/* 1812 */             this.pool.wait();
/* 1813 */           } catch (InterruptedException ex) {}
/*      */           return;
/*      */         } 
/* 1816 */         this.pool.idleProtocol = p;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       while (true) {
/* 1833 */         Response r = p.readIdleResponse();
/* 1834 */         synchronized (this.pool) {
/* 1835 */           if (r == null || !p.processIdleResponse(r)) {
/* 1836 */             this.pool.idleState = 0;
/* 1837 */             this.pool.notifyAll();
/*      */             break;
/*      */           } 
/*      */         } 
/* 1841 */         if (this.enableImapEvents && r.isUnTagged()) {
/* 1842 */           notifyStoreListeners(1000, r.toString());
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1851 */       int minidle = getMinIdleTime();
/* 1852 */       if (minidle > 0) {
/*      */         try {
/* 1854 */           Thread.sleep(minidle);
/* 1855 */         } catch (InterruptedException ex) {}
/*      */       }
/*      */     }
/* 1858 */     catch (BadCommandException bex) {
/* 1859 */       throw new MessagingException("IDLE not supported", bex);
/* 1860 */     } catch (ConnectionException cex) {
/* 1861 */       throw new StoreClosedException(this, cex.getMessage());
/* 1862 */     } catch (ProtocolException pex) {
/* 1863 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } finally {
/* 1865 */       synchronized (this.pool) {
/* 1866 */         this.pool.idleProtocol = null;
/*      */       } 
/* 1868 */       releaseStoreProtocol(p);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void waitIfIdle() throws ProtocolException {
/* 1878 */     assert Thread.holdsLock(this.pool);
/* 1879 */     while (this.pool.idleState != 0) {
/* 1880 */       if (this.pool.idleState == 1) {
/* 1881 */         this.pool.idleProtocol.idleAbort();
/* 1882 */         this.pool.idleState = 2;
/*      */       } 
/*      */       
/*      */       try {
/* 1886 */         this.pool.wait();
/* 1887 */       } catch (InterruptedException ex) {}
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void handleResponseCode(Response r) {
/* 1896 */     String s = r.getRest();
/* 1897 */     boolean isAlert = false;
/* 1898 */     if (s.startsWith("[")) {
/* 1899 */       int i = s.indexOf(']');
/*      */       
/* 1901 */       if (i > 0 && s.substring(0, i + 1).equalsIgnoreCase("[ALERT]")) {
/* 1902 */         isAlert = true;
/*      */       }
/* 1904 */       s = s.substring(i + 1).trim();
/*      */     } 
/* 1906 */     if (isAlert) {
/* 1907 */       notifyStoreListeners(1, s);
/* 1908 */     } else if (r.isUnTagged() && s.length() > 0) {
/*      */ 
/*      */ 
/*      */       
/* 1912 */       notifyStoreListeners(2, s);
/*      */     } 
/*      */   }
/*      */   private String traceUser(String user) {
/* 1916 */     return this.debugusername ? user : "<user name suppressed>";
/*      */   }
/*      */   
/*      */   private String tracePassword(String password) {
/* 1920 */     return this.debugpassword ? password : ((password == null) ? "<null>" : "<non-null>");
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\IMAPStore.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */