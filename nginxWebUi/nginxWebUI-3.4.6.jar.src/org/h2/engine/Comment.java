/*     */ package org.h2.engine;
/*     */ 
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Comment
/*     */   extends DbObject
/*     */ {
/*     */   private final int objectType;
/*     */   private final String quotedObjectName;
/*     */   private String commentText;
/*     */   
/*     */   public Comment(Database paramDatabase, int paramInt, DbObject paramDbObject) {
/*  23 */     super(paramDatabase, paramInt, getKey(paramDbObject), 2);
/*  24 */     this.objectType = paramDbObject.getType();
/*  25 */     this.quotedObjectName = paramDbObject.getSQL(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/*  30 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */   
/*     */   private static String getTypeName(int paramInt) {
/*  34 */     switch (paramInt) {
/*     */       case 11:
/*  36 */         return "CONSTANT";
/*     */       case 5:
/*  38 */         return "CONSTRAINT";
/*     */       case 9:
/*  40 */         return "ALIAS";
/*     */       case 1:
/*  42 */         return "INDEX";
/*     */       case 7:
/*  44 */         return "ROLE";
/*     */       case 10:
/*  46 */         return "SCHEMA";
/*     */       case 3:
/*  48 */         return "SEQUENCE";
/*     */       case 0:
/*  50 */         return "TABLE";
/*     */       case 4:
/*  52 */         return "TRIGGER";
/*     */       case 2:
/*  54 */         return "USER";
/*     */       case 12:
/*  56 */         return "DOMAIN";
/*     */     } 
/*     */ 
/*     */     
/*  60 */     return "type" + paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  66 */     StringBuilder stringBuilder = new StringBuilder("COMMENT ON ");
/*  67 */     stringBuilder.append(getTypeName(this.objectType)).append(' ')
/*  68 */       .append(this.quotedObjectName).append(" IS ");
/*  69 */     if (this.commentText == null) {
/*  70 */       stringBuilder.append("NULL");
/*     */     } else {
/*  72 */       StringUtils.quoteStringSQL(stringBuilder, this.commentText);
/*     */     } 
/*  74 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/*  79 */     return 13;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/*  84 */     this.database.removeMeta(paramSessionLocal, getId());
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkRename() {
/*  89 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String getKey(DbObject paramDbObject) {
/* 100 */     StringBuilder stringBuilder = (new StringBuilder(getTypeName(paramDbObject.getType()))).append(' ');
/* 101 */     paramDbObject.getSQL(stringBuilder, 0);
/* 102 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCommentText(String paramString) {
/* 111 */     this.commentText = paramString;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\Comment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */