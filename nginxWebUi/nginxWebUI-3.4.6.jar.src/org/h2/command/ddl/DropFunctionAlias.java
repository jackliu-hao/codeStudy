/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.FunctionAlias;
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
/*    */ public class DropFunctionAlias
/*    */   extends SchemaOwnerCommand
/*    */ {
/*    */   private String aliasName;
/*    */   private boolean ifExists;
/*    */   
/*    */   public DropFunctionAlias(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 26 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */ 
/*    */   
/*    */   long update(Schema paramSchema) {
/* 31 */     Database database = this.session.getDatabase();
/* 32 */     FunctionAlias functionAlias = paramSchema.findFunction(this.aliasName);
/* 33 */     if (functionAlias == null) {
/* 34 */       if (!this.ifExists) {
/* 35 */         throw DbException.get(90077, this.aliasName);
/*    */       }
/*    */     } else {
/* 38 */       database.removeSchemaObject(this.session, (SchemaObject)functionAlias);
/*    */     } 
/* 40 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setAliasName(String paramString) {
/* 44 */     this.aliasName = paramString;
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 48 */     this.ifExists = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 53 */     return 39;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropFunctionAlias.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */