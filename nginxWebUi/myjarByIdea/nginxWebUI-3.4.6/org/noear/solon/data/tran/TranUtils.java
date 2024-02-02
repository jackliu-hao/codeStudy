package org.noear.solon.data.tran;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.Aop;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.ext.RunnableEx;

public class TranUtils {
   private static TranExecutor executor = () -> {
      return false;
   };

   public static void execute(Tran tran, RunnableEx runnable) throws Throwable {
      executor.execute(tran, runnable);
   }

   @Note("是否在事务中")
   public static boolean inTrans() {
      return executor.inTrans();
   }

   @Note("是否在事务中且只读")
   public static boolean inTransAndReadOnly() {
      return executor.inTransAndReadOnly();
   }

   @Note("获取链接")
   public static Connection getConnection(DataSource ds) throws SQLException {
      return executor.getConnection(ds);
   }

   static {
      Aop.getAsyn(TranExecutor.class, (bw) -> {
         executor = (TranExecutor)bw.raw();
      });
   }
}
