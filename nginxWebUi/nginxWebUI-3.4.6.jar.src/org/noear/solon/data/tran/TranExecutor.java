/*    */ package org.noear.solon.data.tran;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
/*    */ import javax.sql.DataSource;
/*    */ import org.noear.solon.data.annotation.Tran;
/*    */ import org.noear.solon.ext.RunnableEx;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface TranExecutor
/*    */ {
/*    */   boolean inTrans();
/*    */   
/*    */   default boolean inTransAndReadOnly() {
/* 26 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default Connection getConnection(DataSource ds) throws SQLException {
/* 35 */     return ds.getConnection();
/*    */   }
/*    */   
/*    */   default void execute(Tran meta, RunnableEx runnable) throws Throwable {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tran\TranExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */