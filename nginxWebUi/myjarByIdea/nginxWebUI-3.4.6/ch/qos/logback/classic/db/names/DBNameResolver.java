package ch.qos.logback.classic.db.names;

public interface DBNameResolver {
   <N extends Enum<?>> String getTableName(N var1);

   <N extends Enum<?>> String getColumnName(N var1);
}
