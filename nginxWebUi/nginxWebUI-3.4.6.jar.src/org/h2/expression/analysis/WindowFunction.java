/*     */ package org.h2.expression.analysis;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import org.h2.command.query.Select;
/*     */ import org.h2.command.query.SelectGroups;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueDouble;
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
/*     */ public class WindowFunction
/*     */   extends DataAnalysisOperation
/*     */ {
/*     */   private final WindowFunctionType type;
/*     */   private final Expression[] args;
/*     */   private boolean fromLast;
/*     */   private boolean ignoreNulls;
/*     */   
/*     */   public static int getMinArgumentCount(WindowFunctionType paramWindowFunctionType) {
/*  47 */     switch (paramWindowFunctionType) {
/*     */       case NTILE:
/*     */       case LEAD:
/*     */       case LAG:
/*     */       case FIRST_VALUE:
/*     */       case LAST_VALUE:
/*     */       case RATIO_TO_REPORT:
/*  54 */         return 1;
/*     */       case NTH_VALUE:
/*  56 */         return 2;
/*     */     } 
/*  58 */     return 0;
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
/*     */   public static int getMaxArgumentCount(WindowFunctionType paramWindowFunctionType) {
/*  70 */     switch (paramWindowFunctionType) {
/*     */       case NTILE:
/*     */       case FIRST_VALUE:
/*     */       case LAST_VALUE:
/*     */       case RATIO_TO_REPORT:
/*  75 */         return 1;
/*     */       case LEAD:
/*     */       case LAG:
/*  78 */         return 3;
/*     */       case NTH_VALUE:
/*  80 */         return 2;
/*     */     } 
/*  82 */     return 0;
/*     */   }
/*     */   
/*     */   private static Value getNthValue(Iterator<Value[]> paramIterator, int paramInt, boolean paramBoolean) {
/*     */     Value value;
/*  87 */     ValueNull valueNull = ValueNull.INSTANCE;
/*  88 */     byte b = 0;
/*  89 */     while (paramIterator.hasNext()) {
/*  90 */       Value value1 = ((Value[])paramIterator.next())[0];
/*  91 */       if ((!paramBoolean || value1 != ValueNull.INSTANCE) && 
/*  92 */         b++ == paramInt) {
/*  93 */         value = value1;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/*  98 */     return value;
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
/*     */ 
/*     */   
/*     */   public WindowFunction(WindowFunctionType paramWindowFunctionType, Select paramSelect, Expression[] paramArrayOfExpression) {
/* 112 */     super(paramSelect);
/* 113 */     this.type = paramWindowFunctionType;
/* 114 */     this.args = paramArrayOfExpression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WindowFunctionType getFunctionType() {
/* 123 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFromLast(boolean paramBoolean) {
/* 133 */     this.fromLast = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIgnoreNulls(boolean paramBoolean) {
/* 143 */     this.ignoreNulls = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAggregate() {
/* 148 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void updateAggregate(SessionLocal paramSessionLocal, SelectGroups paramSelectGroups, int paramInt) {
/* 153 */     updateOrderedAggregate(paramSessionLocal, paramSelectGroups, paramInt, this.over.getOrderBy());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void updateGroupAggregates(SessionLocal paramSessionLocal, int paramInt) {
/* 158 */     super.updateGroupAggregates(paramSessionLocal, paramInt);
/* 159 */     if (this.args != null) {
/* 160 */       for (Expression expression : this.args) {
/* 161 */         expression.updateAggregate(paramSessionLocal, paramInt);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected int getNumExpressions() {
/* 168 */     return (this.args != null) ? this.args.length : 0;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void rememberExpressions(SessionLocal paramSessionLocal, Value[] paramArrayOfValue) {
/* 173 */     if (this.args != null) {
/* 174 */       byte b; int i; for (b = 0, i = this.args.length; b < i; b++) {
/* 175 */         paramArrayOfValue[b] = this.args[b].getValue(paramSessionLocal);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object createAggregateData() {
/* 182 */     throw DbException.getUnsupportedException("Window function");
/*     */   }
/*     */   
/*     */   protected void getOrderedResultLoop(SessionLocal paramSessionLocal, HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt) {
/*     */     byte b;
/*     */     int i;
/* 188 */     switch (this.type) {
/*     */       case ROW_NUMBER:
/* 190 */         for (b = 0, i = paramArrayList.size(); b < i;) {
/* 191 */           paramHashMap.put(Integer.valueOf(((Value[])paramArrayList.get(b))[paramInt].getInt()), ValueBigint.get(++b));
/*     */         }
/*     */         return;
/*     */       case RANK:
/*     */       case DENSE_RANK:
/*     */       case PERCENT_RANK:
/* 197 */         getRank(paramHashMap, paramArrayList, paramInt);
/*     */         return;
/*     */       case CUME_DIST:
/* 200 */         getCumeDist(paramHashMap, paramArrayList, paramInt);
/*     */         return;
/*     */       case NTILE:
/* 203 */         getNtile(paramHashMap, paramArrayList, paramInt);
/*     */         return;
/*     */       case LEAD:
/*     */       case LAG:
/* 207 */         getLeadLag(paramHashMap, paramArrayList, paramInt, paramSessionLocal);
/*     */         return;
/*     */       case FIRST_VALUE:
/*     */       case LAST_VALUE:
/*     */       case NTH_VALUE:
/* 212 */         getNth(paramSessionLocal, paramHashMap, paramArrayList, paramInt);
/*     */         return;
/*     */       case RATIO_TO_REPORT:
/* 215 */         getRatioToReport(paramHashMap, paramArrayList, paramInt);
/*     */         return;
/*     */     } 
/* 218 */     throw DbException.getInternalError("type=" + this.type);
/*     */   }
/*     */ 
/*     */   
/*     */   private void getRank(HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt) {
/* 223 */     int i = paramArrayList.size();
/* 224 */     int j = 0;
/* 225 */     for (byte b = 0; b < i; b++) {
/* 226 */       ValueBigint valueBigint; Value[] arrayOfValue = paramArrayList.get(b);
/* 227 */       if (b == 0) {
/* 228 */         j = 1;
/* 229 */       } else if (getOverOrderBySort().compare(paramArrayList.get(b - 1), arrayOfValue) != 0) {
/* 230 */         if (this.type == WindowFunctionType.DENSE_RANK) {
/* 231 */           j++;
/*     */         } else {
/* 233 */           j = b + 1;
/*     */         } 
/*     */       } 
/*     */       
/* 237 */       if (this.type == WindowFunctionType.PERCENT_RANK) {
/* 238 */         int k = j - 1;
/* 239 */         ValueDouble valueDouble = (k == 0) ? ValueDouble.ZERO : ValueDouble.get(k / (i - 1));
/*     */       } else {
/* 241 */         valueBigint = ValueBigint.get(j);
/*     */       } 
/* 243 */       paramHashMap.put(Integer.valueOf(arrayOfValue[paramInt].getInt()), valueBigint);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void getCumeDist(HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt) {
/* 248 */     int i = paramArrayList.size(); int j;
/* 249 */     for (j = 0; j < i; ) {
/* 250 */       Value[] arrayOfValue = paramArrayList.get(j);
/* 251 */       int k = j + 1;
/* 252 */       while (k < i && this.overOrderBySort.compare(arrayOfValue, paramArrayList.get(k)) == 0) {
/* 253 */         k++;
/*     */       }
/* 255 */       ValueDouble valueDouble = ValueDouble.get(k / i);
/* 256 */       for (int m = j; m < k; m++) {
/* 257 */         int n = ((Value[])paramArrayList.get(m))[paramInt].getInt();
/* 258 */         paramHashMap.put(Integer.valueOf(n), valueDouble);
/*     */       } 
/* 260 */       j = k;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void getNtile(HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt) {
/* 265 */     int i = paramArrayList.size();
/* 266 */     for (byte b = 0; b < i; b++) {
/* 267 */       long l5; Value[] arrayOfValue = paramArrayList.get(b);
/* 268 */       long l1 = arrayOfValue[0].getLong();
/* 269 */       if (l1 <= 0L) {
/* 270 */         throw DbException.getInvalidValueException("number of tiles", Long.valueOf(l1));
/*     */       }
/* 272 */       long l2 = i / l1;
/* 273 */       long l3 = i - l2 * l1;
/* 274 */       long l4 = l3 * (l2 + 1L);
/*     */       
/* 276 */       if (b >= l4) {
/* 277 */         l5 = (b - l4) / l2 + l3 + 1L;
/*     */       } else {
/* 279 */         l5 = b / (l2 + 1L) + 1L;
/*     */       } 
/* 281 */       paramHashMap.put(Integer.valueOf(((Value[])paramArrayList.get(b))[paramInt].getInt()), ValueBigint.get(l5));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void getLeadLag(HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt, SessionLocal paramSessionLocal) {
/* 287 */     int i = paramArrayList.size();
/* 288 */     int j = getNumExpressions();
/* 289 */     TypeInfo typeInfo = this.args[0].getType();
/* 290 */     for (byte b = 0; b < i; b++) {
/* 291 */       byte b1; ValueNull valueNull; Value[] arrayOfValue = paramArrayList.get(b);
/* 292 */       int k = arrayOfValue[paramInt].getInt();
/*     */       
/* 294 */       if (j >= 2) {
/* 295 */         b1 = arrayOfValue[1].getInt();
/*     */         
/* 297 */         if (b1 < 0) {
/* 298 */           throw DbException.getInvalidValueException("nth row", Integer.valueOf(b1));
/*     */         }
/*     */       } else {
/* 301 */         b1 = 1;
/*     */       } 
/* 303 */       Value value = null;
/* 304 */       if (!b1) {
/* 305 */         value = ((Value[])paramArrayList.get(b))[0];
/* 306 */       } else if (this.type == WindowFunctionType.LEAD) {
/* 307 */         if (this.ignoreNulls) {
/* 308 */           for (int m = b + 1; b1 && m < i; m++) {
/* 309 */             value = ((Value[])paramArrayList.get(m))[0];
/* 310 */             if (value != ValueNull.INSTANCE) {
/* 311 */               b1--;
/*     */             }
/*     */           } 
/* 314 */           if (b1 > 0) {
/* 315 */             value = null;
/*     */           }
/*     */         }
/* 318 */         else if (b1 <= i - b - 1) {
/* 319 */           value = ((Value[])paramArrayList.get(b + b1))[0];
/*     */         }
/*     */       
/*     */       }
/* 323 */       else if (this.ignoreNulls) {
/* 324 */         for (int m = b - 1; b1 > 0 && m >= 0; m--) {
/* 325 */           value = ((Value[])paramArrayList.get(m))[0];
/* 326 */           if (value != ValueNull.INSTANCE) {
/* 327 */             b1--;
/*     */           }
/*     */         } 
/* 330 */         if (b1 > 0) {
/* 331 */           value = null;
/*     */         }
/*     */       }
/* 334 */       else if (b1 <= b) {
/* 335 */         value = ((Value[])paramArrayList.get(b - b1))[0];
/*     */       } 
/*     */ 
/*     */       
/* 339 */       if (value == null) {
/* 340 */         if (j >= 3) {
/* 341 */           value = arrayOfValue[2].convertTo(typeInfo, (CastDataProvider)paramSessionLocal);
/*     */         } else {
/* 343 */           valueNull = ValueNull.INSTANCE;
/*     */         } 
/*     */       }
/* 346 */       paramHashMap.put(Integer.valueOf(k), valueNull);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void getNth(SessionLocal paramSessionLocal, HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt) {
/* 352 */     int i = paramArrayList.size();
/* 353 */     for (byte b = 0; b < i; b++) {
/* 354 */       Value value; int k; Iterator<Value[]> iterator; Value[] arrayOfValue = paramArrayList.get(b);
/* 355 */       int j = arrayOfValue[paramInt].getInt();
/*     */       
/* 357 */       switch (this.type) {
/*     */         case FIRST_VALUE:
/* 359 */           value = getNthValue(WindowFrame.iterator(this.over, paramSessionLocal, paramArrayList, getOverOrderBySort(), b, false), 0, this.ignoreNulls);
/*     */           break;
/*     */         
/*     */         case LAST_VALUE:
/* 363 */           value = getNthValue(WindowFrame.iterator(this.over, paramSessionLocal, paramArrayList, getOverOrderBySort(), b, true), 0, this.ignoreNulls);
/*     */           break;
/*     */         
/*     */         case NTH_VALUE:
/* 367 */           k = arrayOfValue[1].getInt();
/* 368 */           if (k <= 0) {
/* 369 */             throw DbException.getInvalidValueException("nth row", Integer.valueOf(k));
/*     */           }
/* 371 */           k--;
/* 372 */           iterator = WindowFrame.iterator(this.over, paramSessionLocal, paramArrayList, getOverOrderBySort(), b, this.fromLast);
/*     */           
/* 374 */           value = getNthValue(iterator, k, this.ignoreNulls);
/*     */           break;
/*     */         
/*     */         default:
/* 378 */           throw DbException.getInternalError("type=" + this.type);
/*     */       } 
/* 380 */       paramHashMap.put(Integer.valueOf(j), value);
/*     */     } 
/*     */   }
/*     */   private static void getRatioToReport(HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt) {
/*     */     Value value;
/* 385 */     int i = paramArrayList.size();
/* 386 */     ValueDouble valueDouble = null; byte b;
/* 387 */     for (b = 0; b < i; b++) {
/* 388 */       Value value1 = ((Value[])paramArrayList.get(b))[0];
/* 389 */       if (value1 != ValueNull.INSTANCE) {
/* 390 */         if (valueDouble == null) {
/* 391 */           valueDouble = value1.convertToDouble();
/*     */         } else {
/* 393 */           value = valueDouble.add((Value)value1.convertToDouble());
/*     */         } 
/*     */       }
/*     */     } 
/* 397 */     if (value != null && value.getSignum() == 0) {
/* 398 */       value = null;
/*     */     }
/* 400 */     for (b = 0; b < i; b++) {
/* 401 */       Value value1, arrayOfValue[] = paramArrayList.get(b);
/*     */       
/* 403 */       if (value == null) {
/* 404 */         ValueNull valueNull = ValueNull.INSTANCE;
/*     */       } else {
/* 406 */         value1 = arrayOfValue[0];
/* 407 */         if (value1 != ValueNull.INSTANCE) {
/* 408 */           value1 = value1.convertToDouble().divide(value, TypeInfo.TYPE_DOUBLE);
/*     */         }
/*     */       } 
/* 411 */       paramHashMap.put(Integer.valueOf(arrayOfValue[paramInt].getInt()), value1);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected Value getAggregatedValue(SessionLocal paramSessionLocal, Object paramObject) {
/* 417 */     throw DbException.getUnsupportedException("Window function");
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumnsAnalysis(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 422 */     if (this.args != null) {
/* 423 */       for (Expression expression : this.args) {
/* 424 */         expression.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */       }
/*     */     }
/* 427 */     super.mapColumnsAnalysis(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 432 */     if (this.over.getWindowFrame() != null) {
/* 433 */       String str; switch (this.type) {
/*     */         case FIRST_VALUE:
/*     */         case LAST_VALUE:
/*     */         case NTH_VALUE:
/*     */           break;
/*     */         default:
/* 439 */           str = getTraceSQL();
/* 440 */           throw DbException.getSyntaxError(str, str.length() - 1);
/*     */       } 
/*     */     } 
/* 443 */     if (this.over.getOrderBy() == null) {
/* 444 */       if (this.type.requiresWindowOrdering()) {
/* 445 */         String str = getTraceSQL();
/* 446 */         throw DbException.getSyntaxError(str, str.length() - 1, "ORDER BY");
/*     */       } 
/* 448 */     } else if (this.type == WindowFunctionType.RATIO_TO_REPORT) {
/* 449 */       String str = getTraceSQL();
/* 450 */       throw DbException.getSyntaxError(str, str.length() - 1);
/*     */     } 
/* 452 */     super.optimize(paramSessionLocal);
/*     */ 
/*     */     
/* 455 */     if (this.over.getOrderBy() == null) {
/* 456 */       switch (this.type) {
/*     */         case RANK:
/*     */         case DENSE_RANK:
/* 459 */           return (Expression)ValueExpression.get((Value)ValueBigint.get(1L));
/*     */         case PERCENT_RANK:
/* 461 */           return (Expression)ValueExpression.get((Value)ValueDouble.ZERO);
/*     */         case CUME_DIST:
/* 463 */           return (Expression)ValueExpression.get((Value)ValueDouble.ONE);
/*     */       } 
/*     */     
/*     */     }
/* 467 */     if (this.args != null) {
/* 468 */       for (byte b = 0; b < this.args.length; b++) {
/* 469 */         this.args[b] = this.args[b].optimize(paramSessionLocal);
/*     */       }
/*     */     }
/* 472 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 477 */     if (this.args != null) {
/* 478 */       for (Expression expression : this.args) {
/* 479 */         expression.setEvaluatable(paramTableFilter, paramBoolean);
/*     */       }
/*     */     }
/* 482 */     super.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 487 */     switch (this.type) {
/*     */       case NTILE:
/*     */       case ROW_NUMBER:
/*     */       case RANK:
/*     */       case DENSE_RANK:
/* 492 */         return TypeInfo.TYPE_BIGINT;
/*     */       case RATIO_TO_REPORT:
/*     */       case PERCENT_RANK:
/*     */       case CUME_DIST:
/* 496 */         return TypeInfo.TYPE_DOUBLE;
/*     */       case LEAD:
/*     */       case LAG:
/*     */       case FIRST_VALUE:
/*     */       case LAST_VALUE:
/*     */       case NTH_VALUE:
/* 502 */         return this.args[0].getType();
/*     */     } 
/* 504 */     throw DbException.getInternalError("type=" + this.type);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 510 */     paramStringBuilder.append(this.type.getSQL()).append('(');
/* 511 */     if (this.args != null) {
/* 512 */       writeExpressions(paramStringBuilder, this.args, paramInt);
/*     */     }
/* 514 */     paramStringBuilder.append(')');
/* 515 */     if (this.fromLast && this.type == WindowFunctionType.NTH_VALUE) {
/* 516 */       paramStringBuilder.append(" FROM LAST");
/*     */     }
/* 518 */     if (this.ignoreNulls) {
/* 519 */       switch (this.type) {
/*     */         case LEAD:
/*     */         case LAG:
/*     */         case FIRST_VALUE:
/*     */         case LAST_VALUE:
/*     */         case NTH_VALUE:
/* 525 */           paramStringBuilder.append(" IGNORE NULLS");
/*     */           break;
/*     */       } 
/*     */     
/*     */     }
/* 530 */     return appendTailConditions(paramStringBuilder, paramInt, this.type.requiresWindowOrdering());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 535 */     int i = 1;
/* 536 */     if (this.args != null) {
/* 537 */       for (Expression expression : this.args) {
/* 538 */         i += expression.getCost();
/*     */       }
/*     */     }
/* 541 */     return i;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\analysis\WindowFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */