/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Domain;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.table.Column;
/*    */ import org.h2.table.ColumnTemplate;
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
/*    */ public class AlterDomainExpressions
/*    */   extends AlterDomain
/*    */ {
/*    */   private final int type;
/*    */   private Expression expression;
/*    */   
/*    */   public AlterDomainExpressions(SessionLocal paramSessionLocal, Schema paramSchema, int paramInt) {
/* 31 */     super(paramSessionLocal, paramSchema);
/* 32 */     this.type = paramInt;
/*    */   }
/*    */   
/*    */   public void setExpression(Expression paramExpression) {
/* 36 */     this.expression = paramExpression;
/*    */   }
/*    */ 
/*    */   
/*    */   long update(Schema paramSchema, Domain paramDomain) {
/* 41 */     switch (this.type) {
/*    */       case 94:
/* 43 */         paramDomain.setDefaultExpression(this.session, this.expression);
/*    */         break;
/*    */       case 95:
/* 46 */         paramDomain.setOnUpdateExpression(this.session, this.expression);
/*    */         break;
/*    */       default:
/* 49 */         throw DbException.getInternalError("type=" + this.type);
/*    */     } 
/* 51 */     if (this.expression != null) {
/* 52 */       forAllDependencies(this.session, paramDomain, this::copyColumn, this::copyDomain, true);
/*    */     }
/* 54 */     this.session.getDatabase().updateMeta(this.session, (DbObject)paramDomain);
/* 55 */     return 0L;
/*    */   }
/*    */   
/*    */   private boolean copyColumn(Domain paramDomain, Column paramColumn) {
/* 59 */     return copyExpressions(this.session, paramDomain, (ColumnTemplate)paramColumn);
/*    */   }
/*    */   
/*    */   private boolean copyDomain(Domain paramDomain1, Domain paramDomain2) {
/* 63 */     return copyExpressions(this.session, paramDomain1, (ColumnTemplate)paramDomain2);
/*    */   }
/*    */   private boolean copyExpressions(SessionLocal paramSessionLocal, Domain paramDomain, ColumnTemplate paramColumnTemplate) {
/*    */     Expression expression;
/* 67 */     switch (this.type) {
/*    */       case 94:
/* 69 */         expression = paramDomain.getDefaultExpression();
/* 70 */         if (expression != null && paramColumnTemplate.getDefaultExpression() == null) {
/* 71 */           paramColumnTemplate.setDefaultExpression(paramSessionLocal, expression);
/* 72 */           return true;
/*    */         } 
/*    */         break;
/*    */       
/*    */       case 95:
/* 77 */         expression = paramDomain.getOnUpdateExpression();
/* 78 */         if (expression != null && paramColumnTemplate.getOnUpdateExpression() == null) {
/* 79 */           paramColumnTemplate.setOnUpdateExpression(paramSessionLocal, expression);
/* 80 */           return true;
/*    */         } 
/*    */         break;
/*    */     } 
/* 84 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 89 */     return this.type;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterDomainExpressions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */