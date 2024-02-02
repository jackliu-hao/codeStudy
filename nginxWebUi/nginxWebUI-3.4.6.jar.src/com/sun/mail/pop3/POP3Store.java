/*     */ package com.sun.mail.pop3;
/*     */ 
/*     */ import com.sun.mail.util.MailLogger;
/*     */ import com.sun.mail.util.PropUtil;
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import javax.mail.AuthenticationFailedException;
/*     */ import javax.mail.Folder;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.Store;
/*     */ import javax.mail.URLName;
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
/*     */ public class POP3Store
/*     */   extends Store
/*     */ {
/*  70 */   private String name = "pop3";
/*  71 */   private int defaultPort = 110;
/*     */   
/*     */   private boolean isSSL = false;
/*  74 */   private Protocol port = null;
/*  75 */   private POP3Folder portOwner = null;
/*  76 */   private String host = null;
/*  77 */   private int portNum = -1;
/*  78 */   private String user = null;
/*  79 */   private String passwd = null;
/*     */   
/*     */   private boolean useStartTLS = false;
/*     */   
/*     */   private boolean requireStartTLS = false;
/*     */   private boolean usingSSL = false;
/*     */   private Map capabilities;
/*     */   private MailLogger logger;
/*  87 */   volatile Constructor messageConstructor = null;
/*     */   volatile boolean rsetBeforeQuit = false;
/*     */   volatile boolean disableTop = false;
/*     */   volatile boolean forgetTopHeaders = false;
/*     */   volatile boolean supportsUidl = true;
/*     */   volatile boolean cacheWriteTo = false;
/*     */   volatile boolean useFileCache = false;
/*  94 */   volatile File fileCacheDir = null;
/*     */   volatile boolean keepMessageContent = false;
/*     */   
/*     */   public POP3Store(Session session, URLName url) {
/*  98 */     this(session, url, "pop3", false);
/*     */   }
/*     */ 
/*     */   
/*     */   public POP3Store(Session session, URLName url, String name, boolean isSSL) {
/* 103 */     super(session, url);
/* 104 */     if (url != null)
/* 105 */       name = url.getProtocol(); 
/* 106 */     this.name = name;
/* 107 */     this.logger = new MailLogger(getClass(), "DEBUG POP3", session);
/*     */ 
/*     */     
/* 110 */     if (!isSSL) {
/* 111 */       isSSL = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".ssl.enable", false);
/*     */     }
/* 113 */     if (isSSL) {
/* 114 */       this.defaultPort = 995;
/*     */     } else {
/* 116 */       this.defaultPort = 110;
/* 117 */     }  this.isSSL = isSSL;
/*     */     
/* 119 */     this.rsetBeforeQuit = getBoolProp("rsetbeforequit");
/* 120 */     this.disableTop = getBoolProp("disabletop");
/* 121 */     this.forgetTopHeaders = getBoolProp("forgettopheaders");
/* 122 */     this.cacheWriteTo = getBoolProp("cachewriteto");
/* 123 */     this.useFileCache = getBoolProp("filecache.enable");
/* 124 */     String dir = session.getProperty("mail." + name + ".filecache.dir");
/* 125 */     if (dir != null && this.logger.isLoggable(Level.CONFIG))
/* 126 */       this.logger.config("mail." + name + ".filecache.dir: " + dir); 
/* 127 */     if (dir != null)
/* 128 */       this.fileCacheDir = new File(dir); 
/* 129 */     this.keepMessageContent = getBoolProp("keepmessagecontent");
/*     */ 
/*     */     
/* 132 */     this.useStartTLS = getBoolProp("starttls.enable");
/*     */ 
/*     */     
/* 135 */     this.requireStartTLS = getBoolProp("starttls.required");
/*     */     
/* 137 */     String s = session.getProperty("mail." + name + ".message.class");
/* 138 */     if (s != null) {
/* 139 */       this.logger.log(Level.CONFIG, "message class: {0}", s);
/*     */       try {
/* 141 */         ClassLoader cl = getClass().getClassLoader();
/*     */ 
/*     */         
/* 144 */         Class messageClass = null;
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/* 149 */           messageClass = Class.forName(s, false, cl);
/* 150 */         } catch (ClassNotFoundException ex1) {
/*     */ 
/*     */ 
/*     */           
/* 154 */           messageClass = Class.forName(s);
/*     */         } 
/*     */         
/* 157 */         Class[] c = { Folder.class, int.class };
/* 158 */         this.messageConstructor = messageClass.getConstructor(c);
/* 159 */       } catch (Exception ex) {
/* 160 */         this.logger.log(Level.CONFIG, "failed to load message class", ex);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final synchronized boolean getBoolProp(String prop) {
/* 170 */     prop = "mail." + this.name + "." + prop;
/* 171 */     boolean val = PropUtil.getBooleanSessionProperty(this.session, prop, false);
/* 172 */     if (this.logger.isLoggable(Level.CONFIG))
/* 173 */       this.logger.config(prop + ": " + val); 
/* 174 */     return val;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized Session getSession() {
/* 181 */     return this.session;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized boolean protocolConnect(String host, int portNum, String user, String passwd) throws MessagingException {
/* 188 */     if (host == null || passwd == null || user == null) {
/* 189 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 193 */     if (portNum == -1) {
/* 194 */       portNum = PropUtil.getIntSessionProperty(this.session, "mail." + this.name + ".port", -1);
/*     */     }
/*     */     
/* 197 */     if (portNum == -1) {
/* 198 */       portNum = this.defaultPort;
/*     */     }
/* 200 */     this.host = host;
/* 201 */     this.portNum = portNum;
/* 202 */     this.user = user;
/* 203 */     this.passwd = passwd;
/*     */     try {
/* 205 */       this.port = getPort(null);
/* 206 */     } catch (EOFException eex) {
/* 207 */       throw new AuthenticationFailedException(eex.getMessage());
/* 208 */     } catch (IOException ioex) {
/* 209 */       throw new MessagingException("Connect failed", ioex);
/*     */     } 
/*     */     
/* 212 */     return true;
/*     */   }
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
/*     */   public synchronized boolean isConnected() {
/* 228 */     if (!super.isConnected())
/*     */     {
/*     */       
/* 231 */       return false; } 
/*     */     try {
/* 233 */       if (this.port == null) {
/* 234 */         this.port = getPort(null);
/* 235 */       } else if (!this.port.noop()) {
/* 236 */         throw new IOException("NOOP failed");
/* 237 */       }  return true;
/* 238 */     } catch (IOException ioex) {
/*     */       
/*     */       try {
/* 241 */         super.close();
/* 242 */       } catch (MessagingException mex) {
/*     */       
/*     */       } finally {
/* 245 */         return false;
/*     */       } 
/*     */       while (true);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized Protocol getPort(POP3Folder owner) throws IOException {
/* 254 */     if (this.port != null && this.portOwner == null) {
/* 255 */       this.portOwner = owner;
/* 256 */       return this.port;
/*     */     } 
/*     */ 
/*     */     
/* 260 */     Protocol p = new Protocol(this.host, this.portNum, this.logger, this.session.getProperties(), "mail." + this.name, this.isSSL);
/*     */ 
/*     */     
/* 263 */     if (this.useStartTLS || this.requireStartTLS) {
/* 264 */       if (p.hasCapability("STLS")) {
/* 265 */         if (p.stls()) {
/*     */           
/* 267 */           p.setCapabilities(p.capa());
/* 268 */         } else if (this.requireStartTLS) {
/* 269 */           this.logger.fine("STLS required but failed");
/*     */           
/* 271 */           try { p.quit(); }
/* 272 */           catch (IOException ioex) {  }
/*     */           finally
/* 274 */           { throw new EOFException("STLS required but failed"); }
/*     */         
/*     */         } 
/* 277 */       } else if (this.requireStartTLS) {
/* 278 */         this.logger.fine("STLS required but not supported");
/*     */         
/* 280 */         try { p.quit(); }
/* 281 */         catch (IOException ioex) {  }
/*     */         finally
/* 283 */         { throw new EOFException("STLS required but not supported"); }
/*     */       
/*     */       } 
/*     */     }
/*     */     
/* 288 */     this.capabilities = p.getCapabilities();
/* 289 */     this.usingSSL = p.isSSL();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 297 */     if (!this.disableTop && this.capabilities != null && !this.capabilities.containsKey("TOP")) {
/*     */       
/* 299 */       this.disableTop = true;
/* 300 */       this.logger.fine("server doesn't support TOP, disabling it");
/*     */     } 
/*     */     
/* 303 */     this.supportsUidl = (this.capabilities == null || this.capabilities.containsKey("UIDL"));
/*     */     
/* 305 */     String msg = null;
/* 306 */     if ((msg = p.login(this.user, this.passwd)) != null) {
/*     */       
/* 308 */       try { p.quit(); }
/* 309 */       catch (IOException ioex) {  }
/*     */       finally
/* 311 */       { throw new EOFException(msg); }
/*     */     
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 323 */     if (this.port == null && owner != null) {
/* 324 */       this.port = p;
/* 325 */       this.portOwner = owner;
/*     */     } 
/* 327 */     if (this.portOwner == null)
/* 328 */       this.portOwner = owner; 
/* 329 */     return p;
/*     */   }
/*     */   
/*     */   synchronized void closePort(POP3Folder owner) {
/* 333 */     if (this.portOwner == owner) {
/* 334 */       this.port = null;
/* 335 */       this.portOwner = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized void close() throws MessagingException {
/*     */     
/* 341 */     try { if (this.port != null)
/* 342 */         this.port.quit();  }
/* 343 */     catch (IOException ioex) {  }
/*     */     finally
/* 345 */     { this.port = null;
/*     */ 
/*     */       
/* 348 */       super.close(); }
/*     */   
/*     */   }
/*     */   
/*     */   public Folder getDefaultFolder() throws MessagingException {
/* 353 */     checkConnected();
/* 354 */     return new DefaultFolder(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Folder getFolder(String name) throws MessagingException {
/* 361 */     checkConnected();
/* 362 */     return new POP3Folder(this, name);
/*     */   }
/*     */   
/*     */   public Folder getFolder(URLName url) throws MessagingException {
/* 366 */     checkConnected();
/* 367 */     return new POP3Folder(this, url.getFile());
/*     */   }
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
/*     */   public Map capabilities() throws MessagingException {
/*     */     Map c;
/* 386 */     synchronized (this) {
/* 387 */       c = this.capabilities;
/*     */     } 
/* 389 */     if (c != null) {
/* 390 */       return Collections.unmodifiableMap(c);
/*     */     }
/* 392 */     return Collections.EMPTY_MAP;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSSL() {
/* 402 */     return this.usingSSL;
/*     */   }
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 406 */     super.finalize();
/*     */     
/* 408 */     if (this.port != null)
/* 409 */       close(); 
/*     */   }
/*     */   
/*     */   private void checkConnected() throws MessagingException {
/* 413 */     if (!super.isConnected())
/* 414 */       throw new MessagingException("Not connected"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\pop3\POP3Store.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */