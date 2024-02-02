package com.mysql.cj.result;

import com.mysql.cj.protocol.ColumnDefinition;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DefaultColumnDefinition implements ColumnDefinition {
   protected Field[] fields;
   private Map<String, Integer> columnLabelToIndex = null;
   private Map<String, Integer> columnToIndexCache = new HashMap();
   private Map<String, Integer> fullColumnNameToIndex = null;
   private Map<String, Integer> columnNameToIndex = null;
   private boolean builtIndexMapping = false;

   public DefaultColumnDefinition() {
   }

   public DefaultColumnDefinition(Field[] fields) {
      this.fields = fields;
   }

   public Field[] getFields() {
      return this.fields;
   }

   public void setFields(Field[] fields) {
      this.fields = fields;
   }

   public void buildIndexMapping() {
      int numFields = this.fields.length;
      this.columnLabelToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
      this.fullColumnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
      this.columnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);

      for(int i = numFields - 1; i >= 0; --i) {
         Integer index = i;
         String columnName = this.fields[i].getOriginalName();
         String columnLabel = this.fields[i].getName();
         String fullColumnName = this.fields[i].getFullName();
         if (columnLabel != null) {
            this.columnLabelToIndex.put(columnLabel, index);
         }

         if (fullColumnName != null) {
            this.fullColumnNameToIndex.put(fullColumnName, index);
         }

         if (columnName != null) {
            this.columnNameToIndex.put(columnName, index);
         }
      }

      this.builtIndexMapping = true;
   }

   public boolean hasBuiltIndexMapping() {
      return this.builtIndexMapping;
   }

   public Map<String, Integer> getColumnLabelToIndex() {
      return this.columnLabelToIndex;
   }

   public void setColumnLabelToIndex(Map<String, Integer> columnLabelToIndex) {
      this.columnLabelToIndex = columnLabelToIndex;
   }

   public Map<String, Integer> getFullColumnNameToIndex() {
      return this.fullColumnNameToIndex;
   }

   public void setFullColumnNameToIndex(Map<String, Integer> fullColNameToIndex) {
      this.fullColumnNameToIndex = fullColNameToIndex;
   }

   public Map<String, Integer> getColumnNameToIndex() {
      return this.columnNameToIndex;
   }

   public void setColumnNameToIndex(Map<String, Integer> colNameToIndex) {
      this.columnNameToIndex = colNameToIndex;
   }

   public Map<String, Integer> getColumnToIndexCache() {
      return this.columnToIndexCache;
   }

   public void setColumnToIndexCache(Map<String, Integer> columnToIndexCache) {
      this.columnToIndexCache = columnToIndexCache;
   }

   public void initializeFrom(ColumnDefinition columnDefinition) {
      this.fields = columnDefinition.getFields();
      this.columnLabelToIndex = columnDefinition.getColumnNameToIndex();
      this.fullColumnNameToIndex = columnDefinition.getFullColumnNameToIndex();
      this.builtIndexMapping = true;
   }

   public void exportTo(ColumnDefinition columnDefinition) {
      columnDefinition.setFields(this.fields);
      columnDefinition.setColumnNameToIndex(this.columnLabelToIndex);
      columnDefinition.setFullColumnNameToIndex(this.fullColumnNameToIndex);
   }

   public int findColumn(String columnName, boolean useColumnNamesInFindColumn, int indexBase) {
      if (!this.hasBuiltIndexMapping()) {
         this.buildIndexMapping();
      }

      Integer index = (Integer)this.columnToIndexCache.get(columnName);
      if (index != null) {
         return index + indexBase;
      } else {
         index = (Integer)this.columnLabelToIndex.get(columnName);
         if (index == null && useColumnNamesInFindColumn) {
            index = (Integer)this.columnNameToIndex.get(columnName);
         }

         if (index == null) {
            index = (Integer)this.fullColumnNameToIndex.get(columnName);
         }

         if (index != null) {
            this.columnToIndexCache.put(columnName, index);
            return index + indexBase;
         } else {
            for(int i = 0; i < this.fields.length; ++i) {
               if (this.fields[i].getName().equalsIgnoreCase(columnName)) {
                  return i + indexBase;
               }

               if (this.fields[i].getFullName().equalsIgnoreCase(columnName)) {
                  return i + indexBase;
               }
            }

            return -1;
         }
      }
   }

   public boolean hasLargeFields() {
      if (this.fields != null) {
         int i = 0;

         while(i < this.fields.length) {
            switch (this.fields[i].getMysqlType()) {
               case BLOB:
               case MEDIUMBLOB:
               case LONGBLOB:
               case TEXT:
               case MEDIUMTEXT:
               case LONGTEXT:
               case JSON:
                  return true;
               default:
                  ++i;
            }
         }
      }

      return false;
   }
}
