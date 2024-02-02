/*     */ package org.h2.table;
/*     */ 
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.util.ParserUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IndexColumn
/*     */ {
/*     */   public static final int SQL_NO_ORDER = -2147483648;
/*     */   public final String columnName;
/*     */   public Column column;
/*  37 */   public int sortType = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuilder writeColumns(StringBuilder paramStringBuilder, IndexColumn[] paramArrayOfIndexColumn, int paramInt) {
/*  51 */     return writeColumns(paramStringBuilder, paramArrayOfIndexColumn, 0, paramArrayOfIndexColumn.length, paramInt);
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
/*     */   public static StringBuilder writeColumns(StringBuilder paramStringBuilder, IndexColumn[] paramArrayOfIndexColumn, int paramInt1, int paramInt2, int paramInt3) {
/*  71 */     for (int i = paramInt1; i < paramInt2; i++) {
/*  72 */       if (i > paramInt1) {
/*  73 */         paramStringBuilder.append(", ");
/*     */       }
/*  75 */       paramArrayOfIndexColumn[i].getSQL(paramStringBuilder, paramInt3);
/*     */     } 
/*  77 */     return paramStringBuilder;
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
/*     */   public static StringBuilder writeColumns(StringBuilder paramStringBuilder, IndexColumn[] paramArrayOfIndexColumn, String paramString1, String paramString2, int paramInt) {
/*     */     byte b;
/*     */     int i;
/*  97 */     for (b = 0, i = paramArrayOfIndexColumn.length; b < i; b++) {
/*  98 */       if (b > 0) {
/*  99 */         paramStringBuilder.append(paramString1);
/*     */       }
/* 101 */       paramArrayOfIndexColumn[b].getSQL(paramStringBuilder, paramInt).append(paramString2);
/*     */     } 
/* 103 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IndexColumn(String paramString) {
/* 113 */     this.columnName = paramString;
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
/*     */   public IndexColumn(String paramString, int paramInt) {
/* 125 */     this.columnName = paramString;
/* 126 */     this.sortType = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IndexColumn(Column paramColumn) {
/* 136 */     this.columnName = null;
/* 137 */     this.column = paramColumn;
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
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 150 */     if (this.column != null) {
/* 151 */       this.column.getSQL(paramStringBuilder, paramInt);
/*     */     } else {
/* 153 */       ParserUtil.quoteIdentifier(paramStringBuilder, this.columnName, paramInt);
/*     */     } 
/* 155 */     if ((paramInt & Integer.MIN_VALUE) == 0) {
/* 156 */       SortOrder.typeToString(paramStringBuilder, this.sortType);
/*     */     }
/* 158 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static IndexColumn[] wrap(Column[] paramArrayOfColumn) {
/* 169 */     IndexColumn[] arrayOfIndexColumn = new IndexColumn[paramArrayOfColumn.length];
/* 170 */     for (byte b = 0; b < arrayOfIndexColumn.length; b++) {
/* 171 */       arrayOfIndexColumn[b] = new IndexColumn(paramArrayOfColumn[b]);
/*     */     }
/* 173 */     return arrayOfIndexColumn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void mapColumns(IndexColumn[] paramArrayOfIndexColumn, Table paramTable) {
/* 183 */     for (IndexColumn indexColumn : paramArrayOfIndexColumn) {
/* 184 */       indexColumn.column = paramTable.getColumn(indexColumn.columnName);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 190 */     return getSQL(new StringBuilder("IndexColumn "), 3).toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\IndexColumn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */