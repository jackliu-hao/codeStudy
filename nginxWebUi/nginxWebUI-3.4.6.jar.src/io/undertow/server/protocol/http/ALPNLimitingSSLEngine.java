/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLEngineResult;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLParameters;
/*     */ import javax.net.ssl.SSLSession;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ALPNLimitingSSLEngine
/*     */   extends SSLEngine
/*     */ {
/*  40 */   private static final SSLEngineResult UNDERFLOW_RESULT = new SSLEngineResult(SSLEngineResult.Status.BUFFER_UNDERFLOW, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
/*     */   
/*     */   private final SSLEngine delegate;
/*     */   
/*     */   private final Runnable invalidAlpnRunnable;
/*     */   private boolean done;
/*     */   
/*     */   public ALPNLimitingSSLEngine(SSLEngine delegate, Runnable invalidAlpnRunnable) {
/*  48 */     this.delegate = delegate;
/*  49 */     this.invalidAlpnRunnable = invalidAlpnRunnable;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPeerHost() {
/*  54 */     return this.delegate.getPeerHost();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPeerPort() {
/*  59 */     return this.delegate.getPeerPort();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
/*  64 */     return this.delegate.wrap(src, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer[] srcs, ByteBuffer dst) throws SSLException {
/*  69 */     return wrap(srcs, 0, srcs.length, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
/*  74 */     if (this.done) {
/*  75 */       return this.delegate.unwrap(src, dst);
/*     */     }
/*  77 */     if (ALPNOfferedClientHelloExplorer.isIncompleteHeader(src)) {
/*  78 */       return UNDERFLOW_RESULT;
/*     */     }
/*     */     try {
/*  81 */       List<Integer> clientCiphers = ALPNOfferedClientHelloExplorer.parseClientHello(src);
/*  82 */       if (clientCiphers != null) {
/*  83 */         limitCiphers(clientCiphers);
/*  84 */         this.done = true;
/*     */       } else {
/*  86 */         this.done = true;
/*     */       } 
/*  88 */     } catch (BufferUnderflowException e) {
/*  89 */       return UNDERFLOW_RESULT;
/*     */     } 
/*  91 */     return this.delegate.unwrap(src, dst);
/*     */   }
/*     */   
/*     */   private void limitCiphers(List<Integer> clientCiphers) {
/*  95 */     boolean clientIsCompliant = false;
/*  96 */     for (Iterator<Integer> iterator = clientCiphers.iterator(); iterator.hasNext(); ) { int cipher = ((Integer)iterator.next()).intValue();
/*  97 */       if (cipher == 49199) {
/*  98 */         clientIsCompliant = true;
/*     */       } }
/*     */     
/* 101 */     if (!clientIsCompliant) {
/* 102 */       this.invalidAlpnRunnable.run();
/*     */     } else {
/* 104 */       List<String> ciphers = new ArrayList<>();
/* 105 */       for (String cipher : this.delegate.getEnabledCipherSuites()) {
/* 106 */         if (ALPNBannedCiphers.isAllowed(cipher)) {
/* 107 */           ciphers.add(cipher);
/*     */         }
/*     */       } 
/* 110 */       this.delegate.setEnabledCipherSuites(ciphers.<String>toArray(new String[ciphers.size()]));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException {
/* 116 */     return unwrap(src, dsts, 0, dsts.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getHandshakeSession() {
/* 121 */     return this.delegate.getHandshakeSession();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLParameters getSSLParameters() {
/* 126 */     return this.delegate.getSSLParameters();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSSLParameters(SSLParameters sslParameters) {
/* 131 */     this.delegate.setSSLParameters(sslParameters);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer[] srcs, int off, int len, ByteBuffer dst) throws SSLException {
/* 136 */     return this.delegate.wrap(srcs, off, len, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers, int i, int i1) throws SSLException {
/* 141 */     if (this.done) {
/* 142 */       return this.delegate.unwrap(byteBuffer, byteBuffers, i, i1);
/*     */     }
/*     */     
/* 145 */     if (ALPNOfferedClientHelloExplorer.isIncompleteHeader(byteBuffer)) {
/* 146 */       return UNDERFLOW_RESULT;
/*     */     }
/*     */     try {
/* 149 */       List<Integer> clientCiphers = ALPNOfferedClientHelloExplorer.parseClientHello(byteBuffer);
/* 150 */       if (clientCiphers != null) {
/* 151 */         limitCiphers(clientCiphers);
/* 152 */         this.done = true;
/*     */       } else {
/* 154 */         this.done = true;
/*     */       } 
/* 156 */     } catch (BufferUnderflowException e) {
/* 157 */       return UNDERFLOW_RESULT;
/*     */     } 
/* 159 */     return this.delegate.unwrap(byteBuffer, byteBuffers, i, i1);
/*     */   }
/*     */ 
/*     */   
/*     */   public Runnable getDelegatedTask() {
/* 164 */     return this.delegate.getDelegatedTask();
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeInbound() throws SSLException {
/* 169 */     this.delegate.closeInbound();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInboundDone() {
/* 174 */     return this.delegate.isInboundDone();
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeOutbound() {
/* 179 */     this.delegate.closeOutbound();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOutboundDone() {
/* 184 */     return this.delegate.isOutboundDone();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getSupportedCipherSuites() {
/* 189 */     return this.delegate.getSupportedCipherSuites();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getEnabledCipherSuites() {
/* 194 */     return this.delegate.getEnabledCipherSuites();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnabledCipherSuites(String[] strings) {
/* 199 */     this.delegate.setEnabledCipherSuites(strings);
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getSupportedProtocols() {
/* 204 */     return this.delegate.getSupportedProtocols();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getEnabledProtocols() {
/* 209 */     return this.delegate.getEnabledProtocols();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnabledProtocols(String[] strings) {
/* 214 */     this.delegate.setEnabledProtocols(strings);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSession() {
/* 219 */     return this.delegate.getSession();
/*     */   }
/*     */ 
/*     */   
/*     */   public void beginHandshake() throws SSLException {
/* 224 */     this.delegate.beginHandshake();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
/* 229 */     return this.delegate.getHandshakeStatus();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUseClientMode(boolean b) {
/* 234 */     if (b) {
/* 235 */       throw new IllegalArgumentException();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getUseClientMode() {
/* 241 */     return this.delegate.getUseClientMode();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNeedClientAuth(boolean b) {
/* 246 */     this.delegate.setNeedClientAuth(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getNeedClientAuth() {
/* 251 */     return this.delegate.getNeedClientAuth();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWantClientAuth(boolean b) {
/* 256 */     this.delegate.setWantClientAuth(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWantClientAuth() {
/* 261 */     return this.delegate.getWantClientAuth();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnableSessionCreation(boolean b) {
/* 266 */     this.delegate.setEnableSessionCreation(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getEnableSessionCreation() {
/* 271 */     return this.delegate.getEnableSessionCreation();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\ALPNLimitingSSLEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */