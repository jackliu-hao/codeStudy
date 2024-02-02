/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Constant;
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
/*    */ public class DropConstant
/*    */   extends SchemaOwnerCommand
/*    */ {
/*    */   private String constantName;
/*    */   private boolean ifExists;
/*    */   
/*    */   public DropConstant(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 26 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 30 */     this.ifExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setConstantName(String paramString) {
/* 34 */     this.constantName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   long update(Schema paramSchema) {
/* 39 */     Database database = this.session.getDatabase();
/* 40 */     Constant constant = paramSchema.findConstant(this.constantName);
/* 41 */     if (constant == null) {
/* 42 */       if (!this.ifExists) {
/* 43 */         throw DbException.get(90115, this.constantName);
/*    */       }
/*    */     } else {
/* 46 */       database.removeSchemaObject(this.session, (SchemaObject)constant);
/*    */     } 
/* 48 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 53 */     return 37;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropConstant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */