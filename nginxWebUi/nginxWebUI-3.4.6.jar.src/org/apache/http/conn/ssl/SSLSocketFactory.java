/*     */ package org.apache.http.conn.ssl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import javax.net.SocketFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.ConnectTimeoutException;
/*     */ import org.apache.http.conn.HttpInetSocketAddress;
/*     */ import org.apache.http.conn.scheme.HostNameResolver;
/*     */ import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
/*     */ import org.apache.http.conn.scheme.LayeredSocketFactory;
/*     */ import org.apache.http.conn.scheme.SchemeLayeredSocketFactory;
/*     */ import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
/*     */ import org.apache.http.params.HttpConnectionParams;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ public class SSLSocketFactory
/*     */   implements LayeredConnectionSocketFactory, SchemeLayeredSocketFactory, LayeredSchemeSocketFactory, LayeredSocketFactory
/*     */ {
/*     */   public static final String TLS = "TLS";
/*     */   public static final String SSL = "SSL";
/*     */   public static final String SSLV2 = "SSLv2";
/* 154 */   public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();
/*     */ 
/*     */   
/* 157 */   public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifier();
/*     */ 
/*     */   
/* 160 */   public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER = new StrictHostnameVerifier();
/*     */   
/*     */   private final javax.net.ssl.SSLSocketFactory socketfactory;
/*     */   
/*     */   private final HostNameResolver nameResolver;
/*     */   
/*     */   private volatile X509HostnameVerifier hostnameVerifier;
/*     */   private final String[] supportedProtocols;
/*     */   private final String[] supportedCipherSuites;
/*     */   
/*     */   public static SSLSocketFactory getSocketFactory() throws SSLInitializationException {
/* 171 */     return new SSLSocketFactory(SSLContexts.createDefault(), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String[] split(String s) {
/* 177 */     if (TextUtils.isBlank(s)) {
/* 178 */       return null;
/*     */     }
/* 180 */     return s.split(" *, *");
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
/*     */   public static SSLSocketFactory getSystemSocketFactory() throws SSLInitializationException {
/* 193 */     return new SSLSocketFactory((javax.net.ssl.SSLSocketFactory)javax.net.ssl.SSLSocketFactory.getDefault(), split(System.getProperty("https.protocols")), split(System.getProperty("https.cipherSuites")), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
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
/*     */   public SSLSocketFactory(String algorithm, KeyStore keystore, String keyPassword, KeyStore truststore, SecureRandom random, HostNameResolver nameResolver) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
/* 215 */     this(SSLContexts.custom().useProtocol(algorithm).setSecureRandom(random).loadKeyMaterial(keystore, (keyPassword != null) ? keyPassword.toCharArray() : null).loadTrustMaterial(truststore).build(), nameResolver);
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
/*     */   public SSLSocketFactory(String algorithm, KeyStore keystore, String keyPassword, KeyStore truststore, SecureRandom random, TrustStrategy trustStrategy, X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
/* 236 */     this(SSLContexts.custom().useProtocol(algorithm).setSecureRandom(random).loadKeyMaterial(keystore, (keyPassword != null) ? keyPassword.toCharArray() : null).loadTrustMaterial(truststore, trustStrategy).build(), hostnameVerifier);
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
/*     */   public SSLSocketFactory(String algorithm, KeyStore keystore, String keyPassword, KeyStore truststore, SecureRandom random, X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
/* 256 */     this(SSLContexts.custom().useProtocol(algorithm).setSecureRandom(random).loadKeyMaterial(keystore, (keyPassword != null) ? keyPassword.toCharArray() : null).loadTrustMaterial(truststore).build(), hostnameVerifier);
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
/*     */   public SSLSocketFactory(KeyStore keystore, String keystorePassword, KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
/* 270 */     this(SSLContexts.custom().loadKeyMaterial(keystore, (keystorePassword != null) ? keystorePassword.toCharArray() : null).loadTrustMaterial(truststore).build(), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLSocketFactory(KeyStore keystore, String keystorePassword) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
/* 281 */     this(SSLContexts.custom().loadKeyMaterial(keystore, (keystorePassword != null) ? keystorePassword.toCharArray() : null).build(), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
/* 290 */     this(SSLContexts.custom().loadTrustMaterial(truststore).build(), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
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
/*     */   public SSLSocketFactory(TrustStrategy trustStrategy, X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
/* 303 */     this(SSLContexts.custom().loadTrustMaterial(null, trustStrategy).build(), hostnameVerifier);
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
/*     */   public SSLSocketFactory(TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
/* 315 */     this(SSLContexts.custom().loadTrustMaterial(null, trustStrategy).build(), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLSocketFactory(SSLContext sslContext) {
/* 322 */     this(sslContext, BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLSocketFactory(SSLContext sslContext, HostNameResolver nameResolver) {
/* 328 */     this.socketfactory = sslContext.getSocketFactory();
/* 329 */     this.hostnameVerifier = BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
/* 330 */     this.nameResolver = nameResolver;
/* 331 */     this.supportedProtocols = null;
/* 332 */     this.supportedCipherSuites = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLSocketFactory(SSLContext sslContext, X509HostnameVerifier hostnameVerifier) {
/* 340 */     this(((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), (String[])null, (String[])null, hostnameVerifier);
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
/*     */   public SSLSocketFactory(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
/* 352 */     this(((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLSocketFactory(javax.net.ssl.SSLSocketFactory socketfactory, X509HostnameVerifier hostnameVerifier) {
/* 362 */     this(socketfactory, (String[])null, (String[])null, hostnameVerifier);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLSocketFactory(javax.net.ssl.SSLSocketFactory socketfactory, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
/* 373 */     this.socketfactory = (javax.net.ssl.SSLSocketFactory)Args.notNull(socketfactory, "SSL socket factory");
/* 374 */     this.supportedProtocols = supportedProtocols;
/* 375 */     this.supportedCipherSuites = supportedCipherSuites;
/* 376 */     this.hostnameVerifier = (hostnameVerifier != null) ? hostnameVerifier : BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
/* 377 */     this.nameResolver = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket createSocket(HttpParams params) throws IOException {
/* 387 */     return createSocket((HttpContext)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Socket createSocket() throws IOException {
/* 392 */     return createSocket((HttpContext)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket connectSocket(Socket socket, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
/*     */     HttpHost host;
/* 404 */     Args.notNull(remoteAddress, "Remote address");
/* 405 */     Args.notNull(params, "HTTP parameters");
/*     */     
/* 407 */     if (remoteAddress instanceof HttpInetSocketAddress) {
/* 408 */       host = ((HttpInetSocketAddress)remoteAddress).getHttpHost();
/*     */     } else {
/* 410 */       host = new HttpHost(remoteAddress.getHostName(), remoteAddress.getPort(), "https");
/*     */     } 
/* 412 */     int socketTimeout = HttpConnectionParams.getSoTimeout(params);
/* 413 */     int connectTimeout = HttpConnectionParams.getConnectionTimeout(params);
/* 414 */     socket.setSoTimeout(socketTimeout);
/* 415 */     return connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, (HttpContext)null);
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
/*     */   public boolean isSecure(Socket sock) throws IllegalArgumentException {
/* 435 */     Args.notNull(sock, "Socket");
/* 436 */     Asserts.check(sock instanceof SSLSocket, "Socket not created by this factory");
/* 437 */     Asserts.check(!sock.isClosed(), "Socket is closed");
/* 438 */     return true;
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
/*     */   public Socket createLayeredSocket(Socket socket, String host, int port, HttpParams params) throws IOException, UnknownHostException {
/* 450 */     return createLayeredSocket(socket, host, port, (HttpContext)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket createLayeredSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
/* 459 */     return createLayeredSocket(socket, host, port, (HttpContext)null);
/*     */   }
/*     */   
/*     */   public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
/* 463 */     Args.notNull(hostnameVerifier, "Hostname verifier");
/* 464 */     this.hostnameVerifier = hostnameVerifier;
/*     */   }
/*     */   
/*     */   public X509HostnameVerifier getHostnameVerifier() {
/* 468 */     return this.hostnameVerifier;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket connectSocket(Socket socket, String host, int port, InetAddress local, int localPort, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
/*     */     InetAddress remote;
/* 478 */     if (this.nameResolver != null) {
/* 479 */       remote = this.nameResolver.resolve(host);
/*     */     } else {
/* 481 */       remote = InetAddress.getByName(host);
/*     */     } 
/* 483 */     InetSocketAddress localAddress = null;
/* 484 */     if (local != null || localPort > 0) {
/* 485 */       localAddress = new InetSocketAddress(local, (localPort > 0) ? localPort : 0);
/*     */     }
/* 487 */     HttpInetSocketAddress httpInetSocketAddress = new HttpInetSocketAddress(new HttpHost(host, port), remote, port);
/*     */     
/* 489 */     return connectSocket(socket, (InetSocketAddress)httpInetSocketAddress, localAddress, params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
/* 497 */     return createLayeredSocket(socket, host, port, autoClose);
/*     */   }
/*     */ 
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
/*     */   private void internalPrepareSocket(SSLSocket socket) throws IOException {
/* 514 */     if (this.supportedProtocols != null) {
/* 515 */       socket.setEnabledProtocols(this.supportedProtocols);
/*     */     }
/* 517 */     if (this.supportedCipherSuites != null) {
/* 518 */       socket.setEnabledCipherSuites(this.supportedCipherSuites);
/*     */     }
/* 520 */     prepareSocket(socket);
/*     */   }
/*     */ 
/*     */   
/*     */   public Socket createSocket(HttpContext context) throws IOException {
/* 525 */     return SocketFactory.getDefault().createSocket();
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
/* 536 */     Args.notNull(host, "HTTP host");
/* 537 */     Args.notNull(remoteAddress, "Remote address");
/* 538 */     Socket sock = (socket != null) ? socket : createSocket(context);
/* 539 */     if (localAddress != null) {
/* 540 */       sock.bind(localAddress);
/*     */     }
/*     */     try {
/* 543 */       sock.connect(remoteAddress, connectTimeout);
/* 544 */     } catch (SocketTimeoutException ex) {
/* 545 */       throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
/*     */     } 
/*     */     
/* 548 */     if (sock instanceof SSLSocket) {
/* 549 */       SSLSocket sslsock = (SSLSocket)sock;
/* 550 */       sslsock.startHandshake();
/* 551 */       verifyHostname(sslsock, host.getHostName());
/* 552 */       return sock;
/*     */     } 
/* 554 */     return createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
/* 564 */     SSLSocket sslsock = (SSLSocket)this.socketfactory.createSocket(socket, target, port, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 569 */     internalPrepareSocket(sslsock);
/* 570 */     sslsock.startHandshake();
/* 571 */     verifyHostname(sslsock, target);
/* 572 */     return sslsock;
/*     */   }
/*     */   
/*     */   private void verifyHostname(SSLSocket sslsock, String hostname) throws IOException {
/*     */     try {
/* 577 */       this.hostnameVerifier.verify(hostname, sslsock);
/*     */     }
/* 579 */     catch (IOException iox) {
/*     */       
/* 581 */       try { sslsock.close(); } catch (Exception x) {}
/* 582 */       throw iox;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ssl\SSLSocketFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */