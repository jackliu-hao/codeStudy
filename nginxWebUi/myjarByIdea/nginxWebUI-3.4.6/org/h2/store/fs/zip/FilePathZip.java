package org.h2.store.fs.zip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.h2.message.DbException;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.disk.FilePathDisk;

public class FilePathZip extends FilePath {
   public FilePathZip getPath(String var1) {
      FilePathZip var2 = new FilePathZip();
      var2.name = var1;
      return var2;
   }

   public void createDirectory() {
   }

   public boolean createFile() {
      throw DbException.getUnsupportedException("write");
   }

   public void delete() {
      throw DbException.getUnsupportedException("write");
   }

   public boolean exists() {
      try {
         String var1 = this.getEntryName();
         if (var1.isEmpty()) {
            return true;
         } else {
            ZipFile var2 = this.openZipFile();
            Throwable var3 = null;

            boolean var4;
            try {
               var4 = var2.getEntry(var1) != null;
            } catch (Throwable var14) {
               var3 = var14;
               throw var14;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var13) {
                        var3.addSuppressed(var13);
                     }
                  } else {
                     var2.close();
                  }
               }

            }

            return var4;
         }
      } catch (IOException var16) {
         return false;
      }
   }

   public long lastModified() {
      return 0L;
   }

   public FilePath getParent() {
      int var1 = this.name.lastIndexOf(47);
      return var1 < 0 ? null : this.getPath(this.name.substring(0, var1));
   }

   public boolean isAbsolute() {
      String var1 = translateFileName(this.name);
      return FilePath.get(var1).isAbsolute();
   }

   public FilePath unwrap() {
      return FilePath.get(this.name.substring(this.getScheme().length() + 1));
   }

   public boolean isDirectory() {
      try {
         String var1 = this.getEntryName();
         if (var1.isEmpty()) {
            return true;
         } else {
            ZipFile var2 = this.openZipFile();
            Throwable var3 = null;

            try {
               Enumeration var4 = var2.entries();

               String var6;
               boolean var7;
               do {
                  if (!var4.hasMoreElements()) {
                     return false;
                  }

                  ZipEntry var5 = (ZipEntry)var4.nextElement();
                  var6 = var5.getName();
                  if (var6.equals(var1)) {
                     var7 = var5.isDirectory();
                     return var7;
                  }
               } while(!var6.startsWith(var1) || var6.length() != var1.length() + 1 || !var6.equals(var1 + "/"));

               var7 = true;
               return var7;
            } catch (Throwable var19) {
               var3 = var19;
               throw var19;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var18) {
                        var3.addSuppressed(var18);
                     }
                  } else {
                     var2.close();
                  }
               }

            }
         }
      } catch (IOException var21) {
         return false;
      }
   }

   public boolean canWrite() {
      return false;
   }

   public boolean setReadOnly() {
      return true;
   }

   public long size() {
      try {
         ZipFile var1 = this.openZipFile();
         Throwable var2 = null;

         long var4;
         try {
            ZipEntry var3 = var1.getEntry(this.getEntryName());
            var4 = var3 == null ? 0L : var3.getSize();
         } catch (Throwable var15) {
            var2 = var15;
            throw var15;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var14) {
                     var2.addSuppressed(var14);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return var4;
      } catch (IOException var17) {
         return 0L;
      }
   }

   public ArrayList<FilePath> newDirectoryStream() {
      String var1 = this.name;
      ArrayList var2 = new ArrayList();

      try {
         if (var1.indexOf(33) < 0) {
            var1 = var1 + "!";
         }

         if (!var1.endsWith("/")) {
            var1 = var1 + "/";
         }

         ZipFile var3 = this.openZipFile();
         Throwable var4 = null;

         try {
            String var5 = this.getEntryName();
            String var6 = var1.substring(0, var1.length() - var5.length());
            Enumeration var7 = var3.entries();

            while(var7.hasMoreElements()) {
               ZipEntry var8 = (ZipEntry)var7.nextElement();
               String var9 = var8.getName();
               if (var9.startsWith(var5) && var9.length() > var5.length()) {
                  int var10 = var9.indexOf(47, var5.length());
                  if (var10 < 0 || var10 >= var9.length() - 1) {
                     var2.add(this.getPath(var6 + var9));
                  }
               }
            }
         } catch (Throwable var19) {
            var4 = var19;
            throw var19;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var18) {
                     var4.addSuppressed(var18);
                  }
               } else {
                  var3.close();
               }
            }

         }

         return var2;
      } catch (IOException var21) {
         throw DbException.convertIOException(var21, "listFiles " + var1);
      }
   }

   public FileChannel open(String var1) throws IOException {
      ZipFile var2 = this.openZipFile();
      ZipEntry var3 = var2.getEntry(this.getEntryName());
      if (var3 == null) {
         var2.close();
         throw new FileNotFoundException(this.name);
      } else {
         return new FileZip(var2, var3);
      }
   }

   public OutputStream newOutputStream(boolean var1) throws IOException {
      throw new IOException("write");
   }

   public void moveTo(FilePath var1, boolean var2) {
      throw DbException.getUnsupportedException("write");
   }

   private static String translateFileName(String var0) {
      if (var0.startsWith("zip:")) {
         var0 = var0.substring("zip:".length());
      }

      int var1 = var0.indexOf(33);
      if (var1 >= 0) {
         var0 = var0.substring(0, var1);
      }

      return FilePathDisk.expandUserHomeDirectory(var0);
   }

   public FilePath toRealPath() {
      return this;
   }

   private String getEntryName() {
      int var1 = this.name.indexOf(33);
      String var2;
      if (var1 <= 0) {
         var2 = "";
      } else {
         var2 = this.name.substring(var1 + 1);
      }

      var2 = var2.replace('\\', '/');
      if (var2.startsWith("/")) {
         var2 = var2.substring(1);
      }

      return var2;
   }

   private ZipFile openZipFile() throws IOException {
      String var1 = translateFileName(this.name);
      return new ZipFile(var1);
   }

   public FilePath createTempFile(String var1, boolean var2) throws IOException {
      if (!var2) {
         throw new IOException("File system is read-only");
      } else {
         return (new FilePathDisk()).getPath(this.name).createTempFile(var1, true);
      }
   }

   public String getScheme() {
      return "zip";
   }
}
