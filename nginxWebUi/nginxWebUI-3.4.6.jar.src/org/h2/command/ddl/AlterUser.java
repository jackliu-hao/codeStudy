/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.engine.User;
/*    */ import org.h2.expression.Expression;
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
/*    */ 
/*    */ 
/*    */ public class AlterUser
/*    */   extends DefineCommand
/*    */ {
/*    */   private int type;
/*    */   private User user;
/*    */   private String newName;
/*    */   private Expression password;
/*    */   private Expression salt;
/*    */   private Expression hash;
/*    */   private boolean admin;
/*    */   
/*    */   public AlterUser(SessionLocal paramSessionLocal) {
/* 33 */     super(paramSessionLocal);
/*    */   }
/*    */   
/*    */   public void setType(int paramInt) {
/* 37 */     this.type = paramInt;
/*    */   }
/*    */   
/*    */   public void setNewName(String paramString) {
/* 41 */     this.newName = paramString;
/*    */   }
/*    */   
/*    */   public void setUser(User paramUser) {
/* 45 */     this.user = paramUser;
/*    */   }
/*    */   
/*    */   public void setAdmin(boolean paramBoolean) {
/* 49 */     this.admin = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setSalt(Expression paramExpression) {
/* 53 */     this.salt = paramExpression;
/*    */   }
/*    */   
/*    */   public void setHash(Expression paramExpression) {
/* 57 */     this.hash = paramExpression;
/*    */   }
/*    */   
/*    */   public void setPassword(Expression paramExpression) {
/* 61 */     this.password = paramExpression;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 66 */     Database database = this.session.getDatabase();
/* 67 */     switch (this.type) {
/*    */       case 19:
/* 69 */         if (this.user != this.session.getUser()) {
/* 70 */           this.session.getUser().checkAdmin();
/*    */         }
/* 72 */         if (this.hash != null && this.salt != null) {
/* 73 */           CreateUser.setSaltAndHash(this.user, this.session, this.salt, this.hash);
/*    */         } else {
/* 75 */           CreateUser.setPassword(this.user, this.session, this.password);
/*    */         } 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 92 */         database.updateMeta(this.session, (DbObject)this.user);
/* 93 */         return 0L;case 18: this.session.getUser().checkAdmin(); if (database.findUser(this.newName) != null || this.newName.equals(this.user.getName())) throw DbException.get(90033, this.newName);  database.renameDatabaseObject(this.session, (DbObject)this.user, this.newName); database.updateMeta(this.session, (DbObject)this.user); return 0L;case 17: this.session.getUser().checkAdmin(); this.user.setAdmin(this.admin); database.updateMeta(this.session, (DbObject)this.user); return 0L;
/*    */     } 
/*    */     throw DbException.getInternalError("type=" + this.type);
/*    */   }
/*    */   public int getType() {
/* 98 */     return this.type;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterUser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */