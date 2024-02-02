/*    */ package org.h2.expression;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
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
/*    */ public final class Variable
/*    */   extends Operation0
/*    */ {
/*    */   private final String name;
/*    */   private Value lastValue;
/*    */   
/*    */   public Variable(SessionLocal paramSessionLocal, String paramString) {
/* 22 */     this.name = paramString;
/* 23 */     this.lastValue = paramSessionLocal.getVariable(paramString);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 28 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 33 */     return ParserUtil.quoteIdentifier(paramStringBuilder.append('@'), this.name, paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 38 */     return this.lastValue.getType();
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 43 */     this.lastValue = paramSessionLocal.getVariable(this.name);
/* 44 */     return this.lastValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 49 */     switch (paramExpressionVisitor.getType()) {
/*    */       case 2:
/* 51 */         return false;
/*    */     } 
/* 53 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 58 */     return this.name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Variable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */