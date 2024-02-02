/*    */ package org.h2.expression;
/*    */ 
/*    */ import org.h2.constraint.DomainColumnResolver;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.table.ColumnResolver;
/*    */ import org.h2.util.ParserUtil;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class DomainValueExpression
/*    */   extends Operation0
/*    */ {
/*    */   private DomainColumnResolver columnResolver;
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 29 */     return this.columnResolver.getValue(null);
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 34 */     return this.columnResolver.getValueType();
/*    */   }
/*    */ 
/*    */   
/*    */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 39 */     if (paramColumnResolver instanceof DomainColumnResolver) {
/* 40 */       this.columnResolver = (DomainColumnResolver)paramColumnResolver;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 46 */     if (this.columnResolver == null) {
/* 47 */       throw DbException.get(42122, "VALUE");
/*    */     }
/* 49 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isValueSet() {
/* 54 */     return (this.columnResolver.getValue(null) != null);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 59 */     if (this.columnResolver != null) {
/* 60 */       String str = this.columnResolver.getColumnName();
/* 61 */       if (str != null) {
/* 62 */         return ParserUtil.quoteIdentifier(paramStringBuilder, str, paramInt);
/*    */       }
/*    */     } 
/* 65 */     return paramStringBuilder.append("VALUE");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 70 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 75 */     return 1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\DomainValueExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */