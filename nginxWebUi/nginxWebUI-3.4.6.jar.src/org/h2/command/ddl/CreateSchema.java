/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.RightOwner;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
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
/*    */ public class CreateSchema
/*    */   extends DefineCommand
/*    */ {
/*    */   private String schemaName;
/*    */   private String authorization;
/*    */   private boolean ifNotExists;
/*    */   private ArrayList<String> tableEngineParams;
/*    */   
/*    */   public CreateSchema(SessionLocal paramSessionLocal) {
/* 29 */     super(paramSessionLocal);
/*    */   }
/*    */   
/*    */   public void setIfNotExists(boolean paramBoolean) {
/* 33 */     this.ifNotExists = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 38 */     this.session.getUser().checkSchemaAdmin();
/* 39 */     Database database = this.session.getDatabase();
/* 40 */     RightOwner rightOwner = database.findUserOrRole(this.authorization);
/* 41 */     if (rightOwner == null) {
/* 42 */       throw DbException.get(90071, this.authorization);
/*    */     }
/* 44 */     if (database.findSchema(this.schemaName) != null) {
/* 45 */       if (this.ifNotExists) {
/* 46 */         return 0L;
/*    */       }
/* 48 */       throw DbException.get(90078, this.schemaName);
/*    */     } 
/* 50 */     int i = getObjectId();
/* 51 */     Schema schema = new Schema(database, i, this.schemaName, rightOwner, false);
/* 52 */     schema.setTableEngineParams(this.tableEngineParams);
/* 53 */     database.addDatabaseObject(this.session, (DbObject)schema);
/* 54 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setSchemaName(String paramString) {
/* 58 */     this.schemaName = paramString;
/*    */   }
/*    */   
/*    */   public void setAuthorization(String paramString) {
/* 62 */     this.authorization = paramString;
/*    */   }
/*    */   
/*    */   public void setTableEngineParams(ArrayList<String> paramArrayList) {
/* 66 */     this.tableEngineParams = paramArrayList;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 71 */     return 28;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */