/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.MysqlxSession;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.x.XMessageBuilder;
/*     */ import com.mysql.cj.protocol.x.XProtocolError;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.result.StringValueFactory;
/*     */ import com.mysql.cj.result.ValueFactory;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.stream.Collectors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SchemaImpl
/*     */   implements Schema
/*     */ {
/*     */   private MysqlxSession mysqlxSession;
/*     */   private XMessageBuilder xbuilder;
/*     */   private Session session;
/*     */   private String name;
/*     */   private ValueFactory<String> svf;
/*     */   
/*     */   SchemaImpl(MysqlxSession mysqlxSession, Session session, String name) {
/*  61 */     this.mysqlxSession = mysqlxSession;
/*  62 */     this.session = session;
/*  63 */     this.name = name;
/*  64 */     this.xbuilder = (XMessageBuilder)this.mysqlxSession.getMessageBuilder();
/*  65 */     this.svf = (ValueFactory<String>)new StringValueFactory(this.mysqlxSession.getPropertySet());
/*     */   }
/*     */   
/*     */   public Session getSession() {
/*  69 */     return this.session;
/*     */   }
/*     */   
/*     */   public Schema getSchema() {
/*  73 */     return this;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  77 */     return this.name;
/*     */   }
/*     */   
/*     */   public DatabaseObject.DbObjectStatus existsInDatabase() {
/*  81 */     StringBuilder stmt = new StringBuilder("select count(*) from information_schema.schemata where schema_name = '");
/*     */     
/*  83 */     stmt.append(this.name.replaceAll("'", "\\'"));
/*  84 */     stmt.append("'");
/*  85 */     return this.mysqlxSession.getDataStoreMetadata().schemaExists(this.name) ? DatabaseObject.DbObjectStatus.EXISTS : DatabaseObject.DbObjectStatus.NOT_EXISTS;
/*     */   }
/*     */   
/*     */   public List<Collection> getCollections() {
/*  89 */     return getCollections(null);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Collection> getCollections(String pattern) {
/*  94 */     Set<String> strTypes = (Set<String>)Arrays.<DatabaseObject.DbObjectType>stream(new DatabaseObject.DbObjectType[] { DatabaseObject.DbObjectType.COLLECTION }).map(Enum::toString).collect(Collectors.toSet());
/*  95 */     Predicate<Row> rowFiler = r -> strTypes.contains(r.getValue(1, this.svf));
/*  96 */     Function<Row, String> rowToName = r -> (String)r.getValue(0, this.svf);
/*  97 */     List<String> objectNames = (List<String>)this.mysqlxSession.query((Message)this.xbuilder.buildListObjects(this.name, pattern), rowFiler, rowToName, Collectors.toList());
/*  98 */     return (List<Collection>)objectNames.stream().map(this::getCollection).collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Table> getTables() {
/* 103 */     return getTables(null);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Table> getTables(String pattern) {
/* 108 */     Set<String> strTypes = (Set<String>)Arrays.<DatabaseObject.DbObjectType>stream(new DatabaseObject.DbObjectType[] { DatabaseObject.DbObjectType.TABLE, DatabaseObject.DbObjectType.VIEW, DatabaseObject.DbObjectType.COLLECTION_VIEW }).map(Enum::toString).collect(Collectors.toSet());
/* 109 */     Predicate<Row> rowFiler = r -> strTypes.contains(r.getValue(1, this.svf));
/* 110 */     Function<Row, String> rowToName = r -> (String)r.getValue(0, this.svf);
/* 111 */     List<String> objectNames = (List<String>)this.mysqlxSession.query((Message)this.xbuilder.buildListObjects(this.name, pattern), rowFiler, rowToName, Collectors.toList());
/* 112 */     return (List<Table>)objectNames.stream().map(this::getTable).collect(Collectors.toList());
/*     */   }
/*     */   
/*     */   public Collection getCollection(String collectionName) {
/* 116 */     return new CollectionImpl(this.mysqlxSession, this, collectionName);
/*     */   }
/*     */   
/*     */   public Collection getCollection(String collectionName, boolean requireExists) {
/* 120 */     CollectionImpl coll = new CollectionImpl(this.mysqlxSession, this, collectionName);
/* 121 */     if (requireExists && coll.existsInDatabase() != DatabaseObject.DbObjectStatus.EXISTS) {
/* 122 */       throw new WrongArgumentException(coll.toString() + " doesn't exist");
/*     */     }
/* 124 */     return coll;
/*     */   }
/*     */   
/*     */   public Table getCollectionAsTable(String collectionName) {
/* 128 */     return getTable(collectionName);
/*     */   }
/*     */   
/*     */   public Table getTable(String tableName) {
/* 132 */     return new TableImpl(this.mysqlxSession, this, tableName);
/*     */   }
/*     */   
/*     */   public Table getTable(String tableName, boolean requireExists) {
/* 136 */     TableImpl table = new TableImpl(this.mysqlxSession, this, tableName);
/* 137 */     if (requireExists && table.existsInDatabase() != DatabaseObject.DbObjectStatus.EXISTS) {
/* 138 */       throw new WrongArgumentException(table.toString() + " doesn't exist");
/*     */     }
/* 140 */     return table;
/*     */   }
/*     */   
/*     */   public Collection createCollection(String collectionName) {
/* 144 */     this.mysqlxSession.query((Message)this.xbuilder.buildCreateCollection(this.name, collectionName), new UpdateResultBuilder<>());
/* 145 */     return new CollectionImpl(this.mysqlxSession, this, collectionName);
/*     */   }
/*     */   
/*     */   public Collection createCollection(String collectionName, boolean reuseExisting) {
/*     */     try {
/* 150 */       return createCollection(collectionName);
/* 151 */     } catch (XProtocolError ex) {
/* 152 */       if (reuseExisting && ex.getErrorCode() == 1050) {
/* 153 */         return getCollection(collectionName);
/*     */       }
/* 155 */       throw ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection createCollection(String collectionName, Schema.CreateCollectionOptions options) {
/*     */     try {
/* 162 */       this.mysqlxSession.query((Message)this.xbuilder.buildCreateCollection(this.name, collectionName, options), new UpdateResultBuilder<>());
/* 163 */       return new CollectionImpl(this.mysqlxSession, this, collectionName);
/* 164 */     } catch (XProtocolError ex) {
/* 165 */       if (ex.getErrorCode() == 5015) {
/* 166 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Schema.CreateCollection"), ex);
/*     */       }
/* 168 */       throw ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void modifyCollection(String collectionName, Schema.ModifyCollectionOptions options) {
/*     */     try {
/* 175 */       this.mysqlxSession.query((Message)this.xbuilder.buildModifyCollectionOptions(this.name, collectionName, options), new UpdateResultBuilder<>());
/* 176 */     } catch (XProtocolError ex) {
/* 177 */       if (ex.getErrorCode() == 5157) {
/* 178 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Schema.CreateCollection"), ex);
/*     */       }
/* 180 */       throw ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 186 */     return (other != null && other.getClass() == SchemaImpl.class && ((SchemaImpl)other).session == this.session && ((SchemaImpl)other).mysqlxSession == this.mysqlxSession && this.name
/* 187 */       .equals(((SchemaImpl)other).name));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 192 */     assert false : "hashCode not designed";
/* 193 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 198 */     StringBuilder sb = new StringBuilder("Schema(");
/* 199 */     sb.append(ExprUnparser.quoteIdentifier(this.name));
/* 200 */     sb.append(")");
/* 201 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void dropCollection(String collectionName) {
/*     */     try {
/* 207 */       this.mysqlxSession.query((Message)this.xbuilder.buildDropCollection(this.name, collectionName), new UpdateResultBuilder<>());
/* 208 */     } catch (XProtocolError e) {
/*     */ 
/*     */       
/* 211 */       if (e.getErrorCode() != 1051)
/* 212 */         throw e; 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\SchemaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */