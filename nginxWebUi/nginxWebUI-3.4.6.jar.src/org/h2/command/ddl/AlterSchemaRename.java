/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.SessionLocal;
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
/*    */ public class AlterSchemaRename
/*    */   extends DefineCommand
/*    */ {
/*    */   private Schema oldSchema;
/*    */   private String newSchemaName;
/*    */   
/*    */   public AlterSchemaRename(SessionLocal paramSessionLocal) {
/* 27 */     super(paramSessionLocal);
/*    */   }
/*    */   
/*    */   public void setOldSchema(Schema paramSchema) {
/* 31 */     this.oldSchema = paramSchema;
/*    */   }
/*    */   
/*    */   public void setNewName(String paramString) {
/* 35 */     this.newSchemaName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 40 */     this.session.getUser().checkSchemaAdmin();
/* 41 */     Database database = this.session.getDatabase();
/* 42 */     if (!this.oldSchema.canDrop()) {
/* 43 */       throw DbException.get(90090, this.oldSchema.getName());
/*    */     }
/* 45 */     if (database.findSchema(this.newSchemaName) != null || this.newSchemaName.equals(this.oldSchema.getName())) {
/* 46 */       throw DbException.get(90078, this.newSchemaName);
/*    */     }
/* 48 */     database.renameDatabaseObject(this.session, (DbObject)this.oldSchema, this.newSchemaName);
/* 49 */     ArrayList arrayList = new ArrayList();
/* 50 */     for (Schema schema : database.getAllSchemas()) {
/* 51 */       schema.getAll(arrayList);
/* 52 */       for (SchemaObject schemaObject : arrayList) {
/* 53 */         database.updateMeta(this.session, (DbObject)schemaObject);
/*    */       }
/* 55 */       arrayList.clear();
/*    */     } 
/* 57 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 62 */     return 2;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterSchemaRename.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */