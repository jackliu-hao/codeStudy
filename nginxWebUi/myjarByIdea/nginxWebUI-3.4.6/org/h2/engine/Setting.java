package org.h2.engine;

import org.h2.message.DbException;
import org.h2.table.Table;

public final class Setting extends DbObject {
   private int intValue;
   private String stringValue;

   public Setting(Database var1, int var2, String var3) {
      super(var1, var2, var3, 10);
   }

   public String getSQL(int var1) {
      return this.getName();
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return var1.append(this.getName());
   }

   public void setIntValue(int var1) {
      this.intValue = var1;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public void setStringValue(String var1) {
      this.stringValue = var1;
   }

   public String getStringValue() {
      return this.stringValue;
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      throw DbException.getInternalError(this.toString());
   }

   public String getCreateSQL() {
      StringBuilder var1 = new StringBuilder("SET ");
      this.getSQL(var1, 0).append(' ');
      if (this.stringValue != null) {
         var1.append(this.stringValue);
      } else {
         var1.append(this.intValue);
      }

      return var1.toString();
   }

   public int getType() {
      return 6;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.database.removeMeta(var1, this.getId());
      this.invalidate();
   }

   public void checkRename() {
      throw DbException.getUnsupportedException("RENAME");
   }
}
