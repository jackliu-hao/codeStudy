/*     */ package com.mysql.cj.protocol;
/*     */ 
/*     */ import com.mysql.cj.MessageBuilder;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.Session;
/*     */ import com.mysql.cj.TransactionEventHandler;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.util.TimeUtil;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.LinkedList;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
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
/*     */ public abstract class AbstractProtocol<M extends Message>
/*     */   implements Protocol<M>, Protocol.ProtocolEventHandler
/*     */ {
/*     */   protected Session session;
/*     */   protected SocketConnection socketConnection;
/*     */   protected PropertySet propertySet;
/*     */   protected TransactionEventHandler transactionManager;
/*     */   protected transient Log log;
/*     */   protected ExceptionInterceptor exceptionInterceptor;
/*     */   protected AuthenticationProvider<M> authProvider;
/*     */   protected MessageBuilder<M> messageBuilder;
/*  67 */   private PacketSentTimeHolder packetSentTimeHolder = new PacketSentTimeHolder() {  }
/*     */   ;
/*  69 */   private PacketReceivedTimeHolder packetReceivedTimeHolder = new PacketReceivedTimeHolder() {
/*     */     
/*     */     };
/*  72 */   protected LinkedList<StringBuilder> packetDebugRingBuffer = null;
/*     */   
/*     */   protected boolean useNanosForElapsedTime;
/*     */   
/*     */   protected String queryTimingUnits;
/*  77 */   private CopyOnWriteArrayList<WeakReference<Protocol.ProtocolEventListener>> listeners = new CopyOnWriteArrayList<>();
/*     */ 
/*     */   
/*     */   public void init(Session sess, SocketConnection phConnection, PropertySet propSet, TransactionEventHandler trManager) {
/*  81 */     this.session = sess;
/*  82 */     this.propertySet = propSet;
/*     */     
/*  84 */     this.socketConnection = phConnection;
/*  85 */     this.exceptionInterceptor = this.socketConnection.getExceptionInterceptor();
/*     */     
/*  87 */     this.transactionManager = trManager;
/*     */     
/*  89 */     this.useNanosForElapsedTime = (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useNanosForElapsedTime).getValue()).booleanValue() && TimeUtil.nanoTimeAvailable());
/*  90 */     this.queryTimingUnits = this.useNanosForElapsedTime ? Messages.getString("Nanoseconds") : Messages.getString("Milliseconds");
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketConnection getSocketConnection() {
/*  95 */     return this.socketConnection;
/*     */   }
/*     */   
/*     */   public AuthenticationProvider<M> getAuthenticationProvider() {
/*  99 */     return this.authProvider;
/*     */   }
/*     */   
/*     */   public ExceptionInterceptor getExceptionInterceptor() {
/* 103 */     return this.exceptionInterceptor;
/*     */   }
/*     */   
/*     */   public PacketSentTimeHolder getPacketSentTimeHolder() {
/* 107 */     return this.packetSentTimeHolder;
/*     */   }
/*     */   
/*     */   public void setPacketSentTimeHolder(PacketSentTimeHolder packetSentTimeHolder) {
/* 111 */     this.packetSentTimeHolder = packetSentTimeHolder;
/*     */   }
/*     */   
/*     */   public PacketReceivedTimeHolder getPacketReceivedTimeHolder() {
/* 115 */     return this.packetReceivedTimeHolder;
/*     */   }
/*     */   
/*     */   public void setPacketReceivedTimeHolder(PacketReceivedTimeHolder packetReceivedTimeHolder) {
/* 119 */     this.packetReceivedTimeHolder = packetReceivedTimeHolder;
/*     */   }
/*     */   
/*     */   public PropertySet getPropertySet() {
/* 123 */     return this.propertySet;
/*     */   }
/*     */   
/*     */   public void setPropertySet(PropertySet propertySet) {
/* 127 */     this.propertySet = propertySet;
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageBuilder<M> getMessageBuilder() {
/* 132 */     return this.messageBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public String getQueryTimingUnits() {
/* 142 */     return this.queryTimingUnits;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addListener(Protocol.ProtocolEventListener l) {
/* 147 */     this.listeners.addIfAbsent(new WeakReference<>(l));
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeListener(Protocol.ProtocolEventListener listener) {
/* 152 */     for (WeakReference<Protocol.ProtocolEventListener> wr : this.listeners) {
/* 153 */       Protocol.ProtocolEventListener l = wr.get();
/* 154 */       if (l == listener) {
/* 155 */         this.listeners.remove(wr);
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void invokeListeners(Protocol.ProtocolEventListener.EventType type, Throwable reason) {
/* 163 */     for (WeakReference<Protocol.ProtocolEventListener> wr : this.listeners) {
/* 164 */       Protocol.ProtocolEventListener l = wr.get();
/* 165 */       if (l != null) {
/* 166 */         l.handleEvent(type, this, reason); continue;
/*     */       } 
/* 168 */       this.listeners.remove(wr);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\AbstractProtocol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */