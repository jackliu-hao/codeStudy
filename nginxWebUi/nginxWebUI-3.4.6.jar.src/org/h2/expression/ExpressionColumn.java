/*     */ package org.h2.expression;
/*     */ 
/*     */ import org.h2.command.query.Select;
/*     */ import org.h2.command.query.SelectGroups;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.condition.Comparison;
/*     */ import org.h2.expression.function.CurrentDateTimeValueFunction;
/*     */ import org.h2.index.IndexCondition;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Constant;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.ParserUtil;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueDecfloat;
/*     */ import org.h2.value.ValueDouble;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueNumeric;
/*     */ import org.h2.value.ValueReal;
/*     */ import org.h2.value.ValueSmallint;
/*     */ import org.h2.value.ValueTinyint;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ExpressionColumn
/*     */   extends Expression
/*     */ {
/*     */   private final Database database;
/*     */   private final String schemaName;
/*     */   private final String tableAlias;
/*     */   private final String columnName;
/*     */   private final boolean rowId;
/*     */   private final boolean quotedName;
/*     */   private ColumnResolver columnResolver;
/*     */   private int queryLevel;
/*     */   private Column column;
/*     */   
/*     */   public ExpressionColumn(Database paramDatabase, Column paramColumn) {
/*  64 */     this.database = paramDatabase;
/*  65 */     this.column = paramColumn;
/*  66 */     this.columnName = this.tableAlias = this.schemaName = null;
/*  67 */     this.rowId = paramColumn.isRowId();
/*  68 */     this.quotedName = true;
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
/*     */   public ExpressionColumn(Database paramDatabase, String paramString1, String paramString2, String paramString3) {
/*  85 */     this(paramDatabase, paramString1, paramString2, paramString3, true);
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
/*     */   public ExpressionColumn(Database paramDatabase, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
/* 105 */     this.database = paramDatabase;
/* 106 */     this.schemaName = paramString1;
/* 107 */     this.tableAlias = paramString2;
/* 108 */     this.columnName = paramString3;
/* 109 */     this.rowId = false;
/* 110 */     this.quotedName = paramBoolean;
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
/*     */   public ExpressionColumn(Database paramDatabase, String paramString1, String paramString2) {
/* 125 */     this.database = paramDatabase;
/* 126 */     this.schemaName = paramString1;
/* 127 */     this.tableAlias = paramString2;
/* 128 */     this.columnName = "_ROWID_";
/* 129 */     this.quotedName = this.rowId = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 134 */     if (this.schemaName != null) {
/* 135 */       ParserUtil.quoteIdentifier(paramStringBuilder, this.schemaName, paramInt).append('.');
/*     */     }
/* 137 */     if (this.tableAlias != null) {
/* 138 */       ParserUtil.quoteIdentifier(paramStringBuilder, this.tableAlias, paramInt).append('.');
/*     */     }
/* 140 */     if (this.column != null) {
/* 141 */       if (this.columnResolver != null && this.columnResolver.hasDerivedColumnList()) {
/* 142 */         ParserUtil.quoteIdentifier(paramStringBuilder, this.columnResolver.getColumnName(this.column), paramInt);
/*     */       } else {
/* 144 */         this.column.getSQL(paramStringBuilder, paramInt);
/*     */       } 
/* 146 */     } else if (this.rowId) {
/* 147 */       paramStringBuilder.append(this.columnName);
/*     */     } else {
/* 149 */       ParserUtil.quoteIdentifier(paramStringBuilder, this.columnName, paramInt);
/*     */     } 
/* 151 */     return paramStringBuilder;
/*     */   }
/*     */   
/*     */   public TableFilter getTableFilter() {
/* 155 */     return (this.columnResolver == null) ? null : this.columnResolver.getTableFilter();
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 160 */     if (this.tableAlias != null && !this.database.equalsIdentifiers(this.tableAlias, paramColumnResolver.getTableAlias())) {
/*     */       return;
/*     */     }
/* 163 */     if (this.schemaName != null && !this.database.equalsIdentifiers(this.schemaName, paramColumnResolver.getSchemaName())) {
/*     */       return;
/*     */     }
/* 166 */     if (this.rowId) {
/* 167 */       Column column1 = paramColumnResolver.getRowIdColumn();
/* 168 */       if (column1 != null) {
/* 169 */         mapColumn(paramColumnResolver, column1, paramInt1);
/*     */       }
/*     */       return;
/*     */     } 
/* 173 */     Column column = paramColumnResolver.findColumn(this.columnName);
/* 174 */     if (column != null) {
/* 175 */       mapColumn(paramColumnResolver, column, paramInt1);
/*     */       return;
/*     */     } 
/* 178 */     Column[] arrayOfColumn = paramColumnResolver.getSystemColumns();
/* 179 */     for (byte b = 0; arrayOfColumn != null && b < arrayOfColumn.length; b++) {
/* 180 */       column = arrayOfColumn[b];
/* 181 */       if (this.database.equalsIdentifiers(this.columnName, column.getName())) {
/* 182 */         mapColumn(paramColumnResolver, column, paramInt1);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void mapColumn(ColumnResolver paramColumnResolver, Column paramColumn, int paramInt) {
/* 189 */     if (this.columnResolver == null) {
/* 190 */       this.queryLevel = paramInt;
/* 191 */       this.column = paramColumn;
/* 192 */       this.columnResolver = paramColumnResolver;
/* 193 */     } else if (this.queryLevel == paramInt && this.columnResolver != paramColumnResolver && 
/* 194 */       !(paramColumnResolver instanceof org.h2.command.query.SelectListColumnResolver)) {
/*     */ 
/*     */       
/* 197 */       throw DbException.get(90059, this.columnName);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 204 */     if (this.columnResolver == null) {
/* 205 */       Schema schema = paramSessionLocal.getDatabase().findSchema((this.tableAlias == null) ? paramSessionLocal
/* 206 */           .getCurrentSchemaName() : this.tableAlias);
/* 207 */       if (schema != null) {
/* 208 */         Constant constant = schema.findConstant(this.columnName);
/* 209 */         if (constant != null) {
/* 210 */           return constant.getValue();
/*     */         }
/*     */       } 
/* 213 */       return optimizeOther();
/*     */     } 
/* 215 */     return this.columnResolver.optimize(this, this.column);
/*     */   }
/*     */   
/*     */   private Expression optimizeOther() {
/* 219 */     if (this.tableAlias == null && !this.quotedName) {
/* 220 */       switch (StringUtils.toUpperEnglish(this.columnName)) {
/*     */         case "SYSDATE":
/*     */         case "TODAY":
/* 223 */           return (Expression)new CurrentDateTimeValueFunction(0, -1);
/*     */         case "SYSTIME":
/* 225 */           return (Expression)new CurrentDateTimeValueFunction(2, -1);
/*     */         case "SYSTIMESTAMP":
/* 227 */           return (Expression)new CurrentDateTimeValueFunction(3, -1);
/*     */       } 
/*     */     }
/* 230 */     throw getColumnException(42122);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbException getColumnException(int paramInt) {
/* 240 */     String str = this.columnName;
/* 241 */     if (this.tableAlias != null) {
/* 242 */       if (this.schemaName != null) {
/* 243 */         str = this.schemaName + '.' + this.tableAlias + '.' + str;
/*     */       } else {
/* 245 */         str = this.tableAlias + '.' + str;
/*     */       } 
/*     */     }
/* 248 */     return DbException.get(paramInt, str);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 253 */     Select select = this.columnResolver.getSelect();
/* 254 */     if (select == null) {
/* 255 */       throw DbException.get(90016, getTraceSQL());
/*     */     }
/* 257 */     if (paramInt == 0) {
/*     */       return;
/*     */     }
/* 260 */     SelectGroups selectGroups = select.getGroupDataIfCurrent(false);
/* 261 */     if (selectGroups == null) {
/*     */       return;
/*     */     }
/*     */     
/* 265 */     Value value = (Value)selectGroups.getCurrentGroupExprData(this);
/* 266 */     if (value == null) {
/* 267 */       selectGroups.setCurrentGroupExprData(this, this.columnResolver.getValue(this.column));
/* 268 */     } else if (!select.isGroupWindowStage2() && 
/* 269 */       !paramSessionLocal.areEqual(this.columnResolver.getValue(this.column), value)) {
/* 270 */       throw DbException.get(90016, getTraceSQL());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/* 277 */     Select select = this.columnResolver.getSelect();
/* 278 */     if (select != null) {
/* 279 */       SelectGroups selectGroups = select.getGroupDataIfCurrent(false);
/* 280 */       if (selectGroups != null) {
/* 281 */         Value value1 = (Value)selectGroups.getCurrentGroupExprData(this);
/* 282 */         if (value1 != null) {
/* 283 */           return value1;
/*     */         }
/* 285 */         if (select.isGroupWindowStage2()) {
/* 286 */           throw DbException.get(90016, getTraceSQL());
/*     */         }
/*     */       } 
/*     */     } 
/* 290 */     Value value = this.columnResolver.getValue(this.column);
/* 291 */     if (value == null) {
/* 292 */       if (select == null) {
/* 293 */         throw DbException.get(23502, getTraceSQL());
/*     */       }
/* 295 */       throw DbException.get(90016, getTraceSQL());
/*     */     } 
/*     */     
/* 298 */     return value;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 303 */     return (this.column != null) ? this.column.getType() : (this.rowId ? TypeInfo.TYPE_BIGINT : TypeInfo.TYPE_UNKNOWN);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {}
/*     */ 
/*     */   
/*     */   public Column getColumn() {
/* 311 */     return this.column;
/*     */   }
/*     */   
/*     */   public String getOriginalColumnName() {
/* 315 */     return this.columnName;
/*     */   }
/*     */   
/*     */   public String getOriginalTableAliasName() {
/* 319 */     return this.tableAlias;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(SessionLocal paramSessionLocal, int paramInt) {
/* 324 */     if (this.column != null) {
/* 325 */       if (this.columnResolver != null) {
/* 326 */         return this.columnResolver.getColumnName(this.column);
/*     */       }
/* 328 */       return this.column.getName();
/*     */     } 
/* 330 */     return this.columnName;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSchemaName() {
/* 335 */     Table table = this.column.getTable();
/* 336 */     return (table == null) ? null : table.getSchema().getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTableName() {
/* 341 */     Table table = this.column.getTable();
/* 342 */     return (table == null) ? null : table.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAlias(SessionLocal paramSessionLocal, int paramInt) {
/* 347 */     if (this.column != null) {
/* 348 */       if (this.columnResolver != null) {
/* 349 */         return this.columnResolver.getColumnName(this.column);
/*     */       }
/* 351 */       return this.column.getName();
/*     */     } 
/* 353 */     if (this.tableAlias != null) {
/* 354 */       return this.tableAlias + '.' + this.columnName;
/*     */     }
/* 356 */     return this.columnName;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnNameForView(SessionLocal paramSessionLocal, int paramInt) {
/* 361 */     return getAlias(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIdentity() {
/* 366 */     return this.column.isIdentity();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNullable() {
/* 371 */     return this.column.isNullable() ? 1 : 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 376 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 1:
/* 378 */         return false;
/*     */       case 0:
/* 380 */         return (this.queryLevel < paramExpressionVisitor.getQueryLevel());
/*     */ 
/*     */       
/*     */       case 3:
/* 384 */         if (paramExpressionVisitor.getQueryLevel() < this.queryLevel) {
/* 385 */           return true;
/*     */         }
/* 387 */         if (getTableFilter() == null) {
/* 388 */           return false;
/*     */         }
/* 390 */         return getTableFilter().isEvaluatable();
/*     */       case 4:
/* 392 */         paramExpressionVisitor.addDataModificationId(this.column.getTable().getMaxDataModificationId());
/* 393 */         return true;
/*     */       case 6:
/* 395 */         return (this.columnResolver != paramExpressionVisitor.getResolver());
/*     */       case 7:
/* 397 */         if (this.column != null) {
/* 398 */           paramExpressionVisitor.addDependency((DbObject)this.column.getTable());
/*     */         }
/* 400 */         return true;
/*     */       case 9:
/* 402 */         if (this.column == null) {
/* 403 */           throw DbException.get(42122, getTraceSQL());
/*     */         }
/* 405 */         paramExpressionVisitor.addColumn1(this.column);
/* 406 */         return true;
/*     */       case 10:
/* 408 */         if (this.column == null) {
/* 409 */           throw DbException.get(42122, getTraceSQL());
/*     */         }
/* 411 */         paramExpressionVisitor.addColumn2(this.column);
/* 412 */         return true;
/*     */       case 11:
/* 414 */         if (this.column == null) {
/* 415 */           throw DbException.get(42122, getTraceSQL());
/*     */         }
/* 417 */         if (paramExpressionVisitor.getColumnResolvers().contains(this.columnResolver)) {
/* 418 */           int i = paramExpressionVisitor.getQueryLevel();
/* 419 */           if (i > 0) {
/* 420 */             if (this.queryLevel > 0) {
/* 421 */               this.queryLevel--;
/* 422 */               return true;
/*     */             } 
/* 424 */             throw DbException.getInternalError("queryLevel=0");
/*     */           } 
/* 426 */           return (this.queryLevel > 0);
/*     */         } 
/*     */         break;
/*     */     } 
/*     */     
/* 431 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 437 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/* 442 */     TableFilter tableFilter = getTableFilter();
/* 443 */     if (paramTableFilter == tableFilter && this.column.getType().getValueType() == 8)
/* 444 */       paramTableFilter.addIndexCondition(IndexCondition.get(0, this, ValueExpression.TRUE));  } public Expression getNotIfPossible(SessionLocal paramSessionLocal) { ValueBoolean valueBoolean; ValueTinyint valueTinyint; ValueSmallint valueSmallint; ValueInteger valueInteger;
/*     */     ValueBigint valueBigint;
/*     */     ValueNumeric valueNumeric;
/*     */     ValueReal valueReal;
/*     */     ValueDouble valueDouble;
/*     */     ValueDecfloat valueDecfloat;
/* 450 */     Expression expression = optimize(paramSessionLocal);
/* 451 */     if (expression != this) {
/* 452 */       return expression.getNotIfPossible(paramSessionLocal);
/*     */     }
/*     */     
/* 455 */     switch (this.column.getType().getValueType()) {
/*     */       case 8:
/* 457 */         valueBoolean = ValueBoolean.FALSE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 491 */         return (Expression)new Comparison(0, this, ValueExpression.get((Value)valueBoolean), false);case 9: valueTinyint = ValueTinyint.get((byte)0); return (Expression)new Comparison(0, this, ValueExpression.get((Value)valueTinyint), false);case 10: valueSmallint = ValueSmallint.get((short)0); return (Expression)new Comparison(0, this, ValueExpression.get((Value)valueSmallint), false);case 11: valueInteger = ValueInteger.get(0); return (Expression)new Comparison(0, this, ValueExpression.get((Value)valueInteger), false);case 12: valueBigint = ValueBigint.get(0L); return (Expression)new Comparison(0, this, ValueExpression.get((Value)valueBigint), false);case 13: valueNumeric = ValueNumeric.ZERO; return (Expression)new Comparison(0, this, ValueExpression.get((Value)valueNumeric), false);case 14: valueReal = ValueReal.ZERO; return (Expression)new Comparison(0, this, ValueExpression.get((Value)valueReal), false);case 15: valueDouble = ValueDouble.ZERO; return (Expression)new Comparison(0, this, ValueExpression.get((Value)valueDouble), false);case 16: valueDecfloat = ValueDecfloat.ZERO; return (Expression)new Comparison(0, this, ValueExpression.get((Value)valueDecfloat), false);
/*     */     } 
/*     */     return null; }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\ExpressionColumn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */