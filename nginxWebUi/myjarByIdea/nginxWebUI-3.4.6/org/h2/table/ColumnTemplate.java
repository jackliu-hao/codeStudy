package org.h2.table;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.schema.Domain;

public interface ColumnTemplate {
   Domain getDomain();

   void setDomain(Domain var1);

   void setDefaultExpression(SessionLocal var1, Expression var2);

   Expression getDefaultExpression();

   Expression getEffectiveDefaultExpression();

   String getDefaultSQL();

   void setOnUpdateExpression(SessionLocal var1, Expression var2);

   Expression getOnUpdateExpression();

   Expression getEffectiveOnUpdateExpression();

   String getOnUpdateSQL();

   void prepareExpressions(SessionLocal var1);
}
