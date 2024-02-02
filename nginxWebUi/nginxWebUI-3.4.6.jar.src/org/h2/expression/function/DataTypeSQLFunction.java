/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Constant;
/*     */ import org.h2.schema.Domain;
/*     */ import org.h2.schema.FunctionAlias;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueToObjectConverter2;
/*     */ import org.h2.value.ValueVarchar;
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
/*     */ public final class DataTypeSQLFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public DataTypeSQLFunction(Expression paramExpression1, Expression paramExpression2, Expression paramExpression3, Expression paramExpression4) {
/*  32 */     super(new Expression[] { paramExpression1, paramExpression2, paramExpression3, paramExpression4 }); } public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, Value paramValue3) { TypeInfo typeInfo; Constant constant; Domain domain; int i; Table table;
/*     */     FunctionAlias functionAlias;
/*     */     int j, k;
/*     */     Column[] arrayOfColumn;
/*     */     FunctionAlias.JavaMethod arrayOfJavaMethod[], javaMethod;
/*  37 */     Schema schema = paramSessionLocal.getDatabase().findSchema(paramValue1.getString());
/*  38 */     if (schema == null) {
/*  39 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  41 */     String str1 = paramValue2.getString();
/*  42 */     String str2 = paramValue3.getString();
/*  43 */     String str3 = this.args[3].getValue(paramSessionLocal).getString();
/*  44 */     if (str3 == null) {
/*  45 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*     */     
/*  48 */     switch (str2) {
/*     */       case "CONSTANT":
/*  50 */         constant = schema.findConstant(str1);
/*  51 */         if (constant == null || !str3.equals("TYPE")) {
/*  52 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/*  54 */         typeInfo = constant.getValue().getType();
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
/* 133 */         return ValueVarchar.get(typeInfo.getSQL(0));case "DOMAIN": domain = schema.findDomain(str1); if (domain == null || !str3.equals("TYPE")) return (Value)ValueNull.INSTANCE;  typeInfo = domain.getDataType(); return ValueVarchar.get(typeInfo.getSQL(0));case "ROUTINE": i = str1.lastIndexOf('_'); if (i < 0) return (Value)ValueNull.INSTANCE;  functionAlias = schema.findFunction(str1.substring(0, i)); if (functionAlias == null) return (Value)ValueNull.INSTANCE;  try { k = Integer.parseInt(str1.substring(i + 1)); } catch (NumberFormatException numberFormatException) { return (Value)ValueNull.INSTANCE; }  try { arrayOfJavaMethod = functionAlias.getJavaMethods(); } catch (DbException dbException) { return (Value)ValueNull.INSTANCE; }  if (k < 1 || k > arrayOfJavaMethod.length) return (Value)ValueNull.INSTANCE;  javaMethod = arrayOfJavaMethod[k - 1]; if (str3.equals("RESULT")) { typeInfo = javaMethod.getDataType(); } else { try { k = Integer.parseInt(str3); } catch (NumberFormatException numberFormatException) { return (Value)ValueNull.INSTANCE; }  if (k < 1) return (Value)ValueNull.INSTANCE;  if (!javaMethod.hasConnectionParam()) k--;  Class[] arrayOfClass = javaMethod.getColumnClasses(); if (k >= arrayOfClass.length) return (Value)ValueNull.INSTANCE;  typeInfo = ValueToObjectConverter2.classToType(arrayOfClass[k]); }  return ValueVarchar.get(typeInfo.getSQL(0));case "TABLE": table = schema.findTableOrView(paramSessionLocal, str1); if (table == null) return (Value)ValueNull.INSTANCE;  try { j = Integer.parseInt(str3); } catch (NumberFormatException numberFormatException) { return (Value)ValueNull.INSTANCE; }  arrayOfColumn = table.getColumns(); if (j < 1 || j > arrayOfColumn.length) return (Value)ValueNull.INSTANCE;  typeInfo = arrayOfColumn[j - 1].getType(); return ValueVarchar.get(typeInfo.getSQL(0));
/*     */     } 
/*     */     return (Value)ValueNull.INSTANCE; }
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 138 */     optimizeArguments(paramSessionLocal, false);
/* 139 */     this.type = TypeInfo.TYPE_VARCHAR;
/* 140 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 145 */     return "DATA_TYPE_SQL";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 150 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 2:
/* 152 */         return false;
/*     */     } 
/* 154 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\DataTypeSQLFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */