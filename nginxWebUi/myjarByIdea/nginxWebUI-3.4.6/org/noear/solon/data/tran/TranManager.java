package org.noear.solon.data.tran;

import org.noear.solon.data.tranImp.DbTran;

public final class TranManager {
   private static final ThreadLocal<DbTran> _tl_tran = new ThreadLocal();

   private TranManager() {
   }

   public static void currentSet(DbTran tran) {
      _tl_tran.set(tran);
   }

   public static DbTran current() {
      return (DbTran)_tl_tran.get();
   }

   public static void currentRemove() {
      _tl_tran.remove();
   }

   public static DbTran trySuspend() {
      DbTran tran = current();
      if (tran != null) {
         currentRemove();
      }

      return tran;
   }

   public static void tryResume(DbTran tran) {
      if (tran != null) {
         currentSet(tran);
      }

   }
}
