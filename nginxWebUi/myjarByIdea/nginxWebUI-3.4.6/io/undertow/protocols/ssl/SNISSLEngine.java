package io.undertow.protocols.ssl;

import io.undertow.UndertowMessages;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.security.cert.X509Certificate;

class SNISSLEngine extends SSLEngine {
   private static final SSLEngineResult UNDERFLOW_UNWRAP;
   private static final SSLEngineResult OK_UNWRAP;
   private final AtomicReference<SSLEngine> currentRef;
   private Function<SSLEngine, SSLEngine> selectionCallback = Function.identity();
   static final int FL_WANT_C_AUTH = 1;
   static final int FL_NEED_C_AUTH = 2;
   static final int FL_SESSION_CRE = 4;
   static final SSLEngine CLOSED_STATE;

   SNISSLEngine(SNIContextMatcher selector) {
      this.currentRef = new AtomicReference(new InitialState(selector, SSLContext::createSSLEngine));
   }

   SNISSLEngine(SNIContextMatcher selector, String host, int port) {
      super(host, port);
      this.currentRef = new AtomicReference(new InitialState(selector, (sslContext) -> {
         return sslContext.createSSLEngine(host, port);
      }));
   }

   public Function<SSLEngine, SSLEngine> getSelectionCallback() {
      return this.selectionCallback;
   }

   public void setSelectionCallback(Function<SSLEngine, SSLEngine> selectionCallback) {
      this.selectionCallback = selectionCallback;
   }

