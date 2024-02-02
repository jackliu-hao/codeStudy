/*    */ package com.mysql.cj.jdbc.result;
/*    */ 
/*    */ import com.mysql.cj.result.DefaultColumnDefinition;
/*    */ import java.sql.ResultSetMetaData;
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
/*    */ public class CachedResultSetMetaDataImpl
/*    */   extends DefaultColumnDefinition
/*    */   implements CachedResultSetMetaData
/*    */ {
/*    */   ResultSetMetaData metadata;
/*    */   
/*    */   public ResultSetMetaData getMetadata() {
/* 41 */     return this.metadata;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setMetadata(ResultSetMetaData metadata) {
/* 46 */     this.metadata = metadata;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\result\CachedResultSetMetaDataImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */