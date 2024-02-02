package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.x.XMessage;
import com.mysql.cj.protocol.x.XMessageBuilder;
import java.util.concurrent.CompletableFuture;

public class RemoveStatementImpl extends FilterableStatement<RemoveStatement, Result> implements RemoveStatement {
   RemoveStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, String criteria) {
      super(new DocFilterParams(schema, collection, false));
      this.mysqlxSession = mysqlxSession;
      if (criteria != null && criteria.trim().length() != 0) {
         this.filterParams.setCriteria(criteria);
      } else {
         throw new XDevAPIError(Messages.getString("RemoveStatement.0", new String[]{"criteria"}));
      }
   }

   /** @deprecated */
   @Deprecated
   public RemoveStatement orderBy(String... sortFields) {
      return (RemoveStatement)super.orderBy(sortFields);
   }

   public Result executeStatement() {
      return (Result)this.mysqlxSession.query(this.getMessageBuilder().buildDelete(this.filterParams), new UpdateResultBuilder());
   }

   protected XMessage getPrepareStatementXMessage() {
      return this.getMessageBuilder().buildPrepareDelete(this.preparedStatementId, this.filterParams);
   }

   protected Result executePreparedStatement() {
      return (Result)this.mysqlxSession.query(this.getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new UpdateResultBuilder());
   }

   public CompletableFuture<Result> executeAsync() {
      return this.mysqlxSession.queryAsync(((XMessageBuilder)this.mysqlxSession.getMessageBuilder()).buildDelete(this.filterParams), new UpdateResultBuilder());
   }

   /** @deprecated */
   @Deprecated
   public RemoveStatement where(String searchCondition) {
      return (RemoveStatement)super.where(searchCondition);
   }
}
