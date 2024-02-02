/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.RightOwner;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.User;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.security.SHA256;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.Value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CreateUser
/*     */   extends DefineCommand
/*     */ {
/*     */   private String userName;
/*     */   private boolean admin;
/*     */   private Expression password;
/*     */   private Expression salt;
/*     */   private Expression hash;
/*     */   private boolean ifNotExists;
/*     */   private String comment;
/*     */   
/*     */   public CreateUser(SessionLocal paramSessionLocal) {
/*  36 */     super(paramSessionLocal);
/*     */   }
/*     */   
/*     */   public void setIfNotExists(boolean paramBoolean) {
/*  40 */     this.ifNotExists = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setUserName(String paramString) {
/*  44 */     this.userName = paramString;
/*     */   }
/*     */   
/*     */   public void setPassword(Expression paramExpression) {
/*  48 */     this.password = paramExpression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void setSaltAndHash(User paramUser, SessionLocal paramSessionLocal, Expression paramExpression1, Expression paramExpression2) {
/*  60 */     paramUser.setSaltAndHash(getByteArray(paramSessionLocal, paramExpression1), getByteArray(paramSessionLocal, paramExpression2));
/*     */   }
/*     */   
/*     */   private static byte[] getByteArray(SessionLocal paramSessionLocal, Expression paramExpression) {
/*  64 */     Value value = paramExpression.optimize(paramSessionLocal).getValue(paramSessionLocal);
/*  65 */     if (DataType.isBinaryStringType(value.getValueType())) {
/*  66 */       byte[] arrayOfByte = value.getBytes();
/*  67 */       return (arrayOfByte == null) ? new byte[0] : arrayOfByte;
/*     */     } 
/*  69 */     String str = value.getString();
/*  70 */     return (str == null) ? new byte[0] : StringUtils.convertHexToBytes(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void setPassword(User paramUser, SessionLocal paramSessionLocal, Expression paramExpression) {
/*     */     byte[] arrayOfByte;
/*  81 */     String str1 = paramExpression.optimize(paramSessionLocal).getValue(paramSessionLocal).getString();
/*  82 */     char[] arrayOfChar = (str1 == null) ? new char[0] : str1.toCharArray();
/*     */     
/*  84 */     String str2 = paramUser.getName();
/*  85 */     if (str2.isEmpty() && arrayOfChar.length == 0) {
/*  86 */       arrayOfByte = new byte[0];
/*     */     } else {
/*  88 */       arrayOfByte = SHA256.getKeyPasswordHash(str2, arrayOfChar);
/*     */     } 
/*  90 */     paramUser.setUserPasswordHash(arrayOfByte);
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/*  95 */     this.session.getUser().checkAdmin();
/*  96 */     Database database = this.session.getDatabase();
/*  97 */     RightOwner rightOwner = database.findUserOrRole(this.userName);
/*  98 */     if (rightOwner != null) {
/*  99 */       if (rightOwner instanceof User) {
/* 100 */         if (this.ifNotExists) {
/* 101 */           return 0L;
/*     */         }
/* 103 */         throw DbException.get(90033, this.userName);
/*     */       } 
/* 105 */       throw DbException.get(90069, this.userName);
/*     */     } 
/* 107 */     int i = getObjectId();
/* 108 */     User user = new User(database, i, this.userName, false);
/* 109 */     user.setAdmin(this.admin);
/* 110 */     user.setComment(this.comment);
/* 111 */     if (this.hash != null && this.salt != null) {
/* 112 */       setSaltAndHash(user, this.session, this.salt, this.hash);
/* 113 */     } else if (this.password != null) {
/* 114 */       setPassword(user, this.session, this.password);
/*     */     } else {
/* 116 */       throw DbException.getInternalError();
/*     */     } 
/* 118 */     database.addDatabaseObject(this.session, (DbObject)user);
/* 119 */     return 0L;
/*     */   }
/*     */   
/*     */   public void setSalt(Expression paramExpression) {
/* 123 */     this.salt = paramExpression;
/*     */   }
/*     */   
/*     */   public void setHash(Expression paramExpression) {
/* 127 */     this.hash = paramExpression;
/*     */   }
/*     */   
/*     */   public void setAdmin(boolean paramBoolean) {
/* 131 */     this.admin = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setComment(String paramString) {
/* 135 */     this.comment = paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 140 */     return 32;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateUser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */