package cn.hutool.db.meta;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.db.DbRuntimeException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Column implements Serializable, Cloneable {
   private static final long serialVersionUID = 577527740359719367L;
   private String tableName;
   private String name;
   private int type;
   private String typeName;
   private long size;
   private Integer digit;
   private boolean isNullable;
   private String comment;
   private boolean autoIncrement;
   private String columnDef;
   private boolean isPk;

   public static Column create(Table table, ResultSet columnMetaRs) {
      return new Column(table, columnMetaRs);
   }

   public Column() {
   }

   public Column(Table table, ResultSet columnMetaRs) {
      try {
         this.init(table, columnMetaRs);
      } catch (SQLException var4) {
         throw new DbRuntimeException(var4, "Get table [{}] meta info error!", new Object[]{this.tableName});
      }
   }

   public void init(Table table, ResultSet columnMetaRs) throws SQLException {
      this.tableName = table.getTableName();
      this.name = columnMetaRs.getString("COLUMN_NAME");
      this.isPk = table.isPk(this.name);
      this.type = columnMetaRs.getInt("DATA_TYPE");
      String typeName = columnMetaRs.getString("TYPE_NAME");
      typeName = ReUtil.delLast((String)"\\(\\d+\\)", typeName);
      this.typeName = typeName;
      this.size = columnMetaRs.getLong("COLUMN_SIZE");
      this.isNullable = columnMetaRs.getBoolean("NULLABLE");
      this.comment = columnMetaRs.getString("REMARKS");
      this.columnDef = columnMetaRs.getString("COLUMN_DEF");

      try {
         this.digit = columnMetaRs.getInt("DECIMAL_DIGITS");
      } catch (SQLException var6) {
      }

      try {
         String auto = columnMetaRs.getString("IS_AUTOINCREMENT");
         if (BooleanUtil.toBoolean(auto)) {
            this.autoIncrement = true;
         }
      } catch (SQLException var5) {
      }

   }

   public String getTableName() {
      return this.tableName;
   }

   public Column setTableName(String tableName) {
      this.tableName = tableName;
      return this;
   }

   public String getName() {
      return this.name;
   }

   public Column setName(String name) {
      this.name = name;
      return this;
   }

   public JdbcType getTypeEnum() {
      return JdbcType.valueOf(this.type);
   }

   public int getType() {
      return this.type;
   }

   public Column setType(int type) {
      this.type = type;
      return this;
   }

   public String getTypeName() {
      return this.typeName;
   }

   public Column setTypeName(String typeName) {
      this.typeName = typeName;
      return this;
   }

   public long getSize() {
      return this.size;
   }

   public Column setSize(int size) {
      this.size = (long)size;
      return this;
   }

   public int getDigit() {
      return this.digit;
   }

   public Column setDigit(int digit) {
      this.digit = digit;
      return this;
   }

   public boolean isNullable() {
      return this.isNullable;
   }

   public Column setNullable(boolean isNullable) {
      this.isNullable = isNullable;
      return this;
   }

   public String getComment() {
      return this.comment;
   }

   public Column setComment(String comment) {
      this.comment = comment;
      return this;
   }

   public boolean isAutoIncrement() {
      return this.autoIncrement;
   }

   public Column setAutoIncrement(boolean autoIncrement) {
      this.autoIncrement = autoIncrement;
      return this;
   }

   public boolean isPk() {
      return this.isPk;
   }

   public Column setPk(boolean isPk) {
      this.isPk = isPk;
      return this;
   }

   public String getColumnDef() {
      return this.columnDef;
   }

   public Column setColumnDef(String columnDef) {
      this.columnDef = columnDef;
      return this;
   }

   public String toString() {
      return "Column [tableName=" + this.tableName + ", name=" + this.name + ", type=" + this.type + ", size=" + this.size + ", isNullable=" + this.isNullable + "]";
   }

   public Column clone() throws CloneNotSupportedException {
      return (Column)super.clone();
   }
}
