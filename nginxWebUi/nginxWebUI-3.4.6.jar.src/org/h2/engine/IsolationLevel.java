/*     */ package org.h2.engine;
/*     */ 
/*     */ import org.h2.message.DbException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum IsolationLevel
/*     */ {
/*  20 */   READ_UNCOMMITTED(1, 0),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  26 */   READ_COMMITTED(2, 3),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  32 */   REPEATABLE_READ(4, 1),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  37 */   SNAPSHOT(6, 1),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   SERIALIZABLE(8, 1);
/*     */ 
/*     */   
/*     */   private final String sql;
/*     */   
/*     */   private final int jdbc;
/*     */   
/*     */   private final int lockMode;
/*     */ 
/*     */   
/*     */   public static IsolationLevel fromJdbc(int paramInt) {
/*  55 */     switch (paramInt) {
/*     */       case 1:
/*  57 */         return READ_UNCOMMITTED;
/*     */       case 2:
/*  59 */         return READ_COMMITTED;
/*     */       case 4:
/*  61 */         return REPEATABLE_READ;
/*     */       case 6:
/*  63 */         return SNAPSHOT;
/*     */       case 8:
/*  65 */         return SERIALIZABLE;
/*     */     } 
/*  67 */     throw DbException.getInvalidValueException("isolation level", Integer.valueOf(paramInt));
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
/*     */   
/*     */   public static IsolationLevel fromLockMode(int paramInt) {
/*  80 */     switch (paramInt)
/*     */     { case 0:
/*  82 */         return READ_UNCOMMITTED;
/*     */       
/*     */       default:
/*  85 */         return READ_COMMITTED;
/*     */       case 1:
/*     */       case 2:
/*  88 */         break; }  return SERIALIZABLE;
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
/*     */   public static IsolationLevel fromSql(String paramString) {
/* 100 */     switch (paramString) {
/*     */       case "READ UNCOMMITTED":
/* 102 */         return READ_UNCOMMITTED;
/*     */       case "READ COMMITTED":
/* 104 */         return READ_COMMITTED;
/*     */       case "REPEATABLE READ":
/* 106 */         return REPEATABLE_READ;
/*     */       case "SNAPSHOT":
/* 108 */         return SNAPSHOT;
/*     */       case "SERIALIZABLE":
/* 110 */         return SERIALIZABLE;
/*     */     } 
/* 112 */     throw DbException.getInvalidValueException("isolation level", paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   IsolationLevel(int paramInt1, int paramInt2) {
/* 121 */     this.sql = name().replace('_', ' ').intern();
/* 122 */     this.jdbc = paramInt1;
/* 123 */     this.lockMode = paramInt2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSQL() {
/* 132 */     return this.sql;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getJdbc() {
/* 141 */     return this.jdbc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLockMode() {
/* 150 */     return this.lockMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allowNonRepeatableRead() {
/* 159 */     return (ordinal() < REPEATABLE_READ.ordinal());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\IsolationLevel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */