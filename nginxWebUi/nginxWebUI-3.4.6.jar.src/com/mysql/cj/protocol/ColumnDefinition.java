package com.mysql.cj.protocol;

import com.mysql.cj.result.Field;
import java.util.Map;

public interface ColumnDefinition extends ProtocolEntity {
  Field[] getFields();
  
  void setFields(Field[] paramArrayOfField);
  
  void buildIndexMapping();
  
  boolean hasBuiltIndexMapping();
  
  Map<String, Integer> getColumnLabelToIndex();
  
  void setColumnLabelToIndex(Map<String, Integer> paramMap);
  
  Map<String, Integer> getFullColumnNameToIndex();
  
  void setFullColumnNameToIndex(Map<String, Integer> paramMap);
  
  Map<String, Integer> getColumnNameToIndex();
  
  void setColumnNameToIndex(Map<String, Integer> paramMap);
  
  Map<String, Integer> getColumnToIndexCache();
  
  void setColumnToIndexCache(Map<String, Integer> paramMap);
  
  void initializeFrom(ColumnDefinition paramColumnDefinition);
  
  void exportTo(ColumnDefinition paramColumnDefinition);
  
  int findColumn(String paramString, boolean paramBoolean, int paramInt);
  
  boolean hasLargeFields();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\ColumnDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */