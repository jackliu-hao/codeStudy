/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.MysqlxSession;
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.DefaultPropertySet;
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.PropertyDefinitions;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.x.XMessageBuilder;
/*     */ import com.mysql.cj.protocol.x.XProtocol;
/*     */ import com.mysql.cj.protocol.x.XProtocolError;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.result.StringValueFactory;
/*     */ import com.mysql.cj.result.ValueFactory;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SessionImpl
/*     */   implements Session
/*     */ {
/*     */   protected MysqlxSession session;
/*     */   protected String defaultSchemaName;
/*     */   private XMessageBuilder xbuilder;
/*     */   
/*     */   public SessionImpl(HostInfo hostInfo) {
/*  70 */     DefaultPropertySet defaultPropertySet = new DefaultPropertySet();
/*  71 */     defaultPropertySet.initializeProperties(hostInfo.exposeAsProperties());
/*  72 */     this.session = new MysqlxSession(hostInfo, (PropertySet)defaultPropertySet);
/*  73 */     this.defaultSchemaName = hostInfo.getDatabase();
/*  74 */     this.xbuilder = (XMessageBuilder)this.session.getMessageBuilder();
/*     */   }
/*     */   
/*     */   public SessionImpl(XProtocol prot) {
/*  78 */     this.session = new MysqlxSession(prot);
/*  79 */     this.defaultSchemaName = prot.defaultSchemaName;
/*  80 */     this.xbuilder = (XMessageBuilder)this.session.getMessageBuilder();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SessionImpl() {}
/*     */   
/*     */   public List<Schema> getSchemas() {
/*  87 */     Function<Row, String> rowToName = r -> (String)r.getValue(0, (ValueFactory)new StringValueFactory(this.session.getPropertySet()));
/*  88 */     Function<Row, Schema> rowToSchema = rowToName.andThen(n -> new SchemaImpl(this.session, this, n));
/*  89 */     return (List<Schema>)this.session.query((Message)this.xbuilder.buildSqlStatement("select schema_name from information_schema.schemata"), null, rowToSchema, 
/*  90 */         Collectors.toList());
/*     */   }
/*     */   
/*     */   public Schema getSchema(String schemaName) {
/*  94 */     return new SchemaImpl(this.session, this, schemaName);
/*     */   }
/*     */   
/*     */   public String getDefaultSchemaName() {
/*  98 */     return this.defaultSchemaName;
/*     */   }
/*     */   
/*     */   public Schema getDefaultSchema() {
/* 102 */     if (this.defaultSchemaName == null || this.defaultSchemaName.length() == 0) {
/* 103 */       return null;
/*     */     }
/* 105 */     return new SchemaImpl(this.session, this, this.defaultSchemaName);
/*     */   }
/*     */   
/*     */   public Schema createSchema(String schemaName) {
/* 109 */     StringBuilder stmtString = new StringBuilder("CREATE DATABASE ");
/* 110 */     stmtString.append(StringUtils.quoteIdentifier(schemaName, true));
/* 111 */     this.session.query((Message)this.xbuilder.buildSqlStatement(stmtString.toString()), new UpdateResultBuilder<>());
/* 112 */     return getSchema(schemaName);
/*     */   }
/*     */   
/*     */   public Schema createSchema(String schemaName, boolean reuseExistingObject) {
/*     */     try {
/* 117 */       return createSchema(schemaName);
/* 118 */     } catch (XProtocolError ex) {
/* 119 */       if (ex.getErrorCode() == 1007) {
/* 120 */         return getSchema(schemaName);
/*     */       }
/* 122 */       throw ex;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void dropSchema(String schemaName) {
/* 127 */     StringBuilder stmtString = new StringBuilder("DROP DATABASE ");
/* 128 */     stmtString.append(StringUtils.quoteIdentifier(schemaName, true));
/* 129 */     this.session.query((Message)this.xbuilder.buildSqlStatement(stmtString.toString()), new UpdateResultBuilder<>());
/*     */   }
/*     */   
/*     */   public void startTransaction() {
/* 133 */     this.session.query((Message)this.xbuilder.buildSqlStatement("START TRANSACTION"), new UpdateResultBuilder<>());
/*     */   }
/*     */   
/*     */   public void commit() {
/* 137 */     this.session.query((Message)this.xbuilder.buildSqlStatement("COMMIT"), new UpdateResultBuilder<>());
/*     */   }
/*     */   
/*     */   public void rollback() {
/* 141 */     this.session.query((Message)this.xbuilder.buildSqlStatement("ROLLBACK"), new UpdateResultBuilder<>());
/*     */   }
/*     */ 
/*     */   
/*     */   public String setSavepoint() {
/* 146 */     return setSavepoint(StringUtils.getUniqueSavepointId());
/*     */   }
/*     */ 
/*     */   
/*     */   public String setSavepoint(String name) {
/* 151 */     if (name == null || name.trim().length() == 0) {
/* 152 */       throw new XDevAPIError(Messages.getString("XSession.0", new String[] { "name" }));
/*     */     }
/*     */     
/* 155 */     this.session.query((Message)this.xbuilder.buildSqlStatement("SAVEPOINT " + StringUtils.quoteIdentifier(name, true)), new UpdateResultBuilder<>());
/* 156 */     return name;
/*     */   }
/*     */ 
/*     */   
/*     */   public void rollbackTo(String name) {
/* 161 */     if (name == null || name.trim().length() == 0) {
/* 162 */       throw new XDevAPIError(Messages.getString("XSession.0", new String[] { "name" }));
/*     */     }
/*     */     
/* 165 */     this.session.query((Message)this.xbuilder.buildSqlStatement("ROLLBACK TO " + StringUtils.quoteIdentifier(name, true)), new UpdateResultBuilder<>());
/*     */   }
/*     */ 
/*     */   
/*     */   public void releaseSavepoint(String name) {
/* 170 */     if (name == null || name.trim().length() == 0) {
/* 171 */       throw new XDevAPIError(Messages.getString("XSession.0", new String[] { "name" }));
/*     */     }
/*     */     
/* 174 */     this.session.query((Message)this.xbuilder.buildSqlStatement("RELEASE SAVEPOINT " + StringUtils.quoteIdentifier(name, true)), new UpdateResultBuilder<>());
/*     */   }
/*     */   
/*     */   public String getUri() {
/* 178 */     PropertySet pset = this.session.getPropertySet();
/*     */     
/* 180 */     StringBuilder sb = new StringBuilder(ConnectionUrl.Type.XDEVAPI_SESSION.getScheme());
/* 181 */     sb.append("//").append(this.session.getProcessHost()).append(":").append(this.session.getPort()).append("/").append(this.defaultSchemaName).append("?");
/*     */     
/* 183 */     boolean isFirstParam = true;
/*     */     
/* 185 */     for (PropertyKey propKey : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet()) {
/* 186 */       RuntimeProperty<?> propToGet = pset.getProperty(propKey);
/* 187 */       if (propToGet.isExplicitlySet()) {
/* 188 */         String propValue = propToGet.getStringValue();
/* 189 */         Object defaultValue = propToGet.getPropertyDefinition().getDefaultValue();
/* 190 */         if ((defaultValue == null && !StringUtils.isNullOrEmpty(propValue)) || (defaultValue != null && propValue == null) || (defaultValue != null && propValue != null && 
/* 191 */           !propValue.equals(defaultValue.toString()))) {
/* 192 */           if (isFirstParam) {
/* 193 */             isFirstParam = false;
/*     */           } else {
/* 195 */             sb.append("&");
/*     */           } 
/* 197 */           sb.append(propKey.getKeyName());
/* 198 */           sb.append("=");
/* 199 */           sb.append(propValue);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 208 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 213 */     return !this.session.isClosed();
/*     */   }
/*     */   
/*     */   public void close() {
/* 217 */     this.session.quit();
/*     */   }
/*     */   
/*     */   public SqlStatementImpl sql(String sql) {
/* 221 */     return new SqlStatementImpl(this.session, sql);
/*     */   }
/*     */   
/*     */   public MysqlxSession getSession() {
/* 225 */     return this.session;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\SessionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */