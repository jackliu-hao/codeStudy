package io.undertow.server.protocol.http;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;

public class ALPNLimitingSSLEngine extends SSLEngine {
   private static final SSLEngineResult UNDERFLOW_RESULT;
   private final SSLEngine delegate;
   private final Runnable invalidAlpnRunnable;
   private boolean done;

   public ALPNLimitingSSLEngine(SSLEngine delegate, Runnable invalidAlpnRunnable) {
      this.delegate = delegate;
      this.invalidAlpnRunnable = invalidAlpnRunnable;
   }

   public String getPeerHost() {
      return this.delegate.getPeerHost();
   }

   public int getPeerPort() {
      return this.delegate.getPeerPort();
   }

   public SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
      return this.delegate.wrap(src, dst);
   }

   public SSLEngineResult wrap(ByteBuffer[] srcs, ByteBuffer dst) throws SSLException {
      return this.wrap(srcs, 0, srcs.length, dst);
   }

   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
      if (this.done) {
         return this.delegate.unwrap(src, dst);
      } else if (ALPNOfferedClientHelloExplorer.isIncompleteHeader(src)) {
         return UNDERFLOW_RESULT;
      } else {
         try {
            List<Integer> clientCiphers = ALPNOfferedClientHelloExplorer.parseClientHello(src);
            if (clientCiphers != null) {
               this.limitCiphers(clientCiphers);
               this.done = true;
            } else {
               this.done = true;
            }
         } catch (BufferUnderflowException var4) {
            return UNDERFLOW_RESULT;
         }

         return this.delegate.unwrap(src, dst);
      }
   }

   private void limitCiphers(List<Integer> clientCiphers) {
      boolean clientIsCompliant = false;
      Iterator var3 = clientCiphers.iterator();

      while(var3.hasNext()) {
         int cipher = (Integer)var3.next();
         if (cipher == 49199) {
            clientIsCompliant = true;
         }
      }

      if (!clientIsCompliant) {
         this.invalidAlpnRunnable.run();
      } else {
         List<String> ciphers = new ArrayList();
         String[] var9 = this.delegate.getEnabledCipherSuites();
         int var5 = var9.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String cipher = var9[var6];
            if (ALPNBannedCiphers.isAllowed(cipher)) {
               ciphers.add(cipher);
            }
         }

         this.delegate.setEnabledCipherSuites((String[])ciphers.toArray(new String[ciphers.size()]));
      }

   }

   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException {
      return this.unwrap(src, dsts, 0, dsts.length);
   }

   public SSLSession getHandshakeSession() {
      return this.delegate.getHandshakeSession();
   }

   public SSLParameters getSSLParameters() {
      return this.delegate.getSSLParameters();
   }

   public void setSSLParameters(SSLParameters sslParameters) {
      this.delegate.setSSLParameters(sslParameters);
   }

   public SSLEngineResult wrap(ByteBuffer[] srcs, int off, int len, ByteBuffer dst) throws SSLException {
      return this.delegate.wrap(srcs, off, len, dst);
   }

   public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers, int i, int i1) throws SSLException {
      if (this.done) {
         return this.delegate.unwrap(byteBuffer, byteBuffers, i, i1);
      } else if (ALPNOfferedClientHelloExplorer.isIncompleteHeader(byteBuffer)) {
         return UNDERFLOW_RESULT;
      } else {
         try {
            List<Integer> clientCiphers = ALPNOfferedClientHelloExplorer.parseClientHello(byteBuffer);
            if (clientCiphers != null) {
               this.limitCiphers(clientCiphers);
               this.done = true;
            } else {
               this.done = true;
            }
         } catch (BufferUnderflowException var6) {
            return UNDERFLOW_RESULT;
         }

         return this.delegate.unwrap(byteBuffer, byteBuffers, i, i1);
      }
   }

   public Runnable getDelegatedTask() {
      return this.delegate.getDelegatedTask();
   }

   public void closeInbound() throws SSLException {
      this.delegate.closeInbound();
   }

   public boolean isInboundDone() {
      return this.delegate.isInboundDone();
   }

   public void closeOutbound() {
      this.delegate.closeOutbound();
   }

   public boolean isOutboundDone() {
      return this.delegate.isOutboundDone();
   }

   public String[] getSupportedCipherSuites() {
      return this.delegate.getSupportedCipherSuites();
   }

   public String[] getEnabledCipherSuites() {
      return this.delegate.getEnabledCipherSuites();
   }

   public void setEnabledCipherSuites(String[] strings) {
      this.delegate.setEnabledCipherSuites(strings);
   }

   public String[] getSupportedProtocols() {
      return this.delegate.getSupportedProtocols();
   }

   public String[] getEnabledProtocols() {
      return this.delegate.getEnabledProtocols();
   }

   public void setEnabledProtocols(String[] strings) {
      this.delegate.setEnabledProtocols(strings);
   }

   public SSLSession getSession() {
      return this.delegate.getSession();
   }

   public void beginHandshake() throws SSLException {
      this.delegate.beginHandshake();
   }

   public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
      return this.delegate.getHandshakeStatus();
   }

   public void setUseClientMode(boolean b) {
      if (b) {
         throw new IllegalArgumentException();
      }
   }

   public boolean getUseClientMode() {
      return this.delegate.getUseClientMode();
   }

   public void setNeedClientAuth(boolean b) {
      this.delegate.setNeedClientAuth(b);
   }

   public boolean getNeedClientAuth() {
      return this.delegate.getNeedClientAuth();
   }

   public void setWantClientAuth(boolean b) {
      this.delegate.setWantClientAuth(b);
   }

   public boolean getWantClientAuth() {
      return this.delegate.getWantClientAuth();
   }

   public void setEnableSessionCreation(boolean b) {
      this.delegate.setEnableSessionCreation(b);
   }

   public boolean getEnableSessionCreation() {
      return this.delegate.getEnableSessionCreation();
   }

   static {
      UNDERFLOW_RESULT = new SSLEngineResult(Status.BUFFER_UNDERFLOW, HandshakeStatus.NEED_UNWRAP, 0, 0);
   }
}
