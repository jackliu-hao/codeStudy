package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.JdbcConnection;
import java.lang.reflect.InvocationHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BalanceStrategy {
   JdbcConnection pickConnection(InvocationHandler var1, List<String> var2, Map<String, JdbcConnection> var3, long[] var4, int var5) throws SQLException;
}
