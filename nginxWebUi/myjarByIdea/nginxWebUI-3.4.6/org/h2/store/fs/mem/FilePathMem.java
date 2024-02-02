package org.h2.store.fs.mem;

import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import org.h2.message.DbException;
import org.h2.store.fs.FilePath;

public class FilePathMem extends FilePath {
   private static final TreeMap<String, FileMemData> MEMORY_FILES = new TreeMap();
   private static final FileMemData DIRECTORY = new FileMemData("", false);

   public FilePathMem getPath(String var1) {
      FilePathMem var2 = new FilePathMem();
      var2.name = getCanonicalPath(var1);
      return var2;
   }

   public long size() {
      return this.getMemoryFile().length();
   }

   public void moveTo(FilePath var1, boolean var2) {
      synchronized(MEMORY_FILES) {
         if (!var2 && !var1.name.equals(this.name) && MEMORY_FILES.containsKey(var1.name)) {
            throw DbException.get(90024, this.name, var1 + " (exists)");
         } else {
            FileMemData var4 = this.getMemoryFile();
            var4.setName(var1.name);
            MEMORY_FILES.remove(this.name);
            MEMORY_FILES.put(var1.name, var4);
         }
      }
   }

   public boolean createFile() {
      synchronized(MEMORY_FILES) {
         if (this.exists()) {
            return false;
         } else {
            this.getMemoryFile();
            return true;
         }
      }
   }

   public boolean exists() {
      if (this.isRoot()) {
         return true;
      } else {
         synchronized(MEMORY_FILES) {
            return MEMORY_FILES.get(this.name) != null;
         }
      }
   }

   public void delete() {
      if (!this.isRoot()) {
         synchronized(MEMORY_FILES) {
            FileMemData var2 = (FileMemData)MEMORY_FILES.remove(this.name);
            if (var2 != null) {
               var2.truncate(0L);
            }

         }
      }
   }

   public List<FilePath> newDirectoryStream() {
      ArrayList var1 = new ArrayList();
      synchronized(MEMORY_FILES) {
         Iterator var3 = MEMORY_FILES.tailMap(this.name).keySet().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            if (!var4.startsWith(this.name)) {
               break;
            }

            if (!var4.equals(this.name) && var4.indexOf(47, this.name.length() + 1) < 0) {
               var1.add(this.getPath(var4));
            }
         }

         return var1;
      }
   }

   public boolean setReadOnly() {
      return this.getMemoryFile().setReadOnly();
   }

   public boolean canWrite() {
      return this.getMemoryFile().canWrite();
   }

   public FilePathMem getParent() {
      int var1 = this.name.lastIndexOf(47);
      return var1 < 0 ? null : this.getPath(this.name.substring(0, var1));
   }

   public boolean isDirectory() {
      if (this.isRoot()) {
         return true;
      } else {
         synchronized(MEMORY_FILES) {
            FileMemData var2 = (FileMemData)MEMORY_FILES.get(this.name);
            return var2 == DIRECTORY;
         }
      }
   }

   public boolean isAbsolute() {
      return true;
   }

   public FilePathMem toRealPath() {
      return this;
   }

   public long lastModified() {
      return this.getMemoryFile().getLastModified();
   }

   public void createDirectory() {
      if (this.exists()) {
         throw DbException.get(90062, this.name + " (a file with this name already exists)");
      } else {
         synchronized(MEMORY_FILES) {
            MEMORY_FILES.put(this.name, DIRECTORY);
         }
      }
   }

   public FileChannel open(String var1) {
      FileMemData var2 = this.getMemoryFile();
      return new FileMem(var2, "r".equals(var1));
   }

   private FileMemData getMemoryFile() {
      synchronized(MEMORY_FILES) {
         FileMemData var2 = (FileMemData)MEMORY_FILES.get(this.name);
         if (var2 == DIRECTORY) {
            throw DbException.get(90062, this.name + " (a directory with this name already exists)");
         } else {
            if (var2 == null) {
               var2 = new FileMemData(this.name, this.compressed());
               MEMORY_FILES.put(this.name, var2);
            }

            return var2;
         }
      }
   }

   private boolean isRoot() {
      return this.name.equals(this.getScheme() + ":");
   }

   protected static String getCanonicalPath(String var0) {
      var0 = var0.replace('\\', '/');
      int var1 = var0.indexOf(58) + 1;
      if (var0.length() > var1 && var0.charAt(var1) != '/') {
         var0 = var0.substring(0, var1) + "/" + var0.substring(var1);
      }

      return var0;
   }

   public String getScheme() {
      return "memFS";
   }

   boolean compressed() {
      return false;
   }
}
