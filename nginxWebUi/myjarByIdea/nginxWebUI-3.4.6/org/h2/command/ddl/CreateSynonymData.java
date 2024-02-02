package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.schema.Schema;

public class CreateSynonymData {
   public Schema schema;
   public String synonymName;
   public String synonymFor;
   public Schema synonymForSchema;
   public int id;
   public SessionLocal session;
}
