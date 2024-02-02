package org.h2.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import org.h2.command.CommandInterface;
import org.h2.engine.Session;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.result.ResultInterface;
import org.h2.result.ResultWithGeneratedKeys;
import org.h2.result.SimpleResult;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public class JdbcStatement extends TraceObject implements Statement, JdbcStatementBackwardsCompat {
   protected JdbcConnection conn;
   protected Session session;
   protected JdbcResultSet resultSet;
   protected long maxRows;
   protected int fetchSize;
   protected long updateCount;
   protected JdbcResultSet generatedKeys;
   protected final int resultSetType;
   protected final int resultSetConcurrency;
   private volatile CommandInterface executingCommand;
   private ArrayList<String> batchCommands;
   private boolean escapeProcessing;
   private volatile boolean cancelled;
   private boolean closeOnCompletion;

   JdbcStatement(JdbcConnection var1, int var2, int var3, int var4) {
      this.fetchSize = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
      this.escapeProcessing = true;
      this.conn = var1;
      this.session = var1.getSession();
      this.setTrace(this.session.getTrace(), 8, var2);
      this.resultSetType = var3;
      this.resultSetConcurrency = var4;
   }

   public ResultSet executeQuery(String var1) throws SQLException {
      try {
         int var2 = getNextId(4);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("ResultSet", 4, var2, "executeQuery(" + quote(var1) + ')');
         }

         synchronized(this.session) {
            this.checkClosed();
            this.closeOldResultSet();
            var1 = JdbcConnection.translateSQL(var1, this.escapeProcessing);
            CommandInterface var4 = this.conn.prepareCommand(var1, this.fetchSize);
            boolean var6 = false;
            boolean var7 = this.resultSetType != 1003;
            boolean var8 = this.resultSetConcurrency == 1008;
            this.setExecutingStatement(var4);

            ResultInterface var5;
            try {
               var5 = var4.executeQuery(this.maxRows, var7);
               var6 = var5.isLazy();
            } finally {
               if (!var6) {
                  this.setExecutingStatement((CommandInterface)null);
               }

            }

            if (!var6) {
               var4.close();
            }

            this.resultSet = new JdbcResultSet(this.conn, this, var4, var5, var2, var7, var8, false);
         }

         return this.resultSet;
      } catch (Exception var16) {
         throw this.logAndConvert(var16);
      }
   }

   public final int executeUpdate(String var1) throws SQLException {
      try {
         this.debugCodeCall("executeUpdate", var1);
         long var2 = this.executeUpdateInternal(var1, (Object)null);
         return var2 <= 2147483647L ? (int)var2 : -2;
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public final long executeLargeUpdate(String var1) throws SQLException {
      try {
         this.debugCodeCall("executeLargeUpdate", var1);
         return this.executeUpdateInternal(var1, (Object)null);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private long executeUpdateInternal(String var1, Object var2) {
      if (this.getClass() != JdbcStatement.class) {
         throw DbException.get(90130);
      } else {
         this.checkClosed();
         this.closeOldResultSet();
         var1 = JdbcConnection.translateSQL(var1, this.escapeProcessing);
         CommandInterface var3 = this.conn.prepareCommand(var1, this.fetchSize);
         synchronized(this.session) {
            this.setExecutingStatement(var3);

            try {
               ResultWithGeneratedKeys var5 = var3.executeUpdate(var2);
               this.updateCount = var5.getUpdateCount();
               ResultInterface var6 = var5.getGeneratedKeys();
               if (var6 != null) {
                  int var7 = getNextId(4);
                  this.generatedKeys = new JdbcResultSet(this.conn, this, var3, var6, var7, true, false, false);
               }
            } finally {
               this.setExecutingStatement((CommandInterface)null);
            }
         }

         var3.close();
         return this.updateCount;
      }
   }

   public final boolean execute(String var1) throws SQLException {
      try {
         this.debugCodeCall("execute", var1);
         return this.executeInternal(var1, false);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private boolean executeInternal(String var1, Object var2) {
      if (this.getClass() != JdbcStatement.class) {
         throw DbException.get(90130);
      } else {
         int var3 = getNextId(4);
         this.checkClosed();
         this.closeOldResultSet();
         var1 = JdbcConnection.translateSQL(var1, this.escapeProcessing);
         CommandInterface var4 = this.conn.prepareCommand(var1, this.fetchSize);
         boolean var5 = false;
         boolean var6;
         synchronized(this.session) {
            this.setExecutingStatement(var4);

            try {
               if (var4.isQuery()) {
                  var6 = true;
                  boolean var8 = this.resultSetType != 1003;
                  boolean var9 = this.resultSetConcurrency == 1008;
                  ResultInterface var10 = var4.executeQuery(this.maxRows, var8);
                  var5 = var10.isLazy();
                  this.resultSet = new JdbcResultSet(this.conn, this, var4, var10, var3, var8, var9, false);
               } else {
                  var6 = false;
                  ResultWithGeneratedKeys var17 = var4.executeUpdate(var2);
                  this.updateCount = var17.getUpdateCount();
                  ResultInterface var18 = var17.getGeneratedKeys();
                  if (var18 != null) {
                     this.generatedKeys = new JdbcResultSet(this.conn, this, var4, var18, var3, true, false, false);
                  }
               }
            } finally {
               if (!var5) {
                  this.setExecutingStatement((CommandInterface)null);
               }

            }
         }

         if (!var5) {
            var4.close();
         }

         return var6;
      }
   }

   public ResultSet getResultSet() throws SQLException {
      try {
         this.checkClosed();
         if (this.resultSet != null) {
            int var1 = this.resultSet.getTraceId();
            this.debugCodeAssign("ResultSet", 4, var1, "getResultSet()");
         } else {
            this.debugCodeCall("getResultSet");
         }

         return this.resultSet;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public final int getUpdateCount() throws SQLException {
      try {
         this.debugCodeCall("getUpdateCount");
         this.checkClosed();
         return this.updateCount <= 2147483647L ? (int)this.updateCount : -2;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public final long getLargeUpdateCount() throws SQLException {
      try {
         this.debugCodeCall("getLargeUpdateCount");
         this.checkClosed();
         return this.updateCount;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void close() throws SQLException {
      try {
         this.debugCodeCall("close");
         this.closeInternal();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   private void closeInternal() {
      synchronized(this.session) {
         this.closeOldResultSet();
         if (this.conn != null) {
            this.conn = null;
         }

      }
   }

   public Connection getConnection() {
      this.debugCodeCall("getConnection");
      return this.conn;
   }

   public SQLWarning getWarnings() throws SQLException {
      try {
         this.debugCodeCall("getWarnings");
         this.checkClosed();
         return null;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void clearWarnings() throws SQLException {
      try {
         this.debugCodeCall("clearWarnings");
         this.checkClosed();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void setCursorName(String var1) throws SQLException {
      try {
         this.debugCodeCall("setCursorName", var1);
         this.checkClosed();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void setFetchDirection(int var1) throws SQLException {
      try {
         this.debugCodeCall("setFetchDirection", (long)var1);
         this.checkClosed();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getFetchDirection() throws SQLException {
      try {
         this.debugCodeCall("getFetchDirection");
         this.checkClosed();
         return 1000;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int getMaxRows() throws SQLException {
      try {
         this.debugCodeCall("getMaxRows");
         this.checkClosed();
         return this.maxRows <= 2147483647L ? (int)this.maxRows : 0;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public long getLargeMaxRows() throws SQLException {
      try {
         this.debugCodeCall("getLargeMaxRows");
         this.checkClosed();
         return this.maxRows;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void setMaxRows(int var1) throws SQLException {
      try {
         this.debugCodeCall("setMaxRows", (long)var1);
         this.checkClosed();
         if (var1 < 0) {
            throw DbException.getInvalidValueException("maxRows", var1);
         } else {
            this.maxRows = (long)var1;
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void setLargeMaxRows(long var1) throws SQLException {
      try {
         this.debugCodeCall("setLargeMaxRows", var1);
         this.checkClosed();
         if (var1 < 0L) {
            throw DbException.getInvalidValueException("maxRows", var1);
         } else {
            this.maxRows = var1;
         }
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setFetchSize(int var1) throws SQLException {
      try {
         this.debugCodeCall("setFetchSize", (long)var1);
         this.checkClosed();
         if (var1 < 0 || var1 > 0 && this.maxRows > 0L && (long)var1 > this.maxRows) {
            throw DbException.getInvalidValueException("rows", var1);
         } else {
            if (var1 == 0) {
               var1 = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
            }

            this.fetchSize = var1;
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getFetchSize() throws SQLException {
      try {
         this.debugCodeCall("getFetchSize");
         this.checkClosed();
         return this.fetchSize;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int getResultSetConcurrency() throws SQLException {
      try {
         this.debugCodeCall("getResultSetConcurrency");
         this.checkClosed();
         return this.resultSetConcurrency;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int getResultSetType() throws SQLException {
      try {
         this.debugCodeCall("getResultSetType");
         this.checkClosed();
         return this.resultSetType;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int getMaxFieldSize() throws SQLException {
      try {
         this.debugCodeCall("getMaxFieldSize");
         this.checkClosed();
         return 0;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void setMaxFieldSize(int var1) throws SQLException {
      try {
         this.debugCodeCall("setMaxFieldSize", (long)var1);
         this.checkClosed();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void setEscapeProcessing(boolean var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setEscapeProcessing(" + var1 + ')');
         }

         this.checkClosed();
         this.escapeProcessing = var1;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void cancel() throws SQLException {
      try {
         this.debugCodeCall("cancel");
         this.checkClosed();
         CommandInterface var1 = this.executingCommand;

         try {
            if (var1 != null) {
               var1.cancel();
               this.cancelled = true;
            }
         } finally {
            this.setExecutingStatement((CommandInterface)null);
         }

      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public int getQueryTimeout() throws SQLException {
      try {
         this.debugCodeCall("getQueryTimeout");
         this.checkClosed();
         return this.conn.getQueryTimeout();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void setQueryTimeout(int var1) throws SQLException {
      try {
         this.debugCodeCall("setQueryTimeout", (long)var1);
         this.checkClosed();
         if (var1 < 0) {
            throw DbException.getInvalidValueException("seconds", var1);
         } else {
            this.conn.setQueryTimeout(var1);
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void addBatch(String var1) throws SQLException {
      try {
         this.debugCodeCall("addBatch", var1);
         this.checkClosed();
         var1 = JdbcConnection.translateSQL(var1, this.escapeProcessing);
         if (this.batchCommands == null) {
            this.batchCommands = Utils.newSmallArrayList();
         }

         this.batchCommands.add(var1);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void clearBatch() throws SQLException {
      try {
         this.debugCodeCall("clearBatch");
         this.checkClosed();
         this.batchCommands = null;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int[] executeBatch() throws SQLException {
      try {
         this.debugCodeCall("executeBatch");
         this.checkClosed();
         if (this.batchCommands == null) {
            this.batchCommands = new ArrayList();
         }

         int var1 = this.batchCommands.size();
         int[] var2 = new int[var1];
         SQLException var3 = new SQLException();

         for(int var4 = 0; var4 < var1; ++var4) {
            long var5 = this.executeBatchElement((String)this.batchCommands.get(var4), var3);
            var2[var4] = var5 <= 2147483647L ? (int)var5 : -2;
         }

         this.batchCommands = null;
         var3 = var3.getNextException();
         if (var3 != null) {
            throw new JdbcBatchUpdateException(var3, var2);
         } else {
            return var2;
         }
      } catch (Exception var7) {
         throw this.logAndConvert(var7);
      }
   }

   public long[] executeLargeBatch() throws SQLException {
      try {
         this.debugCodeCall("executeLargeBatch");
         this.checkClosed();
         if (this.batchCommands == null) {
            this.batchCommands = new ArrayList();
         }

         int var1 = this.batchCommands.size();
         long[] var2 = new long[var1];
         SQLException var3 = new SQLException();

         for(int var4 = 0; var4 < var1; ++var4) {
            var2[var4] = this.executeBatchElement((String)this.batchCommands.get(var4), var3);
         }

         this.batchCommands = null;
         var3 = var3.getNextException();
         if (var3 != null) {
            throw new JdbcBatchUpdateException(var3, var2);
         } else {
            return var2;
         }
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   private long executeBatchElement(String var1, SQLException var2) {
      long var3;
      try {
         var3 = this.executeUpdateInternal(var1, (Object)null);
      } catch (Exception var6) {
         var2.setNextException(this.logAndConvert(var6));
         var3 = -3L;
      }

      return var3;
   }

   public ResultSet getGeneratedKeys() throws SQLException {
      try {
         int var1 = this.generatedKeys != null ? this.generatedKeys.getTraceId() : getNextId(4);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("ResultSet", 4, var1, "getGeneratedKeys()");
         }

         this.checkClosed();
         if (this.generatedKeys == null) {
            this.generatedKeys = new JdbcResultSet(this.conn, this, (CommandInterface)null, new SimpleResult(), var1, true, false, false);
         }

         return this.generatedKeys;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean getMoreResults() throws SQLException {
      try {
         this.debugCodeCall("getMoreResults");
         this.checkClosed();
         this.closeOldResultSet();
         return false;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean getMoreResults(int var1) throws SQLException {
      try {
         this.debugCodeCall("getMoreResults", (long)var1);
         switch (var1) {
            case 1:
            case 3:
               this.checkClosed();
               this.closeOldResultSet();
            case 2:
               return false;
            default:
               throw DbException.getInvalidValueException("current", var1);
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public final int executeUpdate(String var1, int var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("executeUpdate(" + quote(var1) + ", " + var2 + ')');
         }

         long var3 = this.executeUpdateInternal(var1, var2 == 1);
         return var3 <= 2147483647L ? (int)var3 : -2;
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public final long executeLargeUpdate(String var1, int var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("executeLargeUpdate(" + quote(var1) + ", " + var2 + ')');
         }

         return this.executeUpdateInternal(var1, var2 == 1);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public final int executeUpdate(String var1, int[] var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("executeUpdate(" + quote(var1) + ", " + quoteIntArray(var2) + ')');
         }

         long var3 = this.executeUpdateInternal(var1, var2);
         return var3 <= 2147483647L ? (int)var3 : -2;
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public final long executeLargeUpdate(String var1, int[] var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("executeLargeUpdate(" + quote(var1) + ", " + quoteIntArray(var2) + ')');
         }

         return this.executeUpdateInternal(var1, var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public final int executeUpdate(String var1, String[] var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("executeUpdate(" + quote(var1) + ", " + quoteArray(var2) + ')');
         }

         long var3 = this.executeUpdateInternal(var1, var2);
         return var3 <= 2147483647L ? (int)var3 : -2;
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public final long executeLargeUpdate(String var1, String[] var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("executeLargeUpdate(" + quote(var1) + ", " + quoteArray(var2) + ')');
         }

         return this.executeUpdateInternal(var1, var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public final boolean execute(String var1, int var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("execute(" + quote(var1) + ", " + var2 + ')');
         }

         return this.executeInternal(var1, var2 == 1);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public final boolean execute(String var1, int[] var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("execute(" + quote(var1) + ", " + quoteIntArray(var2) + ')');
         }

         return this.executeInternal(var1, var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public final boolean execute(String var1, String[] var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("execute(" + quote(var1) + ", " + quoteArray(var2) + ')');
         }

         return this.executeInternal(var1, var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public int getResultSetHoldability() throws SQLException {
      try {
         this.debugCodeCall("getResultSetHoldability");
         this.checkClosed();
         return 1;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void closeOnCompletion() throws SQLException {
      try {
         this.debugCodeCall("closeOnCompletion");
         this.checkClosed();
         this.closeOnCompletion = true;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean isCloseOnCompletion() throws SQLException {
      try {
         this.debugCodeCall("isCloseOnCompletion");
         this.checkClosed();
         return this.closeOnCompletion;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   void closeIfCloseOnCompletion() {
      if (this.closeOnCompletion) {
         try {
            this.closeInternal();
         } catch (Exception var2) {
            this.logAndConvert(var2);
         }
      }

   }

   void checkClosed() {
      if (this.conn == null) {
         throw DbException.get(90007);
      } else {
         this.conn.checkClosed();
      }
   }

   protected void closeOldResultSet() {
      try {
         if (this.resultSet != null) {
            this.resultSet.closeInternal(true);
         }

         if (this.generatedKeys != null) {
            this.generatedKeys.closeInternal(true);
         }
      } finally {
         this.cancelled = false;
         this.resultSet = null;
         this.updateCount = -1L;
         this.generatedKeys = null;
      }

   }

   void setExecutingStatement(CommandInterface var1) {
      if (var1 == null) {
         this.conn.setExecutingStatement((Statement)null);
      } else {
         this.conn.setExecutingStatement(this);
      }

      this.executingCommand = var1;
   }

   void onLazyResultSetClose(CommandInterface var1, boolean var2) {
      this.setExecutingStatement((CommandInterface)null);
      var1.stop();
      if (var2) {
         var1.close();
      }

   }

   public boolean isClosed() throws SQLException {
      try {
         this.debugCodeCall("isClosed");
         return this.conn == null;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      try {
         if (this.isWrapperFor(var1)) {
            return this;
         } else {
            throw DbException.getInvalidValueException("iface", var1);
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      return var1 != null && var1.isAssignableFrom(this.getClass());
   }

   public boolean isPoolable() {
      this.debugCodeCall("isPoolable");
      return false;
   }

   public void setPoolable(boolean var1) {
      if (this.isDebugEnabled()) {
         this.debugCode("setPoolable(" + var1 + ')');
      }

   }

   public String enquoteIdentifier(String var1, boolean var2) throws SQLException {
      if (this.isSimpleIdentifier(var1)) {
         return var2 ? '"' + var1 + '"' : var1;
      } else {
         try {
            int var3 = var1.length();
            if (var3 > 0) {
               if (var1.charAt(0) == '"') {
                  checkQuotes(var1, 1, var3);
                  return var1;
               }

               if (var1.startsWith("U&\"") || var1.startsWith("u&\"")) {
                  checkQuotes(var1, 3, var3);
                  StringUtils.decodeUnicodeStringSQL(var1, 92);
                  return var1;
               }
            }

            return StringUtils.quoteIdentifier(var1);
         } catch (Exception var4) {
            throw this.logAndConvert(var4);
         }
      }
   }

   private static void checkQuotes(String var0, int var1, int var2) {
      boolean var3 = true;

      for(int var4 = var1; var4 < var2; ++var4) {
         if (var0.charAt(var4) == '"') {
            var3 = !var3;
         } else if (!var3) {
            throw DbException.get(42602, (String)var0);
         }
      }

      if (var3) {
         throw DbException.get(42602, (String)var0);
      }
   }

   public boolean isSimpleIdentifier(String var1) throws SQLException {
      Session.StaticSettings var2;
      try {
         this.checkClosed();
         var2 = this.conn.getStaticSettings();
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }

      return ParserUtil.isSimpleIdentifier(var1, var2.databaseToUpper, var2.databaseToLower);
   }

   public String toString() {
      return this.getTraceObjectName();
   }
}
