/*    */ package org.h2.command.ddl;
/*    */ 
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
/*    */ public class DropSequence
/*    */   extends SchemaOwnerCommand
/*    */ {
/*    */   private String sequenceName;
/*    */   private boolean ifExists;
/*    */   
/*    */   public DropSequence(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 25 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 29 */     this.ifExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setSequenceName(String paramString) {
/* 33 */     this.sequenceName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   long update(Schema paramSchema) {
/* 38 */     Sequence sequence = paramSchema.findSequence(this.sequenceName);
/* 39 */     if (sequence == null) {
/* 40 */       if (!this.ifExists) {
/* 41 */         throw DbException.get(90036, this.sequenceName);
/*    */       }
/*    */     } else {
/* 44 */       if (sequence.getBelongsToTable()) {
/* 45 */         throw DbException.get(90082, this.sequenceName);
/*    */       }
/* 47 */       this.session.getDatabase().removeSchemaObject(this.session, (SchemaObject)sequence);
/*    */     } 
/* 49 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 54 */     return 43;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */