/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
/*    */ import org.h2.schema.Sequence;
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
/*    */ public class CreateSequence
/*    */   extends SchemaOwnerCommand
/*    */ {
/*    */   private String sequenceName;
/*    */   private boolean ifNotExists;
/*    */   private SequenceOptions options;
/*    */   private boolean belongsToTable;
/*    */   
/*    */   public CreateSequence(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 30 */     super(paramSessionLocal, paramSchema);
/* 31 */     this.transactional = true;
/*    */   }
/*    */   
/*    */   public void setSequenceName(String paramString) {
/* 35 */     this.sequenceName = paramString;
/*    */   }
/*    */   
/*    */   public void setIfNotExists(boolean paramBoolean) {
/* 39 */     this.ifNotExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setOptions(SequenceOptions paramSequenceOptions) {
/* 43 */     this.options = paramSequenceOptions;
/*    */   }
/*    */ 
/*    */   
/*    */   long update(Schema paramSchema) {
/* 48 */     Database database = this.session.getDatabase();
/* 49 */     if (paramSchema.findSequence(this.sequenceName) != null) {
/* 50 */       if (this.ifNotExists) {
/* 51 */         return 0L;
/*    */       }
/* 53 */       throw DbException.get(90035, this.sequenceName);
/*    */     } 
/* 55 */     int i = getObjectId();
/* 56 */     Sequence sequence = new Sequence(this.session, paramSchema, i, this.sequenceName, this.options, this.belongsToTable);
/* 57 */     database.addSchemaObject(this.session, (SchemaObject)sequence);
/* 58 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setBelongsToTable(boolean paramBoolean) {
/* 62 */     this.belongsToTable = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 67 */     return 29;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */