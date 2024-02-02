/*    */ package org.h2.table;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.index.Index;
/*    */ import org.h2.index.IndexType;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.result.Row;
/*    */ import org.h2.schema.Schema;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class VirtualTable
/*    */   extends Table
/*    */ {
/*    */   protected VirtualTable(Schema paramSchema, int paramInt, String paramString) {
/* 23 */     super(paramSchema, paramInt, paramString, false, true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void close(SessionLocal paramSessionLocal) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public Index addIndex(SessionLocal paramSessionLocal, String paramString1, int paramInt1, IndexColumn[] paramArrayOfIndexColumn, int paramInt2, IndexType paramIndexType, boolean paramBoolean, String paramString2) {
/* 34 */     throw DbException.getUnsupportedException("Virtual table");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isInsertable() {
/* 39 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 44 */     throw DbException.getUnsupportedException("Virtual table");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public long truncate(SessionLocal paramSessionLocal) {
/* 50 */     throw DbException.getUnsupportedException("Virtual table");
/*    */   }
/*    */ 
/*    */   
/*    */   public void addRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 55 */     throw DbException.getUnsupportedException("Virtual table");
/*    */   }
/*    */ 
/*    */   
/*    */   public void checkSupportAlter() {
/* 60 */     throw DbException.getUnsupportedException("Virtual table");
/*    */   }
/*    */ 
/*    */   
/*    */   public TableType getTableType() {
/* 65 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public ArrayList<Index> getIndexes() {
/* 70 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canReference() {
/* 75 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canDrop() {
/* 80 */     throw DbException.getInternalError(toString());
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCreateSQL() {
/* 85 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void checkRename() {
/* 90 */     throw DbException.getUnsupportedException("Virtual table");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\VirtualTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */