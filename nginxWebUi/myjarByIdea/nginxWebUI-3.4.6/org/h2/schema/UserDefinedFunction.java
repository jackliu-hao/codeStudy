package org.h2.schema;

import org.h2.message.DbException;
import org.h2.table.Table;

public abstract class UserDefinedFunction extends SchemaObject {
   String className;

   UserDefinedFunction(Schema var1, int var2, String var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public final String getCreateSQLForCopy(Table var1, String var2) {
      throw DbException.getInternalError(this.toString());
   }

   public final void checkRename() {
      throw DbException.getUnsupportedException("RENAME");
   }

   public final String getJavaClassName() {
      return this.className;
   }
}
