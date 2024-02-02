package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.BooleanPropertyDefinition;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.DefaultPropertySet;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.IntegerPropertyDefinition;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.x.XProtocol;
import com.mysql.cj.protocol.x.XProtocolError;
import com.mysql.cj.util.StringUtils;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientImpl implements Client, Protocol.ProtocolEventListener {
   boolean isClosed = false;
   private ConnectionUrl connUrl = null;
   private boolean poolingEnabled = true;
   private int maxSize = 25;
   int maxIdleTime = 0;
   private int queueTimeout = 0;
   private int demotedTimeout = 120000;
   Map<HostInfo, Long> demotedHosts = null;
   BlockingQueue<PooledXProtocol> idleProtocols = null;
   Set<WeakReference<PooledXProtocol>> activeProtocols = null;
   Set<WeakReference<Session>> nonPooledSessions = null;
   SessionFactory sessionFactory = new SessionFactory();

   public ClientImpl(String url, String clientPropsJson) {
      Properties clientProps = StringUtils.isNullOrEmpty(clientPropsJson) ? new Properties() : this.clientPropsFromJson(clientPropsJson);
      this.init(url, clientProps);
   }

   public ClientImpl(String url, Properties clientProps) {
      this.init(url, clientProps != null ? clientProps : new Properties());
   }

   private Properties clientPropsFromJson(String clientPropsJson) {
      Properties props = new Properties();
      DbDoc clientPropsDoc = JsonParser.parseDoc(clientPropsJson);
      JsonValue pooling = (JsonValue)clientPropsDoc.remove("pooling");
      if (pooling != null) {
         if (!DbDoc.class.isAssignableFrom(pooling.getClass())) {
            throw new XDevAPIError(String.format("Client option 'pooling' does not support value '%s'.", pooling.toFormattedString()));
         }

         DbDoc poolingDoc = (DbDoc)pooling;
         JsonValue jsonVal = (JsonValue)poolingDoc.remove("enabled");
         if (jsonVal != null) {
            if (!JsonLiteral.class.isAssignableFrom(jsonVal.getClass())) {
               if (JsonString.class.isAssignableFrom(jsonVal.getClass())) {
                  throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", Client.ClientProperty.POOLING_ENABLED.getKeyName(), ((JsonString)jsonVal).getString()));
               }

               throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", Client.ClientProperty.POOLING_ENABLED.getKeyName(), jsonVal.toFormattedString()));
            }

            JsonLiteral pe = (JsonLiteral)jsonVal;
            if (pe != JsonLiteral.FALSE && pe != JsonLiteral.TRUE) {
               throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", Client.ClientProperty.POOLING_ENABLED.getKeyName(), jsonVal.toFormattedString()));
            }

            props.setProperty(Client.ClientProperty.POOLING_ENABLED.getKeyName(), pe.value);
         }

         jsonVal = (JsonValue)poolingDoc.remove("maxSize");
         if (jsonVal != null) {
            if (!JsonNumber.class.isAssignableFrom(jsonVal.getClass())) {
               if (JsonString.class.isAssignableFrom(jsonVal.getClass())) {
                  throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", Client.ClientProperty.POOLING_MAX_SIZE.getKeyName(), ((JsonString)jsonVal).getString()));
               }

               throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", Client.ClientProperty.POOLING_MAX_SIZE.getKeyName(), jsonVal.toFormattedString()));
            }

            props.setProperty(Client.ClientProperty.POOLING_MAX_SIZE.getKeyName(), ((JsonNumber)jsonVal).toString());
         }

         jsonVal = (JsonValue)poolingDoc.remove("maxIdleTime");
         if (jsonVal != null) {
            if (!JsonNumber.class.isAssignableFrom(jsonVal.getClass())) {
               if (JsonString.class.isAssignableFrom(jsonVal.getClass())) {
                  throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName(), ((JsonString)jsonVal).getString()));
               }

               throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName(), jsonVal.toFormattedString()));
            }

            props.setProperty(Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName(), ((JsonNumber)jsonVal).toString());
         }

         jsonVal = (JsonValue)poolingDoc.remove("queueTimeout");
         if (jsonVal != null) {
            if (!JsonNumber.class.isAssignableFrom(jsonVal.getClass())) {
               if (JsonString.class.isAssignableFrom(jsonVal.getClass())) {
                  throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName(), ((JsonString)jsonVal).getString()));
               }

               throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName(), jsonVal.toFormattedString()));
            }

            props.setProperty(Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName(), ((JsonNumber)jsonVal).toString());
         }

         if (poolingDoc.size() > 0) {
            String key = (String)poolingDoc.keySet().stream().findFirst().get();
            throw new XDevAPIError(String.format("Client option 'pooling.%s' is not recognized as valid.", key));
         }
      }

      if (!clientPropsDoc.isEmpty()) {
         String key = (String)clientPropsDoc.keySet().stream().findFirst().get();
         throw new XDevAPIError(String.format("Client option '%s' is not recognized as valid.", key));
      } else {
         return props;
      }
   }

   private void validateAndInitializeClientProps(Properties clientProps) {
      String propKey = "";
      String propValue = "";
      propKey = Client.ClientProperty.POOLING_ENABLED.getKeyName();
      if (clientProps.containsKey(propKey)) {
         propValue = clientProps.getProperty(propKey);

         try {
            this.poolingEnabled = BooleanPropertyDefinition.booleanFrom(propKey, propValue, (ExceptionInterceptor)null);
         } catch (CJException var8) {
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", propKey, propValue), var8);
         }
      }

      propKey = Client.ClientProperty.POOLING_MAX_SIZE.getKeyName();
      if (clientProps.containsKey(propKey)) {
         propValue = clientProps.getProperty(propKey);

         try {
            this.maxSize = IntegerPropertyDefinition.integerFrom(propKey, propValue, 1, (ExceptionInterceptor)null);
         } catch (WrongArgumentException var7) {
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", propKey, propValue), var7);
         }

         if (this.maxSize <= 0) {
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", propKey, propValue));
         }
      }

      propKey = Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName();
      if (clientProps.containsKey(propKey)) {
         propValue = clientProps.getProperty(propKey);

         try {
            this.maxIdleTime = IntegerPropertyDefinition.integerFrom(propKey, propValue, 1, (ExceptionInterceptor)null);
         } catch (WrongArgumentException var6) {
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", propKey, propValue), var6);
         }

         if (this.maxIdleTime < 0) {
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", propKey, propValue));
         }
      }

      propKey = Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName();
      if (clientProps.containsKey(propKey)) {
         propValue = clientProps.getProperty(propKey);

         try {
            this.queueTimeout = IntegerPropertyDefinition.integerFrom(propKey, propValue, 1, (ExceptionInterceptor)null);
         } catch (WrongArgumentException var5) {
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", propKey, propValue), var5);
         }

         if (this.queueTimeout < 0) {
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", propKey, propValue));
         }
      }

      List<String> clientPropsAsString = (List)Stream.of(Client.ClientProperty.values()).map(Client.ClientProperty::getKeyName).collect(Collectors.toList());
      propKey = (String)clientProps.keySet().stream().filter((k) -> {
         return !clientPropsAsString.contains(k);
      }).findFirst().orElse((Object)null);
      if (propKey != null) {
         throw new XDevAPIError(String.format("Client option '%s' is not recognized as valid.", propKey));
      }
   }

   private void init(String url, Properties clientProps) {
      this.connUrl = this.sessionFactory.parseUrl(url);
      this.validateAndInitializeClientProps(clientProps);
      if (this.poolingEnabled) {
         this.demotedHosts = new HashMap();
         this.idleProtocols = new LinkedBlockingQueue(this.maxSize);
         this.activeProtocols = new HashSet(this.maxSize);
      } else {
         this.nonPooledSessions = new HashSet();
      }

   }

   public Session getSession() {
      if (this.isClosed) {
         throw new XDevAPIError("Client is closed.");
      } else if (!this.poolingEnabled) {
         synchronized(this) {
            List<WeakReference<Session>> obsoletedSessions = new ArrayList();
            Iterator var22 = this.nonPooledSessions.iterator();

            while(true) {
               WeakReference ws;
               Session s;
               do {
                  do {
                     if (!var22.hasNext()) {
                        var22 = obsoletedSessions.iterator();

                        while(var22.hasNext()) {
                           ws = (WeakReference)var22.next();
                           this.nonPooledSessions.remove(ws);
                        }

                        Session sess = this.sessionFactory.getSession(this.connUrl);
                        this.nonPooledSessions.add(new WeakReference(sess));
                        return sess;
                     }

                     ws = (WeakReference)var22.next();
                  } while(ws == null);

                  s = (Session)ws.get();
               } while(s != null && s.isOpen());

               obsoletedSessions.add(ws);
            }
         }
      } else {
         PooledXProtocol prot = null;
         List<HostInfo> hostsList = this.connUrl.getHostsList();
         synchronized(this) {
            List<PooledXProtocol> toCloseAndRemove = (List)this.idleProtocols.stream().filter((p) -> {
               return !p.isHostInfoValid(hostsList);
            }).collect(Collectors.toList());
            Stream var10000 = toCloseAndRemove.stream().peek(PooledXProtocol::realClose);
            BlockingQueue var10001 = this.idleProtocols;
            var10001.getClass();
            var10000 = (Stream)var10000.peek(var10001::remove).map(PooledXProtocol::getHostInfo).sequential();
            Map var28 = this.demotedHosts;
            var10000.forEach(var28::remove);
         }

         long start = System.currentTimeMillis();

         while(prot == null && (this.queueTimeout == 0 || System.currentTimeMillis() < start + (long)this.queueTimeout)) {
            synchronized(this.idleProtocols) {
               if (this.idleProtocols.peek() != null) {
                  PooledXProtocol tryProt = (PooledXProtocol)this.idleProtocols.poll();
                  if (tryProt.isOpen()) {
                     if (tryProt.isIdleTimeoutReached()) {
                        tryProt.realClose();
                     } else {
                        try {
                           tryProt.reset();
                           prot = tryProt;
                        } catch (XProtocolError | CJCommunicationsException var15) {
                        }
                     }
                  }
               } else if (this.idleProtocols.size() + this.activeProtocols.size() >= this.maxSize) {
                  if (this.queueTimeout > 0) {
                     long currentTimeout = (long)this.queueTimeout - (System.currentTimeMillis() - start);

                     try {
                        if (currentTimeout > 0L) {
                           prot = (PooledXProtocol)this.idleProtocols.poll(currentTimeout, TimeUnit.MILLISECONDS);
                        }
                     } catch (InterruptedException var14) {
                        throw new XDevAPIError("Session can not be obtained within " + this.queueTimeout + " milliseconds.", var14);
                     }
                  } else {
                     prot = (PooledXProtocol)this.idleProtocols.poll();
                  }
               } else {
                  CJException latestException = null;
                  List<HostInfo> hostsToRevisit = new ArrayList();
                  Iterator var8 = hostsList.iterator();

                  HostInfo hi;
                  while(var8.hasNext()) {
                     hi = (HostInfo)var8.next();
                     if (this.demotedHosts.containsKey(hi)) {
                        if (start - (Long)this.demotedHosts.get(hi) <= (long)this.demotedTimeout) {
                           hostsToRevisit.add(hi);
                           continue;
                        }

                        this.demotedHosts.remove(hi);
                     }

                     try {
                        prot = this.newPooledXProtocol(hi);
                        break;
                     } catch (CJCommunicationsException var19) {
                        if (var19.getCause() == null) {
                           throw var19;
                        }

                        latestException = var19;
                        this.demotedHosts.put(hi, System.currentTimeMillis());
                     }
                  }

                  if (prot == null) {
                     var8 = hostsToRevisit.iterator();

                     while(var8.hasNext()) {
                        hi = (HostInfo)var8.next();

                        try {
                           prot = this.newPooledXProtocol(hi);
                           this.demotedHosts.remove(hi);
                           break;
                        } catch (CJCommunicationsException var18) {
                           if (var18.getCause() == null) {
                              throw var18;
                           }

                           latestException = var18;
                           this.demotedHosts.put(hi, System.currentTimeMillis());
                        }
                     }
                  }

                  if (prot == null && latestException != null) {
                     throw (CJCommunicationsException)ExceptionFactory.createException((Class)CJCommunicationsException.class, (String)Messages.getString("Session.Create.Failover.0"), (Throwable)latestException);
                  }
               }
            }
         }

         if (prot == null) {
            throw new XDevAPIError("Session can not be obtained within " + this.queueTimeout + " milliseconds.");
         } else {
            synchronized(this) {
               this.activeProtocols.add(new WeakReference(prot));
            }

            SessionImpl sess = new SessionImpl(prot);
            return sess;
         }
      }
   }

   private PooledXProtocol newPooledXProtocol(HostInfo hi) {
      PropertySet pset = new DefaultPropertySet();
      pset.initializeProperties(hi.exposeAsProperties());
      PooledXProtocol tryProt = new PooledXProtocol(hi, pset);
      tryProt.addListener(this);
      tryProt.connect(hi.getUser(), hi.getPassword(), hi.getDatabase());
      return tryProt;
   }

   public void close() {
      synchronized(this) {
         if (this.poolingEnabled) {
            if (!this.isClosed) {
               this.isClosed = true;
               this.idleProtocols.forEach((s) -> {
                  s.realClose();
               });
               this.idleProtocols.clear();
               this.activeProtocols.stream().map(Reference::get).filter(Objects::nonNull).forEach((s) -> {
                  s.realClose();
               });
               this.activeProtocols.clear();
            }
         } else {
            this.nonPooledSessions.stream().map(Reference::get).filter(Objects::nonNull).filter(Session::isOpen).forEach((s) -> {
               s.close();
            });
         }

      }
   }

   void idleProtocol(PooledXProtocol prot) {
      synchronized(this) {
         if (!this.isClosed) {
            List<WeakReference<PooledXProtocol>> removeThem = new ArrayList();
            Iterator var4 = this.activeProtocols.iterator();

            WeakReference wps;
            while(var4.hasNext()) {
               wps = (WeakReference)var4.next();
               if (wps != null) {
                  PooledXProtocol as = (PooledXProtocol)wps.get();
                  if (as == null) {
                     removeThem.add(wps);
                  } else if (as == prot) {
                     removeThem.add(wps);
                     this.idleProtocols.add(as);
                  }
               }
            }

            var4 = removeThem.iterator();

            while(var4.hasNext()) {
               wps = (WeakReference)var4.next();
               this.activeProtocols.remove(wps);
            }
         }

      }
   }

   public void handleEvent(Protocol.ProtocolEventListener.EventType type, Object info, Throwable reason) {
      switch (type) {
         case SERVER_SHUTDOWN:
            HostInfo hi = ((PooledXProtocol)info).getHostInfo();
            synchronized(this) {
               List<PooledXProtocol> toCloseAndRemove = (List)this.idleProtocols.stream().filter((p) -> {
                  return p.getHostInfo().equalHostPortPair(hi);
               }).collect(Collectors.toList());
               Stream var10000 = toCloseAndRemove.stream().peek(PooledXProtocol::realClose);
               BlockingQueue var10001 = this.idleProtocols;
               var10001.getClass();
               var10000 = (Stream)var10000.peek(var10001::remove).map(PooledXProtocol::getHostInfo).sequential();
               Map var11 = this.demotedHosts;
               var10000.forEach(var11::remove);
               this.removeActivePooledXProtocol((PooledXProtocol)info);
               break;
            }
         case SERVER_CLOSED_SESSION:
            synchronized(this) {
               this.removeActivePooledXProtocol((PooledXProtocol)info);
            }
      }

   }

   private void removeActivePooledXProtocol(PooledXProtocol prot) {
      WeakReference<PooledXProtocol> wprot = null;
      Iterator var3 = this.activeProtocols.iterator();

      while(var3.hasNext()) {
         WeakReference<PooledXProtocol> wps = (WeakReference)var3.next();
         if (wps != null) {
            PooledXProtocol as = (PooledXProtocol)wps.get();
            if (as == prot) {
               wprot = wps;
               break;
            }
         }
      }

      this.activeProtocols.remove(wprot);
      prot.realClose();
   }

   public class PooledXProtocol extends XProtocol {
      long idleSince = -1L;
      HostInfo hostInfo = null;

      public PooledXProtocol(HostInfo hostInfo, PropertySet propertySet) {
         super(hostInfo, propertySet);
         this.hostInfo = hostInfo;
      }

      public void close() {
         this.reset();
         this.idleSince = System.currentTimeMillis();
         ClientImpl.this.idleProtocol(this);
      }

      public HostInfo getHostInfo() {
         return this.hostInfo;
      }

      boolean isIdleTimeoutReached() {
         return ClientImpl.this.maxIdleTime > 0 && this.idleSince > 0L && System.currentTimeMillis() > this.idleSince + (long)ClientImpl.this.maxIdleTime;
      }

      boolean isHostInfoValid(List<HostInfo> hostsList) {
         return hostsList.stream().filter((h) -> {
            return h.equalHostPortPair(this.hostInfo);
         }).findFirst().isPresent();
      }

      void realClose() {
         try {
            super.close();
         } catch (IOException var2) {
         }

      }
   }
}
