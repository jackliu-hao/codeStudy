package org.h2.command.dml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.h2.command.Prepared;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.db.Store;
import org.h2.result.ResultInterface;
import org.h2.store.FileLister;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;

public class BackupCommand extends Prepared {
   private Expression fileNameExpr;

   public BackupCommand(SessionLocal var1) {
      super(var1);
   }

   public void setFileName(Expression var1) {
      this.fileNameExpr = var1;
   }

   public long update() {
      String var1 = this.fileNameExpr.getValue(this.session).getString();
      this.session.getUser().checkAdmin();
      this.backupTo(var1);
      return 0L;
   }

   private void backupTo(String var1) {
      Database var2 = this.session.getDatabase();
      if (!var2.isPersistent()) {
         throw DbException.get(90126);
      } else {
         try {
            Store var3 = var2.getStore();
            var3.flush();
            String var4 = var2.getName();
            var4 = FileUtils.getName(var4);
            OutputStream var5 = FileUtils.newOutputStream(var1, false);
            Throwable var6 = null;

            try {
               ZipOutputStream var7 = new ZipOutputStream(var5);
               var2.flush();
               String var8 = FileUtils.getParent(var2.getName());
               synchronized(var2.getLobSyncObject()) {
                  String var10 = var2.getDatabasePath();
                  String var11 = FileUtils.getParent(var10);
                  var11 = FileLister.getDir(var11);
                  ArrayList var12 = FileLister.getDatabaseFiles(var11, var4, true);
                  Iterator var13 = var12.iterator();

                  while(var13.hasNext()) {
                     String var14 = (String)var13.next();
                     if (var14.endsWith(".mv.db")) {
                        MVStore var15 = var3.getMvStore();
                        boolean var16 = var15.getReuseSpace();
                        var15.setReuseSpace(false);

                        try {
                           InputStream var17 = var3.getInputStream();
                           backupFile(var7, var8, var14, var17);
                        } finally {
                           var15.setReuseSpace(var16);
                        }
                     }
                  }
               }

               var7.close();
            } catch (Throwable var38) {
               var6 = var38;
               throw var38;
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

         } catch (IOException var40) {
            throw DbException.convertIOException(var40, var1);
         }
      }
   }

   private static void backupFile(ZipOutputStream var0, String var1, String var2, InputStream var3) throws IOException {
      String var4 = FileUtils.toRealPath(var2);
      var1 = FileUtils.toRealPath(var1);
      if (!var4.startsWith(var1)) {
         throw DbException.getInternalError(var4 + " does not start with " + var1);
      } else {
         var4 = var4.substring(var1.length());
         var4 = correctFileName(var4);
         var0.putNextEntry(new ZipEntry(var4));
         IOUtils.copyAndCloseInput(var3, var0);
         var0.closeEntry();
      }
   }

   public boolean isTransactional() {
      return true;
   }

   public static String correctFileName(String var0) {
      var0 = var0.replace('\\', '/');
      if (var0.startsWith("/")) {
         var0 = var0.substring(1);
      }

      return var0;
   }

   public boolean needRecompile() {
      return false;
   }

   public ResultInterface queryMeta() {
      return null;
   }

   public int getType() {
      return 56;
   }
}
