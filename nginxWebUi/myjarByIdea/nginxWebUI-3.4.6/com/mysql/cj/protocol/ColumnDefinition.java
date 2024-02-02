package com.mysql.cj.protocol;

import com.mysql.cj.result.Field;
import java.util.Map;

public interface ColumnDefinition extends ProtocolEntity {
   Field[] getFields();

   void setFields(Field[] var1);

   void buildIndexMapping();

   boolean hasBuiltIndexMapping();

   Map<String, Integer> getColumnLabelToIndex();

   void setColumnLabelToIndex(Map<String, Integer> var1);

   Map<String, Integer> getFullColumnNameToIndex();

   void setFullColumnNameToIndex(Map<String, Integer> var1);

   Map<String, Integer> getColumnNameToIndex();

   void setColumnNameToIndex(Map<String, Integer> var1);

   Map<String, Integer> getColumnToIndexCache();

   void setColumnToIndexCache(Map<String, Integer> var1);

   void initializeFrom(ColumnDefinition var1);

   void exportTo(ColumnDefinition var1);

   int findColumn(String var1, boolean var2, int var3);

   boolean hasLargeFields();
}
