/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.security.AccessController;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateParsingException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.logging.Level;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.net.SocketFactory;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.SSLSocketFactory;
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
/*     */ public class SocketFetcher
/*     */ {
/*  66 */   private static MailLogger logger = new MailLogger(SocketFetcher.class, "socket", "DEBUG SocketFetcher", PropUtil.getBooleanSystemProperty("mail.socket.debug", false), System.out);
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
/*     */   private static final String SOCKS_SUPPORT = "com.sun.mail.util.SocksSupport";
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
/*     */   public static Socket getSocket(String host, int port, Properties props, String prefix, boolean useSSL) throws IOException {
/* 143 */     if (logger.isLoggable(Level.FINER)) {
/* 144 */       logger.finer("getSocket, host " + host + ", port " + port + ", prefix " + prefix + ", useSSL " + useSSL);
/*     */     }
/* 146 */     if (prefix == null)
/* 147 */       prefix = "socket"; 
/* 148 */     if (props == null)
/* 149 */       props = new Properties(); 
/* 150 */     int cto = PropUtil.getIntProperty(props, prefix + ".connectiontimeout", -1);
/*     */     
/* 152 */     Socket socket = null;
/* 153 */     String localaddrstr = props.getProperty(prefix + ".localaddress", null);
/* 154 */     InetAddress localaddr = null;
/* 155 */     if (localaddrstr != null)
/* 156 */       localaddr = InetAddress.getByName(localaddrstr); 
/* 157 */     int localport = PropUtil.getIntProperty(props, prefix + ".localport", 0);
/*     */ 
/*     */     
/* 160 */     boolean fb = PropUtil.getBooleanProperty(props, prefix + ".socketFactory.fallback", true);
/*     */ 
/*     */     
/* 163 */     int sfPort = -1;
/* 164 */     String sfErr = "unknown socket factory";
/* 165 */     int to = PropUtil.getIntProperty(props, prefix + ".timeout", -1);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 171 */       SocketFactory sf = null;
/* 172 */       String sfPortName = null;
/* 173 */       if (useSSL) {
/* 174 */         Object sfo = props.get(prefix + ".ssl.socketFactory");
/* 175 */         if (sfo instanceof SocketFactory) {
/* 176 */           sf = (SocketFactory)sfo;
/* 177 */           sfErr = "SSL socket factory instance " + sf;
/*     */         } 
/* 179 */         if (sf == null) {
/* 180 */           String sfClass = props.getProperty(prefix + ".ssl.socketFactory.class");
/*     */           
/* 182 */           sf = getSocketFactory(sfClass);
/* 183 */           sfErr = "SSL socket factory class " + sfClass;
/*     */         } 
/* 185 */         sfPortName = ".ssl.socketFactory.port";
/*     */       } 
/*     */       
/* 188 */       if (sf == null) {
/* 189 */         Object sfo = props.get(prefix + ".socketFactory");
/* 190 */         if (sfo instanceof SocketFactory) {
/* 191 */           sf = (SocketFactory)sfo;
/* 192 */           sfErr = "socket factory instance " + sf;
/*     */         } 
/* 194 */         if (sf == null) {
/* 195 */           String sfClass = props.getProperty(prefix + ".socketFactory.class");
/*     */           
/* 197 */           sf = getSocketFactory(sfClass);
/* 198 */           sfErr = "socket factory class " + sfClass;
/*     */         } 
/* 200 */         sfPortName = ".socketFactory.port";
/*     */       } 
/*     */ 
/*     */       
/* 204 */       if (sf != null) {
/* 205 */         sfPort = PropUtil.getIntProperty(props, prefix + sfPortName, -1);
/*     */ 
/*     */ 
/*     */         
/* 209 */         if (sfPort == -1)
/* 210 */           sfPort = port; 
/* 211 */         socket = createSocket(localaddr, localport, host, sfPort, cto, to, props, prefix, sf, useSSL);
/*     */       }
/*     */     
/* 214 */     } catch (SocketTimeoutException sex) {
/* 215 */       throw sex;
/* 216 */     } catch (Exception ex) {
/* 217 */       if (!fb) {
/* 218 */         if (ex instanceof InvocationTargetException) {
/* 219 */           Throwable t = ((InvocationTargetException)ex).getTargetException();
/*     */           
/* 221 */           if (t instanceof Exception)
/* 222 */             ex = (Exception)t; 
/*     */         } 
/* 224 */         if (ex instanceof IOException)
/* 225 */           throw (IOException)ex; 
/* 226 */         IOException ioex = new IOException("Couldn't connect using " + sfErr + " to host, port: " + host + ", " + sfPort + "; Exception: " + ex);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 231 */         ioex.initCause(ex);
/* 232 */         throw ioex;
/*     */       } 
/*     */     } 
/*     */     
/* 236 */     if (socket == null) {
/* 237 */       socket = createSocket(localaddr, localport, host, port, cto, to, props, prefix, null, useSSL);
/*     */ 
/*     */     
/*     */     }
/* 241 */     else if (to >= 0) {
/* 242 */       socket.setSoTimeout(to);
/*     */     } 
/*     */     
/* 245 */     return socket;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Socket getSocket(String host, int port, Properties props, String prefix) throws IOException {
/* 250 */     return getSocket(host, port, props, prefix, false);
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
/*     */   private static Socket createSocket(InetAddress localaddr, int localport, String host, int port, int cto, int to, Properties props, String prefix, SocketFactory sf, boolean useSSL) throws IOException {
/* 265 */     Socket socket = null;
/*     */     
/* 267 */     String socksHost = props.getProperty(prefix + ".socks.host", null);
/* 268 */     int socksPort = 1080;
/* 269 */     if (socksHost != null) {
/* 270 */       int i = socksHost.indexOf(':');
/* 271 */       if (i >= 0) {
/* 272 */         socksHost = socksHost.substring(0, i);
/*     */         try {
/* 274 */           socksPort = Integer.parseInt(socksHost.substring(i + 1));
/* 275 */         } catch (NumberFormatException ex) {}
/*     */       } 
/*     */ 
/*     */       
/* 279 */       socksPort = PropUtil.getIntProperty(props, prefix + ".socks.port", socksPort);
/*     */       
/* 281 */       if (logger.isLoggable(Level.FINER)) {
/* 282 */         logger.finer("socks host " + socksHost + ", port " + socksPort);
/*     */       }
/*     */     } 
/* 285 */     if (sf != null)
/* 286 */       socket = sf.createSocket(); 
/* 287 */     if (socket == null) {
/* 288 */       if (socksHost != null) {
/*     */         try {
/* 290 */           ClassLoader cl = getContextClassLoader();
/* 291 */           Class proxySupport = null;
/* 292 */           if (cl != null) {
/*     */             try {
/* 294 */               proxySupport = Class.forName("com.sun.mail.util.SocksSupport", false, cl);
/*     */             }
/* 296 */             catch (Exception cex) {}
/*     */           }
/* 298 */           if (proxySupport == null) {
/* 299 */             proxySupport = Class.forName("com.sun.mail.util.SocksSupport");
/*     */           }
/* 301 */           Method mthGetSocket = proxySupport.getMethod("getSocket", new Class[] { String.class, int.class });
/*     */           
/* 303 */           socket = (Socket)mthGetSocket.invoke(new Object(), new Object[] { socksHost, new Integer(socksPort) });
/*     */         }
/* 305 */         catch (Exception ex) {
/*     */           
/* 307 */           logger.log(Level.FINER, "failed to load ProxySupport class", ex);
/*     */         } 
/*     */       }
/*     */       
/* 311 */       if (socket == null)
/* 312 */         socket = new Socket(); 
/*     */     } 
/* 314 */     if (to >= 0)
/* 315 */       socket.setSoTimeout(to); 
/* 316 */     if (localaddr != null)
/* 317 */       socket.bind(new InetSocketAddress(localaddr, localport)); 
/* 318 */     if (cto >= 0) {
/* 319 */       socket.connect(new InetSocketAddress(host, port), cto);
/*     */     } else {
/* 321 */       socket.connect(new InetSocketAddress(host, port));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 327 */     if (useSSL && !(socket instanceof SSLSocket)) {
/*     */       SSLSocketFactory sSLSocketFactory;
/*     */       String trusted;
/* 330 */       if ((trusted = props.getProperty(prefix + ".ssl.trust")) != null) {
/*     */         try {
/* 332 */           MailSSLSocketFactory msf = new MailSSLSocketFactory();
/* 333 */           if (trusted.equals("*")) {
/* 334 */             msf.setTrustAllHosts(true);
/*     */           } else {
/* 336 */             msf.setTrustedHosts(trusted.split("\\s+"));
/* 337 */           }  sSLSocketFactory = msf;
/* 338 */         } catch (GeneralSecurityException gex) {
/* 339 */           IOException ioex = new IOException("Can't create MailSSLSocketFactory");
/*     */           
/* 341 */           ioex.initCause(gex);
/* 342 */           throw ioex;
/*     */         } 
/*     */       } else {
/* 345 */         sSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
/* 346 */       }  socket = sSLSocketFactory.createSocket(socket, host, port, true);
/* 347 */       sf = sSLSocketFactory;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 354 */     configureSSLSocket(socket, host, props, prefix, sf);
/*     */     
/* 356 */     return socket;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static SocketFactory getSocketFactory(String sfClass) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
/* 367 */     if (sfClass == null || sfClass.length() == 0) {
/* 368 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 372 */     ClassLoader cl = getContextClassLoader();
/* 373 */     Class clsSockFact = null;
/* 374 */     if (cl != null) {
/*     */       try {
/* 376 */         clsSockFact = Class.forName(sfClass, false, cl);
/* 377 */       } catch (ClassNotFoundException cex) {}
/*     */     }
/* 379 */     if (clsSockFact == null) {
/* 380 */       clsSockFact = Class.forName(sfClass);
/*     */     }
/* 382 */     Method mthGetDefault = clsSockFact.getMethod("getDefault", new Class[0]);
/*     */     
/* 384 */     SocketFactory sf = (SocketFactory)mthGetDefault.invoke(new Object(), new Object[0]);
/*     */     
/* 386 */     return sf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Socket startTLS(Socket socket) throws IOException {
/* 396 */     return startTLS(socket, new Properties(), "socket");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Socket startTLS(Socket socket, Properties props, String prefix) throws IOException {
/* 407 */     InetAddress a = socket.getInetAddress();
/* 408 */     String host = a.getHostName();
/* 409 */     return startTLS(socket, host, props, prefix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Socket startTLS(Socket socket, String host, Properties props, String prefix) throws IOException {
/* 418 */     int port = socket.getPort();
/* 419 */     if (logger.isLoggable(Level.FINER)) {
/* 420 */       logger.finer("startTLS host " + host + ", port " + port);
/*     */     }
/* 422 */     String sfErr = "unknown socket factory";
/*     */     try {
/* 424 */       SSLSocketFactory ssf = null;
/* 425 */       SocketFactory sf = null;
/*     */ 
/*     */       
/* 428 */       Object sfo = props.get(prefix + ".ssl.socketFactory");
/* 429 */       if (sfo instanceof SocketFactory) {
/* 430 */         sf = (SocketFactory)sfo;
/* 431 */         sfErr = "SSL socket factory instance " + sf;
/*     */       } 
/* 433 */       if (sf == null) {
/* 434 */         String sfClass = props.getProperty(prefix + ".ssl.socketFactory.class");
/*     */         
/* 436 */         sf = getSocketFactory(sfClass);
/* 437 */         sfErr = "SSL socket factory class " + sfClass;
/*     */       } 
/* 439 */       if (sf != null && sf instanceof SSLSocketFactory) {
/* 440 */         ssf = (SSLSocketFactory)sf;
/*     */       }
/*     */ 
/*     */       
/* 444 */       if (ssf == null) {
/* 445 */         sfo = props.get(prefix + ".socketFactory");
/* 446 */         if (sfo instanceof SocketFactory) {
/* 447 */           sf = (SocketFactory)sfo;
/* 448 */           sfErr = "socket factory instance " + sf;
/*     */         } 
/* 450 */         if (sf == null) {
/* 451 */           String sfClass = props.getProperty(prefix + ".socketFactory.class");
/*     */           
/* 453 */           sf = getSocketFactory(sfClass);
/* 454 */           sfErr = "socket factory class " + sfClass;
/*     */         } 
/* 456 */         if (sf != null && sf instanceof SSLSocketFactory) {
/* 457 */           ssf = (SSLSocketFactory)sf;
/*     */         }
/*     */       } 
/*     */       
/* 461 */       if (ssf == null) {
/*     */         String trusted;
/* 463 */         if ((trusted = props.getProperty(prefix + ".ssl.trust")) != null) {
/*     */           
/*     */           try {
/* 466 */             MailSSLSocketFactory msf = new MailSSLSocketFactory();
/* 467 */             if (trusted.equals("*")) {
/* 468 */               msf.setTrustAllHosts(true);
/*     */             } else {
/* 470 */               msf.setTrustedHosts(trusted.split("\\s+"));
/* 471 */             }  ssf = msf;
/* 472 */             sfErr = "mail SSL socket factory";
/* 473 */           } catch (GeneralSecurityException gex) {
/* 474 */             IOException ioex = new IOException("Can't create MailSSLSocketFactory");
/*     */             
/* 476 */             ioex.initCause(gex);
/* 477 */             throw ioex;
/*     */           } 
/*     */         } else {
/* 480 */           ssf = (SSLSocketFactory)SSLSocketFactory.getDefault();
/* 481 */           sfErr = "default SSL socket factory";
/*     */         } 
/*     */       } 
/*     */       
/* 485 */       socket = ssf.createSocket(socket, host, port, true);
/* 486 */       configureSSLSocket(socket, host, props, prefix, ssf);
/* 487 */     } catch (Exception ex) {
/* 488 */       if (ex instanceof InvocationTargetException) {
/* 489 */         Throwable t = ((InvocationTargetException)ex).getTargetException();
/*     */         
/* 491 */         if (t instanceof Exception)
/* 492 */           ex = (Exception)t; 
/*     */       } 
/* 494 */       if (ex instanceof IOException) {
/* 495 */         throw (IOException)ex;
/*     */       }
/* 497 */       IOException ioex = new IOException("Exception in startTLS using " + sfErr + ": host, port: " + host + ", " + port + "; Exception: " + ex);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 502 */       ioex.initCause(ex);
/* 503 */       throw ioex;
/*     */     } 
/* 505 */     return socket;
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
/*     */   private static void configureSSLSocket(Socket socket, String host, Properties props, String prefix, SocketFactory sf) throws IOException {
/* 518 */     if (!(socket instanceof SSLSocket))
/*     */       return; 
/* 520 */     SSLSocket sslsocket = (SSLSocket)socket;
/*     */     
/* 522 */     String protocols = props.getProperty(prefix + ".ssl.protocols", null);
/* 523 */     if (protocols != null) {
/* 524 */       sslsocket.setEnabledProtocols(stringArray(protocols));
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */ 
/*     */       
/* 532 */       sslsocket.setEnabledProtocols(new String[] { "TLSv1" });
/*     */     } 
/* 534 */     String ciphers = props.getProperty(prefix + ".ssl.ciphersuites", null);
/* 535 */     if (ciphers != null)
/* 536 */       sslsocket.setEnabledCipherSuites(stringArray(ciphers)); 
/* 537 */     if (logger.isLoggable(Level.FINER)) {
/* 538 */       logger.finer("SSL protocols after " + Arrays.<String>asList(sslsocket.getEnabledProtocols()));
/*     */       
/* 540 */       logger.finer("SSL ciphers after " + Arrays.<String>asList(sslsocket.getEnabledCipherSuites()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 549 */     sslsocket.startHandshake();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 554 */     boolean idCheck = PropUtil.getBooleanProperty(props, prefix + ".ssl.checkserveridentity", false);
/*     */     
/* 556 */     if (idCheck)
/* 557 */       checkServerIdentity(host, sslsocket); 
/* 558 */     if (sf instanceof MailSSLSocketFactory) {
/* 559 */       MailSSLSocketFactory msf = (MailSSLSocketFactory)sf;
/* 560 */       if (!msf.isServerTrusted(host, sslsocket)) {
/*     */         try {
/* 562 */           sslsocket.close();
/*     */         } finally {
/* 564 */           throw new IOException("Server is not trusted: " + host);
/*     */         } 
/*     */       }
/*     */     } 
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
/*     */   private static void checkServerIdentity(String server, SSLSocket sslSocket) throws IOException {
/*     */     try {
/* 583 */       Certificate[] certChain = sslSocket.getSession().getPeerCertificates();
/*     */       
/* 585 */       if (certChain != null && certChain.length > 0 && certChain[0] instanceof X509Certificate && matchCert(server, (X509Certificate)certChain[0])) {
/*     */         return;
/*     */       }
/*     */     }
/* 589 */     catch (SSLPeerUnverifiedException e) {
/* 590 */       sslSocket.close();
/* 591 */       IOException ioex = new IOException("Can't verify identity of server: " + server);
/*     */       
/* 593 */       ioex.initCause(e);
/* 594 */       throw ioex;
/*     */     } 
/*     */ 
/*     */     
/* 598 */     sslSocket.close();
/* 599 */     throw new IOException("Can't verify identity of server: " + server);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean matchCert(String server, X509Certificate cert) {
/* 610 */     if (logger.isLoggable(Level.FINER)) {
/* 611 */       logger.finer("matchCert server " + server + ", cert " + cert);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 621 */       Class hnc = Class.forName("sun.security.util.HostnameChecker");
/*     */ 
/*     */ 
/*     */       
/* 625 */       Method getInstance = hnc.getMethod("getInstance", new Class[] { byte.class });
/*     */       
/* 627 */       Object hostnameChecker = getInstance.invoke(new Object(), new Object[] { new Byte((byte)2) });
/*     */ 
/*     */ 
/*     */       
/* 631 */       if (logger.isLoggable(Level.FINER))
/* 632 */         logger.finer("using sun.security.util.HostnameChecker"); 
/* 633 */       Method match = hnc.getMethod("match", new Class[] { String.class, X509Certificate.class });
/*     */       
/*     */       try {
/* 636 */         match.invoke(hostnameChecker, new Object[] { server, cert });
/* 637 */         return true;
/* 638 */       } catch (InvocationTargetException cex) {
/* 639 */         logger.log(Level.FINER, "FAIL", cex);
/* 640 */         return false;
/*     */       } 
/* 642 */     } catch (Exception ex) {
/* 643 */       logger.log(Level.FINER, "NO sun.security.util.HostnameChecker", ex);
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
/*     */       try {
/* 657 */         Collection names = cert.getSubjectAlternativeNames();
/* 658 */         if (names != null) {
/* 659 */           boolean foundName = false;
/* 660 */           for (Iterator it = names.iterator(); it.hasNext(); ) {
/* 661 */             List nameEnt = (List)it.next();
/* 662 */             Integer type = nameEnt.get(0);
/* 663 */             if (type.intValue() == 2) {
/* 664 */               foundName = true;
/* 665 */               String name = (String)nameEnt.get(1);
/* 666 */               if (logger.isLoggable(Level.FINER))
/* 667 */                 logger.finer("found name: " + name); 
/* 668 */               if (matchServer(server, name))
/* 669 */                 return true; 
/*     */             } 
/*     */           } 
/* 672 */           if (foundName)
/* 673 */             return false; 
/*     */         } 
/* 675 */       } catch (CertificateParsingException certificateParsingException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 681 */       Pattern p = Pattern.compile("CN=([^,]*)");
/* 682 */       Matcher m = p.matcher(cert.getSubjectX500Principal().getName());
/* 683 */       if (m.find() && matchServer(server, m.group(1).trim())) {
/* 684 */         return true;
/*     */       }
/* 686 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean matchServer(String server, String name) {
/* 697 */     if (logger.isLoggable(Level.FINER))
/* 698 */       logger.finer("match server " + server + " with " + name); 
/* 699 */     if (name.startsWith("*.")) {
/*     */       
/* 701 */       String tail = name.substring(2);
/* 702 */       if (tail.length() == 0)
/* 703 */         return false; 
/* 704 */       int off = server.length() - tail.length();
/* 705 */       if (off < 1) {
/* 706 */         return false;
/*     */       }
/* 708 */       return (server.charAt(off - 1) == '.' && server.regionMatches(true, off, tail, 0, tail.length()));
/*     */     } 
/*     */     
/* 711 */     return server.equalsIgnoreCase(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String[] stringArray(String s) {
/* 719 */     StringTokenizer st = new StringTokenizer(s);
/* 720 */     List tokens = new ArrayList();
/* 721 */     while (st.hasMoreTokens())
/* 722 */       tokens.add(st.nextToken()); 
/* 723 */     return tokens.<String>toArray(new String[tokens.size()]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ClassLoader getContextClassLoader() {
/* 732 */     return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/* 735 */             ClassLoader cl = null;
/*     */             try {
/* 737 */               cl = Thread.currentThread().getContextClassLoader();
/* 738 */             } catch (SecurityException ex) {}
/* 739 */             return cl;
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\SocketFetcher.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */