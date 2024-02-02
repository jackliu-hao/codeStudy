package javax.mail;

import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.MailLogger;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

public final class Session {
   private final Properties props;
   private final Authenticator authenticator;
   private final Hashtable authTable = new Hashtable();
   private boolean debug = false;
   private PrintStream out;
   private MailLogger logger;
   private final Vector providers = new Vector();
   private final Hashtable providersByProtocol = new Hashtable();
   private final Hashtable providersByClassName = new Hashtable();
   private final Properties addressMap = new Properties();
   private static Session defaultSession = null;

   private Session(Properties props, Authenticator authenticator) {
      this.props = props;
      this.authenticator = authenticator;
      if (Boolean.valueOf(props.getProperty("mail.debug"))) {
         this.debug = true;
      }

      this.initLogger();
      this.logger.log(Level.CONFIG, "JavaMail version {0}", (Object)"1.4.7");
      Class cl;
      if (authenticator != null) {
         cl = authenticator.getClass();
      } else {
         cl = this.getClass();
      }

      this.loadProviders(cl);
      this.loadAddressMap(cl);
   }

   private final void initLogger() {
      this.logger = new MailLogger(this.getClass(), "DEBUG", this.debug, this.getDebugOut());
   }

   public static Session getInstance(Properties props, Authenticator authenticator) {
      return new Session(props, authenticator);
   }

   public static Session getInstance(Properties props) {
      return new Session(props, (Authenticator)null);
   }

   public static synchronized Session getDefaultInstance(Properties props, Authenticator authenticator) {
      if (defaultSession == null) {
         defaultSession = new Session(props, authenticator);
      } else if (defaultSession.authenticator != authenticator && (defaultSession.authenticator == null || authenticator == null || defaultSession.authenticator.getClass().getClassLoader() != authenticator.getClass().getClassLoader())) {
         throw new SecurityException("Access to default session denied");
      }

      return defaultSession;
   }

   public static Session getDefaultInstance(Properties props) {
      return getDefaultInstance(props, (Authenticator)null);
   }

   public synchronized void setDebug(boolean debug) {
      this.debug = debug;
      this.initLogger();
      this.logger.log(Level.CONFIG, "setDebug: JavaMail version {0}", (Object)"1.4.7");
   }

   public synchronized boolean getDebug() {
      return this.debug;
   }

   public synchronized void setDebugOut(PrintStream out) {
      this.out = out;
      this.initLogger();
   }

   public synchronized PrintStream getDebugOut() {
      return this.out == null ? System.out : this.out;
   }

   public synchronized Provider[] getProviders() {
      Provider[] _providers = new Provider[this.providers.size()];
      this.providers.copyInto(_providers);
      return _providers;
   }

   public synchronized Provider getProvider(String protocol) throws NoSuchProviderException {
      if (protocol != null && protocol.length() > 0) {
         Provider _provider = null;
         String _className = this.props.getProperty("mail." + protocol + ".class");
         if (_className != null) {
            if (this.logger.isLoggable(Level.FINE)) {
               this.logger.fine("mail." + protocol + ".class property exists and points to " + _className);
            }

            _provider = (Provider)this.providersByClassName.get(_className);
         }

         if (_provider != null) {
            return _provider;
         } else {
            _provider = (Provider)this.providersByProtocol.get(protocol);
            if (_provider == null) {
               throw new NoSuchProviderException("No provider for " + protocol);
            } else {
               if (this.logger.isLoggable(Level.FINE)) {
                  this.logger.fine("getProvider() returning " + _provider.toString());
               }

               return _provider;
            }
         }
      } else {
         throw new NoSuchProviderException("Invalid protocol: null");
      }
   }

   public synchronized void setProvider(Provider provider) throws NoSuchProviderException {
      if (provider == null) {
         throw new NoSuchProviderException("Can't set null provider");
      } else {
         this.providersByProtocol.put(provider.getProtocol(), provider);
         this.props.put("mail." + provider.getProtocol() + ".class", provider.getClassName());
      }
   }

   public Store getStore() throws NoSuchProviderException {
      return this.getStore(this.getProperty("mail.store.protocol"));
   }

   public Store getStore(String protocol) throws NoSuchProviderException {
      return this.getStore(new URLName(protocol, (String)null, -1, (String)null, (String)null, (String)null));
   }

   public Store getStore(URLName url) throws NoSuchProviderException {
      String protocol = url.getProtocol();
      Provider p = this.getProvider(protocol);
      return this.getStore(p, url);
   }

   public Store getStore(Provider provider) throws NoSuchProviderException {
      return this.getStore(provider, (URLName)null);
   }

   private Store getStore(Provider provider, URLName url) throws NoSuchProviderException {
      if (provider != null && provider.getType() == Provider.Type.STORE) {
         try {
            return (Store)this.getService(provider, url);
         } catch (ClassCastException var4) {
            throw new NoSuchProviderException("incorrect class");
         }
      } else {
         throw new NoSuchProviderException("invalid provider");
      }
   }

   public Folder getFolder(URLName url) throws MessagingException {
      Store store = this.getStore(url);
      store.connect();
      return store.getFolder(url);
   }

   public Transport getTransport() throws NoSuchProviderException {
      return this.getTransport(this.getProperty("mail.transport.protocol"));
   }

   public Transport getTransport(String protocol) throws NoSuchProviderException {
      return this.getTransport(new URLName(protocol, (String)null, -1, (String)null, (String)null, (String)null));
   }

   public Transport getTransport(URLName url) throws NoSuchProviderException {
      String protocol = url.getProtocol();
      Provider p = this.getProvider(protocol);
      return this.getTransport(p, url);
   }

   public Transport getTransport(Provider provider) throws NoSuchProviderException {
      return this.getTransport(provider, (URLName)null);
   }

   public Transport getTransport(Address address) throws NoSuchProviderException {
      String transportProtocol = this.getProperty("mail.transport.protocol." + address.getType());
      if (transportProtocol != null) {
         return this.getTransport(transportProtocol);
      } else {
         transportProtocol = (String)this.addressMap.get(address.getType());
         if (transportProtocol != null) {
            return this.getTransport(transportProtocol);
         } else {
            throw new NoSuchProviderException("No provider for Address type: " + address.getType());
         }
      }
   }

   private Transport getTransport(Provider provider, URLName url) throws NoSuchProviderException {
      if (provider != null && provider.getType() == Provider.Type.TRANSPORT) {
         try {
            return (Transport)this.getService(provider, url);
         } catch (ClassCastException var4) {
            throw new NoSuchProviderException("incorrect class");
         }
      } else {
         throw new NoSuchProviderException("invalid provider");
      }
   }

   private Object getService(Provider provider, URLName url) throws NoSuchProviderException {
      if (provider == null) {
         throw new NoSuchProviderException("null");
      } else {
         if (url == null) {
            url = new URLName(provider.getProtocol(), (String)null, -1, (String)null, (String)null, (String)null);
         }

         Object service = null;
         ClassLoader cl;
         if (this.authenticator != null) {
            cl = this.authenticator.getClass().getClassLoader();
         } else {
            cl = this.getClass().getClassLoader();
         }

         Class serviceClass = null;

         try {
            ClassLoader ccl = getContextClassLoader();
            if (ccl != null) {
               try {
                  serviceClass = Class.forName(provider.getClassName(), false, ccl);
               } catch (ClassNotFoundException var11) {
               }
            }

            if (serviceClass == null) {
               serviceClass = Class.forName(provider.getClassName(), false, cl);
            }
         } catch (Exception var12) {
            try {
               serviceClass = Class.forName(provider.getClassName());
            } catch (Exception var10) {
               this.logger.log(Level.FINE, "Exception loading provider", (Throwable)var10);
               throw new NoSuchProviderException(provider.getProtocol());
            }
         }

         try {
            Class[] c = new Class[]{Session.class, URLName.class};
            Constructor cons = serviceClass.getConstructor(c);
            Object[] o = new Object[]{this, url};
            service = cons.newInstance(o);
            return service;
         } catch (Exception var9) {
            this.logger.log(Level.FINE, "Exception loading provider", (Throwable)var9);
            throw new NoSuchProviderException(provider.getProtocol());
         }
      }
   }

   public void setPasswordAuthentication(URLName url, PasswordAuthentication pw) {
      if (pw == null) {
         this.authTable.remove(url);
      } else {
         this.authTable.put(url, pw);
      }

   }

   public PasswordAuthentication getPasswordAuthentication(URLName url) {
      return (PasswordAuthentication)this.authTable.get(url);
   }

   public PasswordAuthentication requestPasswordAuthentication(InetAddress addr, int port, String protocol, String prompt, String defaultUserName) {
      return this.authenticator != null ? this.authenticator.requestPasswordAuthentication(addr, port, protocol, prompt, defaultUserName) : null;
   }

   public Properties getProperties() {
      return this.props;
   }

   public String getProperty(String name) {
      return this.props.getProperty(name);
   }

   private void loadProviders(Class cl) {
      StreamLoader loader = new StreamLoader() {
         public void load(InputStream is) throws IOException {
            Session.this.loadProvidersFromStream(is);
         }
      };

      try {
         String res = System.getProperty("java.home") + File.separator + "lib" + File.separator + "javamail.providers";
         this.loadFile(res, loader);
      } catch (SecurityException var4) {
         this.logger.log(Level.CONFIG, "can't get java.home", (Throwable)var4);
      }

      this.loadAllResources("META-INF/javamail.providers", cl, loader);
      this.loadResource("/META-INF/javamail.default.providers", cl, loader);
      if (this.providers.size() == 0) {
         this.logger.config("failed to load any providers, using defaults");
         this.addProvider(new Provider(Provider.Type.STORE, "imap", "com.sun.mail.imap.IMAPStore", "Sun Microsystems, Inc.", "1.4.7"));
         this.addProvider(new Provider(Provider.Type.STORE, "imaps", "com.sun.mail.imap.IMAPSSLStore", "Sun Microsystems, Inc.", "1.4.7"));
         this.addProvider(new Provider(Provider.Type.STORE, "pop3", "com.sun.mail.pop3.POP3Store", "Sun Microsystems, Inc.", "1.4.7"));
         this.addProvider(new Provider(Provider.Type.STORE, "pop3s", "com.sun.mail.pop3.POP3SSLStore", "Sun Microsystems, Inc.", "1.4.7"));
         this.addProvider(new Provider(Provider.Type.TRANSPORT, "smtp", "com.sun.mail.smtp.SMTPTransport", "Sun Microsystems, Inc.", "1.4.7"));
         this.addProvider(new Provider(Provider.Type.TRANSPORT, "smtps", "com.sun.mail.smtp.SMTPSSLTransport", "Sun Microsystems, Inc.", "1.4.7"));
      }

      if (this.logger.isLoggable(Level.CONFIG)) {
         this.logger.config("Tables of loaded providers");
         this.logger.config("Providers Listed By Class Name: " + this.providersByClassName.toString());
         this.logger.config("Providers Listed By Protocol: " + this.providersByProtocol.toString());
      }

   }

   private void loadProvidersFromStream(InputStream is) throws IOException {
      if (is != null) {
         LineInputStream lis = new LineInputStream(is);

         while(true) {
            while(true) {
               String currLine;
               do {
                  if ((currLine = lis.readLine()) == null) {
                     return;
                  }
               } while(currLine.startsWith("#"));

               Provider.Type type = null;
               String protocol = null;
               String className = null;
               String vendor = null;
               String version = null;
               StringTokenizer tuples = new StringTokenizer(currLine, ";");

               while(tuples.hasMoreTokens()) {
                  String currTuple = tuples.nextToken().trim();
                  int sep = currTuple.indexOf("=");
                  if (currTuple.startsWith("protocol=")) {
                     protocol = currTuple.substring(sep + 1);
                  } else if (currTuple.startsWith("type=")) {
                     String strType = currTuple.substring(sep + 1);
                     if (strType.equalsIgnoreCase("store")) {
                        type = Provider.Type.STORE;
                     } else if (strType.equalsIgnoreCase("transport")) {
                        type = Provider.Type.TRANSPORT;
                     }
                  } else if (currTuple.startsWith("class=")) {
                     className = currTuple.substring(sep + 1);
                  } else if (currTuple.startsWith("vendor=")) {
                     vendor = currTuple.substring(sep + 1);
                  } else if (currTuple.startsWith("version=")) {
                     version = currTuple.substring(sep + 1);
                  }
               }

               if (type != null && protocol != null && className != null && protocol.length() > 0 && className.length() > 0) {
                  Provider provider = new Provider(type, protocol, className, vendor, version);
                  this.addProvider(provider);
               } else {
                  this.logger.log(Level.CONFIG, "Bad provider entry: {0}", (Object)currLine);
               }
            }
         }
      }
   }

   public synchronized void addProvider(Provider provider) {
      this.providers.addElement(provider);
      this.providersByClassName.put(provider.getClassName(), provider);
      if (!this.providersByProtocol.containsKey(provider.getProtocol())) {
         this.providersByProtocol.put(provider.getProtocol(), provider);
      }

   }

   private void loadAddressMap(Class cl) {
      StreamLoader loader = new StreamLoader() {
         public void load(InputStream is) throws IOException {
            Session.this.addressMap.load(is);
         }
      };
      this.loadResource("/META-INF/javamail.default.address.map", cl, loader);
      this.loadAllResources("META-INF/javamail.address.map", cl, loader);

      try {
         String res = System.getProperty("java.home") + File.separator + "lib" + File.separator + "javamail.address.map";
         this.loadFile(res, loader);
      } catch (SecurityException var4) {
         this.logger.log(Level.CONFIG, "can't get java.home", (Throwable)var4);
      }

      if (this.addressMap.isEmpty()) {
         this.logger.config("failed to load address map, using defaults");
         this.addressMap.put("rfc822", "smtp");
      }

   }

   public synchronized void setProtocolForAddress(String addresstype, String protocol) {
      if (protocol == null) {
         this.addressMap.remove(addresstype);
      } else {
         this.addressMap.put(addresstype, protocol);
      }

   }

   private void loadFile(String name, StreamLoader loader) {
      InputStream clis = null;

      try {
         clis = new BufferedInputStream(new FileInputStream(name));
         loader.load(clis);
         this.logger.log(Level.CONFIG, "successfully loaded file: {0}", (Object)name);
      } catch (FileNotFoundException var17) {
      } catch (IOException var18) {
         if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.log(Level.CONFIG, "not loading file: " + name, (Throwable)var18);
         }
      } catch (SecurityException var19) {
         if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.log(Level.CONFIG, "not loading file: " + name, (Throwable)var19);
         }
      } finally {
         try {
            if (clis != null) {
               clis.close();
            }
         } catch (IOException var16) {
         }

      }

   }

   private void loadResource(String name, Class cl, StreamLoader loader) {
      InputStream clis = null;

      try {
         clis = getResourceAsStream(cl, name);
         if (clis != null) {
            loader.load(clis);
            this.logger.log(Level.CONFIG, "successfully loaded resource: {0}", (Object)name);
         }
      } catch (IOException var16) {
         this.logger.log(Level.CONFIG, "Exception loading resource", (Throwable)var16);
      } catch (SecurityException var17) {
         this.logger.log(Level.CONFIG, "Exception loading resource", (Throwable)var17);
      } finally {
         try {
            if (clis != null) {
               clis.close();
            }
         } catch (IOException var15) {
         }

      }

   }

   private void loadAllResources(String name, Class cl, StreamLoader loader) {
      boolean anyLoaded = false;

      try {
         ClassLoader cld = null;
         cld = getContextClassLoader();
         if (cld == null) {
            cld = cl.getClassLoader();
         }

         URL[] urls;
         if (cld != null) {
            urls = getResources(cld, name);
         } else {
            urls = getSystemResources(name);
         }

         if (urls != null) {
            for(int i = 0; i < urls.length; ++i) {
               URL url = urls[i];
               InputStream clis = null;
               this.logger.log(Level.CONFIG, "URL {0}", (Object)url);

               try {
                  clis = openStream(url);
                  if (clis != null) {
                     loader.load(clis);
                     anyLoaded = true;
                     this.logger.log(Level.CONFIG, "successfully loaded resource: {0}", (Object)url);
                  } else {
                     this.logger.log(Level.CONFIG, "not loading resource: {0}", (Object)url);
                  }
               } catch (FileNotFoundException var24) {
               } catch (IOException var25) {
                  this.logger.log(Level.CONFIG, "Exception loading resource", (Throwable)var25);
               } catch (SecurityException var26) {
                  this.logger.log(Level.CONFIG, "Exception loading resource", (Throwable)var26);
               } finally {
                  try {
                     if (clis != null) {
                        clis.close();
                     }
                  } catch (IOException var23) {
                  }

               }
            }
         }
      } catch (Exception var28) {
         this.logger.log(Level.CONFIG, "Exception loading resource", (Throwable)var28);
      }

      if (!anyLoaded) {
         this.loadResource("/" + name, cl, loader);
      }

   }

   private static ClassLoader getContextClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            ClassLoader cl = null;

            try {
               cl = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException var3) {
            }

            return cl;
         }
      });
   }

   private static InputStream getResourceAsStream(final Class c, final String name) throws IOException {
      try {
         return (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws IOException {
               return c.getResourceAsStream(name);
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getException();
      }
   }

   private static URL[] getResources(final ClassLoader cl, final String name) {
      return (URL[])((URL[])AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            URL[] ret = null;

            try {
               Vector v = new Vector();
               Enumeration e = cl.getResources(name);

               while(e != null && e.hasMoreElements()) {
                  URL url = (URL)e.nextElement();
                  if (url != null) {
                     v.addElement(url);
                  }
               }

               if (v.size() > 0) {
                  ret = new URL[v.size()];
                  v.copyInto(ret);
               }
            } catch (IOException var5) {
            } catch (SecurityException var6) {
            }

            return ret;
         }
      }));
   }

   private static URL[] getSystemResources(final String name) {
      return (URL[])((URL[])AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            URL[] ret = null;

            try {
               Vector v = new Vector();
               Enumeration e = ClassLoader.getSystemResources(name);

               while(e != null && e.hasMoreElements()) {
                  URL url = (URL)e.nextElement();
                  if (url != null) {
                     v.addElement(url);
                  }
               }

               if (v.size() > 0) {
                  ret = new URL[v.size()];
                  v.copyInto(ret);
               }
            } catch (IOException var5) {
            } catch (SecurityException var6) {
            }

            return ret;
         }
      }));
   }

   private static InputStream openStream(final URL url) throws IOException {
      try {
         return (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws IOException {
               return url.openStream();
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (IOException)var2.getException();
      }
   }
}
