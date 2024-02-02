package cn.hutool.db.sql;

import cn.hutool.core.util.StrUtil;
import java.io.Serializable;

public class Order implements Serializable {
   private static final long serialVersionUID = 1L;
   private String field;
   private Direction direction;

   public Order() {
   }

   public Order(String field) {
      this.field = field;
   }

   public Order(String field, Direction direction) {
      this(field);
      this.direction = direction;
   }

   public String getField() {
      return this.field;
   }

   public void setField(String field) {
      this.field = field;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public void setDirection(Direction direction) {
      this.direction = direction;
   }

   public String toString() {
      return StrUtil.builder().append(this.field).append(" ").append(null == this.direction ? "" : this.direction).toString();
   }
}
