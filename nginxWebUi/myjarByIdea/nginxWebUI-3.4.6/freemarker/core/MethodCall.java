package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import java.util.ArrayList;
import java.util.List;

final class MethodCall extends Expression {
   private final Expression target;
   private final ListLiteral arguments;

   MethodCall(Expression target, ArrayList arguments) {
      this(target, new ListLiteral(arguments));
   }

   private MethodCall(Expression target, ListLiteral arguments) {
      this.target = target;
      this.arguments = arguments;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateModel targetModel = this.target.eval(env);
      if (targetModel instanceof TemplateMethodModel) {
         TemplateMethodModel targetMethod = (TemplateMethodModel)targetModel;
         List argumentStrings = targetMethod instanceof TemplateMethodModelEx ? this.arguments.getModelList(env) : this.arguments.getValueList(env);
         Object result = targetMethod.exec(argumentStrings);
         return env.getObjectWrapper().wrap(result);
      } else if (targetModel instanceof Macro) {
         return env.invokeFunction(env, (Macro)targetModel, this.arguments.items, this);
      } else {
         throw new NonMethodException(this.target, targetModel, true, false, (String[])null, env);
      }
   }

   public String getCanonicalForm() {
      StringBuilder buf = new StringBuilder();
      buf.append(this.target.getCanonicalForm());
      buf.append("(");
      String list = this.arguments.getCanonicalForm();
      buf.append(list.substring(1, list.length() - 1));
      buf.append(")");
      return buf.toString();
   }

   String getNodeTypeSymbol() {
      return "...(...)";
   }

   TemplateModel getConstantValue() {
      return null;
   }

   boolean isLiteral() {
      return false;
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new MethodCall(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), (ListLiteral)this.arguments.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
   }

   int getParameterCount() {
      return 1 + this.arguments.items.size();
   }

   Expression getTarget() {
      return this.target;
   }

   Object getParameterValue(int idx) {
      if (idx == 0) {
         return this.target;
      } else if (idx < this.getParameterCount()) {
         return this.arguments.items.get(idx - 1);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx == 0) {
         return ParameterRole.CALLEE;
      } else if (idx < this.getParameterCount()) {
         return ParameterRole.ARGUMENT_VALUE;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
