/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.Operation0;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CurrentDateTimeValueFunction
/*     */   extends Operation0
/*     */   implements NamedExpression
/*     */ {
/*     */   public static final int CURRENT_DATE = 0;
/*     */   public static final int CURRENT_TIME = 1;
/*     */   public static final int LOCALTIME = 2;
/*     */   public static final int CURRENT_TIMESTAMP = 3;
/*     */   public static final int LOCALTIMESTAMP = 4;
/*  46 */   private static final int[] TYPES = new int[] { 17, 19, 18, 21, 20 };
/*     */   
/*  48 */   private static final String[] NAMES = new String[] { "CURRENT_DATE", "CURRENT_TIME", "LOCALTIME", "CURRENT_TIMESTAMP", "LOCALTIMESTAMP" };
/*     */   
/*     */   private final int function;
/*     */   
/*     */   private final int scale;
/*     */   
/*     */   private final TypeInfo type;
/*     */ 
/*     */   
/*     */   public static String getName(int paramInt) {
/*  58 */     return NAMES[paramInt];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CurrentDateTimeValueFunction(int paramInt1, int paramInt2) {
/*  66 */     this.function = paramInt1;
/*  67 */     this.scale = paramInt2;
/*  68 */     if (paramInt2 < 0) {
/*  69 */       paramInt2 = (paramInt1 >= 3) ? 6 : 0;
/*     */     }
/*  71 */     this.type = TypeInfo.getTypeInfo(TYPES[paramInt1], 0L, paramInt2, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  76 */     return paramSessionLocal.currentTimestamp().castTo(this.type, (CastDataProvider)paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  81 */     paramStringBuilder.append(getName());
/*  82 */     if (this.scale >= 0) {
/*  83 */       paramStringBuilder.append('(').append(this.scale).append(')');
/*     */     }
/*  85 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/*  90 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 2:
/*  92 */         return false;
/*     */     } 
/*  94 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  99 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 104 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 109 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\CurrentDateTimeValueFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */