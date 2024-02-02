package com.mysql.cj;

import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.log.Log;
import com.mysql.cj.log.LogFactory;
import com.mysql.cj.log.NullLogger;
import com.mysql.cj.log.ProfilerEventHandler;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.util.Util;
import java.net.SocketAddress;

public abstract class CoreSession implements Session {
   protected PropertySet propertySet;
   protected ExceptionInterceptor exceptionInterceptor;
   protected transient Log log;
   protected static final Log NULL_LOGGER = new NullLogger("MySQL");
   protected transient Protocol<? extends Message> protocol;
   protected MessageBuilder<? extends Message> messageBuilder;
   protected long connectionCreationTimeMillis = 0L;
   protected HostInfo hostInfo = null;
   protected RuntimeProperty<Boolean> gatherPerfMetrics;
   protected RuntimeProperty<String> characterEncoding;
   protected RuntimeProperty<Boolean> disconnectOnExpiredPasswords;
   protected RuntimeProperty<Boolean> cacheServerConfiguration;
   protected RuntimeProperty<Boolean> autoReconnect;
   protected RuntimeProperty<Boolean> autoReconnectForPools;
   protected RuntimeProperty<Boolean> maintainTimeStats;
   protected int sessionMaxRows = -1;
   private ProfilerEventHandler eventSink;

   public CoreSession(HostInfo hostInfo, PropertySet propSet) {
      this.connectionCreationTimeMillis = System.currentTimeMillis();
      this.hostInfo = hostInfo;
      this.propertySet = propSet;
      this.gatherPerfMetrics = this.getPropertySet().getBooleanProperty(PropertyKey.gatherPerfMetrics);
      this.characterEncoding = this.getPropertySet().getStringProperty(PropertyKey.characterEncoding);
      this.disconnectOnExpiredPasswords = this.getPropertySet().getBooleanProperty(PropertyKey.disconnectOnExpiredPasswords);
      this.cacheServerConfiguration = this.getPropertySet().getBooleanProperty(PropertyKey.cacheServerConfiguration);
      this.autoReconnect = this.getPropertySet().getBooleanProperty(PropertyKey.autoReconnect);
      this.autoReconnectForPools = this.getPropertySet().getBooleanProperty(PropertyKey.autoReconnectForPools);
      this.maintainTimeStats = this.getPropertySet().getBooleanProperty(PropertyKey.maintainTimeStats);
      this.log = LogFactory.getLogger(this.getPropertySet().getStringProperty(PropertyKey.logger).getStringValue(), "MySQL");
   }

   public void changeUser(String user, String password, String database) {
      this.sessionMaxRows = -1;
      this.protocol.changeUser(user, password, database);
   }

   public PropertySet getPropertySet() {
      return this.propertySet;
   }

   public ExceptionInterceptor getExceptionInterceptor() {
      return this.exceptionInterceptor;
   }

   public void setExceptionInterceptor(ExceptionInterceptor exceptionInterceptor) {
      this.exceptionInterceptor = exceptionInterceptor;
   }

   public Log getLog() {
      return this.log;
   }

   public HostInfo getHostInfo() {
      return this.hostInfo;
   }

   public <M extends Message> MessageBuilder<M> getMessageBuilder() {
      return this.messageBuilder;
   }

   public ServerSession getServerSession() {
      return this.protocol.getServerSession();
   }

   public boolean versionMeetsMinimum(int major, int minor, int subminor) {
      return this.protocol.versionMeetsMinimum(major, minor, subminor);
   }

   public long getThreadId() {
      return this.protocol.getServerSession().getCapabilities().getThreadId();
   }

   public void quit() {
      if (this.eventSink != null) {
         this.eventSink.destroy();
         this.eventSink = null;
      }

   }

   public void forceClose() {
      if (this.eventSink != null) {
         this.eventSink.destroy();
         this.eventSink = null;
      }

   }

   public boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public ProfilerEventHandler getProfilerEventHandler() {
      if (this.eventSink == null) {
         synchronized(this) {
            if (this.eventSink == null) {
               this.eventSink = (ProfilerEventHandler)Util.getInstance(this.propertySet.getStringProperty(PropertyKey.profilerEventHandler).getStringValue(), new Class[0], new Object[0], this.exceptionInterceptor);
               this.eventSink.init(this.log);
            }
         }
      }

      return this.eventSink;
   }

   public boolean isSSLEstablished() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public SocketAddress getRemoteSocketAddress() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public void addListener(Session.SessionEventListener l) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public void removeListener(Session.SessionEventListener l) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public String getIdentifierQuoteString() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public DataStoreMetadata getDataStoreMetadata() {
      return new DataStoreMetadataImpl(this);
   }

   public String getQueryTimingUnits() {
      return this.protocol.getQueryTimingUnits();
   }
}
