package freemarker.core;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;

final class UnaryPlusMinusExpression extends Expression {
   private static final int TYPE_MINUS = 0;
   private static final int TYPE_PLUS = 1;
   private final Expression target;
   private final boolean isMinus;
   private static final Integer MINUS_ONE = -1;

   UnaryPlusMinusExpression(Expression target, boolean isMinus) {
      this.target = target;
      this.isMinus = isMinus;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateNumberModel targetModel = null;
      TemplateModel tm = this.target.eval(env);

      try {
         targetModel = (TemplateNumberModel)tm;
      } catch (ClassCastException var5) {
         throw new NonNumericalException(this.target, tm, env);
      }

      if (!this.isMinus) {
         return targetModel;
      } else {
         this.target.assertNonNull(targetModel, env);
         Number n = targetModel.getAsNumber();
         n = ArithmeticEngine.CONSERVATIVE_ENGINE.multiply(MINUS_ONE, n);
         return new SimpleNumber(n);
      }
   }

   public String getCanonicalForm() {
      String op = this.isMinus ? "-" : "+";
      return op + this.target.getCanonicalForm();
   }

   String getNodeTypeSymbol() {
      return this.isMinus ? "-..." : "+...";
   }

   boolean isLiteral() {
      return this.target.isLiteral();
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new UnaryPlusMinusExpression(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.isMinus);
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.target;
         case 1:
            return this.isMinus ? 0 : 1;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      switch (idx) {
         case 0:
            return ParameterRole.RIGHT_HAND_OPERAND;
         case 1:
            return ParameterRole.AST_NODE_SUBTYPE;
         default:
            throw new IndexOutOfBoundsException();
      }
   }
}
