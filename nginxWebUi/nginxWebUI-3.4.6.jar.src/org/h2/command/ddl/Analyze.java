/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.index.Cursor;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableType;
/*     */ import org.h2.value.DataType;
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
/*     */ public class Analyze
/*     */   extends DefineCommand
/*     */ {
/*     */   private int sampleRows;
/*     */   private Table table;
/*     */   
/*     */   private static final class SelectivityData
/*     */   {
/*     */     private long distinctCount;
/*     */     private int size;
/*  49 */     private int[] elements = new int[8];
/*  50 */     private int maxSize = 7;
/*     */     private boolean zeroElement;
/*     */     
/*     */     void add(Value param1Value) {
/*  54 */       int i = currentSize();
/*  55 */       if (i >= 10000) {
/*  56 */         this.size = 0;
/*  57 */         Arrays.fill(this.elements, 0);
/*  58 */         this.zeroElement = false;
/*  59 */         this.distinctCount += i;
/*     */       } 
/*  61 */       int j = param1Value.hashCode();
/*  62 */       if (j == 0) {
/*  63 */         this.zeroElement = true;
/*     */       } else {
/*  65 */         if (this.size >= this.maxSize) {
/*  66 */           rehash();
/*     */         }
/*  68 */         add(j);
/*     */       } 
/*     */     }
/*     */     
/*     */     int getSelectivity(long param1Long) {
/*     */       int i;
/*  74 */       if (param1Long == 0L) {
/*  75 */         i = 0;
/*     */       } else {
/*  77 */         i = (int)(100L * (this.distinctCount + currentSize()) / param1Long);
/*  78 */         if (i <= 0) {
/*  79 */           i = 1;
/*     */         }
/*     */       } 
/*  82 */       return i;
/*     */     }
/*     */     
/*     */     private int currentSize() {
/*  86 */       int i = this.size;
/*  87 */       if (this.zeroElement) {
/*  88 */         i++;
/*     */       }
/*  90 */       return i;
/*     */     }
/*     */     
/*     */     private void add(int param1Int) {
/*  94 */       int i = this.elements.length;
/*  95 */       int j = i - 1;
/*  96 */       int k = param1Int & j;
/*  97 */       byte b = 1;
/*     */       do {
/*  99 */         int m = this.elements[k];
/* 100 */         if (m == 0) {
/*     */           
/* 102 */           this.size++;
/* 103 */           this.elements[k] = param1Int; return;
/*     */         } 
/* 105 */         if (m == param1Int) {
/*     */           return;
/*     */         }
/*     */         
/* 109 */         k = k + b++ & j;
/* 110 */       } while (b <= i);
/*     */     }
/*     */ 
/*     */     
/*     */     private void rehash() {
/* 115 */       this.size = 0;
/* 116 */       int[] arrayOfInt = this.elements;
/* 117 */       int i = arrayOfInt.length << 1;
/* 118 */       this.elements = new int[i];
/* 119 */       this.maxSize = (int)(i * 90L / 100L);
/* 120 */       for (int j : arrayOfInt) {
/* 121 */         if (j != 0) {
/* 122 */           add(j);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Analyze(SessionLocal paramSessionLocal) {
/* 139 */     super(paramSessionLocal);
/* 140 */     this.sampleRows = (paramSessionLocal.getDatabase().getSettings()).analyzeSample;
/*     */   }
/*     */   
/*     */   public void setTable(Table paramTable) {
/* 144 */     this.table = paramTable;
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/* 149 */     this.session.getUser().checkAdmin();
/* 150 */     Database database = this.session.getDatabase();
/* 151 */     if (this.table != null) {
/* 152 */       analyzeTable(this.session, this.table, this.sampleRows, true);
/*     */     } else {
/* 154 */       for (Schema schema : database.getAllSchemasNoMeta()) {
/* 155 */         for (Table table : schema.getAllTablesAndViews(null)) {
/* 156 */           analyzeTable(this.session, table, this.sampleRows, true);
/*     */         }
/*     */       } 
/*     */     } 
/* 160 */     return 0L;
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
/*     */   public static void analyzeTable(SessionLocal paramSessionLocal, Table paramTable, int paramInt, boolean paramBoolean) {
/* 172 */     if (paramTable.getTableType() != TableType.TABLE || paramTable
/* 173 */       .isHidden() || paramSessionLocal == null || (!paramBoolean && (paramSessionLocal
/*     */       
/* 175 */       .getDatabase().isSysTableLocked() || paramTable.hasSelectTrigger())) || (paramTable
/* 176 */       .isTemporary() && !paramTable.isGlobalTemporary() && paramSessionLocal
/* 177 */       .findLocalTempTable(paramTable.getName()) == null) || (paramTable
/* 178 */       .isLockedExclusively() && !paramTable.isLockedExclusivelyBy(paramSessionLocal)) || 
/* 179 */       !paramSessionLocal.getUser().hasTableRight(paramTable, 1) || paramSessionLocal
/*     */       
/* 181 */       .getCancel() != 0L) {
/*     */       return;
/*     */     }
/* 184 */     paramTable.lock(paramSessionLocal, 0);
/* 185 */     Column[] arrayOfColumn = paramTable.getColumns();
/* 186 */     int i = arrayOfColumn.length;
/* 187 */     if (i == 0) {
/*     */       return;
/*     */     }
/* 190 */     Cursor cursor = paramTable.getScanIndex(paramSessionLocal).find(paramSessionLocal, null, null);
/* 191 */     if (cursor.next()) {
/* 192 */       SelectivityData[] arrayOfSelectivityData = new SelectivityData[i];
/* 193 */       for (byte b1 = 0; b1 < i; b1++) {
/* 194 */         Column column = arrayOfColumn[b1];
/* 195 */         if (!DataType.isLargeObject(column.getType().getValueType())) {
/* 196 */           arrayOfSelectivityData[b1] = new SelectivityData();
/*     */         }
/*     */       } 
/* 199 */       long l = 0L;
/*     */       do {
/* 201 */         Row row = cursor.get();
/* 202 */         for (byte b = 0; b < i; b++) {
/* 203 */           SelectivityData selectivityData = arrayOfSelectivityData[b];
/* 204 */           if (selectivityData != null) {
/* 205 */             selectivityData.add(row.getValue(b));
/*     */           }
/*     */         } 
/* 208 */         l++;
/* 209 */       } while ((paramInt <= 0 || l < paramInt) && cursor.next());
/* 210 */       for (byte b2 = 0; b2 < i; b2++) {
/* 211 */         SelectivityData selectivityData = arrayOfSelectivityData[b2];
/* 212 */         if (selectivityData != null) {
/* 213 */           arrayOfColumn[b2].setSelectivity(selectivityData.getSelectivity(l));
/*     */         }
/*     */       } 
/*     */     } else {
/* 217 */       for (byte b = 0; b < i; b++) {
/* 218 */         arrayOfColumn[b].setSelectivity(0);
/*     */       }
/*     */     } 
/* 221 */     paramSessionLocal.getDatabase().updateMeta(paramSessionLocal, (DbObject)paramTable);
/*     */   }
/*     */   
/*     */   public void setTop(int paramInt) {
/* 225 */     this.sampleRows = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 230 */     return 21;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\Analyze.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */