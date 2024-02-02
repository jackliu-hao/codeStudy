/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.FunctionAlias;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
/*    */ import org.h2.util.StringUtils;
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
/*    */ public class CreateFunctionAlias
/*    */   extends SchemaCommand
/*    */ {
/*    */   private String aliasName;
/*    */   private String javaClassMethod;
/*    */   private boolean deterministic;
/*    */   private boolean ifNotExists;
/*    */   private boolean force;
/*    */   private String source;
/*    */   
/*    */   public CreateFunctionAlias(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 31 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 36 */     this.session.getUser().checkAdmin();
/* 37 */     Database database = this.session.getDatabase();
/* 38 */     Schema schema = getSchema();
/* 39 */     if (schema.findFunctionOrAggregate(this.aliasName) != null) {
/* 40 */       if (!this.ifNotExists)
/* 41 */         throw DbException.get(90076, this.aliasName); 
/*    */     } else {
/*    */       FunctionAlias functionAlias;
/* 44 */       int i = getObjectId();
/*    */       
/* 46 */       if (this.javaClassMethod != null) {
/* 47 */         functionAlias = FunctionAlias.newInstance(schema, i, this.aliasName, this.javaClassMethod, this.force);
/*    */       } else {
/* 49 */         functionAlias = FunctionAlias.newInstanceFromSource(schema, i, this.aliasName, this.source, this.force);
/*    */       } 
/* 51 */       functionAlias.setDeterministic(this.deterministic);
/* 52 */       database.addSchemaObject(this.session, (SchemaObject)functionAlias);
/*    */     } 
/* 54 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setAliasName(String paramString) {
/* 58 */     this.aliasName = paramString;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setJavaClassMethod(String paramString) {
/* 67 */     this.javaClassMethod = StringUtils.replaceAll(paramString, " ", "");
/*    */   }
/*    */   
/*    */   public void setIfNotExists(boolean paramBoolean) {
/* 71 */     this.ifNotExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setForce(boolean paramBoolean) {
/* 75 */     this.force = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setDeterministic(boolean paramBoolean) {
/* 79 */     this.deterministic = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setSource(String paramString) {
/* 83 */     this.source = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 88 */     return 24;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateFunctionAlias.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */