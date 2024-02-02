package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlxSession;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.x.XMessageBuilder;
import com.mysql.cj.protocol.x.XProtocolError;
import com.mysql.cj.result.StringValueFactory;
import com.mysql.cj.result.ValueFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SchemaImpl implements Schema {
   private MysqlxSession mysqlxSession;
   private XMessageBuilder xbuilder;
   private Session session;
   private String name;
   private ValueFactory<String> svf;

   SchemaImpl(MysqlxSession mysqlxSession, Session session, String name) {
      this.mysqlxSession = mysqlxSession;
      this.session = session;
      this.name = name;
      this.xbuilder = (XMessageBuilder)this.mysqlxSession.getMessageBuilder();
      this.svf = new StringValueFactory(this.mysqlxSession.getPropertySet());
   }

   public Session getSession() {
      return this.session;
   }

   public Schema getSchema() {
      return this;
   }

   public String getName() {
      return this.name;
   }

   public DatabaseObject.DbObjectStatus existsInDatabase() {
      StringBuilder stmt = new StringBuilder("select count(*) from information_schema.schemata where schema_name = '");
      stmt.append(this.name.replaceAll("'", "\\'"));
      stmt.append("'");
      return this.mysqlxSession.getDataStoreMetadata().schemaExists(this.name) ? DatabaseObject.DbObjectStatus.EXISTS : DatabaseObject.DbObjectStatus.NOT_EXISTS;
   }

   public List<Collection> getCollections() {
      return this.getCollections((String)null);
   }

   public List<Collection> getCollections(String pattern) {
      Set<String> strTypes = (Set)Arrays.stream(new DatabaseObject.DbObjectType[]{DatabaseObject.DbObjectType.COLLECTION}).map(Enum::toString).collect(Collectors.toSet());
      Predicate<com.mysql.cj.result.Row> rowFiler = (r) -> {
         return strTypes.contains(r.getValue(1, this.svf));
      };
      Function<com.mysql.cj.result.Row, String> rowToName = (r) -> {
         return (String)r.getValue(0, this.svf);
      };
      List<String> objectNames = (List)this.mysqlxSession.query(this.xbuilder.buildListObjects(this.name, pattern), rowFiler, rowToName, Collectors.toList());
      return (List)objectNames.stream().map(this::getCollection).collect(Collectors.toList());
   }

   public List<Table> getTables() {
      return this.getTables((String)null);
   }

   public List<Table> getTables(String pattern) {
      Set<String> strTypes = (Set)Arrays.stream(new DatabaseObject.DbObjectType[]{DatabaseObject.DbObjectType.TABLE, DatabaseObject.DbObjectType.VIEW, DatabaseObject.DbObjectType.COLLECTION_VIEW}).map(Enum::toString).collect(Collectors.toSet());
      Predicate<com.mysql.cj.result.Row> rowFiler = (r) -> {
         return strTypes.contains(r.getValue(1, this.svf));
      };
      Function<com.mysql.cj.result.Row, String> rowToName = (r) -> {
         return (String)r.getValue(0, this.svf);
      };
      List<String> objectNames = (List)this.mysqlxSession.query(this.xbuilder.buildListObjects(this.name, pattern), rowFiler, rowToName, Collectors.toList());
      return (List)objectNames.stream().map(this::getTable).collect(Collectors.toList());
   }

   public Collection getCollection(String collectionName) {
      return new CollectionImpl(this.mysqlxSession, this, collectionName);
   }

   public Collection getCollection(String collectionName, boolean requireExists) {
      CollectionImpl coll = new CollectionImpl(this.mysqlxSession, this, collectionName);
      if (requireExists && coll.existsInDatabase() != DatabaseObject.DbObjectStatus.EXISTS) {
         throw new WrongArgumentException(coll.toString() + " doesn't exist");
      } else {
         return coll;
      }
   }

   public Table getCollectionAsTable(String collectionName) {
      return this.getTable(collectionName);
   }

   public Table getTable(String tableName) {
      return new TableImpl(this.mysqlxSession, this, tableName);
   }

   public Table getTable(String tableName, boolean requireExists) {
      TableImpl table = new TableImpl(this.mysqlxSession, this, tableName);
      if (requireExists && table.existsInDatabase() != DatabaseObject.DbObjectStatus.EXISTS) {
         throw new WrongArgumentException(table.toString() + " doesn't exist");
      } else {
         return table;
      }
   }

   public Collection createCollection(String collectionName) {
      this.mysqlxSession.query(this.xbuilder.buildCreateCollection(this.name, collectionName), new UpdateResultBuilder());
      return new CollectionImpl(this.mysqlxSession, this, collectionName);
   }

   public Collection createCollection(String collectionName, boolean reuseExisting) {
      try {
         return this.createCollection(collectionName);
      } catch (XProtocolError var4) {
         if (reuseExisting && var4.getErrorCode() == 1050) {
            return this.getCollection(collectionName);
         } else {
            throw var4;
         }
      }
   }

   public Collection createCollection(String collectionName, Schema.CreateCollectionOptions options) {
      try {
         this.mysqlxSession.query(this.xbuilder.buildCreateCollection(this.name, collectionName, options), new UpdateResultBuilder());
         return new CollectionImpl(this.mysqlxSession, this, collectionName);
      } catch (XProtocolError var4) {
         if (var4.getErrorCode() == 5015) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("Schema.CreateCollection"), (Throwable)var4);
         } else {
            throw var4;
         }
      }
   }

   public void modifyCollection(String collectionName, Schema.ModifyCollectionOptions options) {
      try {
         this.mysqlxSession.query(this.xbuilder.buildModifyCollectionOptions(this.name, collectionName, options), new UpdateResultBuilder());
      } catch (XProtocolError var4) {
         if (var4.getErrorCode() == 5157) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("Schema.CreateCollection"), (Throwable)var4);
         } else {
            throw var4;
         }
      }
   }

   public boolean equals(Object other) {
      return other != null && other.getClass() == SchemaImpl.class && ((SchemaImpl)other).session == this.session && ((SchemaImpl)other).mysqlxSession == this.mysqlxSession && this.name.equals(((SchemaImpl)other).name);
   }

   public int hashCode() {
      assert false : "hashCode not designed";

      return 0;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("Schema(");
      sb.append(ExprUnparser.quoteIdentifier(this.name));
      sb.append(")");
      return sb.toString();
   }

   public void dropCollection(String collectionName) {
      try {
         this.mysqlxSession.query(this.xbuilder.buildDropCollection(this.name, collectionName), new UpdateResultBuilder());
      } catch (XProtocolError var3) {
         if (var3.getErrorCode() != 1051) {
            throw var3;
         }
      }

   }
}
