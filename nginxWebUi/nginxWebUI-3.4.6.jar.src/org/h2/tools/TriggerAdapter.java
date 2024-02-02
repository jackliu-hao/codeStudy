/*    */ package org.h2.tools;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import org.h2.api.Trigger;
/*    */ import org.h2.message.DbException;
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
/*    */ public abstract class TriggerAdapter
/*    */   implements Trigger
/*    */ {
/*    */   protected String schemaName;
/*    */   protected String triggerName;
/*    */   protected String tableName;
/*    */   protected boolean before;
/*    */   protected int type;
/*    */   
/*    */   public void init(Connection paramConnection, String paramString1, String paramString2, String paramString3, boolean paramBoolean, int paramInt) throws SQLException {
/* 67 */     this.schemaName = paramString1;
/* 68 */     this.triggerName = paramString2;
/* 69 */     this.tableName = paramString3;
/* 70 */     this.before = paramBoolean;
/* 71 */     this.type = paramInt;
/*    */   }
/*    */ 
/*    */   
/*    */   public final void fire(Connection paramConnection, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) throws SQLException {
/* 76 */     throw DbException.getInternalError();
/*    */   }
/*    */   
/*    */   public abstract void fire(Connection paramConnection, ResultSet paramResultSet1, ResultSet paramResultSet2) throws SQLException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\TriggerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */