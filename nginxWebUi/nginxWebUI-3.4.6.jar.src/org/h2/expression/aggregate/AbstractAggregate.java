/*     */ package org.h2.expression.aggregate;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import org.h2.command.query.Select;
/*     */ import org.h2.command.query.SelectGroups;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.analysis.DataAnalysisOperation;
/*     */ import org.h2.expression.analysis.WindowFrame;
/*     */ import org.h2.expression.analysis.WindowFrameBound;
/*     */ import org.h2.expression.analysis.WindowFrameBoundType;
/*     */ import org.h2.expression.analysis.WindowFrameExclusion;
/*     */ import org.h2.expression.analysis.WindowFrameUnits;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
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
/*     */ public abstract class AbstractAggregate
/*     */   extends DataAnalysisOperation
/*     */ {
/*     */   protected final boolean distinct;
/*     */   protected final Expression[] args;
/*     */   protected Expression filterCondition;
/*     */   protected TypeInfo type;
/*     */   
/*     */   AbstractAggregate(Select paramSelect, Expression[] paramArrayOfExpression, boolean paramBoolean) {
/*  53 */     super(paramSelect);
/*  54 */     this.args = paramArrayOfExpression;
/*  55 */     this.distinct = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isAggregate() {
/*  60 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFilterCondition(Expression paramExpression) {
/*  70 */     this.filterCondition = paramExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  75 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumnsAnalysis(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/*  80 */     for (Expression expression : this.args) {
/*  81 */       expression.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     }
/*  83 */     if (this.filterCondition != null) {
/*  84 */       this.filterCondition.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     }
/*  86 */     super.mapColumnsAnalysis(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  91 */     for (byte b = 0; b < this.args.length; b++) {
/*  92 */       this.args[b] = this.args[b].optimize(paramSessionLocal);
/*     */     }
/*  94 */     if (this.filterCondition != null) {
/*  95 */       this.filterCondition = this.filterCondition.optimizeCondition(paramSessionLocal);
/*     */     }
/*  97 */     return super.optimize(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 102 */     for (Expression expression : this.args) {
/* 103 */       expression.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     }
/* 105 */     if (this.filterCondition != null) {
/* 106 */       this.filterCondition.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     }
/* 108 */     super.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void getOrderedResultLoop(SessionLocal paramSessionLocal, HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt) {
/* 114 */     WindowFrame windowFrame = this.over.getWindowFrame();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     boolean bool = (windowFrame == null || (windowFrame.getUnits() != WindowFrameUnits.ROWS && windowFrame.getExclusion().isGroupOrNoOthers())) ? true : false;
/* 122 */     if (windowFrame == null) {
/* 123 */       aggregateFastPartition(paramSessionLocal, paramHashMap, paramArrayList, paramInt, bool);
/*     */       return;
/*     */     } 
/* 126 */     boolean bool1 = windowFrame.isVariableBounds();
/* 127 */     if (bool1) {
/* 128 */       bool1 = checkVariableBounds(windowFrame, paramArrayList);
/*     */     }
/* 130 */     if (bool1) {
/* 131 */       bool = false;
/* 132 */     } else if (windowFrame.getExclusion() == WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
/* 133 */       WindowFrameBound windowFrameBound = windowFrame.getFollowing();
/*     */       
/* 135 */       boolean bool2 = (windowFrameBound != null && windowFrameBound.getType() == WindowFrameBoundType.UNBOUNDED_FOLLOWING) ? true : false;
/* 136 */       if (windowFrame.getStarting().getType() == WindowFrameBoundType.UNBOUNDED_PRECEDING) {
/* 137 */         if (bool2) {
/* 138 */           aggregateWholePartition(paramSessionLocal, paramHashMap, paramArrayList, paramInt);
/*     */         } else {
/* 140 */           aggregateFastPartition(paramSessionLocal, paramHashMap, paramArrayList, paramInt, bool);
/*     */         } 
/*     */         return;
/*     */       } 
/* 144 */       if (bool2) {
/* 145 */         aggregateFastPartitionInReverse(paramSessionLocal, paramHashMap, paramArrayList, paramInt, bool);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 150 */     int i = paramArrayList.size(); int j;
/* 151 */     for (j = 0; j < i; ) {
/* 152 */       Object object = createAggregateData();
/* 153 */       Iterator<Value[]> iterator = WindowFrame.iterator(this.over, paramSessionLocal, paramArrayList, getOverOrderBySort(), j, false);
/* 154 */       while (iterator.hasNext()) {
/* 155 */         updateFromExpressions(paramSessionLocal, object, iterator.next());
/*     */       }
/* 157 */       Value value = getAggregatedValue(paramSessionLocal, object);
/* 158 */       j = processGroup(paramHashMap, value, paramArrayList, paramInt, j, i, bool);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean checkVariableBounds(WindowFrame paramWindowFrame, ArrayList<Value[]> paramArrayList) {
/* 163 */     int i = paramArrayList.size();
/* 164 */     WindowFrameBound windowFrameBound = paramWindowFrame.getStarting();
/* 165 */     if (windowFrameBound.isVariable()) {
/* 166 */       int j = windowFrameBound.getExpressionIndex();
/* 167 */       Value value = ((Value[])paramArrayList.get(0))[j];
/* 168 */       for (byte b = 1; b < i; b++) {
/* 169 */         if (!value.equals(((Value[])paramArrayList.get(b))[j])) {
/* 170 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 174 */     windowFrameBound = paramWindowFrame.getFollowing();
/* 175 */     if (windowFrameBound != null && windowFrameBound.isVariable()) {
/* 176 */       int j = windowFrameBound.getExpressionIndex();
/* 177 */       Value value = ((Value[])paramArrayList.get(0))[j];
/* 178 */       for (byte b = 1; b < i; b++) {
/* 179 */         if (!value.equals(((Value[])paramArrayList.get(b))[j])) {
/* 180 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 184 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void aggregateFastPartition(SessionLocal paramSessionLocal, HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt, boolean paramBoolean) {
/* 189 */     Object object = createAggregateData();
/* 190 */     int i = paramArrayList.size();
/* 191 */     int j = -1;
/* 192 */     Value value = null; int k;
/* 193 */     for (k = 0; k < i; ) {
/* 194 */       int m = WindowFrame.getEndIndex(this.over, paramSessionLocal, paramArrayList, getOverOrderBySort(), k);
/* 195 */       assert m >= j;
/* 196 */       if (m > j) {
/* 197 */         for (int n = j + 1; n <= m; n++) {
/* 198 */           updateFromExpressions(paramSessionLocal, object, paramArrayList.get(n));
/*     */         }
/* 200 */         j = m;
/* 201 */         value = getAggregatedValue(paramSessionLocal, object);
/* 202 */       } else if (value == null) {
/* 203 */         value = getAggregatedValue(paramSessionLocal, object);
/*     */       } 
/* 205 */       k = processGroup(paramHashMap, value, paramArrayList, paramInt, k, i, paramBoolean);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void aggregateFastPartitionInReverse(SessionLocal paramSessionLocal, HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt, boolean paramBoolean) {
/* 211 */     Object object = createAggregateData();
/* 212 */     int i = paramArrayList.size();
/* 213 */     Value value = null; int j;
/* 214 */     label30: for (j = i - 1; j >= 0; ) {
/* 215 */       int k = this.over.getWindowFrame().getStartIndex(paramSessionLocal, paramArrayList, getOverOrderBySort(), j);
/* 216 */       assert k <= i;
/* 217 */       if (k < i) {
/* 218 */         for (int m = i - 1; m >= k; m--) {
/* 219 */           updateFromExpressions(paramSessionLocal, object, paramArrayList.get(m));
/*     */         }
/* 221 */         i = k;
/* 222 */         value = getAggregatedValue(paramSessionLocal, object);
/* 223 */       } else if (value == null) {
/* 224 */         value = getAggregatedValue(paramSessionLocal, object);
/*     */       } 
/* 226 */       Value[] arrayOfValue1 = paramArrayList.get(j), arrayOfValue2 = arrayOfValue1;
/*     */       while (true) {
/* 228 */         paramHashMap.put(Integer.valueOf(arrayOfValue2[paramInt].getInt()), value);
/* 229 */         if (--j >= 0) { if (paramBoolean) { if (this.overOrderBySort
/* 230 */               .compare(arrayOfValue1, arrayOfValue2 = paramArrayList.get(j)) != 0)
/*     */               continue label30;  continue; }
/*     */            continue label30; }
/*     */         
/*     */         continue label30;
/*     */       } 
/* 236 */     }  } private int processGroup(HashMap<Integer, Value> paramHashMap, Value paramValue, ArrayList<Value[]> paramArrayList, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) { Value[] arrayOfValue1 = paramArrayList.get(paramInt2), arrayOfValue2 = arrayOfValue1;
/*     */     do {
/* 238 */       paramHashMap.put(Integer.valueOf(arrayOfValue2[paramInt1].getInt()), paramValue);
/* 239 */     } while (++paramInt2 < paramInt3 && paramBoolean && this.overOrderBySort
/* 240 */       .compare(arrayOfValue1, arrayOfValue2 = paramArrayList.get(paramInt2)) == 0);
/* 241 */     return paramInt2; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void aggregateWholePartition(SessionLocal paramSessionLocal, HashMap<Integer, Value> paramHashMap, ArrayList<Value[]> paramArrayList, int paramInt) {
/* 247 */     Object object = createAggregateData();
/* 248 */     for (Value[] arrayOfValue : paramArrayList) {
/* 249 */       updateFromExpressions(paramSessionLocal, object, arrayOfValue);
/*     */     }
/*     */     
/* 252 */     Value value = getAggregatedValue(paramSessionLocal, object);
/* 253 */     for (Value[] arrayOfValue : paramArrayList) {
/* 254 */       paramHashMap.put(Integer.valueOf(arrayOfValue[paramInt].getInt()), value);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void updateFromExpressions(SessionLocal paramSessionLocal, Object paramObject, Value[] paramArrayOfValue);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void updateAggregate(SessionLocal paramSessionLocal, SelectGroups paramSelectGroups, int paramInt) {
/* 272 */     if (this.filterCondition == null || this.filterCondition.getBooleanValue(paramSessionLocal)) {
/* 273 */       if (this.over != null) {
/* 274 */         if (this.over.isOrdered()) {
/* 275 */           updateOrderedAggregate(paramSessionLocal, paramSelectGroups, paramInt, this.over.getOrderBy());
/*     */         } else {
/* 277 */           updateAggregate(paramSessionLocal, getWindowData(paramSessionLocal, paramSelectGroups, false));
/*     */         } 
/*     */       } else {
/* 280 */         updateAggregate(paramSessionLocal, getGroupData(paramSelectGroups, false));
/*     */       } 
/* 282 */     } else if (this.over != null && this.over.isOrdered()) {
/* 283 */       updateOrderedAggregate(paramSessionLocal, paramSelectGroups, paramInt, this.over.getOrderBy());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void updateAggregate(SessionLocal paramSessionLocal, Object paramObject);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void updateGroupAggregates(SessionLocal paramSessionLocal, int paramInt) {
/* 299 */     if (this.filterCondition != null) {
/* 300 */       this.filterCondition.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/* 302 */     super.updateGroupAggregates(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   protected StringBuilder appendTailConditions(StringBuilder paramStringBuilder, int paramInt, boolean paramBoolean) {
/* 307 */     if (this.filterCondition != null) {
/* 308 */       paramStringBuilder.append(" FILTER (WHERE ");
/* 309 */       this.filterCondition.getUnenclosedSQL(paramStringBuilder, paramInt).append(')');
/*     */     } 
/* 311 */     return super.appendTailConditions(paramStringBuilder, paramInt, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 316 */     return this.args.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 321 */     return this.args[paramInt];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AbstractAggregate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */