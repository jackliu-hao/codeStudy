/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import org.h2.command.Command;
/*     */ import org.h2.command.query.Query;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.expression.ValueExpression;
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
/*     */ public final class Merge
/*     */   extends CommandWithValues
/*     */ {
/*     */   private boolean isReplace;
/*     */   private Table table;
/*     */   private Column[] columns;
/*     */   private Column[] keys;
/*     */   private Query query;
/*     */   private Update update;
/*     */   
/*     */   public Merge(SessionLocal paramSessionLocal, boolean paramBoolean) {
/*  52 */     super(paramSessionLocal);
/*  53 */     this.isReplace = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCommand(Command paramCommand) {
/*  58 */     super.setCommand(paramCommand);
/*  59 */     if (this.query != null) {
/*  60 */       this.query.setCommand(paramCommand);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Table getTable() {
/*  66 */     return this.table;
/*     */   }
/*     */   
/*     */   public void setTable(Table paramTable) {
/*  70 */     this.table = paramTable;
/*     */   }
/*     */   
/*     */   public void setColumns(Column[] paramArrayOfColumn) {
/*  74 */     this.columns = paramArrayOfColumn;
/*     */   }
/*     */   
/*     */   public void setKeys(Column[] paramArrayOfColumn) {
/*  78 */     this.keys = paramArrayOfColumn;
/*     */   }
/*     */   
/*     */   public void setQuery(Query paramQuery) {
/*  82 */     this.query = paramQuery;
/*     */   }
/*     */ 
/*     */   
/*     */   public long update(ResultTarget paramResultTarget, DataChangeDeltaTable.ResultOption paramResultOption) {
/*  87 */     long l = 0L;
/*  88 */     this.session.getUser().checkTableRight(this.table, 4);
/*  89 */     this.session.getUser().checkTableRight(this.table, 8);
/*  90 */     setCurrentRowNumber(0L);
/*  91 */     if (!this.valuesExpressionList.isEmpty()) {
/*     */       byte b; int i;
/*  93 */       for (b = 0, i = this.valuesExpressionList.size(); b < i; b++) {
/*  94 */         setCurrentRowNumber((b + 1));
/*  95 */         Expression[] arrayOfExpression = this.valuesExpressionList.get(b);
/*  96 */         Row row = this.table.getTemplateRow(); byte b1; int j;
/*  97 */         for (b1 = 0, j = this.columns.length; b1 < j; b1++) {
/*  98 */           Column column = this.columns[b1];
/*  99 */           int k = column.getColumnId();
/* 100 */           Expression expression = arrayOfExpression[b1];
/* 101 */           if (expression != ValueExpression.DEFAULT) {
/*     */             try {
/* 103 */               row.setValue(k, expression.getValue(this.session));
/* 104 */             } catch (DbException dbException) {
/* 105 */               throw setRow(dbException, l, getSimpleSQL(arrayOfExpression));
/*     */             } 
/*     */           }
/*     */         } 
/* 109 */         l += merge(row, arrayOfExpression, paramResultTarget, paramResultOption);
/*     */       } 
/*     */     } else {
/*     */       
/* 113 */       this.query.setNeverLazy(true);
/* 114 */       ResultInterface resultInterface = this.query.query(0L);
/* 115 */       this.table.fire(this.session, 3, true);
/* 116 */       this.table.lock(this.session, 1);
/* 117 */       while (resultInterface.next()) {
/* 118 */         Value[] arrayOfValue = resultInterface.currentRow();
/* 119 */         Row row = this.table.getTemplateRow();
/* 120 */         setCurrentRowNumber(l);
/* 121 */         for (byte b = 0; b < this.columns.length; b++) {
/* 122 */           row.setValue(this.columns[b].getColumnId(), arrayOfValue[b]);
/*     */         }
/* 124 */         l += merge(row, (Expression[])null, paramResultTarget, paramResultOption);
/*     */       } 
/* 126 */       resultInterface.close();
/* 127 */       this.table.fire(this.session, 3, false);
/*     */     } 
/* 129 */     return l;
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
/*     */   private int merge(Row paramRow, Expression[] paramArrayOfExpression, ResultTarget paramResultTarget, DataChangeDeltaTable.ResultOption paramResultOption) {
/*     */     long l;
/* 145 */     if (this.update == null) {
/*     */ 
/*     */       
/* 148 */       l = 0L;
/*     */     } else {
/* 150 */       ArrayList<Parameter> arrayList = this.update.getParameters();
/* 151 */       byte b1 = 0;
/* 152 */       for (byte b2 = 0; b2 < null; b2++) {
/* 153 */         Column column = this.columns[b2];
/* 154 */         if (column.isGeneratedAlways()) {
/* 155 */           if (paramArrayOfExpression == null || paramArrayOfExpression[b2] != ValueExpression.DEFAULT) {
/* 156 */             throw DbException.get(90154, column
/* 157 */                 .getSQLWithTable(new StringBuilder(), 3).toString());
/*     */           }
/*     */         } else {
/* 160 */           Value value = paramRow.getValue(column.getColumnId());
/* 161 */           if (value == null) {
/* 162 */             Expression expression = column.getEffectiveDefaultExpression();
/* 163 */             value = (expression != null) ? expression.getValue(this.session) : (Value)ValueNull.INSTANCE;
/*     */           } 
/* 165 */           ((Parameter)arrayList.get(b1++)).setValue(value);
/*     */         } 
/*     */       } 
/* 168 */       for (Column column : this.keys) {
/* 169 */         Value value = paramRow.getValue(column.getColumnId());
/* 170 */         if (value == null) {
/* 171 */           throw DbException.get(90081, column.getTraceSQL());
/*     */         }
/* 173 */         ((Parameter)arrayList.get(b1++)).setValue(value);
/*     */       } 
/* 175 */       l = this.update.update(paramResultTarget, paramResultOption);
/*     */     } 
/*     */     
/* 178 */     if (l == 0L)
/*     */       try {
/* 180 */         this.table.convertInsertRow(this.session, paramRow, null);
/* 181 */         if (paramResultOption == DataChangeDeltaTable.ResultOption.NEW) {
/* 182 */           paramResultTarget.addRow((Value[])paramRow.getValueList().clone());
/*     */         }
/* 184 */         if (!this.table.fireBeforeRow(this.session, null, paramRow)) {
/* 185 */           this.table.lock(this.session, 1);
/* 186 */           this.table.addRow(this.session, paramRow);
/* 187 */           DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, paramResultTarget, paramResultOption, paramRow);
/*     */           
/* 189 */           this.table.fireAfterRow(this.session, null, paramRow, false);
/*     */         } else {
/* 191 */           DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, paramResultTarget, paramResultOption, paramRow);
/*     */         } 
/*     */         
/* 194 */         return 1;
/* 195 */       } catch (DbException dbException) {
/* 196 */         if (dbException.getErrorCode() == 23505) {
/*     */           
/* 198 */           Index index = (Index)dbException.getSource();
/* 199 */           if (index != null) {
/*     */             Column[] arrayOfColumn;
/*     */             boolean bool;
/* 202 */             if (index instanceof MVPrimaryIndex) {
/* 203 */               MVPrimaryIndex mVPrimaryIndex = (MVPrimaryIndex)index;
/*     */               
/* 205 */               arrayOfColumn = new Column[] { (mVPrimaryIndex.getIndexColumns()[mVPrimaryIndex.getMainIndexColumn()]).column };
/*     */             } else {
/* 207 */               arrayOfColumn = index.getColumns();
/*     */             } 
/*     */             
/* 210 */             if (arrayOfColumn.length <= this.keys.length) {
/* 211 */               bool = true;
/* 212 */               for (byte b = 0; b < arrayOfColumn.length; b++) {
/* 213 */                 if (arrayOfColumn[b] != this.keys[b]) {
/* 214 */                   bool = false;
/*     */                   break;
/*     */                 } 
/*     */               } 
/*     */             } else {
/* 219 */               bool = false;
/*     */             } 
/* 221 */             if (bool) {
/* 222 */               throw DbException.get(90131, this.table.getName());
/*     */             }
/*     */           } 
/*     */         } 
/* 226 */         throw dbException;
/*     */       }  
/* 228 */     if (l == 1L) {
/* 229 */       return this.isReplace ? 2 : 1;
/*     */     }
/* 231 */     throw DbException.get(23505, this.table.getTraceSQL());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPlanSQL(int paramInt) {
/* 236 */     StringBuilder stringBuilder = new StringBuilder(this.isReplace ? "REPLACE INTO " : "MERGE INTO ");
/* 237 */     this.table.getSQL(stringBuilder, paramInt).append('(');
/* 238 */     Column.writeColumns(stringBuilder, this.columns, paramInt);
/* 239 */     stringBuilder.append(')');
/* 240 */     if (!this.isReplace && this.keys != null) {
/* 241 */       stringBuilder.append(" KEY(");
/* 242 */       Column.writeColumns(stringBuilder, this.keys, paramInt);
/* 243 */       stringBuilder.append(')');
/*     */     } 
/* 245 */     stringBuilder.append('\n');
/* 246 */     if (!this.valuesExpressionList.isEmpty()) {
/* 247 */       stringBuilder.append("VALUES ");
/* 248 */       byte b = 0;
/* 249 */       for (Expression[] arrayOfExpression : this.valuesExpressionList) {
/* 250 */         if (b++ > 0) {
/* 251 */           stringBuilder.append(", ");
/*     */         }
/* 253 */         Expression.writeExpressions(stringBuilder.append('('), arrayOfExpression, paramInt).append(')');
/*     */       } 
/*     */     } else {
/* 256 */       stringBuilder.append(this.query.getPlanSQL(paramInt));
/*     */     } 
/* 258 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepare() {
/* 263 */     if (this.columns == null) {
/* 264 */       if (!this.valuesExpressionList.isEmpty() && ((Expression[])this.valuesExpressionList.get(0)).length == 0) {
/*     */         
/* 266 */         this.columns = new Column[0];
/*     */       } else {
/* 268 */         this.columns = this.table.getColumns();
/*     */       } 
/*     */     }
/* 271 */     if (!this.valuesExpressionList.isEmpty()) {
/* 272 */       for (Expression[] arrayOfExpression : this.valuesExpressionList) {
/* 273 */         if (arrayOfExpression.length != this.columns.length) {
/* 274 */           throw DbException.get(21002);
/*     */         }
/* 276 */         for (byte b1 = 0; b1 < arrayOfExpression.length; b1++) {
/* 277 */           Expression expression = arrayOfExpression[b1];
/* 278 */           if (expression != null) {
/* 279 */             arrayOfExpression[b1] = expression.optimize(this.session);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } else {
/* 284 */       this.query.prepare();
/* 285 */       if (this.query.getColumnCount() != this.columns.length) {
/* 286 */         throw DbException.get(21002);
/*     */       }
/*     */     } 
/* 289 */     if (this.keys == null) {
/* 290 */       Index index = this.table.getPrimaryKey();
/* 291 */       if (index == null) {
/* 292 */         throw DbException.get(90057, "PRIMARY KEY");
/*     */       }
/* 294 */       this.keys = index.getColumns();
/*     */     } 
/* 296 */     if (this.isReplace)
/*     */     {
/*     */       
/* 299 */       for (Column column : this.keys) {
/* 300 */         boolean bool1 = false;
/* 301 */         for (Column column1 : this.columns) {
/* 302 */           if (column1.getColumnId() == column.getColumnId()) {
/* 303 */             bool1 = true;
/*     */             break;
/*     */           } 
/*     */         } 
/* 307 */         if (!bool1) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */     }
/* 312 */     StringBuilder stringBuilder = this.table.getSQL(new StringBuilder("UPDATE "), 0).append(" SET ");
/* 313 */     boolean bool = false; byte b; int i;
/* 314 */     for (b = 0, i = this.columns.length; b < i; b++) {
/* 315 */       Column column = this.columns[b];
/* 316 */       if (!column.isGeneratedAlways()) {
/* 317 */         if (bool) {
/* 318 */           stringBuilder.append(", ");
/*     */         }
/* 320 */         bool = true;
/* 321 */         column.getSQL(stringBuilder, 0).append("=?");
/*     */       } 
/*     */     } 
/* 324 */     if (!bool) {
/* 325 */       throw DbException.getSyntaxError(this.sqlStatement, this.sqlStatement.length(), "Valid MERGE INTO statement with at least one updatable column");
/*     */     }
/*     */     
/* 328 */     Column.writeColumns(stringBuilder.append(" WHERE "), this.keys, " AND ", "=?", 0);
/* 329 */     this.update = (Update)this.session.prepare(stringBuilder.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 334 */     return this.isReplace ? 63 : 62;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStatementName() {
/* 339 */     return this.isReplace ? "REPLACE" : "MERGE";
/*     */   }
/*     */ 
/*     */   
/*     */   public void collectDependencies(HashSet<DbObject> paramHashSet) {
/* 344 */     if (this.query != null)
/* 345 */       this.query.collectDependencies(paramHashSet); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\Merge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */