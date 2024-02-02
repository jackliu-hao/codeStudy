/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
/*    */ import org.h2.schema.UserAggregate;
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
/*    */ public class CreateAggregate
/*    */   extends SchemaCommand
/*    */ {
/*    */   private String name;
/*    */   private String javaClassMethod;
/*    */   private boolean ifNotExists;
/*    */   private boolean force;
/*    */   
/*    */   public CreateAggregate(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 28 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 33 */     this.session.getUser().checkAdmin();
/* 34 */     Database database = this.session.getDatabase();
/* 35 */     Schema schema = getSchema();
/* 36 */     if (schema.findFunctionOrAggregate(this.name) != null) {
/* 37 */       if (!this.ifNotExists) {
/* 38 */         throw DbException.get(90076, this.name);
/*    */       }
/*    */     } else {
/* 41 */       int i = getObjectId();
/* 42 */       UserAggregate userAggregate = new UserAggregate(schema, i, this.name, this.javaClassMethod, this.force);
/* 43 */       database.addSchemaObject(this.session, (SchemaObject)userAggregate);
/*    */     } 
/* 45 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setName(String paramString) {
/* 49 */     this.name = paramString;
/*    */   }
/*    */   
/*    */   public void setJavaClassMethod(String paramString) {
/* 53 */     this.javaClassMethod = paramString;
/*    */   }
/*    */   
/*    */   public void setIfNotExists(boolean paramBoolean) {
/* 57 */     this.ifNotExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setForce(boolean paramBoolean) {
/* 61 */     this.force = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 66 */     return 22;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateAggregate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */