/*     */ package org.h2.result;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.mvstore.db.RowDataType;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.value.CompareMode;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Typed;
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
/*     */ public abstract class RowFactory
/*     */ {
/*     */   private static final class Holder
/*     */   {
/*  26 */     static final RowFactory EFFECTIVE = RowFactory.DefaultRowFactory.INSTANCE;
/*     */   }
/*     */   
/*     */   public static DefaultRowFactory getDefaultRowFactory() {
/*  30 */     return DefaultRowFactory.INSTANCE;
/*     */   }
/*     */   
/*     */   public static RowFactory getRowFactory() {
/*  34 */     return Holder.EFFECTIVE;
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
/*     */   public RowFactory createRowFactory(CastDataProvider paramCastDataProvider, CompareMode paramCompareMode, DataHandler paramDataHandler, Typed[] paramArrayOfTyped, IndexColumn[] paramArrayOfIndexColumn, boolean paramBoolean) {
/*  50 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Row createRow(Value[] paramArrayOfValue, int paramInt);
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract SearchRow createRow();
/*     */ 
/*     */   
/*     */   public abstract RowDataType getRowDataType();
/*     */ 
/*     */   
/*     */   public abstract int[] getIndexes();
/*     */ 
/*     */   
/*     */   public abstract TypeInfo[] getColumnTypes();
/*     */ 
/*     */   
/*     */   public abstract int getColumnCount();
/*     */ 
/*     */   
/*     */   public abstract boolean getStoreKeys();
/*     */ 
/*     */   
/*     */   public static final class DefaultRowFactory
/*     */     extends RowFactory
/*     */   {
/*     */     private final RowDataType dataType;
/*     */     
/*     */     private final int columnCount;
/*     */     
/*     */     private final int[] indexes;
/*     */     
/*     */     private TypeInfo[] columnTypes;
/*     */     
/*     */     private final int[] map;
/*     */     
/*  90 */     public static final DefaultRowFactory INSTANCE = new DefaultRowFactory();
/*     */     
/*     */     DefaultRowFactory() {
/*  93 */       this(new RowDataType(null, CompareMode.getInstance(null, 0), null, null, null, 0, true), 0, null, null);
/*     */     }
/*     */     
/*     */     private DefaultRowFactory(RowDataType param1RowDataType, int param1Int, int[] param1ArrayOfint, TypeInfo[] param1ArrayOfTypeInfo) {
/*  97 */       this.dataType = param1RowDataType;
/*  98 */       this.columnCount = param1Int;
/*  99 */       this.indexes = param1ArrayOfint;
/* 100 */       if (param1ArrayOfint == null) {
/* 101 */         this.map = null;
/*     */       } else {
/* 103 */         this.map = new int[param1Int]; byte b; int i;
/* 104 */         for (b = 0, i = param1ArrayOfint.length; b < i;) {
/* 105 */           this.map[param1ArrayOfint[b]] = ++b;
/*     */         }
/*     */       } 
/* 108 */       this.columnTypes = param1ArrayOfTypeInfo;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public RowFactory createRowFactory(CastDataProvider param1CastDataProvider, CompareMode param1CompareMode, DataHandler param1DataHandler, Typed[] param1ArrayOfTyped, IndexColumn[] param1ArrayOfIndexColumn, boolean param1Boolean) {
/* 114 */       int[] arrayOfInt1 = null;
/* 115 */       int[] arrayOfInt2 = null;
/* 116 */       TypeInfo[] arrayOfTypeInfo = null;
/* 117 */       int i = 0;
/* 118 */       if (param1ArrayOfTyped != null) {
/* 119 */         i = param1ArrayOfTyped.length;
/* 120 */         if (param1ArrayOfIndexColumn == null) {
/* 121 */           arrayOfInt2 = new int[i];
/* 122 */           for (byte b1 = 0; b1 < i; b1++) {
/* 123 */             arrayOfInt2[b1] = 0;
/*     */           }
/*     */         } else {
/* 126 */           int j = param1ArrayOfIndexColumn.length;
/* 127 */           arrayOfInt1 = new int[j];
/* 128 */           arrayOfInt2 = new int[j];
/* 129 */           for (byte b1 = 0; b1 < j; b1++) {
/* 130 */             IndexColumn indexColumn = param1ArrayOfIndexColumn[b1];
/* 131 */             arrayOfInt1[b1] = indexColumn.column.getColumnId();
/* 132 */             arrayOfInt2[b1] = indexColumn.sortType;
/*     */           } 
/*     */         } 
/* 135 */         arrayOfTypeInfo = new TypeInfo[i];
/* 136 */         for (byte b = 0; b < i; b++) {
/* 137 */           arrayOfTypeInfo[b] = param1ArrayOfTyped[b].getType();
/*     */         }
/*     */       } 
/* 140 */       return createRowFactory(param1CastDataProvider, param1CompareMode, param1DataHandler, arrayOfInt2, arrayOfInt1, arrayOfTypeInfo, i, param1Boolean);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public RowFactory createRowFactory(CastDataProvider param1CastDataProvider, CompareMode param1CompareMode, DataHandler param1DataHandler, int[] param1ArrayOfint1, int[] param1ArrayOfint2, TypeInfo[] param1ArrayOfTypeInfo, int param1Int, boolean param1Boolean) {
/* 159 */       RowDataType rowDataType = new RowDataType(param1CastDataProvider, param1CompareMode, param1DataHandler, param1ArrayOfint1, param1ArrayOfint2, param1Int, param1Boolean);
/*     */       
/* 161 */       DefaultRowFactory defaultRowFactory = new DefaultRowFactory(rowDataType, param1Int, param1ArrayOfint2, param1ArrayOfTypeInfo);
/* 162 */       rowDataType.setRowFactory(defaultRowFactory);
/* 163 */       return defaultRowFactory;
/*     */     }
/*     */ 
/*     */     
/*     */     public Row createRow(Value[] param1ArrayOfValue, int param1Int) {
/* 168 */       return new DefaultRow(param1ArrayOfValue, param1Int);
/*     */     }
/*     */ 
/*     */     
/*     */     public SearchRow createRow() {
/* 173 */       if (this.indexes == null)
/* 174 */         return new DefaultRow(this.columnCount); 
/* 175 */       if (this.indexes.length == 1) {
/* 176 */         return new SimpleRowValue(this.columnCount, this.indexes[0]);
/*     */       }
/* 178 */       return new Sparse(this.columnCount, this.indexes.length, this.map);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public RowDataType getRowDataType() {
/* 184 */       return this.dataType;
/*     */     }
/*     */ 
/*     */     
/*     */     public int[] getIndexes() {
/* 189 */       return this.indexes;
/*     */     }
/*     */ 
/*     */     
/*     */     public TypeInfo[] getColumnTypes() {
/* 194 */       return this.columnTypes;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getColumnCount() {
/* 199 */       return this.columnCount;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean getStoreKeys() {
/* 204 */       return this.dataType.isStoreKeys();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\RowFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */