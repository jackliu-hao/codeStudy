/*      */ package com.mysql.cj.protocol.a;
/*      */ 
/*      */ import com.mysql.cj.CharsetSettings;
/*      */ import com.mysql.cj.Constants;
/*      */ import com.mysql.cj.MessageBuilder;
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.MysqlType;
/*      */ import com.mysql.cj.NativeCharsetSettings;
/*      */ import com.mysql.cj.NativeSession;
/*      */ import com.mysql.cj.Query;
/*      */ import com.mysql.cj.QueryAttributesBindValue;
/*      */ import com.mysql.cj.QueryAttributesBindings;
/*      */ import com.mysql.cj.ServerVersion;
/*      */ import com.mysql.cj.Session;
/*      */ import com.mysql.cj.TransactionEventHandler;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.conf.PropertySet;
/*      */ import com.mysql.cj.conf.RuntimeProperty;
/*      */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*      */ import com.mysql.cj.exceptions.CJConnectionFeatureNotAvailableException;
/*      */ import com.mysql.cj.exceptions.CJException;
/*      */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*      */ import com.mysql.cj.exceptions.CJPacketTooBigException;
/*      */ import com.mysql.cj.exceptions.ClosedOnExpiredPasswordException;
/*      */ import com.mysql.cj.exceptions.DataTruncationException;
/*      */ import com.mysql.cj.exceptions.ExceptionFactory;
/*      */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*      */ import com.mysql.cj.exceptions.MysqlErrorNumbers;
/*      */ import com.mysql.cj.exceptions.PasswordExpiredException;
/*      */ import com.mysql.cj.exceptions.WrongArgumentException;
/*      */ import com.mysql.cj.interceptors.QueryInterceptor;
/*      */ import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
/*      */ import com.mysql.cj.log.BaseMetricsHolder;
/*      */ import com.mysql.cj.log.Log;
/*      */ import com.mysql.cj.log.ProfilerEventHandler;
/*      */ import com.mysql.cj.protocol.AbstractProtocol;
/*      */ import com.mysql.cj.protocol.ColumnDefinition;
/*      */ import com.mysql.cj.protocol.ExportControlled;
/*      */ import com.mysql.cj.protocol.FullReadInputStream;
/*      */ import com.mysql.cj.protocol.Message;
/*      */ import com.mysql.cj.protocol.MessageReader;
/*      */ import com.mysql.cj.protocol.MessageSender;
/*      */ import com.mysql.cj.protocol.PacketReceivedTimeHolder;
/*      */ import com.mysql.cj.protocol.PacketSentTimeHolder;
/*      */ import com.mysql.cj.protocol.Protocol;
/*      */ import com.mysql.cj.protocol.ProtocolEntity;
/*      */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*      */ import com.mysql.cj.protocol.ProtocolEntityReader;
/*      */ import com.mysql.cj.protocol.ResultBuilder;
/*      */ import com.mysql.cj.protocol.Resultset;
/*      */ import com.mysql.cj.protocol.ResultsetRow;
/*      */ import com.mysql.cj.protocol.ResultsetRows;
/*      */ import com.mysql.cj.protocol.ServerCapabilities;
/*      */ import com.mysql.cj.protocol.ServerSession;
/*      */ import com.mysql.cj.protocol.SocketConnection;
/*      */ import com.mysql.cj.protocol.a.result.OkPacket;
/*      */ import com.mysql.cj.result.Field;
/*      */ import com.mysql.cj.result.IntegerValueFactory;
/*      */ import com.mysql.cj.result.Row;
/*      */ import com.mysql.cj.result.StringValueFactory;
/*      */ import com.mysql.cj.result.ValueFactory;
/*      */ import com.mysql.cj.util.LazyString;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import com.mysql.cj.util.TestUtils;
/*      */ import com.mysql.cj.util.TimeUtil;
/*      */ import com.mysql.cj.util.Util;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.management.ManagementFactory;
/*      */ import java.lang.management.ThreadInfo;
/*      */ import java.lang.management.ThreadMXBean;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.net.InetAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.Socket;
/*      */ import java.net.URL;
/*      */ import java.net.UnknownHostException;
/*      */ import java.nio.file.InvalidPathException;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.Paths;
/*      */ import java.sql.SQLWarning;
/*      */ import java.time.ZoneId;
/*      */ import java.time.ZoneOffset;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Optional;
/*      */ import java.util.TimeZone;
/*      */ import java.util.function.Supplier;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class NativeProtocol
/*      */   extends AbstractProtocol<NativePacketPayload>
/*      */   implements Protocol<NativePacketPayload>, RuntimeProperty.RuntimePropertyListener
/*      */ {
/*      */   protected static final int INITIAL_PACKET_SIZE = 1024;
/*      */   protected static final int COMP_HEADER_LENGTH = 3;
/*      */   protected static final int MAX_QUERY_SIZE_TO_EXPLAIN = 1048576;
/*      */   protected static final int SSL_REQUEST_LENGTH = 32;
/*      */   private static final String EXPLAINABLE_STATEMENT = "SELECT";
/*  144 */   private static final String[] EXPLAINABLE_STATEMENT_EXTENSION = new String[] { "INSERT", "UPDATE", "REPLACE", "DELETE" };
/*      */ 
/*      */   
/*      */   protected MessageSender<NativePacketPayload> packetSender;
/*      */   
/*      */   protected MessageReader<NativePacketHeader, NativePacketPayload> packetReader;
/*      */   
/*      */   protected NativeServerSession serverSession;
/*      */   
/*      */   protected CompressedPacketSender compressedPacketSender;
/*      */   
/*  155 */   protected NativePacketPayload sharedSendPacket = null;
/*      */   
/*  157 */   protected NativePacketPayload reusablePacket = null;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SoftReference<NativePacketPayload> loadFileBufRef;
/*      */ 
/*      */ 
/*      */   
/*  166 */   protected byte packetSequence = 0;
/*      */   
/*      */   protected boolean useCompression = false;
/*      */   
/*      */   private RuntimeProperty<Integer> maxAllowedPacket;
/*      */   
/*      */   private RuntimeProperty<Boolean> useServerPrepStmts;
/*      */   
/*      */   private boolean autoGenerateTestcaseScript;
/*      */   
/*      */   private boolean logSlowQueries = false;
/*      */   
/*      */   private boolean useAutoSlowLog;
/*      */   
/*      */   private boolean profileSQL = false;
/*      */   
/*      */   private long slowQueryThreshold;
/*  183 */   private int commandCount = 0;
/*      */   
/*      */   protected boolean hadWarnings = false;
/*  186 */   private int warningCount = 0;
/*      */   
/*      */   protected Map<Class<? extends ProtocolEntity>, ProtocolEntityReader<? extends ProtocolEntity, ? extends Message>> PROTOCOL_ENTITY_CLASS_TO_TEXT_READER;
/*      */   
/*      */   protected Map<Class<? extends ProtocolEntity>, ProtocolEntityReader<? extends ProtocolEntity, ? extends Message>> PROTOCOL_ENTITY_CLASS_TO_BINARY_READER;
/*  191 */   private int statementExecutionDepth = 0;
/*      */ 
/*      */   
/*      */   private List<QueryInterceptor> queryInterceptors;
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> maintainTimeStats;
/*      */   
/*      */   private RuntimeProperty<Integer> maxQuerySizeToLog;
/*      */   
/*      */   private InputStream localInfileInputStream;
/*      */   
/*      */   private BaseMetricsHolder metricsHolder;
/*      */   
/*  205 */   private String queryComment = null;
/*      */   
/*  207 */   private NativeMessageBuilder commandBuilder = null;
/*      */   private ResultsetRows streamingData;
/*      */   
/*      */   public static NativeProtocol getInstance(Session session, SocketConnection socketConnection, PropertySet propertySet, Log log, TransactionEventHandler transactionManager) {
/*  211 */     NativeProtocol protocol = new NativeProtocol(log);
/*  212 */     protocol.init(session, socketConnection, propertySet, transactionManager);
/*  213 */     return protocol;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void init(Session sess, SocketConnection phConnection, PropertySet propSet, TransactionEventHandler trManager) {
/*  223 */     super.init(sess, phConnection, propSet, trManager);
/*      */     
/*  225 */     this.maintainTimeStats = this.propertySet.getBooleanProperty(PropertyKey.maintainTimeStats);
/*  226 */     this.maxQuerySizeToLog = this.propertySet.getIntegerProperty(PropertyKey.maxQuerySizeToLog);
/*  227 */     this.useAutoSlowLog = ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.autoSlowLog).getValue()).booleanValue();
/*  228 */     this.logSlowQueries = ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.logSlowQueries).getValue()).booleanValue();
/*  229 */     this.maxAllowedPacket = this.propertySet.getIntegerProperty(PropertyKey.maxAllowedPacket);
/*  230 */     this.profileSQL = ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.profileSQL).getValue()).booleanValue();
/*  231 */     this.autoGenerateTestcaseScript = ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.autoGenerateTestcaseScript).getValue()).booleanValue();
/*  232 */     this.useServerPrepStmts = this.propertySet.getBooleanProperty(PropertyKey.useServerPrepStmts);
/*      */     
/*  234 */     this.reusablePacket = new NativePacketPayload(1024);
/*      */ 
/*      */     
/*      */     try {
/*  238 */       this.packetSender = new SimplePacketSender(this.socketConnection.getMysqlOutput());
/*  239 */       this.packetReader = new SimplePacketReader(this.socketConnection, this.maxAllowedPacket);
/*  240 */     } catch (IOException ioEx) {
/*  241 */       throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/*  242 */           getPacketReceivedTimeHolder(), ioEx, getExceptionInterceptor());
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  247 */     if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.logSlowQueries).getValue()).booleanValue()) {
/*  248 */       calculateSlowQueryThreshold();
/*      */     }
/*      */     
/*  251 */     this.authProvider = new NativeAuthenticationProvider();
/*  252 */     this.authProvider.init(this, getPropertySet(), this.socketConnection.getExceptionInterceptor());
/*      */     
/*  254 */     Map<Class<? extends ProtocolEntity>, ProtocolEntityReader<? extends ProtocolEntity, NativePacketPayload>> protocolEntityClassToTextReader = new HashMap<>();
/*  255 */     protocolEntityClassToTextReader.put(ColumnDefinition.class, new ColumnDefinitionReader(this));
/*  256 */     protocolEntityClassToTextReader.put(ResultsetRow.class, new ResultsetRowReader(this));
/*  257 */     protocolEntityClassToTextReader.put(Resultset.class, new TextResultsetReader(this));
/*  258 */     this.PROTOCOL_ENTITY_CLASS_TO_TEXT_READER = Collections.unmodifiableMap(protocolEntityClassToTextReader);
/*      */     
/*  260 */     Map<Class<? extends ProtocolEntity>, ProtocolEntityReader<? extends ProtocolEntity, NativePacketPayload>> protocolEntityClassToBinaryReader = new HashMap<>();
/*  261 */     protocolEntityClassToBinaryReader.put(ColumnDefinition.class, new ColumnDefinitionReader(this));
/*  262 */     protocolEntityClassToBinaryReader.put(Resultset.class, new BinaryResultsetReader(this));
/*  263 */     this.PROTOCOL_ENTITY_CLASS_TO_BINARY_READER = Collections.unmodifiableMap(protocolEntityClassToBinaryReader);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public MessageBuilder<NativePacketPayload> getMessageBuilder() {
/*  269 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */   
/*      */   public MessageSender<NativePacketPayload> getPacketSender() {
/*  273 */     return this.packetSender;
/*      */   }
/*      */   
/*      */   public MessageReader<NativePacketHeader, NativePacketPayload> getPacketReader() {
/*  277 */     return this.packetReader;
/*      */   }
/*      */   
/*      */   private NativeMessageBuilder getCommandBuilder() {
/*  281 */     if (this.commandBuilder != null) {
/*  282 */       return this.commandBuilder;
/*      */     }
/*  284 */     return this.commandBuilder = new NativeMessageBuilder(this.serverSession.supportsQueryAttributes());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void negotiateSSLConnection() {
/*  293 */     if (!ExportControlled.enabled()) {
/*  294 */       throw new CJConnectionFeatureNotAvailableException(getPropertySet(), this.serverSession, getPacketSentTimeHolder(), null);
/*      */     }
/*      */     
/*  297 */     long clientParam = this.serverSession.getClientParam();
/*      */     
/*  299 */     NativePacketPayload packet = new NativePacketPayload(32);
/*  300 */     packet.writeInteger(NativeConstants.IntegerDataType.INT4, clientParam);
/*  301 */     packet.writeInteger(NativeConstants.IntegerDataType.INT4, 16777215L);
/*  302 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, this.serverSession.getCharsetSettings().configurePreHandshake(false));
/*  303 */     packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, new byte[23]);
/*      */     
/*  305 */     send(packet, packet.getPosition());
/*      */     
/*      */     try {
/*  308 */       this.socketConnection.performTlsHandshake(this.serverSession, this.log);
/*      */ 
/*      */       
/*  311 */       this.packetSender = new SimplePacketSender(this.socketConnection.getMysqlOutput());
/*  312 */       this.packetReader = new SimplePacketReader(this.socketConnection, this.maxAllowedPacket);
/*      */     }
/*  314 */     catch (FeatureNotAvailableException nae) {
/*  315 */       throw new CJConnectionFeatureNotAvailableException(getPropertySet(), this.serverSession, getPacketSentTimeHolder(), nae);
/*  316 */     } catch (IOException ioEx) {
/*  317 */       throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/*  318 */           getPacketReceivedTimeHolder(), ioEx, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   
/*      */   public void rejectProtocol(NativePacketPayload msg) {
/*      */     try {
/*  324 */       this.socketConnection.getMysqlSocket().close();
/*  325 */     } catch (Exception exception) {}
/*      */ 
/*      */ 
/*      */     
/*  329 */     int errno = 2000;
/*      */     
/*  331 */     NativePacketPayload buf = msg;
/*  332 */     buf.setPosition(1);
/*  333 */     errno = (int)buf.readInteger(NativeConstants.IntegerDataType.INT2);
/*      */     
/*  335 */     String serverErrorMessage = "";
/*      */     try {
/*  337 */       serverErrorMessage = buf.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
/*  338 */     } catch (Exception exception) {}
/*      */ 
/*      */ 
/*      */     
/*  342 */     StringBuilder errorBuf = new StringBuilder(Messages.getString("Protocol.0"));
/*  343 */     errorBuf.append(serverErrorMessage);
/*  344 */     errorBuf.append("\"");
/*      */     
/*  346 */     String xOpen = MysqlErrorNumbers.mysqlToSqlState(errno);
/*      */     
/*  348 */     throw ExceptionFactory.createException(MysqlErrorNumbers.get(xOpen) + ", " + errorBuf.toString(), xOpen, errno, false, null, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void beforeHandshake() {
/*  354 */     this.packetReader.resetMessageSequence();
/*      */ 
/*      */     
/*  357 */     this.serverSession = new NativeServerSession(this.propertySet);
/*      */     
/*  359 */     this.serverSession.setCharsetSettings((CharsetSettings)new NativeCharsetSettings((NativeSession)this.session));
/*      */ 
/*      */     
/*  362 */     this.serverSession.setCapabilities(readServerCapabilities());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void afterHandshake() {
/*  369 */     checkTransactionState();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  375 */       if ((this.serverSession.getCapabilities().getCapabilityFlags() & 0x20) != 0 && ((Boolean)this.propertySet
/*  376 */         .getBooleanProperty(PropertyKey.useCompression).getValue()).booleanValue() && 
/*  377 */         !(this.socketConnection.getMysqlInput().getUnderlyingStream() instanceof CompressedInputStream)) {
/*  378 */         this.useCompression = true;
/*  379 */         this.socketConnection.setMysqlInput(new FullReadInputStream(new CompressedInputStream((InputStream)this.socketConnection.getMysqlInput(), this.propertySet
/*  380 */                 .getBooleanProperty(PropertyKey.traceProtocol), this.log)));
/*  381 */         this.compressedPacketSender = new CompressedPacketSender(this.socketConnection.getMysqlOutput());
/*  382 */         this.packetSender = this.compressedPacketSender;
/*      */       } 
/*      */       
/*  385 */       applyPacketDecorators(this.packetSender, this.packetReader);
/*      */       
/*  387 */       this.socketConnection.getSocketFactory().afterHandshake();
/*  388 */     } catch (IOException ioEx) {
/*  389 */       throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/*  390 */           getPacketReceivedTimeHolder(), ioEx, getExceptionInterceptor());
/*      */     } 
/*      */ 
/*      */     
/*  394 */     RuntimeProperty<Boolean> useInformationSchema = this.propertySet.getProperty(PropertyKey.useInformationSchema);
/*  395 */     if (versionMeetsMinimum(8, 0, 3) && !((Boolean)useInformationSchema.getValue()).booleanValue() && !useInformationSchema.isExplicitlySet()) {
/*  396 */       useInformationSchema.setValue(Boolean.valueOf(true));
/*      */     }
/*      */ 
/*      */     
/*  400 */     this.maintainTimeStats.addListener(this);
/*  401 */     this.propertySet.getBooleanProperty(PropertyKey.traceProtocol).addListener(this);
/*  402 */     this.propertySet.getBooleanProperty(PropertyKey.enablePacketDebug).addListener(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public void handlePropertyChange(RuntimeProperty<?> prop) {
/*  407 */     switch (prop.getPropertyDefinition().getPropertyKey()) {
/*      */       
/*      */       case maintainTimeStats:
/*      */       case traceProtocol:
/*      */       case enablePacketDebug:
/*  412 */         applyPacketDecorators(this.packetSender.undecorateAll(), this.packetReader.undecorateAll());
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void applyPacketDecorators(MessageSender<NativePacketPayload> sender, MessageReader<NativePacketHeader, NativePacketPayload> messageReader) {
/*  430 */     TimeTrackingPacketSender ttSender = null;
/*  431 */     TimeTrackingPacketReader ttReader = null;
/*  432 */     LinkedList<StringBuilder> debugRingBuffer = null;
/*      */     
/*  434 */     if (((Boolean)this.maintainTimeStats.getValue()).booleanValue()) {
/*  435 */       ttSender = new TimeTrackingPacketSender(sender);
/*  436 */       sender = ttSender;
/*      */       
/*  438 */       ttReader = new TimeTrackingPacketReader(messageReader);
/*  439 */       messageReader = ttReader;
/*      */     } 
/*      */     
/*  442 */     if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.traceProtocol).getValue()).booleanValue()) {
/*  443 */       sender = new TracingPacketSender(sender, this.log, this.socketConnection.getHost(), getServerSession().getCapabilities().getThreadId());
/*  444 */       messageReader = new TracingPacketReader(messageReader, this.log);
/*      */     } 
/*      */     
/*  447 */     if (((Boolean)getPropertySet().getBooleanProperty(PropertyKey.enablePacketDebug).getValue()).booleanValue()) {
/*      */       
/*  449 */       debugRingBuffer = new LinkedList<>();
/*      */       
/*  451 */       sender = new DebugBufferingPacketSender(sender, debugRingBuffer, this.propertySet.getIntegerProperty(PropertyKey.packetDebugBufferSize));
/*      */       
/*  453 */       messageReader = new DebugBufferingPacketReader(messageReader, debugRingBuffer, this.propertySet.getIntegerProperty(PropertyKey.packetDebugBufferSize));
/*      */     } 
/*      */ 
/*      */     
/*  457 */     messageReader = new MultiPacketReader(messageReader);
/*      */ 
/*      */     
/*  460 */     synchronized (this.packetReader) {
/*  461 */       this.packetReader = messageReader;
/*  462 */       this.packetDebugRingBuffer = debugRingBuffer;
/*  463 */       setPacketSentTimeHolder((ttSender != null) ? ttSender : new PacketSentTimeHolder() {  }
/*      */         );
/*      */     } 
/*  466 */     synchronized (this.packetSender) {
/*  467 */       this.packetSender = sender;
/*  468 */       setPacketReceivedTimeHolder((ttReader != null) ? ttReader : new PacketReceivedTimeHolder() {
/*      */           
/*      */           });
/*      */     } 
/*      */   }
/*      */   
/*      */   public NativeCapabilities readServerCapabilities() {
/*  475 */     NativePacketPayload buf = readMessage((NativePacketPayload)null);
/*      */ 
/*      */     
/*  478 */     if (buf.isErrorPacket()) {
/*  479 */       rejectProtocol(buf);
/*      */     }
/*      */     
/*  482 */     return new NativeCapabilities(buf);
/*      */   }
/*      */ 
/*      */   
/*      */   public NativeServerSession getServerSession() {
/*  487 */     return this.serverSession;
/*      */   }
/*      */ 
/*      */   
/*      */   public void changeDatabase(String database) {
/*  492 */     if (database == null || database.length() == 0) {
/*      */       return;
/*      */     }
/*      */     
/*      */     try {
/*  497 */       sendCommand(getCommandBuilder().buildComInitDb(getSharedSendPacket(), database), false, 0);
/*  498 */     } catch (CJException ex) {
/*  499 */       if (((Boolean)getPropertySet().getBooleanProperty(PropertyKey.createDatabaseIfNotExist).getValue()).booleanValue()) {
/*  500 */         sendCommand(getCommandBuilder().buildComQuery(getSharedSendPacket(), "CREATE DATABASE IF NOT EXISTS " + 
/*  501 */               StringUtils.quoteIdentifier(database, true)), false, 0);
/*      */         
/*  503 */         sendCommand(getCommandBuilder().buildComInitDb(getSharedSendPacket(), database), false, 0);
/*      */       } else {
/*  505 */         throw ExceptionFactory.createCommunicationsException(getPropertySet(), this.serverSession, getPacketSentTimeHolder(), 
/*  506 */             getPacketReceivedTimeHolder(), ex, getExceptionInterceptor());
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final NativePacketPayload readMessage(NativePacketPayload reuse) {
/*      */     try {
/*  514 */       NativePacketHeader header = (NativePacketHeader)this.packetReader.readHeader();
/*  515 */       NativePacketPayload buf = (NativePacketPayload)this.packetReader.readMessage(Optional.ofNullable(reuse), header);
/*  516 */       this.packetSequence = header.getMessageSequence();
/*  517 */       return buf;
/*      */     }
/*  519 */     catch (IOException ioEx) {
/*  520 */       throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/*  521 */           getPacketReceivedTimeHolder(), ioEx, getExceptionInterceptor());
/*  522 */     } catch (OutOfMemoryError oom) {
/*  523 */       throw ExceptionFactory.createException(oom.getMessage(), "HY001", 0, false, oom, this.exceptionInterceptor);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final NativePacketPayload probeMessage(NativePacketPayload reuse) {
/*      */     try {
/*  530 */       NativePacketHeader header = (NativePacketHeader)this.packetReader.probeHeader();
/*  531 */       NativePacketPayload buf = (NativePacketPayload)this.packetReader.probeMessage(Optional.ofNullable(reuse), header);
/*  532 */       this.packetSequence = header.getMessageSequence();
/*  533 */       return buf;
/*      */     }
/*  535 */     catch (IOException ioEx) {
/*  536 */       throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/*  537 */           getPacketReceivedTimeHolder(), ioEx, getExceptionInterceptor());
/*  538 */     } catch (OutOfMemoryError oom) {
/*  539 */       throw ExceptionFactory.createException(oom.getMessage(), "HY001", 0, false, oom, this.exceptionInterceptor);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void send(Message packet, int packetLen) {
/*      */     try {
/*  553 */       if (((Integer)this.maxAllowedPacket.getValue()).intValue() > 0 && packetLen > ((Integer)this.maxAllowedPacket.getValue()).intValue()) {
/*  554 */         throw new CJPacketTooBigException(packetLen, ((Integer)this.maxAllowedPacket.getValue()).intValue());
/*      */       }
/*      */       
/*  557 */       this.packetSequence = (byte)(this.packetSequence + 1);
/*  558 */       this.packetSender.send(packet.getByteBuffer(), packetLen, this.packetSequence);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  563 */       if (packet == this.sharedSendPacket) {
/*  564 */         reclaimLargeSharedSendPacket();
/*      */       }
/*  566 */     } catch (IOException ioEx) {
/*  567 */       throw ExceptionFactory.createCommunicationsException(getPropertySet(), this.serverSession, getPacketSentTimeHolder(), 
/*  568 */           getPacketReceivedTimeHolder(), ioEx, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final NativePacketPayload sendCommand(Message queryPacket, boolean skipCheck, int timeoutMillis) {
/*  574 */     int command = queryPacket.getByteBuffer()[0];
/*  575 */     this.commandCount++;
/*      */     
/*  577 */     if (this.queryInterceptors != null) {
/*  578 */       NativePacketPayload interceptedPacketPayload = (NativePacketPayload)invokeQueryInterceptorsPre(queryPacket, false);
/*      */       
/*  580 */       if (interceptedPacketPayload != null) {
/*  581 */         return interceptedPacketPayload;
/*      */       }
/*      */     } 
/*      */     
/*  585 */     this.packetReader.resetMessageSequence();
/*      */     
/*  587 */     int oldTimeout = 0;
/*      */     
/*  589 */     if (timeoutMillis != 0) {
/*      */       try {
/*  591 */         oldTimeout = this.socketConnection.getMysqlSocket().getSoTimeout();
/*  592 */         this.socketConnection.getMysqlSocket().setSoTimeout(timeoutMillis);
/*  593 */       } catch (IOException e) {
/*  594 */         throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/*  595 */             getPacketReceivedTimeHolder(), e, getExceptionInterceptor());
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/*  601 */       checkForOutstandingStreamingData();
/*      */ 
/*      */       
/*  604 */       this.serverSession.setStatusFlags(0, true);
/*  605 */       this.hadWarnings = false;
/*  606 */       setWarningCount(0);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  611 */       if (this.useCompression) {
/*  612 */         int bytesLeft = this.socketConnection.getMysqlInput().available();
/*      */         
/*  614 */         if (bytesLeft > 0) {
/*  615 */           this.socketConnection.getMysqlInput().skip(bytesLeft);
/*      */         }
/*      */       } 
/*      */       
/*      */       try {
/*  620 */         clearInputStream();
/*  621 */         this.packetSequence = -1;
/*  622 */         send(queryPacket, queryPacket.getPosition());
/*      */       }
/*  624 */       catch (CJException ex) {
/*      */         
/*  626 */         throw ex;
/*  627 */       } catch (Exception ex) {
/*  628 */         throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/*  629 */             getPacketReceivedTimeHolder(), ex, getExceptionInterceptor());
/*      */       } 
/*      */       
/*  632 */       NativePacketPayload returnPacket = null;
/*      */       
/*  634 */       if (!skipCheck) {
/*  635 */         if (command == 23 || command == 26) {
/*  636 */           this.packetReader.resetMessageSequence();
/*      */         }
/*      */         
/*  639 */         returnPacket = checkErrorMessage(command);
/*      */         
/*  641 */         if (this.queryInterceptors != null) {
/*  642 */           returnPacket = (NativePacketPayload)invokeQueryInterceptorsPost(queryPacket, returnPacket, false);
/*      */         }
/*      */       } 
/*      */       
/*  646 */       return returnPacket;
/*  647 */     } catch (IOException ioEx) {
/*  648 */       this.serverSession.preserveOldTransactionState();
/*  649 */       throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/*  650 */           getPacketReceivedTimeHolder(), ioEx, getExceptionInterceptor());
/*  651 */     } catch (CJException e) {
/*  652 */       this.serverSession.preserveOldTransactionState();
/*  653 */       throw e;
/*      */     } finally {
/*      */       
/*  656 */       if (timeoutMillis != 0) {
/*      */         try {
/*  658 */           this.socketConnection.getMysqlSocket().setSoTimeout(oldTimeout);
/*  659 */         } catch (IOException e) {
/*  660 */           throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/*  661 */               getPacketReceivedTimeHolder(), e, getExceptionInterceptor());
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void checkTransactionState() {
/*  668 */     int transState = this.serverSession.getTransactionState();
/*  669 */     if (transState == 3) {
/*  670 */       this.transactionManager.transactionCompleted();
/*  671 */     } else if (transState == 2) {
/*  672 */       this.transactionManager.transactionBegun();
/*      */     } 
/*      */   }
/*      */   
/*      */   public NativePacketPayload checkErrorMessage() {
/*  677 */     return checkErrorMessage(-1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private NativePacketPayload checkErrorMessage(int command) {
/*  694 */     NativePacketPayload resultPacket = null;
/*  695 */     this.serverSession.setStatusFlags(0);
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  700 */       resultPacket = readMessage(this.reusablePacket);
/*  701 */     } catch (CJException ex) {
/*      */       
/*  703 */       throw ex;
/*  704 */     } catch (Exception fallThru) {
/*  705 */       throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/*  706 */           getPacketReceivedTimeHolder(), fallThru, getExceptionInterceptor());
/*      */     } 
/*      */     
/*  709 */     checkErrorMessage(resultPacket);
/*      */     
/*  711 */     return resultPacket;
/*      */   }
/*      */ 
/*      */   
/*      */   public void checkErrorMessage(NativePacketPayload resultPacket) {
/*  716 */     resultPacket.setPosition(0);
/*  717 */     byte statusCode = (byte)(int)resultPacket.readInteger(NativeConstants.IntegerDataType.INT1);
/*      */ 
/*      */     
/*  720 */     if (statusCode == -1) {
/*      */       
/*  722 */       int errno = 2000;
/*      */       
/*  724 */       errno = (int)resultPacket.readInteger(NativeConstants.IntegerDataType.INT2);
/*      */       
/*  726 */       String xOpen = null;
/*      */       
/*  728 */       String serverErrorMessage = resultPacket.readString(NativeConstants.StringSelfDataType.STRING_TERM, this.serverSession.getCharsetSettings().getErrorMessageEncoding());
/*      */       
/*  730 */       if (serverErrorMessage.charAt(0) == '#') {
/*      */ 
/*      */         
/*  733 */         if (serverErrorMessage.length() > 6) {
/*  734 */           xOpen = serverErrorMessage.substring(1, 6);
/*  735 */           serverErrorMessage = serverErrorMessage.substring(6);
/*      */           
/*  737 */           if (xOpen.equals("HY000")) {
/*  738 */             xOpen = MysqlErrorNumbers.mysqlToSqlState(errno);
/*      */           }
/*      */         } else {
/*  741 */           xOpen = MysqlErrorNumbers.mysqlToSqlState(errno);
/*      */         } 
/*      */       } else {
/*  744 */         xOpen = MysqlErrorNumbers.mysqlToSqlState(errno);
/*      */       } 
/*      */       
/*  747 */       clearInputStream();
/*      */       
/*  749 */       StringBuilder errorBuf = new StringBuilder();
/*      */       
/*  751 */       String xOpenErrorMessage = MysqlErrorNumbers.get(xOpen);
/*      */       
/*  753 */       boolean useOnlyServerErrorMessages = ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useOnlyServerErrorMessages).getValue()).booleanValue();
/*  754 */       if (!useOnlyServerErrorMessages && 
/*  755 */         xOpenErrorMessage != null) {
/*  756 */         errorBuf.append(xOpenErrorMessage);
/*  757 */         errorBuf.append(Messages.getString("Protocol.0"));
/*      */       } 
/*      */ 
/*      */       
/*  761 */       errorBuf.append(serverErrorMessage);
/*      */       
/*  763 */       if (!useOnlyServerErrorMessages && 
/*  764 */         xOpenErrorMessage != null) {
/*  765 */         errorBuf.append("\"");
/*      */       }
/*      */ 
/*      */       
/*  769 */       appendDeadlockStatusInformation(this.session, xOpen, errorBuf);
/*      */       
/*  771 */       if (xOpen != null) {
/*  772 */         if (xOpen.startsWith("22")) {
/*  773 */           throw new DataTruncationException(errorBuf.toString(), 0, true, false, 0, 0, errno);
/*      */         }
/*      */         
/*  776 */         if (errno == 1820) {
/*  777 */           throw (PasswordExpiredException)ExceptionFactory.createException(PasswordExpiredException.class, errorBuf.toString(), getExceptionInterceptor());
/*      */         }
/*  779 */         if (errno == 1862) {
/*  780 */           throw (ClosedOnExpiredPasswordException)ExceptionFactory.createException(ClosedOnExpiredPasswordException.class, errorBuf.toString(), getExceptionInterceptor());
/*      */         }
/*  782 */         if (errno == 4031) {
/*  783 */           throw (CJCommunicationsException)ExceptionFactory.createException(CJCommunicationsException.class, errorBuf.toString(), null, getExceptionInterceptor());
/*      */         }
/*      */       } 
/*      */       
/*  787 */       throw ExceptionFactory.createException(errorBuf.toString(), xOpen, errno, false, null, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void reclaimLargeSharedSendPacket() {
/*  793 */     if (this.sharedSendPacket != null && this.sharedSendPacket.getCapacity() > 1048576) {
/*  794 */       this.sharedSendPacket = new NativePacketPayload(1024);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearInputStream() {
/*      */     try {
/*      */       int len;
/*  804 */       while ((len = this.socketConnection.getMysqlInput().available()) > 0 && this.socketConnection.getMysqlInput().skip(len) > 0L);
/*      */     
/*      */     }
/*  807 */     catch (IOException ioEx) {
/*  808 */       throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/*  809 */           getPacketReceivedTimeHolder(), ioEx, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void reclaimLargeReusablePacket() {
/*  817 */     if (this.reusablePacket != null && this.reusablePacket.getCapacity() > 1048576) {
/*  818 */       this.reusablePacket = new NativePacketPayload(1024);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final <T extends Resultset> T sendQueryString(Query callingQuery, String query, String characterEncoding, int maxRows, boolean streamResults, ColumnDefinition cachedMetadata, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) throws IOException {
/*  847 */     String statementComment = this.queryComment;
/*      */     
/*  849 */     if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.includeThreadNamesAsStatementComment).getValue()).booleanValue()) {
/*  850 */       statementComment = ((statementComment != null) ? (statementComment + ", ") : "") + "java thread: " + Thread.currentThread().getName();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  855 */     int packLength = 1 + query.length() * 4 + 2;
/*      */     
/*  857 */     byte[] commentAsBytes = null;
/*      */     
/*  859 */     if (statementComment != null) {
/*  860 */       commentAsBytes = StringUtils.getBytes(statementComment, characterEncoding);
/*      */       
/*  862 */       packLength += commentAsBytes.length;
/*  863 */       packLength += 6;
/*      */     } 
/*      */     
/*  866 */     boolean supportsQueryAttributes = this.serverSession.supportsQueryAttributes();
/*  867 */     QueryAttributesBindings queryAttributes = null;
/*      */     
/*  869 */     if (!supportsQueryAttributes && callingQuery != null && callingQuery.getQueryAttributesBindings().getCount() > 0) {
/*  870 */       this.log.logWarn(Messages.getString("QueryAttributes.SetButNotSupported"));
/*      */     }
/*      */     
/*  873 */     if (supportsQueryAttributes) {
/*  874 */       if (callingQuery != null) {
/*  875 */         queryAttributes = callingQuery.getQueryAttributesBindings();
/*      */       }
/*      */       
/*  878 */       if (queryAttributes != null && queryAttributes.getCount() > 0) {
/*  879 */         packLength += 10;
/*  880 */         packLength += (queryAttributes.getCount() + 7) / 8 + 1;
/*  881 */         for (int i = 0; i < queryAttributes.getCount(); i++) {
/*  882 */           QueryAttributesBindValue queryAttribute = queryAttributes.getAttributeValue(i);
/*  883 */           packLength = (int)(packLength + (2 + queryAttribute.getName().length()) + queryAttribute.getBoundLength());
/*      */         } 
/*      */       } else {
/*  886 */         packLength += 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  891 */     NativePacketPayload sendPacket = new NativePacketPayload(packLength);
/*      */     
/*  893 */     sendPacket.setPosition(0);
/*  894 */     sendPacket.writeInteger(NativeConstants.IntegerDataType.INT1, 3L);
/*      */     
/*  896 */     if (supportsQueryAttributes) {
/*  897 */       if (queryAttributes != null && queryAttributes.getCount() > 0) {
/*  898 */         sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, queryAttributes.getCount());
/*  899 */         sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 1L);
/*  900 */         byte[] nullBitsBuffer = new byte[(queryAttributes.getCount() + 7) / 8];
/*  901 */         for (int i = 0; i < queryAttributes.getCount(); i++) {
/*  902 */           if (queryAttributes.getAttributeValue(i).isNull()) {
/*  903 */             nullBitsBuffer[i >>> 3] = (byte)(nullBitsBuffer[i >>> 3] | 1 << (i & 0x7));
/*      */           }
/*      */         } 
/*  906 */         sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_VAR, nullBitsBuffer);
/*  907 */         sendPacket.writeInteger(NativeConstants.IntegerDataType.INT1, 1L);
/*  908 */         queryAttributes.runThroughAll(a -> {
/*      */               sendPacket.writeInteger(NativeConstants.IntegerDataType.INT2, a.getType());
/*      */               sendPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, a.getName().getBytes());
/*      */             });
/*  912 */         ValueEncoder valueEncoder = new ValueEncoder(sendPacket, characterEncoding, this.serverSession.getDefaultTimeZone());
/*  913 */         queryAttributes.runThroughAll(a -> valueEncoder.encodeValue(a.getValue(), a.getType()));
/*      */       } else {
/*  915 */         sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 0L);
/*  916 */         sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 1L);
/*      */       } 
/*      */     }
/*  919 */     sendPacket.setTag("QUERY");
/*      */     
/*  921 */     if (commentAsBytes != null) {
/*  922 */       sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, Constants.SLASH_STAR_SPACE_AS_BYTES);
/*  923 */       sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, commentAsBytes);
/*  924 */       sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
/*      */     } 
/*      */     
/*  927 */     if (!this.session.getServerSession().getCharsetSettings().doesPlatformDbCharsetMatches() && StringUtils.startsWithIgnoreCaseAndWs(query, "LOAD DATA")) {
/*  928 */       sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, StringUtils.getBytes(query));
/*      */     } else {
/*  930 */       sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, StringUtils.getBytes(query, characterEncoding));
/*      */     } 
/*      */     
/*  933 */     return sendQueryPacket(callingQuery, sendPacket, maxRows, streamResults, cachedMetadata, resultSetFactory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final <T extends Resultset> T sendQueryPacket(Query callingQuery, NativePacketPayload queryPacket, int maxRows, boolean streamResults, ColumnDefinition cachedMetadata, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) throws IOException {
/*  960 */     long queryStartTime = getCurrentTimeNanosOrMillis();
/*      */     
/*  962 */     this.statementExecutionDepth++;
/*      */     
/*  964 */     byte[] queryBuf = queryPacket.getByteBuffer();
/*  965 */     int oldPacketPosition = queryPacket.getPosition();
/*  966 */     int queryPosition = queryPacket.getTag("QUERY");
/*  967 */     LazyString query = new LazyString(queryBuf, queryPosition, oldPacketPosition - queryPosition);
/*      */ 
/*      */     
/*      */     try {
/*  971 */       if (this.queryInterceptors != null) {
/*  972 */         T interceptedResults = invokeQueryInterceptorsPre((Supplier<String>)query, callingQuery, false);
/*  973 */         if (interceptedResults != null) {
/*  974 */           return interceptedResults;
/*      */         }
/*      */       } 
/*      */       
/*  978 */       if (this.autoGenerateTestcaseScript) {
/*  979 */         StringBuilder debugBuf = new StringBuilder(query.length() + 32);
/*  980 */         generateQueryCommentBlock(debugBuf);
/*  981 */         debugBuf.append(query);
/*  982 */         debugBuf.append(';');
/*  983 */         TestUtils.dumpTestcaseQuery(debugBuf.toString());
/*      */       } 
/*      */ 
/*      */       
/*  987 */       NativePacketPayload resultPacket = sendCommand(queryPacket, false, 0);
/*      */       
/*  989 */       long queryEndTime = getCurrentTimeNanosOrMillis();
/*  990 */       long queryDuration = queryEndTime - queryStartTime;
/*  991 */       if (callingQuery != null) {
/*  992 */         callingQuery.setExecuteTime(queryDuration);
/*      */       }
/*      */ 
/*      */       
/*  996 */       boolean queryWasSlow = (this.logSlowQueries && (this.useAutoSlowLog ? this.metricsHolder.checkAbonormallyLongQuery(queryDuration) : (queryDuration > ((Integer)this.propertySet.getIntegerProperty(PropertyKey.slowQueryThresholdMillis).getValue()).intValue())));
/*      */       
/*  998 */       long fetchBeginTime = this.profileSQL ? getCurrentTimeNanosOrMillis() : 0L;
/*      */       
/* 1000 */       T rs = readAllResults(maxRows, streamResults, resultPacket, false, cachedMetadata, resultSetFactory);
/*      */       
/* 1002 */       if (this.profileSQL || queryWasSlow) {
/* 1003 */         long fetchEndTime = this.profileSQL ? getCurrentTimeNanosOrMillis() : 0L;
/*      */ 
/*      */         
/* 1006 */         boolean truncated = (oldPacketPosition - queryPosition > ((Integer)this.maxQuerySizeToLog.getValue()).intValue());
/* 1007 */         int extractPosition = truncated ? (((Integer)this.maxQuerySizeToLog.getValue()).intValue() + queryPosition) : oldPacketPosition;
/* 1008 */         String extractedQuery = StringUtils.toString(queryBuf, queryPosition, extractPosition - queryPosition);
/* 1009 */         if (truncated) {
/* 1010 */           extractedQuery = extractedQuery + Messages.getString("Protocol.2");
/*      */         }
/*      */         
/* 1013 */         ProfilerEventHandler eventSink = this.session.getProfilerEventHandler();
/*      */         
/* 1015 */         if (this.logSlowQueries) {
/* 1016 */           if (queryWasSlow) {
/* 1017 */             eventSink.processEvent((byte)6, this.session, callingQuery, (Resultset)rs, queryDuration, new Throwable(), 
/* 1018 */                 Messages.getString("Protocol.SlowQuery", new Object[] {
/* 1019 */                     this.useAutoSlowLog ? " 95% of all queries " : String.valueOf(this.slowQueryThreshold), this.queryTimingUnits, 
/* 1020 */                     Long.valueOf(queryDuration), extractedQuery
/*      */                   }));
/* 1022 */             if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.explainSlowQueries).getValue()).booleanValue()) {
/* 1023 */               if (oldPacketPosition - queryPosition < 1048576) {
/* 1024 */                 queryPacket.setPosition(queryPosition);
/* 1025 */                 explainSlowQuery(query.toString(), extractedQuery);
/*      */               } else {
/* 1027 */                 this.log.logWarn(Messages.getString("Protocol.3", new Object[] { Integer.valueOf(1048576) }));
/*      */               } 
/*      */             }
/*      */           } 
/*      */           
/* 1032 */           if (this.serverSession.noGoodIndexUsed()) {
/* 1033 */             eventSink.processEvent((byte)6, this.session, callingQuery, (Resultset)rs, queryDuration, new Throwable(), 
/* 1034 */                 Messages.getString("Protocol.4") + extractedQuery);
/*      */           }
/* 1036 */           if (this.serverSession.noIndexUsed()) {
/* 1037 */             eventSink.processEvent((byte)6, this.session, callingQuery, (Resultset)rs, queryDuration, new Throwable(), 
/* 1038 */                 Messages.getString("Protocol.5") + extractedQuery);
/*      */           }
/* 1040 */           if (this.serverSession.queryWasSlow()) {
/* 1041 */             eventSink.processEvent((byte)6, this.session, callingQuery, (Resultset)rs, queryDuration, new Throwable(), 
/* 1042 */                 Messages.getString("Protocol.ServerSlowQuery") + extractedQuery);
/*      */           }
/*      */         } 
/*      */         
/* 1046 */         if (this.profileSQL) {
/* 1047 */           eventSink.processEvent((byte)3, this.session, callingQuery, (Resultset)rs, queryDuration, new Throwable(), extractedQuery);
/* 1048 */           eventSink.processEvent((byte)5, this.session, callingQuery, (Resultset)rs, fetchEndTime - fetchBeginTime, new Throwable(), null);
/*      */         } 
/*      */       } 
/*      */       
/* 1052 */       if (this.hadWarnings) {
/* 1053 */         scanForAndThrowDataTruncation();
/*      */       }
/*      */       
/* 1056 */       if (this.queryInterceptors != null) {
/* 1057 */         rs = invokeQueryInterceptorsPost((Supplier<String>)query, callingQuery, rs, false);
/*      */       }
/*      */       
/* 1060 */       return rs;
/*      */     }
/* 1062 */     catch (CJException sqlEx) {
/* 1063 */       if (this.queryInterceptors != null)
/*      */       {
/* 1065 */         invokeQueryInterceptorsPost((Supplier<String>)query, callingQuery, (Resultset)null, false);
/*      */       }
/*      */       
/* 1068 */       if (callingQuery != null) {
/* 1069 */         callingQuery.checkCancelTimeout();
/*      */       }
/*      */       
/* 1072 */       throw sqlEx;
/*      */     } finally {
/*      */       
/* 1075 */       this.statementExecutionDepth--;
/*      */     } 
/*      */   }
/*      */   public <T extends Resultset> T invokeQueryInterceptorsPre(Supplier<String> sql, Query interceptedQuery, boolean forceExecute) {
/*      */     Resultset resultset;
/* 1080 */     T previousResultSet = null;
/*      */     
/* 1082 */     for (int i = 0, s = this.queryInterceptors.size(); i < s; i++) {
/* 1083 */       QueryInterceptor interceptor = this.queryInterceptors.get(i);
/*      */       
/* 1085 */       boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
/* 1086 */       boolean shouldExecute = ((executeTopLevelOnly && (this.statementExecutionDepth == 1 || forceExecute)) || !executeTopLevelOnly);
/*      */       
/* 1088 */       if (shouldExecute) {
/* 1089 */         Resultset resultset1 = interceptor.preProcess(sql, interceptedQuery);
/*      */         
/* 1091 */         if (resultset1 != null) {
/* 1092 */           resultset = resultset1;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1097 */     return (T)resultset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <M extends Message> M invokeQueryInterceptorsPre(M queryPacket, boolean forceExecute) {
/*      */     Message message;
/* 1111 */     M previousPacketPayload = null;
/*      */     
/* 1113 */     for (int i = 0, s = this.queryInterceptors.size(); i < s; i++) {
/* 1114 */       QueryInterceptor interceptor = this.queryInterceptors.get(i);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1121 */       Message message1 = interceptor.preProcess((Message)queryPacket);
/* 1122 */       if (message1 != null) {
/* 1123 */         message = message1;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1128 */     return (M)message;
/*      */   }
/*      */   
/*      */   public <T extends Resultset> T invokeQueryInterceptorsPost(Supplier<String> sql, Query interceptedQuery, T originalResultSet, boolean forceExecute) {
/*      */     Resultset resultset;
/* 1133 */     for (int i = 0, s = this.queryInterceptors.size(); i < s; i++) {
/* 1134 */       QueryInterceptor interceptor = this.queryInterceptors.get(i);
/*      */       
/* 1136 */       boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
/* 1137 */       boolean shouldExecute = ((executeTopLevelOnly && (this.statementExecutionDepth == 1 || forceExecute)) || !executeTopLevelOnly);
/*      */       
/* 1139 */       if (shouldExecute) {
/* 1140 */         Resultset resultset1 = interceptor.postProcess(sql, interceptedQuery, (Resultset)originalResultSet, this.serverSession);
/*      */         
/* 1142 */         if (resultset1 != null) {
/* 1143 */           resultset = resultset1;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1148 */     return (T)resultset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <M extends Message> M invokeQueryInterceptorsPost(M queryPacket, M originalResponsePacket, boolean forceExecute) {
/*      */     Message message;
/* 1165 */     for (int i = 0, s = this.queryInterceptors.size(); i < s; i++) {
/* 1166 */       QueryInterceptor interceptor = this.queryInterceptors.get(i);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1173 */       Message message1 = interceptor.postProcess((Message)queryPacket, (Message)originalResponsePacket);
/* 1174 */       if (message1 != null) {
/* 1175 */         message = message1;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1180 */     return (M)message;
/*      */   }
/*      */   
/*      */   public long getCurrentTimeNanosOrMillis() {
/* 1184 */     return this.useNanosForElapsedTime ? TimeUtil.getCurrentTimeNanosOrMillis() : System.currentTimeMillis();
/*      */   }
/*      */   
/*      */   public boolean hadWarnings() {
/* 1188 */     return this.hadWarnings;
/*      */   }
/*      */   
/*      */   public void setHadWarnings(boolean hadWarnings) {
/* 1192 */     this.hadWarnings = hadWarnings;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void explainSlowQuery(String query, String truncatedQuery) {
/* 1205 */     if (StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, "SELECT") || (
/* 1206 */       versionMeetsMinimum(5, 6, 3) && StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, EXPLAINABLE_STATEMENT_EXTENSION) != -1)) {
/*      */       
/*      */       try {
/* 1209 */         NativePacketPayload resultPacket = sendCommand(getCommandBuilder().buildComQuery(getSharedSendPacket(), "EXPLAIN " + query), false, 0);
/*      */         
/* 1211 */         Resultset rs = readAllResults(-1, false, resultPacket, false, (ColumnDefinition)null, new ResultsetFactory(Resultset.Type.FORWARD_ONLY, null));
/*      */         
/* 1213 */         StringBuilder explainResults = new StringBuilder(Messages.getString("Protocol.6"));
/* 1214 */         explainResults.append(truncatedQuery);
/* 1215 */         explainResults.append(Messages.getString("Protocol.7"));
/*      */         
/* 1217 */         appendResultSetSlashGStyle(explainResults, rs);
/*      */         
/* 1219 */         this.log.logWarn(explainResults.toString());
/* 1220 */       } catch (CJException sqlEx) {
/* 1221 */         throw sqlEx;
/*      */       }
/* 1223 */       catch (Exception ex) {
/* 1224 */         throw ExceptionFactory.createException(ex.getMessage(), ex, getExceptionInterceptor());
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void skipPacket() {
/*      */     try {
/* 1239 */       int packetLength = ((NativePacketHeader)this.packetReader.readHeader()).getMessageSize();
/*      */       
/* 1241 */       this.socketConnection.getMysqlInput().skipFully(packetLength);
/*      */     }
/* 1243 */     catch (IOException ioEx) {
/* 1244 */       throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/* 1245 */           getPacketReceivedTimeHolder(), ioEx, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void quit() {
/*      */     try {
/*      */       try {
/* 1256 */         if (!ExportControlled.isSSLEstablished(this.socketConnection.getMysqlSocket()) && 
/* 1257 */           !this.socketConnection.getMysqlSocket().isClosed()) {
/*      */           
/*      */           try {
/* 1260 */             this.socketConnection.getMysqlSocket().shutdownInput();
/* 1261 */           } catch (UnsupportedOperationException unsupportedOperationException) {}
/*      */         
/*      */         }
/*      */       
/*      */       }
/* 1266 */       catch (IOException iOException) {}
/*      */ 
/*      */ 
/*      */       
/* 1270 */       this.packetSequence = -1;
/* 1271 */       NativePacketPayload packet = new NativePacketPayload(1);
/* 1272 */       send(getCommandBuilder().buildComQuit(packet), packet.getPosition());
/*      */     } finally {
/* 1274 */       this.socketConnection.forceClose();
/* 1275 */       this.localInfileInputStream = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NativePacketPayload getSharedSendPacket() {
/* 1286 */     if (this.sharedSendPacket == null) {
/* 1287 */       this.sharedSendPacket = new NativePacketPayload(1024);
/*      */     }
/* 1289 */     this.sharedSendPacket.setPosition(0);
/*      */     
/* 1291 */     return this.sharedSendPacket;
/*      */   }
/*      */   
/*      */   private void calculateSlowQueryThreshold() {
/* 1295 */     this.slowQueryThreshold = ((Integer)this.propertySet.getIntegerProperty(PropertyKey.slowQueryThresholdMillis).getValue()).intValue();
/*      */     
/* 1297 */     if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useNanosForElapsedTime).getValue()).booleanValue()) {
/* 1298 */       long nanosThreshold = ((Long)this.propertySet.getLongProperty(PropertyKey.slowQueryThresholdNanos).getValue()).longValue();
/*      */       
/* 1300 */       if (nanosThreshold != 0L) {
/* 1301 */         this.slowQueryThreshold = nanosThreshold;
/*      */       } else {
/* 1303 */         this.slowQueryThreshold *= 1000000L;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void changeUser(String user, String password, String database) {
/* 1320 */     this.packetSequence = -1;
/* 1321 */     this.packetSender = this.packetSender.undecorateAll();
/* 1322 */     this.packetReader = this.packetReader.undecorateAll();
/*      */     
/* 1324 */     this.authProvider.changeUser(user, password, database);
/*      */   }
/*      */   
/*      */   protected boolean useNanosForElapsedTime() {
/* 1328 */     return this.useNanosForElapsedTime;
/*      */   }
/*      */   
/*      */   public long getSlowQueryThreshold() {
/* 1332 */     return this.slowQueryThreshold;
/*      */   }
/*      */   
/*      */   public int getCommandCount() {
/* 1336 */     return this.commandCount;
/*      */   }
/*      */   
/*      */   public void setQueryInterceptors(List<QueryInterceptor> queryInterceptors) {
/* 1340 */     this.queryInterceptors = queryInterceptors.isEmpty() ? null : queryInterceptors;
/*      */   }
/*      */   
/*      */   public List<QueryInterceptor> getQueryInterceptors() {
/* 1344 */     return this.queryInterceptors;
/*      */   }
/*      */   
/*      */   public void setSocketTimeout(int milliseconds) {
/*      */     try {
/* 1349 */       Socket soc = this.socketConnection.getMysqlSocket();
/* 1350 */       if (soc != null) {
/* 1351 */         soc.setSoTimeout(milliseconds);
/*      */       }
/* 1353 */     } catch (IOException e) {
/* 1354 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.8"), e, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   
/*      */   public void releaseResources() {
/* 1359 */     if (this.compressedPacketSender != null) {
/* 1360 */       this.compressedPacketSender.stop();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void connect(String user, String password, String database) {
/* 1367 */     beforeHandshake();
/*      */     
/* 1369 */     this.authProvider.connect(user, password, database);
/*      */   }
/*      */   
/*      */   protected boolean isDataAvailable() {
/*      */     try {
/* 1374 */       return (this.socketConnection.getMysqlInput().available() > 0);
/* 1375 */     } catch (IOException ioEx) {
/* 1376 */       throw ExceptionFactory.createCommunicationsException(this.propertySet, this.serverSession, getPacketSentTimeHolder(), 
/* 1377 */           getPacketReceivedTimeHolder(), ioEx, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */   
/*      */   public NativePacketPayload getReusablePacket() {
/* 1382 */     return this.reusablePacket;
/*      */   }
/*      */   
/*      */   public int getWarningCount() {
/* 1386 */     return this.warningCount;
/*      */   }
/*      */   
/*      */   public void setWarningCount(int warningCount) {
/* 1390 */     this.warningCount = warningCount;
/*      */   }
/*      */ 
/*      */   
/*      */   public void dumpPacketRingBuffer() {
/* 1395 */     LinkedList<StringBuilder> localPacketDebugRingBuffer = this.packetDebugRingBuffer;
/* 1396 */     if (localPacketDebugRingBuffer != null) {
/* 1397 */       StringBuilder dumpBuffer = new StringBuilder();
/*      */       
/* 1399 */       dumpBuffer.append("Last " + localPacketDebugRingBuffer.size() + " packets received from server, from oldest->newest:\n");
/* 1400 */       dumpBuffer.append("\n");
/*      */       
/* 1402 */       for (Iterator<StringBuilder> ringBufIter = localPacketDebugRingBuffer.iterator(); ringBufIter.hasNext(); ) {
/* 1403 */         dumpBuffer.append(ringBufIter.next());
/* 1404 */         dumpBuffer.append("\n");
/*      */       } 
/*      */       
/* 1407 */       this.log.logTrace(dumpBuffer.toString());
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor) {
/* 1412 */     return this.serverSession.getServerVersion().meetsMinimum(new ServerVersion(major, minor, subminor));
/*      */   }
/*      */ 
/*      */   
/*      */   public static MysqlType findMysqlType(PropertySet propertySet, int mysqlTypeId, short colFlag, long length, LazyString tableName, LazyString originalTableName, int collationIndex, String encoding) {
/*      */     int newMysqlTypeId;
/* 1418 */     boolean isUnsigned = ((colFlag & 0x20) > 0);
/* 1419 */     boolean isFromFunction = (originalTableName.length() == 0);
/* 1420 */     boolean isBinary = ((colFlag & 0x80) > 0);
/*      */ 
/*      */ 
/*      */     
/* 1424 */     boolean isImplicitTemporaryTable = (tableName.length() > 0 && tableName.toString().startsWith("#sql_"));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1429 */     boolean isOpaqueBinary = (isBinary && collationIndex == 63 && (mysqlTypeId == 254 || mysqlTypeId == 253 || mysqlTypeId == 15)) ? (!isImplicitTemporaryTable) : "binary".equalsIgnoreCase(encoding);
/*      */     
/* 1431 */     switch (mysqlTypeId) {
/*      */       case 0:
/*      */       case 246:
/* 1434 */         return isUnsigned ? MysqlType.DECIMAL_UNSIGNED : MysqlType.DECIMAL;
/*      */ 
/*      */       
/*      */       case 1:
/* 1438 */         if (!isUnsigned && length == 1L && ((Boolean)propertySet.getBooleanProperty(PropertyKey.tinyInt1isBit).getValue()).booleanValue()) {
/* 1439 */           if (((Boolean)propertySet.getBooleanProperty(PropertyKey.transformedBitIsBoolean).getValue()).booleanValue()) {
/* 1440 */             return MysqlType.BOOLEAN;
/*      */           }
/* 1442 */           return MysqlType.BIT;
/*      */         } 
/* 1444 */         return isUnsigned ? MysqlType.TINYINT_UNSIGNED : MysqlType.TINYINT;
/*      */       
/*      */       case 2:
/* 1447 */         return isUnsigned ? MysqlType.SMALLINT_UNSIGNED : MysqlType.SMALLINT;
/*      */       
/*      */       case 3:
/* 1450 */         return isUnsigned ? MysqlType.INT_UNSIGNED : MysqlType.INT;
/*      */       
/*      */       case 4:
/* 1453 */         return isUnsigned ? MysqlType.FLOAT_UNSIGNED : MysqlType.FLOAT;
/*      */       
/*      */       case 5:
/* 1456 */         return isUnsigned ? MysqlType.DOUBLE_UNSIGNED : MysqlType.DOUBLE;
/*      */       
/*      */       case 6:
/* 1459 */         return MysqlType.NULL;
/*      */       
/*      */       case 7:
/* 1462 */         return MysqlType.TIMESTAMP;
/*      */       
/*      */       case 8:
/* 1465 */         return isUnsigned ? MysqlType.BIGINT_UNSIGNED : MysqlType.BIGINT;
/*      */       
/*      */       case 9:
/* 1468 */         return isUnsigned ? MysqlType.MEDIUMINT_UNSIGNED : MysqlType.MEDIUMINT;
/*      */       
/*      */       case 10:
/* 1471 */         return MysqlType.DATE;
/*      */       
/*      */       case 11:
/* 1474 */         return MysqlType.TIME;
/*      */       
/*      */       case 12:
/* 1477 */         return MysqlType.DATETIME;
/*      */       
/*      */       case 13:
/* 1480 */         return MysqlType.YEAR;
/*      */ 
/*      */       
/*      */       case 15:
/*      */       case 253:
/* 1485 */         if (isOpaqueBinary && (!isFromFunction || !((Boolean)propertySet.getBooleanProperty(PropertyKey.functionsNeverReturnBlobs).getValue()).booleanValue())) {
/* 1486 */           return MysqlType.VARBINARY;
/*      */         }
/*      */         
/* 1489 */         return MysqlType.VARCHAR;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 16:
/* 1498 */         return MysqlType.BIT;
/*      */       
/*      */       case 245:
/* 1501 */         return MysqlType.JSON;
/*      */       
/*      */       case 247:
/* 1504 */         return MysqlType.ENUM;
/*      */       
/*      */       case 248:
/* 1507 */         return MysqlType.SET;
/*      */       
/*      */       case 249:
/* 1510 */         if (!isBinary || collationIndex != 63 || ((Boolean)propertySet
/* 1511 */           .getBooleanProperty(PropertyKey.blobsAreStrings).getValue()).booleanValue() || (isFromFunction && ((Boolean)propertySet
/* 1512 */           .getBooleanProperty(PropertyKey.functionsNeverReturnBlobs).getValue()).booleanValue())) {
/* 1513 */           return MysqlType.TINYTEXT;
/*      */         }
/* 1515 */         return MysqlType.TINYBLOB;
/*      */       
/*      */       case 250:
/* 1518 */         if (!isBinary || collationIndex != 63 || ((Boolean)propertySet
/* 1519 */           .getBooleanProperty(PropertyKey.blobsAreStrings).getValue()).booleanValue() || (isFromFunction && ((Boolean)propertySet
/* 1520 */           .getBooleanProperty(PropertyKey.functionsNeverReturnBlobs).getValue()).booleanValue())) {
/* 1521 */           return MysqlType.MEDIUMTEXT;
/*      */         }
/* 1523 */         return MysqlType.MEDIUMBLOB;
/*      */       
/*      */       case 251:
/* 1526 */         if (!isBinary || collationIndex != 63 || ((Boolean)propertySet
/* 1527 */           .getBooleanProperty(PropertyKey.blobsAreStrings).getValue()).booleanValue() || (isFromFunction && ((Boolean)propertySet
/* 1528 */           .getBooleanProperty(PropertyKey.functionsNeverReturnBlobs).getValue()).booleanValue())) {
/* 1529 */           return MysqlType.LONGTEXT;
/*      */         }
/* 1531 */         return MysqlType.LONGBLOB;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 252:
/* 1537 */         newMysqlTypeId = mysqlTypeId;
/*      */ 
/*      */         
/* 1540 */         if (length <= MysqlType.TINYBLOB.getPrecision().longValue()) {
/* 1541 */           newMysqlTypeId = 249;
/*      */         } else {
/* 1543 */           if (length <= MysqlType.BLOB.getPrecision().longValue()) {
/* 1544 */             if (!isBinary || collationIndex != 63 || ((Boolean)propertySet
/* 1545 */               .getBooleanProperty(PropertyKey.blobsAreStrings).getValue()).booleanValue() || (isFromFunction && ((Boolean)propertySet
/* 1546 */               .getBooleanProperty(PropertyKey.functionsNeverReturnBlobs).getValue()).booleanValue())) {
/* 1547 */               newMysqlTypeId = 15;
/* 1548 */               return MysqlType.TEXT;
/*      */             } 
/* 1550 */             return MysqlType.BLOB;
/*      */           } 
/* 1552 */           if (length <= MysqlType.MEDIUMBLOB.getPrecision().longValue()) {
/* 1553 */             newMysqlTypeId = 250;
/*      */           } else {
/* 1555 */             newMysqlTypeId = 251;
/*      */           } 
/*      */         } 
/*      */         
/* 1559 */         return findMysqlType(propertySet, newMysqlTypeId, colFlag, length, tableName, originalTableName, collationIndex, encoding);
/*      */       
/*      */       case 254:
/* 1562 */         if (isOpaqueBinary && !((Boolean)propertySet.getBooleanProperty(PropertyKey.blobsAreStrings).getValue()).booleanValue()) {
/* 1563 */           return MysqlType.BINARY;
/*      */         }
/* 1565 */         return MysqlType.CHAR;
/*      */       
/*      */       case 255:
/* 1568 */         return MysqlType.GEOMETRY;
/*      */     } 
/*      */     
/* 1571 */     return MysqlType.UNKNOWN;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T extends ProtocolEntity> T read(Class<T> requiredClass, ProtocolEntityFactory<T, NativePacketPayload> protocolEntityFactory) throws IOException {
/* 1583 */     ProtocolEntityReader<T, NativePacketPayload> sr = (ProtocolEntityReader<T, NativePacketPayload>)this.PROTOCOL_ENTITY_CLASS_TO_TEXT_READER.get(requiredClass);
/* 1584 */     if (sr == null) {
/* 1585 */       throw (FeatureNotAvailableException)ExceptionFactory.createException(FeatureNotAvailableException.class, "ProtocolEntityReader isn't available for class " + requiredClass);
/*      */     }
/* 1587 */     return (T)sr.read(protocolEntityFactory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T extends ProtocolEntity> T read(Class<Resultset> requiredClass, int maxRows, boolean streamResults, NativePacketPayload resultPacket, boolean isBinaryEncoded, ColumnDefinition metadata, ProtocolEntityFactory<T, NativePacketPayload> protocolEntityFactory) throws IOException {
/* 1596 */     ProtocolEntityReader<T, NativePacketPayload> sr = isBinaryEncoded ? (ProtocolEntityReader<T, NativePacketPayload>)this.PROTOCOL_ENTITY_CLASS_TO_BINARY_READER.get(requiredClass) : (ProtocolEntityReader<T, NativePacketPayload>)this.PROTOCOL_ENTITY_CLASS_TO_TEXT_READER.get(requiredClass);
/* 1597 */     if (sr == null) {
/* 1598 */       throw (FeatureNotAvailableException)ExceptionFactory.createException(FeatureNotAvailableException.class, "ProtocolEntityReader isn't available for class " + requiredClass);
/*      */     }
/* 1600 */     return (T)sr.read(maxRows, streamResults, resultPacket, metadata, protocolEntityFactory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T extends ProtocolEntity> T readNextResultset(T currentProtocolEntity, int maxRows, boolean streamResults, boolean isBinaryEncoded, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) throws IOException {
/* 1625 */     T result = null;
/* 1626 */     if (Resultset.class.isAssignableFrom(currentProtocolEntity.getClass()) && this.serverSession.useMultiResults() && 
/* 1627 */       this.serverSession.hasMoreResults()) {
/*      */       
/* 1629 */       T currentResultSet = currentProtocolEntity;
/*      */       
/*      */       do {
/* 1632 */         NativePacketPayload fieldPacket = checkErrorMessage();
/* 1633 */         fieldPacket.setPosition(0);
/* 1634 */         T newResultSet = read(Resultset.class, maxRows, streamResults, fieldPacket, isBinaryEncoded, (ColumnDefinition)null, resultSetFactory);
/* 1635 */         ((Resultset)currentResultSet).setNextResultset((Resultset)newResultSet);
/* 1636 */         currentResultSet = newResultSet;
/*      */         
/* 1638 */         if (result != null)
/*      */           continue; 
/* 1640 */         result = currentResultSet;
/*      */       }
/* 1642 */       while (streamResults && this.serverSession.hasMoreResults() && 
/* 1643 */         !((Resultset)currentResultSet).hasRows());
/*      */     } 
/*      */ 
/*      */     
/* 1647 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T extends Resultset> T readAllResults(int maxRows, boolean streamResults, NativePacketPayload resultPacket, boolean isBinaryEncoded, ColumnDefinition metadata, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) throws IOException {
/* 1653 */     resultPacket.setPosition(0);
/* 1654 */     Resultset resultset = read(Resultset.class, maxRows, streamResults, resultPacket, isBinaryEncoded, metadata, resultSetFactory);
/*      */     
/* 1656 */     if (this.serverSession.hasMoreResults()) {
/* 1657 */       Resultset resultset1 = resultset;
/* 1658 */       if (streamResults) {
/* 1659 */         resultset1 = readNextResultset(resultset1, maxRows, true, isBinaryEncoded, resultSetFactory);
/*      */       } else {
/* 1661 */         while (this.serverSession.hasMoreResults()) {
/* 1662 */           resultset1 = readNextResultset(resultset1, maxRows, false, isBinaryEncoded, resultSetFactory);
/*      */         }
/* 1664 */         clearInputStream();
/*      */       } 
/*      */     } 
/*      */     
/* 1668 */     if (this.hadWarnings) {
/* 1669 */       scanForAndThrowDataTruncation();
/*      */     }
/*      */     
/* 1672 */     reclaimLargeReusablePacket();
/* 1673 */     return (T)resultset;
/*      */   }
/*      */   
/*      */   public final <T> T readServerStatusForResultSets(NativePacketPayload rowPacket, boolean saveOldStatus) {
/*      */     OkPacket okPacket;
/* 1678 */     T result = null;
/* 1679 */     if (rowPacket.isEOFPacket()) {
/*      */       
/* 1681 */       rowPacket.setPosition(1);
/* 1682 */       this.warningCount = (int)rowPacket.readInteger(NativeConstants.IntegerDataType.INT2);
/* 1683 */       if (this.warningCount > 0) {
/* 1684 */         this.hadWarnings = true;
/*      */       }
/*      */       
/* 1687 */       this.serverSession.setStatusFlags((int)rowPacket.readInteger(NativeConstants.IntegerDataType.INT2), saveOldStatus);
/* 1688 */       checkTransactionState();
/*      */     } else {
/*      */       
/* 1691 */       OkPacket ok = OkPacket.parse(rowPacket, this.serverSession.getCharsetSettings().getErrorMessageEncoding());
/* 1692 */       okPacket = ok;
/*      */       
/* 1694 */       this.serverSession.setStatusFlags(ok.getStatusFlags(), saveOldStatus);
/* 1695 */       this.serverSession.getServerSessionStateController().setSessionStateChanges(ok.getSessionStateChanges());
/* 1696 */       checkTransactionState();
/*      */       
/* 1698 */       this.warningCount = ok.getWarningCount();
/* 1699 */       if (this.warningCount > 0) {
/* 1700 */         this.hadWarnings = true;
/*      */       }
/*      */     } 
/* 1703 */     return (T)okPacket;
/*      */   }
/*      */ 
/*      */   
/*      */   public <T extends com.mysql.cj.QueryResult> T readQueryResult(ResultBuilder<T> resultBuilder) {
/* 1708 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */   
/*      */   public InputStream getLocalInfileInputStream() {
/* 1712 */     return this.localInfileInputStream;
/*      */   }
/*      */   
/*      */   public void setLocalInfileInputStream(InputStream stream) {
/* 1716 */     this.localInfileInputStream = stream;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final NativePacketPayload sendFileToServer(String fileName) {
/* 1727 */     NativePacketPayload filePacket = (this.loadFileBufRef == null) ? null : this.loadFileBufRef.get();
/*      */     
/* 1729 */     int bigPacketLength = Math.min(((Integer)this.maxAllowedPacket.getValue()).intValue() - 12, 
/* 1730 */         alignPacketSize(((Integer)this.maxAllowedPacket.getValue()).intValue() - 16, 4096) - 12);
/* 1731 */     int oneMeg = 1048576;
/* 1732 */     int smallerPacketSizeAligned = Math.min(oneMeg - 12, 
/* 1733 */         alignPacketSize(oneMeg - 16, 4096) - 12);
/* 1734 */     int packetLength = Math.min(smallerPacketSizeAligned, bigPacketLength);
/*      */     
/* 1736 */     if (filePacket == null) {
/*      */       try {
/* 1738 */         filePacket = new NativePacketPayload(packetLength);
/* 1739 */         this.loadFileBufRef = new SoftReference<>(filePacket);
/* 1740 */       } catch (OutOfMemoryError oom) {
/* 1741 */         throw ExceptionFactory.createException(Messages.getString("MysqlIO.111", new Object[] { Integer.valueOf(packetLength) }), "HY001", 0, false, oom, this.exceptionInterceptor);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 1746 */     filePacket.setPosition(0);
/*      */     
/* 1748 */     byte[] fileBuf = new byte[packetLength];
/* 1749 */     BufferedInputStream fileIn = null;
/*      */     try {
/* 1751 */       fileIn = getFileStream(fileName);
/*      */       
/* 1753 */       int bytesRead = 0;
/* 1754 */       while ((bytesRead = fileIn.read(fileBuf)) != -1) {
/* 1755 */         filePacket.setPosition(0);
/* 1756 */         filePacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, fileBuf, 0, bytesRead);
/* 1757 */         send(filePacket, filePacket.getPosition());
/*      */       } 
/* 1759 */     } catch (IOException ioEx) {
/* 1760 */       boolean isParanoid = ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue();
/*      */       
/* 1762 */       StringBuilder messageBuf = new StringBuilder(Messages.getString("MysqlIO.62"));
/* 1763 */       if (fileName != null && !isParanoid) {
/* 1764 */         messageBuf.append("'");
/* 1765 */         messageBuf.append(fileName);
/* 1766 */         messageBuf.append("'");
/*      */       } 
/* 1768 */       messageBuf.append(Messages.getString("MysqlIO.63"));
/* 1769 */       if (!isParanoid) {
/* 1770 */         messageBuf.append(Messages.getString("MysqlIO.64"));
/* 1771 */         messageBuf.append(Util.stackTraceToString(ioEx));
/*      */       } 
/*      */       
/* 1774 */       throw ExceptionFactory.createException(messageBuf.toString(), ioEx, this.exceptionInterceptor);
/*      */     } finally {
/* 1776 */       if (fileIn != null) {
/*      */         try {
/* 1778 */           fileIn.close();
/* 1779 */         } catch (Exception ex) {
/* 1780 */           throw ExceptionFactory.createException(Messages.getString("MysqlIO.65"), ex, this.exceptionInterceptor);
/*      */         } 
/*      */         
/* 1783 */         fileIn = null;
/*      */       } else {
/*      */         
/* 1786 */         filePacket.setPosition(0);
/* 1787 */         send(filePacket, filePacket.getPosition());
/* 1788 */         checkErrorMessage();
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1793 */     filePacket.setPosition(0);
/* 1794 */     send(filePacket, filePacket.getPosition());
/*      */     
/* 1796 */     return checkErrorMessage();
/*      */   }
/*      */   private BufferedInputStream getFileStream(String fileName) throws IOException {
/*      */     Path safePath;
/* 1800 */     RuntimeProperty<Boolean> allowLoadLocalInfile = this.propertySet.getBooleanProperty(PropertyKey.allowLoadLocalInfile);
/* 1801 */     RuntimeProperty<String> allowLoadLocaInfileInPath = this.propertySet.getStringProperty(PropertyKey.allowLoadLocalInfileInPath);
/* 1802 */     RuntimeProperty<Boolean> allowUrlInLocalInfile = this.propertySet.getBooleanProperty(PropertyKey.allowUrlInLocalInfile);
/*      */     
/* 1804 */     if (!((Boolean)allowLoadLocalInfile.getValue()).booleanValue() && !allowLoadLocaInfileInPath.isExplicitlySet()) {
/* 1805 */       throw ExceptionFactory.createException(Messages.getString("MysqlIO.LoadDataLocalNotAllowed"), this.exceptionInterceptor);
/*      */     }
/*      */     
/* 1808 */     if (((Boolean)allowLoadLocalInfile.getValue()).booleanValue()) {
/*      */       
/* 1810 */       InputStream hookedStream = getLocalInfileInputStream();
/* 1811 */       if (hookedStream != null)
/* 1812 */         return new BufferedInputStream(hookedStream); 
/* 1813 */       if (((Boolean)allowUrlInLocalInfile.getValue()).booleanValue())
/*      */       {
/* 1815 */         if (fileName.indexOf(':') != -1) {
/*      */           try {
/* 1817 */             URL urlFromFileName = new URL(fileName);
/* 1818 */             return new BufferedInputStream(urlFromFileName.openStream());
/* 1819 */           } catch (MalformedURLException malformedURLException) {}
/*      */         }
/*      */       }
/*      */ 
/*      */       
/* 1824 */       return new BufferedInputStream(new FileInputStream(fileName));
/*      */     } 
/*      */ 
/*      */     
/* 1828 */     String safePathValue = (String)allowLoadLocaInfileInPath.getValue();
/*      */     
/* 1830 */     if (safePathValue.length() == 0) {
/* 1831 */       throw ExceptionFactory.createException(
/* 1832 */           Messages.getString("MysqlIO.60", new Object[] { safePathValue, PropertyKey.allowLoadLocalInfileInPath.getKeyName() }), this.exceptionInterceptor);
/*      */     }
/*      */     
/*      */     try {
/* 1836 */       safePath = Paths.get(safePathValue, new String[0]).toRealPath(new java.nio.file.LinkOption[0]);
/* 1837 */     } catch (IOException|InvalidPathException e) {
/* 1838 */       throw ExceptionFactory.createException(
/* 1839 */           Messages.getString("MysqlIO.60", new Object[] { safePathValue, PropertyKey.allowLoadLocalInfileInPath.getKeyName() }), e, this.exceptionInterceptor);
/*      */     } 
/*      */ 
/*      */     
/* 1843 */     if (((Boolean)allowUrlInLocalInfile.getValue()).booleanValue()) {
/*      */       try {
/* 1845 */         URL urlFromFileName = new URL(fileName);
/*      */         
/* 1847 */         if (!urlFromFileName.getProtocol().equalsIgnoreCase("file")) {
/* 1848 */           throw ExceptionFactory.createException(Messages.getString("MysqlIO.66", new Object[] { urlFromFileName.getProtocol() }), this.exceptionInterceptor);
/*      */         }
/*      */ 
/*      */         
/*      */         try {
/* 1853 */           InetAddress addr = InetAddress.getByName(urlFromFileName.getHost());
/* 1854 */           if (!addr.isLoopbackAddress()) {
/* 1855 */             throw ExceptionFactory.createException(Messages.getString("MysqlIO.67", new Object[] { urlFromFileName.getHost() }), this.exceptionInterceptor);
/*      */           }
/*      */         }
/* 1858 */         catch (UnknownHostException e) {
/* 1859 */           throw ExceptionFactory.createException(Messages.getString("MysqlIO.68", new Object[] { fileName }), e, this.exceptionInterceptor);
/*      */         } 
/*      */         
/* 1862 */         Path path = null;
/*      */         try {
/* 1864 */           path = Paths.get(urlFromFileName.toURI()).toRealPath(new java.nio.file.LinkOption[0]);
/* 1865 */         } catch (InvalidPathException e) {
/*      */           
/* 1867 */           String pathString = urlFromFileName.getPath();
/* 1868 */           if (pathString.indexOf(':') != -1 && (pathString.startsWith("/") || pathString.startsWith("\\"))) {
/* 1869 */             pathString = pathString.replaceFirst("^[/\\\\]*", "");
/*      */           }
/* 1871 */           path = Paths.get(pathString, new String[0]).toRealPath(new java.nio.file.LinkOption[0]);
/* 1872 */         } catch (IllegalArgumentException e) {
/*      */           
/* 1874 */           path = Paths.get(urlFromFileName.getPath(), new String[0]).toRealPath(new java.nio.file.LinkOption[0]);
/*      */         } 
/* 1876 */         if (!path.startsWith(safePath)) {
/* 1877 */           throw ExceptionFactory.createException(Messages.getString("MysqlIO.61", new Object[] { path, safePath }), this.exceptionInterceptor);
/*      */         }
/*      */         
/* 1880 */         return new BufferedInputStream(urlFromFileName.openStream());
/* 1881 */       } catch (MalformedURLException|java.net.URISyntaxException malformedURLException) {}
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1886 */     Path filePath = Paths.get(fileName, new String[0]).toRealPath(new java.nio.file.LinkOption[0]);
/* 1887 */     if (!filePath.startsWith(safePath)) {
/* 1888 */       throw ExceptionFactory.createException(Messages.getString("MysqlIO.61", new Object[] { filePath, safePath }), this.exceptionInterceptor);
/*      */     }
/* 1890 */     return new BufferedInputStream(new FileInputStream(filePath.toFile()));
/*      */   }
/*      */   
/*      */   private int alignPacketSize(int a, int l) {
/* 1894 */     return a + l - 1 & (l - 1 ^ 0xFFFFFFFF);
/*      */   }
/*      */   
/* 1897 */   public NativeProtocol(Log logger) { this.streamingData = null;
/*      */     this.log = logger;
/*      */     this.metricsHolder = new BaseMetricsHolder(); } public ResultsetRows getStreamingData() {
/* 1900 */     return this.streamingData;
/*      */   }
/*      */   
/*      */   public void setStreamingData(ResultsetRows streamingData) {
/* 1904 */     this.streamingData = streamingData;
/*      */   }
/*      */   
/*      */   public void checkForOutstandingStreamingData() {
/* 1908 */     if (this.streamingData != null) {
/* 1909 */       boolean shouldClobber = ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.clobberStreamingResults).getValue()).booleanValue();
/*      */       
/* 1911 */       if (!shouldClobber) {
/* 1912 */         throw ExceptionFactory.createException(Messages.getString("MysqlIO.39") + this.streamingData + Messages.getString("MysqlIO.40") + 
/* 1913 */             Messages.getString("MysqlIO.41") + Messages.getString("MysqlIO.42"), this.exceptionInterceptor);
/*      */       }
/*      */ 
/*      */       
/* 1917 */       this.streamingData.getOwner().closeOwner(false);
/*      */ 
/*      */       
/* 1920 */       clearInputStream();
/*      */     } 
/*      */   }
/*      */   
/*      */   public void unsetStreamingData(ResultsetRows streamer) {
/* 1925 */     if (this.streamingData == null) {
/* 1926 */       throw ExceptionFactory.createException(Messages.getString("MysqlIO.17") + streamer + Messages.getString("MysqlIO.18"), this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     
/* 1930 */     if (streamer == this.streamingData) {
/* 1931 */       this.streamingData = null;
/*      */     }
/*      */   }
/*      */   
/*      */   public void scanForAndThrowDataTruncation() {
/* 1936 */     if (this.streamingData == null && ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.jdbcCompliantTruncation).getValue()).booleanValue() && getWarningCount() > 0) {
/* 1937 */       int warningCountOld = getWarningCount();
/* 1938 */       convertShowWarningsToSQLWarnings(true);
/* 1939 */       setWarningCount(warningCountOld);
/*      */     } 
/*      */   }
/*      */   
/*      */   public StringBuilder generateQueryCommentBlock(StringBuilder buf) {
/* 1944 */     buf.append("/* conn id ");
/* 1945 */     buf.append(getServerSession().getCapabilities().getThreadId());
/* 1946 */     buf.append(" clock: ");
/* 1947 */     buf.append(System.currentTimeMillis());
/* 1948 */     buf.append(" */ ");
/*      */     
/* 1950 */     return buf;
/*      */   }
/*      */   
/*      */   public BaseMetricsHolder getMetricsHolder() {
/* 1954 */     return this.metricsHolder;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getQueryComment() {
/* 1959 */     return this.queryComment;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setQueryComment(String comment) {
/* 1964 */     this.queryComment = comment;
/*      */   }
/*      */   
/*      */   private void appendDeadlockStatusInformation(Session sess, String xOpen, StringBuilder errorBuf) {
/* 1968 */     if (((Boolean)sess.getPropertySet().getBooleanProperty(PropertyKey.includeInnodbStatusInDeadlockExceptions).getValue()).booleanValue() && xOpen != null && (xOpen
/* 1969 */       .startsWith("40") || xOpen.startsWith("41")) && getStreamingData() == null) {
/*      */       
/*      */       try {
/* 1972 */         NativePacketPayload resultPacket = sendCommand(getCommandBuilder().buildComQuery(getSharedSendPacket(), "SHOW ENGINE INNODB STATUS"), false, 0);
/*      */         
/* 1974 */         Resultset rs = readAllResults(-1, false, resultPacket, false, (ColumnDefinition)null, new ResultsetFactory(Resultset.Type.FORWARD_ONLY, null));
/*      */         
/* 1976 */         int colIndex = 0;
/* 1977 */         Field f = null;
/* 1978 */         for (int i = 0; i < (rs.getColumnDefinition().getFields()).length; i++) {
/* 1979 */           f = rs.getColumnDefinition().getFields()[i];
/* 1980 */           if ("Status".equals(f.getName())) {
/* 1981 */             colIndex = i;
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/* 1986 */         StringValueFactory stringValueFactory = new StringValueFactory(this.propertySet);
/*      */         
/*      */         Row r;
/* 1989 */         if ((r = (Row)rs.getRows().next()) != null) {
/* 1990 */           errorBuf.append("\n\n").append((String)r.getValue(colIndex, (ValueFactory)stringValueFactory));
/*      */         } else {
/* 1992 */           errorBuf.append("\n\n").append(Messages.getString("MysqlIO.NoInnoDBStatusFound"));
/*      */         } 
/* 1994 */       } catch (IOException|CJException ex) {
/* 1995 */         errorBuf.append("\n\n").append(Messages.getString("MysqlIO.InnoDBStatusFailed")).append("\n\n").append(Util.stackTraceToString(ex));
/*      */       } 
/*      */     }
/*      */     
/* 1999 */     if (((Boolean)sess.getPropertySet().getBooleanProperty(PropertyKey.includeThreadDumpInDeadlockExceptions).getValue()).booleanValue()) {
/* 2000 */       errorBuf.append("\n\n*** Java threads running at time of deadlock ***\n\n");
/*      */       
/* 2002 */       ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();
/* 2003 */       long[] threadIds = threadMBean.getAllThreadIds();
/*      */       
/* 2005 */       ThreadInfo[] threads = threadMBean.getThreadInfo(threadIds, 2147483647);
/* 2006 */       List<ThreadInfo> activeThreads = new ArrayList<>();
/*      */       
/* 2008 */       for (ThreadInfo info : threads) {
/* 2009 */         if (info != null) {
/* 2010 */           activeThreads.add(info);
/*      */         }
/*      */       } 
/*      */       
/* 2014 */       for (ThreadInfo threadInfo : activeThreads) {
/*      */ 
/*      */         
/* 2017 */         errorBuf.append('"').append(threadInfo.getThreadName()).append("\" tid=").append(threadInfo.getThreadId()).append(" ")
/* 2018 */           .append(threadInfo.getThreadState());
/*      */         
/* 2020 */         if (threadInfo.getLockName() != null) {
/* 2021 */           errorBuf.append(" on lock=").append(threadInfo.getLockName());
/*      */         }
/* 2023 */         if (threadInfo.isSuspended()) {
/* 2024 */           errorBuf.append(" (suspended)");
/*      */         }
/* 2026 */         if (threadInfo.isInNative()) {
/* 2027 */           errorBuf.append(" (running in native)");
/*      */         }
/*      */         
/* 2030 */         StackTraceElement[] stackTrace = threadInfo.getStackTrace();
/*      */         
/* 2032 */         if (stackTrace.length > 0) {
/* 2033 */           errorBuf.append(" in ");
/* 2034 */           errorBuf.append(stackTrace[0].getClassName()).append(".");
/* 2035 */           errorBuf.append(stackTrace[0].getMethodName()).append("()");
/*      */         } 
/*      */         
/* 2038 */         errorBuf.append("\n");
/*      */         
/* 2040 */         if (threadInfo.getLockOwnerName() != null) {
/* 2041 */           errorBuf.append("\t owned by ").append(threadInfo.getLockOwnerName()).append(" Id=").append(threadInfo.getLockOwnerId()).append("\n");
/*      */         }
/*      */         
/* 2044 */         for (int j = 0; j < stackTrace.length; j++) {
/* 2045 */           StackTraceElement ste = stackTrace[j];
/* 2046 */           errorBuf.append("\tat ").append(ste.toString()).append("\n");
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private StringBuilder appendResultSetSlashGStyle(StringBuilder appendTo, Resultset rs) {
/* 2053 */     Field[] fields = rs.getColumnDefinition().getFields();
/* 2054 */     int maxWidth = 0;
/* 2055 */     for (int i = 0; i < fields.length; i++) {
/* 2056 */       if (fields[i].getColumnLabel().length() > maxWidth) {
/* 2057 */         maxWidth = fields[i].getColumnLabel().length();
/*      */       }
/*      */     } 
/*      */     
/* 2061 */     int rowCount = 1;
/*      */     Row r;
/* 2063 */     while ((r = (Row)rs.getRows().next()) != null) {
/* 2064 */       appendTo.append("*************************** ");
/* 2065 */       appendTo.append(rowCount++);
/* 2066 */       appendTo.append(". row ***************************\n");
/*      */       
/* 2068 */       for (int j = 0; j < fields.length; j++) {
/* 2069 */         int leftPad = maxWidth - fields[j].getColumnLabel().length();
/* 2070 */         for (int k = 0; k < leftPad; k++) {
/* 2071 */           appendTo.append(" ");
/*      */         }
/* 2073 */         appendTo.append(fields[j].getColumnLabel()).append(": ");
/* 2074 */         String stringVal = (String)r.getValue(j, (ValueFactory)new StringValueFactory(this.propertySet));
/* 2075 */         appendTo.append((stringVal != null) ? stringVal : "NULL").append("\n");
/*      */       } 
/* 2077 */       appendTo.append("\n");
/*      */     } 
/* 2079 */     return appendTo;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SQLWarning convertShowWarningsToSQLWarnings(boolean forTruncationOnly) {
/* 2094 */     if (this.warningCount == 0) {
/* 2095 */       return null;
/*      */     }
/*      */     
/* 2098 */     SQLWarning currentWarning = null;
/* 2099 */     ResultsetRows rows = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*      */       SQLWarning sQLWarning;
/*      */ 
/*      */ 
/*      */       
/* 2109 */       NativePacketPayload resultPacket = sendCommand(getCommandBuilder().buildComQuery(getSharedSendPacket(), "SHOW WARNINGS"), false, 0);
/*      */       
/* 2111 */       Resultset warnRs = readAllResults(-1, (this.warningCount > 99), resultPacket, false, (ColumnDefinition)null, new ResultsetFactory(Resultset.Type.FORWARD_ONLY, Resultset.Concurrency.READ_ONLY));
/*      */ 
/*      */       
/* 2114 */       int codeFieldIndex = warnRs.getColumnDefinition().findColumn("Code", false, 1) - 1;
/* 2115 */       int messageFieldIndex = warnRs.getColumnDefinition().findColumn("Message", false, 1) - 1;
/*      */       
/* 2117 */       StringValueFactory stringValueFactory = new StringValueFactory(this.propertySet);
/* 2118 */       IntegerValueFactory integerValueFactory = new IntegerValueFactory(this.propertySet);
/*      */       
/* 2120 */       rows = warnRs.getRows();
/*      */       Row r;
/* 2122 */       while ((r = (Row)rows.next()) != null) {
/*      */         MysqlDataTruncation mysqlDataTruncation;
/* 2124 */         int code = ((Integer)r.getValue(codeFieldIndex, (ValueFactory)integerValueFactory)).intValue();
/*      */         
/* 2126 */         if (forTruncationOnly) {
/* 2127 */           if (code == 1265 || code == 1264) {
/* 2128 */             MysqlDataTruncation mysqlDataTruncation1 = new MysqlDataTruncation((String)r.getValue(messageFieldIndex, (ValueFactory)stringValueFactory), 0, false, false, 0, 0, code);
/*      */             
/* 2130 */             if (currentWarning == null) {
/* 2131 */               mysqlDataTruncation = mysqlDataTruncation1; continue;
/*      */             } 
/* 2133 */             mysqlDataTruncation.setNextWarning((SQLWarning)mysqlDataTruncation1);
/*      */           } 
/*      */           
/*      */           continue;
/*      */         } 
/* 2138 */         String message = (String)r.getValue(messageFieldIndex, (ValueFactory)stringValueFactory);
/*      */         
/* 2140 */         SQLWarning newWarning = new SQLWarning(message, MysqlErrorNumbers.mysqlToSqlState(code), code);
/* 2141 */         if (mysqlDataTruncation == null) {
/* 2142 */           sQLWarning = newWarning; continue;
/*      */         } 
/* 2144 */         sQLWarning.setNextWarning(newWarning);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2149 */       if (forTruncationOnly && sQLWarning != null) {
/* 2150 */         throw ExceptionFactory.createException(sQLWarning.getMessage(), sQLWarning);
/*      */       }
/*      */       
/* 2153 */       return sQLWarning;
/* 2154 */     } catch (IOException ex) {
/* 2155 */       throw ExceptionFactory.createException(ex.getMessage(), ex);
/*      */     } finally {
/* 2157 */       if (rows != null) {
/* 2158 */         rows.close();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ColumnDefinition readMetadata() {
/* 2165 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */   
/*      */   public void close() throws IOException {
/* 2170 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void configureTimeZone() {
/* 2181 */     String connectionTimeZone = (String)getPropertySet().getStringProperty(PropertyKey.connectionTimeZone).getValue();
/*      */     
/* 2183 */     TimeZone selectedTz = null;
/*      */     
/* 2185 */     if (connectionTimeZone == null || StringUtils.isEmptyOrWhitespaceOnly(connectionTimeZone) || "LOCAL".equals(connectionTimeZone)) {
/* 2186 */       selectedTz = TimeZone.getDefault();
/*      */     } else {
/* 2188 */       if ("SERVER".equals(connectionTimeZone)) {
/*      */         return;
/*      */       }
/*      */ 
/*      */       
/* 2193 */       selectedTz = TimeZone.getTimeZone(ZoneId.of(connectionTimeZone));
/*      */     } 
/*      */     
/* 2196 */     this.serverSession.setSessionTimeZone(selectedTz);
/*      */     
/* 2198 */     if (((Boolean)getPropertySet().getBooleanProperty(PropertyKey.forceConnectionTimeZoneToSession).getValue()).booleanValue()) {
/*      */ 
/*      */       
/* 2201 */       StringBuilder query = new StringBuilder("SET SESSION time_zone='");
/*      */       
/* 2203 */       ZoneId zid = selectedTz.toZoneId().normalized();
/* 2204 */       if (zid instanceof ZoneOffset) {
/* 2205 */         String offsetStr = ((ZoneOffset)zid).getId().replace("Z", "+00:00");
/* 2206 */         query.append(offsetStr);
/* 2207 */         this.serverSession.getServerVariables().put("time_zone", offsetStr);
/*      */       } else {
/* 2209 */         query.append(selectedTz.getID());
/* 2210 */         this.serverSession.getServerVariables().put("time_zone", selectedTz.getID());
/*      */       } 
/*      */       
/* 2213 */       query.append("'");
/* 2214 */       sendCommand(getCommandBuilder().buildComQuery((NativePacketPayload)null, query.toString()), false, 0);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void initServerSession() {
/* 2220 */     configureTimeZone();
/*      */     
/* 2222 */     if (this.serverSession.getServerVariables().containsKey("max_allowed_packet")) {
/* 2223 */       int serverMaxAllowedPacket = this.serverSession.getServerVariable("max_allowed_packet", -1);
/*      */ 
/*      */       
/* 2226 */       if (serverMaxAllowedPacket != -1 && (!this.maxAllowedPacket.isExplicitlySet() || serverMaxAllowedPacket < ((Integer)this.maxAllowedPacket.getValue()).intValue())) {
/* 2227 */         this.maxAllowedPacket.setValue(Integer.valueOf(serverMaxAllowedPacket));
/*      */       }
/*      */       
/* 2230 */       if (((Boolean)this.useServerPrepStmts.getValue()).booleanValue()) {
/* 2231 */         RuntimeProperty<Integer> blobSendChunkSize = this.propertySet.getProperty(PropertyKey.blobSendChunkSize);
/* 2232 */         int preferredBlobSendChunkSize = ((Integer)blobSendChunkSize.getValue()).intValue();
/*      */ 
/*      */         
/* 2235 */         int packetHeaderSize = 8203;
/* 2236 */         int allowedBlobSendChunkSize = Math.min(preferredBlobSendChunkSize, ((Integer)this.maxAllowedPacket.getValue()).intValue()) - packetHeaderSize;
/*      */         
/* 2238 */         if (allowedBlobSendChunkSize <= 0) {
/* 2239 */           throw ExceptionFactory.createException(Messages.getString("Connection.15", new Object[] { Integer.valueOf(packetHeaderSize) }), "01S00", 0, false, null, this.exceptionInterceptor);
/*      */         }
/*      */ 
/*      */         
/* 2243 */         blobSendChunkSize.setValue(Integer.valueOf(allowedBlobSendChunkSize));
/*      */       } 
/*      */     } 
/*      */     
/* 2247 */     this.serverSession.getCharsetSettings().configurePostHandshake(false);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\NativeProtocol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */