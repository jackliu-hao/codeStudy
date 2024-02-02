/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
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
/*     */ public class ListenerRegistry
/*     */ {
/*  42 */   private final ConcurrentMap<String, Listener> listeners = (ConcurrentMap<String, Listener>)new CopyOnWriteMap();
/*     */   
/*     */   public Listener getListener(String name) {
/*  45 */     return this.listeners.get(name);
/*     */   }
/*     */   
/*     */   public void addListener(Listener listener) {
/*  49 */     if (this.listeners.putIfAbsent(listener.getName(), listener) != null) {
/*  50 */       throw UndertowMessages.MESSAGES.listenerAlreadyRegistered(listener.getName());
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeListener(String name) {
/*  55 */     this.listeners.remove(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class Listener
/*     */   {
/*     */     private final String protocol;
/*     */     
/*     */     private final String name;
/*     */     
/*     */     private final String serverName;
/*     */     
/*     */     private final InetSocketAddress bindAddress;
/*  68 */     private final Map<String, Object> contextInformation = (Map<String, Object>)new CopyOnWriteMap();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  73 */     private final Set<ListenerRegistry.HttpUpgradeMetadata> httpUpgradeMetadata = new CopyOnWriteArraySet<>();
/*     */     
/*     */     public Listener(String protocol, String name, String serverName, InetSocketAddress bindAddress) {
/*  76 */       this.protocol = protocol;
/*  77 */       this.name = name;
/*  78 */       this.serverName = serverName;
/*  79 */       this.bindAddress = bindAddress;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getProtocol() {
/*  86 */       return this.protocol;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getName() {
/*  93 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getServerName() {
/* 100 */       return this.serverName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public InetSocketAddress getBindAddress() {
/* 107 */       return this.bindAddress;
/*     */     }
/*     */     
/*     */     public Collection<String> getContextKeys() {
/* 111 */       return this.contextInformation.keySet();
/*     */     }
/*     */     
/*     */     public Object removeContextInformation(String key) {
/* 115 */       return this.contextInformation.remove(key);
/*     */     }
/*     */     
/*     */     public Object setContextInformation(String key, Object value) {
/* 119 */       return this.contextInformation.put(key, value);
/*     */     }
/*     */     
/*     */     public Object getContextInformation(String key) {
/* 123 */       return this.contextInformation.get(key);
/*     */     }
/*     */     
/*     */     public void addHttpUpgradeMetadata(ListenerRegistry.HttpUpgradeMetadata upgradeMetadata) {
/* 127 */       this.httpUpgradeMetadata.add(upgradeMetadata);
/*     */     }
/*     */     
/*     */     public void removeHttpUpgradeMetadata(ListenerRegistry.HttpUpgradeMetadata upgradeMetadata) {
/* 131 */       this.httpUpgradeMetadata.remove(upgradeMetadata);
/*     */     }
/*     */     
/*     */     public Set<ListenerRegistry.HttpUpgradeMetadata> getHttpUpgradeMetadata() {
/* 135 */       return Collections.unmodifiableSet(this.httpUpgradeMetadata);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class HttpUpgradeMetadata
/*     */   {
/*     */     private final String protocol;
/*     */     private final String subProtocol;
/* 143 */     private final Map<String, Object> contextInformation = (Map<String, Object>)new CopyOnWriteMap();
/*     */ 
/*     */     
/*     */     public HttpUpgradeMetadata(String protocol, String subProtocol) {
/* 147 */       this.protocol = protocol;
/* 148 */       this.subProtocol = subProtocol;
/*     */     }
/*     */     
/*     */     public String getProtocol() {
/* 152 */       return this.protocol;
/*     */     }
/*     */     
/*     */     public String getSubProtocol() {
/* 156 */       return this.subProtocol;
/*     */     }
/*     */     
/*     */     public Collection<String> getContextKeys() {
/* 160 */       return this.contextInformation.keySet();
/*     */     }
/*     */     
/*     */     public Object removeContextInformation(String key) {
/* 164 */       return this.contextInformation.remove(key);
/*     */     }
/*     */     
/*     */     public Object setContextInformation(String key, Object value) {
/* 168 */       return this.contextInformation.put(key, value);
/*     */     }
/*     */     
/*     */     public Object getContextInformation(String key) {
/* 172 */       return this.contextInformation.get(key);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\ListenerRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */