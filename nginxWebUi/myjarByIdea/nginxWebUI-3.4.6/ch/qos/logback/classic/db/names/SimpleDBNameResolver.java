package ch.qos.logback.classic.db.names;

public class SimpleDBNameResolver implements DBNameResolver {
   private String tableNamePrefix = "";
   private String tableNameSuffix = "";
   private String columnNamePrefix = "";
   private String columnNameSuffix = "";

   public <N extends Enum<?>> String getTableName(N tableName) {
      return this.tableNamePrefix + tableName.name().toLowerCase() + this.tableNameSuffix;
   }

   public <N extends Enum<?>> String getColumnName(N columnName) {
      return this.columnNamePrefix + columnName.name().toLowerCase() + this.columnNameSuffix;
   }

   public void setTableNamePrefix(String tableNamePrefix) {
      this.tableNamePrefix = tableNamePrefix != null ? tableNamePrefix : "";
   }

   public void setTableNameSuffix(String tableNameSuffix) {
      this.tableNameSuffix = tableNameSuffix != null ? tableNameSuffix : "";
   }

   public void setColumnNamePrefix(String columnNamePrefix) {
      this.columnNamePrefix = columnNamePrefix != null ? columnNamePrefix : "";
   }

   public void setColumnNameSuffix(String columnNameSuffix) {
      this.columnNameSuffix = columnNameSuffix != null ? columnNameSuffix : "";
   }
}
