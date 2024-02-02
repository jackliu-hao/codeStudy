package com.sun.mail.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.SocketFactory;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SocketFetcher {
   private static MailLogger logger;
   private static final String SOCKS_SUPPORT = "com.sun.mail.util.SocksSupport";

   private SocketFetcher() {
   }

   public static Socket getSocket(String host, int port, Properties props, String prefix, boolean useSSL) throws IOException {
      if (logger.isLoggable(Level.FINER)) {
         logger.finer("getSocket, host " + host + ", port " + port + ", prefix " + prefix + ", useSSL " + useSSL);
      }

      if (prefix == null) {
         prefix = "socket";
      }

      if (props == null) {
         props = new Properties();
      }

      int cto = PropUtil.getIntProperty(props, prefix + ".connectiontimeout", -1);
      Socket socket = null;
      String localaddrstr = props.getProperty(prefix + ".localaddress", (String)null);
      InetAddress localaddr = null;
      if (localaddrstr != null) {
         localaddr = InetAddress.getByName(localaddrstr);
      }

      int localport = PropUtil.getIntProperty(props, prefix + ".localport", 0);
      boolean fb = PropUtil.getBooleanProperty(props, prefix + ".socketFactory.fallback", true);
      int sfPort = -1;
      String sfErr = "unknown socket factory";
      int to = PropUtil.getIntProperty(props, prefix + ".timeout", -1);

      try {
         SocketFactory sf = null;
         String sfPortName = null;
         Object sfo;
         String sfClass;
         if (useSSL) {
            sfo = props.get(prefix + ".ssl.socketFactory");
            if (sfo instanceof SocketFactory) {
               sf = (SocketFactory)sfo;
               (new StringBuffer()).append("SSL socket factory instance ").append(sf).toString();
            }

            if (sf == null) {
               sfClass = props.getProperty(prefix + ".ssl.socketFactory.class");
               sf = getSocketFactory(sfClass);
               (new StringBuffer()).append("SSL socket factory class ").append(sfClass).toString();
            }

            sfPortName = ".ssl.socketFactory.port";
         }

         if (sf == null) {
            sfo = props.get(prefix + ".socketFactory");
            if (sfo instanceof SocketFactory) {
               sf = (SocketFactory)sfo;
               (new StringBuffer()).append("socket factory instance ").append(sf).toString();
            }

            if (sf == null) {
               sfClass = props.getProperty(prefix + ".socketFactory.class");
               sf = getSocketFactory(sfClass);
               (new StringBuffer()).append("socket factory class ").append(sfClass).toString();
            }

            sfPortName = ".socketFactory.port";
         }

         if (sf != null) {
            sfPort = PropUtil.getIntProperty(props, prefix + sfPortName, -1);
            if (sfPort == -1) {
               sfPort = port;
            }

            socket = createSocket(localaddr, localport, host, sfPort, cto, to, props, prefix, sf, useSSL);
         }
      } catch (SocketTimeoutException var18) {
         throw var18;
      } catch (Exception var19) {
         Exception ex = var19;
         if (!fb) {
            if (var19 instanceof InvocationTargetException) {
               Throwable t = ((InvocationTargetException)var19).getTargetException();
               if (t instanceof Exception) {
                  ex = (Exception)t;
               }
            }

            if (ex instanceof IOException) {
               throw (IOException)ex;
            }

            IOException ioex = new IOException("Couldn't connect using " + sfErr + " to host, port: " + host + ", " + sfPort + "; Exception: " + ex);
            ioex.initCause(ex);
            throw ioex;
         }
      }

      if (socket == null) {
         socket = createSocket(localaddr, localport, host, port, cto, to, props, prefix, (SocketFactory)null, useSSL);
      } else if (to >= 0) {
         socket.setSoTimeout(to);
      }

      return socket;
   }

   public static Socket getSocket(String host, int port, Properties props, String prefix) throws IOException {
      return getSocket(host, port, props, prefix, false);
   }

   private static Socket createSocket(InetAddress localaddr, int localport, String host, int port, int cto, int to, Properties props, String prefix, SocketFactory sf, boolean useSSL) throws IOException {
      Socket socket = null;
      String socksHost = props.getProperty(prefix + ".socks.host", (String)null);
      int socksPort = 1080;
      if (socksHost != null) {
         int i = socksHost.indexOf(58);
         if (i >= 0) {
            socksHost = socksHost.substring(0, i);

            try {
               socksPort = Integer.parseInt(socksHost.substring(i + 1));
            } catch (NumberFormatException var20) {
            }
         }

         socksPort = PropUtil.getIntProperty(props, prefix + ".socks.port", socksPort);
         if (logger.isLoggable(Level.FINER)) {
            logger.finer("socks host " + socksHost + ", port " + socksPort);
         }
      }

      if (sf != null) {
         socket = ((SocketFactory)sf).createSocket();
      }

      if (socket == null) {
         if (socksHost != null) {
            try {
               ClassLoader cl = getContextClassLoader();
               Class proxySupport = null;
               if (cl != null) {
                  try {
                     proxySupport = Class.forName("com.sun.mail.util.SocksSupport", false, cl);
                  } catch (Exception var18) {
                  }
               }

               if (proxySupport == null) {
                  proxySupport = Class.forName("com.sun.mail.util.SocksSupport");
               }

               Method mthGetSocket = proxySupport.getMethod("getSocket", String.class, Integer.TYPE);
               socket = (Socket)mthGetSocket.invoke(new Object(), socksHost, new Integer(socksPort));
            } catch (Exception var19) {
               logger.log(Level.FINER, "failed to load ProxySupport class", (Throwable)var19);
            }
         }

         if (socket == null) {
            socket = new Socket();
         }
      }

      if (to >= 0) {
         socket.setSoTimeout(to);
      }

      if (localaddr != null) {
         socket.bind(new InetSocketAddress(localaddr, localport));
      }

      if (cto >= 0) {
         socket.connect(new InetSocketAddress(host, port), cto);
      } else {
         socket.connect(new InetSocketAddress(host, port));
      }

      if (useSSL && !(socket instanceof SSLSocket)) {
         String trusted;
         Object ssf;
         if ((trusted = props.getProperty(prefix + ".ssl.trust")) != null) {
            try {
               MailSSLSocketFactory msf = new MailSSLSocketFactory();
               if (trusted.equals("*")) {
                  msf.setTrustAllHosts(true);
               } else {
                  msf.setTrustedHosts(trusted.split("\\s+"));
               }

               ssf = msf;
            } catch (GeneralSecurityException var17) {
               IOException ioex = new IOException("Can't create MailSSLSocketFactory");
               ioex.initCause(var17);
               throw ioex;
            }
         } else {
            ssf = (SSLSocketFactory)SSLSocketFactory.getDefault();
         }

         socket = ((SSLSocketFactory)ssf).createSocket(socket, host, port, true);
         sf = ssf;
      }

      configureSSLSocket(socket, host, props, prefix, (SocketFactory)sf);
      return socket;
   }

   private static SocketFactory getSocketFactory(String sfClass) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      if (sfClass != null && sfClass.length() != 0) {
         ClassLoader cl = getContextClassLoader();
         Class clsSockFact = null;
         if (cl != null) {
            try {
               clsSockFact = Class.forName(sfClass, false, cl);
            } catch (ClassNotFoundException var5) {
            }
         }

         if (clsSockFact == null) {
            clsSockFact = Class.forName(sfClass);
         }

         Method mthGetDefault = clsSockFact.getMethod("getDefault");
         SocketFactory sf = (SocketFactory)mthGetDefault.invoke(new Object());
         return sf;
      } else {
         return null;
      }
   }

   public static Socket startTLS(Socket socket) throws IOException {
      return startTLS(socket, new Properties(), "socket");
   }

   public static Socket startTLS(Socket socket, Properties props, String prefix) throws IOException {
      InetAddress a = socket.getInetAddress();
      String host = a.getHostName();
      return startTLS(socket, host, props, prefix);
   }

   public static Socket startTLS(Socket socket, String host, Properties props, String prefix) throws IOException {
      int port = socket.getPort();
      if (logger.isLoggable(Level.FINER)) {
         logger.finer("startTLS host " + host + ", port " + port);
      }

      String sfErr = "unknown socket factory";

      try {
         SSLSocketFactory ssf = null;
         SocketFactory sf = null;
         Object sfo = props.get(prefix + ".ssl.socketFactory");
         if (sfo instanceof SocketFactory) {
            sf = (SocketFactory)sfo;
            (new StringBuffer()).append("SSL socket factory instance ").append(sf).toString();
         }

         String trusted;
         if (sf == null) {
            trusted = props.getProperty(prefix + ".ssl.socketFactory.class");
            sf = getSocketFactory(trusted);
            (new StringBuffer()).append("SSL socket factory class ").append(trusted).toString();
         }

         if (sf != null && sf instanceof SSLSocketFactory) {
            ssf = (SSLSocketFactory)sf;
         }

         if (ssf == null) {
            sfo = props.get(prefix + ".socketFactory");
            if (sfo instanceof SocketFactory) {
               sf = (SocketFactory)sfo;
               (new StringBuffer()).append("socket factory instance ").append(sf).toString();
            }

            if (sf == null) {
               trusted = props.getProperty(prefix + ".socketFactory.class");
               sf = getSocketFactory(trusted);
               (new StringBuffer()).append("socket factory class ").append(trusted).toString();
            }

            if (sf != null && sf instanceof SSLSocketFactory) {
               ssf = (SSLSocketFactory)sf;
            }
         }

         if (ssf == null) {
            if ((trusted = props.getProperty(prefix + ".ssl.trust")) != null) {
               try {
                  MailSSLSocketFactory msf = new MailSSLSocketFactory();
                  if (trusted.equals("*")) {
                     msf.setTrustAllHosts(true);
                  } else {
                     msf.setTrustedHosts(trusted.split("\\s+"));
                  }

                  ssf = msf;
                  sfErr = "mail SSL socket factory";
               } catch (GeneralSecurityException var12) {
                  IOException ioex = new IOException("Can't create MailSSLSocketFactory");
                  ioex.initCause(var12);
                  throw ioex;
               }
            } else {
               ssf = (SSLSocketFactory)SSLSocketFactory.getDefault();
               sfErr = "default SSL socket factory";
            }
         }

         socket = ((SSLSocketFactory)ssf).createSocket(socket, host, port, true);
         configureSSLSocket(socket, host, props, prefix, (SocketFactory)ssf);
         return socket;
      } catch (Exception var13) {
         Exception ex = var13;
         if (var13 instanceof InvocationTargetException) {
            Throwable t = ((InvocationTargetException)var13).getTargetException();
            if (t instanceof Exception) {
               ex = (Exception)t;
            }
         }

         if (ex instanceof IOException) {
            throw (IOException)ex;
         } else {
            IOException ioex = new IOException("Exception in startTLS using " + sfErr + ": host, port: " + host + ", " + port + "; Exception: " + ex);
            ioex.initCause(ex);
            throw ioex;
         }
      }
   }

   private static void configureSSLSocket(Socket socket, String host, Properties props, String prefix, SocketFactory sf) throws IOException {
      if (socket instanceof SSLSocket) {
         SSLSocket sslsocket = (SSLSocket)socket;
         String protocols = props.getProperty(prefix + ".ssl.protocols", (String)null);
         if (protocols != null) {
            sslsocket.setEnabledProtocols(stringArray(protocols));
         } else {
            sslsocket.setEnabledProtocols(new String[]{"TLSv1"});
         }

         String ciphers = props.getProperty(prefix + ".ssl.ciphersuites", (String)null);
         if (ciphers != null) {
            sslsocket.setEnabledCipherSuites(stringArray(ciphers));
         }

         if (logger.isLoggable(Level.FINER)) {
            logger.finer("SSL protocols after " + Arrays.asList(sslsocket.getEnabledProtocols()));
            logger.finer("SSL ciphers after " + Arrays.asList(sslsocket.getEnabledCipherSuites()));
         }

         sslsocket.startHandshake();
         boolean idCheck = PropUtil.getBooleanProperty(props, prefix + ".ssl.checkserveridentity", false);
         if (idCheck) {
            checkServerIdentity(host, sslsocket);
         }

         if (sf instanceof MailSSLSocketFactory) {
            MailSSLSocketFactory msf = (MailSSLSocketFactory)sf;
            if (!msf.isServerTrusted(host, sslsocket)) {
               try {
                  sslsocket.close();
               } finally {
                  throw new IOException("Server is not trusted: " + host);
               }

               throw new IOException("Server is not trusted: " + host);
            }
         }

      }
   }

   private static void checkServerIdentity(String server, SSLSocket sslSocket) throws IOException {
      try {
         Certificate[] certChain = sslSocket.getSession().getPeerCertificates();
         if (certChain != null && certChain.length > 0 && certChain[0] instanceof X509Certificate && matchCert(server, (X509Certificate)certChain[0])) {
            return;
         }
      } catch (SSLPeerUnverifiedException var4) {
         sslSocket.close();
         IOException ioex = new IOException("Can't verify identity of server: " + server);
         ioex.initCause(var4);
         throw ioex;
      }

      sslSocket.close();
      throw new IOException("Can't verify identity of server: " + server);
   }

   private static boolean matchCert(String server, X509Certificate cert) {
      if (logger.isLoggable(Level.FINER)) {
         logger.finer("matchCert server " + server + ", cert " + cert);
      }

      try {
         Class hnc = Class.forName("sun.security.util.HostnameChecker");
         Method getInstance = hnc.getMethod("getInstance", Byte.TYPE);
         Object hostnameChecker = getInstance.invoke(new Object(), new Byte((byte)2));
         if (logger.isLoggable(Level.FINER)) {
            logger.finer("using sun.security.util.HostnameChecker");
         }

         Method match = hnc.getMethod("match", String.class, X509Certificate.class);

         try {
            match.invoke(hostnameChecker, server, cert);
            return true;
         } catch (InvocationTargetException var8) {
            logger.log(Level.FINER, "FAIL", (Throwable)var8);
            return false;
         }
      } catch (Exception var10) {
         logger.log(Level.FINER, "NO sun.security.util.HostnameChecker", (Throwable)var10);

         try {
            Collection names = cert.getSubjectAlternativeNames();
            if (names != null) {
               boolean foundName = false;
               Iterator it = names.iterator();

               while(it.hasNext()) {
                  List nameEnt = (List)it.next();
                  Integer type = (Integer)nameEnt.get(0);
                  if (type == 2) {
                     foundName = true;
                     String name = (String)nameEnt.get(1);
                     if (logger.isLoggable(Level.FINER)) {
                        logger.finer("found name: " + name);
                     }

                     if (matchServer(server, name)) {
                        return true;
                     }
                  }
               }

               if (foundName) {
                  return false;
               }
            }
         } catch (CertificateParsingException var9) {
         }

         Pattern p = Pattern.compile("CN=([^,]*)");
         Matcher m = p.matcher(cert.getSubjectX500Principal().getName());
         return m.find() && matchServer(server, m.group(1).trim());
      }
   }

   private static boolean matchServer(String server, String name) {
      if (logger.isLoggable(Level.FINER)) {
         logger.finer("match server " + server + " with " + name);
      }

      if (!name.startsWith("*.")) {
         return server.equalsIgnoreCase(name);
      } else {
         String tail = name.substring(2);
         if (tail.length() == 0) {
            return false;
         } else {
            int off = server.length() - tail.length();
            if (off < 1) {
               return false;
            } else {
               return server.charAt(off - 1) == '.' && server.regionMatches(true, off, tail, 0, tail.length());
            }
         }
      }
   }

   private static String[] stringArray(String s) {
      StringTokenizer st = new StringTokenizer(s);
      List tokens = new ArrayList();

      while(st.hasMoreTokens()) {
         tokens.add(st.nextToken());
      }

      return (String[])((String[])tokens.toArray(new String[tokens.size()]));
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

   static {
      logger = new MailLogger(SocketFetcher.class, "socket", "DEBUG SocketFetcher", PropUtil.getBooleanSystemProperty("mail.socket.debug", false), System.out);
   }
}
