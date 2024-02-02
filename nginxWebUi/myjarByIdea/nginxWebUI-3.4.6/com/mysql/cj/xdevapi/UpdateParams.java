package com.mysql.cj.xdevapi;

import com.mysql.cj.x.protobuf.MysqlxExpr;
import java.util.HashMap;
import java.util.Map;

public class UpdateParams {
   private Map<MysqlxExpr.ColumnIdentifier, MysqlxExpr.Expr> updateOps = new HashMap();

   public void setUpdates(Map<String, Object> updates) {
      updates.entrySet().forEach((e) -> {
         this.addUpdate((String)e.getKey(), e.getValue());
      });
   }

   public void addUpdate(String path, Object value) {
      this.updateOps.put((new ExprParser(path, true)).parseTableUpdateField(), ExprUtil.argObjectToExpr(value, true));
   }

   public Object getUpdates() {
      return this.updateOps;
   }
}
