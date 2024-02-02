package org.h2.mvstore.db;

import java.io.IOException;
import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Iterator;
import org.h2.engine.Database;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.mvstore.MVStore;
import org.h2.result.ResultExternal;
import org.h2.result.SortOrder;
import org.h2.store.fs.FileUtils;
import org.h2.util.TempFileDeleter;
import org.h2.value.Value;

public abstract class MVTempResult implements ResultExternal {
   private final Database database;
   final MVStore store;
   final Expression[] expressions;
   final int visibleColumnCount;
   final int resultColumnCount;
   int rowCount;
   final MVTempResult parent;
   int childCount;
   boolean closed;
   private final TempFileDeleter tempFileDeleter;
   private final CloseImpl closeable;
   private final Reference<?> fileRef;

   public static ResultExternal of(Database var0, Expression[] var1, boolean var2, int[] var3, int var4, int var5, SortOrder var6) {
      return (ResultExternal)(!var2 && var3 == null && var6 == null ? new MVPlainTempResult(var0, var1, var4, var5) : new MVSortedTempResult(var0, var1, var2, var3, var4, var5, var6));
   }

   MVTempResult(MVTempResult var1) {
      this.parent = var1;
      this.database = var1.database;
      this.store = var1.store;
      this.expressions = var1.expressions;
      this.visibleColumnCount = var1.visibleColumnCount;
      this.resultColumnCount = var1.resultColumnCount;
      this.tempFileDeleter = null;
      this.closeable = null;
      this.fileRef = null;
   }

   MVTempResult(Database var1, Expression[] var2, int var3, int var4) {
      this.database = var1;

      try {
         String var5 = FileUtils.createTempFile("h2tmp", ".temp.db", true);
         MVStore.Builder var6 = (new MVStore.Builder()).fileName(var5).cacheSize(0).autoCommitDisabled();
         byte[] var7 = var1.getFileEncryptionKey();
         if (var7 != null) {
            var6.encryptionKey(Store.decodePassword(var7));
         }

         this.store = var6.open();
         this.expressions = var2;
         this.visibleColumnCount = var3;
         this.resultColumnCount = var4;
         this.tempFileDeleter = var1.getTempFileDeleter();
         this.closeable = new CloseImpl(this.store, var5);
         this.fileRef = this.tempFileDeleter.addFile(this.closeable, this);
      } catch (IOException var8) {
         throw DbException.convert(var8);
      }

      this.parent = null;
   }

   public int addRows(Collection<Value[]> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Value[] var3 = (Value[])var2.next();
         this.addRow(var3);
      }

      return this.rowCount;
   }

   public synchronized void close() {
      if (!this.closed) {
         this.closed = true;
         if (this.parent != null) {
            this.parent.closeChild();
         } else if (this.childCount == 0) {
            this.delete();
         }

      }
   }

   private synchronized void closeChild() {
      if (--this.childCount == 0 && this.closed) {
         this.delete();
      }

   }

   private void delete() {
      this.tempFileDeleter.deleteFile(this.fileRef, this.closeable);
   }

   private static final class CloseImpl implements AutoCloseable {
      private final MVStore store;
      private final String fileName;

      CloseImpl(MVStore var1, String var2) {
         this.store = var1;
         this.fileName = var2;
      }

      public void close() throws Exception {
         this.store.closeImmediately();
         FileUtils.tryDelete(this.fileName);
      }
   }
}
