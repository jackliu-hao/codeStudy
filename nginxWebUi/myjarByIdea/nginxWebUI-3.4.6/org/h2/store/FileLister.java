package org.h2.store;

import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.h2.message.DbException;
import org.h2.message.TraceSystem;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FileUtils;

public class FileLister {
   private FileLister() {
   }

   public static void tryUnlockDatabase(List<String> var0, String var1) throws SQLException {
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (var3.endsWith(".lock.db")) {
            FileLock var4 = new FileLock(new TraceSystem((String)null), var3, 1000);

            try {
               var4.lock(FileLockMethod.FILE);
               var4.unlock();
            } catch (DbException var16) {
               throw DbException.getJdbcSQLException(90133, var1);
            }
         } else if (var3.endsWith(".mv.db")) {
            try {
               FileChannel var20 = FilePath.get(var3).open("r");
               Throwable var5 = null;

               try {
                  java.nio.channels.FileLock var6 = var20.tryLock(0L, Long.MAX_VALUE, true);
                  var6.release();
               } catch (Throwable var17) {
                  var5 = var17;
                  throw var17;
               } finally {
                  if (var20 != null) {
                     if (var5 != null) {
                        try {
                           var20.close();
                        } catch (Throwable var15) {
                           var5.addSuppressed(var15);
                        }
                     } else {
                        var20.close();
                     }
                  }

               }
            } catch (Exception var19) {
               throw DbException.getJdbcSQLException(90133, var19, var1);
            }
         }
      }

   }

   public static String getDir(String var0) {
      return var0 != null && !var0.equals("") ? FileUtils.toRealPath(var0) : ".";
   }

   public static ArrayList<String> getDatabaseFiles(String var0, String var1, boolean var2) {
      ArrayList var3 = new ArrayList();
      String var4 = var1 == null ? null : FileUtils.toRealPath(var0 + "/" + var1) + ".";
      Iterator var5 = FileUtils.newDirectoryStream(var0).iterator();

      while(true) {
         String var6;
         boolean var7;
         do {
            do {
               if (!var5.hasNext()) {
                  return var3;
               }

               var6 = (String)var5.next();
               var7 = false;
               if (var6.endsWith(".mv.db")) {
                  var7 = true;
               } else if (var2) {
                  if (var6.endsWith(".lock.db")) {
                     var7 = true;
                  } else if (var6.endsWith(".temp.db")) {
                     var7 = true;
                  } else if (var6.endsWith(".trace.db")) {
                     var7 = true;
                  }
               }
            } while(!var7);
         } while(var1 != null && !var6.startsWith(var4));

         var3.add(var6);
      }
   }
}
