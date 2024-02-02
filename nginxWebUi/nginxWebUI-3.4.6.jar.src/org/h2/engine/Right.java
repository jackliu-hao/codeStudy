/*     */ package org.h2.engine;
/*     */ 
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.Table;
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
/*     */ public final class Right
/*     */   extends DbObject
/*     */ {
/*     */   public static final int SELECT = 1;
/*     */   public static final int DELETE = 2;
/*     */   public static final int INSERT = 4;
/*     */   public static final int UPDATE = 8;
/*     */   public static final int ALTER_ANY_SCHEMA = 16;
/*     */   public static final int SCHEMA_OWNER = 32;
/*     */   public static final int ALL = 15;
/*     */   private RightOwner grantee;
/*     */   private Role grantedRole;
/*     */   private int grantedRight;
/*     */   private DbObject grantedObject;
/*     */   
/*     */   public Right(Database paramDatabase, int paramInt, RightOwner paramRightOwner, Role paramRole) {
/*  77 */     super(paramDatabase, paramInt, "RIGHT_" + paramInt, 13);
/*  78 */     this.grantee = paramRightOwner;
/*  79 */     this.grantedRole = paramRole;
/*     */   }
/*     */   
/*     */   public Right(Database paramDatabase, int paramInt1, RightOwner paramRightOwner, int paramInt2, DbObject paramDbObject) {
/*  83 */     super(paramDatabase, paramInt1, Integer.toString(paramInt1), 13);
/*  84 */     this.grantee = paramRightOwner;
/*  85 */     this.grantedRight = paramInt2;
/*  86 */     this.grantedObject = paramDbObject;
/*     */   }
/*     */   
/*     */   private static boolean appendRight(StringBuilder paramStringBuilder, int paramInt1, int paramInt2, String paramString, boolean paramBoolean) {
/*  90 */     if ((paramInt1 & paramInt2) != 0) {
/*  91 */       if (paramBoolean) {
/*  92 */         paramStringBuilder.append(", ");
/*     */       }
/*  94 */       paramStringBuilder.append(paramString);
/*  95 */       return true;
/*     */     } 
/*  97 */     return paramBoolean;
/*     */   }
/*     */   
/*     */   public String getRights() {
/* 101 */     StringBuilder stringBuilder = new StringBuilder();
/* 102 */     if (this.grantedRight == 15) {
/* 103 */       stringBuilder.append("ALL");
/*     */     } else {
/* 105 */       boolean bool = false;
/* 106 */       bool = appendRight(stringBuilder, this.grantedRight, 1, "SELECT", bool);
/* 107 */       bool = appendRight(stringBuilder, this.grantedRight, 2, "DELETE", bool);
/* 108 */       bool = appendRight(stringBuilder, this.grantedRight, 4, "INSERT", bool);
/* 109 */       bool = appendRight(stringBuilder, this.grantedRight, 8, "UPDATE", bool);
/* 110 */       appendRight(stringBuilder, this.grantedRight, 16, "ALTER ANY SCHEMA", bool);
/*     */     } 
/* 112 */     return stringBuilder.toString();
/*     */   }
/*     */   
/*     */   public Role getGrantedRole() {
/* 116 */     return this.grantedRole;
/*     */   }
/*     */   
/*     */   public DbObject getGrantedObject() {
/* 120 */     return this.grantedObject;
/*     */   }
/*     */   
/*     */   public DbObject getGrantee() {
/* 124 */     return this.grantee;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/* 129 */     return getCreateSQLForCopy((DbObject)paramTable);
/*     */   }
/*     */   
/*     */   private String getCreateSQLForCopy(DbObject paramDbObject) {
/* 133 */     StringBuilder stringBuilder = new StringBuilder();
/* 134 */     stringBuilder.append("GRANT ");
/* 135 */     if (this.grantedRole != null) {
/* 136 */       this.grantedRole.getSQL(stringBuilder, 0);
/*     */     } else {
/* 138 */       stringBuilder.append(getRights());
/* 139 */       if (paramDbObject != null) {
/* 140 */         if (paramDbObject instanceof org.h2.schema.Schema) {
/* 141 */           stringBuilder.append(" ON SCHEMA ");
/* 142 */           paramDbObject.getSQL(stringBuilder, 0);
/* 143 */         } else if (paramDbObject instanceof Table) {
/* 144 */           stringBuilder.append(" ON ");
/* 145 */           paramDbObject.getSQL(stringBuilder, 0);
/*     */         } 
/*     */       }
/*     */     } 
/* 149 */     stringBuilder.append(" TO ");
/* 150 */     this.grantee.getSQL(stringBuilder, 0);
/* 151 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/* 156 */     return getCreateSQLForCopy(this.grantedObject);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 161 */     return 8;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 166 */     if (this.grantedRole != null) {
/* 167 */       this.grantee.revokeRole(this.grantedRole);
/*     */     } else {
/* 169 */       this.grantee.revokeRight(this.grantedObject);
/*     */     } 
/* 171 */     this.database.removeMeta(paramSessionLocal, getId());
/* 172 */     this.grantedRole = null;
/* 173 */     this.grantedObject = null;
/* 174 */     this.grantee = null;
/* 175 */     invalidate();
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkRename() {
/* 180 */     throw DbException.getInternalError();
/*     */   }
/*     */   
/*     */   public void setRightMask(int paramInt) {
/* 184 */     this.grantedRight = paramInt;
/*     */   }
/*     */   
/*     */   public int getRightMask() {
/* 188 */     return this.grantedRight;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\Right.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */