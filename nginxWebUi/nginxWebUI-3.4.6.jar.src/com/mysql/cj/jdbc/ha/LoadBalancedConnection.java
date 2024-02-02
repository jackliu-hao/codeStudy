package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.JdbcConnection;
import java.sql.SQLException;

public interface LoadBalancedConnection extends JdbcConnection {
  boolean addHost(String paramString) throws SQLException;
  
  void removeHost(String paramString) throws SQLException;
  
  void removeHostWhenNotInUse(String paramString) throws SQLException;
  
  void ping(boolean paramBoolean) throws SQLException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\LoadBalancedConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */