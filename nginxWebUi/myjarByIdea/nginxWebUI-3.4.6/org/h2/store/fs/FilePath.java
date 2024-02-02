package org.h2.store.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.h2.store.fs.disk.FilePathDisk;
import org.h2.util.MathUtils;

public abstract class FilePath {
   private static final FilePath defaultProvider;
   private static final ConcurrentHashMap<String, FilePath> providers;
   private static String tempRandom;
   private static long tempSequence;
   public String name;

   public static FilePath get(String var0) {
      var0 = var0.replace('\\', '/');
      int var1 = var0.indexOf(58);
      if (var1 < 2) {
         return defaultProvider.getPath(var0);
      } else {
         String var2 = var0.substring(0, var1);
         FilePath var3 = (FilePath)providers.get(var2);
         if (var3 == null) {
            var3 = defaultProvider;
         }

         return var3.getPath(var0);
      }
   }

   public static void register(FilePath var0) {
      providers.put(var0.getScheme(), var0);
   }

   public static void unregister(FilePath var0) {
      providers.remove(var0.getScheme());
   }

   public abstract long size();

   public abstract void moveTo(FilePath var1, boolean var2);

   public abstract boolean createFile();

   public abstract boolean exists();

   public abstract void delete();

   public abstract List<FilePath> newDirectoryStream();

   public abstract FilePath toRealPath();

   public abstract FilePath getParent();

   public abstract boolean isDirectory();

   public abstract boolean isAbsolute();

   public abstract long lastModified();

   public abstract boolean canWrite();

   public abstract void createDirectory();

   public String getName() {
      int var1 = Math.max(this.name.indexOf(58), this.name.lastIndexOf(47));
      return var1 < 0 ? this.name : this.name.substring(var1 + 1);
   }

   public OutputStream newOutputStream(boolean var1) throws IOException {
      return newFileChannelOutputStream(this.open("rw"), var1);
   }

   public static final OutputStream newFileChannelOutputStream(FileChannel var0, boolean var1) throws IOException {
      if (var1) {
         var0.position(var0.size());
      } else {
         var0.position(0L);
         var0.truncate(0L);
      }

      return Channels.newOutputStream(var0);
   }

   public abstract FileChannel open(String var1) throws IOException;

   public InputStream newInputStream() throws IOException {
      return Channels.newInputStream(this.open("r"));
   }

   public abstract boolean setReadOnly();

   public FilePath createTempFile(String var1, boolean var2) throws IOException {
      while(true) {
         FilePath var3 = this.getPath(this.name + getNextTempFileNamePart(false) + var1);
         if (!var3.exists() && var3.createFile()) {
            var3.open("rw").close();
            return var3;
         }

         getNextTempFileNamePart(true);
      }
   }

   private static synchronized String getNextTempFileNamePart(boolean var0) {
      if (var0 || tempRandom == null) {
         tempRandom = MathUtils.randomInt(Integer.MAX_VALUE) + ".";
      }

      return tempRandom + tempSequence++;
   }

   public String toString() {
      return this.name;
   }

   public abstract String getScheme();

   public abstract FilePath getPath(String var1);

   public FilePath unwrap() {
      return this;
   }

   static {
      FilePath var0 = null;
      ConcurrentHashMap var1 = new ConcurrentHashMap();
      String[] var2 = new String[]{"org.h2.store.fs.disk.FilePathDisk", "org.h2.store.fs.mem.FilePathMem", "org.h2.store.fs.mem.FilePathMemLZF", "org.h2.store.fs.niomem.FilePathNioMem", "org.h2.store.fs.niomem.FilePathNioMemLZF", "org.h2.store.fs.split.FilePathSplit", "org.h2.store.fs.niomapped.FilePathNioMapped", "org.h2.store.fs.async.FilePathAsync", "org.h2.store.fs.zip.FilePathZip", "org.h2.store.fs.retry.FilePathRetryOnInterrupt"};
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];

         try {
            FilePath var6 = (FilePath)Class.forName(var5).getDeclaredConstructor().newInstance();
            var1.put(var6.getScheme(), var6);
            if (var6.getClass() == FilePathDisk.class) {
               var1.put("nio", var6);
            }

            if (var0 == null) {
               var0 = var6;
            }
         } catch (Exception var7) {
         }
      }

      defaultProvider = var0;
      providers = var1;
   }
}
