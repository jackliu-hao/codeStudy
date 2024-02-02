/*    */ package org.h2.command.dml;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.command.Prepared;
/*    */ import org.h2.engine.Procedure;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.Parameter;
/*    */ import org.h2.result.ResultInterface;
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
/*    */ public class ExecuteProcedure
/*    */   extends Prepared
/*    */ {
/* 25 */   private final ArrayList<Expression> expressions = Utils.newSmallArrayList();
/*    */   private Procedure procedure;
/*    */   
/*    */   public ExecuteProcedure(SessionLocal paramSessionLocal) {
/* 29 */     super(paramSessionLocal);
/*    */   }
/*    */   
/*    */   public void setProcedure(Procedure paramProcedure) {
/* 33 */     this.procedure = paramProcedure;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setExpression(int paramInt, Expression paramExpression) {
/* 43 */     this.expressions.add(paramInt, paramExpression);
/*    */   }
/*    */   
/*    */   private void setParameters() {
/* 47 */     Prepared prepared = this.procedure.getPrepared();
/* 48 */     ArrayList<Parameter> arrayList = prepared.getParameters();
/* 49 */     for (byte b = 0; arrayList != null && b < arrayList.size() && b < this.expressions
/* 50 */       .size(); b++) {
/* 51 */       Expression expression = this.expressions.get(b);
/* 52 */       Parameter parameter = arrayList.get(b);
/* 53 */       parameter.setValue(expression.getValue(this.session));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isQuery() {
/* 59 */     Prepared prepared = this.procedure.getPrepared();
/* 60 */     return prepared.isQuery();
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 65 */     setParameters();
/* 66 */     Prepared prepared = this.procedure.getPrepared();
/* 67 */     return prepared.update();
/*    */   }
/*    */ 
/*    */   
/*    */   public ResultInterface query(long paramLong) {
/* 72 */     setParameters();
/* 73 */     Prepared prepared = this.procedure.getPrepared();
/* 74 */     return prepared.query(paramLong);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isTransactional() {
/* 79 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public ResultInterface queryMeta() {
/* 84 */     Prepared prepared = this.procedure.getPrepared();
/* 85 */     return prepared.queryMeta();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 90 */     return 59;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\ExecuteProcedure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */