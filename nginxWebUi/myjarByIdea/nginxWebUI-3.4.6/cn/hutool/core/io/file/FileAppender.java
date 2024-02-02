package cn.hutool.core.io.file;

import cn.hutool.core.thread.lock.LockUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

public class FileAppender implements Serializable {
   private static final long serialVersionUID = 1L;
   private final FileWriter writer;
   private final int capacity;
   private final boolean isNewLineMode;
   private final List<String> list;
   private final Lock lock;

   public FileAppender(File destFile, int capacity, boolean isNewLineMode) {
      this(destFile, CharsetUtil.CHARSET_UTF_8, capacity, isNewLineMode);
   }

   public FileAppender(File destFile, Charset charset, int capacity, boolean isNewLineMode) {
      this(destFile, charset, capacity, isNewLineMode, (Lock)null);
   }

   public FileAppender(File destFile, Charset charset, int capacity, boolean isNewLineMode, Lock lock) {
      this.capacity = capacity;
      this.list = new ArrayList(capacity);
      this.isNewLineMode = isNewLineMode;
      this.writer = FileWriter.create(destFile, charset);
      this.lock = (Lock)ObjectUtil.defaultIfNull(lock, (Supplier)(LockUtil::getNoLock));
   }

   public FileAppender append(String line) {
      if (this.list.size() >= this.capacity) {
         this.flush();
      }

      this.lock.lock();

      try {
         this.list.add(line);
      } finally {
         this.lock.unlock();
      }

      return this;
   }

   public FileAppender flush() {
      this.lock.lock();

      try {
         PrintWriter pw = this.writer.getPrintWriter(true);
         Throwable var2 = null;

         try {
            Iterator var3 = this.list.iterator();

            while(var3.hasNext()) {
               String str = (String)var3.next();
               pw.print(str);
               if (this.isNewLineMode) {
                  pw.println();
               }
            }
         } catch (Throwable var19) {
            var2 = var19;
            throw var19;
         } finally {
            if (pw != null) {
               if (var2 != null) {
                  try {
                     pw.close();
                  } catch (Throwable var18) {
                     var2.addSuppressed(var18);
                  }
               } else {
                  pw.close();
               }
            }

         }

         this.list.clear();
      } finally {
         this.lock.unlock();
      }

      return this;
   }
}
