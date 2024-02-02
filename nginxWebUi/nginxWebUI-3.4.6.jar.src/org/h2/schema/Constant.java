/*    */ package org.h2.schema;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.ValueExpression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.table.Table;
/*    */ import org.h2.value.Value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Constant
/*    */   extends SchemaObject
/*    */ {
/*    */   private Value value;
/*    */   private ValueExpression expression;
/*    */   
/*    */   public Constant(Schema paramSchema, int paramInt, String paramString) {
/* 26 */     super(paramSchema, paramInt, paramString, 8);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/* 31 */     throw DbException.getInternalError(toString());
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCreateSQL() {
/* 36 */     StringBuilder stringBuilder = new StringBuilder("CREATE CONSTANT ");
/* 37 */     getSQL(stringBuilder, 0).append(" VALUE ");
/* 38 */     return this.value.getSQL(stringBuilder, 0).toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 43 */     return 11;
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 48 */     this.database.removeMeta(paramSessionLocal, getId());
/* 49 */     invalidate();
/*    */   }
/*    */   
/*    */   public void setValue(Value paramValue) {
/* 53 */     this.value = paramValue;
/* 54 */     this.expression = ValueExpression.get(paramValue);
/*    */   }
/*    */   
/*    */   public ValueExpression getValue() {
/* 58 */     return this.expression;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\schema\Constant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */