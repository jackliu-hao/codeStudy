package freemarker.core;

import freemarker.template.TemplateException;

final class ComparisonExpression extends BooleanExpression {
   private final Expression left;
   private final Expression right;
   private final int operation;
   private final String opString;

   ComparisonExpression(Expression left, Expression right, String opString) {
      this.left = left;
      this.right = right;
      opString = opString.intern();
      this.opString = opString;
      if (opString != "==" && opString != "=") {
         if (opString == "!=") {
            this.operation = 2;
         } else if (opString != "gt" && opString != "\\gt" && opString != ">" && opString != "&gt;") {
            if (opString != "gte" && opString != "\\gte" && opString != ">=" && opString != "&gt;=") {
               if (opString != "lt" && opString != "\\lt" && opString != "<" && opString != "&lt;") {
                  if (opString != "lte" && opString != "\\lte" && opString != "<=" && opString != "&lt;=") {
                     throw new BugException("Unknown comparison operator " + opString);
                  }

                  this.operation = 5;
               } else {
                  this.operation = 3;
               }
            } else {
               this.operation = 6;
            }
         } else {
            this.operation = 4;
         }
      } else {
         this.operation = 1;
      }

      Expression cleanedLeft = MiscUtil.peelParentheses(left);
      Expression cleanedRight = MiscUtil.peelParentheses(right);
      if (cleanedLeft instanceof BuiltInsForMultipleTypes.sizeBI) {
         if (cleanedRight instanceof NumberLiteral) {
            ((BuiltInsForMultipleTypes.sizeBI)cleanedLeft).setCountingLimit(this.operation, (NumberLiteral)cleanedRight);
         }
      } else if (cleanedRight instanceof BuiltInsForMultipleTypes.sizeBI && cleanedLeft instanceof NumberLiteral) {
         ((BuiltInsForMultipleTypes.sizeBI)cleanedRight).setCountingLimit(EvalUtil.mirrorCmpOperator(this.operation), (NumberLiteral)cleanedLeft);
      }

   }

   boolean evalToBoolean(Environment env) throws TemplateException {
      return EvalUtil.compare(this.left, this.operation, this.opString, this.right, this, env);
   }

   public String getCanonicalForm() {
      return this.left.getCanonicalForm() + ' ' + this.opString + ' ' + this.right.getCanonicalForm();
   }

   String getNodeTypeSymbol() {
      return this.opString;
   }

   boolean isLiteral() {
      return this.constantValue != null || this.left.isLiteral() && this.right.isLiteral();
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new ComparisonExpression(this.left.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.right.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.opString);
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      return idx == 0 ? this.left : this.right;
   }

   ParameterRole getParameterRole(int idx) {
      return ParameterRole.forBinaryOperatorOperand(idx);
   }
}
