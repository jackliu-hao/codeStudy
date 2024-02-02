/*    */ package cn.hutool.db.meta;
/*    */ 
/*    */ import cn.hutool.db.DbRuntimeException;
/*    */ import java.io.Serializable;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
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
/*    */ public class ColumnIndexInfo
/*    */   implements Serializable, Cloneable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private String columnName;
/*    */   private String ascOrDesc;
/*    */   
/*    */   public static ColumnIndexInfo create(ResultSet rs) {
/*    */     try {
/* 26 */       return new ColumnIndexInfo(rs
/* 27 */           .getString("COLUMN_NAME"), rs
/* 28 */           .getString("ASC_OR_DESC"));
/* 29 */     } catch (SQLException e) {
/* 30 */       throw new DbRuntimeException(e);
/*    */     } 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ColumnIndexInfo(String columnName, String ascOrDesc) {
/* 50 */     this.columnName = columnName;
/* 51 */     this.ascOrDesc = ascOrDesc;
/*    */   }
/*    */   
/*    */   public String getColumnName() {
/* 55 */     return this.columnName;
/*    */   }
/*    */   
/*    */   public void setColumnName(String columnName) {
/* 59 */     this.columnName = columnName;
/*    */   }
/*    */   
/*    */   public String getAscOrDesc() {
/* 63 */     return this.ascOrDesc;
/*    */   }
/*    */   
/*    */   public void setAscOrDesc(String ascOrDesc) {
/* 67 */     this.ascOrDesc = ascOrDesc;
/*    */   }
/*    */ 
/*    */   
/*    */   public ColumnIndexInfo clone() throws CloneNotSupportedException {
/* 72 */     return (ColumnIndexInfo)super.clone();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 77 */     return "ColumnIndexInfo{columnName='" + this.columnName + '\'' + ", ascOrDesc='" + this.ascOrDesc + '\'' + '}';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\meta\ColumnIndexInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */