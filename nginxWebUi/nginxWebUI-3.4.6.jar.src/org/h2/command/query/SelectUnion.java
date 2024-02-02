/*     */ package org.h2.command.query;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.LazyResult;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.ResultTarget;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SelectUnion
/*     */   extends Query
/*     */ {
/*     */   private final UnionType unionType;
/*     */   final Query left;
/*     */   final Query right;
/*     */   private boolean isForUpdate;
/*     */   
/*     */   public enum UnionType
/*     */   {
/*  39 */     UNION,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  44 */     UNION_ALL,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  49 */     EXCEPT,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  54 */     INTERSECT;
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
/*     */   public SelectUnion(SessionLocal paramSessionLocal, UnionType paramUnionType, Query paramQuery1, Query paramQuery2) {
/*  72 */     super(paramSessionLocal);
/*  73 */     this.unionType = paramUnionType;
/*  74 */     this.left = paramQuery1;
/*  75 */     this.right = paramQuery2;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUnion() {
/*  80 */     return true;
/*     */   }
/*     */   
/*     */   public UnionType getUnionType() {
/*  84 */     return this.unionType;
/*     */   }
/*     */   
/*     */   public Query getLeft() {
/*  88 */     return this.left;
/*     */   }
/*     */   
/*     */   public Query getRight() {
/*  92 */     return this.right;
/*     */   }
/*     */   
/*     */   private Value[] convert(Value[] paramArrayOfValue, int paramInt) {
/*     */     Value[] arrayOfValue;
/*  97 */     if (paramInt == paramArrayOfValue.length) {
/*     */       
/*  99 */       arrayOfValue = paramArrayOfValue;
/*     */     }
/*     */     else {
/*     */       
/* 103 */       arrayOfValue = new Value[paramInt];
/*     */     } 
/* 105 */     for (byte b = 0; b < paramInt; b++) {
/* 106 */       Expression expression = this.expressions.get(b);
/* 107 */       arrayOfValue[b] = paramArrayOfValue[b].convertTo(expression.getType(), (CastDataProvider)this.session);
/*     */     } 
/* 109 */     return arrayOfValue;
/*     */   }
/*     */   
/*     */   public LocalResult getEmptyResult() {
/* 113 */     int i = this.left.getColumnCount();
/* 114 */     return createLocalResult(i);
/*     */   }
/*     */   
/*     */   protected ResultInterface queryWithoutCache(long paramLong, ResultTarget paramResultTarget) {
/*     */     LocalResult localResult2;
/* 119 */     Query.OffsetFetch offsetFetch = getOffsetFetch(paramLong);
/* 120 */     long l1 = offsetFetch.offset;
/* 121 */     long l2 = offsetFetch.fetch;
/* 122 */     boolean bool = offsetFetch.fetchPercent;
/* 123 */     Database database = this.session.getDatabase();
/* 124 */     if ((database.getSettings()).optimizeInsertFromSelect && 
/* 125 */       this.unionType == UnionType.UNION_ALL && paramResultTarget != null && 
/* 126 */       this.sort == null && !this.distinct && l2 < 0L && l1 == 0L) {
/* 127 */       this.left.query(0L, paramResultTarget);
/* 128 */       this.right.query(0L, paramResultTarget);
/* 129 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 133 */     int i = this.left.getColumnCount();
/* 134 */     if (this.session.isLazyQueryExecution() && this.unionType == UnionType.UNION_ALL && !this.distinct && this.sort == null && !this.randomAccessResult && !this.isForUpdate && l1 == 0L && !bool && !this.withTies && 
/*     */       
/* 136 */       isReadOnly())
/*     */     {
/* 138 */       if (l2 != 0L) {
/* 139 */         LazyResultUnion lazyResultUnion = new LazyResultUnion(this.expressionArray, i);
/* 140 */         if (l2 > 0L) {
/* 141 */           lazyResultUnion.setLimit(l2);
/*     */         }
/* 143 */         return (ResultInterface)lazyResultUnion;
/*     */       } 
/*     */     }
/* 146 */     LocalResult localResult1 = createLocalResult(i);
/* 147 */     if (this.sort != null) {
/* 148 */       localResult1.setSortOrder(this.sort);
/*     */     }
/* 150 */     if (this.distinct) {
/* 151 */       this.left.setDistinctIfPossible();
/* 152 */       this.right.setDistinctIfPossible();
/* 153 */       localResult1.setDistinct();
/*     */     } 
/* 155 */     switch (this.unionType) {
/*     */       case UNION:
/*     */       case EXCEPT:
/* 158 */         this.left.setDistinctIfPossible();
/* 159 */         this.right.setDistinctIfPossible();
/* 160 */         localResult1.setDistinct();
/*     */         break;
/*     */       case UNION_ALL:
/*     */         break;
/*     */       case INTERSECT:
/* 165 */         this.left.setDistinctIfPossible();
/* 166 */         this.right.setDistinctIfPossible();
/*     */         break;
/*     */       default:
/* 169 */         throw DbException.getInternalError("type=" + this.unionType);
/*     */     } 
/* 171 */     ResultInterface resultInterface1 = this.left.query(0L);
/* 172 */     ResultInterface resultInterface2 = this.right.query(0L);
/* 173 */     resultInterface1.reset();
/* 174 */     resultInterface2.reset();
/* 175 */     switch (this.unionType) {
/*     */       case UNION:
/*     */       case UNION_ALL:
/* 178 */         while (resultInterface1.next()) {
/* 179 */           localResult1.addRow(convert(resultInterface1.currentRow(), i));
/*     */         }
/* 181 */         while (resultInterface2.next()) {
/* 182 */           localResult1.addRow(convert(resultInterface2.currentRow(), i));
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
/* 213 */         resultInterface1.close();
/* 214 */         resultInterface2.close();
/* 215 */         return (ResultInterface)finishResult(localResult1, l1, l2, bool, paramResultTarget);case EXCEPT: while (resultInterface1.next()) localResult1.addRow(convert(resultInterface1.currentRow(), i));  while (resultInterface2.next()) localResult1.removeDistinct(convert(resultInterface2.currentRow(), i));  resultInterface1.close(); resultInterface2.close(); return (ResultInterface)finishResult(localResult1, l1, l2, bool, paramResultTarget);case INTERSECT: localResult2 = createLocalResult(i); localResult2.setDistinct(); while (resultInterface1.next()) localResult2.addRow(convert(resultInterface1.currentRow(), i));  while (resultInterface2.next()) { Value[] arrayOfValue = convert(resultInterface2.currentRow(), i); if (localResult2.containsDistinct(arrayOfValue)) localResult1.addRow(arrayOfValue);  }  localResult2.close(); resultInterface1.close(); resultInterface2.close(); return (ResultInterface)finishResult(localResult1, l1, l2, bool, paramResultTarget);
/*     */     } 
/*     */     throw DbException.getInternalError("type=" + this.unionType);
/*     */   } private LocalResult createLocalResult(int paramInt) {
/* 219 */     return new LocalResult(this.session, this.expressionArray, paramInt, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void init() {
/* 224 */     if (this.checkInit) {
/* 225 */       throw DbException.getInternalError();
/*     */     }
/* 227 */     this.checkInit = true;
/* 228 */     this.left.init();
/* 229 */     this.right.init();
/* 230 */     int i = this.left.getColumnCount();
/* 231 */     if (i != this.right.getColumnCount()) {
/* 232 */       throw DbException.get(21002);
/*     */     }
/* 234 */     ArrayList<Expression> arrayList = this.left.getExpressions();
/*     */ 
/*     */     
/* 237 */     this.expressions = new ArrayList<>(i);
/* 238 */     for (byte b = 0; b < i; b++) {
/* 239 */       Expression expression = arrayList.get(b);
/* 240 */       this.expressions.add(expression);
/*     */     } 
/* 242 */     this.visibleColumnCount = i;
/* 243 */     if (this.withTies && !hasOrder()) {
/* 244 */       throw DbException.get(90122);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepare() {
/* 250 */     if (this.isPrepared) {
/*     */       return;
/*     */     }
/*     */     
/* 254 */     if (!this.checkInit) {
/* 255 */       throw DbException.getInternalError("not initialized");
/*     */     }
/* 257 */     this.isPrepared = true;
/* 258 */     this.left.prepare();
/* 259 */     this.right.prepare();
/* 260 */     int i = this.left.getColumnCount();
/*     */     
/* 262 */     this.expressions = new ArrayList<>(i);
/* 263 */     ArrayList<Expression> arrayList1 = this.left.getExpressions();
/* 264 */     ArrayList<Expression> arrayList2 = this.right.getExpressions();
/* 265 */     for (byte b = 0; b < i; b++) {
/* 266 */       Expression expression1 = arrayList1.get(b);
/* 267 */       Expression expression2 = arrayList2.get(b);
/* 268 */       Column column = new Column(expression1.getAlias(this.session, b), TypeInfo.getHigherType(expression1.getType(), expression2.getType()));
/* 269 */       ExpressionColumn expressionColumn = new ExpressionColumn(this.session.getDatabase(), column);
/* 270 */       this.expressions.add(expressionColumn);
/*     */     } 
/* 272 */     if (this.orderList != null && 
/* 273 */       initOrder((ArrayList<String>)null, true, (ArrayList<TableFilter>)null)) {
/* 274 */       prepareOrder(this.orderList, this.expressions.size());
/* 275 */       cleanupOrder();
/*     */     } 
/*     */     
/* 278 */     this.resultColumnCount = this.expressions.size();
/* 279 */     this.expressionArray = this.expressions.<Expression>toArray(new Expression[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getCost() {
/* 284 */     return this.left.getCost() + this.right.getCost();
/*     */   }
/*     */ 
/*     */   
/*     */   public HashSet<Table> getTables() {
/* 289 */     HashSet<Table> hashSet = this.left.getTables();
/* 290 */     hashSet.addAll(this.right.getTables());
/* 291 */     return hashSet;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setForUpdate(boolean paramBoolean) {
/* 296 */     this.left.setForUpdate(paramBoolean);
/* 297 */     this.right.setForUpdate(paramBoolean);
/* 298 */     this.isForUpdate = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt) {
/* 303 */     this.left.mapColumns(paramColumnResolver, paramInt);
/* 304 */     this.right.mapColumns(paramColumnResolver, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 309 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/* 310 */     this.right.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addGlobalCondition(Parameter paramParameter, int paramInt1, int paramInt2) {
/* 316 */     addParameter(paramParameter);
/* 317 */     switch (this.unionType) {
/*     */       case UNION:
/*     */       case UNION_ALL:
/*     */       case INTERSECT:
/* 321 */         this.left.addGlobalCondition(paramParameter, paramInt1, paramInt2);
/* 322 */         this.right.addGlobalCondition(paramParameter, paramInt1, paramInt2);
/*     */         return;
/*     */       
/*     */       case EXCEPT:
/* 326 */         this.left.addGlobalCondition(paramParameter, paramInt1, paramInt2);
/*     */         return;
/*     */     } 
/*     */     
/* 330 */     throw DbException.getInternalError("type=" + this.unionType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPlanSQL(int paramInt) {
/* 336 */     StringBuilder stringBuilder = new StringBuilder();
/* 337 */     stringBuilder.append('(').append(this.left.getPlanSQL(paramInt)).append(')');
/* 338 */     switch (this.unionType) {
/*     */       case UNION_ALL:
/* 340 */         stringBuilder.append("\nUNION ALL\n");
/*     */         break;
/*     */       case UNION:
/* 343 */         stringBuilder.append("\nUNION\n");
/*     */         break;
/*     */       case INTERSECT:
/* 346 */         stringBuilder.append("\nINTERSECT\n");
/*     */         break;
/*     */       case EXCEPT:
/* 349 */         stringBuilder.append("\nEXCEPT\n");
/*     */         break;
/*     */       default:
/* 352 */         throw DbException.getInternalError("type=" + this.unionType);
/*     */     } 
/* 354 */     stringBuilder.append('(').append(this.right.getPlanSQL(paramInt)).append(')');
/* 355 */     appendEndOfQueryToSQL(stringBuilder, paramInt, this.expressions.<Expression>toArray(new Expression[0]));
/* 356 */     if (this.isForUpdate) {
/* 357 */       stringBuilder.append("\nFOR UPDATE");
/*     */     }
/* 359 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 364 */     return (this.left.isEverything(paramExpressionVisitor) && this.right.isEverything(paramExpressionVisitor));
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 369 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/* 370 */     this.right.updateAggregate(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void fireBeforeSelectTriggers() {
/* 375 */     this.left.fireBeforeSelectTriggers();
/* 376 */     this.right.fireBeforeSelectTriggers();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean allowGlobalConditions() {
/* 381 */     return (this.left.allowGlobalConditions() && this.right.allowGlobalConditions());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConstantQuery() {
/* 386 */     return (super.isConstantQuery() && this.left.isConstantQuery() && this.right.isConstantQuery());
/*     */   }
/*     */ 
/*     */   
/*     */   private final class LazyResultUnion
/*     */     extends LazyResult
/*     */   {
/*     */     int columnCount;
/*     */     
/*     */     ResultInterface l;
/*     */     ResultInterface r;
/*     */     boolean leftDone;
/*     */     boolean rightDone;
/*     */     
/*     */     LazyResultUnion(Expression[] param1ArrayOfExpression, int param1Int) {
/* 401 */       super(SelectUnion.this.getSession(), param1ArrayOfExpression);
/* 402 */       this.columnCount = param1Int;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getVisibleColumnCount() {
/* 407 */       return this.columnCount;
/*     */     }
/*     */ 
/*     */     
/*     */     protected Value[] fetchNextRow() {
/* 412 */       if (this.rightDone) {
/* 413 */         return null;
/*     */       }
/* 415 */       if (!this.leftDone) {
/* 416 */         if (this.l == null) {
/* 417 */           this.l = SelectUnion.this.left.query(0L);
/* 418 */           this.l.reset();
/*     */         } 
/* 420 */         if (this.l.next()) {
/* 421 */           return this.l.currentRow();
/*     */         }
/* 423 */         this.leftDone = true;
/*     */       } 
/* 425 */       if (this.r == null) {
/* 426 */         this.r = SelectUnion.this.right.query(0L);
/* 427 */         this.r.reset();
/*     */       } 
/* 429 */       if (this.r.next()) {
/* 430 */         return this.r.currentRow();
/*     */       }
/* 432 */       this.rightDone = true;
/* 433 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() {
/* 438 */       super.close();
/* 439 */       if (this.l != null) {
/* 440 */         this.l.close();
/*     */       }
/* 442 */       if (this.r != null) {
/* 443 */         this.r.close();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void reset() {
/* 449 */       super.reset();
/* 450 */       if (this.l != null) {
/* 451 */         this.l.reset();
/*     */       }
/* 453 */       if (this.r != null) {
/* 454 */         this.r.reset();
/*     */       }
/* 456 */       this.leftDone = false;
/* 457 */       this.rightDone = false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\query\SelectUnion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */