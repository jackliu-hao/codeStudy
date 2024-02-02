package org.h2.store.fs.disk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;

public class FilePathDisk extends FilePath {
   private static final String CLASSPATH_PREFIX = "classpath:";

   public FilePathDisk getPath(String var1) {
      FilePathDisk var2 = new FilePathDisk();
      var2.name = translateFileName(var1);
      return var2;
   }

   public long size() {
      if (this.name.startsWith("classpath:")) {
         try {
            String var1 = this.name.substring("classpath:".length());
            if (!var1.startsWith("/")) {
               var1 = "/" + var1;
            }

            URL var2 = this.getClass().getResource(var1);
            return var2 != null ? Files.size(Paths.get(var2.toURI())) : 0L;
         } catch (Exception var3) {
            return 0L;
         }
      } else {
         try {
            return Files.size(Paths.get(this.name));
         } catch (IOException var4) {
            return 0L;
         }
      }
   }

   protected static String translateFileName(String var0) {
      var0 = var0.replace('\\', '/');
      if (var0.startsWith("file:")) {
         var0 = var0.substring(5);
      } else if (var0.startsWith("nio:")) {
         var0 = var0.substring(4);
      }

      return expandUserHomeDirectory(var0);
   }

   public static String expandUserHomeDirectory(String var0) {
      if (var0.startsWith("~") && (var0.length() == 1 || var0.startsWith("~/"))) {
         String var1 = SysProperties.USER_HOME;
         var0 = var1 + var0.substring(1);
      }

      return var0;
   }

   public void moveTo(FilePath var1, boolean var2) {
      Path var3 = Paths.get(this.name);
      Path var4 = Paths.get(var1.name);
      if (!Files.exists(var3, new LinkOption[0])) {
         throw DbException.get(90024, this.name + " (not found)", var1.name);
      } else {
         if (var2) {
            try {
               Files.move(var3, var4, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
               return;
            } catch (AtomicMoveNotSupportedException var14) {
            } catch (IOException var15) {
               throw DbException.get(90024, var15, this.name, var1.name);
            }
         }

         CopyOption[] var5 = var2 ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[0];

         try {
            Files.move(var3, var4, var5);
         } catch (FileAlreadyExistsException var12) {
            throw DbException.get(90024, this.name, var1 + " (exists)");
         } catch (IOException var13) {
            IOException var7 = var13;
            IOException var6 = var13;
            int var8 = 0;

            while(var8 < SysProperties.MAX_FILE_RETRY) {
               IOUtils.trace("rename", this.name + " >" + var1, (Object)null);

               try {
                  Files.move(var3, var4, var5);
                  return;
               } catch (FileAlreadyExistsException var10) {
                  throw DbException.get(90024, this.name, var1 + " (exists)");
               } catch (IOException var11) {
                  var6 = var7;
                  wait(var8);
                  ++var8;
               }
            }

            throw DbException.get(90024, var6, this.name, var1.name);
         }
      }
   }

   private static void wait(int var0) {
      if (var0 == 8) {
         System.gc();
      }

      try {
         long var1 = (long)Math.min(256, var0 * var0);
         Thread.sleep(var1);
      } catch (InterruptedException var3) {
      }

   }

   public boolean createFile() {
      Path var1 = Paths.get(this.name);
      int var2 = 0;

      while(var2 < SysProperties.MAX_FILE_RETRY) {
         try {
            Files.createFile(var1);
            return true;
         } catch (FileAlreadyExistsException var4) {
            return false;
         } catch (IOException var5) {
            wait(var2);
            ++var2;
         }
      }

      return false;
   }

   public boolean exists() {
      return Files.exists(Paths.get(this.name), new LinkOption[0]);
   }

   public void delete() {
      Path var1 = Paths.get(this.name);
      IOException var2 = null;
      int var3 = 0;

      while(var3 < SysProperties.MAX_FILE_RETRY) {
         IOUtils.trace("delete", this.name, (Object)null);

         try {
            Files.deleteIfExists(var1);
            return;
         } catch (DirectoryNotEmptyException var5) {
            throw DbException.get(90025, var5, this.name);
         } catch (IOException var6) {
            var2 = var6;
            wait(var3);
            ++var3;
         }
      }

      throw DbException.get(90025, var2, this.name);
   }

   public List<FilePath> newDirectoryStream() {
      try {
         Stream var1 = Files.list(Paths.get(this.name).toRealPath());
         Throwable var2 = null;

         List var3;
         try {
            var3 = (List)var1.collect(ArrayList::new, (var1x, var2x) -> {
               var1x.add(this.getPath(var2x.toString()));
            }, ArrayList::addAll);
         } catch (Throwable var14) {
            var2 = var14;
            throw var14;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var13) {
                     var2.addSuppressed(var13);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return var3;
      } catch (NoSuchFileException var16) {
         return Collections.emptyList();
      } catch (IOException var17) {
         throw DbException.convertIOException(var17, this.name);
      }
   }

   public boolean canWrite() {
      try {
         return Files.isWritable(Paths.get(this.name));
      } catch (Exception var2) {
         return false;
      }
   }

   public boolean setReadOnly() {
      Path var1 = Paths.get(this.name);

      try {
         FileStore var2 = Files.getFileStore(var1);
         if (var2.supportsFileAttributeView(PosixFileAttributeView.class)) {
            HashSet var3 = new HashSet();
            Iterator var4 = Files.getPosixFilePermissions(var1).iterator();

            while(var4.hasNext()) {
               PosixFilePermission var5 = (PosixFilePermission)var4.next();
               switch (var5) {
                  case OWNER_WRITE:
                  case GROUP_WRITE:
                  case OTHERS_WRITE:
                     break;
                  default:
                     var3.add(var5);
               }
            }

            Files.setPosixFilePermissions(var1, var3);
         } else {
            if (!var2.supportsFileAttributeView(DosFileAttributeView.class)) {
               return false;
            }

            Files.setAttribute(var1, "dos:readonly", true);
         }

         return true;
      } catch (IOException var6) {
         return false;
      }
   }

   public FilePathDisk toRealPath() {
      Path var1 = Paths.get(this.name);

      try {
         return this.getPath(var1.toRealPath().toString());
      } catch (IOException var3) {
         return this.getPath(toRealPath(var1.toAbsolutePath().normalize()).toString());
      }
   }

   private static Path toRealPath(Path var0) {
      Path var1 = var0.getParent();
      if (var1 == null) {
         return var0;
      } else {
         try {
            var1 = var1.toRealPath();
         } catch (IOException var3) {
            var1 = toRealPath(var1);
         }

         return var1.resolve(var0.getFileName());
      }
   }

   public FilePath getParent() {
      Path var1 = Paths.get(this.name).getParent();
      return var1 == null ? null : this.getPath(var1.toString());
   }

   public boolean isDirectory() {
      return Files.isDirectory(Paths.get(this.name), new LinkOption[0]);
   }

   public boolean isAbsolute() {
      return Paths.get(this.name).isAbsolute();
   }

   public long lastModified() {
      try {
         return Files.getLastModifiedTime(Paths.get(this.name)).toMillis();
      } catch (IOException var2) {
         return 0L;
      }
   }

   public void createDirectory() {
      Path var1 = Paths.get(this.name);

      try {
         Files.createDirectory(var1);
      } catch (FileAlreadyExistsException var8) {
         throw DbException.get(90062, this.name + " (a file with this name already exists)");
      } catch (IOException var9) {
         IOException var3 = var9;

         for(int var4 = 0; var4 < SysProperties.MAX_FILE_RETRY; ++var4) {
            if (Files.isDirectory(var1, new LinkOption[0])) {
               return;
            }

            try {
               Files.createDirectory(var1);
            } catch (FileAlreadyExistsException var6) {
               throw DbException.get(90062, this.name + " (a file with this name already exists)");
            } catch (IOException var7) {
               var3 = var7;
            }

            wait(var4);
         }

         throw DbException.get(90062, var3, this.name);
      }
   }

   public OutputStream newOutputStream(boolean var1) throws IOException {
      Path var2 = Paths.get(this.name);
      OpenOption[] var3 = var1 ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND} : new OpenOption[0];

      try {
         Path var4 = var2.getParent();
         if (var4 != null) {
            Files.createDirectories(var4);
         }

         OutputStream var5 = Files.newOutputStream(var2, var3);
         IOUtils.trace("openFileOutputStream", this.name, var5);
         return var5;
      } catch (IOException var6) {
         freeMemoryAndFinalize();
         return Files.newOutputStream(var2, var3);
      }
   }

   public InputStream newInputStream() throws IOException {
      if (this.name.matches("[a-zA-Z]{2,19}:.*")) {
         if (this.name.startsWith("classpath:")) {
            String var4 = this.name.substring("classpath:".length());
            if (!var4.startsWith("/")) {
               var4 = "/" + var4;
            }

            InputStream var2 = this.getClass().getResourceAsStream(var4);
            if (var2 == null) {
               var2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(var4.substring(1));
            }

            if (var2 == null) {
               throw new FileNotFoundException("resource " + var4);
            } else {
               return var2;
            }
         } else {
            URL var3 = new URL(this.name);
            return var3.openStream();
         }
      } else {
         InputStream var1 = Files.newInputStream(Paths.get(this.name));
         IOUtils.trace("openFileInputStream", this.name, var1);
         return var1;
      }
   }

   static void freeMemoryAndFinalize() {
      IOUtils.trace("freeMemoryAndFinalize", (String)null, (Object)null);
      Runtime var0 = Runtime.getRuntime();
      long var1 = var0.freeMemory();

      for(int var3 = 0; var3 < 16; ++var3) {
         var0.gc();
         long var4 = var0.freeMemory();
         var0.runFinalization();
         if (var4 == var1) {
            break;
         }

         var1 = var4;
      }

   }

   public FileChannel open(String var1) throws IOException {
      FileChannel var2 = FileChannel.open(Paths.get(this.name), FileUtils.modeToOptions(var1), FileUtils.NO_ATTRIBUTES);
      IOUtils.trace("open", this.name, var2);
      return var2;
   }

   public String getScheme() {
      return "file";
   }

   public FilePath createTempFile(String var1, boolean var2) throws IOException {
      Path var3 = Paths.get(this.name + '.').toAbsolutePath();
      String var4 = var3.getFileName().toString();
      if (var2) {
         Files.createDirectories(Paths.get(System.getProperty("java.io.tmpdir", ".")));
         var3 = Files.createTempFile(var4, var1);
      } else {
         Path var5 = var3.getParent();
         Files.createDirectories(var5);
         var3 = Files.createTempFile(var5, var4, var1);
      }

      return get(var3.toString());
   }
}
