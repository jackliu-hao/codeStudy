package cn.hutool.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NumberHandler implements RsHandler<Number> {
   private static final long serialVersionUID = 4081498054379705596L;

   public static NumberHandler create() {
      return new NumberHandler();
   }

   public Number handle(ResultSet rs) throws SQLException {
      return null != rs && rs.next() ? rs.getBigDecimal(1) : null;
   }
}
