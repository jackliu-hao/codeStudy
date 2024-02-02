package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.schema.Schema;

abstract class SchemaOwnerCommand extends SchemaCommand {
   SchemaOwnerCommand(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public final long update() {
      Schema var1 = this.getSchema();
      this.session.getUser().checkSchemaOwner(var1);
      return this.update(var1);
   }

   abstract long update(Schema var1);
}
