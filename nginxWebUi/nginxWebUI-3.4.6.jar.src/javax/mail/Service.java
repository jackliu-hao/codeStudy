/*     */ package javax.mail;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Vector;
/*     */ import javax.mail.event.ConnectionEvent;
/*     */ import javax.mail.event.ConnectionListener;
/*     */ import javax.mail.event.MailEvent;
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
/*     */ public abstract class Service
/*     */ {
/*     */   protected Session session;
/*  71 */   protected URLName url = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean debug = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean connected = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   private final Vector connectionListeners = new Vector();
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
/*     */   private EventQueue q;
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
/*     */   private Object qLock;
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
/*     */   public void connect() throws MessagingException {
/* 125 */     connect(null, null, null);
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
/*     */   public void connect(String host, String user, String password) throws MessagingException {
/* 176 */     connect(host, -1, user, password);
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
/*     */ 
/*     */   
/*     */   public void connect(String user, String password) throws MessagingException {
/* 196 */     connect(null, user, password);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void connect(String host, int port, String user, String password) throws MessagingException {
/* 217 */     if (isConnected()) {
/* 218 */       throw new IllegalStateException("already connected");
/*     */     }
/*     */     
/* 221 */     boolean connected = false;
/* 222 */     boolean save = false;
/* 223 */     String protocol = null;
/* 224 */     String file = null;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 229 */     if (this.url != null) {
/* 230 */       protocol = this.url.getProtocol();
/* 231 */       if (host == null)
/* 232 */         host = this.url.getHost(); 
/* 233 */       if (port == -1) {
/* 234 */         port = this.url.getPort();
/*     */       }
/* 236 */       if (user == null) {
/* 237 */         user = this.url.getUsername();
/* 238 */         if (password == null) {
/* 239 */           password = this.url.getPassword();
/*     */         }
/* 241 */       } else if (password == null && user.equals(this.url.getUsername())) {
/*     */         
/* 243 */         password = this.url.getPassword();
/*     */       } 
/*     */       
/* 246 */       file = this.url.getFile();
/*     */     } 
/*     */ 
/*     */     
/* 250 */     if (protocol != null) {
/* 251 */       if (host == null)
/* 252 */         host = this.session.getProperty("mail." + protocol + ".host"); 
/* 253 */       if (user == null) {
/* 254 */         user = this.session.getProperty("mail." + protocol + ".user");
/*     */       }
/*     */     } 
/*     */     
/* 258 */     if (host == null) {
/* 259 */       host = this.session.getProperty("mail.host");
/*     */     }
/* 261 */     if (user == null) {
/* 262 */       user = this.session.getProperty("mail.user");
/*     */     }
/*     */     
/* 265 */     if (user == null) {
/*     */       try {
/* 267 */         user = System.getProperty("user.name");
/* 268 */       } catch (SecurityException sex) {
/* 269 */         if (this.debug) {
/* 270 */           sex.printStackTrace(this.session.getDebugOut());
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 275 */     if (password == null && this.url != null) {
/*     */       
/* 277 */       setURLName(new URLName(protocol, host, port, file, user, null));
/* 278 */       PasswordAuthentication pw = this.session.getPasswordAuthentication(getURLName());
/* 279 */       if (pw != null) {
/* 280 */         if (user == null) {
/* 281 */           user = pw.getUserName();
/* 282 */           password = pw.getPassword();
/* 283 */         } else if (user.equals(pw.getUserName())) {
/* 284 */           password = pw.getPassword();
/*     */         } 
/*     */       } else {
/* 287 */         save = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 293 */     AuthenticationFailedException authEx = null;
/*     */     try {
/* 295 */       connected = protocolConnect(host, port, user, password);
/* 296 */     } catch (AuthenticationFailedException ex) {
/* 297 */       authEx = ex;
/*     */     } 
/*     */ 
/*     */     
/* 301 */     if (!connected) {
/*     */       InetAddress inetAddress;
/*     */       try {
/* 304 */         inetAddress = InetAddress.getByName(host);
/* 305 */       } catch (UnknownHostException e) {
/* 306 */         inetAddress = null;
/*     */       } 
/* 308 */       PasswordAuthentication pw = this.session.requestPasswordAuthentication(inetAddress, port, protocol, null, user);
/*     */ 
/*     */ 
/*     */       
/* 312 */       if (pw != null) {
/* 313 */         user = pw.getUserName();
/* 314 */         password = pw.getPassword();
/*     */ 
/*     */         
/* 317 */         connected = protocolConnect(host, port, user, password);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 322 */     if (!connected) {
/* 323 */       if (authEx != null)
/* 324 */         throw authEx; 
/* 325 */       if (user == null) {
/* 326 */         throw new AuthenticationFailedException("failed to connect, no user name specified?");
/*     */       }
/* 328 */       if (password == null) {
/* 329 */         throw new AuthenticationFailedException("failed to connect, no password specified?");
/*     */       }
/*     */       
/* 332 */       throw new AuthenticationFailedException("failed to connect");
/*     */     } 
/*     */     
/* 335 */     setURLName(new URLName(protocol, host, port, file, user, password));
/*     */     
/* 337 */     if (save) {
/* 338 */       this.session.setPasswordAuthentication(getURLName(), new PasswordAuthentication(user, password));
/*     */     }
/*     */ 
/*     */     
/* 342 */     setConnected(true);
/*     */ 
/*     */     
/* 345 */     notifyConnectionListeners(1);
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
/*     */   protected boolean protocolConnect(String host, int port, String user, String password) throws MessagingException {
/* 382 */     return false;
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
/* 398 */     return this.connected;
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
/*     */   protected synchronized void setConnected(boolean connected) {
/* 415 */     this.connected = connected;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void close() throws MessagingException {
/* 438 */     setConnected(false);
/* 439 */     notifyConnectionListeners(3);
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
/*     */   public synchronized URLName getURLName() {
/* 457 */     if (this.url != null && (this.url.getPassword() != null || this.url.getFile() != null)) {
/* 458 */       return new URLName(this.url.getProtocol(), this.url.getHost(), this.url.getPort(), null, this.url.getUsername(), null);
/*     */     }
/*     */ 
/*     */     
/* 462 */     return this.url;
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized void setURLName(URLName url) {
/* 483 */     this.url = url;
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
/*     */   public void addConnectionListener(ConnectionListener l) {
/* 496 */     this.connectionListeners.addElement(l);
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
/*     */   public void removeConnectionListener(ConnectionListener l) {
/* 509 */     this.connectionListeners.removeElement(l);
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
/*     */   
/*     */   protected void notifyConnectionListeners(int type) {
/* 528 */     if (this.connectionListeners.size() > 0) {
/* 529 */       ConnectionEvent e = new ConnectionEvent(this, type);
/* 530 */       queueEvent((MailEvent)e, this.connectionListeners);
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
/*     */ 
/*     */     
/* 543 */     if (type == 3) {
/* 544 */       terminateQueue();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 552 */     URLName url = getURLName();
/* 553 */     if (url != null) {
/* 554 */       return url.toString();
/*     */     }
/* 556 */     return super.toString();
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
/*     */   protected Service(Session session, URLName urlname) {
/* 570 */     this.qLock = new Object();
/*     */     this.session = session;
/*     */     this.url = urlname;
/*     */     this.debug = session.getDebug();
/*     */   }
/*     */   
/*     */   protected void queueEvent(MailEvent event, Vector vector) {
/* 577 */     synchronized (this.qLock) {
/* 578 */       if (this.q == null) {
/* 579 */         this.q = new EventQueue();
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 590 */     Vector v = (Vector)vector.clone();
/* 591 */     this.q.enqueue(event, v);
/*     */   }
/*     */   
/*     */   static class TerminatorEvent extends MailEvent {
/*     */     private static final long serialVersionUID = 5542172141759168416L;
/*     */     
/*     */     TerminatorEvent() {
/* 598 */       super(new Object());
/*     */     }
/*     */ 
/*     */     
/*     */     public void dispatch(Object listener) {
/* 603 */       Thread.currentThread().interrupt();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void terminateQueue() {
/* 609 */     synchronized (this.qLock) {
/* 610 */       if (this.q != null) {
/* 611 */         Vector dummyListeners = new Vector();
/* 612 */         dummyListeners.setSize(1);
/* 613 */         this.q.enqueue(new TerminatorEvent(), dummyListeners);
/* 614 */         this.q = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 623 */     super.finalize();
/* 624 */     terminateQueue();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Service.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */