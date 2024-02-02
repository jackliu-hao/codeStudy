package io.undertow.protocols.ssl;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;

public class ALPNHackSSLEngine extends SSLEngine {
   public static final boolean ENABLED;
   private static final Field HANDSHAKER;
   private static final Field HANDSHAKER_PROTOCOL_VERSION;
   private static final Field HANDSHAKE_HASH;
   private static final Field HANDSHAKE_HASH_VERSION;
   private static final Method HANDSHAKE_HASH_UPDATE;
   private static final Method HANDSHAKE_HASH_PROTOCOL_DETERMINED;
   private static final Field HANDSHAKE_HASH_DATA;
   private static final Field HANDSHAKE_HASH_FIN_MD;
   private static final Class<?> SSL_ENGINE_IMPL_CLASS;
   private final SSLEngine delegate;
   private boolean unwrapHelloSeen = false;
   private boolean ourHelloSent = false;
   private ALPNHackServerByteArrayOutputStream alpnHackServerByteArrayOutputStream;
   private ALPNHackClientByteArrayOutputStream ALPNHackClientByteArrayOutputStream;
   private List<String> applicationProtocols;
   private String selectedApplicationProtocol;
   private ByteBuffer bufferedWrapData;

   public ALPNHackSSLEngine(SSLEngine delegate) {
      this.delegate = delegate;
   }

   public static boolean isEnabled(SSLEngine engine) {
      return !ENABLED ? false : SSL_ENGINE_IMPL_CLASS.isAssignableFrom(engine.getClass());
   }

   public SSLEngineResult wrap(ByteBuffer[] byteBuffers, int i, int i1, ByteBuffer byteBuffer) throws SSLException {
      int pos;
      if (this.bufferedWrapData != null) {
         pos = this.bufferedWrapData.remaining();
         byteBuffer.put(this.bufferedWrapData);
         this.bufferedWrapData = null;
         return new SSLEngineResult(Status.OK, HandshakeStatus.NEED_WRAP, 0, pos);
      } else {
         pos = byteBuffer.position();
         int limit = byteBuffer.limit();
         SSLEngineResult res = this.delegate.wrap(byteBuffers, i, i1, byteBuffer);
         if (!this.ourHelloSent && res.bytesProduced() > 0) {
            if (this.delegate.getUseClientMode() && this.applicationProtocols != null && !this.applicationProtocols.isEmpty()) {
               this.ourHelloSent = true;
               this.ALPNHackClientByteArrayOutputStream = replaceClientByteOutput(this.delegate);
               ByteBuffer newBuf = byteBuffer.duplicate();
               newBuf.flip();
               byte[] data = new byte[newBuf.remaining()];
               newBuf.get(data);
               byte[] newData = ALPNHackClientHelloExplorer.rewriteClientHello(data, this.applicationProtocols);
               if (newData != null) {
                  byte[] clientHelloMesage = new byte[newData.length - 5];
                  System.arraycopy(newData, 5, clientHelloMesage, 0, clientHelloMesage.length);
                  this.ALPNHackClientByteArrayOutputStream.setSentClientHello(clientHelloMesage);
                  byteBuffer.clear();
                  byteBuffer.put(newData);
               }
            } else if (!this.getUseClientMode() && this.selectedApplicationProtocol != null && this.alpnHackServerByteArrayOutputStream != null) {
               byte[] newServerHello = this.alpnHackServerByteArrayOutputStream.getServerHello();
               if (newServerHello != null) {
                  byteBuffer.flip();
                  List<ByteBuffer> records = ALPNHackServerHelloExplorer.extractRecords(byteBuffer);
                  ByteBuffer newData = ALPNHackServerHelloExplorer.createNewOutputRecords(newServerHello, records);
                  byteBuffer.position(pos);
                  byteBuffer.limit(limit);
                  if (newData.remaining() > byteBuffer.remaining()) {
                     int old = newData.limit();
                     newData.limit(newData.position() + byteBuffer.remaining());
                     res = new SSLEngineResult(res.getStatus(), res.getHandshakeStatus(), res.bytesConsumed(), newData.remaining());
                     byteBuffer.put(newData);
                     newData.limit(old);
                     this.bufferedWrapData = newData;
                  } else {
                     res = new SSLEngineResult(res.getStatus(), res.getHandshakeStatus(), res.bytesConsumed(), newData.remaining());
                     byteBuffer.put(newData);
                  }
               }
            }
         }

         if (res.bytesProduced() > 0) {
            this.ourHelloSent = true;
         }

         return res;
      }
   }

   public SSLEngineResult unwrap(ByteBuffer dataToUnwrap, ByteBuffer[] byteBuffers, int i, int i1) throws SSLException {
      if (!this.unwrapHelloSeen) {
         if (!this.delegate.getUseClientMode() && this.applicationProtocols != null) {
            try {
               List<String> result = ALPNHackClientHelloExplorer.exploreClientHello(dataToUnwrap.duplicate());
               if (result != null) {
                  Iterator var23 = this.applicationProtocols.iterator();

                  while(var23.hasNext()) {
                     String protocol = (String)var23.next();
                     if (result.contains(protocol)) {
                        this.selectedApplicationProtocol = protocol;
                        break;
                     }
                  }
               }

               this.unwrapHelloSeen = true;
            } catch (BufferUnderflowException var20) {
               return new SSLEngineResult(Status.BUFFER_UNDERFLOW, HandshakeStatus.NEED_UNWRAP, 0, 0);
            }
         } else if (this.delegate.getUseClientMode() && this.ALPNHackClientByteArrayOutputStream != null) {
            if (!dataToUnwrap.hasRemaining()) {
               return this.delegate.unwrap(dataToUnwrap, byteBuffers, i, i1);
            }

            try {
               ByteBuffer dup = dataToUnwrap.duplicate();
               int type = dup.get();
               int major = dup.get();
               int minor = dup.get();
               if (type == 22 && major == 3 && minor == 3) {
                  List<ByteBuffer> records = ALPNHackServerHelloExplorer.extractRecords(dataToUnwrap.duplicate());
                  ByteBuffer firstRecord = (ByteBuffer)records.get(0);
                  AtomicReference<String> alpnResult = new AtomicReference();
                  ByteBuffer dupFirst = firstRecord.duplicate();
                  dupFirst.position(firstRecord.position() + 5);
                  ByteBuffer firstLessFraming = dupFirst.duplicate();
                  byte[] result = ALPNHackServerHelloExplorer.removeAlpnExtensionsFromServerHello(dupFirst, alpnResult);
                  firstLessFraming.limit(dupFirst.position());
                  this.unwrapHelloSeen = true;
                  if (result != null) {
                     this.selectedApplicationProtocol = (String)alpnResult.get();
                     int newFirstRecordLength = result.length + dupFirst.remaining();
                     byte[] newFirstRecord = new byte[newFirstRecordLength];
                     System.arraycopy(result, 0, newFirstRecord, 0, result.length);
                     dupFirst.get(newFirstRecord, result.length, dupFirst.remaining());
                     dataToUnwrap.position(dataToUnwrap.limit());
                     byte[] originalFirstRecord = new byte[firstLessFraming.remaining()];
                     firstLessFraming.get(originalFirstRecord);
                     ByteBuffer newData = ALPNHackServerHelloExplorer.createNewOutputRecords(newFirstRecord, records);
                     dataToUnwrap.clear();
                     dataToUnwrap.put(newData);
                     dataToUnwrap.flip();
                     this.ALPNHackClientByteArrayOutputStream.setReceivedServerHello(originalFirstRecord);
                  }
               }
            } catch (BufferUnderflowException var19) {
               return new SSLEngineResult(Status.BUFFER_UNDERFLOW, HandshakeStatus.NEED_UNWRAP, 0, 0);
            }
         }
      }

      SSLEngineResult res = this.delegate.unwrap(dataToUnwrap, byteBuffers, i, i1);
      if (!this.delegate.getUseClientMode() && this.selectedApplicationProtocol != null && this.alpnHackServerByteArrayOutputStream == null) {
         this.alpnHackServerByteArrayOutputStream = replaceServerByteOutput(this.delegate, this.selectedApplicationProtocol);
      }

      return res;
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
      this.delegate.setUseClientMode(b);
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

   public void setApplicationProtocols(List<String> applicationProtocols) {
      this.applicationProtocols = applicationProtocols;
   }

   public List<String> getApplicationProtocols() {
      return this.applicationProtocols;
   }

   public String getSelectedApplicationProtocol() {
      return this.selectedApplicationProtocol;
   }

   static ALPNHackServerByteArrayOutputStream replaceServerByteOutput(SSLEngine sslEngine, String selectedAlpnProtocol) throws SSLException {
      try {
         Object handshaker = HANDSHAKER.get(sslEngine);
         Object hash = HANDSHAKE_HASH.get(handshaker);
         ByteArrayOutputStream existing = (ByteArrayOutputStream)HANDSHAKE_HASH_DATA.get(hash);
         ALPNHackServerByteArrayOutputStream out = new ALPNHackServerByteArrayOutputStream(sslEngine, existing.toByteArray(), selectedAlpnProtocol);
         HANDSHAKE_HASH_DATA.set(hash, out);
         return out;
      } catch (Exception var6) {
         throw UndertowMessages.MESSAGES.failedToReplaceHashOutputStream(var6);
      }
   }

   static ALPNHackClientByteArrayOutputStream replaceClientByteOutput(SSLEngine sslEngine) throws SSLException {
      try {
         Object handshaker = HANDSHAKER.get(sslEngine);
         Object hash = HANDSHAKE_HASH.get(handshaker);
         ALPNHackClientByteArrayOutputStream out = new ALPNHackClientByteArrayOutputStream(sslEngine);
         HANDSHAKE_HASH_DATA.set(hash, out);
         return out;
      } catch (Exception var4) {
         throw UndertowMessages.MESSAGES.failedToReplaceHashOutputStream(var4);
      }
   }

   static void regenerateHashes(SSLEngine sslEngineToHack, ByteArrayOutputStream data, byte[]... hashBytes) {
      try {
         Object handshaker = HANDSHAKER.get(sslEngineToHack);
         Object hash = HANDSHAKE_HASH.get(handshaker);
         data.reset();
         Object protocolVersion = HANDSHAKER_PROTOCOL_VERSION.get(handshaker);
         HANDSHAKE_HASH_VERSION.set(hash, -1);
         HANDSHAKE_HASH_PROTOCOL_DETERMINED.invoke(hash, protocolVersion);
         MessageDigest digest = (MessageDigest)HANDSHAKE_HASH_FIN_MD.get(hash);
         digest.reset();
         byte[][] var7 = hashBytes;
         int var8 = hashBytes.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            byte[] b = var7[var9];
            HANDSHAKE_HASH_UPDATE.invoke(hash, b, 0, b.length);
         }

      } catch (Exception var11) {
         throw UndertowMessages.MESSAGES.failedToReplaceHashOutputStreamOnWrite(var11);
      }
   }

   static {
      boolean enabled = true;

      Field handshaker;
      Field handshakeHash;
      Field handshakeHashVersion;
      Field handshakeHashData;
      Field handshakeHashFinMd;
      Field protocolVersion;
      Method handshakeHashUpdate;
      Method handshakeHashProtocolDetermined;
      Class sslEngineImpleClass;
      try {
         Class<?> protocolVersionClass = Class.forName("sun.security.ssl.ProtocolVersion", true, ClassLoader.getSystemClassLoader());
         sslEngineImpleClass = Class.forName("sun.security.ssl.SSLEngineImpl", true, ClassLoader.getSystemClassLoader());
         handshaker = sslEngineImpleClass.getDeclaredField("handshaker");
         handshaker.setAccessible(true);
         handshakeHash = handshaker.getType().getDeclaredField("handshakeHash");
         handshakeHash.setAccessible(true);
         protocolVersion = handshaker.getType().getDeclaredField("protocolVersion");
         protocolVersion.setAccessible(true);
         handshakeHashVersion = handshakeHash.getType().getDeclaredField("version");
         handshakeHashVersion.setAccessible(true);
         handshakeHashUpdate = handshakeHash.getType().getDeclaredMethod("update", byte[].class, Integer.TYPE, Integer.TYPE);
         handshakeHashUpdate.setAccessible(true);
         handshakeHashProtocolDetermined = handshakeHash.getType().getDeclaredMethod("protocolDetermined", protocolVersionClass);
         handshakeHashProtocolDetermined.setAccessible(true);
         handshakeHashData = handshakeHash.getType().getDeclaredField("data");
         handshakeHashData.setAccessible(true);
         handshakeHashFinMd = handshakeHash.getType().getDeclaredField("finMD");
         handshakeHashFinMd.setAccessible(true);
      } catch (Exception var11) {
         UndertowLogger.ROOT_LOGGER.debug("JDK8 ALPN Hack failed ", var11);
         enabled = false;
         handshaker = null;
         handshakeHash = null;
         handshakeHashVersion = null;
         handshakeHashUpdate = null;
         handshakeHashProtocolDetermined = null;
         handshakeHashData = null;
         handshakeHashFinMd = null;
         protocolVersion = null;
         sslEngineImpleClass = null;
      }

      ENABLED = enabled && !Boolean.getBoolean("io.undertow.disable-jdk8-alpn");
      HANDSHAKER = handshaker;
      HANDSHAKE_HASH = handshakeHash;
      HANDSHAKE_HASH_PROTOCOL_DETERMINED = handshakeHashProtocolDetermined;
      HANDSHAKE_HASH_VERSION = handshakeHashVersion;
      HANDSHAKE_HASH_UPDATE = handshakeHashUpdate;
      HANDSHAKE_HASH_DATA = handshakeHashData;
      HANDSHAKE_HASH_FIN_MD = handshakeHashFinMd;
      HANDSHAKER_PROTOCOL_VERSION = protocolVersion;
      SSL_ENGINE_IMPL_CLASS = sslEngineImpleClass;
   }
}
