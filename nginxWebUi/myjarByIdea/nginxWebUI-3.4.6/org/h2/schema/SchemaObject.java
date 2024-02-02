package org.h2.schema;

import org.h2.engine.DbObject;

public abstract class SchemaObject extends DbObject {
   private final Schema schema;

   protected SchemaObject(Schema var1, int var2, String var3, int var4) {
      super(var1.getDatabase(), var2, var3, var4);
      this.schema = var1;
   }

   public final Schema getSchema() {
      return this.schema;
   }

   public String getSQL(int var1) {
      return this.getSQL(new StringBuilder(), var1).toString();
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      this.schema.getSQL(var1, var2).append('.');
      return super.getSQL(var1, var2);
   }

   public boolean isHidden() {
      return false;
   }
}
