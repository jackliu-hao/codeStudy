/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.Right;
/*     */ import org.h2.engine.RightOwner;
/*     */ import org.h2.engine.Role;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.User;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.Utils;
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
/*     */ public class GrantRevoke
/*     */   extends DefineCommand
/*     */ {
/*     */   private ArrayList<String> roleNames;
/*     */   private int operationType;
/*     */   private int rightMask;
/*  36 */   private final ArrayList<Table> tables = Utils.newSmallArrayList();
/*     */   private Schema schema;
/*     */   private RightOwner grantee;
/*     */   
/*     */   public GrantRevoke(SessionLocal paramSessionLocal) {
/*  41 */     super(paramSessionLocal);
/*     */   }
/*     */   
/*     */   public void setOperationType(int paramInt) {
/*  45 */     this.operationType = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRight(int paramInt) {
/*  54 */     this.rightMask |= paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRoleName(String paramString) {
/*  63 */     if (this.roleNames == null) {
/*  64 */       this.roleNames = Utils.newSmallArrayList();
/*     */     }
/*  66 */     this.roleNames.add(paramString);
/*     */   }
/*     */   
/*     */   public void setGranteeName(String paramString) {
/*  70 */     Database database = this.session.getDatabase();
/*  71 */     this.grantee = database.findUserOrRole(paramString);
/*  72 */     if (this.grantee == null) {
/*  73 */       throw DbException.get(90071, paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/*  79 */     Database database = this.session.getDatabase();
/*  80 */     User user = this.session.getUser();
/*  81 */     if (this.roleNames != null) {
/*  82 */       user.checkAdmin();
/*  83 */       for (String str : this.roleNames) {
/*  84 */         Role role = database.findRole(str);
/*  85 */         if (role == null) {
/*  86 */           throw DbException.get(90070, str);
/*     */         }
/*  88 */         if (this.operationType == 49) {
/*  89 */           grantRole(role); continue;
/*  90 */         }  if (this.operationType == 50) {
/*  91 */           revokeRole(role); continue;
/*     */         } 
/*  93 */         throw DbException.getInternalError("type=" + this.operationType);
/*     */       } 
/*     */     } else {
/*     */       
/*  97 */       if ((this.rightMask & 0x10) != 0) {
/*  98 */         user.checkAdmin();
/*     */       } else {
/* 100 */         if (this.schema != null) {
/* 101 */           user.checkSchemaOwner(this.schema);
/*     */         }
/* 103 */         for (Table table : this.tables) {
/* 104 */           user.checkSchemaOwner(table.getSchema());
/*     */         }
/*     */       } 
/* 107 */       if (this.operationType == 49) {
/* 108 */         grantRight();
/* 109 */       } else if (this.operationType == 50) {
/* 110 */         revokeRight();
/*     */       } else {
/* 112 */         throw DbException.getInternalError("type=" + this.operationType);
/*     */       } 
/*     */     } 
/* 115 */     return 0L;
/*     */   }
/*     */   
/*     */   private void grantRight() {
/* 119 */     if (this.schema != null) {
/* 120 */       grantRight((DbObject)this.schema);
/*     */     }
/* 122 */     for (Table table : this.tables) {
/* 123 */       grantRight((DbObject)table);
/*     */     }
/*     */   }
/*     */   
/*     */   private void grantRight(DbObject paramDbObject) {
/* 128 */     Database database = this.session.getDatabase();
/* 129 */     Right right = this.grantee.getRightForObject(paramDbObject);
/* 130 */     if (right == null) {
/* 131 */       int i = getPersistedObjectId();
/* 132 */       if (i == 0) {
/* 133 */         i = this.session.getDatabase().allocateObjectId();
/*     */       }
/* 135 */       right = new Right(database, i, this.grantee, this.rightMask, paramDbObject);
/* 136 */       this.grantee.grantRight(paramDbObject, right);
/* 137 */       database.addDatabaseObject(this.session, (DbObject)right);
/*     */     } else {
/* 139 */       right.setRightMask(right.getRightMask() | this.rightMask);
/* 140 */       database.updateMeta(this.session, (DbObject)right);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void grantRole(Role paramRole) {
/* 145 */     if (paramRole != this.grantee && this.grantee.isRoleGranted(paramRole)) {
/*     */       return;
/*     */     }
/* 148 */     if (this.grantee instanceof Role) {
/* 149 */       Role role = (Role)this.grantee;
/* 150 */       if (paramRole.isRoleGranted(role))
/*     */       {
/* 152 */         throw DbException.get(90074, paramRole.getTraceSQL());
/*     */       }
/*     */     } 
/* 155 */     Database database = this.session.getDatabase();
/* 156 */     int i = getObjectId();
/* 157 */     Right right = new Right(database, i, this.grantee, paramRole);
/* 158 */     database.addDatabaseObject(this.session, (DbObject)right);
/* 159 */     this.grantee.grantRole(paramRole, right);
/*     */   }
/*     */   
/*     */   private void revokeRight() {
/* 163 */     if (this.schema != null) {
/* 164 */       revokeRight((DbObject)this.schema);
/*     */     }
/* 166 */     for (Table table : this.tables) {
/* 167 */       revokeRight((DbObject)table);
/*     */     }
/*     */   }
/*     */   
/*     */   private void revokeRight(DbObject paramDbObject) {
/* 172 */     Right right = this.grantee.getRightForObject(paramDbObject);
/* 173 */     if (right == null) {
/*     */       return;
/*     */     }
/* 176 */     int i = right.getRightMask();
/* 177 */     int j = i & (this.rightMask ^ 0xFFFFFFFF);
/* 178 */     Database database = this.session.getDatabase();
/* 179 */     if (j == 0) {
/* 180 */       database.removeDatabaseObject(this.session, (DbObject)right);
/*     */     } else {
/* 182 */       right.setRightMask(j);
/* 183 */       database.updateMeta(this.session, (DbObject)right);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void revokeRole(Role paramRole) {
/* 189 */     Right right = this.grantee.getRightForRole(paramRole);
/* 190 */     if (right == null) {
/*     */       return;
/*     */     }
/* 193 */     Database database = this.session.getDatabase();
/* 194 */     database.removeDatabaseObject(this.session, (DbObject)right);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTransactional() {
/* 199 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTable(Table paramTable) {
/* 208 */     this.tables.add(paramTable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSchema(Schema paramSchema) {
/* 217 */     this.schema = paramSchema;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 222 */     return this.operationType;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\GrantRevoke.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */