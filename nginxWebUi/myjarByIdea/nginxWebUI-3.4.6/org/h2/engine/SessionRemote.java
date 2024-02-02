package org.h2.engine;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.h2.api.DatabaseEventListener;
import org.h2.api.JavaObjectSerializer;
import org.h2.command.CommandInterface;
import org.h2.command.CommandRemote;
import org.h2.expression.ParameterInterface;
import org.h2.jdbc.JdbcException;
import org.h2.jdbc.meta.DatabaseMeta;
import org.h2.jdbc.meta.DatabaseMetaLegacy;
import org.h2.jdbc.meta.DatabaseMetaRemote;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceSystem;
import org.h2.result.ResultInterface;
import org.h2.store.DataHandler;
import org.h2.store.FileStore;
import org.h2.store.LobStorageFrontend;
import org.h2.store.fs.FileUtils;
import org.h2.util.DateTimeUtils;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.SmallLRUCache;
import org.h2.util.StringUtils;
import org.h2.util.TempFileDeleter;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.value.CompareMode;
import org.h2.value.Transfer;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueLob;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarchar;

public final class SessionRemote extends Session implements DataHandler {
   public static final int SESSION_PREPARE = 0;
   public static final int SESSION_CLOSE = 1;
   public static final int COMMAND_EXECUTE_QUERY = 2;
   public static final int COMMAND_EXECUTE_UPDATE = 3;
   public static final int COMMAND_CLOSE = 4;
   public static final int RESULT_FETCH_ROWS = 5;
   public static final int RESULT_RESET = 6;
   public static final int RESULT_CLOSE = 7;
   public static final int COMMAND_COMMIT = 8;
   public static final int CHANGE_ID = 9;
   public static final int COMMAND_GET_META_DATA = 10;
   public static final int SESSION_SET_ID = 12;
   public static final int SESSION_CANCEL_STATEMENT = 13;
   public static final int SESSION_CHECK_KEY = 14;
   public static final int SESSION_SET_AUTOCOMMIT = 15;
   public static final int SESSION_HAS_PENDING_TRANSACTION = 16;
   public static final int LOB_READ = 17;
   public static final int SESSION_PREPARE_READ_PARAMS2 = 18;
   public static final int GET_JDBC_META = 19;
   public static final int STATUS_ERROR = 0;
   public static final int STATUS_OK = 1;
   public static final int STATUS_CLOSED = 2;
   public static final int STATUS_OK_STATE_CHANGED = 3;
   private TraceSystem traceSystem;
   private Trace trace;
   private ArrayList<Transfer> transferList = Utils.newSmallArrayList();
   private int nextId;
   private boolean autoCommit = true;
   private ConnectionInfo connectionInfo;
   private String databaseName;
   private String cipher;
   private byte[] fileEncryptionKey;
   private final Object lobSyncObject = new Object();
   private String sessionId;
   private int clientVersion;
   private boolean autoReconnect;
   private int lastReconnect;
   private Session embedded;
   private DatabaseEventListener eventListener;
   private LobStorageFrontend lobStorage;
   private boolean cluster;
   private TempFileDeleter tempFileDeleter;
   private JavaObjectSerializer javaObjectSerializer;
   private final CompareMode compareMode = CompareMode.getInstance((String)null, 0);
   private final boolean oldInformationSchema;
   private String currentSchemaName;
   private volatile Session.DynamicSettings dynamicSettings;

   public SessionRemote(ConnectionInfo var1) {
      this.connectionInfo = var1;
      this.oldInformationSchema = var1.getProperty("OLD_INFORMATION_SCHEMA", false);
   }

   public ArrayList<String> getClusterServers() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.transferList.iterator();

      while(var2.hasNext()) {
         Transfer var3 = (Transfer)var2.next();
         var1.add(var3.getSocket().getInetAddress().getHostAddress() + ":" + var3.getSocket().getPort());
      }

      return var1;
   }

   private Transfer initTransfer(ConnectionInfo var1, String var2, String var3) throws IOException {
      Socket var4 = NetUtils.createSocket((String)var3, 9092, var1.isSSL(), var1.getProperty("NETWORK_TIMEOUT", 0));
      Transfer var5 = new Transfer(this, var4);
      var5.setSSL(var1.isSSL());
      var5.init();
      var5.writeInt(17);
      var5.writeInt(20);
      var5.writeString(var2);
      var5.writeString(var1.getOriginalURL());
      var5.writeString(var1.getUserName());
      var5.writeBytes(var1.getUserPasswordHash());
      var5.writeBytes(var1.getFilePasswordHash());
      String[] var6 = var1.getKeys();
      var5.writeInt(var6.length);
      String[] var7 = var6;
      int var8 = var6.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         String var10 = var7[var9];
         var5.writeString(var10).writeString(var1.getProperty(var10));
      }

      try {
         this.done(var5);
         this.clientVersion = var5.readInt();
         var5.setVersion(this.clientVersion);
         if (var1.getFileEncryptionKey() != null) {
            var5.writeBytes(var1.getFileEncryptionKey());
         }

         var5.writeInt(12);
         var5.writeString(this.sessionId);
         if (this.clientVersion >= 20) {
            TimeZoneProvider var12 = var1.getTimeZone();
            if (var12 == null) {
               var12 = DateTimeUtils.getTimeZone();
            }

            var5.writeString(var12.getId());
         }

         this.done(var5);
         this.autoCommit = var5.readBoolean();
         return var5;
      } catch (DbException var11) {
         var5.close();
         throw var11;
      }
   }

   public boolean hasPendingTransaction() {
      int var1 = 0;
      int var2 = 0;

      while(var1 < this.transferList.size()) {
         Transfer var3 = (Transfer)this.transferList.get(var1);

         try {
            this.traceOperation("SESSION_HAS_PENDING_TRANSACTION", 0);
            var3.writeInt(16);
            this.done(var3);
            return var3.readInt() != 0;
         } catch (IOException var5) {
            int var10002 = var1--;
            ++var2;
            this.removeServer(var5, var10002, var2);
            ++var1;
         }
      }

      return true;
   }

   public void cancel() {
   }

   public void cancelStatement(int var1) {
      Iterator var2 = this.transferList.iterator();

      while(var2.hasNext()) {
         Transfer var3 = (Transfer)var2.next();

         try {
            Transfer var4 = var3.openNewConnection();
            var4.init();
            var4.writeInt(this.clientVersion);
            var4.writeInt(this.clientVersion);
            var4.writeString((String)null);
            var4.writeString((String)null);
            var4.writeString(this.sessionId);
            var4.writeInt(13);
            var4.writeInt(var1);
            var4.close();
         } catch (IOException var5) {
            this.trace.debug((Throwable)var5, (String)"could not cancel statement");
         }
      }

   }

   private void checkClusterDisableAutoCommit(String var1) {
      if (this.autoCommit && this.transferList.size() > 1) {
         this.setAutoCommitSend(false);
         CommandInterface var2 = this.prepareCommand("SET CLUSTER " + var1, Integer.MAX_VALUE);
         var2.executeUpdate((Object)null);
         this.autoCommit = true;
         this.cluster = true;
      }

   }

   public int getClientVersion() {
      return this.clientVersion;
   }

   public boolean getAutoCommit() {
      return this.autoCommit;
   }

   public void setAutoCommit(boolean var1) {
      if (!this.cluster) {
         this.setAutoCommitSend(var1);
      }

      this.autoCommit = var1;
   }

   public void setAutoCommitFromServer(boolean var1) {
      if (this.cluster) {
         if (var1) {
            this.setAutoCommitSend(false);
            this.autoCommit = true;
         }
      } else {
         this.autoCommit = var1;
      }

   }

   private synchronized void setAutoCommitSend(boolean var1) {
      int var2 = 0;

      for(int var3 = 0; var2 < this.transferList.size(); ++var2) {
         Transfer var4 = (Transfer)this.transferList.get(var2);

         try {
            this.traceOperation("SESSION_SET_AUTOCOMMIT", var1 ? 1 : 0);
            var4.writeInt(15).writeBoolean(var1);
            this.done(var4);
         } catch (IOException var6) {
            int var10002 = var2--;
            ++var3;
            this.removeServer(var6, var10002, var3);
         }
      }

   }

   public void autoCommitIfCluster() {
      if (this.autoCommit && this.cluster) {
         int var1 = 0;

         for(int var2 = 0; var1 < this.transferList.size(); ++var1) {
            Transfer var3 = (Transfer)this.transferList.get(var1);

            try {
               this.traceOperation("COMMAND_COMMIT", 0);
               var3.writeInt(8);
               this.done(var3);
            } catch (IOException var5) {
               int var10002 = var1--;
               ++var2;
               this.removeServer(var5, var10002, var2);
            }
         }
      }

   }

   private String getFilePrefix(String var1) {
      StringBuilder var2 = new StringBuilder(var1);
      var2.append('/');

      for(int var3 = 0; var3 < this.databaseName.length(); ++var3) {
         char var4 = this.databaseName.charAt(var3);
         if (Character.isLetterOrDigit(var4)) {
            var2.append(var4);
         } else {
            var2.append('_');
         }
      }

      return var2.toString();
   }

   public Session connectEmbeddedOrServer(boolean var1) {
      ConnectionInfo var2 = this.connectionInfo;
      if (var2.isRemote()) {
         this.connectServer(var2);
         return this;
      } else {
         boolean var3 = var2.getProperty("AUTO_SERVER", false);
         ConnectionInfo var4 = null;

         try {
            if (var3) {
               var4 = var2.clone();
               this.connectionInfo = var2.clone();
            }

            if (var1) {
               var2.setProperty("OPEN_NEW", "true");
            }

            return Engine.createSession(var2);
         } catch (Exception var8) {
            DbException var6 = DbException.convert(var8);
            if (var6.getErrorCode() == 90020 && var3) {
               String var7 = ((JdbcException)var6.getSQLException()).getSQL();
               if (var7 != null) {
                  var4.setServerKey(var7);
                  var4.removeProperty("OPEN_NEW", (String)null);
                  this.connectServer(var4);
                  return this;
               }
            }

            throw var6;
         }
      }
   }

   private void connectServer(ConnectionInfo var1) {
      String var2 = var1.getName();
      if (var2.startsWith("//")) {
         var2 = var2.substring("//".length());
      }

      int var3 = var2.indexOf(47);
      if (var3 < 0) {
         throw var1.getFormatException();
      } else {
         this.databaseName = var2.substring(var3 + 1);
         String var4 = var2.substring(0, var3);
         this.traceSystem = new TraceSystem((String)null);
         String var5 = var1.getProperty(9, (String)null);
         String var7;
         if (var5 != null) {
            int var6 = Integer.parseInt(var5);
            var7 = this.getFilePrefix(SysProperties.CLIENT_TRACE_DIRECTORY);

            try {
               this.traceSystem.setLevelFile(var6);
               if (var6 > 0 && var6 < 4) {
                  String var8 = FileUtils.createTempFile(var7, ".trace.db", false);
                  this.traceSystem.setFileName(var8);
               }
            } catch (IOException var18) {
               throw DbException.convertIOException(var18, var7);
            }
         }

         String var21 = var1.getProperty(8, (String)null);
         if (var21 != null) {
            int var22 = Integer.parseInt(var21);
            this.traceSystem.setLevelSystemOut(var22);
         }

         this.trace = this.traceSystem.getTrace(6);
         var7 = null;
         if (var4.indexOf(44) >= 0) {
            var7 = StringUtils.quoteStringSQL(var4);
            var1.setProperty("CLUSTER", "TRUE");
         }

         this.autoReconnect = var1.getProperty("AUTO_RECONNECT", false);
         boolean var23 = var1.getProperty("AUTO_SERVER", false);
         if (var23 && var7 != null) {
            throw DbException.getUnsupportedException("autoServer && serverList != null");
         } else {
            this.autoReconnect |= var23;
            if (this.autoReconnect) {
               String var9 = var1.getProperty("DATABASE_EVENT_LISTENER");
               if (var9 != null) {
                  var9 = StringUtils.trim(var9, true, true, "'");

                  try {
                     this.eventListener = (DatabaseEventListener)JdbcUtils.loadUserClass(var9).getDeclaredConstructor().newInstance();
                  } catch (Throwable var17) {
                     throw DbException.convert(var17);
                  }
               }
            }

            this.cipher = var1.getProperty("CIPHER");
            if (this.cipher != null) {
               this.fileEncryptionKey = MathUtils.secureRandomBytes(32);
            }

            String[] var24 = StringUtils.arraySplit(var4, ',', true);
            int var10 = var24.length;
            this.transferList.clear();
            this.sessionId = StringUtils.convertBytesToHex(MathUtils.secureRandomBytes(32));
            boolean var11 = false;

            try {
               String[] var12 = var24;
               int var13 = var24.length;

               for(int var14 = 0; var14 < var13; ++var14) {
                  String var15 = var12[var14];

                  try {
                     Transfer var16 = this.initTransfer(var1, this.databaseName, var15);
                     this.transferList.add(var16);
                  } catch (IOException var19) {
                     if (var10 == 1) {
                        throw DbException.get(90067, var19, var19 + ": " + var15);
                     }

                     var11 = true;
                  }
               }

               this.checkClosed();
               if (var11) {
                  this.switchOffCluster();
               }

               this.checkClusterDisableAutoCommit(var7);
            } catch (DbException var20) {
               this.traceSystem.close();
               throw var20;
            }

            this.getDynamicSettings();
         }
      }
   }

   private void switchOffCluster() {
      CommandInterface var1 = this.prepareCommand("SET CLUSTER ''", Integer.MAX_VALUE);
      var1.executeUpdate((Object)null);
   }

   public void removeServer(IOException var1, int var2, int var3) {
      this.trace.debug((Throwable)var1, (String)"removing server because of exception");
      this.transferList.remove(var2);
      if (!this.transferList.isEmpty() || !this.autoReconnect(var3)) {
         this.checkClosed();
         this.switchOffCluster();
      }
   }

   public synchronized CommandInterface prepareCommand(String var1, int var2) {
      this.checkClosed();
      return new CommandRemote(this, this.transferList, var1, var2);
   }

   private boolean autoReconnect(int var1) {
      if (!this.isClosed()) {
         return false;
      } else if (!this.autoReconnect) {
         return false;
      } else if (!this.cluster && !this.autoCommit) {
         return false;
      } else if (var1 > SysProperties.MAX_RECONNECT) {
         return false;
      } else {
         ++this.lastReconnect;

         while(true) {
            try {
               this.embedded = this.connectEmbeddedOrServer(false);
               break;
            } catch (DbException var5) {
               if (var5.getErrorCode() != 90135) {
                  throw var5;
               }

               try {
                  Thread.sleep(500L);
               } catch (Exception var4) {
               }
            }
         }

         if (this.embedded == this) {
            this.embedded = null;
         } else {
            this.connectEmbeddedOrServer(true);
         }

         this.recreateSessionState();
         if (this.eventListener != null) {
            this.eventListener.setProgress(4, this.databaseName, (long)var1, (long)SysProperties.MAX_RECONNECT);
         }

         return true;
      }
   }

   public void checkClosed() {
      if (this.isClosed()) {
         throw DbException.get(90067, "session closed");
      }
   }

   public void close() {
      RuntimeException var1 = null;
      if (this.transferList != null) {
         synchronized(this) {
            Iterator var3 = this.transferList.iterator();

            while(true) {
               if (!var3.hasNext()) {
                  break;
               }

               Transfer var4 = (Transfer)var3.next();

               try {
                  this.traceOperation("SESSION_CLOSE", 0);
                  var4.writeInt(1);
                  this.done(var4);
                  var4.close();
               } catch (RuntimeException var7) {
                  this.trace.error(var7, "close");
                  var1 = var7;
               } catch (Exception var8) {
                  this.trace.error(var8, "close");
               }
            }
         }

         this.transferList = null;
      }

      this.traceSystem.close();
      if (this.embedded != null) {
         this.embedded.close();
         this.embedded = null;
      }

      if (var1 != null) {
         throw var1;
      }
   }

   public Trace getTrace() {
      return this.traceSystem.getTrace(6);
   }

   public int getNextId() {
      return this.nextId++;
   }

   public int getCurrentId() {
      return this.nextId;
   }

   public void done(Transfer var1) throws IOException {
      var1.flush();
      int var2 = var1.readInt();
      switch (var2) {
         case 0:
            throw readException(var1);
         case 1:
            break;
         case 2:
            this.transferList = null;
            break;
         case 3:
            this.sessionStateChanged = true;
            this.currentSchemaName = null;
            this.dynamicSettings = null;
            break;
         default:
            throw DbException.get(90067, "unexpected status " + var2);
      }

   }

   public static DbException readException(Transfer var0) throws IOException {
      String var1 = var0.readString();
      String var2 = var0.readString();
      String var3 = var0.readString();
      int var4 = var0.readInt();
      String var5 = var0.readString();
      SQLException var6 = DbException.getJdbcSQLException(var2, var3, var1, var4, (Throwable)null, var5);
      if (var4 == 90067) {
         throw new IOException(var6.toString(), var6);
      } else {
         return DbException.convert(var6);
      }
   }

   public boolean isClustered() {
      return this.cluster;
   }

   public boolean isClosed() {
      return this.transferList == null || this.transferList.isEmpty();
   }

   public void traceOperation(String var1, int var2) {
      if (this.trace.isDebugEnabled()) {
         this.trace.debug("{0} {1}", var1, var2);
      }

   }

   public void checkPowerOff() {
   }

   public void checkWritingAllowed() {
   }

   public String getDatabasePath() {
      return "";
   }

   public int getMaxLengthInplaceLob() {
      return SysProperties.LOB_CLIENT_MAX_SIZE_MEMORY;
   }

   public FileStore openFile(String var1, String var2, boolean var3) {
      if (var3 && !FileUtils.exists(var1)) {
         throw DbException.get(90124, var1);
      } else {
         FileStore var4;
         if (this.cipher == null) {
            var4 = FileStore.open(this, var1, var2);
         } else {
            var4 = FileStore.open(this, var1, var2, this.cipher, this.fileEncryptionKey, 0);
         }

         var4.setCheckedWriting(false);

         try {
            var4.init();
            return var4;
         } catch (DbException var6) {
            var4.closeSilently();
            throw var6;
         }
      }
   }

   public DataHandler getDataHandler() {
      return this;
   }

   public Object getLobSyncObject() {
      return this.lobSyncObject;
   }

   public SmallLRUCache<String, String[]> getLobFileListCache() {
      return null;
   }

   public int getLastReconnect() {
      return this.lastReconnect;
   }

   public TempFileDeleter getTempFileDeleter() {
      if (this.tempFileDeleter == null) {
         this.tempFileDeleter = TempFileDeleter.getInstance();
      }

      return this.tempFileDeleter;
   }

   public LobStorageFrontend getLobStorage() {
      if (this.lobStorage == null) {
         this.lobStorage = new LobStorageFrontend(this);
      }

      return this.lobStorage;
   }

   public synchronized int readLob(long var1, byte[] var3, long var4, byte[] var6, int var7, int var8) {
      this.checkClosed();
      int var9 = 0;
      int var10 = 0;

      while(var9 < this.transferList.size()) {
         Transfer var11 = (Transfer)this.transferList.get(var9);

         try {
            this.traceOperation("LOB_READ", (int)var1);
            var11.writeInt(17);
            var11.writeLong(var1);
            var11.writeBytes(var3);
            var11.writeLong(var4);
            var11.writeInt(var8);
            this.done(var11);
            var8 = var11.readInt();
            if (var8 <= 0) {
               return var8;
            }

            var11.readBytes(var6, var7, var8);
            return var8;
         } catch (IOException var13) {
            int var10002 = var9--;
            ++var10;
            this.removeServer(var13, var10002, var10);
            ++var9;
         }
      }

      return 1;
   }

   public JavaObjectSerializer getJavaObjectSerializer() {
      if (this.dynamicSettings == null) {
         this.getDynamicSettings();
      }

      return this.javaObjectSerializer;
   }

   public ValueLob addTemporaryLob(ValueLob var1) {
      return var1;
   }

   public CompareMode getCompareMode() {
      return this.compareMode;
   }

   public boolean isRemote() {
      return true;
   }

   public String getCurrentSchemaName() {
      String var1 = this.currentSchemaName;
      if (var1 == null) {
         synchronized(this) {
            CommandInterface var3 = this.prepareCommand("CALL SCHEMA()", 1);
            Throwable var4 = null;

            try {
               ResultInterface var5 = var3.executeQuery(1L, false);
               Throwable var6 = null;

               try {
                  var5.next();
                  this.currentSchemaName = var1 = var5.currentRow()[0].getString();
               } catch (Throwable var32) {
                  var6 = var32;
                  throw var32;
               } finally {
                  if (var5 != null) {
                     if (var6 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var31) {
                           var6.addSuppressed(var31);
                        }
                     } else {
                        var5.close();
                     }
                  }

               }
            } catch (Throwable var34) {
               var4 = var34;
               throw var34;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var30) {
                        var4.addSuppressed(var30);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         }
      }

      return var1;
   }

   public synchronized void setCurrentSchemaName(String var1) {
      this.currentSchemaName = null;
      CommandInterface var2 = this.prepareCommand(StringUtils.quoteIdentifier(new StringBuilder("SET SCHEMA "), var1).toString(), 0);
      Throwable var3 = null;

      try {
         var2.executeUpdate((Object)null);
         this.currentSchemaName = var1;
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   public void setNetworkConnectionInfo(NetworkConnectionInfo var1) {
   }

   public IsolationLevel getIsolationLevel() {
      CommandInterface var1;
      Throwable var2;
      ResultInterface var3;
      Throwable var4;
      Object var5;
      if (this.clientVersion >= 19) {
         var1 = this.prepareCommand(!this.isOldInformationSchema() ? "SELECT ISOLATION_LEVEL FROM INFORMATION_SCHEMA.SESSIONS WHERE SESSION_ID = SESSION_ID()" : "SELECT ISOLATION_LEVEL FROM INFORMATION_SCHEMA.SESSIONS WHERE ID = SESSION_ID()", 1);
         var2 = null;

         try {
            var3 = var1.executeQuery(1L, false);
            var4 = null;

            try {
               var3.next();
               var5 = IsolationLevel.fromSql(var3.currentRow()[0].getString());
            } catch (Throwable var77) {
               var5 = var77;
               var4 = var77;
               throw var77;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var76) {
                        var4.addSuppressed(var76);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         } catch (Throwable var83) {
            var2 = var83;
            throw var83;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var73) {
                     var2.addSuppressed(var73);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return (IsolationLevel)var5;
      } else {
         var1 = this.prepareCommand("CALL LOCK_MODE()", 1);
         var2 = null;

         try {
            var3 = var1.executeQuery(1L, false);
            var4 = null;

            try {
               var3.next();
               var5 = IsolationLevel.fromLockMode(var3.currentRow()[0].getInt());
            } catch (Throwable var78) {
               var5 = var78;
               var4 = var78;
               throw var78;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var75) {
                        var4.addSuppressed(var75);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         } catch (Throwable var80) {
            var2 = var80;
            throw var80;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var74) {
                     var2.addSuppressed(var74);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return (IsolationLevel)var5;
      }
   }

   public void setIsolationLevel(IsolationLevel var1) {
      CommandInterface var2;
      Throwable var3;
      if (this.clientVersion >= 19) {
         var2 = this.prepareCommand("SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL " + var1.getSQL(), 0);
         var3 = null;

         try {
            var2.executeUpdate((Object)null);
         } catch (Throwable var27) {
            var3 = var27;
            throw var27;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var25) {
                     var3.addSuppressed(var25);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } else {
         var2 = this.prepareCommand("SET LOCK_MODE ?", 0);
         var3 = null;

         try {
            ((ParameterInterface)var2.getParameters().get(0)).setValue(ValueInteger.get(var1.getLockMode()), false);
            var2.executeUpdate((Object)null);
         } catch (Throwable var26) {
            var3 = var26;
            throw var26;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var24) {
                     var3.addSuppressed(var24);
                  }
               } else {
                  var2.close();
               }
            }

         }
      }

   }

   public Session.StaticSettings getStaticSettings() {
      Session.StaticSettings var1 = this.staticSettings;
      if (var1 == null) {
         boolean var2 = true;
         boolean var3 = false;
         boolean var4 = false;
         CommandInterface var5 = this.getSettingsCommand(" IN (?, ?, ?)");
         Throwable var6 = null;

         try {
            ArrayList var7 = var5.getParameters();
            ((ParameterInterface)var7.get(0)).setValue(ValueVarchar.get("DATABASE_TO_UPPER"), false);
            ((ParameterInterface)var7.get(1)).setValue(ValueVarchar.get("DATABASE_TO_LOWER"), false);
            ((ParameterInterface)var7.get(2)).setValue(ValueVarchar.get("CASE_INSENSITIVE_IDENTIFIERS"), false);
            ResultInterface var8 = var5.executeQuery(0L, false);
            Throwable var9 = null;

            try {
               while(var8.next()) {
                  Value[] var10 = var8.currentRow();
                  String var11 = var10[1].getString();
                  switch (var10[0].getString()) {
                     case "DATABASE_TO_UPPER":
                        var2 = Boolean.valueOf(var11);
                        break;
                     case "DATABASE_TO_LOWER":
                        var3 = Boolean.valueOf(var11);
                        break;
                     case "CASE_INSENSITIVE_IDENTIFIERS":
                        var4 = Boolean.valueOf(var11);
                  }
               }
            } catch (Throwable var35) {
               var9 = var35;
               throw var35;
            } finally {
               if (var8 != null) {
                  if (var9 != null) {
                     try {
                        var8.close();
                     } catch (Throwable var34) {
                        var9.addSuppressed(var34);
                     }
                  } else {
                     var8.close();
                  }
               }

            }
         } catch (Throwable var37) {
            var6 = var37;
            throw var37;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var33) {
                     var6.addSuppressed(var33);
                  }
               } else {
                  var5.close();
               }
            }

         }

         if (this.clientVersion < 18) {
            var4 = !var2;
         }

         this.staticSettings = var1 = new Session.StaticSettings(var2, var3, var4);
      }

      return var1;
   }

   public Session.DynamicSettings getDynamicSettings() {
      Session.DynamicSettings var1 = this.dynamicSettings;
      if (var1 == null) {
         String var2 = Mode.ModeEnum.REGULAR.name();
         TimeZoneProvider var3 = DateTimeUtils.getTimeZone();
         String var4 = null;
         CommandInterface var5 = this.getSettingsCommand(" IN (?, ?, ?)");
         Throwable var6 = null;

         try {
            ArrayList var7 = var5.getParameters();
            ((ParameterInterface)var7.get(0)).setValue(ValueVarchar.get("MODE"), false);
            ((ParameterInterface)var7.get(1)).setValue(ValueVarchar.get("TIME ZONE"), false);
            ((ParameterInterface)var7.get(2)).setValue(ValueVarchar.get("JAVA_OBJECT_SERIALIZER"), false);
            ResultInterface var8 = var5.executeQuery(0L, false);
            Throwable var9 = null;

            try {
               while(var8.next()) {
                  Value[] var10 = var8.currentRow();
                  String var11 = var10[1].getString();
                  switch (var10[0].getString()) {
                     case "MODE":
                        var2 = var11;
                        break;
                     case "TIME ZONE":
                        var3 = TimeZoneProvider.ofId(var11);
                        break;
                     case "JAVA_OBJECT_SERIALIZER":
                        var4 = var11;
                  }
               }
            } catch (Throwable var38) {
               var9 = var38;
               throw var38;
            } finally {
               if (var8 != null) {
                  if (var9 != null) {
                     try {
                        var8.close();
                     } catch (Throwable var36) {
                        var9.addSuppressed(var36);
                     }
                  } else {
                     var8.close();
                  }
               }

            }
         } catch (Throwable var40) {
            var6 = var40;
            throw var40;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var35) {
                     var6.addSuppressed(var35);
                  }
               } else {
                  var5.close();
               }
            }

         }

         Mode var42 = Mode.getInstance(var2);
         if (var42 == null) {
            var42 = Mode.getRegular();
         }

         this.dynamicSettings = var1 = new Session.DynamicSettings(var42, var3);
         if (var4 != null && !(var4 = var4.trim()).isEmpty() && !var4.equals("null")) {
            try {
               this.javaObjectSerializer = (JavaObjectSerializer)JdbcUtils.loadUserClass(var4).getDeclaredConstructor().newInstance();
            } catch (Exception var37) {
               throw DbException.convert(var37);
            }
         } else {
            this.javaObjectSerializer = null;
         }
      }

      return var1;
   }

   private CommandInterface getSettingsCommand(String var1) {
      return this.prepareCommand((!this.isOldInformationSchema() ? "SELECT SETTING_NAME, SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE SETTING_NAME" : "SELECT NAME, `VALUE` FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME") + var1, Integer.MAX_VALUE);
   }

   public ValueTimestampTimeZone currentTimestamp() {
      return DateTimeUtils.currentTimestamp(this.getDynamicSettings().timeZone);
   }

   public TimeZoneProvider currentTimeZone() {
      return this.getDynamicSettings().timeZone;
   }

   public Mode getMode() {
      return this.getDynamicSettings().mode;
   }

   public DatabaseMeta getDatabaseMeta() {
      return (DatabaseMeta)(this.clientVersion >= 20 ? new DatabaseMetaRemote(this, this.transferList) : new DatabaseMetaLegacy(this));
   }

   public boolean isOldInformationSchema() {
      return this.oldInformationSchema || this.clientVersion < 20;
   }

   public boolean zeroBasedEnums() {
      return false;
   }
}
