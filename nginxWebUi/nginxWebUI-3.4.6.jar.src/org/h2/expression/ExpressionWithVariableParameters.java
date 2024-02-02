package org.h2.expression;

import org.h2.message.DbException;

public interface ExpressionWithVariableParameters {
  void addParameter(Expression paramExpression);
  
  void doneWithParameters() throws DbException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\ExpressionWithVariableParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */