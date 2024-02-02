package com.sun.mail.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MailSSLSocketFactory extends SSLSocketFactory {
   private boolean trustAllHosts;
   private String[] trustedHosts;
   private SSLContext sslcontext;
   private KeyManager[] keyManagers;
   private TrustManager[] trustManagers;
   private SecureRandom secureRandom;
   private SSLSocketFactory adapteeFactory;

   public MailSSLSocketFactory() throws GeneralSecurityException {
      this("TLS");
   }

   public MailSSLSocketFactory(String protocol) throws GeneralSecurityException {
      this.trustedHosts = null;
      this.adapteeFactory = null;
      this.trustAllHosts = false;
      this.sslcontext = SSLContext.getInstance(protocol);
      this.keyManagers = null;
      this.trustManagers = new TrustManager[]{new MailTrustManager()};
      this.secureRandom = null;
      this.newAdapteeFactory();
   }

   private synchronized void newAdapteeFactory() throws KeyManagementException {
      this.sslcontext.init(this.keyManagers, this.trustManagers, this.secureRandom);
      this.adapteeFactory = this.sslcontext.getSocketFactory();
   }

   public synchronized KeyManager[] getKeyManagers() {
      return (KeyManager[])((KeyManager[])this.keyManagers.clone());
   }

   public synchronized void setKeyManagers(KeyManager[] keyManagers) throws GeneralSecurityException {
      this.keyManagers = (KeyManager[])((KeyManager[])keyManagers.clone());
      this.newAdapteeFactory();
   }

   public synchronized SecureRandom getSecureRandom() {
      return this.secureRandom;
   }

   public synchronized void setSecureRandom(SecureRandom secureRandom) throws GeneralSecurityException {
      this.secureRandom = secureRandom;
      this.newAdapteeFactory();
   }

   public synchronized TrustManager[] getTrustManagers() {
      return this.trustManagers;
   }

   public synchronized void setTrustManagers(TrustManager[] trustManagers) throws GeneralSecurityException {
      this.trustManagers = trustManagers;
      this.newAdapteeFactory();
   }

   public synchronized boolean isTrustAllHosts() {
      return this.trustAllHosts;
   }

   public synchronized void setTrustAllHosts(boolean trustAllHosts) {
      this.trustAllHosts = trustAllHosts;
   }

   public synchronized String[] getTrustedHosts() {
      return (String[])((String[])this.trustedHosts.clone());
   }

   public synchronized void setTrustedHosts(String[] trustedHosts) {
      this.trustedHosts = (String[])((String[])trustedHosts.clone());
   }

   public synchronized boolean isServerTrusted(String server, SSLSocket sslSocket) {
      if (this.trustAllHosts) {
         return true;
      } else {
         return this.trustedHosts != null ? Arrays.asList(this.trustedHosts).contains(server) : true;
      }
   }

   public synchronized Socket createSocket(Socket socket, String s, int i, boolean flag) throws IOException {
      return this.adapteeFactory.createSocket(socket, s, i, flag);
   }

   public synchronized String[] getDefaultCipherSuites() {
      return this.adapteeFactory.getDefaultCipherSuites();
   }

   public synchronized String[] getSupportedCipherSuites() {
      return this.adapteeFactory.getSupportedCipherSuites();
   }

   public synchronized Socket createSocket() throws IOException {
      return this.adapteeFactory.createSocket();
   }

   public synchronized Socket createSocket(InetAddress inetaddress, int i, InetAddress inetaddress1, int j) throws IOException {
      return this.adapteeFactory.createSocket(inetaddress, i, inetaddress1, j);
   }

   public synchronized Socket createSocket(InetAddress inetaddress, int i) throws IOException {
      return this.adapteeFactory.createSocket(inetaddress, i);
   }

   public synchronized Socket createSocket(String s, int i, InetAddress inetaddress, int j) throws IOException, UnknownHostException {
      return this.adapteeFactory.createSocket(s, i, inetaddress, j);
   }

   public synchronized Socket createSocket(String s, int i) throws IOException, UnknownHostException {
      return this.adapteeFactory.createSocket(s, i);
   }

   private class MailTrustManager implements X509TrustManager {
      private X509TrustManager adapteeTrustManager;

      private MailTrustManager() throws GeneralSecurityException {
         this.adapteeTrustManager = null;
         TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
         tmf.init((KeyStore)null);
         this.adapteeTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
      }

      public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
         if (!MailSSLSocketFactory.this.isTrustAllHosts() && MailSSLSocketFactory.this.getTrustedHosts() == null) {
            this.adapteeTrustManager.checkClientTrusted(certs, authType);
         }

      }

      public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
         if (!MailSSLSocketFactory.this.isTrustAllHosts() && MailSSLSocketFactory.this.getTrustedHosts() == null) {
            this.adapteeTrustManager.checkServerTrusted(certs, authType);
         }

      }

      public X509Certificate[] getAcceptedIssuers() {
         return this.adapteeTrustManager.getAcceptedIssuers();
      }

      // $FF: synthetic method
      MailTrustManager(Object x1) throws GeneralSecurityException {
         this();
      }
   }
}
