package cn.hutool.db.meta;

import cn.hutool.db.DbRuntimeException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnIndexInfo implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private String columnName;
   private String ascOrDesc;

   public static ColumnIndexInfo create(ResultSet rs) {
      try {
         return new ColumnIndexInfo(rs.getString("COLUMN_NAME"), rs.getString("ASC_OR_DESC"));
      } catch (SQLException var2) {
         throw new DbRuntimeException(var2);
      }
   }

   public ColumnIndexInfo(String columnName, String ascOrDesc) {
      this.columnName = columnName;
      this.ascOrDesc = ascOrDesc;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public String getAscOrDesc() {
      return this.ascOrDesc;
   }

   public void setAscOrDesc(String ascOrDesc) {
      this.ascOrDesc = ascOrDesc;
   }

   public ColumnIndexInfo clone() throws CloneNotSupportedException {
      return (ColumnIndexInfo)super.clone();
   }

   public String toString() {
      return "ColumnIndexInfo{columnName='" + this.columnName + '\'' + ", ascOrDesc='" + this.ascOrDesc + '\'' + '}';
   }
}
