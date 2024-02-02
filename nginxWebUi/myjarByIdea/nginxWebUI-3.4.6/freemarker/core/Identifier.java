package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

final class Identifier extends Expression {
   private final String name;

   Identifier(String name) {
      this.name = name;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      try {
         return env.getVariable(this.name);
      } catch (NullPointerException var3) {
         if (env == null) {
            throw new _MiscTemplateException(new Object[]{"Variables are not available (certainly you are in a parse-time executed directive). The name of the variable you tried to read: ", this.name});
         } else {
            throw var3;
         }
      }
   }

   public String getCanonicalForm() {
      return _CoreStringUtils.toFTLTopLevelIdentifierReference(this.name);
   }

   String getName() {
      return this.name;
   }

   String getNodeTypeSymbol() {
      return this.getCanonicalForm();
   }

   boolean isLiteral() {
      return false;
   }

   int getParameterCount() {
      return 0;
   }

   Object getParameterValue(int idx) {
      throw new IndexOutOfBoundsException();
   }

   ParameterRole getParameterRole(int idx) {
      throw new IndexOutOfBoundsException();
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      if (this.name.equals(replacedIdentifier)) {
         if (replacementState.replacementAlreadyInUse) {
            Expression clone = replacement.deepCloneWithIdentifierReplaced((String)null, (Expression)null, replacementState);
            clone.copyLocationFrom(replacement);
            return clone;
         } else {
            replacementState.replacementAlreadyInUse = true;
            return replacement;
         }
      } else {
         return new Identifier(this.name);
      }
   }
}
