/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Mode;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueArray;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueCollectionBase;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public final class ArrayFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public static final int TRIM_ARRAY = 0;
/*     */   public static final int ARRAY_CONTAINS = 1;
/*     */   public static final int ARRAY_SLICE = 2;
/*  43 */   private static final String[] NAMES = new String[] { "TRIM_ARRAY", "ARRAY_CONTAINS", "ARRAY_SLICE" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public ArrayFunction(Expression paramExpression1, Expression paramExpression2, Expression paramExpression3, int paramInt) {
/*  50 */     super((paramExpression3 == null) ? new Expression[2] : new Expression[3]);
/*  51 */     this.function = paramInt; } public Value getValue(SessionLocal paramSessionLocal) { ValueNull valueNull2;
/*     */     ValueArray valueArray2;
/*     */     ValueNull valueNull1;
/*     */     ValueArray valueArray1;
/*     */     int i;
/*  56 */     Value value3, value1 = this.args[0].getValue(paramSessionLocal), value2 = this.args[1].getValue(paramSessionLocal);
/*  57 */     switch (this.function) {
/*     */       case 0:
/*  59 */         if (value2 == ValueNull.INSTANCE) {
/*  60 */           valueNull2 = ValueNull.INSTANCE;
/*     */         } else {
/*     */           
/*  63 */           int j = value2.getInt();
/*  64 */           if (j < 0)
/*     */           {
/*  66 */             throw DbException.get(22034, new String[] { Integer.toString(j), "0..CARDINALITY(array)" });
/*     */           }
/*     */           
/*  69 */           if (valueNull2 != ValueNull.INSTANCE) {
/*     */ 
/*     */             
/*  72 */             ValueArray valueArray = valueNull2.convertToAnyArray((CastDataProvider)paramSessionLocal);
/*  73 */             Value[] arrayOfValue = valueArray.getList();
/*  74 */             int k = arrayOfValue.length;
/*  75 */             if (j > k)
/*  76 */               throw DbException.get(22034, new String[] { Integer.toString(j), "0.." + k }); 
/*  77 */             if (j == 0) {
/*  78 */               valueArray2 = valueArray;
/*     */             } else {
/*  80 */               valueArray2 = ValueArray.get(valueArray.getComponentType(), Arrays.<Value>copyOf(arrayOfValue, k - j), (CastDataProvider)paramSessionLocal);
/*     */             } 
/*     */           } 
/*     */         } 
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
/* 142 */         return (Value)valueArray2;case 1: i = valueArray2.getValueType(); if (i == 40 || i == 41) { Value[] arrayOfValue = ((ValueCollectionBase)valueArray2).getList(); ValueBoolean valueBoolean = ValueBoolean.FALSE; for (Value value : arrayOfValue) { if (paramSessionLocal.areEqual(value, value2)) { valueBoolean = ValueBoolean.TRUE; break; }  }  } else { valueNull1 = ValueNull.INSTANCE; }  return (Value)valueNull1;case 2: if (valueNull1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE || (value3 = this.args[2].getValue(paramSessionLocal)) == ValueNull.INSTANCE) { valueNull1 = ValueNull.INSTANCE; } else { ValueArray valueArray = valueNull1.convertToAnyArray((CastDataProvider)paramSessionLocal); int j = value2.getInt() - 1; int k = value3.getInt(); boolean bool = (paramSessionLocal.getMode().getEnum() == Mode.ModeEnum.PostgreSQL) ? true : false; if (j > k) { valueNull1 = bool ? (ValueNull)ValueArray.get(valueArray.getComponentType(), Value.EMPTY_VALUES, (CastDataProvider)paramSessionLocal) : ValueNull.INSTANCE; } else { if (j < 0) if (bool) { j = 0; } else { valueNull1 = ValueNull.INSTANCE; return (Value)valueNull1; }   if (k > (valueArray.getList()).length) if (bool) { k = (valueArray.getList()).length; } else { valueNull1 = ValueNull.INSTANCE; return (Value)valueNull1; }   valueArray1 = ValueArray.get(valueArray.getComponentType(), Arrays.<Value>copyOfRange(valueArray.getList(), j, k), (CastDataProvider)paramSessionLocal); }  }  return (Value)valueArray1;
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function); } public Expression optimize(SessionLocal paramSessionLocal) {
/*     */     Expression expression;
/*     */     int i;
/* 147 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/* 148 */     switch (this.function) {
/*     */       case 0:
/*     */       case 2:
/* 151 */         expression = this.args[0];
/* 152 */         this.type = expression.getType();
/* 153 */         i = this.type.getValueType();
/* 154 */         if (i != 40 && i != 0) {
/* 155 */           throw DbException.getInvalidExpressionTypeException(getName() + " array argument", expression);
/*     */         }
/*     */         break;
/*     */       
/*     */       case 1:
/* 160 */         this.type = TypeInfo.TYPE_BOOLEAN;
/*     */         break;
/*     */       default:
/* 163 */         throw DbException.getInternalError("function=" + this.function);
/*     */     } 
/* 165 */     if (bool) {
/* 166 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 168 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 173 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\ArrayFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */