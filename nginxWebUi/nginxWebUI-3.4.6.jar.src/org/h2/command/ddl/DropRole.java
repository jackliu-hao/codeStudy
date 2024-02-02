/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.DbObject;
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
/*    */ public class DropRole
/*    */   extends DefineCommand
/*    */ {
/*    */   private String roleName;
/*    */   private boolean ifExists;
/*    */   
/*    */   public DropRole(SessionLocal paramSessionLocal) {
/* 25 */     super(paramSessionLocal);
/*    */   }
/*    */   
/*    */   public void setRoleName(String paramString) {
/* 29 */     this.roleName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 34 */     this.session.getUser().checkAdmin();
/* 35 */     Database database = this.session.getDatabase();
/* 36 */     Role role = database.findRole(this.roleName);
/* 37 */     if (role == null) {
/* 38 */       if (!this.ifExists) {
/* 39 */         throw DbException.get(90070, this.roleName);
/*    */       }
/*    */     } else {
/* 42 */       if (role == database.getPublicRole()) {
/* 43 */         throw DbException.get(90091, this.roleName);
/*    */       }
/* 45 */       role.checkOwnsNoSchemas();
/* 46 */       database.removeDatabaseObject(this.session, (DbObject)role);
/*    */     } 
/* 48 */     return 0L;
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 52 */     this.ifExists = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 57 */     return 41;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropRole.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */