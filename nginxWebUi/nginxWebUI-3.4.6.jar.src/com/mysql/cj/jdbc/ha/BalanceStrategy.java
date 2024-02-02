package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.JdbcConnection;
import java.lang.reflect.InvocationHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BalanceStrategy {
  JdbcConnection pickConnection(InvocationHandler paramInvocationHandler, List<String> paramList, Map<String, JdbcConnection> paramMap, long[] paramArrayOflong, int paramInt) throws SQLException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\BalanceStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */