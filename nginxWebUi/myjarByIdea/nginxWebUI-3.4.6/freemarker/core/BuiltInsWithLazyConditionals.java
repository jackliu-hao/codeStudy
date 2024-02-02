package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class BuiltInsWithLazyConditionals {
   private BuiltInsWithLazyConditionals() {
   }

   static class switch_BI extends BuiltInWithParseTimeParameters {
      private List<Expression> parameters;

      void bindToParameters(List<Expression> parameters, Token openParen, Token closeParen) throws ParseException {
         if (parameters.size() < 2) {
            throw this.newArgumentCountException("must have at least 2", openParen, closeParen);
         } else {
            this.parameters = parameters;
         }
      }

      protected List<Expression> getArgumentsAsList() {
         return this.parameters;
      }

      protected int getArgumentsCount() {
         return this.parameters.size();
      }

      protected Expression getArgumentParameterValue(int argIdx) {
         return (Expression)this.parameters.get(argIdx);
      }

      protected void cloneArguments(Expression clone, String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
         List<Expression> parametersClone = new ArrayList(this.parameters.size());
         Iterator var6 = this.parameters.iterator();

         while(var6.hasNext()) {
            Expression parameter = (Expression)var6.next();
            parametersClone.add(parameter.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
         }

         ((switch_BI)clone).parameters = parametersClone;
      }

      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel targetValue = this.target.evalToNonMissing(env);
         List<Expression> parameters = this.parameters;
         int paramCnt = parameters.size();

         for(int i = 0; i + 1 < paramCnt; i += 2) {
            Expression caseExp = (Expression)parameters.get(i);
            TemplateModel caseValue = caseExp.evalToNonMissing(env);
            if (EvalUtil.compare(targetValue, this.target, 1, "==", caseValue, caseExp, this, true, false, false, false, env)) {
               return ((Expression)parameters.get(i + 1)).evalToNonMissing(env);
            }
         }

         if (paramCnt % 2 == 0) {
            throw new _MiscTemplateException(this.target, new Object[]{"The value before ?", this.key, "(case1, value1, case2, value2, ...) didn't match any of the case parameters, and there was no default value parameter (an additional last parameter) eithter. "});
         } else {
            return ((Expression)parameters.get(paramCnt - 1)).evalToNonMissing(env);
         }
      }
   }

   static class then_BI extends BuiltInWithParseTimeParameters {
      private Expression whenTrueExp;
      private Expression whenFalseExp;

      TemplateModel _eval(Environment env) throws TemplateException {
         boolean lho = this.target.evalToBoolean(env);
         return (lho ? this.whenTrueExp : this.whenFalseExp).evalToNonMissing(env);
      }

      void bindToParameters(List<Expression> parameters, Token openParen, Token closeParen) throws ParseException {
         if (parameters.size() != 2) {
            throw this.newArgumentCountException("requires exactly 2", openParen, closeParen);
         } else {
            this.whenTrueExp = (Expression)parameters.get(0);
            this.whenFalseExp = (Expression)parameters.get(1);
         }
      }

      protected Expression getArgumentParameterValue(int argIdx) {
         switch (argIdx) {
            case 0:
               return this.whenTrueExp;
            case 1:
               return this.whenFalseExp;
            default:
               throw new IndexOutOfBoundsException();
         }
      }

      protected int getArgumentsCount() {
         return 2;
      }

      protected List<Expression> getArgumentsAsList() {
         ArrayList<Expression> args = new ArrayList(2);
         args.add(this.whenTrueExp);
         args.add(this.whenFalseExp);
         return args;
      }

      protected void cloneArguments(Expression cloneExp, String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
         then_BI clone = (then_BI)cloneExp;
         clone.whenTrueExp = this.whenTrueExp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState);
         clone.whenFalseExp = this.whenFalseExp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState);
      }
   }
}
