/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.command.Command;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionVisitor;
/*    */ import org.h2.message.DbException;
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
/*    */ public final class SessionControlFunction
/*    */   extends Function1
/*    */ {
/*    */   public static final int ABORT_SESSION = 0;
/*    */   public static final int CANCEL_SESSION = 1;
/* 33 */   private static final String[] NAMES = new String[] { "ABORT_SESSION", "CANCEL_SESSION" };
/*    */ 
/*    */   
/*    */   private final int function;
/*    */ 
/*    */   
/*    */   public SessionControlFunction(Expression paramExpression, int paramInt) {
/* 40 */     super(paramExpression);
/* 41 */     this.function = paramInt;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 46 */     Value value = this.arg.getValue(paramSessionLocal);
/* 47 */     if (value == ValueNull.INSTANCE) {
/* 48 */       return (Value)ValueNull.INSTANCE;
/*    */     }
/* 50 */     int i = value.getInt();
/* 51 */     paramSessionLocal.getUser().checkAdmin();
/* 52 */     for (SessionLocal sessionLocal : paramSessionLocal.getDatabase().getSessions(false)) {
/* 53 */       if (sessionLocal.getId() == i) {
/* 54 */         Command command = sessionLocal.getCurrentCommand();
/* 55 */         switch (this.function) {
/*    */           case 0:
/* 57 */             if (command != null) {
/* 58 */               command.cancel();
/*    */             }
/* 60 */             sessionLocal.close();
/* 61 */             return (Value)ValueBoolean.TRUE;
/*    */           case 1:
/* 63 */             if (command != null) {
/* 64 */               command.cancel();
/* 65 */               return (Value)ValueBoolean.TRUE;
/*    */             } 
/*    */             break;
/*    */         } 
/* 69 */         throw DbException.getInternalError("function=" + this.function);
/*    */       } 
/*    */     } 
/*    */     
/* 73 */     return (Value)ValueBoolean.FALSE;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 78 */     this.arg = this.arg.optimize(paramSessionLocal);
/* 79 */     this.type = TypeInfo.TYPE_BOOLEAN;
/* 80 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 85 */     switch (paramExpressionVisitor.getType()) {
/*    */       case 2:
/*    */       case 5:
/*    */       case 8:
/* 89 */         return false;
/*    */     } 
/* 91 */     return super.isEverything(paramExpressionVisitor);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 96 */     return NAMES[this.function];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\SessionControlFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */