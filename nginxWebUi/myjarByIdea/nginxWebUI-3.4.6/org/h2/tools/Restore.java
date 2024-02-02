package org.h2.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.h2.message.DbException;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.Tool;

public class Restore extends Tool {
   public static void main(String... var0) throws SQLException {
      (new Restore()).runTool(var0);
   }

   public void runTool(String... var1) throws SQLException {
      String var2 = "backup.zip";
      String var3 = ".";
      String var4 = null;

      for(int var5 = 0; var1 != null && var5 < var1.length; ++var5) {
         String var6 = var1[var5];
         if (var6.equals("-dir")) {
            ++var5;
            var3 = var1[var5];
         } else if (var6.equals("-file")) {
            ++var5;
            var2 = var1[var5];
         } else if (var6.equals("-db")) {
            ++var5;
            var4 = var1[var5];
         } else if (!var6.equals("-quiet")) {
            if (var6.equals("-help") || var6.equals("-?")) {
               this.showUsage();
               return;
            }

            this.showUsageAndThrowUnsupportedOption(var6);
         }
      }

      execute(var2, var3, var4);
   }

   private static String getOriginalDbName(String var0, String var1) throws IOException {
      InputStream var2 = FileUtils.newInputStream(var0);
      Throwable var3 = null;

      try {
         ZipInputStream var4 = new ZipInputStream(var2);
         String var5 = null;
         boolean var6 = false;

         while(true) {
            ZipEntry var7 = var4.getNextEntry();
            if (var7 != null) {
               String var8 = var7.getName();
               var4.closeEntry();
               String var9 = getDatabaseNameFromFileName(var8);
               if (var9 == null) {
                  continue;
               }

               if (!var1.equals(var9)) {
                  if (var5 == null) {
                     var5 = var9;
                  } else {
                     var6 = true;
                  }
                  continue;
               }

               var5 = var9;
            }

            var4.close();
            if (var6 && !var1.equals(var5)) {
               throw new IOException("Multiple databases found, but not " + var1);
            }

            String var19 = var5;
            return var19;
         }
      } catch (Throwable var17) {
         var3 = var17;
         throw var17;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var16) {
                  var3.addSuppressed(var16);
               }
            } else {
               var2.close();
            }
         }

      }
   }

   private static String getDatabaseNameFromFileName(String var0) {
      return var0.endsWith(".mv.db") ? var0.substring(0, var0.length() - ".mv.db".length()) : null;
   }

   public static void execute(String var0, String var1, String var2) {
      InputStream var3 = null;

      try {
         if (!FileUtils.exists(var0)) {
            throw new IOException("File not found: " + var0);
         } else {
            String var4 = null;
            int var5 = 0;
            if (var2 != null) {
               var4 = getOriginalDbName(var0, var2);
               if (var4 == null) {
                  throw new IOException("No database named " + var2 + " found");
               }

               if (var4.startsWith(File.separator)) {
                  var4 = var4.substring(1);
               }

               var5 = var4.length();
            }

            var3 = FileUtils.newInputStream(var0);
            ZipInputStream var6 = new ZipInputStream(var3);
            Throwable var7 = null;

            try {
               while(true) {
                  ZipEntry var8 = var6.getNextEntry();
                  if (var8 == null) {
                     var6.closeEntry();
                     return;
                  }

                  String var9 = var8.getName();
                  var9 = IOUtils.nameSeparatorsToNative(var9);
                  if (var9.startsWith(File.separator)) {
                     var9 = var9.substring(1);
                  }

                  boolean var10 = false;
                  if (var2 == null) {
                     var10 = true;
                  } else if (var9.startsWith(var4 + ".")) {
                     var9 = var2 + var9.substring(var5);
                     var10 = true;
                  }

                  if (var10) {
                     OutputStream var11 = null;

                     try {
                        var11 = FileUtils.newOutputStream(var1 + File.separatorChar + var9, false);
                        IOUtils.copy(var6, var11);
                        var11.close();
                     } finally {
                        IOUtils.closeSilently(var11);
                     }
                  }

                  var6.closeEntry();
               }
            } catch (Throwable var38) {
               var7 = var38;
               throw var38;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var36) {
                        var7.addSuppressed(var36);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         }
      } catch (IOException var40) {
         throw DbException.convertIOException(var40, var0);
      } finally {
         IOUtils.closeSilently(var3);
      }
   }
}
