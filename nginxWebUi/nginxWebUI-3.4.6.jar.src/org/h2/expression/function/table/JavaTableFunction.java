/*    */ package org.h2.expression.function.table;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.result.ResultInterface;
/*    */ import org.h2.schema.FunctionAlias;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class JavaTableFunction
/*    */   extends TableFunction
/*    */ {
/*    */   private final FunctionAlias functionAlias;
/*    */   private final FunctionAlias.JavaMethod javaMethod;
/*    */   
/*    */   public JavaTableFunction(FunctionAlias paramFunctionAlias, Expression[] paramArrayOfExpression) {
/* 24 */     super(paramArrayOfExpression);
/* 25 */     this.functionAlias = paramFunctionAlias;
/* 26 */     this.javaMethod = paramFunctionAlias.findJavaMethod(paramArrayOfExpression);
/* 27 */     if (this.javaMethod.getDataType() != null) {
/* 28 */       throw DbException.get(90000, getName());
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public ResultInterface getValue(SessionLocal paramSessionLocal) {
/* 34 */     return this.javaMethod.getTableValue(paramSessionLocal, this.args, false);
/*    */   }
/*    */ 
/*    */   
/*    */   public ResultInterface getValueTemplate(SessionLocal paramSessionLocal) {
/* 39 */     return this.javaMethod.getTableValue(paramSessionLocal, this.args, true);
/*    */   }
/*    */ 
/*    */   
/*    */   public void optimize(SessionLocal paramSessionLocal) {
/* 44 */     super.optimize(paramSessionLocal);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 49 */     return Expression.writeExpressions(this.functionAlias.getSQL(paramStringBuilder, paramInt).append('('), this.args, paramInt)
/* 50 */       .append(')');
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 55 */     return this.functionAlias.getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isDeterministic() {
/* 60 */     return this.functionAlias.isDeterministic();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\table\JavaTableFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */