package com.mysql.cj.xdevapi;

public interface Column {
   String getSchemaName();

   String getTableName();

   String getTableLabel();

   String getColumnName();

   String getColumnLabel();

   Type getType();

   long getLength();

   int getFractionalDigits();

   boolean isNumberSigned();

   String getCollationName();

   String getCharacterSetName();

   boolean isPadded();

   boolean isNullable();

   boolean isAutoIncrement();

   boolean isPrimaryKey();

   boolean isUniqueKey();

   boolean isPartKey();
}
