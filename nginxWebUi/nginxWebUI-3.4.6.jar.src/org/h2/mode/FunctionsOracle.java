/*     */ package org.h2.mode;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.expression.function.DateTimeFunction;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueTimestamp;
/*     */ import org.h2.value.ValueUuid;
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
/*     */ public final class FunctionsOracle
/*     */   extends ModeFunction
/*     */ {
/*     */   private static final int ADD_MONTHS = 2001;
/*     */   private static final int SYS_GUID = 2002;
/*     */   private static final int TO_DATE = 2003;
/*     */   private static final int TO_TIMESTAMP = 2004;
/*     */   private static final int TO_TIMESTAMP_TZ = 2005;
/*  36 */   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>();
/*     */   
/*     */   static {
/*  39 */     FUNCTIONS.put("ADD_MONTHS", new FunctionInfo("ADD_MONTHS", 2001, 2, 20, true, true));
/*     */     
/*  41 */     FUNCTIONS.put("SYS_GUID", new FunctionInfo("SYS_GUID", 2002, 0, 6, false, false));
/*     */     
/*  43 */     FUNCTIONS.put("TO_DATE", new FunctionInfo("TO_DATE", 2003, -1, 20, true, true));
/*     */     
/*  45 */     FUNCTIONS.put("TO_TIMESTAMP", new FunctionInfo("TO_TIMESTAMP", 2004, -1, 20, true, true));
/*     */     
/*  47 */     FUNCTIONS.put("TO_TIMESTAMP_TZ", new FunctionInfo("TO_TIMESTAMP_TZ", 2005, -1, 21, true, true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FunctionsOracle getFunction(String paramString) {
/*  59 */     FunctionInfo functionInfo = FUNCTIONS.get(paramString);
/*  60 */     return (functionInfo != null) ? new FunctionsOracle(functionInfo) : null;
/*     */   }
/*     */   
/*     */   private FunctionsOracle(FunctionInfo paramFunctionInfo) {
/*  64 */     super(paramFunctionInfo);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void checkParameterCount(int paramInt) {
/*  69 */     byte b = 0; int i = Integer.MAX_VALUE;
/*  70 */     switch (this.info.type) {
/*     */       case 2004:
/*     */       case 2005:
/*  73 */         b = 1;
/*  74 */         i = 2;
/*     */         break;
/*     */       case 2003:
/*  77 */         b = 1;
/*  78 */         i = 3;
/*     */         break;
/*     */       default:
/*  81 */         throw DbException.getInternalError("type=" + this.info.type);
/*     */     } 
/*  83 */     if (paramInt < b || paramInt > i) {
/*  84 */       throw DbException.get(7001, new String[] { this.info.name, b + ".." + i });
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  90 */     boolean bool = optimizeArguments(paramSessionLocal);
/*  91 */     switch (this.info.type) {
/*     */       case 2002:
/*  93 */         this.type = TypeInfo.getTypeInfo(6, 16L, 0, null);
/*     */         break;
/*     */       default:
/*  96 */         this.type = TypeInfo.getTypeInfo(this.info.returnDataType); break;
/*     */     } 
/*  98 */     if (bool) {
/*  99 */       return (Expression)ValueExpression.get(getValue(paramSessionLocal));
/*     */     }
/* 101 */     return (Expression)this;
/*     */   }
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*     */     Value value3;
/*     */     ValueTimestamp valueTimestamp;
/* 106 */     Value[] arrayOfValue = getArgumentsValues(paramSessionLocal, this.args);
/* 107 */     if (arrayOfValue == null) {
/* 108 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 110 */     Value value1 = getNullOrValue(paramSessionLocal, this.args, arrayOfValue, 0);
/* 111 */     Value value2 = getNullOrValue(paramSessionLocal, this.args, arrayOfValue, 1);
/*     */     
/* 113 */     switch (this.info.type) {
/*     */       case 2001:
/* 115 */         value3 = DateTimeFunction.dateadd(paramSessionLocal, 1, value2.getInt(), value1);
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
/* 132 */         return value3;case 2002: value3 = ValueUuid.getNewRandom().convertTo(TypeInfo.TYPE_VARBINARY); return value3;case 2003: valueTimestamp = ToDateParser.toDate(paramSessionLocal, value1.getString(), (value2 == null) ? null : value2.getString()); return (Value)valueTimestamp;case 2004: valueTimestamp = ToDateParser.toTimestamp(paramSessionLocal, value1.getString(), (value2 == null) ? null : value2.getString()); return (Value)valueTimestamp;
/*     */       case 2005:
/*     */         return (Value)ToDateParser.toTimestampTz(paramSessionLocal, value1.getString(), (value2 == null) ? null : value2.getString());
/*     */     } 
/*     */     throw DbException.getInternalError("type=" + this.info.type);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\FunctionsOracle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */