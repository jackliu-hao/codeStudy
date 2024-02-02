package cn.hutool.cache.file;

import cn.hutool.cache.Cache;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import java.io.File;
import java.io.Serializable;

public abstract class AbstractFileCache implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final int capacity;
   protected final int maxFileSize;
   protected final long timeout;
   protected final Cache<File, byte[]> cache;
   protected int usedSize;

   public AbstractFileCache(int capacity, int maxFileSize, long timeout) {
      this.capacity = capacity;
      this.maxFileSize = maxFileSize;
      this.timeout = timeout;
      this.cache = this.initCache();
   }

   public int capacity() {
      return this.capacity;
   }

   public int getUsedSize() {
      return this.usedSize;
   }

   public int maxFileSize() {
      return this.maxFileSize;
   }

   public int getCachedFilesCount() {
      return this.cache.size();
   }

   public long timeout() {
      return this.timeout;
   }

   public void clear() {
      this.cache.clear();
      this.usedSize = 0;
   }

   public byte[] getFileBytes(String path) throws IORuntimeException {
      return this.getFileBytes(new File(path));
   }

   public byte[] getFileBytes(File file) throws IORuntimeException {
      byte[] bytes = (byte[])this.cache.get(file);
      if (bytes != null) {
         return bytes;
      } else {
         bytes = FileUtil.readBytes(file);
         if (this.maxFileSize != 0 && file.length() > (long)this.maxFileSize) {
            return bytes;
         } else {
            this.usedSize += bytes.length;
            this.cache.put(file, bytes);
            return bytes;
         }
      }
   }

   protected abstract Cache<File, byte[]> initCache();
}
