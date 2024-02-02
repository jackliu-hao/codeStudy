package org.h2.expression;

import org.h2.message.DbException;

public interface ExpressionWithVariableParameters {
   void addParameter(Expression var1);

   void doneWithParameters() throws DbException;
}
