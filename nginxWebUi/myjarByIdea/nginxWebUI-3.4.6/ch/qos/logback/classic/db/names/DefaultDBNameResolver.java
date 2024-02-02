package ch.qos.logback.classic.db.names;

public class DefaultDBNameResolver implements DBNameResolver {
   public <N extends Enum<?>> String getTableName(N tableName) {
      return tableName.toString().toLowerCase();
   }

   public <N extends Enum<?>> String getColumnName(N columnName) {
      return columnName.toString().toLowerCase();
   }
}
