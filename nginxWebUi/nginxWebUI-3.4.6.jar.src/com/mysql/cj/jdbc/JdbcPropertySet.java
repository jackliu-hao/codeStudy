package com.mysql.cj.jdbc;

import com.mysql.cj.conf.PropertySet;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.List;

public interface JdbcPropertySet extends PropertySet {
  List<DriverPropertyInfo> exposeAsDriverPropertyInfo() throws SQLException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\JdbcPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */