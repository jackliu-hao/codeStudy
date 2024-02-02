/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import org.h2.command.Command;
/*     */ import org.h2.command.query.Query;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.expression.condition.Comparison;
/*     */ import org.h2.expression.condition.ConditionAndOr;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.db.MVPrimaryIndex;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.ResultTarget;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.DataChangeDeltaTable;
/*     */ import org.h2.table.Table;
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
/*     */ 
/*     */ public final class Insert
/*     */   extends CommandWithValues
/*     */   implements ResultTarget
/*     */ {
/*     */   private Table table;
/*     */   private Column[] columns;
/*     */   private Query query;
/*     */   private long rowNumber;
/*     */   private boolean insertFromSelect;
/*     */   private Boolean overridingSystem;
/*     */   private HashMap<Column, Expression> duplicateKeyAssignmentMap;
/*     */   private Value[] onDuplicateKeyRow;
/*     */   private boolean ignore;
/*     */   private ResultTarget deltaChangeCollector;
/*     */   private DataChangeDeltaTable.ResultOption deltaChangeCollectionMode;
/*     */   
/*     */   public Insert(SessionLocal paramSessionLocal) {
/*  72 */     super(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCommand(Command paramCommand) {
/*  77 */     super.setCommand(paramCommand);
/*  78 */     if (this.query != null) {
/*  79 */       this.query.setCommand(paramCommand);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Table getTable() {
/*  85 */     return this.table;
/*     */   }
/*     */   
/*     */   public void setTable(Table paramTable) {
/*  89 */     this.table = paramTable;
/*     */   }
/*     */   
/*     */   public void setColumns(Column[] paramArrayOfColumn) {
/*  93 */     this.columns = paramArrayOfColumn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIgnore(boolean paramBoolean) {
/* 103 */     this.ignore = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setQuery(Query paramQuery) {
/* 107 */     this.query = paramQuery;
/*     */   }
/*     */   
/*     */   public void setOverridingSystem(Boolean paramBoolean) {
/* 111 */     this.overridingSystem = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAssignmentForDuplicate(Column paramColumn, Expression paramExpression) {
/* 122 */     if (this.duplicateKeyAssignmentMap == null) {
/* 123 */       this.duplicateKeyAssignmentMap = new HashMap<>();
/*     */     }
/* 125 */     if (this.duplicateKeyAssignmentMap.putIfAbsent(paramColumn, paramExpression) != null) {
/* 126 */       throw DbException.get(42121, paramColumn.getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long update(ResultTarget paramResultTarget, DataChangeDeltaTable.ResultOption paramResultOption) {
/* 132 */     this.deltaChangeCollector = paramResultTarget;
/* 133 */     this.deltaChangeCollectionMode = paramResultOption;
/*     */     try {
/* 135 */       return insertRows();
/*     */     } finally {
/* 137 */       this.deltaChangeCollector = null;
/* 138 */       this.deltaChangeCollectionMode = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private long insertRows() {
/* 143 */     this.session.getUser().checkTableRight(this.table, 4);
/* 144 */     setCurrentRowNumber(0L);
/* 145 */     this.table.fire(this.session, 1, true);
/* 146 */     this.rowNumber = 0L;
/* 147 */     int i = this.valuesExpressionList.size();
/* 148 */     if (i > 0) {
/* 149 */       int j = this.columns.length;
/* 150 */       for (byte b = 0; b < i; b++) {
/* 151 */         Row row = this.table.getTemplateRow();
/* 152 */         Expression[] arrayOfExpression = this.valuesExpressionList.get(b);
/* 153 */         setCurrentRowNumber((b + 1));
/* 154 */         for (byte b1 = 0; b1 < j; b1++) {
/* 155 */           Column column = this.columns[b1];
/* 156 */           int k = column.getColumnId();
/* 157 */           Expression expression = arrayOfExpression[b1];
/* 158 */           if (expression != ValueExpression.DEFAULT) {
/*     */             try {
/* 160 */               row.setValue(k, expression.getValue(this.session));
/* 161 */             } catch (DbException dbException) {
/* 162 */               throw setRow(dbException, b, getSimpleSQL(arrayOfExpression));
/*     */             } 
/*     */           }
/*     */         } 
/* 166 */         this.rowNumber++;
/* 167 */         this.table.convertInsertRow(this.session, row, this.overridingSystem);
/* 168 */         if (this.deltaChangeCollectionMode == DataChangeDeltaTable.ResultOption.NEW) {
/* 169 */           this.deltaChangeCollector.addRow((Value[])row.getValueList().clone());
/*     */         }
/* 171 */         if (!this.table.fireBeforeRow(this.session, null, row)) {
/* 172 */           this.table.lock(this.session, 1);
/*     */           try {
/* 174 */             this.table.addRow(this.session, row);
/* 175 */           } catch (DbException dbException) {
/* 176 */             if (handleOnDuplicate(dbException, (Value[])null)) {
/*     */ 
/*     */               
/* 179 */               this.rowNumber++;
/*     */             } else {
/*     */               
/* 182 */               this.rowNumber--;
/*     */             } 
/*     */           } 
/*     */           
/* 186 */           DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, row);
/*     */           
/* 188 */           this.table.fireAfterRow(this.session, null, row, false);
/*     */         } else {
/* 190 */           DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, row);
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 195 */       this.table.lock(this.session, 1);
/* 196 */       if (this.insertFromSelect) {
/* 197 */         this.query.query(0L, this);
/*     */       } else {
/* 199 */         ResultInterface resultInterface = this.query.query(0L);
/* 200 */         while (resultInterface.next()) {
/* 201 */           Value[] arrayOfValue = resultInterface.currentRow();
/*     */           try {
/* 203 */             addRow(arrayOfValue);
/* 204 */           } catch (DbException dbException) {
/* 205 */             if (handleOnDuplicate(dbException, arrayOfValue)) {
/*     */ 
/*     */               
/* 208 */               this.rowNumber++;
/*     */               continue;
/*     */             } 
/* 211 */             this.rowNumber--;
/*     */           } 
/*     */         } 
/*     */         
/* 215 */         resultInterface.close();
/*     */       } 
/*     */     } 
/* 218 */     this.table.fire(this.session, 1, false);
/* 219 */     return this.rowNumber;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addRow(Value... paramVarArgs) {
/* 224 */     Row row = this.table.getTemplateRow();
/* 225 */     setCurrentRowNumber(++this.rowNumber); byte b; int i;
/* 226 */     for (b = 0, i = this.columns.length; b < i; b++) {
/* 227 */       row.setValue(this.columns[b].getColumnId(), paramVarArgs[b]);
/*     */     }
/* 229 */     this.table.convertInsertRow(this.session, row, this.overridingSystem);
/* 230 */     if (this.deltaChangeCollectionMode == DataChangeDeltaTable.ResultOption.NEW) {
/* 231 */       this.deltaChangeCollector.addRow((Value[])row.getValueList().clone());
/*     */     }
/* 233 */     if (!this.table.fireBeforeRow(this.session, null, row)) {
/* 234 */       this.table.addRow(this.session, row);
/* 235 */       DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, row);
/*     */       
/* 237 */       this.table.fireAfterRow(this.session, null, row, false);
/*     */     } else {
/* 239 */       DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, row);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRowCount() {
/* 247 */     return this.rowNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void limitsWereApplied() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPlanSQL(int paramInt) {
/* 257 */     StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
/* 258 */     this.table.getSQL(stringBuilder, paramInt).append('(');
/* 259 */     Column.writeColumns(stringBuilder, this.columns, paramInt);
/* 260 */     stringBuilder.append(")\n");
/* 261 */     if (this.insertFromSelect) {
/* 262 */       stringBuilder.append("DIRECT ");
/*     */     }
/* 264 */     if (!this.valuesExpressionList.isEmpty()) {
/* 265 */       stringBuilder.append("VALUES ");
/* 266 */       byte b = 0;
/* 267 */       if (this.valuesExpressionList.size() > 1) {
/* 268 */         stringBuilder.append('\n');
/*     */       }
/* 270 */       for (Expression[] arrayOfExpression : this.valuesExpressionList) {
/* 271 */         if (b++ > 0) {
/* 272 */           stringBuilder.append(",\n");
/*     */         }
/* 274 */         Expression.writeExpressions(stringBuilder.append('('), arrayOfExpression, paramInt).append(')');
/*     */       } 
/*     */     } else {
/* 277 */       stringBuilder.append(this.query.getPlanSQL(paramInt));
/*     */     } 
/* 279 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepare() {
/* 284 */     if (this.columns == null) {
/* 285 */       if (!this.valuesExpressionList.isEmpty() && ((Expression[])this.valuesExpressionList.get(0)).length == 0) {
/*     */         
/* 287 */         this.columns = new Column[0];
/*     */       } else {
/* 289 */         this.columns = this.table.getColumns();
/*     */       } 
/*     */     }
/* 292 */     if (!this.valuesExpressionList.isEmpty()) {
/* 293 */       for (Expression[] arrayOfExpression : this.valuesExpressionList) {
/* 294 */         if (arrayOfExpression.length != this.columns.length)
/* 295 */           throw DbException.get(21002);  byte b;
/*     */         int i;
/* 297 */         for (b = 0, i = arrayOfExpression.length; b < i; b++) {
/* 298 */           Expression expression = arrayOfExpression[b];
/* 299 */           if (expression != null) {
/* 300 */             expression = expression.optimize(this.session);
/* 301 */             if (expression instanceof Parameter) {
/* 302 */               Parameter parameter = (Parameter)expression;
/* 303 */               parameter.setColumn(this.columns[b]);
/*     */             } 
/* 305 */             arrayOfExpression[b] = expression;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } else {
/* 310 */       this.query.prepare();
/* 311 */       if (this.query.getColumnCount() != this.columns.length) {
/* 312 */         throw DbException.get(21002);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 319 */     return 61;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStatementName() {
/* 324 */     return "INSERT";
/*     */   }
/*     */   
/*     */   public void setInsertFromSelect(boolean paramBoolean) {
/* 328 */     this.insertFromSelect = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCacheable() {
/* 333 */     return (this.duplicateKeyAssignmentMap == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean handleOnDuplicate(DbException paramDbException, Value[] paramArrayOfValue) {
/* 342 */     if (paramDbException.getErrorCode() != 23505) {
/* 343 */       throw paramDbException;
/*     */     }
/* 345 */     if (this.duplicateKeyAssignmentMap == null) {
/* 346 */       if (this.ignore) {
/* 347 */         return false;
/*     */       }
/* 349 */       throw paramDbException;
/*     */     } 
/*     */     
/* 352 */     int i = this.columns.length;
/* 353 */     Expression[] arrayOfExpression = (paramArrayOfValue == null) ? this.valuesExpressionList.get((int)getCurrentRowNumber() - 1) : new Expression[i];
/*     */     
/* 355 */     this.onDuplicateKeyRow = new Value[(this.table.getColumns()).length];
/* 356 */     for (byte b = 0; b < i; b++) {
/*     */       Value value;
/* 358 */       if (paramArrayOfValue != null) {
/* 359 */         value = paramArrayOfValue[b];
/* 360 */         arrayOfExpression[b] = (Expression)ValueExpression.get(value);
/*     */       } else {
/* 362 */         value = arrayOfExpression[b].getValue(this.session);
/*     */       } 
/* 364 */       this.onDuplicateKeyRow[this.columns[b].getColumnId()] = value;
/*     */     } 
/*     */     
/* 367 */     StringBuilder stringBuilder = new StringBuilder("UPDATE ");
/* 368 */     this.table.getSQL(stringBuilder, 0).append(" SET ");
/* 369 */     boolean bool1 = false;
/* 370 */     for (Map.Entry<Column, Expression> entry : this.duplicateKeyAssignmentMap.entrySet()) {
/* 371 */       if (bool1) {
/* 372 */         stringBuilder.append(", ");
/*     */       }
/* 374 */       bool1 = true;
/* 375 */       ((Column)entry.getKey()).getSQL(stringBuilder, 0).append('=');
/* 376 */       ((Expression)entry.getValue()).getUnenclosedSQL(stringBuilder, 0);
/*     */     } 
/* 378 */     stringBuilder.append(" WHERE ");
/* 379 */     Index index = (Index)paramDbException.getSource();
/* 380 */     if (index == null) {
/* 381 */       throw DbException.getUnsupportedException("Unable to apply ON DUPLICATE KEY UPDATE, no index found!");
/*     */     }
/*     */     
/* 384 */     prepareUpdateCondition(index, arrayOfExpression).getUnenclosedSQL(stringBuilder, 0);
/* 385 */     String str = stringBuilder.toString();
/* 386 */     Update update = (Update)this.session.prepare(str);
/* 387 */     update.setOnDuplicateKeyInsert(this);
/* 388 */     for (Parameter parameter1 : update.getParameters()) {
/* 389 */       Parameter parameter2 = this.parameters.get(parameter1.getIndex());
/* 390 */       parameter1.setValue(parameter2.getValue(this.session));
/*     */     } 
/* 392 */     boolean bool2 = (update.update() > 0L) ? true : false;
/* 393 */     this.onDuplicateKeyRow = null;
/* 394 */     return bool2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Expression prepareUpdateCondition(Index paramIndex, Expression[] paramArrayOfExpression) {
/*     */     Column[] arrayOfColumn;
/*     */     ConditionAndOr conditionAndOr;
/* 405 */     if (paramIndex instanceof MVPrimaryIndex) {
/* 406 */       MVPrimaryIndex mVPrimaryIndex = (MVPrimaryIndex)paramIndex;
/*     */       
/* 408 */       arrayOfColumn = new Column[] { (mVPrimaryIndex.getIndexColumns()[mVPrimaryIndex.getMainIndexColumn()]).column };
/*     */     } else {
/* 410 */       arrayOfColumn = paramIndex.getColumns();
/*     */     } 
/*     */     
/* 413 */     Comparison comparison = null;
/* 414 */     for (Column column : arrayOfColumn) {
/*     */       
/* 416 */       ExpressionColumn expressionColumn = new ExpressionColumn(this.session.getDatabase(), this.table.getSchema().getName(), this.table.getName(), column.getName());
/* 417 */       for (byte b = 0; b < this.columns.length; b++) {
/* 418 */         if (expressionColumn.getColumnName(this.session, b).equals(this.columns[b].getName())) {
/* 419 */           if (comparison == null) {
/* 420 */             comparison = new Comparison(0, (Expression)expressionColumn, paramArrayOfExpression[b], false); break;
/*     */           } 
/* 422 */           conditionAndOr = new ConditionAndOr(0, (Expression)comparison, (Expression)new Comparison(0, (Expression)expressionColumn, paramArrayOfExpression[b], false));
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 429 */     return (Expression)conditionAndOr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Value getOnDuplicateKeyValue(int paramInt) {
/* 439 */     return this.onDuplicateKeyRow[paramInt];
/*     */   }
/*     */ 
/*     */   
/*     */   public void collectDependencies(HashSet<DbObject> paramHashSet) {
/* 444 */     ExpressionVisitor expressionVisitor = ExpressionVisitor.getDependenciesVisitor(paramHashSet);
/* 445 */     if (!this.valuesExpressionList.isEmpty()) {
/* 446 */       for (Expression[] arrayOfExpression : this.valuesExpressionList) {
/* 447 */         for (Expression expression : arrayOfExpression) {
/* 448 */           expression.isEverything(expressionVisitor);
/*     */         }
/*     */       } 
/*     */     } else {
/* 452 */       this.query.isEverything(expressionVisitor);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\Insert.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */