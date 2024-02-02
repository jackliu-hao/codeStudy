package org.h2.store.fs;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FileUtils {
   public static final Set<? extends OpenOption> R;
   public static final Set<? extends OpenOption> RW;
   public static final Set<? extends OpenOption> RWS;
   public static final Set<? extends OpenOption> RWD;
   public static final FileAttribute<?>[] NO_ATTRIBUTES;

   public static boolean exists(String var0) {
      return FilePath.get(var0).exists();
   }

   public static void createDirectory(String var0) {
      FilePath.get(var0).createDirectory();
   }

   public static boolean createFile(String var0) {
      return FilePath.get(var0).createFile();
   }

   public static void delete(String var0) {
      FilePath.get(var0).delete();
   }

   public static String toRealPath(String var0) {
      return FilePath.get(var0).toRealPath().toString();
   }

   public static String getParent(String var0) {
      FilePath var1 = FilePath.get(var0).getParent();
      return var1 == null ? null : var1.toString();
   }

   public static boolean isAbsolute(String var0) {
      return FilePath.get(var0).isAbsolute() || var0.startsWith(File.separator) || var0.startsWith("/");
   }

   public static void move(String var0, String var1) {
      FilePath.get(var0).moveTo(FilePath.get(var1), false);
   }

   public static void moveAtomicReplace(String var0, String var1) {
      FilePath.get(var0).moveTo(FilePath.get(var1), true);
   }

   public static String getName(String var0) {
      return FilePath.get(var0).getName();
   }

   public static List<String> newDirectoryStream(String var0) {
      List var1 = FilePath.get(var0).newDirectoryStream();
      int var2 = var1.size();
      ArrayList var3 = new ArrayList(var2);
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         FilePath var5 = (FilePath)var4.next();
         var3.add(var5.toString());
      }

      return var3;
   }

   public static long lastModified(String var0) {
      return FilePath.get(var0).lastModified();
   }

   public static long size(String var0) {
      return FilePath.get(var0).size();
   }

   public static boolean isDirectory(String var0) {
      return FilePath.get(var0).isDirectory();
   }

   public static FileChannel open(String var0, String var1) throws IOException {
      return FilePath.get(var0).open(var1);
   }

   public static InputStream newInputStream(String var0) throws IOException {
      return FilePath.get(var0).newInputStream();
   }

   public static BufferedReader newBufferedReader(String var0, Charset var1) throws IOException {
      return new BufferedReader(new InputStreamReader(newInputStream(var0), var1), 4096);
   }

   public static OutputStream newOutputStream(String var0, boolean var1) throws IOException {
      return FilePath.get(var0).newOutputStream(var1);
   }

   public static boolean canWrite(String var0) {
      return FilePath.get(var0).canWrite();
   }

   public static boolean setReadOnly(String var0) {
      return FilePath.get(var0).setReadOnly();
   }

   public static String unwrap(String var0) {
      return FilePath.get(var0).unwrap().toString();
   }

   public static void deleteRecursive(String var0, boolean var1) {
      if (exists(var0)) {
         if (isDirectory(var0)) {
            Iterator var2 = newDirectoryStream(var0).iterator();

            while(var2.hasNext()) {
               String var3 = (String)var2.next();
               deleteRecursive(var3, var1);
            }
         }

         if (var1) {
            tryDelete(var0);
         } else {
            delete(var0);
         }
      }

   }

   public static void createDirectories(String var0) {
      if (var0 != null) {
         if (exists(var0)) {
            if (!isDirectory(var0)) {
               createDirectory(var0);
            }
         } else {
            String var1 = getParent(var0);
            createDirectories(var1);
            createDirectory(var0);
         }
      }

   }

   public static boolean tryDelete(String var0) {
      try {
         FilePath.get(var0).delete();
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   public static String createTempFile(String var0, String var1, boolean var2) throws IOException {
      return FilePath.get(var0).createTempFile(var1, var2).toString();
   }

   public static void readFully(FileChannel var0, ByteBuffer var1) throws IOException {
      do {
         int var2 = var0.read(var1);
         if (var2 < 0) {
            throw new EOFException();
         }
      } while(var1.remaining() > 0);

   }

   public static void writeFully(FileChannel var0, ByteBuffer var1) throws IOException {
      do {
         var0.write(var1);
      } while(var1.remaining() > 0);

   }

   public static Set<? extends OpenOption> modeToOptions(String var0) {
      Set var1;
      switch (var0) {
         case "r":
            var1 = R;
            break;
         case "rw":
            var1 = RW;
            break;
         case "rws":
            var1 = RWS;
            break;
         case "rwd":
            var1 = RWD;
            break;
         default:
            throw new IllegalArgumentException(var0);
      }

      return var1;
   }

   static {
      R = Collections.singleton(StandardOpenOption.READ);
      RW = Collections.unmodifiableSet(EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE));
      RWS = Collections.unmodifiableSet(EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.SYNC));
      RWD = Collections.unmodifiableSet(EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.DSYNC));
      NO_ATTRIBUTES = new FileAttribute[0];
   }
}
