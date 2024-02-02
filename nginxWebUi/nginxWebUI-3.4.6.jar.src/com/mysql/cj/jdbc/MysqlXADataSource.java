/*    */ package com.mysql.cj.jdbc;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertyKey;
/*    */ import com.mysql.cj.exceptions.CJException;
/*    */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
/*    */ import javax.sql.XAConnection;
/*    */ import javax.sql.XADataSource;
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
/*    */ public class MysqlXADataSource
/*    */   extends MysqlDataSource
/*    */   implements XADataSource
/*    */ {
/*    */   static final long serialVersionUID = 7911390333152247455L;
/*    */   
/*    */   public XAConnection getXAConnection() throws SQLException {
/*    */     
/* 52 */     try { Connection conn = getConnection();
/*    */       
/* 54 */       return wrapConnection(conn); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*    */   
/*    */   }
/*    */   
/*    */   public XAConnection getXAConnection(String u, String p) throws SQLException {
/*    */     
/* 60 */     try { Connection conn = getConnection(u, p);
/*    */       
/* 62 */       return wrapConnection(conn); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*    */   
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private XAConnection wrapConnection(Connection conn) throws SQLException {
/* 75 */     if (((Boolean)getBooleanProperty(PropertyKey.pinGlobalTxToPhysicalConnection).getValue()).booleanValue() || ((Boolean)((JdbcConnection)conn)
/* 76 */       .getPropertySet().getBooleanProperty(PropertyKey.pinGlobalTxToPhysicalConnection).getValue()).booleanValue()) {
/* 77 */       return SuspendableXAConnection.getInstance((JdbcConnection)conn);
/*    */     }
/*    */     
/* 80 */     return MysqlXAConnection.getInstance((JdbcConnection)conn, ((Boolean)getBooleanProperty(PropertyKey.logXaCommands).getValue()).booleanValue());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\MysqlXADataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */