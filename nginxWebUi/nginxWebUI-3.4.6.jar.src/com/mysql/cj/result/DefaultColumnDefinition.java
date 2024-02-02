/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.MysqlType;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
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
/*     */ 
/*     */ 
/*     */ public class DefaultColumnDefinition
/*     */   implements ColumnDefinition
/*     */ {
/*     */   protected Field[] fields;
/*  47 */   private Map<String, Integer> columnLabelToIndex = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   private Map<String, Integer> columnToIndexCache = new HashMap<>();
/*     */ 
/*     */   
/*  56 */   private Map<String, Integer> fullColumnNameToIndex = null;
/*     */ 
/*     */   
/*  59 */   private Map<String, Integer> columnNameToIndex = null;
/*     */   
/*     */   private boolean builtIndexMapping = false;
/*     */ 
/*     */   
/*     */   public DefaultColumnDefinition() {}
/*     */   
/*     */   public DefaultColumnDefinition(Field[] fields) {
/*  67 */     this.fields = fields;
/*     */   }
/*     */   
/*     */   public Field[] getFields() {
/*  71 */     return this.fields;
/*     */   }
/*     */   
/*     */   public void setFields(Field[] fields) {
/*  75 */     this.fields = fields;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void buildIndexMapping() {
/*  84 */     int numFields = this.fields.length;
/*  85 */     this.columnLabelToIndex = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
/*  86 */     this.fullColumnNameToIndex = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
/*  87 */     this.columnNameToIndex = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  96 */     for (int i = numFields - 1; i >= 0; i--) {
/*  97 */       Integer index = Integer.valueOf(i);
/*  98 */       String columnName = this.fields[i].getOriginalName();
/*  99 */       String columnLabel = this.fields[i].getName();
/* 100 */       String fullColumnName = this.fields[i].getFullName();
/*     */       
/* 102 */       if (columnLabel != null) {
/* 103 */         this.columnLabelToIndex.put(columnLabel, index);
/*     */       }
/*     */       
/* 106 */       if (fullColumnName != null) {
/* 107 */         this.fullColumnNameToIndex.put(fullColumnName, index);
/*     */       }
/*     */       
/* 110 */       if (columnName != null) {
/* 111 */         this.columnNameToIndex.put(columnName, index);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 116 */     this.builtIndexMapping = true;
/*     */   }
/*     */   
/*     */   public boolean hasBuiltIndexMapping() {
/* 120 */     return this.builtIndexMapping;
/*     */   }
/*     */   
/*     */   public Map<String, Integer> getColumnLabelToIndex() {
/* 124 */     return this.columnLabelToIndex;
/*     */   }
/*     */   
/*     */   public void setColumnLabelToIndex(Map<String, Integer> columnLabelToIndex) {
/* 128 */     this.columnLabelToIndex = columnLabelToIndex;
/*     */   }
/*     */   
/*     */   public Map<String, Integer> getFullColumnNameToIndex() {
/* 132 */     return this.fullColumnNameToIndex;
/*     */   }
/*     */   
/*     */   public void setFullColumnNameToIndex(Map<String, Integer> fullColNameToIndex) {
/* 136 */     this.fullColumnNameToIndex = fullColNameToIndex;
/*     */   }
/*     */   
/*     */   public Map<String, Integer> getColumnNameToIndex() {
/* 140 */     return this.columnNameToIndex;
/*     */   }
/*     */   
/*     */   public void setColumnNameToIndex(Map<String, Integer> colNameToIndex) {
/* 144 */     this.columnNameToIndex = colNameToIndex;
/*     */   }
/*     */   
/*     */   public Map<String, Integer> getColumnToIndexCache() {
/* 148 */     return this.columnToIndexCache;
/*     */   }
/*     */   
/*     */   public void setColumnToIndexCache(Map<String, Integer> columnToIndexCache) {
/* 152 */     this.columnToIndexCache = columnToIndexCache;
/*     */   }
/*     */   
/*     */   public void initializeFrom(ColumnDefinition columnDefinition) {
/* 156 */     this.fields = columnDefinition.getFields();
/* 157 */     this.columnLabelToIndex = columnDefinition.getColumnNameToIndex();
/* 158 */     this.fullColumnNameToIndex = columnDefinition.getFullColumnNameToIndex();
/* 159 */     this.builtIndexMapping = true;
/*     */   }
/*     */   
/*     */   public void exportTo(ColumnDefinition columnDefinition) {
/* 163 */     columnDefinition.setFields(this.fields);
/* 164 */     columnDefinition.setColumnNameToIndex(this.columnLabelToIndex);
/* 165 */     columnDefinition.setFullColumnNameToIndex(this.fullColumnNameToIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int findColumn(String columnName, boolean useColumnNamesInFindColumn, int indexBase) {
/* 172 */     if (!hasBuiltIndexMapping()) {
/* 173 */       buildIndexMapping();
/*     */     }
/*     */     
/* 176 */     Integer index = this.columnToIndexCache.get(columnName);
/*     */     
/* 178 */     if (index != null) {
/* 179 */       return index.intValue() + indexBase;
/*     */     }
/*     */     
/* 182 */     index = this.columnLabelToIndex.get(columnName);
/*     */     
/* 184 */     if (index == null && useColumnNamesInFindColumn) {
/* 185 */       index = this.columnNameToIndex.get(columnName);
/*     */     }
/*     */     
/* 188 */     if (index == null) {
/* 189 */       index = this.fullColumnNameToIndex.get(columnName);
/*     */     }
/*     */     
/* 192 */     if (index != null) {
/* 193 */       this.columnToIndexCache.put(columnName, index);
/*     */       
/* 195 */       return index.intValue() + indexBase;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 200 */     for (int i = 0; i < this.fields.length; i++) {
/* 201 */       if (this.fields[i].getName().equalsIgnoreCase(columnName))
/* 202 */         return i + indexBase; 
/* 203 */       if (this.fields[i].getFullName().equalsIgnoreCase(columnName)) {
/* 204 */         return i + indexBase;
/*     */       }
/*     */     } 
/*     */     
/* 208 */     return -1;
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
/*     */   public boolean hasLargeFields() {
/* 220 */     if (this.fields != null) {
/* 221 */       for (int i = 0; i < this.fields.length; i++) {
/* 222 */         switch (this.fields[i].getMysqlType()) {
/*     */           case BLOB:
/*     */           case MEDIUMBLOB:
/*     */           case LONGBLOB:
/*     */           case TEXT:
/*     */           case MEDIUMTEXT:
/*     */           case LONGTEXT:
/*     */           case JSON:
/* 230 */             return true;
/*     */         } 
/*     */ 
/*     */       
/*     */       } 
/*     */     }
/* 236 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\DefaultColumnDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */