/*     */ package org.h2.command.query;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.analysis.DataAnalysisOperation;
/*     */ import org.h2.expression.analysis.PartitionData;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueRow;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SelectGroups
/*     */ {
/*     */   final SessionLocal session;
/*     */   final ArrayList<Expression> expressions;
/*     */   Object[] currentGroupByExprData;
/*     */   
/*     */   private static final class Grouped
/*     */     extends SelectGroups
/*     */   {
/*     */     private final int[] groupIndex;
/*     */     private TreeMap<ValueRow, Object[]> groupByData;
/*     */     private ValueRow currentGroupsKey;
/*     */     private Iterator<Map.Entry<ValueRow, Object[]>> cursor;
/*     */     
/*     */     Grouped(SessionLocal param1SessionLocal, ArrayList<Expression> param1ArrayList, int[] param1ArrayOfint) {
/*  68 */       super(param1SessionLocal, param1ArrayList);
/*  69 */       this.groupIndex = param1ArrayOfint;
/*     */     }
/*     */ 
/*     */     
/*     */     public void reset() {
/*  74 */       super.reset();
/*  75 */       this.groupByData = (TreeMap)new TreeMap<>((Comparator<? super ValueRow>)this.session.getDatabase().getCompareMode());
/*  76 */       this.currentGroupsKey = null;
/*  77 */       this.cursor = null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void nextSource() {
/*  82 */       if (this.groupIndex == null) {
/*  83 */         this.currentGroupsKey = ValueRow.EMPTY;
/*     */       } else {
/*  85 */         Value[] arrayOfValue = new Value[this.groupIndex.length];
/*     */         
/*  87 */         for (byte b = 0; b < this.groupIndex.length; b++) {
/*  88 */           int i = this.groupIndex[b];
/*  89 */           Expression expression = this.expressions.get(i);
/*  90 */           arrayOfValue[b] = expression.getValue(this.session);
/*     */         } 
/*  92 */         this.currentGroupsKey = ValueRow.get(arrayOfValue);
/*     */       } 
/*  94 */       Object[] arrayOfObject = this.groupByData.get(this.currentGroupsKey);
/*  95 */       if (arrayOfObject == null) {
/*  96 */         arrayOfObject = createRow();
/*  97 */         this.groupByData.put(this.currentGroupsKey, arrayOfObject);
/*     */       } 
/*  99 */       this.currentGroupByExprData = arrayOfObject;
/* 100 */       this.currentGroupRowId++;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     void updateCurrentGroupExprData() {
/* 106 */       if (this.currentGroupsKey != null)
/*     */       {
/*     */         
/* 109 */         this.groupByData.put(this.currentGroupsKey, this.currentGroupByExprData);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void done() {
/* 115 */       super.done();
/* 116 */       if (this.groupIndex == null && this.groupByData.size() == 0) {
/* 117 */         this.groupByData.put(ValueRow.EMPTY, createRow());
/*     */       }
/* 119 */       this.cursor = this.groupByData.entrySet().iterator();
/*     */     }
/*     */ 
/*     */     
/*     */     public ValueRow next() {
/* 124 */       if (this.cursor.hasNext()) {
/* 125 */         Map.Entry entry = this.cursor.next();
/* 126 */         this.currentGroupByExprData = (Object[])entry.getValue();
/* 127 */         this.currentGroupRowId++;
/* 128 */         return (ValueRow)entry.getKey();
/*     */       } 
/* 130 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 135 */       this.cursor.remove();
/* 136 */       this.currentGroupByExprData = null;
/* 137 */       this.currentGroupRowId--;
/*     */     }
/*     */ 
/*     */     
/*     */     public void resetLazy() {
/* 142 */       super.resetLazy();
/* 143 */       this.currentGroupsKey = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class Plain
/*     */     extends SelectGroups
/*     */   {
/*     */     private ArrayList<Object[]> rows;
/*     */     
/*     */     private Iterator<Object[]> cursor;
/*     */ 
/*     */     
/*     */     Plain(SessionLocal param1SessionLocal, ArrayList<Expression> param1ArrayList) {
/* 157 */       super(param1SessionLocal, param1ArrayList);
/*     */     }
/*     */ 
/*     */     
/*     */     public void reset() {
/* 162 */       super.reset();
/* 163 */       this.rows = new ArrayList();
/* 164 */       this.cursor = null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void nextSource() {
/* 169 */       Object[] arrayOfObject = createRow();
/* 170 */       this.rows.add(arrayOfObject);
/* 171 */       this.currentGroupByExprData = arrayOfObject;
/* 172 */       this.currentGroupRowId++;
/*     */     }
/*     */ 
/*     */     
/*     */     void updateCurrentGroupExprData() {
/* 177 */       this.rows.set(this.rows.size() - 1, this.currentGroupByExprData);
/*     */     }
/*     */ 
/*     */     
/*     */     public void done() {
/* 182 */       super.done();
/* 183 */       this.cursor = this.rows.iterator();
/*     */     }
/*     */ 
/*     */     
/*     */     public ValueRow next() {
/* 188 */       if (this.cursor.hasNext()) {
/* 189 */         this.currentGroupByExprData = this.cursor.next();
/* 190 */         this.currentGroupRowId++;
/* 191 */         return ValueRow.EMPTY;
/*     */       } 
/* 193 */       return null;
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 216 */   private final HashMap<Expression, Integer> exprToIndexInGroupByData = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 221 */   private final HashMap<DataAnalysisOperation, PartitionData> windowData = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 226 */   private final HashMap<DataAnalysisOperation, TreeMap<Value, PartitionData>> windowPartitionData = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int currentGroupRowId;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SelectGroups getInstance(SessionLocal paramSessionLocal, ArrayList<Expression> paramArrayList, boolean paramBoolean, int[] paramArrayOfint) {
/* 248 */     return paramBoolean ? new Grouped(paramSessionLocal, paramArrayList, paramArrayOfint) : new Plain(paramSessionLocal, paramArrayList);
/*     */   }
/*     */   
/*     */   SelectGroups(SessionLocal paramSessionLocal, ArrayList<Expression> paramArrayList) {
/* 252 */     this.session = paramSessionLocal;
/* 253 */     this.expressions = paramArrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCurrentGroup() {
/* 263 */     return (this.currentGroupByExprData != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Object getCurrentGroupExprData(Expression paramExpression) {
/* 274 */     Integer integer = this.exprToIndexInGroupByData.get(paramExpression);
/* 275 */     if (integer == null) {
/* 276 */       return null;
/*     */     }
/* 278 */     return this.currentGroupByExprData[integer.intValue()];
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
/*     */   public final void setCurrentGroupExprData(Expression paramExpression, Object paramObject) {
/* 290 */     Integer integer = this.exprToIndexInGroupByData.get(paramExpression);
/* 291 */     if (integer != null) {
/* 292 */       assert this.currentGroupByExprData[integer.intValue()] == null;
/* 293 */       this.currentGroupByExprData[integer.intValue()] = paramObject;
/*     */       return;
/*     */     } 
/* 296 */     integer = Integer.valueOf(this.exprToIndexInGroupByData.size());
/* 297 */     this.exprToIndexInGroupByData.put(paramExpression, integer);
/* 298 */     if (integer.intValue() >= this.currentGroupByExprData.length) {
/* 299 */       this.currentGroupByExprData = Arrays.copyOf(this.currentGroupByExprData, this.currentGroupByExprData.length * 2);
/* 300 */       updateCurrentGroupExprData();
/*     */     } 
/* 302 */     this.currentGroupByExprData[integer.intValue()] = paramObject;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final Object[] createRow() {
/* 311 */     return new Object[Math.max(this.exprToIndexInGroupByData.size(), this.expressions.size())];
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
/*     */   public final PartitionData getWindowExprData(DataAnalysisOperation paramDataAnalysisOperation, Value paramValue) {
/* 324 */     if (paramValue == null) {
/* 325 */       return this.windowData.get(paramDataAnalysisOperation);
/*     */     }
/* 327 */     TreeMap treeMap = this.windowPartitionData.get(paramDataAnalysisOperation);
/* 328 */     return (treeMap != null) ? (PartitionData)treeMap.get(paramValue) : null;
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
/*     */   public final void setWindowExprData(DataAnalysisOperation paramDataAnalysisOperation, Value paramValue, PartitionData paramPartitionData) {
/* 343 */     if (paramValue == null) {
/* 344 */       Object object = this.windowData.put(paramDataAnalysisOperation, paramPartitionData);
/* 345 */       assert object == null;
/*     */     } else {
/* 347 */       TreeMap<Object, Object> treeMap = (TreeMap)this.windowPartitionData.get(paramDataAnalysisOperation);
/* 348 */       if (treeMap == null) {
/* 349 */         treeMap = new TreeMap<>((Comparator<?>)this.session.getDatabase().getCompareMode());
/* 350 */         this.windowPartitionData.put(paramDataAnalysisOperation, treeMap);
/*     */       } 
/* 352 */       treeMap.put(paramValue, paramPartitionData);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract void updateCurrentGroupExprData();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCurrentGroupRowId() {
/* 368 */     return this.currentGroupRowId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 375 */     this.currentGroupByExprData = null;
/* 376 */     this.exprToIndexInGroupByData.clear();
/* 377 */     this.windowData.clear();
/* 378 */     this.windowPartitionData.clear();
/* 379 */     this.currentGroupRowId = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void nextSource();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void done() {
/* 392 */     this.currentGroupRowId = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ValueRow next();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove() {
/* 408 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetLazy() {
/* 415 */     this.currentGroupByExprData = null;
/* 416 */     this.currentGroupRowId = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void nextLazyGroup() {
/* 423 */     this.currentGroupByExprData = new Object[Math.max(this.exprToIndexInGroupByData.size(), this.expressions.size())];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void nextLazyRow() {
/* 430 */     this.currentGroupRowId++;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\query\SelectGroups.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */