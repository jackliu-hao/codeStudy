package org.h2.command.ddl;

import java.util.ArrayList;
import org.h2.command.Prepared;
import org.h2.engine.Procedure;
import org.h2.engine.SessionLocal;
import org.h2.expression.Parameter;

public class PrepareProcedure extends DefineCommand {
   private String procedureName;
   private Prepared prepared;

   public PrepareProcedure(SessionLocal var1) {
      super(var1);
   }

   public void checkParameters() {
   }

   public long update() {
      Procedure var1 = new Procedure(this.procedureName, this.prepared);
      this.prepared.setParameterList(this.parameters);
      this.prepared.setPrepareAlways(this.prepareAlways);
      this.prepared.prepare();
      this.session.addProcedure(var1);
      return 0L;
   }

   public void setProcedureName(String var1) {
      this.procedureName = var1;
   }

   public void setPrepared(Prepared var1) {
      this.prepared = var1;
   }

   public ArrayList<Parameter> getParameters() {
      return new ArrayList(0);
   }

   public int getType() {
      return 51;
   }
}
