package com.mysql.cj.protocol;

import com.mysql.cj.MessageBuilder;
import com.mysql.cj.Messages;
import com.mysql.cj.Session;
import com.mysql.cj.TransactionEventHandler;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.log.Log;
import com.mysql.cj.util.TimeUtil;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractProtocol<M extends Message> implements Protocol<M>, Protocol.ProtocolEventHandler {
   protected Session session;
   protected SocketConnection socketConnection;
   protected PropertySet propertySet;
   protected TransactionEventHandler transactionManager;
   protected transient Log log;
   protected ExceptionInterceptor exceptionInterceptor;
   protected AuthenticationProvider<M> authProvider;
   protected MessageBuilder<M> messageBuilder;
   private PacketSentTimeHolder packetSentTimeHolder = new PacketSentTimeHolder() {
   };
   private PacketReceivedTimeHolder packetReceivedTimeHolder = new PacketReceivedTimeHolder() {
   };
   protected LinkedList<StringBuilder> packetDebugRingBuffer = null;
   protected boolean useNanosForElapsedTime;
   protected String queryTimingUnits;
   private CopyOnWriteArrayList<WeakReference<Protocol.ProtocolEventListener>> listeners = new CopyOnWriteArrayList();

   public void init(Session sess, SocketConnection phConnection, PropertySet propSet, TransactionEventHandler trManager) {
      this.session = sess;
      this.propertySet = propSet;
      this.socketConnection = phConnection;
      this.exceptionInterceptor = this.socketConnection.getExceptionInterceptor();
      this.transactionManager = trManager;
      this.useNanosForElapsedTime = (Boolean)this.propertySet.getBooleanProperty(PropertyKey.useNanosForElapsedTime).getValue() && TimeUtil.nanoTimeAvailable();
      this.queryTimingUnits = this.useNanosForElapsedTime ? Messages.getString("Nanoseconds") : Messages.getString("Milliseconds");
   }

   public SocketConnection getSocketConnection() {
      return this.socketConnection;
   }

   public AuthenticationProvider<M> getAuthenticationProvider() {
      return this.authProvider;
   }

   public ExceptionInterceptor getExceptionInterceptor() {
      return this.exceptionInterceptor;
   }

   public PacketSentTimeHolder getPacketSentTimeHolder() {
      return this.packetSentTimeHolder;
   }

   public void setPacketSentTimeHolder(PacketSentTimeHolder packetSentTimeHolder) {
      this.packetSentTimeHolder = packetSentTimeHolder;
   }

   public PacketReceivedTimeHolder getPacketReceivedTimeHolder() {
      return this.packetReceivedTimeHolder;
   }

   public void setPacketReceivedTimeHolder(PacketReceivedTimeHolder packetReceivedTimeHolder) {
      this.packetReceivedTimeHolder = packetReceivedTimeHolder;
   }

   public PropertySet getPropertySet() {
      return this.propertySet;
   }

   public void setPropertySet(PropertySet propertySet) {
      this.propertySet = propertySet;
   }

   public MessageBuilder<M> getMessageBuilder() {
      return this.messageBuilder;
   }

   public void reset() {
   }

   public String getQueryTimingUnits() {
      return this.queryTimingUnits;
   }

   public void addListener(Protocol.ProtocolEventListener l) {
      this.listeners.addIfAbsent(new WeakReference(l));
   }

   public void removeListener(Protocol.ProtocolEventListener listener) {
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         WeakReference<Protocol.ProtocolEventListener> wr = (WeakReference)var2.next();
         Protocol.ProtocolEventListener l = (Protocol.ProtocolEventListener)wr.get();
         if (l == listener) {
            this.listeners.remove(wr);
            break;
         }
      }

   }

   public void invokeListeners(Protocol.ProtocolEventListener.EventType type, Throwable reason) {
      Iterator var3 = this.listeners.iterator();

      while(var3.hasNext()) {
         WeakReference<Protocol.ProtocolEventListener> wr = (WeakReference)var3.next();
         Protocol.ProtocolEventListener l = (Protocol.ProtocolEventListener)wr.get();
         if (l != null) {
            l.handleEvent(type, this, reason);
         } else {
            this.listeners.remove(wr);
         }
      }

   }
}
