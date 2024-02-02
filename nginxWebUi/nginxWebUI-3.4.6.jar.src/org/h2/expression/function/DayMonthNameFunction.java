/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.text.DateFormatSymbols;
/*     */ import java.util.Locale;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.DateTimeUtils;
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
/*     */ 
/*     */ public final class DayMonthNameFunction
/*     */   extends Function1
/*     */ {
/*     */   public static final int DAYNAME = 0;
/*     */   public static final int MONTHNAME = 1;
/*  36 */   private static final String[] NAMES = new String[] { "DAYNAME", "MONTHNAME" };
/*     */ 
/*     */ 
/*     */   
/*     */   private static volatile String[][] MONTHS_AND_WEEKS;
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */ 
/*     */   
/*     */   public DayMonthNameFunction(Expression paramExpression, int paramInt) {
/*  48 */     super(paramExpression);
/*  49 */     this.function = paramInt;
/*     */   }
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*     */     String str;
/*  54 */     Value value = this.arg.getValue(paramSessionLocal);
/*  55 */     if (value == ValueNull.INSTANCE) {
/*  56 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  58 */     long l = DateTimeUtils.dateAndTimeFromValue(value, (CastDataProvider)paramSessionLocal)[0];
/*     */     
/*  60 */     switch (this.function) {
/*     */       case 0:
/*  62 */         str = getMonthsAndWeeks(1)[DateTimeUtils.getDayOfWeek(l, 0)];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  70 */         return ValueVarchar.get(str, (CastDataProvider)paramSessionLocal);case 1: str = getMonthsAndWeeks(0)[DateTimeUtils.monthFromDateValue(l) - 1]; return ValueVarchar.get(str, (CastDataProvider)paramSessionLocal);
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String[] getMonthsAndWeeks(int paramInt) {
/*  81 */     String[][] arrayOfString = MONTHS_AND_WEEKS;
/*  82 */     if (arrayOfString == null) {
/*  83 */       arrayOfString = new String[2][];
/*  84 */       DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(Locale.ENGLISH);
/*  85 */       arrayOfString[0] = dateFormatSymbols.getMonths();
/*  86 */       arrayOfString[1] = dateFormatSymbols.getWeekdays();
/*  87 */       MONTHS_AND_WEEKS = arrayOfString;
/*     */     } 
/*  89 */     return arrayOfString[paramInt];
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  94 */     this.arg = this.arg.optimize(paramSessionLocal);
/*  95 */     this.type = TypeInfo.getTypeInfo(2, 20L, 0, null);
/*  96 */     if (this.arg.isConstant()) {
/*  97 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/*  99 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 104 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\DayMonthNameFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */