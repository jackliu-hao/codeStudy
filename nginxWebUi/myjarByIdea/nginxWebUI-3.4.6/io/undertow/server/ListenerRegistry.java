package io.undertow.server;

import io.undertow.UndertowMessages;
import io.undertow.util.CopyOnWriteMap;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ListenerRegistry {
   private final ConcurrentMap<String, Listener> listeners = new CopyOnWriteMap();

   public Listener getListener(String name) {
      return (Listener)this.listeners.get(name);
   }

   public void addListener(Listener listener) {
      if (this.listeners.putIfAbsent(listener.getName(), listener) != null) {
         throw UndertowMessages.MESSAGES.listenerAlreadyRegistered(listener.getName());
      }
   }

   public void removeListener(String name) {
      this.listeners.remove(name);
   }

   public static final class HttpUpgradeMetadata {
      private final String protocol;
      private final String subProtocol;
      private final Map<String, Object> contextInformation = new CopyOnWriteMap();

      public HttpUpgradeMetadata(String protocol, String subProtocol) {
         this.protocol = protocol;
         this.subProtocol = subProtocol;
      }

      public String getProtocol() {
         return this.protocol;
      }

      public String getSubProtocol() {
         return this.subProtocol;
      }

      public Collection<String> getContextKeys() {
         return this.contextInformation.keySet();
      }

      public Object removeContextInformation(String key) {
         return this.contextInformation.remove(key);
      }

      public Object setContextInformation(String key, Object value) {
         return this.contextInformation.put(key, value);
      }

      public Object getContextInformation(String key) {
         return this.contextInformation.get(key);
      }
   }

   public static final class Listener {
      private final String protocol;
      private final String name;
      private final String serverName;
      private final InetSocketAddress bindAddress;
      private final Map<String, Object> contextInformation = new CopyOnWriteMap();
      private final Set<HttpUpgradeMetadata> httpUpgradeMetadata = new CopyOnWriteArraySet();

      public Listener(String protocol, String name, String serverName, InetSocketAddress bindAddress) {
         this.protocol = protocol;
         this.name = name;
         this.serverName = serverName;
         this.bindAddress = bindAddress;
      }

      public String getProtocol() {
         return this.protocol;
      }

      public String getName() {
         return this.name;
      }

      public String getServerName() {
         return this.serverName;
      }

      public InetSocketAddress getBindAddress() {
         return this.bindAddress;
      }

      public Collection<String> getContextKeys() {
         return this.contextInformation.keySet();
      }

      public Object removeContextInformation(String key) {
         return this.contextInformation.remove(key);
      }

      public Object setContextInformation(String key, Object value) {
         return this.contextInformation.put(key, value);
      }

      public Object getContextInformation(String key) {
         return this.contextInformation.get(key);
      }

      public void addHttpUpgradeMetadata(HttpUpgradeMetadata upgradeMetadata) {
         this.httpUpgradeMetadata.add(upgradeMetadata);
      }

      public void removeHttpUpgradeMetadata(HttpUpgradeMetadata upgradeMetadata) {
         this.httpUpgradeMetadata.remove(upgradeMetadata);
      }

      public Set<HttpUpgradeMetadata> getHttpUpgradeMetadata() {
         return Collections.unmodifiableSet(this.httpUpgradeMetadata);
      }
   }
}
