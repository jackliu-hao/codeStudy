package org.h2.server.pg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.h2.message.DbException;
import org.h2.server.Service;
import org.h2.util.NetUtils;
import org.h2.util.Tool;
import org.h2.util.Utils10;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class PgServer implements Service {
   public static final int DEFAULT_PORT = 5435;
   public static final int PG_TYPE_VARCHAR = 1043;
   public static final int PG_TYPE_BOOL = 16;
   public static final int PG_TYPE_BYTEA = 17;
   public static final int PG_TYPE_BPCHAR = 1042;
   public static final int PG_TYPE_INT8 = 20;
   public static final int PG_TYPE_INT2 = 21;
   public static final int PG_TYPE_INT4 = 23;
   public static final int PG_TYPE_TEXT = 25;
   public static final int PG_TYPE_FLOAT4 = 700;
   public static final int PG_TYPE_FLOAT8 = 701;
   public static final int PG_TYPE_UNKNOWN = 705;
   public static final int PG_TYPE_INT2_ARRAY = 1005;
   public static final int PG_TYPE_INT4_ARRAY = 1007;
   public static final int PG_TYPE_VARCHAR_ARRAY = 1015;
   public static final int PG_TYPE_DATE = 1082;
   public static final int PG_TYPE_TIME = 1083;
   public static final int PG_TYPE_TIMETZ = 1266;
   public static final int PG_TYPE_TIMESTAMP = 1114;
   public static final int PG_TYPE_TIMESTAMPTZ = 1184;
   public static final int PG_TYPE_NUMERIC = 1700;
   private final HashSet<Integer> typeSet = new HashSet();
   private int port = 5435;
   private boolean portIsSet;
   private boolean stop;
   private boolean trace;
   private ServerSocket serverSocket;
   private final Set<PgServerThread> running = Collections.synchronizedSet(new HashSet());
   private final AtomicInteger pid = new AtomicInteger();
   private String baseDir;
   private boolean allowOthers;
   private boolean isDaemon;
   private boolean ifExists = true;
   private String key;
   private String keyDatabase;

   public void init(String... var1) {
      this.port = 5435;

      for(int var2 = 0; var1 != null && var2 < var1.length; ++var2) {
         String var3 = var1[var2];
         if (Tool.isOption(var3, "-trace")) {
            this.trace = true;
         } else if (Tool.isOption(var3, "-pgPort")) {
            ++var2;
            this.port = Integer.decode(var1[var2]);
            this.portIsSet = true;
         } else if (Tool.isOption(var3, "-baseDir")) {
            ++var2;
            this.baseDir = var1[var2];
         } else if (Tool.isOption(var3, "-pgAllowOthers")) {
            this.allowOthers = true;
         } else if (Tool.isOption(var3, "-pgDaemon")) {
            this.isDaemon = true;
         } else if (Tool.isOption(var3, "-ifExists")) {
            this.ifExists = true;
         } else if (Tool.isOption(var3, "-ifNotExists")) {
            this.ifExists = false;
         } else if (Tool.isOption(var3, "-key")) {
            ++var2;
            this.key = var1[var2];
            ++var2;
            this.keyDatabase = var1[var2];
         }
      }

   }

   boolean getTrace() {
      return this.trace;
   }

   void trace(String var1) {
      if (this.trace) {
         System.out.println(var1);
      }

   }

   synchronized void remove(PgServerThread var1) {
      this.running.remove(var1);
   }

   void traceError(Exception var1) {
      if (this.trace) {
         var1.printStackTrace();
      }

   }

   public String getURL() {
      return "pg://" + NetUtils.getLocalAddress() + ":" + this.port;
   }

   public int getPort() {
      return this.port;
   }

   private boolean allow(Socket var1) {
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

   public void start() {
      this.stop = false;

      try {
         this.serverSocket = NetUtils.createServerSocket(this.port, false);
      } catch (DbException var2) {
         if (this.portIsSet) {
            throw var2;
         }

         this.serverSocket = NetUtils.createServerSocket(0, false);
      }

      this.port = this.serverSocket.getLocalPort();
   }

   public void listen() {
      String var1 = Thread.currentThread().getName();

      try {
         while(!this.stop) {
            Socket var2 = this.serverSocket.accept();
            if (!this.allow(var2)) {
               this.trace("Connection not allowed");
               var2.close();
            } else {
               Utils10.setTcpQuickack(var2, true);
               PgServerThread var3 = new PgServerThread(var2, this);
               this.running.add(var3);
               int var4 = this.pid.incrementAndGet();
               var3.setProcessId(var4);
               Thread var5 = new Thread(var3, var1 + " thread-" + var4);
               var5.setDaemon(this.isDaemon);
               var3.setThread(var5);
               var5.start();
            }
         }
      } catch (Exception var6) {
         if (!this.stop) {
            var6.printStackTrace();
         }
      }

   }

   public void stop() {
      if (!this.stop) {
         this.stop = true;
         if (this.serverSocket != null) {
            try {
               this.serverSocket.close();
            } catch (IOException var5) {
               var5.printStackTrace();
            }

            this.serverSocket = null;
         }
      }

      Iterator var1 = (new ArrayList(this.running)).iterator();

      while(var1.hasNext()) {
         PgServerThread var2 = (PgServerThread)var1.next();
         var2.close();

         try {
            Thread var3 = var2.getThread();
            if (var3 != null) {
               var3.join(100L);
            }
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

   }

   public boolean isRunning(boolean var1) {
      if (this.serverSocket == null) {
         return false;
      } else {
         try {
            Socket var2 = NetUtils.createLoopbackSocket(this.serverSocket.getLocalPort(), false);
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

   PgServerThread getThread(int var1) {
      Iterator var2 = (new ArrayList(this.running)).iterator();

      PgServerThread var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (PgServerThread)var2.next();
      } while(var3.getProcessId() != var1);

      return var3;
   }

   String getBaseDir() {
      return this.baseDir;
   }

   public boolean getAllowOthers() {
      return this.allowOthers;
   }

   public String getType() {
      return "PG";
   }

   public String getName() {
      return "H2 PG Server";
   }

   boolean getIfExists() {
      return this.ifExists;
   }

   public static String formatType(int var0) {
      byte var1;
      switch (var0) {
         case 0:
            return "-";
         case 16:
            var1 = 8;
            break;
         case 17:
            var1 = 6;
            break;
         case 18:
            return "char";
         case 19:
            return "name";
         case 20:
            var1 = 12;
            break;
         case 21:
            var1 = 10;
            break;
         case 22:
            return "int2vector";
         case 23:
            var1 = 11;
            break;
         case 24:
            return "regproc";
         case 25:
            var1 = 3;
            break;
         case 700:
            var1 = 14;
            break;
         case 701:
            var1 = 15;
            break;
         case 1005:
            return "smallint[]";
         case 1007:
            return "integer[]";
         case 1015:
            return "character varying[]";
         case 1042:
            var1 = 1;
            break;
         case 1043:
            var1 = 2;
            break;
         case 1082:
            var1 = 17;
            break;
         case 1083:
            var1 = 18;
            break;
         case 1114:
            var1 = 20;
            break;
         case 1184:
            var1 = 21;
            break;
         case 1266:
            var1 = 19;
            break;
         case 1700:
            var1 = 13;
            break;
         case 2205:
            return "regclass";
         default:
            return "???";
      }

      return Value.getTypeName(var1);
   }

   public static int convertType(TypeInfo var0) {
      switch (var0.getValueType()) {
         case 0:
         case 3:
            return 25;
         case 1:
            return 1042;
         case 2:
            return 1043;
         case 4:
         case 7:
         case 9:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         default:
            return 705;
         case 5:
         case 6:
            return 17;
         case 8:
            return 16;
         case 10:
            return 21;
         case 11:
            return 23;
         case 12:
            return 20;
         case 13:
         case 16:
            return 1700;
         case 14:
            return 700;
         case 15:
            return 701;
         case 17:
            return 1082;
         case 18:
            return 1083;
         case 19:
            return 1266;
         case 20:
            return 1114;
         case 21:
            return 1184;
         case 40:
            var0 = (TypeInfo)var0.getExtTypeInfo();
            switch (var0.getValueType()) {
               case 2:
                  return 1015;
               case 10:
                  return 1005;
               case 11:
                  return 1007;
               default:
                  return 1015;
            }
      }
   }

   HashSet<Integer> getTypeSet() {
      return this.typeSet;
   }

   void checkType(int var1) {
      if (!this.typeSet.contains(var1)) {
         this.trace("Unsupported type: " + var1);
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
