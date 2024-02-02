package cn.hutool.db.meta;

import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IndexInfo implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private boolean nonUnique;
   private String indexName;
   private String tableName;
   private String schema;
   private String catalog;
   private List<ColumnIndexInfo> columnIndexInfoList;

   public IndexInfo(boolean nonUnique, String indexName, String tableName, String schema, String catalog) {
      this.nonUnique = nonUnique;
      this.indexName = indexName;
      this.tableName = tableName;
      this.schema = schema;
      this.catalog = catalog;
      this.setColumnIndexInfoList(new ArrayList());
   }

   public boolean isNonUnique() {
      return this.nonUnique;
   }

   public void setNonUnique(boolean nonUnique) {
      this.nonUnique = nonUnique;
   }

   public String getIndexName() {
      return this.indexName;
   }

   public void setIndexName(String indexName) {
      this.indexName = indexName;
   }

   public String getTableName() {
      return this.tableName;
   }

   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   public String getSchema() {
      return this.schema;
   }

   public void setSchema(String schema) {
      this.schema = schema;
   }

   public String getCatalog() {
      return this.catalog;
   }

   public void setCatalog(String catalog) {
      this.catalog = catalog;
   }

   public List<ColumnIndexInfo> getColumnIndexInfoList() {
      return this.columnIndexInfoList;
   }

   public void setColumnIndexInfoList(List<ColumnIndexInfo> columnIndexInfoList) {
      this.columnIndexInfoList = columnIndexInfoList;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         IndexInfo indexInfo = (IndexInfo)o;
         return ObjectUtil.equals(this.indexName, indexInfo.indexName) && ObjectUtil.equals(this.tableName, indexInfo.tableName);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.indexName, this.tableName});
   }

   public IndexInfo clone() throws CloneNotSupportedException {
      return (IndexInfo)super.clone();
   }

   public String toString() {
      return "IndexInfo{nonUnique=" + this.nonUnique + ", indexName='" + this.indexName + '\'' + ", tableName='" + this.tableName + '\'' + ", schema='" + this.schema + '\'' + ", catalog='" + this.catalog + '\'' + ", columnIndexInfoList=" + this.columnIndexInfoList + '}';
   }
}
