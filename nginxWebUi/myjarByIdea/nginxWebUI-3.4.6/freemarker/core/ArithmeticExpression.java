package freemarker.core;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

final class ArithmeticExpression extends Expression {
   static final int TYPE_SUBSTRACTION = 0;
   static final int TYPE_MULTIPLICATION = 1;
   static final int TYPE_DIVISION = 2;
   static final int TYPE_MODULO = 3;
   private static final char[] OPERATOR_IMAGES = new char[]{'-', '*', '/', '%'};
   private final Expression lho;
   private final Expression rho;
   private final int operator;

   ArithmeticExpression(Expression lho, Expression rho, int operator) {
      this.lho = lho;
      this.rho = rho;
      this.operator = operator;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      return _eval(env, this, this.lho.evalToNumber(env), this.operator, this.rho.evalToNumber(env));
   }

   static TemplateModel _eval(Environment env, TemplateObject parent, Number lhoNumber, int operator, Number rhoNumber) throws TemplateException, _MiscTemplateException {
      ArithmeticEngine ae = EvalUtil.getArithmeticEngine(env, parent);

      try {
         switch (operator) {
            case 0:
               return new SimpleNumber(ae.subtract(lhoNumber, rhoNumber));
            case 1:
               return new SimpleNumber(ae.multiply(lhoNumber, rhoNumber));
            case 2:
               return new SimpleNumber(ae.divide(lhoNumber, rhoNumber));
            case 3:
               return new SimpleNumber(ae.modulus(lhoNumber, rhoNumber));
            default:
               if (parent instanceof Expression) {
                  throw new _MiscTemplateException((Expression)parent, new Object[]{"Unknown operation: ", operator});
               } else {
                  throw new _MiscTemplateException(new Object[]{"Unknown operation: ", operator});
               }
         }
      } catch (ArithmeticException var7) {
         throw new _MiscTemplateException(var7, env, new Object[]{"Arithmetic operation failed", var7.getMessage() != null ? new String[]{": ", var7.getMessage()} : " (see cause exception)"});
      }
   }

   public String getCanonicalForm() {
      return this.lho.getCanonicalForm() + ' ' + getOperatorSymbol(this.operator) + ' ' + this.rho.getCanonicalForm();
   }

   String getNodeTypeSymbol() {
      return String.valueOf(getOperatorSymbol(this.operator));
   }

   static char getOperatorSymbol(int operator) {
      return OPERATOR_IMAGES[operator];
   }

   boolean isLiteral() {
      return this.constantValue != null || this.lho.isLiteral() && this.rho.isLiteral();
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new ArithmeticExpression(this.lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.operator);
   }

   int getParameterCount() {
      return 3;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.lho;
         case 1:
            return this.rho;
         case 2:
            return this.operator;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      switch (idx) {
         case 0:
            return ParameterRole.LEFT_HAND_OPERAND;
         case 1:
            return ParameterRole.RIGHT_HAND_OPERAND;
         case 2:
            return ParameterRole.AST_NODE_SUBTYPE;
         default:
            throw new IndexOutOfBoundsException();
      }
   }
}
