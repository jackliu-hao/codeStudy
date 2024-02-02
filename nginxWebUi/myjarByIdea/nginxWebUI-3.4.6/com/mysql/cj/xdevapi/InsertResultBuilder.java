package com.mysql.cj.xdevapi;

public class InsertResultBuilder extends UpdateResultBuilder<InsertResult> {
   public InsertResult build() {
      return new InsertResultImpl(this.statementExecuteOkBuilder.build());
   }
}
