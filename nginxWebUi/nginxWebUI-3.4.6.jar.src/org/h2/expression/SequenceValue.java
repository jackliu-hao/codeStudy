/*    */ package org.h2.expression;
/*    */ 
/*    */ import org.h2.command.Prepared;
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.schema.Sequence;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SequenceValue
/*    */   extends Operation0
/*    */ {
/*    */   private final Sequence sequence;
/*    */   private final boolean current;
/*    */   private final Prepared prepared;
/*    */   
/*    */   public SequenceValue(Sequence paramSequence, Prepared paramPrepared) {
/* 34 */     this.sequence = paramSequence;
/* 35 */     this.current = false;
/* 36 */     this.prepared = paramPrepared;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SequenceValue(Sequence paramSequence) {
/* 46 */     this.sequence = paramSequence;
/* 47 */     this.current = true;
/* 48 */     this.prepared = null;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 53 */     return this.current ? paramSessionLocal.getCurrentValueFor(this.sequence) : paramSessionLocal.getNextValueFor(this.sequence, this.prepared);
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 58 */     return this.sequence.getDataType();
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 63 */     paramStringBuilder.append(this.current ? "CURRENT" : "NEXT").append(" VALUE FOR ");
/* 64 */     return this.sequence.getSQL(paramStringBuilder, paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 69 */     switch (paramExpressionVisitor.getType()) {
/*    */       case 0:
/*    */       case 2:
/*    */       case 8:
/* 73 */         return false;
/*    */       case 4:
/* 75 */         paramExpressionVisitor.addDataModificationId(this.sequence.getModificationId());
/* 76 */         return true;
/*    */       case 7:
/* 78 */         paramExpressionVisitor.addDependency((DbObject)this.sequence);
/* 79 */         return true;
/*    */       case 5:
/* 81 */         return this.current;
/*    */     } 
/* 83 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 89 */     return 1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\SequenceValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */