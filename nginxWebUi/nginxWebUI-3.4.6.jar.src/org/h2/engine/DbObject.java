/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.command.Parser;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.Trace;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.HasSQL;
/*     */ import org.h2.util.ParserUtil;
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
/*     */ public abstract class DbObject
/*     */   implements HasSQL
/*     */ {
/*     */   public static final int TABLE_OR_VIEW = 0;
/*     */   public static final int INDEX = 1;
/*     */   public static final int USER = 2;
/*     */   public static final int SEQUENCE = 3;
/*     */   public static final int TRIGGER = 4;
/*     */   public static final int CONSTRAINT = 5;
/*     */   public static final int SETTING = 6;
/*     */   public static final int ROLE = 7;
/*     */   public static final int RIGHT = 8;
/*     */   public static final int FUNCTION_ALIAS = 9;
/*     */   public static final int SCHEMA = 10;
/*     */   public static final int CONSTANT = 11;
/*     */   public static final int DOMAIN = 12;
/*     */   public static final int COMMENT = 13;
/*     */   public static final int AGGREGATE = 14;
/*     */   public static final int SYNONYM = 15;
/*     */   protected Database database;
/*     */   protected Trace trace;
/*     */   protected String comment;
/*     */   private int id;
/*     */   private String objectName;
/*     */   private long modificationId;
/*     */   private boolean temporary;
/*     */   
/*     */   protected DbObject(Database paramDatabase, int paramInt1, String paramString, int paramInt2) {
/* 135 */     this.database = paramDatabase;
/* 136 */     this.trace = paramDatabase.getTrace(paramInt2);
/* 137 */     this.id = paramInt1;
/* 138 */     this.objectName = paramString;
/* 139 */     this.modificationId = paramDatabase.getModificationMetaId();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setModified() {
/* 146 */     this.modificationId = (this.database == null) ? -1L : this.database.getNextModificationMetaId();
/*     */   }
/*     */   
/*     */   public final long getModificationId() {
/* 150 */     return this.modificationId;
/*     */   }
/*     */   
/*     */   protected final void setObjectName(String paramString) {
/* 154 */     this.objectName = paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSQL(int paramInt) {
/* 159 */     return Parser.quoteIdentifier(this.objectName, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 164 */     return ParserUtil.quoteIdentifier(paramStringBuilder, this.objectName, paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<DbObject> getChildren() {
/* 174 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Database getDatabase() {
/* 183 */     return this.database;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getId() {
/* 192 */     return this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getName() {
/* 201 */     return this.objectName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void invalidate() {
/* 209 */     if (this.id == -1) {
/* 210 */       throw DbException.getInternalError();
/*     */     }
/* 212 */     setModified();
/* 213 */     this.id = -1;
/* 214 */     this.database = null;
/* 215 */     this.trace = null;
/* 216 */     this.objectName = null;
/*     */   }
/*     */   
/*     */   public final boolean isValid() {
/* 220 */     return (this.id != -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getCreateSQLForCopy(Table paramTable, String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCreateSQLForMeta() {
/* 239 */     return getCreateSQL();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getCreateSQL();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDropSQL() {
/* 255 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void removeChildrenAndResources(SessionLocal paramSessionLocal);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkRename() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rename(String paramString) {
/* 285 */     checkRename();
/* 286 */     this.objectName = paramString;
/* 287 */     setModified();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTemporary() {
/* 296 */     return this.temporary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTemporary(boolean paramBoolean) {
/* 305 */     this.temporary = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setComment(String paramString) {
/* 314 */     this.comment = (paramString != null && !paramString.isEmpty()) ? paramString : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getComment() {
/* 323 */     return this.comment;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 328 */     return this.objectName + ":" + this.id + ":" + super.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\DbObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */