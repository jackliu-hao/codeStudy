package org.h2.table;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.schema.Domain;

public interface ColumnTemplate {
  Domain getDomain();
  
  void setDomain(Domain paramDomain);
  
  void setDefaultExpression(SessionLocal paramSessionLocal, Expression paramExpression);
  
  Expression getDefaultExpression();
  
  Expression getEffectiveDefaultExpression();
  
  String getDefaultSQL();
  
  void setOnUpdateExpression(SessionLocal paramSessionLocal, Expression paramExpression);
  
  Expression getOnUpdateExpression();
  
  Expression getEffectiveOnUpdateExpression();
  
  String getOnUpdateSQL();
  
  void prepareExpressions(SessionLocal paramSessionLocal);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\ColumnTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */