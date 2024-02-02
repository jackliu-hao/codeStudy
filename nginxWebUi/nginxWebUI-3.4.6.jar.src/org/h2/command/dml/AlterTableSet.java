/*    */ package org.h2.command.dml;
/*    */ 
/*    */ import org.h2.command.ddl.SchemaCommand;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.table.Table;
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
/*    */ public class AlterTableSet
/*    */   extends SchemaCommand
/*    */ {
/*    */   private boolean ifTableExists;
/*    */   private String tableName;
/*    */   private final int type;
/*    */   private final boolean value;
/*    */   private boolean checkExisting;
/*    */   
/*    */   public AlterTableSet(SessionLocal paramSessionLocal, Schema paramSchema, int paramInt, boolean paramBoolean) {
/* 31 */     super(paramSessionLocal, paramSchema);
/* 32 */     this.type = paramInt;
/* 33 */     this.value = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setCheckExisting(boolean paramBoolean) {
/* 37 */     this.checkExisting = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isTransactional() {
/* 42 */     return true;
/*    */   }
/*    */   
/*    */   public void setIfTableExists(boolean paramBoolean) {
/* 46 */     this.ifTableExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setTableName(String paramString) {
/* 50 */     this.tableName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 55 */     Table table = getSchema().resolveTableOrView(this.session, this.tableName);
/* 56 */     if (table == null) {
/* 57 */       if (this.ifTableExists) {
/* 58 */         return 0L;
/*    */       }
/* 60 */       throw DbException.get(42102, this.tableName);
/*    */     } 
/* 62 */     this.session.getUser().checkTableRight(table, 32);
/* 63 */     table.lock(this.session, 2);
/* 64 */     switch (this.type) {
/*    */       case 55:
/* 66 */         table.setCheckForeignKeyConstraints(this.session, this.value, this.value ? this.checkExisting : false);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 72 */         return 0L;
/*    */     } 
/*    */     throw DbException.getInternalError("type=" + this.type);
/*    */   }
/*    */   public int getType() {
/* 77 */     return this.type;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\AlterTableSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */