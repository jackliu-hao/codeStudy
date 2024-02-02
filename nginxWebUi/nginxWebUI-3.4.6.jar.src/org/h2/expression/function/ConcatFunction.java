/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public final class ConcatFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public static final int CONCAT = 0;
/*     */   public static final int CONCAT_WS = 1;
/*  32 */   private static final String[] NAMES = new String[] { "CONCAT", "CONCAT_WS" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public ConcatFunction(int paramInt) {
/*  39 */     this(paramInt, new Expression[4]);
/*     */   }
/*     */   
/*     */   public ConcatFunction(int paramInt, Expression... paramVarArgs) {
/*  43 */     super(paramVarArgs);
/*  44 */     this.function = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  49 */     byte b = 0;
/*  50 */     String str = null;
/*  51 */     if (this.function == 1) {
/*  52 */       b = 1;
/*  53 */       str = this.args[0].getValue(paramSessionLocal).getString();
/*     */     } 
/*  55 */     StringBuilder stringBuilder = new StringBuilder();
/*  56 */     boolean bool = false;
/*  57 */     for (int i = this.args.length; b < i; b++) {
/*  58 */       Value value = this.args[b].getValue(paramSessionLocal);
/*  59 */       if (value != ValueNull.INSTANCE) {
/*  60 */         if (str != null) {
/*  61 */           if (bool) {
/*  62 */             stringBuilder.append(str);
/*     */           }
/*  64 */           bool = true;
/*     */         } 
/*  66 */         stringBuilder.append(value.getString());
/*     */       } 
/*     */     } 
/*  69 */     return ValueVarchar.get(stringBuilder.toString(), (CastDataProvider)paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  74 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/*  75 */     byte b = 0;
/*  76 */     long l1 = 0L;
/*  77 */     if (this.function == 1) {
/*  78 */       b = 1;
/*  79 */       l1 = getPrecision(0);
/*     */     } 
/*  81 */     long l2 = 0L;
/*  82 */     int i = this.args.length;
/*  83 */     boolean bool1 = false;
/*  84 */     for (; b < i; b++) {
/*  85 */       if (!this.args[b].isNullConstant()) {
/*     */ 
/*     */         
/*  88 */         l2 = DataType.addPrecision(l2, getPrecision(b));
/*  89 */         if (l1 != 0L && bool1) {
/*  90 */           l2 = DataType.addPrecision(l2, l1);
/*     */         }
/*  92 */         bool1 = true;
/*     */       } 
/*  94 */     }  this.type = TypeInfo.getTypeInfo(2, l2, 0, null);
/*  95 */     if (bool) {
/*  96 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/*  98 */     return (Expression)this;
/*     */   }
/*     */   
/*     */   private long getPrecision(int paramInt) {
/* 102 */     TypeInfo typeInfo = this.args[paramInt].getType();
/* 103 */     int i = typeInfo.getValueType();
/* 104 */     if (i == 0)
/* 105 */       return 0L; 
/* 106 */     if (DataType.isCharacterStringType(i)) {
/* 107 */       return typeInfo.getPrecision();
/*     */     }
/* 109 */     return Long.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 115 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\ConcatFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */