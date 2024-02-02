/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.MysqlxSession;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.x.XMessageBuilder;
/*     */ import com.mysql.cj.protocol.x.XProtocolError;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.result.StringValueFactory;
/*     */ import com.mysql.cj.result.ValueFactory;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
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
/*     */ 
/*     */ 
/*     */ public class TableImpl
/*     */   implements Table
/*     */ {
/*     */   private MysqlxSession mysqlxSession;
/*     */   private SchemaImpl schema;
/*     */   private String name;
/*  55 */   private Boolean isView = null;
/*     */   private XMessageBuilder xbuilder;
/*     */   
/*     */   TableImpl(MysqlxSession mysqlxSession, SchemaImpl schema, String name) {
/*  59 */     if (mysqlxSession == null) {
/*  60 */       throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "mysqlxSession" }));
/*     */     }
/*  62 */     if (schema == null) {
/*  63 */       throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "schema" }));
/*     */     }
/*  65 */     if (name == null) {
/*  66 */       throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "name" }));
/*     */     }
/*  68 */     this.mysqlxSession = mysqlxSession;
/*  69 */     this.xbuilder = (XMessageBuilder)this.mysqlxSession.getMessageBuilder();
/*  70 */     this.schema = schema;
/*  71 */     this.name = name;
/*     */   }
/*     */   
/*     */   public Session getSession() {
/*  75 */     return this.schema.getSession();
/*     */   }
/*     */   
/*     */   public Schema getSchema() {
/*  79 */     return this.schema;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  83 */     return this.name;
/*     */   }
/*     */   
/*     */   public DatabaseObject.DbObjectStatus existsInDatabase() {
/*  87 */     if (this.mysqlxSession.getDataStoreMetadata().tableExists(this.schema.getName(), this.name)) {
/*  88 */       return DatabaseObject.DbObjectStatus.EXISTS;
/*     */     }
/*  90 */     return DatabaseObject.DbObjectStatus.NOT_EXISTS;
/*     */   }
/*     */   
/*     */   public InsertStatement insert() {
/*  94 */     return new InsertStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, new String[0]);
/*     */   }
/*     */   
/*     */   public InsertStatement insert(String... fields) {
/*  98 */     return new InsertStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, fields);
/*     */   }
/*     */   
/*     */   public InsertStatement insert(Map<String, Object> fieldsAndValues) {
/* 102 */     return new InsertStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, fieldsAndValues);
/*     */   }
/*     */ 
/*     */   
/*     */   public SelectStatement select(String... projection) {
/* 107 */     return new SelectStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, projection);
/*     */   }
/*     */   
/*     */   public UpdateStatement update() {
/* 111 */     return new UpdateStatementImpl(this.mysqlxSession, this.schema.getName(), this.name);
/*     */   }
/*     */   
/*     */   public DeleteStatement delete() {
/* 115 */     return new DeleteStatementImpl(this.mysqlxSession, this.schema.getName(), this.name);
/*     */   }
/*     */   
/*     */   public long count() {
/*     */     try {
/* 120 */       return this.mysqlxSession.getDataStoreMetadata().getTableRowCount(this.schema.getName(), this.name);
/* 121 */     } catch (XProtocolError e) {
/* 122 */       if (e.getErrorCode() == 1146) {
/* 123 */         throw new XProtocolError("Table '" + this.name + "' does not exist in schema '" + this.schema.getName() + "'", e);
/*     */       }
/* 125 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 131 */     return (other != null && other.getClass() == TableImpl.class && ((TableImpl)other).schema.equals(this.schema) && ((TableImpl)other).mysqlxSession == this.mysqlxSession && this.name
/* 132 */       .equals(((TableImpl)other).name));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 137 */     assert false : "hashCode not designed";
/* 138 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 143 */     StringBuilder sb = new StringBuilder("Table(");
/* 144 */     sb.append(ExprUnparser.quoteIdentifier(this.schema.getName()));
/* 145 */     sb.append(".");
/* 146 */     sb.append(ExprUnparser.quoteIdentifier(this.name));
/* 147 */     sb.append(")");
/* 148 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isView() {
/* 154 */     if (this.isView == null) {
/* 155 */       StringValueFactory stringValueFactory = new StringValueFactory(this.mysqlxSession.getPropertySet());
/* 156 */       Function<Row, DatabaseObjectDescription> rowToDatabaseObjectDescription = r -> new DatabaseObjectDescription((String)r.getValue(0, svf), (String)r.getValue(1, svf));
/*     */       
/* 158 */       List<DatabaseObjectDescription> objects = (List<DatabaseObjectDescription>)this.mysqlxSession.query((Message)this.xbuilder.buildListObjects(this.schema.getName(), this.name), null, rowToDatabaseObjectDescription, 
/* 159 */           Collectors.toList());
/* 160 */       if (objects.isEmpty())
/*     */       {
/* 162 */         return false;
/*     */       }
/*     */       
/* 165 */       this.isView = Boolean.valueOf((((DatabaseObjectDescription)objects.get(0)).getObjectType() == DatabaseObject.DbObjectType.VIEW || ((DatabaseObjectDescription)objects.get(0)).getObjectType() == DatabaseObject.DbObjectType.COLLECTION_VIEW));
/*     */     } 
/* 167 */     return this.isView.booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setView(boolean isView) {
/* 177 */     this.isView = Boolean.valueOf(isView);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\TableImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */