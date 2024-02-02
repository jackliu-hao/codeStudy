package cn.hutool.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringHandler implements RsHandler<String> {
   private static final long serialVersionUID = -5296733366845720383L;

   public static StringHandler create() {
      return new StringHandler();
   }

   public String handle(ResultSet rs) throws SQLException {
      return rs.next() ? rs.getString(1) : null;
   }
}
