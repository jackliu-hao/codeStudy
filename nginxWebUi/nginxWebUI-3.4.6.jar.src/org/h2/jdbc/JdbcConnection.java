/*      */ package org.h2.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.NClob;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLClientInfoException;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Struct;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.Properties;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.regex.Pattern;
/*      */ import org.h2.api.JavaObjectSerializer;
/*      */ import org.h2.command.CommandInterface;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.engine.ConnectionInfo;
/*      */ import org.h2.engine.IsolationLevel;
/*      */ import org.h2.engine.Mode;
/*      */ import org.h2.engine.Session;
/*      */ import org.h2.engine.SessionRemote;
/*      */ import org.h2.engine.SysProperties;
/*      */ import org.h2.expression.ParameterInterface;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.message.TraceObject;
/*      */ import org.h2.result.ResultInterface;
/*      */ import org.h2.util.CloseWatcher;
/*      */ import org.h2.util.TimeZoneProvider;
/*      */ import org.h2.value.CompareMode;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueLob;
/*      */ import org.h2.value.ValueNull;
/*      */ import org.h2.value.ValueTimestampTimeZone;
/*      */ import org.h2.value.ValueToObjectConverter;
/*      */ import org.h2.value.ValueVarbinary;
/*      */ import org.h2.value.ValueVarchar;
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
/*      */ public class JdbcConnection
/*      */   extends TraceObject
/*      */   implements Connection, JdbcConnectionBackwardsCompat, CastDataProvider
/*      */ {
/*      */   private static final String NUM_SERVERS = "numServers";
/*      */   private static final String PREFIX_SERVER = "server";
/*      */   private static boolean keepOpenStackTrace;
/*      */   private final String url;
/*      */   private final String user;
/*   83 */   private int holdability = 1;
/*      */   
/*      */   private Session session;
/*      */   
/*      */   private CommandInterface commit;
/*      */   
/*      */   private CommandInterface rollback;
/*      */   
/*      */   private CommandInterface getReadOnly;
/*      */   
/*      */   private CommandInterface getGeneratedKeys;
/*   94 */   private int queryTimeoutCache = -1;
/*      */   
/*      */   private CommandInterface setQueryTimeout;
/*      */   
/*      */   private CommandInterface getQueryTimeout;
/*      */   
/*      */   private int savepointId;
/*      */   
/*      */   private String catalog;
/*      */   
/*      */   private Statement executingStatement;
/*      */   
/*      */   private final CloseWatcher watcher;
/*      */   
/*      */   private Map<String, String> clientInfo;
/*      */ 
/*      */   
/*      */   public JdbcConnection(String paramString1, Properties paramProperties, String paramString2, Object paramObject, boolean paramBoolean) throws SQLException {
/*      */     try {
/*  113 */       ConnectionInfo connectionInfo = new ConnectionInfo(paramString1, paramProperties, paramString2, paramObject);
/*  114 */       if (paramBoolean) {
/*  115 */         connectionInfo.setProperty("FORBID_CREATION", "TRUE");
/*      */       }
/*  117 */       String str = SysProperties.getBaseDir();
/*  118 */       if (str != null) {
/*  119 */         connectionInfo.setBaseDir(str);
/*      */       }
/*      */       
/*  122 */       this.session = (new SessionRemote(connectionInfo)).connectEmbeddedOrServer(false);
/*  123 */       setTrace(this.session.getTrace(), 1, getNextId(1));
/*  124 */       this.user = connectionInfo.getUserName();
/*  125 */       if (isInfoEnabled()) {
/*  126 */         this.trace.infoCode("Connection " + getTraceObjectName() + " = DriverManager.getConnection(" + 
/*      */             
/*  128 */             quote(connectionInfo.getOriginalURL()) + ", " + quote(this.user) + ", \"\");");
/*      */       }
/*      */       
/*  131 */       this.url = connectionInfo.getURL();
/*  132 */       closeOld();
/*  133 */       this.watcher = CloseWatcher.register(this, (AutoCloseable)this.session, keepOpenStackTrace);
/*  134 */     } catch (Exception exception) {
/*  135 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JdbcConnection(JdbcConnection paramJdbcConnection) {
/*  144 */     this.session = paramJdbcConnection.session;
/*  145 */     setTrace(this.session.getTrace(), 1, getNextId(1));
/*  146 */     this.user = paramJdbcConnection.user;
/*  147 */     this.url = paramJdbcConnection.url;
/*  148 */     this.catalog = paramJdbcConnection.catalog;
/*  149 */     this.commit = paramJdbcConnection.commit;
/*  150 */     this.getGeneratedKeys = paramJdbcConnection.getGeneratedKeys;
/*  151 */     this.getQueryTimeout = paramJdbcConnection.getQueryTimeout;
/*  152 */     this.getReadOnly = paramJdbcConnection.getReadOnly;
/*  153 */     this.rollback = paramJdbcConnection.rollback;
/*  154 */     this.watcher = null;
/*  155 */     if (paramJdbcConnection.clientInfo != null) {
/*  156 */       this.clientInfo = new HashMap<>(paramJdbcConnection.clientInfo);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JdbcConnection(Session paramSession, String paramString1, String paramString2) {
/*  167 */     this.session = paramSession;
/*  168 */     setTrace(paramSession.getTrace(), 1, getNextId(1));
/*  169 */     this.user = paramString1;
/*  170 */     this.url = paramString2;
/*  171 */     this.watcher = null;
/*      */   }
/*      */   
/*      */   private void closeOld() {
/*      */     while (true) {
/*  176 */       CloseWatcher closeWatcher = CloseWatcher.pollUnclosed();
/*  177 */       if (closeWatcher == null) {
/*      */         break;
/*      */       }
/*      */       try {
/*  181 */         closeWatcher.getCloseable().close();
/*  182 */       } catch (Exception exception) {
/*  183 */         this.trace.error(exception, "closing session");
/*      */       } 
/*      */ 
/*      */       
/*  187 */       keepOpenStackTrace = true;
/*  188 */       String str = closeWatcher.getOpenStackTrace();
/*      */       
/*  190 */       DbException dbException = DbException.get(90018);
/*  191 */       this.trace.error((Throwable)dbException, str);
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
/*      */   public Statement createStatement() throws SQLException {
/*      */     try {
/*  204 */       int i = getNextId(8);
/*  205 */       debugCodeAssign("Statement", 8, i, "createStatement()");
/*  206 */       checkClosed();
/*  207 */       return new JdbcStatement(this, i, 1003, 1007);
/*  208 */     } catch (Exception exception) {
/*  209 */       throw logAndConvert(exception);
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
/*      */   public Statement createStatement(int paramInt1, int paramInt2) throws SQLException {
/*      */     try {
/*  226 */       int i = getNextId(8);
/*  227 */       if (isDebugEnabled()) {
/*  228 */         debugCodeAssign("Statement", 8, i, "createStatement(" + paramInt1 + ", " + paramInt2 + ')');
/*      */       }
/*      */       
/*  231 */       checkTypeConcurrency(paramInt1, paramInt2);
/*  232 */       checkClosed();
/*  233 */       return new JdbcStatement(this, i, paramInt1, paramInt2);
/*  234 */     } catch (Exception exception) {
/*  235 */       throw logAndConvert(exception);
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
/*      */   public Statement createStatement(int paramInt1, int paramInt2, int paramInt3) throws SQLException {
/*      */     try {
/*  255 */       int i = getNextId(8);
/*  256 */       if (isDebugEnabled()) {
/*  257 */         debugCodeAssign("Statement", 8, i, "createStatement(" + paramInt1 + ", " + paramInt2 + ", " + paramInt3 + ')');
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  262 */       checkTypeConcurrency(paramInt1, paramInt2);
/*  263 */       checkHoldability(paramInt3);
/*  264 */       checkClosed();
/*  265 */       return new JdbcStatement(this, i, paramInt1, paramInt2);
/*  266 */     } catch (Exception exception) {
/*  267 */       throw logAndConvert(exception);
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
/*      */   public PreparedStatement prepareStatement(String paramString) throws SQLException {
/*      */     try {
/*  281 */       int i = getNextId(3);
/*  282 */       if (isDebugEnabled()) {
/*  283 */         debugCodeAssign("PreparedStatement", 3, i, "prepareStatement(" + 
/*  284 */             quote(paramString) + ')');
/*      */       }
/*  286 */       checkClosed();
/*  287 */       paramString = translateSQL(paramString);
/*  288 */       return new JdbcPreparedStatement(this, paramString, i, 1003, 1007, null);
/*      */     }
/*  290 */     catch (Exception exception) {
/*  291 */       throw logAndConvert(exception);
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
/*      */   public DatabaseMetaData getMetaData() throws SQLException {
/*      */     try {
/*  304 */       int i = getNextId(2);
/*  305 */       debugCodeAssign("DatabaseMetaData", 2, i, "getMetaData()");
/*  306 */       checkClosed();
/*  307 */       return new JdbcDatabaseMetaData(this, this.trace, i);
/*  308 */     } catch (Exception exception) {
/*  309 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Session getSession() {
/*  318 */     return this.session;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void close() throws SQLException {
/*      */     try {
/*  330 */       debugCodeCall("close");
/*  331 */       if (this.session == null) {
/*      */         return;
/*      */       }
/*  334 */       CloseWatcher.unregister(this.watcher);
/*  335 */       this.session.cancel();
/*  336 */       synchronized (this.session) {
/*  337 */         if (this.executingStatement != null) {
/*      */           try {
/*  339 */             this.executingStatement.cancel();
/*  340 */           } catch (NullPointerException|SQLException nullPointerException) {}
/*      */         }
/*      */ 
/*      */         
/*      */         try {
/*  345 */           if (!this.session.isClosed()) {
/*      */             try {
/*  347 */               if (this.session.hasPendingTransaction()) {
/*      */                 try {
/*  349 */                   rollbackInternal();
/*  350 */                 } catch (DbException dbException) {
/*      */                   
/*  352 */                   if (dbException.getErrorCode() != 90067 && dbException
/*  353 */                     .getErrorCode() != 90098) {
/*  354 */                     throw dbException;
/*      */                   }
/*      */                 } 
/*      */               }
/*  358 */               closePreparedCommands();
/*      */             } finally {
/*  360 */               this.session.close();
/*      */             } 
/*      */           }
/*      */         } finally {
/*  364 */           this.session = null;
/*      */         } 
/*      */       } 
/*  367 */     } catch (Throwable throwable) {
/*  368 */       throw logAndConvert(throwable);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void closePreparedCommands() {
/*  373 */     this.commit = closeAndSetNull(this.commit);
/*  374 */     this.rollback = closeAndSetNull(this.rollback);
/*  375 */     this.getReadOnly = closeAndSetNull(this.getReadOnly);
/*  376 */     this.getGeneratedKeys = closeAndSetNull(this.getGeneratedKeys);
/*  377 */     this.getQueryTimeout = closeAndSetNull(this.getQueryTimeout);
/*  378 */     this.setQueryTimeout = closeAndSetNull(this.setQueryTimeout);
/*      */   }
/*      */   
/*      */   private static CommandInterface closeAndSetNull(CommandInterface paramCommandInterface) {
/*  382 */     if (paramCommandInterface != null) {
/*  383 */       paramCommandInterface.close();
/*      */     }
/*  385 */     return null;
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
/*      */   public synchronized void setAutoCommit(boolean paramBoolean) throws SQLException {
/*      */     try {
/*  399 */       if (isDebugEnabled()) {
/*  400 */         debugCode("setAutoCommit(" + paramBoolean + ')');
/*      */       }
/*  402 */       checkClosed();
/*  403 */       synchronized (this.session) {
/*  404 */         if (paramBoolean && !this.session.getAutoCommit()) {
/*  405 */           commit();
/*      */         }
/*  407 */         this.session.setAutoCommit(paramBoolean);
/*      */       } 
/*  409 */     } catch (Exception exception) {
/*  410 */       throw logAndConvert(exception);
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
/*      */   public synchronized boolean getAutoCommit() throws SQLException {
/*      */     try {
/*  423 */       checkClosed();
/*  424 */       debugCodeCall("getAutoCommit");
/*  425 */       return this.session.getAutoCommit();
/*  426 */     } catch (Exception exception) {
/*  427 */       throw logAndConvert(exception);
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
/*      */   public synchronized void commit() throws SQLException {
/*      */     try {
/*  440 */       debugCodeCall("commit");
/*  441 */       checkClosed();
/*  442 */       if (SysProperties.FORCE_AUTOCOMMIT_OFF_ON_COMMIT && 
/*  443 */         getAutoCommit()) {
/*  444 */         throw DbException.get(90147, "commit()");
/*      */       }
/*  446 */       this.commit = prepareCommand("COMMIT", this.commit);
/*  447 */       this.commit.executeUpdate(null);
/*  448 */     } catch (Exception exception) {
/*  449 */       throw logAndConvert(exception);
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
/*      */   public synchronized void rollback() throws SQLException {
/*      */     try {
/*  462 */       debugCodeCall("rollback");
/*  463 */       checkClosed();
/*  464 */       if (SysProperties.FORCE_AUTOCOMMIT_OFF_ON_COMMIT && 
/*  465 */         getAutoCommit()) {
/*  466 */         throw DbException.get(90147, "rollback()");
/*      */       }
/*  468 */       rollbackInternal();
/*  469 */     } catch (Exception exception) {
/*  470 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClosed() throws SQLException {
/*      */     try {
/*  482 */       debugCodeCall("isClosed");
/*  483 */       return (this.session == null || this.session.isClosed());
/*  484 */     } catch (Exception exception) {
/*  485 */       throw logAndConvert(exception);
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
/*      */   public String nativeSQL(String paramString) throws SQLException {
/*      */     try {
/*  499 */       debugCodeCall("nativeSQL", paramString);
/*  500 */       checkClosed();
/*  501 */       return translateSQL(paramString);
/*  502 */     } catch (Exception exception) {
/*  503 */       throw logAndConvert(exception);
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
/*      */   public void setReadOnly(boolean paramBoolean) throws SQLException {
/*      */     try {
/*  517 */       if (isDebugEnabled()) {
/*  518 */         debugCode("setReadOnly(" + paramBoolean + ')');
/*      */       }
/*  520 */       checkClosed();
/*  521 */     } catch (Exception exception) {
/*  522 */       throw logAndConvert(exception);
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
/*      */   public boolean isReadOnly() throws SQLException {
/*      */     try {
/*  535 */       debugCodeCall("isReadOnly");
/*  536 */       checkClosed();
/*  537 */       this.getReadOnly = prepareCommand("CALL READONLY()", this.getReadOnly);
/*  538 */       ResultInterface resultInterface = this.getReadOnly.executeQuery(0L, false);
/*  539 */       resultInterface.next();
/*  540 */       return resultInterface.currentRow()[0].getBoolean();
/*  541 */     } catch (Exception exception) {
/*  542 */       throw logAndConvert(exception);
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
/*      */   public void setCatalog(String paramString) throws SQLException {
/*      */     try {
/*  555 */       debugCodeCall("setCatalog", paramString);
/*  556 */       checkClosed();
/*  557 */     } catch (Exception exception) {
/*  558 */       throw logAndConvert(exception);
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
/*      */   public String getCatalog() throws SQLException {
/*      */     try {
/*  571 */       debugCodeCall("getCatalog");
/*  572 */       checkClosed();
/*  573 */       if (this.catalog == null) {
/*  574 */         CommandInterface commandInterface = prepareCommand("CALL DATABASE()", 2147483647);
/*      */         
/*  576 */         ResultInterface resultInterface = commandInterface.executeQuery(0L, false);
/*  577 */         resultInterface.next();
/*  578 */         this.catalog = resultInterface.currentRow()[0].getString();
/*  579 */         commandInterface.close();
/*      */       } 
/*  581 */       return this.catalog;
/*  582 */     } catch (Exception exception) {
/*  583 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SQLWarning getWarnings() throws SQLException {
/*      */     try {
/*  595 */       debugCodeCall("getWarnings");
/*  596 */       checkClosed();
/*  597 */       return null;
/*  598 */     } catch (Exception exception) {
/*  599 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearWarnings() throws SQLException {
/*      */     try {
/*  609 */       debugCodeCall("clearWarnings");
/*  610 */       checkClosed();
/*  611 */     } catch (Exception exception) {
/*  612 */       throw logAndConvert(exception);
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
/*      */   public PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2) throws SQLException {
/*      */     try {
/*  631 */       int i = getNextId(3);
/*  632 */       if (isDebugEnabled()) {
/*  633 */         debugCodeAssign("PreparedStatement", 3, i, "prepareStatement(" + 
/*  634 */             quote(paramString) + ", " + paramInt1 + ", " + paramInt2 + ')');
/*      */       }
/*  636 */       checkTypeConcurrency(paramInt1, paramInt2);
/*  637 */       checkClosed();
/*  638 */       paramString = translateSQL(paramString);
/*  639 */       return new JdbcPreparedStatement(this, paramString, i, paramInt1, paramInt2, null);
/*  640 */     } catch (Exception exception) {
/*  641 */       throw logAndConvert(exception);
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
/*      */   public void setTransactionIsolation(int paramInt) throws SQLException {
/*      */     try {
/*  662 */       debugCodeCall("setTransactionIsolation", paramInt);
/*  663 */       checkClosed();
/*  664 */       if (!getAutoCommit()) {
/*  665 */         commit();
/*      */       }
/*  667 */       this.session.setIsolationLevel(IsolationLevel.fromJdbc(paramInt));
/*  668 */     } catch (Exception exception) {
/*  669 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setQueryTimeout(int paramInt) throws SQLException {
/*      */     try {
/*  678 */       debugCodeCall("setQueryTimeout", paramInt);
/*  679 */       checkClosed();
/*  680 */       this.setQueryTimeout = prepareCommand("SET QUERY_TIMEOUT ?", this.setQueryTimeout);
/*      */       
/*  682 */       ((ParameterInterface)this.setQueryTimeout.getParameters().get(0))
/*  683 */         .setValue((Value)ValueInteger.get(paramInt * 1000), false);
/*  684 */       this.setQueryTimeout.executeUpdate(null);
/*  685 */       this.queryTimeoutCache = paramInt;
/*  686 */     } catch (Exception exception) {
/*  687 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getQueryTimeout() throws SQLException {
/*      */     try {
/*  696 */       if (this.queryTimeoutCache == -1) {
/*  697 */         checkClosed();
/*  698 */         this.getQueryTimeout = prepareCommand(!this.session.isOldInformationSchema() ? "SELECT SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE SETTING_NAME=?" : "SELECT `VALUE` FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME=?", this.getQueryTimeout);
/*      */ 
/*      */         
/*  701 */         ((ParameterInterface)this.getQueryTimeout.getParameters().get(0))
/*  702 */           .setValue(ValueVarchar.get("QUERY_TIMEOUT"), false);
/*  703 */         ResultInterface resultInterface = this.getQueryTimeout.executeQuery(0L, false);
/*  704 */         resultInterface.next();
/*  705 */         int i = resultInterface.currentRow()[0].getInt();
/*  706 */         resultInterface.close();
/*  707 */         if (i != 0)
/*      */         {
/*      */           
/*  710 */           i = (i + 999) / 1000;
/*      */         }
/*  712 */         this.queryTimeoutCache = i;
/*      */       } 
/*  714 */       return this.queryTimeoutCache;
/*  715 */     } catch (Exception exception) {
/*  716 */       throw logAndConvert(exception);
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
/*      */   public int getTransactionIsolation() throws SQLException {
/*      */     try {
/*  729 */       debugCodeCall("getTransactionIsolation");
/*  730 */       checkClosed();
/*  731 */       return this.session.getIsolationLevel().getJdbc();
/*  732 */     } catch (Exception exception) {
/*  733 */       throw logAndConvert(exception);
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
/*      */   public void setHoldability(int paramInt) throws SQLException {
/*      */     try {
/*  748 */       debugCodeCall("setHoldability", paramInt);
/*  749 */       checkClosed();
/*  750 */       checkHoldability(paramInt);
/*  751 */       this.holdability = paramInt;
/*  752 */     } catch (Exception exception) {
/*  753 */       throw logAndConvert(exception);
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
/*      */   public int getHoldability() throws SQLException {
/*      */     try {
/*  766 */       debugCodeCall("getHoldability");
/*  767 */       checkClosed();
/*  768 */       return this.holdability;
/*  769 */     } catch (Exception exception) {
/*  770 */       throw logAndConvert(exception);
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
/*      */   public Map<String, Class<?>> getTypeMap() throws SQLException {
/*      */     try {
/*  783 */       debugCodeCall("getTypeMap");
/*  784 */       checkClosed();
/*  785 */       return null;
/*  786 */     } catch (Exception exception) {
/*  787 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTypeMap(Map<String, Class<?>> paramMap) throws SQLException {
/*      */     try {
/*  798 */       if (isDebugEnabled()) {
/*  799 */         debugCode("setTypeMap(" + quoteMap(paramMap) + ')');
/*      */       }
/*  801 */       checkMap(paramMap);
/*  802 */     } catch (Exception exception) {
/*  803 */       throw logAndConvert(exception);
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
/*      */   public CallableStatement prepareCall(String paramString) throws SQLException {
/*      */     try {
/*  818 */       int i = getNextId(0);
/*  819 */       if (isDebugEnabled()) {
/*  820 */         debugCodeAssign("CallableStatement", 0, i, "prepareCall(" + 
/*  821 */             quote(paramString) + ')');
/*      */       }
/*  823 */       checkClosed();
/*  824 */       paramString = translateSQL(paramString);
/*  825 */       return new JdbcCallableStatement(this, paramString, i, 1003, 1007);
/*      */     
/*      */     }
/*  828 */     catch (Exception exception) {
/*  829 */       throw logAndConvert(exception);
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
/*      */   public CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2) throws SQLException {
/*      */     try {
/*  848 */       int i = getNextId(0);
/*  849 */       if (isDebugEnabled()) {
/*  850 */         debugCodeAssign("CallableStatement", 0, i, "prepareCall(" + 
/*  851 */             quote(paramString) + ", " + paramInt1 + ", " + paramInt2 + ')');
/*      */       }
/*  853 */       checkTypeConcurrency(paramInt1, paramInt2);
/*  854 */       checkClosed();
/*  855 */       paramString = translateSQL(paramString);
/*  856 */       return new JdbcCallableStatement(this, paramString, i, paramInt1, paramInt2);
/*      */     }
/*  858 */     catch (Exception exception) {
/*  859 */       throw logAndConvert(exception);
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
/*      */   public CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2, int paramInt3) throws SQLException {
/*      */     try {
/*  880 */       int i = getNextId(0);
/*  881 */       if (isDebugEnabled()) {
/*  882 */         debugCodeAssign("CallableStatement", 0, i, "prepareCall(" + 
/*  883 */             quote(paramString) + ", " + paramInt1 + ", " + paramInt2 + ", " + paramInt3 + ')');
/*      */       }
/*      */       
/*  886 */       checkTypeConcurrency(paramInt1, paramInt2);
/*  887 */       checkHoldability(paramInt3);
/*  888 */       checkClosed();
/*  889 */       paramString = translateSQL(paramString);
/*  890 */       return new JdbcCallableStatement(this, paramString, i, paramInt1, paramInt2);
/*      */     }
/*  892 */     catch (Exception exception) {
/*  893 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Savepoint setSavepoint() throws SQLException {
/*      */     try {
/*  905 */       int i = getNextId(6);
/*  906 */       debugCodeAssign("Savepoint", 6, i, "setSavepoint()");
/*  907 */       checkClosed();
/*  908 */       CommandInterface commandInterface = prepareCommand("SAVEPOINT " + 
/*  909 */           JdbcSavepoint.getName((String)null, this.savepointId), 2147483647);
/*      */       
/*  911 */       commandInterface.executeUpdate(null);
/*  912 */       JdbcSavepoint jdbcSavepoint = new JdbcSavepoint(this, this.savepointId, null, this.trace, i);
/*      */       
/*  914 */       this.savepointId++;
/*  915 */       return jdbcSavepoint;
/*  916 */     } catch (Exception exception) {
/*  917 */       throw logAndConvert(exception);
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
/*      */   public Savepoint setSavepoint(String paramString) throws SQLException {
/*      */     try {
/*  930 */       int i = getNextId(6);
/*  931 */       if (isDebugEnabled()) {
/*  932 */         debugCodeAssign("Savepoint", 6, i, "setSavepoint(" + quote(paramString) + ')');
/*      */       }
/*  934 */       checkClosed();
/*  935 */       CommandInterface commandInterface = prepareCommand("SAVEPOINT " + 
/*  936 */           JdbcSavepoint.getName(paramString, 0), 2147483647);
/*      */       
/*  938 */       commandInterface.executeUpdate(null);
/*  939 */       return new JdbcSavepoint(this, 0, paramString, this.trace, i);
/*  940 */     } catch (Exception exception) {
/*  941 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void rollback(Savepoint paramSavepoint) throws SQLException {
/*      */     try {
/*  953 */       JdbcSavepoint jdbcSavepoint = convertSavepoint(paramSavepoint);
/*  954 */       if (isDebugEnabled()) {
/*  955 */         debugCode("rollback(" + jdbcSavepoint.getTraceObjectName() + ')');
/*      */       }
/*  957 */       checkClosed();
/*  958 */       jdbcSavepoint.rollback();
/*  959 */     } catch (Exception exception) {
/*  960 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void releaseSavepoint(Savepoint paramSavepoint) throws SQLException {
/*      */     try {
/*  972 */       debugCode("releaseSavepoint(savepoint)");
/*  973 */       checkClosed();
/*  974 */       convertSavepoint(paramSavepoint).release();
/*  975 */     } catch (Exception exception) {
/*  976 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private static JdbcSavepoint convertSavepoint(Savepoint paramSavepoint) {
/*  981 */     if (!(paramSavepoint instanceof JdbcSavepoint)) {
/*  982 */       throw DbException.get(90063, 
/*  983 */           String.valueOf(paramSavepoint));
/*      */     }
/*  985 */     return (JdbcSavepoint)paramSavepoint;
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
/*      */   public PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3) throws SQLException {
/*      */     try {
/* 1005 */       int i = getNextId(3);
/* 1006 */       if (isDebugEnabled()) {
/* 1007 */         debugCodeAssign("PreparedStatement", 3, i, "prepareStatement(" + 
/* 1008 */             quote(paramString) + ", " + paramInt1 + ", " + paramInt2 + ", " + paramInt3 + ')');
/*      */       }
/*      */       
/* 1011 */       checkTypeConcurrency(paramInt1, paramInt2);
/* 1012 */       checkHoldability(paramInt3);
/* 1013 */       checkClosed();
/* 1014 */       paramString = translateSQL(paramString);
/* 1015 */       return new JdbcPreparedStatement(this, paramString, i, paramInt1, paramInt2, null);
/* 1016 */     } catch (Exception exception) {
/* 1017 */       throw logAndConvert(exception);
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
/*      */   public PreparedStatement prepareStatement(String paramString, int paramInt) throws SQLException {
/*      */     try {
/* 1036 */       int i = getNextId(3);
/* 1037 */       if (isDebugEnabled()) {
/* 1038 */         debugCodeAssign("PreparedStatement", 3, i, "prepareStatement(" + 
/* 1039 */             quote(paramString) + ", " + paramInt + ')');
/*      */       }
/* 1041 */       checkClosed();
/* 1042 */       paramString = translateSQL(paramString);
/* 1043 */       return new JdbcPreparedStatement(this, paramString, i, 1003, 1007, 
/* 1044 */           Boolean.valueOf((paramInt == 1)));
/* 1045 */     } catch (Exception exception) {
/* 1046 */       throw logAndConvert(exception);
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
/*      */   public PreparedStatement prepareStatement(String paramString, int[] paramArrayOfint) throws SQLException {
/*      */     try {
/* 1064 */       int i = getNextId(3);
/* 1065 */       if (isDebugEnabled()) {
/* 1066 */         debugCodeAssign("PreparedStatement", 3, i, "prepareStatement(" + 
/* 1067 */             quote(paramString) + ", " + quoteIntArray(paramArrayOfint) + ')');
/*      */       }
/* 1069 */       checkClosed();
/* 1070 */       paramString = translateSQL(paramString);
/* 1071 */       return new JdbcPreparedStatement(this, paramString, i, 1003, 1007, paramArrayOfint);
/*      */     }
/* 1073 */     catch (Exception exception) {
/* 1074 */       throw logAndConvert(exception);
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
/*      */   public PreparedStatement prepareStatement(String paramString, String[] paramArrayOfString) throws SQLException {
/*      */     try {
/* 1092 */       int i = getNextId(3);
/* 1093 */       if (isDebugEnabled()) {
/* 1094 */         debugCodeAssign("PreparedStatement", 3, i, "prepareStatement(" + 
/* 1095 */             quote(paramString) + ", " + quoteArray(paramArrayOfString) + ')');
/*      */       }
/* 1097 */       checkClosed();
/* 1098 */       paramString = translateSQL(paramString);
/* 1099 */       return new JdbcPreparedStatement(this, paramString, i, 1003, 1007, paramArrayOfString);
/*      */     }
/* 1101 */     catch (Exception exception) {
/* 1102 */       throw logAndConvert(exception);
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
/*      */   CommandInterface prepareCommand(String paramString, int paramInt) {
/* 1116 */     return this.session.prepareCommand(paramString, paramInt);
/*      */   }
/*      */   
/*      */   private CommandInterface prepareCommand(String paramString, CommandInterface paramCommandInterface) {
/* 1120 */     return (paramCommandInterface == null) ? this.session.prepareCommand(paramString, 2147483647) : paramCommandInterface;
/*      */   }
/*      */ 
/*      */   
/*      */   private static int translateGetEnd(String paramString, int paramInt, char paramChar) {
/* 1125 */     int j, i = paramString.length();
/* 1126 */     switch (paramChar) {
/*      */       case '$':
/* 1128 */         if (paramInt < i - 1 && paramString.charAt(paramInt + 1) == '$' && (paramInt == 0 || paramString
/* 1129 */           .charAt(paramInt - 1) <= ' ')) {
/* 1130 */           int k = paramString.indexOf("$$", paramInt + 2);
/* 1131 */           if (k < 0) {
/* 1132 */             throw DbException.getSyntaxError(paramString, paramInt);
/*      */           }
/* 1134 */           return k + 1;
/*      */         } 
/* 1136 */         return paramInt;
/*      */       
/*      */       case '\'':
/* 1139 */         j = paramString.indexOf('\'', paramInt + 1);
/* 1140 */         if (j < 0) {
/* 1141 */           throw DbException.getSyntaxError(paramString, paramInt);
/*      */         }
/* 1143 */         return j;
/*      */       
/*      */       case '"':
/* 1146 */         j = paramString.indexOf('"', paramInt + 1);
/* 1147 */         if (j < 0) {
/* 1148 */           throw DbException.getSyntaxError(paramString, paramInt);
/*      */         }
/* 1150 */         return j;
/*      */       
/*      */       case '/':
/* 1153 */         checkRunOver(paramInt + 1, i, paramString);
/* 1154 */         if (paramString.charAt(paramInt + 1) == '*') {
/*      */           
/* 1156 */           j = paramString.indexOf("*/", paramInt + 2);
/* 1157 */           if (j < 0) {
/* 1158 */             throw DbException.getSyntaxError(paramString, paramInt);
/*      */           }
/* 1160 */           paramInt = j + 1;
/* 1161 */         } else if (paramString.charAt(paramInt + 1) == '/') {
/*      */           
/* 1163 */           paramInt += 2;
/* 1164 */           while (paramInt < i && (paramChar = paramString.charAt(paramInt)) != '\r' && paramChar != '\n') {
/* 1165 */             paramInt++;
/*      */           }
/*      */         } 
/* 1168 */         return paramInt;
/*      */       
/*      */       case '-':
/* 1171 */         checkRunOver(paramInt + 1, i, paramString);
/* 1172 */         if (paramString.charAt(paramInt + 1) == '-') {
/*      */           
/* 1174 */           paramInt += 2;
/* 1175 */           while (paramInt < i && (paramChar = paramString.charAt(paramInt)) != '\r' && paramChar != '\n') {
/* 1176 */             paramInt++;
/*      */           }
/*      */         } 
/* 1179 */         return paramInt;
/*      */     } 
/*      */     
/* 1182 */     throw DbException.getInternalError("c=" + paramChar);
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
/*      */   private static String translateSQL(String paramString) {
/* 1194 */     return translateSQL(paramString, true);
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
/*      */   static String translateSQL(String paramString, boolean paramBoolean) {
/* 1206 */     if (paramString == null) {
/* 1207 */       throw DbException.getInvalidValueException("SQL", null);
/*      */     }
/* 1209 */     if (!paramBoolean || paramString.indexOf('{') < 0) {
/* 1210 */       return paramString;
/*      */     }
/* 1212 */     return translateSQLImpl(paramString);
/*      */   }
/*      */   
/*      */   private static String translateSQLImpl(String paramString) {
/* 1216 */     int i = paramString.length();
/* 1217 */     char[] arrayOfChar = null;
/* 1218 */     byte b = 0;
/* 1219 */     for (int j = 0; j < i; j++) {
/* 1220 */       int k, m; char c = paramString.charAt(j);
/* 1221 */       switch (c) {
/*      */         case '"':
/*      */         case '\'':
/*      */         case '-':
/*      */         case '/':
/* 1226 */           j = translateGetEnd(paramString, j, c);
/*      */           break;
/*      */         case '{':
/* 1229 */           b++;
/* 1230 */           if (arrayOfChar == null) {
/* 1231 */             arrayOfChar = paramString.toCharArray();
/*      */           }
/* 1233 */           arrayOfChar[j] = ' ';
/* 1234 */           while (Character.isSpaceChar(arrayOfChar[j])) {
/* 1235 */             j++;
/* 1236 */             checkRunOver(j, i, paramString);
/*      */           } 
/* 1238 */           k = j;
/* 1239 */           if (arrayOfChar[j] >= '0' && arrayOfChar[j] <= '9') {
/* 1240 */             arrayOfChar[j - 1] = '{';
/*      */             while (true) {
/* 1242 */               checkRunOver(j, i, paramString);
/* 1243 */               c = arrayOfChar[j];
/* 1244 */               if (c == '}') {
/*      */                 break;
/*      */               }
/* 1247 */               switch (c) {
/*      */                 case '"':
/*      */                 case '\'':
/*      */                 case '-':
/*      */                 case '/':
/* 1252 */                   j = translateGetEnd(paramString, j, c);
/*      */                   break;
/*      */               } 
/*      */               
/* 1256 */               j++;
/*      */             } 
/* 1258 */             b--; break;
/*      */           } 
/* 1260 */           if (arrayOfChar[j] == '?') {
/* 1261 */             j++;
/* 1262 */             checkRunOver(j, i, paramString);
/* 1263 */             while (Character.isSpaceChar(arrayOfChar[j])) {
/* 1264 */               j++;
/* 1265 */               checkRunOver(j, i, paramString);
/*      */             } 
/* 1267 */             if (paramString.charAt(j) != '=') {
/* 1268 */               throw DbException.getSyntaxError(paramString, j, "=");
/*      */             }
/* 1270 */             j++;
/* 1271 */             checkRunOver(j, i, paramString);
/* 1272 */             while (Character.isSpaceChar(arrayOfChar[j])) {
/* 1273 */               j++;
/* 1274 */               checkRunOver(j, i, paramString);
/*      */             } 
/*      */           } 
/* 1277 */           while (!Character.isSpaceChar(arrayOfChar[j])) {
/* 1278 */             j++;
/* 1279 */             checkRunOver(j, i, paramString);
/*      */           } 
/* 1281 */           m = 0;
/* 1282 */           if (found(paramString, k, "fn"))
/* 1283 */           { m = 2; }
/* 1284 */           else { if (found(paramString, k, "escape"))
/*      */               break; 
/* 1286 */             if (found(paramString, k, "call"))
/*      */               break; 
/* 1288 */             if (found(paramString, k, "oj"))
/* 1289 */             { m = 2; }
/* 1290 */             else { if (found(paramString, k, "ts"))
/*      */                 break; 
/* 1292 */               if (found(paramString, k, "t"))
/*      */                 break; 
/* 1294 */               if (found(paramString, k, "d"))
/*      */                 break; 
/* 1296 */               if (found(paramString, k, "params"))
/* 1297 */                 m = "params".length();  }
/*      */              }
/* 1299 */            for (j = k; m > 0; j++, m--) {
/* 1300 */             arrayOfChar[j] = ' ';
/*      */           }
/*      */           break;
/*      */         case '}':
/* 1304 */           if (--b < 0) {
/* 1305 */             throw DbException.getSyntaxError(paramString, j);
/*      */           }
/* 1307 */           arrayOfChar[j] = ' ';
/*      */           break;
/*      */         case '$':
/* 1310 */           j = translateGetEnd(paramString, j, c);
/*      */           break;
/*      */       } 
/*      */     
/*      */     } 
/* 1315 */     if (b != 0) {
/* 1316 */       throw DbException.getSyntaxError(paramString, paramString.length() - 1);
/*      */     }
/* 1318 */     if (arrayOfChar != null) {
/* 1319 */       paramString = new String(arrayOfChar);
/*      */     }
/* 1321 */     return paramString;
/*      */   }
/*      */   
/*      */   private static void checkRunOver(int paramInt1, int paramInt2, String paramString) {
/* 1325 */     if (paramInt1 >= paramInt2) {
/* 1326 */       throw DbException.getSyntaxError(paramString, paramInt1);
/*      */     }
/*      */   }
/*      */   
/*      */   private static boolean found(String paramString1, int paramInt, String paramString2) {
/* 1331 */     return paramString1.regionMatches(true, paramInt, paramString2, 0, paramString2.length());
/*      */   }
/*      */ 
/*      */   
/*      */   private static void checkTypeConcurrency(int paramInt1, int paramInt2) {
/* 1336 */     switch (paramInt1) {
/*      */       case 1003:
/*      */       case 1004:
/*      */       case 1005:
/*      */         break;
/*      */       default:
/* 1342 */         throw DbException.getInvalidValueException("resultSetType", 
/* 1343 */             Integer.valueOf(paramInt1));
/*      */     } 
/* 1345 */     switch (paramInt2) {
/*      */       case 1007:
/*      */       case 1008:
/*      */         return;
/*      */     } 
/* 1350 */     throw DbException.getInvalidValueException("resultSetConcurrency", 
/* 1351 */         Integer.valueOf(paramInt2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void checkHoldability(int paramInt) {
/* 1358 */     if (paramInt != 1 && paramInt != 2)
/*      */     {
/* 1360 */       throw DbException.getInvalidValueException("resultSetHoldability", 
/* 1361 */           Integer.valueOf(paramInt));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkClosed() {
/* 1371 */     if (this.session == null) {
/* 1372 */       throw DbException.get(90007);
/*      */     }
/* 1374 */     if (this.session.isClosed()) {
/* 1375 */       throw DbException.get(90121);
/*      */     }
/*      */   }
/*      */   
/*      */   String getURL() {
/* 1380 */     checkClosed();
/* 1381 */     return this.url;
/*      */   }
/*      */   
/*      */   String getUser() {
/* 1385 */     checkClosed();
/* 1386 */     return this.user;
/*      */   }
/*      */   
/*      */   private void rollbackInternal() {
/* 1390 */     this.rollback = prepareCommand("ROLLBACK", this.rollback);
/* 1391 */     this.rollback.executeUpdate(null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setExecutingStatement(Statement paramStatement) {
/* 1398 */     this.executingStatement = paramStatement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Clob createClob() throws SQLException {
/*      */     try {
/* 1409 */       int i = getNextId(10);
/* 1410 */       debugCodeAssign("Clob", 10, i, "createClob()");
/* 1411 */       checkClosed();
/* 1412 */       return new JdbcClob(this, (Value)ValueVarchar.EMPTY, JdbcLob.State.NEW, i);
/* 1413 */     } catch (Exception exception) {
/* 1414 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Blob createBlob() throws SQLException {
/*      */     try {
/* 1426 */       int i = getNextId(9);
/* 1427 */       debugCodeAssign("Blob", 9, i, "createClob()");
/* 1428 */       checkClosed();
/* 1429 */       return new JdbcBlob(this, (Value)ValueVarbinary.EMPTY, JdbcLob.State.NEW, i);
/* 1430 */     } catch (Exception exception) {
/* 1431 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NClob createNClob() throws SQLException {
/*      */     try {
/* 1443 */       int i = getNextId(10);
/* 1444 */       debugCodeAssign("NClob", 10, i, "createNClob()");
/* 1445 */       checkClosed();
/* 1446 */       return new JdbcClob(this, (Value)ValueVarchar.EMPTY, JdbcLob.State.NEW, i);
/* 1447 */     } catch (Exception exception) {
/* 1448 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SQLXML createSQLXML() throws SQLException {
/*      */     try {
/* 1460 */       int i = getNextId(17);
/* 1461 */       debugCodeAssign("SQLXML", 17, i, "createSQLXML()");
/* 1462 */       checkClosed();
/* 1463 */       return new JdbcSQLXML(this, (Value)ValueVarchar.EMPTY, JdbcLob.State.NEW, i);
/* 1464 */     } catch (Exception exception) {
/* 1465 */       throw logAndConvert(exception);
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
/*      */   public Array createArrayOf(String paramString, Object[] paramArrayOfObject) throws SQLException {
/*      */     try {
/* 1480 */       int i = getNextId(16);
/* 1481 */       debugCodeAssign("Array", 16, i, "createArrayOf()");
/* 1482 */       checkClosed();
/* 1483 */       Value value = ValueToObjectConverter.objectToValue(this.session, paramArrayOfObject, 40);
/* 1484 */       return new JdbcArray(this, value, i);
/* 1485 */     } catch (Exception exception) {
/* 1486 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Struct createStruct(String paramString, Object[] paramArrayOfObject) throws SQLException {
/* 1496 */     throw unsupported("Struct");
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
/*      */   public synchronized boolean isValid(int paramInt) {
/*      */     try {
/* 1509 */       debugCodeCall("isValid", paramInt);
/* 1510 */       if (this.session == null || this.session.isClosed()) {
/* 1511 */         return false;
/*      */       }
/*      */       
/* 1514 */       getTransactionIsolation();
/* 1515 */       return true;
/* 1516 */     } catch (Exception exception) {
/*      */       
/* 1518 */       logAndConvert(exception);
/* 1519 */       return false;
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
/*      */   public void setClientInfo(String paramString1, String paramString2) throws SQLClientInfoException {
/*      */     try {
/* 1544 */       if (isDebugEnabled()) {
/* 1545 */         debugCode("setClientInfo(" + quote(paramString1) + ", " + quote(paramString2) + ')');
/*      */       }
/* 1547 */       checkClosed();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1552 */       if (Objects.equals(paramString2, getClientInfo(paramString1))) {
/*      */         return;
/*      */       }
/*      */       
/* 1556 */       if (isInternalProperty(paramString1)) {
/* 1557 */         throw new SQLClientInfoException("Property name '" + paramString1 + " is used internally by H2.", 
/*      */             
/* 1559 */             Collections.emptyMap());
/*      */       }
/*      */       
/* 1562 */       Pattern pattern = (getMode()).supportedClientInfoPropertiesRegEx;
/*      */       
/* 1564 */       if (pattern != null && pattern
/* 1565 */         .matcher(paramString1).matches()) {
/* 1566 */         if (this.clientInfo == null) {
/* 1567 */           this.clientInfo = new HashMap<>();
/*      */         }
/* 1569 */         this.clientInfo.put(paramString1, paramString2);
/*      */       } else {
/* 1571 */         throw new SQLClientInfoException("Client info name '" + paramString1 + "' not supported.", 
/*      */             
/* 1573 */             Collections.emptyMap());
/*      */       } 
/* 1575 */     } catch (Exception exception) {
/* 1576 */       throw convertToClientInfoException(logAndConvert(exception));
/*      */     } 
/*      */   }
/*      */   
/*      */   private static boolean isInternalProperty(String paramString) {
/* 1581 */     return ("numServers".equals(paramString) || paramString.startsWith("server"));
/*      */   }
/*      */ 
/*      */   
/*      */   private static SQLClientInfoException convertToClientInfoException(SQLException paramSQLException) {
/* 1586 */     if (paramSQLException instanceof SQLClientInfoException) {
/* 1587 */       return (SQLClientInfoException)paramSQLException;
/*      */     }
/* 1589 */     return new SQLClientInfoException(paramSQLException.getMessage(), paramSQLException.getSQLState(), paramSQLException
/* 1590 */         .getErrorCode(), null, null);
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
/*      */   public void setClientInfo(Properties paramProperties) throws SQLClientInfoException {
/*      */     try {
/* 1605 */       if (isDebugEnabled()) {
/* 1606 */         debugCode("setClientInfo(properties)");
/*      */       }
/* 1608 */       checkClosed();
/* 1609 */       if (this.clientInfo == null) {
/* 1610 */         this.clientInfo = new HashMap<>();
/*      */       } else {
/* 1612 */         this.clientInfo.clear();
/*      */       } 
/* 1614 */       for (Map.Entry<Object, Object> entry : paramProperties.entrySet()) {
/* 1615 */         setClientInfo((String)entry.getKey(), (String)entry
/* 1616 */             .getValue());
/*      */       }
/* 1618 */     } catch (Exception exception) {
/* 1619 */       throw convertToClientInfoException(logAndConvert(exception));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Properties getClientInfo() throws SQLException {
/*      */     try {
/* 1631 */       debugCodeCall("getClientInfo");
/* 1632 */       checkClosed();
/* 1633 */       ArrayList<String> arrayList = this.session.getClusterServers();
/* 1634 */       Properties properties = new Properties();
/*      */       
/* 1636 */       if (this.clientInfo != null) {
/* 1637 */         for (Map.Entry<String, String> entry : this.clientInfo.entrySet()) {
/* 1638 */           properties.setProperty((String)entry.getKey(), (String)entry.getValue());
/*      */         }
/*      */       }
/*      */       
/* 1642 */       properties.setProperty("numServers", Integer.toString(arrayList.size()));
/* 1643 */       for (byte b = 0; b < arrayList.size(); b++) {
/* 1644 */         properties.setProperty("server" + b, arrayList.get(b));
/*      */       }
/*      */       
/* 1647 */       return properties;
/* 1648 */     } catch (Exception exception) {
/* 1649 */       throw logAndConvert(exception);
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
/*      */   public String getClientInfo(String paramString) throws SQLException {
/*      */     try {
/* 1663 */       if (isDebugEnabled()) {
/* 1664 */         debugCodeCall("getClientInfo", paramString);
/*      */       }
/* 1666 */       checkClosed();
/* 1667 */       if (paramString == null) {
/* 1668 */         throw DbException.getInvalidValueException("name", null);
/*      */       }
/* 1670 */       return getClientInfo().getProperty(paramString);
/* 1671 */     } catch (Exception exception) {
/* 1672 */       throw logAndConvert(exception);
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
/*      */   public <T> T unwrap(Class<T> paramClass) throws SQLException {
/*      */     try {
/* 1686 */       if (isWrapperFor(paramClass)) {
/* 1687 */         return (T)this;
/*      */       }
/* 1689 */       throw DbException.getInvalidValueException("iface", paramClass);
/* 1690 */     } catch (Exception exception) {
/* 1691 */       throw logAndConvert(exception);
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
/*      */   public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
/* 1703 */     return (paramClass != null && paramClass.isAssignableFrom(getClass()));
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
/*      */   Value createClob(Reader paramReader, long paramLong) {
/* 1715 */     if (paramReader == null) {
/* 1716 */       return (Value)ValueNull.INSTANCE;
/*      */     }
/* 1718 */     if (paramLong <= 0L) {
/* 1719 */       paramLong = -1L;
/*      */     }
/* 1721 */     return (Value)this.session.addTemporaryLob((ValueLob)this.session.getDataHandler().getLobStorage().createClob(paramReader, paramLong));
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
/*      */   Value createBlob(InputStream paramInputStream, long paramLong) {
/* 1733 */     if (paramInputStream == null) {
/* 1734 */       return (Value)ValueNull.INSTANCE;
/*      */     }
/* 1736 */     if (paramLong <= 0L) {
/* 1737 */       paramLong = -1L;
/*      */     }
/* 1739 */     return (Value)this.session.addTemporaryLob((ValueLob)this.session.getDataHandler().getLobStorage().createBlob(paramInputStream, paramLong));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSchema(String paramString) throws SQLException {
/*      */     try {
/* 1751 */       if (isDebugEnabled()) {
/* 1752 */         debugCodeCall("setSchema", paramString);
/*      */       }
/* 1754 */       checkClosed();
/* 1755 */       this.session.setCurrentSchemaName(paramString);
/* 1756 */     } catch (Exception exception) {
/* 1757 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSchema() throws SQLException {
/*      */     try {
/* 1769 */       debugCodeCall("getSchema");
/* 1770 */       checkClosed();
/* 1771 */       return this.session.getCurrentSchemaName();
/* 1772 */     } catch (Exception exception) {
/* 1773 */       throw logAndConvert(exception);
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
/*      */   public void abort(Executor paramExecutor) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNetworkTimeout(Executor paramExecutor, int paramInt) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getNetworkTimeout() {
/* 1803 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static void checkMap(Map<String, Class<?>> paramMap) {
/* 1813 */     if (paramMap != null && paramMap.size() > 0) {
/* 1814 */       throw DbException.getUnsupportedException("map.size > 0");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1823 */     return getTraceObjectName() + ": url=" + this.url + " user=" + this.user;
/*      */   }
/*      */   
/*      */   CompareMode getCompareMode() {
/* 1827 */     return this.session.getDataHandler().getCompareMode();
/*      */   }
/*      */ 
/*      */   
/*      */   public Mode getMode() {
/* 1832 */     return this.session.getMode();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Session.StaticSettings getStaticSettings() {
/* 1840 */     checkClosed();
/* 1841 */     return this.session.getStaticSettings();
/*      */   }
/*      */ 
/*      */   
/*      */   public ValueTimestampTimeZone currentTimestamp() {
/* 1846 */     Session session = this.session;
/* 1847 */     if (session == null) {
/* 1848 */       throw DbException.get(90007);
/*      */     }
/* 1850 */     return session.currentTimestamp();
/*      */   }
/*      */ 
/*      */   
/*      */   public TimeZoneProvider currentTimeZone() {
/* 1855 */     Session session = this.session;
/* 1856 */     if (session == null) {
/* 1857 */       throw DbException.get(90007);
/*      */     }
/* 1859 */     return session.currentTimeZone();
/*      */   }
/*      */ 
/*      */   
/*      */   public JavaObjectSerializer getJavaObjectSerializer() {
/* 1864 */     Session session = this.session;
/* 1865 */     if (session == null) {
/* 1866 */       throw DbException.get(90007);
/*      */     }
/* 1868 */     return session.getJavaObjectSerializer();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean zeroBasedEnums() {
/* 1873 */     Session session = this.session;
/* 1874 */     if (session == null) {
/* 1875 */       throw DbException.get(90007);
/*      */     }
/* 1877 */     return session.zeroBasedEnums();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */