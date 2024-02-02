package com.mysql.cj.xdevapi;

public class Expression {
   private String expressionString;

   public Expression(String expressionString) {
      this.expressionString = expressionString;
   }

   public String getExpressionString() {
      return this.expressionString;
   }

   public static Expression expr(String expressionString) {
      return new Expression(expressionString);
   }
}
