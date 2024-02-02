/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.RightOwner;
/*    */ import org.h2.engine.Role;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
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
/*    */ public class CreateRole
/*    */   extends DefineCommand
/*    */ {
/*    */   private String roleName;
/*    */   private boolean ifNotExists;
/*    */   
/*    */   public CreateRole(SessionLocal paramSessionLocal) {
/* 26 */     super(paramSessionLocal);
/*    */   }
/*    */   
/*    */   public void setIfNotExists(boolean paramBoolean) {
/* 30 */     this.ifNotExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setRoleName(String paramString) {
/* 34 */     this.roleName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 39 */     this.session.getUser().checkAdmin();
/* 40 */     Database database = this.session.getDatabase();
/* 41 */     RightOwner rightOwner = database.findUserOrRole(this.roleName);
/* 42 */     if (rightOwner != null) {
/* 43 */       if (rightOwner instanceof Role) {
/* 44 */         if (this.ifNotExists) {
/* 45 */           return 0L;
/*    */         }
/* 47 */         throw DbException.get(90069, this.roleName);
/*    */       } 
/* 49 */       throw DbException.get(90033, this.roleName);
/*    */     } 
/* 51 */     int i = getObjectId();
/* 52 */     Role role = new Role(database, i, this.roleName, false);
/* 53 */     database.addDatabaseObject(this.session, (DbObject)role);
/* 54 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 59 */     return 27;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateRole.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */