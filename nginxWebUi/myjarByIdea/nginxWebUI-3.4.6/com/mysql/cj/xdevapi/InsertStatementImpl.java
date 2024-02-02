package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.x.XMessageBuilder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class InsertStatementImpl implements InsertStatement {
   private MysqlxSession mysqlxSession;
   private String schemaName;
   private String tableName;
   private InsertParams insertParams = new InsertParams();

   InsertStatementImpl(MysqlxSession mysqlxSession, String schema, String table, String[] fields) {
      this.mysqlxSession = mysqlxSession;
      this.schemaName = schema;
      this.tableName = table;
      this.insertParams.setProjection(fields);
   }

   InsertStatementImpl(MysqlxSession mysqlxSession, String schema, String table, Map<String, Object> fieldsAndValues) {
      this.mysqlxSession = mysqlxSession;
      this.schemaName = schema;
      this.tableName = table;
      this.insertParams.setFieldsAndValues(fieldsAndValues);
   }

   public InsertResult execute() {
      return (InsertResult)this.mysqlxSession.query(((XMessageBuilder)this.mysqlxSession.getMessageBuilder()).buildRowInsert(this.schemaName, this.tableName, this.insertParams), new InsertResultBuilder());
   }

   public CompletableFuture<InsertResult> executeAsync() {
      return this.mysqlxSession.queryAsync(((XMessageBuilder)this.mysqlxSession.getMessageBuilder()).buildRowInsert(this.schemaName, this.tableName, this.insertParams), new InsertResultBuilder());
   }

   public InsertStatement values(List<Object> row) {
      this.insertParams.addRow(row);
      return this;
   }
}
