package org.apache.http.conn.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.security.auth.x500.X500Principal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class SSLConnectionSocketFactory implements LayeredConnectionSocketFactory {
   public static final String TLS = "TLS";
   public static final String SSL = "SSL";
   public static final String SSLV2 = "SSLv2";
   /** @deprecated */
   @Deprecated
   public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER;
   /** @deprecated */
   @Deprecated
   public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
   /** @deprecated */
   @Deprecated
   public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER;
   private static final String WEAK_KEY_EXCHANGES = "^(TLS|SSL)_(NULL|ECDH_anon|DH_anon|DH_anon_EXPORT|DHE_RSA_EXPORT|DHE_DSS_EXPORT|DSS_EXPORT|DH_DSS_EXPORT|DH_RSA_EXPORT|RSA_EXPORT|KRB5_EXPORT)_(.*)";
   private static final String WEAK_CIPHERS = "^(TLS|SSL)_(.*)_WITH_(NULL|DES_CBC|DES40_CBC|DES_CBC_40|3DES_EDE_CBC|RC4_128|RC4_40|RC2_CBC_40)_(.*)";
   private static final List<Pattern> WEAK_CIPHER_SUITE_PATTERNS;
   private final Log log;
   private final javax.net.ssl.SSLSocketFactory socketfactory;
   private final HostnameVerifier hostnameVerifier;
   private final String[] supportedProtocols;
   private final String[] supportedCipherSuites;

   public static HostnameVerifier getDefaultHostnameVerifier() {
      return new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault());
   }

   public static SSLConnectionSocketFactory getSocketFactory() throws SSLInitializationException {
      return new SSLConnectionSocketFactory(org.apache.http.ssl.SSLContexts.createDefault(), getDefaultHostnameVerifier());
   }

   static boolean isWeakCipherSuite(String cipherSuite) {
      Iterator i$ = WEAK_CIPHER_SUITE_PATTERNS.iterator();

      Pattern pattern;
      do {
         if (!i$.hasNext()) {
            return false;
         }

         pattern = (Pattern)i$.next();
      } while(!pattern.matcher(cipherSuite).matches());

      return true;
   }

   private static String[] split(String s) {
      return TextUtils.isBlank(s) ? null : s.split(" *, *");
   }

   public static SSLConnectionSocketFactory getSystemSocketFactory() throws SSLInitializationException {
      return new SSLConnectionSocketFactory((javax.net.ssl.SSLSocketFactory)javax.net.ssl.SSLSocketFactory.getDefault(), split(System.getProperty("https.protocols")), split(System.getProperty("https.cipherSuites")), getDefaultHostnameVerifier());
   }

   public SSLConnectionSocketFactory(SSLContext sslContext) {
      this(sslContext, getDefaultHostnameVerifier());
   }

   /** @deprecated */
   @Deprecated
   public SSLConnectionSocketFactory(SSLContext sslContext, X509HostnameVerifier hostnameVerifier) {
      this((javax.net.ssl.SSLSocketFactory)((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), (String[])null, (String[])null, (X509HostnameVerifier)hostnameVerifier);
   }

   /** @deprecated */
   @Deprecated
   public SSLConnectionSocketFactory(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
      this(((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
   }

   /** @deprecated */
   @Deprecated
   public SSLConnectionSocketFactory(javax.net.ssl.SSLSocketFactory socketfactory, X509HostnameVerifier hostnameVerifier) {
      this((javax.net.ssl.SSLSocketFactory)socketfactory, (String[])null, (String[])null, (X509HostnameVerifier)hostnameVerifier);
   }

   /** @deprecated */
   @Deprecated
   public SSLConnectionSocketFactory(javax.net.ssl.SSLSocketFactory socketfactory, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
      this((javax.net.ssl.SSLSocketFactory)socketfactory, supportedProtocols, supportedCipherSuites, (HostnameVerifier)hostnameVerifier);
   }

   public SSLConnectionSocketFactory(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
      this((javax.net.ssl.SSLSocketFactory)((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), (String[])null, (String[])null, (HostnameVerifier)hostnameVerifier);
   }

   public SSLConnectionSocketFactory(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, HostnameVerifier hostnameVerifier) {
      this(((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
   }

   public SSLConnectionSocketFactory(javax.net.ssl.SSLSocketFactory socketfactory, HostnameVerifier hostnameVerifier) {
      this((javax.net.ssl.SSLSocketFactory)socketfactory, (String[])null, (String[])null, (HostnameVerifier)hostnameVerifier);
   }

   public SSLConnectionSocketFactory(javax.net.ssl.SSLSocketFactory socketfactory, String[] supportedProtocols, String[] supportedCipherSuites, HostnameVerifier hostnameVerifier) {
      this.log = LogFactory.getLog(this.getClass());
      this.socketfactory = (javax.net.ssl.SSLSocketFactory)Args.notNull(socketfactory, "SSL socket factory");
      this.supportedProtocols = supportedProtocols;
      this.supportedCipherSuites = supportedCipherSuites;
      this.hostnameVerifier = hostnameVerifier != null ? hostnameVerifier : getDefaultHostnameVerifier();
   }

   protected void prepareSocket(SSLSocket socket) throws IOException {
   }

   public Socket createSocket(HttpContext context) throws IOException {
      return SocketFactory.getDefault().createSocket();
   }

   public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
      Args.notNull(host, "HTTP host");
      Args.notNull(remoteAddress, "Remote address");
      Socket sock = socket != null ? socket : this.createSocket(context);
      if (localAddress != null) {
         sock.bind(localAddress);
      }

      try {
         if (connectTimeout > 0 && sock.getSoTimeout() == 0) {
            sock.setSoTimeout(connectTimeout);
         }

         if (this.log.isDebugEnabled()) {
            this.log.debug("Connecting socket to " + remoteAddress + " with timeout " + connectTimeout);
         }

         sock.connect(remoteAddress, connectTimeout);
      } catch (IOException var11) {
         try {
            sock.close();
         } catch (IOException var10) {
         }

         throw var11;
      }

      if (sock instanceof SSLSocket) {
         SSLSocket sslsock = (SSLSocket)sock;
         this.log.debug("Starting handshake");
         sslsock.startHandshake();
         this.verifyHostname(sslsock, host.getHostName());
         return sock;
      } else {
         return this.createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), context);
      }
   }

   public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
      SSLSocket sslsock = (SSLSocket)this.socketfactory.createSocket(socket, target, port, true);
      String[] allCipherSuites;
      ArrayList enabledCipherSuites;
      String[] arr$;
      int len$;
      int i$;
      String cipherSuite;
      if (this.supportedProtocols != null) {
         sslsock.setEnabledProtocols(this.supportedProtocols);
      } else {
         allCipherSuites = sslsock.getEnabledProtocols();
         enabledCipherSuites = new ArrayList(allCipherSuites.length);
         arr$ = allCipherSuites;
         len$ = allCipherSuites.length;

         for(i$ = 0; i$ < len$; ++i$) {
            cipherSuite = arr$[i$];
            if (!cipherSuite.startsWith("SSL")) {
               enabledCipherSuites.add(cipherSuite);
            }
         }

         if (!enabledCipherSuites.isEmpty()) {
            sslsock.setEnabledProtocols((String[])enabledCipherSuites.toArray(new String[enabledCipherSuites.size()]));
         }
      }

      if (this.supportedCipherSuites != null) {
         sslsock.setEnabledCipherSuites(this.supportedCipherSuites);
      } else {
         allCipherSuites = sslsock.getEnabledCipherSuites();
         enabledCipherSuites = new ArrayList(allCipherSuites.length);
         arr$ = allCipherSuites;
         len$ = allCipherSuites.length;

         for(i$ = 0; i$ < len$; ++i$) {
            cipherSuite = arr$[i$];
            if (!isWeakCipherSuite(cipherSuite)) {
               enabledCipherSuites.add(cipherSuite);
            }
         }

         if (!enabledCipherSuites.isEmpty()) {
            sslsock.setEnabledCipherSuites((String[])enabledCipherSuites.toArray(new String[enabledCipherSuites.size()]));
         }
      }

      if (this.log.isDebugEnabled()) {
         this.log.debug("Enabled protocols: " + Arrays.asList(sslsock.getEnabledProtocols()));
         this.log.debug("Enabled cipher suites:" + Arrays.asList(sslsock.getEnabledCipherSuites()));
      }

      this.prepareSocket(sslsock);
      this.log.debug("Starting handshake");
      sslsock.startHandshake();
      this.verifyHostname(sslsock, target);
      return sslsock;
   }

   private void verifyHostname(SSLSocket sslsock, String hostname) throws IOException {
      try {
         SSLSession session = sslsock.getSession();
         if (session == null) {
            InputStream in = sslsock.getInputStream();
            in.available();
            session = sslsock.getSession();
            if (session == null) {
               sslsock.startHandshake();
               session = sslsock.getSession();
            }
         }

         if (session == null) {
            throw new SSLHandshakeException("SSL session not available");
         } else {
            X509Certificate x509;
            Certificate[] certs;
            if (this.log.isDebugEnabled()) {
               this.log.debug("Secure session established");
               this.log.debug(" negotiated protocol: " + session.getProtocol());
               this.log.debug(" negotiated cipher suite: " + session.getCipherSuite());

               try {
                  certs = session.getPeerCertificates();
                  x509 = (X509Certificate)certs[0];
                  X500Principal peer = x509.getSubjectX500Principal();
                  this.log.debug(" peer principal: " + peer.toString());
                  Collection<List<?>> altNames1 = x509.getSubjectAlternativeNames();
                  if (altNames1 != null) {
                     List<String> altNames = new ArrayList();
                     Iterator i$ = altNames1.iterator();

                     while(i$.hasNext()) {
                        List<?> aC = (List)i$.next();
                        if (!aC.isEmpty()) {
                           altNames.add((String)aC.get(1));
                        }
                     }

                     this.log.debug(" peer alternative names: " + altNames);
                  }

                  X500Principal issuer = x509.getIssuerX500Principal();
                  this.log.debug(" issuer principal: " + issuer.toString());
                  Collection<List<?>> altNames2 = x509.getIssuerAlternativeNames();
                  if (altNames2 != null) {
                     List<String> altNames = new ArrayList();
                     Iterator i$ = altNames2.iterator();

                     while(i$.hasNext()) {
                        List<?> aC = (List)i$.next();
                        if (!aC.isEmpty()) {
                           altNames.add((String)aC.get(1));
                        }
                     }

                     this.log.debug(" issuer alternative names: " + altNames);
                  }
               } catch (Exception var14) {
               }
            }

            if (!this.hostnameVerifier.verify(hostname, session)) {
               certs = session.getPeerCertificates();
               x509 = (X509Certificate)certs[0];
               List<SubjectName> subjectAlts = DefaultHostnameVerifier.getSubjectAltNames(x509);
               throw new SSLPeerUnverifiedException("Certificate for <" + hostname + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
            }
         }
      } catch (IOException var15) {
         try {
            sslsock.close();
         } catch (Exception var13) {
         }

         throw var15;
      }
   }

   static {
      ALLOW_ALL_HOSTNAME_VERIFIER = AllowAllHostnameVerifier.INSTANCE;
      BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = BrowserCompatHostnameVerifier.INSTANCE;
      STRICT_HOSTNAME_VERIFIER = StrictHostnameVerifier.INSTANCE;
      WEAK_CIPHER_SUITE_PATTERNS = Collections.unmodifiableList(Arrays.asList(Pattern.compile("^(TLS|SSL)_(NULL|ECDH_anon|DH_anon|DH_anon_EXPORT|DHE_RSA_EXPORT|DHE_DSS_EXPORT|DSS_EXPORT|DH_DSS_EXPORT|DH_RSA_EXPORT|RSA_EXPORT|KRB5_EXPORT)_(.*)", 2), Pattern.compile("^(TLS|SSL)_(.*)_WITH_(NULL|DES_CBC|DES40_CBC|DES_CBC_40|3DES_EDE_CBC|RC4_128|RC4_40|RC2_CBC_40)_(.*)", 2)));
   }
}
