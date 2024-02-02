/*    */ package org.h2.engine;
/*    */ 
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.table.Table;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Setting
/*    */   extends DbObject
/*    */ {
/*    */   private int intValue;
/*    */   private String stringValue;
/*    */   
/*    */   public Setting(Database paramDatabase, int paramInt, String paramString) {
/* 21 */     super(paramDatabase, paramInt, paramString, 10);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSQL(int paramInt) {
/* 26 */     return getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 31 */     return paramStringBuilder.append(getName());
/*    */   }
/*    */   
/*    */   public void setIntValue(int paramInt) {
/* 35 */     this.intValue = paramInt;
/*    */   }
/*    */   
/*    */   public int getIntValue() {
/* 39 */     return this.intValue;
/*    */   }
/*    */   
/*    */   public void setStringValue(String paramString) {
/* 43 */     this.stringValue = paramString;
/*    */   }
/*    */   
/*    */   public String getStringValue() {
/* 47 */     return this.stringValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/* 52 */     throw DbException.getInternalError(toString());
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCreateSQL() {
/* 57 */     StringBuilder stringBuilder = new StringBuilder("SET ");
/* 58 */     getSQL(stringBuilder, 0).append(' ');
/* 59 */     if (this.stringValue != null) {
/* 60 */       stringBuilder.append(this.stringValue);
/*    */     } else {
/* 62 */       stringBuilder.append(this.intValue);
/*    */     } 
/* 64 */     return stringBuilder.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 69 */     return 6;
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 74 */     this.database.removeMeta(paramSessionLocal, getId());
/* 75 */     invalidate();
/*    */   }
/*    */ 
/*    */   
/*    */   public void checkRename() {
/* 80 */     throw DbException.getUnsupportedException("RENAME");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\Setting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */