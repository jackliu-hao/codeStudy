package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.x.XMessage;
import java.util.concurrent.CompletableFuture;

public class FindStatementImpl extends FilterableStatement<FindStatement, DocResult> implements FindStatement {
   FindStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, String criteria) {
      super(new DocFilterParams(schema, collection));
      this.mysqlxSession = mysqlxSession;
      if (criteria != null && criteria.length() > 0) {
         this.filterParams.setCriteria(criteria);
      }

      if (!this.mysqlxSession.supportsPreparedStatements()) {
         this.preparedState = PreparableStatement.PreparedState.UNSUPPORTED;
      }

   }

   protected DocResult executeStatement() {
      return (DocResult)this.mysqlxSession.query(this.getMessageBuilder().buildFind(this.filterParams), new StreamingDocResultBuilder(this.mysqlxSession));
   }

   protected XMessage getPrepareStatementXMessage() {
      return this.getMessageBuilder().buildPrepareFind(this.preparedStatementId, this.filterParams);
   }

   protected DocResult executePreparedStatement() {
      return (DocResult)this.mysqlxSession.query(this.getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new StreamingDocResultBuilder(this.mysqlxSession));
   }

   public CompletableFuture<DocResult> executeAsync() {
      return this.mysqlxSession.queryAsync(this.getMessageBuilder().buildFind(this.filterParams), new DocResultBuilder(this.mysqlxSession));
   }

   public FindStatement fields(String... projection) {
      this.resetPrepareState();
      this.filterParams.setFields(projection);
      return this;
   }

   public FindStatement fields(Expression docProjection) {
      this.resetPrepareState();
      ((DocFilterParams)this.filterParams).setFields(docProjection);
      return this;
   }

   public FindStatement groupBy(String... groupBy) {
      this.resetPrepareState();
      this.filterParams.setGrouping(groupBy);
      return this;
   }

   public FindStatement having(String having) {
      this.resetPrepareState();
      this.filterParams.setGroupingCriteria(having);
      return this;
   }

   public FindStatement lockShared() {
      return this.lockShared(Statement.LockContention.DEFAULT);
   }

   public FindStatement lockShared(Statement.LockContention lockContention) {
      this.resetPrepareState();
      this.filterParams.setLock(FilterParams.RowLock.SHARED_LOCK);
      switch (lockContention) {
         case NOWAIT:
            this.filterParams.setLockOption(FilterParams.RowLockOptions.NOWAIT);
            break;
         case SKIP_LOCKED:
            this.filterParams.setLockOption(FilterParams.RowLockOptions.SKIP_LOCKED);
         case DEFAULT:
      }

      return this;
   }

   public FindStatement lockExclusive() {
      return this.lockExclusive(Statement.LockContention.DEFAULT);
   }

   public FindStatement lockExclusive(Statement.LockContention lockContention) {
      this.resetPrepareState();
      this.filterParams.setLock(FilterParams.RowLock.EXCLUSIVE_LOCK);
      switch (lockContention) {
         case NOWAIT:
            this.filterParams.setLockOption(FilterParams.RowLockOptions.NOWAIT);
            break;
         case SKIP_LOCKED:
            this.filterParams.setLockOption(FilterParams.RowLockOptions.SKIP_LOCKED);
         case DEFAULT:
      }

      return this;
   }

   /** @deprecated */
   @Deprecated
   public FindStatement where(String searchCondition) {
      return (FindStatement)super.where(searchCondition);
   }
}
