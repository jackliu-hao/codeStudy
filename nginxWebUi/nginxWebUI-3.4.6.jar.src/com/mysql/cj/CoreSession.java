/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.log.LogFactory;
/*     */ import com.mysql.cj.log.NullLogger;
/*     */ import com.mysql.cj.log.ProfilerEventHandler;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.protocol.ServerSession;
/*     */ import com.mysql.cj.util.Util;
/*     */ import java.net.SocketAddress;
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
/*     */ public abstract class CoreSession
/*     */   implements Session
/*     */ {
/*     */   protected PropertySet propertySet;
/*     */   protected ExceptionInterceptor exceptionInterceptor;
/*     */   protected transient Log log;
/*  59 */   protected static final Log NULL_LOGGER = (Log)new NullLogger("MySQL");
/*     */   
/*     */   protected transient Protocol<? extends Message> protocol;
/*     */   
/*     */   protected MessageBuilder<? extends Message> messageBuilder;
/*     */   
/*  65 */   protected long connectionCreationTimeMillis = 0L;
/*  66 */   protected HostInfo hostInfo = null;
/*     */   
/*     */   protected RuntimeProperty<Boolean> gatherPerfMetrics;
/*     */   
/*     */   protected RuntimeProperty<String> characterEncoding;
/*     */   
/*     */   protected RuntimeProperty<Boolean> disconnectOnExpiredPasswords;
/*     */   protected RuntimeProperty<Boolean> cacheServerConfiguration;
/*     */   protected RuntimeProperty<Boolean> autoReconnect;
/*     */   protected RuntimeProperty<Boolean> autoReconnectForPools;
/*     */   protected RuntimeProperty<Boolean> maintainTimeStats;
/*  77 */   protected int sessionMaxRows = -1;
/*     */   
/*     */   private ProfilerEventHandler eventSink;
/*     */ 
/*     */   
/*     */   public CoreSession(HostInfo hostInfo, PropertySet propSet) {
/*  83 */     this.connectionCreationTimeMillis = System.currentTimeMillis();
/*  84 */     this.hostInfo = hostInfo;
/*  85 */     this.propertySet = propSet;
/*     */     
/*  87 */     this.gatherPerfMetrics = getPropertySet().getBooleanProperty(PropertyKey.gatherPerfMetrics);
/*  88 */     this.characterEncoding = getPropertySet().getStringProperty(PropertyKey.characterEncoding);
/*  89 */     this.disconnectOnExpiredPasswords = getPropertySet().getBooleanProperty(PropertyKey.disconnectOnExpiredPasswords);
/*  90 */     this.cacheServerConfiguration = getPropertySet().getBooleanProperty(PropertyKey.cacheServerConfiguration);
/*  91 */     this.autoReconnect = getPropertySet().getBooleanProperty(PropertyKey.autoReconnect);
/*  92 */     this.autoReconnectForPools = getPropertySet().getBooleanProperty(PropertyKey.autoReconnectForPools);
/*  93 */     this.maintainTimeStats = getPropertySet().getBooleanProperty(PropertyKey.maintainTimeStats);
/*     */     
/*  95 */     this.log = LogFactory.getLogger(getPropertySet().getStringProperty(PropertyKey.logger).getStringValue(), "MySQL");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void changeUser(String user, String password, String database) {
/* 101 */     this.sessionMaxRows = -1;
/*     */     
/* 103 */     this.protocol.changeUser(user, password, database);
/*     */   }
/*     */ 
/*     */   
/*     */   public PropertySet getPropertySet() {
/* 108 */     return this.propertySet;
/*     */   }
/*     */ 
/*     */   
/*     */   public ExceptionInterceptor getExceptionInterceptor() {
/* 113 */     return this.exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setExceptionInterceptor(ExceptionInterceptor exceptionInterceptor) {
/* 118 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   
/*     */   public Log getLog() {
/* 123 */     return this.log;
/*     */   }
/*     */   
/*     */   public HostInfo getHostInfo() {
/* 127 */     return this.hostInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <M extends Message> MessageBuilder<M> getMessageBuilder() {
/* 133 */     return (MessageBuilder)this.messageBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSession getServerSession() {
/* 138 */     return this.protocol.getServerSession();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean versionMeetsMinimum(int major, int minor, int subminor) {
/* 143 */     return this.protocol.versionMeetsMinimum(major, minor, subminor);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getThreadId() {
/* 148 */     return this.protocol.getServerSession().getCapabilities().getThreadId();
/*     */   }
/*     */ 
/*     */   
/*     */   public void quit() {
/* 153 */     if (this.eventSink != null) {
/* 154 */       this.eventSink.destroy();
/* 155 */       this.eventSink = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void forceClose() {
/* 161 */     if (this.eventSink != null) {
/* 162 */       this.eventSink.destroy();
/* 163 */       this.eventSink = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag) {
/* 168 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public ProfilerEventHandler getProfilerEventHandler() {
/* 173 */     if (this.eventSink == null) {
/* 174 */       synchronized (this) {
/* 175 */         if (this.eventSink == null) {
/* 176 */           this.eventSink = (ProfilerEventHandler)Util.getInstance(this.propertySet
/* 177 */               .getStringProperty(PropertyKey.profilerEventHandler).getStringValue(), new Class[0], new Object[0], this.exceptionInterceptor);
/*     */ 
/*     */           
/* 180 */           this.eventSink.init(this.log);
/*     */         } 
/*     */       } 
/*     */     }
/* 184 */     return this.eventSink;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSSLEstablished() {
/* 189 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getRemoteSocketAddress() {
/* 194 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public void addListener(Session.SessionEventListener l) {
/* 199 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeListener(Session.SessionEventListener l) {
/* 204 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getIdentifierQuoteString() {
/* 209 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public DataStoreMetadata getDataStoreMetadata() {
/* 214 */     return new DataStoreMetadataImpl(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getQueryTimingUnits() {
/* 219 */     return this.protocol.getQueryTimingUnits();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\CoreSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */