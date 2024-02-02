package com.mysql.cj.jdbc.result;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.Session;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.result.Field;
import java.sql.SQLException;

public class ResultSetMetaData implements java.sql.ResultSetMetaData {
   private Session session;
   private Field[] fields;
   boolean useOldAliasBehavior = false;
   boolean treatYearAsDate = true;
   private ExceptionInterceptor exceptionInterceptor;

   private static int clampedGetLength(Field f) {
      long fieldLength = f.getLength();
      if (fieldLength > 2147483647L) {
         fieldLength = 2147483647L;
      }

      return (int)fieldLength;
   }

   public ResultSetMetaData(Session session, Field[] fields, boolean useOldAliasBehavior, boolean treatYearAsDate, ExceptionInterceptor exceptionInterceptor) {
      this.session = session;
      this.fields = fields;
      this.useOldAliasBehavior = useOldAliasBehavior;
      this.treatYearAsDate = treatYearAsDate;
      this.exceptionInterceptor = exceptionInterceptor;
   }

   public String getCatalogName(int column) throws SQLException {
      try {
         if (this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) {
            return "";
         } else {
            String database = this.getField(column).getDatabaseName();
            return database == null ? "" : database;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.exceptionInterceptor);
      }
   }

   public String getColumnCharacterEncoding(int column) throws SQLException {
      return this.getField(column).getEncoding();
   }

   public String getColumnCharacterSet(int column) throws SQLException {
      return this.session.getServerSession().getCharsetSettings().getMysqlCharsetNameForCollationIndex(this.getField(column).getCollationIndex());
   }

   public String getColumnClassName(int column) throws SQLException {
      try {
         Field f = this.getField(column);
         switch (f.getMysqlType()) {
            case YEAR:
               if (!this.treatYearAsDate) {
                  return Short.class.getName();
               }

               return f.getMysqlType().getClassName();
            default:
               return f.getMysqlType().getClassName();
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.exceptionInterceptor);
      }
   }

   public int getColumnCount() throws SQLException {
      try {
         return this.fields.length;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.exceptionInterceptor);
      }
   }

   public int getColumnDisplaySize(int column) throws SQLException {
      try {
         Field f = this.getField(column);
         int lengthInBytes = clampedGetLength(f);
         return lengthInBytes / this.session.getServerSession().getCharsetSettings().getMaxBytesPerChar(f.getCollationIndex(), f.getEncoding());
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.exceptionInterceptor);
      }
   }

   public String getColumnLabel(int column) throws SQLException {
      try {
         return this.useOldAliasBehavior ? this.getColumnName(column) : this.getField(column).getColumnLabel();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public String getColumnName(int column) throws SQLException {
      try {
         if (this.useOldAliasBehavior) {
            return this.getField(column).getName();
         } else {
            String name = this.getField(column).getOriginalName();
            return name == null ? this.getField(column).getName() : name;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.exceptionInterceptor);
      }
   }

   public int getColumnType(int column) throws SQLException {
      try {
         return this.getField(column).getJavaType();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public String getColumnTypeName(int column) throws SQLException {
      try {
         Field field = this.getField(column);
         return field.getMysqlType().getName();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.exceptionInterceptor);
      }
   }

   protected Field getField(int columnIndex) throws SQLException {
      if (columnIndex >= 1 && columnIndex <= this.fields.length) {
         return this.fields[columnIndex - 1];
      } else {
         throw SQLError.createSQLException(Messages.getString("ResultSetMetaData.46"), "S1002", this.exceptionInterceptor);
      }
   }

   public int getPrecision(int column) throws SQLException {
      try {
         Field f = this.getField(column);
         switch (f.getMysqlType()) {
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
               return clampedGetLength(f);
            default:
               return f.getMysqlType().isDecimal() ? clampedGetLength(f) : clampedGetLength(f) / this.session.getServerSession().getCharsetSettings().getMaxBytesPerChar(f.getCollationIndex(), f.getEncoding());
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.exceptionInterceptor);
      }
   }

   public int getScale(int column) throws SQLException {
      try {
         Field f = this.getField(column);
         return f.getMysqlType().isDecimal() ? f.getDecimals() : 0;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.exceptionInterceptor);
      }
   }

   public String getSchemaName(int column) throws SQLException {
      try {
         if (this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.CATALOG) {
            return "";
         } else {
            String database = this.getField(column).getDatabaseName();
            return database == null ? "" : database;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.exceptionInterceptor);
      }
   }

   public String getTableName(int column) throws SQLException {
      try {
         String res = this.useOldAliasBehavior ? this.getField(column).getTableName() : this.getField(column).getOriginalTableName();
         return res == null ? "" : res;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.exceptionInterceptor);
      }
   }

   public boolean isAutoIncrement(int column) throws SQLException {
      try {
         Field f = this.getField(column);
         return f.isAutoIncrement();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.exceptionInterceptor);
      }
   }

   public boolean isCaseSensitive(int column) throws SQLException {
      try {
         Field field = this.getField(column);
         switch (field.getMysqlType()) {
            case YEAR:
            case BIT:
            case TINYINT:
            case TINYINT_UNSIGNED:
            case SMALLINT:
            case SMALLINT_UNSIGNED:
            case INT:
            case INT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case BIGINT:
            case BIGINT_UNSIGNED:
            case FLOAT:
            case FLOAT_UNSIGNED:
            case DOUBLE:
            case DOUBLE_UNSIGNED:
            case DATE:
            case TIME:
            case TIMESTAMP:
            case DATETIME:
               return false;
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
            default:
               return true;
            case CHAR:
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
            case JSON:
            case ENUM:
            case SET:
               String collationName = this.session.getServerSession().getCharsetSettings().getCollationNameForCollationIndex(field.getCollationIndex());
               return collationName != null && !collationName.endsWith("_ci");
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.exceptionInterceptor);
      }
   }

   public boolean isCurrency(int column) throws SQLException {
      try {
         return false;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public boolean isDefinitelyWritable(int column) throws SQLException {
      try {
         return this.isWritable(column);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public int isNullable(int column) throws SQLException {
      try {
         return !this.getField(column).isNotNull() ? 1 : 0;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public boolean isReadOnly(int column) throws SQLException {
      try {
         return this.getField(column).isReadOnly();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public boolean isSearchable(int column) throws SQLException {
      try {
         return true;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public boolean isSigned(int column) throws SQLException {
      try {
         return MysqlType.isSigned(this.getField(column).getMysqlType());
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public boolean isWritable(int column) throws SQLException {
      try {
         return !this.isReadOnly(column);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public String toString() {
      StringBuilder toStringBuf = new StringBuilder();
      toStringBuf.append(super.toString());
      toStringBuf.append(" - Field level information: ");

      for(int i = 0; i < this.fields.length; ++i) {
         toStringBuf.append("\n\t");
         toStringBuf.append(this.fields[i].toString());
      }

      return toStringBuf.toString();
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      try {
         return iface.isInstance(this);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.exceptionInterceptor);
      }
   }

   public <T> T unwrap(Class<T> iface) throws SQLException {
      try {
         try {
            return iface.cast(this);
         } catch (ClassCastException var4) {
            throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[]{iface.toString()}), "S1009", this.exceptionInterceptor);
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.exceptionInterceptor);
      }
   }

   public Field[] getFields() {
      return this.fields;
   }
}
