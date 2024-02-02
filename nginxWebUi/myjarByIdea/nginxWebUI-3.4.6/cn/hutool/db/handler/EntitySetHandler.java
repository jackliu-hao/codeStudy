package cn.hutool.db.handler;

import cn.hutool.db.Entity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

public class EntitySetHandler implements RsHandler<LinkedHashSet<Entity>> {
   private static final long serialVersionUID = 8191723216703506736L;
   private final boolean caseInsensitive;

   public static EntitySetHandler create() {
      return new EntitySetHandler();
   }

   public EntitySetHandler() {
      this(false);
   }

   public EntitySetHandler(boolean caseInsensitive) {
      this.caseInsensitive = caseInsensitive;
   }

   public LinkedHashSet<Entity> handle(ResultSet rs) throws SQLException {
      return (LinkedHashSet)HandleHelper.handleRs(rs, new LinkedHashSet(), this.caseInsensitive);
   }
}
