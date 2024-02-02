/*     */ package org.apache.http.conn.ssl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.net.SocketFactory;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLHandshakeException;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
/*     */ import org.apache.http.conn.util.PublicSuffixMatcherLoader;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.ssl.SSLContexts;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.TextUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class SSLConnectionSocketFactory
/*     */   implements LayeredConnectionSocketFactory
/*     */ {
/*     */   public static final String TLS = "TLS";
/*     */   public static final String SSL = "SSL";
/*     */   public static final String SSLV2 = "SSLv2";
/*     */   @Deprecated
/* 151 */   public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = AllowAllHostnameVerifier.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 158 */   public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = BrowserCompatHostnameVerifier.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 165 */   public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER = StrictHostnameVerifier.INSTANCE;
/*     */ 
/*     */   
/*     */   private static final String WEAK_KEY_EXCHANGES = "^(TLS|SSL)_(NULL|ECDH_anon|DH_anon|DH_anon_EXPORT|DHE_RSA_EXPORT|DHE_DSS_EXPORT|DSS_EXPORT|DH_DSS_EXPORT|DH_RSA_EXPORT|RSA_EXPORT|KRB5_EXPORT)_(.*)";
/*     */ 
/*     */   
/*     */   private static final String WEAK_CIPHERS = "^(TLS|SSL)_(.*)_WITH_(NULL|DES_CBC|DES40_CBC|DES_CBC_40|3DES_EDE_CBC|RC4_128|RC4_40|RC2_CBC_40)_(.*)";
/*     */   
/* 173 */   private static final List<Pattern> WEAK_CIPHER_SUITE_PATTERNS = Collections.unmodifiableList(Arrays.asList(new Pattern[] { Pattern.compile("^(TLS|SSL)_(NULL|ECDH_anon|DH_anon|DH_anon_EXPORT|DHE_RSA_EXPORT|DHE_DSS_EXPORT|DSS_EXPORT|DH_DSS_EXPORT|DH_RSA_EXPORT|RSA_EXPORT|KRB5_EXPORT)_(.*)", 2), Pattern.compile("^(TLS|SSL)_(.*)_WITH_(NULL|DES_CBC|DES40_CBC|DES_CBC_40|3DES_EDE_CBC|RC4_128|RC4_40|RC2_CBC_40)_(.*)", 2) }));
/*     */ 
/*     */ 
/*     */   
/* 177 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*     */   private final SSLSocketFactory socketfactory;
/*     */   private final HostnameVerifier hostnameVerifier;
/*     */   
/*     */   public static HostnameVerifier getDefaultHostnameVerifier() {
/* 183 */     return new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] supportedProtocols;
/*     */   
/*     */   private final String[] supportedCipherSuites;
/*     */ 
/*     */   
/*     */   public static SSLConnectionSocketFactory getSocketFactory() throws SSLInitializationException {
/* 194 */     return new SSLConnectionSocketFactory(SSLContexts.createDefault(), getDefaultHostnameVerifier());
/*     */   }
/*     */   
/*     */   static boolean isWeakCipherSuite(String cipherSuite) {
/* 198 */     for (Pattern pattern : WEAK_CIPHER_SUITE_PATTERNS) {
/* 199 */       if (pattern.matcher(cipherSuite).matches()) {
/* 200 */         return true;
/*     */       }
/*     */     } 
/* 203 */     return false;
/*     */   }
/*     */   
/*     */   private static String[] split(String s) {
/* 207 */     if (TextUtils.isBlank(s)) {
/* 208 */       return null;
/*     */     }
/* 210 */     return s.split(" *, *");
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
/*     */   public static SSLConnectionSocketFactory getSystemSocketFactory() throws SSLInitializationException {
/* 222 */     return new SSLConnectionSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault(), split(System.getProperty("https.protocols")), split(System.getProperty("https.cipherSuites")), getDefaultHostnameVerifier());
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
/*     */   public SSLConnectionSocketFactory(SSLContext sslContext) {
/* 235 */     this(sslContext, getDefaultHostnameVerifier());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public SSLConnectionSocketFactory(SSLContext sslContext, X509HostnameVerifier hostnameVerifier) {
/* 245 */     this(((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), (String[])null, (String[])null, hostnameVerifier);
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
/*     */   @Deprecated
/*     */   public SSLConnectionSocketFactory(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
/* 259 */     this(((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public SSLConnectionSocketFactory(SSLSocketFactory socketfactory, X509HostnameVerifier hostnameVerifier) {
/* 271 */     this(socketfactory, (String[])null, (String[])null, hostnameVerifier);
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
/*     */   @Deprecated
/*     */   public SSLConnectionSocketFactory(SSLSocketFactory socketfactory, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
/* 284 */     this(socketfactory, supportedProtocols, supportedCipherSuites, hostnameVerifier);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLConnectionSocketFactory(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
/* 292 */     this(((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), (String[])null, (String[])null, hostnameVerifier);
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
/*     */   public SSLConnectionSocketFactory(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, HostnameVerifier hostnameVerifier) {
/* 304 */     this(((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLConnectionSocketFactory(SSLSocketFactory socketfactory, HostnameVerifier hostnameVerifier) {
/* 314 */     this(socketfactory, (String[])null, (String[])null, hostnameVerifier);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLConnectionSocketFactory(SSLSocketFactory socketfactory, String[] supportedProtocols, String[] supportedCipherSuites, HostnameVerifier hostnameVerifier) {
/* 325 */     this.socketfactory = (SSLSocketFactory)Args.notNull(socketfactory, "SSL socket factory");
/* 326 */     this.supportedProtocols = supportedProtocols;
/* 327 */     this.supportedCipherSuites = supportedCipherSuites;
/* 328 */     this.hostnameVerifier = (hostnameVerifier != null) ? hostnameVerifier : getDefaultHostnameVerifier();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void prepareSocket(SSLSocket socket) throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket createSocket(HttpContext context) throws IOException {
/* 344 */     return SocketFactory.getDefault().createSocket();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
/* 355 */     Args.notNull(host, "HTTP host");
/* 356 */     Args.notNull(remoteAddress, "Remote address");
/* 357 */     Socket sock = (socket != null) ? socket : createSocket(context);
/* 358 */     if (localAddress != null) {
/* 359 */       sock.bind(localAddress);
/*     */     }
/*     */     try {
/* 362 */       if (connectTimeout > 0 && sock.getSoTimeout() == 0) {
/* 363 */         sock.setSoTimeout(connectTimeout);
/*     */       }
/* 365 */       if (this.log.isDebugEnabled()) {
/* 366 */         this.log.debug("Connecting socket to " + remoteAddress + " with timeout " + connectTimeout);
/*     */       }
/* 368 */       sock.connect(remoteAddress, connectTimeout);
/* 369 */     } catch (IOException ex) {
/*     */       try {
/* 371 */         sock.close();
/* 372 */       } catch (IOException ignore) {}
/*     */       
/* 374 */       throw ex;
/*     */     } 
/*     */     
/* 377 */     if (sock instanceof SSLSocket) {
/* 378 */       SSLSocket sslsock = (SSLSocket)sock;
/* 379 */       this.log.debug("Starting handshake");
/* 380 */       sslsock.startHandshake();
/* 381 */       verifyHostname(sslsock, host.getHostName());
/* 382 */       return sock;
/*     */     } 
/* 384 */     return createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
/* 393 */     SSLSocket sslsock = (SSLSocket)this.socketfactory.createSocket(socket, target, port, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 398 */     if (this.supportedProtocols != null) {
/* 399 */       sslsock.setEnabledProtocols(this.supportedProtocols);
/*     */     } else {
/*     */       
/* 402 */       String[] allProtocols = sslsock.getEnabledProtocols();
/* 403 */       List<String> enabledProtocols = new ArrayList<String>(allProtocols.length);
/* 404 */       for (String protocol : allProtocols) {
/* 405 */         if (!protocol.startsWith("SSL")) {
/* 406 */           enabledProtocols.add(protocol);
/*     */         }
/*     */       } 
/* 409 */       if (!enabledProtocols.isEmpty()) {
/* 410 */         sslsock.setEnabledProtocols(enabledProtocols.<String>toArray(new String[enabledProtocols.size()]));
/*     */       }
/*     */     } 
/* 413 */     if (this.supportedCipherSuites != null) {
/* 414 */       sslsock.setEnabledCipherSuites(this.supportedCipherSuites);
/*     */     } else {
/*     */       
/* 417 */       String[] allCipherSuites = sslsock.getEnabledCipherSuites();
/* 418 */       List<String> enabledCipherSuites = new ArrayList<String>(allCipherSuites.length);
/* 419 */       for (String cipherSuite : allCipherSuites) {
/* 420 */         if (!isWeakCipherSuite(cipherSuite)) {
/* 421 */           enabledCipherSuites.add(cipherSuite);
/*     */         }
/*     */       } 
/* 424 */       if (!enabledCipherSuites.isEmpty()) {
/* 425 */         sslsock.setEnabledCipherSuites(enabledCipherSuites.<String>toArray(new String[enabledCipherSuites.size()]));
/*     */       }
/*     */     } 
/*     */     
/* 429 */     if (this.log.isDebugEnabled()) {
/* 430 */       this.log.debug("Enabled protocols: " + Arrays.<String>asList(sslsock.getEnabledProtocols()));
/* 431 */       this.log.debug("Enabled cipher suites:" + Arrays.<String>asList(sslsock.getEnabledCipherSuites()));
/*     */     } 
/*     */     
/* 434 */     prepareSocket(sslsock);
/* 435 */     this.log.debug("Starting handshake");
/* 436 */     sslsock.startHandshake();
/* 437 */     verifyHostname(sslsock, target);
/* 438 */     return sslsock;
/*     */   }
/*     */   
/*     */   private void verifyHostname(SSLSocket sslsock, String hostname) throws IOException {
/*     */     try {
/* 443 */       SSLSession session = sslsock.getSession();
/* 444 */       if (session == null) {
/*     */ 
/*     */ 
/*     */         
/* 448 */         InputStream in = sslsock.getInputStream();
/* 449 */         in.available();
/*     */ 
/*     */         
/* 452 */         session = sslsock.getSession();
/* 453 */         if (session == null) {
/*     */ 
/*     */           
/* 456 */           sslsock.startHandshake();
/* 457 */           session = sslsock.getSession();
/*     */         } 
/*     */       } 
/* 460 */       if (session == null) {
/* 461 */         throw new SSLHandshakeException("SSL session not available");
/*     */       }
/*     */       
/* 464 */       if (this.log.isDebugEnabled()) {
/* 465 */         this.log.debug("Secure session established");
/* 466 */         this.log.debug(" negotiated protocol: " + session.getProtocol());
/* 467 */         this.log.debug(" negotiated cipher suite: " + session.getCipherSuite());
/*     */ 
/*     */         
/*     */         try {
/* 471 */           Certificate[] certs = session.getPeerCertificates();
/* 472 */           X509Certificate x509 = (X509Certificate)certs[0];
/* 473 */           X500Principal peer = x509.getSubjectX500Principal();
/*     */           
/* 475 */           this.log.debug(" peer principal: " + peer.toString());
/* 476 */           Collection<List<?>> altNames1 = x509.getSubjectAlternativeNames();
/* 477 */           if (altNames1 != null) {
/* 478 */             List<String> altNames = new ArrayList<String>();
/* 479 */             for (List<?> aC : altNames1) {
/* 480 */               if (!aC.isEmpty()) {
/* 481 */                 altNames.add((String)aC.get(1));
/*     */               }
/*     */             } 
/* 484 */             this.log.debug(" peer alternative names: " + altNames);
/*     */           } 
/*     */           
/* 487 */           X500Principal issuer = x509.getIssuerX500Principal();
/* 488 */           this.log.debug(" issuer principal: " + issuer.toString());
/* 489 */           Collection<List<?>> altNames2 = x509.getIssuerAlternativeNames();
/* 490 */           if (altNames2 != null) {
/* 491 */             List<String> altNames = new ArrayList<String>();
/* 492 */             for (List<?> aC : altNames2) {
/* 493 */               if (!aC.isEmpty()) {
/* 494 */                 altNames.add((String)aC.get(1));
/*     */               }
/*     */             } 
/* 497 */             this.log.debug(" issuer alternative names: " + altNames);
/*     */           } 
/* 499 */         } catch (Exception ignore) {}
/*     */       } 
/*     */ 
/*     */       
/* 503 */       if (!this.hostnameVerifier.verify(hostname, session)) {
/* 504 */         Certificate[] certs = session.getPeerCertificates();
/* 505 */         X509Certificate x509 = (X509Certificate)certs[0];
/* 506 */         List<SubjectName> subjectAlts = DefaultHostnameVerifier.getSubjectAltNames(x509);
/* 507 */         throw new SSLPeerUnverifiedException("Certificate for <" + hostname + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
/*     */       }
/*     */     
/*     */     }
/* 511 */     catch (IOException iox) {
/*     */       
/* 513 */       try { sslsock.close(); } catch (Exception x) {}
/* 514 */       throw iox;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ssl\SSLConnectionSocketFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */