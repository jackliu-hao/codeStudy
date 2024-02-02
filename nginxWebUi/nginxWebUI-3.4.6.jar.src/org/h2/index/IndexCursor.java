/*     */ package org.h2.index;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueGeometry;
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
/*     */ public class IndexCursor
/*     */   implements Cursor
/*     */ {
/*     */   private SessionLocal session;
/*     */   private Index index;
/*     */   private Table table;
/*     */   private IndexColumn[] indexColumns;
/*     */   private boolean alwaysFalse;
/*     */   private SearchRow start;
/*     */   private SearchRow end;
/*     */   private SearchRow intersects;
/*     */   private Cursor cursor;
/*     */   private Column inColumn;
/*     */   private int inListIndex;
/*     */   private Value[] inList;
/*     */   private ResultInterface inResult;
/*     */   
/*     */   public void setIndex(Index paramIndex) {
/*  51 */     this.index = paramIndex;
/*  52 */     this.table = paramIndex.getTable();
/*  53 */     Column[] arrayOfColumn = this.table.getColumns();
/*  54 */     this.indexColumns = new IndexColumn[arrayOfColumn.length];
/*  55 */     IndexColumn[] arrayOfIndexColumn = paramIndex.getIndexColumns();
/*  56 */     if (arrayOfIndexColumn != null) {
/*  57 */       byte b; int i; for (b = 0, i = arrayOfColumn.length; b < i; b++) {
/*  58 */         int j = paramIndex.getColumnIndex(arrayOfColumn[b]);
/*  59 */         if (j >= 0) {
/*  60 */           this.indexColumns[b] = arrayOfIndexColumn[j];
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void prepare(SessionLocal paramSessionLocal, ArrayList<IndexCondition> paramArrayList) {
/*  73 */     this.session = paramSessionLocal;
/*  74 */     this.alwaysFalse = false;
/*  75 */     this.start = this.end = null;
/*  76 */     this.inList = null;
/*  77 */     this.inColumn = null;
/*  78 */     this.inResult = null;
/*  79 */     this.intersects = null;
/*  80 */     for (IndexCondition indexCondition : paramArrayList) {
/*  81 */       if (indexCondition.isAlwaysFalse()) {
/*  82 */         this.alwaysFalse = true;
/*     */         
/*     */         break;
/*     */       } 
/*     */       
/*  87 */       if (this.index.isFindUsingFullTableScan()) {
/*     */         continue;
/*     */       }
/*  90 */       Column column = indexCondition.getColumn();
/*  91 */       if (indexCondition.getCompareType() == 10) {
/*  92 */         if (this.start == null && this.end == null && 
/*  93 */           canUseIndexForIn(column)) {
/*  94 */           this.inColumn = column;
/*  95 */           this.inList = indexCondition.getCurrentValueList(paramSessionLocal);
/*  96 */           this.inListIndex = 0;
/*     */         }  continue;
/*     */       } 
/*  99 */       if (indexCondition.getCompareType() == 11) {
/* 100 */         if (this.start == null && this.end == null && 
/* 101 */           canUseIndexForIn(column)) {
/* 102 */           this.inColumn = column;
/* 103 */           this.inResult = indexCondition.getCurrentResult();
/*     */         } 
/*     */         continue;
/*     */       } 
/* 107 */       Value value = indexCondition.getCurrentValue(paramSessionLocal);
/* 108 */       boolean bool1 = indexCondition.isStart();
/* 109 */       boolean bool2 = indexCondition.isEnd();
/* 110 */       boolean bool3 = indexCondition.isSpatialIntersects();
/* 111 */       int i = column.getColumnId();
/* 112 */       if (i != -1) {
/* 113 */         IndexColumn indexColumn = this.indexColumns[i];
/* 114 */         if (indexColumn != null && (indexColumn.sortType & 0x1) != 0) {
/*     */ 
/*     */ 
/*     */           
/* 118 */           boolean bool = bool1;
/* 119 */           bool1 = bool2;
/* 120 */           bool2 = bool;
/*     */         } 
/*     */       } 
/* 123 */       if (bool1) {
/* 124 */         this.start = getSearchRow(this.start, i, value, true);
/*     */       }
/* 126 */       if (bool2) {
/* 127 */         this.end = getSearchRow(this.end, i, value, false);
/*     */       }
/* 129 */       if (bool3) {
/* 130 */         this.intersects = getSpatialSearchRow(this.intersects, i, value);
/*     */       }
/*     */ 
/*     */       
/* 134 */       if ((bool1 || bool2) && !canUseIndexFor(this.inColumn)) {
/* 135 */         this.inColumn = null;
/* 136 */         this.inList = null;
/* 137 */         this.inResult = null;
/*     */       } 
/*     */     } 
/*     */     
/* 141 */     if (this.inColumn != null) {
/* 142 */       this.start = (SearchRow)this.table.getTemplateRow();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void find(SessionLocal paramSessionLocal, ArrayList<IndexCondition> paramArrayList) {
/* 153 */     prepare(paramSessionLocal, paramArrayList);
/* 154 */     if (this.inColumn != null) {
/*     */       return;
/*     */     }
/* 157 */     if (!this.alwaysFalse) {
/* 158 */       if (this.intersects != null && this.index instanceof SpatialIndex) {
/* 159 */         this.cursor = ((SpatialIndex)this.index).findByGeometry(this.session, this.start, this.end, this.intersects);
/* 160 */       } else if (this.index != null) {
/* 161 */         this.cursor = this.index.find(this.session, this.start, this.end);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean canUseIndexForIn(Column paramColumn) {
/* 167 */     if (this.inColumn != null)
/*     */     {
/* 169 */       return false;
/*     */     }
/* 171 */     return canUseIndexFor(paramColumn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean canUseIndexFor(Column paramColumn) {
/* 179 */     IndexColumn[] arrayOfIndexColumn = this.index.getIndexColumns();
/* 180 */     if (arrayOfIndexColumn == null) {
/* 181 */       return true;
/*     */     }
/* 183 */     IndexColumn indexColumn = arrayOfIndexColumn[0];
/* 184 */     return (indexColumn == null || indexColumn.column == paramColumn);
/*     */   }
/*     */   private SearchRow getSpatialSearchRow(SearchRow paramSearchRow, int paramInt, Value paramValue) {
/*     */     Row row;
/* 188 */     if (paramSearchRow == null) {
/* 189 */       row = this.table.getTemplateRow();
/* 190 */     } else if (row.getValue(paramInt) != null) {
/*     */ 
/*     */ 
/*     */       
/* 194 */       ValueGeometry valueGeometry = row.getValue(paramInt).convertToGeometry(null);
/* 195 */       paramValue = paramValue.convertToGeometry(null).getEnvelopeUnion(valueGeometry);
/*     */     } 
/* 197 */     if (paramInt == -1) {
/* 198 */       row.setKey((paramValue == ValueNull.INSTANCE) ? Long.MIN_VALUE : paramValue.getLong());
/*     */     } else {
/* 200 */       row.setValue(paramInt, paramValue);
/*     */     } 
/* 202 */     return (SearchRow)row;
/*     */   }
/*     */   private SearchRow getSearchRow(SearchRow paramSearchRow, int paramInt, Value paramValue, boolean paramBoolean) {
/*     */     Row row;
/* 206 */     if (paramSearchRow == null) {
/* 207 */       row = this.table.getTemplateRow();
/*     */     } else {
/* 209 */       paramValue = getMax(row.getValue(paramInt), paramValue, paramBoolean);
/*     */     } 
/* 211 */     if (paramInt == -1) {
/* 212 */       row.setKey((paramValue == ValueNull.INSTANCE) ? Long.MIN_VALUE : paramValue.getLong());
/*     */     } else {
/* 214 */       row.setValue(paramInt, paramValue);
/*     */     } 
/* 216 */     return (SearchRow)row;
/*     */   }
/*     */   
/*     */   private Value getMax(Value paramValue1, Value paramValue2, boolean paramBoolean) {
/* 220 */     if (paramValue1 == null)
/* 221 */       return paramValue2; 
/* 222 */     if (paramValue2 == null) {
/* 223 */       return paramValue1;
/*     */     }
/*     */     
/* 226 */     if (paramValue1 == ValueNull.INSTANCE)
/* 227 */       return paramValue2; 
/* 228 */     if (paramValue2 == ValueNull.INSTANCE) {
/* 229 */       return paramValue1;
/*     */     }
/* 231 */     int i = this.session.compare(paramValue1, paramValue2);
/* 232 */     if (i == 0) {
/* 233 */       return paramValue1;
/*     */     }
/* 235 */     return (((i > 0)) == paramBoolean) ? paramValue1 : paramValue2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAlwaysFalse() {
/* 244 */     return this.alwaysFalse;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SearchRow getStart() {
/* 253 */     return this.start;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SearchRow getEnd() {
/* 262 */     return this.end;
/*     */   }
/*     */ 
/*     */   
/*     */   public Row get() {
/* 267 */     if (this.cursor == null) {
/* 268 */       return null;
/*     */     }
/* 270 */     return this.cursor.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public SearchRow getSearchRow() {
/* 275 */     return this.cursor.getSearchRow();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean next() {
/*     */     while (true) {
/* 281 */       if (this.cursor == null) {
/* 282 */         nextCursor();
/* 283 */         if (this.cursor == null) {
/* 284 */           return false;
/*     */         }
/*     */       } 
/* 287 */       if (this.cursor.next()) {
/* 288 */         return true;
/*     */       }
/* 290 */       this.cursor = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void nextCursor() {
/* 295 */     if (this.inList != null) {
/* 296 */       while (this.inListIndex < this.inList.length) {
/* 297 */         Value value = this.inList[this.inListIndex++];
/* 298 */         if (value != ValueNull.INSTANCE) {
/* 299 */           find(value);
/*     */           break;
/*     */         } 
/*     */       } 
/* 303 */     } else if (this.inResult != null) {
/* 304 */       while (this.inResult.next()) {
/* 305 */         Value value = this.inResult.currentRow()[0];
/* 306 */         if (value != ValueNull.INSTANCE) {
/* 307 */           find(value);
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void find(Value paramValue) {
/* 315 */     paramValue = this.inColumn.convert((CastDataProvider)this.session, paramValue);
/* 316 */     int i = this.inColumn.getColumnId();
/* 317 */     this.start.setValue(i, paramValue);
/* 318 */     this.cursor = this.index.find(this.session, this.start, this.start);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean previous() {
/* 323 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\IndexCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */