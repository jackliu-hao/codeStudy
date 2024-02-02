/*    */ package org.h2.command.dml;
/*    */ 
/*    */ import org.h2.command.Prepared;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.result.ResultInterface;
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
/*    */ public class ExecuteImmediate
/*    */   extends Prepared
/*    */ {
/*    */   private Expression statement;
/*    */   
/*    */   public ExecuteImmediate(SessionLocal paramSessionLocal, Expression paramExpression) {
/* 25 */     super(paramSessionLocal);
/* 26 */     this.statement = paramExpression.optimize(paramSessionLocal);
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 31 */     String str = this.statement.getValue(this.session).getString();
/* 32 */     if (str == null) {
/* 33 */       throw DbException.getInvalidValueException("SQL command", null);
/*    */     }
/* 35 */     Prepared prepared = this.session.prepare(str);
/* 36 */     if (prepared.isQuery()) {
/* 37 */       throw DbException.get(42001, new String[] { str, "<not a query>" });
/*    */     }
/* 39 */     return prepared.update();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isTransactional() {
/* 44 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 49 */     return 91;
/*    */   }
/*    */ 
/*    */   
/*    */   public ResultInterface queryMeta() {
/* 54 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\ExecuteImmediate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */