/*      */ package org.h2.jdbc;
/*      */ 
/*      */ import java.sql.Connection;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import java.util.ArrayList;
/*      */ import org.h2.command.CommandInterface;
/*      */ import org.h2.engine.Session;
/*      */ import org.h2.engine.SysProperties;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.message.TraceObject;
/*      */ import org.h2.result.ResultInterface;
/*      */ import org.h2.result.ResultWithGeneratedKeys;
/*      */ import org.h2.result.SimpleResult;
/*      */ import org.h2.util.ParserUtil;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.Utils;
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
/*      */ public class JdbcStatement
/*      */   extends TraceObject
/*      */   implements Statement, JdbcStatementBackwardsCompat
/*      */ {
/*      */   protected JdbcConnection conn;
/*      */   protected Session session;
/*      */   protected JdbcResultSet resultSet;
/*      */   protected long maxRows;
/*   54 */   protected int fetchSize = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
/*      */   protected long updateCount;
/*      */   protected JdbcResultSet generatedKeys;
/*      */   protected final int resultSetType;
/*      */   protected final int resultSetConcurrency;
/*      */   private volatile CommandInterface executingCommand;
/*      */   private ArrayList<String> batchCommands;
/*      */   private boolean escapeProcessing = true;
/*      */   private volatile boolean cancelled;
/*      */   private boolean closeOnCompletion;
/*      */   
/*      */   JdbcStatement(JdbcConnection paramJdbcConnection, int paramInt1, int paramInt2, int paramInt3) {
/*   66 */     this.conn = paramJdbcConnection;
/*   67 */     this.session = paramJdbcConnection.getSession();
/*   68 */     setTrace(this.session.getTrace(), 8, paramInt1);
/*   69 */     this.resultSetType = paramInt2;
/*   70 */     this.resultSetConcurrency = paramInt3;
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
/*      */   public ResultSet executeQuery(String paramString) throws SQLException {
/*      */     try {
/*   84 */       int i = getNextId(4);
/*   85 */       if (isDebugEnabled()) {
/*   86 */         debugCodeAssign("ResultSet", 4, i, "executeQuery(" + quote(paramString) + ')');
/*      */       }
/*   88 */       synchronized (this.session) {
/*   89 */         ResultInterface resultInterface; checkClosed();
/*   90 */         closeOldResultSet();
/*   91 */         paramString = JdbcConnection.translateSQL(paramString, this.escapeProcessing);
/*   92 */         CommandInterface commandInterface = this.conn.prepareCommand(paramString, this.fetchSize);
/*      */         
/*   94 */         boolean bool = false;
/*   95 */         boolean bool1 = (this.resultSetType != 1003) ? true : false;
/*   96 */         boolean bool2 = (this.resultSetConcurrency == 1008) ? true : false;
/*   97 */         setExecutingStatement(commandInterface);
/*      */         try {
/*   99 */           resultInterface = commandInterface.executeQuery(this.maxRows, bool1);
/*  100 */           bool = resultInterface.isLazy();
/*      */         } finally {
/*  102 */           if (!bool) {
/*  103 */             setExecutingStatement((CommandInterface)null);
/*      */           }
/*      */         } 
/*  106 */         if (!bool) {
/*  107 */           commandInterface.close();
/*      */         }
/*  109 */         this.resultSet = new JdbcResultSet(this.conn, this, commandInterface, resultInterface, i, bool1, bool2, false);
/*      */       } 
/*  111 */       return this.resultSet;
/*  112 */     } catch (Exception exception) {
/*  113 */       throw logAndConvert(exception);
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
/*      */   public final int executeUpdate(String paramString) throws SQLException {
/*      */     try {
/*  142 */       debugCodeCall("executeUpdate", paramString);
/*  143 */       long l = executeUpdateInternal(paramString, (Object)null);
/*  144 */       return (l <= 2147483647L) ? (int)l : -2;
/*  145 */     } catch (Exception exception) {
/*  146 */       throw logAndConvert(exception);
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
/*      */   public final long executeLargeUpdate(String paramString) throws SQLException {
/*      */     try {
/*  172 */       debugCodeCall("executeLargeUpdate", paramString);
/*  173 */       return executeUpdateInternal(paramString, (Object)null);
/*  174 */     } catch (Exception exception) {
/*  175 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private long executeUpdateInternal(String paramString, Object paramObject) {
/*  180 */     if (getClass() != JdbcStatement.class) {
/*  181 */       throw DbException.get(90130);
/*      */     }
/*  183 */     checkClosed();
/*  184 */     closeOldResultSet();
/*  185 */     paramString = JdbcConnection.translateSQL(paramString, this.escapeProcessing);
/*  186 */     CommandInterface commandInterface = this.conn.prepareCommand(paramString, this.fetchSize);
/*  187 */     synchronized (this.session) {
/*  188 */       setExecutingStatement(commandInterface);
/*      */       try {
/*  190 */         ResultWithGeneratedKeys resultWithGeneratedKeys = commandInterface.executeUpdate(paramObject);
/*  191 */         this.updateCount = resultWithGeneratedKeys.getUpdateCount();
/*  192 */         ResultInterface resultInterface = resultWithGeneratedKeys.getGeneratedKeys();
/*  193 */         if (resultInterface != null) {
/*  194 */           int i = getNextId(4);
/*  195 */           this.generatedKeys = new JdbcResultSet(this.conn, this, commandInterface, resultInterface, i, true, false, false);
/*      */         } 
/*      */       } finally {
/*  198 */         setExecutingStatement((CommandInterface)null);
/*      */       } 
/*      */     } 
/*  201 */     commandInterface.close();
/*  202 */     return this.updateCount;
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
/*      */   public final boolean execute(String paramString) throws SQLException {
/*      */     try {
/*  222 */       debugCodeCall("execute", paramString);
/*  223 */       return executeInternal(paramString, Boolean.valueOf(false));
/*  224 */     } catch (Exception exception) {
/*  225 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private boolean executeInternal(String paramString, Object paramObject) {
/*      */     boolean bool1;
/*  230 */     if (getClass() != JdbcStatement.class) {
/*  231 */       throw DbException.get(90130);
/*      */     }
/*  233 */     int i = getNextId(4);
/*  234 */     checkClosed();
/*  235 */     closeOldResultSet();
/*  236 */     paramString = JdbcConnection.translateSQL(paramString, this.escapeProcessing);
/*  237 */     CommandInterface commandInterface = this.conn.prepareCommand(paramString, this.fetchSize);
/*  238 */     boolean bool = false;
/*      */     
/*  240 */     synchronized (this.session) {
/*  241 */       setExecutingStatement(commandInterface);
/*      */       try {
/*  243 */         if (commandInterface.isQuery()) {
/*  244 */           bool1 = true;
/*  245 */           boolean bool2 = (this.resultSetType != 1003) ? true : false;
/*  246 */           boolean bool3 = (this.resultSetConcurrency == 1008) ? true : false;
/*  247 */           ResultInterface resultInterface = commandInterface.executeQuery(this.maxRows, bool2);
/*  248 */           bool = resultInterface.isLazy();
/*  249 */           this.resultSet = new JdbcResultSet(this.conn, this, commandInterface, resultInterface, i, bool2, bool3, false);
/*      */         } else {
/*  251 */           bool1 = false;
/*  252 */           ResultWithGeneratedKeys resultWithGeneratedKeys = commandInterface.executeUpdate(paramObject);
/*  253 */           this.updateCount = resultWithGeneratedKeys.getUpdateCount();
/*  254 */           ResultInterface resultInterface = resultWithGeneratedKeys.getGeneratedKeys();
/*  255 */           if (resultInterface != null) {
/*  256 */             this.generatedKeys = new JdbcResultSet(this.conn, this, commandInterface, resultInterface, i, true, false, false);
/*      */           }
/*      */         } 
/*      */       } finally {
/*  260 */         if (!bool) {
/*  261 */           setExecutingStatement((CommandInterface)null);
/*      */         }
/*      */       } 
/*      */     } 
/*  265 */     if (!bool) {
/*  266 */       commandInterface.close();
/*      */     }
/*  268 */     return bool1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getResultSet() throws SQLException {
/*      */     try {
/*  279 */       checkClosed();
/*  280 */       if (this.resultSet != null) {
/*  281 */         int i = this.resultSet.getTraceId();
/*  282 */         debugCodeAssign("ResultSet", 4, i, "getResultSet()");
/*      */       } else {
/*  284 */         debugCodeCall("getResultSet");
/*      */       } 
/*  286 */       return this.resultSet;
/*  287 */     } catch (Exception exception) {
/*  288 */       throw logAndConvert(exception);
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
/*      */   public final int getUpdateCount() throws SQLException {
/*      */     try {
/*  306 */       debugCodeCall("getUpdateCount");
/*  307 */       checkClosed();
/*  308 */       return (this.updateCount <= 2147483647L) ? (int)this.updateCount : -2;
/*  309 */     } catch (Exception exception) {
/*  310 */       throw logAndConvert(exception);
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
/*      */   public final long getLargeUpdateCount() throws SQLException {
/*      */     try {
/*  326 */       debugCodeCall("getLargeUpdateCount");
/*  327 */       checkClosed();
/*  328 */       return this.updateCount;
/*  329 */     } catch (Exception exception) {
/*  330 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws SQLException {
/*      */     try {
/*  342 */       debugCodeCall("close");
/*  343 */       closeInternal();
/*  344 */     } catch (Exception exception) {
/*  345 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void closeInternal() {
/*  350 */     synchronized (this.session) {
/*  351 */       closeOldResultSet();
/*  352 */       if (this.conn != null) {
/*  353 */         this.conn = null;
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
/*      */   public Connection getConnection() {
/*  365 */     debugCodeCall("getConnection");
/*  366 */     return this.conn;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SQLWarning getWarnings() throws SQLException {
/*      */     try {
/*  378 */       debugCodeCall("getWarnings");
/*  379 */       checkClosed();
/*  380 */       return null;
/*  381 */     } catch (Exception exception) {
/*  382 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearWarnings() throws SQLException {
/*      */     try {
/*  393 */       debugCodeCall("clearWarnings");
/*  394 */       checkClosed();
/*  395 */     } catch (Exception exception) {
/*  396 */       throw logAndConvert(exception);
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
/*      */   public void setCursorName(String paramString) throws SQLException {
/*      */     try {
/*  409 */       debugCodeCall("setCursorName", paramString);
/*  410 */       checkClosed();
/*  411 */     } catch (Exception exception) {
/*  412 */       throw logAndConvert(exception);
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
/*      */   public void setFetchDirection(int paramInt) throws SQLException {
/*      */     try {
/*  426 */       debugCodeCall("setFetchDirection", paramInt);
/*  427 */       checkClosed();
/*  428 */     } catch (Exception exception) {
/*  429 */       throw logAndConvert(exception);
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
/*      */   public int getFetchDirection() throws SQLException {
/*      */     try {
/*  442 */       debugCodeCall("getFetchDirection");
/*  443 */       checkClosed();
/*  444 */       return 1000;
/*  445 */     } catch (Exception exception) {
/*  446 */       throw logAndConvert(exception);
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
/*      */   public int getMaxRows() throws SQLException {
/*      */     try {
/*  459 */       debugCodeCall("getMaxRows");
/*  460 */       checkClosed();
/*  461 */       return (this.maxRows <= 2147483647L) ? (int)this.maxRows : 0;
/*  462 */     } catch (Exception exception) {
/*  463 */       throw logAndConvert(exception);
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
/*      */   public long getLargeMaxRows() throws SQLException {
/*      */     try {
/*  476 */       debugCodeCall("getLargeMaxRows");
/*  477 */       checkClosed();
/*  478 */       return this.maxRows;
/*  479 */     } catch (Exception exception) {
/*  480 */       throw logAndConvert(exception);
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
/*      */   public void setMaxRows(int paramInt) throws SQLException {
/*      */     try {
/*  493 */       debugCodeCall("setMaxRows", paramInt);
/*  494 */       checkClosed();
/*  495 */       if (paramInt < 0) {
/*  496 */         throw DbException.getInvalidValueException("maxRows", Integer.valueOf(paramInt));
/*      */       }
/*  498 */       this.maxRows = paramInt;
/*  499 */     } catch (Exception exception) {
/*  500 */       throw logAndConvert(exception);
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
/*      */   public void setLargeMaxRows(long paramLong) throws SQLException {
/*      */     try {
/*  513 */       debugCodeCall("setLargeMaxRows", paramLong);
/*  514 */       checkClosed();
/*  515 */       if (paramLong < 0L) {
/*  516 */         throw DbException.getInvalidValueException("maxRows", Long.valueOf(paramLong));
/*      */       }
/*  518 */       this.maxRows = paramLong;
/*  519 */     } catch (Exception exception) {
/*  520 */       throw logAndConvert(exception);
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
/*      */   public void setFetchSize(int paramInt) throws SQLException {
/*      */     try {
/*  538 */       debugCodeCall("setFetchSize", paramInt);
/*  539 */       checkClosed();
/*  540 */       if (paramInt < 0 || (paramInt > 0 && this.maxRows > 0L && paramInt > this.maxRows)) {
/*  541 */         throw DbException.getInvalidValueException("rows", Integer.valueOf(paramInt));
/*      */       }
/*  543 */       if (paramInt == 0) {
/*  544 */         paramInt = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
/*      */       }
/*  546 */       this.fetchSize = paramInt;
/*  547 */     } catch (Exception exception) {
/*  548 */       throw logAndConvert(exception);
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
/*      */   public int getFetchSize() throws SQLException {
/*      */     try {
/*  561 */       debugCodeCall("getFetchSize");
/*  562 */       checkClosed();
/*  563 */       return this.fetchSize;
/*  564 */     } catch (Exception exception) {
/*  565 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getResultSetConcurrency() throws SQLException {
/*      */     try {
/*  577 */       debugCodeCall("getResultSetConcurrency");
/*  578 */       checkClosed();
/*  579 */       return this.resultSetConcurrency;
/*  580 */     } catch (Exception exception) {
/*  581 */       throw logAndConvert(exception);
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
/*      */   public int getResultSetType() throws SQLException {
/*      */     try {
/*  594 */       debugCodeCall("getResultSetType");
/*  595 */       checkClosed();
/*  596 */       return this.resultSetType;
/*  597 */     } catch (Exception exception) {
/*  598 */       throw logAndConvert(exception);
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
/*      */   public int getMaxFieldSize() throws SQLException {
/*      */     try {
/*  611 */       debugCodeCall("getMaxFieldSize");
/*  612 */       checkClosed();
/*  613 */       return 0;
/*  614 */     } catch (Exception exception) {
/*  615 */       throw logAndConvert(exception);
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
/*      */   public void setMaxFieldSize(int paramInt) throws SQLException {
/*      */     try {
/*  629 */       debugCodeCall("setMaxFieldSize", paramInt);
/*  630 */       checkClosed();
/*  631 */     } catch (Exception exception) {
/*  632 */       throw logAndConvert(exception);
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
/*      */   public void setEscapeProcessing(boolean paramBoolean) throws SQLException {
/*      */     try {
/*  646 */       if (isDebugEnabled()) {
/*  647 */         debugCode("setEscapeProcessing(" + paramBoolean + ')');
/*      */       }
/*  649 */       checkClosed();
/*  650 */       this.escapeProcessing = paramBoolean;
/*  651 */     } catch (Exception exception) {
/*  652 */       throw logAndConvert(exception);
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
/*      */   public void cancel() throws SQLException {
/*      */     try {
/*  668 */       debugCodeCall("cancel");
/*  669 */       checkClosed();
/*      */       
/*  671 */       CommandInterface commandInterface = this.executingCommand;
/*      */       try {
/*  673 */         if (commandInterface != null) {
/*  674 */           commandInterface.cancel();
/*  675 */           this.cancelled = true;
/*      */         } 
/*      */       } finally {
/*  678 */         setExecutingStatement((CommandInterface)null);
/*      */       } 
/*  680 */     } catch (Exception exception) {
/*  681 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCancelled() {
/*  691 */     return this.cancelled;
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
/*      */   public int getQueryTimeout() throws SQLException {
/*      */     try {
/*  709 */       debugCodeCall("getQueryTimeout");
/*  710 */       checkClosed();
/*  711 */       return this.conn.getQueryTimeout();
/*  712 */     } catch (Exception exception) {
/*  713 */       throw logAndConvert(exception);
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
/*      */   public void setQueryTimeout(int paramInt) throws SQLException {
/*      */     try {
/*  730 */       debugCodeCall("setQueryTimeout", paramInt);
/*  731 */       checkClosed();
/*  732 */       if (paramInt < 0) {
/*  733 */         throw DbException.getInvalidValueException("seconds", Integer.valueOf(paramInt));
/*      */       }
/*  735 */       this.conn.setQueryTimeout(paramInt);
/*  736 */     } catch (Exception exception) {
/*  737 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addBatch(String paramString) throws SQLException {
/*      */     try {
/*  749 */       debugCodeCall("addBatch", paramString);
/*  750 */       checkClosed();
/*  751 */       paramString = JdbcConnection.translateSQL(paramString, this.escapeProcessing);
/*  752 */       if (this.batchCommands == null) {
/*  753 */         this.batchCommands = Utils.newSmallArrayList();
/*      */       }
/*  755 */       this.batchCommands.add(paramString);
/*  756 */     } catch (Exception exception) {
/*  757 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearBatch() throws SQLException {
/*      */     try {
/*  767 */       debugCodeCall("clearBatch");
/*  768 */       checkClosed();
/*  769 */       this.batchCommands = null;
/*  770 */     } catch (Exception exception) {
/*  771 */       throw logAndConvert(exception);
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
/*      */   public int[] executeBatch() throws SQLException {
/*      */     try {
/*  785 */       debugCodeCall("executeBatch");
/*  786 */       checkClosed();
/*  787 */       if (this.batchCommands == null) {
/*  788 */         this.batchCommands = new ArrayList<>();
/*      */       }
/*  790 */       int i = this.batchCommands.size();
/*  791 */       int[] arrayOfInt = new int[i];
/*  792 */       SQLException sQLException = new SQLException();
/*  793 */       for (byte b = 0; b < i; b++) {
/*  794 */         long l = executeBatchElement(this.batchCommands.get(b), sQLException);
/*  795 */         arrayOfInt[b] = (l <= 2147483647L) ? (int)l : -2;
/*      */       } 
/*  797 */       this.batchCommands = null;
/*  798 */       sQLException = sQLException.getNextException();
/*  799 */       if (sQLException != null) {
/*  800 */         throw new JdbcBatchUpdateException(sQLException, arrayOfInt);
/*      */       }
/*  802 */       return arrayOfInt;
/*  803 */     } catch (Exception exception) {
/*  804 */       throw logAndConvert(exception);
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
/*      */   public long[] executeLargeBatch() throws SQLException {
/*      */     try {
/*  817 */       debugCodeCall("executeLargeBatch");
/*  818 */       checkClosed();
/*  819 */       if (this.batchCommands == null) {
/*  820 */         this.batchCommands = new ArrayList<>();
/*      */       }
/*  822 */       int i = this.batchCommands.size();
/*  823 */       long[] arrayOfLong = new long[i];
/*  824 */       SQLException sQLException = new SQLException();
/*  825 */       for (byte b = 0; b < i; b++) {
/*  826 */         arrayOfLong[b] = executeBatchElement((String)this.batchCommands.get(b), sQLException);
/*      */       }
/*  828 */       this.batchCommands = null;
/*  829 */       sQLException = sQLException.getNextException();
/*  830 */       if (sQLException != null) {
/*  831 */         throw new JdbcBatchUpdateException(sQLException, arrayOfLong);
/*      */       }
/*  833 */       return arrayOfLong;
/*  834 */     } catch (Exception exception) {
/*  835 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private long executeBatchElement(String paramString, SQLException paramSQLException) {
/*      */     long l;
/*      */     try {
/*  842 */       l = executeUpdateInternal(paramString, (Object)null);
/*  843 */     } catch (Exception exception) {
/*  844 */       paramSQLException.setNextException(logAndConvert(exception));
/*  845 */       l = -3L;
/*      */     } 
/*  847 */     return l;
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
/*      */   public ResultSet getGeneratedKeys() throws SQLException {
/*      */     try {
/*  887 */       int i = (this.generatedKeys != null) ? this.generatedKeys.getTraceId() : getNextId(4);
/*  888 */       if (isDebugEnabled()) {
/*  889 */         debugCodeAssign("ResultSet", 4, i, "getGeneratedKeys()");
/*      */       }
/*  891 */       checkClosed();
/*  892 */       if (this.generatedKeys == null) {
/*  893 */         this.generatedKeys = new JdbcResultSet(this.conn, this, null, (ResultInterface)new SimpleResult(), i, true, false, false);
/*      */       }
/*  895 */       return this.generatedKeys;
/*  896 */     } catch (Exception exception) {
/*  897 */       throw logAndConvert(exception);
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
/*      */   public boolean getMoreResults() throws SQLException {
/*      */     try {
/*  913 */       debugCodeCall("getMoreResults");
/*  914 */       checkClosed();
/*  915 */       closeOldResultSet();
/*  916 */       return false;
/*  917 */     } catch (Exception exception) {
/*  918 */       throw logAndConvert(exception);
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
/*      */   public boolean getMoreResults(int paramInt) throws SQLException {
/*      */     try {
/*  934 */       debugCodeCall("getMoreResults", paramInt);
/*  935 */       switch (paramInt)
/*      */       { case 1:
/*      */         case 3:
/*  938 */           checkClosed();
/*  939 */           closeOldResultSet();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 2:
/*  947 */           return false; }  throw DbException.getInvalidValueException("current", Integer.valueOf(paramInt));
/*  948 */     } catch (Exception exception) {
/*  949 */       throw logAndConvert(exception);
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
/*      */   public final int executeUpdate(String paramString, int paramInt) throws SQLException {
/*      */     try {
/*  975 */       if (isDebugEnabled()) {
/*  976 */         debugCode("executeUpdate(" + quote(paramString) + ", " + paramInt + ')');
/*      */       }
/*  978 */       long l = executeUpdateInternal(paramString, Boolean.valueOf((paramInt == 1)));
/*  979 */       return (l <= 2147483647L) ? (int)l : -2;
/*  980 */     } catch (Exception exception) {
/*  981 */       throw logAndConvert(exception);
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
/*      */   public final long executeLargeUpdate(String paramString, int paramInt) throws SQLException {
/*      */     try {
/* 1003 */       if (isDebugEnabled()) {
/* 1004 */         debugCode("executeLargeUpdate(" + quote(paramString) + ", " + paramInt + ')');
/*      */       }
/* 1006 */       return executeUpdateInternal(paramString, Boolean.valueOf((paramInt == 1)));
/* 1007 */     } catch (Exception exception) {
/* 1008 */       throw logAndConvert(exception);
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
/*      */   public final int executeUpdate(String paramString, int[] paramArrayOfint) throws SQLException {
/*      */     try {
/* 1032 */       if (isDebugEnabled()) {
/* 1033 */         debugCode("executeUpdate(" + quote(paramString) + ", " + quoteIntArray(paramArrayOfint) + ')');
/*      */       }
/* 1035 */       long l = executeUpdateInternal(paramString, paramArrayOfint);
/* 1036 */       return (l <= 2147483647L) ? (int)l : -2;
/* 1037 */     } catch (Exception exception) {
/* 1038 */       throw logAndConvert(exception);
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
/*      */   public final long executeLargeUpdate(String paramString, int[] paramArrayOfint) throws SQLException {
/*      */     try {
/* 1059 */       if (isDebugEnabled()) {
/* 1060 */         debugCode("executeLargeUpdate(" + quote(paramString) + ", " + quoteIntArray(paramArrayOfint) + ')');
/*      */       }
/* 1062 */       return executeUpdateInternal(paramString, paramArrayOfint);
/* 1063 */     } catch (Exception exception) {
/* 1064 */       throw logAndConvert(exception);
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
/*      */   public final int executeUpdate(String paramString, String[] paramArrayOfString) throws SQLException {
/*      */     try {
/* 1088 */       if (isDebugEnabled()) {
/* 1089 */         debugCode("executeUpdate(" + quote(paramString) + ", " + quoteArray(paramArrayOfString) + ')');
/*      */       }
/* 1091 */       long l = executeUpdateInternal(paramString, paramArrayOfString);
/* 1092 */       return (l <= 2147483647L) ? (int)l : -2;
/* 1093 */     } catch (Exception exception) {
/* 1094 */       throw logAndConvert(exception);
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
/*      */   public final long executeLargeUpdate(String paramString, String[] paramArrayOfString) throws SQLException {
/*      */     try {
/* 1115 */       if (isDebugEnabled()) {
/* 1116 */         debugCode("executeLargeUpdate(" + quote(paramString) + ", " + quoteArray(paramArrayOfString) + ')');
/*      */       }
/* 1118 */       return executeUpdateInternal(paramString, paramArrayOfString);
/* 1119 */     } catch (Exception exception) {
/* 1120 */       throw logAndConvert(exception);
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
/*      */   public final boolean execute(String paramString, int paramInt) throws SQLException {
/*      */     try {
/* 1140 */       if (isDebugEnabled()) {
/* 1141 */         debugCode("execute(" + quote(paramString) + ", " + paramInt + ')');
/*      */       }
/* 1143 */       return executeInternal(paramString, Boolean.valueOf((paramInt == 1)));
/* 1144 */     } catch (Exception exception) {
/* 1145 */       throw logAndConvert(exception);
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
/*      */   public final boolean execute(String paramString, int[] paramArrayOfint) throws SQLException {
/*      */     try {
/* 1164 */       if (isDebugEnabled()) {
/* 1165 */         debugCode("execute(" + quote(paramString) + ", " + quoteIntArray(paramArrayOfint) + ')');
/*      */       }
/* 1167 */       return executeInternal(paramString, paramArrayOfint);
/* 1168 */     } catch (Exception exception) {
/* 1169 */       throw logAndConvert(exception);
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
/*      */   public final boolean execute(String paramString, String[] paramArrayOfString) throws SQLException {
/*      */     try {
/* 1188 */       if (isDebugEnabled()) {
/* 1189 */         debugCode("execute(" + quote(paramString) + ", " + quoteArray(paramArrayOfString) + ')');
/*      */       }
/* 1191 */       return executeInternal(paramString, paramArrayOfString);
/* 1192 */     } catch (Exception exception) {
/* 1193 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getResultSetHoldability() throws SQLException {
/*      */     try {
/* 1205 */       debugCodeCall("getResultSetHoldability");
/* 1206 */       checkClosed();
/* 1207 */       return 1;
/* 1208 */     } catch (Exception exception) {
/* 1209 */       throw logAndConvert(exception);
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
/*      */   public void closeOnCompletion() throws SQLException {
/*      */     try {
/* 1223 */       debugCodeCall("closeOnCompletion");
/* 1224 */       checkClosed();
/* 1225 */       this.closeOnCompletion = true;
/* 1226 */     } catch (Exception exception) {
/* 1227 */       throw logAndConvert(exception);
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
/*      */   public boolean isCloseOnCompletion() throws SQLException {
/*      */     try {
/* 1243 */       debugCodeCall("isCloseOnCompletion");
/* 1244 */       checkClosed();
/* 1245 */       return this.closeOnCompletion;
/* 1246 */     } catch (Exception exception) {
/* 1247 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   void closeIfCloseOnCompletion() {
/* 1252 */     if (this.closeOnCompletion) {
/*      */       try {
/* 1254 */         closeInternal();
/* 1255 */       } catch (Exception exception) {
/*      */         
/* 1257 */         logAndConvert(exception);
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
/*      */   void checkClosed() {
/* 1270 */     if (this.conn == null) {
/* 1271 */       throw DbException.get(90007);
/*      */     }
/* 1273 */     this.conn.checkClosed();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void closeOldResultSet() {
/*      */     try {
/* 1282 */       if (this.resultSet != null) {
/* 1283 */         this.resultSet.closeInternal(true);
/*      */       }
/* 1285 */       if (this.generatedKeys != null) {
/* 1286 */         this.generatedKeys.closeInternal(true);
/*      */       }
/*      */     } finally {
/* 1289 */       this.cancelled = false;
/* 1290 */       this.resultSet = null;
/* 1291 */       this.updateCount = -1L;
/* 1292 */       this.generatedKeys = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setExecutingStatement(CommandInterface paramCommandInterface) {
/* 1303 */     if (paramCommandInterface == null) {
/* 1304 */       this.conn.setExecutingStatement(null);
/*      */     } else {
/* 1306 */       this.conn.setExecutingStatement(this);
/*      */     } 
/* 1308 */     this.executingCommand = paramCommandInterface;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void onLazyResultSetClose(CommandInterface paramCommandInterface, boolean paramBoolean) {
/* 1318 */     setExecutingStatement((CommandInterface)null);
/* 1319 */     paramCommandInterface.stop();
/* 1320 */     if (paramBoolean) {
/* 1321 */       paramCommandInterface.close();
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
/* 1333 */       debugCodeCall("isClosed");
/* 1334 */       return (this.conn == null);
/* 1335 */     } catch (Exception exception) {
/* 1336 */       throw logAndConvert(exception);
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
/* 1350 */       if (isWrapperFor(paramClass)) {
/* 1351 */         return (T)this;
/*      */       }
/* 1353 */       throw DbException.getInvalidValueException("iface", paramClass);
/* 1354 */     } catch (Exception exception) {
/* 1355 */       throw logAndConvert(exception);
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
/* 1367 */     return (paramClass != null && paramClass.isAssignableFrom(getClass()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isPoolable() {
/* 1376 */     debugCodeCall("isPoolable");
/* 1377 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPoolable(boolean paramBoolean) {
/* 1388 */     if (isDebugEnabled()) {
/* 1389 */       debugCode("setPoolable(" + paramBoolean + ')');
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
/*      */   public String enquoteIdentifier(String paramString, boolean paramBoolean) throws SQLException {
/* 1407 */     if (isSimpleIdentifier(paramString)) {
/* 1408 */       return paramBoolean ? ('"' + paramString + '"') : paramString;
/*      */     }
/*      */     try {
/* 1411 */       int i = paramString.length();
/* 1412 */       if (i > 0) {
/* 1413 */         if (paramString.charAt(0) == '"') {
/* 1414 */           checkQuotes(paramString, 1, i);
/* 1415 */           return paramString;
/* 1416 */         }  if (paramString.startsWith("U&\"") || paramString.startsWith("u&\"")) {
/*      */           
/* 1418 */           checkQuotes(paramString, 3, i);
/*      */           
/* 1420 */           StringUtils.decodeUnicodeStringSQL(paramString, 92);
/* 1421 */           return paramString;
/*      */         } 
/*      */       } 
/* 1424 */       return StringUtils.quoteIdentifier(paramString);
/* 1425 */     } catch (Exception exception) {
/* 1426 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private static void checkQuotes(String paramString, int paramInt1, int paramInt2) {
/* 1431 */     boolean bool = true;
/* 1432 */     for (int i = paramInt1; i < paramInt2; i++) {
/* 1433 */       if (paramString.charAt(i) == '"') {
/* 1434 */         bool = !bool ? true : false;
/* 1435 */       } else if (!bool) {
/* 1436 */         throw DbException.get(42602, paramString);
/*      */       } 
/*      */     } 
/* 1439 */     if (bool) {
/* 1440 */       throw DbException.get(42602, paramString);
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
/*      */   public boolean isSimpleIdentifier(String paramString) throws SQLException {
/*      */     Session.StaticSettings staticSettings;
/*      */     try {
/* 1455 */       checkClosed();
/* 1456 */       staticSettings = this.conn.getStaticSettings();
/* 1457 */     } catch (Exception exception) {
/* 1458 */       throw logAndConvert(exception);
/*      */     } 
/* 1460 */     return ParserUtil.isSimpleIdentifier(paramString, staticSettings.databaseToUpper, staticSettings.databaseToLower);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1468 */     return getTraceObjectName();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */