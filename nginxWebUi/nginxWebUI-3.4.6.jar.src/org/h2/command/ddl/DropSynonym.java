/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
/*    */ import org.h2.table.TableSynonym;
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
/*    */ public class DropSynonym
/*    */   extends SchemaOwnerCommand
/*    */ {
/*    */   private String synonymName;
/*    */   private boolean ifExists;
/*    */   
/*    */   public DropSynonym(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 25 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public void setSynonymName(String paramString) {
/* 29 */     this.synonymName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   long update(Schema paramSchema) {
/* 34 */     TableSynonym tableSynonym = paramSchema.getSynonym(this.synonymName);
/* 35 */     if (tableSynonym == null) {
/* 36 */       if (!this.ifExists) {
/* 37 */         throw DbException.get(42102, this.synonymName);
/*    */       }
/*    */     } else {
/* 40 */       this.session.getDatabase().removeSchemaObject(this.session, (SchemaObject)tableSynonym);
/*    */     } 
/* 42 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 46 */     this.ifExists = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 51 */     return 89;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropSynonym.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */