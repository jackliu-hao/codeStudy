/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.mvstore.WriteBuffer;
/*     */ import org.h2.mvstore.type.BasicDataType;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.mvstore.type.MetaType;
/*     */ import org.h2.mvstore.type.StatefulDataType;
/*     */ import org.h2.result.RowFactory;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.value.CompareMode;
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
/*     */ public final class RowDataType
/*     */   extends BasicDataType<SearchRow>
/*     */   implements StatefulDataType<Database>
/*     */ {
/*     */   private final ValueDataType valueDataType;
/*     */   private final int[] sortTypes;
/*     */   private final int[] indexes;
/*     */   private final int columnCount;
/*     */   private final boolean storeKeys;
/*     */   
/*     */   public RowDataType(CastDataProvider paramCastDataProvider, CompareMode paramCompareMode, DataHandler paramDataHandler, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt, boolean paramBoolean) {
/*  39 */     this.valueDataType = new ValueDataType(paramCastDataProvider, paramCompareMode, paramDataHandler, paramArrayOfint1);
/*  40 */     this.sortTypes = paramArrayOfint1;
/*  41 */     this.indexes = paramArrayOfint2;
/*  42 */     this.columnCount = paramInt;
/*  43 */     this.storeKeys = paramBoolean;
/*  44 */     assert paramArrayOfint2 == null || paramArrayOfint1.length == paramArrayOfint2.length;
/*     */   }
/*     */   
/*     */   public int[] getIndexes() {
/*  48 */     return this.indexes;
/*     */   }
/*     */   
/*     */   public RowFactory getRowFactory() {
/*  52 */     return this.valueDataType.getRowFactory();
/*     */   }
/*     */   
/*     */   public void setRowFactory(RowFactory paramRowFactory) {
/*  56 */     this.valueDataType.setRowFactory(paramRowFactory);
/*     */   }
/*     */   
/*     */   public int getColumnCount() {
/*  60 */     return this.columnCount;
/*     */   }
/*     */   
/*     */   public boolean isStoreKeys() {
/*  64 */     return this.storeKeys;
/*     */   }
/*     */ 
/*     */   
/*     */   public SearchRow[] createStorage(int paramInt) {
/*  69 */     return new SearchRow[paramInt];
/*     */   }
/*     */ 
/*     */   
/*     */   public int compare(SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/*  74 */     if (paramSearchRow1 == paramSearchRow2) {
/*  75 */       return 0;
/*     */     }
/*  77 */     if (this.indexes == null) {
/*  78 */       int i = paramSearchRow1.getColumnCount();
/*  79 */       assert i == paramSearchRow2.getColumnCount() : i + " != " + paramSearchRow2.getColumnCount();
/*  80 */       for (byte b = 0; b < i; b++) {
/*  81 */         int j = this.valueDataType.compareValues(paramSearchRow1.getValue(b), paramSearchRow2.getValue(b), this.sortTypes[b]);
/*  82 */         if (j != 0) {
/*  83 */           return j;
/*     */         }
/*     */       } 
/*  86 */       return 0;
/*     */     } 
/*  88 */     return compareSearchRows(paramSearchRow1, paramSearchRow2);
/*     */   }
/*     */ 
/*     */   
/*     */   private int compareSearchRows(SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/*  93 */     for (byte b = 0; b < this.indexes.length; b++) {
/*  94 */       int i = this.indexes[b];
/*  95 */       Value value1 = paramSearchRow1.getValue(i);
/*  96 */       Value value2 = paramSearchRow2.getValue(i);
/*  97 */       if (value1 == null || value2 == null) {
/*     */         break;
/*     */       }
/*     */       
/* 101 */       int j = this.valueDataType.compareValues(value1, value2, this.sortTypes[b]);
/* 102 */       if (j != 0) {
/* 103 */         return j;
/*     */       }
/*     */     } 
/* 106 */     long l1 = paramSearchRow1.getKey();
/* 107 */     long l2 = paramSearchRow2.getKey();
/* 108 */     return (l1 == SearchRow.MATCH_ALL_ROW_KEY || l2 == SearchRow.MATCH_ALL_ROW_KEY) ? 0 : 
/* 109 */       Long.compare(l1, l2);
/*     */   }
/*     */ 
/*     */   
/*     */   public int binarySearch(SearchRow paramSearchRow, Object paramObject, int paramInt1, int paramInt2) {
/* 114 */     return binarySearch(paramSearchRow, (SearchRow[])paramObject, paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */   public int binarySearch(SearchRow paramSearchRow, SearchRow[] paramArrayOfSearchRow, int paramInt1, int paramInt2) {
/* 118 */     int i = 0;
/* 119 */     int j = paramInt1 - 1;
/*     */ 
/*     */ 
/*     */     
/* 123 */     int k = paramInt2 - 1;
/* 124 */     if (k < 0 || k > j) {
/* 125 */       k = j >>> 1;
/*     */     }
/* 127 */     while (i <= j) {
/* 128 */       int m = compareSearchRows(paramSearchRow, paramArrayOfSearchRow[k]);
/* 129 */       if (m > 0) {
/* 130 */         i = k + 1;
/* 131 */       } else if (m < 0) {
/* 132 */         j = k - 1;
/*     */       } else {
/* 134 */         return k;
/*     */       } 
/* 136 */       k = i + j >>> 1;
/*     */     } 
/* 138 */     return -(i + 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory(SearchRow paramSearchRow) {
/* 143 */     return paramSearchRow.getMemory();
/*     */   }
/*     */ 
/*     */   
/*     */   public SearchRow read(ByteBuffer paramByteBuffer) {
/* 148 */     RowFactory rowFactory = this.valueDataType.getRowFactory();
/* 149 */     SearchRow searchRow = rowFactory.createRow();
/* 150 */     if (this.storeKeys) {
/* 151 */       searchRow.setKey(DataUtils.readVarLong(paramByteBuffer));
/*     */     }
/* 153 */     TypeInfo[] arrayOfTypeInfo = rowFactory.getColumnTypes();
/* 154 */     if (this.indexes == null) {
/* 155 */       int i = searchRow.getColumnCount();
/* 156 */       for (byte b = 0; b < i; b++) {
/* 157 */         searchRow.setValue(b, this.valueDataType.readValue(paramByteBuffer, (arrayOfTypeInfo != null) ? arrayOfTypeInfo[b] : null));
/*     */       }
/*     */     } else {
/* 160 */       for (int i : this.indexes) {
/* 161 */         searchRow.setValue(i, this.valueDataType.readValue(paramByteBuffer, (arrayOfTypeInfo != null) ? arrayOfTypeInfo[i] : null));
/*     */       }
/*     */     } 
/* 164 */     return searchRow;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(WriteBuffer paramWriteBuffer, SearchRow paramSearchRow) {
/* 169 */     if (this.storeKeys) {
/* 170 */       paramWriteBuffer.putVarLong(paramSearchRow.getKey());
/*     */     }
/* 172 */     if (this.indexes == null) {
/* 173 */       int i = paramSearchRow.getColumnCount();
/* 174 */       for (byte b = 0; b < i; b++) {
/* 175 */         this.valueDataType.write(paramWriteBuffer, paramSearchRow.getValue(b));
/*     */       }
/*     */     } else {
/* 178 */       for (int i : this.indexes) {
/* 179 */         this.valueDataType.write(paramWriteBuffer, paramSearchRow.getValue(i));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 186 */     if (paramObject == this)
/* 187 */       return true; 
/* 188 */     if (paramObject == null || paramObject.getClass() != RowDataType.class) {
/* 189 */       return false;
/*     */     }
/* 191 */     RowDataType rowDataType = (RowDataType)paramObject;
/* 192 */     return (this.columnCount == rowDataType.columnCount && 
/* 193 */       Arrays.equals(this.indexes, rowDataType.indexes) && 
/* 194 */       Arrays.equals(this.sortTypes, rowDataType.sortTypes) && this.valueDataType
/* 195 */       .equals(rowDataType.valueDataType));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 200 */     int i = super.hashCode();
/* 201 */     i = i * 31 + this.columnCount;
/* 202 */     i = i * 31 + Arrays.hashCode(this.indexes);
/* 203 */     i = i * 31 + Arrays.hashCode(this.sortTypes);
/* 204 */     i = i * 31 + this.valueDataType.hashCode();
/* 205 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public void save(WriteBuffer paramWriteBuffer, MetaType<Database> paramMetaType) {
/* 210 */     paramWriteBuffer.putVarInt(this.columnCount);
/* 211 */     writeIntArray(paramWriteBuffer, this.sortTypes);
/* 212 */     writeIntArray(paramWriteBuffer, this.indexes);
/* 213 */     paramWriteBuffer.put(this.storeKeys ? 1 : 0);
/*     */   }
/*     */   
/*     */   private static void writeIntArray(WriteBuffer paramWriteBuffer, int[] paramArrayOfint) {
/* 217 */     if (paramArrayOfint == null) {
/* 218 */       paramWriteBuffer.putVarInt(0);
/*     */     } else {
/* 220 */       paramWriteBuffer.putVarInt(paramArrayOfint.length + 1);
/* 221 */       for (int i : paramArrayOfint) {
/* 222 */         paramWriteBuffer.putVarInt(i);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Factory getFactory() {
/* 229 */     return FACTORY;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 234 */   private static final Factory FACTORY = new Factory();
/*     */   
/*     */   public static final class Factory
/*     */     implements StatefulDataType.Factory<Database>
/*     */   {
/*     */     public RowDataType create(ByteBuffer param1ByteBuffer, MetaType<Database> param1MetaType, Database param1Database) {
/* 240 */       int i = DataUtils.readVarInt(param1ByteBuffer);
/* 241 */       int[] arrayOfInt1 = readIntArray(param1ByteBuffer);
/* 242 */       int[] arrayOfInt2 = readIntArray(param1ByteBuffer);
/* 243 */       boolean bool = (param1ByteBuffer.get() != 0) ? true : false;
/* 244 */       CompareMode compareMode = (param1Database == null) ? CompareMode.getInstance(null, 0) : param1Database.getCompareMode();
/* 245 */       RowFactory rowFactory = RowFactory.getDefaultRowFactory().createRowFactory((CastDataProvider)param1Database, compareMode, (DataHandler)param1Database, arrayOfInt1, arrayOfInt2, null, i, bool);
/*     */       
/* 247 */       return rowFactory.getRowDataType();
/*     */     }
/*     */     
/*     */     private static int[] readIntArray(ByteBuffer param1ByteBuffer) {
/* 251 */       int i = DataUtils.readVarInt(param1ByteBuffer) - 1;
/* 252 */       if (i < 0) {
/* 253 */         return null;
/*     */       }
/* 255 */       int[] arrayOfInt = new int[i];
/* 256 */       for (byte b = 0; b < arrayOfInt.length; b++) {
/* 257 */         arrayOfInt[b] = DataUtils.readVarInt(param1ByteBuffer);
/*     */       }
/* 259 */       return arrayOfInt;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\RowDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */