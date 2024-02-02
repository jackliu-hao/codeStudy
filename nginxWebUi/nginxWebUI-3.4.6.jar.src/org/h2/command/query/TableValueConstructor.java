/*     */ package org.h2.command.query;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.ExpressionList;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.ResultTarget;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.table.TableValueConstructorTable;
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
/*     */ public class TableValueConstructor
/*     */   extends Query
/*     */ {
/*     */   private final ArrayList<ArrayList<Expression>> rows;
/*     */   TableValueConstructorTable table;
/*     */   private TableValueColumnResolver columnResolver;
/*     */   private double cost;
/*     */   
/*     */   public TableValueConstructor(SessionLocal paramSessionLocal, ArrayList<ArrayList<Expression>> paramArrayList) {
/*  60 */     super(paramSessionLocal);
/*  61 */     this.rows = paramArrayList;
/*  62 */     if ((this.visibleColumnCount = ((ArrayList)paramArrayList.get(0)).size()) > 16384) {
/*  63 */       throw DbException.get(54011, "16384");
/*     */     }
/*  65 */     for (ArrayList<Expression> arrayList : paramArrayList) {
/*  66 */       for (Expression expression : arrayList) {
/*  67 */         if (!expression.isConstant()) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */     } 
/*  72 */     createTable();
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
/*     */   public static void getVisibleResult(SessionLocal paramSessionLocal, ResultTarget paramResultTarget, Column[] paramArrayOfColumn, ArrayList<ArrayList<Expression>> paramArrayList) {
/*  89 */     int i = paramArrayOfColumn.length;
/*  90 */     for (ArrayList<Expression> arrayList : paramArrayList) {
/*  91 */       Value[] arrayOfValue = new Value[i];
/*  92 */       for (byte b = 0; b < i; b++) {
/*  93 */         arrayOfValue[b] = ((Expression)arrayList.get(b)).getValue(paramSessionLocal).convertTo(paramArrayOfColumn[b].getType(), (CastDataProvider)paramSessionLocal);
/*     */       }
/*  95 */       paramResultTarget.addRow(arrayOfValue);
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
/*     */   public static void getValuesSQL(StringBuilder paramStringBuilder, int paramInt, ArrayList<ArrayList<Expression>> paramArrayList) {
/* 110 */     paramStringBuilder.append("VALUES ");
/* 111 */     int i = paramArrayList.size();
/* 112 */     for (byte b = 0; b < i; b++) {
/* 113 */       if (b > 0) {
/* 114 */         paramStringBuilder.append(", ");
/*     */       }
/* 116 */       Expression.writeExpressions(paramStringBuilder.append('('), paramArrayList.get(b), paramInt).append(')');
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUnion() {
/* 122 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ResultInterface queryWithoutCache(long paramLong, ResultTarget paramResultTarget) {
/* 127 */     Query.OffsetFetch offsetFetch = getOffsetFetch(paramLong);
/* 128 */     long l1 = offsetFetch.offset;
/* 129 */     long l2 = offsetFetch.fetch;
/* 130 */     boolean bool = offsetFetch.fetchPercent;
/* 131 */     int i = this.visibleColumnCount, j = this.resultColumnCount;
/* 132 */     LocalResult localResult = new LocalResult(this.session, this.expressionArray, i, j);
/* 133 */     if (this.sort != null) {
/* 134 */       localResult.setSortOrder(this.sort);
/*     */     }
/* 136 */     if (this.distinct) {
/* 137 */       localResult.setDistinct();
/*     */     }
/* 139 */     Column[] arrayOfColumn = this.table.getColumns();
/* 140 */     if (i == j) {
/* 141 */       getVisibleResult(this.session, (ResultTarget)localResult, arrayOfColumn, this.rows);
/*     */     } else {
/* 143 */       for (ArrayList<Expression> arrayList : this.rows) {
/* 144 */         Value[] arrayOfValue = new Value[j]; int k;
/* 145 */         for (k = 0; k < i; k++) {
/* 146 */           arrayOfValue[k] = ((Expression)arrayList.get(k)).getValue(this.session).convertTo(arrayOfColumn[k].getType(), (CastDataProvider)this.session);
/*     */         }
/* 148 */         this.columnResolver.currentRow = arrayOfValue;
/* 149 */         for (k = i; k < j; k++) {
/* 150 */           arrayOfValue[k] = this.expressionArray[k].getValue(this.session);
/*     */         }
/* 152 */         localResult.addRow(arrayOfValue);
/*     */       } 
/* 154 */       this.columnResolver.currentRow = null;
/*     */     } 
/* 156 */     return (ResultInterface)finishResult(localResult, l1, l2, bool, paramResultTarget);
/*     */   }
/*     */ 
/*     */   
/*     */   public void init() {
/* 161 */     if (this.checkInit) {
/* 162 */       throw DbException.getInternalError();
/*     */     }
/* 164 */     this.checkInit = true;
/* 165 */     if (this.withTies && !hasOrder()) {
/* 166 */       throw DbException.get(90122);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepare() {
/* 172 */     if (this.isPrepared) {
/*     */       return;
/*     */     }
/*     */     
/* 176 */     if (!this.checkInit) {
/* 177 */       throw DbException.getInternalError("not initialized");
/*     */     }
/* 179 */     this.isPrepared = true;
/* 180 */     if (this.columnResolver == null) {
/* 181 */       createTable();
/*     */     }
/* 183 */     if (this.orderList != null) {
/* 184 */       ArrayList<String> arrayList = new ArrayList();
/* 185 */       for (Expression expression : this.expressions) {
/* 186 */         arrayList.add(expression.getSQL(0, 2));
/*     */       }
/* 188 */       if (initOrder(arrayList, false, (ArrayList<TableFilter>)null)) {
/* 189 */         prepareOrder(this.orderList, this.expressions.size());
/*     */       }
/*     */     } 
/* 192 */     this.resultColumnCount = this.expressions.size(); int i;
/* 193 */     for (i = 0; i < this.resultColumnCount; i++) {
/* 194 */       ((Expression)this.expressions.get(i)).mapColumns(this.columnResolver, 0, 0);
/*     */     }
/* 196 */     for (i = this.visibleColumnCount; i < this.resultColumnCount; i++) {
/* 197 */       this.expressions.set(i, ((Expression)this.expressions.get(i)).optimize(this.session));
/*     */     }
/* 199 */     if (this.sort != null) {
/* 200 */       cleanupOrder();
/*     */     }
/* 202 */     this.expressionArray = this.expressions.<Expression>toArray(new Expression[0]);
/* 203 */     double d = 0.0D;
/* 204 */     int j = this.visibleColumnCount;
/* 205 */     for (ArrayList<Expression> arrayList : this.rows) {
/* 206 */       for (byte b = 0; b < j; b++) {
/* 207 */         d += ((Expression)arrayList.get(b)).getCost();
/*     */       }
/*     */     } 
/* 210 */     this.cost = d + this.rows.size();
/*     */   }
/*     */   
/*     */   private void createTable() {
/* 214 */     int i = this.rows.size();
/* 215 */     ArrayList<Expression> arrayList = this.rows.get(0);
/* 216 */     int j = arrayList.size();
/* 217 */     TypeInfo[] arrayOfTypeInfo = new TypeInfo[j]; byte b1;
/* 218 */     for (b1 = 0; b1 < j; b1++) {
/* 219 */       Expression expression = ((Expression)arrayList.get(b1)).optimize(this.session);
/* 220 */       arrayList.set(b1, expression);
/* 221 */       TypeInfo typeInfo = expression.getType();
/* 222 */       if (typeInfo.getValueType() == -1) {
/* 223 */         typeInfo = TypeInfo.TYPE_VARCHAR;
/*     */       }
/* 225 */       arrayOfTypeInfo[b1] = typeInfo;
/*     */     } 
/* 227 */     for (b1 = 1; b1 < i; b1++) {
/* 228 */       arrayList = this.rows.get(b1);
/* 229 */       for (byte b = 0; b < j; b++) {
/* 230 */         Expression expression = ((Expression)arrayList.get(b)).optimize(this.session);
/* 231 */         arrayList.set(b, expression);
/* 232 */         arrayOfTypeInfo[b] = TypeInfo.getHigherType(arrayOfTypeInfo[b], expression.getType());
/*     */       } 
/*     */     } 
/* 235 */     Column[] arrayOfColumn = new Column[j];
/* 236 */     for (byte b2 = 0; b2 < j; ) {
/* 237 */       TypeInfo typeInfo = arrayOfTypeInfo[b2];
/* 238 */       arrayOfColumn[b2] = new Column("C" + ++b2, typeInfo);
/*     */     } 
/* 240 */     Database database = this.session.getDatabase();
/* 241 */     ArrayList<ExpressionColumn> arrayList1 = new ArrayList(j);
/* 242 */     for (byte b3 = 0; b3 < j; b3++) {
/* 243 */       arrayList1.add(new ExpressionColumn(database, null, null, arrayOfColumn[b3].getName()));
/*     */     }
/* 245 */     this.expressions = (ArrayList)arrayList1;
/* 246 */     this.table = new TableValueConstructorTable(this.session.getDatabase().getMainSchema(), this.session, arrayOfColumn, this.rows);
/* 247 */     this.columnResolver = new TableValueColumnResolver();
/*     */   }
/*     */ 
/*     */   
/*     */   public double getCost() {
/* 252 */     return this.cost;
/*     */   }
/*     */ 
/*     */   
/*     */   public HashSet<Table> getTables() {
/* 257 */     HashSet<TableValueConstructorTable> hashSet = new HashSet(1, 1.0F);
/* 258 */     hashSet.add(this.table);
/* 259 */     return (HashSet)hashSet;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setForUpdate(boolean paramBoolean) {
/* 264 */     throw DbException.get(90140);
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt) {
/* 269 */     int i = this.visibleColumnCount;
/* 270 */     for (ArrayList<Expression> arrayList : this.rows) {
/* 271 */       for (byte b = 0; b < i; b++) {
/* 272 */         ((Expression)arrayList.get(b)).mapColumns(paramColumnResolver, paramInt, 0);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 279 */     int i = this.visibleColumnCount;
/* 280 */     for (ArrayList<Expression> arrayList : this.rows) {
/* 281 */       for (byte b = 0; b < i; b++) {
/* 282 */         ((Expression)arrayList.get(b)).setEvaluatable(paramTableFilter, paramBoolean);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addGlobalCondition(Parameter paramParameter, int paramInt1, int paramInt2) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allowGlobalConditions() {
/* 294 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 299 */     ExpressionVisitor expressionVisitor = paramExpressionVisitor.incrementQueryLevel(1);
/* 300 */     for (Expression expression : this.expressionArray) {
/* 301 */       if (!expression.isEverything(expressionVisitor)) {
/* 302 */         return false;
/*     */       }
/*     */     } 
/* 305 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 310 */     int i = this.visibleColumnCount;
/* 311 */     for (ArrayList<Expression> arrayList : this.rows) {
/* 312 */       for (byte b = 0; b < i; b++) {
/* 313 */         ((Expression)arrayList.get(b)).updateAggregate(paramSessionLocal, paramInt);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void fireBeforeSelectTriggers() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPlanSQL(int paramInt) {
/* 325 */     StringBuilder stringBuilder = new StringBuilder();
/* 326 */     getValuesSQL(stringBuilder, paramInt, this.rows);
/* 327 */     appendEndOfQueryToSQL(stringBuilder, paramInt, this.expressionArray);
/* 328 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Table toTable(String paramString, Column[] paramArrayOfColumn, ArrayList<Parameter> paramArrayList, boolean paramBoolean, Query paramQuery) {
/* 334 */     if (!hasOrder() && this.offsetExpr == null && this.fetchExpr == null && this.table != null) {
/* 335 */       return (Table)this.table;
/*     */     }
/* 337 */     return super.toTable(paramString, paramArrayOfColumn, paramArrayList, paramBoolean, paramQuery);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConstantQuery() {
/* 342 */     if (!super.isConstantQuery()) {
/* 343 */       return false;
/*     */     }
/* 345 */     for (ArrayList<Expression> arrayList : this.rows) {
/* 346 */       for (byte b = 0; b < this.visibleColumnCount; b++) {
/* 347 */         if (!((Expression)arrayList.get(b)).isConstant()) {
/* 348 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/* 352 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getIfSingleRow() {
/* 357 */     if (this.offsetExpr != null || this.fetchExpr != null || this.rows.size() != 1) {
/* 358 */       return null;
/*     */     }
/* 360 */     ArrayList<Expression> arrayList = this.rows.get(0);
/* 361 */     if (this.visibleColumnCount == 1) {
/* 362 */       return arrayList.get(0);
/*     */     }
/* 364 */     Expression[] arrayOfExpression = new Expression[this.visibleColumnCount];
/* 365 */     for (byte b = 0; b < this.visibleColumnCount; b++) {
/* 366 */       arrayOfExpression[b] = arrayList.get(b);
/*     */     }
/* 368 */     return (Expression)new ExpressionList(arrayOfExpression, false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final class TableValueColumnResolver
/*     */     implements ColumnResolver
/*     */   {
/*     */     Value[] currentRow;
/*     */ 
/*     */     
/*     */     public Column[] getColumns() {
/* 380 */       return TableValueConstructor.this.table.getColumns();
/*     */     }
/*     */ 
/*     */     
/*     */     public Column findColumn(String param1String) {
/* 385 */       return TableValueConstructor.this.table.findColumn(param1String);
/*     */     }
/*     */ 
/*     */     
/*     */     public Value getValue(Column param1Column) {
/* 390 */       return this.currentRow[param1Column.getColumnId()];
/*     */     }
/*     */ 
/*     */     
/*     */     public Expression optimize(ExpressionColumn param1ExpressionColumn, Column param1Column) {
/* 395 */       return TableValueConstructor.this.expressions.get(param1Column.getColumnId());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\query\TableValueConstructor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */