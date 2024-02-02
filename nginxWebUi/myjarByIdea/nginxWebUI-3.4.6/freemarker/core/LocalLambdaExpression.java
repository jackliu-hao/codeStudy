package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.util.Iterator;
import java.util.List;

final class LocalLambdaExpression extends Expression {
   private final LambdaParameterList lho;
   private final Expression rho;

   LocalLambdaExpression(LambdaParameterList lho, Expression rho) {
      this.lho = lho;
      this.rho = rho;
   }

   public String getCanonicalForm() {
      return this.lho.getCanonicalForm() + " -> " + this.rho.getCanonicalForm();
   }

   String getNodeTypeSymbol() {
      return "->";
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      throw new TemplateException("Can't get lambda expression as a value: Lambdas currently can only be used on a few special places.", env);
   }

   TemplateModel invokeLambdaDefinedFunction(TemplateModel argValue, Environment env) throws TemplateException {
      return env.evaluateWithNewLocal(this.rho, ((Identifier)this.lho.getParameters().get(0)).getName(), (TemplateModel)(argValue != null ? argValue : TemplateNullModel.INSTANCE));
   }

   boolean isLiteral() {
      return false;
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      Iterator var4 = this.lho.getParameters().iterator();

      Identifier parameter;
      do {
         if (!var4.hasNext()) {
            return new LocalLambdaExpression(this.lho, this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
         }

         parameter = (Identifier)var4.next();
      } while(!parameter.getName().equals(replacedIdentifier));

      throw new UncheckedParseException(new ParseException("Escape placeholder (" + replacedIdentifier + ") can't be used in the parameter list of a lambda expressions.", this));
   }

   int getParameterCount() {
      return this.lho.getParameters().size() + 1;
   }

   Object getParameterValue(int idx) {
      int paramCount = this.getParameterCount();
      if (idx < paramCount - 1) {
         return this.lho.getParameters().get(idx);
      } else if (idx == paramCount - 1) {
         return this.rho;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      int paramCount = this.getParameterCount();
      if (idx < paramCount - 1) {
         return ParameterRole.ARGUMENT_NAME;
      } else if (idx == paramCount - 1) {
         return ParameterRole.VALUE;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   LambdaParameterList getLambdaParameterList() {
      return this.lho;
   }

   static class LambdaParameterList {
      private final Token openingParenthesis;
      private final Token closingParenthesis;
      private final List<Identifier> parameters;

      public LambdaParameterList(Token openingParenthesis, List<Identifier> parameters, Token closingParenthesis) {
         this.openingParenthesis = openingParenthesis;
         this.closingParenthesis = closingParenthesis;
         this.parameters = parameters;
      }

      public Token getOpeningParenthesis() {
         return this.openingParenthesis;
      }

      public Token getClosingParenthesis() {
         return this.closingParenthesis;
      }

      public List<Identifier> getParameters() {
         return this.parameters;
      }

      public String getCanonicalForm() {
         if (this.parameters.size() == 1) {
            return ((Identifier)this.parameters.get(0)).getCanonicalForm();
         } else {
            StringBuilder sb = new StringBuilder();
            sb.append('(');

            for(int i = 0; i < this.parameters.size(); ++i) {
               if (i != 0) {
                  sb.append(", ");
               }

               Identifier parameter = (Identifier)this.parameters.get(i);
               sb.append(parameter.getCanonicalForm());
            }

            sb.append(')');
            return sb.toString();
         }
      }
   }
}
