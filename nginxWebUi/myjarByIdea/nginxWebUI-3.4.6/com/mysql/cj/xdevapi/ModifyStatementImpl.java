package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.x.XMessage;
import com.mysql.cj.protocol.x.XMessageBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModifyStatementImpl extends FilterableStatement<ModifyStatement, Result> implements ModifyStatement {
   private List<UpdateSpec> updates = new ArrayList();

   ModifyStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, String criteria) {
      super(new DocFilterParams(schema, collection, false));
      this.mysqlxSession = mysqlxSession;
      if (criteria != null && criteria.trim().length() != 0) {
         this.filterParams.setCriteria(criteria);
         if (!this.mysqlxSession.supportsPreparedStatements()) {
            this.preparedState = PreparableStatement.PreparedState.UNSUPPORTED;
         }

      } else {
         throw new XDevAPIError(Messages.getString("ModifyStatement.0", new String[]{"criteria"}));
      }
   }

   protected Result executeStatement() {
      return (Result)this.mysqlxSession.query(this.getMessageBuilder().buildDocUpdate(this.filterParams, this.updates), new UpdateResultBuilder());
   }

   protected XMessage getPrepareStatementXMessage() {
      return this.getMessageBuilder().buildPrepareDocUpdate(this.preparedStatementId, this.filterParams, this.updates);
   }

   protected Result executePreparedStatement() {
      return (Result)this.mysqlxSession.query(this.getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new UpdateResultBuilder());
   }

   public CompletableFuture<Result> executeAsync() {
      return this.mysqlxSession.queryAsync(((XMessageBuilder)this.mysqlxSession.getMessageBuilder()).buildDocUpdate(this.filterParams, this.updates), new UpdateResultBuilder());
   }

   public ModifyStatement set(String docPath, Object value) {
      this.resetPrepareState();
      this.updates.add((new UpdateSpec(UpdateType.ITEM_SET, docPath)).setValue(value));
      return this;
   }

   public ModifyStatement change(String docPath, Object value) {
      this.resetPrepareState();
      this.updates.add((new UpdateSpec(UpdateType.ITEM_REPLACE, docPath)).setValue(value));
      return this;
   }

   public ModifyStatement unset(String... fields) {
      this.resetPrepareState();
      this.updates.addAll((java.util.Collection)Arrays.stream(fields).map((docPath) -> {
         return new UpdateSpec(UpdateType.ITEM_REMOVE, docPath);
      }).collect(Collectors.toList()));
      return this;
   }

   public ModifyStatement patch(DbDoc document) {
      this.resetPrepareState();
      return this.patch(document.toString());
   }

   public ModifyStatement patch(String document) {
      this.resetPrepareState();
      this.updates.add((new UpdateSpec(UpdateType.MERGE_PATCH, "")).setValue(Expression.expr(document)));
      return this;
   }

   public ModifyStatement arrayInsert(String field, Object value) {
      this.resetPrepareState();
      this.updates.add((new UpdateSpec(UpdateType.ARRAY_INSERT, field)).setValue(value));
      return this;
   }

   public ModifyStatement arrayAppend(String docPath, Object value) {
      this.resetPrepareState();
      this.updates.add((new UpdateSpec(UpdateType.ARRAY_APPEND, docPath)).setValue(value));
      return this;
   }

   /** @deprecated */
   @Deprecated
   public ModifyStatement where(String searchCondition) {
      return (ModifyStatement)super.where(searchCondition);
   }
}
