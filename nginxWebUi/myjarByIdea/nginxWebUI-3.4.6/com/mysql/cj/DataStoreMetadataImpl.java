package com.mysql.cj;

import com.mysql.cj.result.LongValueFactory;
import com.mysql.cj.result.Row;
import com.mysql.cj.xdevapi.ExprUnparser;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DataStoreMetadataImpl implements DataStoreMetadata {
   private Session session;

   public DataStoreMetadataImpl(Session sess) {
      this.session = sess;
   }

   public boolean schemaExists(String schemaName) {
      StringBuilder stmt = new StringBuilder("select count(*) from information_schema.schemata where schema_name = '");
      stmt.append(schemaName.replaceAll("'", "\\'"));
      stmt.append("'");
      Function<Row, Long> rowToLong = (r) -> {
         return (Long)r.getValue(0, new LongValueFactory(this.session.getPropertySet()));
      };
      List<Long> counters = (List)this.session.query(this.session.getMessageBuilder().buildSqlStatement(stmt.toString()), (Predicate)null, rowToLong, Collectors.toList());
      return 1L == (Long)counters.get(0);
   }

   public boolean tableExists(String schemaName, String tableName) {
      StringBuilder stmt = new StringBuilder("select count(*) from information_schema.tables where table_schema = '");
      stmt.append(schemaName.replaceAll("'", "\\'"));
      stmt.append("' and table_name = '");
      stmt.append(tableName.replaceAll("'", "\\'"));
      stmt.append("'");
      Function<Row, Long> rowToLong = (r) -> {
         return (Long)r.getValue(0, new LongValueFactory(this.session.getPropertySet()));
      };
      List<Long> counters = (List)this.session.query(this.session.getMessageBuilder().buildSqlStatement(stmt.toString()), (Predicate)null, rowToLong, Collectors.toList());
      return 1L == (Long)counters.get(0);
   }

   public long getTableRowCount(String schemaName, String tableName) {
      StringBuilder stmt = new StringBuilder("select count(*) from ");
      stmt.append(ExprUnparser.quoteIdentifier(schemaName));
      stmt.append(".");
      stmt.append(ExprUnparser.quoteIdentifier(tableName));
      Function<Row, Long> rowToLong = (r) -> {
         return (Long)r.getValue(0, new LongValueFactory(this.session.getPropertySet()));
      };
      List<Long> counters = (List)this.session.query(this.session.getMessageBuilder().buildSqlStatement(stmt.toString()), (Predicate)null, rowToLong, Collectors.toList());
      return (Long)counters.get(0);
   }
}
