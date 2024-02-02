package org.h2.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.store.fs.FileUtils;

public class TempFileDeleter {
   private final ReferenceQueue<Object> queue = new ReferenceQueue();
   private final HashMap<PhantomReference<?>, Object> refMap = new HashMap();

   private TempFileDeleter() {
   }

   public static TempFileDeleter getInstance() {
      return new TempFileDeleter();
   }

   public synchronized Reference<?> addFile(Object var1, Object var2) {
      if (!(var1 instanceof String) && !(var1 instanceof AutoCloseable)) {
         throw DbException.getUnsupportedException("Unsupported resource " + var1);
      } else {
         IOUtils.trace("TempFileDeleter.addFile", var1 instanceof String ? (String)var1 : "-", var2);
         PhantomReference var3 = new PhantomReference(var2, this.queue);
         this.refMap.put(var3, var1);
         this.deleteUnused();
         return var3;
      }
   }

   public synchronized void deleteFile(Reference<?> var1, Object var2) {
      if (var1 != null) {
         Object var3 = this.refMap.remove(var1);
         if (var3 != null) {
            if (SysProperties.CHECK && var2 != null && !var3.equals(var2)) {
               throw DbException.getInternalError("f2:" + var3 + " f:" + var2);
            }

            var2 = var3;
         }
      }

      if (var2 instanceof String) {
         String var7 = (String)var2;
         if (FileUtils.exists(var7)) {
            try {
               IOUtils.trace("TempFileDeleter.deleteFile", var7, (Object)null);
               FileUtils.tryDelete(var7);
            } catch (Exception var6) {
            }
         }
      } else if (var2 instanceof AutoCloseable) {
         AutoCloseable var8 = (AutoCloseable)var2;

         try {
            IOUtils.trace("TempFileDeleter.deleteCloseable", "-", (Object)null);
            var8.close();
         } catch (Exception var5) {
         }
      }

   }

   public void deleteAll() {
      Iterator var1 = (new ArrayList(this.refMap.values())).iterator();

      while(var1.hasNext()) {
         Object var2 = var1.next();
         this.deleteFile((Reference)null, var2);
      }

      this.deleteUnused();
   }

   public void deleteUnused() {
      while(true) {
         if (this.queue != null) {
            Reference var1 = this.queue.poll();
            if (var1 != null) {
               this.deleteFile(var1, (Object)null);
               continue;
            }
         }

         return;
      }
   }

   public void stopAutoDelete(Reference<?> var1, Object var2) {
      IOUtils.trace("TempFileDeleter.stopAutoDelete", var2 instanceof String ? (String)var2 : "-", var1);
      if (var1 != null) {
         Object var3 = this.refMap.remove(var1);
         if (SysProperties.CHECK && (var3 == null || !var3.equals(var2))) {
            throw DbException.getInternalError("f2:" + var3 + ' ' + (var3 == null ? "" : var3) + " f:" + var2);
         }
      }

      this.deleteUnused();
   }
}
