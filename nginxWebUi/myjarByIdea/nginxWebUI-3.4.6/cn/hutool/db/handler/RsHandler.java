package cn.hutool.db.handler;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RsHandler<T> extends Serializable {
   T handle(ResultSet var1) throws SQLException;
}
