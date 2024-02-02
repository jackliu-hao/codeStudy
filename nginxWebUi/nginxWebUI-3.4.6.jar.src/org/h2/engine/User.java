/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.security.SHA256;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableType;
/*     */ import org.h2.table.TableView;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.StringUtils;
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
/*     */ public final class User
/*     */   extends RightOwner
/*     */ {
/*     */   private final boolean systemUser;
/*     */   private byte[] salt;
/*     */   private byte[] passwordHash;
/*     */   private boolean admin;
/*     */   
/*     */   public User(Database paramDatabase, int paramInt, String paramString, boolean paramBoolean) {
/*  37 */     super(paramDatabase, paramInt, paramString, 13);
/*  38 */     this.systemUser = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setAdmin(boolean paramBoolean) {
/*  42 */     this.admin = paramBoolean;
/*     */   }
/*     */   
/*     */   public boolean isAdmin() {
/*  46 */     return this.admin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSaltAndHash(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/*  56 */     this.salt = paramArrayOfbyte1;
/*  57 */     this.passwordHash = paramArrayOfbyte2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUserPasswordHash(byte[] paramArrayOfbyte) {
/*  67 */     if (paramArrayOfbyte != null) {
/*  68 */       if (paramArrayOfbyte.length == 0) {
/*  69 */         this.salt = this.passwordHash = paramArrayOfbyte;
/*     */       } else {
/*  71 */         this.salt = new byte[8];
/*  72 */         MathUtils.randomBytes(this.salt);
/*  73 */         this.passwordHash = SHA256.getHashWithSalt(paramArrayOfbyte, this.salt);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/*  80 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  85 */     return getCreateSQL(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCreateSQL(boolean paramBoolean) {
/*  96 */     StringBuilder stringBuilder = new StringBuilder("CREATE USER IF NOT EXISTS ");
/*  97 */     getSQL(stringBuilder, 0);
/*  98 */     if (this.comment != null) {
/*  99 */       stringBuilder.append(" COMMENT ");
/* 100 */       StringUtils.quoteStringSQL(stringBuilder, this.comment);
/*     */     } 
/* 102 */     if (paramBoolean) {
/* 103 */       stringBuilder.append(" SALT '");
/* 104 */       StringUtils.convertBytesToHex(stringBuilder, this.salt)
/* 105 */         .append("' HASH '");
/* 106 */       StringUtils.convertBytesToHex(stringBuilder, this.passwordHash)
/* 107 */         .append('\'');
/*     */     } else {
/* 109 */       stringBuilder.append(" PASSWORD ''");
/*     */     } 
/* 111 */     if (this.admin) {
/* 112 */       stringBuilder.append(" ADMIN");
/*     */     }
/* 114 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean validateUserPasswordHash(byte[] paramArrayOfbyte) {
/* 124 */     if (paramArrayOfbyte.length == 0 && this.passwordHash.length == 0) {
/* 125 */       return true;
/*     */     }
/* 127 */     if (paramArrayOfbyte.length == 0) {
/* 128 */       paramArrayOfbyte = SHA256.getKeyPasswordHash(getName(), new char[0]);
/*     */     }
/* 130 */     byte[] arrayOfByte = SHA256.getHashWithSalt(paramArrayOfbyte, this.salt);
/* 131 */     return Utils.compareSecure(arrayOfByte, this.passwordHash);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkAdmin() {
/* 141 */     if (!this.admin) {
/* 142 */       throw DbException.get(90040);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkSchemaAdmin() {
/* 153 */     if (!hasSchemaRight((Schema)null)) {
/* 154 */       throw DbException.get(90040);
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
/*     */   public void checkSchemaOwner(Schema paramSchema) {
/* 166 */     if (!hasSchemaRight(paramSchema)) {
/* 167 */       throw DbException.get(90096, paramSchema.getTraceSQL());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasSchemaRight(Schema paramSchema) {
/* 178 */     if (this.admin) {
/* 179 */       return true;
/*     */     }
/* 181 */     Role role = this.database.getPublicRole();
/* 182 */     if (role.isSchemaRightGrantedRecursive(paramSchema)) {
/* 183 */       return true;
/*     */     }
/* 185 */     return isSchemaRightGrantedRecursive(paramSchema);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkTableRight(Table paramTable, int paramInt) {
/* 196 */     if (!hasTableRight(paramTable, paramInt)) {
/* 197 */       throw DbException.get(90096, paramTable.getTraceSQL());
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
/*     */   public boolean hasTableRight(Table paramTable, int paramInt) {
/* 209 */     if (paramInt != 1 && !this.systemUser) {
/* 210 */       paramTable.checkWritingAllowed();
/*     */     }
/* 212 */     if (this.admin) {
/* 213 */       return true;
/*     */     }
/* 215 */     Role role = this.database.getPublicRole();
/* 216 */     if (role.isTableRightGrantedRecursive(paramTable, paramInt)) {
/* 217 */       return true;
/*     */     }
/* 219 */     if (paramTable instanceof org.h2.table.MetaTable || paramTable instanceof org.h2.table.DualTable || paramTable instanceof org.h2.table.RangeTable)
/*     */     {
/* 221 */       return true;
/*     */     }
/* 223 */     TableType tableType = paramTable.getTableType();
/* 224 */     if (TableType.VIEW == tableType) {
/* 225 */       TableView tableView = (TableView)paramTable;
/* 226 */       if (tableView.getOwner() == this)
/*     */       {
/*     */         
/* 229 */         return true;
/*     */       }
/* 231 */     } else if (tableType == null) {
/*     */       
/* 233 */       return true;
/*     */     } 
/* 235 */     if (paramTable.isTemporary() && !paramTable.isGlobalTemporary())
/*     */     {
/* 237 */       return true;
/*     */     }
/* 239 */     return isTableRightGrantedRecursive(paramTable, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 244 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<DbObject> getChildren() {
/* 249 */     ArrayList<Right> arrayList = new ArrayList();
/* 250 */     for (Right right : this.database.getAllRights()) {
/* 251 */       if (right.getGrantee() == this) {
/* 252 */         arrayList.add(right);
/*     */       }
/*     */     } 
/* 255 */     for (Schema schema : this.database.getAllSchemas()) {
/* 256 */       if (schema.getOwner() == this) {
/* 257 */         arrayList.add(schema);
/*     */       }
/*     */     } 
/* 260 */     return (ArrayList)arrayList;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 265 */     for (Right right : this.database.getAllRights()) {
/* 266 */       if (right.getGrantee() == this) {
/* 267 */         this.database.removeDatabaseObject(paramSessionLocal, right);
/*     */       }
/*     */     } 
/* 270 */     this.database.removeMeta(paramSessionLocal, getId());
/* 271 */     this.salt = null;
/* 272 */     Arrays.fill(this.passwordHash, (byte)0);
/* 273 */     this.passwordHash = null;
/* 274 */     invalidate();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\User.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */