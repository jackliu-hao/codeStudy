package org.h2.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;
import org.h2.api.JavaObjectSerializer;
import org.h2.command.CommandInterface;
import org.h2.engine.CastDataProvider;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.IsolationLevel;
import org.h2.engine.Mode;
import org.h2.engine.Session;
import org.h2.engine.SessionRemote;
import org.h2.engine.SysProperties;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.result.ResultInterface;
import org.h2.util.CloseWatcher;
import org.h2.util.TimeZoneProvider;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueToObjectConverter;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;

public class JdbcConnection extends TraceObject implements Connection, JdbcConnectionBackwardsCompat, CastDataProvider {
   private static final String NUM_SERVERS = "numServers";
   private static final String PREFIX_SERVER = "server";
   private static boolean keepOpenStackTrace;
   private final String url;
   private final String user;
   private int holdability = 1;
   private Session session;
   private CommandInterface commit;
   private CommandInterface rollback;
   private CommandInterface getReadOnly;
   private CommandInterface getGeneratedKeys;
   private CommandInterface setQueryTimeout;
   private CommandInterface getQueryTimeout;
   private int savepointId;
   private String catalog;
   private Statement executingStatement;
   private final CloseWatcher watcher;
   private int queryTimeoutCache = -1;
   private Map<String, String> clientInfo;

   public JdbcConnection(String var1, Properties var2, String var3, Object var4, boolean var5) throws SQLException {
      try {
         ConnectionInfo var6 = new ConnectionInfo(var1, var2, var3, var4);
         if (var5) {
            var6.setProperty("FORBID_CREATION", "TRUE");
         }

         String var7 = SysProperties.getBaseDir();
         if (var7 != null) {
            var6.setBaseDir(var7);
         }

         this.session = (new SessionRemote(var6)).connectEmbeddedOrServer(false);
         this.setTrace(this.session.getTrace(), 1, getNextId(1));
         this.user = var6.getUserName();
         if (this.isInfoEnabled()) {
            this.trace.infoCode("Connection " + this.getTraceObjectName() + " = DriverManager.getConnection(" + quote(var6.getOriginalURL()) + ", " + quote(this.user) + ", \"\");");
         }

         this.url = var6.getURL();
         this.closeOld();
         this.watcher = CloseWatcher.register(this, this.session, keepOpenStackTrace);
      } catch (Exception var8) {
         throw this.logAndConvert(var8);
      }
   }

   public JdbcConnection(JdbcConnection var1) {
      this.session = var1.session;
      this.setTrace(this.session.getTrace(), 1, getNextId(1));
      this.user = var1.user;
      this.url = var1.url;
      this.catalog = var1.catalog;
      this.commit = var1.commit;
      this.getGeneratedKeys = var1.getGeneratedKeys;
      this.getQueryTimeout = var1.getQueryTimeout;
      this.getReadOnly = var1.getReadOnly;
      this.rollback = var1.rollback;
      this.watcher = null;
      if (var1.clientInfo != null) {
         this.clientInfo = new HashMap(var1.clientInfo);
      }

   }

   public JdbcConnection(Session var1, String var2, String var3) {
      this.session = var1;
      this.setTrace(var1.getTrace(), 1, getNextId(1));
      this.user = var2;
      this.url = var3;
      this.watcher = null;
   }

   private void closeOld() {
      while(true) {
         CloseWatcher var1 = CloseWatcher.pollUnclosed();
         if (var1 == null) {
            return;
         }

         try {
            var1.getCloseable().close();
         } catch (Exception var4) {
            this.trace.error(var4, "closing session");
         }

         keepOpenStackTrace = true;
         String var2 = var1.getOpenStackTrace();
         DbException var3 = DbException.get(90018);
         this.trace.error(var3, var2);
      }
   }

   public Statement createStatement() throws SQLException {
      try {
         int var1 = getNextId(8);
         this.debugCodeAssign("Statement", 8, var1, "createStatement()");
         this.checkClosed();
         return new JdbcStatement(this, var1, 1003, 1007);
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public Statement createStatement(int var1, int var2) throws SQLException {
      try {
         int var3 = getNextId(8);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("Statement", 8, var3, "createStatement(" + var1 + ", " + var2 + ')');
         }

         checkTypeConcurrency(var1, var2);
         this.checkClosed();
         return new JdbcStatement(this, var3, var1, var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public Statement createStatement(int var1, int var2, int var3) throws SQLException {
      try {
         int var4 = getNextId(8);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("Statement", 8, var4, "createStatement(" + var1 + ", " + var2 + ", " + var3 + ')');
         }

         checkTypeConcurrency(var1, var2);
         checkHoldability(var3);
         this.checkClosed();
         return new JdbcStatement(this, var4, var1, var2);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public PreparedStatement prepareStatement(String var1) throws SQLException {
      try {
         int var2 = getNextId(3);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("PreparedStatement", 3, var2, "prepareStatement(" + quote(var1) + ')');
         }

         this.checkClosed();
         var1 = translateSQL(var1);
         return new JdbcPreparedStatement(this, var1, var2, 1003, 1007, (Object)null);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public DatabaseMetaData getMetaData() throws SQLException {
      try {
         int var1 = getNextId(2);
         this.debugCodeAssign("DatabaseMetaData", 2, var1, "getMetaData()");
         this.checkClosed();
         return new JdbcDatabaseMetaData(this, this.trace, var1);
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public Session getSession() {
      return this.session;
   }

   public synchronized void close() throws SQLException {
      try {
         this.debugCodeCall("close");
         if (this.session != null) {
            CloseWatcher.unregister(this.watcher);
            this.session.cancel();
            synchronized(this.session) {
               if (this.executingStatement != null) {
                  try {
                     this.executingStatement.cancel();
                  } catch (SQLException | NullPointerException var18) {
                  }
               }

               try {
                  if (!this.session.isClosed()) {
                     try {
                        if (this.session.hasPendingTransaction()) {
                           try {
                              this.rollbackInternal();
                           } catch (DbException var19) {
                              if (var19.getErrorCode() != 90067 && var19.getErrorCode() != 90098) {
                                 throw var19;
                              }
                           }
                        }

                        this.closePreparedCommands();
                     } finally {
                        this.session.close();
                     }
                  }
               } finally {
                  this.session = null;
               }

            }
         }
      } catch (Throwable var23) {
         throw this.logAndConvert(var23);
      }
   }

   private void closePreparedCommands() {
      this.commit = closeAndSetNull(this.commit);
      this.rollback = closeAndSetNull(this.rollback);
      this.getReadOnly = closeAndSetNull(this.getReadOnly);
      this.getGeneratedKeys = closeAndSetNull(this.getGeneratedKeys);
      this.getQueryTimeout = closeAndSetNull(this.getQueryTimeout);
      this.setQueryTimeout = closeAndSetNull(this.setQueryTimeout);
   }

   private static CommandInterface closeAndSetNull(CommandInterface var0) {
      if (var0 != null) {
         var0.close();
      }

      return null;
   }

   public synchronized void setAutoCommit(boolean var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setAutoCommit(" + var1 + ')');
         }

         this.checkClosed();
         synchronized(this.session) {
            if (var1 && !this.session.getAutoCommit()) {
               this.commit();
            }

            this.session.setAutoCommit(var1);
         }
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public synchronized boolean getAutoCommit() throws SQLException {
      try {
         this.checkClosed();
         this.debugCodeCall("getAutoCommit");
         return this.session.getAutoCommit();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public synchronized void commit() throws SQLException {
      try {
         this.debugCodeCall("commit");
         this.checkClosed();
         if (SysProperties.FORCE_AUTOCOMMIT_OFF_ON_COMMIT && this.getAutoCommit()) {
            throw DbException.get(90147, "commit()");
         } else {
            this.commit = this.prepareCommand("COMMIT", this.commit);
            this.commit.executeUpdate((Object)null);
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public synchronized void rollback() throws SQLException {
      try {
         this.debugCodeCall("rollback");
         this.checkClosed();
         if (SysProperties.FORCE_AUTOCOMMIT_OFF_ON_COMMIT && this.getAutoCommit()) {
            throw DbException.get(90147, "rollback()");
         } else {
            this.rollbackInternal();
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         this.debugCodeCall("isClosed");
         return this.session == null || this.session.isClosed();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String nativeSQL(String var1) throws SQLException {
      try {
         this.debugCodeCall("nativeSQL", var1);
         this.checkClosed();
         return translateSQL(var1);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void setReadOnly(boolean var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setReadOnly(" + var1 + ')');
         }

         this.checkClosed();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isReadOnly() throws SQLException {
      try {
         this.debugCodeCall("isReadOnly");
         this.checkClosed();
         this.getReadOnly = this.prepareCommand("CALL READONLY()", this.getReadOnly);
         ResultInterface var1 = this.getReadOnly.executeQuery(0L, false);
         var1.next();
         return var1.currentRow()[0].getBoolean();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void setCatalog(String var1) throws SQLException {
      try {
         this.debugCodeCall("setCatalog", var1);
         this.checkClosed();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getCatalog() throws SQLException {
      try {
         this.debugCodeCall("getCatalog");
         this.checkClosed();
         if (this.catalog == null) {
            CommandInterface var1 = this.prepareCommand("CALL DATABASE()", Integer.MAX_VALUE);
            ResultInterface var2 = var1.executeQuery(0L, false);
            var2.next();
            this.catalog = var2.currentRow()[0].getString();
            var1.close();
         }

         return this.catalog;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
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

   public PreparedStatement prepareStatement(String var1, int var2, int var3) throws SQLException {
      try {
         int var4 = getNextId(3);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("PreparedStatement", 3, var4, "prepareStatement(" + quote(var1) + ", " + var2 + ", " + var3 + ')');
         }

         checkTypeConcurrency(var2, var3);
         this.checkClosed();
         var1 = translateSQL(var1);
         return new JdbcPreparedStatement(this, var1, var4, var2, var3, (Object)null);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void setTransactionIsolation(int var1) throws SQLException {
      try {
         this.debugCodeCall("setTransactionIsolation", (long)var1);
         this.checkClosed();
         if (!this.getAutoCommit()) {
            this.commit();
         }

         this.session.setIsolationLevel(IsolationLevel.fromJdbc(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   void setQueryTimeout(int var1) throws SQLException {
      try {
         this.debugCodeCall("setQueryTimeout", (long)var1);
         this.checkClosed();
         this.setQueryTimeout = this.prepareCommand("SET QUERY_TIMEOUT ?", this.setQueryTimeout);
         ((ParameterInterface)this.setQueryTimeout.getParameters().get(0)).setValue(ValueInteger.get(var1 * 1000), false);
         this.setQueryTimeout.executeUpdate((Object)null);
         this.queryTimeoutCache = var1;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   int getQueryTimeout() throws SQLException {
      try {
         if (this.queryTimeoutCache == -1) {
            this.checkClosed();
            this.getQueryTimeout = this.prepareCommand(!this.session.isOldInformationSchema() ? "SELECT SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE SETTING_NAME=?" : "SELECT `VALUE` FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME=?", this.getQueryTimeout);
            ((ParameterInterface)this.getQueryTimeout.getParameters().get(0)).setValue(ValueVarchar.get("QUERY_TIMEOUT"), false);
            ResultInterface var1 = this.getQueryTimeout.executeQuery(0L, false);
            var1.next();
            int var2 = var1.currentRow()[0].getInt();
            var1.close();
            if (var2 != 0) {
               var2 = (var2 + 999) / 1000;
            }

            this.queryTimeoutCache = var2;
         }

         return this.queryTimeoutCache;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getTransactionIsolation() throws SQLException {
      try {
         this.debugCodeCall("getTransactionIsolation");
         this.checkClosed();
         return this.session.getIsolationLevel().getJdbc();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void setHoldability(int var1) throws SQLException {
      try {
         this.debugCodeCall("setHoldability", (long)var1);
         this.checkClosed();
         checkHoldability(var1);
         this.holdability = var1;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getHoldability() throws SQLException {
      try {
         this.debugCodeCall("getHoldability");
         this.checkClosed();
         return this.holdability;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public Map<String, Class<?>> getTypeMap() throws SQLException {
      try {
         this.debugCodeCall("getTypeMap");
         this.checkClosed();
         return null;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void setTypeMap(Map<String, Class<?>> var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setTypeMap(" + quoteMap(var1) + ')');
         }

         checkMap(var1);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public CallableStatement prepareCall(String var1) throws SQLException {
      try {
         int var2 = getNextId(0);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("CallableStatement", 0, var2, "prepareCall(" + quote(var1) + ')');
         }

         this.checkClosed();
         var1 = translateSQL(var1);
         return new JdbcCallableStatement(this, var1, var2, 1003, 1007);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public CallableStatement prepareCall(String var1, int var2, int var3) throws SQLException {
      try {
         int var4 = getNextId(0);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("CallableStatement", 0, var4, "prepareCall(" + quote(var1) + ", " + var2 + ", " + var3 + ')');
         }

         checkTypeConcurrency(var2, var3);
         this.checkClosed();
         var1 = translateSQL(var1);
         return new JdbcCallableStatement(this, var1, var4, var2, var3);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public CallableStatement prepareCall(String var1, int var2, int var3, int var4) throws SQLException {
      try {
         int var5 = getNextId(0);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("CallableStatement", 0, var5, "prepareCall(" + quote(var1) + ", " + var2 + ", " + var3 + ", " + var4 + ')');
         }

         checkTypeConcurrency(var2, var3);
         checkHoldability(var4);
         this.checkClosed();
         var1 = translateSQL(var1);
         return new JdbcCallableStatement(this, var1, var5, var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public Savepoint setSavepoint() throws SQLException {
      try {
         int var1 = getNextId(6);
         this.debugCodeAssign("Savepoint", 6, var1, "setSavepoint()");
         this.checkClosed();
         CommandInterface var2 = this.prepareCommand("SAVEPOINT " + JdbcSavepoint.getName((String)null, this.savepointId), Integer.MAX_VALUE);
         var2.executeUpdate((Object)null);
         JdbcSavepoint var3 = new JdbcSavepoint(this, this.savepointId, (String)null, this.trace, var1);
         ++this.savepointId;
         return var3;
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public Savepoint setSavepoint(String var1) throws SQLException {
      try {
         int var2 = getNextId(6);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("Savepoint", 6, var2, "setSavepoint(" + quote(var1) + ')');
         }

         this.checkClosed();
         CommandInterface var3 = this.prepareCommand("SAVEPOINT " + JdbcSavepoint.getName(var1, 0), Integer.MAX_VALUE);
         var3.executeUpdate((Object)null);
         return new JdbcSavepoint(this, 0, var1, this.trace, var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void rollback(Savepoint var1) throws SQLException {
      try {
         JdbcSavepoint var2 = convertSavepoint(var1);
         if (this.isDebugEnabled()) {
            this.debugCode("rollback(" + var2.getTraceObjectName() + ')');
         }

         this.checkClosed();
         var2.rollback();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void releaseSavepoint(Savepoint var1) throws SQLException {
      try {
         this.debugCode("releaseSavepoint(savepoint)");
         this.checkClosed();
         convertSavepoint(var1).release();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private static JdbcSavepoint convertSavepoint(Savepoint var0) {
      if (!(var0 instanceof JdbcSavepoint)) {
         throw DbException.get(90063, String.valueOf(var0));
      } else {
         return (JdbcSavepoint)var0;
      }
   }

   public PreparedStatement prepareStatement(String var1, int var2, int var3, int var4) throws SQLException {
      try {
         int var5 = getNextId(3);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("PreparedStatement", 3, var5, "prepareStatement(" + quote(var1) + ", " + var2 + ", " + var3 + ", " + var4 + ')');
         }

         checkTypeConcurrency(var2, var3);
         checkHoldability(var4);
         this.checkClosed();
         var1 = translateSQL(var1);
         return new JdbcPreparedStatement(this, var1, var5, var2, var3, (Object)null);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public PreparedStatement prepareStatement(String var1, int var2) throws SQLException {
      try {
         int var3 = getNextId(3);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("PreparedStatement", 3, var3, "prepareStatement(" + quote(var1) + ", " + var2 + ')');
         }

         this.checkClosed();
         var1 = translateSQL(var1);
         return new JdbcPreparedStatement(this, var1, var3, 1003, 1007, var2 == 1);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public PreparedStatement prepareStatement(String var1, int[] var2) throws SQLException {
      try {
         int var3 = getNextId(3);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("PreparedStatement", 3, var3, "prepareStatement(" + quote(var1) + ", " + quoteIntArray(var2) + ')');
         }

         this.checkClosed();
         var1 = translateSQL(var1);
         return new JdbcPreparedStatement(this, var1, var3, 1003, 1007, var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public PreparedStatement prepareStatement(String var1, String[] var2) throws SQLException {
      try {
         int var3 = getNextId(3);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("PreparedStatement", 3, var3, "prepareStatement(" + quote(var1) + ", " + quoteArray(var2) + ')');
         }

         this.checkClosed();
         var1 = translateSQL(var1);
         return new JdbcPreparedStatement(this, var1, var3, 1003, 1007, var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   CommandInterface prepareCommand(String var1, int var2) {
      return this.session.prepareCommand(var1, var2);
   }

   private CommandInterface prepareCommand(String var1, CommandInterface var2) {
      return var2 == null ? this.session.prepareCommand(var1, Integer.MAX_VALUE) : var2;
   }

   private static int translateGetEnd(String var0, int var1, char var2) {
      int var3 = var0.length();
      int var4;
      switch (var2) {
         case '"':
            var4 = var0.indexOf(34, var1 + 1);
            if (var4 < 0) {
               throw DbException.getSyntaxError(var0, var1);
            }

            return var4;
         case '#':
         case '%':
         case '&':
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '.':
         default:
            throw DbException.getInternalError("c=" + var2);
         case '$':
            if (var1 < var3 - 1 && var0.charAt(var1 + 1) == '$' && (var1 == 0 || var0.charAt(var1 - 1) <= ' ')) {
               var4 = var0.indexOf("$$", var1 + 2);
               if (var4 < 0) {
                  throw DbException.getSyntaxError(var0, var1);
               } else {
                  return var4 + 1;
               }
            } else {
               return var1;
            }
         case '\'':
            var4 = var0.indexOf(39, var1 + 1);
            if (var4 < 0) {
               throw DbException.getSyntaxError(var0, var1);
            }

            return var4;
         case '-':
            checkRunOver(var1 + 1, var3, var0);
            if (var0.charAt(var1 + 1) == '-') {
               for(var1 += 2; var1 < var3 && (var2 = var0.charAt(var1)) != '\r' && var2 != '\n'; ++var1) {
               }
            }

            return var1;
         case '/':
            checkRunOver(var1 + 1, var3, var0);
            if (var0.charAt(var1 + 1) == '*') {
               var4 = var0.indexOf("*/", var1 + 2);
               if (var4 < 0) {
                  throw DbException.getSyntaxError(var0, var1);
               }

               var1 = var4 + 1;
            } else if (var0.charAt(var1 + 1) == '/') {
               for(var1 += 2; var1 < var3 && (var2 = var0.charAt(var1)) != '\r' && var2 != '\n'; ++var1) {
               }
            }

            return var1;
      }
   }

   private static String translateSQL(String var0) {
      return translateSQL(var0, true);
   }

   static String translateSQL(String var0, boolean var1) {
      if (var0 == null) {
         throw DbException.getInvalidValueException("SQL", (Object)null);
      } else {
         return var1 && var0.indexOf(123) >= 0 ? translateSQLImpl(var0) : var0;
      }
   }

   private static String translateSQLImpl(String var0) {
      int var1 = var0.length();
      char[] var2 = null;
      int var3 = 0;

      label131:
      for(int var4 = 0; var4 < var1; ++var4) {
         char var5 = var0.charAt(var4);
         switch (var5) {
            case '"':
            case '\'':
            case '-':
            case '/':
               var4 = translateGetEnd(var0, var4, var5);
               break;
            case '$':
               var4 = translateGetEnd(var0, var4, var5);
               break;
            case '{':
               ++var3;
               if (var2 == null) {
                  var2 = var0.toCharArray();
               }

               var2[var4] = ' ';

               while(Character.isSpaceChar(var2[var4])) {
                  ++var4;
                  checkRunOver(var4, var1, var0);
               }

               int var6 = var4;
               if (var2[var4] >= '0' && var2[var4] <= '9') {
                  var2[var4 - 1] = '{';

                  while(true) {
                     checkRunOver(var4, var1, var0);
                     var5 = var2[var4];
                     if (var5 == '}') {
                        --var3;
                        continue label131;
                     }

                     switch (var5) {
                        case '"':
                        case '\'':
                        case '-':
                        case '/':
                           var4 = translateGetEnd(var0, var4, var5);
                        default:
                           ++var4;
                     }
                  }
               } else {
                  if (var2[var4] == '?') {
                     ++var4;
                     checkRunOver(var4, var1, var0);

                     while(Character.isSpaceChar(var2[var4])) {
                        ++var4;
                        checkRunOver(var4, var1, var0);
                     }

                     if (var0.charAt(var4) != '=') {
                        throw DbException.getSyntaxError(var0, var4, "=");
                     }

                     ++var4;
                     checkRunOver(var4, var1, var0);

                     while(Character.isSpaceChar(var2[var4])) {
                        ++var4;
                        checkRunOver(var4, var1, var0);
                     }
                  }

                  while(!Character.isSpaceChar(var2[var4])) {
                     ++var4;
                     checkRunOver(var4, var1, var0);
                  }

                  int var7 = 0;
                  if (found(var0, var6, "fn")) {
                     var7 = 2;
                  } else {
                     if (found(var0, var6, "escape") || found(var0, var6, "call")) {
                        break;
                     }

                     if (found(var0, var6, "oj")) {
                        var7 = 2;
                     } else {
                        if (found(var0, var6, "ts") || found(var0, var6, "t") || found(var0, var6, "d")) {
                           break;
                        }

                        if (found(var0, var6, "params")) {
                           var7 = "params".length();
                        }
                     }
                  }

                  for(var4 = var6; var7 > 0; --var7) {
                     var2[var4] = ' ';
                     ++var4;
                  }
                  break;
               }
            case '}':
               --var3;
               if (var3 < 0) {
                  throw DbException.getSyntaxError(var0, var4);
               }

               var2[var4] = ' ';
         }
      }

      if (var3 != 0) {
         throw DbException.getSyntaxError(var0, var0.length() - 1);
      } else {
         if (var2 != null) {
            var0 = new String(var2);
         }

         return var0;
      }
   }

   private static void checkRunOver(int var0, int var1, String var2) {
      if (var0 >= var1) {
         throw DbException.getSyntaxError(var2, var0);
      }
   }

   private static boolean found(String var0, int var1, String var2) {
      return var0.regionMatches(true, var1, var2, 0, var2.length());
   }

   private static void checkTypeConcurrency(int var0, int var1) {
      switch (var0) {
         case 1003:
         case 1004:
         case 1005:
            switch (var1) {
               case 1007:
               case 1008:
                  return;
               default:
                  throw DbException.getInvalidValueException("resultSetConcurrency", var1);
            }
         default:
            throw DbException.getInvalidValueException("resultSetType", var0);
      }
   }

   private static void checkHoldability(int var0) {
      if (var0 != 1 && var0 != 2) {
         throw DbException.getInvalidValueException("resultSetHoldability", var0);
      }
   }

   protected void checkClosed() {
      if (this.session == null) {
         throw DbException.get(90007);
      } else if (this.session.isClosed()) {
         throw DbException.get(90121);
      }
   }

   String getURL() {
      this.checkClosed();
      return this.url;
   }

   String getUser() {
      this.checkClosed();
      return this.user;
   }

   private void rollbackInternal() {
      this.rollback = this.prepareCommand("ROLLBACK", this.rollback);
      this.rollback.executeUpdate((Object)null);
   }

   void setExecutingStatement(Statement var1) {
      this.executingStatement = var1;
   }

   public Clob createClob() throws SQLException {
      try {
         int var1 = getNextId(10);
         this.debugCodeAssign("Clob", 10, var1, "createClob()");
         this.checkClosed();
         return new JdbcClob(this, ValueVarchar.EMPTY, JdbcLob.State.NEW, var1);
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public Blob createBlob() throws SQLException {
      try {
         int var1 = getNextId(9);
         this.debugCodeAssign("Blob", 9, var1, "createClob()");
         this.checkClosed();
         return new JdbcBlob(this, ValueVarbinary.EMPTY, JdbcLob.State.NEW, var1);
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public NClob createNClob() throws SQLException {
      try {
         int var1 = getNextId(10);
         this.debugCodeAssign("NClob", 10, var1, "createNClob()");
         this.checkClosed();
         return new JdbcClob(this, ValueVarchar.EMPTY, JdbcLob.State.NEW, var1);
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public SQLXML createSQLXML() throws SQLException {
      try {
         int var1 = getNextId(17);
         this.debugCodeAssign("SQLXML", 17, var1, "createSQLXML()");
         this.checkClosed();
         return new JdbcSQLXML(this, ValueVarchar.EMPTY, JdbcLob.State.NEW, var1);
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public Array createArrayOf(String var1, Object[] var2) throws SQLException {
      try {
         int var3 = getNextId(16);
         this.debugCodeAssign("Array", 16, var3, "createArrayOf()");
         this.checkClosed();
         Value var4 = ValueToObjectConverter.objectToValue(this.session, var2, 40);
         return new JdbcArray(this, var4, var3);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public Struct createStruct(String var1, Object[] var2) throws SQLException {
      throw this.unsupported("Struct");
   }

   public synchronized boolean isValid(int var1) {
      try {
         this.debugCodeCall("isValid", (long)var1);
         if (this.session != null && !this.session.isClosed()) {
            this.getTransactionIsolation();
            return true;
         } else {
            return false;
         }
      } catch (Exception var3) {
         this.logAndConvert(var3);
         return false;
      }
   }

   public void setClientInfo(String var1, String var2) throws SQLClientInfoException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setClientInfo(" + quote(var1) + ", " + quote(var2) + ')');
         }

         this.checkClosed();
         if (!Objects.equals(var2, this.getClientInfo(var1))) {
            if (isInternalProperty(var1)) {
               throw new SQLClientInfoException("Property name '" + var1 + " is used internally by H2.", Collections.emptyMap());
            } else {
               Pattern var3 = this.getMode().supportedClientInfoPropertiesRegEx;
               if (var3 != null && var3.matcher(var1).matches()) {
                  if (this.clientInfo == null) {
                     this.clientInfo = new HashMap();
                  }

                  this.clientInfo.put(var1, var2);
               } else {
                  throw new SQLClientInfoException("Client info name '" + var1 + "' not supported.", Collections.emptyMap());
               }
            }
         }
      } catch (Exception var4) {
         throw convertToClientInfoException(this.logAndConvert(var4));
      }
   }

   private static boolean isInternalProperty(String var0) {
      return "numServers".equals(var0) || var0.startsWith("server");
   }

   private static SQLClientInfoException convertToClientInfoException(SQLException var0) {
      return var0 instanceof SQLClientInfoException ? (SQLClientInfoException)var0 : new SQLClientInfoException(var0.getMessage(), var0.getSQLState(), var0.getErrorCode(), (Map)null, (Throwable)null);
   }

   public void setClientInfo(Properties var1) throws SQLClientInfoException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setClientInfo(properties)");
         }

         this.checkClosed();
         if (this.clientInfo == null) {
            this.clientInfo = new HashMap();
         } else {
            this.clientInfo.clear();
         }

         Iterator var2 = var1.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            this.setClientInfo((String)var3.getKey(), (String)var3.getValue());
         }

      } catch (Exception var4) {
         throw convertToClientInfoException(this.logAndConvert(var4));
      }
   }

   public Properties getClientInfo() throws SQLException {
      try {
         this.debugCodeCall("getClientInfo");
         this.checkClosed();
         ArrayList var1 = this.session.getClusterServers();
         Properties var2 = new Properties();
         if (this.clientInfo != null) {
            Iterator var3 = this.clientInfo.entrySet().iterator();

            while(var3.hasNext()) {
               Map.Entry var4 = (Map.Entry)var3.next();
               var2.setProperty((String)var4.getKey(), (String)var4.getValue());
            }
         }

         var2.setProperty("numServers", Integer.toString(var1.size()));

         for(int var6 = 0; var6 < var1.size(); ++var6) {
            var2.setProperty("server" + var6, (String)var1.get(var6));
         }

         return var2;
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public String getClientInfo(String var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCodeCall("getClientInfo", var1);
         }

         this.checkClosed();
         if (var1 == null) {
            throw DbException.getInvalidValueException("name", (Object)null);
         } else {
            return this.getClientInfo().getProperty(var1);
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
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

   Value createClob(Reader var1, long var2) {
      if (var1 == null) {
         return ValueNull.INSTANCE;
      } else {
         if (var2 <= 0L) {
            var2 = -1L;
         }

         return this.session.addTemporaryLob(this.session.getDataHandler().getLobStorage().createClob(var1, var2));
      }
   }

   Value createBlob(InputStream var1, long var2) {
      if (var1 == null) {
         return ValueNull.INSTANCE;
      } else {
         if (var2 <= 0L) {
            var2 = -1L;
         }

         return this.session.addTemporaryLob(this.session.getDataHandler().getLobStorage().createBlob(var1, var2));
      }
   }

   public void setSchema(String var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCodeCall("setSchema", var1);
         }

         this.checkClosed();
         this.session.setCurrentSchemaName(var1);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getSchema() throws SQLException {
      try {
         this.debugCodeCall("getSchema");
         this.checkClosed();
         return this.session.getCurrentSchemaName();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void abort(Executor var1) {
   }

   public void setNetworkTimeout(Executor var1, int var2) {
   }

   public int getNetworkTimeout() {
      return 0;
   }

   static void checkMap(Map<String, Class<?>> var0) {
      if (var0 != null && var0.size() > 0) {
         throw DbException.getUnsupportedException("map.size > 0");
      }
   }

   public String toString() {
      return this.getTraceObjectName() + ": url=" + this.url + " user=" + this.user;
   }

   CompareMode getCompareMode() {
      return this.session.getDataHandler().getCompareMode();
   }

   public Mode getMode() {
      return this.session.getMode();
   }

   public Session.StaticSettings getStaticSettings() {
      this.checkClosed();
      return this.session.getStaticSettings();
   }

   public ValueTimestampTimeZone currentTimestamp() {
      Session var1 = this.session;
      if (var1 == null) {
         throw DbException.get(90007);
      } else {
         return var1.currentTimestamp();
      }
   }

   public TimeZoneProvider currentTimeZone() {
      Session var1 = this.session;
      if (var1 == null) {
         throw DbException.get(90007);
      } else {
         return var1.currentTimeZone();
      }
   }

   public JavaObjectSerializer getJavaObjectSerializer() {
      Session var1 = this.session;
      if (var1 == null) {
         throw DbException.get(90007);
      } else {
         return var1.getJavaObjectSerializer();
      }
   }

   public boolean zeroBasedEnums() {
      Session var1 = this.session;
      if (var1 == null) {
         throw DbException.get(90007);
      } else {
         return var1.zeroBasedEnums();
      }
   }
}
