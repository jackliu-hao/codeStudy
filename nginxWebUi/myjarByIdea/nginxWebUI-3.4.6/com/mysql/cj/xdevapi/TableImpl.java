package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.x.XMessageBuilder;
import com.mysql.cj.protocol.x.XProtocolError;
import com.mysql.cj.result.StringValueFactory;
import com.mysql.cj.result.ValueFactory;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TableImpl implements Table {
   private MysqlxSession mysqlxSession;
   private SchemaImpl schema;
   private String name;
   private Boolean isView = null;
   private XMessageBuilder xbuilder;

   TableImpl(MysqlxSession mysqlxSession, SchemaImpl schema, String name) {
      if (mysqlxSession == null) {
         throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[]{"mysqlxSession"}));
      } else if (schema == null) {
         throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[]{"schema"}));
      } else if (name == null) {
         throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[]{"name"}));
      } else {
         this.mysqlxSession = mysqlxSession;
         this.xbuilder = (XMessageBuilder)this.mysqlxSession.getMessageBuilder();
         this.schema = schema;
         this.name = name;
      }
   }

   public Session getSession() {
      return this.schema.getSession();
   }

   public Schema getSchema() {
      return this.schema;
   }

   public String getName() {
      return this.name;
   }

   public DatabaseObject.DbObjectStatus existsInDatabase() {
      return this.mysqlxSession.getDataStoreMetadata().tableExists(this.schema.getName(), this.name) ? DatabaseObject.DbObjectStatus.EXISTS : DatabaseObject.DbObjectStatus.NOT_EXISTS;
   }

   public InsertStatement insert() {
      return new InsertStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, new String[0]);
   }

   public InsertStatement insert(String... fields) {
      return new InsertStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, fields);
   }

   public InsertStatement insert(Map<String, Object> fieldsAndValues) {
      return new InsertStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, fieldsAndValues);
   }

   public SelectStatement select(String... projection) {
      return new SelectStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, projection);
   }

   public UpdateStatement update() {
      return new UpdateStatementImpl(this.mysqlxSession, this.schema.getName(), this.name);
   }

   public DeleteStatement delete() {
      return new DeleteStatementImpl(this.mysqlxSession, this.schema.getName(), this.name);
   }

   public long count() {
      try {
         return this.mysqlxSession.getDataStoreMetadata().getTableRowCount(this.schema.getName(), this.name);
      } catch (XProtocolError var2) {
         if (var2.getErrorCode() == 1146) {
            throw new XProtocolError("Table '" + this.name + "' does not exist in schema '" + this.schema.getName() + "'", var2);
         } else {
            throw var2;
         }
      }
   }

   public boolean equals(Object other) {
      return other != null && other.getClass() == TableImpl.class && ((TableImpl)other).schema.equals(this.schema) && ((TableImpl)other).mysqlxSession == this.mysqlxSession && this.name.equals(((TableImpl)other).name);
   }

   public int hashCode() {
      assert false : "hashCode not designed";

      return 0;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("Table(");
      sb.append(ExprUnparser.quoteIdentifier(this.schema.getName()));
      sb.append(".");
      sb.append(ExprUnparser.quoteIdentifier(this.name));
      sb.append(")");
      return sb.toString();
   }

   public boolean isView() {
      if (this.isView == null) {
         ValueFactory<String> svf = new StringValueFactory(this.mysqlxSession.getPropertySet());
         Function<com.mysql.cj.result.Row, DatabaseObjectDescription> rowToDatabaseObjectDescription = (r) -> {
            return new DatabaseObjectDescription((String)r.getValue(0, svf), (String)r.getValue(1, svf));
         };
         List<DatabaseObjectDescription> objects = (List)this.mysqlxSession.query(this.xbuilder.buildListObjects(this.schema.getName(), this.name), (Predicate)null, rowToDatabaseObjectDescription, Collectors.toList());
         if (objects.isEmpty()) {
            return false;
         }

         this.isView = ((DatabaseObjectDescription)objects.get(0)).getObjectType() == DatabaseObject.DbObjectType.VIEW || ((DatabaseObjectDescription)objects.get(0)).getObjectType() == DatabaseObject.DbObjectType.COLLECTION_VIEW;
      }

      return this.isView;
   }

   public void setView(boolean isView) {
      this.isView = isView;
   }
}
