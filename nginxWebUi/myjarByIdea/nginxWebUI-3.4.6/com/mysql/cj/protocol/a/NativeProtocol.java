package com.mysql.cj.protocol.a;

import com.mysql.cj.Constants;
import com.mysql.cj.MessageBuilder;
import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.NativeCharsetSettings;
import com.mysql.cj.NativeSession;
import com.mysql.cj.Query;
import com.mysql.cj.QueryAttributesBindValue;
import com.mysql.cj.QueryAttributesBindings;
import com.mysql.cj.QueryResult;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.Session;
import com.mysql.cj.TransactionEventHandler;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJConnectionFeatureNotAvailableException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.CJPacketTooBigException;
import com.mysql.cj.exceptions.ClosedOnExpiredPasswordException;
import com.mysql.cj.exceptions.DataTruncationException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.FeatureNotAvailableException;
import com.mysql.cj.exceptions.MysqlErrorNumbers;
import com.mysql.cj.exceptions.PasswordExpiredException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import com.mysql.cj.log.BaseMetricsHolder;
import com.mysql.cj.log.Log;
import com.mysql.cj.log.ProfilerEventHandler;
import com.mysql.cj.protocol.AbstractProtocol;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ExportControlled;
import com.mysql.cj.protocol.FullReadInputStream;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.MessageReader;
import com.mysql.cj.protocol.MessageSender;
import com.mysql.cj.protocol.PacketReceivedTimeHolder;
import com.mysql.cj.protocol.PacketSentTimeHolder;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.ProtocolEntityReader;
import com.mysql.cj.protocol.ResultBuilder;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ResultsetRow;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.protocol.SocketConnection;
import com.mysql.cj.protocol.a.result.OkPacket;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.IntegerValueFactory;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.StringValueFactory;
import com.mysql.cj.result.ValueFactory;
import com.mysql.cj.util.LazyString;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.TestUtils;
import com.mysql.cj.util.TimeUtil;
import com.mysql.cj.util.Util;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.ref.SoftReference;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DataTruncation;
import java.sql.SQLWarning;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Supplier;

public class NativeProtocol extends AbstractProtocol<NativePacketPayload> implements Protocol<NativePacketPayload>, RuntimeProperty.RuntimePropertyListener {
   protected static final int INITIAL_PACKET_SIZE = 1024;
   protected static final int COMP_HEADER_LENGTH = 3;
   protected static final int MAX_QUERY_SIZE_TO_EXPLAIN = 1048576;
   protected static final int SSL_REQUEST_LENGTH = 32;
   private static final String EXPLAINABLE_STATEMENT = "SELECT";
   private static final String[] EXPLAINABLE_STATEMENT_EXTENSION = new String[]{"INSERT", "UPDATE", "REPLACE", "DELETE"};
   protected MessageSender<NativePacketPayload> packetSender;
   protected MessageReader<NativePacketHeader, NativePacketPayload> packetReader;
   protected NativeServerSession serverSession;
   protected CompressedPacketSender compressedPacketSender;
   protected NativePacketPayload sharedSendPacket = null;
   protected NativePacketPayload reusablePacket = null;
   private SoftReference<NativePacketPayload> loadFileBufRef;
   protected byte packetSequence = 0;
   protected boolean useCompression = false;
   private RuntimeProperty<Integer> maxAllowedPacket;
   private RuntimeProperty<Boolean> useServerPrepStmts;
   private boolean autoGenerateTestcaseScript;
   private boolean logSlowQueries = false;
   private boolean useAutoSlowLog;
   private boolean profileSQL = false;
   private long slowQueryThreshold;
   private int commandCount = 0;
   protected boolean hadWarnings = false;
   private int warningCount = 0;
   protected Map<Class<? extends ProtocolEntity>, ProtocolEntityReader<? extends ProtocolEntity, ? extends Message>> PROTOCOL_ENTITY_CLASS_TO_TEXT_READER;
   protected Map<Class<? extends ProtocolEntity>, ProtocolEntityReader<? extends ProtocolEntity, ? extends Message>> PROTOCOL_ENTITY_CLASS_TO_BINARY_READER;
   private int statementExecutionDepth = 0;
   private List<QueryInterceptor> queryInterceptors;
   private RuntimeProperty<Boolean> maintainTimeStats;
   private RuntimeProperty<Integer> maxQuerySizeToLog;
   private InputStream localInfileInputStream;
   private BaseMetricsHolder metricsHolder;
   private String queryComment = null;
   private NativeMessageBuilder commandBuilder = null;
   private ResultsetRows streamingData = null;

   public static NativeProtocol getInstance(Session session, SocketConnection socketConnection, PropertySet propertySet, Log log, TransactionEventHandler transactionManager) {
      NativeProtocol protocol = new NativeProtocol(log);
      protocol.init(session, socketConnection, propertySet, transactionManager);
      return protocol;
   }

   public NativeProtocol(Log logger) {
      this.log = logger;
      this.metricsHolder = new BaseMetricsHolder();
   }

   public void init(Session sess, SocketConnection phConnection, PropertySet propSet, TransactionEventHandler trManager) {
      super.init(sess, phConnection, propSet, trManager);
      this.maintainTimeStats = this.propertySet.getBooleanProperty(PropertyKey.maintainTimeStats);
      this.maxQuerySizeToLog = this.propertySet.getIntegerProperty(PropertyKey.maxQuerySizeToLog);
      this.useAutoSlowLog = (Boolean)this.propertySet.getBooleanProperty(PropertyKey.autoSlowLog).getValue();
      this.logSlowQueries = (Boolean)this.propertySet.getBooleanProperty(PropertyKey.logSlowQueries).getValue();
      this.maxAllowedPacket = this.propertySet.getIntegerProperty(PropertyKey.maxAllowedPacket);
      this.profileSQL = (Boolean)this.propertySet.getBooleanProperty(PropertyKey.profileSQL).getValue();
      this.autoGenerateTestcaseScript = (Boolean)this.propertySet.getBooleanProperty(PropertyKey.autoGenerateTestcaseScript).getValue();
      this.useServerPrepStmts = this.propertySet.getBooleanProperty(PropertyKey.useServerPrepStmts);
      this.reusablePacket = new NativePacketPayload(1024);

      try {
         this.packetSender = new SimplePacketSender(this.socketConnection.getMysqlOutput());
         this.packetReader = new SimplePacketReader(this.socketConnection, this.maxAllowedPacket);
      } catch (IOException var7) {
         throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var7, this.getExceptionInterceptor());
      }

      if ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.logSlowQueries).getValue()) {
         this.calculateSlowQueryThreshold();
      }

      this.authProvider = new NativeAuthenticationProvider();
      this.authProvider.init(this, this.getPropertySet(), this.socketConnection.getExceptionInterceptor());
      Map<Class<? extends ProtocolEntity>, ProtocolEntityReader<? extends ProtocolEntity, NativePacketPayload>> protocolEntityClassToTextReader = new HashMap();
      protocolEntityClassToTextReader.put(ColumnDefinition.class, new ColumnDefinitionReader(this));
      protocolEntityClassToTextReader.put(ResultsetRow.class, new ResultsetRowReader(this));
      protocolEntityClassToTextReader.put(Resultset.class, new TextResultsetReader(this));
      this.PROTOCOL_ENTITY_CLASS_TO_TEXT_READER = Collections.unmodifiableMap(protocolEntityClassToTextReader);
      Map<Class<? extends ProtocolEntity>, ProtocolEntityReader<? extends ProtocolEntity, NativePacketPayload>> protocolEntityClassToBinaryReader = new HashMap();
      protocolEntityClassToBinaryReader.put(ColumnDefinition.class, new ColumnDefinitionReader(this));
      protocolEntityClassToBinaryReader.put(Resultset.class, new BinaryResultsetReader(this));
      this.PROTOCOL_ENTITY_CLASS_TO_BINARY_READER = Collections.unmodifiableMap(protocolEntityClassToBinaryReader);
   }

   public MessageBuilder<NativePacketPayload> getMessageBuilder() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public MessageSender<NativePacketPayload> getPacketSender() {
      return this.packetSender;
   }

   public MessageReader<NativePacketHeader, NativePacketPayload> getPacketReader() {
      return this.packetReader;
   }

   private NativeMessageBuilder getCommandBuilder() {
      return this.commandBuilder != null ? this.commandBuilder : (this.commandBuilder = new NativeMessageBuilder(this.serverSession.supportsQueryAttributes()));
   }

   public void negotiateSSLConnection() {
      if (!ExportControlled.enabled()) {
         throw new CJConnectionFeatureNotAvailableException(this.getPropertySet(), this.serverSession, this.getPacketSentTimeHolder(), (Exception)null);
      } else {
         long clientParam = this.serverSession.getClientParam();
         NativePacketPayload packet = new NativePacketPayload(32);
         packet.writeInteger(NativeConstants.IntegerDataType.INT4, clientParam);
         packet.writeInteger(NativeConstants.IntegerDataType.INT4, 16777215L);
         packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)this.serverSession.getCharsetSettings().configurePreHandshake(false));
         packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, new byte[23]);
         this.send(packet, packet.getPosition());

         try {
            this.socketConnection.performTlsHandshake(this.serverSession, this.log);
            this.packetSender = new SimplePacketSender(this.socketConnection.getMysqlOutput());
            this.packetReader = new SimplePacketReader(this.socketConnection, this.maxAllowedPacket);
         } catch (FeatureNotAvailableException var5) {
            throw new CJConnectionFeatureNotAvailableException(this.getPropertySet(), this.serverSession, this.getPacketSentTimeHolder(), var5);
         } catch (IOException var6) {
            throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var6, this.getExceptionInterceptor());
         }
      }
   }

   public void rejectProtocol(NativePacketPayload msg) {
      try {
         this.socketConnection.getMysqlSocket().close();
      } catch (Exception var8) {
      }

      int errno = true;
      NativePacketPayload buf = msg;
      msg.setPosition(1);
      int errno = (int)msg.readInteger(NativeConstants.IntegerDataType.INT2);
      String serverErrorMessage = "";

      try {
         serverErrorMessage = buf.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
      } catch (Exception var7) {
      }

      StringBuilder errorBuf = new StringBuilder(Messages.getString("Protocol.0"));
      errorBuf.append(serverErrorMessage);
      errorBuf.append("\"");
      String xOpen = MysqlErrorNumbers.mysqlToSqlState(errno);
      throw ExceptionFactory.createException(MysqlErrorNumbers.get(xOpen) + ", " + errorBuf.toString(), xOpen, errno, false, (Throwable)null, this.getExceptionInterceptor());
   }

   public void beforeHandshake() {
      this.packetReader.resetMessageSequence();
      this.serverSession = new NativeServerSession(this.propertySet);
      this.serverSession.setCharsetSettings(new NativeCharsetSettings((NativeSession)this.session));
      this.serverSession.setCapabilities(this.readServerCapabilities());
   }

   public void afterHandshake() {
      this.checkTransactionState();

      try {
         if ((this.serverSession.getCapabilities().getCapabilityFlags() & 32) != 0 && (Boolean)this.propertySet.getBooleanProperty(PropertyKey.useCompression).getValue() && !(this.socketConnection.getMysqlInput().getUnderlyingStream() instanceof CompressedInputStream)) {
            this.useCompression = true;
            this.socketConnection.setMysqlInput(new FullReadInputStream(new CompressedInputStream(this.socketConnection.getMysqlInput(), this.propertySet.getBooleanProperty(PropertyKey.traceProtocol), this.log)));
            this.compressedPacketSender = new CompressedPacketSender(this.socketConnection.getMysqlOutput());
            this.packetSender = this.compressedPacketSender;
         }

         this.applyPacketDecorators(this.packetSender, this.packetReader);
         this.socketConnection.getSocketFactory().afterHandshake();
      } catch (IOException var2) {
         throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var2, this.getExceptionInterceptor());
      }

      RuntimeProperty<Boolean> useInformationSchema = this.propertySet.getProperty(PropertyKey.useInformationSchema);
      if (this.versionMeetsMinimum(8, 0, 3) && !(Boolean)useInformationSchema.getValue() && !useInformationSchema.isExplicitlySet()) {
         useInformationSchema.setValue(true);
      }

      this.maintainTimeStats.addListener(this);
      this.propertySet.getBooleanProperty(PropertyKey.traceProtocol).addListener(this);
      this.propertySet.getBooleanProperty(PropertyKey.enablePacketDebug).addListener(this);
   }

   public void handlePropertyChange(RuntimeProperty<?> prop) {
      switch (prop.getPropertyDefinition().getPropertyKey()) {
         case maintainTimeStats:
         case traceProtocol:
         case enablePacketDebug:
            this.applyPacketDecorators(this.packetSender.undecorateAll(), this.packetReader.undecorateAll());
         default:
      }
   }

   public void applyPacketDecorators(MessageSender<NativePacketPayload> sender, MessageReader<NativePacketHeader, NativePacketPayload> messageReader) {
      TimeTrackingPacketSender ttSender = null;
      TimeTrackingPacketReader ttReader = null;
      LinkedList<StringBuilder> debugRingBuffer = null;
      if ((Boolean)this.maintainTimeStats.getValue()) {
         ttSender = new TimeTrackingPacketSender((MessageSender)sender);
         sender = ttSender;
         ttReader = new TimeTrackingPacketReader((MessageReader)messageReader);
         messageReader = ttReader;
      }

      if ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.traceProtocol).getValue()) {
         sender = new TracingPacketSender((MessageSender)sender, this.log, this.socketConnection.getHost(), this.getServerSession().getCapabilities().getThreadId());
         messageReader = new TracingPacketReader((MessageReader)messageReader, this.log);
      }

      if ((Boolean)this.getPropertySet().getBooleanProperty(PropertyKey.enablePacketDebug).getValue()) {
         debugRingBuffer = new LinkedList();
         sender = new DebugBufferingPacketSender((MessageSender)sender, debugRingBuffer, this.propertySet.getIntegerProperty(PropertyKey.packetDebugBufferSize));
         messageReader = new DebugBufferingPacketReader((MessageReader)messageReader, debugRingBuffer, this.propertySet.getIntegerProperty(PropertyKey.packetDebugBufferSize));
      }

      MessageReader<NativePacketHeader, NativePacketPayload> messageReader = new MultiPacketReader((MessageReader)messageReader);
      synchronized(this.packetReader) {
         this.packetReader = messageReader;
         this.packetDebugRingBuffer = debugRingBuffer;
         this.setPacketSentTimeHolder((PacketSentTimeHolder)(ttSender != null ? ttSender : new PacketSentTimeHolder() {
         }));
      }

      synchronized(this.packetSender) {
         this.packetSender = (MessageSender)sender;
         this.setPacketReceivedTimeHolder((PacketReceivedTimeHolder)(ttReader != null ? ttReader : new PacketReceivedTimeHolder() {
         }));
      }
   }

   public NativeCapabilities readServerCapabilities() {
      NativePacketPayload buf = this.readMessage((NativePacketPayload)null);
      if (buf.isErrorPacket()) {
         this.rejectProtocol(buf);
      }

      return new NativeCapabilities(buf);
   }

   public NativeServerSession getServerSession() {
      return this.serverSession;
   }

   public void changeDatabase(String database) {
      if (database != null && database.length() != 0) {
         try {
            this.sendCommand(this.getCommandBuilder().buildComInitDb(this.getSharedSendPacket(), database), false, 0);
         } catch (CJException var3) {
            if (!(Boolean)this.getPropertySet().getBooleanProperty(PropertyKey.createDatabaseIfNotExist).getValue()) {
               throw ExceptionFactory.createCommunicationsException(this.getPropertySet(), this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var3, this.getExceptionInterceptor());
            }

            this.sendCommand(this.getCommandBuilder().buildComQuery(this.getSharedSendPacket(), "CREATE DATABASE IF NOT EXISTS " + StringUtils.quoteIdentifier(database, true)), false, 0);
            this.sendCommand(this.getCommandBuilder().buildComInitDb(this.getSharedSendPacket(), database), false, 0);
         }

      }
   }

   public final NativePacketPayload readMessage(NativePacketPayload reuse) {
      try {
         NativePacketHeader header = (NativePacketHeader)this.packetReader.readHeader();
         NativePacketPayload buf = (NativePacketPayload)this.packetReader.readMessage(Optional.ofNullable(reuse), header);
         this.packetSequence = header.getMessageSequence();
         return buf;
      } catch (IOException var4) {
         throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var4, this.getExceptionInterceptor());
      } catch (OutOfMemoryError var5) {
         throw ExceptionFactory.createException(var5.getMessage(), "HY001", 0, false, var5, this.exceptionInterceptor);
      }
   }

   public final NativePacketPayload probeMessage(NativePacketPayload reuse) {
      try {
         NativePacketHeader header = (NativePacketHeader)this.packetReader.probeHeader();
         NativePacketPayload buf = (NativePacketPayload)this.packetReader.probeMessage(Optional.ofNullable(reuse), header);
         this.packetSequence = header.getMessageSequence();
         return buf;
      } catch (IOException var4) {
         throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var4, this.getExceptionInterceptor());
      } catch (OutOfMemoryError var5) {
         throw ExceptionFactory.createException(var5.getMessage(), "HY001", 0, false, var5, this.exceptionInterceptor);
      }
   }

   public final void send(Message packet, int packetLen) {
      try {
         if ((Integer)this.maxAllowedPacket.getValue() > 0 && packetLen > (Integer)this.maxAllowedPacket.getValue()) {
            throw new CJPacketTooBigException((long)packetLen, (long)(Integer)this.maxAllowedPacket.getValue());
         } else {
            ++this.packetSequence;
            this.packetSender.send(packet.getByteBuffer(), packetLen, this.packetSequence);
            if (packet == this.sharedSendPacket) {
               this.reclaimLargeSharedSendPacket();
            }

         }
      } catch (IOException var4) {
         throw ExceptionFactory.createCommunicationsException(this.getPropertySet(), this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var4, this.getExceptionInterceptor());
      }
   }

   public final NativePacketPayload sendCommand(Message queryPacket, boolean skipCheck, int timeoutMillis) {
      int command = queryPacket.getByteBuffer()[0];
      ++this.commandCount;
      if (this.queryInterceptors != null) {
         NativePacketPayload interceptedPacketPayload = (NativePacketPayload)this.invokeQueryInterceptorsPre(queryPacket, false);
         if (interceptedPacketPayload != null) {
            return interceptedPacketPayload;
         }
      }

      this.packetReader.resetMessageSequence();
      int oldTimeout = 0;
      if (timeoutMillis != 0) {
         try {
            oldTimeout = this.socketConnection.getMysqlSocket().getSoTimeout();
            this.socketConnection.getMysqlSocket().setSoTimeout(timeoutMillis);
         } catch (IOException var22) {
            throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var22, this.getExceptionInterceptor());
         }
      }

      NativePacketPayload var7;
      try {
         this.checkForOutstandingStreamingData();
         this.serverSession.setStatusFlags(0, true);
         this.hadWarnings = false;
         this.setWarningCount(0);
         if (this.useCompression) {
            int bytesLeft = this.socketConnection.getMysqlInput().available();
            if (bytesLeft > 0) {
               this.socketConnection.getMysqlInput().skip((long)bytesLeft);
            }
         }

         try {
            this.clearInputStream();
            this.packetSequence = -1;
            this.send(queryPacket, queryPacket.getPosition());
         } catch (CJException var20) {
            throw var20;
         } catch (Exception var21) {
            throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var21, this.getExceptionInterceptor());
         }

         NativePacketPayload returnPacket = null;
         if (!skipCheck) {
            if (command == 23 || command == 26) {
               this.packetReader.resetMessageSequence();
            }

            returnPacket = this.checkErrorMessage(command);
            if (this.queryInterceptors != null) {
               returnPacket = (NativePacketPayload)this.invokeQueryInterceptorsPost(queryPacket, returnPacket, false);
            }
         }

         var7 = returnPacket;
      } catch (IOException var23) {
         this.serverSession.preserveOldTransactionState();
         throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var23, this.getExceptionInterceptor());
      } catch (CJException var24) {
         this.serverSession.preserveOldTransactionState();
         throw var24;
      } finally {
         if (timeoutMillis != 0) {
            try {
               this.socketConnection.getMysqlSocket().setSoTimeout(oldTimeout);
            } catch (IOException var19) {
               throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var19, this.getExceptionInterceptor());
            }
         }

      }

      return var7;
   }

   public void checkTransactionState() {
      int transState = this.serverSession.getTransactionState();
      if (transState == 3) {
         this.transactionManager.transactionCompleted();
      } else if (transState == 2) {
         this.transactionManager.transactionBegun();
      }

   }

   public NativePacketPayload checkErrorMessage() {
      return this.checkErrorMessage(-1);
   }

   private NativePacketPayload checkErrorMessage(int command) {
      NativePacketPayload resultPacket = null;
      this.serverSession.setStatusFlags(0);

      try {
         resultPacket = this.readMessage(this.reusablePacket);
      } catch (CJException var4) {
         throw var4;
      } catch (Exception var5) {
         throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var5, this.getExceptionInterceptor());
      }

      this.checkErrorMessage(resultPacket);
      return resultPacket;
   }

   public void checkErrorMessage(NativePacketPayload resultPacket) {
      resultPacket.setPosition(0);
      byte statusCode = (byte)((int)resultPacket.readInteger(NativeConstants.IntegerDataType.INT1));
      if (statusCode == -1) {
         int errno = true;
         int errno = (int)resultPacket.readInteger(NativeConstants.IntegerDataType.INT2);
         String xOpen = null;
         String serverErrorMessage = resultPacket.readString(NativeConstants.StringSelfDataType.STRING_TERM, this.serverSession.getCharsetSettings().getErrorMessageEncoding());
         if (serverErrorMessage.charAt(0) == '#') {
            if (serverErrorMessage.length() > 6) {
               xOpen = serverErrorMessage.substring(1, 6);
               serverErrorMessage = serverErrorMessage.substring(6);
               if (xOpen.equals("HY000")) {
                  xOpen = MysqlErrorNumbers.mysqlToSqlState(errno);
               }
            } else {
               xOpen = MysqlErrorNumbers.mysqlToSqlState(errno);
            }
         } else {
            xOpen = MysqlErrorNumbers.mysqlToSqlState(errno);
         }

         this.clearInputStream();
         StringBuilder errorBuf = new StringBuilder();
         String xOpenErrorMessage = MysqlErrorNumbers.get(xOpen);
         boolean useOnlyServerErrorMessages = (Boolean)this.propertySet.getBooleanProperty(PropertyKey.useOnlyServerErrorMessages).getValue();
         if (!useOnlyServerErrorMessages && xOpenErrorMessage != null) {
            errorBuf.append(xOpenErrorMessage);
            errorBuf.append(Messages.getString("Protocol.0"));
         }

         errorBuf.append(serverErrorMessage);
         if (!useOnlyServerErrorMessages && xOpenErrorMessage != null) {
            errorBuf.append("\"");
         }

         this.appendDeadlockStatusInformation(this.session, xOpen, errorBuf);
         if (xOpen != null) {
            if (xOpen.startsWith("22")) {
               throw new DataTruncationException(errorBuf.toString(), 0, true, false, 0, 0, errno);
            }

            if (errno == 1820) {
               throw (PasswordExpiredException)ExceptionFactory.createException(PasswordExpiredException.class, errorBuf.toString(), this.getExceptionInterceptor());
            }

            if (errno == 1862) {
               throw (ClosedOnExpiredPasswordException)ExceptionFactory.createException(ClosedOnExpiredPasswordException.class, errorBuf.toString(), this.getExceptionInterceptor());
            }

            if (errno == 4031) {
               throw (CJCommunicationsException)ExceptionFactory.createException(CJCommunicationsException.class, errorBuf.toString(), (Throwable)null, this.getExceptionInterceptor());
            }
         }

         throw ExceptionFactory.createException(errorBuf.toString(), xOpen, errno, false, (Throwable)null, this.getExceptionInterceptor());
      }
   }

   private void reclaimLargeSharedSendPacket() {
      if (this.sharedSendPacket != null && this.sharedSendPacket.getCapacity() > 1048576) {
         this.sharedSendPacket = new NativePacketPayload(1024);
      }

   }

   public void clearInputStream() {
      try {
         int len;
         while((len = this.socketConnection.getMysqlInput().available()) > 0 && this.socketConnection.getMysqlInput().skip((long)len) > 0L) {
         }

      } catch (IOException var2) {
         throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var2, this.getExceptionInterceptor());
      }
   }

   public void reclaimLargeReusablePacket() {
      if (this.reusablePacket != null && this.reusablePacket.getCapacity() > 1048576) {
         this.reusablePacket = new NativePacketPayload(1024);
      }

   }

   public final <T extends Resultset> T sendQueryString(Query callingQuery, String query, String characterEncoding, int maxRows, boolean streamResults, ColumnDefinition cachedMetadata, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) throws IOException {
      String statementComment = this.queryComment;
      if ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.includeThreadNamesAsStatementComment).getValue()) {
         statementComment = (statementComment != null ? statementComment + ", " : "") + "java thread: " + Thread.currentThread().getName();
      }

      int packLength = 1 + query.length() * 4 + 2;
      byte[] commentAsBytes = null;
      if (statementComment != null) {
         commentAsBytes = StringUtils.getBytes(statementComment, characterEncoding);
         packLength += commentAsBytes.length;
         packLength += 6;
      }

      boolean supportsQueryAttributes = this.serverSession.supportsQueryAttributes();
      QueryAttributesBindings queryAttributes = null;
      if (!supportsQueryAttributes && callingQuery != null && callingQuery.getQueryAttributesBindings().getCount() > 0) {
         this.log.logWarn(Messages.getString("QueryAttributes.SetButNotSupported"));
      }

      if (supportsQueryAttributes) {
         if (callingQuery != null) {
            queryAttributes = callingQuery.getQueryAttributesBindings();
         }

         if (queryAttributes != null && queryAttributes.getCount() > 0) {
            packLength += 10;
            packLength += (queryAttributes.getCount() + 7) / 8 + 1;

            for(int i = 0; i < queryAttributes.getCount(); ++i) {
               QueryAttributesBindValue queryAttribute = queryAttributes.getAttributeValue(i);
               packLength = (int)((long)packLength + (long)(2 + queryAttribute.getName().length()) + queryAttribute.getBoundLength());
            }
         } else {
            packLength += 2;
         }
      }

      NativePacketPayload sendPacket = new NativePacketPayload(packLength);
      sendPacket.setPosition(0);
      sendPacket.writeInteger(NativeConstants.IntegerDataType.INT1, 3L);
      if (supportsQueryAttributes) {
         if (queryAttributes != null && queryAttributes.getCount() > 0) {
            sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, (long)queryAttributes.getCount());
            sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 1L);
            byte[] nullBitsBuffer = new byte[(queryAttributes.getCount() + 7) / 8];

            for(int i = 0; i < queryAttributes.getCount(); ++i) {
               if (queryAttributes.getAttributeValue(i).isNull()) {
                  nullBitsBuffer[i >>> 3] = (byte)(nullBitsBuffer[i >>> 3] | 1 << (i & 7));
               }
            }

            sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_VAR, nullBitsBuffer);
            sendPacket.writeInteger(NativeConstants.IntegerDataType.INT1, 1L);
            queryAttributes.runThroughAll((a) -> {
               sendPacket.writeInteger(NativeConstants.IntegerDataType.INT2, (long)a.getType());
               sendPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, a.getName().getBytes());
            });
            ValueEncoder valueEncoder = new ValueEncoder(sendPacket, characterEncoding, this.serverSession.getDefaultTimeZone());
            queryAttributes.runThroughAll((a) -> {
               valueEncoder.encodeValue(a.getValue(), a.getType());
            });
         } else {
            sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 0L);
            sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 1L);
         }
      }

      sendPacket.setTag("QUERY");
      if (commentAsBytes != null) {
         sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, Constants.SLASH_STAR_SPACE_AS_BYTES);
         sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, commentAsBytes);
         sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
      }

      if (!this.session.getServerSession().getCharsetSettings().doesPlatformDbCharsetMatches() && StringUtils.startsWithIgnoreCaseAndWs(query, "LOAD DATA")) {
         sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, StringUtils.getBytes(query));
      } else {
         sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, StringUtils.getBytes(query, characterEncoding));
      }

      return this.sendQueryPacket(callingQuery, sendPacket, maxRows, streamResults, cachedMetadata, resultSetFactory);
   }

   public final <T extends Resultset> T sendQueryPacket(Query callingQuery, NativePacketPayload queryPacket, int maxRows, boolean streamResults, ColumnDefinition cachedMetadata, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) throws IOException {
      long queryStartTime = this.getCurrentTimeNanosOrMillis();
      ++this.statementExecutionDepth;
      byte[] queryBuf = queryPacket.getByteBuffer();
      int oldPacketPosition = queryPacket.getPosition();
      int queryPosition = queryPacket.getTag("QUERY");
      LazyString query = new LazyString(queryBuf, queryPosition, oldPacketPosition - queryPosition);

      try {
         if (this.queryInterceptors != null) {
            T interceptedResults = this.invokeQueryInterceptorsPre(query, callingQuery, false);
            if (interceptedResults != null) {
               Resultset var35 = interceptedResults;
               return var35;
            }
         }

         if (this.autoGenerateTestcaseScript) {
            StringBuilder debugBuf = new StringBuilder(query.length() + 32);
            this.generateQueryCommentBlock(debugBuf);
            debugBuf.append(query);
            debugBuf.append(';');
            TestUtils.dumpTestcaseQuery(debugBuf.toString());
         }

         NativePacketPayload resultPacket = this.sendCommand(queryPacket, false, 0);
         long queryEndTime = this.getCurrentTimeNanosOrMillis();
         long queryDuration = queryEndTime - queryStartTime;
         if (callingQuery != null) {
            callingQuery.setExecuteTime(queryDuration);
         }

         boolean var10000;
         label287: {
            if (this.logSlowQueries) {
               label283: {
                  if (this.useAutoSlowLog) {
                     if (!this.metricsHolder.checkAbonormallyLongQuery(queryDuration)) {
                        break label283;
                     }
                  } else if (queryDuration <= (long)(Integer)this.propertySet.getIntegerProperty(PropertyKey.slowQueryThresholdMillis).getValue()) {
                     break label283;
                  }

                  var10000 = true;
                  break label287;
               }
            }

            var10000 = false;
         }

         boolean queryWasSlow = var10000;
         long fetchBeginTime = this.profileSQL ? this.getCurrentTimeNanosOrMillis() : 0L;
         T rs = this.readAllResults(maxRows, streamResults, resultPacket, false, cachedMetadata, resultSetFactory);
         if (this.profileSQL || queryWasSlow) {
            long fetchEndTime = this.profileSQL ? this.getCurrentTimeNanosOrMillis() : 0L;
            boolean truncated = oldPacketPosition - queryPosition > (Integer)this.maxQuerySizeToLog.getValue();
            int extractPosition = truncated ? (Integer)this.maxQuerySizeToLog.getValue() + queryPosition : oldPacketPosition;
            String extractedQuery = StringUtils.toString(queryBuf, queryPosition, extractPosition - queryPosition);
            if (truncated) {
               extractedQuery = extractedQuery + Messages.getString("Protocol.2");
            }

            ProfilerEventHandler eventSink = this.session.getProfilerEventHandler();
            if (this.logSlowQueries) {
               if (queryWasSlow) {
                  eventSink.processEvent((byte)6, this.session, callingQuery, rs, queryDuration, new Throwable(), Messages.getString("Protocol.SlowQuery", new Object[]{this.useAutoSlowLog ? " 95% of all queries " : String.valueOf(this.slowQueryThreshold), this.queryTimingUnits, queryDuration, extractedQuery}));
                  if ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.explainSlowQueries).getValue()) {
                     if (oldPacketPosition - queryPosition < 1048576) {
                        queryPacket.setPosition(queryPosition);
                        this.explainSlowQuery(query.toString(), extractedQuery);
                     } else {
                        this.log.logWarn(Messages.getString("Protocol.3", new Object[]{1048576}));
                     }
                  }
               }

               if (this.serverSession.noGoodIndexUsed()) {
                  eventSink.processEvent((byte)6, this.session, callingQuery, rs, queryDuration, new Throwable(), Messages.getString("Protocol.4") + extractedQuery);
               }

               if (this.serverSession.noIndexUsed()) {
                  eventSink.processEvent((byte)6, this.session, callingQuery, rs, queryDuration, new Throwable(), Messages.getString("Protocol.5") + extractedQuery);
               }

               if (this.serverSession.queryWasSlow()) {
                  eventSink.processEvent((byte)6, this.session, callingQuery, rs, queryDuration, new Throwable(), Messages.getString("Protocol.ServerSlowQuery") + extractedQuery);
               }
            }

            if (this.profileSQL) {
               eventSink.processEvent((byte)3, this.session, callingQuery, rs, queryDuration, new Throwable(), extractedQuery);
               eventSink.processEvent((byte)5, this.session, callingQuery, rs, fetchEndTime - fetchBeginTime, new Throwable(), (String)null);
            }
         }

         if (this.hadWarnings) {
            this.scanForAndThrowDataTruncation();
         }

         if (this.queryInterceptors != null) {
            rs = this.invokeQueryInterceptorsPost(query, callingQuery, rs, false);
         }

         Resultset var36 = rs;
         return var36;
      } catch (CJException var31) {
         if (this.queryInterceptors != null) {
            this.invokeQueryInterceptorsPost(query, callingQuery, (Resultset)null, false);
         }

         if (callingQuery != null) {
            callingQuery.checkCancelTimeout();
         }

         throw var31;
      } finally {
         --this.statementExecutionDepth;
      }
   }

   public <T extends Resultset> T invokeQueryInterceptorsPre(Supplier<String> sql, Query interceptedQuery, boolean forceExecute) {
      T previousResultSet = null;
      int i = 0;

      for(int s = this.queryInterceptors.size(); i < s; ++i) {
         QueryInterceptor interceptor = (QueryInterceptor)this.queryInterceptors.get(i);
         boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
         boolean shouldExecute = executeTopLevelOnly && (this.statementExecutionDepth == 1 || forceExecute) || !executeTopLevelOnly;
         if (shouldExecute) {
            T interceptedResultSet = interceptor.preProcess(sql, interceptedQuery);
            if (interceptedResultSet != null) {
               previousResultSet = interceptedResultSet;
            }
         }
      }

      return previousResultSet;
   }

   public <M extends Message> M invokeQueryInterceptorsPre(M queryPacket, boolean forceExecute) {
      M previousPacketPayload = null;
      int i = 0;

      for(int s = this.queryInterceptors.size(); i < s; ++i) {
         QueryInterceptor interceptor = (QueryInterceptor)this.queryInterceptors.get(i);
         M interceptedPacketPayload = interceptor.preProcess(queryPacket);
         if (interceptedPacketPayload != null) {
            previousPacketPayload = interceptedPacketPayload;
         }
      }

      return previousPacketPayload;
   }

   public <T extends Resultset> T invokeQueryInterceptorsPost(Supplier<String> sql, Query interceptedQuery, T originalResultSet, boolean forceExecute) {
      int i = 0;

      for(int s = this.queryInterceptors.size(); i < s; ++i) {
         QueryInterceptor interceptor = (QueryInterceptor)this.queryInterceptors.get(i);
         boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
         boolean shouldExecute = executeTopLevelOnly && (this.statementExecutionDepth == 1 || forceExecute) || !executeTopLevelOnly;
         if (shouldExecute) {
            T interceptedResultSet = interceptor.postProcess(sql, interceptedQuery, originalResultSet, this.serverSession);
            if (interceptedResultSet != null) {
               originalResultSet = interceptedResultSet;
            }
         }
      }

      return originalResultSet;
   }

   public <M extends Message> M invokeQueryInterceptorsPost(M queryPacket, M originalResponsePacket, boolean forceExecute) {
      int i = 0;

      for(int s = this.queryInterceptors.size(); i < s; ++i) {
         QueryInterceptor interceptor = (QueryInterceptor)this.queryInterceptors.get(i);
         M interceptedPacketPayload = interceptor.postProcess(queryPacket, originalResponsePacket);
         if (interceptedPacketPayload != null) {
            originalResponsePacket = interceptedPacketPayload;
         }
      }

      return originalResponsePacket;
   }

   public long getCurrentTimeNanosOrMillis() {
      return this.useNanosForElapsedTime ? TimeUtil.getCurrentTimeNanosOrMillis() : System.currentTimeMillis();
   }

   public boolean hadWarnings() {
      return this.hadWarnings;
   }

   public void setHadWarnings(boolean hadWarnings) {
      this.hadWarnings = hadWarnings;
   }

   public void explainSlowQuery(String query, String truncatedQuery) {
      if (StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, "SELECT") || this.versionMeetsMinimum(5, 6, 3) && StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, EXPLAINABLE_STATEMENT_EXTENSION) != -1) {
         try {
            NativePacketPayload resultPacket = this.sendCommand(this.getCommandBuilder().buildComQuery(this.getSharedSendPacket(), "EXPLAIN " + query), false, 0);
            Resultset rs = this.readAllResults(-1, false, resultPacket, false, (ColumnDefinition)null, new ResultsetFactory(Resultset.Type.FORWARD_ONLY, (Resultset.Concurrency)null));
            StringBuilder explainResults = new StringBuilder(Messages.getString("Protocol.6"));
            explainResults.append(truncatedQuery);
            explainResults.append(Messages.getString("Protocol.7"));
            this.appendResultSetSlashGStyle(explainResults, rs);
            this.log.logWarn(explainResults.toString());
         } catch (CJException var6) {
            throw var6;
         } catch (Exception var7) {
            throw ExceptionFactory.createException((String)var7.getMessage(), (Throwable)var7, (ExceptionInterceptor)this.getExceptionInterceptor());
         }
      }

   }

   public final void skipPacket() {
      try {
         int packetLength = ((NativePacketHeader)this.packetReader.readHeader()).getMessageSize();
         this.socketConnection.getMysqlInput().skipFully((long)packetLength);
      } catch (IOException var2) {
         throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var2, this.getExceptionInterceptor());
      }
   }

   public final void quit() {
      try {
         try {
            if (!ExportControlled.isSSLEstablished(this.socketConnection.getMysqlSocket()) && !this.socketConnection.getMysqlSocket().isClosed()) {
               try {
                  this.socketConnection.getMysqlSocket().shutdownInput();
               } catch (UnsupportedOperationException var6) {
               }
            }
         } catch (IOException var7) {
         }

         this.packetSequence = -1;
         NativePacketPayload packet = new NativePacketPayload(1);
         this.send(this.getCommandBuilder().buildComQuit(packet), packet.getPosition());
      } finally {
         this.socketConnection.forceClose();
         this.localInfileInputStream = null;
      }

   }

   public NativePacketPayload getSharedSendPacket() {
      if (this.sharedSendPacket == null) {
         this.sharedSendPacket = new NativePacketPayload(1024);
      }

      this.sharedSendPacket.setPosition(0);
      return this.sharedSendPacket;
   }

   private void calculateSlowQueryThreshold() {
      this.slowQueryThreshold = (long)(Integer)this.propertySet.getIntegerProperty(PropertyKey.slowQueryThresholdMillis).getValue();
      if ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useNanosForElapsedTime).getValue()) {
         long nanosThreshold = (Long)this.propertySet.getLongProperty(PropertyKey.slowQueryThresholdNanos).getValue();
         if (nanosThreshold != 0L) {
            this.slowQueryThreshold = nanosThreshold;
         } else {
            this.slowQueryThreshold *= 1000000L;
         }
      }

   }

   public void changeUser(String user, String password, String database) {
      this.packetSequence = -1;
      this.packetSender = this.packetSender.undecorateAll();
      this.packetReader = this.packetReader.undecorateAll();
      this.authProvider.changeUser(user, password, database);
   }

   protected boolean useNanosForElapsedTime() {
      return this.useNanosForElapsedTime;
   }

   public long getSlowQueryThreshold() {
      return this.slowQueryThreshold;
   }

   public int getCommandCount() {
      return this.commandCount;
   }

   public void setQueryInterceptors(List<QueryInterceptor> queryInterceptors) {
      this.queryInterceptors = queryInterceptors.isEmpty() ? null : queryInterceptors;
   }

   public List<QueryInterceptor> getQueryInterceptors() {
      return this.queryInterceptors;
   }

   public void setSocketTimeout(int milliseconds) {
      try {
         Socket soc = this.socketConnection.getMysqlSocket();
         if (soc != null) {
            soc.setSoTimeout(milliseconds);
         }

      } catch (IOException var3) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.8"), var3, this.getExceptionInterceptor());
      }
   }

   public void releaseResources() {
      if (this.compressedPacketSender != null) {
         this.compressedPacketSender.stop();
      }

   }

   public void connect(String user, String password, String database) {
      this.beforeHandshake();
      this.authProvider.connect(user, password, database);
   }

   protected boolean isDataAvailable() {
      try {
         return this.socketConnection.getMysqlInput().available() > 0;
      } catch (IOException var2) {
         throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, this.getPacketSentTimeHolder(), this.getPacketReceivedTimeHolder(), var2, this.getExceptionInterceptor());
      }
   }

   public NativePacketPayload getReusablePacket() {
      return this.reusablePacket;
   }

   public int getWarningCount() {
      return this.warningCount;
   }

   public void setWarningCount(int warningCount) {
      this.warningCount = warningCount;
   }

   public void dumpPacketRingBuffer() {
      LinkedList<StringBuilder> localPacketDebugRingBuffer = this.packetDebugRingBuffer;
      if (localPacketDebugRingBuffer != null) {
         StringBuilder dumpBuffer = new StringBuilder();
         dumpBuffer.append("Last " + localPacketDebugRingBuffer.size() + " packets received from server, from oldest->newest:\n");
         dumpBuffer.append("\n");
         Iterator<StringBuilder> ringBufIter = localPacketDebugRingBuffer.iterator();

         while(ringBufIter.hasNext()) {
            dumpBuffer.append((CharSequence)ringBufIter.next());
            dumpBuffer.append("\n");
         }

         this.log.logTrace(dumpBuffer.toString());
      }

   }

   public boolean versionMeetsMinimum(int major, int minor, int subminor) {
      return this.serverSession.getServerVersion().meetsMinimum(new ServerVersion(major, minor, subminor));
   }

   public static MysqlType findMysqlType(PropertySet propertySet, int mysqlTypeId, short colFlag, long length, LazyString tableName, LazyString originalTableName, int collationIndex, String encoding) {
      boolean isUnsigned = (colFlag & 32) > 0;
      boolean isFromFunction = originalTableName.length() == 0;
      boolean isBinary = (colFlag & 128) > 0;
      boolean isImplicitTemporaryTable = tableName.length() > 0 && tableName.toString().startsWith("#sql_");
      boolean isOpaqueBinary = !isBinary || collationIndex != 63 || mysqlTypeId != 254 && mysqlTypeId != 253 && mysqlTypeId != 15 ? "binary".equalsIgnoreCase(encoding) : !isImplicitTemporaryTable;
      switch (mysqlTypeId) {
         case 0:
         case 246:
            return isUnsigned ? MysqlType.DECIMAL_UNSIGNED : MysqlType.DECIMAL;
         case 1:
            if (!isUnsigned && length == 1L && (Boolean)propertySet.getBooleanProperty(PropertyKey.tinyInt1isBit).getValue()) {
               if ((Boolean)propertySet.getBooleanProperty(PropertyKey.transformedBitIsBoolean).getValue()) {
                  return MysqlType.BOOLEAN;
               }

               return MysqlType.BIT;
            }

            return isUnsigned ? MysqlType.TINYINT_UNSIGNED : MysqlType.TINYINT;
         case 2:
            return isUnsigned ? MysqlType.SMALLINT_UNSIGNED : MysqlType.SMALLINT;
         case 3:
            return isUnsigned ? MysqlType.INT_UNSIGNED : MysqlType.INT;
         case 4:
            return isUnsigned ? MysqlType.FLOAT_UNSIGNED : MysqlType.FLOAT;
         case 5:
            return isUnsigned ? MysqlType.DOUBLE_UNSIGNED : MysqlType.DOUBLE;
         case 6:
            return MysqlType.NULL;
         case 7:
            return MysqlType.TIMESTAMP;
         case 8:
            return isUnsigned ? MysqlType.BIGINT_UNSIGNED : MysqlType.BIGINT;
         case 9:
            return isUnsigned ? MysqlType.MEDIUMINT_UNSIGNED : MysqlType.MEDIUMINT;
         case 10:
            return MysqlType.DATE;
         case 11:
            return MysqlType.TIME;
         case 12:
            return MysqlType.DATETIME;
         case 13:
            return MysqlType.YEAR;
         case 15:
         case 253:
            return !isOpaqueBinary || isFromFunction && (Boolean)propertySet.getBooleanProperty(PropertyKey.functionsNeverReturnBlobs).getValue() ? MysqlType.VARCHAR : MysqlType.VARBINARY;
         case 16:
            return MysqlType.BIT;
         case 245:
            return MysqlType.JSON;
         case 247:
            return MysqlType.ENUM;
         case 248:
            return MysqlType.SET;
         case 249:
            if (isBinary && collationIndex == 63 && !(Boolean)propertySet.getBooleanProperty(PropertyKey.blobsAreStrings).getValue() && (!isFromFunction || !(Boolean)propertySet.getBooleanProperty(PropertyKey.functionsNeverReturnBlobs).getValue())) {
               return MysqlType.TINYBLOB;
            }

            return MysqlType.TINYTEXT;
         case 250:
            if (isBinary && collationIndex == 63 && !(Boolean)propertySet.getBooleanProperty(PropertyKey.blobsAreStrings).getValue() && (!isFromFunction || !(Boolean)propertySet.getBooleanProperty(PropertyKey.functionsNeverReturnBlobs).getValue())) {
               return MysqlType.MEDIUMBLOB;
            }

            return MysqlType.MEDIUMTEXT;
         case 251:
            if (isBinary && collationIndex == 63 && !(Boolean)propertySet.getBooleanProperty(PropertyKey.blobsAreStrings).getValue() && (!isFromFunction || !(Boolean)propertySet.getBooleanProperty(PropertyKey.functionsNeverReturnBlobs).getValue())) {
               return MysqlType.LONGBLOB;
            }

            return MysqlType.LONGTEXT;
         case 252:
            short newMysqlTypeId;
            if (length <= MysqlType.TINYBLOB.getPrecision()) {
               newMysqlTypeId = 249;
            } else {
               if (length <= MysqlType.BLOB.getPrecision()) {
                  if (isBinary && collationIndex == 63 && !(Boolean)propertySet.getBooleanProperty(PropertyKey.blobsAreStrings).getValue() && (!isFromFunction || !(Boolean)propertySet.getBooleanProperty(PropertyKey.functionsNeverReturnBlobs).getValue())) {
                     return MysqlType.BLOB;
                  }

                  int newMysqlTypeId = true;
                  return MysqlType.TEXT;
               }

               if (length <= MysqlType.MEDIUMBLOB.getPrecision()) {
                  newMysqlTypeId = 250;
               } else {
                  newMysqlTypeId = 251;
               }
            }

            return findMysqlType(propertySet, newMysqlTypeId, colFlag, length, tableName, originalTableName, collationIndex, encoding);
         case 254:
            if (isOpaqueBinary && !(Boolean)propertySet.getBooleanProperty(PropertyKey.blobsAreStrings).getValue()) {
               return MysqlType.BINARY;
            }

            return MysqlType.CHAR;
         case 255:
            return MysqlType.GEOMETRY;
         default:
            return MysqlType.UNKNOWN;
      }
   }

   public <T extends ProtocolEntity> T read(Class<T> requiredClass, ProtocolEntityFactory<T, NativePacketPayload> protocolEntityFactory) throws IOException {
      ProtocolEntityReader<T, NativePacketPayload> sr = (ProtocolEntityReader)this.PROTOCOL_ENTITY_CLASS_TO_TEXT_READER.get(requiredClass);
      if (sr == null) {
         throw (FeatureNotAvailableException)ExceptionFactory.createException(FeatureNotAvailableException.class, "ProtocolEntityReader isn't available for class " + requiredClass);
      } else {
         return sr.read(protocolEntityFactory);
      }
   }

   public <T extends ProtocolEntity> T read(Class<Resultset> requiredClass, int maxRows, boolean streamResults, NativePacketPayload resultPacket, boolean isBinaryEncoded, ColumnDefinition metadata, ProtocolEntityFactory<T, NativePacketPayload> protocolEntityFactory) throws IOException {
      ProtocolEntityReader<T, NativePacketPayload> sr = isBinaryEncoded ? (ProtocolEntityReader)this.PROTOCOL_ENTITY_CLASS_TO_BINARY_READER.get(requiredClass) : (ProtocolEntityReader)this.PROTOCOL_ENTITY_CLASS_TO_TEXT_READER.get(requiredClass);
      if (sr == null) {
         throw (FeatureNotAvailableException)ExceptionFactory.createException(FeatureNotAvailableException.class, "ProtocolEntityReader isn't available for class " + requiredClass);
      } else {
         return sr.read(maxRows, streamResults, resultPacket, metadata, protocolEntityFactory);
      }
   }

   public <T extends ProtocolEntity> T readNextResultset(T currentProtocolEntity, int maxRows, boolean streamResults, boolean isBinaryEncoded, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) throws IOException {
      T result = null;
      if (Resultset.class.isAssignableFrom(currentProtocolEntity.getClass()) && this.serverSession.useMultiResults() && this.serverSession.hasMoreResults()) {
         T currentResultSet = currentProtocolEntity;

         ProtocolEntity newResultSet;
         do {
            NativePacketPayload fieldPacket = this.checkErrorMessage();
            fieldPacket.setPosition(0);
            newResultSet = this.read(Resultset.class, maxRows, streamResults, (NativePacketPayload)fieldPacket, isBinaryEncoded, (ColumnDefinition)null, resultSetFactory);
            ((Resultset)currentResultSet).setNextResultset((Resultset)newResultSet);
            currentResultSet = newResultSet;
            if (result == null) {
               result = newResultSet;
            }
         } while(streamResults && this.serverSession.hasMoreResults() && !((Resultset)newResultSet).hasRows());
      }

      return result;
   }

   public <T extends Resultset> T readAllResults(int maxRows, boolean streamResults, NativePacketPayload resultPacket, boolean isBinaryEncoded, ColumnDefinition metadata, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) throws IOException {
      resultPacket.setPosition(0);
      T topLevelResultSet = (Resultset)this.read(Resultset.class, maxRows, streamResults, resultPacket, isBinaryEncoded, metadata, resultSetFactory);
      if (this.serverSession.hasMoreResults()) {
         T currentResultSet = topLevelResultSet;
         if (streamResults) {
            currentResultSet = (Resultset)this.readNextResultset(topLevelResultSet, maxRows, true, isBinaryEncoded, resultSetFactory);
         } else {
            while(this.serverSession.hasMoreResults()) {
               currentResultSet = (Resultset)this.readNextResultset(currentResultSet, maxRows, false, isBinaryEncoded, resultSetFactory);
            }

            this.clearInputStream();
         }
      }

      if (this.hadWarnings) {
         this.scanForAndThrowDataTruncation();
      }

      this.reclaimLargeReusablePacket();
      return topLevelResultSet;
   }

   public final <T> T readServerStatusForResultSets(NativePacketPayload rowPacket, boolean saveOldStatus) {
      T result = null;
      if (rowPacket.isEOFPacket()) {
         rowPacket.setPosition(1);
         this.warningCount = (int)rowPacket.readInteger(NativeConstants.IntegerDataType.INT2);
         if (this.warningCount > 0) {
            this.hadWarnings = true;
         }

         this.serverSession.setStatusFlags((int)rowPacket.readInteger(NativeConstants.IntegerDataType.INT2), saveOldStatus);
         this.checkTransactionState();
      } else {
         OkPacket ok = OkPacket.parse(rowPacket, this.serverSession.getCharsetSettings().getErrorMessageEncoding());
         result = ok;
         this.serverSession.setStatusFlags(ok.getStatusFlags(), saveOldStatus);
         this.serverSession.getServerSessionStateController().setSessionStateChanges(ok.getSessionStateChanges());
         this.checkTransactionState();
         this.warningCount = ok.getWarningCount();
         if (this.warningCount > 0) {
            this.hadWarnings = true;
         }
      }

      return result;
   }

   public <T extends QueryResult> T readQueryResult(ResultBuilder<T> resultBuilder) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public InputStream getLocalInfileInputStream() {
      return this.localInfileInputStream;
   }

   public void setLocalInfileInputStream(InputStream stream) {
      this.localInfileInputStream = stream;
   }

   public final NativePacketPayload sendFileToServer(String fileName) {
      NativePacketPayload filePacket = this.loadFileBufRef == null ? null : (NativePacketPayload)this.loadFileBufRef.get();
      int bigPacketLength = Math.min((Integer)this.maxAllowedPacket.getValue() - 12, this.alignPacketSize((Integer)this.maxAllowedPacket.getValue() - 16, 4096) - 12);
      int oneMeg = 1048576;
      int smallerPacketSizeAligned = Math.min(oneMeg - 12, this.alignPacketSize(oneMeg - 16, 4096) - 12);
      int packetLength = Math.min(smallerPacketSizeAligned, bigPacketLength);
      if (filePacket == null) {
         try {
            filePacket = new NativePacketPayload(packetLength);
            this.loadFileBufRef = new SoftReference(filePacket);
         } catch (OutOfMemoryError var20) {
            throw ExceptionFactory.createException(Messages.getString("MysqlIO.111", new Object[]{packetLength}), "HY001", 0, false, var20, this.exceptionInterceptor);
         }
      }

      filePacket.setPosition(0);
      byte[] fileBuf = new byte[packetLength];
      BufferedInputStream fileIn = null;

      try {
         fileIn = this.getFileStream(fileName);
         int bytesRead = false;

         int bytesRead;
         while((bytesRead = fileIn.read(fileBuf)) != -1) {
            filePacket.setPosition(0);
            filePacket.writeBytes((NativeConstants.StringLengthDataType)NativeConstants.StringLengthDataType.STRING_FIXED, fileBuf, 0, bytesRead);
            this.send(filePacket, filePacket.getPosition());
         }
      } catch (IOException var21) {
         boolean isParanoid = (Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue();
         StringBuilder messageBuf = new StringBuilder(Messages.getString("MysqlIO.62"));
         if (fileName != null && !isParanoid) {
            messageBuf.append("'");
            messageBuf.append(fileName);
            messageBuf.append("'");
         }

         messageBuf.append(Messages.getString("MysqlIO.63"));
         if (!isParanoid) {
            messageBuf.append(Messages.getString("MysqlIO.64"));
            messageBuf.append(Util.stackTraceToString(var21));
         }

         throw ExceptionFactory.createException((String)messageBuf.toString(), (Throwable)var21, (ExceptionInterceptor)this.exceptionInterceptor);
      } finally {
         if (fileIn != null) {
            try {
               fileIn.close();
            } catch (Exception var19) {
               throw ExceptionFactory.createException((String)Messages.getString("MysqlIO.65"), (Throwable)var19, (ExceptionInterceptor)this.exceptionInterceptor);
            }

            fileIn = null;
         } else {
            filePacket.setPosition(0);
            this.send(filePacket, filePacket.getPosition());
            this.checkErrorMessage();
         }

      }

      filePacket.setPosition(0);
      this.send(filePacket, filePacket.getPosition());
      return this.checkErrorMessage();
   }

   private BufferedInputStream getFileStream(String fileName) throws IOException {
      RuntimeProperty<Boolean> allowLoadLocalInfile = this.propertySet.getBooleanProperty(PropertyKey.allowLoadLocalInfile);
      RuntimeProperty<String> allowLoadLocaInfileInPath = this.propertySet.getStringProperty(PropertyKey.allowLoadLocalInfileInPath);
      RuntimeProperty<Boolean> allowUrlInLocalInfile = this.propertySet.getBooleanProperty(PropertyKey.allowUrlInLocalInfile);
      if (!(Boolean)allowLoadLocalInfile.getValue() && !allowLoadLocaInfileInPath.isExplicitlySet()) {
         throw ExceptionFactory.createException(Messages.getString("MysqlIO.LoadDataLocalNotAllowed"), this.exceptionInterceptor);
      } else if ((Boolean)allowLoadLocalInfile.getValue()) {
         InputStream hookedStream = this.getLocalInfileInputStream();
         if (hookedStream != null) {
            return new BufferedInputStream(hookedStream);
         } else {
            if ((Boolean)allowUrlInLocalInfile.getValue() && fileName.indexOf(58) != -1) {
               try {
                  URL urlFromFileName = new URL(fileName);
                  return new BufferedInputStream(urlFromFileName.openStream());
               } catch (MalformedURLException var12) {
               }
            }

            return new BufferedInputStream(new FileInputStream(fileName));
         }
      } else {
         String safePathValue = (String)allowLoadLocaInfileInPath.getValue();
         if (safePathValue.length() == 0) {
            throw ExceptionFactory.createException(Messages.getString("MysqlIO.60", new Object[]{safePathValue, PropertyKey.allowLoadLocalInfileInPath.getKeyName()}), this.exceptionInterceptor);
         } else {
            Path safePath;
            try {
               safePath = Paths.get(safePathValue).toRealPath();
            } catch (InvalidPathException | IOException var11) {
               throw ExceptionFactory.createException((String)Messages.getString("MysqlIO.60", new Object[]{safePathValue, PropertyKey.allowLoadLocalInfileInPath.getKeyName()}), (Throwable)var11, (ExceptionInterceptor)this.exceptionInterceptor);
            }

            if ((Boolean)allowUrlInLocalInfile.getValue()) {
               try {
                  URL urlFromFileName = new URL(fileName);
                  if (!urlFromFileName.getProtocol().equalsIgnoreCase("file")) {
                     throw ExceptionFactory.createException(Messages.getString("MysqlIO.66", new Object[]{urlFromFileName.getProtocol()}), this.exceptionInterceptor);
                  }

                  InetAddress filePath;
                  try {
                     filePath = InetAddress.getByName(urlFromFileName.getHost());
                     if (!filePath.isLoopbackAddress()) {
                        throw ExceptionFactory.createException(Messages.getString("MysqlIO.67", new Object[]{urlFromFileName.getHost()}), this.exceptionInterceptor);
                     }
                  } catch (UnknownHostException var15) {
                     throw ExceptionFactory.createException((String)Messages.getString("MysqlIO.68", new Object[]{fileName}), (Throwable)var15, (ExceptionInterceptor)this.exceptionInterceptor);
                  }

                  filePath = null;

                  Path filePath;
                  try {
                     filePath = Paths.get(urlFromFileName.toURI()).toRealPath();
                  } catch (InvalidPathException var13) {
                     String pathString = urlFromFileName.getPath();
                     if (pathString.indexOf(58) != -1 && (pathString.startsWith("/") || pathString.startsWith("\\"))) {
                        pathString = pathString.replaceFirst("^[/\\\\]*", "");
                     }

                     filePath = Paths.get(pathString).toRealPath();
                  } catch (IllegalArgumentException var14) {
                     filePath = Paths.get(urlFromFileName.getPath()).toRealPath();
                  }

                  if (!filePath.startsWith(safePath)) {
                     throw ExceptionFactory.createException(Messages.getString("MysqlIO.61", new Object[]{filePath, safePath}), this.exceptionInterceptor);
                  }

                  return new BufferedInputStream(urlFromFileName.openStream());
               } catch (URISyntaxException | MalformedURLException var16) {
               }
            }

            Path filePath = Paths.get(fileName).toRealPath();
            if (!filePath.startsWith(safePath)) {
               throw ExceptionFactory.createException(Messages.getString("MysqlIO.61", new Object[]{filePath, safePath}), this.exceptionInterceptor);
            } else {
               return new BufferedInputStream(new FileInputStream(filePath.toFile()));
            }
         }
      }
   }

   private int alignPacketSize(int a, int l) {
      return a + l - 1 & ~(l - 1);
   }

   public ResultsetRows getStreamingData() {
      return this.streamingData;
   }

   public void setStreamingData(ResultsetRows streamingData) {
      this.streamingData = streamingData;
   }

   public void checkForOutstandingStreamingData() {
      if (this.streamingData != null) {
         boolean shouldClobber = (Boolean)this.propertySet.getBooleanProperty(PropertyKey.clobberStreamingResults).getValue();
         if (!shouldClobber) {
            throw ExceptionFactory.createException(Messages.getString("MysqlIO.39") + this.streamingData + Messages.getString("MysqlIO.40") + Messages.getString("MysqlIO.41") + Messages.getString("MysqlIO.42"), this.exceptionInterceptor);
         }

         this.streamingData.getOwner().closeOwner(false);
         this.clearInputStream();
      }

   }

   public void unsetStreamingData(ResultsetRows streamer) {
      if (this.streamingData == null) {
         throw ExceptionFactory.createException(Messages.getString("MysqlIO.17") + streamer + Messages.getString("MysqlIO.18"), this.exceptionInterceptor);
      } else {
         if (streamer == this.streamingData) {
            this.streamingData = null;
         }

      }
   }

   public void scanForAndThrowDataTruncation() {
      if (this.streamingData == null && (Boolean)this.propertySet.getBooleanProperty(PropertyKey.jdbcCompliantTruncation).getValue() && this.getWarningCount() > 0) {
         int warningCountOld = this.getWarningCount();
         this.convertShowWarningsToSQLWarnings(true);
         this.setWarningCount(warningCountOld);
      }

   }

   public StringBuilder generateQueryCommentBlock(StringBuilder buf) {
      buf.append("/* conn id ");
      buf.append(this.getServerSession().getCapabilities().getThreadId());
      buf.append(" clock: ");
      buf.append(System.currentTimeMillis());
      buf.append(" */ ");
      return buf;
   }

   public BaseMetricsHolder getMetricsHolder() {
      return this.metricsHolder;
   }

   public String getQueryComment() {
      return this.queryComment;
   }

   public void setQueryComment(String comment) {
      this.queryComment = comment;
   }

   private void appendDeadlockStatusInformation(Session sess, String xOpen, StringBuilder errorBuf) {
      if ((Boolean)sess.getPropertySet().getBooleanProperty(PropertyKey.includeInnodbStatusInDeadlockExceptions).getValue() && xOpen != null && (xOpen.startsWith("40") || xOpen.startsWith("41")) && this.getStreamingData() == null) {
         try {
            NativePacketPayload resultPacket = this.sendCommand(this.getCommandBuilder().buildComQuery(this.getSharedSendPacket(), "SHOW ENGINE INNODB STATUS"), false, 0);
            Resultset rs = this.readAllResults(-1, false, resultPacket, false, (ColumnDefinition)null, new ResultsetFactory(Resultset.Type.FORWARD_ONLY, (Resultset.Concurrency)null));
            int colIndex = 0;
            Field f = null;

            for(int i = 0; i < rs.getColumnDefinition().getFields().length; ++i) {
               f = rs.getColumnDefinition().getFields()[i];
               if ("Status".equals(f.getName())) {
                  colIndex = i;
                  break;
               }
            }

            ValueFactory<String> vf = new StringValueFactory(this.propertySet);
            Row r;
            if ((r = (Row)rs.getRows().next()) != null) {
               errorBuf.append("\n\n").append((String)r.getValue(colIndex, vf));
            } else {
               errorBuf.append("\n\n").append(Messages.getString("MysqlIO.NoInnoDBStatusFound"));
            }
         } catch (CJException | IOException var13) {
            errorBuf.append("\n\n").append(Messages.getString("MysqlIO.InnoDBStatusFailed")).append("\n\n").append(Util.stackTraceToString(var13));
         }
      }

      if ((Boolean)sess.getPropertySet().getBooleanProperty(PropertyKey.includeThreadDumpInDeadlockExceptions).getValue()) {
         errorBuf.append("\n\n*** Java threads running at time of deadlock ***\n\n");
         ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();
         long[] threadIds = threadMBean.getAllThreadIds();
         ThreadInfo[] threads = threadMBean.getThreadInfo(threadIds, Integer.MAX_VALUE);
         List<ThreadInfo> activeThreads = new ArrayList();
         ThreadInfo[] var19 = threads;
         int var20 = threads.length;

         for(int var10 = 0; var10 < var20; ++var10) {
            ThreadInfo info = var19[var10];
            if (info != null) {
               activeThreads.add(info);
            }
         }

         Iterator var21 = activeThreads.iterator();

         while(var21.hasNext()) {
            ThreadInfo threadInfo = (ThreadInfo)var21.next();
            errorBuf.append('"').append(threadInfo.getThreadName()).append("\" tid=").append(threadInfo.getThreadId()).append(" ").append(threadInfo.getThreadState());
            if (threadInfo.getLockName() != null) {
               errorBuf.append(" on lock=").append(threadInfo.getLockName());
            }

            if (threadInfo.isSuspended()) {
               errorBuf.append(" (suspended)");
            }

            if (threadInfo.isInNative()) {
               errorBuf.append(" (running in native)");
            }

            StackTraceElement[] stackTrace = threadInfo.getStackTrace();
            if (stackTrace.length > 0) {
               errorBuf.append(" in ");
               errorBuf.append(stackTrace[0].getClassName()).append(".");
               errorBuf.append(stackTrace[0].getMethodName()).append("()");
            }

            errorBuf.append("\n");
            if (threadInfo.getLockOwnerName() != null) {
               errorBuf.append("\t owned by ").append(threadInfo.getLockOwnerName()).append(" Id=").append(threadInfo.getLockOwnerId()).append("\n");
            }

            for(int j = 0; j < stackTrace.length; ++j) {
               StackTraceElement ste = stackTrace[j];
               errorBuf.append("\tat ").append(ste.toString()).append("\n");
            }
         }
      }

   }

   private StringBuilder appendResultSetSlashGStyle(StringBuilder appendTo, Resultset rs) {
      Field[] fields = rs.getColumnDefinition().getFields();
      int maxWidth = 0;

      int i;
      for(i = 0; i < fields.length; ++i) {
         if (fields[i].getColumnLabel().length() > maxWidth) {
            maxWidth = fields[i].getColumnLabel().length();
         }
      }

      i = 1;

      Row r;
      while((r = (Row)rs.getRows().next()) != null) {
         appendTo.append("*************************** ");
         appendTo.append(i++);
         appendTo.append(". row ***************************\n");

         for(int i = 0; i < fields.length; ++i) {
            int leftPad = maxWidth - fields[i].getColumnLabel().length();

            for(int j = 0; j < leftPad; ++j) {
               appendTo.append(" ");
            }

            appendTo.append(fields[i].getColumnLabel()).append(": ");
            String stringVal = (String)r.getValue(i, new StringValueFactory(this.propertySet));
            appendTo.append(stringVal != null ? stringVal : "NULL").append("\n");
         }

         appendTo.append("\n");
      }

      return appendTo;
   }

   public SQLWarning convertShowWarningsToSQLWarnings(boolean forTruncationOnly) {
      if (this.warningCount == 0) {
         return null;
      } else {
         SQLWarning currentWarning = null;
         ResultsetRows rows = null;

         try {
            NativePacketPayload resultPacket = this.sendCommand(this.getCommandBuilder().buildComQuery(this.getSharedSendPacket(), "SHOW WARNINGS"), false, 0);
            Resultset warnRs = this.readAllResults(-1, this.warningCount > 99, resultPacket, false, (ColumnDefinition)null, new ResultsetFactory(Resultset.Type.FORWARD_ONLY, Resultset.Concurrency.READ_ONLY));
            int codeFieldIndex = warnRs.getColumnDefinition().findColumn("Code", false, 1) - 1;
            int messageFieldIndex = warnRs.getColumnDefinition().findColumn("Message", false, 1) - 1;
            ValueFactory<String> svf = new StringValueFactory(this.propertySet);
            ValueFactory<Integer> ivf = new IntegerValueFactory(this.propertySet);
            rows = warnRs.getRows();

            while(true) {
               Row r;
               int code;
               label131:
               do {
                  while((r = (Row)rows.next()) != null) {
                     code = (Integer)r.getValue(codeFieldIndex, ivf);
                     if (forTruncationOnly) {
                        continue label131;
                     }

                     String message = (String)r.getValue(messageFieldIndex, svf);
                     SQLWarning newWarning = new SQLWarning(message, MysqlErrorNumbers.mysqlToSqlState(code), code);
                     if (currentWarning == null) {
                        currentWarning = newWarning;
                     } else {
                        ((SQLWarning)currentWarning).setNextWarning(newWarning);
                     }
                  }

                  if (forTruncationOnly && currentWarning != null) {
                     throw ExceptionFactory.createException((String)((SQLWarning)currentWarning).getMessage(), (Throwable)currentWarning);
                  }

                  Object var19 = currentWarning;
                  return (SQLWarning)var19;
               } while(code != 1265 && code != 1264);

               DataTruncation newTruncation = new MysqlDataTruncation((String)r.getValue(messageFieldIndex, svf), 0, false, false, 0, 0, code);
               if (currentWarning == null) {
                  currentWarning = newTruncation;
               } else {
                  ((SQLWarning)currentWarning).setNextWarning(newTruncation);
               }
            }
         } catch (IOException var17) {
            throw ExceptionFactory.createException((String)var17.getMessage(), (Throwable)var17);
         } finally {
            if (rows != null) {
               rows.close();
            }

         }
      }
   }

   public ColumnDefinition readMetadata() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public void close() throws IOException {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public void configureTimeZone() {
      String connectionTimeZone = (String)this.getPropertySet().getStringProperty(PropertyKey.connectionTimeZone).getValue();
      TimeZone selectedTz = null;
      if (connectionTimeZone != null && !StringUtils.isEmptyOrWhitespaceOnly(connectionTimeZone) && !"LOCAL".equals(connectionTimeZone)) {
         if ("SERVER".equals(connectionTimeZone)) {
            return;
         }

         selectedTz = TimeZone.getTimeZone(ZoneId.of(connectionTimeZone));
      } else {
         selectedTz = TimeZone.getDefault();
      }

      this.serverSession.setSessionTimeZone(selectedTz);
      if ((Boolean)this.getPropertySet().getBooleanProperty(PropertyKey.forceConnectionTimeZoneToSession).getValue()) {
         StringBuilder query = new StringBuilder("SET SESSION time_zone='");
         ZoneId zid = selectedTz.toZoneId().normalized();
         if (zid instanceof ZoneOffset) {
            String offsetStr = ((ZoneOffset)zid).getId().replace("Z", "+00:00");
            query.append(offsetStr);
            this.serverSession.getServerVariables().put("time_zone", offsetStr);
         } else {
            query.append(selectedTz.getID());
            this.serverSession.getServerVariables().put("time_zone", selectedTz.getID());
         }

         query.append("'");
         this.sendCommand(this.getCommandBuilder().buildComQuery((NativePacketPayload)null, (String)query.toString()), false, 0);
      }

   }

   public void initServerSession() {
      this.configureTimeZone();
      if (this.serverSession.getServerVariables().containsKey("max_allowed_packet")) {
         int serverMaxAllowedPacket = this.serverSession.getServerVariable("max_allowed_packet", -1);
         if (serverMaxAllowedPacket != -1 && (!this.maxAllowedPacket.isExplicitlySet() || serverMaxAllowedPacket < (Integer)this.maxAllowedPacket.getValue())) {
            this.maxAllowedPacket.setValue(serverMaxAllowedPacket);
         }

         if ((Boolean)this.useServerPrepStmts.getValue()) {
            RuntimeProperty<Integer> blobSendChunkSize = this.propertySet.getProperty(PropertyKey.blobSendChunkSize);
            int preferredBlobSendChunkSize = (Integer)blobSendChunkSize.getValue();
            int packetHeaderSize = 8203;
            int allowedBlobSendChunkSize = Math.min(preferredBlobSendChunkSize, (Integer)this.maxAllowedPacket.getValue()) - packetHeaderSize;
            if (allowedBlobSendChunkSize <= 0) {
               throw ExceptionFactory.createException(Messages.getString("Connection.15", new Object[]{Integer.valueOf(packetHeaderSize)}), "01S00", 0, false, (Throwable)null, this.exceptionInterceptor);
            }

            blobSendChunkSize.setValue(allowedBlobSendChunkSize);
         }
      }

      this.serverSession.getCharsetSettings().configurePostHandshake(false);
   }
}
