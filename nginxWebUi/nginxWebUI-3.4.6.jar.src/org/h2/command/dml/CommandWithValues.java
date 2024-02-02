/*    */ package org.h2.command.dml;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.util.Utils;
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
/*    */ public abstract class CommandWithValues
/*    */   extends DataChangeStatement
/*    */ {
/* 22 */   protected final ArrayList<Expression[]> valuesExpressionList = Utils.newSmallArrayList();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected CommandWithValues(SessionLocal paramSessionLocal) {
/* 31 */     super(paramSessionLocal);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void addRow(Expression[] paramArrayOfExpression) {
/* 41 */     this.valuesExpressionList.add(paramArrayOfExpression);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\CommandWithValues.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */