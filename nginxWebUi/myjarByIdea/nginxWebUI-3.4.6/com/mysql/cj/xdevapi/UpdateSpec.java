package com.mysql.cj.xdevapi;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.mysql.cj.x.protobuf.MysqlxExpr;

public class UpdateSpec {
   private MysqlxCrud.UpdateOperation.UpdateType updateType;
   private MysqlxExpr.ColumnIdentifier source;
   private MysqlxExpr.Expr value;

   public UpdateSpec(UpdateType updateType, String source) {
      this.updateType = MysqlxCrud.UpdateOperation.UpdateType.valueOf(updateType.name());
      if (source.length() > 0 && source.charAt(0) == '$') {
         source = source.substring(1);
      }

      this.source = (new ExprParser(source, false)).documentField().getIdentifier();
   }

   public Object getUpdateType() {
      return this.updateType;
   }

   public Object getSource() {
      return this.source;
   }

   public UpdateSpec setValue(Object value) {
      this.value = ExprUtil.argObjectToExpr(value, false);
      return this;
   }

   public Object getValue() {
      return this.value;
   }
}
