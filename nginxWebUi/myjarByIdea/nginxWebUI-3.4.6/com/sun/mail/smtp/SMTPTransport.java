package com.sun.mail.smtp;

import com.sun.mail.auth.Ntlm;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.MailLogger;
import com.sun.mail.util.PropUtil;
import com.sun.mail.util.SocketFetcher;
import com.sun.mail.util.TraceInputStream;
import com.sun.mail.util.TraceOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.ParseException;
import javax.net.ssl.SSLSocket;

public class SMTPTransport extends Transport {
   private String name;
   private int defaultPort;
   private boolean isSSL;
   private String host;
   private MimeMessage message;
   private Address[] addresses;
   private Address[] validSentAddr;
   private Address[] validUnsentAddr;
   private Address[] invalidAddr;
   private boolean sendPartiallyFailed;
   private MessagingException exception;
   private SMTPOutputStream dataStream;
   private Hashtable extMap;
   private Map authenticators;
   private String defaultAuthenticationMechanisms;
   private boolean quitWait;
   private String saslRealm;
   private String authorizationID;
   private boolean enableSASL;
   private String[] saslMechanisms;
   private String ntlmDomain;
   private boolean reportSuccess;
   private boolean useStartTLS;
   private boolean requireStartTLS;
   private boolean useRset;
   private boolean noopStrict;
   private MailLogger logger;
   private MailLogger traceLogger;
   private String localHostName;
   private String lastServerResponse;
   private int lastReturnCode;
   private boolean notificationDone;
   private SaslAuthenticator saslAuthenticator;
   private boolean noauthdebug;
   private static final String[] ignoreList;
   private static final byte[] CRLF;
   private static final String UNKNOWN = "UNKNOWN";
   private static final String[] UNKNOWN_SA;
   private BufferedInputStream serverInput;
   private LineInputStream lineInputStream;
   private OutputStream serverOutput;
   private Socket serverSocket;
   private TraceInputStream traceInput;
   private TraceOutputStream traceOutput;
   private static char[] hexchar;
   // $FF: synthetic field
   static final boolean $assertionsDisabled;

   public SMTPTransport(Session session, URLName urlname) {
      this(session, urlname, "smtp", false);
   }

   protected SMTPTransport(Session session, URLName urlname, String name, boolean isSSL) {
      super(session, urlname);
      this.name = "smtp";
      this.defaultPort = 25;
      this.isSSL = false;
      this.sendPartiallyFailed = false;
      this.authenticators = new HashMap();
      this.quitWait = false;
      this.saslRealm = "UNKNOWN";
      this.authorizationID = "UNKNOWN";
      this.enableSASL = false;
      this.saslMechanisms = UNKNOWN_SA;
      this.ntlmDomain = "UNKNOWN";
      this.noopStrict = true;
      this.noauthdebug = true;
      this.logger = new MailLogger(this.getClass(), "DEBUG SMTP", session);
      this.traceLogger = this.logger.getSubLogger("protocol", (String)null);
      this.noauthdebug = !PropUtil.getBooleanSessionProperty(session, "mail.debug.auth", false);
      if (urlname != null) {
         name = urlname.getProtocol();
      }

      this.name = name;
      if (!isSSL) {
         isSSL = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".ssl.enable", false);
      }

      if (isSSL) {
         this.defaultPort = 465;
      } else {
         this.defaultPort = 25;
      }

      this.isSSL = isSSL;
      this.quitWait = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".quitwait", true);
      this.reportSuccess = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".reportsuccess", false);
      this.useStartTLS = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".starttls.enable", false);
      this.requireStartTLS = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".starttls.required", false);
      this.useRset = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".userset", false);
      this.noopStrict = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".noop.strict", true);
      this.enableSASL = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".sasl.enable", false);
      if (this.enableSASL) {
         this.logger.config("enable SASL");
      }

      Authenticator[] a = new Authenticator[]{new LoginAuthenticator(), new PlainAuthenticator(), new DigestMD5Authenticator(), new NtlmAuthenticator()};
      StringBuffer sb = new StringBuffer();

      for(int i = 0; i < a.length; ++i) {
         this.authenticators.put(a[i].getMechanism(), a[i]);
         sb.append(a[i].getMechanism()).append(' ');
      }

      this.defaultAuthenticationMechanisms = sb.toString();
   }

   public synchronized String getLocalHost() {
      if (this.localHostName == null || this.localHostName.length() <= 0) {
         this.localHostName = this.session.getProperty("mail." + this.name + ".localhost");
      }

      if (this.localHostName == null || this.localHostName.length() <= 0) {
         this.localHostName = this.session.getProperty("mail." + this.name + ".localaddress");
      }

      InetAddress localHost;
      try {
         if (this.localHostName == null || this.localHostName.length() <= 0) {
            localHost = InetAddress.getLocalHost();
            this.localHostName = localHost.getCanonicalHostName();
            if (this.localHostName == null) {
               this.localHostName = "[" + localHost.getHostAddress() + "]";
            }
         }
      } catch (UnknownHostException var2) {
      }

      if ((this.localHostName == null || this.localHostName.length() <= 0) && this.serverSocket != null && this.serverSocket.isBound()) {
         localHost = this.serverSocket.getLocalAddress();
         this.localHostName = localHost.getCanonicalHostName();
         if (this.localHostName == null) {
            this.localHostName = "[" + localHost.getHostAddress() + "]";
         }
      }

      return this.localHostName;
   }

   public synchronized void setLocalHost(String localhost) {
      this.localHostName = localhost;
   }

   public synchronized void connect(Socket socket) throws MessagingException {
      this.serverSocket = socket;
      super.connect();
   }

   public synchronized String getAuthorizationId() {
      if (this.authorizationID == "UNKNOWN") {
         this.authorizationID = this.session.getProperty("mail." + this.name + ".sasl.authorizationid");
      }

      return this.authorizationID;
   }

   public synchronized void setAuthorizationID(String authzid) {
      this.authorizationID = authzid;
   }

   public synchronized boolean getSASLEnabled() {
      return this.enableSASL;
   }

   public synchronized void setSASLEnabled(boolean enableSASL) {
      this.enableSASL = enableSASL;
   }

   public synchronized String getSASLRealm() {
      if (this.saslRealm == "UNKNOWN") {
         this.saslRealm = this.session.getProperty("mail." + this.name + ".sasl.realm");
         if (this.saslRealm == null) {
            this.saslRealm = this.session.getProperty("mail." + this.name + ".saslrealm");
         }
      }

      return this.saslRealm;
   }

   public synchronized void setSASLRealm(String saslRealm) {
      this.saslRealm = saslRealm;
   }

   public synchronized String[] getSASLMechanisms() {
      if (this.saslMechanisms == UNKNOWN_SA) {
         List v = new ArrayList(5);
         String s = this.session.getProperty("mail." + this.name + ".sasl.mechanisms");
         if (s != null && s.length() > 0) {
            if (this.logger.isLoggable(Level.FINE)) {
               this.logger.fine("SASL mechanisms allowed: " + s);
            }

            StringTokenizer st = new StringTokenizer(s, " ,");

            while(st.hasMoreTokens()) {
               String m = st.nextToken();
               if (m.length() > 0) {
                  v.add(m);
               }
            }
         }

         this.saslMechanisms = new String[v.size()];
         v.toArray(this.saslMechanisms);
      }

      return this.saslMechanisms == null ? null : (String[])((String[])this.saslMechanisms.clone());
   }

   public synchronized void setSASLMechanisms(String[] mechanisms) {
      if (mechanisms != null) {
         mechanisms = (String[])((String[])mechanisms.clone());
      }

      this.saslMechanisms = mechanisms;
   }

   public synchronized String getNTLMDomain() {
      if (this.ntlmDomain == "UNKNOWN") {
         this.ntlmDomain = this.session.getProperty("mail." + this.name + ".auth.ntlm.domain");
      }

      return this.ntlmDomain;
   }

   public synchronized void setNTLMDomain(String ntlmDomain) {
      this.ntlmDomain = ntlmDomain;
   }

   public synchronized boolean getReportSuccess() {
      return this.reportSuccess;
   }

   public synchronized void setReportSuccess(boolean reportSuccess) {
      this.reportSuccess = reportSuccess;
   }

   public synchronized boolean getStartTLS() {
      return this.useStartTLS;
   }

   public synchronized void setStartTLS(boolean useStartTLS) {
      this.useStartTLS = useStartTLS;
   }

   public synchronized boolean getRequireStartTLS() {
      return this.requireStartTLS;
   }

   public synchronized void setRequireStartTLS(boolean requireStartTLS) {
      this.requireStartTLS = requireStartTLS;
   }

   public boolean isSSL() {
      return this.serverSocket instanceof SSLSocket;
   }

   public synchronized boolean getUseRset() {
      return this.useRset;
   }

   public synchronized void setUseRset(boolean useRset) {
      this.useRset = useRset;
   }

   public synchronized boolean getNoopStrict() {
      return this.noopStrict;
   }

   public synchronized void setNoopStrict(boolean noopStrict) {
      this.noopStrict = noopStrict;
   }

   public synchronized String getLastServerResponse() {
      return this.lastServerResponse;
   }

   public synchronized int getLastReturnCode() {
      return this.lastReturnCode;
   }

   protected synchronized boolean protocolConnect(String host, int port, String user, String passwd) throws MessagingException {
      boolean useEhlo = PropUtil.getBooleanSessionProperty(this.session, "mail." + this.name + ".ehlo", true);
      boolean useAuth = PropUtil.getBooleanSessionProperty(this.session, "mail." + this.name + ".auth", false);
      if (this.logger.isLoggable(Level.FINE)) {
         this.logger.fine("useEhlo " + useEhlo + ", useAuth " + useAuth);
      }

      if (useAuth && (user == null || passwd == null)) {
         return false;
      } else {
         if (port == -1) {
            port = PropUtil.getIntSessionProperty(this.session, "mail." + this.name + ".port", -1);
         }

         if (port == -1) {
            port = this.defaultPort;
         }

         if (host == null || host.length() == 0) {
            host = "localhost";
         }

         boolean connected = false;

         boolean var9;
         try {
            if (this.serverSocket != null) {
               this.openServer();
            } else {
               this.openServer(host, port);
            }

            boolean succeed = false;
            if (useEhlo) {
               succeed = this.ehlo(this.getLocalHost());
            }

            if (!succeed) {
               this.helo(this.getLocalHost());
            }

            if (this.useStartTLS || this.requireStartTLS) {
               if (this.serverSocket instanceof SSLSocket) {
                  this.logger.fine("STARTTLS requested but already using SSL");
               } else if (this.supportsExtension("STARTTLS")) {
                  this.startTLS();
                  this.ehlo(this.getLocalHost());
               } else if (this.requireStartTLS) {
                  this.logger.fine("STARTTLS required but not supported");
                  throw new MessagingException("STARTTLS is required but host does not support STARTTLS");
               }
            }

            if (!useAuth && (user == null || passwd == null) || !this.supportsExtension("AUTH") && !this.supportsExtension("AUTH=LOGIN")) {
               connected = true;
               var9 = true;
               return var9;
            }

            connected = this.authenticate(user, passwd);
            var9 = connected;
         } finally {
            if (!connected) {
               try {
                  this.closeConnection();
               } catch (MessagingException var17) {
               }
            }

         }

         return var9;
      }
   }

   private boolean authenticate(String user, String passwd) throws MessagingException {
      String mechs = this.session.getProperty("mail." + this.name + ".auth.mechanisms");
      if (mechs == null) {
         mechs = this.defaultAuthenticationMechanisms;
      }

      String authzid = this.getAuthorizationId();
      if (authzid == null) {
         authzid = user;
      }

      if (this.enableSASL) {
         this.logger.fine("Authenticate with SASL");
         if (this.sasllogin(this.getSASLMechanisms(), this.getSASLRealm(), authzid, user, passwd)) {
            return true;
         }

         this.logger.fine("SASL authentication failed");
      }

      if (this.logger.isLoggable(Level.FINE)) {
         this.logger.fine("Attempt to authenticate using mechanisms: " + mechs);
      }

      StringTokenizer st = new StringTokenizer(mechs);

      while(st.hasMoreTokens()) {
         String m = st.nextToken();
         String dprop = "mail." + this.name + ".auth." + m.toLowerCase(Locale.ENGLISH) + ".disable";
         boolean disabled = PropUtil.getBooleanSessionProperty(this.session, dprop, false);
         if (disabled) {
            if (this.logger.isLoggable(Level.FINE)) {
               this.logger.fine("mechanism " + m + " disabled by property: " + dprop);
            }
         } else {
            m = m.toUpperCase(Locale.ENGLISH);
            if (!this.supportsAuthentication(m)) {
               this.logger.log(Level.FINE, "mechanism {0} not supported by server", (Object)m);
            } else {
               Authenticator a = (Authenticator)this.authenticators.get(m);
               if (a != null) {
                  return a.authenticate(this.host, authzid, user, passwd);
               }

               this.logger.log(Level.FINE, "no authenticator for mechanism {0}", (Object)m);
            }
         }
      }

      throw new AuthenticationFailedException("No authentication mechansims supported by both server and client");
   }

   public boolean sasllogin(String[] allowed, String realm, String authzid, String u, String p) throws MessagingException {
      if (this.saslAuthenticator == null) {
         try {
            Class sac = Class.forName("com.sun.mail.smtp.SMTPSaslAuthenticator");
            Constructor c = sac.getConstructor(SMTPTransport.class, String.class, Properties.class, MailLogger.class, String.class);
            this.saslAuthenticator = (SaslAuthenticator)c.newInstance(this, this.name, this.session.getProperties(), this.logger, this.host);
         } catch (Exception var13) {
            this.logger.log(Level.FINE, "Can't load SASL authenticator", (Throwable)var13);
            return false;
         }
      }

      ArrayList v;
      if (allowed != null && allowed.length > 0) {
         v = new ArrayList(allowed.length);

         for(int i = 0; i < allowed.length; ++i) {
            if (this.supportsAuthentication(allowed[i])) {
               v.add(allowed[i]);
            }
         }
      } else {
         v = new ArrayList();
         if (this.extMap != null) {
            String a = (String)this.extMap.get("AUTH");
            if (a != null) {
               StringTokenizer st = new StringTokenizer(a);

               while(st.hasMoreTokens()) {
                  v.add(st.nextToken());
               }
            }
         }
      }

      String[] mechs = (String[])((String[])v.toArray(new String[v.size()]));

      boolean var19;
      try {
         if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("SASL AUTH command trace suppressed");
            this.suspendTracing();
         }

         var19 = this.saslAuthenticator.authenticate(mechs, realm, authzid, u, p);
      } finally {
         this.resumeTracing();
      }

      return var19;
   }

   public synchronized void sendMessage(Message message, Address[] addresses) throws MessagingException, SendFailedException {
      this.sendMessageStart(message != null ? message.getSubject() : "");
      this.checkConnected();
      if (!(message instanceof MimeMessage)) {
         this.logger.fine("Can only send RFC822 msgs");
         throw new MessagingException("SMTP can only send RFC822 messages");
      } else {
         for(int i = 0; i < addresses.length; ++i) {
            if (!(addresses[i] instanceof InternetAddress)) {
               throw new MessagingException(addresses[i] + " is not an InternetAddress");
            }
         }

         if (addresses.length == 0) {
            throw new SendFailedException("No recipient addresses");
         } else {
            this.message = (MimeMessage)message;
            this.addresses = addresses;
            this.validUnsentAddr = addresses;
            this.expandGroups();
            boolean use8bit = false;
            if (message instanceof SMTPMessage) {
               use8bit = ((SMTPMessage)message).getAllow8bitMIME();
            }

            if (!use8bit) {
               use8bit = PropUtil.getBooleanSessionProperty(this.session, "mail." + this.name + ".allow8bitmime", false);
            }

            if (this.logger.isLoggable(Level.FINE)) {
               this.logger.fine("use8bit " + use8bit);
            }

            if (use8bit && this.supportsExtension("8BITMIME") && this.convertTo8Bit(this.message)) {
               try {
                  this.message.saveChanges();
               } catch (MessagingException var16) {
               }
            }

            try {
               this.mailFrom();
               this.rcptTo();
               this.message.writeTo(this.data(), ignoreList);
               this.finishData();
               if (this.sendPartiallyFailed) {
                  this.logger.fine("Sending partially failed because of invalid destination addresses");
                  this.notifyTransportListeners(3, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                  throw new SMTPSendFailedException(".", this.lastReturnCode, this.lastServerResponse, this.exception, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
               }

               this.notifyTransportListeners(1, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
            } catch (MessagingException var17) {
               this.logger.log(Level.FINE, "MessagingException while sending", (Throwable)var17);
               if (var17.getNextException() instanceof IOException) {
                  this.logger.fine("nested IOException, closing");

                  try {
                     this.closeConnection();
                  } catch (MessagingException var14) {
                  }
               }

               this.addressesFailed();
               this.notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
               throw var17;
            } catch (IOException var18) {
               this.logger.log(Level.FINE, "IOException while sending, closing", (Throwable)var18);

               try {
                  this.closeConnection();
               } catch (MessagingException var15) {
               }

               this.addressesFailed();
               this.notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
               throw new MessagingException("IOException while sending message", var18);
            } finally {
               this.validSentAddr = this.validUnsentAddr = this.invalidAddr = null;
               this.addresses = null;
               this.message = null;
               this.exception = null;
               this.sendPartiallyFailed = false;
               this.notificationDone = false;
            }

            this.sendMessageEnd();
         }
      }
   }

   private void addressesFailed() {
      if (this.validSentAddr != null) {
         if (this.validUnsentAddr != null) {
            Address[] newa = new Address[this.validSentAddr.length + this.validUnsentAddr.length];
            System.arraycopy(this.validSentAddr, 0, newa, 0, this.validSentAddr.length);
            System.arraycopy(this.validUnsentAddr, 0, newa, this.validSentAddr.length, this.validUnsentAddr.length);
            this.validSentAddr = null;
            this.validUnsentAddr = newa;
         } else {
            this.validUnsentAddr = this.validSentAddr;
            this.validSentAddr = null;
         }
      }

   }

   public synchronized void close() throws MessagingException {
      if (super.isConnected()) {
         try {
            if (this.serverSocket != null) {
               this.sendCommand("QUIT");
               if (this.quitWait) {
                  int resp = this.readServerResponse();
                  if (resp != 221 && resp != -1 && this.logger.isLoggable(Level.FINE)) {
                     this.logger.fine("QUIT failed with " + resp);
                  }
               }
            }
         } finally {
            this.closeConnection();
         }

      }
   }

   private void closeConnection() throws MessagingException {
      try {
         if (this.serverSocket != null) {
            this.serverSocket.close();
         }
      } catch (IOException var6) {
         throw new MessagingException("Server Close Failed", var6);
      } finally {
         this.serverSocket = null;
         this.serverOutput = null;
         this.serverInput = null;
         this.lineInputStream = null;
         if (super.isConnected()) {
            super.close();
         }

      }

   }

   public synchronized boolean isConnected() {
      if (!super.isConnected()) {
         return false;
      } else {
         try {
            if (this.useRset) {
               this.sendCommand("RSET");
            } else {
               this.sendCommand("NOOP");
            }

            int resp = this.readServerResponse();
            if (resp >= 0) {
               label53: {
                  if (this.noopStrict) {
                     if (resp != 250) {
                        break label53;
                     }
                  } else if (resp == 421) {
                     break label53;
                  }

                  return true;
               }
            }

            try {
               this.closeConnection();
            } catch (MessagingException var4) {
            }

            return false;
         } catch (Exception var5) {
            try {
               this.closeConnection();
            } catch (MessagingException var3) {
            }

            return false;
         }
      }
   }

   protected void notifyTransportListeners(int type, Address[] validSent, Address[] validUnsent, Address[] invalid, Message msg) {
      if (!this.notificationDone) {
         super.notifyTransportListeners(type, validSent, validUnsent, invalid, msg);
         this.notificationDone = true;
      }

   }

   private void expandGroups() {
      Vector groups = null;

      for(int i = 0; i < this.addresses.length; ++i) {
         InternetAddress a = (InternetAddress)this.addresses[i];
         if (!a.isGroup()) {
            if (groups != null) {
               groups.addElement(a);
            }
         } else {
            if (groups == null) {
               groups = new Vector();

               for(int k = 0; k < i; ++k) {
                  groups.addElement(this.addresses[k]);
               }
            }

            try {
               InternetAddress[] ia = a.getGroup(true);
               if (ia != null) {
                  for(int j = 0; j < ia.length; ++j) {
                     groups.addElement(ia[j]);
                  }
               } else {
                  groups.addElement(a);
               }
            } catch (ParseException var6) {
               groups.addElement(a);
            }
         }
      }

      if (groups != null) {
         InternetAddress[] newa = new InternetAddress[groups.size()];
         groups.copyInto(newa);
         this.addresses = newa;
      }

   }

   private boolean convertTo8Bit(MimePart part) {
      boolean changed = false;

      try {
         if (part.isMimeType("text/*")) {
            String enc = part.getEncoding();
            if (enc != null && (enc.equalsIgnoreCase("quoted-printable") || enc.equalsIgnoreCase("base64"))) {
               InputStream is = null;

               try {
                  is = part.getInputStream();
                  if (this.is8Bit(is)) {
                     part.setContent(part.getContent(), part.getContentType());
                     part.setHeader("Content-Transfer-Encoding", "8bit");
                     changed = true;
                  }
               } finally {
                  if (is != null) {
                     try {
                        is.close();
                     } catch (IOException var13) {
                     }
                  }

               }
            }
         } else if (part.isMimeType("multipart/*")) {
            MimeMultipart mp = (MimeMultipart)part.getContent();
            int count = mp.getCount();

            for(int i = 0; i < count; ++i) {
               if (this.convertTo8Bit((MimePart)mp.getBodyPart(i))) {
                  changed = true;
               }
            }
         }
      } catch (IOException var15) {
      } catch (MessagingException var16) {
      }

      return changed;
   }

   private boolean is8Bit(InputStream is) {
      int linelen = 0;
      boolean need8bit = false;

      int b;
      try {
         while((b = is.read()) >= 0) {
            b &= 255;
            if (b != 13 && b != 10) {
               if (b == 0) {
                  return false;
               }

               ++linelen;
               if (linelen > 998) {
                  return false;
               }
            } else {
               linelen = 0;
            }

            if (b > 127) {
               need8bit = true;
            }
         }
      } catch (IOException var6) {
         return false;
      }

      if (need8bit) {
         this.logger.fine("found an 8bit part");
      }

      return need8bit;
   }

   protected void finalize() throws Throwable {
      super.finalize();

      try {
         this.closeConnection();
      } catch (MessagingException var2) {
      }

   }

   protected void helo(String domain) throws MessagingException {
      if (domain != null) {
         this.issueCommand("HELO " + domain, 250);
      } else {
         this.issueCommand("HELO", 250);
      }

   }

   protected boolean ehlo(String domain) throws MessagingException {
      String cmd;
      if (domain != null) {
         cmd = "EHLO " + domain;
      } else {
         cmd = "EHLO";
      }

      this.sendCommand(cmd);
      int resp = this.readServerResponse();
      if (resp == 250) {
         BufferedReader rd = new BufferedReader(new StringReader(this.lastServerResponse));
         this.extMap = new Hashtable();

         try {
            boolean first = true;

            String line;
            while((line = rd.readLine()) != null) {
               if (first) {
                  first = false;
               } else if (line.length() >= 5) {
                  line = line.substring(4);
                  int i = line.indexOf(32);
                  String arg = "";
                  if (i > 0) {
                     arg = line.substring(i + 1);
                     line = line.substring(0, i);
                  }

                  if (this.logger.isLoggable(Level.FINE)) {
                     this.logger.fine("Found extension \"" + line + "\", arg \"" + arg + "\"");
                  }

                  this.extMap.put(line.toUpperCase(Locale.ENGLISH), arg);
               }
            }
         } catch (IOException var9) {
         }
      }

      return resp == 250;
   }

   protected void mailFrom() throws MessagingException {
      String from = null;
      if (this.message instanceof SMTPMessage) {
         from = ((SMTPMessage)this.message).getEnvelopeFrom();
      }

      if (from == null || from.length() <= 0) {
         from = this.session.getProperty("mail." + this.name + ".from");
      }

      if (from == null || from.length() <= 0) {
         Address[] fa;
         Object me;
         if (this.message != null && (fa = this.message.getFrom()) != null && fa.length > 0) {
            me = fa[0];
         } else {
            me = InternetAddress.getLocalAddress(this.session);
         }

         if (me == null) {
            throw new MessagingException("can't determine local email address");
         }

         from = ((InternetAddress)me).getAddress();
      }

      String cmd = "MAIL FROM:" + this.normalizeAddress(from);
      String submitter;
      if (this.supportsExtension("DSN")) {
         submitter = null;
         if (this.message instanceof SMTPMessage) {
            submitter = ((SMTPMessage)this.message).getDSNRet();
         }

         if (submitter == null) {
            submitter = this.session.getProperty("mail." + this.name + ".dsn.ret");
         }

         if (submitter != null) {
            cmd = cmd + " RET=" + submitter;
         }
      }

      if (this.supportsExtension("AUTH")) {
         submitter = null;
         if (this.message instanceof SMTPMessage) {
            submitter = ((SMTPMessage)this.message).getSubmitter();
         }

         if (submitter == null) {
            submitter = this.session.getProperty("mail." + this.name + ".submitter");
         }

         if (submitter != null) {
            try {
               String s = xtext(submitter);
               cmd = cmd + " AUTH=" + s;
            } catch (IllegalArgumentException var9) {
               if (this.logger.isLoggable(Level.FINE)) {
                  this.logger.log(Level.FINE, "ignoring invalid submitter: " + submitter, (Throwable)var9);
               }
            }
         }
      }

      submitter = null;
      if (this.message instanceof SMTPMessage) {
         submitter = ((SMTPMessage)this.message).getMailExtension();
      }

      if (submitter == null) {
         submitter = this.session.getProperty("mail." + this.name + ".mailextension");
      }

      if (submitter != null && submitter.length() > 0) {
         cmd = cmd + " " + submitter;
      }

      try {
         this.issueSendCommand(cmd, 250);
      } catch (SMTPSendFailedException var8) {
         SMTPSendFailedException ex = var8;
         int retCode = var8.getReturnCode();
         switch (retCode) {
            case 501:
            case 503:
            case 550:
            case 551:
            case 553:
               try {
                  ex.setNextException(new SMTPSenderFailedException(new InternetAddress(from), cmd, retCode, ex.getMessage()));
               } catch (AddressException var7) {
               }
         }

         throw var8;
      }
   }

   protected void rcptTo() throws MessagingException {
      Vector valid = new Vector();
      Vector validUnsent = new Vector();
      Vector invalid = new Vector();
      int retCode = true;
      MessagingException mex = null;
      boolean sendFailed = false;
      MessagingException sfex = null;
      this.validSentAddr = this.validUnsentAddr = this.invalidAddr = null;
      boolean sendPartial = false;
      if (this.message instanceof SMTPMessage) {
         sendPartial = ((SMTPMessage)this.message).getSendPartial();
      }

      if (!sendPartial) {
         sendPartial = PropUtil.getBooleanSessionProperty(this.session, "mail." + this.name + ".sendpartial", false);
      }

      if (sendPartial) {
         this.logger.fine("sendPartial set");
      }

      boolean dsn = false;
      String notify = null;
      if (this.supportsExtension("DSN")) {
         if (this.message instanceof SMTPMessage) {
            notify = ((SMTPMessage)this.message).getDSNNotify();
         }

         if (notify == null) {
            notify = this.session.getProperty("mail." + this.name + ".dsn.notify");
         }

         if (notify != null) {
            dsn = true;
         }
      }

      int k;
      for(k = 0; k < this.addresses.length; ++k) {
         sfex = null;
         InternetAddress ia = (InternetAddress)this.addresses[k];
         String cmd = "RCPT TO:" + this.normalizeAddress(ia.getAddress());
         if (dsn) {
            cmd = cmd + " NOTIFY=" + notify;
         }

         this.sendCommand(cmd);
         int retCode = this.readServerResponse();
         switch (retCode) {
            case 250:
            case 251:
               valid.addElement(ia);
               if (this.reportSuccess) {
                  MessagingException sfex = new SMTPAddressSucceededException(ia, cmd, retCode, this.lastServerResponse);
                  if (mex == null) {
                     mex = sfex;
                  } else {
                     ((MessagingException)mex).setNextException(sfex);
                  }
               }
               continue;
            case 450:
            case 451:
            case 452:
            case 552:
               if (!sendPartial) {
                  sendFailed = true;
               }

               validUnsent.addElement(ia);
               sfex = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
               if (mex == null) {
                  mex = sfex;
               } else {
                  ((MessagingException)mex).setNextException(sfex);
               }
               continue;
            case 501:
            case 503:
            case 550:
            case 551:
            case 553:
               if (!sendPartial) {
                  sendFailed = true;
               }

               invalid.addElement(ia);
               sfex = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
               if (mex == null) {
                  mex = sfex;
               } else {
                  ((MessagingException)mex).setNextException(sfex);
               }
               continue;
         }

         if (retCode >= 400 && retCode <= 499) {
            validUnsent.addElement(ia);
         } else {
            if (retCode < 500 || retCode > 599) {
               if (this.logger.isLoggable(Level.FINE)) {
                  this.logger.fine("got response code " + retCode + ", with response: " + this.lastServerResponse);
               }

               String _lsr = this.lastServerResponse;
               int _lrc = this.lastReturnCode;
               if (this.serverSocket != null) {
                  this.issueCommand("RSET", -1);
               }

               this.lastServerResponse = _lsr;
               this.lastReturnCode = _lrc;
               throw new SMTPAddressFailedException(ia, cmd, retCode, _lsr);
            }

            invalid.addElement(ia);
         }

         if (!sendPartial) {
            sendFailed = true;
         }

         sfex = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
         if (mex == null) {
            mex = sfex;
         } else {
            ((MessagingException)mex).setNextException(sfex);
         }
      }

      if (sendPartial && valid.size() == 0) {
         sendFailed = true;
      }

      int lrc;
      if (sendFailed) {
         this.invalidAddr = new Address[invalid.size()];
         invalid.copyInto(this.invalidAddr);
         this.validUnsentAddr = new Address[valid.size() + validUnsent.size()];
         k = 0;

         for(lrc = 0; lrc < valid.size(); ++lrc) {
            this.validUnsentAddr[k++] = (Address)valid.elementAt(lrc);
         }

         for(lrc = 0; lrc < validUnsent.size(); ++lrc) {
            this.validUnsentAddr[k++] = (Address)validUnsent.elementAt(lrc);
         }
      } else if (!this.reportSuccess && (!sendPartial || invalid.size() <= 0 && validUnsent.size() <= 0)) {
         this.validSentAddr = this.addresses;
      } else {
         this.sendPartiallyFailed = true;
         this.exception = (MessagingException)mex;
         this.invalidAddr = new Address[invalid.size()];
         invalid.copyInto(this.invalidAddr);
         this.validUnsentAddr = new Address[validUnsent.size()];
         validUnsent.copyInto(this.validUnsentAddr);
         this.validSentAddr = new Address[valid.size()];
         valid.copyInto(this.validSentAddr);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         if (this.validSentAddr != null && this.validSentAddr.length > 0) {
            this.logger.fine("Verified Addresses");

            for(k = 0; k < this.validSentAddr.length; ++k) {
               this.logger.fine("  " + this.validSentAddr[k]);
            }
         }

         if (this.validUnsentAddr != null && this.validUnsentAddr.length > 0) {
            this.logger.fine("Valid Unsent Addresses");

            for(k = 0; k < this.validUnsentAddr.length; ++k) {
               this.logger.fine("  " + this.validUnsentAddr[k]);
            }
         }

         if (this.invalidAddr != null && this.invalidAddr.length > 0) {
            this.logger.fine("Invalid Addresses");

            for(k = 0; k < this.invalidAddr.length; ++k) {
               this.logger.fine("  " + this.invalidAddr[k]);
            }
         }
      }

      if (sendFailed) {
         this.logger.fine("Sending failed because of invalid destination addresses");
         this.notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
         String lsr = this.lastServerResponse;
         lrc = this.lastReturnCode;

         try {
            if (this.serverSocket != null) {
               this.issueCommand("RSET", -1);
            }
         } catch (MessagingException var22) {
            try {
               this.close();
            } catch (MessagingException var21) {
               this.logger.log(Level.FINE, "close failed", (Throwable)var21);
            }
         } finally {
            this.lastServerResponse = lsr;
            this.lastReturnCode = lrc;
         }

         throw new SendFailedException("Invalid Addresses", (Exception)mex, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
      }
   }

   protected OutputStream data() throws MessagingException {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         this.issueSendCommand("DATA", 354);
         this.dataStream = new SMTPOutputStream(this.serverOutput);
         return this.dataStream;
      }
   }

   protected void finishData() throws IOException, MessagingException {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         this.dataStream.ensureAtBOL();
         this.issueSendCommand(".", 250);
      }
   }

   protected void startTLS() throws MessagingException {
      this.issueCommand("STARTTLS", 220);

      try {
         this.serverSocket = SocketFetcher.startTLS(this.serverSocket, this.host, this.session.getProperties(), "mail." + this.name);
         this.initStreams();
      } catch (IOException var2) {
         this.closeConnection();
         throw new MessagingException("Could not convert socket to TLS", var2);
      }
   }

   private void openServer(String host, int port) throws MessagingException {
      if (this.logger.isLoggable(Level.FINE)) {
         this.logger.fine("trying to connect to host \"" + host + "\", port " + port + ", isSSL " + this.isSSL);
      }

      try {
         Properties props = this.session.getProperties();
         this.serverSocket = SocketFetcher.getSocket(host, port, props, "mail." + this.name, this.isSSL);
         port = this.serverSocket.getPort();
         this.host = host;
         this.initStreams();
         int r = true;
         int r;
         if ((r = this.readServerResponse()) != 220) {
            this.serverSocket.close();
            this.serverSocket = null;
            this.serverOutput = null;
            this.serverInput = null;
            this.lineInputStream = null;
            if (this.logger.isLoggable(Level.FINE)) {
               this.logger.fine("could not connect to host \"" + host + "\", port: " + port + ", response: " + r + "\n");
            }

            throw new MessagingException("Could not connect to SMTP host: " + host + ", port: " + port + ", response: " + r);
         } else {
            if (this.logger.isLoggable(Level.FINE)) {
               this.logger.fine("connected to host \"" + host + "\", port: " + port + "\n");
            }

         }
      } catch (UnknownHostException var5) {
         throw new MessagingException("Unknown SMTP host: " + host, var5);
      } catch (IOException var6) {
         throw new MessagingException("Could not connect to SMTP host: " + host + ", port: " + port, var6);
      }
   }

   private void openServer() throws MessagingException {
      int port = -1;
      this.host = "UNKNOWN";

      try {
         port = this.serverSocket.getPort();
         this.host = this.serverSocket.getInetAddress().getHostName();
         if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("starting protocol to host \"" + this.host + "\", port " + port);
         }

         this.initStreams();
         int r = true;
         int r;
         if ((r = this.readServerResponse()) != 220) {
            this.serverSocket.close();
            this.serverSocket = null;
            this.serverOutput = null;
            this.serverInput = null;
            this.lineInputStream = null;
            if (this.logger.isLoggable(Level.FINE)) {
               this.logger.fine("got bad greeting from host \"" + this.host + "\", port: " + port + ", response: " + r + "\n");
            }

            throw new MessagingException("Got bad greeting from SMTP host: " + this.host + ", port: " + port + ", response: " + r);
         } else {
            if (this.logger.isLoggable(Level.FINE)) {
               this.logger.fine("protocol started to host \"" + this.host + "\", port: " + port + "\n");
            }

         }
      } catch (IOException var3) {
         throw new MessagingException("Could not start protocol to SMTP host: " + this.host + ", port: " + port, var3);
      }
   }

   private void initStreams() throws IOException {
      boolean quote = PropUtil.getBooleanSessionProperty(this.session, "mail.debug.quote", false);
      this.traceInput = new TraceInputStream(this.serverSocket.getInputStream(), this.traceLogger);
      this.traceInput.setQuote(quote);
      this.traceOutput = new TraceOutputStream(this.serverSocket.getOutputStream(), this.traceLogger);
      this.traceOutput.setQuote(quote);
      this.serverOutput = new BufferedOutputStream(this.traceOutput);
      this.serverInput = new BufferedInputStream(this.traceInput);
      this.lineInputStream = new LineInputStream(this.serverInput);
   }

   private boolean isTracing() {
      return this.traceLogger.isLoggable(Level.FINEST);
   }

   private void suspendTracing() {
      if (this.traceLogger.isLoggable(Level.FINEST)) {
         this.traceInput.setTrace(false);
         this.traceOutput.setTrace(false);
      }

   }

   private void resumeTracing() {
      if (this.traceLogger.isLoggable(Level.FINEST)) {
         this.traceInput.setTrace(true);
         this.traceOutput.setTrace(true);
      }

   }

   public synchronized void issueCommand(String cmd, int expect) throws MessagingException {
      this.sendCommand(cmd);
      int resp = this.readServerResponse();
      if (expect != -1 && resp != expect) {
         throw new MessagingException(this.lastServerResponse);
      }
   }

   private void issueSendCommand(String cmd, int expect) throws MessagingException {
      this.sendCommand(cmd);
      int ret;
      if ((ret = this.readServerResponse()) != expect) {
         int vsl = this.validSentAddr == null ? 0 : this.validSentAddr.length;
         int vul = this.validUnsentAddr == null ? 0 : this.validUnsentAddr.length;
         Address[] valid = new Address[vsl + vul];
         if (vsl > 0) {
            System.arraycopy(this.validSentAddr, 0, valid, 0, vsl);
         }

         if (vul > 0) {
            System.arraycopy(this.validUnsentAddr, 0, valid, vsl, vul);
         }

         this.validSentAddr = null;
         this.validUnsentAddr = valid;
         if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("got response code " + ret + ", with response: " + this.lastServerResponse);
         }

         String _lsr = this.lastServerResponse;
         int _lrc = this.lastReturnCode;
         if (this.serverSocket != null) {
            this.issueCommand("RSET", -1);
         }

         this.lastServerResponse = _lsr;
         this.lastReturnCode = _lrc;
         throw new SMTPSendFailedException(cmd, ret, this.lastServerResponse, this.exception, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
      }
   }

   public synchronized int simpleCommand(String cmd) throws MessagingException {
      this.sendCommand(cmd);
      return this.readServerResponse();
   }

   protected int simpleCommand(byte[] cmd) throws MessagingException {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         this.sendCommand(cmd);
         return this.readServerResponse();
      }
   }

   protected void sendCommand(String cmd) throws MessagingException {
      this.sendCommand(ASCIIUtility.getBytes(cmd));
   }

   private void sendCommand(byte[] cmdBytes) throws MessagingException {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         try {
            this.serverOutput.write(cmdBytes);
            this.serverOutput.write(CRLF);
            this.serverOutput.flush();
         } catch (IOException var3) {
            throw new MessagingException("Can't send command to SMTP host", var3);
         }
      }
   }

   protected int readServerResponse() throws MessagingException {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         String serverResponse = "";
         int returnCode = false;
         StringBuffer buf = new StringBuffer(100);

         try {
            String line = null;

            while(true) {
               line = this.lineInputStream.readLine();
               if (line == null) {
                  serverResponse = buf.toString();
                  if (serverResponse.length() == 0) {
                     serverResponse = "[EOF]";
                  }

                  this.lastServerResponse = serverResponse;
                  this.lastReturnCode = -1;
                  this.logger.log(Level.FINE, "EOF: {0}", (Object)serverResponse);
                  return -1;
               }

               buf.append(line);
               buf.append("\n");
               if (!this.isNotLastLine(line)) {
                  serverResponse = buf.toString();
                  break;
               }
            }
         } catch (IOException var10) {
            this.logger.log(Level.FINE, "exception reading response", (Throwable)var10);
            this.lastServerResponse = "";
            this.lastReturnCode = 0;
            throw new MessagingException("Exception reading response", var10);
         }

         int returnCode;
         if (serverResponse.length() >= 3) {
            try {
               returnCode = Integer.parseInt(serverResponse.substring(0, 3));
            } catch (NumberFormatException var8) {
               try {
                  this.close();
               } catch (MessagingException var7) {
                  this.logger.log(Level.FINE, "close failed", (Throwable)var7);
               }

               returnCode = -1;
            } catch (StringIndexOutOfBoundsException var9) {
               try {
                  this.close();
               } catch (MessagingException var6) {
                  this.logger.log(Level.FINE, "close failed", (Throwable)var6);
               }

               returnCode = -1;
            }
         } else {
            returnCode = -1;
         }

         if (returnCode == -1) {
            this.logger.log(Level.FINE, "bad server response: {0}", (Object)serverResponse);
         }

         this.lastServerResponse = serverResponse;
         this.lastReturnCode = returnCode;
         return returnCode;
      }
   }

   protected void checkConnected() {
      if (!super.isConnected()) {
         throw new IllegalStateException("Not connected");
      }
   }

   private boolean isNotLastLine(String line) {
      return line != null && line.length() >= 4 && line.charAt(3) == '-';
   }

   private String normalizeAddress(String addr) {
      return !addr.startsWith("<") && !addr.endsWith(">") ? "<" + addr + ">" : addr;
   }

   public boolean supportsExtension(String ext) {
      return this.extMap != null && this.extMap.get(ext.toUpperCase(Locale.ENGLISH)) != null;
   }

   public String getExtensionParameter(String ext) {
      return this.extMap == null ? null : (String)this.extMap.get(ext.toUpperCase(Locale.ENGLISH));
   }

   protected boolean supportsAuthentication(String auth) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else if (this.extMap == null) {
         return false;
      } else {
         String a = (String)this.extMap.get("AUTH");
         if (a == null) {
            return false;
         } else {
            StringTokenizer st = new StringTokenizer(a);

            String tok;
            do {
               if (!st.hasMoreTokens()) {
                  if (auth.equalsIgnoreCase("LOGIN") && this.supportsExtension("AUTH=LOGIN")) {
                     this.logger.fine("use AUTH=LOGIN hack");
                     return true;
                  }

                  return false;
               }

               tok = st.nextToken();
            } while(!tok.equalsIgnoreCase(auth));

            return true;
         }
      }
   }

   protected static String xtext(String s) {
      StringBuffer sb = null;

      for(int i = 0; i < s.length(); ++i) {
         char c = s.charAt(i);
         if (c >= 128) {
            throw new IllegalArgumentException("Non-ASCII character in SMTP submitter: " + s);
         }

         if (c >= '!' && c <= '~' && c != '+' && c != '=') {
            if (sb != null) {
               sb.append(c);
            }
         } else {
            if (sb == null) {
               sb = new StringBuffer(s.length() + 4);
               sb.append(s.substring(0, i));
            }

            sb.append('+');
            sb.append(hexchar[(c & 240) >> 4]);
            sb.append(hexchar[c & 15]);
         }
      }

      return sb != null ? sb.toString() : s;
   }

   private void sendMessageStart(String subject) {
   }

   private void sendMessageEnd() {
   }

   static {
      $assertionsDisabled = !SMTPTransport.class.desiredAssertionStatus();
      ignoreList = new String[]{"Bcc", "Content-Length"};
      CRLF = new byte[]{13, 10};
      UNKNOWN_SA = new String[0];
      hexchar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   }

   private class NtlmAuthenticator extends Authenticator {
      private Ntlm ntlm;
      private int flags;

      NtlmAuthenticator() {
         super("NTLM");
      }

      String getInitialResponse(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
         this.ntlm = new Ntlm(SMTPTransport.this.getNTLMDomain(), SMTPTransport.this.getLocalHost(), user, passwd, SMTPTransport.this.logger);
         this.flags = PropUtil.getIntProperty(SMTPTransport.this.session.getProperties(), "mail." + SMTPTransport.this.name + ".auth.ntlm.flags", 0);
         String type1 = this.ntlm.generateType1Msg(this.flags);
         return type1;
      }

      void doAuth(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
         String type3 = this.ntlm.generateType3Msg(SMTPTransport.this.getLastServerResponse().substring(4).trim());
         this.resp = SMTPTransport.this.simpleCommand(type3);
      }
   }

   private class DigestMD5Authenticator extends Authenticator {
      private DigestMD5 md5support;

      DigestMD5Authenticator() {
         super("DIGEST-MD5");
      }

      private synchronized DigestMD5 getMD5() {
         if (this.md5support == null) {
            this.md5support = new DigestMD5(SMTPTransport.this.logger);
         }

         return this.md5support;
      }

      void doAuth(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
         DigestMD5 md5 = this.getMD5();
         if (md5 == null) {
            this.resp = -1;
         } else {
            byte[] b = md5.authClient(host, user, passwd, SMTPTransport.this.getSASLRealm(), SMTPTransport.this.getLastServerResponse());
            this.resp = SMTPTransport.this.simpleCommand(b);
            if (this.resp == 334) {
               if (!md5.authServer(SMTPTransport.this.getLastServerResponse())) {
                  this.resp = -1;
               } else {
                  this.resp = SMTPTransport.this.simpleCommand(new byte[0]);
               }
            }

         }
      }
   }

   private class PlainAuthenticator extends Authenticator {
      PlainAuthenticator() {
         super("PLAIN");
      }

      String getInitialResponse(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         OutputStream b64os = new BASE64EncoderStream(bos, Integer.MAX_VALUE);
         if (authzid != null) {
            b64os.write(ASCIIUtility.getBytes(authzid));
         }

         b64os.write(0);
         b64os.write(ASCIIUtility.getBytes(user));
         b64os.write(0);
         b64os.write(ASCIIUtility.getBytes(passwd));
         b64os.flush();
         return ASCIIUtility.toString(bos.toByteArray());
      }

      void doAuth(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
         throw new AuthenticationFailedException("PLAIN asked for more");
      }
   }

   private class LoginAuthenticator extends Authenticator {
      LoginAuthenticator() {
         super("LOGIN");
      }

      void doAuth(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
         this.resp = SMTPTransport.this.simpleCommand(BASE64EncoderStream.encode(ASCIIUtility.getBytes(user)));
         if (this.resp == 334) {
            this.resp = SMTPTransport.this.simpleCommand(BASE64EncoderStream.encode(ASCIIUtility.getBytes(passwd)));
         }

      }
   }

   private abstract class Authenticator {
      protected int resp;
      private String mech;

      Authenticator(String mech) {
         this.mech = mech.toUpperCase(Locale.ENGLISH);
      }

      String getMechanism() {
         return this.mech;
      }

      boolean authenticate(String host, String authzid, String user, String passwd) throws MessagingException {
         try {
            String ir = this.getInitialResponse(host, authzid, user, passwd);
            if (SMTPTransport.this.noauthdebug && SMTPTransport.this.isTracing()) {
               SMTPTransport.this.logger.fine("AUTH " + this.mech + " command trace suppressed");
               SMTPTransport.this.suspendTracing();
            }

            if (ir != null) {
               this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech + " " + (ir.length() == 0 ? "=" : ir));
            } else {
               this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech);
            }

            if (this.resp == 530) {
               SMTPTransport.this.startTLS();
               if (ir != null) {
                  this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech + " " + ir);
               } else {
                  this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech);
               }
            }

            if (this.resp == 334) {
               this.doAuth(host, authzid, user, passwd);
            }
         } catch (IOException var10) {
            SMTPTransport.this.logger.log(Level.FINE, "AUTH " + this.mech + " failed", (Throwable)var10);
         } finally {
            if (SMTPTransport.this.noauthdebug && SMTPTransport.this.isTracing()) {
               SMTPTransport.this.logger.fine("AUTH " + this.mech + " " + (this.resp == 235 ? "succeeded" : "failed"));
            }

            SMTPTransport.this.resumeTracing();
            if (this.resp != 235) {
               SMTPTransport.this.closeConnection();
               throw new AuthenticationFailedException(SMTPTransport.this.getLastServerResponse());
            }

         }

         return true;
      }

      String getInitialResponse(String host, String authzid, String user, String passwd) throws MessagingException, IOException {
         return null;
      }

      abstract void doAuth(String var1, String var2, String var3, String var4) throws MessagingException, IOException;
   }
}
