/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.command.Parser;
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionColumn;
/*    */ import org.h2.expression.ExpressionVisitor;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.Sequence;
/*    */ import org.h2.util.StringUtils;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class CompatibilitySequenceValueFunction
/*    */   extends Function1_2
/*    */ {
/*    */   private final boolean current;
/*    */   
/*    */   public CompatibilitySequenceValueFunction(Expression paramExpression1, Expression paramExpression2, boolean paramBoolean) {
/* 29 */     super(paramExpression1, paramExpression2);
/* 30 */     this.current = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/*    */     String str1, str2;
/* 36 */     if (paramValue2 == null) {
/* 37 */       Parser parser = new Parser(paramSessionLocal);
/* 38 */       String str = paramValue1.getString();
/* 39 */       Expression expression = parser.parseExpression(str);
/* 40 */       if (expression instanceof ExpressionColumn) {
/* 41 */         ExpressionColumn expressionColumn = (ExpressionColumn)expression;
/* 42 */         str1 = expressionColumn.getOriginalTableAliasName();
/* 43 */         if (str1 == null) {
/* 44 */           str1 = paramSessionLocal.getCurrentSchemaName();
/* 45 */           str2 = str;
/*    */         } else {
/* 47 */           str2 = expressionColumn.getColumnName(paramSessionLocal, -1);
/*    */         } 
/*    */       } else {
/* 50 */         throw DbException.getSyntaxError(str, 1);
/*    */       } 
/*    */     } else {
/* 53 */       str1 = paramValue1.getString();
/* 54 */       str2 = paramValue2.getString();
/*    */     } 
/* 56 */     Database database = paramSessionLocal.getDatabase();
/* 57 */     Schema schema = database.findSchema(str1);
/* 58 */     if (schema == null) {
/* 59 */       str1 = StringUtils.toUpperEnglish(str1);
/* 60 */       schema = database.getSchema(str1);
/*    */     } 
/* 62 */     Sequence sequence = schema.findSequence(str2);
/* 63 */     if (sequence == null) {
/* 64 */       str2 = StringUtils.toUpperEnglish(str2);
/* 65 */       sequence = schema.getSequence(str2);
/*    */     } 
/* 67 */     return (this.current ? paramSessionLocal.getCurrentValueFor(sequence) : paramSessionLocal.getNextValueFor(sequence, null)).convertTo(this.type);
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 72 */     this.left = this.left.optimize(paramSessionLocal);
/* 73 */     if (this.right != null) {
/* 74 */       this.right = this.right.optimize(paramSessionLocal);
/*    */     }
/* 76 */     this.type = (paramSessionLocal.getMode()).decimalSequences ? TypeInfo.TYPE_NUMERIC_BIGINT : TypeInfo.TYPE_BIGINT;
/* 77 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 82 */     switch (paramExpressionVisitor.getType()) {
/*    */       case 0:
/*    */       case 2:
/*    */       case 8:
/* 86 */         return false;
/*    */       case 5:
/* 88 */         if (!this.current)
/* 89 */           return false; 
/*    */         break;
/*    */     } 
/* 92 */     return super.isEverything(paramExpressionVisitor);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 97 */     return this.current ? "CURRVAL" : "NEXTVAL";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\CompatibilitySequenceValueFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */