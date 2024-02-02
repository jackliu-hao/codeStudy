package cn.hutool.db.handler;

import cn.hutool.db.Entity;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class EntityHandler implements RsHandler<Entity> {
   private static final long serialVersionUID = -8742432871908355992L;
   private final boolean caseInsensitive;

   public static EntityHandler create() {
      return new EntityHandler();
   }

   public EntityHandler() {
      this(false);
   }

   public EntityHandler(boolean caseInsensitive) {
      this.caseInsensitive = caseInsensitive;
   }

   public Entity handle(ResultSet rs) throws SQLException {
      ResultSetMetaData meta = rs.getMetaData();
      int columnCount = meta.getColumnCount();
      return rs.next() ? HandleHelper.handleRow(columnCount, meta, rs, this.caseInsensitive) : null;
   }
}
