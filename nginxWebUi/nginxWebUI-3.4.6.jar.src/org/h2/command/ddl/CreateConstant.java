/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Constant;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
/*    */ import org.h2.value.Value;
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
/*    */ public class CreateConstant
/*    */   extends SchemaOwnerCommand
/*    */ {
/*    */   private String constantName;
/*    */   private Expression expression;
/*    */   private boolean ifNotExists;
/*    */   
/*    */   public CreateConstant(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 29 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public void setIfNotExists(boolean paramBoolean) {
/* 33 */     this.ifNotExists = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   long update(Schema paramSchema) {
/* 38 */     Database database = this.session.getDatabase();
/* 39 */     if (paramSchema.findConstant(this.constantName) != null) {
/* 40 */       if (this.ifNotExists) {
/* 41 */         return 0L;
/*    */       }
/* 43 */       throw DbException.get(90114, this.constantName);
/*    */     } 
/* 45 */     int i = getObjectId();
/* 46 */     Constant constant = new Constant(paramSchema, i, this.constantName);
/* 47 */     this.expression = this.expression.optimize(this.session);
/* 48 */     Value value = this.expression.getValue(this.session);
/* 49 */     constant.setValue(value);
/* 50 */     database.addSchemaObject(this.session, (SchemaObject)constant);
/* 51 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setConstantName(String paramString) {
/* 55 */     this.constantName = paramString;
/*    */   }
/*    */   
/*    */   public void setExpression(Expression paramExpression) {
/* 59 */     this.expression = paramExpression;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 64 */     return 23;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateConstant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */