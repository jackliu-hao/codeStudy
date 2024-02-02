package cn.hutool.db.handler;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RsHandler<T> extends Serializable {
  T handle(ResultSet paramResultSet) throws SQLException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\handler\RsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */