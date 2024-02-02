package org.h2.store.fs.niomem;

import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import org.h2.message.DbException;
import org.h2.store.fs.FilePath;

public class FilePathNioMem extends FilePath {
   private static final TreeMap<String, FileNioMemData> MEMORY_FILES = new TreeMap();
   float compressLaterCachePercent = 1.0F;

   public FilePathNioMem getPath(String var1) {
      FilePathNioMem var2 = new FilePathNioMem();
      var2.name = getCanonicalPath(var1);
      return var2;
   }

   public long size() {
      return this.getMemoryFile().length();
   }

   public void moveTo(FilePath var1, boolean var2) {
      synchronized(MEMORY_FILES) {
         if (!var2 && !this.name.equals(var1.name) && MEMORY_FILES.containsKey(var1.name)) {
            throw DbException.get(90024, this.name, var1 + " (exists)");
         } else {
            FileNioMemData var4 = this.getMemoryFile();
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
            MEMORY_FILES.remove(this.name);
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

            var1.add(this.getPath(var4));
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

   public FilePathNioMem getParent() {
      int var1 = this.name.lastIndexOf(47);
      return var1 < 0 ? null : this.getPath(this.name.substring(0, var1));
   }

   public boolean isDirectory() {
      if (this.isRoot()) {
         return true;
      } else {
         synchronized(MEMORY_FILES) {
            return MEMORY_FILES.get(this.name) == null;
         }
      }
   }

   public boolean isAbsolute() {
      return true;
   }

   public FilePathNioMem toRealPath() {
      return this;
   }

   public long lastModified() {
      return this.getMemoryFile().getLastModified();
   }

   public void createDirectory() {
      if (this.exists() && this.isDirectory()) {
         throw DbException.get(90062, this.name + " (a file with this name already exists)");
      }
   }

   public FileChannel open(String var1) {
      FileNioMemData var2 = this.getMemoryFile();
      return new FileNioMem(var2, "r".equals(var1));
   }

   private FileNioMemData getMemoryFile() {
      synchronized(MEMORY_FILES) {
         FileNioMemData var2 = (FileNioMemData)MEMORY_FILES.get(this.name);
         if (var2 == null) {
            var2 = new FileNioMemData(this.name, this.compressed(), this.compressLaterCachePercent);
            MEMORY_FILES.put(this.name, var2);
         }

         return var2;
      }
   }

   protected boolean isRoot() {
      return this.name.equals(this.getScheme() + ":");
   }

   protected static String getCanonicalPath(String var0) {
      var0 = var0.replace('\\', '/');
      int var1 = var0.lastIndexOf(58) + 1;
      if (var0.length() > var1 && var0.charAt(var1) != '/') {
         var0 = var0.substring(0, var1) + "/" + var0.substring(var1);
      }

      return var0;
   }

   public String getScheme() {
      return "nioMemFS";
   }

   boolean compressed() {
      return false;
   }
}
