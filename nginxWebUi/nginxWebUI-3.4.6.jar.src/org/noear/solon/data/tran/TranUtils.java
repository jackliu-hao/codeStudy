/*    */ package org.noear.solon.data.tran;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
/*    */ import javax.sql.DataSource;
/*    */ import org.noear.solon.annotation.Note;
/*    */ import org.noear.solon.core.Aop;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ import org.noear.solon.data.annotation.Tran;
/*    */ import org.noear.solon.ext.RunnableEx;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TranUtils
/*    */ {
/*    */   private static TranExecutor executor = () -> false;
/*    */   
/*    */   static {
/* 21 */     Aop.getAsyn(TranExecutor.class, bw -> executor = (TranExecutor)bw.raw());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void execute(Tran tran, RunnableEx runnable) throws Throwable {
/* 28 */     executor.execute(tran, runnable);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Note("是否在事务中")
/*    */   public static boolean inTrans() {
/* 36 */     return executor.inTrans();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Note("是否在事务中且只读")
/*    */   public static boolean inTransAndReadOnly() {
/* 44 */     return executor.inTransAndReadOnly();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Note("获取链接")
/*    */   public static Connection getConnection(DataSource ds) throws SQLException {
/* 52 */     return executor.getConnection(ds);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tran\TranUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */