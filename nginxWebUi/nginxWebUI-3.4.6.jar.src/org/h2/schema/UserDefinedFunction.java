/*    */ package org.h2.schema;
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
/*    */ public abstract class UserDefinedFunction
/*    */   extends SchemaObject
/*    */ {
/*    */   String className;
/*    */   
/*    */   UserDefinedFunction(Schema paramSchema, int paramInt1, String paramString, int paramInt2) {
/* 19 */     super(paramSchema, paramInt1, paramString, paramInt2);
/*    */   }
/*    */ 
/*    */   
/*    */   public final String getCreateSQLForCopy(Table paramTable, String paramString) {
/* 24 */     throw DbException.getInternalError(toString());
/*    */   }
/*    */ 
/*    */   
/*    */   public final void checkRename() {
/* 29 */     throw DbException.getUnsupportedException("RENAME");
/*    */   }
/*    */   
/*    */   public final String getJavaClassName() {
/* 33 */     return this.className;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\schema\UserDefinedFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */