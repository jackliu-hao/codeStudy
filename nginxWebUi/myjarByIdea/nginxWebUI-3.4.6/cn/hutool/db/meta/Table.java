package cn.hutool.db.meta;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Table implements Serializable, Cloneable {
   private static final long serialVersionUID = -810699625961392983L;
   private String schema;
   private String catalog;
   private String tableName;
   private String comment;
   private Set<String> pkNames = new LinkedHashSet();
   private List<IndexInfo> indexInfoList;
   private final Map<String, Column> columns = new LinkedHashMap();

   public static Table create(String tableName) {
      return new Table(tableName);
   }

   public Table(String tableName) {
      this.setTableName(tableName);
   }

   public String getSchema() {
      return this.schema;
   }

   public Table setSchema(String schema) {
      this.schema = schema;
      return this;
   }

   public String getCatalog() {
      return this.catalog;
   }

   public Table setCatalog(String catalog) {
      this.catalog = catalog;
      return this;
   }

   public String getTableName() {
      return this.tableName;
   }

   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   public String getComment() {
      return this.comment;
   }

   public Table setComment(String comment) {
      this.comment = comment;
      return this;
   }

   public Set<String> getPkNames() {
      return this.pkNames;
   }

   public boolean isPk(String columnName) {
      return this.getPkNames().contains(columnName);
   }

   public void setPkNames(Set<String> pkNames) {
      this.pkNames = pkNames;
   }

   public Table setColumn(Column column) {
      this.columns.put(column.getName(), column);
      return this;
   }

   public Column getColumn(String name) {
      return (Column)this.columns.get(name);
   }

   public Collection<Column> getColumns() {
      return this.columns.values();
   }

   public Table addPk(String pkColumnName) {
      this.pkNames.add(pkColumnName);
      return this;
   }

   public List<IndexInfo> getIndexInfoList() {
      return this.indexInfoList;
   }

   public void setIndexInfoList(List<IndexInfo> indexInfoList) {
      this.indexInfoList = indexInfoList;
   }

   public Table clone() throws CloneNotSupportedException {
      return (Table)super.clone();
   }
}