   public SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
      return ((SSLEngine)this.currentRef.get()).wrap(srcs, offset, length, dst);
   }

   public SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
      return ((SSLEngine)this.currentRef.get()).wrap(src, dst);
   }

   public SSLEngineResult wrap(ByteBuffer[] srcs, ByteBuffer dst) throws SSLException {
      return ((SSLEngine)this.currentRef.get()).wrap(srcs, dst);
   }

   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
      return ((SSLEngine)this.currentRef.get()).unwrap(src, dsts, offset, length);
   }

   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
      return ((SSLEngine)this.currentRef.get()).unwrap(src, dst);
   }

   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException {
      return ((SSLEngine)this.currentRef.get()).unwrap(src, dsts);
   }

   public String getPeerHost() {
      return ((SSLEngine)this.currentRef.get()).getPeerHost();
   }

   public int getPeerPort() {
      return ((SSLEngine)this.currentRef.get()).getPeerPort();
   }

   public SSLSession getHandshakeSession() {
      return ((SSLEngine)this.currentRef.get()).getHandshakeSession();
   }

   public SSLParameters getSSLParameters() {
      return ((SSLEngine)this.currentRef.get()).getSSLParameters();
   }

   public void setSSLParameters(SSLParameters params) {
      ((SSLEngine)this.currentRef.get()).setSSLParameters(params);
   }

   public Runnable getDelegatedTask() {
      return ((SSLEngine)this.currentRef.get()).getDelegatedTask();
   }

   public void closeInbound() throws SSLException {
      ((SSLEngine)this.currentRef.get()).closeInbound();
   }

   public boolean isInboundDone() {
      return ((SSLEngine)this.currentRef.get()).isInboundDone();
   }

   public void closeOutbound() {
      ((SSLEngine)this.currentRef.get()).closeOutbound();
   }

   public boolean isOutboundDone() {
      return ((SSLEngine)this.currentRef.get()).isOutboundDone();
   }

   public String[] getSupportedCipherSuites() {
      return ((SSLEngine)this.currentRef.get()).getSupportedCipherSuites();
   }

   public String[] getEnabledCipherSuites() {
      return ((SSLEngine)this.currentRef.get()).getEnabledCipherSuites();
   }

   public void setEnabledCipherSuites(String[] cipherSuites) {
      ((SSLEngine)this.currentRef.get()).setEnabledCipherSuites(cipherSuites);
   }

   public String[] getSupportedProtocols() {
      return ((SSLEngine)this.currentRef.get()).getSupportedProtocols();
   }

   public String[] getEnabledProtocols() {
      return ((SSLEngine)this.currentRef.get()).getEnabledProtocols();
   }

   public void setEnabledProtocols(String[] protocols) {
      ((SSLEngine)this.currentRef.get()).setEnabledProtocols(protocols);
   }

   public SSLSession getSession() {
      return ((SSLEngine)this.currentRef.get()).getSession();
   }

   public void beginHandshake() throws SSLException {
      ((SSLEngine)this.currentRef.get()).beginHandshake();
   }

   public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
      return ((SSLEngine)this.currentRef.get()).getHandshakeStatus();
   }

   public void setUseClientMode(boolean clientMode) {
      ((SSLEngine)this.currentRef.get()).setUseClientMode(clientMode);
   }

   public boolean getUseClientMode() {
      return ((SSLEngine)this.currentRef.get()).getUseClientMode();
   }

   public void setNeedClientAuth(boolean clientAuth) {
      ((SSLEngine)this.currentRef.get()).setNeedClientAuth(clientAuth);
   }

   public boolean getNeedClientAuth() {
      return ((SSLEngine)this.currentRef.get()).getNeedClientAuth();
   }

   public void setWantClientAuth(boolean want) {
      ((SSLEngine)this.currentRef.get()).setWantClientAuth(want);
   }

   public boolean getWantClientAuth() {
      return ((SSLEngine)this.currentRef.get()).getWantClientAuth();
   }

   public void setEnableSessionCreation(boolean flag) {
      ((SSLEngine)this.currentRef.get()).setEnableSessionCreation(flag);
   }

   public boolean getEnableSessionCreation() {
      return ((SSLEngine)this.currentRef.get()).getEnableSessionCreation();
   }

   static {
      UNDERFLOW_UNWRAP = new SSLEngineResult(Status.BUFFER_UNDERFLOW, HandshakeStatus.NEED_UNWRAP, 0, 0);
      OK_UNWRAP = new SSLEngineResult(Status.OK, HandshakeStatus.NEED_UNWRAP, 0, 0);
      CLOSED_STATE = new SSLEngine() {
         public SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
            throw new SSLException(new ClosedChannelException());
         }

         public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
            throw new SSLException(new ClosedChannelException());
         }

         public Runnable getDelegatedTask() {
            return null;
         }

         public void closeInbound() throws SSLException {
         }

         public boolean isInboundDone() {
            return true;
         }

         public void closeOutbound() {
         }

         public boolean isOutboundDone() {
            return true;
         }

         public String[] getSupportedCipherSuites() {
            throw new UnsupportedOperationException();
         }

         public String[] getEnabledCipherSuites() {
            throw new UnsupportedOperationException();
         }

         public void setEnabledCipherSuites(String[] suites) {
            throw new UnsupportedOperationException();
         }

         public String[] getSupportedProtocols() {
            throw new UnsupportedOperationException();
         }

         public String[] getEnabledProtocols() {
            throw new UnsupportedOperationException();
         }

         public void setEnabledProtocols(String[] protocols) {
            throw new UnsupportedOperationException();
         }

         public SSLSession getSession() {
            return null;
         }

         public void beginHandshake() throws SSLException {
            throw new SSLException(new ClosedChannelException());
         }

         public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
            return HandshakeStatus.NOT_HANDSHAKING;
         }

         public void setUseClientMode(boolean mode) {
            throw new UnsupportedOperationException();
         }

         public boolean getUseClientMode() {
            return false;
         }

         public void setNeedClientAuth(boolean need) {
         }

         public boolean getNeedClientAuth() {
            return false;
         }

         public void setWantClientAuth(boolean want) {
         }

         public boolean getWantClientAuth() {
            return false;
         }

         public void setEnableSessionCreation(boolean flag) {
         }

         public boolean getEnableSessionCreation() {
            return false;
         }
      };
   }

   class InitialState extends SSLEngine {
      private final SNIContextMatcher selector;
      private final AtomicInteger flags = new AtomicInteger(4);
      private final Function<SSLContext, SSLEngine> engineFunction;
      private int packetBufferSize = 5;
      private String[] enabledSuites;
      private String[] enabledProtocols;
      private final SSLSession handshakeSession = new SSLSession() {
         public byte[] getId() {
            throw new UnsupportedOperationException();
         }

         public SSLSessionContext getSessionContext() {
            throw new UnsupportedOperationException();
         }

         public long getCreationTime() {
            throw new UnsupportedOperationException();
         }

         public long getLastAccessedTime() {
            throw new UnsupportedOperationException();
         }

         public void invalidate() {
            throw new UnsupportedOperationException();
         }

         public boolean isValid() {
            return false;
         }

         public void putValue(String s, Object o) {
            throw new UnsupportedOperationException();
         }

         public Object getValue(String s) {
            return null;
         }

         public void removeValue(String s) {
         }

         public String[] getValueNames() {
            throw new UnsupportedOperationException();
         }

         public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
            throw new UnsupportedOperationException();
         }

         public Certificate[] getLocalCertificates() {
            return null;
         }

         public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
            throw new UnsupportedOperationException();
         }

         public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
            throw new UnsupportedOperationException();
         }

         public Principal getLocalPrincipal() {
            throw new UnsupportedOperationException();
         }

         public String getCipherSuite() {
            throw new UnsupportedOperationException();
         }

         public String getProtocol() {
            throw new UnsupportedOperationException();
         }

         public String getPeerHost() {
            return SNISSLEngine.this.getPeerHost();
         }

         public int getPeerPort() {
            return SNISSLEngine.this.getPeerPort();
         }

         public int getPacketBufferSize() {
            return InitialState.this.packetBufferSize;
         }

         public int getApplicationBufferSize() {
            throw new UnsupportedOperationException();
         }
      };

      InitialState(SNIContextMatcher selector, Function<SSLContext, SSLEngine> engineFunction) {
         this.selector = selector;
         this.engineFunction = engineFunction;
      }

      public SSLSession getHandshakeSession() {
         return this.handshakeSession;
      }

      public SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
         return SNISSLEngine.OK_UNWRAP;
      }

      public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
         int mark = src.position();

         SSLEngine next;
         try {
            if (src.remaining() < 5) {
               this.packetBufferSize = 5;
               SSLEngineResult var14 = SNISSLEngine.UNDERFLOW_UNWRAP;
               return var14;
            }

            int requiredSize = SNISSLExplorer.getRequiredSize(src);
            if (src.remaining() < requiredSize) {
               this.packetBufferSize = requiredSize;
               SSLEngineResult var15 = SNISSLEngine.UNDERFLOW_UNWRAP;
               return var15;
            }

            List<SNIServerName> names = SNISSLExplorer.explore(src);
            SSLContext sslContext = this.selector.getContext(names);
            if (sslContext == null) {
               throw UndertowMessages.MESSAGES.noContextForSslConnection();
            }

            next = (SSLEngine)this.engineFunction.apply(sslContext);
            if (this.enabledSuites != null) {
               next.setEnabledCipherSuites(this.enabledSuites);
            }

            if (this.enabledProtocols != null) {
               next.setEnabledProtocols(this.enabledProtocols);
            }

            next.setUseClientMode(false);
            int flagsVal = this.flags.get();
            if ((flagsVal & 1) != 0) {
               next.setWantClientAuth(true);
            } else if ((flagsVal & 2) != 0) {
               next.setNeedClientAuth(true);
            }

            if ((flagsVal & 4) != 0) {
               next.setEnableSessionCreation(true);
            }

            next = (SSLEngine)SNISSLEngine.this.selectionCallback.apply(next);
            SNISSLEngine.this.currentRef.set(next);
         } finally {
            src.position(mark);
         }

         return next.unwrap(src, dsts, offset, length);
      }

      public Runnable getDelegatedTask() {
         return null;
      }

      public void closeInbound() throws SSLException {
         SNISSLEngine.this.currentRef.set(SNISSLEngine.CLOSED_STATE);
      }

      public boolean isInboundDone() {
         return false;
      }

      public void closeOutbound() {
         SNISSLEngine.this.currentRef.set(SNISSLEngine.CLOSED_STATE);
      }

      public boolean isOutboundDone() {
         return false;
      }

      public String[] getSupportedCipherSuites() {
         return this.enabledSuites == null ? new String[0] : this.enabledSuites;
      }

      public String[] getEnabledCipherSuites() {
         return this.enabledSuites;
      }

      public void setEnabledCipherSuites(String[] suites) {
         this.enabledSuites = suites;
      }

      public String[] getSupportedProtocols() {
         return this.enabledProtocols == null ? new String[0] : this.enabledProtocols;
      }

      public String[] getEnabledProtocols() {
         return this.enabledProtocols;
      }

      public void setEnabledProtocols(String[] protocols) {
         this.enabledProtocols = protocols;
      }

      public SSLSession getSession() {
         return null;
      }

      public void beginHandshake() throws SSLException {
      }

      public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
         return HandshakeStatus.NEED_UNWRAP;
      }

      public void setUseClientMode(boolean mode) {
         if (mode) {
            throw new UnsupportedOperationException();
         }
      }

      public boolean getUseClientMode() {
         return false;
      }

      public void setNeedClientAuth(boolean need) {
         AtomicInteger flags = this.flags;

         int oldVal;
         int newVal;
         do {
            oldVal = flags.get();
            if ((oldVal & 2) != 0 == need) {
               return;
            }

            newVal = oldVal & 4 | 2;
         } while(!flags.compareAndSet(oldVal, newVal));

      }

      public boolean getNeedClientAuth() {
         return (this.flags.get() & 2) != 0;
      }

      public void setWantClientAuth(boolean want) {
         AtomicInteger flags = this.flags;

         int oldVal;
         int newVal;
         do {
            oldVal = flags.get();
            if ((oldVal & 1) != 0 == want) {
               return;
            }

            newVal = oldVal & 4 | 1;
         } while(!flags.compareAndSet(oldVal, newVal));

      }

      public boolean getWantClientAuth() {
         return (this.flags.get() & 1) != 0;
      }

      public void setEnableSessionCreation(boolean flag) {
         AtomicInteger flags = this.flags;

         int oldVal;
         int newVal;
         do {
            oldVal = flags.get();
            if ((oldVal & 4) != 0 == flag) {
               return;
            }

            newVal = oldVal ^ 4;
         } while(!flags.compareAndSet(oldVal, newVal));

      }

      public boolean getEnableSessionCreation() {
         return (this.flags.get() & 4) != 0;
      }
   }
}
