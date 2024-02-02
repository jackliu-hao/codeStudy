/*      */ package com.mysql.cj.protocol.x;
/*      */ 
/*      */ import com.google.protobuf.GeneratedMessageV3;
/*      */ import com.google.protobuf.Message;
/*      */ import com.mysql.cj.Constants;
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.Session;
/*      */ import com.mysql.cj.TransactionEventHandler;
/*      */ import com.mysql.cj.conf.HostInfo;
/*      */ import com.mysql.cj.conf.PropertyDefinitions;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.conf.PropertySet;
/*      */ import com.mysql.cj.conf.RuntimeProperty;
/*      */ import com.mysql.cj.exceptions.AssertionFailedException;
/*      */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*      */ import com.mysql.cj.exceptions.CJConnectionFeatureNotAvailableException;
/*      */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*      */ import com.mysql.cj.exceptions.ConnectionIsClosedException;
/*      */ import com.mysql.cj.exceptions.ExceptionFactory;
/*      */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*      */ import com.mysql.cj.exceptions.SSLParamsException;
/*      */ import com.mysql.cj.exceptions.WrongArgumentException;
/*      */ import com.mysql.cj.log.LogFactory;
/*      */ import com.mysql.cj.protocol.AbstractProtocol;
/*      */ import com.mysql.cj.protocol.ColumnDefinition;
/*      */ import com.mysql.cj.protocol.ExportControlled;
/*      */ import com.mysql.cj.protocol.FullReadInputStream;
/*      */ import com.mysql.cj.protocol.Message;
/*      */ import com.mysql.cj.protocol.MessageListener;
/*      */ import com.mysql.cj.protocol.MessageReader;
/*      */ import com.mysql.cj.protocol.MessageSender;
/*      */ import com.mysql.cj.protocol.Protocol;
/*      */ import com.mysql.cj.protocol.ProtocolEntity;
/*      */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*      */ import com.mysql.cj.protocol.ResultBuilder;
/*      */ import com.mysql.cj.protocol.ResultStreamer;
/*      */ import com.mysql.cj.protocol.Resultset;
/*      */ import com.mysql.cj.protocol.ServerCapabilities;
/*      */ import com.mysql.cj.protocol.ServerSession;
/*      */ import com.mysql.cj.protocol.SocketConnection;
/*      */ import com.mysql.cj.protocol.a.NativeSocketConnection;
/*      */ import com.mysql.cj.result.DefaultColumnDefinition;
/*      */ import com.mysql.cj.result.Field;
/*      */ import com.mysql.cj.result.LongValueFactory;
/*      */ import com.mysql.cj.result.ValueFactory;
/*      */ import com.mysql.cj.util.SequentialIdLease;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import com.mysql.cj.x.protobuf.Mysqlx;
/*      */ import com.mysql.cj.x.protobuf.MysqlxConnection;
/*      */ import com.mysql.cj.x.protobuf.MysqlxDatatypes;
/*      */ import com.mysql.cj.x.protobuf.MysqlxNotice;
/*      */ import com.mysql.cj.x.protobuf.MysqlxResultset;
/*      */ import com.mysql.cj.x.protobuf.MysqlxSession;
/*      */ import com.mysql.cj.x.protobuf.MysqlxSql;
/*      */ import com.mysql.cj.xdevapi.PreparableStatement;
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Optional;
/*      */ import java.util.TreeMap;
/*      */ import java.util.concurrent.CompletableFuture;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.stream.Collectors;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class XProtocol
/*      */   extends AbstractProtocol<XMessage>
/*      */   implements Protocol<XMessage>
/*      */ {
/*  120 */   private static int RETRY_PREPARE_STATEMENT_COUNTDOWN = 100;
/*      */   
/*      */   private MessageReader<XMessageHeader, XMessage> reader;
/*      */   
/*      */   private MessageSender<XMessage> sender;
/*      */   
/*      */   private Closeable managedResource;
/*      */   
/*      */   private ResultStreamer currentResultStreamer;
/*  129 */   XServerSession serverSession = null;
/*  130 */   Boolean useSessionResetKeepOpen = null;
/*      */   
/*      */   public String defaultSchemaName;
/*      */   
/*  134 */   private Map<String, Object> clientCapabilities = new HashMap<>();
/*      */   
/*      */   private boolean supportsPreparedStatements = true;
/*      */   
/*  138 */   private int retryPrepareStatementCountdown = 0;
/*  139 */   private SequentialIdLease preparedStatementIds = new SequentialIdLease();
/*  140 */   private ReferenceQueue<PreparableStatement<?>> preparableStatementRefQueue = new ReferenceQueue<>();
/*  141 */   private Map<Integer, PreparableStatement.PreparableStatementFinalizer> preparableStatementFinalizerReferences = new TreeMap<>();
/*      */   
/*      */   private boolean compressionEnabled = false;
/*      */   
/*      */   private CompressionAlgorithm compressionAlgorithm;
/*  146 */   private Map<Class<? extends GeneratedMessageV3>, ProtocolEntityFactory<? extends ProtocolEntity, XMessage>> messageToProtocolEntityFactory = new HashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String currUser;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String currPassword;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String currDatabase;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void init(Session sess, SocketConnection socketConn, PropertySet propSet, TransactionEventHandler trManager) {
/*  173 */     super.init(sess, socketConn, propSet, trManager);
/*      */ 
/*      */     
/*  176 */     this.log = LogFactory.getLogger(getPropertySet().getStringProperty(PropertyKey.logger).getStringValue(), "MySQL");
/*      */     
/*  178 */     this.messageBuilder = new XMessageBuilder();
/*      */     
/*  180 */     this.authProvider = new XAuthenticationProvider();
/*  181 */     this.authProvider.init(this, propSet, null);
/*      */     
/*  183 */     this.useSessionResetKeepOpen = null;
/*      */     
/*  185 */     this.messageToProtocolEntityFactory.put(MysqlxResultset.ColumnMetaData.class, new FieldFactory("latin1"));
/*  186 */     this.messageToProtocolEntityFactory.put(MysqlxNotice.Frame.class, new NoticeFactory());
/*  187 */     this.messageToProtocolEntityFactory.put(MysqlxResultset.Row.class, new XProtocolRowFactory());
/*  188 */     this.messageToProtocolEntityFactory.put(MysqlxResultset.FetchDoneMoreResultsets.class, new FetchDoneMoreResultsFactory());
/*  189 */     this.messageToProtocolEntityFactory.put(MysqlxResultset.FetchDone.class, new FetchDoneEntityFactory());
/*  190 */     this.messageToProtocolEntityFactory.put(MysqlxSql.StmtExecuteOk.class, new StatementExecuteOkFactory());
/*  191 */     this.messageToProtocolEntityFactory.put(Mysqlx.Ok.class, new OkFactory());
/*      */   }
/*      */   
/*      */   public ServerSession getServerSession() {
/*  195 */     return this.serverSession;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendCapabilities(Map<String, Object> keyValuePair) {
/*  205 */     keyValuePair.forEach((k, v) -> ((XServerCapabilities)getServerSession().getCapabilities()).setCapability(k, v));
/*  206 */     this.sender.send(((XMessageBuilder)this.messageBuilder).buildCapabilitiesSet(keyValuePair));
/*  207 */     readQueryResult(new OkBuilder());
/*      */   }
/*      */ 
/*      */   
/*      */   public void negotiateSSLConnection() {
/*  212 */     if (!ExportControlled.enabled()) {
/*  213 */       throw new CJConnectionFeatureNotAvailableException();
/*      */     }
/*      */     
/*  216 */     if (!((XServerCapabilities)this.serverSession.getCapabilities()).hasCapability(XServerCapabilities.KEY_TLS)) {
/*  217 */       throw new CJCommunicationsException("A secure connection is required but the server is not configured with SSL.");
/*      */     }
/*      */ 
/*      */     
/*  221 */     this.reader.stopAfterNextMessage();
/*      */     
/*  223 */     Map<String, Object> tlsCapabilities = new HashMap<>();
/*  224 */     tlsCapabilities.put(XServerCapabilities.KEY_TLS, Boolean.valueOf(true));
/*  225 */     sendCapabilities(tlsCapabilities);
/*      */     
/*      */     try {
/*  228 */       this.socketConnection.performTlsHandshake(null, this.log);
/*  229 */     } catch (SSLParamsException|com.mysql.cj.exceptions.FeatureNotAvailableException|IOException e) {
/*  230 */       throw new CJCommunicationsException(e);
/*      */     } 
/*      */     
/*      */     try {
/*  234 */       this.sender = new SyncMessageSender(this.socketConnection.getMysqlOutput());
/*  235 */       this.reader = new SyncMessageReader(this.socketConnection.getMysqlInput(), (Protocol.ProtocolEventHandler)this);
/*  236 */     } catch (IOException e) {
/*  237 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void negotiateCompression() {
/*  246 */     PropertyDefinitions.Compression compression = (PropertyDefinitions.Compression)this.propertySet.getEnumProperty(PropertyKey.xdevapiCompression.getKeyName()).getValue();
/*  247 */     if (compression == PropertyDefinitions.Compression.DISABLED) {
/*      */       return;
/*      */     }
/*      */     
/*  251 */     Map<String, List<String>> compressionCapabilities = this.serverSession.serverCapabilities.getCompression();
/*  252 */     if (compressionCapabilities.isEmpty() || !compressionCapabilities.containsKey(XServerCapabilities.SUBKEY_COMPRESSION_ALGORITHM) || ((List)compressionCapabilities
/*  253 */       .get(XServerCapabilities.SUBKEY_COMPRESSION_ALGORITHM)).isEmpty()) {
/*  254 */       if (compression == PropertyDefinitions.Compression.REQUIRED) {
/*  255 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.0"));
/*      */       }
/*      */       
/*      */       return;
/*      */     } 
/*  260 */     RuntimeProperty<String> compressionAlgorithmsProp = this.propertySet.getStringProperty(PropertyKey.xdevapiCompressionAlgorithms.getKeyName());
/*  261 */     String compressionAlgorithmsList = (String)compressionAlgorithmsProp.getValue();
/*  262 */     compressionAlgorithmsList = (compressionAlgorithmsList == null) ? "" : compressionAlgorithmsList.trim();
/*      */     
/*  264 */     String[] compressionAlgsOrder = compressionAlgorithmsList.split("\\s*,\\s*");
/*      */     
/*  266 */     String[] compressionAlgorithmsOrder = (String[])Arrays.<String>stream(compressionAlgsOrder).sequential().filter(n -> (n != null && n.length() > 0)).map(String::toLowerCase).map(CompressionAlgorithm::getNormalizedAlgorithmName).toArray(x$0 -> new String[x$0]);
/*      */     
/*  268 */     String compressionExtensions = (String)this.propertySet.getStringProperty(PropertyKey.xdevapiCompressionExtensions.getKeyName()).getValue();
/*  269 */     compressionExtensions = (compressionExtensions == null) ? "" : compressionExtensions.trim();
/*  270 */     Map<String, CompressionAlgorithm> compressionAlgorithms = getCompressionExtensions(compressionExtensions);
/*      */ 
/*      */ 
/*      */     
/*  274 */     Optional<String> algorithmOpt = Arrays.<String>stream(compressionAlgorithmsOrder).sequential().filter((List)compressionCapabilities.get(XServerCapabilities.SUBKEY_COMPRESSION_ALGORITHM)::contains).filter(compressionAlgorithms::containsKey).findFirst();
/*  275 */     if (!algorithmOpt.isPresent()) {
/*  276 */       if (compression == PropertyDefinitions.Compression.REQUIRED) {
/*  277 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.2"));
/*      */       }
/*      */       return;
/*      */     } 
/*  281 */     String algorithm = algorithmOpt.get();
/*  282 */     this.compressionAlgorithm = compressionAlgorithms.get(algorithm);
/*      */ 
/*      */     
/*  285 */     this.compressionAlgorithm.getInputStreamClass();
/*  286 */     this.compressionAlgorithm.getOutputStreamClass();
/*      */     
/*  288 */     Map<String, Object> compressionCap = new HashMap<>();
/*  289 */     compressionCap.put(XServerCapabilities.SUBKEY_COMPRESSION_ALGORITHM, algorithm);
/*  290 */     compressionCap.put(XServerCapabilities.SUBKEY_COMPRESSION_SERVER_COMBINE_MIXED_MESSAGES, Boolean.valueOf(true));
/*  291 */     sendCapabilities(Collections.singletonMap(XServerCapabilities.KEY_COMPRESSION, compressionCap));
/*      */     
/*  293 */     this.compressionEnabled = true;
/*      */   }
/*      */   
/*      */   public void beforeHandshake() {
/*  297 */     this.serverSession = new XServerSession();
/*      */     
/*      */     try {
/*  300 */       this.sender = new SyncMessageSender(this.socketConnection.getMysqlOutput());
/*  301 */       this.reader = new SyncMessageReader(this.socketConnection.getMysqlInput(), (Protocol.ProtocolEventHandler)this);
/*  302 */       this.managedResource = this.socketConnection.getMysqlSocket();
/*  303 */     } catch (IOException e) {
/*  304 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */     
/*  307 */     this.serverSession.setCapabilities(readServerCapabilities());
/*      */ 
/*      */     
/*  310 */     String attributes = (String)this.propertySet.getStringProperty(PropertyKey.xdevapiConnectionAttributes).getValue();
/*  311 */     if (attributes == null || !attributes.equalsIgnoreCase("false")) {
/*  312 */       Map<String, String> attMap = getConnectionAttributesMap("true".equalsIgnoreCase(attributes) ? "" : attributes);
/*  313 */       this.clientCapabilities.put(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS, attMap);
/*      */     } 
/*      */ 
/*      */     
/*  317 */     RuntimeProperty<PropertyDefinitions.XdevapiSslMode> xdevapiSslMode = this.propertySet.getEnumProperty(PropertyKey.xdevapiSslMode);
/*  318 */     RuntimeProperty<PropertyDefinitions.SslMode> jdbcSslMode = this.propertySet.getEnumProperty(PropertyKey.sslMode);
/*  319 */     if (xdevapiSslMode.isExplicitlySet() || !jdbcSslMode.isExplicitlySet()) {
/*  320 */       jdbcSslMode.setValue(PropertyDefinitions.SslMode.valueOf(((PropertyDefinitions.XdevapiSslMode)xdevapiSslMode.getValue()).toString()));
/*      */     }
/*  322 */     RuntimeProperty<String> xdevapiSslKeyStoreUrl = this.propertySet.getStringProperty(PropertyKey.xdevapiSslKeyStoreUrl);
/*  323 */     RuntimeProperty<String> jdbcClientCertKeyStoreUrl = this.propertySet.getStringProperty(PropertyKey.clientCertificateKeyStoreUrl);
/*  324 */     if (xdevapiSslKeyStoreUrl.isExplicitlySet() || !jdbcClientCertKeyStoreUrl.isExplicitlySet()) {
/*  325 */       jdbcClientCertKeyStoreUrl.setValue(xdevapiSslKeyStoreUrl.getValue());
/*      */     }
/*  327 */     RuntimeProperty<String> xdevapiSslKeyStoreType = this.propertySet.getStringProperty(PropertyKey.xdevapiSslKeyStoreType);
/*  328 */     RuntimeProperty<String> jdbcClientCertKeyStoreType = this.propertySet.getStringProperty(PropertyKey.clientCertificateKeyStoreType);
/*  329 */     if (xdevapiSslKeyStoreType.isExplicitlySet() || !jdbcClientCertKeyStoreType.isExplicitlySet()) {
/*  330 */       jdbcClientCertKeyStoreType.setValue(xdevapiSslKeyStoreType.getValue());
/*      */     }
/*  332 */     RuntimeProperty<String> xdevapiSslKeyStorePassword = this.propertySet.getStringProperty(PropertyKey.xdevapiSslKeyStorePassword);
/*  333 */     RuntimeProperty<String> jdbcClientCertKeyStorePassword = this.propertySet.getStringProperty(PropertyKey.clientCertificateKeyStorePassword);
/*  334 */     if (xdevapiSslKeyStorePassword.isExplicitlySet() || !jdbcClientCertKeyStorePassword.isExplicitlySet()) {
/*  335 */       jdbcClientCertKeyStorePassword.setValue(xdevapiSslKeyStorePassword.getValue());
/*      */     }
/*  337 */     RuntimeProperty<Boolean> xdevapiFallbackToSystemKeyStore = this.propertySet.getBooleanProperty(PropertyKey.xdevapiFallbackToSystemKeyStore);
/*  338 */     RuntimeProperty<Boolean> jdbcFallbackToSystemKeyStore = this.propertySet.getBooleanProperty(PropertyKey.fallbackToSystemKeyStore);
/*  339 */     if (xdevapiFallbackToSystemKeyStore.isExplicitlySet() || !jdbcFallbackToSystemKeyStore.isExplicitlySet()) {
/*  340 */       jdbcFallbackToSystemKeyStore.setValue(xdevapiFallbackToSystemKeyStore.getValue());
/*      */     }
/*  342 */     RuntimeProperty<String> xdevapiSslTrustStoreUrl = this.propertySet.getStringProperty(PropertyKey.xdevapiSslTrustStoreUrl);
/*  343 */     RuntimeProperty<String> jdbcTrustCertKeyStoreUrl = this.propertySet.getStringProperty(PropertyKey.trustCertificateKeyStoreUrl);
/*  344 */     if (xdevapiSslTrustStoreUrl.isExplicitlySet() || !jdbcTrustCertKeyStoreUrl.isExplicitlySet()) {
/*  345 */       jdbcTrustCertKeyStoreUrl.setValue(xdevapiSslTrustStoreUrl.getValue());
/*      */     }
/*  347 */     RuntimeProperty<String> xdevapiSslTrustStoreType = this.propertySet.getStringProperty(PropertyKey.xdevapiSslTrustStoreType);
/*  348 */     RuntimeProperty<String> jdbcTrustCertKeyStoreType = this.propertySet.getStringProperty(PropertyKey.trustCertificateKeyStoreType);
/*  349 */     if (xdevapiSslTrustStoreType.isExplicitlySet() || !jdbcTrustCertKeyStoreType.isExplicitlySet()) {
/*  350 */       jdbcTrustCertKeyStoreType.setValue(xdevapiSslTrustStoreType.getValue());
/*      */     }
/*  352 */     RuntimeProperty<String> xdevapiSslTrustStorePassword = this.propertySet.getStringProperty(PropertyKey.xdevapiSslTrustStorePassword);
/*  353 */     RuntimeProperty<String> jdbcTrustCertKeyStorePassword = this.propertySet.getStringProperty(PropertyKey.trustCertificateKeyStorePassword);
/*  354 */     if (xdevapiSslTrustStorePassword.isExplicitlySet() || !jdbcTrustCertKeyStorePassword.isExplicitlySet()) {
/*  355 */       jdbcTrustCertKeyStorePassword.setValue(xdevapiSslTrustStorePassword.getValue());
/*      */     }
/*  357 */     RuntimeProperty<Boolean> xdevapiFallbackToSystemTrustStore = this.propertySet.getBooleanProperty(PropertyKey.xdevapiFallbackToSystemTrustStore);
/*  358 */     RuntimeProperty<Boolean> jdbcFallbackToSystemTrustStore = this.propertySet.getBooleanProperty(PropertyKey.fallbackToSystemTrustStore);
/*  359 */     if (xdevapiFallbackToSystemTrustStore.isExplicitlySet() || !jdbcFallbackToSystemTrustStore.isExplicitlySet()) {
/*  360 */       jdbcFallbackToSystemTrustStore.setValue(xdevapiFallbackToSystemTrustStore.getValue());
/*      */     }
/*      */     
/*  363 */     RuntimeProperty<PropertyDefinitions.SslMode> sslMode = jdbcSslMode;
/*  364 */     if (sslMode.getValue() == PropertyDefinitions.SslMode.PREFERRED) {
/*  365 */       sslMode.setValue(PropertyDefinitions.SslMode.REQUIRED);
/*      */     }
/*      */     
/*  368 */     RuntimeProperty<String> xdevapiTlsVersions = this.propertySet.getStringProperty(PropertyKey.xdevapiTlsVersions);
/*  369 */     RuntimeProperty<String> jdbcEnabledTlsProtocols = this.propertySet.getStringProperty(PropertyKey.tlsVersions);
/*  370 */     if (xdevapiTlsVersions.isExplicitlySet()) {
/*  371 */       if (sslMode.getValue() == PropertyDefinitions.SslMode.DISABLED) {
/*  372 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Option '" + PropertyKey.xdevapiTlsVersions
/*  373 */             .getKeyName() + "' can not be specified when SSL connections are disabled.");
/*      */       }
/*      */       
/*  376 */       String[] tlsVersions = ((String)xdevapiTlsVersions.getValue()).split("\\s*,\\s*");
/*  377 */       List<String> tryProtocols = Arrays.asList(tlsVersions);
/*  378 */       ExportControlled.checkValidProtocols(tryProtocols);
/*  379 */       jdbcEnabledTlsProtocols.setValue(xdevapiTlsVersions.getValue());
/*      */     } 
/*      */     
/*  382 */     RuntimeProperty<String> xdevapiTlsCiphersuites = this.propertySet.getStringProperty(PropertyKey.xdevapiTlsCiphersuites);
/*  383 */     RuntimeProperty<String> jdbcEnabledSslCipherSuites = this.propertySet.getStringProperty(PropertyKey.tlsCiphersuites);
/*  384 */     if (xdevapiTlsCiphersuites.isExplicitlySet()) {
/*  385 */       if (sslMode.getValue() == PropertyDefinitions.SslMode.DISABLED) {
/*  386 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Option '" + PropertyKey.xdevapiTlsCiphersuites
/*  387 */             .getKeyName() + "' can not be specified when SSL connections are disabled.");
/*      */       }
/*      */       
/*  390 */       jdbcEnabledSslCipherSuites.setValue(xdevapiTlsCiphersuites.getValue());
/*      */     } 
/*      */     
/*  393 */     boolean verifyServerCert = (sslMode.getValue() == PropertyDefinitions.SslMode.VERIFY_CA || sslMode.getValue() == PropertyDefinitions.SslMode.VERIFY_IDENTITY);
/*  394 */     String trustStoreUrl = (String)jdbcTrustCertKeyStoreUrl.getValue();
/*  395 */     if (!verifyServerCert && !StringUtils.isNullOrEmpty(trustStoreUrl)) {
/*  396 */       StringBuilder msg = new StringBuilder("Incompatible security settings. The property '");
/*  397 */       msg.append(PropertyKey.xdevapiSslTrustStoreUrl.getKeyName()).append("' requires '");
/*  398 */       msg.append(PropertyKey.xdevapiSslMode.getKeyName()).append("' as '");
/*  399 */       msg.append(PropertyDefinitions.SslMode.VERIFY_CA).append("' or '");
/*  400 */       msg.append(PropertyDefinitions.SslMode.VERIFY_IDENTITY).append("'.");
/*  401 */       throw new CJCommunicationsException(msg.toString());
/*      */     } 
/*      */     
/*  404 */     if (this.clientCapabilities.size() > 0) {
/*      */       try {
/*  406 */         sendCapabilities(this.clientCapabilities);
/*  407 */       } catch (XProtocolError e) {
/*      */ 
/*      */         
/*  410 */         if (e.getErrorCode() != 5002 && 
/*  411 */           !e.getMessage().contains(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS)) {
/*  412 */           throw e;
/*      */         }
/*  414 */         this.clientCapabilities.remove(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS);
/*      */       } 
/*      */     }
/*      */     
/*  418 */     if (jdbcSslMode.getValue() != PropertyDefinitions.SslMode.DISABLED) {
/*  419 */       negotiateSSLConnection();
/*      */     }
/*      */ 
/*      */     
/*  423 */     negotiateCompression();
/*      */   }
/*      */   
/*      */   private Map<String, String> getConnectionAttributesMap(String attStr) {
/*  427 */     Map<String, String> attMap = new HashMap<>();
/*      */     
/*  429 */     if (attStr != null) {
/*  430 */       if (attStr.startsWith("[") && attStr.endsWith("]")) {
/*  431 */         attStr = attStr.substring(1, attStr.length() - 1);
/*      */       }
/*  433 */       if (!StringUtils.isNullOrEmpty(attStr)) {
/*  434 */         String[] pairs = attStr.split(",");
/*  435 */         for (String pair : pairs) {
/*  436 */           String[] kv = pair.split("=");
/*  437 */           String key = kv[0].trim();
/*  438 */           String value = (kv.length > 1) ? kv[1].trim() : "";
/*  439 */           if (key.startsWith("_"))
/*  440 */             throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.WrongAttributeName")); 
/*  441 */           if (attMap.put(key, value) != null) {
/*  442 */             throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  443 */                 Messages.getString("Protocol.DuplicateAttribute", new Object[] { key }));
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  449 */     attMap.put("_platform", Constants.OS_ARCH);
/*  450 */     attMap.put("_os", Constants.OS_NAME + "-" + Constants.OS_VERSION);
/*  451 */     attMap.put("_client_name", "MySQL Connector/J");
/*  452 */     attMap.put("_client_version", "8.0.28");
/*  453 */     attMap.put("_client_license", "GPL");
/*  454 */     attMap.put("_runtime_version", Constants.JVM_VERSION);
/*  455 */     attMap.put("_runtime_vendor", Constants.JVM_VENDOR);
/*  456 */     return attMap;
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
/*      */   private Map<String, CompressionAlgorithm> getCompressionExtensions(String compressionExtensions) {
/*  470 */     Map<String, CompressionAlgorithm> compressionExtensionsMap = CompressionAlgorithm.getDefaultInstances();
/*      */     
/*  472 */     if (compressionExtensions.length() == 0) {
/*  473 */       return compressionExtensionsMap;
/*      */     }
/*      */     
/*  476 */     String[] compressionExtAlgs = compressionExtensions.split(",");
/*  477 */     for (String compressionExtAlg : compressionExtAlgs) {
/*  478 */       String[] compressionExtAlgParts = compressionExtAlg.split(":");
/*  479 */       if (compressionExtAlgParts.length != 3) {
/*  480 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.1"));
/*      */       }
/*  482 */       String algorithmName = compressionExtAlgParts[0].toLowerCase();
/*  483 */       String inputStreamClassName = compressionExtAlgParts[1];
/*  484 */       String outputStreamClassName = compressionExtAlgParts[2];
/*  485 */       CompressionAlgorithm compressionAlg = new CompressionAlgorithm(algorithmName, inputStreamClassName, outputStreamClassName);
/*  486 */       compressionExtensionsMap.put(compressionAlg.getAlgorithmIdentifier(), compressionAlg);
/*      */     } 
/*  488 */     return compressionExtensionsMap;
/*      */   }
/*      */   
/*  491 */   public XProtocol(HostInfo hostInfo, PropertySet propertySet) { this.currUser = null; this.currPassword = null; this.currDatabase = null; String host = hostInfo.getHost(); if (host == null || StringUtils.isEmptyOrWhitespaceOnly(host))
/*      */       host = "localhost";  int port = hostInfo.getPort(); if (port < 0)
/*      */       port = 33060;  this.defaultSchemaName = hostInfo.getDatabase(); RuntimeProperty<Integer> connectTimeout = propertySet.getIntegerProperty(PropertyKey.connectTimeout); RuntimeProperty<Integer> xdevapiConnectTimeout = propertySet.getIntegerProperty(PropertyKey.xdevapiConnectTimeout); if (xdevapiConnectTimeout.isExplicitlySet() || !connectTimeout.isExplicitlySet())
/*      */       connectTimeout.setValue(xdevapiConnectTimeout.getValue());  NativeSocketConnection nativeSocketConnection = new NativeSocketConnection(); nativeSocketConnection.connect(host, port, propertySet, null, null, 0);
/*  495 */     init((Session)null, (SocketConnection)nativeSocketConnection, propertySet, (TransactionEventHandler)null); } public void connect(String user, String password, String database) { this.currUser = user;
/*  496 */     this.currPassword = password;
/*  497 */     this.currDatabase = database;
/*      */     
/*  499 */     beforeHandshake();
/*  500 */     this.authProvider.connect(user, password, database); }
/*      */ 
/*      */   
/*      */   public void changeUser(String user, String password, String database) {
/*  504 */     this.currUser = user;
/*  505 */     this.currPassword = password;
/*  506 */     this.currDatabase = database;
/*      */     
/*  508 */     this.authProvider.changeUser(user, password, database);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void afterHandshake() {
/*  514 */     if (this.compressionEnabled) {
/*      */       try {
/*  516 */         this
/*  517 */           .reader = new SyncMessageReader(new FullReadInputStream(new CompressionSplittedInputStream((InputStream)this.socketConnection.getMysqlInput(), new CompressorStreamsFactory(this.compressionAlgorithm))), (Protocol.ProtocolEventHandler)this);
/*      */       }
/*  519 */       catch (IOException e) {
/*  520 */         ExceptionFactory.createException(Messages.getString("Protocol.Compression.6"), e);
/*      */       } 
/*      */       try {
/*  523 */         this
/*  524 */           .sender = new SyncMessageSender(new CompressionSplittedOutputStream(this.socketConnection.getMysqlOutput(), new CompressorStreamsFactory(this.compressionAlgorithm)));
/*  525 */       } catch (IOException e) {
/*  526 */         ExceptionFactory.createException(Messages.getString("Protocol.Compression.7"), e);
/*      */       } 
/*      */     } 
/*      */     
/*  530 */     initServerSession();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void configureTimeZone() {}
/*      */ 
/*      */ 
/*      */   
/*      */   public void initServerSession() {
/*  540 */     configureTimeZone();
/*      */     
/*  542 */     send(this.messageBuilder.buildSqlStatement("select @@mysqlx_max_allowed_packet"), 0);
/*      */     
/*  544 */     ColumnDefinition metadata = readMetadata();
/*  545 */     long count = ((Long)(new XProtocolRowInputStream(metadata, this, null)).next().getValue(0, (ValueFactory)new LongValueFactory(this.propertySet))).longValue();
/*  546 */     readQueryResult(new StatementExecuteOkBuilder());
/*  547 */     setMaxAllowedPacket((int)count);
/*      */   }
/*      */   
/*      */   public void readAuthenticateOk() {
/*      */     try {
/*  552 */       XMessage mess = (XMessage)this.reader.readMessage(null, 4);
/*  553 */       if (mess != null && mess.getNotices() != null) {
/*  554 */         for (Notice notice : mess.getNotices()) {
/*  555 */           if (notice instanceof Notice.XSessionStateChanged) {
/*  556 */             switch (((Notice.XSessionStateChanged)notice).getParamType().intValue()) {
/*      */               case 11:
/*  558 */                 getServerSession().getCapabilities().setThreadId(((Notice.XSessionStateChanged)notice).getValue().getVUnsignedInt());
/*      */             } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           }
/*      */         } 
/*      */       }
/*  569 */     } catch (IOException e) {
/*  570 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */   
/*      */   public byte[] readAuthenticateContinue() {
/*      */     try {
/*  576 */       MysqlxSession.AuthenticateContinue msg = (MysqlxSession.AuthenticateContinue)((XMessage)this.reader.readMessage(null, 3)).getMessage();
/*  577 */       byte[] data = msg.getAuthData().toByteArray();
/*  578 */       if (data.length != 20) {
/*  579 */         throw AssertionFailedException.shouldNotHappen("Salt length should be 20, but is " + data.length);
/*      */       }
/*  581 */       return data;
/*  582 */     } catch (IOException e) {
/*  583 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean hasMoreResults() {
/*      */     try {
/*  589 */       if (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 16) {
/*  590 */         this.reader.readMessage(null, 16);
/*  591 */         if (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 14)
/*      */         {
/*  593 */           return false;
/*      */         }
/*  595 */         return true;
/*      */       } 
/*  597 */       return false;
/*  598 */     } catch (IOException e) {
/*  599 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public <T extends com.mysql.cj.QueryResult> T readQueryResult(ResultBuilder<T> resultBuilder) {
/*      */     try {
/*  606 */       boolean done = false;
/*      */       
/*  608 */       while (!done) {
/*  609 */         XMessageHeader header = (XMessageHeader)this.reader.readHeader();
/*  610 */         XMessage mess = (XMessage)this.reader.readMessage(null, header);
/*      */         
/*  612 */         Class<? extends GeneratedMessageV3> msgClass = (Class)mess.getMessage().getClass();
/*      */         
/*  614 */         if (Mysqlx.Error.class.equals(msgClass)) {
/*  615 */           throw new XProtocolError((Mysqlx.Error)Mysqlx.Error.class.cast(mess.getMessage()));
/*      */         }
/*  617 */         if (!this.messageToProtocolEntityFactory.containsKey(msgClass)) {
/*  618 */           throw new WrongArgumentException("Unhandled msg class (" + msgClass + ") + msg=" + mess.getMessage());
/*      */         }
/*      */         
/*      */         List<Notice> notices;
/*  622 */         if ((notices = mess.getNotices()) != null) {
/*  623 */           notices.stream().forEach(resultBuilder::addProtocolEntity);
/*      */         }
/*  625 */         done = resultBuilder.addProtocolEntity((ProtocolEntity)((ProtocolEntityFactory)this.messageToProtocolEntityFactory.get(msgClass)).createFromMessage(mess));
/*      */       } 
/*      */       
/*  628 */       return (T)resultBuilder.build();
/*  629 */     } catch (IOException e) {
/*  630 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasResults() {
/*      */     try {
/*  641 */       return (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 12);
/*  642 */     } catch (IOException e) {
/*  643 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void drainRows() {
/*      */     try {
/*  652 */       while (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 13) {
/*  653 */         this.reader.readMessage(null, 13);
/*      */       }
/*  655 */     } catch (XProtocolError e) {
/*  656 */       this.currentResultStreamer = null;
/*  657 */       throw e;
/*  658 */     } catch (IOException e) {
/*  659 */       this.currentResultStreamer = null;
/*  660 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */   
/*      */   public ColumnDefinition readMetadata() {
/*  665 */     return readMetadata((Consumer<Notice>)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public ColumnDefinition readMetadata(Consumer<Notice> noticeConsumer) {
/*      */     try {
/*  671 */       List<MysqlxResultset.ColumnMetaData> fromServer = new LinkedList<>();
/*      */       while (true)
/*  673 */       { XMessage mess = (XMessage)this.reader.readMessage(null, 12); List<Notice> notices;
/*  674 */         if (noticeConsumer != null && (notices = mess.getNotices()) != null) {
/*  675 */           notices.stream().forEach(noticeConsumer::accept);
/*      */         }
/*  677 */         fromServer.add((MysqlxResultset.ColumnMetaData)mess.getMessage());
/*  678 */         if (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() != 12)
/*  679 */         { ArrayList<Field> metadata = new ArrayList<>(fromServer.size());
/*      */ 
/*      */           
/*  682 */           ProtocolEntityFactory<Field, XMessage> fieldFactory = (ProtocolEntityFactory<Field, XMessage>)this.messageToProtocolEntityFactory.get(MysqlxResultset.ColumnMetaData.class);
/*  683 */           fromServer.forEach(col -> metadata.add(fieldFactory.createFromMessage(new XMessage((Message)col))));
/*      */           
/*  685 */           return (ColumnDefinition)new DefaultColumnDefinition(metadata.<Field>toArray(new Field[0])); }  } 
/*  686 */     } catch (IOException e) {
/*  687 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ColumnDefinition readMetadata(Field f, Consumer<Notice> noticeConsumer) {
/*      */     try {
/*  694 */       List<MysqlxResultset.ColumnMetaData> fromServer = new LinkedList<>();
/*  695 */       while (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 12) {
/*  696 */         XMessage mess = (XMessage)this.reader.readMessage(null, 12); List<Notice> notices;
/*  697 */         if (noticeConsumer != null && (notices = mess.getNotices()) != null) {
/*  698 */           notices.stream().forEach(noticeConsumer::accept);
/*      */         }
/*  700 */         fromServer.add((MysqlxResultset.ColumnMetaData)mess.getMessage());
/*      */       } 
/*  702 */       ArrayList<Field> metadata = new ArrayList<>(fromServer.size());
/*  703 */       metadata.add(f);
/*      */ 
/*      */       
/*  706 */       ProtocolEntityFactory<Field, XMessage> fieldFactory = (ProtocolEntityFactory<Field, XMessage>)this.messageToProtocolEntityFactory.get(MysqlxResultset.ColumnMetaData.class);
/*  707 */       fromServer.forEach(col -> metadata.add(fieldFactory.createFromMessage(new XMessage((Message)col))));
/*      */       
/*  709 */       return (ColumnDefinition)new DefaultColumnDefinition(metadata.<Field>toArray(new Field[0]));
/*  710 */     } catch (IOException e) {
/*  711 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public XProtocolRow readRowOrNull(ColumnDefinition metadata, Consumer<Notice> noticeConsumer) {
/*      */     try {
/*  718 */       if (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 13) {
/*  719 */         XMessage mess = (XMessage)this.reader.readMessage(null, 13); List<Notice> notices;
/*  720 */         if (noticeConsumer != null && (notices = mess.getNotices()) != null) {
/*  721 */           notices.stream().forEach(noticeConsumer::accept);
/*      */         }
/*  723 */         XProtocolRow res = new XProtocolRow((MysqlxResultset.Row)mess.getMessage());
/*  724 */         res.setMetadata(metadata);
/*  725 */         return res;
/*      */       } 
/*  727 */       return null;
/*  728 */     } catch (XProtocolError e) {
/*  729 */       this.currentResultStreamer = null;
/*  730 */       throw e;
/*  731 */     } catch (IOException e) {
/*  732 */       this.currentResultStreamer = null;
/*  733 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsPreparedStatements() {
/*  744 */     return this.supportsPreparedStatements;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean readyForPreparingStatements() {
/*  754 */     if (this.retryPrepareStatementCountdown == 0) {
/*  755 */       return true;
/*      */     }
/*  757 */     this.retryPrepareStatementCountdown--;
/*  758 */     return false;
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
/*      */   public int getNewPreparedStatementId(PreparableStatement<?> preparableStatement) {
/*  771 */     if (!this.supportsPreparedStatements) {
/*  772 */       throw new XProtocolError("The connected MySQL server does not support prepared statements.");
/*      */     }
/*  774 */     int preparedStatementId = this.preparedStatementIds.allocateSequentialId();
/*  775 */     this.preparableStatementFinalizerReferences.put(Integer.valueOf(preparedStatementId), new PreparableStatement.PreparableStatementFinalizer(preparableStatement, this.preparableStatementRefQueue, preparedStatementId));
/*      */     
/*  777 */     return preparedStatementId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void freePreparedStatementId(int preparedStatementId) {
/*  788 */     if (!this.supportsPreparedStatements) {
/*  789 */       throw new XProtocolError("The connected MySQL server does not support prepared statements.");
/*      */     }
/*  791 */     this.preparedStatementIds.releaseSequentialId(preparedStatementId);
/*  792 */     this.preparableStatementFinalizerReferences.remove(Integer.valueOf(preparedStatementId));
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
/*      */   public boolean failedPreparingStatement(int preparedStatementId, XProtocolError e) {
/*  806 */     freePreparedStatementId(preparedStatementId);
/*      */     
/*  808 */     if (e.getErrorCode() == 1461) {
/*  809 */       this.retryPrepareStatementCountdown = RETRY_PREPARE_STATEMENT_COUNTDOWN;
/*  810 */       return true;
/*      */     } 
/*      */     
/*  813 */     if (e.getErrorCode() == 1047 && this.preparableStatementFinalizerReferences.isEmpty()) {
/*      */       
/*  815 */       this.supportsPreparedStatements = false;
/*  816 */       this.retryPrepareStatementCountdown = 0;
/*  817 */       this.preparedStatementIds = null;
/*  818 */       this.preparableStatementRefQueue = null;
/*  819 */       this.preparableStatementFinalizerReferences = null;
/*  820 */       return true;
/*      */     } 
/*      */     
/*  823 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void newCommand() {
/*  831 */     if (this.currentResultStreamer != null) {
/*      */       try {
/*  833 */         this.currentResultStreamer.finishStreaming();
/*      */       } finally {
/*      */         
/*  836 */         this.currentResultStreamer = null;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  841 */     if (this.supportsPreparedStatements) {
/*      */       Reference<? extends PreparableStatement<?>> ref;
/*  843 */       while ((ref = this.preparableStatementRefQueue.poll()) != null) {
/*  844 */         PreparableStatement.PreparableStatementFinalizer psf = (PreparableStatement.PreparableStatementFinalizer)ref;
/*  845 */         psf.clear();
/*      */         try {
/*  847 */           this.sender.send(((XMessageBuilder)this.messageBuilder).buildPrepareDeallocate(psf.getPreparedStatementId()));
/*  848 */           readQueryResult(new OkBuilder());
/*  849 */         } catch (XProtocolError e) {
/*  850 */           if (e.getErrorCode() != 5110) {
/*  851 */             throw e;
/*      */           }
/*      */         } finally {
/*  854 */           freePreparedStatementId(psf.getPreparedStatementId());
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public <M extends Message, R extends com.mysql.cj.QueryResult> R query(M message, ResultBuilder<R> resultBuilder) {
/*  861 */     send((Message)message, 0);
/*  862 */     R res = readQueryResult(resultBuilder);
/*  863 */     if (ResultStreamer.class.isAssignableFrom(res.getClass())) {
/*  864 */       this.currentResultStreamer = (ResultStreamer)res;
/*      */     }
/*  866 */     return res;
/*      */   }
/*      */   
/*      */   public <M extends Message, R extends com.mysql.cj.QueryResult> CompletableFuture<R> queryAsync(M message, ResultBuilder<R> resultBuilder) {
/*  870 */     newCommand();
/*  871 */     CompletableFuture<R> f = new CompletableFuture<>();
/*  872 */     MessageListener<XMessage> l = new ResultMessageListener<>(this.messageToProtocolEntityFactory, resultBuilder, f);
/*  873 */     this.sender.send((XMessage)message, f, () -> this.reader.pushMessageListener(l));
/*  874 */     return f;
/*      */   }
/*      */   
/*      */   public boolean isOpen() {
/*  878 */     return (this.managedResource != null);
/*      */   }
/*      */   
/*      */   public void close() throws IOException {
/*      */     
/*  883 */     try { send(this.messageBuilder.buildClose(), 0);
/*  884 */       readQueryResult(new OkBuilder()); }
/*  885 */     catch (Exception exception)
/*      */     
/*      */     { 
/*      */       
/*  889 */       try { if (this.managedResource == null) {
/*  890 */           throw new ConnectionIsClosedException();
/*      */         }
/*  892 */         this.managedResource.close();
/*  893 */         this.managedResource = null; }
/*  894 */       catch (IOException ex)
/*  895 */       { throw new CJCommunicationsException(ex); }  } finally { try { if (this.managedResource == null) throw new ConnectionIsClosedException();  this.managedResource.close(); this.managedResource = null; } catch (IOException ex) { throw new CJCommunicationsException(ex); }
/*      */        }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean isSqlResultPending() {
/*      */     try {
/*  902 */       switch (((SyncMessageReader)this.reader).getNextNonNoticeMessageType()) {
/*      */         case 12:
/*  904 */           return true;
/*      */         case 16:
/*  906 */           this.reader.readMessage(null, 16);
/*      */           break;
/*      */       } 
/*      */ 
/*      */       
/*  911 */       return false;
/*  912 */     } catch (IOException e) {
/*  913 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setMaxAllowedPacket(int maxAllowedPacket) {
/*  918 */     this.sender.setMaxAllowedPacket(maxAllowedPacket);
/*      */   }
/*      */ 
/*      */   
/*      */   public void send(Message message, int packetLen) {
/*  923 */     newCommand();
/*  924 */     this.sender.send(message);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ServerCapabilities readServerCapabilities() {
/*      */     try {
/*  936 */       this.sender.send(((XMessageBuilder)this.messageBuilder).buildCapabilitiesGet());
/*  937 */       return new XServerCapabilities((Map<String, MysqlxDatatypes.Any>)((MysqlxConnection.Capabilities)((XMessage)this.reader.readMessage(null, 2)).getMessage())
/*  938 */           .getCapabilitiesList().stream().collect(Collectors.toMap(MysqlxConnection.Capability::getName, MysqlxConnection.Capability::getValue)));
/*  939 */     } catch (IOException|AssertionFailedException e) {
/*  940 */       throw new XProtocolError(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void reset() {
/*  946 */     newCommand();
/*  947 */     this.propertySet.reset();
/*      */     
/*  949 */     if (this.useSessionResetKeepOpen == null) {
/*      */       try {
/*  951 */         send(((XMessageBuilder)this.messageBuilder).buildExpectOpen(), 0);
/*  952 */         readQueryResult(new OkBuilder());
/*  953 */         this.useSessionResetKeepOpen = Boolean.valueOf(true);
/*  954 */       } catch (XProtocolError e) {
/*  955 */         if (e.getErrorCode() != 5168 && e
/*  956 */           .getErrorCode() != 5160) {
/*  957 */           throw e;
/*      */         }
/*  959 */         this.useSessionResetKeepOpen = Boolean.valueOf(false);
/*      */       } 
/*      */     }
/*  962 */     if (this.useSessionResetKeepOpen.booleanValue()) {
/*  963 */       send(((XMessageBuilder)this.messageBuilder).buildSessionResetKeepOpen(), 0);
/*  964 */       readQueryResult(new OkBuilder());
/*      */     } else {
/*  966 */       send(((XMessageBuilder)this.messageBuilder).buildSessionResetAndClose(), 0);
/*  967 */       readQueryResult(new OkBuilder());
/*      */       
/*  969 */       if (this.clientCapabilities.containsKey(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS)) {
/*      */         
/*  971 */         Map<String, Object> reducedClientCapabilities = new HashMap<>();
/*  972 */         reducedClientCapabilities.put(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS, this.clientCapabilities
/*  973 */             .get(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS));
/*  974 */         if (reducedClientCapabilities.size() > 0) {
/*  975 */           sendCapabilities(reducedClientCapabilities);
/*      */         }
/*      */       } 
/*      */       
/*  979 */       this.authProvider.changeUser(this.currUser, this.currPassword, this.currDatabase);
/*      */     } 
/*      */ 
/*      */     
/*  983 */     if (this.supportsPreparedStatements) {
/*  984 */       this.retryPrepareStatementCountdown = 0;
/*  985 */       this.preparedStatementIds = new SequentialIdLease();
/*  986 */       this.preparableStatementRefQueue = new ReferenceQueue<>();
/*  987 */       this.preparableStatementFinalizerReferences = new TreeMap<>();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ExceptionInterceptor getExceptionInterceptor() {
/*  993 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */   
/*      */   public void changeDatabase(String database) {
/*  997 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor) {
/* 1003 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */   
/*      */   public XMessage readMessage(XMessage reuse) {
/* 1008 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */   
/*      */   public XMessage checkErrorMessage() {
/* 1013 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */   
/*      */   public XMessage sendCommand(Message queryPacket, boolean skipCheck, int timeoutMillis) {
/* 1018 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */   
/*      */   public <T extends ProtocolEntity> T read(Class<T> requiredClass, ProtocolEntityFactory<T, XMessage> protocolEntityFactory) throws IOException {
/* 1023 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T extends ProtocolEntity> T read(Class<Resultset> requiredClass, int maxRows, boolean streamResults, XMessage resultPacket, boolean isBinaryEncoded, ColumnDefinition metadata, ProtocolEntityFactory<T, XMessage> protocolEntityFactory) throws IOException {
/* 1029 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */   
/*      */   public void setLocalInfileInputStream(InputStream stream) {
/* 1034 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */   
/*      */   public InputStream getLocalInfileInputStream() {
/* 1039 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */   
/*      */   public String getQueryComment() {
/* 1044 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ 
/*      */   
/*      */   public void setQueryComment(String comment) {
/* 1049 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XProtocol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */