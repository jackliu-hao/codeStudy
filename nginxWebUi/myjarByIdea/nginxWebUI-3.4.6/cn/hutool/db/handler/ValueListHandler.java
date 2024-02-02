package cn.hutool.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ValueListHandler implements RsHandler<List<List<Object>>> {
   private static final long serialVersionUID = 1L;

   public static ValueListHandler create() {
      return new ValueListHandler();
   }

   public List<List<Object>> handle(ResultSet rs) throws SQLException {
      ArrayList<List<Object>> result = new ArrayList();

      while(rs.next()) {
         result.add(HandleHelper.handleRowToList(rs));
      }

      return result;
   }
}
