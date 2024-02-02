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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateSynonymData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */