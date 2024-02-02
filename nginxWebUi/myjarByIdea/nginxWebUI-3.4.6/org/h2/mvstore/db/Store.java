package org.h2.mvstore.db;

import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.h2.command.ddl.CreateTableData;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.mvstore.FileStore;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.MVStoreTool;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.mvstore.type.MetaType;
import org.h2.store.InDoubtTransaction;
import org.h2.store.fs.FileChannelInputStream;
import org.h2.store.fs.FileUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public final class Store {
   private final ConcurrentHashMap<String, MVTable> tableMap = new ConcurrentHashMap();
   private final MVStore mvStore;
   private final TransactionStore transactionStore;
   private long statisticsStart;
   private int temporaryMapId;
   private final boolean encrypted;
   private final String fileName;

   static char[] decodePassword(byte[] var0) {
      char[] var1 = new char[var0.length / 2];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = (char)((var0[var2 + var2] & 255) << 16 | var0[var2 + var2 + 1] & 255);
      }

      return var1;
   }

   public Store(Database var1) {
      byte[] var2 = var1.getFileEncryptionKey();
      String var3 = var1.getDatabasePath();
      MVStore.Builder var4 = new MVStore.Builder();
      boolean var5 = false;
      if (var3 != null) {
         String var6 = var3 + ".mv.db";
         MVStoreTool.compactCleanUp(var6);
         var4.fileName(var6);
         var4.pageSplitSize(var1.getPageSize());
         if (var1.isReadOnly()) {
            var4.readOnly();
         } else {
            boolean var7 = FileUtils.exists(var6);
            if (!var7 || FileUtils.canWrite(var6)) {
               String var8 = FileUtils.getParent(var6);
               FileUtils.createDirectories(var8);
            }

            int var11 = var1.getSettings().autoCompactFillRate;
            if (var11 <= 100) {
               var4.autoCompactFillRate(var11);
            }
         }

         if (var2 != null) {
            var5 = true;
            var4.encryptionKey(decodePassword(var2));
         }

         if (var1.getSettings().compressData) {
            var4.compress();
            var4.pageSplitSize(65536);
         }

         var4.backgroundExceptionHandler((var1x, var2x) -> {
            var1.setBackgroundException(DbException.convert(var2x));
         });
         var4.autoCommitDisabled();
      }

      this.encrypted = var5;

      try {
         this.mvStore = var4.open();
         FileStore var10 = this.mvStore.getFileStore();
         this.fileName = var10 != null ? var10.getFileName() : null;
         if (!var1.getSettings().reuseSpace) {
            this.mvStore.setReuseSpace(false);
         }

         this.mvStore.setVersionsToKeep(0);
         this.transactionStore = new TransactionStore(this.mvStore, new MetaType(var1, this.mvStore.backgroundExceptionHandler), new ValueDataType(var1, (int[])null), var1.getLockTimeout());
      } catch (MVStoreException var9) {
         throw this.convertMVStoreException(var9);
      }
   }

   DbException convertMVStoreException(MVStoreException var1) {
      switch (var1.getErrorCode()) {
         case 1:
         case 2:
            throw DbException.get(90028, var1, this.fileName);
         case 3:
         case 5:
         default:
            throw DbException.get(50000, var1, var1.getMessage());
         case 4:
            throw DbException.get(90098, var1, this.fileName);
         case 6:
            if (this.encrypted) {
               throw DbException.get(90049, var1, this.fileName);
            }

            throw DbException.get(90030, var1, this.fileName);
         case 7:
            throw DbException.get(90020, var1, this.fileName);
      }
   }

   public MVStore getMvStore() {
      return this.mvStore;
   }

   public TransactionStore getTransactionStore() {
      return this.transactionStore;
   }

   public MVTable getTable(String var1) {
      return (MVTable)this.tableMap.get(var1);
   }

   public MVTable createTable(CreateTableData var1) {
      try {
         MVTable var2 = new MVTable(var1, this);
         this.tableMap.put(var2.getMapName(), var2);
         return var2;
      } catch (MVStoreException var3) {
         throw this.convertMVStoreException(var3);
      }
   }

   public void removeTable(MVTable var1) {
      try {
         this.tableMap.remove(var1.getMapName());
      } catch (MVStoreException var3) {
         throw this.convertMVStoreException(var3);
      }
   }

   public void flush() {
      FileStore var1 = this.mvStore.getFileStore();
      if (var1 != null && !var1.isReadOnly()) {
         if (!this.mvStore.compact(50, 4194304)) {
            this.mvStore.commit();
         }

      }
   }

   public void closeImmediately() {
      if (!this.mvStore.isClosed()) {
         this.mvStore.closeImmediately();
      }

   }

   public void removeTemporaryMaps(BitSet var1) {
      Iterator var2 = this.mvStore.getMapNames().iterator();

      while(true) {
         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            if (var3.startsWith("temp.")) {
               this.mvStore.removeMap(var3);
            } else if (var3.startsWith("table.") || var3.startsWith("index.")) {
               int var4 = StringUtils.parseUInt31(var3, var3.indexOf(46) + 1, var3.length());
               if (!var1.get(var4)) {
                  this.mvStore.removeMap(var3);
               }
            }
         }

         return;
      }
   }

   public synchronized String nextTemporaryMapName() {
      return "temp." + this.temporaryMapId++;
   }

   public void prepareCommit(SessionLocal var1, String var2) {
      Transaction var3 = var1.getTransaction();
      var3.setName(var2);
      var3.prepare();
      this.mvStore.commit();
   }

   public ArrayList<InDoubtTransaction> getInDoubtTransactions() {
      List var1 = this.transactionStore.getOpenTransactions();
      ArrayList var2 = Utils.newSmallArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Transaction var4 = (Transaction)var3.next();
         if (var4.getStatus() == 2) {
            var2.add(new MVInDoubtTransaction(this.mvStore, var4));
         }
      }

      return var2;
   }

   public void setCacheSize(int var1) {
      this.mvStore.setCacheSize(Math.max(1, var1 / 1024));
   }

   public InputStream getInputStream() {
      FileChannel var1 = this.mvStore.getFileStore().getEncryptedFile();
      if (var1 == null) {
         var1 = this.mvStore.getFileStore().getFile();
      }

      return new FileChannelInputStream(var1, false);
   }

   public void sync() {
      this.flush();
      this.mvStore.sync();
   }

   public void compactFile(int var1) {
      this.mvStore.compactFile(var1);
   }

   public void close(int var1) {
      try {
         FileStore var2 = this.mvStore.getFileStore();
         if (!this.mvStore.isClosed() && var2 != null) {
            boolean var6 = var1 == -1;
            if (var2.isReadOnly()) {
               var6 = false;
            } else {
               this.transactionStore.close();
            }

            if (var6) {
               var1 = 0;
            }

            this.mvStore.close(var1);
            String var4 = var2.getFileName();
            if (var6 && FileUtils.exists(var4)) {
               MVStoreTool.compact(var4, true);
            }
         }

      } catch (MVStoreException var5) {
         int var3 = var5.getErrorCode();
         if (var3 != 2 && var3 == 6) {
         }

         this.mvStore.closeImmediately();
         throw DbException.get(90028, var5, "Closing");
      }
   }

   public void statisticsStart() {
      FileStore var1 = this.mvStore.getFileStore();
      this.statisticsStart = var1 == null ? 0L : var1.getReadCount();
   }

   public Map<String, Integer> statisticsEnd() {
      HashMap var1 = new HashMap();
      FileStore var2 = this.mvStore.getFileStore();
      int var3 = var2 == null ? 0 : (int)(var2.getReadCount() - this.statisticsStart);
      var1.put("reads", var3);
      return var1;
   }
}
