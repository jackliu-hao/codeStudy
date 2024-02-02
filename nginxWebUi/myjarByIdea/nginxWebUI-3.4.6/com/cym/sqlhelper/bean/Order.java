package com.cym.sqlhelper.bean;

public class Order {
   Sort.Direction direction;
   String column;

   public Sort.Direction getDirection() {
      return this.direction;
   }

   public void setDirection(Sort.Direction direction) {
      this.direction = direction;
   }

   public String getColumn() {
      return this.column;
   }

   public void setColumn(String column) {
      this.column = column;
   }
}
