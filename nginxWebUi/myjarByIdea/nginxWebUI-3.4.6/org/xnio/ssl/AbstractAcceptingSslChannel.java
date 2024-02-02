package org.xnio.ssl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Pool;
import org.xnio.Sequence;
import org.xnio.SslClientAuthMode;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio._private.Messages;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.ConnectedChannel;

abstract class AbstractAcceptingSslChannel<C extends ConnectedChannel, S extends ConnectedChannel> implements AcceptingChannel<C> {
   private final SSLContext sslContext;
   private final AcceptingChannel<? extends S> tcpServer;
   private volatile SslClientAuthMode clientAuthMode;
   private volatile int useClientMode;
   private volatile int enableSessionCreation;
   private volatile String[] cipherSuites;
   private volatile String[] protocols;
   private static final AtomicReferenceFieldUpdater<AbstractAcceptingSslChannel, SslClientAuthMode> clientAuthModeUpdater = AtomicReferenceFieldUpdater.newUpdater(AbstractAcceptingSslChannel.class, SslClientAuthMode.class, "clientAuthMode");
   private static final AtomicIntegerFieldUpdater<AbstractAcceptingSslChannel> useClientModeUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractAcceptingSslChannel.class, "useClientMode");
   private static final AtomicIntegerFieldUpdater<AbstractAcceptingSslChannel> enableSessionCreationUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractAcceptingSslChannel.class, "enableSessionCreation");
   private static final AtomicReferenceFieldUpdater<AbstractAcceptingSslChannel, String[]> cipherSuitesUpdater = AtomicReferenceFieldUpdater.newUpdater(AbstractAcceptingSslChannel.class, String[].class, "cipherSuites");
   private static final AtomicReferenceFieldUpdater<AbstractAcceptingSslChannel, String[]> protocolsUpdater = AtomicReferenceFieldUpdater.newUpdater(AbstractAcceptingSslChannel.class, String[].class, "protocols");
   private final ChannelListener.Setter<AcceptingChannel<C>> closeSetter;
   private final ChannelListener.Setter<AcceptingChannel<C>> acceptSetter;
   protected final boolean startTls;
   protected final Pool<ByteBuffer> socketBufferPool;
   protected final Pool<ByteBuffer> applicationBufferPool;
   private static final Set<Option<?>> SUPPORTED_OPTIONS;

   AbstractAcceptingSslChannel(SSLContext sslContext, AcceptingChannel<? extends S> tcpServer, OptionMap optionMap, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool, boolean startTls) {
      this.tcpServer = tcpServer;
      this.sslContext = sslContext;
      this.socketBufferPool = socketBufferPool;
      this.applicationBufferPool = applicationBufferPool;
      this.startTls = startTls;
      this.clientAuthMode = (SslClientAuthMode)optionMap.get(Options.SSL_CLIENT_AUTH_MODE);
      this.useClientMode = optionMap.get(Options.SSL_USE_CLIENT_MODE, false) ? 1 : 0;
      this.enableSessionCreation = optionMap.get(Options.SSL_ENABLE_SESSION_CREATION, true) ? 1 : 0;
      Sequence<String> enabledCipherSuites = (Sequence)optionMap.get(Options.SSL_ENABLED_CIPHER_SUITES);
      this.cipherSuites = enabledCipherSuites != null ? (String[])enabledCipherSuites.toArray(new String[enabledCipherSuites.size()]) : null;
      Sequence<String> enabledProtocols = (Sequence)optionMap.get(Options.SSL_ENABLED_PROTOCOLS);
      this.protocols = enabledProtocols != null ? (String[])enabledProtocols.toArray(new String[enabledProtocols.size()]) : null;
      this.closeSetter = ChannelListeners.getDelegatingSetter(tcpServer.getCloseSetter(), this);
      this.acceptSetter = ChannelListeners.getDelegatingSetter(tcpServer.getAcceptSetter(), this);
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      if (option == Options.SSL_CLIENT_AUTH_MODE) {
         return option.cast(clientAuthModeUpdater.getAndSet(this, Options.SSL_CLIENT_AUTH_MODE.cast(value)));
      } else {
         Boolean valueObject;
         if (option == Options.SSL_USE_CLIENT_MODE) {
            valueObject = (Boolean)Options.SSL_USE_CLIENT_MODE.cast(value);
            if (valueObject != null) {
               return option.cast(useClientModeUpdater.getAndSet(this, valueObject ? 1 : 0) != 0);
            }
         } else {
            if (option != Options.SSL_ENABLE_SESSION_CREATION) {
               Sequence seq;
               if (option == Options.SSL_ENABLED_CIPHER_SUITES) {
                  seq = (Sequence)Options.SSL_ENABLED_CIPHER_SUITES.cast(value);
                  return option.cast(cipherSuitesUpdater.getAndSet(this, seq == null ? null : (String[])seq.toArray(new String[seq.size()])));
               }

               if (option == Options.SSL_ENABLED_PROTOCOLS) {
                  seq = (Sequence)Options.SSL_ENABLED_PROTOCOLS.cast(value);
                  return option.cast(protocolsUpdater.getAndSet(this, seq == null ? null : (String[])seq.toArray(new String[seq.size()])));
               }

               return this.tcpServer.setOption(option, value);
            }

            valueObject = (Boolean)Options.SSL_ENABLE_SESSION_CREATION.cast(value);
            if (valueObject != null) {
               return option.cast(enableSessionCreationUpdater.getAndSet(this, valueObject ? 1 : 0) != 0);
            }
         }

         throw Messages.msg.nullParameter("value");
      }
   }

   public XnioWorker getWorker() {
      return this.tcpServer.getWorker();
   }

   public C accept() throws IOException {
      S tcpConnection = this.tcpServer.accept();
      if (tcpConnection == null) {
         return null;
      } else {
         InetSocketAddress peerAddress = (InetSocketAddress)tcpConnection.getPeerAddress(InetSocketAddress.class);
         SSLEngine engine = this.sslContext.createSSLEngine(peerAddress.getHostString(), peerAddress.getPort());
         boolean clientMode = this.useClientMode != 0;
         engine.setUseClientMode(clientMode);
         if (!clientMode) {
            SslClientAuthMode clientAuthMode = this.clientAuthMode;
            if (clientAuthMode != null) {
               switch (clientAuthMode) {
                  case NOT_REQUESTED:
                     engine.setNeedClientAuth(false);
                     engine.setWantClientAuth(false);
                     break;
                  case REQUESTED:
                     engine.setWantClientAuth(true);
                     break;
                  case REQUIRED:
                     engine.setNeedClientAuth(true);
                     break;
                  default:
                     throw new IllegalStateException();
               }
            }
         }

         engine.setEnableSessionCreation(this.enableSessionCreation != 0);
         String[] cipherSuites = this.cipherSuites;
         int var10;
         if (cipherSuites != null) {
            Set<String> supported = new HashSet(Arrays.asList(engine.getSupportedCipherSuites()));
            List<String> finalList = new ArrayList();
            String[] var8 = cipherSuites;
            int var9 = cipherSuites.length;

            for(var10 = 0; var10 < var9; ++var10) {
               String name = var8[var10];
               if (supported.contains(name)) {
                  finalList.add(name);
               }
            }

            engine.setEnabledCipherSuites((String[])finalList.toArray(new String[finalList.size()]));
         }

         String[] protocols = this.protocols;
         if (protocols != null) {
            Set<String> supported = new HashSet(Arrays.asList(engine.getSupportedProtocols()));
            List<String> finalList = new ArrayList();
            String[] var17 = protocols;
            var10 = protocols.length;

            for(int var18 = 0; var18 < var10; ++var18) {
               String name = var17[var18];
               if (supported.contains(name)) {
                  finalList.add(name);
               }
            }

            engine.setEnabledProtocols((String[])finalList.toArray(new String[finalList.size()]));
         }

         return this.accept(tcpConnection, engine);
      }
   }

   protected abstract C accept(S var1, SSLEngine var2) throws IOException;

   public ChannelListener.Setter<? extends AcceptingChannel<C>> getCloseSetter() {
      return this.closeSetter;
   }

   public boolean isOpen() {
      return this.tcpServer.isOpen();
   }

   public void close() throws IOException {
      this.tcpServer.close();
   }

   public boolean supportsOption(Option<?> option) {
      return SUPPORTED_OPTIONS.contains(option) || this.tcpServer.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      if (option == Options.SSL_CLIENT_AUTH_MODE) {
         return option.cast(this.clientAuthMode);
      } else if (option == Options.SSL_USE_CLIENT_MODE) {
         return option.cast(this.useClientMode != 0);
      } else if (option == Options.SSL_ENABLE_SESSION_CREATION) {
         return option.cast(this.enableSessionCreation != 0);
      } else {
         String[] protocols;
         if (option == Options.SSL_ENABLED_CIPHER_SUITES) {
            protocols = this.cipherSuites;
            return protocols == null ? null : option.cast(Sequence.of((Object[])protocols));
         } else if (option == Options.SSL_ENABLED_PROTOCOLS) {
            protocols = this.protocols;
            return protocols == null ? null : option.cast(Sequence.of((Object[])protocols));
         } else {
            return this.tcpServer.getOption(option);
         }
      }
   }

   public ChannelListener.Setter<? extends AcceptingChannel<C>> getAcceptSetter() {
      return this.acceptSetter;
   }

   public SocketAddress getLocalAddress() {
      return this.tcpServer.getLocalAddress();
   }

   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      return this.tcpServer.getLocalAddress(type);
   }

   public void suspendAccepts() {
      this.tcpServer.suspendAccepts();
   }

   public void resumeAccepts() {
      this.tcpServer.resumeAccepts();
   }

   public boolean isAcceptResumed() {
      return this.tcpServer.isAcceptResumed();
   }

   public void wakeupAccepts() {
      this.tcpServer.wakeupAccepts();
   }

   public void awaitAcceptable() throws IOException {
      this.tcpServer.awaitAcceptable();
   }

   public void awaitAcceptable(long time, TimeUnit timeUnit) throws IOException {
      this.tcpServer.awaitAcceptable(time, timeUnit);
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getAcceptThread() {
      return this.tcpServer.getAcceptThread();
   }

   public XnioIoThread getIoThread() {
      return this.tcpServer.getIoThread();
   }

   static {
      SUPPORTED_OPTIONS = Option.setBuilder().add(Options.SSL_CLIENT_AUTH_MODE).add(Options.SSL_USE_CLIENT_MODE).add(Options.SSL_ENABLE_SESSION_CREATION).add(Options.SSL_ENABLED_CIPHER_SUITES).add(Options.SSL_ENABLED_PROTOCOLS).create();
   }
}
