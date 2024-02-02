package org.h2.server;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import org.h2.command.Command;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Engine;
import org.h2.engine.Session;
import org.h2.engine.SessionLocal;
import org.h2.engine.SysProperties;
import org.h2.expression.Parameter;
import org.h2.expression.ParameterInterface;
import org.h2.expression.ParameterRemote;
import org.h2.jdbc.JdbcException;
import org.h2.jdbc.meta.DatabaseMetaServer;
import org.h2.message.DbException;
import org.h2.result.ResultColumn;
import org.h2.result.ResultInterface;
import org.h2.result.ResultWithGeneratedKeys;
import org.h2.store.LobStorageInterface;
import org.h2.util.IOUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.SmallLRUCache;
import org.h2.util.SmallMap;
import org.h2.util.TimeZoneProvider;
import org.h2.value.Transfer;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueLob;

public class TcpServerThread implements Runnable {
   protected final Transfer transfer;
   private final TcpServer server;
   private SessionLocal session;
   private boolean stop;
   private Thread thread;
   private Command commit;
   private final SmallMap cache;
   private final SmallLRUCache<Long, CachedInputStream> lobs;
   private final int threadId;
   private int clientVersion;
   private String sessionId;
   private long lastRemoteSettingsId;

   TcpServerThread(Socket var1, TcpServer var2, int var3) {
      this.cache = new SmallMap(SysProperties.SERVER_CACHED_OBJECTS);
      this.lobs = SmallLRUCache.newInstance(Math.max(SysProperties.SERVER_CACHED_OBJECTS, SysProperties.SERVER_RESULT_SET_FETCH_SIZE * 5));
      this.server = var2;
      this.threadId = var3;
      this.transfer = new Transfer((Session)null, var1);
   }

   private void trace(String var1) {
      this.server.trace(this + " " + var1);
   }

   public void run() {
      try {
         this.transfer.init();
         this.trace("Connect");

         try {
            Socket var1 = this.transfer.getSocket();
            if (var1 == null) {
               return;
            }

            if (!this.server.allow(this.transfer.getSocket())) {
               throw DbException.get(90117);
            }

            int var2 = this.transfer.readInt();
            if (var2 < 6) {
               throw DbException.get(90047, Integer.toString(var2), "17");
            }

            int var3 = this.transfer.readInt();
            if (var3 < 17) {
               throw DbException.get(90047, Integer.toString(var3), "17");
            }

            if (var2 > 20) {
               throw DbException.get(90047, Integer.toString(var2), "20");
            }

            if (var3 >= 20) {
               this.clientVersion = 20;
            } else {
               this.clientVersion = var3;
            }

            this.transfer.setVersion(this.clientVersion);
            String var4 = this.transfer.readString();
            String var5 = this.transfer.readString();
            String var6;
            int var8;
            if (var4 == null && var5 == null) {
               var6 = this.transfer.readString();
               int var7 = this.transfer.readInt();
               this.stop = true;
               if (var7 == 13) {
                  var8 = this.transfer.readInt();
                  this.server.cancelStatement(var6, var8);
               } else if (var7 == 14) {
                  var4 = this.server.checkKeyAndGetDatabaseName(var6);
                  if (!var6.equals(var4)) {
                     this.transfer.writeInt(1);
                  } else {
                     this.transfer.writeInt(0);
                  }
               }
            }

            var6 = this.server.getBaseDir();
            if (var6 == null) {
               var6 = SysProperties.getBaseDir();
            }

            var4 = this.server.checkKeyAndGetDatabaseName(var4);
            ConnectionInfo var21 = new ConnectionInfo(var4);
            var21.setOriginalURL(var5);
            var21.setUserName(this.transfer.readString());
            var21.setUserPasswordHash(this.transfer.readBytes());
            var21.setFilePasswordHash(this.transfer.readBytes());
            var8 = this.transfer.readInt();

            for(int var9 = 0; var9 < var8; ++var9) {
               var21.setProperty(this.transfer.readString(), this.transfer.readString());
            }

            if (var6 != null) {
               var21.setBaseDir(var6);
            }

            if (this.server.getIfExists()) {
               var21.setProperty("FORBID_CREATION", "TRUE");
            }

            this.transfer.writeInt(1);
            this.transfer.writeInt(this.clientVersion);
            this.transfer.flush();
            if (var21.getFilePasswordHash() != null) {
               var21.setFileEncryptionKey(this.transfer.readBytes());
            }

            var21.setNetworkConnectionInfo(new NetworkConnectionInfo(NetUtils.ipToShortForm(new StringBuilder(this.server.getSSL() ? "ssl://" : "tcp://"), var1.getLocalAddress().getAddress(), true).append(':').append(var1.getLocalPort()).toString(), var1.getInetAddress().getAddress(), var1.getPort(), "" + 'P' + this.clientVersion));
            if (this.clientVersion < 20) {
               var21.setProperty("OLD_INFORMATION_SCHEMA", "TRUE");
               var21.setProperty("NON_KEYWORDS", "VALUE");
            }

            this.session = Engine.createSession(var21);
            this.transfer.setSession(this.session);
            this.server.addConnection(this.threadId, var5, var21.getUserName());
            this.trace("Connected");
            this.lastRemoteSettingsId = this.session.getDatabase().getRemoteSettingsId();
         } catch (OutOfMemoryError var17) {
            this.server.traceError(var17);
            this.sendError(var17, true);
            this.stop = true;
         } catch (Throwable var18) {
            this.sendError(var18, true);
            this.stop = true;
         }

         while(!this.stop) {
            try {
               this.process();
            } catch (Throwable var16) {
               this.sendError(var16, true);
            }
         }

         this.trace("Disconnect");
      } catch (Throwable var19) {
         this.server.traceError(var19);
      } finally {
         this.close();
      }

   }

   private void closeSession() {
      if (this.session != null) {
         RuntimeException var1 = null;

         try {
            this.session.close();
            this.server.removeConnection(this.threadId);
         } catch (RuntimeException var7) {
            var1 = var7;
            this.server.traceError(var7);
         } catch (Exception var8) {
            this.server.traceError(var8);
         } finally {
            this.session = null;
         }

         if (var1 != null) {
            throw var1;
         }
      }

   }

   void close() {
      try {
         this.stop = true;
         this.closeSession();
      } catch (Exception var5) {
         this.server.traceError(var5);
      } finally {
         this.transfer.close();
         this.trace("Close");
         this.server.remove(this);
      }

   }

   private void sendError(Throwable var1, boolean var2) {
      try {
         SQLException var3 = DbException.convert(var1).getSQLException();
         StringWriter var4 = new StringWriter();
         var3.printStackTrace(new PrintWriter(var4));
         String var5 = var4.toString();
         String var6;
         String var7;
         if (var3 instanceof JdbcException) {
            JdbcException var8 = (JdbcException)var3;
            var6 = var8.getOriginalMessage();
            var7 = var8.getSQL();
         } else {
            var6 = var3.getMessage();
            var7 = null;
         }

         if (var2) {
            this.transfer.writeInt(0);
         }

         this.transfer.writeString(var3.getSQLState()).writeString(var6).writeString(var7).writeInt(var3.getErrorCode()).writeString(var5).flush();
      } catch (Exception var9) {
         if (!this.transfer.isClosed()) {
            this.server.traceError(var9);
         }

         this.stop = true;
      }

   }

   private void setParameters(Command var1) throws IOException {
      int var2 = this.transfer.readInt();
      ArrayList var3 = var1.getParameters();

      for(int var4 = 0; var4 < var2; ++var4) {
         Parameter var5 = (Parameter)var3.get(var4);
         var5.setValue(this.transfer.readValue((TypeInfo)null));
      }

   }

   private void process() throws IOException {
      int var1 = this.transfer.readInt();
      int var2;
      int var3;
      int var7;
      int var8;
      int var11;
      long var12;
      int var14;
      ResultInterface var22;
      Command var25;
      int var32;
      switch (var1) {
         case 0:
         case 18:
            var2 = this.transfer.readInt();
            String var29 = this.transfer.readString();
            int var35 = this.session.getModificationId();
            Command var37 = this.session.prepareLocal(var29);
            boolean var36 = var37.isReadOnly();
            this.cache.addObject(var2, var37);
            boolean var46 = var37.isQuery();
            this.transfer.writeInt(this.getState(var35)).writeBoolean(var46).writeBoolean(var36);
            if (var1 != 0) {
               this.transfer.writeInt(var37.getCommandType());
            }

            ArrayList var48 = var37.getParameters();
            this.transfer.writeInt(var48.size());
            if (var1 != 0) {
               Iterator var52 = var48.iterator();

               while(var52.hasNext()) {
                  ParameterInterface var50 = (ParameterInterface)var52.next();
                  ParameterRemote.writeMetaData(this.transfer, var50);
               }
            }

            this.transfer.flush();
            break;
         case 1:
            this.stop = true;
            this.closeSession();
            this.transfer.writeInt(1).flush();
            this.close();
            break;
         case 2:
            var2 = this.transfer.readInt();
            var3 = this.transfer.readInt();
            long var33 = this.transfer.readRowCount();
            var32 = this.transfer.readInt();
            Command var43 = (Command)this.cache.getObject(var2, false);
            this.setParameters(var43);
            var8 = this.session.getModificationId();
            ResultInterface var51;
            synchronized(this.session) {
               var51 = var43.executeQuery(var33, false);
            }

            this.cache.addObject(var3, var51);
            int var49 = var51.getVisibleColumnCount();
            var11 = this.getState(var8);
            this.transfer.writeInt(var11).writeInt(var49);
            var12 = var51.isLazy() ? -1L : var51.getRowCount();
            this.transfer.writeRowCount(var12);

            for(var14 = 0; var14 < var49; ++var14) {
               ResultColumn.writeColumn(this.transfer, var51, var14);
            }

            this.sendRows(var51, var12 >= 0L ? Math.min(var12, (long)var32) : (long)var32);
            this.transfer.flush();
            break;
         case 3:
            var2 = this.transfer.readInt();
            var25 = (Command)this.cache.getObject(var2, false);
            this.setParameters(var25);
            boolean var28 = true;
            var32 = this.transfer.readInt();
            Object var34;
            int var44;
            switch (var32) {
               case 0:
                  var34 = false;
                  var28 = false;
                  break;
               case 1:
                  var34 = true;
                  break;
               case 2:
                  var7 = this.transfer.readInt();
                  int[] var42 = new int[var7];

                  for(var44 = 0; var44 < var7; ++var44) {
                     var42[var44] = this.transfer.readInt();
                  }

                  var34 = var42;
                  break;
               case 3:
                  var7 = this.transfer.readInt();
                  String[] var40 = new String[var7];

                  for(var44 = 0; var44 < var7; ++var44) {
                     var40[var44] = this.transfer.readString();
                  }

                  var34 = var40;
                  break;
               default:
                  throw DbException.get(90067, "Unsupported generated keys' mode " + var32);
            }

            var7 = this.session.getModificationId();
            ResultWithGeneratedKeys var45;
            synchronized(this.session) {
               var45 = var25.executeUpdate(var34);
            }

            if (this.session.isClosed()) {
               var44 = 2;
               this.stop = true;
            } else {
               var44 = this.getState(var7);
            }

            this.transfer.writeInt(var44);
            this.transfer.writeRowCount(var45.getUpdateCount());
            this.transfer.writeBoolean(this.session.getAutoCommit());
            if (var28) {
               ResultInterface var47 = var45.getGeneratedKeys();
               var11 = var47.getVisibleColumnCount();
               this.transfer.writeInt(var11);
               var12 = var47.getRowCount();
               this.transfer.writeRowCount(var12);

               for(var14 = 0; var14 < var11; ++var14) {
                  ResultColumn.writeColumn(this.transfer, var47, var14);
               }

               this.sendRows(var47, var12);
               var47.close();
            }

            this.transfer.flush();
            break;
         case 4:
            var2 = this.transfer.readInt();
            var25 = (Command)this.cache.getObject(var2, true);
            if (var25 != null) {
               var25.close();
               this.cache.freeObject(var2);
            }
            break;
         case 5:
            var2 = this.transfer.readInt();
            var3 = this.transfer.readInt();
            ResultInterface var27 = (ResultInterface)this.cache.getObject(var2, false);
            this.transfer.writeInt(1);
            this.sendRows(var27, (long)var3);
            this.transfer.flush();
            break;
         case 6:
            var2 = this.transfer.readInt();
            var22 = (ResultInterface)this.cache.getObject(var2, false);
            var22.reset();
            break;
         case 7:
            var2 = this.transfer.readInt();
            var22 = (ResultInterface)this.cache.getObject(var2, true);
            if (var22 != null) {
               var22.close();
               this.cache.freeObject(var2);
            }
            break;
         case 8:
            if (this.commit == null) {
               this.commit = this.session.prepareLocal("COMMIT");
            }

            var2 = this.session.getModificationId();
            this.commit.executeUpdate((Object)null);
            this.transfer.writeInt(this.getState(var2)).flush();
            break;
         case 9:
            var2 = this.transfer.readInt();
            var3 = this.transfer.readInt();
            Object var26 = this.cache.getObject(var2, false);
            this.cache.freeObject(var2);
            this.cache.addObject(var3, var26);
            break;
         case 10:
            var2 = this.transfer.readInt();
            var3 = this.transfer.readInt();
            Command var24 = (Command)this.cache.getObject(var2, false);
            ResultInterface var31 = var24.getMetaData();
            this.cache.addObject(var3, var31);
            var32 = var31.getVisibleColumnCount();
            this.transfer.writeInt(1).writeInt(var32).writeRowCount(0L);

            for(var7 = 0; var7 < var32; ++var7) {
               ResultColumn.writeColumn(this.transfer, var31, var7);
            }

            this.transfer.flush();
            break;
         case 11:
         case 13:
         case 14:
         default:
            this.trace("Unknown operation: " + var1);
            this.close();
            break;
         case 12:
            this.sessionId = this.transfer.readString();
            if (this.clientVersion >= 20) {
               this.session.setTimeZone(TimeZoneProvider.ofId(this.transfer.readString()));
            }

            this.transfer.writeInt(1).writeBoolean(this.session.getAutoCommit()).flush();
            break;
         case 15:
            boolean var21 = this.transfer.readBoolean();
            this.session.setAutoCommit(var21);
            this.transfer.writeInt(1).flush();
            break;
         case 16:
            this.transfer.writeInt(1).writeInt(this.session.hasPendingTransaction() ? 1 : 0).flush();
            break;
         case 17:
            long var20 = this.transfer.readLong();
            byte[] var23 = this.transfer.readBytes();
            long var30 = this.transfer.readLong();
            var7 = this.transfer.readInt();
            this.transfer.verifyLobMac(var23, var20);
            CachedInputStream var38 = (CachedInputStream)this.lobs.get(var20);
            if (var38 == null || var38.getPos() != var30) {
               LobStorageInterface var39 = this.session.getDataHandler().getLobStorage();
               InputStream var10 = var39.getInputStream(var20, -1L);
               var38 = new CachedInputStream(var10);
               this.lobs.put(var20, var38);
               var10.skip(var30);
            }

            var7 = Math.min(65536, var7);
            byte[] var41 = new byte[var7];
            var7 = IOUtils.readFully((InputStream)var38, (byte[])var41, var7);
            this.transfer.writeInt(1);
            this.transfer.writeInt(var7);
            this.transfer.writeBytes(var41, 0, var7);
            this.transfer.flush();
            break;
         case 19:
            var2 = this.transfer.readInt();
            var3 = this.transfer.readInt();
            Value[] var4 = new Value[var3];

            int var5;
            for(var5 = 0; var5 < var3; ++var5) {
               var4[var5] = this.transfer.readValue((TypeInfo)null);
            }

            var5 = this.session.getModificationId();
            ResultInterface var6;
            synchronized(this.session) {
               var6 = DatabaseMetaServer.process(this.session, var2, var4);
            }

            var7 = var6.getVisibleColumnCount();
            var8 = this.getState(var5);
            this.transfer.writeInt(var8).writeInt(var7);
            long var9 = var6.getRowCount();
            this.transfer.writeRowCount(var9);

            for(var11 = 0; var11 < var7; ++var11) {
               ResultColumn.writeColumn(this.transfer, var6, var11);
            }

            this.sendRows(var6, var9);
            this.transfer.flush();
      }

   }

   private int getState(int var1) {
      if (this.session == null) {
         return 2;
      } else {
         if (this.session.getModificationId() == var1) {
            long var2 = this.session.getDatabase().getRemoteSettingsId();
            if (this.lastRemoteSettingsId == var2) {
               return 1;
            }

            this.lastRemoteSettingsId = var2;
         }

         return 3;
      }
   }

   private void sendRows(ResultInterface var1, long var2) throws IOException {
      int var4 = var1.getVisibleColumnCount();
      boolean var5 = var1.isLazy();
      Session var6 = var5 ? this.session.setThreadLocalSession() : null;

      try {
         label107:
         while(var2-- > 0L) {
            boolean var7;
            try {
               var7 = var1.next();
            } catch (Exception var15) {
               this.transfer.writeByte((byte)-1);
               this.sendError(var15, false);
               break;
            }

            if (var7) {
               this.transfer.writeByte((byte)1);
               Value[] var8 = var1.currentRow();
               int var9 = 0;

               while(true) {
                  if (var9 >= var4) {
                     continue label107;
                  }

                  Object var10 = var8[var9];
                  if (var5 && var10 instanceof ValueLob) {
                     ValueLob var11 = ((ValueLob)var10).copyToResult();
                     if (var11 != var10) {
                        var10 = this.session.addTemporaryLob(var11);
                     }
                  }

                  this.transfer.writeValue((Value)var10);
                  ++var9;
               }
            }

            this.transfer.writeByte((byte)0);
            break;
         }
      } finally {
         if (var5) {
            this.session.resetThreadLocalSession(var6);
         }

      }

   }

   void setThread(Thread var1) {
      this.thread = var1;
   }

   Thread getThread() {
      return this.thread;
   }

   void cancelStatement(String var1, int var2) {
      if (Objects.equals(var1, this.sessionId)) {
         Command var3 = (Command)this.cache.getObject(var2, false);
         var3.cancel();
      }

   }

   static class CachedInputStream extends FilterInputStream {
      private static final ByteArrayInputStream DUMMY = new ByteArrayInputStream(new byte[0]);
      private long pos;

      CachedInputStream(InputStream var1) {
         super((InputStream)(var1 == null ? DUMMY : var1));
         if (var1 == null) {
            this.pos = -1L;
         }

      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         var3 = super.read(var1, var2, var3);
         if (var3 > 0) {
            this.pos += (long)var3;
         }

         return var3;
      }

      public int read() throws IOException {
         int var1 = this.in.read();
         if (var1 >= 0) {
            ++this.pos;
         }

         return var1;
      }

      public long skip(long var1) throws IOException {
         var1 = super.skip(var1);
         if (var1 > 0L) {
            this.pos += var1;
         }

         return var1;
      }

      public long getPos() {
         return this.pos;
      }
   }
}
