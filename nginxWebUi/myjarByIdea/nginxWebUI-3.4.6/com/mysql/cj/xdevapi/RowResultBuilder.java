package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ResultBuilder;
import com.mysql.cj.protocol.x.FetchDoneEntity;
import com.mysql.cj.protocol.x.Notice;
import com.mysql.cj.protocol.x.StatementExecuteOk;
import com.mysql.cj.protocol.x.StatementExecuteOkBuilder;
import com.mysql.cj.result.BufferedRowList;
import com.mysql.cj.result.DefaultColumnDefinition;
import com.mysql.cj.result.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class RowResultBuilder implements ResultBuilder<RowResult> {
   private ArrayList<Field> fields = new ArrayList();
   private ColumnDefinition metadata;
   private List<com.mysql.cj.result.Row> rows = new ArrayList();
   private RowResult result;
   TimeZone defaultTimeZone;
   PropertySet pset;
   private StatementExecuteOkBuilder statementExecuteOkBuilder = new StatementExecuteOkBuilder();

   public RowResultBuilder(MysqlxSession sess) {
      this.defaultTimeZone = sess.getServerSession().getDefaultTimeZone();
      this.pset = sess.getPropertySet();
   }

   public boolean addProtocolEntity(ProtocolEntity entity) {
      if (entity instanceof Field) {
         this.fields.add((Field)entity);
         return false;
      } else if (entity instanceof com.mysql.cj.result.Row) {
         if (this.metadata == null) {
            this.metadata = new DefaultColumnDefinition((Field[])this.fields.toArray(new Field[0]));
         }

         this.rows.add(((com.mysql.cj.result.Row)entity).setMetadata(this.metadata));
         return false;
      } else if (entity instanceof Notice) {
         this.statementExecuteOkBuilder.addProtocolEntity(entity);
         return false;
      } else if (entity instanceof FetchDoneEntity) {
         return false;
      } else if (entity instanceof StatementExecuteOk) {
         return true;
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unexpected protocol entity " + entity);
      }
   }

   public RowResult build() {
      if (this.metadata == null) {
         this.metadata = new DefaultColumnDefinition((Field[])this.fields.toArray(new Field[0]));
      }

      this.result = new RowResultImpl(this.metadata, this.defaultTimeZone, new BufferedRowList(this.rows), () -> {
         return this.statementExecuteOkBuilder.build();
      }, this.pset);
      return this.result;
   }
}
