package com.mysql.cj.xdevapi;

import com.mysql.cj.CharsetMapping;
import com.mysql.cj.MysqlType;
import com.mysql.cj.result.Field;

public class ColumnImpl implements Column {
   private Field field;

   public ColumnImpl(Field f) {
      this.field = f;
   }

   public String getSchemaName() {
      return this.field.getDatabaseName();
   }

   public String getTableName() {
      return this.field.getOriginalTableName();
   }

   public String getTableLabel() {
      return this.field.getTableName();
   }

   public String getColumnName() {
      return this.field.getOriginalName();
   }

   public String getColumnLabel() {
      return this.field.getName();
   }

   public Type getType() {
      int len;
      switch (this.field.getMysqlType()) {
         case BIT:
            return Type.BIT;
         case BIGINT:
            len = (int)this.field.getLength();
            if (len < 5) {
               return Type.TINYINT;
            } else if (len < 7) {
               return Type.SMALLINT;
            } else if (len < 10) {
               return Type.MEDIUMINT;
            } else if (len < 12) {
               return Type.INT;
            } else {
               if (len < 21) {
                  return Type.BIGINT;
               }

               throw new IllegalArgumentException("Unknown field length `" + this.field.getLength() + "` for signed int");
            }
         case BIGINT_UNSIGNED:
            len = (int)this.field.getLength();
            if (len < 4) {
               return Type.TINYINT;
            } else if (len < 6) {
               return Type.SMALLINT;
            } else if (len < 9) {
               return Type.MEDIUMINT;
            } else if (len < 11) {
               return Type.INT;
            } else {
               if (len < 21) {
                  return Type.BIGINT;
               }

               throw new IllegalArgumentException("Unknown field length `" + this.field.getLength() + "` for unsigned int");
            }
         case FLOAT:
         case FLOAT_UNSIGNED:
            return Type.FLOAT;
         case DECIMAL:
         case DECIMAL_UNSIGNED:
            return Type.DECIMAL;
         case DOUBLE:
         case DOUBLE_UNSIGNED:
            return Type.DOUBLE;
         case CHAR:
         case VARCHAR:
            return Type.STRING;
         case JSON:
            return Type.JSON;
         case VARBINARY:
            return Type.BYTES;
         case TIME:
            return Type.TIME;
         case DATETIME:
            len = (int)this.field.getLength();
            if (len == 10) {
               return Type.DATE;
            } else {
               if (len > 18 && len < 27) {
                  return Type.DATETIME;
               }

               throw new IllegalArgumentException("Unknown field length `" + this.field.getLength() + "` for datetime");
            }
         case TIMESTAMP:
            return Type.TIMESTAMP;
         case SET:
            return Type.SET;
         case ENUM:
            return Type.ENUM;
         case GEOMETRY:
            return Type.GEOMETRY;
         default:
            throw new IllegalArgumentException("Unknown type in metadata: " + this.field.getMysqlType());
      }
   }

   public long getLength() {
      return this.field.getLength();
   }

   public int getFractionalDigits() {
      return this.field.getDecimals();
   }

   public boolean isNumberSigned() {
      return MysqlType.isSigned(this.field.getMysqlType());
   }

   public String getCollationName() {
      return CharsetMapping.getStaticCollationNameForCollationIndex(this.field.getCollationIndex());
   }

   public String getCharacterSetName() {
      return CharsetMapping.getStaticMysqlCharsetNameForCollationIndex(this.field.getCollationIndex());
   }

   public boolean isPadded() {
      return this.field.isZeroFill() || this.field.getMysqlType() == MysqlType.CHAR;
   }

   public boolean isNullable() {
      return !this.field.isNotNull();
   }

   public boolean isAutoIncrement() {
      return this.field.isAutoIncrement();
   }

   public boolean isPrimaryKey() {
      return this.field.isPrimaryKey();
   }

   public boolean isUniqueKey() {
      return this.field.isUniqueKey();
   }

   public boolean isPartKey() {
      return this.field.isMultipleKey();
   }
}
