/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.MysqlxSession;
/*     */ import com.mysql.cj.exceptions.AssertionFailedException;
/*     */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.x.XMessageBuilder;
/*     */ import com.mysql.cj.protocol.x.XProtocolError;
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.util.Map;
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
/*     */ public class CollectionImpl
/*     */   implements Collection
/*     */ {
/*     */   private MysqlxSession mysqlxSession;
/*     */   private XMessageBuilder xbuilder;
/*     */   private SchemaImpl schema;
/*     */   private String name;
/*     */   
/*     */   CollectionImpl(MysqlxSession mysqlxSession, SchemaImpl schema, String name) {
/*  52 */     this.mysqlxSession = mysqlxSession;
/*  53 */     this.schema = schema;
/*  54 */     this.name = name;
/*  55 */     this.xbuilder = (XMessageBuilder)this.mysqlxSession.getMessageBuilder();
/*     */   }
/*     */   
/*     */   public Session getSession() {
/*  59 */     return this.schema.getSession();
/*     */   }
/*     */   
/*     */   public Schema getSchema() {
/*  63 */     return this.schema;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  67 */     return this.name;
/*     */   }
/*     */   
/*     */   public DatabaseObject.DbObjectStatus existsInDatabase() {
/*  71 */     if (this.mysqlxSession.getDataStoreMetadata().tableExists(this.schema.getName(), this.name))
/*     */     {
/*  73 */       return DatabaseObject.DbObjectStatus.EXISTS;
/*     */     }
/*  75 */     return DatabaseObject.DbObjectStatus.NOT_EXISTS;
/*     */   }
/*     */   
/*     */   public AddStatement add(Map<String, ?> doc) {
/*  79 */     throw new FeatureNotAvailableException("TODO: ");
/*     */   }
/*     */ 
/*     */   
/*     */   public AddStatement add(String... jsonString) {
/*     */     try {
/*  85 */       DbDoc[] docs = new DbDoc[jsonString.length];
/*  86 */       for (int i = 0; i < jsonString.length; i++) {
/*  87 */         docs[i] = JsonParser.parseDoc(new StringReader(jsonString[i]));
/*     */       }
/*  89 */       return add(docs);
/*  90 */     } catch (IOException ex) {
/*  91 */       throw AssertionFailedException.shouldNotHappen(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public AddStatement add(DbDoc doc) {
/*  97 */     return new AddStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, doc);
/*     */   }
/*     */   
/*     */   public AddStatement add(DbDoc... docs) {
/* 101 */     return new AddStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, docs);
/*     */   }
/*     */   
/*     */   public FindStatement find() {
/* 105 */     return find(null);
/*     */   }
/*     */   
/*     */   public FindStatement find(String searchCondition) {
/* 109 */     return new FindStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, searchCondition);
/*     */   }
/*     */   
/*     */   public ModifyStatement modify(String searchCondition) {
/* 113 */     return new ModifyStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, searchCondition);
/*     */   }
/*     */   
/*     */   public RemoveStatement remove(String searchCondition) {
/* 117 */     return new RemoveStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, searchCondition);
/*     */   }
/*     */ 
/*     */   
/*     */   public Result createIndex(String indexName, DbDoc indexDefinition) {
/* 122 */     return (Result)this.mysqlxSession.query((Message)this.xbuilder
/* 123 */         .buildCreateCollectionIndex(this.schema.getName(), this.name, new CreateIndexParams(indexName, indexDefinition)), new UpdateResultBuilder<>());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Result createIndex(String indexName, String jsonIndexDefinition) {
/* 129 */     return (Result)this.mysqlxSession.query((Message)this.xbuilder
/* 130 */         .buildCreateCollectionIndex(this.schema.getName(), this.name, new CreateIndexParams(indexName, jsonIndexDefinition)), new UpdateResultBuilder<>());
/*     */   }
/*     */ 
/*     */   
/*     */   public void dropIndex(String indexName) {
/*     */     try {
/* 136 */       this.mysqlxSession.query((Message)this.xbuilder.buildDropCollectionIndex(this.schema.getName(), this.name, indexName), new UpdateResultBuilder<>());
/* 137 */     } catch (XProtocolError e) {
/*     */ 
/*     */       
/* 140 */       if (e.getErrorCode() != 1091) {
/* 141 */         throw e;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public long count() {
/*     */     try {
/* 148 */       return this.mysqlxSession.getDataStoreMetadata().getTableRowCount(this.schema.getName(), this.name);
/* 149 */     } catch (XProtocolError e) {
/* 150 */       if (e.getErrorCode() == 1146) {
/* 151 */         throw new XProtocolError("Collection '" + this.name + "' does not exist in schema '" + this.schema.getName() + "'", e);
/*     */       }
/* 153 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   public DbDoc newDoc() {
/* 158 */     return new DbDocImpl();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 163 */     return (other != null && other.getClass() == CollectionImpl.class && ((CollectionImpl)other).schema.equals(this.schema) && ((CollectionImpl)other).mysqlxSession == this.mysqlxSession && this.name
/* 164 */       .equals(((CollectionImpl)other).name));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 169 */     assert false : "hashCode not designed";
/* 170 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 175 */     StringBuilder sb = new StringBuilder("Collection(");
/* 176 */     sb.append(ExprUnparser.quoteIdentifier(this.schema.getName()));
/* 177 */     sb.append(".");
/* 178 */     sb.append(ExprUnparser.quoteIdentifier(this.name));
/* 179 */     sb.append(")");
/* 180 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Result replaceOne(String id, DbDoc doc) {
/* 185 */     if (id == null) {
/* 186 */       throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "id" }));
/*     */     }
/* 188 */     if (doc == null) {
/* 189 */       throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "doc" }));
/*     */     }
/* 191 */     JsonValue docId = doc.get("_id");
/* 192 */     if (docId != null && (!JsonString.class.isInstance(docId) || !id.equals(((JsonString)docId).getString()))) {
/* 193 */       throw new XDevAPIError(Messages.getString("Collection.DocIdMismatch"));
/*     */     }
/* 195 */     return modify("_id = :id").set("$", doc).bind("id", id).execute();
/*     */   }
/*     */ 
/*     */   
/*     */   public Result replaceOne(String id, String jsonString) {
/* 200 */     if (id == null) {
/* 201 */       throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "id" }));
/*     */     }
/* 203 */     if (jsonString == null) {
/* 204 */       throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "jsonString" }));
/*     */     }
/*     */     try {
/* 207 */       return replaceOne(id, JsonParser.parseDoc(new StringReader(jsonString)));
/* 208 */     } catch (IOException e) {
/* 209 */       throw AssertionFailedException.shouldNotHappen(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Result addOrReplaceOne(String id, DbDoc doc) {
/* 215 */     if (id == null) {
/* 216 */       throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "id" }));
/*     */     }
/* 218 */     if (doc == null) {
/* 219 */       throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "doc" }));
/*     */     }
/* 221 */     JsonValue docId = doc.get("_id");
/* 222 */     if (docId == null) {
/* 223 */       doc.add("_id", (new JsonString()).setValue(id));
/* 224 */     } else if (!JsonString.class.isInstance(docId) || !id.equals(((JsonString)docId).getString())) {
/* 225 */       throw new XDevAPIError(Messages.getString("Collection.DocIdMismatch"));
/*     */     } 
/* 227 */     return add(doc).setUpsert(true).execute();
/*     */   }
/*     */ 
/*     */   
/*     */   public Result addOrReplaceOne(String id, String jsonString) {
/* 232 */     if (id == null) {
/* 233 */       throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "id" }));
/*     */     }
/* 235 */     if (jsonString == null) {
/* 236 */       throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "jsonString" }));
/*     */     }
/*     */     try {
/* 239 */       return addOrReplaceOne(id, JsonParser.parseDoc(new StringReader(jsonString)));
/* 240 */     } catch (IOException e) {
/* 241 */       throw AssertionFailedException.shouldNotHappen(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public DbDoc getOne(String id) {
/* 247 */     return find("_id = :id").bind("id", id).execute().fetchOne();
/*     */   }
/*     */ 
/*     */   
/*     */   public Result removeOne(String id) {
/* 252 */     return remove("_id = :id").bind("id", id).execute();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\CollectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */