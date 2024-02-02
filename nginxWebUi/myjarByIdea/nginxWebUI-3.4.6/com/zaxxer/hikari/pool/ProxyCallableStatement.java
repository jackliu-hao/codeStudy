package com.zaxxer.hikari.pool;

import java.sql.CallableStatement;

public abstract class ProxyCallableStatement extends ProxyPreparedStatement implements CallableStatement {
   protected ProxyCallableStatement(ProxyConnection connection, CallableStatement statement) {
      super(connection, statement);
   }
}
