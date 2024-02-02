package org.h2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils10;

public class TcpServer implements Service {
   private static final int SHUTDOWN_NORMAL = 0;
   private static final int SHUTDOWN_FORCE = 1;
   private static final String MANAGEMENT_DB_PREFIX = "management_db_";
   private static final ConcurrentHashMap<Integer, TcpServer> SERVERS = new ConcurrentHashMap();
   private int port;
   private boolean portIsSet;
   private boolean trace;
   private boolean ssl;
   private boolean stop;
   private ShutdownHandler shutdownHandler;
   private ServerSocket serverSocket;
   private final Set<TcpServerThread> running = Collections.synchronizedSet(new HashSet());
   private String baseDir;
   private boolean allowOthers;
   private boolean isDaemon;
   private boolean ifExists = true;
   private JdbcConnection managementDb;
   private PreparedStatement managementDbAdd;
   private PreparedStatement managementDbRemove;
   private String managementPassword = "";
   private Thread listenerThread;
   private int nextThreadId;
   private String key;
   private String keyDatabase;

   public static String getManagementDbName(int var0) {
      return "mem:management_db_" + var0;
   }

   private void initManagementDb() throws SQLException {
      if (this.managementPassword.isEmpty()) {
         this.managementPassword = StringUtils.convertBytesToHex(MathUtils.secureRandomBytes(32));
      }

      JdbcConnection var1 = new JdbcConnection("jdbc:h2:" + getManagementDbName(this.port), (Properties)null, "", this.managementPassword, false);
      this.managementDb = var1;
      Statement var2 = var1.createStatement();
      Throwable var3 = null;

      try {
         var2.execute("CREATE ALIAS IF NOT EXISTS STOP_SERVER FOR '" + TcpServer.class.getName() + ".stopServer'");
         var2.execute("CREATE TABLE IF NOT EXISTS SESSIONS(ID INT PRIMARY KEY, URL VARCHAR, `USER` VARCHAR, CONNECTED TIMESTAMP(9) WITH TIME ZONE)");
         this.managementDbAdd = var1.prepareStatement("INSERT INTO SESSIONS VALUES(?, ?, ?, CURRENT_TIMESTAMP(9))");
         this.managementDbRemove = var1.prepareStatement("DELETE FROM SESSIONS WHERE ID=?");
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

      SERVERS.put(this.port, this);
   }

   void shutdown() {
      if (this.shutdownHandler != null) {
         this.shutdownHandler.shutdown();
      }

   }

   public void setShutdownHandler(ShutdownHandler var1) {
      this.shutdownHandler = var1;
   }

   synchronized void addConnection(int var1, String var2, String var3) {
      try {
         this.managementDbAdd.setInt(1, var1);
         this.managementDbAdd.setString(2, var2);
         this.managementDbAdd.setString(3, var3);
         this.managementDbAdd.execute();
      } catch (SQLException var5) {
         DbException.traceThrowable(var5);
      }

   }

   synchronized void removeConnection(int var1) {
      try {
         this.managementDbRemove.setInt(1, var1);
         this.managementDbRemove.execute();
      } catch (SQLException var3) {
         DbException.traceThrowable(var3);
      }

   }

   private synchronized void stopManagementDb() {
      if (this.managementDb != null) {
         try {
            this.managementDb.close();
         } catch (SQLException var2) {
            DbException.traceThrowable(var2);
         }

         this.managementDb = null;
      }

   }

   public void init(String... var1) {
      this.port = 9092;

      for(int var2 = 0; var1 != null && var2 < var1.length; ++var2) {
         String var3 = var1[var2];
         if (Tool.isOption(var3, "-trace")) {
            this.trace = true;
         } else if (Tool.isOption(var3, "-tcpSSL")) {
            this.ssl = true;
         } else if (Tool.isOption(var3, "-tcpPort")) {
            ++var2;
            this.port = Integer.decode(var1[var2]);
            this.portIsSet = true;
         } else if (Tool.isOption(var3, "-tcpPassword")) {
            ++var2;
            this.managementPassword = var1[var2];
         } else if (Tool.isOption(var3, "-baseDir")) {
            ++var2;
            this.baseDir = var1[var2];
         } else if (Tool.isOption(var3, "-key")) {
            ++var2;
            this.key = var1[var2];
            ++var2;
            this.keyDatabase = var1[var2];
         } else if (Tool.isOption(var3, "-tcpAllowOthers")) {
            this.allowOthers = true;
         } else if (Tool.isOption(var3, "-tcpDaemon")) {
            this.isDaemon = true;
         } else if (Tool.isOption(var3, "-ifExists")) {
            this.ifExists = true;
         } else if (Tool.isOption(var3, "-ifNotExists")) {
            this.ifExists = false;
         }
      }

   }

   public String getURL() {
      return (this.ssl ? "ssl" : "tcp") + "://" + NetUtils.getLocalAddress() + ":" + this.port;
   }

   public int getPort() {
      return this.port;
   }

   public boolean getSSL() {
      return this.ssl;
   }

   boolean allow(Socket var1) {
      if (this.allowOthers) {
         return true;
      } else {
         try {
            return NetUtils.isLocalAddress(var1);
         } catch (UnknownHostException var3) {
            this.traceError(var3);
            return false;
         }
      }
   }

   public synchronized void start() throws SQLException {
      this.stop = false;

      try {
         this.serverSocket = NetUtils.createServerSocket(this.port, this.ssl);
      } catch (DbException var2) {
         if (this.portIsSet) {
            throw var2;
         }

         this.serverSocket = NetUtils.createServerSocket(0, this.ssl);
      }

      this.port = this.serverSocket.getLocalPort();
      this.initManagementDb();
   }

   public void listen() {
      this.listenerThread = Thread.currentThread();
      String var1 = this.listenerThread.getName();

      try {
         while(!this.stop) {
            Socket var2 = this.serverSocket.accept();
            Utils10.setTcpQuickack(var2, true);
            int var3 = this.nextThreadId++;
            TcpServerThread var4 = new TcpServerThread(var2, this, var3);
            this.running.add(var4);
            Thread var5 = new Thread(var4, var1 + " thread-" + var3);
            var5.setDaemon(this.isDaemon);
            var4.setThread(var5);
            var5.start();
         }

         this.serverSocket = NetUtils.closeSilently(this.serverSocket);
      } catch (Exception var6) {
         if (!this.stop) {
            DbException.traceThrowable(var6);
         }
      }

      this.stopManagementDb();
   }

   public synchronized boolean isRunning(boolean var1) {
      if (this.serverSocket == null) {
         return false;
      } else {
         try {
            Socket var2 = NetUtils.createLoopbackSocket(this.port, this.ssl);
            var2.close();
            return true;
         } catch (Exception var3) {
            if (var1) {
               this.traceError(var3);
            }

            return false;
         }
      }
   }

   public void stop() {
      SERVERS.remove(this.port);
      if (!this.stop) {
         this.stopManagementDb();
         this.stop = true;
         if (this.serverSocket != null) {
            try {
               this.serverSocket.close();
            } catch (IOException var6) {
               DbException.traceThrowable(var6);
            } catch (NullPointerException var7) {
            }

            this.serverSocket = null;
         }

         if (this.listenerThread != null) {
            try {
               this.listenerThread.join(1000L);
            } catch (InterruptedException var5) {
               DbException.traceThrowable(var5);
            }
         }
      }

      Iterator var1 = (new ArrayList(this.running)).iterator();

      while(var1.hasNext()) {
         TcpServerThread var2 = (TcpServerThread)var1.next();
         if (var2 != null) {
            var2.close();

            try {
               var2.getThread().join(100L);
            } catch (Exception var4) {
               DbException.traceThrowable(var4);
            }
         }
      }

   }

   public static void stopServer(int var0, String var1, int var2) {
      if (var0 == 0) {
         Integer[] var8 = (Integer[])SERVERS.keySet().toArray(new Integer[0]);
         int var9 = var8.length;

         for(int var5 = 0; var5 < var9; ++var5) {
            int var6 = var8[var5];
            if (var6 != 0) {
               stopServer(var6, var1, var2);
            }
         }

      } else {
         TcpServer var3 = (TcpServer)SERVERS.get(var0);
         if (var3 != null) {
            if (var3.managementPassword.equals(var1)) {
               if (var2 == 0) {
                  var3.stopManagementDb();
                  var3.stop = true;

                  try {
                     Socket var4 = NetUtils.createLoopbackSocket(var0, false);
                     var4.close();
                  } catch (Exception var7) {
                  }
               } else if (var2 == 1) {
                  var3.stop();
               }

               var3.shutdown();
            }
         }
      }
   }

   void remove(TcpServerThread var1) {
      this.running.remove(var1);
   }

   String getBaseDir() {
      return this.baseDir;
   }

   void trace(String var1) {
      if (this.trace) {
         System.out.println(var1);
      }

   }

   void traceError(Throwable var1) {
      if (this.trace) {
         var1.printStackTrace();
      }

   }

   public boolean getAllowOthers() {
      return this.allowOthers;
   }

   public String getType() {
      return "TCP";
   }

   public String getName() {
      return "H2 TCP Server";
   }

   boolean getIfExists() {
      return this.ifExists;
   }

   public static synchronized void shutdown(String var0, String var1, boolean var2, boolean var3) throws SQLException {
      try {
         int var4 = 9092;
         int var5 = var0.lastIndexOf(58);
         String var6;
         if (var5 >= 0) {
            var6 = var0.substring(var5 + 1);
            if (StringUtils.isNumber(var6)) {
               var4 = Integer.decode(var6);
            }
         }

         var6 = getManagementDbName(var4);
         int var7 = 0;

         while(var7 < 2) {
            try {
               JdbcConnection var8 = new JdbcConnection("jdbc:h2:" + var0 + '/' + var6, (Properties)null, "", var1, true);
               Throwable var9 = null;

               try {
                  PreparedStatement var10 = var8.prepareStatement("CALL STOP_SERVER(?, ?, ?)");
                  var10.setInt(1, var3 ? 0 : var4);
                  var10.setString(2, var1);
                  var10.setInt(3, var2 ? 1 : 0);

                  try {
                     var10.execute();
                  } catch (SQLException var22) {
                     if (!var2 && var22.getErrorCode() != 90067) {
                        throw var22;
                     }
                  }
                  break;
               } catch (Throwable var23) {
                  var9 = var23;
                  throw var23;
               } finally {
                  if (var8 != null) {
                     if (var9 != null) {
                        try {
                           var8.close();
                        } catch (Throwable var21) {
                           var9.addSuppressed(var21);
                        }
                     } else {
                        var8.close();
                     }
                  }

               }
            } catch (SQLException var25) {
               if (var7 == 1) {
                  throw var25;
               }

               ++var7;
            }
         }

      } catch (Exception var26) {
         throw DbException.toSQLException(var26);
      }
   }

   void cancelStatement(String var1, int var2) {
      Iterator var3 = (new ArrayList(this.running)).iterator();

      while(var3.hasNext()) {
         TcpServerThread var4 = (TcpServerThread)var3.next();
         if (var4 != null) {
            var4.cancelStatement(var1, var2);
         }
      }

   }

   public String checkKeyAndGetDatabaseName(String var1) {
      if (this.key == null) {
         return var1;
      } else if (this.key.equals(var1)) {
         return this.keyDatabase;
      } else {
         throw DbException.get(28000);
      }
   }

   public boolean isDaemon() {
      return this.isDaemon;
   }
}
