/*     */ package org.h2.expression.analysis;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import org.h2.command.query.QueryOrderBy;
/*     */ import org.h2.command.query.Select;
/*     */ import org.h2.command.query.SelectGroups;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInteger;
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
/*     */ public abstract class DataAnalysisOperation
/*     */   extends Expression
/*     */ {
/*     */   public static final int STAGE_RESET = 0;
/*     */   public static final int STAGE_GROUP = 1;
/*     */   public static final int STAGE_WINDOW = 2;
/*     */   protected final Select select;
/*     */   protected Window over;
/*     */   protected SortOrder overOrderBySort;
/*     */   private int numFrameExpressions;
/*     */   private int lastGroupRowId;
/*     */   
/*     */   protected static SortOrder createOrder(SessionLocal paramSessionLocal, ArrayList<QueryOrderBy> paramArrayList, int paramInt) {
/*  77 */     int i = paramArrayList.size();
/*  78 */     int[] arrayOfInt1 = new int[i];
/*  79 */     int[] arrayOfInt2 = new int[i];
/*  80 */     for (byte b = 0; b < i; b++) {
/*  81 */       QueryOrderBy queryOrderBy = paramArrayList.get(b);
/*  82 */       arrayOfInt1[b] = b + paramInt;
/*  83 */       arrayOfInt2[b] = queryOrderBy.sortType;
/*     */     } 
/*  85 */     return new SortOrder(paramSessionLocal, arrayOfInt1, arrayOfInt2, null);
/*     */   }
/*     */   
/*     */   protected DataAnalysisOperation(Select paramSelect) {
/*  89 */     this.select = paramSelect;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOverCondition(Window paramWindow) {
/*  99 */     this.over = paramWindow;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean isAggregate();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SortOrder getOverOrderBySort() {
/* 116 */     return this.overOrderBySort;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 121 */     if (this.over != null) {
/* 122 */       if (paramInt2 != 0) {
/* 123 */         throw DbException.get(90054, getTraceSQL());
/*     */       }
/* 125 */       paramInt2 = 1;
/*     */     } else {
/* 127 */       if (paramInt2 == 2) {
/* 128 */         throw DbException.get(90054, getTraceSQL());
/*     */       }
/* 130 */       paramInt2 = 2;
/*     */     } 
/* 132 */     mapColumnsAnalysis(paramColumnResolver, paramInt1, paramInt2);
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
/*     */   protected void mapColumnsAnalysis(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 146 */     if (this.over != null) {
/* 147 */       this.over.mapColumns(paramColumnResolver, paramInt1);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 153 */     if (this.over != null) {
/* 154 */       this.over.optimize(paramSessionLocal);
/* 155 */       ArrayList<QueryOrderBy> arrayList = this.over.getOrderBy();
/* 156 */       if (arrayList != null) {
/* 157 */         this.overOrderBySort = createOrder(paramSessionLocal, arrayList, getNumExpressions());
/* 158 */       } else if (!isAggregate()) {
/* 159 */         this.overOrderBySort = new SortOrder(paramSessionLocal, new int[getNumExpressions()]);
/*     */       } 
/* 161 */       WindowFrame windowFrame = this.over.getWindowFrame();
/* 162 */       if (windowFrame != null) {
/* 163 */         int i = getNumExpressions();
/* 164 */         int j = 0;
/* 165 */         if (arrayList != null) {
/* 166 */           j = arrayList.size();
/* 167 */           i += j;
/*     */         } 
/* 169 */         byte b = 0;
/* 170 */         WindowFrameBound windowFrameBound = windowFrame.getStarting();
/* 171 */         if (windowFrameBound.isParameterized()) {
/* 172 */           checkOrderBy(windowFrame.getUnits(), j);
/* 173 */           if (windowFrameBound.isVariable()) {
/* 174 */             windowFrameBound.setExpressionIndex(i);
/* 175 */             b++;
/*     */           } 
/*     */         } 
/* 178 */         windowFrameBound = windowFrame.getFollowing();
/* 179 */         if (windowFrameBound != null && windowFrameBound.isParameterized()) {
/* 180 */           checkOrderBy(windowFrame.getUnits(), j);
/* 181 */           if (windowFrameBound.isVariable()) {
/* 182 */             windowFrameBound.setExpressionIndex(i + b);
/* 183 */             b++;
/*     */           } 
/*     */         } 
/* 186 */         this.numFrameExpressions = b;
/*     */       } 
/*     */     } 
/* 189 */     return this;
/*     */   }
/*     */   
/*     */   private void checkOrderBy(WindowFrameUnits paramWindowFrameUnits, int paramInt) {
/* 193 */     switch (paramWindowFrameUnits) {
/*     */       case RANGE:
/* 195 */         if (paramInt != 1) {
/* 196 */           String str = getTraceSQL();
/* 197 */           throw DbException.getSyntaxError(str, str.length() - 1, "exactly one sort key is required for RANGE units");
/*     */         } 
/*     */         break;
/*     */       
/*     */       case GROUPS:
/* 202 */         if (paramInt < 1) {
/* 203 */           String str = getTraceSQL();
/* 204 */           throw DbException.getSyntaxError(str, str.length() - 1, "a sort key is required for GROUPS units");
/*     */         } 
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 214 */     if (this.over != null) {
/* 215 */       this.over.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public final void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 221 */     if (paramInt == 0) {
/* 222 */       updateGroupAggregates(paramSessionLocal, 0);
/* 223 */       this.lastGroupRowId = 0;
/*     */       return;
/*     */     } 
/* 226 */     boolean bool = (paramInt == 2) ? true : false;
/* 227 */     if (bool != ((this.over != null) ? true : false)) {
/* 228 */       if (!bool && this.select.isWindowQuery()) {
/* 229 */         updateGroupAggregates(paramSessionLocal, paramInt);
/*     */       }
/*     */       return;
/*     */     } 
/* 233 */     SelectGroups selectGroups = this.select.getGroupDataIfCurrent(bool);
/* 234 */     if (selectGroups == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 239 */     int i = selectGroups.getCurrentGroupRowId();
/* 240 */     if (this.lastGroupRowId == i) {
/*     */       return;
/*     */     }
/*     */     
/* 244 */     this.lastGroupRowId = i;
/*     */     
/* 246 */     if (this.over != null && 
/* 247 */       !this.select.isGroupQuery()) {
/* 248 */       this.over.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */     
/* 251 */     updateAggregate(paramSessionLocal, selectGroups, i);
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
/*     */   protected abstract void updateAggregate(SessionLocal paramSessionLocal, SelectGroups paramSelectGroups, int paramInt);
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
/*     */   protected void updateGroupAggregates(SessionLocal paramSessionLocal, int paramInt) {
/* 276 */     if (this.over != null) {
/* 277 */       this.over.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract int getNumExpressions();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getNumFrameExpressions() {
/* 294 */     return this.numFrameExpressions;
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
/*     */   protected abstract void rememberExpressions(SessionLocal paramSessionLocal, Value[] paramArrayOfValue);
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
/*     */   protected Object getWindowData(SessionLocal paramSessionLocal, SelectGroups paramSelectGroups, boolean paramBoolean) {
/*     */     Object object;
/* 320 */     Value value = this.over.getCurrentKey(paramSessionLocal);
/* 321 */     PartitionData partitionData = paramSelectGroups.getWindowExprData(this, value);
/* 322 */     if (partitionData == null) {
/* 323 */       object = paramBoolean ? new ArrayList() : createAggregateData();
/* 324 */       paramSelectGroups.setWindowExprData(this, value, new PartitionData(object));
/*     */     } else {
/* 326 */       object = partitionData.getData();
/*     */     } 
/* 328 */     return object;
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
/*     */   
/*     */   protected Object getGroupData(SelectGroups paramSelectGroups, boolean paramBoolean) {
/* 343 */     Object object = paramSelectGroups.getCurrentGroupExprData(this);
/* 344 */     if (object == null) {
/* 345 */       if (paramBoolean) {
/* 346 */         return null;
/*     */       }
/* 348 */       object = createAggregateData();
/* 349 */       paramSelectGroups.setCurrentGroupExprData(this, object);
/*     */     } 
/* 351 */     return object;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract Object createAggregateData();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 363 */     if (this.over == null) {
/* 364 */       return true;
/*     */     }
/* 366 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 0:
/*     */       case 1:
/*     */       case 2:
/*     */       case 8:
/*     */       case 11:
/* 372 */         return false;
/*     */     } 
/* 374 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/* 380 */     SelectGroups selectGroups = this.select.getGroupDataIfCurrent((this.over != null));
/* 381 */     if (selectGroups == null) {
/* 382 */       throw DbException.get(90054, getTraceSQL());
/*     */     }
/* 384 */     return (this.over == null) ? getAggregatedValue(paramSessionLocal, getGroupData(selectGroups, true)) : 
/* 385 */       getWindowResult(paramSessionLocal, selectGroups);
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
/*     */   
/*     */   private Value getWindowResult(SessionLocal paramSessionLocal, SelectGroups paramSelectGroups) {
/*     */     Object object;
/* 401 */     boolean bool = this.over.isOrdered();
/* 402 */     Value value1 = this.over.getCurrentKey(paramSessionLocal);
/* 403 */     PartitionData partitionData = paramSelectGroups.getWindowExprData(this, value1);
/* 404 */     if (partitionData == null) {
/*     */       
/* 406 */       object = bool ? new ArrayList() : createAggregateData();
/* 407 */       partitionData = new PartitionData(object);
/* 408 */       paramSelectGroups.setWindowExprData(this, value1, partitionData);
/*     */     } else {
/* 410 */       object = partitionData.getData();
/*     */     } 
/* 412 */     if (bool || !isAggregate()) {
/* 413 */       Value value = getOrderedResult(paramSessionLocal, paramSelectGroups, partitionData, object);
/* 414 */       if (value == null) {
/* 415 */         return getAggregatedValue(paramSessionLocal, null);
/*     */       }
/* 417 */       return value;
/*     */     } 
/*     */     
/* 420 */     Value value2 = partitionData.getResult();
/* 421 */     if (value2 == null) {
/* 422 */       value2 = getAggregatedValue(paramSessionLocal, object);
/* 423 */       partitionData.setResult(value2);
/*     */     } 
/* 425 */     return value2;
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
/*     */   
/*     */   protected abstract Value getAggregatedValue(SessionLocal paramSessionLocal, Object paramObject);
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
/*     */   protected void updateOrderedAggregate(SessionLocal paramSessionLocal, SelectGroups paramSelectGroups, int paramInt, ArrayList<QueryOrderBy> paramArrayList) {
/* 453 */     int i = getNumExpressions();
/* 454 */     byte b1 = (paramArrayList != null) ? paramArrayList.size() : 0;
/* 455 */     int j = getNumFrameExpressions();
/* 456 */     Value[] arrayOfValue = new Value[i + b1 + j + 1];
/* 457 */     rememberExpressions(paramSessionLocal, arrayOfValue);
/* 458 */     for (byte b2 = 0; b2 < b1; b2++) {
/*     */       
/* 460 */       QueryOrderBy queryOrderBy = paramArrayList.get(b2);
/* 461 */       arrayOfValue[i++] = queryOrderBy.expression.getValue(paramSessionLocal);
/*     */     } 
/* 463 */     if (j > 0) {
/* 464 */       WindowFrame windowFrame = this.over.getWindowFrame();
/* 465 */       WindowFrameBound windowFrameBound = windowFrame.getStarting();
/* 466 */       if (windowFrameBound.isVariable()) {
/* 467 */         arrayOfValue[i++] = windowFrameBound.getValue().getValue(paramSessionLocal);
/*     */       }
/* 469 */       windowFrameBound = windowFrame.getFollowing();
/* 470 */       if (windowFrameBound != null && windowFrameBound.isVariable()) {
/* 471 */         arrayOfValue[i++] = windowFrameBound.getValue().getValue(paramSessionLocal);
/*     */       }
/*     */     } 
/* 474 */     arrayOfValue[i] = (Value)ValueInteger.get(paramInt);
/*     */     
/* 476 */     ArrayList<Value[]> arrayList = (ArrayList)getWindowData(paramSessionLocal, paramSelectGroups, true);
/* 477 */     arrayList.add(arrayOfValue);
/*     */   }
/*     */ 
/*     */   
/*     */   private Value getOrderedResult(SessionLocal paramSessionLocal, SelectGroups paramSelectGroups, PartitionData paramPartitionData, Object paramObject) {
/* 482 */     HashMap<Integer, Value> hashMap = paramPartitionData.getOrderedResult();
/* 483 */     if (hashMap == null) {
/* 484 */       hashMap = new HashMap<>();
/*     */       
/* 486 */       ArrayList<Value[]> arrayList = (ArrayList)paramObject;
/* 487 */       int i = getNumExpressions();
/* 488 */       ArrayList<QueryOrderBy> arrayList1 = this.over.getOrderBy();
/* 489 */       if (arrayList1 != null) {
/* 490 */         i += arrayList1.size();
/* 491 */         arrayList.sort((Comparator)this.overOrderBySort);
/*     */       } 
/* 493 */       i += getNumFrameExpressions();
/* 494 */       getOrderedResultLoop(paramSessionLocal, hashMap, arrayList, i);
/* 495 */       paramPartitionData.setOrderedResult(hashMap);
/*     */     } 
/* 497 */     return hashMap.get(Integer.valueOf(paramSelectGroups.getCurrentGroupRowId()));
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void getOrderedResultLoop(SessionLocal paramSessionLocal, HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt);
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
/*     */   protected StringBuilder appendTailConditions(StringBuilder paramStringBuilder, int paramInt, boolean paramBoolean) {
/* 529 */     if (this.over != null) {
/* 530 */       paramStringBuilder.append(' ');
/* 531 */       this.over.getSQL(paramStringBuilder, paramInt, paramBoolean);
/*     */     } 
/* 533 */     return paramStringBuilder;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\analysis\DataAnalysisOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */