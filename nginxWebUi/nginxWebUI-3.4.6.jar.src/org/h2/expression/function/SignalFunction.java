/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import java.util.regex.Pattern;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.message.DbException;
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
/*    */ public final class SignalFunction
/*    */   extends Function2
/*    */ {
/* 21 */   private static final Pattern SIGNAL_PATTERN = Pattern.compile("[0-9A-Z]{5}");
/*    */   
/*    */   public SignalFunction(Expression paramExpression1, Expression paramExpression2) {
/* 24 */     super(paramExpression1, paramExpression2);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/* 29 */     String str = paramValue1.getString();
/* 30 */     if (str.startsWith("00") || !SIGNAL_PATTERN.matcher(str).matches()) {
/* 31 */       throw DbException.getInvalidValueException("SQLSTATE", str);
/*    */     }
/* 33 */     throw DbException.fromUser(str, paramValue2.getString());
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 38 */     this.left = this.left.optimize(paramSessionLocal);
/* 39 */     this.right = this.right.optimize(paramSessionLocal);
/* 40 */     this.type = TypeInfo.TYPE_NULL;
/* 41 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 46 */     return "SIGNAL";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\SignalFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */