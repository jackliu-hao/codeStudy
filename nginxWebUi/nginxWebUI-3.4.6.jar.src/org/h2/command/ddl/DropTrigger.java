/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
/*    */ import org.h2.schema.TriggerObject;
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
/*    */ public class DropTrigger
/*    */   extends SchemaCommand
/*    */ {
/*    */   private String triggerName;
/*    */   private boolean ifExists;
/*    */   
/*    */   public DropTrigger(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 28 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 32 */     this.ifExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setTriggerName(String paramString) {
/* 36 */     this.triggerName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 41 */     Database database = this.session.getDatabase();
/* 42 */     TriggerObject triggerObject = getSchema().findTrigger(this.triggerName);
/* 43 */     if (triggerObject == null) {
/* 44 */       if (!this.ifExists) {
/* 45 */         throw DbException.get(90042, this.triggerName);
/*    */       }
/*    */     } else {
/* 48 */       Table table = triggerObject.getTable();
/* 49 */       this.session.getUser().checkTableRight(table, 32);
/* 50 */       database.removeSchemaObject(this.session, (SchemaObject)triggerObject);
/*    */     } 
/* 52 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 57 */     return 45;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropTrigger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */