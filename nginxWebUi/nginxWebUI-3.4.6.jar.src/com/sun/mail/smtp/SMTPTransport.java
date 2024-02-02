/*      */ package com.sun.mail.smtp;
/*      */ 
/*      */ import com.sun.mail.auth.Ntlm;
/*      */ import com.sun.mail.util.ASCIIUtility;
/*      */ import com.sun.mail.util.BASE64EncoderStream;
/*      */ import com.sun.mail.util.LineInputStream;
/*      */ import com.sun.mail.util.MailLogger;
/*      */ import com.sun.mail.util.PropUtil;
/*      */ import com.sun.mail.util.SocketFetcher;
/*      */ import com.sun.mail.util.TraceInputStream;
/*      */ import com.sun.mail.util.TraceOutputStream;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.StringReader;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.net.InetAddress;
/*      */ import java.net.Socket;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import java.util.logging.Level;
/*      */ import javax.mail.Address;
/*      */ import javax.mail.AuthenticationFailedException;
/*      */ import javax.mail.Message;
/*      */ import javax.mail.MessagingException;
/*      */ import javax.mail.SendFailedException;
/*      */ import javax.mail.Session;
/*      */ import javax.mail.Transport;
/*      */ import javax.mail.URLName;
/*      */ import javax.mail.internet.AddressException;
/*      */ import javax.mail.internet.InternetAddress;
/*      */ import javax.mail.internet.MimeMessage;
/*      */ import javax.mail.internet.MimeMultipart;
/*      */ import javax.mail.internet.MimePart;
/*      */ import javax.mail.internet.ParseException;
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
/*      */ public class SMTPTransport
/*      */   extends Transport
/*      */ {
/*   91 */   private String name = "smtp";
/*   92 */   private int defaultPort = 25;
/*      */   
/*      */   private boolean isSSL = false;
/*      */   
/*      */   private String host;
/*      */   
/*      */   private MimeMessage message;
/*      */   
/*      */   private Address[] addresses;
/*      */   
/*      */   private Address[] validSentAddr;
/*      */   
/*      */   private Address[] validUnsentAddr;
/*      */   
/*      */   private Address[] invalidAddr;
/*      */   private boolean sendPartiallyFailed = false;
/*      */   private MessagingException exception;
/*      */   private SMTPOutputStream dataStream;
/*      */   private Hashtable extMap;
/*  111 */   private Map authenticators = new HashMap();
/*      */   
/*      */   private String defaultAuthenticationMechanisms;
/*      */   
/*      */   private boolean quitWait = false;
/*  116 */   private String saslRealm = "UNKNOWN";
/*  117 */   private String authorizationID = "UNKNOWN";
/*      */   private boolean enableSASL = false;
/*  119 */   private String[] saslMechanisms = UNKNOWN_SA;
/*      */   
/*  121 */   private String ntlmDomain = "UNKNOWN";
/*      */   
/*      */   private boolean reportSuccess;
/*      */   
/*      */   private boolean useStartTLS;
/*      */   
/*      */   private boolean requireStartTLS;
/*      */   
/*      */   private boolean useRset;
/*      */   
/*      */   private boolean noopStrict = true;
/*      */   
/*      */   private MailLogger logger;
/*      */   private MailLogger traceLogger;
/*      */   private String localHostName;
/*      */   private String lastServerResponse;
/*      */   private int lastReturnCode;
/*      */   private boolean notificationDone;
/*      */   private SaslAuthenticator saslAuthenticator;
/*      */   private boolean noauthdebug = true;
/*  141 */   private static final String[] ignoreList = new String[] { "Bcc", "Content-Length" };
/*  142 */   private static final byte[] CRLF = new byte[] { 13, 10 };
/*      */   private static final String UNKNOWN = "UNKNOWN";
/*  144 */   private static final String[] UNKNOWN_SA = new String[0]; private BufferedInputStream serverInput; private LineInputStream lineInputStream;
/*      */   private OutputStream serverOutput;
/*      */   private Socket serverSocket;
/*      */   private TraceInputStream traceInput;
/*      */   private TraceOutputStream traceOutput;
/*      */   
/*      */   public SMTPTransport(Session session, URLName urlname) {
/*  151 */     this(session, urlname, "smtp", false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SMTPTransport(Session session, URLName urlname, String name, boolean isSSL) {
/*  159 */     super(session, urlname);
/*  160 */     this.logger = new MailLogger(getClass(), "DEBUG SMTP", session);
/*  161 */     this.traceLogger = this.logger.getSubLogger("protocol", null);
/*  162 */     this.noauthdebug = !PropUtil.getBooleanSessionProperty(session, "mail.debug.auth", false);
/*      */     
/*  164 */     if (urlname != null)
/*  165 */       name = urlname.getProtocol(); 
/*  166 */     this.name = name;
/*  167 */     if (!isSSL) {
/*  168 */       isSSL = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".ssl.enable", false);
/*      */     }
/*  170 */     if (isSSL) {
/*  171 */       this.defaultPort = 465;
/*      */     } else {
/*  173 */       this.defaultPort = 25;
/*  174 */     }  this.isSSL = isSSL;
/*      */ 
/*      */ 
/*      */     
/*  178 */     this.quitWait = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".quitwait", true);
/*      */ 
/*      */ 
/*      */     
/*  182 */     this.reportSuccess = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".reportsuccess", false);
/*      */ 
/*      */ 
/*      */     
/*  186 */     this.useStartTLS = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".starttls.enable", false);
/*      */ 
/*      */ 
/*      */     
/*  190 */     this.requireStartTLS = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".starttls.required", false);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  195 */     this.useRset = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".userset", false);
/*      */ 
/*      */ 
/*      */     
/*  199 */     this.noopStrict = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".noop.strict", true);
/*      */ 
/*      */ 
/*      */     
/*  203 */     this.enableSASL = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".sasl.enable", false);
/*      */     
/*  205 */     if (this.enableSASL) {
/*  206 */       this.logger.config("enable SASL");
/*      */     }
/*      */     
/*  209 */     Authenticator[] a = { new LoginAuthenticator(), new PlainAuthenticator(), new DigestMD5Authenticator(), new NtlmAuthenticator() };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  215 */     StringBuffer sb = new StringBuffer();
/*  216 */     for (int i = 0; i < a.length; i++) {
/*  217 */       this.authenticators.put(a[i].getMechanism(), a[i]);
/*  218 */       sb.append(a[i].getMechanism()).append(' ');
/*      */     } 
/*  220 */     this.defaultAuthenticationMechanisms = sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String getLocalHost() {
/*  230 */     if (this.localHostName == null || this.localHostName.length() <= 0) {
/*  231 */       this.localHostName = this.session.getProperty("mail." + this.name + ".localhost");
/*      */     }
/*  233 */     if (this.localHostName == null || this.localHostName.length() <= 0) {
/*  234 */       this.localHostName = this.session.getProperty("mail." + this.name + ".localaddress");
/*      */     }
/*      */     try {
/*  237 */       if (this.localHostName == null || this.localHostName.length() <= 0) {
/*  238 */         InetAddress localHost = InetAddress.getLocalHost();
/*  239 */         this.localHostName = localHost.getCanonicalHostName();
/*      */         
/*  241 */         if (this.localHostName == null)
/*      */         {
/*  243 */           this.localHostName = "[" + localHost.getHostAddress() + "]"; } 
/*      */       } 
/*  245 */     } catch (UnknownHostException uhex) {}
/*      */ 
/*      */ 
/*      */     
/*  249 */     if ((this.localHostName == null || this.localHostName.length() <= 0) && 
/*  250 */       this.serverSocket != null && this.serverSocket.isBound()) {
/*  251 */       InetAddress localHost = this.serverSocket.getLocalAddress();
/*  252 */       this.localHostName = localHost.getCanonicalHostName();
/*      */       
/*  254 */       if (this.localHostName == null)
/*      */       {
/*  256 */         this.localHostName = "[" + localHost.getHostAddress() + "]";
/*      */       }
/*      */     } 
/*  259 */     return this.localHostName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setLocalHost(String localhost) {
/*  268 */     this.localHostName = localhost;
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
/*      */   public synchronized void connect(Socket socket) throws MessagingException {
/*  280 */     this.serverSocket = socket;
/*  281 */     connect();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String getAuthorizationId() {
/*  292 */     if (this.authorizationID == "UNKNOWN") {
/*  293 */       this.authorizationID = this.session.getProperty("mail." + this.name + ".sasl.authorizationid");
/*      */     }
/*      */     
/*  296 */     return this.authorizationID;
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
/*      */   public synchronized void setAuthorizationID(String authzid) {
/*  308 */     this.authorizationID = authzid;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean getSASLEnabled() {
/*  319 */     return this.enableSASL;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setSASLEnabled(boolean enableSASL) {
/*  330 */     this.enableSASL = enableSASL;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String getSASLRealm() {
/*  341 */     if (this.saslRealm == "UNKNOWN") {
/*  342 */       this.saslRealm = this.session.getProperty("mail." + this.name + ".sasl.realm");
/*  343 */       if (this.saslRealm == null)
/*  344 */         this.saslRealm = this.session.getProperty("mail." + this.name + ".saslrealm"); 
/*      */     } 
/*  346 */     return this.saslRealm;
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
/*      */   public synchronized void setSASLRealm(String saslRealm) {
/*  358 */     this.saslRealm = saslRealm;
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
/*      */   public synchronized String[] getSASLMechanisms() {
/*  371 */     if (this.saslMechanisms == UNKNOWN_SA) {
/*  372 */       List v = new ArrayList(5);
/*  373 */       String s = this.session.getProperty("mail." + this.name + ".sasl.mechanisms");
/*  374 */       if (s != null && s.length() > 0) {
/*  375 */         if (this.logger.isLoggable(Level.FINE))
/*  376 */           this.logger.fine("SASL mechanisms allowed: " + s); 
/*  377 */         StringTokenizer st = new StringTokenizer(s, " ,");
/*  378 */         while (st.hasMoreTokens()) {
/*  379 */           String m = st.nextToken();
/*  380 */           if (m.length() > 0)
/*  381 */             v.add(m); 
/*      */         } 
/*      */       } 
/*  384 */       this.saslMechanisms = new String[v.size()];
/*  385 */       v.toArray(this.saslMechanisms);
/*      */     } 
/*  387 */     if (this.saslMechanisms == null)
/*  388 */       return null; 
/*  389 */     return (String[])this.saslMechanisms.clone();
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
/*      */   public synchronized void setSASLMechanisms(String[] mechanisms) {
/*  402 */     if (mechanisms != null)
/*  403 */       mechanisms = (String[])mechanisms.clone(); 
/*  404 */     this.saslMechanisms = mechanisms;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String getNTLMDomain() {
/*  415 */     if (this.ntlmDomain == "UNKNOWN") {
/*  416 */       this.ntlmDomain = this.session.getProperty("mail." + this.name + ".auth.ntlm.domain");
/*      */     }
/*      */     
/*  419 */     return this.ntlmDomain;
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
/*      */   public synchronized void setNTLMDomain(String ntlmDomain) {
/*  431 */     this.ntlmDomain = ntlmDomain;
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
/*      */   public synchronized boolean getReportSuccess() {
/*  448 */     return this.reportSuccess;
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
/*      */   public synchronized void setReportSuccess(boolean reportSuccess) {
/*  460 */     this.reportSuccess = reportSuccess;
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
/*      */   public synchronized boolean getStartTLS() {
/*  472 */     return this.useStartTLS;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setStartTLS(boolean useStartTLS) {
/*  483 */     this.useStartTLS = useStartTLS;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean getRequireStartTLS() {
/*  494 */     return this.requireStartTLS;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setRequireStartTLS(boolean requireStartTLS) {
/*  505 */     this.requireStartTLS = requireStartTLS;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSSL() {
/*  515 */     return this.serverSocket instanceof javax.net.ssl.SSLSocket;
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
/*      */   public synchronized boolean getUseRset() {
/*  527 */     return this.useRset;
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
/*      */   public synchronized void setUseRset(boolean useRset) {
/*  539 */     this.useRset = useRset;
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
/*      */   public synchronized boolean getNoopStrict() {
/*  551 */     return this.noopStrict;
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
/*      */   public synchronized void setNoopStrict(boolean noopStrict) {
/*  563 */     this.noopStrict = noopStrict;
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
/*      */   public synchronized String getLastServerResponse() {
/*  578 */     return this.lastServerResponse;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getLastReturnCode() {
/*  589 */     return this.lastReturnCode;
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
/*      */   protected synchronized boolean protocolConnect(String host, int port, String user, String passwd) throws MessagingException {
/*  613 */     boolean useEhlo = PropUtil.getBooleanSessionProperty(this.session, "mail." + this.name + ".ehlo", true);
/*      */ 
/*      */     
/*  616 */     boolean useAuth = PropUtil.getBooleanSessionProperty(this.session, "mail." + this.name + ".auth", false);
/*      */ 
/*      */     
/*  619 */     if (this.logger.isLoggable(Level.FINE)) {
/*  620 */       this.logger.fine("useEhlo " + useEhlo + ", useAuth " + useAuth);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  628 */     if (useAuth && (user == null || passwd == null)) {
/*  629 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  635 */     if (port == -1) {
/*  636 */       port = PropUtil.getIntSessionProperty(this.session, "mail." + this.name + ".port", -1);
/*      */     }
/*  638 */     if (port == -1) {
/*  639 */       port = this.defaultPort;
/*      */     }
/*  641 */     if (host == null || host.length() == 0) {
/*  642 */       host = "localhost";
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  648 */     boolean connected = false;
/*      */     
/*      */     try {
/*  651 */       if (this.serverSocket != null) {
/*  652 */         openServer();
/*      */       } else {
/*  654 */         openServer(host, port);
/*      */       } 
/*  656 */       boolean succeed = false;
/*  657 */       if (useEhlo)
/*  658 */         succeed = ehlo(getLocalHost()); 
/*  659 */       if (!succeed) {
/*  660 */         helo(getLocalHost());
/*      */       }
/*  662 */       if (this.useStartTLS || this.requireStartTLS) {
/*  663 */         if (this.serverSocket instanceof javax.net.ssl.SSLSocket) {
/*  664 */           this.logger.fine("STARTTLS requested but already using SSL");
/*  665 */         } else if (supportsExtension("STARTTLS")) {
/*  666 */           startTLS();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  673 */           ehlo(getLocalHost());
/*  674 */         } else if (this.requireStartTLS) {
/*  675 */           this.logger.fine("STARTTLS required but not supported");
/*  676 */           throw new MessagingException("STARTTLS is required but host does not support STARTTLS");
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  682 */       if ((useAuth || (user != null && passwd != null)) && (supportsExtension("AUTH") || supportsExtension("AUTH=LOGIN"))) {
/*      */ 
/*      */         
/*  685 */         connected = authenticate(user, passwd);
/*  686 */         return connected;
/*      */       } 
/*      */ 
/*      */       
/*  690 */       connected = true;
/*  691 */       return true;
/*      */     
/*      */     }
/*      */     finally {
/*      */       
/*  696 */       if (!connected) {
/*      */         try {
/*  698 */           closeConnection();
/*  699 */         } catch (MessagingException mex) {}
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
/*      */   private boolean authenticate(String user, String passwd) throws MessagingException {
/*  712 */     String mechs = this.session.getProperty("mail." + this.name + ".auth.mechanisms");
/*  713 */     if (mechs == null) {
/*  714 */       mechs = this.defaultAuthenticationMechanisms;
/*      */     }
/*  716 */     String authzid = getAuthorizationId();
/*  717 */     if (authzid == null)
/*  718 */       authzid = user; 
/*  719 */     if (this.enableSASL) {
/*  720 */       this.logger.fine("Authenticate with SASL");
/*  721 */       if (sasllogin(getSASLMechanisms(), getSASLRealm(), authzid, user, passwd))
/*      */       {
/*  723 */         return true; } 
/*  724 */       this.logger.fine("SASL authentication failed");
/*      */     } 
/*      */     
/*  727 */     if (this.logger.isLoggable(Level.FINE)) {
/*  728 */       this.logger.fine("Attempt to authenticate using mechanisms: " + mechs);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  736 */     StringTokenizer st = new StringTokenizer(mechs);
/*  737 */     while (st.hasMoreTokens()) {
/*  738 */       String m = st.nextToken();
/*  739 */       String dprop = "mail." + this.name + ".auth." + m.toLowerCase(Locale.ENGLISH) + ".disable";
/*      */       
/*  741 */       boolean disabled = PropUtil.getBooleanSessionProperty(this.session, dprop, false);
/*      */       
/*  743 */       if (disabled) {
/*  744 */         if (this.logger.isLoggable(Level.FINE)) {
/*  745 */           this.logger.fine("mechanism " + m + " disabled by property: " + dprop);
/*      */         }
/*      */         continue;
/*      */       } 
/*  749 */       m = m.toUpperCase(Locale.ENGLISH);
/*  750 */       if (!supportsAuthentication(m)) {
/*  751 */         this.logger.log(Level.FINE, "mechanism {0} not supported by server", m);
/*      */         
/*      */         continue;
/*      */       } 
/*  755 */       Authenticator a = (Authenticator)this.authenticators.get(m);
/*  756 */       if (a == null) {
/*  757 */         this.logger.log(Level.FINE, "no authenticator for mechanism {0}", m);
/*      */         
/*      */         continue;
/*      */       } 
/*  761 */       return a.authenticate(this.host, authzid, user, passwd);
/*      */     } 
/*      */ 
/*      */     
/*  765 */     throw new AuthenticationFailedException("No authentication mechansims supported by both server and client");
/*      */   }
/*      */ 
/*      */   
/*      */   private abstract class Authenticator
/*      */   {
/*      */     protected int resp;
/*      */     
/*      */     private String mech;
/*      */     private final SMTPTransport this$0;
/*      */     
/*      */     Authenticator(String mech) {
/*  777 */       this.mech = mech.toUpperCase(Locale.ENGLISH);
/*      */     }
/*      */     
/*      */     String getMechanism() {
/*  781 */       return this.mech;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean authenticate(String host, String authzid, String user, String passwd) throws MessagingException {
/*      */       try {
/*  793 */         String ir = getInitialResponse(host, authzid, user, passwd);
/*  794 */         if (SMTPTransport.this.noauthdebug && SMTPTransport.this.isTracing()) {
/*  795 */           SMTPTransport.this.logger.fine("AUTH " + this.mech + " command trace suppressed");
/*  796 */           SMTPTransport.this.suspendTracing();
/*      */         } 
/*  798 */         if (ir != null) {
/*  799 */           this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech + " " + ((ir.length() == 0) ? "=" : ir));
/*      */         } else {
/*      */           
/*  802 */           this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  808 */         if (this.resp == 530) {
/*  809 */           SMTPTransport.this.startTLS();
/*  810 */           if (ir != null) {
/*  811 */             this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech + " " + ir);
/*      */           } else {
/*  813 */             this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech);
/*      */           } 
/*  815 */         }  if (this.resp == 334)
/*  816 */           doAuth(host, authzid, user, passwd); 
/*  817 */       } catch (IOException ex) {
/*  818 */         SMTPTransport.this.logger.log(Level.FINE, "AUTH " + this.mech + " failed", ex);
/*      */       } finally {
/*  820 */         if (SMTPTransport.this.noauthdebug && SMTPTransport.this.isTracing()) {
/*  821 */           SMTPTransport.this.logger.fine("AUTH " + this.mech + " " + ((this.resp == 235) ? "succeeded" : "failed"));
/*      */         }
/*  823 */         SMTPTransport.this.resumeTracing();
/*  824 */         if (this.resp != 235) {
/*  825 */           SMTPTransport.this.closeConnection();
/*  826 */           throw new AuthenticationFailedException(SMTPTransport.this.getLastServerResponse());
/*      */         } 
/*      */       } 
/*      */       
/*  830 */       return true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String getInitialResponse(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
/*  840 */       return null;
/*      */     }
/*      */     
/*      */     abstract void doAuth(String param1String1, String param1String2, String param1String3, String param1String4) throws MessagingException, IOException;
/*      */   }
/*      */   
/*      */   private class LoginAuthenticator
/*      */     extends Authenticator
/*      */   {
/*      */     private final SMTPTransport this$0;
/*      */     
/*      */     LoginAuthenticator() {
/*  852 */       super("LOGIN");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void doAuth(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
/*  858 */       this.resp = SMTPTransport.this.simpleCommand(BASE64EncoderStream.encode(ASCIIUtility.getBytes(user)));
/*      */       
/*  860 */       if (this.resp == 334)
/*      */       {
/*  862 */         this.resp = SMTPTransport.this.simpleCommand(BASE64EncoderStream.encode(ASCIIUtility.getBytes(passwd)));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private class PlainAuthenticator
/*      */     extends Authenticator
/*      */   {
/*      */     private final SMTPTransport this$0;
/*      */     
/*      */     PlainAuthenticator() {
/*  873 */       super("PLAIN");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     String getInitialResponse(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
/*  879 */       ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  880 */       BASE64EncoderStream bASE64EncoderStream = new BASE64EncoderStream(bos, 2147483647);
/*      */       
/*  882 */       if (authzid != null)
/*  883 */         bASE64EncoderStream.write(ASCIIUtility.getBytes(authzid)); 
/*  884 */       bASE64EncoderStream.write(0);
/*  885 */       bASE64EncoderStream.write(ASCIIUtility.getBytes(user));
/*  886 */       bASE64EncoderStream.write(0);
/*  887 */       bASE64EncoderStream.write(ASCIIUtility.getBytes(passwd));
/*  888 */       bASE64EncoderStream.flush();
/*      */       
/*  890 */       return ASCIIUtility.toString(bos.toByteArray());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void doAuth(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
/*  896 */       throw new AuthenticationFailedException("PLAIN asked for more");
/*      */     }
/*      */   }
/*      */   
/*      */   private class DigestMD5Authenticator
/*      */     extends Authenticator
/*      */   {
/*      */     private DigestMD5 md5support;
/*      */     private final SMTPTransport this$0;
/*      */     
/*      */     DigestMD5Authenticator() {
/*  907 */       super("DIGEST-MD5");
/*      */     }
/*      */     
/*      */     private synchronized DigestMD5 getMD5() {
/*  911 */       if (this.md5support == null)
/*  912 */         this.md5support = new DigestMD5(SMTPTransport.this.logger); 
/*  913 */       return this.md5support;
/*      */     }
/*      */ 
/*      */     
/*      */     void doAuth(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
/*  918 */       DigestMD5 md5 = getMD5();
/*  919 */       if (md5 == null) {
/*  920 */         this.resp = -1;
/*      */         
/*      */         return;
/*      */       } 
/*  924 */       byte[] b = md5.authClient(host, user, passwd, SMTPTransport.this.getSASLRealm(), SMTPTransport.this.getLastServerResponse());
/*      */       
/*  926 */       this.resp = SMTPTransport.this.simpleCommand(b);
/*  927 */       if (this.resp == 334) {
/*  928 */         if (!md5.authServer(SMTPTransport.this.getLastServerResponse())) {
/*      */           
/*  930 */           this.resp = -1;
/*      */         } else {
/*      */           
/*  933 */           this.resp = SMTPTransport.this.simpleCommand(new byte[0]);
/*      */         } 
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private class NtlmAuthenticator
/*      */     extends Authenticator
/*      */   {
/*      */     private Ntlm ntlm;
/*      */     private int flags;
/*      */     private final SMTPTransport this$0;
/*      */     
/*      */     NtlmAuthenticator() {
/*  947 */       super("NTLM");
/*      */     }
/*      */ 
/*      */     
/*      */     String getInitialResponse(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
/*  952 */       this.ntlm = new Ntlm(SMTPTransport.this.getNTLMDomain(), SMTPTransport.this.getLocalHost(), user, passwd, SMTPTransport.this.logger);
/*      */ 
/*      */       
/*  955 */       this.flags = PropUtil.getIntProperty(SMTPTransport.this.session.getProperties(), "mail." + SMTPTransport.this.name + ".auth.ntlm.flags", 0);
/*      */ 
/*      */ 
/*      */       
/*  959 */       String type1 = this.ntlm.generateType1Msg(this.flags);
/*  960 */       return type1;
/*      */     }
/*      */ 
/*      */     
/*      */     void doAuth(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
/*  965 */       String type3 = this.ntlm.generateType3Msg(SMTPTransport.this.getLastServerResponse().substring(4).trim());
/*      */ 
/*      */       
/*  968 */       this.resp = SMTPTransport.this.simpleCommand(type3);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean sasllogin(String[] allowed, String realm, String authzid, String u, String p) throws MessagingException {
/*      */     List v;
/*  977 */     if (this.saslAuthenticator == null) {
/*      */       try {
/*  979 */         Class sac = Class.forName("com.sun.mail.smtp.SMTPSaslAuthenticator");
/*      */         
/*  981 */         Constructor c = sac.getConstructor(new Class[] { SMTPTransport.class, String.class, Properties.class, MailLogger.class, String.class });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  988 */         this.saslAuthenticator = (SaslAuthenticator)c.newInstance(new Object[] { this, this.name, this.session.getProperties(), this.logger, this.host });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  996 */       catch (Exception ex) {
/*  997 */         this.logger.log(Level.FINE, "Can't load SASL authenticator", ex);
/*      */         
/*  999 */         return false;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1005 */     if (allowed != null && allowed.length > 0) {
/*      */       
/* 1007 */       v = new ArrayList(allowed.length);
/* 1008 */       for (int i = 0; i < allowed.length; i++) {
/* 1009 */         if (supportsAuthentication(allowed[i]))
/* 1010 */           v.add(allowed[i]); 
/*      */       } 
/*      */     } else {
/* 1013 */       v = new ArrayList();
/* 1014 */       if (this.extMap != null) {
/* 1015 */         String a = (String)this.extMap.get("AUTH");
/* 1016 */         if (a != null) {
/* 1017 */           StringTokenizer st = new StringTokenizer(a);
/* 1018 */           while (st.hasMoreTokens())
/* 1019 */             v.add(st.nextToken()); 
/*      */         } 
/*      */       } 
/*      */     } 
/* 1023 */     String[] mechs = v.<String>toArray(new String[v.size()]);
/*      */     try {
/* 1025 */       if (this.noauthdebug && isTracing()) {
/* 1026 */         this.logger.fine("SASL AUTH command trace suppressed");
/* 1027 */         suspendTracing();
/*      */       } 
/* 1029 */       return this.saslAuthenticator.authenticate(mechs, realm, authzid, u, p);
/*      */     } finally {
/* 1031 */       resumeTracing();
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
/*      */ 
/*      */   
/*      */   public synchronized void sendMessage(Message message, Address[] addresses) throws MessagingException, SendFailedException {
/* 1073 */     sendMessageStart((message != null) ? message.getSubject() : "");
/* 1074 */     checkConnected();
/*      */ 
/*      */ 
/*      */     
/* 1078 */     if (!(message instanceof MimeMessage)) {
/* 1079 */       this.logger.fine("Can only send RFC822 msgs");
/* 1080 */       throw new MessagingException("SMTP can only send RFC822 messages");
/*      */     } 
/* 1082 */     for (int i = 0; i < addresses.length; i++) {
/* 1083 */       if (!(addresses[i] instanceof InternetAddress)) {
/* 1084 */         throw new MessagingException(addresses[i] + " is not an InternetAddress");
/*      */       }
/*      */     } 
/*      */     
/* 1088 */     if (addresses.length == 0) {
/* 1089 */       throw new SendFailedException("No recipient addresses");
/*      */     }
/* 1091 */     this.message = (MimeMessage)message;
/* 1092 */     this.addresses = addresses;
/* 1093 */     this.validUnsentAddr = addresses;
/* 1094 */     expandGroups();
/*      */     
/* 1096 */     boolean use8bit = false;
/* 1097 */     if (message instanceof SMTPMessage)
/* 1098 */       use8bit = ((SMTPMessage)message).getAllow8bitMIME(); 
/* 1099 */     if (!use8bit) {
/* 1100 */       use8bit = PropUtil.getBooleanSessionProperty(this.session, "mail." + this.name + ".allow8bitmime", false);
/*      */     }
/* 1102 */     if (this.logger.isLoggable(Level.FINE))
/* 1103 */       this.logger.fine("use8bit " + use8bit); 
/* 1104 */     if (use8bit && supportsExtension("8BITMIME") && 
/* 1105 */       convertTo8Bit((MimePart)this.message)) {
/*      */       
/*      */       try {
/*      */         
/* 1109 */         this.message.saveChanges();
/* 1110 */       } catch (MessagingException mex) {}
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1117 */       mailFrom();
/* 1118 */       rcptTo();
/* 1119 */       this.message.writeTo(data(), ignoreList);
/* 1120 */       finishData();
/* 1121 */       if (this.sendPartiallyFailed) {
/*      */ 
/*      */         
/* 1124 */         this.logger.fine("Sending partially failed because of invalid destination addresses");
/*      */         
/* 1126 */         notifyTransportListeners(3, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, (Message)this.message);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1131 */         throw new SMTPSendFailedException(".", this.lastReturnCode, this.lastServerResponse, this.exception, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
/*      */       } 
/*      */ 
/*      */       
/* 1135 */       notifyTransportListeners(1, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, (Message)this.message);
/*      */     
/*      */     }
/* 1138 */     catch (MessagingException mex) {
/* 1139 */       this.logger.log(Level.FINE, "MessagingException while sending", (Throwable)mex);
/*      */       
/* 1141 */       if (mex.getNextException() instanceof IOException) {
/*      */ 
/*      */         
/* 1144 */         this.logger.fine("nested IOException, closing");
/*      */         try {
/* 1146 */           closeConnection();
/* 1147 */         } catch (MessagingException cex) {}
/*      */       } 
/* 1149 */       addressesFailed();
/* 1150 */       notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, (Message)this.message);
/*      */ 
/*      */ 
/*      */       
/* 1154 */       throw mex;
/* 1155 */     } catch (IOException ex) {
/* 1156 */       this.logger.log(Level.FINE, "IOException while sending, closing", ex);
/*      */ 
/*      */       
/*      */       try {
/* 1160 */         closeConnection();
/* 1161 */       } catch (MessagingException mex) {}
/* 1162 */       addressesFailed();
/* 1163 */       notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, (Message)this.message);
/*      */ 
/*      */ 
/*      */       
/* 1167 */       throw new MessagingException("IOException while sending message", ex);
/*      */     }
/*      */     finally {
/*      */       
/* 1171 */       this.validSentAddr = this.validUnsentAddr = this.invalidAddr = null;
/* 1172 */       this.addresses = null;
/* 1173 */       this.message = null;
/* 1174 */       this.exception = null;
/* 1175 */       this.sendPartiallyFailed = false;
/* 1176 */       this.notificationDone = false;
/*      */     } 
/* 1178 */     sendMessageEnd();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addressesFailed() {
/* 1185 */     if (this.validSentAddr != null) {
/* 1186 */       if (this.validUnsentAddr != null) {
/* 1187 */         Address[] newa = new Address[this.validSentAddr.length + this.validUnsentAddr.length];
/*      */         
/* 1189 */         System.arraycopy(this.validSentAddr, 0, newa, 0, this.validSentAddr.length);
/*      */         
/* 1191 */         System.arraycopy(this.validUnsentAddr, 0, newa, this.validSentAddr.length, this.validUnsentAddr.length);
/*      */         
/* 1193 */         this.validSentAddr = null;
/* 1194 */         this.validUnsentAddr = newa;
/*      */       } else {
/* 1196 */         this.validUnsentAddr = this.validSentAddr;
/* 1197 */         this.validSentAddr = null;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void close() throws MessagingException {
/* 1206 */     if (!super.isConnected())
/*      */       return; 
/*      */     try {
/* 1209 */       if (this.serverSocket != null) {
/* 1210 */         sendCommand("QUIT");
/* 1211 */         if (this.quitWait) {
/* 1212 */           int resp = readServerResponse();
/* 1213 */           if (resp != 221 && resp != -1 && this.logger.isLoggable(Level.FINE))
/*      */           {
/* 1215 */             this.logger.fine("QUIT failed with " + resp); } 
/*      */         } 
/*      */       } 
/*      */     } finally {
/* 1219 */       closeConnection();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void closeConnection() throws MessagingException {
/*      */     try {
/* 1225 */       if (this.serverSocket != null)
/* 1226 */         this.serverSocket.close(); 
/* 1227 */     } catch (IOException ioex) {
/* 1228 */       throw new MessagingException("Server Close Failed", ioex);
/*      */     } finally {
/* 1230 */       this.serverSocket = null;
/* 1231 */       this.serverOutput = null;
/* 1232 */       this.serverInput = null;
/* 1233 */       this.lineInputStream = null;
/* 1234 */       if (super.isConnected()) {
/* 1235 */         super.close();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isConnected() {
/* 1244 */     if (!super.isConnected())
/*      */     {
/* 1246 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/* 1251 */       if (this.useRset) {
/* 1252 */         sendCommand("RSET");
/*      */       } else {
/* 1254 */         sendCommand("NOOP");
/* 1255 */       }  int resp = readServerResponse();
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
/* 1274 */       if (resp >= 0 && (this.noopStrict ? (resp == 250) : (resp != 421))) {
/* 1275 */         return true;
/*      */       }
/*      */       try {
/* 1278 */         closeConnection();
/* 1279 */       } catch (MessagingException mex) {}
/* 1280 */       return false;
/*      */     }
/* 1282 */     catch (Exception ex) {
/*      */       try {
/* 1284 */         closeConnection();
/* 1285 */       } catch (MessagingException mex) {}
/* 1286 */       return false;
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
/*      */   protected void notifyTransportListeners(int type, Address[] validSent, Address[] validUnsent, Address[] invalid, Message msg) {
/* 1300 */     if (!this.notificationDone) {
/* 1301 */       super.notifyTransportListeners(type, validSent, validUnsent, invalid, msg);
/*      */       
/* 1303 */       this.notificationDone = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void expandGroups() {
/* 1311 */     Vector groups = null;
/* 1312 */     for (int i = 0; i < this.addresses.length; i++) {
/* 1313 */       InternetAddress a = (InternetAddress)this.addresses[i];
/* 1314 */       if (a.isGroup()) {
/* 1315 */         if (groups == null) {
/*      */           
/* 1317 */           groups = new Vector();
/* 1318 */           for (int k = 0; k < i; k++) {
/* 1319 */             groups.addElement(this.addresses[k]);
/*      */           }
/*      */         } 
/*      */         try {
/* 1323 */           InternetAddress[] ia = a.getGroup(true);
/* 1324 */           if (ia != null)
/* 1325 */           { for (int j = 0; j < ia.length; j++)
/* 1326 */               groups.addElement(ia[j]);  }
/*      */           else
/* 1328 */           { groups.addElement(a); } 
/* 1329 */         } catch (ParseException pex) {
/*      */           
/* 1331 */           groups.addElement(a);
/*      */         }
/*      */       
/*      */       }
/* 1335 */       else if (groups != null) {
/* 1336 */         groups.addElement(a);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1341 */     if (groups != null) {
/* 1342 */       InternetAddress[] newa = new InternetAddress[groups.size()];
/* 1343 */       groups.copyInto((Object[])newa);
/* 1344 */       this.addresses = (Address[])newa;
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
/*      */   private boolean convertTo8Bit(MimePart part) {
/* 1359 */     boolean changed = false;
/*      */     try {
/* 1361 */       if (part.isMimeType("text/*")) {
/* 1362 */         String enc = part.getEncoding();
/* 1363 */         if (enc != null && (enc.equalsIgnoreCase("quoted-printable") || enc.equalsIgnoreCase("base64")))
/*      */         {
/* 1365 */           InputStream is = null;
/*      */           try {
/* 1367 */             is = part.getInputStream();
/* 1368 */             if (is8Bit(is)) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1378 */               part.setContent(part.getContent(), part.getContentType());
/*      */               
/* 1380 */               part.setHeader("Content-Transfer-Encoding", "8bit");
/* 1381 */               changed = true;
/*      */             } 
/*      */           } finally {
/* 1384 */             if (is != null) {
/*      */               try {
/* 1386 */                 is.close();
/* 1387 */               } catch (IOException ex2) {}
/*      */             }
/*      */           }
/*      */         
/*      */         }
/*      */       
/* 1393 */       } else if (part.isMimeType("multipart/*")) {
/* 1394 */         MimeMultipart mp = (MimeMultipart)part.getContent();
/* 1395 */         int count = mp.getCount();
/* 1396 */         for (int i = 0; i < count; i++) {
/* 1397 */           if (convertTo8Bit((MimePart)mp.getBodyPart(i)))
/* 1398 */             changed = true; 
/*      */         } 
/*      */       } 
/* 1401 */     } catch (IOException ioex) {
/*      */     
/* 1403 */     } catch (MessagingException mex) {}
/*      */ 
/*      */     
/* 1406 */     return changed;
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
/*      */   private boolean is8Bit(InputStream is) {
/* 1418 */     int linelen = 0;
/* 1419 */     boolean need8bit = false; try {
/*      */       int b;
/* 1421 */       while ((b = is.read()) >= 0) {
/* 1422 */         b &= 0xFF;
/* 1423 */         if (b == 13 || b == 10)
/* 1424 */         { linelen = 0; }
/* 1425 */         else { if (b == 0) {
/* 1426 */             return false;
/*      */           }
/* 1428 */           linelen++;
/* 1429 */           if (linelen > 998)
/* 1430 */             return false;  }
/*      */         
/* 1432 */         if (b > 127)
/* 1433 */           need8bit = true; 
/*      */       } 
/* 1435 */     } catch (IOException ex) {
/* 1436 */       return false;
/*      */     } 
/* 1438 */     if (need8bit)
/* 1439 */       this.logger.fine("found an 8bit part"); 
/* 1440 */     return need8bit;
/*      */   }
/*      */   
/*      */   protected void finalize() throws Throwable {
/* 1444 */     super.finalize();
/*      */     try {
/* 1446 */       closeConnection();
/* 1447 */     } catch (MessagingException mex) {}
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
/*      */   protected void helo(String domain) throws MessagingException {
/* 1468 */     if (domain != null) {
/* 1469 */       issueCommand("HELO " + domain, 250);
/*      */     } else {
/* 1471 */       issueCommand("HELO", 250);
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
/*      */   protected boolean ehlo(String domain) throws MessagingException {
/*      */     String cmd;
/* 1485 */     if (domain != null) {
/* 1486 */       cmd = "EHLO " + domain;
/*      */     } else {
/* 1488 */       cmd = "EHLO";
/* 1489 */     }  sendCommand(cmd);
/* 1490 */     int resp = readServerResponse();
/* 1491 */     if (resp == 250) {
/*      */       
/* 1493 */       BufferedReader rd = new BufferedReader(new StringReader(this.lastServerResponse));
/*      */ 
/*      */       
/* 1496 */       this.extMap = new Hashtable();
/*      */       try {
/* 1498 */         boolean first = true; String line;
/* 1499 */         while ((line = rd.readLine()) != null) {
/* 1500 */           if (first) {
/* 1501 */             first = false;
/*      */             continue;
/*      */           } 
/* 1504 */           if (line.length() < 5)
/*      */             continue; 
/* 1506 */           line = line.substring(4);
/* 1507 */           int i = line.indexOf(' ');
/* 1508 */           String arg = "";
/* 1509 */           if (i > 0) {
/* 1510 */             arg = line.substring(i + 1);
/* 1511 */             line = line.substring(0, i);
/*      */           } 
/* 1513 */           if (this.logger.isLoggable(Level.FINE)) {
/* 1514 */             this.logger.fine("Found extension \"" + line + "\", arg \"" + arg + "\"");
/*      */           }
/* 1516 */           this.extMap.put(line.toUpperCase(Locale.ENGLISH), arg);
/*      */         } 
/* 1518 */       } catch (IOException ex) {}
/*      */     } 
/* 1520 */     return (resp == 250);
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
/*      */   protected void mailFrom() throws MessagingException {
/* 1538 */     String from = null;
/* 1539 */     if (this.message instanceof SMTPMessage)
/* 1540 */       from = ((SMTPMessage)this.message).getEnvelopeFrom(); 
/* 1541 */     if (from == null || from.length() <= 0)
/* 1542 */       from = this.session.getProperty("mail." + this.name + ".from"); 
/* 1543 */     if (from == null || from.length() <= 0) {
/*      */       InternetAddress internetAddress;
/*      */       Address[] fa;
/* 1546 */       if (this.message != null && (fa = this.message.getFrom()) != null && fa.length > 0) {
/*      */         
/* 1548 */         Address me = fa[0];
/*      */       } else {
/* 1550 */         internetAddress = InternetAddress.getLocalAddress(this.session);
/*      */       } 
/* 1552 */       if (internetAddress != null) {
/* 1553 */         from = internetAddress.getAddress();
/*      */       } else {
/* 1555 */         throw new MessagingException("can't determine local email address");
/*      */       } 
/*      */     } 
/*      */     
/* 1559 */     String cmd = "MAIL FROM:" + normalizeAddress(from);
/*      */ 
/*      */     
/* 1562 */     if (supportsExtension("DSN")) {
/* 1563 */       String ret = null;
/* 1564 */       if (this.message instanceof SMTPMessage)
/* 1565 */         ret = ((SMTPMessage)this.message).getDSNRet(); 
/* 1566 */       if (ret == null) {
/* 1567 */         ret = this.session.getProperty("mail." + this.name + ".dsn.ret");
/*      */       }
/* 1569 */       if (ret != null) {
/* 1570 */         cmd = cmd + " RET=" + ret;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1578 */     if (supportsExtension("AUTH")) {
/* 1579 */       String submitter = null;
/* 1580 */       if (this.message instanceof SMTPMessage)
/* 1581 */         submitter = ((SMTPMessage)this.message).getSubmitter(); 
/* 1582 */       if (submitter == null) {
/* 1583 */         submitter = this.session.getProperty("mail." + this.name + ".submitter");
/*      */       }
/* 1585 */       if (submitter != null) {
/*      */         try {
/* 1587 */           String s = xtext(submitter);
/* 1588 */           cmd = cmd + " AUTH=" + s;
/* 1589 */         } catch (IllegalArgumentException ex) {
/* 1590 */           if (this.logger.isLoggable(Level.FINE)) {
/* 1591 */             this.logger.log(Level.FINE, "ignoring invalid submitter: " + submitter, ex);
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1600 */     String ext = null;
/* 1601 */     if (this.message instanceof SMTPMessage)
/* 1602 */       ext = ((SMTPMessage)this.message).getMailExtension(); 
/* 1603 */     if (ext == null)
/* 1604 */       ext = this.session.getProperty("mail." + this.name + ".mailextension"); 
/* 1605 */     if (ext != null && ext.length() > 0) {
/* 1606 */       cmd = cmd + " " + ext;
/*      */     }
/*      */     try {
/* 1609 */       issueSendCommand(cmd, 250);
/* 1610 */     } catch (SMTPSendFailedException ex) {
/* 1611 */       int retCode = ex.getReturnCode();
/* 1612 */       switch (retCode) { case 501: case 503:
/*      */         case 550:
/*      */         case 551:
/*      */         case 553:
/* 1616 */           try { ex.setNextException((Exception)new SMTPSenderFailedException(new InternetAddress(from), cmd, retCode, ex.getMessage()));
/*      */              }
/*      */           
/* 1619 */           catch (AddressException aex) {}
/*      */           break; }
/*      */ 
/*      */ 
/*      */       
/* 1624 */       throw ex;
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
/*      */   protected void rcptTo() throws MessagingException {
/*      */     SMTPAddressFailedException sMTPAddressFailedException;
/* 1650 */     Vector valid = new Vector();
/* 1651 */     Vector validUnsent = new Vector();
/* 1652 */     Vector invalid = new Vector();
/* 1653 */     int retCode = -1;
/* 1654 */     MessagingException mex = null;
/* 1655 */     boolean sendFailed = false;
/* 1656 */     MessagingException sfex = null;
/* 1657 */     this.validSentAddr = this.validUnsentAddr = this.invalidAddr = null;
/* 1658 */     boolean sendPartial = false;
/* 1659 */     if (this.message instanceof SMTPMessage)
/* 1660 */       sendPartial = ((SMTPMessage)this.message).getSendPartial(); 
/* 1661 */     if (!sendPartial) {
/* 1662 */       sendPartial = PropUtil.getBooleanSessionProperty(this.session, "mail." + this.name + ".sendpartial", false);
/*      */     }
/* 1664 */     if (sendPartial) {
/* 1665 */       this.logger.fine("sendPartial set");
/*      */     }
/* 1667 */     boolean dsn = false;
/* 1668 */     String notify = null;
/* 1669 */     if (supportsExtension("DSN")) {
/* 1670 */       if (this.message instanceof SMTPMessage)
/* 1671 */         notify = ((SMTPMessage)this.message).getDSNNotify(); 
/* 1672 */       if (notify == null) {
/* 1673 */         notify = this.session.getProperty("mail." + this.name + ".dsn.notify");
/*      */       }
/* 1675 */       if (notify != null) {
/* 1676 */         dsn = true;
/*      */       }
/*      */     } 
/*      */     int i;
/* 1680 */     for (i = 0; i < this.addresses.length; i++) {
/*      */       SMTPAddressFailedException sMTPAddressFailedException1;
/* 1682 */       sfex = null;
/* 1683 */       InternetAddress ia = (InternetAddress)this.addresses[i];
/* 1684 */       String cmd = "RCPT TO:" + normalizeAddress(ia.getAddress());
/* 1685 */       if (dsn) {
/* 1686 */         cmd = cmd + " NOTIFY=" + notify;
/*      */       }
/* 1688 */       sendCommand(cmd);
/*      */       
/* 1690 */       retCode = readServerResponse();
/* 1691 */       switch (retCode) { case 250:
/*      */         case 251:
/* 1693 */           valid.addElement(ia);
/* 1694 */           if (!this.reportSuccess) {
/*      */             break;
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1701 */           sfex = new SMTPAddressSucceededException(ia, cmd, retCode, this.lastServerResponse);
/*      */           
/* 1703 */           if (mex == null) {
/* 1704 */             mex = sfex; break;
/*      */           } 
/* 1706 */           mex.setNextException((Exception)sfex); break;
/*      */         case 501: case 503:
/*      */         case 550:
/*      */         case 551:
/*      */         case 553:
/* 1711 */           if (!sendPartial)
/* 1712 */             sendFailed = true; 
/* 1713 */           invalid.addElement(ia);
/*      */           
/* 1715 */           sMTPAddressFailedException1 = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
/*      */           
/* 1717 */           if (mex == null) {
/* 1718 */             sMTPAddressFailedException = sMTPAddressFailedException1; break;
/*      */           } 
/* 1720 */           sMTPAddressFailedException.setNextException((Exception)sMTPAddressFailedException1); break;
/*      */         case 450:
/*      */         case 451:
/*      */         case 452:
/*      */         case 552:
/* 1725 */           if (!sendPartial)
/* 1726 */             sendFailed = true; 
/* 1727 */           validUnsent.addElement(ia);
/*      */           
/* 1729 */           sMTPAddressFailedException1 = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
/*      */           
/* 1731 */           if (sMTPAddressFailedException == null) {
/* 1732 */             sMTPAddressFailedException = sMTPAddressFailedException1; break;
/*      */           } 
/* 1734 */           sMTPAddressFailedException.setNextException((Exception)sMTPAddressFailedException1);
/*      */           break;
/*      */ 
/*      */         
/*      */         default:
/* 1739 */           if (retCode >= 400 && retCode <= 499) {
/*      */             
/* 1741 */             validUnsent.addElement(ia);
/* 1742 */           } else if (retCode >= 500 && retCode <= 599) {
/*      */             
/* 1744 */             invalid.addElement(ia);
/*      */           } else {
/*      */             
/* 1747 */             if (this.logger.isLoggable(Level.FINE)) {
/* 1748 */               this.logger.fine("got response code " + retCode + ", with response: " + this.lastServerResponse);
/*      */             }
/* 1750 */             String _lsr = this.lastServerResponse;
/* 1751 */             int _lrc = this.lastReturnCode;
/* 1752 */             if (this.serverSocket != null)
/* 1753 */               issueCommand("RSET", -1); 
/* 1754 */             this.lastServerResponse = _lsr;
/* 1755 */             this.lastReturnCode = _lrc;
/* 1756 */             throw new SMTPAddressFailedException(ia, cmd, retCode, _lsr);
/*      */           } 
/*      */           
/* 1759 */           if (!sendPartial) {
/* 1760 */             sendFailed = true;
/*      */           }
/* 1762 */           sMTPAddressFailedException1 = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
/*      */           
/* 1764 */           if (sMTPAddressFailedException == null) {
/* 1765 */             sMTPAddressFailedException = sMTPAddressFailedException1; break;
/*      */           } 
/* 1767 */           sMTPAddressFailedException.setNextException((Exception)sMTPAddressFailedException1);
/*      */           break; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     } 
/* 1774 */     if (sendPartial && valid.size() == 0) {
/* 1775 */       sendFailed = true;
/*      */     }
/*      */     
/* 1778 */     if (sendFailed) {
/*      */       
/* 1780 */       this.invalidAddr = new Address[invalid.size()];
/* 1781 */       invalid.copyInto((Object[])this.invalidAddr);
/*      */ 
/*      */       
/* 1784 */       this.validUnsentAddr = new Address[valid.size() + validUnsent.size()];
/* 1785 */       i = 0; int j;
/* 1786 */       for (j = 0; j < valid.size(); j++)
/* 1787 */         this.validUnsentAddr[i++] = (Address)valid.elementAt(j); 
/* 1788 */       for (j = 0; j < validUnsent.size(); j++)
/* 1789 */         this.validUnsentAddr[i++] = (Address)validUnsent.elementAt(j); 
/* 1790 */     } else if (this.reportSuccess || (sendPartial && (invalid.size() > 0 || validUnsent.size() > 0))) {
/*      */ 
/*      */ 
/*      */       
/* 1794 */       this.sendPartiallyFailed = true;
/* 1795 */       this.exception = (MessagingException)sMTPAddressFailedException;
/*      */ 
/*      */       
/* 1798 */       this.invalidAddr = new Address[invalid.size()];
/* 1799 */       invalid.copyInto((Object[])this.invalidAddr);
/*      */ 
/*      */       
/* 1802 */       this.validUnsentAddr = new Address[validUnsent.size()];
/* 1803 */       validUnsent.copyInto((Object[])this.validUnsentAddr);
/*      */ 
/*      */       
/* 1806 */       this.validSentAddr = new Address[valid.size()];
/* 1807 */       valid.copyInto((Object[])this.validSentAddr);
/*      */     } else {
/* 1809 */       this.validSentAddr = this.addresses;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1814 */     if (this.logger.isLoggable(Level.FINE)) {
/* 1815 */       if (this.validSentAddr != null && this.validSentAddr.length > 0) {
/* 1816 */         this.logger.fine("Verified Addresses");
/* 1817 */         for (int l = 0; l < this.validSentAddr.length; l++) {
/* 1818 */           this.logger.fine("  " + this.validSentAddr[l]);
/*      */         }
/*      */       } 
/* 1821 */       if (this.validUnsentAddr != null && this.validUnsentAddr.length > 0) {
/* 1822 */         this.logger.fine("Valid Unsent Addresses");
/* 1823 */         for (int j = 0; j < this.validUnsentAddr.length; j++) {
/* 1824 */           this.logger.fine("  " + this.validUnsentAddr[j]);
/*      */         }
/*      */       } 
/* 1827 */       if (this.invalidAddr != null && this.invalidAddr.length > 0) {
/* 1828 */         this.logger.fine("Invalid Addresses");
/* 1829 */         for (int k = 0; k < this.invalidAddr.length; k++) {
/* 1830 */           this.logger.fine("  " + this.invalidAddr[k]);
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1836 */     if (sendFailed) {
/* 1837 */       this.logger.fine("Sending failed because of invalid destination addresses");
/*      */       
/* 1839 */       notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, (Message)this.message);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1844 */       String lsr = this.lastServerResponse;
/* 1845 */       int lrc = this.lastReturnCode;
/*      */       try {
/* 1847 */         if (this.serverSocket != null)
/* 1848 */           issueCommand("RSET", -1); 
/* 1849 */       } catch (MessagingException ex) {
/*      */         
/*      */         try {
/* 1852 */           close();
/* 1853 */         } catch (MessagingException ex2) {
/*      */           
/* 1855 */           this.logger.log(Level.FINE, "close failed", (Throwable)ex2);
/*      */         } 
/*      */       } finally {
/* 1858 */         this.lastServerResponse = lsr;
/* 1859 */         this.lastReturnCode = lrc;
/*      */       } 
/*      */       
/* 1862 */       throw new SendFailedException("Invalid Addresses", sMTPAddressFailedException, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
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
/*      */   protected OutputStream data() throws MessagingException {
/* 1875 */     assert Thread.holdsLock(this);
/* 1876 */     issueSendCommand("DATA", 354);
/* 1877 */     this.dataStream = new SMTPOutputStream(this.serverOutput);
/* 1878 */     return (OutputStream)this.dataStream;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void finishData() throws IOException, MessagingException {
/* 1887 */     assert Thread.holdsLock(this);
/* 1888 */     this.dataStream.ensureAtBOL();
/* 1889 */     issueSendCommand(".", 250);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void startTLS() throws MessagingException {
/* 1899 */     issueCommand("STARTTLS", 220);
/*      */     
/*      */     try {
/* 1902 */       this.serverSocket = SocketFetcher.startTLS(this.serverSocket, this.host, this.session.getProperties(), "mail." + this.name);
/*      */       
/* 1904 */       initStreams();
/* 1905 */     } catch (IOException ioex) {
/* 1906 */       closeConnection();
/* 1907 */       throw new MessagingException("Could not convert socket to TLS", ioex);
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
/*      */   private void openServer(String host, int port) throws MessagingException {
/* 1920 */     if (this.logger.isLoggable(Level.FINE)) {
/* 1921 */       this.logger.fine("trying to connect to host \"" + host + "\", port " + port + ", isSSL " + this.isSSL);
/*      */     }
/*      */     
/*      */     try {
/* 1925 */       Properties props = this.session.getProperties();
/*      */       
/* 1927 */       this.serverSocket = SocketFetcher.getSocket(host, port, props, "mail." + this.name, this.isSSL);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1932 */       port = this.serverSocket.getPort();
/*      */       
/* 1934 */       this.host = host;
/*      */       
/* 1936 */       initStreams();
/*      */       
/* 1938 */       int r = -1;
/* 1939 */       if ((r = readServerResponse()) != 220) {
/* 1940 */         this.serverSocket.close();
/* 1941 */         this.serverSocket = null;
/* 1942 */         this.serverOutput = null;
/* 1943 */         this.serverInput = null;
/* 1944 */         this.lineInputStream = null;
/* 1945 */         if (this.logger.isLoggable(Level.FINE)) {
/* 1946 */           this.logger.fine("could not connect to host \"" + host + "\", port: " + port + ", response: " + r + "\n");
/*      */         }
/*      */         
/* 1949 */         throw new MessagingException("Could not connect to SMTP host: " + host + ", port: " + port + ", response: " + r);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1954 */       if (this.logger.isLoggable(Level.FINE)) {
/* 1955 */         this.logger.fine("connected to host \"" + host + "\", port: " + port + "\n");
/*      */       }
/*      */     }
/* 1958 */     catch (UnknownHostException uhex) {
/* 1959 */       throw new MessagingException("Unknown SMTP host: " + host, uhex);
/* 1960 */     } catch (IOException ioe) {
/* 1961 */       throw new MessagingException("Could not connect to SMTP host: " + host + ", port: " + port, ioe);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void openServer() throws MessagingException {
/* 1971 */     int port = -1;
/* 1972 */     this.host = "UNKNOWN";
/*      */     try {
/* 1974 */       port = this.serverSocket.getPort();
/* 1975 */       this.host = this.serverSocket.getInetAddress().getHostName();
/* 1976 */       if (this.logger.isLoggable(Level.FINE)) {
/* 1977 */         this.logger.fine("starting protocol to host \"" + this.host + "\", port " + port);
/*      */       }
/*      */       
/* 1980 */       initStreams();
/*      */       
/* 1982 */       int r = -1;
/* 1983 */       if ((r = readServerResponse()) != 220) {
/* 1984 */         this.serverSocket.close();
/* 1985 */         this.serverSocket = null;
/* 1986 */         this.serverOutput = null;
/* 1987 */         this.serverInput = null;
/* 1988 */         this.lineInputStream = null;
/* 1989 */         if (this.logger.isLoggable(Level.FINE)) {
/* 1990 */           this.logger.fine("got bad greeting from host \"" + this.host + "\", port: " + port + ", response: " + r + "\n");
/*      */         }
/*      */         
/* 1993 */         throw new MessagingException("Got bad greeting from SMTP host: " + this.host + ", port: " + port + ", response: " + r);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1998 */       if (this.logger.isLoggable(Level.FINE)) {
/* 1999 */         this.logger.fine("protocol started to host \"" + this.host + "\", port: " + port + "\n");
/*      */       }
/*      */     }
/* 2002 */     catch (IOException ioe) {
/* 2003 */       throw new MessagingException("Could not start protocol to SMTP host: " + this.host + ", port: " + port, ioe);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initStreams() throws IOException {
/* 2011 */     boolean quote = PropUtil.getBooleanSessionProperty(this.session, "mail.debug.quote", false);
/*      */ 
/*      */     
/* 2014 */     this.traceInput = new TraceInputStream(this.serverSocket.getInputStream(), this.traceLogger);
/*      */     
/* 2016 */     this.traceInput.setQuote(quote);
/*      */     
/* 2018 */     this.traceOutput = new TraceOutputStream(this.serverSocket.getOutputStream(), this.traceLogger);
/*      */     
/* 2020 */     this.traceOutput.setQuote(quote);
/*      */     
/* 2022 */     this.serverOutput = new BufferedOutputStream((OutputStream)this.traceOutput);
/*      */     
/* 2024 */     this.serverInput = new BufferedInputStream((InputStream)this.traceInput);
/*      */     
/* 2026 */     this.lineInputStream = new LineInputStream(this.serverInput);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isTracing() {
/* 2033 */     return this.traceLogger.isLoggable(Level.FINEST);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void suspendTracing() {
/* 2041 */     if (this.traceLogger.isLoggable(Level.FINEST)) {
/* 2042 */       this.traceInput.setTrace(false);
/* 2043 */       this.traceOutput.setTrace(false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void resumeTracing() {
/* 2051 */     if (this.traceLogger.isLoggable(Level.FINEST)) {
/* 2052 */       this.traceInput.setTrace(true);
/* 2053 */       this.traceOutput.setTrace(true);
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
/*      */   public synchronized void issueCommand(String cmd, int expect) throws MessagingException {
/* 2068 */     sendCommand(cmd);
/*      */ 
/*      */ 
/*      */     
/* 2072 */     int resp = readServerResponse();
/* 2073 */     if (expect != -1 && resp != expect) {
/* 2074 */       throw new MessagingException(this.lastServerResponse);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void issueSendCommand(String cmd, int expect) throws MessagingException {
/* 2082 */     sendCommand(cmd);
/*      */ 
/*      */     
/*      */     int ret;
/*      */     
/* 2087 */     if ((ret = readServerResponse()) != expect) {
/*      */ 
/*      */       
/* 2090 */       int vsl = (this.validSentAddr == null) ? 0 : this.validSentAddr.length;
/* 2091 */       int vul = (this.validUnsentAddr == null) ? 0 : this.validUnsentAddr.length;
/* 2092 */       Address[] valid = new Address[vsl + vul];
/* 2093 */       if (vsl > 0)
/* 2094 */         System.arraycopy(this.validSentAddr, 0, valid, 0, vsl); 
/* 2095 */       if (vul > 0)
/* 2096 */         System.arraycopy(this.validUnsentAddr, 0, valid, vsl, vul); 
/* 2097 */       this.validSentAddr = null;
/* 2098 */       this.validUnsentAddr = valid;
/* 2099 */       if (this.logger.isLoggable(Level.FINE)) {
/* 2100 */         this.logger.fine("got response code " + ret + ", with response: " + this.lastServerResponse);
/*      */       }
/* 2102 */       String _lsr = this.lastServerResponse;
/* 2103 */       int _lrc = this.lastReturnCode;
/* 2104 */       if (this.serverSocket != null)
/* 2105 */         issueCommand("RSET", -1); 
/* 2106 */       this.lastServerResponse = _lsr;
/* 2107 */       this.lastReturnCode = _lrc;
/* 2108 */       throw new SMTPSendFailedException(cmd, ret, this.lastServerResponse, this.exception, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
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
/*      */   public synchronized int simpleCommand(String cmd) throws MessagingException {
/* 2121 */     sendCommand(cmd);
/* 2122 */     return readServerResponse();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int simpleCommand(byte[] cmd) throws MessagingException {
/* 2132 */     assert Thread.holdsLock(this);
/* 2133 */     sendCommand(cmd);
/* 2134 */     return readServerResponse();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void sendCommand(String cmd) throws MessagingException {
/* 2144 */     sendCommand(ASCIIUtility.getBytes(cmd));
/*      */   }
/*      */   
/*      */   private void sendCommand(byte[] cmdBytes) throws MessagingException {
/* 2148 */     assert Thread.holdsLock(this);
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 2153 */       this.serverOutput.write(cmdBytes);
/* 2154 */       this.serverOutput.write(CRLF);
/* 2155 */       this.serverOutput.flush();
/* 2156 */     } catch (IOException ex) {
/* 2157 */       throw new MessagingException("Can't send command to SMTP host", ex);
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
/*      */   protected int readServerResponse() throws MessagingException {
/* 2171 */     assert Thread.holdsLock(this);
/* 2172 */     String serverResponse = "";
/* 2173 */     int returnCode = 0;
/* 2174 */     StringBuffer buf = new StringBuffer(100);
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 2179 */       String line = null;
/*      */       
/*      */       do {
/* 2182 */         line = this.lineInputStream.readLine();
/* 2183 */         if (line == null) {
/* 2184 */           serverResponse = buf.toString();
/* 2185 */           if (serverResponse.length() == 0)
/* 2186 */             serverResponse = "[EOF]"; 
/* 2187 */           this.lastServerResponse = serverResponse;
/* 2188 */           this.lastReturnCode = -1;
/* 2189 */           this.logger.log(Level.FINE, "EOF: {0}", serverResponse);
/* 2190 */           return -1;
/*      */         } 
/* 2192 */         buf.append(line);
/* 2193 */         buf.append("\n");
/* 2194 */       } while (isNotLastLine(line));
/*      */       
/* 2196 */       serverResponse = buf.toString();
/* 2197 */     } catch (IOException ioex) {
/* 2198 */       this.logger.log(Level.FINE, "exception reading response", ioex);
/*      */       
/* 2200 */       this.lastServerResponse = "";
/* 2201 */       this.lastReturnCode = 0;
/* 2202 */       throw new MessagingException("Exception reading response", ioex);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2211 */     if (serverResponse.length() >= 3) {
/*      */       try {
/* 2213 */         returnCode = Integer.parseInt(serverResponse.substring(0, 3));
/* 2214 */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 2216 */           close();
/* 2217 */         } catch (MessagingException mex) {
/*      */           
/* 2219 */           this.logger.log(Level.FINE, "close failed", (Throwable)mex);
/*      */         } 
/* 2221 */         returnCode = -1;
/* 2222 */       } catch (StringIndexOutOfBoundsException ex) {
/*      */         try {
/* 2224 */           close();
/* 2225 */         } catch (MessagingException mex) {
/*      */           
/* 2227 */           this.logger.log(Level.FINE, "close failed", (Throwable)mex);
/*      */         } 
/* 2229 */         returnCode = -1;
/*      */       } 
/*      */     } else {
/* 2232 */       returnCode = -1;
/*      */     } 
/* 2234 */     if (returnCode == -1) {
/* 2235 */       this.logger.log(Level.FINE, "bad server response: {0}", serverResponse);
/*      */     }
/* 2237 */     this.lastServerResponse = serverResponse;
/* 2238 */     this.lastReturnCode = returnCode;
/* 2239 */     return returnCode;
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
/*      */   protected void checkConnected() {
/* 2251 */     if (!super.isConnected()) {
/* 2252 */       throw new IllegalStateException("Not connected");
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean isNotLastLine(String line) {
/* 2257 */     return (line != null && line.length() >= 4 && line.charAt(3) == '-');
/*      */   }
/*      */ 
/*      */   
/*      */   private String normalizeAddress(String addr) {
/* 2262 */     if (!addr.startsWith("<") && !addr.endsWith(">")) {
/* 2263 */       return "<" + addr + ">";
/*      */     }
/* 2265 */     return addr;
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
/*      */   public boolean supportsExtension(String ext) {
/* 2281 */     return (this.extMap != null && this.extMap.get(ext.toUpperCase(Locale.ENGLISH)) != null);
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
/*      */   public String getExtensionParameter(String ext) {
/* 2295 */     return (this.extMap == null) ? null : (String)this.extMap.get(ext.toUpperCase(Locale.ENGLISH));
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
/*      */   protected boolean supportsAuthentication(String auth) {
/* 2310 */     assert Thread.holdsLock(this);
/* 2311 */     if (this.extMap == null)
/* 2312 */       return false; 
/* 2313 */     String a = (String)this.extMap.get("AUTH");
/* 2314 */     if (a == null)
/* 2315 */       return false; 
/* 2316 */     StringTokenizer st = new StringTokenizer(a);
/* 2317 */     while (st.hasMoreTokens()) {
/* 2318 */       String tok = st.nextToken();
/* 2319 */       if (tok.equalsIgnoreCase(auth)) {
/* 2320 */         return true;
/*      */       }
/*      */     } 
/* 2323 */     if (auth.equalsIgnoreCase("LOGIN") && supportsExtension("AUTH=LOGIN")) {
/* 2324 */       this.logger.fine("use AUTH=LOGIN hack");
/* 2325 */       return true;
/*      */     } 
/* 2327 */     return false;
/*      */   }
/*      */   
/* 2330 */   private static char[] hexchar = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
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
/*      */   static final boolean $assertionsDisabled;
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
/*      */   protected static String xtext(String s) {
/* 2354 */     StringBuffer sb = null;
/* 2355 */     for (int i = 0; i < s.length(); i++) {
/* 2356 */       char c = s.charAt(i);
/* 2357 */       if (c >= '') {
/* 2358 */         throw new IllegalArgumentException("Non-ASCII character in SMTP submitter: " + s);
/*      */       }
/* 2360 */       if (c < '!' || c > '~' || c == '+' || c == '=') {
/* 2361 */         if (sb == null) {
/* 2362 */           sb = new StringBuffer(s.length() + 4);
/* 2363 */           sb.append(s.substring(0, i));
/*      */         } 
/* 2365 */         sb.append('+');
/* 2366 */         sb.append(hexchar[(c & 0xF0) >> 4]);
/* 2367 */         sb.append(hexchar[c & 0xF]);
/*      */       }
/* 2369 */       else if (sb != null) {
/* 2370 */         sb.append(c);
/*      */       } 
/*      */     } 
/* 2373 */     return (sb != null) ? sb.toString() : s;
/*      */   }
/*      */   
/*      */   private void sendMessageStart(String subject) {}
/*      */   
/*      */   private void sendMessageEnd() {}
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\smtp\SMTPTransport.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */