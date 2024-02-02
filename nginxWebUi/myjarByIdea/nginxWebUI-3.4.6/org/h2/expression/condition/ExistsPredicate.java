package org.h2.expression.condition;

import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;

public class ExistsPredicate extends PredicateWithSubquery {
   public ExistsPredicate(Query var1) {
      super(var1);
   }

   public Value getValue(SessionLocal var1) {
      this.query.setSession(var1);
      return ValueBoolean.get(this.query.exists());
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return super.getUnenclosedSQL(var1.append("EXISTS"), var2);
   }
}
