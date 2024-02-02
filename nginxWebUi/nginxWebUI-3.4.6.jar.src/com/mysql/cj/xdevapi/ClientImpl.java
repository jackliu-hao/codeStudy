/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.BooleanPropertyDefinition;
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.DefaultPropertySet;
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.IntegerPropertyDefinition;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.protocol.x.XProtocol;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.Stream;
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
/*     */ public class ClientImpl
/*     */   implements Client, Protocol.ProtocolEventListener
/*     */ {
/*     */   boolean isClosed = false;
/*  67 */   private ConnectionUrl connUrl = null;
/*     */   
/*     */   private boolean poolingEnabled = true;
/*  70 */   private int maxSize = 25;
/*  71 */   int maxIdleTime = 0;
/*  72 */   private int queueTimeout = 0;
/*     */   
/*  74 */   private int demotedTimeout = 120000;
/*  75 */   Map<HostInfo, Long> demotedHosts = null;
/*     */   
/*  77 */   BlockingQueue<PooledXProtocol> idleProtocols = null;
/*  78 */   Set<WeakReference<PooledXProtocol>> activeProtocols = null;
/*     */   
/*  80 */   Set<WeakReference<Session>> nonPooledSessions = null;
/*     */   
/*  82 */   SessionFactory sessionFactory = new SessionFactory();
/*     */   
/*     */   public ClientImpl(String url, String clientPropsJson) {
/*  85 */     Properties clientProps = StringUtils.isNullOrEmpty(clientPropsJson) ? new Properties() : clientPropsFromJson(clientPropsJson);
/*  86 */     init(url, clientProps);
/*     */   }
/*     */   
/*     */   public ClientImpl(String url, Properties clientProps) {
/*  90 */     init(url, (clientProps != null) ? clientProps : new Properties());
/*     */   }
/*     */   
/*     */   private Properties clientPropsFromJson(String clientPropsJson) {
/*  94 */     Properties props = new Properties();
/*  95 */     DbDoc clientPropsDoc = JsonParser.parseDoc(clientPropsJson);
/*     */     
/*  97 */     JsonValue pooling = clientPropsDoc.remove("pooling");
/*  98 */     if (pooling != null) {
/*  99 */       if (!DbDoc.class.isAssignableFrom(pooling.getClass())) {
/* 100 */         throw new XDevAPIError(String.format("Client option 'pooling' does not support value '%s'.", new Object[] { pooling.toFormattedString() }));
/*     */       }
/* 102 */       DbDoc poolingDoc = (DbDoc)pooling;
/*     */ 
/*     */       
/* 105 */       JsonValue jsonVal = poolingDoc.remove("enabled");
/* 106 */       if (jsonVal != null) {
/* 107 */         if (JsonLiteral.class.isAssignableFrom(jsonVal.getClass()))
/* 108 */         { JsonLiteral pe = (JsonLiteral)jsonVal;
/* 109 */           if (pe != JsonLiteral.FALSE && pe != JsonLiteral.TRUE) {
/* 110 */             throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_ENABLED.getKeyName(), jsonVal
/* 111 */                     .toFormattedString() }));
/*     */           }
/* 113 */           props.setProperty(Client.ClientProperty.POOLING_ENABLED.getKeyName(), pe.value); }
/* 114 */         else { if (JsonString.class.isAssignableFrom(jsonVal.getClass())) {
/* 115 */             throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_ENABLED.getKeyName(), ((JsonString)jsonVal)
/* 116 */                     .getString() }));
/*     */           }
/* 118 */           throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_ENABLED.getKeyName(), jsonVal
/* 119 */                   .toFormattedString() })); }
/*     */       
/*     */       }
/* 122 */       jsonVal = poolingDoc.remove("maxSize");
/* 123 */       if (jsonVal != null) {
/* 124 */         if (JsonNumber.class.isAssignableFrom(jsonVal.getClass()))
/* 125 */         { props.setProperty(Client.ClientProperty.POOLING_MAX_SIZE.getKeyName(), ((JsonNumber)jsonVal).toString()); }
/* 126 */         else { if (JsonString.class.isAssignableFrom(jsonVal.getClass())) {
/* 127 */             throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_MAX_SIZE.getKeyName(), ((JsonString)jsonVal)
/* 128 */                     .getString() }));
/*     */           }
/* 130 */           throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_MAX_SIZE.getKeyName(), jsonVal
/* 131 */                   .toFormattedString() })); }
/*     */       
/*     */       }
/* 134 */       jsonVal = poolingDoc.remove("maxIdleTime");
/* 135 */       if (jsonVal != null) {
/* 136 */         if (JsonNumber.class.isAssignableFrom(jsonVal.getClass()))
/* 137 */         { props.setProperty(Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName(), ((JsonNumber)jsonVal).toString()); }
/* 138 */         else { if (JsonString.class.isAssignableFrom(jsonVal.getClass())) {
/* 139 */             throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName(), ((JsonString)jsonVal)
/* 140 */                     .getString() }));
/*     */           }
/* 142 */           throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName(), jsonVal
/* 143 */                   .toFormattedString() })); }
/*     */       
/*     */       }
/* 146 */       jsonVal = poolingDoc.remove("queueTimeout");
/* 147 */       if (jsonVal != null) {
/* 148 */         if (JsonNumber.class.isAssignableFrom(jsonVal.getClass()))
/* 149 */         { props.setProperty(Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName(), ((JsonNumber)jsonVal).toString()); }
/* 150 */         else { if (JsonString.class.isAssignableFrom(jsonVal.getClass())) {
/* 151 */             throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName(), ((JsonString)jsonVal)
/* 152 */                     .getString() }));
/*     */           }
/* 154 */           throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName(), jsonVal
/* 155 */                   .toFormattedString() })); }
/*     */       
/*     */       }
/* 158 */       if (poolingDoc.size() > 0) {
/* 159 */         String key = poolingDoc.keySet().stream().findFirst().get();
/* 160 */         throw new XDevAPIError(String.format("Client option 'pooling.%s' is not recognized as valid.", new Object[] { key }));
/*     */       } 
/*     */     } 
/*     */     
/* 164 */     if (!clientPropsDoc.isEmpty()) {
/* 165 */       String key = clientPropsDoc.keySet().stream().findFirst().get();
/* 166 */       throw new XDevAPIError(String.format("Client option '%s' is not recognized as valid.", new Object[] { key }));
/*     */     } 
/*     */     
/* 169 */     return props;
/*     */   }
/*     */   
/*     */   private void validateAndInitializeClientProps(Properties clientProps) {
/* 173 */     String propKey = "";
/* 174 */     String propValue = "";
/* 175 */     propKey = Client.ClientProperty.POOLING_ENABLED.getKeyName();
/* 176 */     if (clientProps.containsKey(propKey)) {
/* 177 */       propValue = clientProps.getProperty(propKey);
/*     */       try {
/* 179 */         this.poolingEnabled = BooleanPropertyDefinition.booleanFrom(propKey, propValue, null).booleanValue();
/* 180 */       } catch (CJException e) {
/* 181 */         throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue }), e);
/*     */       } 
/*     */     } 
/*     */     
/* 185 */     propKey = Client.ClientProperty.POOLING_MAX_SIZE.getKeyName();
/* 186 */     if (clientProps.containsKey(propKey)) {
/* 187 */       propValue = clientProps.getProperty(propKey);
/*     */       try {
/* 189 */         this.maxSize = IntegerPropertyDefinition.integerFrom(propKey, propValue, 1, null).intValue();
/* 190 */       } catch (WrongArgumentException e) {
/* 191 */         throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue }), e);
/*     */       } 
/* 193 */       if (this.maxSize <= 0) {
/* 194 */         throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue }));
/*     */       }
/*     */     } 
/*     */     
/* 198 */     propKey = Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName();
/* 199 */     if (clientProps.containsKey(propKey)) {
/* 200 */       propValue = clientProps.getProperty(propKey);
/*     */       try {
/* 202 */         this.maxIdleTime = IntegerPropertyDefinition.integerFrom(propKey, propValue, 1, null).intValue();
/* 203 */       } catch (WrongArgumentException e) {
/* 204 */         throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue }), e);
/*     */       } 
/* 206 */       if (this.maxIdleTime < 0) {
/* 207 */         throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue }));
/*     */       }
/*     */     } 
/*     */     
/* 211 */     propKey = Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName();
/* 212 */     if (clientProps.containsKey(propKey)) {
/* 213 */       propValue = clientProps.getProperty(propKey);
/*     */       try {
/* 215 */         this.queueTimeout = IntegerPropertyDefinition.integerFrom(propKey, propValue, 1, null).intValue();
/* 216 */       } catch (WrongArgumentException e) {
/* 217 */         throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue }), e);
/*     */       } 
/* 219 */       if (this.queueTimeout < 0) {
/* 220 */         throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue }));
/*     */       }
/*     */     } 
/*     */     
/* 224 */     List<String> clientPropsAsString = (List<String>)Stream.<Client.ClientProperty>of(Client.ClientProperty.values()).map(Client.ClientProperty::getKeyName).collect(Collectors.toList());
/* 225 */     propKey = clientProps.keySet().stream().filter(k -> !clientPropsAsString.contains(k)).findFirst().orElse(null);
/* 226 */     if (propKey != null) {
/* 227 */       throw new XDevAPIError(String.format("Client option '%s' is not recognized as valid.", new Object[] { propKey }));
/*     */     }
/*     */   }
/*     */   
/*     */   private void init(String url, Properties clientProps) {
/* 232 */     this.connUrl = this.sessionFactory.parseUrl(url);
/*     */     
/* 234 */     validateAndInitializeClientProps(clientProps);
/*     */     
/* 236 */     if (this.poolingEnabled) {
/* 237 */       this.demotedHosts = new HashMap<>();
/* 238 */       this.idleProtocols = new LinkedBlockingQueue<>(this.maxSize);
/* 239 */       this.activeProtocols = new HashSet<>(this.maxSize);
/*     */     } else {
/* 241 */       this.nonPooledSessions = new HashSet<>();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Session getSession() {
/* 247 */     if (this.isClosed) {
/* 248 */       throw new XDevAPIError("Client is closed.");
/*     */     }
/*     */     
/* 251 */     if (!this.poolingEnabled) {
/* 252 */       synchronized (this) {
/*     */         
/* 254 */         List<WeakReference<Session>> obsoletedSessions = new ArrayList<>();
/* 255 */         for (WeakReference<Session> ws : this.nonPooledSessions) {
/* 256 */           if (ws != null) {
/* 257 */             Session s = ws.get();
/* 258 */             if (s == null || !s.isOpen()) {
/* 259 */               obsoletedSessions.add(ws);
/*     */             }
/*     */           } 
/*     */         } 
/* 263 */         for (WeakReference<Session> ws : obsoletedSessions) {
/* 264 */           this.nonPooledSessions.remove(ws);
/*     */         }
/*     */         
/* 267 */         Session session = this.sessionFactory.getSession(this.connUrl);
/* 268 */         this.nonPooledSessions.add(new WeakReference<>(session));
/* 269 */         return session;
/*     */       } 
/*     */     }
/*     */     
/* 273 */     PooledXProtocol prot = null;
/* 274 */     List<HostInfo> hostsList = this.connUrl.getHostsList();
/*     */     
/* 276 */     synchronized (this) {
/*     */       
/* 278 */       List<PooledXProtocol> toCloseAndRemove = (List<PooledXProtocol>)this.idleProtocols.stream().filter(p -> !p.isHostInfoValid(hostsList)).collect(Collectors.toList());
/* 279 */       toCloseAndRemove.stream().peek(PooledXProtocol::realClose).peek(this.idleProtocols::remove).map(PooledXProtocol::getHostInfo).sequential()
/* 280 */         .forEach(this.demotedHosts::remove);
/*     */     } 
/*     */     
/* 283 */     long start = System.currentTimeMillis();
/* 284 */     while (prot == null && (this.queueTimeout == 0 || System.currentTimeMillis() < start + this.queueTimeout)) {
/* 285 */       synchronized (this.idleProtocols) {
/* 286 */         if (this.idleProtocols.peek() != null) {
/*     */           
/* 288 */           PooledXProtocol tryProt = this.idleProtocols.poll();
/* 289 */           if (tryProt.isOpen()) {
/* 290 */             if (tryProt.isIdleTimeoutReached()) {
/* 291 */               tryProt.realClose();
/*     */             } else {
/*     */               try {
/* 294 */                 tryProt.reset();
/* 295 */                 prot = tryProt;
/* 296 */               } catch (CJCommunicationsException|com.mysql.cj.protocol.x.XProtocolError cJCommunicationsException) {}
/*     */             
/*     */             }
/*     */           
/*     */           }
/*     */         }
/* 302 */         else if (this.idleProtocols.size() + this.activeProtocols.size() < this.maxSize) {
/*     */           CJCommunicationsException cJCommunicationsException;
/* 304 */           CJException latestException = null;
/* 305 */           List<HostInfo> hostsToRevisit = new ArrayList<>();
/* 306 */           for (HostInfo hi : hostsList) {
/* 307 */             if (this.demotedHosts.containsKey(hi)) {
/* 308 */               if (start - ((Long)this.demotedHosts.get(hi)).longValue() > this.demotedTimeout) {
/* 309 */                 this.demotedHosts.remove(hi);
/*     */               } else {
/* 311 */                 hostsToRevisit.add(hi);
/*     */                 continue;
/*     */               } 
/*     */             }
/*     */             try {
/* 316 */               prot = newPooledXProtocol(hi);
/*     */               break;
/* 318 */             } catch (CJCommunicationsException e) {
/* 319 */               if (e.getCause() == null) {
/* 320 */                 throw e;
/*     */               }
/* 322 */               cJCommunicationsException = e;
/* 323 */               this.demotedHosts.put(hi, Long.valueOf(System.currentTimeMillis()));
/*     */             } 
/*     */           } 
/* 326 */           if (prot == null)
/*     */           {
/* 328 */             for (HostInfo hi : hostsToRevisit) {
/*     */               try {
/* 330 */                 prot = newPooledXProtocol(hi);
/*     */                 
/* 332 */                 this.demotedHosts.remove(hi);
/*     */                 break;
/* 334 */               } catch (CJCommunicationsException e) {
/* 335 */                 if (e.getCause() == null) {
/* 336 */                   throw e;
/*     */                 }
/* 338 */                 cJCommunicationsException = e;
/* 339 */                 this.demotedHosts.put(hi, Long.valueOf(System.currentTimeMillis()));
/*     */               } 
/*     */             } 
/*     */           }
/* 343 */           if (prot == null && cJCommunicationsException != null) {
/* 344 */             throw (CJCommunicationsException)ExceptionFactory.createException(CJCommunicationsException.class, Messages.getString("Session.Create.Failover.0"), cJCommunicationsException);
/*     */           
/*     */           }
/*     */         }
/* 348 */         else if (this.queueTimeout > 0) {
/*     */           
/* 350 */           long currentTimeout = this.queueTimeout - System.currentTimeMillis() - start;
/*     */           try {
/* 352 */             if (currentTimeout > 0L) {
/* 353 */               prot = this.idleProtocols.poll(currentTimeout, TimeUnit.MILLISECONDS);
/*     */             }
/* 355 */           } catch (InterruptedException e) {
/* 356 */             throw new XDevAPIError("Session can not be obtained within " + this.queueTimeout + " milliseconds.", e);
/*     */           }
/*     */         
/*     */         } else {
/*     */           
/* 361 */           prot = this.idleProtocols.poll();
/*     */         } 
/*     */       } 
/*     */     } 
/* 365 */     if (prot == null) {
/* 366 */       throw new XDevAPIError("Session can not be obtained within " + this.queueTimeout + " milliseconds.");
/*     */     }
/* 368 */     synchronized (this) {
/* 369 */       this.activeProtocols.add(new WeakReference<>(prot));
/*     */     } 
/* 371 */     SessionImpl sess = new SessionImpl(prot);
/* 372 */     return sess;
/*     */   }
/*     */ 
/*     */   
/*     */   private PooledXProtocol newPooledXProtocol(HostInfo hi) {
/* 377 */     DefaultPropertySet defaultPropertySet = new DefaultPropertySet();
/* 378 */     defaultPropertySet.initializeProperties(hi.exposeAsProperties());
/* 379 */     PooledXProtocol tryProt = new PooledXProtocol(hi, (PropertySet)defaultPropertySet);
/* 380 */     tryProt.addListener(this);
/* 381 */     tryProt.connect(hi.getUser(), hi.getPassword(), hi.getDatabase());
/* 382 */     return tryProt;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 387 */     synchronized (this) {
/* 388 */       if (this.poolingEnabled) {
/* 389 */         if (!this.isClosed) {
/* 390 */           this.isClosed = true;
/* 391 */           this.idleProtocols.forEach(s -> s.realClose());
/* 392 */           this.idleProtocols.clear();
/* 393 */           this.activeProtocols.stream().map(Reference::get).filter(Objects::nonNull).forEach(s -> s.realClose());
/* 394 */           this.activeProtocols.clear();
/*     */         } 
/*     */       } else {
/* 397 */         this.nonPooledSessions.stream().map(Reference::get).filter(Objects::nonNull).filter(Session::isOpen).forEach(s -> s.close());
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   void idleProtocol(PooledXProtocol prot) {
/* 403 */     synchronized (this) {
/* 404 */       if (!this.isClosed) {
/* 405 */         List<WeakReference<PooledXProtocol>> removeThem = new ArrayList<>();
/* 406 */         for (WeakReference<PooledXProtocol> wps : this.activeProtocols) {
/* 407 */           if (wps != null) {
/* 408 */             PooledXProtocol as = wps.get();
/* 409 */             if (as == null) {
/* 410 */               removeThem.add(wps); continue;
/* 411 */             }  if (as == prot) {
/* 412 */               removeThem.add(wps);
/* 413 */               this.idleProtocols.add(as);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 418 */         for (WeakReference<PooledXProtocol> wr : removeThem)
/* 419 */           this.activeProtocols.remove(wr); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public class PooledXProtocol
/*     */     extends XProtocol {
/* 426 */     long idleSince = -1L;
/* 427 */     HostInfo hostInfo = null;
/*     */     
/*     */     public PooledXProtocol(HostInfo hostInfo, PropertySet propertySet) {
/* 430 */       super(hostInfo, propertySet);
/* 431 */       this.hostInfo = hostInfo;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() {
/* 436 */       reset();
/* 437 */       this.idleSince = System.currentTimeMillis();
/* 438 */       ClientImpl.this.idleProtocol(this);
/*     */     }
/*     */     
/*     */     public HostInfo getHostInfo() {
/* 442 */       return this.hostInfo;
/*     */     }
/*     */     
/*     */     boolean isIdleTimeoutReached() {
/* 446 */       return (ClientImpl.this.maxIdleTime > 0 && this.idleSince > 0L && System.currentTimeMillis() > this.idleSince + ClientImpl.this.maxIdleTime);
/*     */     }
/*     */     
/*     */     boolean isHostInfoValid(List<HostInfo> hostsList) {
/* 450 */       return hostsList.stream().filter(h -> h.equalHostPortPair(this.hostInfo)).findFirst().isPresent();
/*     */     }
/*     */     
/*     */     void realClose() {
/*     */       try {
/* 455 */         super.close();
/* 456 */       } catch (IOException iOException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleEvent(Protocol.ProtocolEventListener.EventType type, Object info, Throwable reason) {
/*     */     HostInfo hi;
/* 464 */     switch (type) {
/*     */       case SERVER_SHUTDOWN:
/* 466 */         hi = ((PooledXProtocol)info).getHostInfo();
/* 467 */         synchronized (this) {
/*     */ 
/*     */           
/* 470 */           List<PooledXProtocol> toCloseAndRemove = (List<PooledXProtocol>)this.idleProtocols.stream().filter(p -> p.getHostInfo().equalHostPortPair(hi)).collect(Collectors.toList());
/* 471 */           toCloseAndRemove.stream().peek(PooledXProtocol::realClose).peek(this.idleProtocols::remove).map(PooledXProtocol::getHostInfo).sequential()
/* 472 */             .forEach(this.demotedHosts::remove);
/*     */           
/* 474 */           removeActivePooledXProtocol((PooledXProtocol)info);
/*     */         } 
/*     */         break;
/*     */       
/*     */       case SERVER_CLOSED_SESSION:
/* 479 */         synchronized (this) {
/* 480 */           removeActivePooledXProtocol((PooledXProtocol)info);
/*     */         } 
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void removeActivePooledXProtocol(PooledXProtocol prot) {
/* 490 */     WeakReference<PooledXProtocol> wprot = null;
/* 491 */     for (WeakReference<PooledXProtocol> wps : this.activeProtocols) {
/* 492 */       if (wps != null) {
/* 493 */         PooledXProtocol as = wps.get();
/* 494 */         if (as == prot) {
/* 495 */           wprot = wps;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 500 */     this.activeProtocols.remove(wprot);
/* 501 */     prot.realClose();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\ClientImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */