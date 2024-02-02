package com.sun.mail.pop3;

import com.sun.mail.util.MailLogger;
import com.sun.mail.util.PropUtil;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

public class POP3Store extends Store {
   private String name;
   private int defaultPort;
   private boolean isSSL;
   private Protocol port;
   private POP3Folder portOwner;
   private String host;
   private int portNum;
   private String user;
   private String passwd;
   private boolean useStartTLS;
   private boolean requireStartTLS;
   private boolean usingSSL;
   private Map capabilities;
   private MailLogger logger;
   volatile Constructor messageConstructor;
   volatile boolean rsetBeforeQuit;
   volatile boolean disableTop;
   volatile boolean forgetTopHeaders;
   volatile boolean supportsUidl;
   volatile boolean cacheWriteTo;
   volatile boolean useFileCache;
   volatile File fileCacheDir;
   volatile boolean keepMessageContent;

   public POP3Store(Session session, URLName url) {
      this(session, url, "pop3", false);
   }

   public POP3Store(Session session, URLName url, String name, boolean isSSL) {
      super(session, url);
      this.name = "pop3";
      this.defaultPort = 110;
      this.isSSL = false;
      this.port = null;
      this.portOwner = null;
      this.host = null;
      this.portNum = -1;
      this.user = null;
      this.passwd = null;
      this.useStartTLS = false;
      this.requireStartTLS = false;
      this.usingSSL = false;
      this.messageConstructor = null;
      this.rsetBeforeQuit = false;
      this.disableTop = false;
      this.forgetTopHeaders = false;
      this.supportsUidl = true;
      this.cacheWriteTo = false;
      this.useFileCache = false;
      this.fileCacheDir = null;
      this.keepMessageContent = false;
      if (url != null) {
         name = url.getProtocol();
      }

      this.name = name;
      this.logger = new MailLogger(this.getClass(), "DEBUG POP3", session);
      if (!isSSL) {
         isSSL = PropUtil.getBooleanSessionProperty(session, "mail." + name + ".ssl.enable", false);
      }

      if (isSSL) {
         this.defaultPort = 995;
      } else {
         this.defaultPort = 110;
      }

      this.isSSL = isSSL;
      this.rsetBeforeQuit = this.getBoolProp("rsetbeforequit");
      this.disableTop = this.getBoolProp("disabletop");
      this.forgetTopHeaders = this.getBoolProp("forgettopheaders");
      this.cacheWriteTo = this.getBoolProp("cachewriteto");
      this.useFileCache = this.getBoolProp("filecache.enable");
      String dir = session.getProperty("mail." + name + ".filecache.dir");
      if (dir != null && this.logger.isLoggable(Level.CONFIG)) {
         this.logger.config("mail." + name + ".filecache.dir: " + dir);
      }

      if (dir != null) {
         this.fileCacheDir = new File(dir);
      }

      this.keepMessageContent = this.getBoolProp("keepmessagecontent");
      this.useStartTLS = this.getBoolProp("starttls.enable");
      this.requireStartTLS = this.getBoolProp("starttls.required");
      String s = session.getProperty("mail." + name + ".message.class");
      if (s != null) {
         this.logger.log(Level.CONFIG, "message class: {0}", (Object)s);

         try {
            ClassLoader cl = this.getClass().getClassLoader();
            Class messageClass = null;

            try {
               messageClass = Class.forName(s, false, cl);
            } catch (ClassNotFoundException var10) {
               messageClass = Class.forName(s);
            }

            Class[] c = new Class[]{Folder.class, Integer.TYPE};
            this.messageConstructor = messageClass.getConstructor(c);
         } catch (Exception var11) {
            this.logger.log(Level.CONFIG, "failed to load message class", (Throwable)var11);
         }
      }

   }

   private final synchronized boolean getBoolProp(String prop) {
      prop = "mail." + this.name + "." + prop;
      boolean val = PropUtil.getBooleanSessionProperty(this.session, prop, false);
      if (this.logger.isLoggable(Level.CONFIG)) {
         this.logger.config(prop + ": " + val);
      }

      return val;
   }

   synchronized Session getSession() {
      return this.session;
   }

   protected synchronized boolean protocolConnect(String host, int portNum, String user, String passwd) throws MessagingException {
      if (host != null && passwd != null && user != null) {
         if (portNum == -1) {
            portNum = PropUtil.getIntSessionProperty(this.session, "mail." + this.name + ".port", -1);
         }

         if (portNum == -1) {
            portNum = this.defaultPort;
         }

         this.host = host;
         this.portNum = portNum;
         this.user = user;
         this.passwd = passwd;

         try {
            this.port = this.getPort((POP3Folder)null);
            return true;
         } catch (EOFException var6) {
            throw new AuthenticationFailedException(var6.getMessage());
         } catch (IOException var7) {
            throw new MessagingException("Connect failed", var7);
         }
      } else {
         return false;
      }
   }

   public synchronized boolean isConnected() {
      if (!super.isConnected()) {
         return false;
      } else {
         try {
            if (this.port == null) {
               this.port = this.getPort((POP3Folder)null);
            } else if (!this.port.noop()) {
               throw new IOException("NOOP failed");
            }

            return true;
         } catch (IOException var10) {
            try {
               super.close();
            } catch (MessagingException var8) {
            } finally {
               return false;
            }

            return false;
         }
      }
   }

   synchronized Protocol getPort(POP3Folder owner) throws IOException {
      if (this.port != null && this.portOwner == null) {
         this.portOwner = owner;
         return this.port;
      } else {
         Protocol p = new Protocol(this.host, this.portNum, this.logger, this.session.getProperties(), "mail." + this.name, this.isSSL);
         if (this.useStartTLS || this.requireStartTLS) {
            if (p.hasCapability("STLS")) {
               if (p.stls()) {
                  p.setCapabilities(p.capa());
               } else if (this.requireStartTLS) {
                  this.logger.fine("STLS required but failed");

                  try {
                     p.quit();
                  } catch (IOException var28) {
                  } finally {
                     throw new EOFException("STLS required but failed");
                  }

                  throw new EOFException("STLS required but failed");
               }
            } else if (this.requireStartTLS) {
               this.logger.fine("STLS required but not supported");

               try {
                  p.quit();
               } catch (IOException var29) {
               } finally {
                  throw new EOFException("STLS required but not supported");
               }

               throw new EOFException("STLS required but not supported");
            }
         }

         this.capabilities = p.getCapabilities();
         this.usingSSL = p.isSSL();
         if (!this.disableTop && this.capabilities != null && !this.capabilities.containsKey("TOP")) {
            this.disableTop = true;
            this.logger.fine("server doesn't support TOP, disabling it");
         }

         this.supportsUidl = this.capabilities == null || this.capabilities.containsKey("UIDL");
         String msg = null;
         if ((msg = p.login(this.user, this.passwd)) != null) {
            try {
               p.quit();
            } catch (IOException var30) {
            } finally {
               throw new EOFException(msg);
            }

            throw new EOFException(msg);
         } else {
            if (this.port == null && owner != null) {
               this.port = p;
               this.portOwner = owner;
            }

            if (this.portOwner == null) {
               this.portOwner = owner;
            }

            return p;
         }
      }
   }

   synchronized void closePort(POP3Folder owner) {
      if (this.portOwner == owner) {
         this.port = null;
         this.portOwner = null;
      }

   }

   public synchronized void close() throws MessagingException {
      try {
         if (this.port != null) {
            this.port.quit();
         }
      } catch (IOException var6) {
      } finally {
         this.port = null;
         super.close();
      }

   }

   public Folder getDefaultFolder() throws MessagingException {
      this.checkConnected();
      return new DefaultFolder(this);
   }

   public Folder getFolder(String name) throws MessagingException {
      this.checkConnected();
      return new POP3Folder(this, name);
   }

   public Folder getFolder(URLName url) throws MessagingException {
      this.checkConnected();
      return new POP3Folder(this, url.getFile());
   }

   public Map capabilities() throws MessagingException {
      Map c;
      synchronized(this) {
         c = this.capabilities;
      }

      return c != null ? Collections.unmodifiableMap(c) : Collections.EMPTY_MAP;
   }

   public boolean isSSL() {
      return this.usingSSL;
   }

   protected void finalize() throws Throwable {
      super.finalize();
      if (this.port != null) {
         this.close();
      }

   }

   private void checkConnected() throws MessagingException {
      if (!super.isConnected()) {
         throw new MessagingException("Not connected");
      }
   }
}
