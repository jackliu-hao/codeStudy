/*    */ package org.h2.expression.condition;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import org.h2.command.query.Query;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionVisitor;
/*    */ import org.h2.expression.ValueExpression;
/*    */ import org.h2.result.LocalResult;
/*    */ import org.h2.result.ResultTarget;
/*    */ import org.h2.table.ColumnResolver;
/*    */ import org.h2.table.TableFilter;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueBoolean;
/*    */ import org.h2.value.ValueNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UniquePredicate
/*    */   extends PredicateWithSubquery
/*    */ {
/*    */   private static final class Target
/*    */     implements ResultTarget
/*    */   {
/*    */     private final int columnCount;
/*    */     private final LocalResult result;
/*    */     boolean hasDuplicates;
/*    */     
/*    */     Target(int param1Int, LocalResult param1LocalResult) {
/* 34 */       this.columnCount = param1Int;
/* 35 */       this.result = param1LocalResult;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public void limitsWereApplied() {}
/*    */ 
/*    */ 
/*    */     
/*    */     public long getRowCount() {
/* 46 */       return 0L;
/*    */     }
/*    */ 
/*    */     
/*    */     public void addRow(Value... param1VarArgs) {
/* 51 */       if (this.hasDuplicates) {
/*    */         return;
/*    */       }
/* 54 */       for (byte b = 0; b < this.columnCount; b++) {
/* 55 */         if (param1VarArgs[b] == ValueNull.INSTANCE) {
/*    */           return;
/*    */         }
/*    */       } 
/* 59 */       if (param1VarArgs.length != this.columnCount) {
/* 60 */         param1VarArgs = Arrays.<Value>copyOf(param1VarArgs, this.columnCount);
/*    */       }
/* 62 */       long l = this.result.getRowCount() + 1L;
/* 63 */       this.result.addRow(param1VarArgs);
/* 64 */       if (l != this.result.getRowCount()) {
/* 65 */         this.hasDuplicates = true;
/* 66 */         this.result.close();
/*    */       } 
/*    */     }
/*    */   }
/*    */   
/*    */   public UniquePredicate(Query paramQuery) {
/* 72 */     super(paramQuery);
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 77 */     super.optimize(paramSessionLocal);
/* 78 */     if (this.query.isStandardDistinct()) {
/* 79 */       return (Expression)ValueExpression.TRUE;
/*    */     }
/* 81 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 86 */     this.query.setSession(paramSessionLocal);
/* 87 */     int i = this.query.getColumnCount();
/*    */     
/* 89 */     LocalResult localResult = new LocalResult(paramSessionLocal, (Expression[])this.query.getExpressions().toArray((Object[])new Expression[0]), i, i);
/* 90 */     localResult.setDistinct();
/* 91 */     Target target = new Target(i, localResult);
/* 92 */     this.query.query(2147483647L, target);
/* 93 */     localResult.close();
/* 94 */     return (Value)ValueBoolean.get(!target.hasDuplicates);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 99 */     return super.getUnenclosedSQL(paramStringBuilder.append("UNIQUE"), paramInt);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\UniquePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */