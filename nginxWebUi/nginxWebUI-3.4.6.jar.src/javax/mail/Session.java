/*      */ package javax.mail;
/*      */ 
/*      */ import com.sun.mail.util.LineInputStream;
/*      */ import com.sun.mail.util.MailLogger;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.net.InetAddress;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Properties;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import java.util.logging.Level;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Session
/*      */ {
/*      */   private final Properties props;
/*      */   private final Authenticator authenticator;
/*      */   private final Hashtable authTable;
/*      */   private boolean debug;
/*      */   private PrintStream out;
/*      */   private MailLogger logger;
/*      */   private final Vector providers;
/*      */   private final Hashtable providersByProtocol;
/*      */   private final Hashtable providersByClassName;
/*      */   private final Properties addressMap;
/*  202 */   private static Session defaultSession = null; private Session(Properties props, Authenticator authenticator) { Class cl; this.authTable = new Hashtable(); this.debug = false; this.providers = new Vector();
/*      */     this.providersByProtocol = new Hashtable();
/*      */     this.providersByClassName = new Hashtable();
/*      */     this.addressMap = new Properties();
/*  206 */     this.props = props;
/*  207 */     this.authenticator = authenticator;
/*      */     
/*  209 */     if (Boolean.valueOf(props.getProperty("mail.debug")).booleanValue()) {
/*  210 */       this.debug = true;
/*      */     }
/*  212 */     initLogger();
/*  213 */     this.logger.log(Level.CONFIG, "JavaMail version {0}", "1.4.7");
/*      */ 
/*      */ 
/*      */     
/*  217 */     if (authenticator != null) {
/*  218 */       cl = authenticator.getClass();
/*      */     } else {
/*  220 */       cl = getClass();
/*      */     } 
/*  222 */     loadProviders(cl);
/*  223 */     loadAddressMap(cl); }
/*      */ 
/*      */   
/*      */   private final void initLogger() {
/*  227 */     this.logger = new MailLogger(getClass(), "DEBUG", this.debug, getDebugOut());
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
/*      */   public static Session getInstance(Properties props, Authenticator authenticator) {
/*  248 */     return new Session(props, authenticator);
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
/*      */   public static Session getInstance(Properties props) {
/*  265 */     return new Session(props, null);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static synchronized Session getDefaultInstance(Properties props, Authenticator authenticator) {
/*  314 */     if (defaultSession == null) {
/*  315 */       defaultSession = new Session(props, authenticator);
/*      */     
/*      */     }
/*  318 */     else if (defaultSession.authenticator != authenticator) {
/*      */       
/*  320 */       if (defaultSession.authenticator == null || authenticator == null || defaultSession.authenticator.getClass().getClassLoader() != authenticator.getClass().getClassLoader())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  327 */         throw new SecurityException("Access to default session denied");
/*      */       }
/*      */     } 
/*  330 */     return defaultSession;
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
/*      */   public static Session getDefaultInstance(Properties props) {
/*  355 */     return getDefaultInstance(props, null);
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
/*      */   public synchronized void setDebug(boolean debug) {
/*  374 */     this.debug = debug;
/*  375 */     initLogger();
/*  376 */     this.logger.log(Level.CONFIG, "setDebug: JavaMail version {0}", "1.4.7");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean getDebug() {
/*  386 */     return this.debug;
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
/*      */   public synchronized void setDebugOut(PrintStream out) {
/*  400 */     this.out = out;
/*  401 */     initLogger();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized PrintStream getDebugOut() {
/*  412 */     if (this.out == null) {
/*  413 */       return System.out;
/*      */     }
/*  415 */     return this.out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Provider[] getProviders() {
/*  426 */     Provider[] _providers = new Provider[this.providers.size()];
/*  427 */     this.providers.copyInto((Object[])_providers);
/*  428 */     return _providers;
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
/*      */   public synchronized Provider getProvider(String protocol) throws NoSuchProviderException {
/*  448 */     if (protocol == null || protocol.length() <= 0) {
/*  449 */       throw new NoSuchProviderException("Invalid protocol: null");
/*      */     }
/*      */     
/*  452 */     Provider _provider = null;
/*      */ 
/*      */     
/*  455 */     String _className = this.props.getProperty("mail." + protocol + ".class");
/*  456 */     if (_className != null) {
/*  457 */       if (this.logger.isLoggable(Level.FINE)) {
/*  458 */         this.logger.fine("mail." + protocol + ".class property exists and points to " + _className);
/*      */       }
/*      */ 
/*      */       
/*  462 */       _provider = (Provider)this.providersByClassName.get(_className);
/*      */     } 
/*      */     
/*  465 */     if (_provider != null) {
/*  466 */       return _provider;
/*      */     }
/*      */     
/*  469 */     _provider = (Provider)this.providersByProtocol.get(protocol);
/*      */ 
/*      */     
/*  472 */     if (_provider == null) {
/*  473 */       throw new NoSuchProviderException("No provider for " + protocol);
/*      */     }
/*  475 */     if (this.logger.isLoggable(Level.FINE)) {
/*  476 */       this.logger.fine("getProvider() returning " + _provider.toString());
/*      */     }
/*  478 */     return _provider;
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
/*      */   public synchronized void setProvider(Provider provider) throws NoSuchProviderException {
/*  493 */     if (provider == null) {
/*  494 */       throw new NoSuchProviderException("Can't set null provider");
/*      */     }
/*  496 */     this.providersByProtocol.put(provider.getProtocol(), provider);
/*  497 */     this.props.put("mail." + provider.getProtocol() + ".class", provider.getClassName());
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
/*      */   public Store getStore() throws NoSuchProviderException {
/*  513 */     return getStore(getProperty("mail.store.protocol"));
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
/*      */   public Store getStore(String protocol) throws NoSuchProviderException {
/*  527 */     return getStore(new URLName(protocol, null, -1, null, null, null));
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
/*      */   public Store getStore(URLName url) throws NoSuchProviderException {
/*  546 */     String protocol = url.getProtocol();
/*  547 */     Provider p = getProvider(protocol);
/*  548 */     return getStore(p, url);
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
/*      */   public Store getStore(Provider provider) throws NoSuchProviderException {
/*  561 */     return getStore(provider, null);
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
/*      */   private Store getStore(Provider provider, URLName url) throws NoSuchProviderException {
/*  581 */     if (provider == null || provider.getType() != Provider.Type.STORE) {
/*  582 */       throw new NoSuchProviderException("invalid provider");
/*      */     }
/*      */     
/*      */     try {
/*  586 */       return (Store)getService(provider, url);
/*  587 */     } catch (ClassCastException cce) {
/*  588 */       throw new NoSuchProviderException("incorrect class");
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
/*      */   public Folder getFolder(URLName url) throws MessagingException {
/*  619 */     Store store = getStore(url);
/*  620 */     store.connect();
/*  621 */     return store.getFolder(url);
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
/*      */   public Transport getTransport() throws NoSuchProviderException {
/*  634 */     return getTransport(getProperty("mail.transport.protocol"));
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
/*      */   public Transport getTransport(String protocol) throws NoSuchProviderException {
/*  648 */     return getTransport(new URLName(protocol, null, -1, null, null, null));
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
/*      */   public Transport getTransport(URLName url) throws NoSuchProviderException {
/*  666 */     String protocol = url.getProtocol();
/*  667 */     Provider p = getProvider(protocol);
/*  668 */     return getTransport(p, url);
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
/*      */   public Transport getTransport(Provider provider) throws NoSuchProviderException {
/*  682 */     return getTransport(provider, null);
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
/*      */   public Transport getTransport(Address address) throws NoSuchProviderException {
/*  699 */     String transportProtocol = getProperty("mail.transport.protocol." + address.getType());
/*      */     
/*  701 */     if (transportProtocol != null)
/*  702 */       return getTransport(transportProtocol); 
/*  703 */     transportProtocol = (String)this.addressMap.get(address.getType());
/*  704 */     if (transportProtocol != null)
/*  705 */       return getTransport(transportProtocol); 
/*  706 */     throw new NoSuchProviderException("No provider for Address type: " + address.getType());
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
/*      */   private Transport getTransport(Provider provider, URLName url) throws NoSuchProviderException {
/*  723 */     if (provider == null || provider.getType() != Provider.Type.TRANSPORT) {
/*  724 */       throw new NoSuchProviderException("invalid provider");
/*      */     }
/*      */     
/*      */     try {
/*  728 */       return (Transport)getService(provider, url);
/*  729 */     } catch (ClassCastException cce) {
/*  730 */       throw new NoSuchProviderException("incorrect class");
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
/*      */   private Object getService(Provider provider, URLName url) throws NoSuchProviderException {
/*      */     ClassLoader cl;
/*  749 */     if (provider == null) {
/*  750 */       throw new NoSuchProviderException("null");
/*      */     }
/*      */ 
/*      */     
/*  754 */     if (url == null) {
/*  755 */       url = new URLName(provider.getProtocol(), null, -1, null, null, null);
/*      */     }
/*      */ 
/*      */     
/*  759 */     Object service = null;
/*      */ 
/*      */ 
/*      */     
/*  763 */     if (this.authenticator != null) {
/*  764 */       cl = this.authenticator.getClass().getClassLoader();
/*      */     } else {
/*  766 */       cl = getClass().getClassLoader();
/*      */     } 
/*      */     
/*  769 */     Class serviceClass = null;
/*      */     
/*      */     try {
/*  772 */       ClassLoader ccl = getContextClassLoader();
/*  773 */       if (ccl != null) {
/*      */         try {
/*  775 */           serviceClass = Class.forName(provider.getClassName(), false, ccl);
/*      */         }
/*  777 */         catch (ClassNotFoundException ex) {}
/*      */       }
/*      */       
/*  780 */       if (serviceClass == null) {
/*  781 */         serviceClass = Class.forName(provider.getClassName(), false, cl);
/*      */       }
/*  783 */     } catch (Exception ex1) {
/*      */ 
/*      */       
/*      */       try {
/*      */         
/*  788 */         serviceClass = Class.forName(provider.getClassName());
/*  789 */       } catch (Exception ex) {
/*      */         
/*  791 */         this.logger.log(Level.FINE, "Exception loading provider", ex);
/*  792 */         throw new NoSuchProviderException(provider.getProtocol());
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/*  798 */       Class[] c = { Session.class, URLName.class };
/*  799 */       Constructor cons = serviceClass.getConstructor(c);
/*      */       
/*  801 */       Object[] o = { this, url };
/*  802 */       service = cons.newInstance(o);
/*      */     }
/*  804 */     catch (Exception ex) {
/*  805 */       this.logger.log(Level.FINE, "Exception loading provider", ex);
/*  806 */       throw new NoSuchProviderException(provider.getProtocol());
/*      */     } 
/*      */     
/*  809 */     return service;
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
/*      */   public void setPasswordAuthentication(URLName url, PasswordAuthentication pw) {
/*  822 */     if (pw == null) {
/*  823 */       this.authTable.remove(url);
/*      */     } else {
/*  825 */       this.authTable.put(url, pw);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PasswordAuthentication getPasswordAuthentication(URLName url) {
/*  835 */     return (PasswordAuthentication)this.authTable.get(url);
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
/*      */   public PasswordAuthentication requestPasswordAuthentication(InetAddress addr, int port, String protocol, String prompt, String defaultUserName) {
/*  861 */     if (this.authenticator != null) {
/*  862 */       return this.authenticator.requestPasswordAuthentication(addr, port, protocol, prompt, defaultUserName);
/*      */     }
/*      */     
/*  865 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Properties getProperties() {
/*  875 */     return this.props;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getProperty(String name) {
/*  885 */     return this.props.getProperty(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void loadProviders(Class cl) {
/*  892 */     StreamLoader loader = new StreamLoader() { private final Session this$0;
/*      */         public void load(InputStream is) throws IOException {
/*  894 */           Session.this.loadProvidersFromStream(is);
/*      */         } }
/*      */       ;
/*      */ 
/*      */     
/*      */     try {
/*  900 */       String res = System.getProperty("java.home") + File.separator + "lib" + File.separator + "javamail.providers";
/*      */ 
/*      */       
/*  903 */       loadFile(res, loader);
/*  904 */     } catch (SecurityException sex) {
/*  905 */       this.logger.log(Level.CONFIG, "can't get java.home", sex);
/*      */     } 
/*      */ 
/*      */     
/*  909 */     loadAllResources("META-INF/javamail.providers", cl, loader);
/*      */ 
/*      */     
/*  912 */     loadResource("/META-INF/javamail.default.providers", cl, loader);
/*      */     
/*  914 */     if (this.providers.size() == 0) {
/*  915 */       this.logger.config("failed to load any providers, using defaults");
/*      */       
/*  917 */       addProvider(new Provider(Provider.Type.STORE, "imap", "com.sun.mail.imap.IMAPStore", "Sun Microsystems, Inc.", "1.4.7"));
/*      */ 
/*      */       
/*  920 */       addProvider(new Provider(Provider.Type.STORE, "imaps", "com.sun.mail.imap.IMAPSSLStore", "Sun Microsystems, Inc.", "1.4.7"));
/*      */ 
/*      */       
/*  923 */       addProvider(new Provider(Provider.Type.STORE, "pop3", "com.sun.mail.pop3.POP3Store", "Sun Microsystems, Inc.", "1.4.7"));
/*      */ 
/*      */       
/*  926 */       addProvider(new Provider(Provider.Type.STORE, "pop3s", "com.sun.mail.pop3.POP3SSLStore", "Sun Microsystems, Inc.", "1.4.7"));
/*      */ 
/*      */       
/*  929 */       addProvider(new Provider(Provider.Type.TRANSPORT, "smtp", "com.sun.mail.smtp.SMTPTransport", "Sun Microsystems, Inc.", "1.4.7"));
/*      */ 
/*      */       
/*  932 */       addProvider(new Provider(Provider.Type.TRANSPORT, "smtps", "com.sun.mail.smtp.SMTPSSLTransport", "Sun Microsystems, Inc.", "1.4.7"));
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  937 */     if (this.logger.isLoggable(Level.CONFIG)) {
/*      */       
/*  939 */       this.logger.config("Tables of loaded providers");
/*  940 */       this.logger.config("Providers Listed By Class Name: " + this.providersByClassName.toString());
/*      */       
/*  942 */       this.logger.config("Providers Listed By Protocol: " + this.providersByProtocol.toString());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void loadProvidersFromStream(InputStream is) throws IOException {
/*  949 */     if (is != null) {
/*  950 */       LineInputStream lis = new LineInputStream(is);
/*      */       
/*      */       String currLine;
/*      */       
/*  954 */       while ((currLine = lis.readLine()) != null) {
/*      */         
/*  956 */         if (currLine.startsWith("#"))
/*      */           continue; 
/*  958 */         Provider.Type type = null;
/*  959 */         String protocol = null, className = null;
/*  960 */         String vendor = null, version = null;
/*      */ 
/*      */         
/*  963 */         StringTokenizer tuples = new StringTokenizer(currLine, ";");
/*  964 */         while (tuples.hasMoreTokens()) {
/*  965 */           String currTuple = tuples.nextToken().trim();
/*      */ 
/*      */           
/*  968 */           int sep = currTuple.indexOf("=");
/*  969 */           if (currTuple.startsWith("protocol=")) {
/*  970 */             protocol = currTuple.substring(sep + 1); continue;
/*  971 */           }  if (currTuple.startsWith("type=")) {
/*  972 */             String strType = currTuple.substring(sep + 1);
/*  973 */             if (strType.equalsIgnoreCase("store")) {
/*  974 */               type = Provider.Type.STORE; continue;
/*  975 */             }  if (strType.equalsIgnoreCase("transport"))
/*  976 */               type = Provider.Type.TRANSPORT;  continue;
/*      */           } 
/*  978 */           if (currTuple.startsWith("class=")) {
/*  979 */             className = currTuple.substring(sep + 1); continue;
/*  980 */           }  if (currTuple.startsWith("vendor=")) {
/*  981 */             vendor = currTuple.substring(sep + 1); continue;
/*  982 */           }  if (currTuple.startsWith("version=")) {
/*  983 */             version = currTuple.substring(sep + 1);
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  988 */         if (type == null || protocol == null || className == null || protocol.length() <= 0 || className.length() <= 0) {
/*      */ 
/*      */           
/*  991 */           this.logger.log(Level.CONFIG, "Bad provider entry: {0}", currLine);
/*      */           
/*      */           continue;
/*      */         } 
/*  995 */         Provider provider = new Provider(type, protocol, className, vendor, version);
/*      */ 
/*      */ 
/*      */         
/*  999 */         addProvider(provider);
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
/*      */   public synchronized void addProvider(Provider provider) {
/* 1011 */     this.providers.addElement(provider);
/* 1012 */     this.providersByClassName.put(provider.getClassName(), provider);
/* 1013 */     if (!this.providersByProtocol.containsKey(provider.getProtocol())) {
/* 1014 */       this.providersByProtocol.put(provider.getProtocol(), provider);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void loadAddressMap(Class cl) {
/* 1020 */     StreamLoader loader = new StreamLoader() { private final Session this$0;
/*      */         public void load(InputStream is) throws IOException {
/* 1022 */           Session.this.addressMap.load(is);
/*      */         } }
/*      */       ;
/*      */ 
/*      */     
/* 1027 */     loadResource("/META-INF/javamail.default.address.map", cl, loader);
/*      */ 
/*      */     
/* 1030 */     loadAllResources("META-INF/javamail.address.map", cl, loader);
/*      */ 
/*      */     
/*      */     try {
/* 1034 */       String res = System.getProperty("java.home") + File.separator + "lib" + File.separator + "javamail.address.map";
/*      */ 
/*      */       
/* 1037 */       loadFile(res, loader);
/* 1038 */     } catch (SecurityException sex) {
/* 1039 */       this.logger.log(Level.CONFIG, "can't get java.home", sex);
/*      */     } 
/*      */     
/* 1042 */     if (this.addressMap.isEmpty()) {
/* 1043 */       this.logger.config("failed to load address map, using defaults");
/* 1044 */       this.addressMap.put("rfc822", "smtp");
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
/*      */   public synchronized void setProtocolForAddress(String addresstype, String protocol) {
/* 1061 */     if (protocol == null) {
/* 1062 */       this.addressMap.remove(addresstype);
/*      */     } else {
/* 1064 */       this.addressMap.put(addresstype, protocol);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void loadFile(String name, StreamLoader loader) {
/* 1071 */     InputStream clis = null;
/*      */     
/* 1073 */     try { clis = new BufferedInputStream(new FileInputStream(name));
/* 1074 */       loader.load(clis);
/* 1075 */       this.logger.log(Level.CONFIG, "successfully loaded file: {0}", name); }
/* 1076 */     catch (FileNotFoundException fex)
/*      */     
/*      */     { 
/*      */       try {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1086 */         if (clis != null)
/* 1087 */           clis.close(); 
/* 1088 */       } catch (IOException ex) {} } catch (IOException e) { if (this.logger.isLoggable(Level.CONFIG)) this.logger.log(Level.CONFIG, "not loading file: " + name, e);  } catch (SecurityException sex) { if (this.logger.isLoggable(Level.CONFIG)) this.logger.log(Level.CONFIG, "not loading file: " + name, sex);  } finally { try { if (clis != null) clis.close();  } catch (IOException ex) {} }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void loadResource(String name, Class cl, StreamLoader loader) {
/* 1096 */     InputStream clis = null;
/*      */     try {
/* 1098 */       clis = getResourceAsStream(cl, name);
/* 1099 */       if (clis != null) {
/* 1100 */         loader.load(clis);
/* 1101 */         this.logger.log(Level.CONFIG, "successfully loaded resource: {0}", name);
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */     
/*      */     }
/* 1108 */     catch (IOException e) {
/* 1109 */       this.logger.log(Level.CONFIG, "Exception loading resource", e);
/* 1110 */     } catch (SecurityException sex) {
/* 1111 */       this.logger.log(Level.CONFIG, "Exception loading resource", sex);
/*      */     } finally {
/*      */       try {
/* 1114 */         if (clis != null)
/* 1115 */           clis.close(); 
/* 1116 */       } catch (IOException ex) {}
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void loadAllResources(String name, Class cl, StreamLoader loader) {
/* 1124 */     boolean anyLoaded = false;
/*      */     try {
/*      */       URL[] urls;
/* 1127 */       ClassLoader cld = null;
/*      */       
/* 1129 */       cld = getContextClassLoader();
/* 1130 */       if (cld == null)
/* 1131 */         cld = cl.getClassLoader(); 
/* 1132 */       if (cld != null) {
/* 1133 */         urls = getResources(cld, name);
/*      */       } else {
/* 1135 */         urls = getSystemResources(name);
/* 1136 */       }  if (urls != null) {
/* 1137 */         for (int i = 0; i < urls.length; i++) {
/* 1138 */           URL url = urls[i];
/* 1139 */           InputStream clis = null;
/* 1140 */           this.logger.log(Level.CONFIG, "URL {0}", url);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 1168 */     catch (Exception ex) {
/* 1169 */       this.logger.log(Level.CONFIG, "Exception loading resource", ex);
/*      */     } 
/*      */ 
/*      */     
/* 1173 */     if (!anyLoaded)
/*      */     {
/*      */ 
/*      */       
/* 1177 */       loadResource("/" + name, cl, loader);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static ClassLoader getContextClassLoader() {
/* 1186 */     return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Object run() {
/* 1189 */             ClassLoader cl = null;
/*      */             try {
/* 1191 */               cl = Thread.currentThread().getContextClassLoader();
/* 1192 */             } catch (SecurityException ex) {}
/* 1193 */             return cl;
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */   
/*      */   private static InputStream getResourceAsStream(final Class c, final String name) throws IOException {
/*      */     try {
/* 1201 */       return AccessController.<InputStream>doPrivileged(new PrivilegedExceptionAction() { private final Class val$c; private final String val$name;
/*      */             
/*      */             public Object run() throws IOException {
/* 1204 */               return c.getResourceAsStream(name);
/*      */             } }
/*      */         );
/* 1207 */     } catch (PrivilegedActionException e) {
/* 1208 */       throw (IOException)e.getException();
/*      */     } 
/*      */   }
/*      */   
/*      */   private static URL[] getResources(final ClassLoader cl, final String name) {
/* 1213 */     return AccessController.<URL[]>doPrivileged(new PrivilegedAction() { private final ClassLoader val$cl; private final String val$name;
/*      */           
/*      */           public Object run() {
/* 1216 */             URL[] ret = null;
/*      */             
/* 1218 */             try { Vector v = new Vector();
/* 1219 */               Enumeration e = cl.getResources(name);
/* 1220 */               while (e != null && e.hasMoreElements()) {
/* 1221 */                 URL url = e.nextElement();
/* 1222 */                 if (url != null)
/* 1223 */                   v.addElement(url); 
/*      */               } 
/* 1225 */               if (v.size() > 0) {
/* 1226 */                 ret = new URL[v.size()];
/* 1227 */                 v.copyInto((Object[])ret);
/*      */               }  }
/* 1229 */             catch (IOException ioex) {  }
/* 1230 */             catch (SecurityException ex) {}
/* 1231 */             return ret;
/*      */           } }
/*      */       );
/*      */   }
/*      */   
/*      */   private static URL[] getSystemResources(final String name) {
/* 1237 */     return AccessController.<URL[]>doPrivileged(new PrivilegedAction() { private final String val$name;
/*      */           
/*      */           public Object run() {
/* 1240 */             URL[] ret = null;
/*      */             
/* 1242 */             try { Vector v = new Vector();
/* 1243 */               Enumeration e = ClassLoader.getSystemResources(name);
/* 1244 */               while (e != null && e.hasMoreElements()) {
/* 1245 */                 URL url = e.nextElement();
/* 1246 */                 if (url != null)
/* 1247 */                   v.addElement(url); 
/*      */               } 
/* 1249 */               if (v.size() > 0) {
/* 1250 */                 ret = new URL[v.size()];
/* 1251 */                 v.copyInto((Object[])ret);
/*      */               }  }
/* 1253 */             catch (IOException ioex) {  }
/* 1254 */             catch (SecurityException ex) {}
/* 1255 */             return ret;
/*      */           } }
/*      */       );
/*      */   }
/*      */   
/*      */   private static InputStream openStream(final URL url) throws IOException {
/*      */     try {
/* 1262 */       return AccessController.<InputStream>doPrivileged(new PrivilegedExceptionAction() { private final URL val$url;
/*      */             
/*      */             public Object run() throws IOException {
/* 1265 */               return url.openStream();
/*      */             } }
/*      */         );
/* 1268 */     } catch (PrivilegedActionException e) {
/* 1269 */       throw (IOException)e.getException();
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Session.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */