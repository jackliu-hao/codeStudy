package com.mysql.cj.jdbc;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

public class MysqlXADataSource extends MysqlDataSource implements XADataSource {
   static final long serialVersionUID = 7911390333152247455L;

   public XAConnection getXAConnection() throws SQLException {
      try {
         Connection conn = this.getConnection();
         return this.wrapConnection(conn);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3);
      }
   }

   public XAConnection getXAConnection(String u, String p) throws SQLException {
      try {
         Connection conn = this.getConnection(u, p);
         return this.wrapConnection(conn);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5);
      }
   }

   private XAConnection wrapConnection(Connection conn) throws SQLException {
      return (XAConnection)(!(Boolean)this.getBooleanProperty(PropertyKey.pinGlobalTxToPhysicalConnection).getValue() && !(Boolean)((JdbcConnection)conn).getPropertySet().getBooleanProperty(PropertyKey.pinGlobalTxToPhysicalConnection).getValue() ? MysqlXAConnection.getInstance((JdbcConnection)conn, (Boolean)this.getBooleanProperty(PropertyKey.logXaCommands).getValue()) : SuspendableXAConnection.getInstance((JdbcConnection)conn));
   }
}
