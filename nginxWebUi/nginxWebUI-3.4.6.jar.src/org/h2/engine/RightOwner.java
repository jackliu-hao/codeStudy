/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class RightOwner
/*     */   extends DbObject
/*     */ {
/*     */   private HashMap<Role, Right> grantedRoles;
/*     */   private HashMap<DbObject, Right> grantedRights;
/*     */   
/*     */   protected RightOwner(Database paramDatabase, int paramInt1, String paramString, int paramInt2) {
/*  35 */     super(paramDatabase, paramInt1, StringUtils.toUpperEnglish(paramString), paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public void rename(String paramString) {
/*  40 */     super.rename(StringUtils.toUpperEnglish(paramString));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRoleGranted(Role paramRole) {
/*  50 */     if (paramRole == this) {
/*  51 */       return true;
/*     */     }
/*  53 */     if (this.grantedRoles != null) {
/*  54 */       for (Role role : this.grantedRoles.keySet()) {
/*  55 */         if (role == paramRole) {
/*  56 */           return true;
/*     */         }
/*  58 */         if (role.isRoleGranted(paramRole)) {
/*  59 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/*  63 */     return false;
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
/*     */ 
/*     */ 
/*     */   
/*     */   final boolean isTableRightGrantedRecursive(Table paramTable, int paramInt) {
/*  79 */     Schema schema = paramTable.getSchema();
/*  80 */     if (schema.getOwner() == this) {
/*  81 */       return true;
/*     */     }
/*  83 */     if (this.grantedRights != null) {
/*  84 */       Right right = this.grantedRights.get(null);
/*  85 */       if (right != null && (right.getRightMask() & 0x10) == 16) {
/*  86 */         return true;
/*     */       }
/*  88 */       right = this.grantedRights.get(schema);
/*  89 */       if (right != null && (right.getRightMask() & paramInt) == paramInt) {
/*  90 */         return true;
/*     */       }
/*  92 */       right = this.grantedRights.get(paramTable);
/*  93 */       if (right != null && (right.getRightMask() & paramInt) == paramInt) {
/*  94 */         return true;
/*     */       }
/*     */     } 
/*  97 */     if (this.grantedRoles != null) {
/*  98 */       for (Role role : this.grantedRoles.keySet()) {
/*  99 */         if (role.isTableRightGrantedRecursive(paramTable, paramInt)) {
/* 100 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 104 */     return false;
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
/*     */   
/*     */   final boolean isSchemaRightGrantedRecursive(Schema paramSchema) {
/* 118 */     if (paramSchema != null && paramSchema.getOwner() == this) {
/* 119 */       return true;
/*     */     }
/* 121 */     if (this.grantedRights != null) {
/* 122 */       Right right = this.grantedRights.get(null);
/* 123 */       if (right != null && (right.getRightMask() & 0x10) == 16) {
/* 124 */         return true;
/*     */       }
/*     */     } 
/* 127 */     if (this.grantedRoles != null) {
/* 128 */       for (Role role : this.grantedRoles.keySet()) {
/* 129 */         if (role.isSchemaRightGrantedRecursive(paramSchema)) {
/* 130 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 134 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void grantRight(DbObject paramDbObject, Right paramRight) {
/* 145 */     if (this.grantedRights == null) {
/* 146 */       this.grantedRights = new HashMap<>();
/*     */     }
/* 148 */     this.grantedRights.put(paramDbObject, paramRight);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void revokeRight(DbObject paramDbObject) {
/* 157 */     if (this.grantedRights == null) {
/*     */       return;
/*     */     }
/* 160 */     this.grantedRights.remove(paramDbObject);
/* 161 */     if (this.grantedRights.size() == 0) {
/* 162 */       this.grantedRights = null;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void grantRole(Role paramRole, Right paramRight) {
/* 173 */     if (this.grantedRoles == null) {
/* 174 */       this.grantedRoles = new HashMap<>();
/*     */     }
/* 176 */     this.grantedRoles.put(paramRole, paramRight);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void revokeRole(Role paramRole) {
/* 185 */     if (this.grantedRoles == null) {
/*     */       return;
/*     */     }
/* 188 */     Right right = this.grantedRoles.get(paramRole);
/* 189 */     if (right == null) {
/*     */       return;
/*     */     }
/* 192 */     this.grantedRoles.remove(paramRole);
/* 193 */     if (this.grantedRoles.size() == 0) {
/* 194 */       this.grantedRoles = null;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void revokeTemporaryRightsOnRoles() {
/* 202 */     if (this.grantedRoles == null) {
/*     */       return;
/*     */     }
/* 205 */     ArrayList arrayList = new ArrayList();
/* 206 */     for (Map.Entry<Role, Right> entry : this.grantedRoles.entrySet()) {
/* 207 */       if (((Right)entry.getValue()).isTemporary() || !((Right)entry.getValue()).isValid()) {
/* 208 */         arrayList.add(entry.getKey());
/*     */       }
/*     */     } 
/* 211 */     for (Role role : arrayList) {
/* 212 */       revokeRole(role);
/*     */     }
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
/*     */   public Right getRightForObject(DbObject paramDbObject) {
/* 225 */     if (this.grantedRights == null) {
/* 226 */       return null;
/*     */     }
/* 228 */     return this.grantedRights.get(paramDbObject);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Right getRightForRole(Role paramRole) {
/* 238 */     if (this.grantedRoles == null) {
/* 239 */       return null;
/*     */     }
/* 241 */     return this.grantedRoles.get(paramRole);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void checkOwnsNoSchemas() {
/* 252 */     for (Schema schema : this.database.getAllSchemas()) {
/* 253 */       if (this == schema.getOwner())
/* 254 */         throw DbException.get(90107, new String[] { getName(), schema.getName() }); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\RightOwner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */