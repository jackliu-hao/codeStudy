/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.RightOwner;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.engine.User;
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
/*    */ public class DropUser
/*    */   extends DefineCommand
/*    */ {
/*    */   private boolean ifExists;
/*    */   private String userName;
/*    */   
/*    */   public DropUser(SessionLocal paramSessionLocal) {
/* 26 */     super(paramSessionLocal);
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 30 */     this.ifExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setUserName(String paramString) {
/* 34 */     this.userName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 39 */     this.session.getUser().checkAdmin();
/* 40 */     Database database = this.session.getDatabase();
/* 41 */     User user = database.findUser(this.userName);
/* 42 */     if (user == null) {
/* 43 */       if (!this.ifExists) {
/* 44 */         throw DbException.get(90032, this.userName);
/*    */       }
/*    */     } else {
/* 47 */       if (user == this.session.getUser()) {
/* 48 */         byte b = 0;
/* 49 */         for (RightOwner rightOwner : database.getAllUsersAndRoles()) {
/* 50 */           if (rightOwner instanceof User && ((User)rightOwner).isAdmin()) {
/* 51 */             b++;
/*    */           }
/*    */         } 
/* 54 */         if (b == 1) {
/* 55 */           throw DbException.get(90019);
/*    */         }
/*    */       } 
/* 58 */       user.checkOwnsNoSchemas();
/* 59 */       database.removeDatabaseObject(this.session, (DbObject)user);
/*    */     } 
/* 61 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isTransactional() {
/* 66 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 71 */     return 46;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropUser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */