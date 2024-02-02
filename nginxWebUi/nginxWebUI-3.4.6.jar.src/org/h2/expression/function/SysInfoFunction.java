/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Constants;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.Operation0;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueInteger;
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
/*     */ 
/*     */ 
/*     */ public final class SysInfoFunction
/*     */   extends Operation0
/*     */   implements NamedExpression
/*     */ {
/*     */   public static final int AUTOCOMMIT = 0;
/*     */   public static final int DATABASE_PATH = 1;
/*     */   public static final int H2VERSION = 2;
/*     */   public static final int LOCK_MODE = 3;
/*     */   public static final int LOCK_TIMEOUT = 4;
/*     */   public static final int MEMORY_FREE = 5;
/*     */   public static final int MEMORY_USED = 6;
/*     */   public static final int READONLY = 7;
/*     */   public static final int SESSION_ID = 8;
/*     */   public static final int TRANSACTION_ID = 9;
/*  77 */   private static final int[] TYPES = new int[] { 8, 2, 2, 11, 11, 12, 12, 8, 11, 2 };
/*     */ 
/*     */   
/*  80 */   private static final String[] NAMES = new String[] { "AUTOCOMMIT", "DATABASE_PATH", "H2VERSION", "LOCK_MODE", "LOCK_TIMEOUT", "MEMORY_FREE", "MEMORY_USED", "READONLY", "SESSION_ID", "TRANSACTION_ID" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   private final TypeInfo type;
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getName(int paramInt) {
/*  91 */     return NAMES[paramInt];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SysInfoFunction(int paramInt) {
/*  99 */     this.function = paramInt;
/* 100 */     this.type = TypeInfo.getTypeInfo(TYPES[paramInt]); } public Value getValue(SessionLocal paramSessionLocal) { ValueBoolean valueBoolean2; Value value;
/*     */     ValueInteger valueInteger2;
/*     */     ValueBigint valueBigint;
/*     */     ValueBoolean valueBoolean1;
/*     */     ValueInteger valueInteger1;
/*     */     String str;
/* 106 */     switch (this.function)
/*     */     { case 0:
/* 108 */         valueBoolean2 = ValueBoolean.get(paramSessionLocal.getAutoCommit());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 144 */         return (Value)valueBoolean2;case 1: str = paramSessionLocal.getDatabase().getDatabasePath(); valueBoolean2 = (str != null) ? (ValueBoolean)ValueVarchar.get(str, (CastDataProvider)paramSessionLocal) : (ValueBoolean)ValueNull.INSTANCE; return (Value)valueBoolean2;case 2: return ValueVarchar.get(Constants.VERSION, (CastDataProvider)paramSessionLocal);case 3: valueInteger2 = ValueInteger.get(paramSessionLocal.getDatabase().getLockMode()); return (Value)valueInteger2;case 4: valueInteger2 = ValueInteger.get(paramSessionLocal.getLockTimeout()); return (Value)valueInteger2;case 5: paramSessionLocal.getUser().checkAdmin(); valueBigint = ValueBigint.get(Utils.getMemoryFree()); return (Value)valueBigint;case 6: paramSessionLocal.getUser().checkAdmin(); valueBigint = ValueBigint.get(Utils.getMemoryUsed()); return (Value)valueBigint;
/*     */       case 7: return (Value)ValueBoolean.get(paramSessionLocal.getDatabase().isReadOnly());
/*     */       case 8:
/*     */         return (Value)ValueInteger.get(paramSessionLocal.getId());
/*     */       case 9:
/* 149 */         return paramSessionLocal.getTransactionId(); }  throw DbException.getInternalError("function=" + this.function); } public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) { return paramStringBuilder.append(getName()).append("()"); }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 154 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 2:
/* 156 */         return false;
/*     */     } 
/* 158 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 163 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 168 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 173 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\SysInfoFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */