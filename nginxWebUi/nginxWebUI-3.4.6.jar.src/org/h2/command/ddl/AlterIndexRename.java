/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.index.Index;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
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
/*    */ public class AlterIndexRename
/*    */   extends DefineCommand
/*    */ {
/*    */   private boolean ifExists;
/*    */   private Schema oldSchema;
/*    */   private String oldIndexName;
/*    */   private String newIndexName;
/*    */   
/*    */   public AlterIndexRename(SessionLocal paramSessionLocal) {
/* 29 */     super(paramSessionLocal);
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 33 */     this.ifExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setOldSchema(Schema paramSchema) {
/* 37 */     this.oldSchema = paramSchema;
/*    */   }
/*    */   
/*    */   public void setOldName(String paramString) {
/* 41 */     this.oldIndexName = paramString;
/*    */   }
/*    */   
/*    */   public void setNewName(String paramString) {
/* 45 */     this.newIndexName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 50 */     Database database = this.session.getDatabase();
/* 51 */     Index index = this.oldSchema.findIndex(this.session, this.oldIndexName);
/* 52 */     if (index == null) {
/* 53 */       if (!this.ifExists) {
/* 54 */         throw DbException.get(42112, this.newIndexName);
/*    */       }
/*    */       
/* 57 */       return 0L;
/*    */     } 
/* 59 */     if (this.oldSchema.findIndex(this.session, this.newIndexName) != null || this.newIndexName
/* 60 */       .equals(this.oldIndexName)) {
/* 61 */       throw DbException.get(42111, this.newIndexName);
/*    */     }
/*    */     
/* 64 */     this.session.getUser().checkTableRight(index.getTable(), 32);
/* 65 */     database.renameSchemaObject(this.session, (SchemaObject)index, this.newIndexName);
/* 66 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 71 */     return 1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterIndexRename.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */