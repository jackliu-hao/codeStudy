/*    */ package org.h2.expression.condition;
/*    */ 
/*    */ import org.h2.command.query.Query;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionVisitor;
/*    */ import org.h2.table.ColumnResolver;
/*    */ import org.h2.table.TableFilter;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueBoolean;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExistsPredicate
/*    */   extends PredicateWithSubquery
/*    */ {
/*    */   public ExistsPredicate(Query paramQuery) {
/* 19 */     super(paramQuery);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 24 */     this.query.setSession(paramSessionLocal);
/* 25 */     return (Value)ValueBoolean.get(this.query.exists());
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 30 */     return super.getUnenclosedSQL(paramStringBuilder.append("EXISTS"), paramInt);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\ExistsPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */