package org.h2.table;

public enum TableType {
   TABLE_LINK,
   SYSTEM_TABLE,
   TABLE,
   VIEW,
   EXTERNAL_TABLE_ENGINE;

   public String toString() {
      if (this == EXTERNAL_TABLE_ENGINE) {
         return "EXTERNAL";
      } else if (this == SYSTEM_TABLE) {
         return "SYSTEM TABLE";
      } else {
         return this == TABLE_LINK ? "TABLE LINK" : super.toString();
      }
   }
}
