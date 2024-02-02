/*     */ package io.undertow.protocols.ssl;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.security.Principal;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.function.Function;
/*     */ import javax.net.ssl.SNIServerName;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLEngineResult;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLParameters;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSessionContext;
/*     */ import javax.security.cert.X509Certificate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SNISSLEngine
/*     */   extends SSLEngine
/*     */ {
/*  45 */   private static final SSLEngineResult UNDERFLOW_UNWRAP = new SSLEngineResult(SSLEngineResult.Status.BUFFER_UNDERFLOW, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
/*  46 */   private static final SSLEngineResult OK_UNWRAP = new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
/*     */   private final AtomicReference<SSLEngine> currentRef;
/*  48 */   private Function<SSLEngine, SSLEngine> selectionCallback = Function.identity(); static final int FL_WANT_C_AUTH = 1;
/*     */   
/*     */   SNISSLEngine(SNIContextMatcher selector) {
/*  51 */     this.currentRef = new AtomicReference<>(new InitialState(selector, SSLContext::createSSLEngine));
/*     */   }
/*     */   static final int FL_NEED_C_AUTH = 2; static final int FL_SESSION_CRE = 4;
/*     */   SNISSLEngine(SNIContextMatcher selector, String host, int port) {
/*  55 */     super(host, port);
/*  56 */     this.currentRef = new AtomicReference<>(new InitialState(selector, sslContext -> sslContext.createSSLEngine(host, port)));
/*     */   }
/*     */   
/*     */   public Function<SSLEngine, SSLEngine> getSelectionCallback() {
/*  60 */     return this.selectionCallback;
/*     */   }
/*     */   
/*     */   public void setSelectionCallback(Function<SSLEngine, SSLEngine> selectionCallback) {
/*  64 */     this.selectionCallback = selectionCallback;
/*     */   }
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
/*  68 */     return ((SSLEngine)this.currentRef.get()).wrap(srcs, offset, length, dst);
/*     */   }
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
/*  72 */     return ((SSLEngine)this.currentRef.get()).wrap(src, dst);
/*     */   }
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer[] srcs, ByteBuffer dst) throws SSLException {
/*  76 */     return ((SSLEngine)this.currentRef.get()).wrap(srcs, dst);
/*     */   }
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
/*  80 */     return ((SSLEngine)this.currentRef.get()).unwrap(src, dsts, offset, length);
/*     */   }
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
/*  84 */     return ((SSLEngine)this.currentRef.get()).unwrap(src, dst);
/*     */   }
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException {
/*  88 */     return ((SSLEngine)this.currentRef.get()).unwrap(src, dsts);
/*     */   }
/*     */   
/*     */   public String getPeerHost() {
/*  92 */     return ((SSLEngine)this.currentRef.get()).getPeerHost();
/*     */   }
/*     */   
/*     */   public int getPeerPort() {
/*  96 */     return ((SSLEngine)this.currentRef.get()).getPeerPort();
/*     */   }
/*     */   
/*     */   public SSLSession getHandshakeSession() {
/* 100 */     return ((SSLEngine)this.currentRef.get()).getHandshakeSession();
/*     */   }
/*     */   
/*     */   public SSLParameters getSSLParameters() {
/* 104 */     return ((SSLEngine)this.currentRef.get()).getSSLParameters();
/*     */   }
/*     */   
/*     */   public void setSSLParameters(SSLParameters params) {
/* 108 */     ((SSLEngine)this.currentRef.get()).setSSLParameters(params);
/*     */   }
/*     */   
/*     */   public Runnable getDelegatedTask() {
/* 112 */     return ((SSLEngine)this.currentRef.get()).getDelegatedTask();
/*     */   }
/*     */   
/*     */   public void closeInbound() throws SSLException {
/* 116 */     ((SSLEngine)this.currentRef.get()).closeInbound();
/*     */   }
/*     */   
/*     */   public boolean isInboundDone() {
/* 120 */     return ((SSLEngine)this.currentRef.get()).isInboundDone();
/*     */   }
/*     */   
/*     */   public void closeOutbound() {
/* 124 */     ((SSLEngine)this.currentRef.get()).closeOutbound();
/*     */   }
/*     */   
/*     */   public boolean isOutboundDone() {
/* 128 */     return ((SSLEngine)this.currentRef.get()).isOutboundDone();
/*     */   }
/*     */   
/*     */   public String[] getSupportedCipherSuites() {
/* 132 */     return ((SSLEngine)this.currentRef.get()).getSupportedCipherSuites();
/*     */   }
/*     */   
/*     */   public String[] getEnabledCipherSuites() {
/* 136 */     return ((SSLEngine)this.currentRef.get()).getEnabledCipherSuites();
/*     */   }
/*     */   
/*     */   public void setEnabledCipherSuites(String[] cipherSuites) {
/* 140 */     ((SSLEngine)this.currentRef.get()).setEnabledCipherSuites(cipherSuites);
/*     */   }
/*     */   
/*     */   public String[] getSupportedProtocols() {
/* 144 */     return ((SSLEngine)this.currentRef.get()).getSupportedProtocols();
/*     */   }
/*     */   
/*     */   public String[] getEnabledProtocols() {
/* 148 */     return ((SSLEngine)this.currentRef.get()).getEnabledProtocols();
/*     */   }
/*     */   
/*     */   public void setEnabledProtocols(String[] protocols) {
/* 152 */     ((SSLEngine)this.currentRef.get()).setEnabledProtocols(protocols);
/*     */   }
/*     */   
/*     */   public SSLSession getSession() {
/* 156 */     return ((SSLEngine)this.currentRef.get()).getSession();
/*     */   }
/*     */   
/*     */   public void beginHandshake() throws SSLException {
/* 160 */     ((SSLEngine)this.currentRef.get()).beginHandshake();
/*     */   }
/*     */   
/*     */   public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
/* 164 */     return ((SSLEngine)this.currentRef.get()).getHandshakeStatus();
/*     */   }
/*     */   
/*     */   public void setUseClientMode(boolean clientMode) {
/* 168 */     ((SSLEngine)this.currentRef.get()).setUseClientMode(clientMode);
/*     */   }
/*     */   
/*     */   public boolean getUseClientMode() {
/* 172 */     return ((SSLEngine)this.currentRef.get()).getUseClientMode();
/*     */   }
/*     */   
/*     */   public void setNeedClientAuth(boolean clientAuth) {
/* 176 */     ((SSLEngine)this.currentRef.get()).setNeedClientAuth(clientAuth);
/*     */   }
/*     */   
/*     */   public boolean getNeedClientAuth() {
/* 180 */     return ((SSLEngine)this.currentRef.get()).getNeedClientAuth();
/*     */   }
/*     */   
/*     */   public void setWantClientAuth(boolean want) {
/* 184 */     ((SSLEngine)this.currentRef.get()).setWantClientAuth(want);
/*     */   }
/*     */   
/*     */   public boolean getWantClientAuth() {
/* 188 */     return ((SSLEngine)this.currentRef.get()).getWantClientAuth();
/*     */   }
/*     */   
/*     */   public void setEnableSessionCreation(boolean flag) {
/* 192 */     ((SSLEngine)this.currentRef.get()).setEnableSessionCreation(flag);
/*     */   }
/*     */   
/*     */   public boolean getEnableSessionCreation() {
/* 196 */     return ((SSLEngine)this.currentRef.get()).getEnableSessionCreation();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   class InitialState
/*     */     extends SSLEngine
/*     */   {
/*     */     private final SNIContextMatcher selector;
/*     */     
/* 206 */     private final AtomicInteger flags = new AtomicInteger(4);
/*     */     private final Function<SSLContext, SSLEngine> engineFunction;
/* 208 */     private int packetBufferSize = 5;
/*     */     private String[] enabledSuites;
/*     */     private String[] enabledProtocols;
/*     */     
/* 212 */     private final SSLSession handshakeSession = new SSLSession() {
/*     */         public byte[] getId() {
/* 214 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public SSLSessionContext getSessionContext() {
/* 218 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public long getCreationTime() {
/* 222 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public long getLastAccessedTime() {
/* 226 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public void invalidate() {
/* 230 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public boolean isValid() {
/* 234 */           return false;
/*     */         }
/*     */         
/*     */         public void putValue(String s, Object o) {
/* 238 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public Object getValue(String s) {
/* 242 */           return null;
/*     */         }
/*     */ 
/*     */         
/*     */         public void removeValue(String s) {}
/*     */         
/*     */         public String[] getValueNames() {
/* 249 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
/* 253 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public Certificate[] getLocalCertificates() {
/* 257 */           return null;
/*     */         }
/*     */         
/*     */         public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
/* 261 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
/* 265 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public Principal getLocalPrincipal() {
/* 269 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public String getCipherSuite() {
/* 273 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public String getProtocol() {
/* 277 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         
/*     */         public String getPeerHost() {
/* 281 */           return SNISSLEngine.this.getPeerHost();
/*     */         }
/*     */         
/*     */         public int getPeerPort() {
/* 285 */           return SNISSLEngine.this.getPeerPort();
/*     */         }
/*     */         
/*     */         public int getPacketBufferSize() {
/* 289 */           return SNISSLEngine.InitialState.this.packetBufferSize;
/*     */         }
/*     */         
/*     */         public int getApplicationBufferSize() {
/* 293 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/*     */     
/*     */     InitialState(SNIContextMatcher selector, Function<SSLContext, SSLEngine> engineFunction) {
/* 298 */       this.selector = selector;
/* 299 */       this.engineFunction = engineFunction;
/*     */     }
/*     */     
/*     */     public SSLSession getHandshakeSession() {
/* 303 */       return this.handshakeSession;
/*     */     }
/*     */     
/*     */     public SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
/* 307 */       return SNISSLEngine.OK_UNWRAP;
/*     */     }
/*     */     
/*     */     public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
/*     */       SSLEngine next;
/* 312 */       int mark = src.position();
/*     */       try {
/* 314 */         if (src.remaining() < 5) {
/* 315 */           this.packetBufferSize = 5;
/* 316 */           return SNISSLEngine.UNDERFLOW_UNWRAP;
/*     */         } 
/* 318 */         int requiredSize = SNISSLExplorer.getRequiredSize(src);
/* 319 */         if (src.remaining() < requiredSize) {
/* 320 */           this.packetBufferSize = requiredSize;
/* 321 */           return SNISSLEngine.UNDERFLOW_UNWRAP;
/*     */         } 
/* 323 */         List<SNIServerName> names = SNISSLExplorer.explore(src);
/* 324 */         SSLContext sslContext = this.selector.getContext(names);
/* 325 */         if (sslContext == null)
/*     */         {
/* 327 */           throw UndertowMessages.MESSAGES.noContextForSslConnection();
/*     */         }
/* 329 */         next = this.engineFunction.apply(sslContext);
/* 330 */         if (this.enabledSuites != null) {
/* 331 */           next.setEnabledCipherSuites(this.enabledSuites);
/*     */         }
/* 333 */         if (this.enabledProtocols != null) {
/* 334 */           next.setEnabledProtocols(this.enabledProtocols);
/*     */         }
/* 336 */         next.setUseClientMode(false);
/* 337 */         int flagsVal = this.flags.get();
/* 338 */         if ((flagsVal & 0x1) != 0) {
/* 339 */           next.setWantClientAuth(true);
/* 340 */         } else if ((flagsVal & 0x2) != 0) {
/* 341 */           next.setNeedClientAuth(true);
/*     */         } 
/* 343 */         if ((flagsVal & 0x4) != 0) {
/* 344 */           next.setEnableSessionCreation(true);
/*     */         }
/* 346 */         next = SNISSLEngine.this.selectionCallback.apply(next);
/* 347 */         SNISSLEngine.this.currentRef.set(next);
/*     */       } finally {
/* 349 */         src.position(mark);
/*     */       } 
/* 351 */       return next.unwrap(src, dsts, offset, length);
/*     */     }
/*     */     
/*     */     public Runnable getDelegatedTask() {
/* 355 */       return null;
/*     */     }
/*     */     
/*     */     public void closeInbound() throws SSLException {
/* 359 */       SNISSLEngine.this.currentRef.set(SNISSLEngine.CLOSED_STATE);
/*     */     }
/*     */     
/*     */     public boolean isInboundDone() {
/* 363 */       return false;
/*     */     }
/*     */     
/*     */     public void closeOutbound() {
/* 367 */       SNISSLEngine.this.currentRef.set(SNISSLEngine.CLOSED_STATE);
/*     */     }
/*     */     
/*     */     public boolean isOutboundDone() {
/* 371 */       return false;
/*     */     }
/*     */     
/*     */     public String[] getSupportedCipherSuites() {
/* 375 */       if (this.enabledSuites == null) {
/* 376 */         return new String[0];
/*     */       }
/* 378 */       return this.enabledSuites;
/*     */     }
/*     */     
/*     */     public String[] getEnabledCipherSuites() {
/* 382 */       return this.enabledSuites;
/*     */     }
/*     */     
/*     */     public void setEnabledCipherSuites(String[] suites) {
/* 386 */       this.enabledSuites = suites;
/*     */     }
/*     */     
/*     */     public String[] getSupportedProtocols() {
/* 390 */       if (this.enabledProtocols == null) {
/* 391 */         return new String[0];
/*     */       }
/*     */       
/* 394 */       return this.enabledProtocols;
/*     */     }
/*     */     
/*     */     public String[] getEnabledProtocols() {
/* 398 */       return this.enabledProtocols;
/*     */     }
/*     */     
/*     */     public void setEnabledProtocols(String[] protocols) {
/* 402 */       this.enabledProtocols = protocols;
/*     */     }
/*     */     
/*     */     public SSLSession getSession() {
/* 406 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void beginHandshake() throws SSLException {}
/*     */     
/*     */     public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
/* 413 */       return SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
/*     */     }
/*     */     
/*     */     public void setUseClientMode(boolean mode) {
/* 417 */       if (mode) throw new UnsupportedOperationException(); 
/*     */     }
/*     */     
/*     */     public boolean getUseClientMode() {
/* 421 */       return false;
/*     */     }
/*     */     public void setNeedClientAuth(boolean need) {
/*     */       int oldVal, newVal;
/* 425 */       AtomicInteger flags = this.flags;
/*     */       
/*     */       do {
/* 428 */         oldVal = flags.get();
/* 429 */         if ((((oldVal & 0x2) != 0)) == need) {
/*     */           return;
/*     */         }
/* 432 */         newVal = oldVal & 0x4 | 0x2;
/* 433 */       } while (!flags.compareAndSet(oldVal, newVal));
/*     */     }
/*     */     
/*     */     public boolean getNeedClientAuth() {
/* 437 */       return ((this.flags.get() & 0x2) != 0);
/*     */     }
/*     */     public void setWantClientAuth(boolean want) {
/*     */       int oldVal, newVal;
/* 441 */       AtomicInteger flags = this.flags;
/*     */       
/*     */       do {
/* 444 */         oldVal = flags.get();
/* 445 */         if ((((oldVal & 0x1) != 0)) == want) {
/*     */           return;
/*     */         }
/* 448 */         newVal = oldVal & 0x4 | 0x1;
/* 449 */       } while (!flags.compareAndSet(oldVal, newVal));
/*     */     }
/*     */     
/*     */     public boolean getWantClientAuth() {
/* 453 */       return ((this.flags.get() & 0x1) != 0);
/*     */     }
/*     */     public void setEnableSessionCreation(boolean flag) {
/*     */       int oldVal, newVal;
/* 457 */       AtomicInteger flags = this.flags;
/*     */       
/*     */       do {
/* 460 */         oldVal = flags.get();
/* 461 */         if ((((oldVal & 0x4) != 0)) == flag) {
/*     */           return;
/*     */         }
/* 464 */         newVal = oldVal ^ 0x4;
/* 465 */       } while (!flags.compareAndSet(oldVal, newVal));
/*     */     }
/*     */     
/*     */     public boolean getEnableSessionCreation() {
/* 469 */       return ((this.flags.get() & 0x4) != 0);
/*     */     }
/*     */   }
/*     */   
/* 473 */   static final SSLEngine CLOSED_STATE = new SSLEngine() {
/*     */       public SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
/* 475 */         throw new SSLException(new ClosedChannelException());
/*     */       }
/*     */       
/*     */       public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
/* 479 */         throw new SSLException(new ClosedChannelException());
/*     */       }
/*     */       
/*     */       public Runnable getDelegatedTask() {
/* 483 */         return null;
/*     */       }
/*     */ 
/*     */       
/*     */       public void closeInbound() throws SSLException {}
/*     */       
/*     */       public boolean isInboundDone() {
/* 490 */         return true;
/*     */       }
/*     */ 
/*     */       
/*     */       public void closeOutbound() {}
/*     */ 
/*     */       
/*     */       public boolean isOutboundDone() {
/* 498 */         return true;
/*     */       }
/*     */       
/*     */       public String[] getSupportedCipherSuites() {
/* 502 */         throw new UnsupportedOperationException();
/*     */       }
/*     */       
/*     */       public String[] getEnabledCipherSuites() {
/* 506 */         throw new UnsupportedOperationException();
/*     */       }
/*     */       
/*     */       public void setEnabledCipherSuites(String[] suites) {
/* 510 */         throw new UnsupportedOperationException();
/*     */       }
/*     */       
/*     */       public String[] getSupportedProtocols() {
/* 514 */         throw new UnsupportedOperationException();
/*     */       }
/*     */       
/*     */       public String[] getEnabledProtocols() {
/* 518 */         throw new UnsupportedOperationException();
/*     */       }
/*     */       
/*     */       public void setEnabledProtocols(String[] protocols) {
/* 522 */         throw new UnsupportedOperationException();
/*     */       }
/*     */       
/*     */       public SSLSession getSession() {
/* 526 */         return null;
/*     */       }
/*     */       
/*     */       public void beginHandshake() throws SSLException {
/* 530 */         throw new SSLException(new ClosedChannelException());
/*     */       }
/*     */       
/*     */       public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
/* 534 */         return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
/*     */       }
/*     */       
/*     */       public void setUseClientMode(boolean mode) {
/* 538 */         throw new UnsupportedOperationException();
/*     */       }
/*     */       
/*     */       public boolean getUseClientMode() {
/* 542 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       public void setNeedClientAuth(boolean need) {}
/*     */       
/*     */       public boolean getNeedClientAuth() {
/* 549 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       public void setWantClientAuth(boolean want) {}
/*     */       
/*     */       public boolean getWantClientAuth() {
/* 556 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       public void setEnableSessionCreation(boolean flag) {}
/*     */       
/*     */       public boolean getEnableSessionCreation() {
/* 563 */         return false;
/*     */       }
/*     */     };
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\SNISSLEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */