package org.h2.store;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.Properties;
import org.h2.Driver;
import org.h2.engine.Session;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceSystem;
import org.h2.store.fs.FileUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.value.Transfer;

public class FileLock implements Runnable {
   private static final String MAGIC = "FileLock";
   private static final String FILE = "file";
   private static final String SOCKET = "socket";
   private static final int RANDOM_BYTES = 16;
   private static final int SLEEP_GAP = 25;
   private static final int TIME_GRANULARITY = 2000;
   private volatile String fileName;
   private volatile ServerSocket serverSocket;
   private volatile boolean locked;
   private final int sleep;
   private final Trace trace;
   private long lastWrite;
   private String method;
   private Properties properties;
   private String uniqueId;
   private Thread watchdog;

   public FileLock(TraceSystem var1, String var2, int var3) {
      this.trace = var1 == null ? null : var1.getTrace(4);
      this.fileName = var2;
      this.sleep = var3;
   }

   public synchronized void lock(FileLockMethod var1) {
      this.checkServer();
      if (this.locked) {
         throw DbException.getInternalError("already locked");
      } else {
         switch (var1) {
            case FILE:
               this.lockFile();
               break;
            case SOCKET:
               this.lockSocket();
            case FS:
            case NO:
         }

         this.locked = true;
      }
   }

   public synchronized void unlock() {
      if (this.locked) {
         this.locked = false;

         try {
            if (this.watchdog != null) {
               this.watchdog.interrupt();
            }
         } catch (Exception var18) {
            this.trace.debug((Throwable)var18, (String)"unlock");
         }

         try {
            if (this.fileName != null && this.load().equals(this.properties)) {
               FileUtils.delete(this.fileName);
            }

            if (this.serverSocket != null) {
               this.serverSocket.close();
            }
         } catch (Exception var16) {
            this.trace.debug((Throwable)var16, (String)"unlock");
         } finally {
            this.fileName = null;
            this.serverSocket = null;
         }

         try {
            if (this.watchdog != null) {
               this.watchdog.join();
            }
         } catch (Exception var14) {
            this.trace.debug((Throwable)var14, (String)"unlock");
         } finally {
            this.watchdog = null;
         }

      }
   }

   public void setProperty(String var1, String var2) {
      if (var2 == null) {
         this.properties.remove(var1);
      } else {
         this.properties.put(var1, var2);
      }

   }

   public Properties save() {
      try {
         OutputStream var1 = FileUtils.newOutputStream(this.fileName, false);
         Throwable var2 = null;

         try {
            this.properties.store(var1, "FileLock");
         } catch (Throwable var12) {
            var2 = var12;
            throw var12;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var11) {
                     var2.addSuppressed(var11);
                  }
               } else {
                  var1.close();
               }
            }

         }

         this.lastWrite = aggressiveLastModified(this.fileName);
         if (this.trace.isDebugEnabled()) {
            this.trace.debug("save " + this.properties);
         }

         return this.properties;
      } catch (IOException var14) {
         throw getExceptionFatal("Could not save properties " + this.fileName, var14);
      }
   }

   private static long aggressiveLastModified(String var0) {
      try {
         FileChannel var1 = FileChannel.open(Paths.get(var0), FileUtils.RWS, FileUtils.NO_ATTRIBUTES);
         Throwable var2 = null;

         try {
            ByteBuffer var3 = ByteBuffer.wrap(new byte[1]);
            var1.read(var3);
         } catch (Throwable var12) {
            var2 = var12;
            throw var12;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var11) {
                     var2.addSuppressed(var11);
                  }
               } else {
                  var1.close();
               }
            }

         }
      } catch (IOException var14) {
      }

      return FileUtils.lastModified(var0);
   }

   private void checkServer() {
      Properties var1 = this.load();
      String var2 = var1.getProperty("server");
      if (var2 != null) {
         boolean var3 = false;
         String var4 = var1.getProperty("id");

         try {
            Socket var5 = NetUtils.createSocket((String)var2, 9092, false);
            Transfer var6 = new Transfer((Session)null, var5);
            var6.init();
            var6.writeInt(17);
            var6.writeInt(20);
            var6.writeString((String)null);
            var6.writeString((String)null);
            var6.writeString(var4);
            var6.writeInt(14);
            var6.flush();
            int var7 = var6.readInt();
            if (var7 == 1) {
               var3 = true;
            }

            var6.close();
            var5.close();
         } catch (IOException var8) {
            return;
         }

         if (var3) {
            DbException var9 = DbException.get(90020, "Server is running");
            throw var9.addSQL(var2 + "/" + var4);
         }
      }
   }

   public Properties load() {
      IOException var1 = null;
      int var2 = 0;

      while(var2 < 5) {
         try {
            SortedProperties var3 = SortedProperties.loadProperties(this.fileName);
            if (this.trace.isDebugEnabled()) {
               this.trace.debug("load " + var3);
            }

            return var3;
         } catch (IOException var4) {
            var1 = var4;
            ++var2;
         }
      }

      throw getExceptionFatal("Could not load properties " + this.fileName, var1);
   }

   private void waitUntilOld() {
      for(int var1 = 0; var1 < 160; ++var1) {
         long var2 = aggressiveLastModified(this.fileName);
         long var4 = System.currentTimeMillis() - var2;
         if (var4 < -2000L) {
            try {
               Thread.sleep(2L * (long)this.sleep);
            } catch (Exception var7) {
               this.trace.debug((Throwable)var7, (String)"sleep");
            }

            return;
         }

         if (var4 > 2000L) {
            return;
         }

         try {
            Thread.sleep(25L);
         } catch (Exception var8) {
            this.trace.debug((Throwable)var8, (String)"sleep");
         }
      }

      throw getExceptionFatal("Lock file recently modified", (Throwable)null);
   }

   private void setUniqueId() {
      byte[] var1 = MathUtils.secureRandomBytes(16);
      String var2 = StringUtils.convertBytesToHex(var1);
      this.uniqueId = Long.toHexString(System.currentTimeMillis()) + var2;
      this.properties.setProperty("id", this.uniqueId);
   }

   private void lockFile() {
      this.method = "file";
      this.properties = new SortedProperties();
      this.properties.setProperty("method", String.valueOf(this.method));
      this.setUniqueId();
      FileUtils.createDirectories(FileUtils.getParent(this.fileName));
      if (!FileUtils.createFile(this.fileName)) {
         this.waitUntilOld();
         String var1 = this.load().getProperty("method", "file");
         if (!var1.equals("file")) {
            throw getExceptionFatal("Unsupported lock method " + var1, (Throwable)null);
         }

         this.save();
         sleep(2 * this.sleep);
         if (!this.load().equals(this.properties)) {
            throw this.getExceptionAlreadyInUse("Locked by another process: " + this.fileName);
         }

         FileUtils.delete(this.fileName);
         if (!FileUtils.createFile(this.fileName)) {
            throw getExceptionFatal("Another process was faster", (Throwable)null);
         }
      }

      this.save();
      sleep(25);
      if (!this.load().equals(this.properties)) {
         this.fileName = null;
         throw getExceptionFatal("Concurrent update", (Throwable)null);
      } else {
         this.locked = true;
         this.watchdog = new Thread(this, "H2 File Lock Watchdog " + this.fileName);
         Driver.setThreadContextClassLoader(this.watchdog);
         this.watchdog.setDaemon(true);
         this.watchdog.setPriority(9);
         this.watchdog.start();
      }
   }

   private void lockSocket() {
      this.method = "socket";
      this.properties = new SortedProperties();
      this.properties.setProperty("method", String.valueOf(this.method));
      this.setUniqueId();
      String var1 = NetUtils.getLocalAddress();
      FileUtils.createDirectories(FileUtils.getParent(this.fileName));
      if (!FileUtils.createFile(this.fileName)) {
         this.waitUntilOld();
         long var2 = aggressiveLastModified(this.fileName);
         Properties var4 = this.load();
         String var5 = var4.getProperty("method", "socket");
         if (var5.equals("file")) {
            this.lockFile();
            return;
         }

         if (!var5.equals("socket")) {
            throw getExceptionFatal("Unsupported lock method " + var5, (Throwable)null);
         }

         String var6 = var4.getProperty("ipAddress", var1);
         if (!var1.equals(var6)) {
            throw this.getExceptionAlreadyInUse("Locked by another computer: " + var6);
         }

         String var7 = var4.getProperty("port", "0");
         int var8 = Integer.parseInt(var7);

         InetAddress var9;
         try {
            var9 = InetAddress.getByName(var6);
         } catch (UnknownHostException var13) {
            throw getExceptionFatal("Unknown host " + var6, var13);
         }

         int var10 = 0;

         while(true) {
            if (var10 >= 3) {
               if (var2 != aggressiveLastModified(this.fileName)) {
                  throw getExceptionFatal("Concurrent update", (Throwable)null);
               }

               FileUtils.delete(this.fileName);
               if (!FileUtils.createFile(this.fileName)) {
                  throw getExceptionFatal("Another process was faster", (Throwable)null);
               }
               break;
            }

            try {
               Socket var11 = new Socket(var9, var8);
               var11.close();
               throw this.getExceptionAlreadyInUse("Locked by another process");
            } catch (BindException var14) {
               throw getExceptionFatal("Bind Exception", (Throwable)null);
            } catch (ConnectException var15) {
               this.trace.debug((Throwable)var15, (String)("socket not connected to port " + var7));
               ++var10;
            } catch (IOException var16) {
               throw getExceptionFatal("IOException", (Throwable)null);
            }
         }
      }

      try {
         this.serverSocket = NetUtils.createServerSocket(0, false);
         int var17 = this.serverSocket.getLocalPort();
         this.properties.setProperty("ipAddress", var1);
         this.properties.setProperty("port", Integer.toString(var17));
      } catch (Exception var12) {
         this.trace.debug((Throwable)var12, (String)"lock");
         this.serverSocket = null;
         this.lockFile();
         return;
      }

      this.save();
      this.locked = true;
      this.watchdog = new Thread(this, "H2 File Lock Watchdog (Socket) " + this.fileName);
      this.watchdog.setDaemon(true);
      this.watchdog.start();
   }

   private static void sleep(int var0) {
      try {
         Thread.sleep((long)var0);
      } catch (InterruptedException var2) {
         throw getExceptionFatal("Sleep interrupted", var2);
      }
   }

   private static DbException getExceptionFatal(String var0, Throwable var1) {
      return DbException.get(8000, var1, var0);
   }

   private DbException getExceptionAlreadyInUse(String var1) {
      DbException var2 = DbException.get(90020, var1);
      if (this.fileName != null) {
         try {
            Properties var3 = this.load();
            String var4 = var3.getProperty("server");
            if (var4 != null) {
               String var5 = var4 + "/" + var3.getProperty("id");
               var2 = var2.addSQL(var5);
            }
         } catch (DbException var6) {
         }
      }

      return var2;
   }

   public static FileLockMethod getFileLockMethod(String var0) {
      if (var0 != null && !var0.equalsIgnoreCase("FILE")) {
         if (var0.equalsIgnoreCase("NO")) {
            return FileLockMethod.NO;
         } else if (var0.equalsIgnoreCase("SOCKET")) {
            return FileLockMethod.SOCKET;
         } else if (var0.equalsIgnoreCase("FS")) {
            return FileLockMethod.FS;
         } else {
            throw DbException.get(90060, var0);
         }
      } else {
         return FileLockMethod.FILE;
      }
   }

   public String getUniqueId() {
      return this.uniqueId;
   }

   public void run() {
      try {
         label41:
         while(true) {
            if (!this.locked || this.fileName == null) {
               while(true) {
                  ServerSocket var1 = this.serverSocket;
                  if (var1 == null) {
                     break label41;
                  }

                  try {
                     this.trace.debug("watchdog accept");
                     Socket var2 = var1.accept();
                     var2.close();
                  } catch (Exception var3) {
                     this.trace.debug((Throwable)var3, (String)"watchdog");
                  }
               }
            }

            try {
               if (!FileUtils.exists(this.fileName) || aggressiveLastModified(this.fileName) != this.lastWrite) {
                  this.save();
               }

               Thread.sleep((long)this.sleep);
            } catch (NullPointerException | InterruptedException | OutOfMemoryError var4) {
            } catch (Exception var5) {
               this.trace.debug((Throwable)var5, (String)"watchdog");
            }
         }
      } catch (Exception var6) {
         this.trace.debug((Throwable)var6, (String)"watchdog");
      }

      this.trace.debug("watchdog end");
   }
}
