package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ResultBuilder;
import com.mysql.cj.protocol.x.FetchDoneEntity;
import com.mysql.cj.protocol.x.FetchDoneMoreResults;
import com.mysql.cj.protocol.x.Notice;
import com.mysql.cj.protocol.x.StatementExecuteOk;
import com.mysql.cj.protocol.x.StatementExecuteOkBuilder;
import com.mysql.cj.result.BufferedRowList;
import com.mysql.cj.result.DefaultColumnDefinition;
import com.mysql.cj.result.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class SqlResultBuilder implements ResultBuilder<SqlResult> {
   private ArrayList<Field> fields = new ArrayList();
   private ColumnDefinition metadata;
   private List<com.mysql.cj.result.Row> rows = new ArrayList();
   TimeZone defaultTimeZone;
   PropertySet pset;
   boolean isRowResult = false;
   List<SqlSingleResult> resultSets = new ArrayList();
   private ProtocolEntity prevEntity = null;
   private StatementExecuteOkBuilder statementExecuteOkBuilder = new StatementExecuteOkBuilder();

   public SqlResultBuilder(TimeZone defaultTimeZone, PropertySet pset) {
      this.defaultTimeZone = defaultTimeZone;
      this.pset = pset;
   }

   public SqlResultBuilder(MysqlxSession sess) {
      this.defaultTimeZone = sess.getServerSession().getDefaultTimeZone();
      this.pset = sess.getPropertySet();
   }

   public boolean addProtocolEntity(ProtocolEntity entity) {
      if (entity instanceof Field) {
         this.fields.add((Field)entity);
         if (!this.isRowResult) {
            this.isRowResult = true;
         }

         this.prevEntity = entity;
         return false;
      } else if (entity instanceof Notice) {
         this.statementExecuteOkBuilder.addProtocolEntity(entity);
         return false;
      } else {
         if (this.isRowResult && this.metadata == null) {
            this.metadata = new DefaultColumnDefinition((Field[])this.fields.toArray(new Field[0]));
         }

         if (entity instanceof com.mysql.cj.result.Row) {
            this.rows.add(((com.mysql.cj.result.Row)entity).setMetadata(this.metadata));
         } else if (entity instanceof FetchDoneMoreResults) {
            this.resultSets.add(new SqlSingleResult(this.metadata, this.defaultTimeZone, new BufferedRowList(this.rows), () -> {
               return this.statementExecuteOkBuilder.build();
            }, this.pset));
            this.fields = new ArrayList();
            this.metadata = null;
            this.rows = new ArrayList();
            this.statementExecuteOkBuilder = new StatementExecuteOkBuilder();
         } else if (entity instanceof FetchDoneEntity) {
            if (!(this.prevEntity instanceof FetchDoneMoreResults)) {
               this.resultSets.add(new SqlSingleResult(this.metadata, this.defaultTimeZone, new BufferedRowList(this.rows), () -> {
                  return this.statementExecuteOkBuilder.build();
               }, this.pset));
            }
         } else if (entity instanceof StatementExecuteOk) {
            return true;
         }

         this.prevEntity = entity;
         return false;
      }
   }

   public SqlResult build() {
      return (SqlResult)(this.isRowResult ? new SqlMultiResult(() -> {
         return this.resultSets.size() > 0 ? (SqlResult)this.resultSets.remove(0) : null;
      }) : new SqlUpdateResult(this.statementExecuteOkBuilder.build()));
   }
}
