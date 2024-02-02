/*     */ package io.undertow.protocols.ssl;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLEngineResult;
/*     */ import javax.net.ssl.SSLException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ALPNHackSSLEngine
/*     */   extends SSLEngine
/*     */ {
/*     */   public static final boolean ENABLED;
/*     */   private static final Field HANDSHAKER;
/*     */   private static final Field HANDSHAKER_PROTOCOL_VERSION;
/*     */   private static final Field HANDSHAKE_HASH;
/*     */   private static final Field HANDSHAKE_HASH_VERSION;
/*     */   private static final Method HANDSHAKE_HASH_UPDATE;
/*     */   private static final Method HANDSHAKE_HASH_PROTOCOL_DETERMINED;
/*     */   private static final Field HANDSHAKE_HASH_DATA;
/*     */   private static final Field HANDSHAKE_HASH_FIN_MD;
/*     */   private static final Class<?> SSL_ENGINE_IMPL_CLASS;
/*     */   private final SSLEngine delegate;
/*     */   
/*     */   static {
/*     */     Field handshaker, handshakeHash, handshakeHashVersion, handshakeHashData, handshakeHashFinMd, protocolVersion;
/*     */     Method handshakeHashUpdate, handshakeHashProtocolDetermined;
/*     */     Class<?> sslEngineImpleClass;
/*  67 */     boolean enabled = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  78 */       Class<?> protocolVersionClass = Class.forName("sun.security.ssl.ProtocolVersion", true, ClassLoader.getSystemClassLoader());
/*  79 */       sslEngineImpleClass = Class.forName("sun.security.ssl.SSLEngineImpl", true, ClassLoader.getSystemClassLoader());
/*  80 */       handshaker = sslEngineImpleClass.getDeclaredField("handshaker");
/*  81 */       handshaker.setAccessible(true);
/*  82 */       handshakeHash = handshaker.getType().getDeclaredField("handshakeHash");
/*  83 */       handshakeHash.setAccessible(true);
/*  84 */       protocolVersion = handshaker.getType().getDeclaredField("protocolVersion");
/*  85 */       protocolVersion.setAccessible(true);
/*  86 */       handshakeHashVersion = handshakeHash.getType().getDeclaredField("version");
/*  87 */       handshakeHashVersion.setAccessible(true);
/*  88 */       handshakeHashUpdate = handshakeHash.getType().getDeclaredMethod("update", new Class[] { byte[].class, int.class, int.class });
/*  89 */       handshakeHashUpdate.setAccessible(true);
/*  90 */       handshakeHashProtocolDetermined = handshakeHash.getType().getDeclaredMethod("protocolDetermined", new Class[] { protocolVersionClass });
/*  91 */       handshakeHashProtocolDetermined.setAccessible(true);
/*  92 */       handshakeHashData = handshakeHash.getType().getDeclaredField("data");
/*  93 */       handshakeHashData.setAccessible(true);
/*  94 */       handshakeHashFinMd = handshakeHash.getType().getDeclaredField("finMD");
/*  95 */       handshakeHashFinMd.setAccessible(true);
/*     */     }
/*  97 */     catch (Exception e) {
/*  98 */       UndertowLogger.ROOT_LOGGER.debug("JDK8 ALPN Hack failed ", e);
/*  99 */       enabled = false;
/* 100 */       handshaker = null;
/* 101 */       handshakeHash = null;
/* 102 */       handshakeHashVersion = null;
/* 103 */       handshakeHashUpdate = null;
/* 104 */       handshakeHashProtocolDetermined = null;
/* 105 */       handshakeHashData = null;
/* 106 */       handshakeHashFinMd = null;
/* 107 */       protocolVersion = null;
/* 108 */       sslEngineImpleClass = null;
/*     */     } 
/* 110 */     ENABLED = (enabled && !Boolean.getBoolean("io.undertow.disable-jdk8-alpn"));
/* 111 */     HANDSHAKER = handshaker;
/* 112 */     HANDSHAKE_HASH = handshakeHash;
/* 113 */     HANDSHAKE_HASH_PROTOCOL_DETERMINED = handshakeHashProtocolDetermined;
/* 114 */     HANDSHAKE_HASH_VERSION = handshakeHashVersion;
/* 115 */     HANDSHAKE_HASH_UPDATE = handshakeHashUpdate;
/* 116 */     HANDSHAKE_HASH_DATA = handshakeHashData;
/* 117 */     HANDSHAKE_HASH_FIN_MD = handshakeHashFinMd;
/* 118 */     HANDSHAKER_PROTOCOL_VERSION = protocolVersion;
/* 119 */     SSL_ENGINE_IMPL_CLASS = sslEngineImpleClass;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean unwrapHelloSeen = false;
/*     */   
/*     */   private boolean ourHelloSent = false;
/*     */   
/*     */   private ALPNHackServerByteArrayOutputStream alpnHackServerByteArrayOutputStream;
/*     */   private ALPNHackClientByteArrayOutputStream ALPNHackClientByteArrayOutputStream;
/*     */   private List<String> applicationProtocols;
/*     */   private String selectedApplicationProtocol;
/*     */   private ByteBuffer bufferedWrapData;
/*     */   
/*     */   public ALPNHackSSLEngine(SSLEngine delegate) {
/* 134 */     this.delegate = delegate;
/*     */   }
/*     */   
/*     */   public static boolean isEnabled(SSLEngine engine) {
/* 138 */     if (!ENABLED) {
/* 139 */       return false;
/*     */     }
/* 141 */     return SSL_ENGINE_IMPL_CLASS.isAssignableFrom(engine.getClass());
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer[] byteBuffers, int i, int i1, ByteBuffer byteBuffer) throws SSLException {
/* 146 */     if (this.bufferedWrapData != null) {
/* 147 */       int prod = this.bufferedWrapData.remaining();
/* 148 */       byteBuffer.put(this.bufferedWrapData);
/* 149 */       this.bufferedWrapData = null;
/* 150 */       return new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, prod);
/*     */     } 
/* 152 */     int pos = byteBuffer.position();
/* 153 */     int limit = byteBuffer.limit();
/* 154 */     SSLEngineResult res = this.delegate.wrap(byteBuffers, i, i1, byteBuffer);
/* 155 */     if (!this.ourHelloSent && res.bytesProduced() > 0) {
/* 156 */       if (this.delegate.getUseClientMode() && this.applicationProtocols != null && !this.applicationProtocols.isEmpty()) {
/* 157 */         this.ourHelloSent = true;
/* 158 */         this.ALPNHackClientByteArrayOutputStream = replaceClientByteOutput(this.delegate);
/* 159 */         ByteBuffer newBuf = byteBuffer.duplicate();
/* 160 */         newBuf.flip();
/* 161 */         byte[] data = new byte[newBuf.remaining()];
/* 162 */         newBuf.get(data);
/* 163 */         byte[] newData = ALPNHackClientHelloExplorer.rewriteClientHello(data, this.applicationProtocols);
/* 164 */         if (newData != null) {
/* 165 */           byte[] clientHelloMesage = new byte[newData.length - 5];
/* 166 */           System.arraycopy(newData, 5, clientHelloMesage, 0, clientHelloMesage.length);
/* 167 */           this.ALPNHackClientByteArrayOutputStream.setSentClientHello(clientHelloMesage);
/* 168 */           byteBuffer.clear();
/* 169 */           byteBuffer.put(newData);
/*     */         } 
/* 171 */       } else if (!getUseClientMode() && 
/* 172 */         this.selectedApplicationProtocol != null && this.alpnHackServerByteArrayOutputStream != null) {
/* 173 */         byte[] newServerHello = this.alpnHackServerByteArrayOutputStream.getServerHello();
/* 174 */         if (newServerHello != null) {
/* 175 */           byteBuffer.flip();
/* 176 */           List<ByteBuffer> records = ALPNHackServerHelloExplorer.extractRecords(byteBuffer);
/* 177 */           ByteBuffer newData = ALPNHackServerHelloExplorer.createNewOutputRecords(newServerHello, records);
/* 178 */           byteBuffer.position(pos);
/* 179 */           byteBuffer.limit(limit);
/* 180 */           if (newData.remaining() > byteBuffer.remaining()) {
/* 181 */             int old = newData.limit();
/* 182 */             newData.limit(newData.position() + byteBuffer.remaining());
/* 183 */             res = new SSLEngineResult(res.getStatus(), res.getHandshakeStatus(), res.bytesConsumed(), newData.remaining());
/* 184 */             byteBuffer.put(newData);
/* 185 */             newData.limit(old);
/* 186 */             this.bufferedWrapData = newData;
/*     */           } else {
/* 188 */             res = new SSLEngineResult(res.getStatus(), res.getHandshakeStatus(), res.bytesConsumed(), newData.remaining());
/* 189 */             byteBuffer.put(newData);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 195 */     if (res.bytesProduced() > 0) {
/* 196 */       this.ourHelloSent = true;
/*     */     }
/* 198 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer dataToUnwrap, ByteBuffer[] byteBuffers, int i, int i1) throws SSLException {
/* 203 */     if (!this.unwrapHelloSeen) {
/* 204 */       if (!this.delegate.getUseClientMode() && this.applicationProtocols != null) {
/*     */         try {
/* 206 */           List<String> result = ALPNHackClientHelloExplorer.exploreClientHello(dataToUnwrap.duplicate());
/* 207 */           if (result != null) {
/* 208 */             for (String protocol : this.applicationProtocols) {
/* 209 */               if (result.contains(protocol)) {
/* 210 */                 this.selectedApplicationProtocol = protocol;
/*     */                 break;
/*     */               } 
/*     */             } 
/*     */           }
/* 215 */           this.unwrapHelloSeen = true;
/* 216 */         } catch (BufferUnderflowException e) {
/* 217 */           return new SSLEngineResult(SSLEngineResult.Status.BUFFER_UNDERFLOW, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
/*     */         } 
/* 219 */       } else if (this.delegate.getUseClientMode() && this.ALPNHackClientByteArrayOutputStream != null) {
/* 220 */         if (!dataToUnwrap.hasRemaining()) {
/* 221 */           return this.delegate.unwrap(dataToUnwrap, byteBuffers, i, i1);
/*     */         }
/*     */         try {
/* 224 */           ByteBuffer dup = dataToUnwrap.duplicate();
/* 225 */           int type = dup.get();
/* 226 */           int major = dup.get();
/* 227 */           int minor = dup.get();
/* 228 */           if (type == 22 && major == 3 && minor == 3) {
/*     */ 
/*     */             
/* 231 */             List<ByteBuffer> records = ALPNHackServerHelloExplorer.extractRecords(dataToUnwrap.duplicate());
/*     */             
/* 233 */             ByteBuffer firstRecord = records.get(0);
/*     */             
/* 235 */             AtomicReference<String> alpnResult = new AtomicReference<>();
/* 236 */             ByteBuffer dupFirst = firstRecord.duplicate();
/* 237 */             dupFirst.position(firstRecord.position() + 5);
/* 238 */             ByteBuffer firstLessFraming = dupFirst.duplicate();
/*     */             
/* 240 */             byte[] result = ALPNHackServerHelloExplorer.removeAlpnExtensionsFromServerHello(dupFirst, alpnResult);
/* 241 */             firstLessFraming.limit(dupFirst.position());
/* 242 */             this.unwrapHelloSeen = true;
/* 243 */             if (result != null) {
/* 244 */               this.selectedApplicationProtocol = alpnResult.get();
/* 245 */               int newFirstRecordLength = result.length + dupFirst.remaining();
/* 246 */               byte[] newFirstRecord = new byte[newFirstRecordLength];
/* 247 */               System.arraycopy(result, 0, newFirstRecord, 0, result.length);
/* 248 */               dupFirst.get(newFirstRecord, result.length, dupFirst.remaining());
/* 249 */               dataToUnwrap.position(dataToUnwrap.limit());
/*     */               
/* 251 */               byte[] originalFirstRecord = new byte[firstLessFraming.remaining()];
/* 252 */               firstLessFraming.get(originalFirstRecord);
/*     */               
/* 254 */               ByteBuffer newData = ALPNHackServerHelloExplorer.createNewOutputRecords(newFirstRecord, records);
/* 255 */               dataToUnwrap.clear();
/* 256 */               dataToUnwrap.put(newData);
/* 257 */               dataToUnwrap.flip();
/* 258 */               this.ALPNHackClientByteArrayOutputStream.setReceivedServerHello(originalFirstRecord);
/*     */             } 
/*     */           } 
/* 261 */         } catch (BufferUnderflowException e) {
/* 262 */           return new SSLEngineResult(SSLEngineResult.Status.BUFFER_UNDERFLOW, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
/*     */         } 
/*     */       } 
/*     */     }
/* 266 */     SSLEngineResult res = this.delegate.unwrap(dataToUnwrap, byteBuffers, i, i1);
/* 267 */     if (!this.delegate.getUseClientMode() && this.selectedApplicationProtocol != null && this.alpnHackServerByteArrayOutputStream == null) {
/* 268 */       this.alpnHackServerByteArrayOutputStream = replaceServerByteOutput(this.delegate, this.selectedApplicationProtocol);
/*     */     }
/* 270 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public Runnable getDelegatedTask() {
/* 275 */     return this.delegate.getDelegatedTask();
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeInbound() throws SSLException {
/* 280 */     this.delegate.closeInbound();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInboundDone() {
/* 285 */     return this.delegate.isInboundDone();
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeOutbound() {
/* 290 */     this.delegate.closeOutbound();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOutboundDone() {
/* 295 */     return this.delegate.isOutboundDone();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getSupportedCipherSuites() {
/* 300 */     return this.delegate.getSupportedCipherSuites();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getEnabledCipherSuites() {
/* 305 */     return this.delegate.getEnabledCipherSuites();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnabledCipherSuites(String[] strings) {
/* 310 */     this.delegate.setEnabledCipherSuites(strings);
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getSupportedProtocols() {
/* 315 */     return this.delegate.getSupportedProtocols();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getEnabledProtocols() {
/* 320 */     return this.delegate.getEnabledProtocols();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnabledProtocols(String[] strings) {
/* 325 */     this.delegate.setEnabledProtocols(strings);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSession() {
/* 330 */     return this.delegate.getSession();
/*     */   }
/*     */ 
/*     */   
/*     */   public void beginHandshake() throws SSLException {
/* 335 */     this.delegate.beginHandshake();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
/* 340 */     return this.delegate.getHandshakeStatus();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUseClientMode(boolean b) {
/* 345 */     this.delegate.setUseClientMode(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getUseClientMode() {
/* 350 */     return this.delegate.getUseClientMode();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNeedClientAuth(boolean b) {
/* 355 */     this.delegate.setNeedClientAuth(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getNeedClientAuth() {
/* 360 */     return this.delegate.getNeedClientAuth();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWantClientAuth(boolean b) {
/* 365 */     this.delegate.setWantClientAuth(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWantClientAuth() {
/* 370 */     return this.delegate.getWantClientAuth();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnableSessionCreation(boolean b) {
/* 375 */     this.delegate.setEnableSessionCreation(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getEnableSessionCreation() {
/* 380 */     return this.delegate.getEnableSessionCreation();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setApplicationProtocols(List<String> applicationProtocols) {
/* 390 */     this.applicationProtocols = applicationProtocols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getApplicationProtocols() {
/* 399 */     return this.applicationProtocols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSelectedApplicationProtocol() {
/* 408 */     return this.selectedApplicationProtocol;
/*     */   }
/*     */ 
/*     */   
/*     */   static ALPNHackServerByteArrayOutputStream replaceServerByteOutput(SSLEngine sslEngine, String selectedAlpnProtocol) throws SSLException {
/*     */     try {
/* 414 */       Object handshaker = HANDSHAKER.get(sslEngine);
/* 415 */       Object hash = HANDSHAKE_HASH.get(handshaker);
/* 416 */       ByteArrayOutputStream existing = (ByteArrayOutputStream)HANDSHAKE_HASH_DATA.get(hash);
/*     */       
/* 418 */       ALPNHackServerByteArrayOutputStream out = new ALPNHackServerByteArrayOutputStream(sslEngine, existing.toByteArray(), selectedAlpnProtocol);
/* 419 */       HANDSHAKE_HASH_DATA.set(hash, out);
/* 420 */       return out;
/* 421 */     } catch (Exception e) {
/* 422 */       throw UndertowMessages.MESSAGES.failedToReplaceHashOutputStream(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   static ALPNHackClientByteArrayOutputStream replaceClientByteOutput(SSLEngine sslEngine) throws SSLException {
/*     */     try {
/* 428 */       Object handshaker = HANDSHAKER.get(sslEngine);
/* 429 */       Object hash = HANDSHAKE_HASH.get(handshaker);
/*     */       
/* 431 */       ALPNHackClientByteArrayOutputStream out = new ALPNHackClientByteArrayOutputStream(sslEngine);
/* 432 */       HANDSHAKE_HASH_DATA.set(hash, out);
/* 433 */       return out;
/* 434 */     } catch (Exception e) {
/* 435 */       throw UndertowMessages.MESSAGES.failedToReplaceHashOutputStream(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void regenerateHashes(SSLEngine sslEngineToHack, ByteArrayOutputStream data, byte[]... hashBytes) {
/*     */     try {
/* 441 */       Object handshaker = HANDSHAKER.get(sslEngineToHack);
/* 442 */       Object hash = HANDSHAKE_HASH.get(handshaker);
/* 443 */       data.reset();
/* 444 */       Object protocolVersion = HANDSHAKER_PROTOCOL_VERSION.get(handshaker);
/* 445 */       HANDSHAKE_HASH_VERSION.set(hash, Integer.valueOf(-1));
/* 446 */       HANDSHAKE_HASH_PROTOCOL_DETERMINED.invoke(hash, new Object[] { protocolVersion });
/* 447 */       MessageDigest digest = (MessageDigest)HANDSHAKE_HASH_FIN_MD.get(hash);
/* 448 */       digest.reset();
/* 449 */       for (byte[] b : hashBytes) {
/* 450 */         HANDSHAKE_HASH_UPDATE.invoke(hash, new Object[] { b, Integer.valueOf(0), Integer.valueOf(b.length) });
/*     */       } 
/* 452 */     } catch (Exception e) {
/* 453 */       throw UndertowMessages.MESSAGES.failedToReplaceHashOutputStreamOnWrite(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\ALPNHackSSLEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */