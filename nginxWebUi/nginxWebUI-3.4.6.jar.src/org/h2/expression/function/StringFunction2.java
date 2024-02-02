/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueVarchar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class StringFunction2
/*     */   extends Function2
/*     */ {
/*     */   public static final int LEFT = 0;
/*     */   public static final int RIGHT = 1;
/*     */   public static final int REPEAT = 2;
/*  36 */   private static final String[] NAMES = new String[] { "LEFT", "RIGHT", "REPEAT" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public StringFunction2(Expression paramExpression1, Expression paramExpression2, int paramInt) {
/*  43 */     super(paramExpression1, paramExpression2);
/*  44 */     this.function = paramInt;
/*     */   }
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/*     */     StringBuilder stringBuilder;
/*  49 */     String str = paramValue1.getString();
/*  50 */     int i = paramValue2.getInt();
/*  51 */     if (i <= 0) {
/*  52 */       return ValueVarchar.get("", (CastDataProvider)paramSessionLocal);
/*     */     }
/*  54 */     int j = str.length();
/*  55 */     switch (this.function) {
/*     */       case 0:
/*  57 */         if (i > j) {
/*  58 */           i = j;
/*     */         }
/*  60 */         str = str.substring(0, i);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  79 */         return ValueVarchar.get(str, (CastDataProvider)paramSessionLocal);case 1: if (i > j) i = j;  str = str.substring(j - i); return ValueVarchar.get(str, (CastDataProvider)paramSessionLocal);case 2: stringBuilder = new StringBuilder(j * i); while (i-- > 0) stringBuilder.append(str);  str = stringBuilder.toString(); return ValueVarchar.get(str, (CastDataProvider)paramSessionLocal);
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function);
/*     */   }
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  84 */     this.left = this.left.optimize(paramSessionLocal);
/*  85 */     this.right = this.right.optimize(paramSessionLocal);
/*  86 */     switch (this.function) {
/*     */       case 0:
/*     */       case 1:
/*  89 */         this.type = TypeInfo.getTypeInfo(2, this.left.getType().getPrecision(), 0, null);
/*     */         break;
/*     */       case 2:
/*  92 */         this.type = TypeInfo.TYPE_VARCHAR;
/*     */         break;
/*     */       default:
/*  95 */         throw DbException.getInternalError("function=" + this.function);
/*     */     } 
/*  97 */     if (this.left.isConstant() && this.right.isConstant()) {
/*  98 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 100 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 105 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\StringFunction2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */