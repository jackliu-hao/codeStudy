package org.h2.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.h2.command.dml.BackupCommand;
import org.h2.message.DbException;
import org.h2.store.FileLister;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.Tool;

public class Backup extends Tool {
   public static void main(String... var0) throws SQLException {
      (new Backup()).runTool(var0);
   }

   public void runTool(String... var1) throws SQLException {
      String var2 = "backup.zip";
      String var3 = ".";
      String var4 = null;
      boolean var5 = false;

      for(int var6 = 0; var1 != null && var6 < var1.length; ++var6) {
         String var7 = var1[var6];
         if (var7.equals("-dir")) {
            ++var6;
            var3 = var1[var6];
         } else if (var7.equals("-db")) {
            ++var6;
            var4 = var1[var6];
         } else if (var7.equals("-quiet")) {
            var5 = true;
         } else if (var7.equals("-file")) {
            ++var6;
            var2 = var1[var6];
         } else {
            if (var7.equals("-help") || var7.equals("-?")) {
               this.showUsage();
               return;
            }

            this.showUsageAndThrowUnsupportedOption(var7);
         }
      }

      try {
         this.process(var2, var3, var4, var5);
      } catch (Exception var8) {
         throw DbException.toSQLException(var8);
      }
   }

   public static void execute(String var0, String var1, String var2, boolean var3) throws SQLException {
      try {
         (new Backup()).process(var0, var1, var2, var3);
      } catch (Exception var5) {
         throw DbException.toSQLException(var5);
      }
   }

   private void process(String var1, String var2, String var3, boolean var4) throws SQLException {
      boolean var6 = var3 != null && var3.isEmpty();
      Object var5;
      if (var6) {
         var5 = FileUtils.newDirectoryStream(var2);
      } else {
         var5 = FileLister.getDatabaseFiles(var2, var3, true);
      }

      if (((List)var5).isEmpty()) {
         if (!var4) {
            this.printNoDatabaseFilesFound(var2, var3);
         }

      } else {
         if (!var4) {
            FileLister.tryUnlockDatabase((List)var5, "backup");
         }

         var1 = FileUtils.toRealPath(var1);
         FileUtils.delete(var1);
         OutputStream var7 = null;

         try {
            var7 = FileUtils.newOutputStream(var1, false);
            ZipOutputStream var8 = new ZipOutputStream(var7);
            Throwable var9 = null;

            try {
               String var10 = "";
               Iterator var11 = ((List)var5).iterator();

               while(true) {
                  String var12;
                  if (var11.hasNext()) {
                     var12 = (String)var11.next();
                     if (!var6 && !var12.endsWith(".mv.db")) {
                        continue;
                     }

                     var10 = FileUtils.getParent(var12);
                  }

                  var11 = ((List)var5).iterator();

                  while(var11.hasNext()) {
                     var12 = (String)var11.next();
                     String var13 = FileUtils.toRealPath(var12);
                     if (!var13.startsWith(var10)) {
                        throw DbException.getInternalError(var13 + " does not start with " + var10);
                     }

                     if (!var13.endsWith(var1) && !FileUtils.isDirectory(var12)) {
                        var13 = var13.substring(var10.length());
                        var13 = BackupCommand.correctFileName(var13);
                        ZipEntry var14 = new ZipEntry(var13);
                        var8.putNextEntry(var14);
                        InputStream var15 = null;

                        try {
                           var15 = FileUtils.newInputStream(var12);
                           IOUtils.copyAndCloseInput(var15, var8);
                        } catch (FileNotFoundException var45) {
                        } finally {
                           IOUtils.closeSilently(var15);
                        }

                        var8.closeEntry();
                        if (!var4) {
                           this.out.println("Processed: " + var12);
                        }
                     }
                  }

                  return;
               }
            } catch (Throwable var47) {
               var9 = var47;
               throw var47;
            } finally {
               if (var8 != null) {
                  if (var9 != null) {
                     try {
                        var8.close();
                     } catch (Throwable var44) {
                        var9.addSuppressed(var44);
                     }
                  } else {
                     var8.close();
                  }
               }

            }
         } catch (IOException var49) {
            throw DbException.convertIOException(var49, var1);
         } finally {
            IOUtils.closeSilently(var7);
         }
      }
   }
}
