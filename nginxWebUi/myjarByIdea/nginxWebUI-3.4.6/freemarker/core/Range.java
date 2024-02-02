package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template._TemplateAPI;

final class Range extends Expression {
   static final int END_INCLUSIVE = 0;
   static final int END_EXCLUSIVE = 1;
   static final int END_UNBOUND = 2;
   static final int END_SIZE_LIMITED = 3;
   final Expression lho;
   final Expression rho;
   final int endType;

   Range(Expression lho, Expression rho, int endType) {
      this.lho = lho;
      this.rho = rho;
      this.endType = endType;
   }

   int getEndType() {
      return this.endType;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      int begin = this.lho.evalToNumber(env).intValue();
      if (this.endType != 2) {
         int lhoValue = this.rho.evalToNumber(env).intValue();
         return new BoundedRangeModel(begin, this.endType != 3 ? lhoValue : begin + lhoValue, this.endType == 0, this.endType == 3);
      } else {
         return (TemplateModel)(_TemplateAPI.getTemplateLanguageVersionAsInt((TemplateObject)this) >= _TemplateAPI.VERSION_INT_2_3_21 ? new ListableRightUnboundedRangeModel(begin) : new NonListableRightUnboundedRangeModel(begin));
      }
   }

   boolean evalToBoolean(Environment env) throws TemplateException {
      throw new NonBooleanException(this, new BoundedRangeModel(0, 0, false, false), env);
   }

   public String getCanonicalForm() {
      String rhs = this.rho != null ? this.rho.getCanonicalForm() : "";
      return this.lho.getCanonicalForm() + this.getNodeTypeSymbol() + rhs;
   }

   String getNodeTypeSymbol() {
      switch (this.endType) {
         case 0:
            return "..";
         case 1:
            return "..<";
         case 2:
            return "..";
         case 3:
            return "..*";
         default:
            throw new BugException(this.endType);
      }
   }

   boolean isLiteral() {
      boolean rightIsLiteral = this.rho == null || this.rho.isLiteral();
      return this.constantValue != null || this.lho.isLiteral() && rightIsLiteral;
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new Range(this.lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.endType);
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.lho;
         case 1:
            return this.rho;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      return ParameterRole.forBinaryOperatorOperand(idx);
   }
}
