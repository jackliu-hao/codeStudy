package org.h2.constraint;

public enum ConstraintActionType {
   RESTRICT,
   CASCADE,
   SET_DEFAULT,
   SET_NULL;

   public String getSqlName() {
      if (this == SET_DEFAULT) {
         return "SET DEFAULT";
      } else {
         return this == SET_NULL ? "SET NULL" : this.name();
      }
   }
}
