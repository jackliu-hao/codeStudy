package com.cym.sqlhelper.utils;

public class Condition {
   String column;
   String operation;
   Object value;

   public Condition(String column, String operation, Object value) {
      this.column = column;
      this.operation = operation;
      this.value = value;
   }

   public String getColumn() {
      return this.column;
   }

   public void setColumn(String column) {
      this.column = column;
   }

   public String getOperation() {
      return this.operation;
   }

   public void setOperation(String operation) {
      this.operation = operation;
   }

   public Object getValue() {
      return this.value;
   }

   public void setValue(Object value) {
      this.value = value;
   }
}
