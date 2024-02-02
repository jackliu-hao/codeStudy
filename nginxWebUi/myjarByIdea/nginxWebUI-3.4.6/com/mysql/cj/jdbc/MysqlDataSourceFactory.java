package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

public class MysqlDataSourceFactory implements ObjectFactory {
   protected static final String DATA_SOURCE_CLASS_NAME = MysqlDataSource.class.getName();
   protected static final String POOL_DATA_SOURCE_CLASS_NAME = MysqlConnectionPoolDataSource.class.getName();
   protected static final String XA_DATA_SOURCE_CLASS_NAME = MysqlXADataSource.class.getName();

   public Object getObjectInstance(Object refObj, Name nm, Context ctx, Hashtable<?, ?> env) throws Exception {
      Reference ref = (Reference)refObj;
      String className = ref.getClassName();
      if (className != null && (className.equals(DATA_SOURCE_CLASS_NAME) || className.equals(POOL_DATA_SOURCE_CLASS_NAME) || className.equals(XA_DATA_SOURCE_CLASS_NAME))) {
         MysqlDataSource dataSource = null;

         try {
            dataSource = (MysqlDataSource)Class.forName(className).newInstance();
         } catch (Exception var15) {
            throw new RuntimeException(Messages.getString("MysqlDataSourceFactory.0", new Object[]{className, var15.toString()}));
         }

         int portNumber = 3306;
         String portNumberAsString = this.nullSafeRefAddrStringGet("port", ref);
         if (portNumberAsString != null) {
            portNumber = Integer.parseInt(portNumberAsString);
         }

         dataSource.setPort(portNumber);
         String user = this.nullSafeRefAddrStringGet(PropertyKey.USER.getKeyName(), ref);
         if (user != null) {
            dataSource.setUser(user);
         }

         String password = this.nullSafeRefAddrStringGet(PropertyKey.PASSWORD.getKeyName(), ref);
         if (password != null) {
            dataSource.setPassword(password);
         }

         String serverName = this.nullSafeRefAddrStringGet("serverName", ref);
         if (serverName != null) {
            dataSource.setServerName(serverName);
         }

         String databaseName = this.nullSafeRefAddrStringGet("databaseName", ref);
         if (databaseName != null) {
            dataSource.setDatabaseName(databaseName);
         }

         String explicitUrlAsString = this.nullSafeRefAddrStringGet("explicitUrl", ref);
         if (explicitUrlAsString != null && Boolean.valueOf(explicitUrlAsString)) {
            dataSource.setUrl(this.nullSafeRefAddrStringGet("url", ref));
         }

         dataSource.setPropertiesViaRef(ref);
         return dataSource;
      } else {
         return null;
      }
   }

   private String nullSafeRefAddrStringGet(String referenceName, Reference ref) {
      RefAddr refAddr = ref.get(referenceName);
      String asString = refAddr != null ? (String)refAddr.getContent() : null;
      return asString;
   }
}
