package org.h2.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.h2.message.DbException;
import org.h2.mvstore.MVStore;
import org.h2.store.FileLister;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FileUtils;
import org.h2.store.fs.encrypt.FileEncrypt;
import org.h2.store.fs.encrypt.FilePathEncrypt;
import org.h2.util.Tool;

public class ChangeFileEncryption extends Tool {
   private String directory;
   private String cipherType;
   private byte[] decryptKey;
   private byte[] encryptKey;

   public static void main(String... var0) {
      try {
         (new ChangeFileEncryption()).runTool(var0);
      } catch (SQLException var2) {
         var2.printStackTrace(System.err);
         System.exit(1);
      }

   }

   public void runTool(String... var1) throws SQLException {
      String var2 = ".";
      String var3 = null;
      char[] var4 = null;
      char[] var5 = null;
      String var6 = null;
      boolean var7 = false;

      for(int var8 = 0; var1 != null && var8 < var1.length; ++var8) {
         String var9 = var1[var8];
         if (var9.equals("-dir")) {
            ++var8;
            var2 = var1[var8];
         } else if (var9.equals("-cipher")) {
            ++var8;
            var3 = var1[var8];
         } else if (var9.equals("-db")) {
            ++var8;
            var6 = var1[var8];
         } else if (var9.equals("-decrypt")) {
            ++var8;
            var4 = var1[var8].toCharArray();
         } else if (var9.equals("-encrypt")) {
            ++var8;
            var5 = var1[var8].toCharArray();
         } else if (var9.equals("-quiet")) {
            var7 = true;
         } else {
            if (var9.equals("-help") || var9.equals("-?")) {
               this.showUsage();
               return;
            }

            this.showUsageAndThrowUnsupportedOption(var9);
         }
      }

      if ((var5 != null || var4 != null) && var3 != null) {
         try {
            this.process(var2, var6, var3, var4, var5, var7);
         } catch (Exception var10) {
            throw DbException.toSQLException(var10);
         }
      } else {
         this.showUsage();
         throw new SQLException("Encryption or decryption password not set, or cipher not set");
      }
   }

   public static void execute(String var0, String var1, String var2, char[] var3, char[] var4, boolean var5) throws SQLException {
      try {
         (new ChangeFileEncryption()).process(var0, var1, var2, var3, var4, var5);
      } catch (Exception var7) {
         throw DbException.toSQLException(var7);
      }
   }

   private void process(String var1, String var2, String var3, char[] var4, char[] var5, boolean var6) throws SQLException {
      var1 = FileLister.getDir(var1);
      ChangeFileEncryption var7 = new ChangeFileEncryption();
      if (var5 != null) {
         char[] var8 = var5;
         int var9 = var5.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            char var11 = var8[var10];
            if (var11 == ' ') {
               throw new SQLException("The file password may not contain spaces");
            }
         }

         var7.encryptKey = FilePathEncrypt.getPasswordBytes(var5);
      }

      if (var4 != null) {
         var7.decryptKey = FilePathEncrypt.getPasswordBytes(var4);
      }

      var7.out = this.out;
      var7.directory = var1;
      var7.cipherType = var3;
      ArrayList var12 = FileLister.getDatabaseFiles(var1, var2, true);
      FileLister.tryUnlockDatabase(var12, "encryption");
      var12 = FileLister.getDatabaseFiles(var1, var2, false);
      if (var12.isEmpty() && !var6) {
         this.printNoDatabaseFilesFound(var1, var2);
      }

      Iterator var13 = var12.iterator();

      String var14;
      while(var13.hasNext()) {
         var14 = (String)var13.next();
         String var15 = var1 + "/temp.db";
         FileUtils.delete(var15);
         FileUtils.move(var14, var15);
         FileUtils.move(var15, var14);
      }

      var13 = var12.iterator();

      while(var13.hasNext()) {
         var14 = (String)var13.next();
         if (!FileUtils.isDirectory(var14)) {
            var7.process(var14, var6, var4);
         }
      }

   }

   private void process(String var1, boolean var2, char[] var3) throws SQLException {
      if (var1.endsWith(".mv.db")) {
         try {
            this.copyMvStore(var1, var2, var3);
         } catch (IOException var5) {
            throw DbException.convertIOException(var5, "Error encrypting / decrypting file " + var1);
         }
      }
   }

   private void copyMvStore(String var1, boolean var2, char[] var3) throws IOException, SQLException {
      if (!FileUtils.isDirectory(var1)) {
         try {
            MVStore var4 = (new MVStore.Builder()).fileName(var1).readOnly().encryptionKey(var3).open();
            var4.close();
         } catch (IllegalStateException var64) {
            throw new SQLException("error decrypting file " + var1, var64);
         }

         String var71 = this.directory + "/temp.db";
         FileChannel var5 = getFileChannel(var1, "r", this.decryptKey);
         Throwable var6 = null;

         try {
            InputStream var7 = Channels.newInputStream(var5);
            Throwable var8 = null;

            try {
               FileUtils.delete(var71);
               OutputStream var9 = Channels.newOutputStream(getFileChannel(var71, "rw", this.encryptKey));
               Throwable var10 = null;

               try {
                  byte[] var11 = new byte[4096];
                  long var12 = var5.size();
                  long var14 = var12;

                  int var18;
                  for(long var16 = System.nanoTime(); var12 > 0L; var12 -= (long)var18) {
                     if (!var2 && System.nanoTime() - var16 > TimeUnit.SECONDS.toNanos(1L)) {
                        this.out.println(var1 + ": " + (100L - 100L * var12 / var14) + "%");
                        var16 = System.nanoTime();
                     }

                     var18 = (int)Math.min((long)var11.length, var12);
                     var18 = var7.read(var11, 0, var18);
                     var9.write(var11, 0, var18);
                  }
               } catch (Throwable var65) {
                  var10 = var65;
                  throw var65;
               } finally {
                  if (var9 != null) {
                     if (var10 != null) {
                        try {
                           var9.close();
                        } catch (Throwable var63) {
                           var10.addSuppressed(var63);
                        }
                     } else {
                        var9.close();
                     }
                  }

               }
            } catch (Throwable var67) {
               var8 = var67;
               throw var67;
            } finally {
               if (var7 != null) {
                  if (var8 != null) {
                     try {
                        var7.close();
                     } catch (Throwable var62) {
                        var8.addSuppressed(var62);
                     }
                  } else {
                     var7.close();
                  }
               }

            }
         } catch (Throwable var69) {
            var6 = var69;
            throw var69;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var61) {
                     var6.addSuppressed(var61);
                  }
               } else {
                  var5.close();
               }
            }

         }

         FileUtils.delete(var1);
         FileUtils.move(var71, var1);
      }
   }

   private static FileChannel getFileChannel(String var0, String var1, byte[] var2) throws IOException {
      Object var3 = FilePath.get(var0).open(var1);
      if (var2 != null) {
         var3 = new FileEncrypt(var0, var2, (FileChannel)var3);
      }

      return (FileChannel)var3;
   }
}
