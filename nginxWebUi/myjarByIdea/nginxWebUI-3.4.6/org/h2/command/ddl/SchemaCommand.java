package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.schema.Schema;

public abstract class SchemaCommand extends DefineCommand {
   private final Schema schema;

   public SchemaCommand(SessionLocal var1, Schema var2) {
      super(var1);
      this.schema = var2;
   }

   protected final Schema getSchema() {
      return this.schema;
   }
}
