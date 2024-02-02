package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlxSession;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.DefaultPropertySet;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.protocol.x.XMessageBuilder;
import com.mysql.cj.protocol.x.XProtocol;
import com.mysql.cj.protocol.x.XProtocolError;
import com.mysql.cj.result.StringValueFactory;
import com.mysql.cj.util.StringUtils;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SessionImpl implements Session {
   protected MysqlxSession session;
   protected String defaultSchemaName;
   private XMessageBuilder xbuilder;

   public SessionImpl(HostInfo hostInfo) {
      PropertySet pset = new DefaultPropertySet();
      pset.initializeProperties(hostInfo.exposeAsProperties());
      this.session = new MysqlxSession(hostInfo, pset);
      this.defaultSchemaName = hostInfo.getDatabase();
      this.xbuilder = (XMessageBuilder)this.session.getMessageBuilder();
   }

   public SessionImpl(XProtocol prot) {
      this.session = new MysqlxSession(prot);
      this.defaultSchemaName = prot.defaultSchemaName;
      this.xbuilder = (XMessageBuilder)this.session.getMessageBuilder();
   }

   protected SessionImpl() {
   }

   public List<Schema> getSchemas() {
      Function<com.mysql.cj.result.Row, String> rowToName = (r) -> {
         return (String)r.getValue(0, new StringValueFactory(this.session.getPropertySet()));
      };
      Function<com.mysql.cj.result.Row, Schema> rowToSchema = rowToName.andThen((n) -> {
         return new SchemaImpl(this.session, this, n);
      });
      return (List)this.session.query(this.xbuilder.buildSqlStatement("select schema_name from information_schema.schemata"), (Predicate)null, rowToSchema, Collectors.toList());
   }

   public Schema getSchema(String schemaName) {
      return new SchemaImpl(this.session, this, schemaName);
   }

   public String getDefaultSchemaName() {
      return this.defaultSchemaName;
   }

   public Schema getDefaultSchema() {
      return this.defaultSchemaName != null && this.defaultSchemaName.length() != 0 ? new SchemaImpl(this.session, this, this.defaultSchemaName) : null;
   }

   public Schema createSchema(String schemaName) {
      StringBuilder stmtString = new StringBuilder("CREATE DATABASE ");
      stmtString.append(StringUtils.quoteIdentifier(schemaName, true));
      this.session.query(this.xbuilder.buildSqlStatement(stmtString.toString()), new UpdateResultBuilder());
      return this.getSchema(schemaName);
   }

   public Schema createSchema(String schemaName, boolean reuseExistingObject) {
      try {
         return this.createSchema(schemaName);
      } catch (XProtocolError var4) {
         if (var4.getErrorCode() == 1007) {
            return this.getSchema(schemaName);
         } else {
            throw var4;
         }
      }
   }

   public void dropSchema(String schemaName) {
      StringBuilder stmtString = new StringBuilder("DROP DATABASE ");
      stmtString.append(StringUtils.quoteIdentifier(schemaName, true));
      this.session.query(this.xbuilder.buildSqlStatement(stmtString.toString()), new UpdateResultBuilder());
   }

   public void startTransaction() {
      this.session.query(this.xbuilder.buildSqlStatement("START TRANSACTION"), new UpdateResultBuilder());
   }

   public void commit() {
      this.session.query(this.xbuilder.buildSqlStatement("COMMIT"), new UpdateResultBuilder());
   }

   public void rollback() {
      this.session.query(this.xbuilder.buildSqlStatement("ROLLBACK"), new UpdateResultBuilder());
   }

   public String setSavepoint() {
      return this.setSavepoint(StringUtils.getUniqueSavepointId());
   }

   public String setSavepoint(String name) {
      if (name != null && name.trim().length() != 0) {
         this.session.query(this.xbuilder.buildSqlStatement("SAVEPOINT " + StringUtils.quoteIdentifier(name, true)), new UpdateResultBuilder());
         return name;
      } else {
         throw new XDevAPIError(Messages.getString("XSession.0", new String[]{"name"}));
      }
   }

   public void rollbackTo(String name) {
      if (name != null && name.trim().length() != 0) {
         this.session.query(this.xbuilder.buildSqlStatement("ROLLBACK TO " + StringUtils.quoteIdentifier(name, true)), new UpdateResultBuilder());
      } else {
         throw new XDevAPIError(Messages.getString("XSession.0", new String[]{"name"}));
      }
   }

   public void releaseSavepoint(String name) {
      if (name != null && name.trim().length() != 0) {
         this.session.query(this.xbuilder.buildSqlStatement("RELEASE SAVEPOINT " + StringUtils.quoteIdentifier(name, true)), new UpdateResultBuilder());
      } else {
         throw new XDevAPIError(Messages.getString("XSession.0", new String[]{"name"}));
      }
   }

   public String getUri() {
      PropertySet pset = this.session.getPropertySet();
      StringBuilder sb = new StringBuilder(ConnectionUrl.Type.XDEVAPI_SESSION.getScheme());
      sb.append("//").append(this.session.getProcessHost()).append(":").append(this.session.getPort()).append("/").append(this.defaultSchemaName).append("?");
      boolean isFirstParam = true;
      Iterator var4 = PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet().iterator();

      while(true) {
         PropertyKey propKey;
         String propValue;
         Object defaultValue;
         do {
            RuntimeProperty propToGet;
            do {
               if (!var4.hasNext()) {
                  return sb.toString();
               }

               propKey = (PropertyKey)var4.next();
               propToGet = pset.getProperty(propKey);
            } while(!propToGet.isExplicitlySet());

            propValue = propToGet.getStringValue();
            defaultValue = propToGet.getPropertyDefinition().getDefaultValue();
         } while((defaultValue != null || StringUtils.isNullOrEmpty(propValue)) && (defaultValue == null || propValue != null) && (defaultValue == null || propValue == null || propValue.equals(defaultValue.toString())));

         if (isFirstParam) {
            isFirstParam = false;
         } else {
            sb.append("&");
         }

         sb.append(propKey.getKeyName());
         sb.append("=");
         sb.append(propValue);
      }
   }

   public boolean isOpen() {
      return !this.session.isClosed();
   }

   public void close() {
      this.session.quit();
   }

   public SqlStatementImpl sql(String sql) {
      return new SqlStatementImpl(this.session, sql);
   }

   public MysqlxSession getSession() {
      return this.session;
   }
}
