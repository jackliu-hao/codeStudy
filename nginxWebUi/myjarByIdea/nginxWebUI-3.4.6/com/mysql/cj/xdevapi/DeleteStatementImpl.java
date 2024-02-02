package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.x.XMessage;
import java.util.concurrent.CompletableFuture;

public class DeleteStatementImpl extends FilterableStatement<DeleteStatement, Result> implements DeleteStatement {
   DeleteStatementImpl(MysqlxSession mysqlxSession, String schema, String table) {
      super(new TableFilterParams(schema, table, false));
      this.mysqlxSession = mysqlxSession;
   }

   protected Result executeStatement() {
      return (Result)this.mysqlxSession.query(this.getMessageBuilder().buildDelete(this.filterParams), new UpdateResultBuilder());
   }

   protected XMessage getPrepareStatementXMessage() {
      return this.getMessageBuilder().buildPrepareDelete(this.preparedStatementId, this.filterParams);
   }

   protected Result executePreparedStatement() {
      return (Result)this.mysqlxSession.query(this.getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new UpdateResultBuilder());
   }

   public CompletableFuture<Result> executeAsync() {
      return this.mysqlxSession.queryAsync(this.getMessageBuilder().buildDelete(this.filterParams), new UpdateResultBuilder());
   }
}
